package edu.northwestern.at.utils.corpuslinguistics.stringsimilarity;

/*	Please see the license information at the end of this file. */

import java.math.*;
import java.util.*;

import edu.northwestern.at.utils.MapFactory;

/**	Letter equivalence for two strings.
 */

public class LetterEquivalence implements StringSimilarity
{
	/**	Create LetterEquivalence instance. */

	public LetterEquivalence()
	{
	}

	/**	Compute letter equivalence for two strings.
	 *
	 *	@param	s1	First string.
	 *	@param	s2	Second string.
	 *
	 *	@return		Letter equivalence value.
	 */

	public static int letterEquivalence( String s1 , String s2 )
	{
		int result	= 0;

		if ( ( s1 == null ) || ( s2 == null ) ) return result;

		Map<String, Integer> s1Map	= MapFactory.createNewMap();
		Map<String, Integer> s2Map	= MapFactory.createNewMap();

		for ( int i = 0 ; i < s1.length() ; i++ )
		{
			String c	= s1.charAt( i ) + "";

			if ( s1Map.get( c ) == null )
			{
				s1Map.put( c , new Integer( 1 ) );
			}
			else
			{
				int count	= ((Integer)s1Map.get( c )).intValue();
				s1Map.put( c , new Integer( count + 1 ) );
			}
		}

		for ( int i = 0 ; i < s2.length() ; i++ )
		{
			String c	= s2.charAt( i ) + "";

			if ( s2Map.get( c ) == null )
			{
				s2Map.put( c , new Integer( 1 ) );
			}
			else
			{
				int count	= ((Integer)s2Map.get( c )).intValue();
				s2Map.put( c , new Integer( count + 1 ) );
			}
		}

		Iterator<String> iterator	= s1Map.keySet().iterator();

		while ( iterator.hasNext() )
		{
			String c	= iterator.next();
			int count	= s1Map.get( c ).intValue();

			if ( s2Map.get( c ) != null )
			{
				int count2	= s2Map.get( c ).intValue();

				if ( count == count2 ) result++;
			}
		}

		return result;
	}

	/**	Compute letter equivalence similarity of two strings.
	 *
	 *	@param	s1	First string.
	 *	@param	s2	Second string.
	 *
	 *	@return		Similarity measure in the range [0,1] .
	 */

	public static double letterEquivalenceSimilarity( String s1 , String s2 )
	{
		double result	= 0.0D;

		if ( ( s1 != null ) && ( s2 != null ) )
		{
			double dl	= Math.min( s1.length() , s2.length() );

			if ( dl > 0.0D )
			{
				result	= letterEquivalence( s1 , s2 ) / dl;
			}
		}

		return result;
	}

	/**	Compute letter equivalence similarity of two strings.
	 *
	 *	@param	s1	First string.
	 *	@param	s2	Second string.
	 *
	 *	@return		Similarity measure in the range [0,1] .
	 */

	public double similarity( String s1 , String s2 )
	{
		return letterEquivalenceSimilarity( s1 , s2 );
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



