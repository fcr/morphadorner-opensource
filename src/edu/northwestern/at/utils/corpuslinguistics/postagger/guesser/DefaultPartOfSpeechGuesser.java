package edu.northwestern.at.utils.corpuslinguistics.postagger.guesser;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.lexicon.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.*;
import edu.northwestern.at.utils.corpuslinguistics.spellingstandardizer.*;

/**	Default part of speech guesser.
 *
 *	<p>
 *	This default guesser returns a noun, number or punctuation tag.
 *	The lexicon provides the part of speech tags for these items.
 *	</p>
 */

public class DefaultPartOfSpeechGuesser
	extends AbstractPartOfSpeechGuesser
	implements PartOfSpeechGuesser
{
	/**	Guesses part of speech for a word.
	 *
	 *	@param	theWord		The word.
	 *
	 *	@return					Guessed parts of speech.
	 */

	public Map<String, MutableInteger> guessPartsOfSpeech( String theWord )
	{
		if ( debug )
		{
			logger.logDebug( "guessPartsOfSpeech: word=" + theWord );
		}

		String word	= theWord;

								//	Is word in cache?  Return
								//	existing parts of speech if so.

		Map<String, MutableInteger> result	= checkCachedWord( word );

		if ( result != null )
		{
			if ( debug )
			{
				logger.logDebug
				(
					"guessPartsOfSpeech:   in cache, pos=" + result
				);
			}

			if ( result.size() == 0 ) result = null;
		}

		if ( result != null ) return result;

								//	If the word is in the word lexicon,
								//	get the parts of speech from
								//	the lexicon.

		if ( wordLexicon != null )
		{
			if ( wordLexicon.containsEntry( word ) )
			{
				result	=
					clonePosTagMap
					(
						wordLexicon.getCategoryCountsForEntry( word )
					);
			}

			if ( result != null )
			{
				if ( debug )
				{
					logger.logDebug
					(
						"guessPartsOfSpeech:   in word lexicon, pos=" + result
					);
				}
			}

			if ( result != null ) return result;

								//	If the word is all caps,
								//	change it to first letter capitalized
								//	only for the remainder of the
								//	checks.

//			word	= EnglishDecruftifier.decruftify( word );

			if ( CharUtils.allLettersCapital( word ) )
			{
				word	= CharUtils.capitalizeFirstLetter( word );

								//	Look up recapitalized word
								//	in lexicon.

				if ( wordLexicon.containsEntry( word ) )
				{
					result	=
						clonePosTagMap
						(
							wordLexicon.getCategoryCountsForEntry( word )
						);
				}

				if ( result != null )
				{
					if ( debug )
					{
						logger.logDebug
						(
							"guessPartsOfSpeech:   recapitalized in word " +
							"lexicon, pos=" + result
						);
					}
				}

				if ( result != null ) return result;
			}
		}
								//	If punctuation, return that
								//	unchanged.

		result	= checkPunctuation( word );

		if ( result != null )
		{
			if ( debug )
			{
				logger.logDebug
				(
					"guessPartsOfSpeech:   is punctuation, pos=" + result
				);
			}
		}

		if ( result != null ) return result;

								//	If symbol, return that
								//	unchanged.

		result	= checkSymbol( word );

		if ( result != null )
		{
			if ( debug )
			{
				logger.logDebug
				(
					"guessPartsOfSpeech:   is symbol, pos=" + result
				);
			}
		}

		if ( result != null ) return result;

								//	See if it is a number.

		result	= checkNumber( word );

		if ( result != null )
		{
			if ( debug )
			{
				logger.logDebug
				(
					"guessPartsOfSpeech:   is number, pos=" + result
				);
			}
		}

		if ( result != null ) return result;

								//	See if it is currency.

		result	= checkCurrency( word );

		if ( result != null )
		{
			if ( debug )
			{
				logger.logDebug
				(
					"guessPartsOfSpeech:   is currency, pos=" + result
				);
			}
		}

		if ( result != null ) return result;

								//	See if it is an abbreviation.

		result	= checkAbbreviation( word );

		if ( result != null )
		{
			if ( debug )
			{
				logger.logDebug
				(
					"guessPartsOfSpeech:   is abbreviation, pos=" + result
				);
			}
		}

		if ( result != null ) return result;

								//	See if it is a Roman Numeral.

		result	= checkRomanNumeral( word );

		if ( result != null )
		{
			if ( debug )
			{
				logger.logDebug
				(
					"guessPartsOfSpeech:   is Roman numeral, pos=" +
					result
				);
			}
		}

		if ( result != null ) return result;

								//	Check for hyphenated word.

		result	= checkHyphenatedWord( word );

		if ( result != null )
		{
			if ( debug )
			{
				logger.logDebug
				(
					"guessPartsOfSpeech:   is hyphenated word, pos=" +
					result
				);
			}
		}

		if ( result != null ) return result;

								//	See if we have any standardized
								//	spellings for this word.  If so,
								//	and the word is in the word lexicon,
								//	return the parts of speech for
								//	the standardized spellings.

		String[] standardSpellings	= getStandardizedSpellings( word );

		if ( standardSpellings != null )
		{
			result	= checkStandardSpellings( word , standardSpellings );

			if ( result != null )
			{
				if ( debug )
				{
					logger.logDebug
					(
						"guessPartsOfSpeech:   from standard spellings, " +
						"pos=" + result
					);
				}
			}

			if ( result != null ) return result;
		}
								//	Standard spelling not found in
								//	lexicon.

								//	See if it is a proper name.

		result	= checkName( word );

		if ( result != null )
		{
			if ( debug )
			{
				logger.logDebug
				(
					"guessPartsOfSpeech:   is name, pos=" + result
				);
			}
		}

		if ( result != null ) return result;

								//	See if it is a possessive.

		result	= checkPossessiveNoun( word );

		if ( result != null )
		{
			if ( debug )
			{
				logger.logDebug
				(
					"guessPartsOfSpeech:   is possessive, pos=" + result
				);
			}
		}

		if ( result != null ) return result;

								//	See if word is in the auxiliary
								//	word lists.

		result	= checkAuxiliaryWordLists( word );

		if ( result != null )
		{
			if ( debug )
			{
				logger.logDebug
				(
					"guessPartsOfSpeech:   from auxiliary word lists, " +
					"pos=" + result
				);
			}
		}

		if ( result != null ) return result;

								//	Try looking at successively shorter
								//	suffixes and assign part of speech tags
								//	for the longest matching suffix in
								//	the suffix lexicon, if any.

		if ( suffixLexicon != null )
		{
			result	= checkSuffixes( word , standardSpellings );

			if ( result != null )
			{
				if ( debug )
				{
					logger.logDebug
					(
						"guessPartsOfSpeech:   from suffix analysis, " +
						"pos=" + result
					);
				}
			}

			if ( result != null ) return result;
		}
								//	No suffix matched.
								//	If all uppercase, assume it is
								//	a noun.

		result	= checkAllUpperCase( word );

		if ( result != null )
		{
			if ( debug )
			{
				logger.logDebug
				(
					"guessPartsOfSpeech:   is all uppercase, pos=" +
					result
				);
			}
		}

		if ( result != null ) return result;

								//	Nothing matched so far.
								//	Assume word is a noun.

		result	= getNoun( word );

		if ( result != null )
		{
			if ( debug )
			{
				logger.logDebug
				(
					"guessPartsOfSpeech:   assigning noun, pos=" + result
				);
			}
		}

		return result;
	}

	/**	Guesses part of speech for a word in a sentence.
	 *
	 *	@param	sentence		Sentence as a list of words.
	 *	@param	wordIndex		The word index in the sentence.
	 *
	 *	@return					Guessed parts of speech.
	 */

	 public Map<String, MutableInteger> guessPartsOfSpeech
	 (
	 	List<String> sentence ,
	 	int wordIndex
	 )
	 {
	 	String word	= (String)sentence.get( wordIndex );

								//	Check for capitalized word not
								//	first word in sentence.
/*
	 	if ( wordIndex > 0 )
		{
								//	If word starts with a capital
								//	letter, assume it is a proper name.

			if ( ( 'A' <= word.charAt( 0 ) ) && ( word.charAt( 0 ) <= 'Z' ) )
			{
				String[] result	=
					new String[]{ wordLexicon.getSingularProperNounPOS() };

				addCachedWord( word , result );

				return result;
			}
		}
*/
		return guessPartsOfSpeech( word );
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



