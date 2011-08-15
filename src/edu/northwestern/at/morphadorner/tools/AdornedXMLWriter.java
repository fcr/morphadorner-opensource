package edu.northwestern.at.morphadorner.tools;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.text.*;
import java.util.*;

import org.jdom.*;
import org.jdom.filter.*;
import org.jdom.input.*;
import org.jdom.output.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.xml.*;

/**	Writes JDOM document in MorphAdorner XML output format.
 */

public class AdornedXMLWriter
{
	/**	Create adorned XML Writer.
	 *
	 *	@param	document			The JDOM document to output.
	 *	@param	xmlOutputFileName	The XML output file name.
	 *
	 *	@throws	java.io.IOException	when an I/O exception occurs.
	 */

	public AdornedXMLWriter
	(
		Document document ,
		String xmlOutputFileName
	)
		throws org.xml.sax.SAXException, java.io.IOException
	{
		outputXML( document , xmlOutputFileName );
	}

	/**	Output XML.
	 *
	 *	@param	document			The JDOM document to output.
	 *	@param	xmlOutputFileName	The XML output file name.
	 *
	 *	@throws	java.io.IOException	when an I/O exception occurs.
	 */

	protected void outputXML
	(
		Document document ,
		String xmlOutputFileName
	)
		throws java.io.IOException
	{
								//	Use raw format as base
								//	for output.

		org.jdom.output.Format format	=
			org.jdom.output.Format.getRawFormat();

								//	Two character indentation.

		format.setIndent( "  " );

								//	UTF-8 output always for
								//	MorphAdorner XML output.

		format.setEncoding( "utf-8" );

								//	Expand empty elements.

		format.setExpandEmptyElements( true );

								//	Normalize whitespace for
								//	most elements.

		format.setTextMode( org.jdom.output.Format.TextMode.NORMALIZE );

								//	Create special XML outputter
								//	so we can modify output for
								//	certain elements.

		XMLOutputter xmlOut	= new AdornerXMLOutputter( format );

								//	Make sure output directory
								//	exists for XML output file.

		FileUtils.createPathForFile( xmlOutputFileName );

								//	Open XML output file.

		FileOutputStream outputStream			=
			new FileOutputStream( xmlOutputFileName , false );

		BufferedOutputStream bufferedStream	=
			new BufferedOutputStream( outputStream );

		OutputStreamWriter writer	=
			new OutputStreamWriter( bufferedStream , "utf-8" );

								//	Output the XML.

		xmlOut.output( document , writer );
	}

	/**	Adorner XML Outputter that handles "<c>" elements specially.
	 */

	public class AdornerXMLOutputter
		extends XMLOutputter
	{
		/**	Output format for XML. */

		protected org.jdom.output.Format outputFormat;

		/**	Create XML outputter.
		 *
		 *	@param	format	The output format.
		 */

		public AdornerXMLOutputter( org.jdom.output.Format format )
		{
			super( format );

			outputFormat	= format;
		}

		/**	Print an XML element.
		 *
		 *	@param	out			The output writer.
		 *	@param	element		The element to output.
		 *	@param	level		The current element depth.
		 *	@param	namespaces	The XML namespaces.
		 *
		 *	@throws	java.io.IOException	when an I/O exception occurs.
		 */

		protected void printElement
		(
			java.io.Writer out,
			Element element,
			int level,
			XMLOutputter.NamespaceStack namespaces
		)
			throws java.io.IOException
		{
								//	If element is a "c",
								//	remove the "part=" attribute,
								//	and change the whitespace output
								//	to preserve the embedded blanks.

			if ( element.getName().equals( "c" ) )
			{
				element.removeAttribute( "part" );

				outputFormat.setTextMode
				(
					org.jdom.output.Format.TextMode.PRESERVE
				);

				setFormat( outputFormat );
			}

			super.printElement( out , element , level , namespaces );

								//	Change back to normalized
								//	whitespace output once we're
								//	through outputting the "c".

			if ( element.getName().equals( "c" ) )
			{
				outputFormat.setTextMode
				(
					org.jdom.output.Format.TextMode.NORMALIZE
				);

				setFormat( outputFormat );
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



