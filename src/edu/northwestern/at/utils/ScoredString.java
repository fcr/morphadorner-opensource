package edu.northwestern.at.utils;

/*	Please see the license information at the end of this file. */

import java.io.Serializable;
import java.util.*;
import edu.northwestern.at.utils.*;

/**	Associates a string with a score.
 */

public class ScoredString implements Comparable, Serializable
{
	/**	The string. */

	protected String string;

	/**	The string score. */

	protected double score;

	/**	Create scored string.
	 */

	public ScoredString()
	{
		this.string	= "";
		this.score	= 0.0D;
	}

	/**	Create scored string.
	 *
	 *	@param	string	String.
	 *	@param	score	Score.
	 */

	public ScoredString( String string , double score )
	{
		this.string	= string;
		this.score	= score;
	}

	/**	Get string.
	 *
	 *	@return		The string.
	 */

	public String getString()
	{
		return string;
	}

	/**	Set string.
	 *
	 *	@param	string	The string.
	 */

	public void putString( String string )
	{
		this.string	= string;
	}

	/**	Get score.
	 *
	 *	@return		The score.
	 */

	public double getScore()
	{
		return score;
	}

	/**	Set score.
	 *
	 *	@param	score	The score.
	 */

	public void setScore( double score )
	{
		this.score	= score;
	}

	/**	Generate displayable string.
	 *
	 *	@return		String followed by score in parentheses.
	 */

	public String toString()
	{
		return string + " (" + score + ")";
	}

    /**	Check if another object is equal to this one.
     *
     *	@param	other	Other object to test for equality.
     *
     *	@return			true if other object is equal to this one.
     */

	public boolean equals( Object other )
	{
		boolean result	= false;

		if ( other instanceof ScoredString )
		{
			ScoredString otherScoredString	= (ScoredString)other;

			result	=
				( string.equals( otherScoredString.getString() ) ) &&
				( score == otherScoredString.getScore() );
		}

		return result;
	}

 	/**	Compare this scored string with another.
 	 *
 	 *	@param	other	The other scored string
 	 *
	 *	@return			< 0 if this scored string is less than the other,
	 *					= 0 if the two scored strings are equal,
	 *					> 0 if this scored string is greater than the other.
 	 */

	public int compareTo( Object other )
	{
		int result	= 0;

		if ( ( other == null ) ||
			!( other instanceof ScoredString ) )
		{
			result	= Integer.MIN_VALUE;
		}
		else
		{
			ScoredString otherScoredString	= (ScoredString)other;

			result	= Compare.compare( score , otherScoredString.getScore() );

			if ( result == 0 )
			{
				result	=
					-Compare.compare( string , otherScoredString.getString() );
			}
		}

		return result;
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



