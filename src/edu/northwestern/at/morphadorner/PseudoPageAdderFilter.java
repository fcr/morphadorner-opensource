package edu.northwestern.at.morphadorner;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.text.*;
import java.util.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.xml.*;

/**	Filter to add pseudopage milestones to an adorned file.
  */

public class PseudoPageAdderFilter extends ExtendedXMLFilterImpl
{
	/**	List of tags for determining node ancestry of each word. */

	protected List<String> tagList	= ListFactory.createNewList();

	/**	Page size in number of tokens. */

	protected int pseudoPageSize		= 300;

	/**	Current pseudo page count. */

	protected int pseudoPageCount		= 0;

	/**	Current pseudo page word count. */

	protected int pseudoPageWordCount	= 0;

	/**	True if pseudo page started. */

	protected boolean pseudoPageStarted	= false;

	/**	Div tag stack. */

	protected QueueStack<String> divStack	= new QueueStack<String>();

	/**	Element attributes stack. */

	protected QueueStack<Attributes> attrStack	= new QueueStack<Attributes>();

	/**	Pseudo-page ending div types. */

	protected Set<String> pseudoPageContainerDivTypes	=
		SetFactory.createNewSet();

	/**	Create adorned word info filter.
	  *
	  *	@param	reader				XML input reader to which this filter applies.
	  *	@param	pseudoPageSize		Number of words in a pseudopage.
	  *	@param	pageEndingDivTypes	div types that end a pseudopage.
	  */

	public PseudoPageAdderFilter
	(
		XMLReader reader ,
		int pseudoPageSize ,
		String pageEndingDivTypes
	)
	{
		super( reader );
								//	Words per pseudopage.

		this.pseudoPageSize	= pseudoPageSize;

								//	Pseudo page ending div types.

		String[] divTypes	=
			StringUtils.makeTokenArray( pageEndingDivTypes );

		for ( int i = 0 ; i < divTypes.length ; i++ )
		{
			this.pseudoPageContainerDivTypes.add( divTypes[ i ].toLowerCase() );
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
								//	True to emit element.

		boolean emitElement	= true;

								//	Get modifiable attributes.

		AttributesImpl newAtts	= new AttributesImpl( atts );

								//	If we have a word tag element ...

		boolean isW	= qName.equals( "w" );
		boolean isLastWordPart	= false;

								//	See if we have a path (p=) attribute.

		String p	= atts.getValue( "p" );

		if ( isW )
		{
								//	See if this is the last (or only)
								//	part of a word.  If not, don't
								//	worry about pseudopage breaks.

			String part	= atts.getValue( "part" );

			if ( part == null )
			{
				part	= "N";
			}

			isLastWordPart	=
				part.equals( "N" ) || part.equals( "F" );

			if ( isLastWordPart )
			{
								//	Create start pseudopage element if
								//	this is the first word in a
								//	pseudopage.

				if	(	( pseudoPageWordCount == 0 ) &&
						( !pseudoPageStarted )
					)
				{
					if ( ( p != null ) && ( p.length() > 0 ) )
					{
						int bsPos	= p.lastIndexOf( "\\" );

	  	  				if ( bsPos > 0 )
						{
							p	= p.substring( 0 , bsPos );
						}

						p	= p + "\\milestone[" + ( pseudoPageCount + 1 ) + "]";
					}

					emitPseudoPageElement
					(
						createPseudoPageElement
						(
							uri ,
							false ,
							true ,
							p
						)
					);
				}
								//	Increment count of words in current
								//	pseudopage.

				pseudoPageWordCount++;
			}
		}
								//	Note if we have a div tag.  Save
								//	the div type if given, otherwise
								//	save "*div".

		else if ( qName.equalsIgnoreCase( "div" ) )
		{
			String divType	= atts.getValue( "type" );

			if ( ( divType == null ) || ( divType.length() == 0 ) )
			{
				divType	= "*div";
			}

			divStack.push( divType.toLowerCase() );
		}
								//	Evict the "part=" attribute from a
								//	"<c>" if the parser added it.

		else if ( qName.equalsIgnoreCase( "c" ) )
		{
			removeAttribute( newAtts , "part" );
		}
								//	Remove existing pseudopage milestones.

		else if ( qName.equalsIgnoreCase( "milestone" ) )
		{
			String unit	= newAtts.getValue( "type" );
			emitElement	= ( unit != null ) && !unit.equals( "pseudopage" );
		}
								//	Output the start element.

		if ( emitElement )
		{
			super.startElement( uri , localName , qName , newAtts );
		}
								//	Save attributes on stack.
								//	Note: we must save attributes
								//	even for deleted milestone elements
								//	because we need to remove the
								//	end milestone element as well.

		attrStack.push( newAtts );
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
		super.characters( ch , start , length );
	}

    /**	Handle whitespace.
     *
     *	@param	ch		Array of characters.
     *	@param	start	The starting position in the array.
     *	@param	length	The number of characters.
     *
     *	@throws	org.xml.sax.SAXException If there is an error.
     */

	public void ignorableWhitespace( char ch[] , int start , int length )
		throws SAXException
	{
		super.ignorableWhitespace( ch , start , length );
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
								//	True to emit element.

		boolean emitElement	= true;

								//	Pop the attribute stack.

		Attributes attrs	= new AttributesImpl();

		if ( !attrStack.isEmpty() )
		{
			attrs	= attrStack.pop();
		}
								//	Remember if we pop the div stack.

		boolean removedDiv		= false;
		String removedDivType	= "";

								//	Remember if this is a w tag.

		boolean isW				= false;
		boolean isLastWordPart	= false;

								//	If this the end of a div tag,
								//	pop the div stack.

		if ( qName.equals( "div" ) )
		{
			if ( !divStack.isEmpty() )
			{
				removedDivType	= divStack.pop();
				removedDiv		= true;
			}
		}
		else if ( qName.equals( "w" ) )
		{
			isW	= true;

			String part		= attrs.getValue( "part" );

			isLastWordPart	=
				( part == null ) ||
				part.equals( "N" ) ||
				part.equals( "F" );
		}
								//	Remove existing pseudopage milestones.

		else if ( qName.equalsIgnoreCase( "milestone" ) )
		{
			String unit	= attrs.getValue( "type" );
			emitElement	= ( unit != null ) && !unit.equals( "pseudopage" );
		}

		if ( emitElement )
		{
			super.endElement( uri , localName , qName );
		}
								//	If this is the end of a div,
								//	and it is a pseudopage ending
								//	div type, make sure we emit
								//	the end pseudopage.
		String p	= null;

		if	(	removedDiv &&
				pseudoPageContainerDivTypes.contains( removedDivType )
			)
		{

			if ( ( p != null ) && ( p.length() > 0 ) )
			{
				int bsPos	= p.lastIndexOf( "\\" );

				if ( bsPos > 0 )
				{
					p	= p.substring( 0 , bsPos );
				}

				p	= p + "\\milestone[" + ( pseudoPageCount + 1 ) + "]";
			}

			emitPseudoPageElement
			(
				createPseudoPageElement
				(
					uri ,
					false ,
					false ,
					p
				)
			);
		}
		else if ( isW && isLastWordPart )
		{
								//	Create end pseudopage element if
								//	this is the last word in a
								//	pseudopage.

			if ( pseudoPageWordCount >= pseudoPageSize )
			{
				if ( ( p != null ) && ( p.length() > 0 ) )
				{
					int bsPos	= p.lastIndexOf( "\\" );

					if ( bsPos > 0 )
					{
						p	= p.substring( 0 , bsPos );
					}

					p	= p + "\\milestone[" + ( pseudoPageCount + 1 ) + "]";
				}

				emitPseudoPageElement
				(
					createPseudoPageElement
					(
						uri ,
						false ,
						false ,
						p
					)
				);
			}
		}
	}

	/**	Create a pseudo page milestone.
	 *
	 *	@param	uri			Element URI.
	 *	@param	forcedEmit	Emit pseudo page milestone even if
	 *						not enough words accumulated, as long as
	 *						at least one word in current block.
	 *	@param	start		true if starting milestone, false if ending.
	 *	@param	path		Path attribute.  May be null.
	 *
	 *	@return				The pseudo page element.
	 */

	public PendingElement createPseudoPageElement
	(
		String uri ,
		boolean forcedEmit ,
		boolean start ,
		String path
	)
	{
								//	Increment pseudo page count
								//	if starting new pseudo page.
		if ( start )
		{
			pseudoPageCount++;
			pseudoPageStarted	= true;
		}
		else
		{
			pseudoPageStarted	= false;
		}
								//	Clear pseudo page word count.

		pseudoPageWordCount	= 0;

								//	Create attributes holder for
								//	milestone element.

		AttributesImpl pageAttributes	= new AttributesImpl();

								//	Create "unit=pseudopage" attribute for
								//	pseudo page count.
		setAttributeValue
		(
			pageAttributes ,
			"unit" ,
			"pseudopage"
		);
								//	Create "n=" attribute for
								//	pseudo page count.
		setAttributeValue
		(
			pageAttributes ,
			"n" ,
			pseudoPageCount + ""
		);
								//	Create "position=" attribute for
								//	pseudo page count.
		setAttributeValue
		(
			pageAttributes ,
			"position" ,
			( start ? "start" : "end" )
		);
								//	Add "p=" path attribute if not null.

		if ( ( path != null ) && ( path.length() > 0 ) )
		{
			setAttributeValue
			(
				pageAttributes ,
				"p" ,
				path
			);
		}
								//	Create the pseudo page element.
		return
			new PendingElement
			(
				uri  ,
				"milestone" ,
				"milestone" ,
				pageAttributes
			);
	}

	/**	Emit a pseudo page milestone.
	 *
	 *	@param	pseudoPageElement	The pseudo page element to emit.
	 */

	public void emitPseudoPageElement( PendingElement pseudoPageElement )
	{
		if ( pseudoPageElement != null )
		{
			try
			{
				super.startElement
				(
					pseudoPageElement.getURI() ,
					pseudoPageElement.getLocalName() ,
					pseudoPageElement.getQName() ,
					pseudoPageElement.getAttributes()
				);

				super.endElement
				(
					pseudoPageElement.getURI() ,
					pseudoPageElement.getLocalName() ,
					pseudoPageElement.getQName()
				);
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



