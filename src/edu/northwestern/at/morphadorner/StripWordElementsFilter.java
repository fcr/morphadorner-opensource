package edu.northwestern.at.morphadorner;

/*	Please see the license information at the end of this file. */

import java.io.*;

import java.text.*;
import java.util.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.xml.*;

/**	Filter to strip word elements for specified tags from adorned file.
  */

public class StripWordElementsFilter extends ExtendedXMLFilterImpl
{
	/**	Set of tags from which to strip words. */

	protected Set<String> elementsToStripSet;

	/**	Strip element stack. */

	protected QueueStack<String> stripElementStack	=
		new QueueStack<String>();

	/**	True if processing w or c element. */

	protected boolean processingWorC;

	/**	Create filter.
	  *
	  *	@param	reader			XML input reader to which filter applies.
	  *	@param	elementsToStrip	Elements to strip separated by spaces.
	  */

	public StripWordElementsFilter
	(
		XMLReader reader ,
		String elementsToStrip
	)
	{
		super( reader );

								//	Create set of elements from which
								//	to strip <w> and <c> elements.

		elementsToStripSet	= SetFactory.createNewSet();

		elementsToStripSet.addAll
		(
			Arrays.asList( StringUtils.makeTokenArray( elementsToStrip ) )
		);
								//	Not processing <w> or <c> yet.

		processingWorC	= false;
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
								//	If this element is one from which
								//	we're to strip <w> and <c> elements,
								//	push the element onto the stack.

		if ( elementsToStripSet.contains( localName ) )
		{
			stripElementStack.push( localName );
		}
								//	Not processing <w> or <c> yet.

		processingWorC	= false;

								//	If we are in an element from which
								//	to strip <w> and <c> ...

		if ( stripElementStack.size() > 0 )
		{
								//	Set flag if we're starting a <w>
								//	or <c> element.  We do not emit
								//	the <w> or <c> element, just its
								//	text.

			if ( qName.equals( "w" ) )
			{
				processingWorC	= true;
			}
			else if ( qName.equals( "c" ) )
			{
				processingWorC	= true;
			}
			else
			{
				super.startElement( uri , localName , qName , atts );
			}
		}
		else
		{
			super.startElement( uri , localName , qName , atts );
		}
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
								//	If we're in a descendent of an element
								//	from which to strip <w> and <c>,
								//	only emit character if we're processing
								//	a <w> or <c> element.

		if ( stripElementStack.size() > 0 )
		{
			if ( processingWorC )
			{
				super.characters( ch , start , length );
			}
		}
		else
		{
			super.characters( ch , start , length );
		}
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
								//	Pop stack if this is the end of an
								//	element from which to remove <w>
								//	or <c>.

		if ( elementsToStripSet.contains( localName ) )
		{
			stripElementStack.pop();
		}
								//	Not processing <w> or <c> anymore.

		processingWorC	= false;

								//	Do not output end element tag
								//	for skipped <w> or <c>.

		if ( stripElementStack.size() > 0 )
		{
			if ( qName.equals( "w" ) )
			{
			}
			else if ( qName.equals( "c" ) )
			{
			}
        	else
        	{
				super.endElement( uri , localName , qName );
			}
		}
		else
		{
			super.endElement( uri , localName , qName );
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



