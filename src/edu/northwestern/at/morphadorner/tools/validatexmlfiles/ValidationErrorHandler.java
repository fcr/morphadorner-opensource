package edu.northwestern.at.morphadorner.tools.validatexmlfiles;

/*	Please see the license information at the end of this file. */

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**	Error handler class for a JAXP Schema validation.
 */

public class ValidationErrorHandler implements ErrorHandler
{
	/**	Counts validation errors. */

	protected int errorCount	= 0;

	/**	Construct error handler.
	 */

	public ValidationErrorHandler()
	{
	}

	/**	Reset the error count to zero.
	 */

	public void resetErrorCount()
	{
		errorCount	= 0;
	}

	/**	Get the validation error count.
	 *
	 *	@return		The validation error count.
	 */

	public int getErrorCount()
	{
		return errorCount;
	}

	/**	Callback from a JAXP validation process for a warning.
	 *
	 *	@param	e	SAX parser exception.
	 */

	public void warning( SAXParseException e ) throws SAXException
	{
		printError( e );
	}

	/**	Callback from a JAXP validation process for an error.
	 *
	 *	@param	e	SAX parser exception.
	 */

	public void error( SAXParseException e ) throws SAXException
	{
		printError( e );

		errorCount++;
	}

	/**	Callback from a JAXP validation process for a fatal error.
	 *
	 *	@param	e	SAX parser exception.
	 */

	public void fatalError( SAXParseException e ) throws SAXException
	{
		printError( e );

		errorCount++;

		throw e;
	}

	/**	Print error.
	 *
	 *	@param	e	SAX parser exception.
	 */

	protected void printError( SAXParseException e )
	{
								//	Get line and column of error.

		int lineNumber		= e.getLineNumber();
		int columnNumber	= e.getColumnNumber();

								//	Print line number if given.

		if ( lineNumber >= 0 )
		{
			System.out.print( "  Line " + lineNumber );
		}
                                //	Print column number if given.

		if ( columnNumber >= 0 )
		{
			if ( lineNumber >= 0 )
			{
				System.out.print( ", " );
			}

			System.out.print( "Column " + columnNumber );
		}

		if ( ( lineNumber >= 0 ) || ( columnNumber >= 0 ) )
		{
			System.out.print( ": " );
		}
								//	Print error message.

		System.out.println( e.getMessage() );
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



