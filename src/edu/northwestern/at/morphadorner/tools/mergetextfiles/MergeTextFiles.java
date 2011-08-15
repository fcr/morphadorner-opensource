package edu.northwestern.at.morphadorner.tools.mergetextfiles;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;

import edu.northwestern.at.utils.*;

/**	Merge text files.
 *
 *	<p>
 *	Usage:
 *	</p>
 *
 *	<p>
 *	java edu.northwestern.at.morphadorner.tools.mergetextfiles.MergeTextFiles output.txt input.txt input2.txt ...<br />
 *	<br />
 *	output.txt -- output merged text file.<br />
 *	input*.txt -- input text files to be merged.<br />
 *	</p>
 *
 *	<p>
 *	The output file is a utf-8 text file containing the merged content
 *	of the input files.
 *	</p>
 */

public class MergeTextFiles
{
	/**	# params before input file specs. */

	protected static final int INITPARAMS	= 1;

	/**	Number of documents to process. */

	protected static int filesToProcess		= 0;

	/**	Current document. */

	protected static int currentFileNumber	= 0;

	/**	Total lines merged. */

	protected static int totalLines			= 0;

	/**	Output file name. */

	protected static String mergedOutputFileName	= "";

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
								//	Check for enough parameters.

		if ( args.length < 2 )
		{
			System.out.println( "Not enough parameters." );
			return false;
		}
								//	Get output file name.

		mergedOutputFileName	= args[ 0 ];

		return true;
	}

	/**	Process one file.
	 *
	 *	@param	inputFileName		Input file name.
	 *	@param	outputFileName		Output file name.
	 */

	protected static void processOneFile
	(
		String inputFileName ,
		String outputFileName
	)
	{
								//	Increment count of documents
								//	processed.
		currentFileNumber++;

		System.out.println(
			"Processing " + inputFileName + " (" + currentFileNumber +
			"/" + filesToProcess + ")" );

		try
		{
								//	Open input file.

			UnicodeReader streamReader	=
				new UnicodeReader
				(
					new FileInputStream( new File( inputFileName ) ) ,
					"utf-8"
				);

			BufferedReader in	= new BufferedReader( streamReader );

								//	Open output file for append.

			PrintWriter printWriter	=
				new PrintWriter
				(
					new OutputStreamWriter
					(
						new FileOutputStream( outputFileName , true ) ,
						"utf-8"
					)
				);
								//	Read each line of input file
								//	and copy to output file.

			String inputLine	= in.readLine();

			while ( inputLine != null )
			{
								//	Increment line count.
				totalLines++;

								//	Write input line to output.

				printWriter.println( inputLine );

								//	Read next input line.

				inputLine	= in.readLine();
			}
								//	Close the input and output files.
			in.close();
			printWriter.close();
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
			processOneFile( fileNames[ i ] , mergedOutputFileName );
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
				totalLines
			) +
			StringUtils.pluralize
			(
				totalLines ,
				" line in " ,
				" liness in "
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



