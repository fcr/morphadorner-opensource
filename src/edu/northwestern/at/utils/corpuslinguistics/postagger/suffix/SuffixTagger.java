package edu.northwestern.at.utils.corpuslinguistics.postagger.suffix;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.adornedword.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.unigram.*;
import edu.northwestern.at.utils.corpuslinguistics.tokenizer.*;

/**	Suffix Part of Speech tagger.
 *
 *	<p>
 *	The suffix part of speech tagger uses a suffix wordLexicon to assign
 *	a part of speech tag to a spelling
 *	based upon the most common part of speech for found for other words
 *	with the same last few characters of the spelling.
 *	</p>
 */

public class SuffixTagger extends UnigramTagger
	implements PartOfSpeechTagger, CanTagOneWord
{
	/**	Create a suffix tagger.
	 */

	public SuffixTagger()
	{
	}

	/**	Tag a single word.
	 *
	 *	@param	word	The word.
	 *
	 *	@return			The part of speech for the word.
	 */

	public String tagWord( String word )
	{
		String result	= "";
								//	If punctuation or symbol, return that
								//	unchanged.

		if ( CharUtils.isPunctuationOrSymbol( word ) )
		{
			result	= word;
		}
		else
		{
								//	Try suffixes of length four down to
								//	one to assign part of speech tag.

			int l	= word.length();

			for ( int i = Math.min( 10 , l ) ; i > 0 ; i-- )
			{
				String suffix	= word.substring( l - i , l );

				if ( lexicon.getEntryCount( suffix ) > 0 )
				{
					result	= lexicon.getLargestCategory( word );
					break;
				}
			}

			if ( result.length() == 0 )
			{
				result	= lexicon.getPartOfSpeechTags().getSingularNounTag();
			}
		}

		return result;
	}

	/**	Return tagger description.
	 *
	 *	@return		Tagger description.
	 */

	public String toString()
	{
		return "Suffix tagger";
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



