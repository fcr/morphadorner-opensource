package edu.northwestern.at.utils.corpuslinguistics.postagger.simple;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.adornedword.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.*;
import edu.northwestern.at.utils.corpuslinguistics.tokenizer.*;

/**	Simple Part of Speech tagger.
 *
 *	<p>
 *	The simple part of speech tagger assigns a "noun" type
 *	part of speech to all words, except those that appear to
 *	be numbers.  Numbers are assigned a "number" part of speech.
 *	Words starting with a capital letter can be assigned a
 *	separate "proper name" part of speech.
 *	</p>
 *
 *	<p>
 *	This simple tagger is useful as a backup for a more
 *	sophisticated tagger when unknown words are encountered.
 *	</p>
 */

public class SimpleTagger extends AbstractPartOfSpeechTagger
	implements PartOfSpeechTagger, CanTagOneWord
{
	/**	Noun part of speech tag. */

	protected static String nounPOS;

	/**	Proper name part of speech tag. */

	protected static String namePOS;

	/**	Number part of speech tag. */

	protected static String numberPOS;

	/**	Create a simple tagger.
	 */

	public SimpleTagger()
	{
	}

	/**	Create a simple tagger.
	 *
	 *	@param	nounPOS		Part of speech for a noun.
	 *	@param	namePOS		Part of speech for a proper name.
	 *	@param	numberPOS	Part of speech tag for a number.
	 */

	public SimpleTagger
	(
		String nounPOS ,
		String namePOS ,
		String numberPOS
	)
	{
		this.nounPOS	= nounPOS;
		this.namePOS	= namePOS;
		this.numberPOS	= numberPOS;
	}

	/**	Tag a sentence.
	 *
	 *	@param	sentence	The sentence as an {@link edu.northwestern.at.utils.corpuslinguistics.adornedword.AdornedWord}
	 *
	 *	@return				The input List<AdornedWord> with its
	 *							words tagged with parts of speech.
	 */

	public<T extends AdornedWord> List<T> tagAdornedWordList
	(
		List<T> sentence
	)
	{
								//	Loop over words in sentence.

		for ( int i = 0 ; i < sentence.size() ; i++ )
		{
										//	Get next word.

			AdornedWord word	= sentence.get( i );

								//	Assign part of speech tag.

			tagWord( word );
		}
								//	Return tagged sentence.
		return sentence;
	}

	/**	Tag a single word.
	 *
	 *	@param	word	The word.
	 *
	 *	@return			The part of speech for the word.
	 */

	public String tagWord( String word )
	{
								//	Default tag is noun.

		String tag	= nounPOS;

								//	If word starts with a capital
								//	letter, assume it is a proper name.

		if ( Character.isUpperCase( word.charAt( 0 ) ) )
		{
			tag	= namePOS;
		}
								//	Check for number.

		if ( CharUtils.isNumber( word ) )
		{
			tag	= numberPOS;
		}
		else if ( CharUtils.isPunctuationOrSymbol( word ) )
		{
			tag	= word;
		}

		return tag;
	}

	/**	Tag a single adorned word.
	 *
	 *	@param	word	The adorned word.
	 *
	 *	@return			The adorned word with the part of speech
	 *					assigned.
	 */

	 public String tagWord( AdornedWord word )
	 {
	 	String tag	= tagWord( word.getSpelling() );

	 	word.setPartsOfSpeech( tag );

	 	return tag;
	 }

	/**	Return tagger description.
	 *
	 *	@return		Tagger description.
	 */

	public String toString()
	{
		return "Simple tagger";
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



