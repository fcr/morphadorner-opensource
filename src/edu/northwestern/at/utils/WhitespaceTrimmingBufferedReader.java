package edu.northwestern.at.utils;

/*	Please see the license information at the end of this file. */

import java.io.*;

/**	A BufferedReader that trims whitespace from input lines.
 */

public class WhitespaceTrimmingBufferedReader extends BufferedReader
{
	/**	Single character buffer. */

	protected char[] oneCharBuffer	= new char[ 1 ];

	/**	Hold characters in current input line. */

	protected  char[] lineBuffer	= null;

	/**	Current offset in current input line. */

	protected  int lineOffset		= 0;

	/**	Length of current input line. */

	protected  int lineLength		= 0;

	/**	Wrap existing reader with a whitespace trimming reader.
 	 *
 	 *	@param	reader	Reader to wrap.
 	 */

	public WhitespaceTrimmingBufferedReader( Reader reader )
	{
		super( reader );
	}

	/**	Read a single character.
	 *
	 *	@return		The character read, as an integer in the range 0 to 65535
	 *				(0x00-0xffff), or -1 if the end of the stream has been
	 *				reached.
	 *
	 *	@throws		IOException	when an I/O error occurs.
	 */

	public int read() throws IOException
	{
		int result = read( oneCharBuffer , 0 , 1 );

		return ( result < 0 ) ? result: oneCharBuffer[ 0 ];
	}

	/**	Read characters into a portion of an array.
	 *
	 *	@param	buffer	Destination buffer.
	 *	@param	offset	Offset at which to start storing characters.
	 *	@param	length	Maximum number of characters to read.
	 *
	 *	@return			The number of characters read, or -1 if the end
	 *					of the stream has been reached.
	 *
	 *	@throws			IOException	when an I/O error occurs.
	 */

	public int read( char[] buffer ,int offset ,int length )
		throws IOException
	{
								//	Any characters left in current
								//	input line?

		if ( lineLength <= 0 )
		{
								//	Read next trimmed input line if not.

			String line = readLine();

								//	Return -1 if end of stream reached.

			if ( line == null )
			{
				return -1;
			}
								//	Add single blank at end of line.
								//	This replaces the original
								//	end of line character(s).
			line += " ";

								//	Set up to return characters from
								//	this input line on subsequent calls.

			lineBuffer = line.toCharArray();
			lineOffset = 0;
			lineLength = lineBuffer.length;
		}
								//	Get number of characters to return.

		length = Math.min( length , lineLength );

								//	Copy characters from line buffer
								//	to result buffer.

		System.arraycopy( lineBuffer , lineOffset , buffer , offset ,length );

								//	Update line offset and number
								//	of characters remaining in current
								//	input line.

		lineOffset += length;
		lineLength -= length;

								//	Return number of characters read.
		return length;
	}

	/**	Read a line of text.
	 *
	 *	@return		A String containing the contents of the line,
	 *				not including any line-termination characters,
	 *				or null if the end of the stream has been reached.
	 *
	 *	@throws		IOException	when an I/O error occurs.
	 */

	public String readLine() throws IOException
	{
								//	Read the line.

		String line = super.readLine();

								//	Trim the whitespace.
		if ( line != null )
		{
			line	= line.trim();
		}

		return line;
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



