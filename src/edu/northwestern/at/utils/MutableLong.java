package edu.northwestern.at.utils;

/*	Please see the license information at the end of this file. */

import java.io.Serializable;

/**	Implements a long object wrapper which allows changing the long value.
 *
 *	<p>
 *	The built-in Java Long class does not allow changing the value
 *	of the wrapped Long value once the Long object is created.
 *	MutableLong provides most of the same methods as Long but adds
 *	extra methods to allow changing the value of the stored long.
 *	This is useful when wrapping longs for storage in collection types
 *	since it is much more efficient to update the value of an existing
 *	MutableLong than to create a new Long every time the value
 *	changes.
 *	</p>
 */

public class MutableLong extends Number implements Serializable
{
	/**	The long wrapped here. */

	protected long mutableLong;

	/**	Create MutableLong object from an int value.
	 *
	 *	@param	l	The long being wrapped.
	 */

	public MutableLong( long l )
	{
		mutableLong	= l;
	}

	/**	Create MutableLong object from a string.
	 *
	 *	@param	s	String containing an integer value.
	 */

	public MutableLong( String s )
		throws NumberFormatException
	{
		mutableLong	= Long.parseLong( s );
	}

	/**	 Compares this object to another object.
	 *
	 *	@param	obj		The other object.
	 */

	public int compareTo( Object obj )
	{
		return compareTo( (Number)obj );
	}

	/**	 Compares this number to another number.
	 *
	 *	@param	number	The other number.
	 */

	public int compareTo( Number number )
	{
		return doCompare( mutableLong , number.longValue() );
	}

	protected int doCompare( long i , long j )
	{
		return ( i >= j ) ? ( (int)( ( i != j ) ? 1 : 0 ) ) : -1;
	}

	public boolean equals( Object obj )
	{
		if ( ( obj != null ) && ( obj instanceof Number ) )
		{
			return ( mutableLong == ((Number)obj).longValue() );
		}
		else
		{
			return false;
		}
	}

	public int hashCode()
	{
		return (int)mutableLong;
	}

	public byte byteValue()
	{
		return (byte)mutableLong;
	}

	public short shortValue()
	{
		return (short)mutableLong;
	}

	public int intValue()
	{
		return (int)mutableLong;
	}

	public long longValue()
	{
		return mutableLong;
	}

	public float floatValue()
	{
		return (float)mutableLong;
	}

	public double doubleValue()
	{
		return (double)mutableLong;
	}

	public String toString()
	{
		return String.valueOf( mutableLong );
	}

	public void setValue( byte aByte )
	{
		mutableLong	= aByte;
	}

	public void setValue( short aWord )
	{
		mutableLong	= aWord;
	}

	public void setValue( int i )
	{
		mutableLong	= (long)i;
	}

	public void setValue( long l )
	{
		mutableLong	= l;
	}

	public void setValue( float f )
	{
		mutableLong	= (long)f;
	}

	public void setValue( double d )
	{
		mutableLong	= (long)d;
	}

	public Long toLong()
	{
		return new Long( mutableLong );
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



