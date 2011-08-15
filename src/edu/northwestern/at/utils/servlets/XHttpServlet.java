package edu.northwestern.at.utils.servlets;

/*	Please see the license information at the end of this file. */

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

/**	Extends HttpServlet to map a get request to a post request.
 */

public class XHttpServlet extends HttpServlet
{
	/** Map servlet get to servlet post.
	 */

	protected void doGet
	(
		HttpServletRequest request,
		HttpServletResponse response
	)
		throws ServletException, java.io.IOException
	{
		doPost( request , response );
	}

	/**	Create redirect URL with title, success, and failure strings.
	 *
	 *	@param	response	Servlet response object.
	 *	@param	url			Base URL (JSP page or servlet name).
	 *	@param	title		Title text.
	 *	@param	success		Success message text.
	 *	@param	failure		Failure message text.
	 */

	public String createRedirectURL
	(
		HttpServletResponse response ,
		String url ,
		String title ,
		String success ,
		String failure
	)
	{
		StringBuffer sb = new StringBuffer();

		sb.append( url );
		boolean needsQuestion = true;

		if ( ( title != null ) && ( title.length() > 0 ) )
		{
			sb.append( "?" );
			sb.append( "title=" );
			sb.append( title );

			needsQuestion = false;
		}

		if ( ( success != null ) && ( success.length() > 0 ) )
		{
			if ( needsQuestion )
			{
				sb.append( "?" );
				needsQuestion = false;
			}
			else
			{
				sb.append( "&" );
			}

			sb.append( "success=" );
			sb.append( success );
		}

		if ( ( failure != null ) && ( failure.length() > 0 ) )
		{
			if ( needsQuestion )
			{
				sb.append( "?" );
				needsQuestion = false;
			}
			else
			{
				sb.append( "&" );
			}

			sb.append( "failure=" );
			sb.append( failure );
		}

		return response.encodeRedirectURL( sb.toString() );
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



