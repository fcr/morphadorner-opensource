package edu.northwestern.at.utils.xml;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;

import com.megginson.sax.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import edu.northwestern.at.utils.*;

/**	XMLWriter which indents the output before each tag.
 *
 *	<p>
 *	This code assumes the XML is not currently indented.
 *	</p>
 */

public class IndentingXMLWriter extends XMLWriter
{
	/**	Number of spaces to indent each level of XML. */

	protected int indentStep	= 0;

	/**	Blanks for indentation. */

	protected char[] indents	= new char[ 0 ];

	/**	End of line character(s). */

 	protected char[] eol		= Env.LINE_SEPARATOR.toCharArray();

	/**	Stack which remembers if a tag has at least one child.
	 *
	 *	<p>
	 *	The start and end tags of an element with no children are
	 *	kept on the same line.
	 *	</p>
	 */

	protected QueueStack<Boolean> childStack	= new QueueStack<Boolean>();

	/**	Create indenting XML writer.
	 *
	 *	@param	xmlReader	The XML Reader to which this writer is attached.
	 *	@param	writer		The output writer to which to output the
	 *						indented XML.
	 */

    public IndentingXMLWriter
    (
    	XMLReader xmlReader ,
    	Writer writer
    )
    {
    	super( xmlReader , writer );
	}

    /**	Return the current indent step.
     *
     * @return	The number of indents in each indentation step.
     *			0 or less for no indentation.
     */

	public int getIndentStep()
	{
		return indentStep;
	}

    /**	Set the current indent step.
     *
     *	@param	indentStep	The new indent step (0 or less for no
     *						indentation).
     */

	public void setIndentStep( int indentStep )
	{
		this.indentStep = indentStep;

		if ( indentStep > 0 )
		{
			indents	= new char[ indentStep ];

			Arrays.fill( indents , ' ' );
		}
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
								//	Emit EOL before new element. */

		super.ignorableWhitespace( eol , 0 , eol.length );

								//	Indent this line based upon
								//	depth in XML tree.  The number
								//	of elements on the child stack
								//	provides the number of parents
								//	of this element, and the number
								//	of indentation blocks.
		if ( indentStep > 0 )
		{
			for ( int i = 0 ; i < childStack.size() ; i++ )
			{
				super.ignorableWhitespace( indents , 0 , indents.length );
			}
		}
								//	If this element has a parent,
								//	set the child stack value for the
								//	parent to true.

		if ( !childStack.isEmpty() )
		{
			childStack.pop();
			childStack.push( true );
		}
								//	Assume this element has no children.
								//	We update this if we find children
								//	later.

		childStack.push( false );

								//	Perform standard processing
								//	for this element.

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
									//	See if this element
									//	had any children.

		boolean hadChildElement = childStack.pop();

		if ( hadChildElement )
		{
									//	If so, emit EOL, and
									//	output indentation before
									//	the close tag.

			super.ignorableWhitespace( eol , 0 , eol.length );

			if ( indentStep > 0 )
			{
				for ( int i = 0 ; i < childStack.size(); i++ )
				{
					super.ignorableWhitespace( indents , 0 , indents.length );
				}
			}
		}
								//	Perform standard processing
								//	for this element.

		super.endElement( uri , localName , qName );
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



