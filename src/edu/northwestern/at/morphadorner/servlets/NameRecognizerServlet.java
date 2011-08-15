package edu.northwestern.at.morphadorner.servlets;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.ServletConfig;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.adornedword.*;
import edu.northwestern.at.utils.corpuslinguistics.namerecognizer.*;
import edu.northwestern.at.utils.servlets.*;

/**	Extract names and places from text.
 */

public class NameRecognizerServlet extends BaseAdornerServlet
{
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

	/**	Handle servlet request.
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
		ServletResult result	= null;

								//	Get servlet session.

		HttpSession session = request.getSession( true );

								//	Determine if we got here from a form
								//	submission or an SSI include.

		boolean clear			= ( request.getParameter( "clear" ) != null );
		boolean extract			= ( request.getParameter( "extract" ) != null );
		boolean fromForm		= clear || extract;
		String adornerName	=
			request.getParameter( "adornername" );

								//	Get which adorner to use.

		AdornerInfo adornerInfo	= getAdornerInfo( adornerName );

								//	See if we're just returning the
								//	output of the previous invocation.

		String recognizerResults	=
			(String)session.getAttribute( "namerecognizerresults" );

								//	If so, just pipe the output back
								//	to the client.

		if ( ( recognizerResults != null ) && !fromForm )
		{
			session.setAttribute( "namerecognizerresults" , null );

			result	=
				new ServletResult
				(
					false ,
					recognizerResults ,
					"Name Recognizer Example" ,
					"/morphadorner/namerecognizer/example/" ,
					"namerecognizerresults"
				);
		}
		else
		{						//	Get string output writer.

			StringPrintWriter out	= new StringPrintWriter();

								//	See if we have text from which to
								//	extract names.

			String text					= "";
			Set<String>[] namesInText	= null;


			if ( extract )
			{
				text	= request.getParameter( "text" );

				if ( text == null )
				{
					text	= "";
				}
				else
				{
					text	= unTag( text );
				}
								//	Get names.

				if ( text.length() > 0 )
				{
					namesInText	=
						extractNames( out , adornerInfo , text );
				}
			}
								//	Output form.

			outputForm( out , text , adornerName );

								//	Output names in the text.

			outputNames( out , namesInText );

								//	Create results.
			result	=
				new ServletResult
				(
					fromForm ,
					out.getString() ,
					"Name Recognizer Example" ,
					"/morphadorner/namerecognizer/example/" ,
					"namerecognizerresults"
				);
		}

		return result;
	}

	/**	Extract names from text.
	 *
	 *	@param	adornerInfo		Adorner information.
	 *	@param	text			Text from which to extract names.
	 *
	 *	@return					Name sets.
	 */

	protected Set<String>[] extractNames
	(
		java.io.PrintWriter out ,
		AdornerInfo adornerInfo ,
		String text
	)
	{
		return adornerInfo.nameRecognizer.findNames( text );
	}

	/**	Output form.
	 *
	 *	@param	out				PrintWriter for servlet output.
	 *	@param	text			Text to split into sentences.
	 *	@param	adornerName		Adorner name.
	 */

	public void outputForm
	(
		java.io.PrintWriter out ,
		String text ,
		String adornerName
	)
	{
		out.println( "<p>" );
		out.println( "Enter text from which to extract names in the ");
		out.println( "input field below.<br />" );
		out.println( "</p>" );

		out.println(
			"<form method=\"post\" action=\"/morphadorner/namerecognizer/example/NameRecognizer\" name=\"recognizer\">" );

		out.println( "<table cellpadding=\"0\" cellspacing=\"5\">" );

		out.println( "<tr>" );
		out.print  ( "<td colspan=\"2\"><textarea name=\"text\" rows=\"10\"" +
			" cols=\"50\">" );
		out.print  ( text );
		out.println( "</textarea>" );
		out.println( "</td>" );
		out.println( "</tr>" );

		outputAdornerSelection( out , "Lexicon:" , adornerName );

		outputSpacerRow( out , 2 );

		out.println( "<tr>" );
		out.println( "<td colspan=\"2\">" );
		out.println( "<input type=\"submit\" name=\"extract\" " +
			"value=\"Extract Names\" />" );
		out.println( "<input type=\"submit\" name=\"clear\" " +
			"value=\"Clear\" />" );
		out.println( "</td>" );
		out.println( "</tr>" );

		out.println( "</table>" );
		out.println( "</form>" );
	}

	/**	Output the names.
	 *
	 *	@param	out			PrintWriter for servlet output.
	 *	@param	nameSet		Set of names.
	 *	@param	nameType	String describing type of names in nameSet.
	 */

	public void outputNameSet
	(
		java.io.PrintWriter out ,
		Set<String> nameSet ,
		String nameType
	)
	{
		if ( nameSet == null ) return;

		out.println( "<hr noshade=\"noshade\" />" );

		switch ( nameSet.size() )
		{
			case 0:
				out.println( "<h3>No " + nameType + "s found.</h3>" );
				return;

			case 1:
				out.println( "<h3>1 " + nameType + " found.</h3>" );
				break;

			default:
				out.println( "<h3>" + nameSet.size() + " " + nameType +
					"s found.</h3>" );
				break;
		}

		out.println( "<table width=\"100%\">");

		String[] names	=
			(String[])nameSet.toArray( new String[ nameSet.size() ] );

		Arrays.sort( names );

		for ( int i = 0 ; i < names.length ; i++ )
		{
			out.println( "<tr>" );
			out.println( "<td valign=\"top\" class=\"width1pct\">" );
			out.println( ( i + 1 ) + "" );
			out.println( "</td>" );

			out.println( "<td class=\"width99pct\">" );
			out.println( names[ i ] );
			out.println( "</td>" );
			out.println( "</tr>" );
		}

    	out.println( "</table>");
	}

	/**	Output the names extracted from the text.
	 *
	 *	@param	out			PrintWriter for servlet output.
	 *	@param	nameSets	Set array with two entries.
	 *						names[ 0 ] = set of proper names.
	 *						names[ 1 ] = set of place names.
	 */

	public void outputNames( PrintWriter out , Set<String>[] nameSets )
	{
		if ( nameSets == null ) return;

		outputNameSet( out , nameSets[ 0 ] , "proper name" );
		outputNameSet( out , nameSets[ 1 ] , "place name" );
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



