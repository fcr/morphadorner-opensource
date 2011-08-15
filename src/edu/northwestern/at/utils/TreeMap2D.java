package edu.northwestern.at.utils;

/*	Please see the license information at the end of this file. */

import java.util.*;

/**	Two dimensional TreeMap.
 *
 *	<p>
 *	The two dimensional tree map is implemented as a tree map of
 *	tree maps.  Each "row" key points to a "column" tree map.
 *	</p>
 *
 *	<p>
 *	Example of use:
 *	</p>
 *
 *	<blockquote>
 *	<p>
 *	<code>
 *	Map2D map	= new TreeMap2D();
 *	map.put( "a" , "b" , "my value" );
 *	String s	= map.get( "a" , "b" );
 *	</code>
 *	</p>
 *	</blockquote>
 *
 *	<p>
 *	Note: TreeMap2D does not implement the Map interface.
 *	</p>
 */

public class TreeMap2D<K1 extends Comparable, K2 extends Comparable, V>
	implements Map2D<K1, K2, V>
{
	/**	Local map.
	 */

	protected Map<K1 , TreeMap<K2,V>> localMap;

	/**	Create two dimensional tree map.
	 */

	public TreeMap2D()
	{
		localMap	= new TreeMap<K1, TreeMap<K2,V>>();
	}

	/**	Clear all entries from this map and its child maps.
	 */

	public void clear()
	{
		Iterator<K1> iterator	= localMap.keySet().iterator();

		while ( iterator.hasNext() )
		{
			Map<K2,V> columnMap	= localMap.get( iterator.next() );

			if ( columnMap != null )
			{
				columnMap.clear();
			}
		}

		localMap.clear();
	}

	/**	Return number of entries.
	 *
	 *	@return		Number of entries in map.
	 *
	 *	<p>
	 *	The number of entries is given by the sum of the number of
	 *	entries for each submap.
	 *	</p>
	 */

	public int size()
	{
		int result	= 0;

		Iterator<K1> iterator	= localMap.keySet().iterator();

		while ( iterator.hasNext() )
		{
			TreeMap<K2,V> columnMap	= localMap.get( iterator.next() );

			if ( columnMap != null )
			{
				result	+= columnMap.size();
			}
		}

		return result;
	}

    /**	Determine if map contains a key pair.
     *
     *	@param	rowKey		Row key.
     *	@param	columnKey	Column key.
     *
     *	@return				true if entry exists, false otherwise.
     */

	public boolean containsKeys
	(
		Object rowKey ,
		Object columnKey
	)
	{
		boolean result	= false;

		Map<K2,V> map	= localMap.get( rowKey );

		if ( map != null )
		{
			result	= map.containsKey( columnKey ) ;
		}

		return result;
	}

    /**	Determine if map contains a compound key.
     *
     *	@param	key		Compound key.
     *
     *	@return			true if entry exists, false otherwise.
     */

	public boolean containsKey
	(
		CompoundKey key
	)
	{
		boolean result	= false;

		if ( key != null )
		{
			Comparable[] keyValues	= key.getKeyValues();

			Map<K2,V> map	= localMap.get( keyValues[ 0 ] );

			if ( map != null )
			{
				result	= map.containsKey( keyValues[ 1 ] );
			}
		}

		return result;
	}

	/**	Get value at specified (rowKey, columnKey) position.
	 *
	 *	@param		rowKey		Row key.
	 *	@param		columnKey	Column key.
	 *
	 *	@return		The value at the specified (rowKey, columnKey) position.
	 */

	public V get( Object rowKey , Object columnKey )
	{
		V result	= null;

		Map<K2,V> map	= localMap.get( rowKey );

		if ( map != null )
		{
			result	= map.get( columnKey );
		}

		return result;
	}

	/**	Get value at specified CompoundKey position.
	 *
	 *	@param		key		Compound key.
	 *
	 *	@return		The value at the specified compound key position.
	 */

	public V get( CompoundKey key )
	{
		V result	= null;

		if ( key != null )
		{
			Comparable[] keyValues	= key.getKeyValues();

			Map<K2,V> map	= localMap.get( keyValues[ 0 ] );

			if ( map != null )
			{
				result	= map.get( keyValues[ 1 ] );
			}
		}

		return result;
	}

	/**	Add value for specified (rowKey, columnKey) .
	 *
	 *	@param		rowKey		Row key.
	 *	@param		columnKey	Column key.
	 *	@param		value		Value to store.
	 *
	 *	@return		Previous value for (rowKey, columnKey).
	 *				May be null.
	 */

	public V put
	(
		K1 rowKey ,
		K2 columnKey ,
		V value
	)
	{
		V previousValue	= null;

		TreeMap<K2,V> map	= localMap.get( rowKey );

		if ( map != null )
		{
			previousValue	= map.get( columnKey );
		}
		else
		{
			map	= new TreeMap<K2,V>();
		}

		map.put( columnKey , value );

								//	Add or update value in column map
								//	for specified column key.

		localMap.put( rowKey , map );

								//	Return previous value.

		return previousValue;
	}

	/**	Remove entry at (rowKey, columnKey).
	 *
	 *	@param		rowKey		Row key.
	 *	@param		columnKey	Column key.
	 *
	 *	@return		Previous value for (rowKey, columnKey).
	 *				May be null.
	 */

	public V remove( Object rowKey , Object columnKey )
	{
		V result	= null;

		Map<K2,V> map	= localMap.get( rowKey );

		if ( map != null )
		{
			result	= map.get( columnKey );

			if ( result != null )
			{
				map.remove( columnKey );
			}
		}

		return result;
	}

	/**	Get the compound key set.
	 *
	 *	@return		The compound key set.
	 */

	public Set<CompoundKey> keySet()
	{
		TreeSet<CompoundKey> result	= new TreeSet<CompoundKey>();

		for ( K1 rowKey : localMap.keySet() )
		{
			Map<K2,V> colMap	= localMap.get( rowKey );

			if ( colMap != null )
			{
				for ( K2 columnKey : colMap.keySet() )
				{
					CompoundKey key	=
						new CompoundKey
						(
							(Comparable)rowKey ,
							(Comparable)columnKey
						);

					result.add( key );
				}
			}
		}

		return result;
	}

	/**	Get row key set.
	 *
	 *		@return 	rows key set.
	 */

	public Set<K1> rowKeySet()
	{
		return localMap.keySet();
	}

	/**	Get column  key set.
	 *
	 *		@return 	column key set.
	 */

	public Set<K2> columnKeySet()
	{
		Set<K2> result	= new TreeSet<K2>();

		for ( K1 rowKey : localMap.keySet() )
		{
			Map<K2,V> colMap	= localMap.get( rowKey );

			if ( colMap != null )
			{
				result.addAll( colMap.keySet() );
			}
		}

		return result;
	}

	/**	Get column  key set for given row key.
	 *
	 *	@param	rowKey	The row key.
	 *
	 *	@return 	column key set.
	 */

	public Set<K2> columnKeySet( Object rowKey )
	{
		Set<K2> result		= new TreeSet<K2>();

		Map<K2,V> colMap	= localMap.get( rowKey );

		if ( colMap != null )
		{
			result	= colMap.keySet();
		}

		return result;
	}

	/**	Return formatted string displaying all entries.
	 *
	 *	@return		Formatted string displaying all entries.
	 */

	public String toString()
	{
		StringBuffer sb	= new StringBuffer();

		Iterator<CompoundKey> iterator	= keySet().iterator();

		while ( iterator.hasNext() )
		{
			CompoundKey key	= iterator.next();

			if ( sb.length() > 0 )
			{
				sb.append( "; " );
			}

			sb.append( key.toString() );
			sb.append( "=" );
			sb.append( get( key ) );
		}

		return "[" + sb.toString() + "]";
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



