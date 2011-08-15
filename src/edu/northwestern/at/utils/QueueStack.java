package edu.northwestern.at.utils;

/*	Please see the license information at the end of this file. */

import java.util.*;

/**	A stack implemented using a single-ended queue.
 *
 *	<p>
 *	We actually use an ArrayList to mimic a queue.
 *	</p>
 */

public class QueueStack<E>
{
	/**	The queue which is used to hold the stack entries. */

	protected ArrayList<E> queue;

	/**	Create an empty stack. */

	public QueueStack()
	{
		queue	= new ArrayList<E>();
	}

	/**	Check if stack is empty.
	 *
	 *	@return		true if stack is empty.
	 */

	public boolean isEmpty()
	{
		return queue.isEmpty();
	}

	/**	Peek at top element of stack without removing it.
	 *
	 *	@return		Element returned.
	 *
	 *	@throws		EmptyQueueStackException when stack is empty.
	 */

	public E peek()
	{
		try
		{
			return queue.get( queue.size() - 1 );
		}
		catch ( NoSuchElementException e )
		{
			throw new EmptyQueueStackException( "Empty stack." );
		}
	}

	/**	Remove element from stack.
	 *
	 *	@return		Element returned.
	 *
	 *	@throws		EmptyQueueStackException when stack is empty.
	 */

	public E pop()
	{
		try
		{
			return queue.remove( queue.size() - 1 );
		}
		catch ( NoSuchElementException e )
		{
			throw new EmptyQueueStackException( "Empty stack." );
		}
	}

	/**	Add element to stack.
	 *
	 *	@param	e	Element to add to stack.
	 */

	public void push( E e )
	{
		queue.add( e );
	}

	/**	Number of entries in stack.
	 *
	 *	@return		Number of entries in stack.
	 */

	public int size()
	{
		return queue.size();
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



