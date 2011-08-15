package edu.northwestern.at.morphadorner.tools.mergewordlists;

/*	Please see the license information at the end of this file. */

import java.net.*;
import java.io.*;
import java.text.*;
import java.util.*;

import edu.northwestern.at.utils.*;

/**	Merge word lists.
 *
 *	<p>
 *	Usage:
 *	</p>
 *
 *	<p>
 *	java edu.northwestern.at.morphadorner.tools.mergewordlists.MergeWordLists output.txt input.txt input2.txt ...<br />
 *	<br />
 *	output.txt -- output merged word list file.<br />
 *	input*.txt -- input text files containing word lists to be merged.<br />
 *	</p>
 *
 *	<p>
 *	The output file is a utf-8 text file containing the merged word
 *	list from the input files.  Only one copy of a word is output
 *	if it appears multiple times.  The merged words appear in
 *	ascending alphanumeric order in the output file.
 *	</p>
 */

public class MergeWordLists
{
	/**	Merged word list. */

	protected static Set<String> mergedWordSet	= new TreeSet<String>();

	/**	Main program.
	 */

	public static void main( String[] args )
	{
								//	Pick up the file names to process.

		String[] fileNames	= new String[ args.length - 1 ];

		for ( int i = 1 ; i < args.length ; i++ )
		{
			fileNames[ i - 1 ]	= args[ i ];
		}
								//	Expand file name wildcard to
								//	full file name list.

		fileNames	= FileNameUtils.expandFileNameWildcards( fileNames );

		for ( int i = 0 ; i < fileNames.length ; i++ )
		{
			try
			{
				loadAndMergeWords( fileNames[ i ] );
			}
			catch ( Exception e )
			{
				e.printStackTrace();
			}
		}

		try
		{
			saveMergedWords( args[ 0 ] );
		}
		catch ( Exception e )
		{
			System.out.println( "Unable to save merged words." );
		}
	}

	/**	Merge word lists from a file.
	 */

	protected static void loadAndMergeWords( String inputFileName )
		throws Exception
	{
		Set<String> set	= SetUtils.loadSet( inputFileName , "utf-8" );

		System.out.println
		(
			"Merging " + set.size() + " words from " + inputFileName
		);

		mergedWordSet.addAll( set );
	}

	/**	Save the merged word lists.
	 */

	protected static void saveMergedWords( String outputFileName )
		throws Exception
	{
		System.out.println
		(
			"Saving " + mergedWordSet.size() + " words to " + outputFileName
		);

		SetUtils.saveSet( mergedWordSet , outputFileName , "utf-8" );
	}

	/**	Allow overrides but not instantiation.
	 */

	protected MergeWordLists()
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



