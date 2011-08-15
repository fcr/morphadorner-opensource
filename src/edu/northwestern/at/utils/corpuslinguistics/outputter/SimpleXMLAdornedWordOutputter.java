package edu.northwestern.at.utils.corpuslinguistics.outputter;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;

import edu.northwestern.at.utils.*;

/**	Outputs adorned words to a print stream in a simple XML format.
 *
 *	<p>
 *	The output format is as follows.
 *	</p>
 *
 *	<words>
 *	  <word id="1">
 *		<tok>Poets</tok>
 *	    <spe>Poets</spe>
 *	    <pos>n2</pos>
 *	    <reg>Poets</reg>
 *	    <lem>poet</lem>
 *	    <eos>0</eos>
 *    </word>
 *    <word id="2">
 *      ...
 *	  </word>
 *		...
 *	</words>
 *
 *	<p>
 *	There may be more or fewer word level children depending upon
 *	which output options are chosen.
 *	</p>
 */

public class SimpleXMLAdornedWordOutputter
	extends PrintStreamAdornedWordOutputter
	implements AdornedWordOutputter
{
	/**	Word ID. */

	protected int id	= 0;

	/**	Create outputter.
	 */

	public SimpleXMLAdornedWordOutputter()
	{
		super();
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
		super.createOutputFile( fileName , encoding , separatorCharacter );

		printStream.println( "<words>" );
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
		printStream.println( "  <word id=\"" + id++ + "\">" );

		for ( int i = 0 ; i < wordAndAdornments.length ; i++ )
		{
			printStream.print
			(
				"    <" + wordAttributeNames[ i ] + ">"
			);

			printStream.print( wordAndAdornments[ i ] );

			printStream.println
			(
				"</" + wordAttributeNames[ i ] + ">"
			);
		}

		printStream.println( "  </word>" );
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
		printStream.println( "  <word id=\"" + id++ + "\">" );

		for ( int i = 0 ; i < wordAndAdornments.size() ; i++ )
		{
			printStream.print
			(
				"    <" + wordAttributeNames[ i ] + ">"
			);

			printStream.print( wordAndAdornments.get( i ) );

			printStream.println
			(
				"</" + wordAttributeNames[ i ] + ">"
			);
		}

		printStream.println( "  </word>" );
	 }

	/** Close outputter.
	 */

	public void close()
	{
		printStream.println( "</words>" );

		super.close();
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



