package edu.northwestern.at.morphadorner;

/*	Please see the license information at the end of this file. */

import org.xml.sax.*;
import org.xml.sax.helpers.*;

/**	Holds an XML element whose output is pending. */

public class PendingElement
{
	/**	Element URI. */

	protected String uri;

	/**	Element local name. */

    protected String localName;

	/**	Element qname. */

	protected String qName;

	/**	Element attributes. */

	protected AttributesImpl atts;

	/**	Element text. */

	protected String text;

	/**	Create element.
	 *
	 *	@param	uri			URI.
	 *	@param	localName	Local name.
	 *	@param	qName		Qname.
	 *	@param	atts		Attributes.
	 */

	public PendingElement
	(
		String uri ,
		String localName ,
		String qName ,
		AttributesImpl atts
	)
	{
		this.uri		= uri;
   		this.localName	= localName;
		this.qName		= qName;
		this.atts		= atts;
		this.text		= "";
	}

	/**	Append characters to text.
	 *
	 *	@param	ch		Array of characters.
	 *	@param	start	The starting position in the array.
	 *	@param	length	The number of characters.
	 */

	public void appendText( char ch[] , int start , int length )
	{
		text	= text + new String( ch , start , length );
	}

	/**	Get URI.
	 *
	 *	@return	Element URI.
	 */

	public String getURI()
	{
		return uri;
	}

	/**	Get Qname.
	 *
	 *	@return	Element qname.
	 */

	public String getQName()
	{
		return qName;
	}

	/**	Get local name.
	 *
	 *	@return	Element local name.
	 */

	public String getLocalName()
	{
		return localName;
	}

	/**	Get attributes.
	 *
	 *	@return	Element attributes.
	 */

	public AttributesImpl getAttributes()
	{
		return atts;
	}

	/**	Get text.
	 *
	 *	@return	Element text.
	 */

	public String getText()
	{
		return text;
	}

	/**	Return text.
	 *
	 *	@return	Element text.
	 */

	public String toString()
	{
		return text;
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



