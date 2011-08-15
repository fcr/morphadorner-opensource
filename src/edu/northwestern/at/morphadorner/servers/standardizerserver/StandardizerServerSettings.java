package edu.northwestern.at.morphadorner.servers.standardizerserver;

/*	Please see the license information at the end of this file. */

import java.io.InputStream;
import java.util.*;
import javax.swing.*;

import edu.northwestern.at.utils.*;

/** Global settings for Standardizer.
 *
 *	<p>
 *	This class holds the static values of global settings used
 *	by other Standardizer classes.
 *	</p>
 */

public class StandardizerServerSettings
{
	/**	The resource strings. */

	protected static ResourceBundle resourceBundle	= null;

	/** The program name. */

	protected static String programTitle;

	/**	The program version. */

	protected static String programVersion;

	/**	The program banner (title and version number) */

	protected static String programBanner;

	/**	The RMI server url. */

	protected static String rmiServerURL;

	/**	Resource bundle path/name. */

	protected static String resourceName	=
		"edu.northwestern.at.standardizerserver.resources.ss";

	/**	Initialize Standardizer settings.
	 */

	public static void initializeSettings( )
	{
								//	Get resource strings.
		try
		{
			resourceBundle	=
				ResourceBundle.getBundle( resourceName );
		}
		catch ( MissingResourceException mre )
		{
			System.err.println( resourceName + ".properties not found" );
			System.exit( 0 );
		}
								//	Get program title and banner.
		programTitle	=
			StandardizerServerSettings.getString(
				"programTitle" , programTitle );

		programVersion	=
			StandardizerServerSettings.getString(
				"programVersion" , programVersion );

		programBanner	=
			StandardizerServerSettings.getString(
				"programBanner" , programBanner );

								//	Get settings from ss.properties .

		ClassLoader loader		=
			StandardizerServerSettings.class.getClassLoader();

		try
		{
			InputStream in			=
				loader.getResourceAsStream( "ss.properties" );

			Properties properties	= new Properties();
			properties.load( in );
			in.close();
								//	RMI server URL.

			rmiServerURL			= properties.getProperty( "rmi-url" );
		}
		catch ( Exception e )
		{
		}
	}

	/**	Get string from ResourceBundle.  If no string is found, a default
	 *  string is used.
	 *
	 *	@param	resourceName	Name of resource to retrieve.
	 *	@param	defaultValue	Default value for resource.
	 *
	 *	@return   				String value from resource bundle if
	 *							resourceName found there, otherwise
	 *							defaultValue.
	 *
	 *	<p>
	 *	Underline "_" characters are replaced by spaces.
	 *	</p>
	 */

	public static String getString
	(
		String resourceName ,
		String defaultValue
	)
	{
		String result;

		try
		{
			result	= resourceBundle.getString( resourceName );
		}
		catch ( MissingResourceException e )
		{
			result	= defaultValue;
		}

		result	= result.replace( '_' , ' ' );

		return result;
	}

	/**	Get string from ResourceBundle.  If no string is found, an empty
	 *  string is returned.
	 *
	 *	@param	resourceName	Name of resource to retrieve.
	 *
	 *	@return   				String value from resource bundle if
	 *							resourceName found there, otherwise
	 *							empty string.
	 *
	 *	<p>
	 *	Underline "_" characters are replaced by spaces.
	 *	</p>
	 */

	public static String getString( String resourceName )
	{
		String result;

		try
		{
			result	= resourceBundle.getString( resourceName );
		}
		catch ( MissingResourceException e )
		{
			result	= "";
		}

		result	= result.replace( '_' , ' ' );

		return result;
	}

	/**	Parse ResourceBundle for a String array.
	 *
	 *	@param	resourceName	Name of resource.
	 *	@param  defaults		Array of default string values.
	 *	@return					Array of strings if resource name found
	 *							in resources, otherwise default values.
	 */

	public static String[] getStrings
	(
		String resourceName,
		String[] defaults
	)
	{
		String[] result;

		try
		{
			result = tokenize( resourceBundle.getString( resourceName ) );
		}
		catch ( MissingResourceException e )
		{
			result	= defaults;
		}

		return result;
	}

	/**	Get the program title.
	 *
	 *	@return		The program title.
	 */

	public static String getProgramTitle()
	{
		return programTitle;
	}

	/**	Get the program version.
	 *
	 *	@return		The program version strings (e.g., "0.55").
	 */

	public static String getProgramVersion()
	{
		return programVersion;
	}

	/**	Get the RMI server URL.
	 *
	 *	@return		The RMI server URL.
	 */

	public static String getRmiServerURL()
	{
		return rmiServerURL;
	}

	/**	Split string into a series of substrings on whitespace boundries.
	 *
	 *	@param	input	Input string.
	 *	@return			The array of strings after splitting input.
	 *
	 *	<p>
	 *	This is useful for retrieving an array of strings from the
	 *	resource file.  Underline "_" characters are replaced by spaces.
	 *	</p>
	 */

	public static String[] tokenize( String input )
	{
		Vector<String> v	= new Vector<String>();
		StringTokenizer t	= new StringTokenizer( input );
		String result[];
		String s;

		while ( t.hasMoreTokens() )
		{
			v.addElement( t.nextToken() );
		}

		result	= new String[ v.size() ];

		for ( int i = 0 ; i < result.length ; i++ )
		{
			s			= (String)v.elementAt( i );
			result[ i ]	= s.replace( '_' , ' ' );
		}

		return result;
	}

	/**	Can't instantiate but can override. */

	protected StandardizerServerSettings()
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



