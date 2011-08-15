package edu.northwestern.at.utils;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.net.*;
import java.util.*;

/**	Read and write UTF8 properties files.
 */

public class UTF8PropertyUtils
{
	/**	Load properties from a specified file.
	 *
	 *	@param	propertiesFileName	File name of properties file to read.
	 *
	 *	@return	UTF8Properties object with keys and values as read from
	 *			properties file.
	 *
	 *	@throws	FileNotFoundException if properties file not found.
	 *	@throws	IOException if properties file cannot be read.
	 */

	public static UTF8Properties loadUTF8Properties
	(
		String propertiesFileName
	)
		throws IOException, FileNotFoundException
	{
		UTF8Properties properties = new UTF8Properties();

		InputStream propertiesInputStream	=
			new FileInputStream( propertiesFileName );

		properties.load( propertiesInputStream );

		propertiesInputStream.close();

		return properties;
	}

	/**	Load properties from a specified URL.
	 *
	 *	@param	propertiesURL	URL of properties file to read.
	 *
	 *	@return	UTF8Properties object with keys and values as read from
	 *				properties URL.
	 *
	 *	@throws	IOException if properties file cannot be read.
	 */

	public static UTF8Properties loadUTF8Properties( URL propertiesURL )
		throws IOException, FileNotFoundException
	{
		UTF8Properties properties = new UTF8Properties();

		if ( propertiesURL != null )
		{
			InputStream propertiesInputStream	=
				propertiesURL.openStream();

			properties.load( propertiesInputStream );

			propertiesInputStream.close();
		}

		return properties;
	}

	/**	Load properties from a specified URL and default properties.
	 *
	 *	@param	propertiesURL		URL of properties file to read.
	 *	@param	defaultProperties	Default properties.
	 *
	 *	@return	UTF8Properties object with keys and values as read from
	 *				properties URL overriding those loaded from
	 *				default properties.
	 *
	 *	@throws	IOException if properties file cannot be read.
	 */

	public static UTF8Properties loadUTF8Properties
	(
		URL propertiesURL ,
		UTF8Properties defaultProperties
	)
		throws IOException, FileNotFoundException
	{
		UTF8Properties properties = new UTF8Properties( defaultProperties );

		if ( propertiesURL != null )
		{
			InputStream propertiesInputStream	=
				propertiesURL.openStream();

			properties.load( propertiesInputStream );

			propertiesInputStream.close();
		}

		return properties;
	}

	/**	Save properties to a specified file.
	 *
	 *	@param	properties				UTF8Properties collection to save.
	 *	@param	propertiesFileName		Name of file to save to.
	 *	@param	header					Header line describing properties.
	 *
	 *	@throws	IOException if properties file cannot be saved.
	 */

	public static void saveUTF8Properties
	(
		UTF8Properties properties,
		String propertiesFileName,
		String header
	)
		throws IOException
	{
		FileOutputStream propertiesFile	=
			new FileOutputStream( propertiesFileName );

		properties.store( propertiesFile , header );

		propertiesFile.flush();
		propertiesFile.close();
	}

   /**	Load properties from a string.
    *
	*	@param	propertiesString	String containing properties to read.
	*
	*	@return	UTF8Properties object with keys and values as read from
	*				properties string.
	*
	*	@throws	IOException if properties cannot be read from string.
    */

	public static UTF8Properties loadUTF8PropertiesFromString
	(
		String propertiesString
	)
		throws IOException
	{
		UTF8Properties properties = new UTF8Properties();

		properties.load
		(
			new ByteArrayInputStream
			(
//				propertiesString.getBytes( "ISO-8859-1" )
				propertiesString.getBytes( "UTF-8" )
			)
		);

		return properties;
	}

	/**	Saves properties to a specified string.
	 *
	 *	@param     properties         -- properties collection to save.
	 *	@param     header             -- header line describing properties.
	 *
	 *	@throws		IOException if properties cannot be saved to string.
	 */

	public static void saveUTF8PropertiesToString
	(
		UTF8Properties properties,
		String propertiesString,
		String header
	)
		throws IOException
	{
		ByteArrayOutputStream propertiesOutputStream	=
			new ByteArrayOutputStream();

		properties.store( propertiesOutputStream , header );

		propertiesOutputStream.flush();

		propertiesString = propertiesOutputStream.toString();

		propertiesOutputStream.close();
	}

	/** Don't allow instantiation, do allow overrides. */

	protected UTF8PropertyUtils()
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



