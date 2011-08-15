package edu.northwestern.at.morphadorner.tools;

/*	Please see the license information at the end of this file. */

import java.io.*;

import java.text.*;
import java.util.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.xml.*;

/**	Filter to add XML tag path (p=) attributes to an adorned file.
  */

public class AddXMLPathFilter extends ExtendedXMLFilterImpl
{
	/**	Tag stack. */

	protected List<String> tagStack	= ListFactory.createNewList();

	/**	Tag count stack. */

	protected List<Map<String,Integer>> tagCounts	=
		ListFactory.createNewList();

	/**	Path root.  Prepended to all XML paths. */

	protected String pathRoot	= "\\";

	/**	Create filter.
	  *
	  *	@param	reader		XML input reader to which this filter applies.
	  *	@param	pathRoot	Root string prepended to all tag paths.
	  */

	public AddXMLPathFilter( XMLReader reader , String pathRoot )
	{
		super( reader );

		if ( pathRoot != null )
		{
			this.pathRoot	= "\\" + pathRoot;
		}
	}

	/**	Handle start of document. */

	public void startDocument()
	{
		Map<String, Integer> counts	= MapFactory.createNewMap();

		tagCounts.add( counts );
	}

	/**	Handle start of an XML element.
	  *
	  *	@param	uri			The XML element's URI.
	  *	@param	localName	The XML element's local name.
	  *	@param	qName		The XML element's qname.
	  *	@param	atts			The XML element's attributes.
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
		Map<String, Integer> oldCounts	=
			tagCounts.get( tagCounts.size() - 1 );

		Integer oldCount	= oldCounts.get( qName );

		if ( oldCount == null )
		{
			oldCount	= 1;
		}
		else
		{
			oldCount++;
		}

		oldCounts.put( qName , oldCount );

		tagStack.add( qName );

		Map<String, Integer> counts	= MapFactory.createNewMap();

		tagCounts.add( counts );

		AttributesImpl newAttributes	= new AttributesImpl( atts );

		setAttributeValue( newAttributes , "p" , createXMLPath() );

		super.startElement( uri , localName , qName , newAttributes );
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
		tagStack.remove( tagStack.size() - 1 );
		tagCounts.remove( tagCounts.size() - 1 );

		super.endElement( uri , localName , qName );
	}

	/**	Create XML Path from current tag stack state.
	 *
	 *	@return	XML path from current tag stack state.
	 */

	protected String createXMLPath()
	{
		String result	= pathRoot;

		for ( int i = 0 ; i < tagStack.size() ; i++ )
		{
			String tag	= tagStack.get( i );

			if ( ( i == 0 ) && tag.equals( "TEI" ) )
			{
				continue;
			}
			else if ( ( i == 1 ) && tag.equals( "text" ) )
			{
				continue;
			}

			Map<String, Integer> counts	= tagCounts.get( i );

			result	=
				result + "\\" + tag + "[" + counts.get( tag ) + "]";
		}

		return result;
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



