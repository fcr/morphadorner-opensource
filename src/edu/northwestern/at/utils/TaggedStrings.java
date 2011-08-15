package edu.northwestern.at.utils;

/*	Please see the license information at the end of this file. */

import java.util.List;
import java.util.Set;

/**	Interface for a bunch of strings with associated values.
 *
 *	<p>
 *	This is an interface for wrapping various types
 *	of string lists.  Some string lists have one or more different
 *	values for each string, while others have the same value for
 *	each string.  The underlying implementation can be a map,
 *	an array, a trie, a properties list, etc.
 *	</p>
 */

public interface TaggedStrings
{
	/**	See if specified string exists.
	 *
	 *	@param	string	The string.
	 *
	 *	@return			True if specified string exists.
	 */

	public boolean containsString( String string );

	/**	Get the tag value associated with a string.
	 *
	 *	@param	string	The string.
	 *
	 *	@return			The tag value associated with the string.
	 *						May be null.
	 */

	public String getTag( String string );

	/**	Set the tag value associated with a string.
	 *
	 *	@param	string	The string.
	 *	@param	tag		The tag.
	 */

	public void putTag( String string , String tag );

	/**	Get number of strings.
	 *
	 *	@return		Number of strings.
	 */

	public int getStringCount();

	/**	Get set of all unique tag values.
	 *
	 *	@return		List of all tag values.
	 */

	public Set<String> getAllTags();

	/**	Get set of all unique string values.
	 *
	 *	@return		List of all strings.
	 */

	public Set<String> getAllStrings();
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



