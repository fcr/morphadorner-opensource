package edu.northwestern.at.morphadorner.tools.countadornedwords;

/*	Please see the license information at the end of this file. */

import java.io.Serializable;
import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.adornedword.*;

/**	A adorned word with work and spelling count information.
 */

public class AdornedWordCountInfo extends BaseAdornedWord
{
	/**	Count. */

	protected int count;

	/**	Work ID. */

	protected String workID;

	/**	Create empty adorned word info object.
	 */

	public AdornedWordCountInfo()
	{
		super();

		count	= 0;
		workID	= "";
	}

	/**	Get the count.
	 *
	 *	@return		The count.
	 */

	public int getCount()
	{
		return count;
	}

	/**	Set the count.
	 *
	 *	@param	count	The count.
	 */

	public void setCount( int count )
	{
		this.count	= count;
	}

	/**	Get the work ID.
	 *
	 *	@return		The work ID.
	 */

	public String getWorkID()
	{
		return workID;
	}

	/**	Set the work ID.
	 *
	 *	@param	workID	The workID.
	 */

	public void setWorkID( String workID )
	{
		this.workID	= workID;
	}

    /**	Check if another object is equal to this one.
     *
     *	@param	object  Other object to test for equality.
     *
     *	@return			true if other object is equal to this one.
     *
     *	<p>
     *	Two word objects are equal if their spellings, lemmata, and
     *	parts of speech are equal.
     *	</p>
     */

	public boolean equals( Object object )
	{
		boolean result	= false;

		if ( object instanceof AdornedWordCountInfo )
		{
			AdornedWordCountInfo otherAdornedWordCountInfo	=
				(AdornedWordCountInfo)object;

			result	=
				( workID.equals( otherAdornedWordCountInfo.getWorkID() ) ) &&
				( spelling.equals( otherAdornedWordCountInfo.getSpelling() ) ) &&
				( lemmata.equals( otherAdornedWordCountInfo.getLemmata() ) ) &&
				( partsOfSpeech.equals(
					otherAdornedWordCountInfo.getPartsOfSpeech() ) ) &&
				( standardSpelling.equals(
					otherAdornedWordCountInfo.getStandardSpelling() ) );
		}

		return result;
	}

 	/**	Compare this key with another.
 	 *
 	 *	@param	object		The other CompoundKey.
 	 *
 	 *	@return				-1, 0, 1 depending opne whether this
 	 *						adorned word is less than, equal to, or
 	 *						greater than another adorned word.
 	 *
 	 *	<p>
     *	Two word objects are compared first on their spellings,
     *	then their lemmata, and finally their parts of speech.
 	 *	</p>
 	 */

	public int compareTo( Object object )
	{
		int result	= 0;

		if ( ( object == null ) ||
			!( object instanceof AdornedWordCountInfo ) )
		{
			result	= Integer.MIN_VALUE;
		}
		else
		{
			AdornedWordCountInfo otherAdornedWordCountInfo	=
				(AdornedWordCountInfo)object;

			result	=
				workID.compareTo( otherAdornedWordCountInfo.getWorkID() );

			if ( result == 0 )
			{
				result	=
					spelling.compareTo(
						otherAdornedWordCountInfo.getSpelling() );
			}

			if ( result == 0 )
			{
				result	=
					lemmata.compareTo(
						otherAdornedWordCountInfo.getLemmata() );
			}

			if ( result == 0 )
			{
				result	=
					partsOfSpeech.compareTo(
						otherAdornedWordCountInfo.getPartsOfSpeech() );
			}

			if ( result == 0 )
			{
				result	=
					standardSpelling.compareTo(
						otherAdornedWordCountInfo.getStandardSpelling() );
			}
		}

		return result;
	}

	/**	Return a string representation of this object.
	 *
	 *	@return		A string representation of this object.
	 *
	 *	<p>
	 *	Return a tab separated string of the fields in the following
	 *	order.
	 *	</p>
	 *
	 *	<ol>
	 *	<li>workID</li>
	 *	<li>spelling</li>
	 *	<li>standardSpelling</li>
	 *	<li>partsOfSpeech</li>
	 *	<li>lemmata</li>
	 *	</ol>
	 */

	public String toString()
	{
		return
			workID + "\t" +
			spelling + "\t" +
			standardSpelling + "\t" +
			partsOfSpeech + "\t" +
			lemmata
			;
	}

	/**	Return a string representation of this object.
	 *
	 *	@return		A string representation of this object.
	 *
	 *	<p>
	 *	Return a tab separated string of the fields in the following
	 *	order.
	 *	</p>
	 *
	 *	<ol>
	 *	<li>workID</li>
	 *	<li>spelling</li>
	 *	<li>reversed spelling</li>
	 *	<li>standardSpelling</li>
	 *	<li>lemmata</li>
	 *	<li>partsOfSpeech</li>
	 *	</ol>
	 */

	public String toString2()
	{
		return
			workID + "\t" +
			spelling + "\t" +
			StringUtils.reverseString( spelling ) + "\t" +
			standardSpelling + "\t" +
			lemmata + "\t" +
			partsOfSpeech
			;
	}

    /**	Get the hash code of the keys.
     *
     *	@return		The hash code.
     */

	public int hashCode()
	{
		return
			workID.hashCode() ^
			spelling.hashCode() ^
			lemmata.hashCode() ^
			partsOfSpeech.hashCode() ^
			standardSpelling.hashCode();
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



