package edu.northwestern.at.utils;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.text.*;

/**	Class utilities.
 *
 *	<p>
 *	This static class provides various utility methods for manipulating
 *	class names.
 *	</p>
 */

public class ClassUtils
{
	/**	Extracts the unqualified class name from a fully qualified
	 *	class name.
	 *
	 *	@param	name	The fully qualified class name.
	 *
	 *	@return			The unqualified class name.
	 */

	public static String unqualifiedName( String name )
	{
		int index	= name.lastIndexOf( '.' );

		return	name.substring( index + 1 );
	}

	/**	Extracts the package name from a fully qualified class name.
	 *
	 *	@param	name	The fully qualified class name.
	 *
	 *	@return			The package name.
	 */

	public static String packageName( String name )
	{
		int index	= name.lastIndexOf( '.' );

		return name.substring( 0 , index );
	}

	/** Don't allow instantiation, do allow overrides. */

	protected ClassUtils()
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



