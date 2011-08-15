package edu.northwestern.at.utils;

/*	Please see the license information at the end of this file. */

import java.util.*;

/**	A tagged strings implementation where all strings have the same tag value.
 *
 *	<p>
 *	Since all the strings have the same tag value, we use a sorted string
 *	array to hold the strings.  We use binary search to locate the
 *	strings of interest.  While using an array is slower than using
 *	a hash map (binary search = o(n*log(n)), hash = o(1)), the array
 *	saves lots of memory.
 *	</p>
 */

public class SingleTagTaggedStrings implements TaggedStrings
{
	/**	Array of string values. */

	protected String[] strings	= null;

	/**	The common tag for all the string values. */

	protected String tag		= null;

	/**	Create SingleTagTaggedStrings object.
	 *
	 *	@param	strings		String array of strings.
	 *	@param	tag			The common tag for all the strings.
	 */

	public SingleTagTaggedStrings( String[] strings , String tag )
	{
		this.strings	= strings;
		this.tag		= tag;

		if ( strings != null )
		{
			Arrays.sort( strings );
		}
	}

	/**	See if specified string exists.
	 *
	 *	@param	string	The string.
	 *
	 *	@return			True if specified string exists.
	 */

	public boolean containsString( String string )
	{
		boolean result	= false;

		if ( strings != null )
		{
			result	= ( Arrays.binarySearch( strings , string ) >= 0 );
		}

		return result;
	}

	/**	Get the tag value associated with a string.
	 *
	 *	@param	string	The string.
	 *
	 *	@return			The tag value associated with the string.
	 *					May be null.
	 */

	public String getTag( String string )
	{
		String result	= null;

		int index		= Arrays.binarySearch( strings , string );

		if ( index >= 0 ) result = tag;

		return result;
	}

	/**	Get number of strings.
	 *
	 *	@return		Number of strings.
	 */

	public int getStringCount()
	{
		int result	= 0;

		if ( strings != null ) result = strings.length;

		return result;
	}

	/**	Get set of all unique string values.
	 *
	 *	@return		Set of all strings.
	 */

	public Set<String> getAllStrings()
	{
		Set<String> result	= SetFactory.createNewSet();

		result.addAll( Arrays.asList( strings ) );

		return result;
	}

	/**	Get set of all unique tag values.
	 *
	 *	@return		Set of all unique tag values.
	 *
	 *	<p>
	 *	The result always contains just one value, the common tag value
	 *	associated with all the strings.
	 *	</p>
	 */

	public Set<String> getAllTags()
	{
		Set<String> result	= SetFactory.createNewSet();

		if ( tag != null ) result.add( tag );

		return result;
	}

	/**	Set the tag value associated with a string.
	 *
	 *	@param	string	The string.
	 *	@param	tag		The tag.
	 *
	 *	<p>
	 *	This is a no-op in this implementation.
	 *	</p>
	 */

	public void putTag( String string , String tag )
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



