package edu.northwestern.at.morphadorner;

/*	Please see the license information at the end of this file. */

import java.io.*;

import java.text.*;
import java.util.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.xml.*;

import edu.northwestern.at.morphadorner.tools.*;

/**	Filter to add word attributes to adorned file.
  */

public class AddWordAttributesFilter extends ExtendedXMLFilterImpl
{
	/**	ExtendedAdornedWordFilter providing word attribute information. */

	protected ExtendedAdornedWordFilter wordInfoFilter;

	/**	True to output non-redundant attributes only. */

	protected boolean outputNonredundantAttributesOnly	= false;

	/**	True to output non-redundant token attributes only. */

	protected boolean outputNonredundantTokenAttribute	= false;

	/**	True to output whitespace elements. */

	protected boolean outputWhitespace	= true;

	/**	True to output word number attributes. */

	protected boolean outputWordNumber	= false;

	/**	True to output sentence number attributes. */

	protected boolean outputSentenceNumber	= false;

	/**	True to output word ordinal attributes. */

	protected boolean outputWordOrdinal	= false;

	/**	Create filter.
	  *
	  *	@param	reader			XML input reader to which this filter applies.
	  *	@param	wordInfoFilter	ExtendedAdornedWordFilter with word information.
	  */

	public AddWordAttributesFilter
	(
		XMLReader reader ,
		ExtendedAdornedWordFilter wordInfoFilter
	)
	{
		super( reader );

		this.wordInfoFilter	= wordInfoFilter;

								//	Output non-redundant attributes
								//	only.

		this.outputNonredundantAttributesOnly	=
			MorphAdornerSettings.outputNonredundantAttributesOnly;

								//	Output non-redundant token attribute
								//	only.

		this.outputNonredundantTokenAttribute	=
			MorphAdornerSettings.outputNonredundantTokenAttribute;

								//	Save output whitespace option.

		this.outputWhitespace	=
			MorphAdornerSettings.outputWhitespaceElements;

		this.outputSentenceNumber	=
			MorphAdornerSettings.outputSentenceNumber;

		this.outputWordNumber		=
			MorphAdornerSettings.outputWordNumber;

		this.outputWordOrdinal		=
			MorphAdornerSettings.outputWordOrdinal;
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
								//	Get mutable attribute values.

		AttributesImpl newAtts	= new AttributesImpl( atts );

								//	Eject "TEIform=" attribute.

		String teiform	= newAtts.getValue( "TEIform" );

		if ( ( teiform != null ) && ( teiform.length() > 0 ) )
		{
			removeAttribute( newAtts, "TEIform" );
		}
								//	Update word element.

		if ( qName.equals( "w" ) )
		{
								//	Get word ID.

			String id	=
				newAtts.getValue( WordAttributeNames.id );

								//	Get adorned word info for this ID.

			ExtendedAdornedWord wordInfo	=
				wordInfoFilter.getExtendedAdornedWord( id );

								//	Get word attribute values.

			String tok		= wordInfo.getToken();
			String spe		= wordInfo.getSpelling();
			String pos		= wordInfo.getPartsOfSpeech();
			boolean eos		= wordInfo.getEOS();
			String lem		= wordInfo.getLemmata();
			String reg		= wordInfo.getStandardSpelling();
			int ord			= wordInfo.getOrd();
			String part		= wordInfo.getPart();
			String wordText	= wordInfo.getWordText();

								//	Set updated attribute values.

			setAttributeValue(
				newAtts ,
				WordAttributeNames.id ,
				id );

			setAttributeValue(
				newAtts ,
				WordAttributeNames.eos ,
				eos );

			setAttributeValue(
				newAtts ,
				WordAttributeNames.lem ,
				lem );

			if ( outputWordOrdinal )
			{
				setAttributeValue(
					newAtts ,
					WordAttributeNames.ord ,
					ord );
			}
			else
			{
				removeAttribute(
					newAtts ,
					WordAttributeNames.ord );
			}

			setAttributeValue(
				newAtts ,
				WordAttributeNames.part ,
				part );

			setAttributeValue(
				newAtts ,
				WordAttributeNames.pos ,
				pos );

			setAttributeValue(
				newAtts ,
				WordAttributeNames.reg ,
				reg );

			if ( outputSentenceNumber )
			{
				setAttributeValue(
					newAtts ,
					WordAttributeNames.sn ,
					wordInfo.getSentenceNumber() );
			}
			else
			{
				removeAttribute(
					newAtts ,
					WordAttributeNames.sn );
			}

			setAttributeValue(
				newAtts ,
				WordAttributeNames.spe ,
				spe );

			setAttributeValue(
				newAtts ,
				WordAttributeNames.tok ,
				tok );

			if ( outputWordNumber )
			{
				setAttributeValue(
					newAtts ,
					WordAttributeNames.wn ,
					wordInfo.getWordNumber() );
			}
			else
			{
				removeAttribute(
					newAtts ,
					WordAttributeNames.sn );
			}
								//	Remove redundant attributes
								//	if requested.

			if ( outputNonredundantAttributesOnly )
			{
				if ( !eos )
				{
					removeAttribute(
						newAtts ,
						WordAttributeNames.eos );
				}

				if ( spe.equals( tok ) )
				{
					removeAttribute(
						newAtts ,
						WordAttributeNames.spe );
				}

				if ( lem.equals( spe ) )
				{
					removeAttribute(
						newAtts ,
						WordAttributeNames.lem );
				}

				if ( pos.equals( spe ) )
				{
					removeAttribute(
						newAtts ,
						WordAttributeNames.pos );
				}

				if ( reg.equals( spe ) )
				{
					removeAttribute(
						newAtts ,
						WordAttributeNames.reg );
				}

				if ( ( part != null ) && part.equals( "N" ) )
				{
					removeAttribute(
						newAtts ,
						WordAttributeNames.part );
				}

				if ( tok.equals( wordText ) )
				{
					removeAttribute(
						newAtts ,
						WordAttributeNames.tok );
				}
			}
								//	If the word token is the same as
								//	the word text, and we are
								//	outputting abbreviated attributes,
								//	remove the redundant token
								//	attribute.

			else if ( outputNonredundantTokenAttribute )
			{
				if ( tok.equals( wordText ) )
				{
					removeAttribute(
						newAtts ,
						WordAttributeNames.tok );
				}
			}

			super.startElement( uri , localName , qName , newAtts );
		}
								//	Remove part attribute from <c> element.

		else if ( qName.equals( "c" ) )
		{
			removeAttribute(
				newAtts , WordAttributeNames.part );

			if ( outputWhitespace )
			{
				super.startElement( uri , localName , qName , newAtts );
			}
		}
								//	Pass through remaining elements.
		else
		{
			super.startElement( uri , localName , qName , newAtts );
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
		if ( qName.equals( "c" ) )
		{
			if ( outputWhitespace )
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



