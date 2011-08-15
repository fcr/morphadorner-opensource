package edu.northwestern.at.utils.corpuslinguistics.stringsimilarity;

/*	Please see the license information at the end of this file. */

/**	Computes the Levenstein edit distance between two strings.
 *
 *	<p>
 *	The Levenstein edit distance is the number of insertions, deletions,
 *	substitutions, and adjacent transpositions required to transform
 *	one string into another.  The larger the Levenstein distance, the more
 *	different the strings are.
 *	</p>
 *
 *	<p>
 *	The edit distance between two strings s1 and s2 can be converted
 *	to a similarity measure as follows:
 *	</p>
 *
 *	<blockquote>
 *	<p>
 *	max_length		= max( length of s1 , length of s2 )
 *	edit_distance	= edit distance between s1 and s2
 *	similarity		= 1.0 - ( edit_distance / max_length )
 *	</p>
 *	</blockquote>
 *
 *	<p>
 *	This implementation of Levenstein distance is based upon one by
 *	Michael Gilleland and Charles Emerick.
 *	</p>
 */

public class LevensteinDistance implements StringSimilarity
{
	/**	Create Levenstein distance instance. */

	public LevensteinDistance()
	{
	}

	/**	Compute Levenstein edit distance between two strings.
	 *
	 *	@param	s1	First string.
	 *	@param	s2	Second string.,
	 *
	 *	@return		Edit distance between strings s1 and s2.
	 */

	public static int editDistance( String s1 , String s2 )
	{
								//	Copy input strings.
		String ts1	= s1;
		String ts2	= s2;
								//	Set null strings to empty strings.

		if ( ts1 == null ) ts1 = "";
		if ( ts2 == null ) ts2 = "";

								//	Get length of each string.

		int ls1	= ts1.length();
		int	ls2	= ts2.length();

								//	If one string is zero length
								//	but the other is not, return
								//	the length of the non zero length
								//	string as the edit distance.
								//	If both strings are empty, the
								//	edit distance is zero.
		if ( ls1 == 0 )
		{
			return ls2;
		}
		else if ( ls2 == 0 )
		{
			return ls1;
		}
								//	Allocate vectors to hold
								//	edit costs for each character in
								//	the input strings.

		int d[][]	= new int[ 3 ][ ls1 + 1 ];

								//	Set first row of distance matrix
								//	to increasing integers 0 through
								//	length of s2 - 1.  We do not keep
								//	the entire distance matrix in memory,
								//	just the current and previous two rows.
								//	That is sufficient for computing
								//	the edit distance.

		for ( int i = 0 ; i <= ls1 ; i++ )
		{
			d[ 0 ][ i ]	= 0;
			d[ 1 ][ i ]	= i;
		}
                    			//	Iterate over characters in
                    			//	second string.

		for ( int j = 1 ; j <= ls2 ; j++ )
		{
			int cs2		= ts2.charAt( j - 1 );
			d[ 2 ][ 0 ]	= j;

								//	Iterate over characters in
								//	first string.

			for ( int i = 1 ; i <= ls1 ; i++ )
			{
								//	Edit cost is 0 if characters match,
								//	1 if they do not.

				int cs1		= ts1.charAt( i - 1 );
				int cost	= ( cs1 == cs2 ? 0 : 1 );

								//	Compute minimum of cells to the left+1,
								//	to the top+1, diagonally left and up
								//	+ cost .

				int dt		=
					Math.min
					(
						d[ 2 ][ i - 1 ] + 1 ,
						Math.min( d[ 1 ][ i ] + 1 , d[ 1 ][ i - 1 ] + cost )
					);
								//	Check for transpositions.

				if ( ( i > 2 ) && ( j > 2 ) )
				{
					int trans	= d[ 0 ][ i - 2 ] + 1;
					if ( ts1.charAt( i - 2 ) != cs2 ) trans++;
					if ( cs1 != ts2.charAt( j - 2 ) ) trans++;
					if ( dt > trans ) dt = trans;
				}
								//	The updated edit distance.

				d[ 2 ][ i ]		= dt;
			}
								//	Slide down current distance values to
								//	the previous two rows.

			for ( int k = 0 ; k <= ls1 ; k++ )
			{
				d[ 0 ][ k ]	= d[ 1 ][ k ];
				d[ 1 ][ k ]	= d[ 2 ][ k ];
			}
		}
								//	Return edit distance.
		return d[ 2 ][ ls1 ];
	}

	/**	Compute similarity between two strings.
	 *
	 *	@param	s1	First string.
	 *	@param	s2	Second string.
	 *
	 *	@return		Similarity as a value from 0.0 (no similarity) to 1.0
	 *				(perfect similarity).
	 *
	 *	<p>
	 *	The similarity is computed from the edit distance between
	 *	s1 and s2 as follows:
	 *	</p>
	 *
	 *	<p>
	 *	max_length		= max( length of s1 , length of s2 )
 	 *	edit_distance	= edit distance between s1 and s2
 	 *	similarity		= 1.0 - ( edit_distance / max_length )
	 *	</p>
	 */

	public static double levensteinSimilarity( String s1 , String s2 )
	{

								//	Copy input strings.
		String ts1	= s1;
		String ts2	= s2;
								//	Set null strings to empty strings.

		if ( ts1 == null ) ts1 = "";
		if ( ts2 == null ) ts2 = "";

								//	Get length of each string.

		int ls1	= ts1.length();
		int	ls2	= ts2.length();

		double dist			= editDistance( s1 , s2 );
		double maxLength	= Math.max( ls1 , ls2 );

								//	If both strings are length 0, the
								//	similarity will be 1.0.  Otherwise,
								//	compute similarity from the
								//	max length and the edit distance.

		double result	= 1.0D;

        if ( maxLength > 0 )
        {
			result	= 1.0D - ( dist / maxLength );
        }

        return result;
	}

	/**	Are two strings alike based upon edit distance.
	 *
	 *	@param	s1	First string.
	 *	@param	s2	Second string.
	 *
	 *	@return		True if strings are alike.
	 */

	public static boolean areAlike( String s1 , String s2 )
	{
								//	Copy input strings.
		String ts1	= s1;
		String ts2	= s2;
								//	Set null strings to empty strings.

		if ( ts1 == null ) ts1 = "";
		if ( ts2 == null ) ts2 = "";

								//	Get length of each string.

		int ls1	= ts1.length();
		int	ls2	= ts2.length();

								//	If s2 string starts with s1 string,
								//	return true.
		if ( ls2 > ls1 )
		{
			if ( ts2.toLowerCase().startsWith( ts1.toLowerCase() ) )
				return true;
		}
								//	Compute maximum difference threshhold
								//	depending upon length of original
								//	strings.
		int threshold	=
			Math.max( 3 ,
				(int)Math.floor( 1.0D + ( ls1 + 2 ) / 4.0D ) );

		if ( Math.abs( ls2 - ls1 ) > threshold )
			return false;

								//	Strings are alike if the edit distance
								//	is less or equal to the threshold.

		return ( editDistance( ts1 , ts2 ) <= threshold );
	}

	/**	Compute Levenstein distance similarity of two strings.
	 *
	 *	@param	s1	First string.
	 *	@param	s2	Second string.
	 *
	 *	@return		Similarity measure in the range [0,1] .
	 */

	public double similarity( String s1 , String s2 )
	{
		return levensteinSimilarity( s1 , s2 );
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



