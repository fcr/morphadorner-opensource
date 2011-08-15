package edu.northwestern.at.utils.xml;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.util.zip.*;
import java.io.*;
import java.net.*;

import org.jdom.*;
import org.jdom.contrib.schema.*;
import org.jdom.filter.*;
import org.jdom.input.*;
import org.jdom.output.*;
import org.jdom.transform.*;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.northwestern.at.utils.*;

/**	XML JDOM utilities.
 */

public class JDOMUtils
{
	/**	Parses an XML file.
	 *
	 *	@param	file		File.
	 *
	 *	@return				JDOM document.
	 *
	 *	@throws			Exception
	 */

	public static Document parse( File file )
		throws IOException, JDOMException, SAXException
	{
		SAXBuilder builder = new SAXBuilder();

		return builder.build( file );
	}

	/**	Parses an XML file.
	 *
	 *	@param	path		File path.
	 *
	 *	@return					JDOM document.
	 *
	 *	@throws	Exception
	 */

	public static Document parse( String path )
		throws IOException, JDOMException, SAXException
	{
		return parse( new File( path ) );
	}

	/**	Parses XML document from URL.
	 *
	 *	@param	url		URL.
	 *
	 *	@return				JDOM document.
	 *
	 *	@throws	Exception
	 */

	public static Document parse( URL url )
		throws IOException, JDOMException, SAXException
	{
		return new SAXBuilder().build( url.openStream() );
	}

	/**	Parses XML document from a string.
	 *
	 *	@param	text	Document text string.
	 *
	 *	@return			JDOM document.
	 *
	 *	@throws	Exception
	 */

	public static Document parseText( String text )
		throws IOException, JDOMException, SAXException
	{
		return
			new SAXBuilder().build( new InputSource( new StringReader( text ) ) );
	}

	/**	Get attribute.
	 *
	 *	@param	element			The JDOM element.
	 *	@param	attributeName	The attribute name whose value we want.
	 *	@param	ignoreCase		true to ignore attribute name case.
	 *
	 *	@return					The attribute or null if not found.
	 */

	 public static Attribute getAttribute
	 (
	 	Element element ,
	 	String attributeName ,
	 	boolean ignoreCase
	 )
	 {
								//	Null returned by default if
								//	attribute not found.

	 	Attribute result	= null;

		if ( element != null )
		{
								//	Get attributes for element.

		 	List attributeList	= element.getAttributes();

    							//	Lowercase name of attribute to find.

		 	String name			= attributeName;

		 	if ( ignoreCase )
		 	{
		 		name	= name.toLowerCase();
		 	}
    							//	See if we can find lowercase attribute
    							//	name in list of attributes for
    							//	the element.

		 	for ( int i = 0 ; i < attributeList.size() ; i++ )
		 	{
		 		Attribute attribute	= (Attribute)attributeList.get( i );

				String attName		= attribute.getQualifiedName();

				if ( ignoreCase )
				{
					attName	= attName.toLowerCase();
				}

	 			if ( attName.equals( name ) )
	 			{
								//	Found matching attribute name.

					result	= attribute;
					break;
	 			}
	 		}
	 	}
                                //	Return attribute.
	 	return result;
	 }

	/**	Get attribute value.
	 *
	 *	@param	element			The JDOM element.
	 *	@param	attributeName	The attribute name whose value we want.
	 *	@param	ignoreCase		true to ignore attribute name case.
	 *
	 *	@return					The attribute value or null if not found.
	 */

	 public static String getAttributeValue
	 (
	 	Element element ,
	 	String attributeName ,
	 	boolean ignoreCase
	 )
	 {
	 	String result		= null;

		Attribute attribute	=
			getAttribute
			(
				element ,
				attributeName ,
				ignoreCase
			);

		if ( attribute != null )
		{
			result	= attribute.getValue();
		}

		return result;
	 }

	/**	Get attribute value ignoring case.
	 *
	 *	@param	element			The JDOM element.
	 *	@param	attributeName	The attribute name whose value we want.
	 *
	 *	@return					The attribute value or null if not found.
	 */

	 public static String getAttributeValueIgnoreCase
	 (
	 	Element element ,
	 	String attributeName
	 )
	 {
		return getAttributeValue( element , attributeName , true );
	 }

	/**	Get mapped attributes for an element.
	 *
	 *	@param	element		The JDOM element whose attributes are desired.
	 *
	 *	@return				Map with attribute name as key and
	 *						attribute value as value.
	 */

	public static Map<String,String> getAttributeValues( Element element )
	{
		Map<String, String> result	= MapFactory.createNewMap();

		if ( element != null )
		{
								//	Get attributes for element.

		 	List attributeList	= element.getAttributes();

								//	Store each attribute name and its value
								//	in result map.

		 	for ( int i = 0 ; i < attributeList.size() ; i++ )
		 	{
		 		Attribute attribute	= (Attribute)attributeList.get( i );

				result.put
				(
					attribute.getQualifiedName() ,
					attribute.getValue()
				);
	 		}
	 	}
                                //	Return attribute values.
		return result;
	}

	/**	Set attribute value.
	 *
	 *	@param	element			The JDOM element.
	 *	@param	attributeName	The attribute name whose value should be set.
	 *	@param	attributeValue	The attribute value.
	 */

	 public static void setAttributeValue
	 (
	 	Element element ,
	 	String attributeName ,
	 	String attributeValue
	 )
	 {
		Attribute attribute	=
			getAttribute( element , attributeName , false );

		if ( attribute != null )
		{
			attribute.setValue( attributeValue );
		}
		else
		{
			if ( attributeName.indexOf( ":" ) > 0 )
			{
				String[] name	= attributeName.split( ":" );

				attribute	=
					new Attribute
					(
						name[ 1 ] ,
						attributeValue ,
						Namespace.XML_NAMESPACE
					);
			}
			else
			{
				attribute	=
					new Attribute( attributeName , attributeValue );
			}

			element.setAttribute( attribute );
		}
	 }

	/**	Process elements selected by an element filter.
	 *
	 *	@param	document	The document to which to apply the filter.
	 *	@param	filter		The filter.
	 *	@param	processor	The element processor.
	 */

	public static void applyElementFilter
	(
		Document document ,
		Filter filter ,
		ElementProcessor processor
	)
	{
								//	Get list of elements using filter.

		List<Element> elements	= ListFactory.createNewList();

		@SuppressWarnings("unchecked")
		Iterator<Element> iterator	=
			(Iterator<Element>)document.getRootElement().getDescendants( filter );

		while ( iterator.hasNext() )
		{
			Element element	= iterator.next();
			elements.add( element );
		}
								//	Process each selected element.

		for ( int i = 0 ; i < elements.size() ; i++ )
		{
			processor.processElement( document , elements.get( i ) );
		}
	}

	/**	Saves a JDOM document to an XML file in utf-8.
	 *
	 *	@param	document	JDOM document.
	 *	@param	path		Output file path.
	 *	@param	format		The JDOM output format.
	 *
	 *	@throws	FileNotFoundException, IOException
	 */

	public static void save
	(
		Document document ,
		String path ,
		org.jdom.output.Format format
	)
		throws FileNotFoundException , IOException
	{
		XMLOutputter xmlOut	= new XMLOutputter( format );

		FileOutputStream outputStream	=
			new FileOutputStream( new File( path ) , false );

		BufferedOutputStream bufferedStream	=
			new BufferedOutputStream( outputStream );

		OutputStreamWriter writer			=
			new OutputStreamWriter( bufferedStream , "utf-8" );

		xmlOut.output( document , writer );

		writer.close();
	}

	/**	Saves a JDOM document to an XML file in utf-8.
	 *
	 *	@param	document	JDOM document.
	 *	@param	path		Output file path.
	 *
	 *	@throws	IOException
	 *
	 *	<p>
	 *	The document is "pretty printed."
	 *	</p>
	 */

	public static void savePretty( Document document , String path )
		throws FileNotFoundException , IOException
	{
		save( document , path , org.jdom.output.Format.getPrettyFormat() );
	}

	/**	Saves a JDOM document to an XML file in utf-8.
	 *
	 *	@param	document	JDOM document.
	 *	@param	path		Output file path.
	 *
	 *	@throws	IOException
	 *
	 *	<p>
	 *	The document is output raw, without "pretty printing."
	 *	</p>
	 */

	public static void saveRaw( Document document , String path )
		throws FileNotFoundException , IOException
	{
		save( document , path , org.jdom.output.Format.getRawFormat() );
	}

	/**	Saves a JDOM document to a compressed XML file in utf-8.
	 *
	 *	@param	document	JDOM document.
	 *	@param	path		Output file path.
	 *
	 *	@throws	IOException
	 *
	 *	<p>
	 *	The document is output raw, without "pretty printing."
	 *	</p>
	 */

	public static void saveRawCompressed( Document document , String path )
		throws FileNotFoundException , IOException
	{
		XMLOutputter xmlOut	=
			new XMLOutputter( org.jdom.output.Format.getRawFormat() );

		FileOutputStream outputStream	=
			new FileOutputStream( new File( path ) , false );

		BufferedOutputStream bufferedStream	=
			new BufferedOutputStream
			(
				new GZIPOutputStream( outputStream )
			);

		OutputStreamWriter writer			=
			new OutputStreamWriter( bufferedStream , "utf-8" );

		xmlOut.output( document , writer );

		writer.close();
	}

	/**	Validate a JDOM document against a schema.
	 *
	 *	@param	document	The parsed JDOM document to validate.
	 *	@param	schemaURI	The schema URI.
	 *
	 *	@return				List of validation errors.   Null if no errors.
	 *
	 *	@throws				IOException if the schema URI cannot be read.
	 *	@throws				JDOMException if the schema is invalid or
	 *						validation fails.
	 */

	public static List<ValidationError> validateDocument
	(
		Document document ,
		String schemaURI
	)
		throws JDOMException, IOException
	{
		List<ValidationError> result	= null;

								//	No schema?  Assume validation
								//	succeeds.

		if ( ( schemaURI != null ) && ( schemaURI.length() > 0 ) )
		{
								//	Compile the schema.

			Schema schema	= SchemaUtils.parseSchema( schemaURI );

			result	= validateDocument( document , schema );
		}

		return result;
	}

	/**	Validate a JDOM document against a schema.
	 *
	 *	@param	document	The parsed JDOM document to validate.
	 *	@param	schema		A parsed schema.
	 *
	 *	@return				List of validation errors.   Null if no errors.
	 *
	 *	@throws				IOException if the schema URI cannot be read.
	 *	@throws				JDOMException if the schema is invalid or
	 *						validation fails.
	 *
	 *	<p>
	 *	Simply returns without error if validation succeeds.
	 *	</p>
	 */

	public static List<ValidationError> validateDocument
	(
		Document document ,
		Schema schema
	)
		throws JDOMException, IOException
	{
								//	Check the document against the
								//	schema.

		return schema.validate( document );
	}

	/**	Allow overrides but not instantiation.
	 */

	protected JDOMUtils()
	{
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



