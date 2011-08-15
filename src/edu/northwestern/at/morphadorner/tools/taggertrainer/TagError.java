package edu.northwestern.at.morphadorner.tools.taggertrainer;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.morphadorner.tools.taggertrainer.*;
import edu.northwestern.at.utils.*;

/**	Tag confusion matrix entry.
 *
 *	<p>
 *	A TagError entry contains a (correct tag, incorrect tag, error count)
 *	tuple.  The error count is the number of times a part of speech
 *	tagger generated "incorrect tag" instead of "correct tag" in
 *	a tagged corpus.  A list of TagError entries can be used by a
 *	transformation based learning program to generate contextual rules
 *	to correct the tag errors.
 *	</p>
 */

public class TagError implements Comparable
{
	/**	Correct tag. */

	public final String correctTag;

	/**	Incorrect tag. */

	public final String incorrectTag;

	/**	List of word positions where this error occurs.
	 */

	public final List<Integer> errorPositions;

	/**	Number of times incorrect tag appears instead of correct tag. */

	public final int incorrectTagCount;

	/**	The hash code. */

	protected final int hashCode;

	/**	Create a tag error entry.
	 *
	 *	@param	correctTag			The correct part of speech tag.
	 *	@param	incorrectTag		The incorrect part of speech tag.
	 *	@param	errorPositions		The list of tag error positions.
	 */

	public TagError
	(
		String correctTag ,
		String incorrectTag ,
		List<Integer> errorPositions
	)
	{
		this.correctTag			= correctTag;
		this.incorrectTag		= incorrectTag;
		this.errorPositions		= errorPositions;
		this.incorrectTagCount	= this.errorPositions.size();

								//	Compute hash code.

		this.hashCode			=
			this.correctTag.hashCode() ^
			this.incorrectTag.hashCode() ^
			(new Integer( incorrectTagCount )).hashCode();
	}

    /**	Check if another object is equal to this one.
   	 *
     *	@param	object  Other object to test for equality.
   	 *
   	 *	@return			true if other object is equal to this one.
   	 */

	public boolean equals( Object object )
	{
		boolean	result	= false;

		if ( ( object != null ) && ( object instanceof TagError ) )
		{
			TagError otherTagError	= (TagError)object;

			result	=
				( correctTag.equals( otherTagError.correctTag ) ) &&
				( incorrectTag.equals( otherTagError.incorrectTag ) ) &&
				( incorrectTagCount == otherTagError.incorrectTagCount );
		}

		return result;
	}

 	/**	Compare this object with another.
 	 *
	 *	@param	object	The other object.
 	 *
	 *	@return			< 0 if the other object is less than this one,
	 *					= 0 if the two objects are equal,
	 *					> 0 if the other object is greater than this one.
 	 *
	 *	<p>
 	 *	We use compareTo on the array entries in the key.
	 *	This may not give the desired result if the array entries
 	 *	are themselves arrays.
 	 *	</p>
	 */

	public int compareTo( Object object )
	{
		int result	= 0;

		if ( ( object == null ) ||
			!( object instanceof TagError ) )
		{
			result	= Integer.MIN_VALUE;
		}
		else
		{
			TagError otherTagError	= (TagError)object;

			if ( incorrectTagCount > otherTagError.incorrectTagCount )
			{
				result	= -1;
			}
			else if ( incorrectTagCount < otherTagError.incorrectTagCount )
			{
				result	= 1;
			}

			if ( result == 0 )
			{
				result	=
					Compare.compare(
						incorrectTag , otherTagError.incorrectTag );
			}

			if ( result == 0 )
			{
				result	=
					Compare.compare(
						correctTag , otherTagError.correctTag );
           	}
		}

		return result;
	}

	/**	Get the hash code of the keys.
	 *
	 *	@return		The hash code.
	 */

	public int hashCode()
	{
		return hashCode;
	}

	/**	Return a string representation of this object.
	 *
	 *	@return		A string representation of this object.
	 */

	public String toString()
	{
		return
			incorrectTagCount + " " +
			incorrectTag + " should be " + correctTag;
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



