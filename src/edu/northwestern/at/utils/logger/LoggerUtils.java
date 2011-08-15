package edu.northwestern.at.utils.logger;

/*	Please see the license information at the end of this file. */

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.lang.Integer;
import java.util.*;

import edu.northwestern.at.utils.*;

/**	Logger utilities.
 *
 *	<p>
 *	This static class provides various utility methods for writing
 *	information to a log file using a
 *	{@link edu.northwestern.at.utils.logger.Logger}.
 *	</p>
 */

public class LoggerUtils
{
	/**	Date and time format for display. */

	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd hh:mm:ss,SSS";

    public static final SimpleDateFormat DATE_TIME_FORMATTER	=
          new SimpleDateFormat( DATE_TIME_FORMAT );

	/** Logs an array of bytes in string or hex dump format.
	 *
	 *	@param	logger			The logger.
	 *	@param	logLevelTitle	The Logger level for the title/summary.
	 *							See LoggerConstants.
	 *	@param	logLevelData	The Logger level for the data.
	 *							See LoggerConstants.
	 *	@param	arrayLabel		A label for the array in the log output.
	 *	@param	bytes			The array of bytes to log.
	 *	@param	nBytes			The number of bytes to log.
	 *	@param	pctPrintable	The percent used to decide if the bytes are
	 *							written in plain text format or hex dump
	 *							format.
	 *
	 *	<p>
	 *	If less than "pctPrintable" of the bytes are unprintable, the bytes
	 *	are logged as text.  Unprintable characters are replaced by "[hex]"
	 *	where "hex" is the hexademical value of the character.
	 *	When "pctPrintable" or
	 *	more of the characters are unprintable, the characters are logged
	 *	in a side-by-side format.  The left side displays the data in string
	 *	format with unprintable characters replaced by a ".".  The right
	 *	side displays the data in hex format.
	 *	</p>
	 *
	 *	<p>
	 *	The method is adapted from code written by Carl Forde.
	 *	</p>
	 */

	public static void logByteArray
	(
		Logger logger ,
		int logLevelTitle ,
		int logLevelData ,
		String arrayLabel,
		byte bytes[] ,
		int nBytes ,
		int pctPrintable
	)
	{
		logger.log(
			logLevelTitle ,
			"[" + arrayLabel + ", (" + nBytes + " bytes)]" );

								// Text bytes.

		StringBuffer sb = new StringBuffer( nBytes );

								// Hex dump bytes.

		StringBuffer pb = new StringBuffer( 4 * nBytes );

								// Hex string for hex portion of hex dump.

		String hb = new String();

								// Text string for text portion of hex dump.

		String tb = new String();

								// Current column in output line.
		int column = 0;
								// Byte counter

		int charCount = 0;
								// Binary character counter

		float binCharCount = 0;

		for ( int i = 0; i < nBytes; i++ )
		{
								// 32 bit Unicode to 16 bit ASCII

			int value = ( bytes[ i ] & 0xFF );

								// See if this is a text character.
								// If so , append to string bytes as text,
								// other append as "[xx]" where "xx" is
								// hex formatted value of the character.

			if	(	( value == '\r' ) ||
					( value == '\n' ) ||
					( value == '\t' ) ||
					( ( value >= ' ' ) && ( value <= '~' ) ) )
			{
				sb.append( (char)value );
			}
			else
			{
				sb.append(
					"[" +
					StringUtils.zeroPad( Integer.toHexString( value ) , 2 )
					+ "]" );

								// Increment count of binary characters.

				binCharCount++;
			}
								// See if character is printable.
								// Include in text portion of hex dump
								// if so, else replace with a '.' .

			if ( ( value >= ' ' ) && ( value <= '~') )
			{
				tb = tb + (char)value;
			}
			else
			{
				tb = tb + ".";
			}
								// Add hex formatted value to hex portion
								// of hex dump.  For readability, we add a
								// space every four chars in the hex dump.

			hb =
				hb +
				StringUtils.zeroPad( Integer.toHexString( value ) , 2 );

			if ( ( column == 3 ) || ( column == 7 ) || ( column == 11 ) )
			{
				hb = hb + " ";
				tb = tb + " ";
			}
                                // Only do 16 characters per line in hex dump.

			if ( column == 15 )
			{
				pb.append(
					StringUtils.zeroPad(
						Integer.toHexString( charCount ) , 4 ) +
						":  " + hb + "    " + tb + "\n" );

				column		= 0;
				charCount	+= 16;
				hb			= "";
				tb			= "";
			}
			else
			{
				column++;
			}
		}
								// Pad last line to length of other lines.

		for ( int i = hb.length(); i < 35; i++ )
		{
			hb = hb + " ";
		}

		pb.append(
			StringUtils.zeroPad(
				Integer.toHexString( charCount ) , 4 ) + ":  " +
				hb + "    " + tb + "\n" );

											// If less than "pctPrintable"
											// of the bytes are unprintable,
											// log as text, otherwise log
											// as hex dump.

		if ( ( ( binCharCount / nBytes ) * 100 ) < pctPrintable )
		{
			logger.log( logLevelTitle , sb.toString() );
		}
		else
		{
			logger.log( logLevelData , pb.toString() );
		}
	}

	/**	Log contents of a map (HashMap, TreeMap, etc.).
	 *
	 *	@param	logger			The logger.
	 *	@param	level			Logging level.
	 *	@param	mapLabel		Label for map.
	 *	@param	map				The map to log.
	 *
	 *	<p>
	 *	N.B.	This method assumes both the keys and values have
	 *			toString() methods.
	 *	</p>
	 */

	public static<K,V> void logMap
	(
		Logger logger ,
		int level ,
		String mapLabel ,
		Map<K,V> map
	)
	{
		if ( map == null )
		{
			logger.log( level , mapLabel + " is null." );
		}
		else if ( map.size() == 0 )
		{
			logger.log( level , mapLabel + " is empty." );
		}
		else
		{
			logger.log( level , mapLabel );

			Iterator<K> iterator = map.keySet().iterator();

			int i = 0;

			while ( iterator.hasNext() )
			{
				K key = iterator.next();
				V value = map.get( key );

				if ( key == null )
				{
					if ( value == null )
					{
						logger.log( level , i + ": null=null" );
					}
					else
					{
						logger.log(
							level , i + ": null=" + value.toString() );
					}
				}
				else
				{
					if ( value == null )
					{
						logger.log(
							level ,
								i + ": " + key.toString() + "=null" );
					}
					else
					{
						logger.log(
							level , i + ": " +
								key.toString() + "=" + value.toString() );
					}
				}

				i++;
			}
		}
	}

	/** Debugging support -- Print contents of an array.
	 *
	 *	@param	logger			The logger.
	 *	@param	level			Logging level.
	 *	@param	arrayLabel		Label for array.
	 *	@param	array			The array.
	 *
	 *	<p>
	 *	N.B.  This method assumes the array values have toString() methods.
	 *	</p>
	 */

	public static void logArray
	(
		Logger logger ,
		int level ,
		String arrayLabel ,
		Object[] array
	)
	{
		if ( array == null )
		{
			logger.log( level , arrayLabel + " is null." );
		}
		else if ( array.length == 0 )
		{
			logger.log( level , arrayLabel + " is empty." );
		}
		else
		{
			logger.log( level , arrayLabel );

			for ( int i = 0; i < array.length; i++ )
			{
				Object value = array[ i ];

				if ( value == null )
				{
					logger.log( level , i + ": null" );
				}
				else
				{
					logger.log( level , i + ": " + value.toString() );
				}
			}
		}
	}

	/**	Get formatted version of current date and time.
	 *
	 *	@return		Formatted current date and time in
	 *				yyyy/mm/dd hh:mm:ss format.
	 */

	public static String getFormattedCurrentDateAndTime()
	{
		return DATE_TIME_FORMATTER.format( new Date() );
	}

	/** Allow overrides but not instantiation. */

	protected LoggerUtils()
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


