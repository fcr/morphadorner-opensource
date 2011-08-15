package edu.northwestern.at.utils.corpuslinguistics.postagger.trigram;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.logger.*;
import edu.northwestern.at.utils.corpuslinguistics.adornedword.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.smoothing.contextual.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.smoothing.lexical.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.smoothing.*;
import edu.northwestern.at.utils.corpuslinguistics.tokenizer.*;
import edu.northwestern.at.utils.math.*;

/**	Trigram Part of Speech tagger.
 *
 *	<p>
 *	The tigram part of speech tagger assigns tags to words in a sentence
 *	assigning the most probable set of tags as determined by a trigram
 *	hidden Markov model given the possible tags of the previous words.
 *	The Viterbi algorithm is used to reduce the
 *	amount of computation required to find the optimal tag assignments.
 *	</p>
 */

public class TrigramTagger
	extends AbstractPartOfSpeechTagger
	implements PartOfSpeechTagger
{
	/**	True for debug output. */

	protected boolean debug		= false;

	/**	Contextual probabilities for a word in a sentence.
	 */

	protected Map3D<String, String, String, Probability>
		contextualProbabilities	= Map3DFactory.createNewMap3D();

	/**	Total number of states rejected by beam search criterion.
	 */

	protected int beamSearchRejections	= 0;

	/**	Viterbi trellis for tags and probability scores.
     */

	protected Viterbi viterbi	= new Viterbi();

	/**	Count of lines tagged. */

	protected int linesTagged	= 0;

	/**	Count of words tagged. */

	protected int wordsTagged	= 0;

	/**	Create a trigram tagger.
	 */

	public TrigramTagger()
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
	 *	@return		True since trigram tagger uses a probability
	 *					transition matrix.
	 */

	public boolean usesTransitionProbabilities()
	{
		return true;
	}

	/**	Report end of tagging statistics.
	 */

	protected void reportEndOfTaggingStats()
	{
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
				"      # of states rejected by beam search: " +
				beamSearchRejections
			);

			if ( retagger != null )
			{
				logger.logDebug
				(
					"      # of corrections applied by rules  : " +
					retagger.getRuleCorrections()
				);
			}
		}

		logger.logInfo
		(
			"      lines: " +
			Formatters.formatIntegerWithCommas( linesTagged ) +
			"; words: " +
			Formatters.formatIntegerWithCommas( wordsTagged )
		);
	}

	/**	Tag a list of sentences.
	 *
	 *	@param	sentences	The list of sentences.
	 *
	 *	<p>
	 *	The sentences are a {@link java.util.List} of
	 *	{@link java.util.List}s of words to be tagged.
	 *	Each sentence is represented as a list of
	 *	words.
	 *	</p>
	 */

	public List<List<AdornedWord>> tagSentences( List<List<String>> sentences )
	{
								//	Tag the words in the sentences.

		List<List<AdornedWord>> result	= super.tagSentences( sentences );

								//	Report end of tagging statistics.

		reportEndOfTaggingStats();

		return result;
	}

	/**	Tag a list of sentences containing adorned words.
	 *
	 *	@param	sentences	The list of sentences.
	 *
	 *	<p>
	 *	The sentences are a {@link java.util.List} of
	 *	{@link java.util.List}s of adorn words to be tagged.
	 *	Each sentence is represented as a list of
	 *	words.
	 *	</p>
	 */

	public<T extends AdornedWord> List<List<T>> tagAdornedWordSentences
	(
		List<List<T>> sentences
	)
	{
								//	Tag the words in the sentences.

		List<List<T>> result	=
			super.tagAdornedWordSentences( sentences );

								//	Report end of tagging statistics.

		reportEndOfTaggingStats();

		return result;
	}

	/**	Tag a sentence comprised of a list of adorned words.
	 *
	 *	@param	taggedSentence	The sentence as an
	 *							{@link edu.northwestern.at.utils.corpuslinguistics.adornedword.AdornedWord}.
	 *
	 *	@return					An {@link edu.northwestern.at.utils.corpuslinguistics.adornedword.AdornedWord}
	 *							of the words in the sentence tagged with
	 *							parts of speech.
	 *
	 *	<p>
	 *	The input sentence is a
	 *	{@link edu.northwestern.at.utils.corpuslinguistics.adornedword.AdornedWord}
	 *	of words to be tagged.  The output is the same list of words with
	 *	parts of speech added.
	 *	</p>
	 */

	public<T extends AdornedWord> List<T> tagAdornedWordList
	(
		List<T> taggedSentence
	)
	{
								//	Reset Viterbi trellis.
		viterbi.reset();
								//	Assume period as previous word tags.

		List<String> previousTags	=
			ListFactory.createNewList();

		previousTags.add( "." );

		List<String> previousPreviousTags	=
			ListFactory.createNewList();

		previousPreviousTags.add( "." );

								//	Part of speech tags for a word.

		List<String> tags	= null;
		AdornedWord word	= null;

								//	Loop over words in sentence.

		for ( int i = 0 ; i < taggedSentence.size() ; i++ )
		{
								//	Get next word.

			word	= taggedSentence.get( i );

								//	Get part of speech tags for this
								//	this word.

			tags	= getTagsForWord( word.getStandardSpelling() );

								//	Process word.  The returned tags
								//	for the current word are those which
								//	passed the Viterbi beam search
								//	criterion.  These possibly pruned tags
								//	will be the previous tags for the
								//	next word.
			tags	=
				processWord
				(
					i ,
					word.getStandardSpelling() ,
					previousPreviousTags ,
					previousTags ,
					tags
				);
								//	These tags will be previous tags
								//	for the next word.

			previousPreviousTags	= previousTags;
			previousTags			= tags;
		}
								//	Retrieve optimal part of speech tags and
								//	adorn each word with its proper tag.

		List<String> optimalTags	=
			viterbi.optimalTags( taggedSentence.size() , tags );

		for ( int i = 0 ; i < taggedSentence.size() ; i++ )
		{
								//	Get next word.

			word	= taggedSentence.get( i );

								//	Add part of speech tag to word.

			word.setPartsOfSpeech( (String)optimalTags.get( i ) );
		}
								//	Increment total count of states
								//	rejections by beam search criterion.

		beamSearchRejections	+= viterbi.getBeamSearchRejections();

								//	Increment counts of lines and
								//	word tagged.
		linesTagged++;
		wordsTagged	+= taggedSentence.size();

		if ( ( linesTagged % 1000 ) == 0 )
		{
			logger.logInfo
			(
				"      lines: " +
				Formatters.formatIntegerWithCommas( linesTagged ) +
				"; words: " +
				Formatters.formatIntegerWithCommas( wordsTagged )
			);
		}
								//	We have a new finished sentence.

		return taggedSentence;
	}

	/**	Process a single word.
	 *
	 *	@param	wordIndex				Index of word in sentence
	 *										(starts at 0).
	 *	@param	word					Word being processed.
	 *	@param	previousPreviousTags	The previous word's previous
	 *										word's tags.
 	 *	@param	previousTags			The previous word's tags.
	 *	@param	tags					The current word's tags.
	 *
	 *	@return							Updated tag list.
	 */

	protected List<String> processWord
	(
		int wordIndex ,
		String word ,
		List<String> previousPreviousTags ,
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
			String tagI	= (String)tags.get( i );

			lexicalProbs[ i ]	=
				lexicalSmoother.lexicalProbability( word , tagI );

			for ( int j = 0 ; j < previousTags.size() ; j++ )
			{
				String previousTagJ	= (String)previousTags.get( j );

				for ( int k = 0 ; k < previousPreviousTags.size() ; k++ )
				{
					contextualProbabilities.put
					(
						tagI,
						previousTagJ ,
						previousPreviousTags.get( k ) ,
						contextualSmoother.contextualProbability
						(
							tagI ,
							previousTagJ ,
							previousPreviousTags.get( k )
						)
					);
				}
			}
		}

		return viterbi.updateScore
		(
			wordIndex ,
			lexicalProbs ,
			contextualProbabilities ,
			tags ,
			previousTags ,
			previousPreviousTags
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
		return "Trigram tagger";
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



