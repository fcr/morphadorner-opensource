package edu.northwestern.at.utils;

/*	Please see the license information at the end of this file. */

import java.util.*;

/** Maps keys and lists of values.
 *
 *	<p>
 *	This class extends HashMap to allow each key
 *	to take an ArrayList of values.
 *	</p>
 */

public class KeyedLists<K extends Comparable, V>
	extends HashMap<K,List<V>>
{
	/**	Create a keyed lists object.
	 */

	public KeyedLists()
	{
		super();
	}

	/**	Get indexed entry in list for specified key.
	 *
	 *	@param	key		The key.
	 *	@param	index	Index of list entry to retrieve.
	 *
	 *	@return			The indexed entry from the
	 *					list for the given key.
	 *					May be null if not found.
	 */

	public Object get( Object key , int index )
	{
		V result		= null;

		List<V> list	= get( key );

		if ( list != null )
		{
			result	= list.get( index );
    	}

    	return result;
	}

	/**	Add all entries from a collection to list for a key.
	 *
	 *	@param	key			The key.
	 *	@param	collection	Collection whose entries should be added.
	 *
	 *	@return				Updated list.
	 */

	public Object putAll( K key , Collection<V> collection )
	{
		List<V> result	= null;

		List<V> list	= get( key );

		if ( list == null )
		{
			list = new ArrayList<V>();
			super.put( key , list );
		}

		list.addAll( collection );

		if ( list.size() != collection.size() )
		{
			result	= list;
		}

		return result;
	}

	/**	Add an entry to list for a key.
	 *
	 *	@param	key			The key.
	 *	@param	value		Value to add to key's list.
	 *
	 *	@return				Updated list.
	 */

	public Object put( K key , V value )
	{
		List<V> result	= null;
		List<V> list	= get( key );

		if ( list == null )
		{
			list	= new ArrayList<V>();
			super.put( key , list );
		}

		list.add( value );

		if ( list.size() > 1 )
		{
			result	= list;
		}

		return result;
	}

	/**	Remove an entry from list for a key.
	 *
	 *	@param	key			The key.
	 *	@param	value		Value to add to key's list.
	 *
	 *	@return				true if entry removed.
	 *						Only the first occurrence
	 *						of the value is removed.
	 *
	 *	<p>
	 *	The key is removed should the associated
	 *	list become empty.
	 *	</p>
	 */

	public boolean remove( K key , V value )
	{
		boolean result	= false;

		List<V> list	= get( key );

		if ( list != null )
		{
			result	= list.remove( value );

			if ( list.size() == 0 )
			{
				remove( key );
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



