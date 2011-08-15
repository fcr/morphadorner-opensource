package edu.northwestern.at.morphadorner.tools.sampletextfile;

/*	Please see the license information at the end of this file. */

import java.io.*;

import edu.northwestern.at.utils.*;

/**	Sample a text file.
 *
 *	<p>
 *	Copies one text file to another with a selection criterion.
 *	</p>
 *
 *	<p>
 *	Subclasses must implement the setupSampling, lineSelected, and
 *	samplingDone methods.
 *	</p>
 */

abstract public class SampleTextFile
{
	/**	Copy a text file to another while sampling the input lines.
	 *
	 *	@param	inputFileName		Input file name.
	 *	@param	outputFileName		Output file name.
	 *	@param	sample				Sample count, percentage, etc.
	 */

	public SampleTextFile
	(
		String inputFileName ,
		String outputFileName ,
		double sample
	)
	{
		try
		{
								//	Setup sampling.

			setupSampling( inputFileName , outputFileName , sample );

								//	Open input file.

			UnicodeReader streamReader	=
				new UnicodeReader
				(
					new FileInputStream( new File( inputFileName ) ) ,
					"utf-8"
				);

			BufferedReader in	= new BufferedReader( streamReader );

								//	Open output file.

			PrintWriter printWriter	=
				new PrintWriter
				(
					new OutputStreamWriter
					(
						new FileOutputStream( outputFileName , true ) ,
						"utf-8"
					)
				);
								//	Read each line of input file and
								//	copy selected lines to output file.

			String inputLine	= in.readLine();

			while ( ( inputLine != null ) && !samplingDone() )
			{
								//	Write input line to output if selected.

				if ( lineSelected() )
				{
					printWriter.println( inputLine );
				}
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

	/**	Copy a text file to another while sampling the input lines.
	 *
	 *	@param	inputFileName		Input file name.
	 *	@param	outputFileName		Output file name.
	 *	@param	sample				Sample count, percentage, etc.
	 */

	public SampleTextFile
	(
		String inputFileName ,
		String outputFileName ,
		int sample
	)
	{
		this( inputFileName , outputFileName , (double)sample );
	}

	/**	Set up the sampling.
	 *
	 *	@param	inputFileName		Input file name.
	 *	@param	outputFileName		Output file name.
	 *	@param	sample				Sample count, percentage, etc.
	 */

	abstract protected void setupSampling
	(
		String inputFileName ,
		String outputFileName ,
		double sample
	);

	/**	Check if line should be selected.
	 *
	 *	@return	true to select line.
	 *
	 *	<p>
	 *	Subclasses must override this method.
	 *	</p>
	 */

	abstract protected boolean lineSelected();

	/**	Determine if sampling done.
	 *
	 *	@return	true if sampling done.
	 */

	public boolean samplingDone()
	{
		return false;
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



