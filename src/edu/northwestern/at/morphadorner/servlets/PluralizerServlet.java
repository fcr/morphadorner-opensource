package edu.northwestern.at.morphadorner.servlets;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.ServletConfig;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.inflector.*;
import edu.northwestern.at.utils.corpuslinguistics.spellingmapper.*;
import edu.northwestern.at.utils.servlets.*;

/**	Pluralizes a noun.
 */

public class PluralizerServlet extends BaseAdornerServlet
{
	/**	Servlet not ready message. */

	protected String notReadyMessage		=
		"Pluralizer still initializing, please try again later.";

	/**	Initialize the servlet.
	 *
	 *	@param	config	Servlet configuration.
	 *
	 *	@throws			ServletException
	 */

	public void init( ServletConfig config ) throws ServletException
	{
		super.init( config );
								//	Start main initialization.
		initialize( config );
	}

	/**	Handle request.
	 *
	 *	@param	request		Servlet request.
	 *	@param	response	Servlet response.
	 *
	 *	@return				Servlet results.
	 */

	protected ServletResult handleRequest
	(
		HttpServletRequest request ,
		HttpServletResponse response
	)
		throws ServletException, java.io.IOException
	{
		ServletResult result;
								//	Determine if we got here from a form
								//	submission or an SSI include.

		boolean clear		= ( request.getParameter( "clear" ) != null );
		boolean pluralize	= ( request.getParameter( "pluralize" ) != null );
		boolean fromForm	= clear || pluralize;

								//	Get servlet session.

		HttpSession session 	= request.getSession( true );

								//	See if we're just returning the
								//	output of the previous invocation.

		String pluralizerResults	=
			(String)session.getAttribute( "pluralizerresults" );

								//	If so, just pipe the output back
								//	to the client.

		if ( ( pluralizerResults != null ) && !fromForm )
		{
			session.setAttribute( "pluralizeresults" , null );

			result	=
				new ServletResult
				(
					false ,
					pluralizerResults ,
					"Noun Pluralizer Example" ,
					"/morphadorner/pluralizer/example" ,
					"pluralizerresults"
				);
		}
		else
		{						//	Get string output writer.

			StringPrintWriter out	= new StringPrintWriter();

								//	See if we have a noun to
								//	pluralize.

			String noun		= "";
			String plural	= "";

								//	See if we're to prefer American
								//	spellings.

			String american	= request.getParameter( "american" );

			if ( pluralize )
			{
				noun	= request.getParameter( "noun" );

				if ( noun == null )
				{
					noun	= "";
				}
				else
				{
					noun	= noun.trim();
				}
								//	Pluralize noun if we have one.

				if ( noun.length() > 0 )
				{
					plural	= inflector.pluralize( noun );
				}
			}
								//	Change to American spelling
								//	if requested.

			if ( american != null )
			{
				noun	= britishToUS.mapSpelling( noun );
				plural	= britishToUS.mapSpelling( plural );
			}
								//	Output form and results.
			outputForm
			(
				out ,
				noun ,
				plural ,
				american
			);
								//	Return results.
			result	=
				new ServletResult
				(
					fromForm ,
					out.getString() ,
					"Pluralizer Example" ,
					"/morphadorner/pluralizer/example/" ,
					"pluralizeresults"
				);
		}

   		return result;
	}

	/**	Output form.
	 */

	public void outputForm
	(
		java.io.PrintWriter out ,
		String noun ,
		String plural ,
		String american
	)
	{
		out.println( "<p>" );
/*
		out.println( "Enter a singular noun or pronoun in the input box below.");
		out.println( "Press <strong>Pluralize</strong> to see " );
		out.println( "the plural form of the noun or pronoun. " );
*/
		out.println( "Enter a singular noun in the input box below.");
		out.println( "Press <strong>Pluralize</strong> to see " );
		out.println( "the plural form of the noun. " );
		out.println( "Check <strong>American spellings</strong> to display " +
						"American instead of British spellings." );
		out.println( "</p>" );

		out.println(
			"<form method=\"post\" action=\"/morphadorner/pluralizer/example/Pluralizer\" name=\"pluralizer\">" );
		out.println( "<table cellpadding=\"0\" cellspacing=\"5\">" );

		out.println( "<tr>" );
		out.println( "<td><strong>Noun:</strong></td>" );
		out.println( "<td><input type=\"text\" name=\"noun\"" +
			"size = \"20\" value=\"" + noun + "\" /></td>" );
		out.println( "</tr>" );

		String checkedAmerican	=
			( ( american == null ) ? "" : "checked=\"checked\"" );

		out.println( "<tr>" );
		out.println( "<td>&nbsp;</td>" );

		out.println( "<td><input type=\"checkbox\" name=\"american\"" +
				"value=\"american\" " + checkedAmerican +
				"/>American spellings</td>" );

		out.println( "</tr>" );

		out.println( "<tr>" );
		out.println( "<td>" );

		outputSpacerRow( out , 2 );

		out.println( "<tr>" );
		out.println( "<td>" );
		out.println( "<input type=\"submit\" name=\"pluralize\" value=\"Pluralize\" />" );
		out.println( "<input type=\"submit\" name=\"clear\" value=\"Clear\" />" );
		out.println( "</td>" );
		out.println( "</tr>" );

		outputSpacerRow( out , 2 );

		if ( ( noun != null ) && ( noun.length() > 0 ) )
		{
			out.println( "<tr>" );
			out.println( "<td><strong>Plural:</strong></td>" );
			out.println( "<td><label type=\"text\" name=\"plural\"" +
				"size = \"20\">" + plural + "</label></td>" );
			out.println( "</tr>" );
/*
			out.println( "<tr>" );
			out.println( "<td><strong>Singular Possessive:</strong></td>" );
			out.println( "<td><label type=\"text\" name=\"singularpossessive\"" +
				"size = \"20\">" + singularPossessive( noun ) + "</label></td>" );
			out.println( "</tr>" );

			out.println( "<tr>" );
			out.println( "<td><strong>Plural Possessive:</strong></td>" );
			out.println( "<td><label type=\"text\" name=\"pluralpossessive\"" +
				"size = \"20\">" + pluralPossessive( plural ) + "</label></td>" );
			out.println( "</tr>" );
*/
		}

		out.println( "</table>" );
		out.println( "</form>" );
	}

	/**	Get possessive for a singular noun.
	 *
	 *	@param	singularNoun	A singular noun.
	 *
	 *	@return					The possessive form.
	 */

	protected static String singularPossessive( String singularNoun )
	{
		return singularNoun + "'s";
	}

	/**	Get possessive for a plural noun.
	 *
	 *	@param	pluralNoun	A plural noun.
	 *
	 *	@return				The possessive form.
	 */

	protected static String pluralPossessive( String pluralNoun )
	{
		String result;

		if ( pluralNoun.endsWith( "s" ) || pluralNoun.endsWith( "S" ) )
		{
			result	= pluralNoun + "'";
		}
		else
		{
			result	= pluralNoun + "'s";
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



