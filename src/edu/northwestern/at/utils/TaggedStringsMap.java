package edu.northwestern.at.utils;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.utils.TaggedStrings;

/**	Wraps a map as a TaggedStrings object.
 */

public class TaggedStringsMap implements TaggedStrings
{
	/**	Map being wrapped. */

	protected Map<String, String> wrappedMap;

	/**	Create a tagged strings map.
	 */

	public TaggedStringsMap()
	{
		this.wrappedMap	= MapFactory.createNewMap();
	}

	/**	Create a tagged strings map.
	 *
	 *	@param	map		The map to wrap.
	 */

	public TaggedStringsMap( Map<String, String> map )
	{
		this.wrappedMap	= map;
	}

	/**	See if specified string exists.
	 *
	 *	@param	string	The string.
	 *
	 *	@return			True if specified string exists.
	 */

	public boolean containsString( String string )
	{
		return wrappedMap.containsKey( string );
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

		if ( wrappedMap.containsKey( string ) )
		{
			result	= wrappedMap.get( string );
		}

		return result;
	}

	/**	Set the tag value associated with a string.
	 *
	 *	@param	string	The string.
	 *	@param	tag		The tag.
	 */

	public void putTag( String string , String tag )
	{
		wrappedMap.put( string , tag );
	}

	/**	Get number of strings.
	 *
	 *	@return		Number of strings.
	 */

	public int getStringCount()
	{
		return wrappedMap.size();
	}

	/**	Get set of all unique tag values.
	 *
	 *	@return		Set of all unique tag values.
	 */

	public Set<String> getAllTags()
	{
		Set<String> result	= SetFactory.createNewSet();

		result.addAll( wrappedMap.values() );

		return result;
	}

	/**	Get set of all unique string values.
	 *
	 *	@return		Set of all unique string values.
	 */

	public Set<String> getAllStrings()
	{
		Set<String> result	= SetFactory.createNewSet();

		result.addAll( wrappedMap.keySet() );

		return result;
	}

	/**	Return the wrapped map.
	 *
	 *	@return		The wrapped map.
	 */

	public Map<String, String> getMap()
	{
		return wrappedMap;
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



