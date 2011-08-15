package edu.northwestern.at.utils.preprocessor;

/*	Please see the license information at the end of this file. */

import java.io.*;

import edu.northwestern.at.utils.*;

/**	PreprocessDirectoryTree	-- Preprocess directory tree.
 *
 *	<p>
 *	PreprocessDirectoryTree recurses through a directory tree preprocessing
 *	all java files and copying all non-java files to a specified
 *	output directory.
 *	</p>
 *
 *	<p>
 *	Usage:
 *	</p>
 *
 *	<p>
 *	<code>
 *	<blockquote>
 *	java edu.northwestern.at.wordhoard.tools.PreprocessDirectoryTree sourcedirectory destinationdirectory includedirectory
 *	</blockquote>
 *	</code>
 *	</p>
 *
 *	<p>
 *	where "sourcedirectory" is the root of the directory to process;
 *	"destinationdirectory" is the root of the output directory;
 *	and "includedirectory" is the directory containing include files.
 *	</p>
 *
 *	<p>
 *	Example:
 *  </p>
 *
 *	<p>
 *	<code>
 *	<blockquote>
 *	java edu.northwestern.at.wordhoard.tools.PreprocessDirectoryTree /wordhoard/src /secret-src/ /wordhoard/licensetexts/
 *	</blockquote>
 *	</code>
 *	</p>
 *
 *	<p>
 *	ProcessDirectoryTree reports the number of Java files preprocessed,
 *	the number of files copied successfully, and the number of files
 *	which could not be copied.  An error message is display to standard
 *	output for each file which could not be copied.
 *	</p>
 *
 *	<p>
 *	Files whose names start with "." are not copied.
 *	</p>
 */

public class PreprocessDirectoryTree
{
	/**	The root of the source directory tree. */

	protected static String srcRootName		= "";

	/**	The root of the destination directory tree. */

	protected static String destRootName	= "";

	/**	The include file directory. */

	protected static String includeRootName	= "";

	/**	Total number of java files preprocessed. */

	protected static int totalJava			= 0;

	/**	Total number of java files which could not be preprocessed. */

	protected static int totalJavaBad		= 0;

	/**	Total number of non-java files successfully copied. */

	protected static int totalCopied		= 0;

	/**	Total number of non-java files which could not be copied. */

	protected static int totalNotCopied		= 0;

	/**	True to enable verbose output. */

	protected static boolean verbose		= false;

	/**	Preprocess or copy files in a directory.
	 *
	 *	@param	src				Source directory.
	 *	@param	preprocessor	The preprocessor to use.
	 *
	 *	<p>
	 *	All Java files are preprocessed.  Non-java files are copied.
	 *	Directories are processed recursively.
	 *	</p>
	 */

	protected static void runPreprocessor
	(
		File src ,
		Preprocessor preprocessor
	)
	{
		File[] files	=
			src.listFiles
			(
				new FileFilter()
				{
					public boolean accept ( File pathname )
					{
						String name	= pathname.getName();
						return ( name.charAt( 0 ) != '.' );
					}
				}
			);

		for ( int i = 0 ; i < files.length ; i++ )
		{
								//	Get next file or directory.

			File file	= files[ i ].getAbsoluteFile();

								//	If it is a directory,
								//	recurse into it.

			if ( file.isDirectory() )
			{
				runPreprocessor
				(
					file ,
					preprocessor
				);
			}
								//	It is a file.
			else
			{
								//	Get the source file name.

				String srcFilePath	= file.getAbsolutePath();
				String srcFileName	= file.getAbsoluteFile().getName();

								//	Get source file name extension.

				String srcFileNameExt	=
					FileNameUtils.getFileExtension( srcFileName , false );

								//	Get source file directory.

				String srcFileDirectory	= file.getParent();

								//	Create destination file name.

				String destFilePath			=
					new File
					(
						destRootName +
						srcFileDirectory.substring( srcRootName.length() ) ,
						srcFileName
					).getAbsolutePath();

								//	See if the source file is a java file.

				if ( srcFileNameExt.compareToIgnoreCase( "java" ) == 0 )
				{
								//	If a java file, preprocess it.

					if ( !FileUtils.createPathForFile( destFilePath ) )
					{
						if ( verbose )
						{
							System.out.println(
								"Could not preprocess " + srcFilePath +
								" to " + destFilePath );
						}

						totalJavaBad++;
					}
					else
					{
						preprocessor.preprocess(
							srcFilePath , destFilePath , true );

						if ( verbose )
						{
							System.out.println(
								"Preprocessed " + srcFilePath + " to " +
								destFilePath );
            					}

						totalJava++;
					}
				}
				else
				{
								//	If not a Java file, copy it.

					if ( FileUtils.copyFile( srcFilePath , destFilePath ) )
					{
						if ( verbose )
						{
							System.out.println(
								"Copied " + srcFilePath + " to " +
								destFilePath );
						}

						totalCopied++;
					}
					else
					{
						if ( verbose )
						{
							System.out.println(
								"Unable to copy " + srcFilePath +
								" to " + destFilePath );
						}

						totalNotCopied++;
					}
				}
			}
		}
	}

	/**	Print usage.
	 */

	protected static void usage()
	{
		System.out.println(
			"Usage: java edu.northwestern.at.wordhoard.tools." +
			"PreprocessDirectoryTree sourcedirectory " +
			"destinationdirectory includedirectory" );

		System.out.println( "" );

		System.out.println(
			"     sourcedirectory      -- " +
			"the root of the source directory tree" );

		System.out.println(
			"     destinationdirectory -- " +
			"the root of the destination directory tree" );

		System.out.println(
			"     includedirectory -- " +
			"the root of the include files directory tree" );
	}

	/**	Main program for PreProcessDirectoryTree. */

	public static void main( String[] args )
	{
								//	If there are not at least
								//	two command line arguments,
								//	print a usage message and quit.

		if ( args.length < 3 )
		{
			usage();
			System.exit( 1 );
		}
								//	First command line argument is
								//	source directory.

		File srcRoot			= new File( args[ 0 ] );
		srcRoot 			= srcRoot.getAbsoluteFile();

								//	Second command line argument is
								//	destination directory.

		File destRoot		= new File( args[ 1 ] );
		destRoot 			= destRoot.getAbsoluteFile();

								//	Third command line argument is
								//	the include files directory.

		File includeRoot	= new File( args[ 2 ] );
		includeRoot 		= includeRoot.getAbsoluteFile();

								//	Get full file names.

		srcRootName		= srcRoot.getAbsolutePath();
		destRootName		= destRoot.getAbsolutePath();
		includeRootName	= includeRoot.getAbsolutePath();

								//	Source directory must exist
								//	and be a valid directory.

		if ( !srcRoot.isDirectory() )
		{
			System.out.println(
				srcRootName + " is not a valid directory." );

			System.exit( 1 );
		}
								//	Destination directory must be exist
								//	or be creatable.

		if ( !FileUtils.createPath( destRoot ) )
		{
			System.out.println(
				"Unable to access or create output directory " +
				destRootName );

			System.exit( 1 );
		}
								//	Move to include directory.

		String oldDirectory		= FileUtils.chdir( includeRootName );
		String currentDirectory	= FileUtils.getCurrentDirectory();

		if ( !currentDirectory.equals( includeRootName ) )
		{
			System.out.println(
				"Unable to move to include file directory " +
				includeRootName );

			System.exit( 1 );
		}
								//	Dash line for separating portions
								//	of output.

		String dashedLine	=
			"---------------------------------------------------------" +
			"----------";

								//	Say what we're doing.

		System.out.println( dashedLine );

		System.out.println( "Preprocessing directory tree" );

		System.out.println(
			"   Source directory            : " + srcRootName );

		System.out.println(
			"   Destination directory       : " + destRootName );

		System.out.println(
			"   Included files directory    : " +includeRootName );

		System.out.println( dashedLine );

								//	Get time at which processing starts.

		long startTime	= System.currentTimeMillis();

								//	Run preprocessor on source
								//	directory tree.

		runPreprocessor( srcRoot , new Preprocessor() );

								//	Note how long processing took.

		long endTime	= System.currentTimeMillis();
		long procTime	= Math.round( ( endTime - startTime ) / 1000.0D );

								//	Report number of files processed.

		if ( verbose || ( totalNotCopied > 0 ) )
		{
			System.out.println( dashedLine );
		}

		System.out.println(
			"   Java files preprocessed     : " + totalJava );

		System.out.println(
			"   Java files not preprocessed : " + totalJavaBad );

		System.out.println(
			"   Non-Java files copied       : " + totalCopied );

		System.out.println(
			"   Files not copied            : " + totalNotCopied );

		System.out.println(
			"   Total time                  : " + procTime +
				( ( procTime == 1 ) ? " second" : " seconds" ) );
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


