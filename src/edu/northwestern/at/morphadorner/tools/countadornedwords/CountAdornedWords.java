package edu.northwestern.at.morphadorner.tools.countadornedwords;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.adornedword.*;

/**	Tabulate counts of adorned words from XMLToTab output files.
 *
 *	<p>
 *	Usage:
 *	</p>
 *
 *	<p>
 *	java edu.northwestern.at.morphadorner.tools.countadornedwords.CountAdornedWords output.tab input.tab input2.tab ...<br />
 *	<br />
 *	output.tab -- output tab-separated values file described below.<br />
 *	input*.tab -- input tabbed files produced as output by XMLToTab.<br />
 *	</p>
 *
 *	<p>
 *	The output file is a tab-delimited utf-8 text file containing the
 *	following fields, in order.
 *	</p>
 *
 *	<ol>
 *	<li>Short work name, formed from input file name by stripping the path
 *		and file extension.</li>
 *	<li>The corrected original spelling.</li>
 *	<li>The standard spelling.</li>
 *	<li>The parts of speech.</li>
 *	<li>The lemmata.</li>
 *	<li>The count of the tuple (work name, corrected spelling,
 *		standard spelling, parts of speech, lemmata).</li>
 *	</ol>
 */

public class CountAdornedWords
{
	/**	Tabular data input fields. */

//	protected final static int WORKID	= 0;
//	protected final static int WORDID	= 1;
	protected final static int SPELLING	= 2;
//	protected final static int REVERSEDSPELLING	= 3;
	protected final static int STANDARD	= 4;
	protected final static int LEMMA	= 5;
	protected final static int POS		= 6;
//	protected final static int PATH		= 7;

	/**	Adorned word info map. */

	protected static Map<AdornedWordCountInfo, Integer> adornedWordInfoMap	=
		new TreeMap<AdornedWordCountInfo, Integer>();

	/**	Adorned words count map output file name. */

	protected static String adornedWordsMapOutputFileName;

	/**	# params before input file specs. */

	protected static final int INITPARAMS	= 1;

	/**	Number of documents to process. */

	protected static int filesToProcess		= 0;

	/**	Current document. */

	protected static int currentFileNumber	= 0;

	/**	Total words found. */

	protected static int totalWords			= 0;

	/**	Count of document tags. */

	protected static int uniqueWords		= 0;

	/**	Main program.
	 *
	 *	@param	args	Program parameters.
	 */

	public static void main( String[] args )
	{
								//	Initialize.

		int filesProcessed	= 0;
		long processingTime	= 0;

		try
		{
			if ( !initialize( args ) )
			{
				System.exit( 1 );
			}
								//	Process all files.

			long startTime		= System.currentTimeMillis();

			filesProcessed		= processFiles( args );

			processingTime	=
				( System.currentTimeMillis() - startTime + 999 ) / 1000;
		}
		catch ( Exception e )
		{
			e.printStackTrace();

			System.exit( 1 );
		}
								//	Terminate.

		terminate( filesProcessed , processingTime );
	}

	/**	Initialize.
	 */

	protected static boolean initialize( String[] args )
		throws Exception
	{
								//	Check for sufficient arguments.

		if ( args.length < 2 )
		{
			System.out.println( "Not enough parameters." );
			return false;
		}

		adornedWordsMapOutputFileName	= args[ 0 ];

		return true;
	}

	/**	Process one file.
	 *
	 *	@param	tabFileName		XML input file name.
	 */

	protected static void processOneFile( String tabFileName )
	{
								//	Increment count of documents
								//	processed.
		currentFileNumber++;

		System.out.println(
			"Processing " + tabFileName + " (" + currentFileNumber +
			"/" + filesToProcess + ")" );

								//	Get work ID = short file name.
		String workID	=
			FileNameUtils.changeFileExtension
			(
				FileNameUtils.stripPathName( tabFileName ) , ""
			);

		try
		{
								//	Open input file.

			UnicodeReader streamReader	=
				new UnicodeReader
				(
					new FileInputStream( new File( tabFileName ) ) ,
					"utf-8"
				);

			BufferedReader in	= new BufferedReader( streamReader );

								//	Get number of words in set now.

			int wordsNow	= adornedWordInfoMap.size();

								//	Read each line of input file.

			String inputLine	= in.readLine();

			while ( inputLine != null )
			{
				String[] fields	= inputLine.split( "\t" );

				AdornedWordCountInfo adornedWordInfo	=
					new AdornedWordCountInfo();

				adornedWordInfo.setWorkID( workID );

				adornedWordInfo.setSpelling( fields[ SPELLING ] );

				adornedWordInfo.setStandardSpelling( fields[ STANDARD ] );

				adornedWordInfo.setPartsOfSpeech( fields[ POS ] );

				adornedWordInfo.setLemmata( fields[ LEMMA ] );

				adornedWordInfo.setTokenType( 0 );

				adornedWordInfo.setCount( 0 );

				incrementWordCountMap( adornedWordInfo );

				inputLine	= in.readLine();
			}
								//	Close the input file.
			in.close();
								//	Increment global word count.

			totalWords	+= adornedWordInfoMap.size();
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			System.out.println( "   *** Failed" );
		}
	}

	/**	Process files.
	 */

	protected static int processFiles( String[] args )
		throws Exception
	{
		int result	= 0;
								//	Get file name/file wildcard specs.

		String[] wildCards	= new String[ args.length - INITPARAMS ];

		for ( int i = INITPARAMS ; i < args.length ; i++ )
		{
			wildCards[ i - INITPARAMS ]	= args[ i ];
		}
								//	Expand wildcards to list of
								//	file names,

		String[] fileNames	=
			FileNameUtils.expandFileNameWildcards( wildCards );

		filesToProcess		= fileNames.length;

								//	Process each file.

		for ( int i = 0 ; i < fileNames.length ; i++ )
		{
			processOneFile( fileNames[ i ] );
								//	Output results.
			saveWordInfo
			(
				new File( adornedWordsMapOutputFileName ) ,
				"utf-8"
			);

			adornedWordInfoMap.clear();

			System.gc();
		}
								//	Return count of files processed.

		return fileNames.length;
	}

	/**	Terminate.
	 *
	 *	@param	filesProcessed	Number of files processed.
	 *	@param	processingTime	Processing time in seconds.
	 */

	protected static void terminate
	(
		int filesProcessed ,
		long processingTime
	)
	{
								//	Display number of words processed.
		System.out.println
		(
			"Processed " +
			Formatters.formatLongWithCommas
			(
				totalWords
			) +
			StringUtils.pluralize
			(
				totalWords ,
				" word in " ,
				" words in "
			) +
			Formatters.formatIntegerWithCommas
			(
				filesProcessed
			) +
			StringUtils.pluralize
			(
				filesProcessed ,
				" file in " ,
				" files in "
			) +
			Formatters.formatLongWithCommas
			(
				processingTime
			) +
			StringUtils.pluralize
			(
				processingTime ,
				" second." ,
				" seconds."
			)
		);
	}

	/**	Updates counts for an adorned word in a set.
	 *
	 *	@param	adornedWordInfo	The adorned word information.
	 */

	public static void incrementWordCountMap
	(
		AdornedWordCountInfo adornedWordInfo
	)
	{
		int count	= 1;

		if ( adornedWordInfoMap.containsKey( adornedWordInfo ) )
		{
			count	=
				adornedWordInfoMap.get( adornedWordInfo ).intValue() + 1;

			adornedWordInfo.setCount( count );

			adornedWordInfoMap.put(
				adornedWordInfo , new Integer( count ) );
		}
		else
		{
			adornedWordInfo.setCount( 1 );

			adornedWordInfoMap.put(
				adornedWordInfo , new Integer( 1 ) );
		}
	}

	/**	Save adorned word count information to a file.
	 *
	 *	@param	adornedWordInfoFile			Output file name.
	 *	@param	encoding					Character encoding for the file.
	 *
	 *	@throws IOException					If output file has error.
	 */

	public static void saveWordInfo
	(
		File adornedWordInfoFile ,
		String encoding
	)
		throws IOException , FileNotFoundException
	{
		Set<AdornedWordCountInfo> adornedWordInfoSet	=
			adornedWordInfoMap.keySet();

		PrintWriter printWriter	=
			new PrintWriter
			(
				new OutputStreamWriter
				(
					new FileOutputStream( adornedWordInfoFile , true ) ,
					"utf-8"
				)
			);

		for ( AdornedWordCountInfo adornedWordInfo : adornedWordInfoSet )
		{
			String value	=
				adornedWordInfo.toString() + "\t" +
				adornedWordInfoMap.get( adornedWordInfo );

			printWriter.println( value );
		}

		printWriter.flush();
		printWriter.close();
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



