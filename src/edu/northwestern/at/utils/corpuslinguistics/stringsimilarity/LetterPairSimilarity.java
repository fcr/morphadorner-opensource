package edu.northwestern.at.utils.corpuslinguistics.stringsimilarity;

/*	Please see the license information at the end of this file. */

import java.math.*;
import java.util.*;
import edu.northwestern.at.utils.ListFactory;

public class LetterPairSimilarity implements StringSimilarity
{
	protected static String[] letterPairs( String s )
	{
		int numPairs = Math.max( s.length() - 1 , 0 );

		String[] pairs = new String[ numPairs ];

		if ( numPairs > 0 )
		{
			for (int i = 0 ; i < numPairs ; i++ )
			{
				pairs[ i ] = s.substring( i , i + 2 );
			}
		}

		return pairs;
	}

	/** @return an ArrayList of 2-character Strings. */

	protected static List<String> wordLetterPairs( String s )
	{
		List<String> allPairs	= ListFactory.createNewList();

								// Tokenize the string and put
								// the tokens into an array.

		String[] words	= s.split( "\\s" );

								// For each word ...

		for ( int w = 0 ; w < words.length ; w++ )
		{
								// Find pairs of characters

			String[] pairsInWord = letterPairs( words[ w ] );

			for ( int p = 0 ; p < pairsInWord.length ; p++ )
			{
				allPairs.add( pairsInWord[ p ] );
			}
		}

		return allPairs;
	}

	/**	Compute letter pair similarity of two strings.
	 *
	 *	@param	s1	First string.
	 *	@param	s2	Second string.
	 *
	 *	@return		Similarity measure in the range [0,1] .
	 */

	public static double letterPairSimilarity( String s1 , String s2 )
	{
		List pairs1	= wordLetterPairs( s1.toUpperCase() );
		List pairs2	= wordLetterPairs( s2.toUpperCase() );

		int intersection	= 0;

		int union			= pairs1.size() + pairs2.size();

		for ( int i = 0; i < pairs1.size(); i++ )
		{
			Object pair1 = pairs1.get( i );

			for ( int j = 0 ; j < pairs2.size() ; j++ )
			{
				Object pair2	= pairs2.get(j);

				if ( pair1.equals( pair2 ) )
				{
					intersection++;
					pairs2.remove( j );
					break;
				}
			}
		}

		double result	= 0.0D;

		if ( union >= 0.0D )
		{
			result = ( 2.0D * intersection ) / union;
		}

		return result;
	}

	/**	Compute letter pair similarity of two strings.
	 *
	 *	@param	s1	First string.
	 *	@param	s2	Second string.
	 *
	 *	@return		Similarity measure in the range [0,1] .
	 */

	public double similarity( String s1 , String s2 )
	{
		return letterPairSimilarity( s1 , s2 );
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



