package edu.northwestern.at.utils;

/*	Please see the license information at the end of this file. */

import java.util.*;

/** Maps keys and sets of values.
 *
 *	<p>
 *	This class extends HashMap to allow each key
 *	to take a HashSet of values.
 *	</p>
 */

public class KeyedSets<K extends Comparable, V>
	extends HashMap<K,Set<V>>
{
	/**	Create a keyed sets object.
	 */

	public KeyedSets()
	{
		super();
	}

	/**	Check if entry exists in set for specified key.
	 *
	 *	@param	key		The key.
	 *	@param	value	Value to check.
	 *
	 *	@return			true if value found in set for key.
	 */

	public boolean contains( Object key , V value )
	{
		boolean result	= false;

		Set<V> set	= get( key );

		if ( set != null )
		{
			result	= set.contains( value );
    	}

    	return result;
	}

	/**	Add all entries from a collection to set for a key.
	 *
	 *	@param	key			The key.
	 *	@param	collection	Collection whose entries should be added.
	 *
	 *	@return				Updated set.
	 */

	public Set<V> addAll( K key , Collection<V> collection )
	{
		Set<V> result	= null;

		Set<V> set	= get( key );

		if ( set == null )
		{
			set = new HashSet<V>();
			super.put( key , set );
		}

		set.addAll( collection );

		if ( set.size() != collection.size() )
		{
			result	= set;
		}

		return result;
	}

	/**	Add an entry to set for a key.
	 *
	 *	@param	key			The key.
	 *	@param	value		Value to add to key's set.
	 *
	 *	@return				Updated set.
	 */

	public Set<V> add( K key , V value )
	{
		Set<V> result	= null;
		Set<V> set	= get( key );

		if ( set == null )
		{
			set	= new HashSet<V>();
			super.put( key , set );
		}

		set.add( value );

		if ( set.size() > 1 )
		{
			result	= set;
		}

		return result;
	}

	/**	Remove an entry from set for a key.
	 *
	 *	@param	key			The key.
	 *	@param	value		Value to remove from key's set.
	 *
	 *	@return				true if entry removed.
	 *
	 *	<p>
	 *	The key is removed should the associated
	 *	list become empty.
	 *	</p>
	 */

	public boolean remove( K key , V value )
	{
		boolean result	= false;

		Set<V> set	= get( key );

		if ( set != null )
		{
			result	= set.remove( value );

			if ( set.size() == 0 )
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



