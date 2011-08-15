package edu.northwestern.at.utils.xml;

/*	Please see the license information at the end of this file. */

import java.io.*;

import java.text.*;
import java.util.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.xml.*;

/**	SAX event handler to extract text from a TEI XML file.
 *
 *	<p>
 *	Only the text between &lt;text&gt; and &lt;/text&gt; tags
 *	is extracted.  No effort is made to capture any of the
 *	original text division marked by the XML tags.
 *	</p>
 */

public class TEITextExtractorHandler extends DefaultHandler
{
	/**	Holds the extracted text. */

	protected StringBuffer extractedText	= new StringBuffer();

	/**	Track if we're in <text> element. */

	protected static boolean inText	= false;

	/**	Create text extractor handler.
	  */

	public TEITextExtractorHandler()
	{
		super();
	}

	/**	Handle start of an XML element.
	  *
	  *	@param	uri			The XML element's URI.
	  *	@param	localName	The XML element's local name.
	  *	@param	qName		The XML element's qname.
	  *	@param	atts		The XML element's attributes.
	  */

	public void startElement
	(
		String uri ,
		String localName ,
		String qName ,
		Attributes atts
	)
		throws SAXException
	{
								//	Start accumulating text
								//	if <text> seen.

		if ( qName.equals( "text" ) )
		{
			inText	= true;
		}

		super.startElement( uri , localName , qName , atts );
	}

	/**	Handle end of an element.
	 *
	 *	@param	uri			The XML element's URI.
	 *	@param	localName	The XML element's local name.
	 *	@param	qName		The XML element's qname.
	 */

	public void endElement
	(
		String uri ,
		String localName ,
		String qName
	)
		throws SAXException
	{
								//	Stop accumulating text
								//	if </text> found.

		if ( qName.equals( "text" ) )
		{
			inText	= false;
		}

		super.endElement( uri , localName , qName );
	}

	/**	Handle character data.
	 *
	 *	@param	ch		Array of characters.
	 *	@param	start	The starting position in the array.
	 *	@param	length	The number of characters.
	 *
	 *	@throws	org.xml.sax.SAXException If there is an error.
	 */

	public void characters( char ch[] , int start , int length )
		throws SAXException
	{
								//	If we're in <text> ... </text>,
								//	append these characters to
								//	the output text.
		if ( inText )
		{
			extractedText.append( new String( ch , start , length ) );
		}

		super.characters( ch , start , length );
	}

	/**	Return extracted text.
	 *
	 *	@return		The extracted text.
	 */

	public String getExtractedText()
	{
		return extractedText.toString();
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



