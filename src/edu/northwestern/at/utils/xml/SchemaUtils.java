package edu.northwestern.at.utils.xml;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import java.net.*;

import org.jdom.*;
import org.jdom.contrib.schema.*;

import edu.northwestern.at.utils.*;

/**	XML Schema utilities.
 */

public class SchemaUtils
{
	/**	Get a parsed schema given a schema URI.
	 *
	 *	@param	schemaURI	SchemaURI as a string.
	 *
	 *	@return				Parsed schema. Null if schema cannot be parsed.
	 *
	 *	@throws				IOException if schema URI cannot be read.
	 *	@throws				JDOMException if anything else goes wrong.
	 *
	 *	<p>
	 *	Schema URIs ending in ".rng" (any case) are assumed to point to
	 *	a Relax NG schema.
	 *	</p>
	 *
	 *	<p>
	 *	Schema URIs ending in ".xsd" (any case) are assumed to point to
	 *	a W3C schema.
	 *	</p>
	 *
	 *	<p>
	 *	Schema URIs ending in anything else are unrecognized.
	 *	</p>
	 */

	public static Schema parseSchema( String schemaURI )
		throws JDOMException , IOException
	{
		Schema result	= null;

		String lcSchemaURI	= schemaURI.toLowerCase();

		Schema.Type schemaType	= null;

		if ( schemaURI.endsWith( ".rng" ) )
		{
			schemaType	= Schema.RELAX_NG;
		}
		else if ( schemaURI.endsWith( ".xsd" ) )
		{
			schemaType	= Schema.W3C_XML_SCHEMA;
		};

		if ( schemaType != null )
		{
			result	= Schema.parse( schemaURI , schemaType );
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



