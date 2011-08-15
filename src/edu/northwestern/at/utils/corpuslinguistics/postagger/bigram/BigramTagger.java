package edu.northwestern.at.utils.corpuslinguistics.postagger.bigram;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.logger.*;
import edu.northwestern.at.utils.corpuslinguistics.adornedword.*;
import edu.northwestern.at.utils.corpuslinguistics.lexicon.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.smoothing.contextual.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.smoothing.lexical.*;
import edu.northwestern.at.utils.corpuslinguistics.tokenizer.*;
import edu.northwestern.at.utils.math.*;

/**	Bigram Part of Speech tagger.
 *
 *	<p>
 *	The bigram part of speech tagger assigns tags to words in a sentence
 *	assigning the most probable set of tags as determined by a bigram hidden
 *	Markov model given the possible tags of the previous words.
 *	The Viterbi algorithm is used to reduce the
 *	amount of computation required to find the optimal tag assignments.
 *	</p>
 */

public class BigramTagger
	extends AbstractPartOfSpeechTagger
	implements PartOfSpeechTagger
{
	/**	True for debug output.
	 */

	protected boolean debug	= false;

	/**	Contextual probabilities for a word in a sentence.
	 */

	protected Map2D<String, String, Probability>
		contextualProbabilities	= Map2DFactory.createNewMap2D();

	/**	Total number of states rejected by beam search criterion.
	 */

	protected int beamSearchRejections	= 0;

	/**	Viterbi trellis for tags and probability scores.
     */

	protected Viterbi viterbi	= new Viterbi();

	/**	Create a bigram tagger.
	 */

	public BigramTagger()
	{
		super();
								//	Get a lexical smoother.
		lexicalSmoother	=
			new LexicalSmootherFactory().newLexicalSmoother();

		lexicalSmoother.setPartOfSpeechTagger( this );

								//	Get a contextual smoother.
		contextualSmoother	=
			new ContextualSmootherFactory().newContextualSmoother();

		contextualSmoother.setPartOfSpeechTagger( this );
	}

	/**	See if tagger uses a probability transition matrix.
	 *
	 *	@return		True since bigram tagger uses probability transition
	 *				matrix.
	 */

	public boolean usesTransitionProbabilities()
	{
		return true;
	}

	/**	Tag a list of sentences.
	 *
	 *	@param	sentences	The list of sentences.
	 *
	 *	@return				The sentences with words adorned with
	 *							parts of speech.
	 *
	 *	<p>
	 *	The sentences are a {@link java.util.List} of
	 *	{@link java.util.List}s of words to be tagged.
	 *	Each sentence is represented as a list of
	 *	words.  The output is a list of
	 *	{@link edu.northwestern.at.utils.corpuslinguistics.adornedword.AdornedWord}s.
	 *	</p>
	 */

	public List<List<AdornedWord>> tagSentences( List<List<String>> sentences )
	{
								//	Tag the words in the sentences.

		List<List<AdornedWord>> result	= super.tagSentences( sentences );

								//	Report cache usage.
		if ( debug )
		{
			logger.logDebug
			(
				"      # of cached lexical probabilties   : " +
				lexicalSmoother.cachedProbabilitiesCount()
			);

			logger.logDebug
			(
				"      # of cached contextual probabilties: " +
				contextualSmoother.cachedProbabilitiesCount()
			);

			logger.logDebug
			(
				"   # of states rejected by beam search: " +
				beamSearchRejections
			);
		}

		return result;
	}

	/**	Tag a sentence.
	 *
	 *	@param	taggedSentence	The sentence as an
	 *							{@link edu.northwestern.at.utils.corpuslinguistics.adornedword.AdornedWord}.
	 *
	 *	@return					An {@link edu.northwestern.at.utils.corpuslinguistics.adornedword.AdornedWord}
	 *							of the words in the sentence tagged with
	 *							parts of speech.
	 *
	 *	<p>
	 *	The input sentence is a {@link java.util.List} of
	 *	string words to be tagged.  The output is
	 *	{@link edu.northwestern.at.utils.corpuslinguistics.adornedword.AdornedWord}
	 *	of the words with parts of speech added.
	 *	</p>
	 */

	public<T extends AdornedWord> List<T> tagAdornedWordList
	(
		List<T> taggedSentence
	)
	{
								//	Reset Viterbi trellis.
		viterbi.reset();
								//	Assume period as previous word tag.

		List<String> previousTags	=
			ListFactory.createNewList();

		previousTags.add( "." );

								//	Index of word in sentence.

		int wordIndex		= 0;

								//	Iterate over words in sentence.

		Iterator<? extends AdornedWord> wordsIter	=
			taggedSentence.iterator();

		List<String> tags	= null;

		while ( wordsIter.hasNext() )
		{
								//	Get next word.

			AdornedWord word	= wordsIter.next();

								//	Get part of speech tags for this
								//	this word.

			tags				=
				getTagsForWord( word.getStandardSpelling() );

								//	Process word.  The returned tags
								//	for the current word are those which
								//	passed the Viterbi beam search
								//	criterion.  These possibly pruned tags
								//	will be the previous tags for the
								//	next word.

			previousTags	=
				processWord
				(
					wordIndex++ ,
					word.getStandardSpelling() ,
					previousTags ,
					tags
				);
		}
								//	Retrieve optimal tags and
								//	output (word,tag) .

		List<String> optimalTags	= viterbi.optimalTags( wordIndex , tags );
		wordIndex					= 0;

		wordsIter					= taggedSentence.iterator();

		while ( wordsIter.hasNext() )
		{
								//	Get next word.

			AdornedWord word	= (AdornedWord)wordsIter.next();

								//	Add word and tag to tagged sentence.

			word.setPartsOfSpeech( optimalTags.get( wordIndex++ ) );
		}
								//	Increment total count of states
								//	rejections by beam search criterion.

		beamSearchRejections	+= viterbi.getBeamSearchRejections();

								//	We have a new finished sentence.

		return taggedSentence;
	}

	/**	Process a single word.
	 *
	 *	@param	wordIndex		Index of word in sentence (starts at 0).
	 *	@param	word			Word being processed.
	 *	@param	previousTags	The previous word's tags.
	 *	@param	tags			The current word's tags.
	 *
	 *	@return					Updated tag list.
	 */

	protected List<String> processWord
	(
		int wordIndex ,
		String word ,
		List<String> previousTags ,
		List<String> tags
	)
	{
								//	Find tag with largest probability
								//	combined with previous word's tag.

		contextualProbabilities.clear();

		int nTags					= tags.size();
		Probability[] lexicalProbs	= new Probability[ nTags ];

		for ( int i = 0 ; i < nTags ; i++ )
		{
			lexicalProbs[ i ]	=
				lexicalSmoother.lexicalProbability(
					word , (String)tags.get( i ) );

			for ( int j = 0 ; j < previousTags.size() ; j++ )
			{
				contextualProbabilities.put
				(
					tags.get( i ) ,
					previousTags.get( j ) ,
					contextualSmoother.contextualProbability
					(
						tags.get( i ) ,
						previousTags.get( j )
					)
				);
			}
		}

		return viterbi.updateScore
		(
			wordIndex ,
			lexicalProbs ,
			contextualProbabilities ,
			tags ,
			previousTags
		);
	}

	/**	Set the logger.
	 *
	 *	@param	logger		The logger.
	 */

	public void setLogger( Logger logger )
	{
		this.logger	= logger;

		((UsesLogger)lexicalSmoother).setLogger( logger );
		((UsesLogger)contextualSmoother).setLogger( logger );
		viterbi.setLogger( logger );
	}

	/**	Return tagger description.
	 *
	 *	@return		Tagger description.
	 */

	public String toString()
	{
		return "Bigram tagger";
	}
}

/*
Copyright (c) 2008, 2009 by Northwestern University.
All rights reserved.

Developed by:
   Academic and Research Technologies
   Northwestern University
   http://www.it.northwestern.edu/about/departments/at/

Permission is hereby granted, free of charge, to any person
obtaining a copy of this software and associated documentation
files (the "Software"), to deal with the Software without
restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or
sell copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following
conditions:

    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimers.

    * Redistributions in binary form must reproduce the above
      copyright notice, this list of conditions and the following
      disclaimers in the documentation and/or other materials provided
      with the distribution.

    * Neither the names of Academic and Research Technologies,
      Northwestern University, nor the names of its contributors may be
      used to endorse or promote products derived from this Software
      without specific prior written permission.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE CONTRIBUTORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS WITH THE SOFTWARE.
*/



