package edu.northwestern.at.utils;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.net.*;
import java.util.*;

/**	Map utilities.
 */

public class MapUtils
{
	/**	Load string map from a URL.
	 *
	 *	@param	mapURL		URL for map file.
	 *	@param 	separator	Field separator.
	 *	@param	qualifier	Quote character.
	 *	@param	encoding	Character encoding for the file.
	 *
	 *	@throws FileNotFoundException	If input file does not exist.
	 *	@throws IOException				If input file cannot be opened.
	 *
	 *	@return				Map with values read from file.
	 */

	public static Map<String, String> loadMap
	(
		URL mapURL ,
		String separator ,
		String qualifier ,
		String encoding
	)
		throws IOException , FileNotFoundException
	{
		Map<String, String> map	= MapFactory.createNewMap();

		if ( mapURL != null )
		{
			BufferedReader bufferedReader	=
				new BufferedReader
				(
					new UnicodeReader
					(
						mapURL.openStream() ,
						encoding
					)
				);

			String inputLine	= bufferedReader.readLine();
			String[] tokens;

			while ( inputLine != null )
			{
				tokens		= inputLine.split( separator );

				if ( tokens.length > 1 )
				{
					map.put( tokens[ 0 ] , tokens[ 1 ] );
				}

				inputLine	= bufferedReader.readLine();
			}

			bufferedReader.close();
		}

		return map;
	}

	/**	Load string map from a file.
	 *
	 *	@param	mapFile		Map file.
	 *	@param 	separator	Field separator.
	 *	@param	qualifier	Quote character.
	 *	@param	encoding	Character encoding for the file.
	 *
	 *	@throws FileNotFoundException	If input file does not exist.
	 *	@throws IOException				If input file cannot be opened.
	 *
	 *	@return				Set with values read from file.
	 */

	public static Map<String, String> loadMap
	(
		File mapFile ,
		String separator ,
		String qualifier ,
		String encoding
	)
		throws IOException , FileNotFoundException
	{
		return loadMap(
			mapFile.toURI().toURL() , separator , qualifier , encoding );
	}

	/**	Load string map from a file name.
	 *
	 *	@param	mapFileName		Map file name.
	 *	@param 	separator		Field separator.
	 *	@param	qualifier		Quote character.
	 *	@param	encoding		Character encoding for the file.
	 *
	 *	@throws FileNotFoundException	If input file does not exist.
	 *	@throws IOException				If input file cannot be opened.
	 *
	 *	@return					Set with values read from file name.
	 */

	public static Map<String, String> loadMap
	(
		String mapFileName ,
		String separator ,
		String qualifier ,
		String encoding
	)
		throws IOException , FileNotFoundException
	{
		return loadMap(
			new File( mapFileName ) , separator , qualifier , encoding );
	}

	/**	Save map to a file.
	 *
	 *	@param	map				Map to save.
	 *	@param	mapFile			Output file name.
	 *	@param 	separator		Field separator.
	 *	@param	qualifier		Quote character.
	 *	@param	encoding		Character encoding for the file.
	 *
	 *	@throws IOException		If output file has error.
	 */

	public static void saveMap
	(
		Map<?,?> map ,
		File mapFile ,
		String separator ,
		String qualifier ,
		String encoding
	)
		throws IOException , FileNotFoundException
	{
		if ( map != null )
		{
			PrintWriter printWriter	= new PrintWriter( mapFile , "utf-8" );

			Iterator<?> iterator	= map.keySet().iterator();

			while ( iterator.hasNext() )
			{
				Object key		= iterator.next();
				String value	= map.get( key ).toString();

				printWriter.println
				(
					qualifier + key + qualifier +
					separator +
					qualifier + value + qualifier
				);
			}

			printWriter.flush();
			printWriter.close();
		}
	}

	/**	Save map to a file name.
	 *
	 *	@param	map				Map to save.
	 *	@param	mapFileName		Output file name.
	 *	@param 	separator		Field separator.
	 *	@param	qualifier		Quote character.
	 *	@param	encoding		Character encoding for the file.
	 *
	 *	@throws IOException		If output file has error.
	 */

	public static void saveMap
	(
		Map<?,?> map ,
		String mapFileName ,
		String separator ,
		String qualifier ,
		String encoding
	)
		throws IOException , FileNotFoundException
	{
		saveMap
		(
			map , new File( mapFileName ) , separator , qualifier ,
			encoding
		);
	}

	/**	Save map to a file in sorted key order.
	 *
	 *	@param	map				Map to save.
	 *	@param	mapFile			Output file name.
	 *	@param 	separator		Field separator.
	 *	@param	qualifier		Quote character.
	 *	@param	encoding		Character encoding for the file.
	 *
	 *	@throws IOException		If output file has error.
	 */

	public static void saveSortedMap
	(
		Map<?,?> map ,
		File mapFile ,
		String separator ,
		String qualifier ,
		String encoding
	)
		throws IOException , FileNotFoundException
	{
		if ( map != null )
		{
			PrintWriter printWriter		= new PrintWriter( mapFile , "utf-8" );

			Set<Object> keySet			= new TreeSet<Object>( map.keySet() );
			Iterator<Object> iterator	= keySet.iterator();

			while ( iterator.hasNext() )
			{
				Object key		= iterator.next();
				String value	= map.get( key ).toString();

				printWriter.println
				(
					qualifier + key + qualifier +
					separator +
					qualifier + value + qualifier
				);
			}

			printWriter.flush();
			printWriter.close();
		}
	}

	/**	Save map to a file name in sorted key order.
	 *
	 *	@param	map				Map to save.
	 *	@param	mapFileName		Output file name.
	 *	@param 	separator		Field separator.
	 *	@param	qualifier		Quote character.
	 *	@param	encoding		Character encoding for the file.
	 *
	 *	@throws IOException		If output file has error.
	 */

	public static void saveSortedMap
	(
		Map<?,?> map ,
		String mapFileName ,
		String separator ,
		String qualifier ,
		String encoding
	)
		throws IOException , FileNotFoundException
	{
		saveSortedMap
		(
			map , new File( mapFileName ) , separator , qualifier ,
			encoding
		);
	}

	/** Don't allow instantiation, do allow overrides. */

	protected MapUtils()
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



