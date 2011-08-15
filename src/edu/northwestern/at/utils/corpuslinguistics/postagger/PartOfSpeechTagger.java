package edu.northwestern.at.utils.corpuslinguistics.postagger;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.adornedword.*;
import edu.northwestern.at.utils.corpuslinguistics.lexicon.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.guesser.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.transitionmatrix.*;
import edu.northwestern.at.utils.corpuslinguistics.tokenizer.*;

/**	Interface for a Part of Speech tagger.
 */

public interface PartOfSpeechTagger
{
	/**	See if tagger uses context rules.
	 *
	 *	@return		True if tagger uses context rules.
	 */

	public boolean usesContextRules();

	/**	See if tagger uses lexical rules.
	 *
	 *	@return		True if tagger uses lexical rules.
	 */

	public boolean usesLexicalRules();

	/**	See if tagger uses a probability transition matrix.
	 *
	 *	@return		True if tagger uses probability transition matrix.
	 */

	public boolean usesTransitionProbabilities();

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
		throws InvalidRuleException;

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
		throws InvalidRuleException;

	/**	Get the lexicon.
	 *
	 *	@return		The lexicon.	May be null if tagger does not
	 *									a lexicon.
	 */

	public Lexicon getLexicon();

	/**	Get the lexicon for a specific word.
	 *
	 *	@param		word			The word whose associated
	 *									lexicon we want.
	 *
	 *	@return		The lexicon.	May be null if tagger does not
	 *									a lexicon.
	 */

	public Lexicon getLexicon( String word );

	/**	Set the lexicon.
	 *
	 *	@param	lexicon		Lexicon used for tagging.
	 */

	public void setLexicon( Lexicon lexicon );

	/**	Get tag transition probabilities matrix.
	 *
	 *	@return		Tag probabilities transition matrix.
	 *					May be null for taggers which do not use
	 *					a transition matrix.
	 */

	public TransitionMatrix getTransitionMatrix();

	/**	Set tag transition probabilities matrix.
	 *
	 *	@param	transitionMatrix	Tag probabilities transition matrix.
	 *
	 *	<p>
	 *	For taggers which do not use transition matrices, this is a no-op.
	 *	</p>
	 */

	public void setTransitionMatrix( TransitionMatrix transitionMatrix );

	/**	Get part of speech guesser.
	 *
	 *	@return		The part of speech guesser.
	 */

	public PartOfSpeechGuesser getPartOfSpeechGuesser();

	/**	Set part of speech guesser.
	 *
	 *	@param	guesser		The part of speech guesser.
	 */

	public void setPartOfSpeechGuesser( PartOfSpeechGuesser guesser );

	/**	Get part of speech retagger.
	 *
	 *	@return		The part of speech retagger.  May be null.
	 */

	public PartOfSpeechRetagger getRetagger();

	/**	Set part of speech retagger.
	 *
	 *	@param	retagger	The part of speech retagger.
	 */

	public void setRetagger( PartOfSpeechRetagger retagger );

	/**	Get potential part of speech tags for a word.
	 *
	 *	@param	word	The word whose part of speech tags we want.
	 *
	 *	@return			The list of part of speech tags.  May be empty.
	 */

	public List<String> getTagsForWord( String word );

	/**	Get count of times a word appears with a given tag.
	 *
	 *	@param	word	The word.
	 *	@param	tag		The part of speech tag.
	 *
	 *	@return			The number of times the word appears
	 *					with the given tag.
	 */

	public int getTagCount( String word , String  tag );

	/**	Clear count of successful rule applications.
	 */

	public void clearRuleCorrections();

	/**	Increment count of successful rule applications.
	 */

	public void incrementRuleCorrections();

	/**	Get count of successful rule applications.
	 */

	public int getRuleCorrections();

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

	public List<List<AdornedWord>> tagSentences( List<List<String>> sentences );

	/**	Tag a sentence.
	 *
	 *	@param	sentence	The sentence as a List of string tokens.
	 *
	 *	@return				The tagged sentence as an
	 *						{@link edu.northwestern.at.utils.corpuslinguistics.adornedword.AdornedWord}.
	 */

	public List<AdornedWord> tagSentence( List<String> sentence );

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
	);

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
	);

	/**	Tag a list of adorned words.
	 *
	 *	@param	sentence	The sentence as an
	 *						{@link edu.northwestern.at.utils.corpuslinguistics.adornedword.AdornedWord}.
	 *
	 *	@return				The tagged sentence (same as input with
	 *						parts of speech added).
	 */

	public<T extends AdornedWord> List<T> tagAdornedWordList
	(
		List<T> sentence
	);

	/**	Retag words in a tagged sentence.
	 *
	 *	@param	taggedSentence	The tagged sentence as an
	 *							{@link edu.northwestern.at.utils.corpuslinguistics.adornedword.AdornedWord}.
	 *
	 *	@return					The retagged sentence.
	 */

	public<T extends AdornedWord> List<T> retagWords
	(
		List<T> taggedSentence
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



