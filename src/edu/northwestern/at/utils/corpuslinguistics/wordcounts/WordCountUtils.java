package edu.northwestern.at.utils.corpuslinguistics.wordcounts;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.net.*;
import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.stopwords.*;

/**	Word count utilities.
 */

public class WordCountUtils
{
	/**	Count words in sentences.
	 *
	 *	@param	sentences	The sentences.
	 *	@param	stopWords	Stop words.
	 *
	 *	@return				Map of words to WordCountAndSentence objects.
	 */

	public static <W extends Comparable> Map<String , WordCountAndSentences>
	countWordsInSentences
	(
		List<List<W>> sentences ,
		StopWords stopWords
	)
	{
								//	Holds map between each word
								//	and the word's count and appearance.

		Map<String , WordCountAndSentences> wordCounts	=
			new TreeMap<String , WordCountAndSentences>();

								//	Note if we are filtering using
								//	a stop word list.

		boolean checkStopWords	= ( stopWords != null );

								//	Loop over sentences.

		for ( int i = 0 ; i < sentences.size() ; i++ )
		{
								//	Get next sentence.

			List<W> sentence	= sentences.get( i );

								//	Loop over words in sentence.

			for ( int j = 0 ; j < sentence.size() ; j++ )
			{
								//	Get next word.

				W word			= sentence.get( j );

								//	Get string version of word in
								//	lower case.

				String lcWord	= word.toString().toLowerCase();

								//	Ignore punctuation and symbols.

				if ( CharUtils.isPunctuationOrSymbol( lcWord ) )
				{
				}
								//	Ignore stop words.

				else if ( checkStopWords && stopWords.isStopWord( lcWord ) )
				{
				}
				else
				{
								//	Create/update count and appearance data
								//	for this word.

					WordCountAndSentences wcs	= wordCounts.get( lcWord );

					if ( wcs == null )
					{
						wcs	= new WordCountAndSentences( lcWord );
						wordCounts.put( lcWord , wcs );
					}

					wcs.count++;
					wcs.sentences.add( i );
				}
			}
		}

		return wordCounts;
	}

	/**	Allow overrides but not instantiation.
	 */

	protected WordCountUtils()
	{
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




