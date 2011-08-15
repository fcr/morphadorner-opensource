package edu.northwestern.at.utils.corpuslinguistics.outputter;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;

import edu.northwestern.at.utils.*;

/**	Outputs adorned words to a print stream.
 */

public class PrintStreamAdornedWordOutputter
	implements AdornedWordOutputter
{
	/**	PrintStream for console output. */

	protected PrintStream printStream	= null;

	/**	Separator character for output.
	 */

	protected char separatorCharacter	= '\t';

	/**	Output file name. */

	protected String fileName			= null;

	/**	Output file encoding. */

	protected String fileEncoding		= "utf-8";

	/**	Word attribute names. */

	protected String[] wordAttributeNames;

	/**	Create outputter.
	 */

	public PrintStreamAdornedWordOutputter()
	{
	}

	/**	Create output file.
	 *
	 *	@param	fileName				Output file name.
	 *	@param	encoding 				Encoding for the output file.
	 *	@param	separatorCharacter		Output separator character.
	 */

	public void createOutputFile
	(
		String fileName ,
		String encoding ,
		char separatorCharacter
	)
		throws IOException
	{
		if ( !FileUtils.createPathForFile( fileName ) )
		{
			throw new IOException( "Unable to create output directory." );
		};

		this.fileName			= fileName;
		this.fileEncoding		= encoding;
		this.separatorCharacter	= separatorCharacter;

		FileOutputStream outputStream	=
			new FileOutputStream( fileName , false );

		String safeEncoding	= ( encoding == null ) ? "" : encoding;

		if ( safeEncoding.length() > 0 )
		{
			printStream	=
				new PrintStream
				(
					new BufferedOutputStream( outputStream ),
					true ,
					encoding
				);
		}
		else
		{
			printStream	=
				new PrintStream
				(
					new BufferedOutputStream( outputStream ) ,
					true
				);
		}
	}

	/**	Set word attribute names.
	 *
	 *	@param	wordAttributeNames	Word attribute names.
	 */

	public void setWordAttributeNames( List<String> wordAttributeNames )
	{
		this.wordAttributeNames	= new String[ wordAttributeNames.size() ];

		for ( int i = 0 ; i < wordAttributeNames.size() ; i++ )
		{
			this.wordAttributeNames[ i ] 	= wordAttributeNames.get( i );
		}
	}

	/**	Outputs a word and its adornments (part of speech, lemmata, etc).
	 *
	 *	@param	wordAndAdornments	Word and its adornments as an
	 *								array of string.
	 *
	 *	@throws	IOException			If an output error occurs.
	 *
	 *	<p>
	 *	Outputs word and adornments as a tab-separated text line to
	 *	a print stream.
	 *	</p>
	 */

	 public void outputWordAndAdornments( String[] wordAndAdornments )
	 	throws IOException
	 {
		for ( int i = 0 ; i < wordAndAdornments.length ; i++ )
		{
			if ( i > 0 ) printStream.print( separatorCharacter );
			printStream.print( wordAndAdornments[ i ] );
		}

		printStream.println( "" );
	 }

	/**	Outputs a word and its adornments (part of speech, lemmata, etc).
	 *
	 *	@param	wordAndAdornments	Word and its adornments as a list
	 *								of strings.
	 *
	 *	@throws	IOException			If an output error occurs.
	 *
	 *	<p>
	 *	Outputs word and adornments as a tab-separated text line to
	 *	a print stream.
	 *	</p>
	 */

	 public void outputWordAndAdornments( List<String> wordAndAdornments )
	 	throws IOException
	 {
		for ( int i = 0 ; i < wordAndAdornments.size() ; i++ )
		{
			if ( i > 0 ) printStream.print( separatorCharacter );
			printStream.print( wordAndAdornments.get( i ) );
		}

		printStream.println( "" );
	 }

	/**	Get output file name.
	 *
	 *	@return	Output file name.
	 */

	public String getOutputFileName()
	{
		return fileName;
	}

	/**	Get output file encoding.
	 *
	 *	@return	Output file encoding.
	 */

	public String getOutputFileEncoding()
	{
		return fileEncoding;
	}

	/** Close outputter.
	 */

	public void close()
	{
		if ( printStream != null )
		{
			try
			{
				printStream.flush();
				printStream.close();
			}
			catch ( Exception e )
			{
			}
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



