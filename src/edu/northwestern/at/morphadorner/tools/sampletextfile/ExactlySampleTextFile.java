package edu.northwestern.at.morphadorner.tools.sampletextfile;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.math.randomnumbers.*;

/**	Exactly sample a text file.
 *
 *	<p>
 *	Usage:
 *	</p>
 *
 *	<p>
 *	java edu.northwestern.at.morphadorner.tools.sampletextfile.ExactlySampleTextFile input.txt output.txt samplecount<br />
 *	<br />
 *	input.txt -- input text file to be sampled.<br />
 *	output.txt -- output text file.<br />
 *	samplecount -- Size of random sample to extract. Must be positive integer.
 *	</p>
 *
 *	<p>
 *	The output file is a text file containing the sampled text lines
 *	from the input file.  Both the input and the output must be utf-8 encoded.
 *	The output lines are appended to any existing lines in the output file.
 *	</p>
 */

public class ExactlySampleTextFile extends SampleTextFile
{
	/**	Count of lines left in input file. */

	protected int totalCount	= 0;

	/**	Count of lines left to sample. */

	protected int sampleCount	= 0;

	/**	Main program.
	 *
	 *	@param	args	Program parameters.
	 */

	public static void main( String[] args )
	{
		try
		{
			if ( args.length < 3 )
			{
				System.err.println( "Too few arguments." );

				help();

				System.exit( 1 );
			}

			int sampleCount	= Integer.parseInt( args[ 2 ] );

			if ( sampleCount < 0 )
			{
				System.err.println(
					"Bad sample count -- must be an integer greater than zero." );

                System.exit( 1 );
			}
								//	Sample the input file to the output file.

			new ExactlySampleTextFile
			(
				args[ 0 ] , args[ 1 ] , sampleCount
			);
		}
		catch ( Exception e )
		{
			e.printStackTrace();

			System.exit( 1 );
		}
	}

	/**	Help text. */

	public static void help()
	{
		System.out.println();
		System.out.println(
			"java edu.northwestern.at.morphadorner.tools." +
			"sampletextfile.ExactlySampleTextFile input.txt " +
			"output.txt samplecount" );
		System.out.println();
		System.out.println(
			"   input.txt -- input text file to be sampled." );
		System.out.println(
			"   output.txt -- output text file." );
		System.out.println(
			"   samplecount -- count of lines to sample from input." );
	}

	/**	Copy a text file to another while sampling the input lines.
	 *
	 *	@param	inputFileName		Input file name.
	 *	@param	outputFileName		Output file name.
	 *	@param	sample				Sample count, percentage, etc.
	 */

	public ExactlySampleTextFile
	(
		String inputFileName ,
		String outputFileName ,
		int sample
	)
	{
		super( inputFileName , outputFileName , (double)sample );
	}

	/**	Setup sample.
	 *
	 *	@param	inputFileName		Input file name.
	 *	@param	outputFileName		Output file name.
	 *	@param	sample				Sample count, percentage, etc.
	 */


	protected void setupSampling
	(
		String inputFileName ,
		String outputFileName ,
		double sample
	)
	{
		this.sampleCount	= (int)sample;

		try
		{
								//	Count lines in input file.
			totalCount	= 0;

			UnicodeReader streamReader	=
				new UnicodeReader
				(
					new FileInputStream( new File( inputFileName ) ) ,
					"utf-8"
				);

			BufferedReader in	= new BufferedReader( streamReader );

			String inputLine	= in.readLine();

			while ( inputLine != null )
			{
								//	Update line count.
				totalCount++;
								//	Read next input line.

				inputLine	= in.readLine();
			}
								//	Rewind the input file.
			in.close();
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			System.out.println( "   *** Failed" );
		}
	}

	/**	Check if line should be selected.
	 *
	 *	@return	true to select line.
	 */

	protected boolean lineSelected()
	{
		double samp		= (double)sampleCount / (double)totalCount;
		boolean result	= ( RandomVariable.rand() < samp );

		if ( result )
		{
			sampleCount--;
		}

		totalCount--;

		return result;
	}

	/**	Determine if sampling done.
	 *
	 *	@return	true if sampling done.
	 */

	public boolean samplingDone()
	{
		return ( sampleCount <= 0 );
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



