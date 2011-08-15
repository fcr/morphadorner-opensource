package edu.northwestern.at.utils.corpuslinguistics.ngram;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;
import java.net.*;

import edu.northwestern.at.utils.*;

/**	Extract ngrams from text.
 */

public class NGramExtractor
{
	/**	Number of words forming an ngram. */

	int nGramSize	= 2;

	/**	Window size within which to search for ngrams. */

	int windowSize	= 2;

	/**	The list of ngrams and associated counts.
	 *
	 *	<p>
	 *	Key=ngram string<br />
	 *	Value=Integer(count)
	 *	</p>
	 *
	 *	<p>
	 *	The ngram string is two or more words with
	 *	a tab character ("\t") separating the words.
	 *	</p>
	 */

	protected Map<String,Integer> nGramCounts	=
		MapFactory.createNewMap();

	/**	Total number of ngrams. */

	protected int numberOfNGrams	= 0;

	/**	Create NGrams.
	 *
	 *	@param	nGramSize		The number of words forming an ngram.
	 *	@param	windowSize		The window size (number of words)
	 *								within which to construct ngrams.
	 *
	 *	<ul>
	 *	<li>windowSize must be greater than or equal to windowSize.</li>
	 *	<li>if windowSize is the same as nGramSize,
	 *		all ngrams are comprised of adjacent words.
	 *	<li>if windowSize is greater than nGramSize, all non-adjacent
	 *		word sets of length nGramSize are extracted from each
	 *		set of windowSize words.
	 *	</ul>
	 *
	 *	<p>
	 *	Example: nGramSize=2, windowSize=3, text="a quick brown fox".
	 *	</p>
	 *
	 *	<p>
	 *	The first window is "a quick brown".
	 *	The ngrams are "a quick", "a brown", and "quick brown".
	 *	</p>
	 *
	 *	<p>
	 *	The second window is "quick brown fox."
	 *	The ngrams are "quick brown", "quick fox", and "brown fox".
	 *	</p>
	 */

	public NGramExtractor
	(
		int nGramSize ,
		int windowSize
	)
	{
		this.nGramSize		= nGramSize;
		this.windowSize		= windowSize;
	}

	/**	Add words from string array of words.
	 *
	 *	@param	words			The string array with the words.
	 */

	public void addWords( String[] words )
	{
								//	Generate ngrams.

		addWords( Arrays.asList( words ) );
	}

	/**	Add words from list words.
	 *
	 *	@param	wordList	The list with the words.
	 */

	public void addWords( List<String> wordList )
	{
								//	Generate the ngrams and
								//	compute the count of each.

		for ( 	int i = nGramSize - 1 ;
				i < wordList.size() ;
				i++
			)
		{
			StringBuffer sb	= new StringBuffer();

			for ( int j = ( nGramSize - 1 ) ; j >= 0 ; j-- )
			{
				if ( sb.length() > 0 )
				{
					sb	= sb.append( "\t" );
				}

				sb	= sb.append( (String)wordList.get( i - j  ) );
			}

			String nGramString	= sb.toString();

			if ( nGramCounts.containsKey( nGramString ) )
			{
				int freq	=
					((Integer)nGramCounts.get( nGramString )).intValue();

				freq++;

				nGramCounts.put( nGramString , new Integer( freq ) );
			}
			else
			{
				nGramCounts.put( nGramString , new Integer( 1 ) );
			}
		}
	}

	/**	Merge ngrams from another NGramExtractor.
	 *
	 *	@param	extractor	Merge ngrams from another extractor.
	 */

	public void mergeNGramExtractor( NGramExtractor extractor )
	{
		Map<String, Integer> otherMap	= extractor.getNGramMap();

		for ( String nGramString : otherMap.keySet() )
		{
			if ( nGramCounts.containsKey( nGramString ) )
			{
				int freq	=
					((Integer)nGramCounts.get( nGramString )).intValue();

				freq++;

				nGramCounts.put( nGramString , new Integer( freq ) );
			}
			else
			{
				nGramCounts.put( nGramString , new Integer( 1 ) );
			}
		}
								//	Compute total ngram count.

		numberOfNGrams	= 0;

		for ( String nGramString : otherMap.keySet() )
		{
			Integer count	= nGramCounts.get( nGramString );
			numberOfNGrams	+= count.intValue();
		}
	}

	/**	Return count for a specific ngram.
	 *
	 *	@param	ngram	The ngram whose count is desired.
	 *
	 *	@return			The count of the ngram in the text.
	 */

	public int getNGramCount( String ngram )
	{
		int result	= 0;

		if ( nGramCounts.containsKey( ngram ) )
		{
			Integer count	= nGramCounts.get( ngram );
			result			= count.intValue();
		}

		return result;
	}

	/**	Return NGrams.
	 *
	 *	@return	String array of ngrams.
	 */

	public String[] getNGrams()
	{
		int nNGrams					= nGramCounts.size();

		String[] nGrams				= new String[ nNGrams ];

		Set<String> keyset			= nGramCounts.keySet();

		Iterator<String> iterator	= keyset.iterator();

		for ( int i = 0 ; i < nNGrams ; i++ )
		{
			nGrams[ i ]	= iterator.next();
		}

		return nGrams;
	}

	/**	Return NGram map.
	 *
	 *	@return	NGram map.
	 */

	public Map<String, Integer> getNGramMap()
	{
		return nGramCounts;
	}

	/**	Returns the total number of ngrams.
	 *
	 *	@return	The total number of ngrams.
	 */

	public int getNumberOfNGrams()
	{
								//	Compute total ngram count.

		numberOfNGrams	= 0;

		for ( String nGramString : nGramCounts.keySet() )
		{
			Integer count	= nGramCounts.get( nGramString );
			numberOfNGrams	+= count.intValue();
		}

		return numberOfNGrams;
	}

	/**	Returns the number of unique ngrams.
	 *
	 *	@return	The number of unique ngrams.
	 */

	public int getNumberOfUniqueNGrams()
	{
		return nGramCounts.size();
	}

	/**	Returns the individual words comprising an ngram.
	 *
	 *	@param	ngram	The ngram to parse.
	 *
	 *	@return	String array of the individual words
	 *				(in order) comprising the ngram.
	 */

	public static String[] splitNGramIntoWords( String ngram )
	{
		return StringUtils.makeTokenArray( ngram , "\t" );
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



