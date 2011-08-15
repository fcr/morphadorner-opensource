package edu.northwestern.at.morphadorner.tools.countaffixes;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.text.*;
import java.util.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import edu.northwestern.at.morphadorner.tools.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.xml.*;

/**	Count affixes for unique words in MorphAdorner XML output.
 *
 *	<p>
 *	Usage:
 *	</p>
 *
 *	<p>
 *	java edu.northwestern.at.morphadorner.tools.countaffixes.CountAffixes input.xml prefixes.tab suffixes.tab<br />
 *	<br />
 *	input.xml -- input XML file produced as output by MorphAdorner.<br />
 *	prefixes.tab -- output tab-separated prefixes file described below.<br />
 *	suffixes.tab -- output tab-separated suffixes file described below.<br />
 *	</p>
 *
 *	<p>
 *	Both the prefixes.tab and suffixes.tab output files contain two
 *	tab-separated columns.  The first column is a prefix or suffix string,
 *	respectively, and the second column contains the count of the number
 *	of times that prefix or suffix occurred in the unique words in the
 *	input.xml file.
 *	</p>
 */

public class CountAffixes
{
	/**	Main program. */

	public static void main( String[] args )
	{
								//	Must have input file name and
								//	two output file names to extract
								//	affixes.
								//
								//	Display program usage if three
								//	arguments are not provided.

		if ( args.length >= 3 )
		{
			new CountAffixes( args );
		}
		else
		{
			displayUsage();
			System.exit( 1 );
		}
	}

	/**	Display brief program usage.
	 */

	public static void displayUsage()
	{
		System.out.println( "Usage: " );
		System.out.println( "" );
		System.out.println(
			"   java edu.northwestern.at.morphadorner.tool.countaffixes." +
			"CountAffixes input.xml prefixes.tab suffixes.tab" );
		System.out.println( "" );
		System.out.println( "      input.xml -- input XML file" );
		System.out.println( "      prefixes.tab -- output tab-separated " +
			"prefixes and counts file." );
		System.out.println( "      suiffixes.tab -- output tab-separated " +
			"suffixes and counts file." );
	}

	/**	Supervises count of affixes for word specified by XML "<w>" elements.
	 *
	 *	@param	args	Command line arguments.
	 */

	public CountAffixes( String[] args )
	{
								//	Get input XML file name.

		String xmlInputFileName			= args[ 0 ];

								//	Holds spellings and counts.

		Map<String,Number> wordsMap		= MapFactory.createNewMap();

								//	Holds prefixes and counts.

		Map<String,Number> prefixesMap	= MapFactory.createNewMap();

								//	Holds prefixes and counts.

		Map<String,Number> suffixesMap	= MapFactory.createNewMap();

								//	Holds suffixes and counts.

								//	Count affixes for each
								//	word specified by a "<w>" element
								//	in the input file.
		try
		{
								//	Create XML reader for XML input.

			XMLReader reader		=
				XMLReaderFactory.createXMLReader();

								//	Add filter to XML reader to
								//	pull out "<w>" elements containing
								//	word information.

			ExtendedAdornedWordFilter wordInfoFilter	=
				new ExtendedAdornedWordFilter( reader );

								//	Parse the XML input.

			wordInfoFilter.parse( xmlInputFileName );

								//	We have collected information
								//	about each "<w>" element in
								//	the XML input.  For each of these,
								//	we write a corresponding
								//	tab-separated list of the
								//	attribute values to the output file.

			List<String> idList	= wordInfoFilter.getAdornedWordIDs();

			for ( int wordOrd = 0 ; wordOrd < idList.size() ; wordOrd++ )
			{
								//	Get next word's information.

				String id		= idList.get( wordOrd );

				ExtendedAdornedWord w		=
					wordInfoFilter.getExtendedAdornedWord( id );

								//	Only need information from
								//	last part of a multipart word.

				if ( !w.isFirstPart() ) continue;

								//	Get lowercase form of corrected
								//	original spelling.

				String spelling	= w.getSpelling().toLowerCase();

								//	Update count in word counts map.

				CountMapUtils.updateWordCountMap( spelling , 1 , wordsMap );
			}
								//	Now for each unique word, extract
								//	the prefixes and suffixes.

			for ( String spelling : wordsMap.keySet() )
			{
								//	Get prefixes.

				int l	= spelling.length();

				for ( int i = 0 ; i < l ; i++ )
				{
					CountMapUtils.updateWordCountMap
					(
						spelling.substring( 0 , i + 1 ) ,
						1 ,
						prefixesMap
					);

					CountMapUtils.updateWordCountMap
					(
						spelling.substring( i , l ) ,
						1 ,
						suffixesMap
					);
				}
			}
								//	Now we have a map of the
								//	words and their counts.
								//	Output prefixes and counts.

			MapUtils.saveMap
			(
				new TreeMap<String, Number>( prefixesMap ) ,
				args[ 1 ] ,
				"\t" ,
				"" ,
				"utf-8"
			);
								//	Output suffixes and counts.
			MapUtils.saveMap
			(
				new TreeMap<String, Number>( suffixesMap ) ,
				args[ 2 ] ,
				"\t" ,
				"" ,
				"utf-8"
			);
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
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



