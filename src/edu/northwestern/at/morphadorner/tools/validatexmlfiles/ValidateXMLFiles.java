package edu.northwestern.at.morphadorner.tools.validatexmlfiles;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.net.*;
import java.util.*;

import javax.xml.XMLConstants;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.xml.*;

import org.w3c.dom.Document;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

/**	Validate XML files.
 *
 *	<p>
 *	Usage:
 *	</p>
 *
 *	<blockquote>
 *  <pre>
 *	java edu.northwestern.at.morphadorner.tools.validatexmlfiles.ValidateXMLFiles [schemaURI] input1.xml input2.xml ...
 *	</pre>
 *	</blockquote>
 *	<table>
 *	<tr>
 *	<td>schemaURI</td>
 *	<td>Optional URI for a Relax NG or W3C schema against which to validate subsequent files.
 *	The schemaURi is treated as a Relax NG schema if it ends in ".rng", and
 *	as a W3C schema if it ends in ".xsd".  The schema is ignored if it ends
 *	in anything else.
 *	</td>
 *	</tr>
 *	<tr>
 *	<td>input*.xml</td>
 *	<td>input XML files to validate.  At least one file must be specified.
 *	</td>
 *	</tr>
 *	</table>
 *
 *	<p>
 *	Checks that the specified XML files are valid XML.  For XML files
 *	referencing a DTD, checks that the XML is valid in the context of the
 *	DTD.  For XML files that do not specify a DTD, the XML is validated
 *	against the optional leading Relax NG or W3C schema.  If a schema
 *	file is not specified, and the XML document does not specify a DTD,
 *	the file will generally be reported as invalid.
 *	</p>
 *
 *	<p>
 *	Note: Creates a SAX parser for each document (one at a time).
 *	This allows even large adorned files to be validated.
 *	</p>
 */

public class ValidateXMLFiles
{
	/**	Holds compiled schema.  May be null if no schema provided. */

	protected static Schema schema	= null;

	/**	Schema validator.   May be null if no schema provided. */

	protected static Validator validator	= null;

	/**	Validation error handler. */

	protected static ValidationErrorHandler errorHandler	=
		new ValidationErrorHandler();

	/**	Main program.
	 *
	 *	@param	args	Program parameters.
	 */

	public static void main( String[] args )
	{
								//	Initialize.

		if ( !initialize( args ) )
		{
			System.exit( 1 );
		}
								//	Process all files.

		long startTime		= System.currentTimeMillis();

		int filesProcessed	= processFiles( args );

		long processingTime	=
			( System.currentTimeMillis() - startTime + 999 ) / 1000;

								//	Terminate.

		terminate( filesProcessed , processingTime );
	}

	/**	Initialize.
	 */

	protected static boolean initialize( String[] args )
	{
								//	Assume initialization fails.

		boolean result	= false;

								//	Get the file to check for non-standard
								//	spellings.

		if ( args.length < 1 )
		{
			System.err.println( "No files to validate provided." );
		}
        						//	If first argument is a schema URI,
        						//	compile it for subsequent use.
		else
		{
			String schemaURIString		= args[ 0 ];
			String lcSchemaURIString	= schemaURIString.toLowerCase();

			if	(	schemaURIString.endsWith( ".rng" ) ||
					schemaURIString.endsWith( ".xsd" )
				)
			{
				try
				{
					SchemaFactory schemaFactory	= null;

					if ( schemaURIString.endsWith( ".rng" ) )
					{
						schemaFactory =
							SchemaFactory.newInstance
							(
								XMLConstants.RELAXNG_NS_URI
							);
					}
					else if ( schemaURIString.endsWith( ".xsd" ) )
					{
						schemaFactory =
							SchemaFactory.newInstance
							(
								XMLConstants.W3C_XML_SCHEMA_NS_URI
							);
					}

					if ( schemaFactory != null )
					{
						schema =
							schemaFactory.newSchema
							(
								new StreamSource( schemaURIString  )
							);

						validator	= schema.newValidator();

						validator.setErrorHandler( errorHandler );

						System.out.println
						(
							"Schema " + schemaURIString + " processed."
						);
								//	Initialization OK if we also have
								//	at least one more file specification.

						result	= ( args.length > 1 );

						if ( !result )
						{
							System.out.println
							(
								"No files to validate provided."
							);
						}
					}
					else
					{
						result	= true;
					}
				}
				catch ( Exception e )
				{
					System.err.println( "Bad schema file: " );
					System.err.println( e.getMessage() );
				}
			}
		}

		return result;
	}

	/**	Process one file.
	 *
	 *	@param	xmlFileName		Input file name to check for Latin words.
	 */

	protected static void processOneFile( String xmlFileName )
	{
								//	Parse document via SAX and
								//	validate its XML.

		BufferedReader bufferedReader	= null;

		try
		{
			bufferedReader	=
				new BufferedReader
				(
					new UnicodeReader
					(
						new FileInputStream( xmlFileName ) ,
						"utf-8"
					)
				);

			InputSource inputSource	= new InputSource( bufferedReader );

            SAXSource source		= new SAXSource( inputSource );

								//	If a schema was provided, validate the
								//	document against the schema.

			if ( validator != null )
			{
								//	Reset error count in validator.

				errorHandler.resetErrorCount();

								//	Pick up list of errors.

				validator.validate( source );

								//	If no errors, the document is valid.

				if ( errorHandler.getErrorCount() > 0 )
				{
					System.out.println( xmlFileName + " failed validation." );
				}
				else
				{
					System.out.println( xmlFileName + " passed validation." );
				}
			}
			else
			{
				System.out.println( xmlFileName + " passed validation." );
			}
		}
		catch ( Exception e )
		{
			System.out.print  ( xmlFileName + " failed validation: " );
			System.out.println( e.getMessage() );
		}
		finally
		{
			if ( bufferedReader != null )
			{
				try
				{
					bufferedReader.close();
				}
				catch ( Exception e )
				{
				}
			}
		}
	}

	/**	Process files.
	 */

	protected static int processFiles( String[] args )
	{
		int result	= 0;
								//	Get file name/file wildcard specs.

		int schemaBias		= ( schema == null ? 0 : 1 );

		String[] wildCards	= new String[ args.length - schemaBias ];

		for ( int i = schemaBias ; i < args.length ; i++ )
		{
			wildCards[ i - schemaBias ]	= args[ i ];
		}
								//	Expand wildcards to list of
								//	file names,

		String[] fileNames	=
			FileNameUtils.expandFileNameWildcards( wildCards );

								//	Process each file.

		for ( int i = 0 ; i < fileNames.length ; i++ )
		{
			processOneFile( fileNames[ i ] );
		}

		return fileNames.length;
	}

	/**	Terminate.
	 *
	 *	@param	filesProcessed	Number of files processed.
	 *	@param	processingTime	Processing time in seconds.
	 */

	protected static void terminate
	(
		int filesProcessed ,
		long processingTime
	)
	{
		System.out.println
		(
			"Processed " +
			Formatters.formatIntegerWithCommas
			(
				filesProcessed
			) +
			StringUtils.pluralize
			(
				filesProcessed ,
				" file in " ,
				" files in "
			) +
			Formatters.formatLongWithCommas
			(
				processingTime
			) +
			StringUtils.pluralize
			(
				processingTime ,
				" second." ,
				" seconds."
			)
		);
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



