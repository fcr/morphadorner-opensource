package edu.northwestern.at.utils;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.net.*;
import java.util.*;

/**	List utilities.
 */

public class ListUtils
{
	/**	Load string list from a URL.
	 *
	 *	@param	list		List into which to load strings.
	 *						Created if null.
	 *	@param	setURL		URL for list file.
	 *	@param	encoding	Character encoding for the file.
	 *
	 *	@throws FileNotFoundException	If input file does not exist.
	 *	@throws IOException				If input file cannot be opened.
	 *
	 *	@return				List with values read from file.
	 */

	public static List<String> loadList
	(
		List<String> list ,
		URL setURL ,
		String encoding
	)
		throws IOException , FileNotFoundException
	{
		if ( list == null )
		{
			list	= ListFactory.createNewList();
		}

		if ( setURL != null )
		{
			BufferedReader bufferedReader	=
				new BufferedReader
        			(
        				new UnicodeReader
        				(
	        				setURL.openStream() ,
    	    					encoding
        				)
        			);

			String inputLine	= bufferedReader.readLine();

			while ( inputLine != null )
			{
				list.add( inputLine );

				inputLine	= bufferedReader.readLine();
			}

			bufferedReader.close();
		}

		return list;
	}

	/**	Load string list from a URL.
	 *
	 *	@param	setURL		URL for list file.
	 *	@param	encoding	Character encoding for the file.
	 *
	 *	@throws FileNotFoundException	If input file does not exist.
	 *	@throws IOException				If input file cannot be opened.
	 *
	 *	@return				List with values read from file.
	 */

	public static List<String> loadList
	(
		URL setURL ,
		String encoding
	)
		throws IOException , FileNotFoundException
	{
		List<String> list	= ListFactory.createNewList();

		return loadList( list , setURL , encoding );
	}

	/**	Load string list from a file.
	 *
	 *	@param	list		List into which to load strings.
	 *						Created if null.
	 *	@param	listFile	File from which to load list.
	 *	@param	encoding	Character encoding for the file.
	 *
	 *	@throws FileNotFoundException	If input file does not exist.
	 *	@throws IOException				If input file cannot be opened.
	 *
	 *	@return				List with values read from file.
	 */

	public static List<String> loadList
	(
		List<String> list ,
		File listFile ,
		String encoding
	)
		throws IOException , FileNotFoundException
	{
		return loadList( list , listFile.toURI().toURL() , encoding );
	}

	/**	Load string list from a file.
	 *
	 *	@param	listFile	File from which to load list.
	 *	@param	encoding	Character encoding for the file.
	 *
	 *	@throws FileNotFoundException	If input file does not exist.
	 *	@throws IOException				If input file cannot be opened.
	 *
	 *	@return				List with values read from file.
	 */

	public static List<String> loadList
	(
		File listFile ,
		String encoding
	)
		throws IOException , FileNotFoundException
	{
		List<String> list	= ListFactory.createNewList();

		return loadList
		(
			list ,
			listFile.toURI().toURL() ,
			encoding
		);
	}

	/**	Load string list from a file name.
	 *
	 *	@param	list			List into which to load strings.
	 *							Created if null.
	 *	@param	listFileName	File name from which to load list.
	 *	@param	encoding		Character encoding for the file.
	 *
	 *	@throws FileNotFoundException	If input file does not exist.
	 *	@throws IOException				If input file cannot be opened.
	 *
	 *	@return					List with values read from file name.
	 */

	public static List<String> loadList
	(
		List<String> list ,
		String listFileName ,
		String encoding
	)
		throws IOException , FileNotFoundException
	{
		return loadList( list , new File( listFileName ) , encoding );
	}

	/**	Load string list from a file name.
	 *
	 *	@param	listFileName	File name from which to load list.
	 *	@param	encoding		Character encoding for the file.
	 *
	 *	@throws FileNotFoundException	If input file does not exist.
	 *	@throws IOException				If input file cannot be opened.
	 *
	 *	@return					List with values read from file name.
	 */

	public static List<String> loadList
	(
		String listFileName ,
		String encoding
	)
		throws IOException , FileNotFoundException
	{
		List<String> list	= ListFactory.createNewList();

		return loadList
		(
			list ,
			new File( listFileName ) ,
			encoding
		);
	}

	/**	Save list as string to a file.
	 *
	 *	@param	list			List to save.
	 *	@param	listFile		Output file name.
	 *	@param	encoding		Character encoding for the file.
	 *
	 *	@throws IOException		If output file has error.
	 */

	public static void saveList
	(
		List<?> list ,
		File listFile ,
		String encoding
	)
		throws IOException , FileNotFoundException
	{
		if ( list != null )
		{
			PrintWriter printWriter	= new PrintWriter( listFile , "utf-8" );

			Iterator<?> iterator	= list.iterator();

			while ( iterator.hasNext() )
			{
				String value	= iterator.next().toString();

				printWriter.println( value );
			}

			printWriter.flush();
			printWriter.close();
		}
	}

	/**	Save list as string to a file name.
	 *
	 *	@param	list			List to save.
	 *	@param	listFileName	Output file name.
	 *	@param	encoding		Character encoding for the file.
	 *
	 *	@throws IOException		If output file has error.
	 */

	public static void saveList
	(
		List<?> list ,
		String listFileName ,
		String encoding
	)
		throws IOException , FileNotFoundException
	{
		saveList( list , new File( listFileName ) , encoding );
	}

	/**	Get shallow clone of list contents.
	 *
	 *	@param	list	List to clone.
	 *
	 *	@return			Shallow clone of input list.
	 */

	public static<E> List<E> shallowClone( List<E> list )
	{
		List<E> result			= ListFactory.createNewList();

		Iterator<E> iterator	= list.iterator();

		while ( iterator.hasNext() )
		{
			E data	= iterator.next();

			result.add( data );
		}

		return result;
	}

	/** Don't allow instantiation, do allow overrides. */

	protected ListUtils()
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



