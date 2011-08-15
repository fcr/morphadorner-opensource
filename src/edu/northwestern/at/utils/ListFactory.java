package edu.northwestern.at.utils;

/*	Please see the license information at the end of this file. */

import java.util.*;

/**	Factory for creating ArrayLists.
 */

public class ListFactory
{
	/**	Create a new ArrayList.
	 */

	public static<E> List<E> createNewList()
	{
		return new ArrayList<E>();
	}

	/**	Create a new ArrayList of a specified size.
	 *
	 *	@param	nSize	Size of array list to create.
	 */

	public static<E> List<E> createNewList( int nSize )
	{
		return new ArrayList<E>( nSize );
	}

	/**	Create a new ArrayList from a collection.
	 *
	 *	@param	collection	Collection from which to create list.
	 */

	public static<E> List<E> createNewList( Collection<E> collection )
	{
		return new ArrayList<E>( collection );
	}

	/**	Create a new sorted list of a specified size.
	 *
	 *	@param	nSize	Size of array list to create.
	 */

	public static<E> List<E> createNewSortedList( int nSize )
	{
		return new SortedArrayList<E>( nSize );
	}

	/**	Create a new sorted list from a collection.
	 *
	 *	@param	collection	Collection from which to create list.
	 */

	public static<E> List<E> createNewSortedList( Collection<E> collection )
	{
		return new SortedArrayList<E>( collection );
	}

	/** Don't allow instantiation, do allow overrides. */

	protected ListFactory()
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



