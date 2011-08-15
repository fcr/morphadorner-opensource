package edu.northwestern.at.morphadorner.tools;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.text.*;
import java.util.*;

import com.megginson.sax.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.xml.*;

/**	Filter contents of an adorned file.
 */

public class FilterAdornedFile
{
	/**	Filter contents of an adorned file.
	 *
	 *	@param	inputXMLFile	Input XML file.
	 *	@param	outputXMLFile	Output XML file.
	 *	@param	filter			XML filter.
	 *
	 *	@throws SAXException			When an XML parsing error occurs.
	 *	@throws FileNotFoundException	When a file cannot be found.
	 *	@throws IOException				When an I/O error occurs.
	 *
	 *	<p>
	 *	The contents of the input XML file are filtered
	 *	using the specified filter.  The filtered results
	 *	are written to the output XML file.
	 *	</p>
	 */

	public FilterAdornedFile
	(
		String inputXMLFile ,
		String outputXMLFile ,
		XMLFilter filter
	)
		throws SAXException, FileNotFoundException, IOException
	{
								//	Make sure output directory
								//	exists for XML output file.

		FileUtils.createPathForFile( outputXMLFile );

								//	Create pretty-printing
								//	XML output writer.

		OutputStreamWriter outputStreamWriter	=
			new OutputStreamWriter
			(
				new BufferedOutputStream
				(
					new FileOutputStream( outputXMLFile )
				) ,
				"utf-8"
			);

		IndentingXMLWriter writer	=
			new IndentingXMLWriter
			(
				filter ,
				outputStreamWriter
			);
								//	Output characters without
								//	converting to entity form.

		writer.setOutputCharsAsIs( true );

								//	Indent output by 2 spaces
								//	for each nested element.

		writer.setIndentStep( 2 );

								//	Create whitespace trimming
								//	XML input reader.

		WhitespaceTrimmingBufferedReader bufferedReader	=
			new WhitespaceTrimmingBufferedReader
			(
				new UnicodeReader
				(
					new FileInputStream( inputXMLFile ) ,
					"utf-8"
				)
			);

		InputSource inputSource	= new InputSource( bufferedReader );

								//	Parse, filter, and output
								//	filtered XML.

		writer.parse( inputSource );

								//	Close files.

		bufferedReader.close();
		outputStreamWriter.close();
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



