package edu.northwestern.at.utils.cache;

/*	Please see the license information at the end of this file. */

import java.util.*;

/**	Fixed maximum size cache employing least-recently used method to age entries. */

public class OldLRUCache<K, V> implements Cache<K, V>
{
	/*	Maximum number of entries (keys) allowed in cache. */

	protected int maxEntries;

	/*	Nodes in least recently used queue. */

	protected class Node
	{
		/**	Node key. */

		K key;

		/**	Node value. */

		V value;

		/**	Previous node in queue. */

		Node previous;

		/**	Next node in queue. */

		Node next;

		/**	Create wrapped value.
		 *
		 *	@param	key		The key to wrap.
		 *	@param	value	The value to wrap.
		 */

		protected Node( K key , V value )
		{
			this.key		= key;
			this.value		= value;
			this.previous	= null;
			this.next		= null;
		}
    }

	/**	SoftReferenceCache which holds actual cached entries. */

	protected SoftReferenceCache<K, Node> cache;

	/*	Linked list to implement the queue for tracking the age of entries.
	 */

	protected Node head;
	protected Node tail;

	 /**	Create a new cache.
	 *
	 *	@param	maxEntries	Maximum number of entries allowed.
	 */

	public OldLRUCache( int maxEntries )
	{
		cache	= new SoftReferenceCache<K, Node>();

								//	Make sure we have at least
								//	two entries.

		this.maxEntries	= maxEntries;

		if ( this.maxEntries < 2 )
		{
			this.maxEntries	= 2;
		}
	}

	/**	Clear all entries in the cache.
	 */

	public void	clear()
	{
		cache.clear();

		synchronized( this )
		{
			head	= null;
			tail	= null;
		}
	}

	/**	True if cache contains a specified key.
	 *
	 *	@param	key		The key to look up.
	 *
	 *	@return			true if the cache contains the key.
	 */

	public boolean containsKey( K key )
	{
		return cache.containsKey( key );
	}

	/**	Returns the maximum number of items allowed in the cache.
	 *
	 *	@return		Maximum number of entries allowed in cache.
	 */

	public int getMaxSize()
	{
		return maxEntries;
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

		Node node	= cache.get( key );

		if ( node != null )
		{
	        					//	Make this node the most recently used
	        					//	moving it to the front of the queue.

			removeNode( node );
			insertNode( node );

			result	= node.value;
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
								//	If replacing an entry, return
								//	the existing value.

		if ( cache.containsKey( key ) )
		{
			Node keyNode	= cache.remove( key );

			if ( keyNode != null )
			{
				result	= keyNode.value;
			}
		}
						        //	Throw away existing entries from
						        //	tail of queue until we have enough
						        //	room for the next entry.

		while ( size() >= maxEntries )
		{
			if ( !deleteLeastRecentlyUsed() )
			{
				break;
			}
		}
								//	Wrap entry value in a queue node.

		Node node	= new Node( key , value );

								//	Add wrapped node to cache.

		cache.put( key , node );

								//	Add node to queue.
		insertNode( node );
								//	Return old node value if any.
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

		Node node	= cache.get( key );

								//	Remove node from queue.
		if ( node != null )
		{
			removeNode( node );
		}
								//	Remove node from cache.
		cache.remove( key );

		return result;
	}

	/**	Return current size of cache.
	 *
	 *	@return		Number of entries (keys) currently stored in cache.
	 */

	public int size()
	{
		return cache.size();
	}

	/*	Delete least recently used entry.
	 *
	 *	@return		true if a node was removed,
	 *				false if cache is empty.
	 *
	 *	<p>
	 *	The least recently used entry appears at the tail end of the
	 *	node queue.
	 *	</p>
     */

	protected synchronized boolean deleteLeastRecentlyUsed()
	{
		boolean result	= false;

		if ( tail != null )
		{
			cache.remove( tail.key );

			removeNode( tail );

			result	= true;
		}

		return result;
	}

	/*	Insert a node at the head of the queue.
	 */

	protected synchronized void insertNode( Node node )
	{
		node.next		= head;
		node.previous	= null;

		if ( head != null )
		{
			head.previous	= node;
		}

		head	= node;

		if ( tail == null )
		{
			tail = node;
		}
	}

	/*	Remove a node from the queue.
	 *
	 *	@param	node	The node to remove.
	 */

	protected synchronized void removeNode( Node node )
	{
		if ( node.previous != null )
		{
			node.previous.next	= node.next;
		}
		else
		{
			head	= node.next;
		}

		if ( node.next != null )
		{
			node.next.previous	= node.previous;
		}
		else
		{
			tail	= node.previous;
		}
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



