package edu.northwestern.at.utils.servlets;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import java.net.*;

import edu.northwestern.at.utils.*;

/**	A simple HTML template manager for servlets.
 */

public class HtmlTemplate
{
	/**	Text of template. */

	protected TextFile template	= null;

	/**	Create HtmlTemplate with specified template file name.
	 *
	 *	@param	templateFileName	HTML template file name.
	 *	@param	encoding			Character encoding for template.
	 */

	public HtmlTemplate( String templateFileName , String encoding )
	{
		template	= new TextFile( templateFileName , encoding );
	}

	/**	Create HtmlTemplate with specified template URL.
	 *
	 *	@param	templateURL		HTML template URL.
	 *	@param	encoding		Character encoding for template.
	 */

	public HtmlTemplate( URL templateURL , String encoding )
	{
		template	= new TextFile( templateURL , encoding );
	}

	/**	Substitute values in template.
	 *
	 *	@param	writer				Output writer.
	 *
	 *	@param	templateProperties	Properties with values to substitute
	 *								into template.
	 *
	 *	@throws		IOException		If output cannot be written.
	 */

	public synchronized void outputTemplate
	(
		PrintWriter writer ,
		UTF8Properties templateProperties
	)
		throws IOException
	{
								//	Do nothing if template text not
								//	loaded.

		if ( !template.textLoaded() ) return;

								//	Get string array of template lines.

		String[] templateLines	= template.toArray();

								//	Loop over template lines and
								//	substitute template properties.

		for ( int i = 0 ; i < templateLines.length ; i++ )
		{
								//	Get next template line.

			String templateLine	= templateLines[ i ];

								//	Substitute template properties
								//	values if any found in this line.

			if ( templateProperties != null )
			{
								//	Get all property names.

				List properties	=
					ListFactory.createNewList
					(
						templateProperties.getAllStrings()
					);

				int propertyIndex		= -1;
				String propertyName		= "";
				String propertyValue	= "";

								//	Loop over property names.

				for ( int j = 0 ; j < properties.size() ; j++ )
				{
								//	Get next property name.

					propertyName	= (String)properties.get( j );

								//	Get next property value.

					propertyValue	=
						templateProperties.getProperty( propertyName );

								//	See if property occurs in
								//	current template line.

					propertyIndex	=
						templateLine.indexOf( propertyName );

								//	If property does occur in current
								//	template line ...

					while ( propertyIndex >= 0 )
					{
								//	Substitute property value for
								//	property name in template text.
						try
						{
							templateLine	=
								templateLine.substring( 0 , propertyIndex ) +
								propertyValue  +
								templateLine.substring(
									propertyIndex + propertyName.length() );

								//	Keep substuting if property occurs
								//	more than once in template line.

							propertyIndex	=
								templateLine.indexOf( propertyName );
						}
						catch( Exception e)
						{
							propertyIndex	= -1;
						}
					}
				}
            }
								//	Output template line after
								//	properties substituted.

            writer.println( templateLine );
        }
								//	Flush output.
		writer.flush();
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



