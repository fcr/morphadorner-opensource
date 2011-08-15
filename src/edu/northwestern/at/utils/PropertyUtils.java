package edu.northwestern.at.utils;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.net.*;
import java.util.*;

/**	Read and write properties files.
 */

public class PropertyUtils
{
	/**	Load properties from a specified file.
	 *
	 *	@param	propertiesFileName	File name of properties file to read.
	 *
	 *	@return	Properties object with keys and values as read from
	 *			properties file.
	 *
	 *	@throws	FileNotFoundException if properties file not found.
	 *	@throws	IOException if properties file cannot be read.
	 */

	public static Properties loadProperties( String propertiesFileName )
		throws IOException, FileNotFoundException
	{
		Properties properties = new Properties();

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
	 *	@return	Properties object with keys and values as read from
	 *			properties URL.
	 *
	 *	@throws	IOException if properties file cannot be read.
	 */

	public static Properties loadProperties( URL propertiesURL )
		throws IOException, FileNotFoundException
	{
		Properties properties = new Properties();

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
	 *	@param	properties				Properties collection to save.
	 *	@param	propertiesFileName		Name of file to save to.
	 *	@param	header					Header line describing properties.
	 *
	 *	@throws	IOException if properties file cannot be saved.
	 */

	public static void saveProperties
	(
		Properties properties,
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
	*	@return	Properties object with keys and values as read from
	*			properties string.
	*
	*	@throws	IOException if properties cannot be read from string.
    */

	public static Properties loadPropertiesFromString
	(
		String propertiesString
	)
		throws IOException
	{
		Properties properties = new Properties();

		properties.load
		(
			new ByteArrayInputStream
			(
				propertiesString.getBytes( "ISO-8859-1" )
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

	public static void savePropertiesToString
	(
		Properties properties,
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

	protected PropertyUtils()
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



