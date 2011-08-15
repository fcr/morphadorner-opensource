package edu.northwestern.at.utils;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.net.*;
import java.util.*;

/**	Map utilities.
 */

public class Map2DUtils
{
	/**	Load string map2D from a URL.
	 *
	 *	@param	map2DURL	URL for map2Dfile.
	 *	@param 	separator	Field separator.
	 *	@param	qualifier	Quote character.
	 *	@param	encoding	Character encoding for the file.
	 *
	 *	@throws FileNotFoundException	If input file does not exist.
	 *	@throws IOException				If input file cannot be opened.
	 *
	 *	@return				Map2D with values read from file.
	 */

	public static Map2D<String, String, String> loadMap2D
	(
		URL map2DURL ,
		String separator ,
		String qualifier ,
		String encoding
	)
		throws IOException , FileNotFoundException
	{
		Map2D<String, String, String> map2D	= Map2DFactory.createNewMap2D();

		if ( map2DURL != null )
		{
	        BufferedReader bufferedReader	=
    	    	new BufferedReader
        		(
        			new UnicodeReader
        			(
	        			map2DURL.openStream() ,
    	    			encoding
        			)
        		);

			String inputLine	= bufferedReader.readLine();
			String[] tokens;

			while ( inputLine != null )
			{
				tokens		= inputLine.split( separator );

				if ( tokens.length > 2 )
				{
					map2D.put( tokens[ 0 ] , tokens[ 1 ] , tokens[ 2 ] );
				}

				inputLine	= bufferedReader.readLine();
			}

			bufferedReader.close();
		}

		return map2D;
	}

	/**	Load string map2Dfrom a file.
	 *
	 *	@param	mapFile		Map file.
	 *	@param 	separator	Field separator.
	 *	@param	qualifier	Quote character.
	 *	@param	encoding	Character encoding for the file.
	 *
	 *	@throws FileNotFoundException	If input file does not exist.
	 *	@throws IOException				If input file cannot be opened.
	 *
	 *	@return				Map2D with values read from file.
	 */

	public static Map2D<String, String, String> loadMap2D
	(
		File mapFile ,
		String separator ,
		String qualifier ,
		String encoding
	)
		throws IOException , FileNotFoundException
	{
		return loadMap2D(
			mapFile.toURI().toURL() , separator , qualifier , encoding );
	}

	/**	Load string set from a file name.
	 *
	 *	@param	mapFileName		Map file name.
	 *	@param 	separator		Field separator.
	 *	@param	qualifier		Quote character.
	 *	@param	encoding		Character encoding for the file.
	 *
	 *	@throws FileNotFoundException	If input file does not exist.
	 *	@throws IOException				If input file cannot be opened.
	 *
	 *	@return					Map2D with values read from file name.
	 */

	public static Map2D<String, String, String> loadMap2D
	(
		String mapFileName ,
		String separator ,
		String qualifier ,
		String encoding
	)
		throws IOException , FileNotFoundException
	{
		return loadMap2D(
			new File( mapFileName ) , separator , qualifier , encoding );
	}

	/**	Save map2D as string to a file.
	 *
	 *	@param	map2D			Map2D to save.
	 *	@param	mapFile			Output file name.
	 *	@param 	separator		Field separator.
	 *	@param	qualifier		Quote character.
	 *	@param	encoding		Character encoding for the file.
	 *
	 *	@throws IOException		If output file has error.
	 */

	public static void saveMap2D
	(
		Map2D<?,?,?> map2D ,
		File mapFile ,
		String separator ,
		String qualifier ,
		String encoding
	)
		throws IOException , FileNotFoundException
	{
		if ( map2D!= null )
		{
			PrintWriter printWriter	= new PrintWriter( mapFile , "utf-8" );

			Iterator<CompoundKey> iterator	= map2D.keySet().iterator();

			while ( iterator.hasNext() )
			{
				CompoundKey key			= iterator.next();
				String value			= map2D.get( key ).toString();
                Comparable[] keyValues	= key.getKeyValues();

				printWriter.println
				(
					qualifier + keyValues[ 0 ] + qualifier +
					separator +
					qualifier + keyValues[ 1 ] + qualifier +
					separator +
					qualifier + value + qualifier
				);
			}

			printWriter.flush();
			printWriter.close();
		}
	}

	/**	Save map2D as string to a file name.
	 *
	 *	@param	map2D			Map2D to save.
	 *	@param	mapFileName		Output file name.
	 *	@param 	separator		Field separator.
	 *	@param	qualifier		Quote character.
	 *	@param	encoding		Character encoding for the file.
	 *
	 *	@throws IOException		If output file has error.
	 */

	public static void saveMap2D
	(
		Map2D<?,?,?> map2D,
		String mapFileName ,
		String separator ,
		String qualifier ,
		String encoding
	)
		throws IOException , FileNotFoundException
	{
		saveMap2D(
			map2D , new File( mapFileName ) , separator , qualifier ,
			encoding );
	}

	/** Don't allow instantiation, do allow overrides. */

	protected Map2DUtils()
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



