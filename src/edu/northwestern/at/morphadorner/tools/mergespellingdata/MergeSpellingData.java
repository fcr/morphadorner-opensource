package edu.northwestern.at.morphadorner.tools.mergespellingdata;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.outputter.*;

/**	Merges multiple files of altenate spellings into one big file.
 *
 *	<p>
 *	Usage:
 *	</p>
 *
 *	<p>
 *	java edu.northwestern.at.morphadorner.tools.mergespellingdata.MergeSpellingData output.tab input.tab input2.tab ...<br />
 *	<br />
 *	output.tab -- output merged word spelling data file.<br />
 *	input*.tab -- input tab-delimited files containing spelling maps to be merged.<br />
 *	</p>
 *
 *	<p>
 *	Each input spelling map is a utf-8 file containing two fields
 *	separated by a tab character.  The first field is a variant
 *	spelling.  The second field is the standardized spelling
 *	for the variant.
 *	</p>
 *
 *	<p>
 *	The output file is a utf-8 text file containing the merged spelling
 *	maps from the input files.  When a given variant appears more
 *	than once with different standardized spellings in the input
 *	files, the last mapping encountered is the one written to the
 *	output file.
 *	</p>
 */

public class MergeSpellingData
{
	/**	Main program for merge spelling data. */

	public static void main( String[] args )
	{
		try
		{
			mergeSpellingData( args );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}

	/**	Check if a string represents a database null value.
	 *
	 *	@param	s	String to check for null value.
	 *
	 *	@return		true if string is null.
	 */

	protected static boolean isDBNull( String s )
	{
		return ( s == null ) || s.equals( "\\N" ) || s.equals( "NULL" );
	}

	/**	Merge the spelling data.
	 */

	protected static void mergeSpellingData( String[] args )
		throws IOException
	{
								//	Get the file to check for non-standard
								//	spellings.

		if ( args.length == 0 )
		{
			System.out.println( "Usage: MergeSpellingData " +
				"combinedoutput spellinginput1 spellinginput2 ..." );
			System.out.println( "" );

			System.out.println( "       -- combinedoutput is name of " +
								"file to received combined " +
								"alternate/standard spellings" );

			System.out.println( "       -- spellinginput1 ... are names of " +
								" files containing alternative spellings " +
								"mapped to standard spellings." );
			System.exit( 1 );
		}
								//	Get output file name.

		String spellingDataOutputFileName	= args[ 0 ];

								//	Create combined map of alternate
								//	spellings to standard spellings from
								//	each input file.

		Map<String, String> alternateSpellings	=
			new TreeMap<String, String>();

		Set<String> standardSpellings	= SetFactory.createNewSet();

		for ( int i = 1 ; i < args.length ; i++ )
		{
			String altSpellingsFileName	= args[ i ];

			try
			{
				getAlternateSpellings
				(
					new BufferedReader
					(
						new UnicodeReader
						(
							new FileInputStream( altSpellingsFileName ) ,
							"utf-8"
						)
					) ,
					alternateSpellings ,
					standardSpellings
				);

				System.out.println(
					"Merged alternate spellings from " +
					altSpellingsFileName );
        	}
	        catch ( Exception e )
    	    {
        		e.printStackTrace();

				System.out.println(
					"Unable to load alternate spellings from " +
					altSpellingsFileName + "." );

				System.exit( 1 );
        	}
		}

		System.out.println(
			"There are " + alternateSpellings.size() +
			" alternate spellings." );

		System.out.println(
			"There are " + standardSpellings.size() +
			" standard spellings." );

								//	Output the map of alternate to
								//	standard spellings.

		AdornedWordOutputter outputter	=	null;

		try
		{
			outputter	=
				new PrintStreamAdornedWordOutputter();

			outputter.createOutputFile
			(
				spellingDataOutputFileName , "utf-8" , '\t'
			);
		}
		catch ( Exception e )
		{
			e.printStackTrace();

			System.out.println(
				"Unable to open output file " +
				spellingDataOutputFileName + " ." );

			System.exit( 1 );
		}

		for ( String alternateSpelling : alternateSpellings.keySet() )
		{
			String standardSpelling		=
				alternateSpellings.get( alternateSpelling );

			outputter.outputWordAndAdornments
			(
				new String[]{ alternateSpelling , standardSpelling }
			);
		}
								//	Close output file.
		outputter.close();
	}

	/**	Get map of alternative : canonical spelling pairs from a reader.
	 *
	 *	@param	reader	The reader.
	 */

	public static void getAlternateSpellings
	(
		Reader reader ,
		Map<String, String> map ,
		Set<String> set
	)
		throws IOException
	{
		String[] tokens;

        BufferedReader bufferedReader	=
        	new BufferedReader( reader );

		String inputLine				= bufferedReader.readLine();
		String alternateSpelling;
		String standardSpelling;

		while ( inputLine != null )
		{
			tokens		= inputLine.split( "\t" );

			if ( tokens.length > 1 )
			{
				alternateSpelling	= tokens[ 0 ];
				standardSpelling	= tokens[ 1 ];

				if ( !isDBNull( standardSpelling ) )
				{
					alternateSpelling	= alternateSpelling.trim();
					standardSpelling	= standardSpelling.trim();

					if ( map.get( alternateSpelling ) == null )
					{
						if ( !alternateSpelling.endsWith( "-" ) )
						{
							map.put( alternateSpelling , standardSpelling );
							set.add( standardSpelling );
						}
					}
				}
			}
			else
			{
				System.out.println(
					"Skipping line = <" + inputLine + ">" );
			}

			inputLine	= bufferedReader.readLine();
		}

		bufferedReader.close();
	}

	/**	Allow overrides but not instantiation.
	 */

	protected MergeSpellingData()
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




