package edu.northwestern.at.utils;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.utils.TaggedStrings;

/**	Wraps a set as a TaggedStrings object.
 */

public class TaggedStringsSet implements TaggedStrings
{
	/**	Set being wrapped. */

	protected Set<String> wrappedSet;

	/**	The common tag for all the string values. */

	protected String tag	= null;

	/**	Create a tagged strings set.
	 *
	 *	@param	set		The set to wrap.
	 *	@param	tag		The tag value for all set entries.
	 */

	public TaggedStringsSet( Set<String> set , String tag )
	{
		this.wrappedSet	= set;
		this.tag		= tag;
	}

	/**	See if specified string exists.
	 *
	 *	@param	string	The string.
	 *
	 *	@return			True if specified string exists.
	 */

	public boolean containsString( String string )
	{
		return wrappedSet.contains( string );
	}

	/**	Get the tag value associated with a string.
	 *
	 *	@param	string	The string.
	 *
	 *	@return			The tag value associated with the string.
	 *					All strings in the set have the same tag.
	 *					Returns null if the string is not in the set.
	 */

	public String getTag( String string )
	{
		String result	= null;

		if ( wrappedSet.contains( string ) )
		{
			result	= tag;
		}

		return result;
	}

	/**	Set the tag value associated with a string.
	 *
	 *	@param	string	The string.
	 *	@param	tag		The tag.
	 *
	 *	<p>
	 *	Note: the tag value is ignored.
	 *	</p>
	 */

	public void putTag( String string , String tag )
	{
	}

	/**	Get number of strings.
	 *
	 *	@return		Number of strings.
	 */

	public int getStringCount()
	{
		return wrappedSet.size();
	}

	/**	Get set of all unique tag values.
	 *
	 *	@return		Set of all unique tag values.
	 */

	public Set<String> getAllTags()
	{
		Set<String> result	= SetFactory.createNewSet();

		if ( tag != null ) result.add( tag );

		return result;
	}

	/**	Get set of all unique string values.
	 *
	 *	@return		Set of all unique string values.
	 */

	public Set<String> getAllStrings()
	{
		return wrappedSet;
	}

	/**	Return the wrapped set.
	 *
	 *	@return		The wrapped set.
	 */

	public Set<String> getSet()
	{
		return wrappedSet;
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



