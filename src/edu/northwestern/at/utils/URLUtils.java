package edu.northwestern.at.utils;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;
import java.net.*;

/**	URL utilities.
 *
 *	<p>
 *	This static class provides various utility methods for manipulating
 *	URIs/URLs.
 *	</p>
 */

public class URLUtils
{
	/**	Get URL from file name or URL.
	 *
	 *	@param	fileNameOrURL	The file name or URL string.
	 *
	 *	@return					A URL for the specified file name or URL.
	 */

	public static URL getURLFromFileNameOrURL( String fileNameOrURL )
	{
		URL fileURL;

		try
		{
			fileURL	= new URL( fileNameOrURL );
		}
		catch ( MalformedURLException e )
		{
			try
			{
				fileURL	= new File( fileNameOrURL ).toURI().toURL();
			}
			catch ( Exception e2 )
			{
				fileURL	= null;
			}
		}

		return fileURL;
	}

	/**	Get a file name from a URL.
	 *
	 *	@param	url						The URL.
	 *	@param	outputDirectoryName		The output directory name.
	 *
	 *	@return		The file name stripped of its original path
	 *				with the specified output directory attached.
	 */

	public static String getFileNameFromURL
	(
		URL url ,
		String outputDirectoryName
	)
	{
		String hostName	= url.getHost();
		String path		= url.getPath();
		String baseName	= "";

		if ( path.length() == 0 )
		{
			if ( hostName.length() == 0 )
			{
				baseName	= "adorned";
			}
			else
			{
				baseName	= hostName;
			}
		}

		else
		{
			if ( hostName.length() == 0 )
			{
				baseName	= new File( path ).getName();
			}
			else
			{
				baseName	= new File( hostName + "." + path ).getName();
			}
		}

		return new File( outputDirectoryName , baseName ).getPath();
	}

	/**	Check if string is a valid URL.
	 *
	 *	@param	possibleURL	String to check for being a URL.
	 *
	 *	@return						true if string is a URL.
	 */

	public static boolean isURL( String possibleURL )
	{
		boolean result	= false;

		try
		{
			URL url	= new URL( possibleURL );

			result	= true;
		}
		catch ( Exception e )
		{
		}

		return result;
	}

	/** Don't allow instantiation but do allow overrides. */

	protected URLUtils()
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


