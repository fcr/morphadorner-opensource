package edu.northwestern.at.utils.corpuslinguistics.postagger.guesser;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.lexicon.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.*;
import edu.northwestern.at.utils.corpuslinguistics.spellingstandardizer.*;
import edu.northwestern.at.utils.corpuslinguistics.tokenizer.*;

/**	Interface for a PartOfSpeechGuesser.
 *
 *	<p>
 *	A part of speech guesser "guesses" the probable part(s) of speech
 *	for a word which does not appear in the main lexicon.  Alternate
 *	spellings, lexical rules based upon word prefixes or suffixes,
 *	and many other approaches may be used to find potential
 *	part of speech.
 *	</p>
 */

public interface PartOfSpeechGuesser
{
	/**	Guesses part of speech for a word.
	 *
	 *	@param	word			The word.
	 *
	 *	@return					Map of part of speech tags and counts.
	 */

	 public Map<String, MutableInteger> guessPartsOfSpeech( String word );

	/**	Guesses part of speech for a word.
	 *
	 *	@param	word			The word.
	 *	@param	isFirstWord		If word is first word in a sentence.
	 *
	 *	@return					Map of part of speech tags and counts.
	 */

	 public Map<String, MutableInteger> guessPartsOfSpeech
	 (
	 	String word ,
	 	boolean isFirstWord
	 );

	/**	Guesses part of speech for a word in a sentence.
	 *
	 *	@param	sentence		Sentence as a list of words.
	 *	@param	wordIndex		The word index in the sentence.
	 *
	 *	@return					Map of part of speech tags and counts.
	 */

	public Map<String, MutableInteger> guessPartsOfSpeech
	(
		List<String> sentence ,
		int wordIndex
	);

	/**	Get spelling standardizer.
	 *
	 *	@return		The spelling standardizer.
	 */

	public SpellingStandardizer getSpellingStandardizer();

	/**	Set spelling standardizer.
	 *
	 *	@param	spellingStandardizer		The spelling standardizer.
	 */

	public void setSpellingStandardizer
	(
		SpellingStandardizer spellingStandardizer
	);

	/**	Get the word lexicon.
	 *
	 *	@return		The word lexicon.
	 */

	public Lexicon getWordLexicon();

	/**	Set the word lexicon.
	 *
	 *	@param	wordLexicon		The word lexicon.
	 */

	public void setWordLexicon( Lexicon wordLexicon );

	/**	Get the suffix lexicon.
	 *
	 *	@return		The suffix lexicon.
	 */

	public Lexicon getSuffixLexicon();

	/**	Get cached lexicon for a word.
	 *
	 *	@param		word	The word whose source lexicon we want.
	 *
	 *	@return		The lexicon for the word.
	 */

	public Lexicon getCachedLexiconForWord( String word );

	/**	Set the suffix lexicon.
	 *
	 *	@param	suffixLexicon	The suffix lexicon.
	 */

	public void setSuffixLexicon( Lexicon suffixLexicon );

	/**	Add an auxiliary word list.
	 */

	public void addAuxiliaryWordList( TaggedStrings wordList );

	/**	Get auxiliary word lists.
	 */

	public List getAuxiliaryWordLists();

	/**	Try using standardized spellings when guessing parts of speech.
	 */

	public void setTryStandardSpellings( boolean tryStandardSpellings );

	/**	Check for possessives of known nouns when guessing parts of speech.
	 */

	public void setCheckPossessives( boolean checkPossessives );
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



