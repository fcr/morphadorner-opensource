package edu.northwestern.at.utils.corpuslinguistics.ngram;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;
import java.net.*;

import edu.northwestern.at.utils.*;

/**	Counts words in a text.
 */

public class WordCountExtractor
{
	/**	The list of words and word counts in the text.
	 *
	 *	<p>
	 *	Key=word<br />
	 *	Value=Integer(count)
	 *	</p>
	 */

	protected Map<String, Integer> wordCounts	=
		new TreeMap<String, Integer>();

	/** The text parsed into a string array of words.
	 *
	 *	Package scope for the benefit of NGramExtractor.
	 */

	String[] words	= null;

	/** String array of unique words. */

	protected String[] uniqueWords	= null;

	/**	Create word count extractor.
	 */

	public WordCountExtractor()
	{
	}

	/**	Extract word counts from a string array of words.
	 *
	 *	@param	words		The string array with the words.
	 */

	public void countWords
	(
		String[] words
	)
	{
		this.words	= (String[])words.clone();

		computeWordCounts();
	}

	/**	Extract word counts from an arraylist of words.
	 *
	 *	@param	wordList	The list with the words.
	 */

	public void countWords
	(
		List<String> wordList
	)
	{
								//	Get the list of words into
								//	a string array.

		int nWords	= wordList.size();

		words		= new String[ nWords ];

		for ( int i = 0 ; i < nWords ; i++ )
		{
			words[ i ]	= wordList.get( i );
		}

		computeWordCounts();
	}

	/**	Compute word counts from a string array of words.
	 */

	protected void computeWordCounts()
	{
								//	Get the count of occurrence
								//	for each word.

		for ( int i = 0 ; i < words.length ; i++ )
		{
			String word	= words[ i ];

			if ( wordCounts.containsKey( word ) )
			{
				int freq	= wordCounts.get( word ).intValue();
				freq++;
				wordCounts.put( word , new Integer( freq ) );
			}
			else
			{
				wordCounts.put( word , new Integer( 1 ) );
			}
		}
		                        //	Create array of unique words.

		int nUniqueWords			= wordCounts.size();

		uniqueWords					= new String[ nUniqueWords ];

		Set<String> keyset			= wordCounts.keySet();
		Iterator<String> iterator	= keyset.iterator();

		for ( int i = 0 ; i < nUniqueWords ; i++ )
		{
			uniqueWords[ i ]	= iterator.next();
		}
	}

	/**	Return tokenized text words as a string array.
	 *
	 *	@return		The string array of words.
	 */

	public String[] getWords()
	{
		return words;
	}

	/**	Return the total number of words.
	 *
	 *	@return		The number of words.
	 */

	public int getNumberOfWords()
	{
		return words.length;
	}

	/**	Return unique words as a string array.
	 *
	 *	@return		The string array of unique words.
	 */

	public String[] getUniqueWords()
	{
		return uniqueWords;
	}

	/**	Return the number of unique words.
	 *
	 *	@return		The number of unique words.
	 */

	public int getNumberOfUniqueWords()
	{
		return uniqueWords.length;
	}

	/**	Return count for a specific word.
	 *
	 *	@param	word	The word whose count is desired.
	 *
	 *	@return			The count of the word in the text.
	 */

	public int getWordCount( String word )
	{
		int result	= 0;

		if ( wordCounts.containsKey( word ) )
		{
			Integer count	= wordCounts.get( word );
			result			= count.intValue();
		}

		return result;
	}

	/**	Return word count map.
	 *
	 *	@return		Word count map.
	 */

	public Map getWordCounts()
	{
		return wordCounts;
	}

	/**	Reset counter.
	 */

	public void reset()
	{
		wordCounts.clear();

		words		= null;
		uniqueWords	= null;
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


