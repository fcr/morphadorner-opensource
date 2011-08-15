package edu.northwestern.at.utils.corpuslinguistics.postagger;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.logger.*;
import edu.northwestern.at.utils.corpuslinguistics.adornedword.*;
import edu.northwestern.at.utils.corpuslinguistics.lexicon.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.guesser.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.smoothing.contextual.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.smoothing.lexical.*;
import edu.northwestern.at.utils.corpuslinguistics.spellingstandardizer.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.transitionmatrix.*;
import edu.northwestern.at.utils.corpuslinguistics.tokenizer.*;

/**	Abstract Part of Speech tagger.
 *
 *	<p>
 *	Provides default implementations for all of the PartOfSpeech
 *	interface methods.  To create a new PartOfSpeech tagger,
 *	extend this class and override methods as needed.  You must
 *	override the tagSentence method as a minimum.
 *	</p>
 */

abstract public class AbstractPartOfSpeechTagger
	extends IsCloseableObject
	implements PartOfSpeechTagger, IsCloseable, UsesLexicon, UsesLogger
{
	/**	Static lexicon used by tagger. */

	protected Lexicon lexicon;

	/**	Dynamic lexicon built on-the-fly for words not in static lexicon. */

	protected Lexicon dynamicLexicon;

	/**	Transition matrix used by tagger. */

	protected TransitionMatrix transitionMatrix;

	/**	Context rules. */

	protected String[] contextRules;

	/**	Lexical rules. */

	protected String[] lexicalRules;

	/**	Lexical smoother. */

	protected LexicalSmoother lexicalSmoother;

	/**	Contextual smoother. */

	protected ContextualSmoother contextualSmoother;

	/**	Fixup retagger. */

	protected PartOfSpeechRetagger retagger;

	/**	Part of speech guesser for words not in lexicon. */

	protected PartOfSpeechGuesser partOfSpeechGuesser;

	/**	PostTokenizer for mapping raw tokens to initial spellings. */

	protected PostTokenizer postTokenizer;

	/**	Number of corrections applied by rules. */

	protected int ruleCorrections	= 0;

	/**	Logger used for output. */

	protected Logger logger;

	/**	Create tagger.
	 */

	public AbstractPartOfSpeechTagger()
	{
								//	Default lexicons are empty.

		LexiconFactory lexiconFactory	= new LexiconFactory();

		lexicon							= lexiconFactory.newLexicon();
		dynamicLexicon					= lexiconFactory.newLexicon();;

								//	Create post tokenizer.

		PostTokenizerFactory postTokenizerFactory	=
			new PostTokenizerFactory();

		postTokenizer	= postTokenizerFactory.newPostTokenizer();

								//	Create dummy logger.

		logger			= new DummyLogger();
	}

	/**	Get the logger.
	 *
	 *	@return		The logger.
	 */

	public Logger getLogger()
	{
		return logger;
	}

	/**	Set the logger.
	 *
	 *	@param	logger		The logger.
	 */

	public void setLogger( Logger logger )
	{
		this.logger	= logger;
	}

	/**	See if tagger uses context rules.
	 *
	 *	@return		True if tagger uses context rules.
	 */

	public boolean usesContextRules()
	{
		return false;
	}

	/**	See if tagger uses lexical rules.
	 *
	 *	@return		True if tagger uses lexical rules.
	 */

	public boolean usesLexicalRules()
	{
		return false;
	}

	/**	See if tagger uses a probability transition matrix.
	 *
	 *	@return		True if tagger uses probability transition matrix.
	 */

	public boolean usesTransitionProbabilities()
	{
		return false;
	}

	/**	Set context rules for tagging.
	 *
	 *	@param	contextRules	String array of context rules.
	 *
	 *	@throws	InvalidRuleException if a rule is bad.
	 *
	 *	<p>
	 *	For taggers which do not use context rules, this is a no-op.
	 *	</p>
	 */

	public void setContextRules( String[] contextRules )
		throws InvalidRuleException
	{
		this.contextRules	= contextRules;

								//	Set context rules in fixup retagger
								//	if it exists.

		if ( retagger != null )
		{
			retagger.setContextRules( contextRules );
		}
	}

	/**	Set lexical rules for tagging.
	 *
	 *	@param	lexicalRules	String array of lexical rules.
	 *
	 *	@throws	InvalidRuleException if a rule is bad.
	 *
	 *	<p>
	 *	For taggers which do not use lexical rules, this is a no-op.
	 *	</p>
	 */

	public void setLexicalRules( String[] lexicalRules )
		throws InvalidRuleException
	{
		this.lexicalRules	= lexicalRules;

								//	Set lexicl rules in fixup tagger
								//	if it exists.

		if ( retagger != null )
		{
			retagger.setLexicalRules( lexicalRules );
		}
	}

	/**	Get the static word lexicon.
	 *
	 *	@return		The static word lexicon.
	 */

	public Lexicon getLexicon()
	{
		return lexicon;
	}

	/**	Get the dynamic word lexicon.
	 *
	 *	@return		The dynamic lexicon.
	 */

	public Lexicon getDynamicLexicon()
	{
		return dynamicLexicon;
	}

	/**	Get the lexicon associated with a specific word.
	 *
	 *	@param		word	The word whose source lexicon is sought.
	 *
	 *	@return		The lexicon.
	 *
	 *	<p>
	 *	Most words do not have a source lexicon defined, in which
	 *	case they come from the main static word lexicon.
	 *	Usually only words derived by a suffix analysis have
	 *	a source lexicon defined, which will of course be the
	 *	suffix lexicon.
	 *	</p>
	 */

	public Lexicon getLexicon( String word )
	{
		Lexicon result	= lexicon;

		if ( partOfSpeechGuesser != null )
		{
			result	=
				partOfSpeechGuesser.getCachedLexiconForWord( word );
		}

		return result;
	}

	/**	Set the lexicon.
	 *
	 *	@param	lexicon		Lexicon used for tagging.
	 */

	public void setLexicon( Lexicon lexicon )
	{
		this.lexicon	= lexicon;

								//	Set lexicon into fixup tagger
								//	if it exists.

		if ( retagger != null )
		{
			retagger.setLexicon( this.lexicon );
		}
	}

	/**	Get tag transition probabilities matrix.
	 *
	 *	@return		Tag probabilities transition matrix.
	 *	        	May be null for taggers which do not use
	 *				a transition matrix.
	 */

	public TransitionMatrix getTransitionMatrix()
	{
		return transitionMatrix;
	}

	/**	Set tag transition probabilities matrix.
	 *
	 *	@param	transitionMatrix	Tag probabilities transition matrix.
	 *
	 *	<p>
	 *	For taggers which do not use transition matrices, this is a no-op.
	 *	</p>
	 */

	public void setTransitionMatrix( TransitionMatrix transitionMatrix )
	{
		this.transitionMatrix	= transitionMatrix;
	}

	/**	Get part of speech guesser.
	 *
	 *	@return		The part of speech guesser.
	 */

	public PartOfSpeechGuesser getPartOfSpeechGuesser()
	{
		return this.partOfSpeechGuesser;
	}

	/**	Set part of speech guesser.
	 *
	 *	@param	partOfSpeechGuesser		The part of speech guesser.
	 */

	public void setPartOfSpeechGuesser
	(
		PartOfSpeechGuesser partOfSpeechGuesser
	)
	{
		this.partOfSpeechGuesser	= partOfSpeechGuesser;
	}

	/**	Get part of speech retagger.
	 *
	 *	@return		The part of speech retagger.  May be null.
	 */

	public PartOfSpeechRetagger getRetagger()
	{
		return retagger;
	}

	/**	Set part of speech retagger.
	 *
	 *	@param	retagger	The part of speech retagger.
	 */

	public void setRetagger( PartOfSpeechRetagger retagger )
	{
		this.retagger	= retagger;
	}

	/**	Get potential part of speech tags for a word.
	 *
	 *	@param	word	The word whose part of speech tags we want.
	 *
	 *	@return			List of part of speech tags.
	 *					May be null or empty.
	 *
	 *	<p>
	 *	When the word does not appear in the lexicon, the
	 *	part of speech guesser is used to determine the tags
	 *	based upon features of the word (suffix analysis, etc.).
	 *	</p>
	 */

	public List<String> getTagsForWord( String word )
	{
								//	Get part of speech tags for this word
								//	from main or dynamic lexicon.

		Set<String> tagSet	= null;

								//	Word in main lexicon?

		if ( lexicon.containsEntry( word ) )
		{
			tagSet	= lexicon.getCategoriesForEntry( word );
		}
								//	Word in dynamic lexicon?

		else if ( dynamicLexicon.containsEntry( word ) )
		{
			tagSet	= dynamicLexicon.getCategoriesForEntry( word );
		}
								//	Word in neither lexicon.
								//	Get potential parts of speech
								//	and counts from guesser.
								//	Add the guesser results to the
								//	dynamic lexicon.
		else
		{
								//	If we don't have a part of speech
								//	guesser, create one now.

			if ( partOfSpeechGuesser == null )
			{
				createPartOfSpeechGuesser();
			}

			Map<String, MutableInteger> tagMap	=
				partOfSpeechGuesser.guessPartsOfSpeech( word );

			tagSet	= tagMap.keySet();

			Iterator<String> iterator	= tagSet.iterator();

			while ( iterator.hasNext() )
			{
				String category			= iterator.next();
				MutableInteger count	= tagMap.get( category );

				dynamicLexicon.updateEntryCount
				(
					word ,
					category ,
					"*" ,
					count.intValue()
				);
			}
		}

		List<String> result	= ListFactory.createNewList( tagSet );

		return result;
	}

	/**	Get count of times a word appears with a given tag.
	 *
	 *	@param	word	The word.
	 *	@param	tag		The part of speech tag.
	 *
	 *	@return			The number of times the word appears
	 *					with the given tag.
	 *
	 *	<p>
	 *	When the word does not appear in the lexicon, the
	 *	part of speech guesser is used to compute a count
	 *	based upon features of the word (suffix analysis, etc.).
	 *	</p>
	 */

	public int getTagCount( String word , String  tag )
	{
								//	Total number of times this word
								//	appeared with this tag in the
								//	training data.
		int result	= 0;

								//	Word in main lexicon?

		if ( lexicon.containsEntry( word ) )
		{
			result	= lexicon.getCategoryCount( word , tag );
		}
								//	Word in dynamic lexicon?

		else if ( dynamicLexicon.containsEntry( word ) )
		{
			result	= dynamicLexicon.getCategoryCount( word , tag );
		}
								//	Word in neither lexicon.
								//	Add the guesser results to the
								//	dynamic lexicon.
		else
		{
			getTagsForWord( word );

			result	= dynamicLexicon.getCategoryCount( word , tag );
		}

		return Math.max( result , 1 );
    }

	/**	Get the most common tag for a word.
	 *
	 *	@param	word	The word.
	 *
	 *	@return			The most common part of speech tag for the word.
	 */

	public String getMostCommonTag( String word )
	{
		String result	= "";

								//	Word in main lexicon?

		if ( lexicon.containsEntry( word ) )
		{
			result	= lexicon.getLargestCategory( word );
		}
								//	Word in dynamic lexicon?

		else if ( dynamicLexicon.containsEntry( word ) )
		{
			result	= dynamicLexicon.getLargestCategory( word );
		}
								//	Word in neither lexicon.
								//	Add the guesser results to the
								//	dynamic lexicon.
		else
		{
			getTagsForWord( word );

			result	= dynamicLexicon.getLargestCategory( word );
		}

		return result;
	}

	/**	Tag a list of sentences.
	 *
	 *	@param	sentences	The list of sentences.
	 *
	 *	@return				The sentences with words adorned with
	 *						parts of speech.
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
								//	Holds list of tagged sentences.

		List<List<AdornedWord>> output	= ListFactory.createNewList();

								//	Iterator over sentences.

		Iterator<List<String>> sentencesIter	= sentences.iterator();

								//	Tag each sentence in list of sentences.

		while ( sentencesIter.hasNext() )
		{
								//	Get next sentence,

			List<String> sentence	= sentencesIter.next();

								//	Tag sentence and add to output list.

			output.add( retagWords( tagSentence( sentence ) ) );
		}

		return output;
	}

	/**	Tag a list of sentences.
	 *
	 *	@param	sentences	The list of sentences.
	 *
	 *	@return				The sentences with words adorned with
	 *						parts of speech.
	 *
	 *	<p>
	 *	The sentences are a {@link java.util.List} of
	 *	{@link java.util.List}s of adorned words to be tagged.
	 *	Each sentence is represented as a list of
	 *	words.  The output is a list of
	 *	{@link edu.northwestern.at.utils.corpuslinguistics.adornedword.AdornedWord}s.
	 *	</p>
	 */

	public<T extends AdornedWord> List<List<T>> tagAdornedWordSentences
	(
		List<List<T>> sentences
	)
	{
								//	Iterator over sentences.

		Iterator<List<T>> sentencesIter	= sentences.iterator();

								//	Tag each sentence in list of sentences.

		while ( sentencesIter.hasNext() )
		{
								//	Get next sentence,

			List<T> sentence	= sentencesIter.next();

								//	Tag sentence and add to output list.

			retagWords( tagAdornedWordSentence( sentence ) );
		}

		return sentences;
	}

	/**	Retag words in a tagged sentence.
	 *
	 *	@param	taggedSentence	The tagged sentence.
	 *
	 *	@return					The retagged sentence.
	 *
	 *	<p>
	 *	This method calls the retagger, if any.  If no retagger
	 *	is defined, the input tagged sentence is returned unchanged.
	 *	Override this method to add custom retagging without
	 *	the use of a retagger.
	 *	</p>
	 */

	public<T extends AdornedWord> List<T> retagWords
	(
		List<T> taggedSentence
	)
	{
								//	Call fixup tagger to fix the
								//	tagging produced by the bigram
								//	tagger.

		if ( retagger != null )
		{
			return retagger.retagSentence( taggedSentence );
		}
		else
		{
			return taggedSentence;
		}
	}

	/**	Clear count of successful rule applications.
	 */

	public void clearRuleCorrections()
	{
		ruleCorrections	= 0;
	}

	/**	Increment count of successful rule applications.
	 */

	public void incrementRuleCorrections()
	{
		ruleCorrections++;
	}

	/**	Get count of successful rule applications.
	 */

	public int getRuleCorrections()
	{
		return ruleCorrections;
	}

	/**	Create a part of speech guesser.
	 */

	protected void createPartOfSpeechGuesser()
	{
		try
		{
			if ( partOfSpeechGuesser == null )
			{
				AbstractPartOfSpeechGuesser guesser	=
					new DefaultPartOfSpeechGuesser();

				if ( lexicon == null )
				{
					setLexicon( new DefaultWordLexicon() );
				}

				guesser.setWordLexicon( lexicon );
				guesser.setSuffixLexicon( new DefaultSuffixLexicon() );

				guesser.setLogger( logger );

				setPartOfSpeechGuesser( guesser );
			}
		}
		catch ( Exception e )
		{
		}
	}

	/**	Tag a sentence.
	 *
	 *	@param	sentence	The sentence as a list of string words.
	 *
	 *	@return				An {@link edu.northwestern.at.utils.corpuslinguistics.adornedword.AdornedWord}
	 *						of the words in the sentence tagged with
	 *						parts of speech.
	 *
	 *	<p>
	 *	The input sentence is a {@link java.util.List} of
	 *	string words to be tagged.  The output is
	 *	{@link edu.northwestern.at.utils.corpuslinguistics.adornedword.AdornedWord}
	 *	of the words with parts of speech added.
	 *	</p>
	 */

	public List<AdornedWord> tagSentence( List<String> sentence )
	{
								//	List of adorned word results.

		List<AdornedWord> taggedSentence	= ListFactory.createNewList();

								//	Create initial adorned word list
								//	from string tokens in sentence.
		String token;
		String spelling;
		String standardSpelling;

		for ( int i = 0 ; i < sentence.size() ; i++ )
		{
								//	Get next token in input sentence.

			token				= (String)sentence.get( i );
			spelling			= token;
			standardSpelling	= token;

								//	Apply post tokenization to
								//	get spelling.

			if ( postTokenizer != null )
			{
				String[] spellings	= postTokenizer.postTokenize( token );

				spelling			= spellings[ 0 ];
				standardSpelling	= spellings[ 1 ];
			}
								//	Create adorned word from token
								//	and spelling.

			AdornedWord word	= new BaseAdornedWord( token );

			word.setSpelling( spelling );
			word.setStandardSpelling( standardSpelling );

								//	Add adorned word to output sentence.

			taggedSentence.add( word );
		}
								//	Obtain part of speech tag for
								//	each word in sentence.

		tagAdornedWordList( taggedSentence );

		return taggedSentence;
	}

	/**	Tag a sentence.
	 *
	 *	@param	sentence	The sentence as a list of string words.
	 *
	 *	@return				An {@link edu.northwestern.at.utils.corpuslinguistics.adornedword.AdornedWord}
	 *						of the words in the sentence tagged with
	 *						parts of speech.
	 *
	 *	<p>
	 *	The input sentence is a {@link java.util.List} of
	 *	adorned words to be tagged.  The output is
	 *	the same list with parts of speech added/modified.
	 *	</p>
	 */

	public<T extends AdornedWord> List<T> tagAdornedWordSentence
	(
		List<T> sentence
	)
	{
								//	Create initial adorned word list
								//	from string tokens in sentence.
		String token;
		String spelling;
		String standardSpelling;

		for ( int i = 0 ; i < sentence.size() ; i++ )
		{
			AdornedWord word	= sentence.get( i );

								//	Get next token in input sentence.

			token				= word.getToken();
			spelling			= token;
			standardSpelling	= token;

								//	Apply post tokenization to
								//	get spelling.

			if ( postTokenizer != null )
			{
				String[] spellings	= postTokenizer.postTokenize( token );

				spelling			= spellings[ 0 ];
				standardSpelling	= spellings[ 1 ];
			}
								//	Set spellings into adorned word.

			word.setSpelling( spelling );
			word.setStandardSpelling( standardSpelling );
		}
								//	Obtain part of speech tag for
								//	each word in sentence.

		tagAdornedWordList( sentence );

		return sentence;
	}

	/**	Tag a list of adorned words.
	 *
	 *	@param	sentence	The sentence as an
	 *						{@link edu.northwestern.at.utils.corpuslinguistics.adornedword.AdornedWord}.
	 *
	 *	@return				The tagged sentence (same as input with
	 *						parts of speech added).
	 */

	abstract public<T extends AdornedWord> List<T> tagAdornedWordList
	(
		List<T> sentence
	);
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



