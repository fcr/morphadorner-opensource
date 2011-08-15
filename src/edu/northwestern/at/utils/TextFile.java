package edu.northwestern.at.utils;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.Component;
import java.io.*;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.*;

import edu.northwestern.at.utils.*;

/** TextFile reads a text file into an array of strings.
 */

public class TextFile
{
	/** The text file.  May be null if file read from a stream. */

	protected File textFile				= null;

	/**	The text file encoding.  Defaults to utf-8. */

	protected String textFileEncoding	= "utf-8";

	/** The text of the data file, split into lines. */

	protected String[] textFileLines	= null;

	/**	True if file loaded OK. */

	protected boolean textFileLoaded	= false;

	/** Create text file object from file with specified encoding.
	 *
	 *	@param	textFile	Text file.
	 *	@param	encoding	Text file encoding (utf-8, 8859_1, etc.).
	 */

	public TextFile( File textFile , String encoding )
	{
		this.textFile			= textFile;

		String safeEncoding		= ( encoding == null ) ? "" : encoding;
		safeEncoding			= safeEncoding.trim();

		if ( safeEncoding.length() > 0 )
		{
			this.textFileEncoding	= safeEncoding;
		}

		openFile( textFile );
	}

	/** Create text file object from named file with specified encoding.
	 *
	 *	@param	textFileName	The data file name.
	 *	@param	encoding		Text file encoding (utf-8, 8859_1, etc.).
	 */

	public TextFile( String textFileName , String encoding )
	{
		this( new File( textFileName ) , encoding );
	}

	/** Create text file object from named file.
	 *
	 *	@param	textFileName	The data file name.
	 */

	public TextFile( String textFileName )
	{
		this( textFileName , null );
	}

	/** Create text file object from local file.
	 *
	 *	@param	textFile	The data file.
	 */

	public TextFile( File textFile )
	{
		this( textFile , null );
	}

	/**	Create data file object from input stream.
	 *
	 *	@param	inputStream		The input stream for the data file.
	 *	@param	encoding		Text file encoding (utf-8, 8859_1, etc.).
	 */

	public TextFile( InputStream inputStream , String encoding )
	{
		this.textFile	= null;

		String safeEncoding		= ( encoding == null ) ? "" : encoding;
		safeEncoding			= safeEncoding.trim();

		if ( safeEncoding.length() > 0 )
		{
			this.textFileEncoding	= safeEncoding;
		}

		openInputStream( inputStream );
	}

	/**	Create data file object from URL.
	 *
	 *	@param	url			The input URL for the data file.
	 *	@param	encoding	Text file encoding (utf-8, 8859_1, etc.).
	 */

	public TextFile( URL url , String encoding )
	{
		this.textFile			= null;

		String safeEncoding		= ( encoding == null ) ? "" : encoding;
		safeEncoding			= safeEncoding.trim();

		if ( safeEncoding.length() > 0 )
		{
			this.textFileEncoding	= safeEncoding;
		}

		try
		{
			openInputStream( url.openStream() );
		}
		catch ( Exception e )
		{
		}
	}

	/**	Create data file object from input stream.
	 *
	 *	@param	inputStream		The input stream for the data file.
	 */

	public TextFile( InputStream inputStream )
	{
		this( inputStream , null );
	}

	/**	Read local file into array of strings.
	 *
	 *	@param	inputFile	The input file.  The file is opened
	 *						using the urf-8 character set.
	 */

	protected void openFile( File inputFile )
	{
		try
		{
			openInputStream( new FileInputStream( inputFile ) );

			textFileLoaded	= true;
		}
		catch ( FileNotFoundException e )
		{
		}
	}

	/**	Read stream into array of strings.
	 *
	 *	@param	inputStream		The InputStream for the file.
	 */

	protected void openInputStream( InputStream inputStream )
	{
		String textLine;
								// Collect input lines in an array list.

		List<String> lines				= ListFactory.createNewList();
		BufferedReader bufferedReader	= null;

		try
		{
			bufferedReader	=
				new BufferedReader
				(
					new UnicodeReader( inputStream , textFileEncoding )
				);

			while ( ( textLine = bufferedReader.readLine() ) != null )
			{
				lines.add( textLine );
			}

			textFileLoaded	= true;
		}
		catch ( IOException e )
		{
		}
		finally
		{
			try
			{
				if ( bufferedReader != null ) bufferedReader.close();
			}
			catch ( Exception e )
			{
			}
		}
								// Convert array list to array of strings.

		textFileLines	= new String[ lines.size() ];

		for ( int i = 0 ; i < lines.size() ; i++ )
		{
			textFileLines[ i ]	= lines.get( i );
		}
	}

	/**	Return number of lines in the data file.
	 *
	 *	@return		Number of lines in the file.
	 */

	public int size()
	{
		return textFileLines.length;
	}

	/** Did text load OK?
	 *
	 *	@return		true if text file loaded OK.
	 */

	public boolean textLoaded()
	{
		return textFileLoaded;
	}

	/**	Return file contents as a string array.
	 *
	 *	@return		File contents as a string array.
	 */

	public String[] toArray()
	{
		return textFileLines;
	}

	/**	Return file contents as a string.
	 *
	 *	@return		File contents as a string.
	 *
	 *	<p>
	 *	Each line of the data file is separated by the platform
	 *	line separator character in the returned string.
	 *	</p>
	 */

	public String toString()
	{
		StringBuffer sb	= new StringBuffer( 32768 );

		for ( int i = 0 ; i < textFileLines.length ; i++ )
		{
			sb.append( textFileLines[ i ] );
			sb.append( Env.LINE_SEPARATOR );
		}

		return sb.toString();
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



