package edu.northwestern.at.utils;

/*	Please see the license information at the end of this file. */

import java.io.Serializable;

/**	Implements an integer object wrapper which allows changing the integer value.
 *
 *	<p>
 *	The built-in Java Integer class does not allow changing the value
 *	of the wrapped integer value once the Integer object is created.
 *	MutableInteger provides most of the same methods as Integer but adds
 *	extra methods to allow changing the value of the stored integer.
 *	This is useful when wrapping integers for storage in collection types
 *	since it is much more efficient to update the value of an existing
 *	MutableInteger than to create a new Integer every time the value
 *	changes.
 *	</p>
 */

public class MutableInteger extends Number implements Serializable
{
	/**	The integer wrapped here. */

	protected int mutableInteger;

	/**	Create MutableInteger object from an int value.
	 *
	 *	@param	i	The integer being wrapped.
	 */

	public MutableInteger( int i )
	{
		mutableInteger	= i;
	}

	/**	Create MutableInteger object from a string.
	 *
	 *	@param	s	String containing an integer value.
	 */

	public MutableInteger( String s )
		throws NumberFormatException
	{
		mutableInteger	= Integer.parseInt( s );
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
		return doCompare( mutableInteger , number.intValue() );
	}

	protected int doCompare( int i , int j )
	{
		return ( i >= j ) ? ( (int)( ( i != j ) ? 1 : 0 ) ) : -1;
	}

	public boolean equals( Object obj )
	{
		if ( ( obj != null ) && ( obj instanceof Number ) )
			return ( mutableInteger == ((Number) obj).intValue() );
		else
			return false;
	}

	public int hashCode()
	{
		return mutableInteger;
	}

	public byte byteValue()
	{
		return (byte)mutableInteger;
	}

	public short shortValue()
	{
		return (short)mutableInteger;
	}

	public int intValue()
	{
		return mutableInteger;
	}

	public long longValue()
	{
		return (long)mutableInteger;
	}

	public float floatValue()
	{
		return (float)mutableInteger;
	}

	public double doubleValue()
	{
		return (double)mutableInteger;
	}

	public String toString()
	{
		return String.valueOf( mutableInteger );
	}

	public void setValue( byte aByte )
	{
		mutableInteger	= aByte;
	}

	public void setValue( short aWord )
	{
		mutableInteger	= aWord;
	}

	public void setValue( int i )
	{
		mutableInteger	= i;
	}

	public void setValue( long l )
	{
		mutableInteger	= (int)l;
	}

	public void setValue( float f )
	{
		mutableInteger	= (int)f;
	}

	public void setValue( double d )
	{
		mutableInteger	= (int)d;
	}

	public Integer toInteger()
	{
		return new Integer( mutableInteger );
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



