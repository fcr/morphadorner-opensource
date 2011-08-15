package edu.northwestern.at.utils.cache;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.lang.ref.*;

import edu.northwestern.at.utils.*;

/**	A cache that uses soft references.  Values in this cache
 *  are expired by the VM in response to memory needs.  For
 *  more information on how this works, see the JavaDoc
 *  for the <TT>java.lang.ref</TT> package.
 */

public class SoftReferenceCache<K, V> implements Cache<K, V>
{
	/**	Map holding cached values. */

	protected Map<K, SoftReference<V>> cacheMap = null;

	/**	Create cache. */

	public SoftReferenceCache()
	{
		clear();
	}

	/**	Clear all entries in the cache.
	 */

	public void clear()
	{
		cacheMap	=
			Collections.synchronizedMap
			(
				new HashMap<K, SoftReference<V>>()
			);
	}

	/**	True if cache contains a specified key.
	 *
	 *	@param	key		The key to look up.
	 *
	 *	@return			true if the cache contains the key.
	 */

	public boolean containsKey( K key )
	{
		return cacheMap.containsKey( key );
	}

	/**	Retrieve a cached value.
	 *
	 *	@param	key		The key of the entry to retrieve.
	 *
	 *	@return			The value of the cached entry specified by key;
	 *					null if the cache does not contain the key.
	 */

	public V get( K key )
	{
		V result	= null;

		SoftReference<V> softReference	= cacheMap.get( key );

		if ( softReference != null )
		{
			V object	= softReference.get();

								//	If object value is null,
								//	remove the key.

			if ( object == null )
			{
				remove( key );
			};

			result	= object;
		}

		return result;
	}

	/**	Add or replace a cached value.
	 *
	 *	@param	key		The key of the entry to add.
	 *	@param	value	The value of the entry to add.
	 *
	 *	@return			The value of any existing cached entry specified
	 *					by the key; null if the cache does not contain
	 *					the key.
	 */

	public V put( K key , V value )
	{
		V result	= null;

		SoftReference<V> softReference	=
			cacheMap.put( key , new SoftReference<V>( value ) );

		if ( softReference != null )
		{
			result	= softReference.get();
		}

		return result;
	}

	/**	Remove a specific entry from the cache.
	 *
	 *	@param	key		The key of the entry to remove.
	 *
	 *	@return			The entry removed, or null if none.
	 */

	public V remove( K key )
	{
		V result	= null;

		SoftReference<V> softReference	= cacheMap.remove( key );

		if ( softReference != null )
		{
			result	= softReference.get();
		}

		return result;
	}

	/**	Return current size of cache.
	 *
	 *	@return		Number of entries (keys) currently stored in cache.
	 */

	public int size()
	{
		return cacheMap.size();
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



