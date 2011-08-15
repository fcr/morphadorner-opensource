package edu.northwestern.at.morphadorner.servlets;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.ServletConfig;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.languagerecognizer.*;
import edu.northwestern.at.utils.servlets.*;

/**	Determine language in which a text is written.
 */

public class LanguageRecognizerServlet extends BaseAdornerServlet
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

		boolean clear		= ( request.getParameter( "clear" ) != null );
		boolean recognize	= ( request.getParameter( "recognize" ) != null );
		boolean fromForm	= clear || recognize;

								//	See if we're just returning the
								//	output of the previous invocation.

		String recognizerResults	=
			(String)session.getAttribute( "languagerecognizerresults" );

								//	If so, just pipe the output back
								//	to the client.

		if ( ( recognizerResults != null ) && !fromForm )
		{
			session.setAttribute( "languagerecognizerresults" , null );

			result	=
				new ServletResult
				(
					false ,
					recognizerResults ,
					"Language Recognizer Example" ,
					"/morphadorner/languagerecognizer/example/" ,
					"languagerecognizerresults"
				);
		}
		else
		{						//	Get string output writer.

			StringPrintWriter out	= new StringPrintWriter();

								//	See if we have text for which to
								//	determine language.

			String text					= "";
			ScoredString[] languages	= null;

								//	Clear list of suggested languages.

			session.setAttribute( "languageSuggestions" , null );

			if ( recognize )
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
								//	Get languages.

				if ( text.length() > 0 )
				{
	    		    languages	=
	    		    	languageRecognizer.recognizeLanguage( text );
				}

				session.setAttribute(
						"languageSuggestions" , languages );
			}
								//	Output form.

    		outputForm( out , text );

								//	Output language(s) for the text.

			outputLanguages( out , languages );

								//	Create results.
			result	=
				new ServletResult
				(
					fromForm ,
					out.getString() ,
					"Language Recognizer Example" ,
					"/morphadorner/languagerecognizer/example/" ,
					"languagerecognizerresults"
				);
		}

		return result;
	}

	/**	Output form.
	 *
	 *	@param	out			PrintWriter for servlet output.
	 *	@param	text		Text to split into sentences.
	 */

	public void outputForm
	(
		java.io.PrintWriter out ,
		String text
	)
	{
		out.println( "<p>" );
		out.println( "Enter text for which to recognize the language in the ");
		out.println( "input field below.<br />" );
		out.println( "</p>" );

		out.println(
			"<form method=\"post\" action=\"/morphadorner/languagerecognizer/example/LanguageRecognizer\" name=\"recognizer\">" );

		out.println( "<table cellpadding=\"0\" cellspacing=\"5\">" );

		out.println( "<tr>" );
		out.print  ( "<td><textarea name=\"text\" rows=\"10\"" +
			" cols=\"50\">" );
		out.print  ( text );
		out.println( "</textarea>" );
		out.println( "</td>" );
		out.println( "</tr>" );

		out.println( "<tr>" );
		out.println( "<td>&nbsp;</td>" );
		out.println( "</tr>" );

		out.println( "<tr>" );
		out.println( "<td>" );
		out.println( "<input type=\"submit\" name=\"recognize\" value=\"Recognize\" />" );
		out.println( "<input type=\"submit\" name=\"clear\" value=\"Clear\" />" );
		out.println( "</td>" );
		out.println( "</tr>" );

		out.println( "</table>" );
		out.println( "</form>" );
	}

	/**	Output languages.
	 *
	 *	@param	out				PrintWriter for servlet output.
	 *	@param	languages		Scored strings containing possible languages.
	 */

	public void outputLanguages
	(
		java.io.PrintWriter out ,
		ScoredString[] languages
	)
	{
		if ( languages == null ) return;
		if ( languages.length < 1 ) return;

		out.println( "<hr shade=\"noshade\" />" );
		out.println( "<h3>" );

		switch ( languages.length )
		{
			case 0:
				out.println( "<h3>No language identified.</h3>" );
				return;

			case 1:
				out.println( "1 language identified.</h3>" );
				break;

			default:
				out.println( languages.length + " languages identified." );
				break;
		}

		out.println( "</h3>" );

		out.println( "<table cellpadding=\"2\">" );
		out.println( "<tr>" );
		out.println( "<th align=\"left\">Language</th>" );
		out.println( "<th align=\"left\">Score</th>" );
		out.println( "</tr>" );

		for ( int i = 0 ; i < languages.length ; i++ )
		{
			ScoredString scoredString	= languages[ i ];

			String language	= scoredString.getString() + "";
			String score	=
				Formatters.formatDouble( scoredString.getScore() , 4 );

			out.println( "<tr>" );
			out.println( "<td>" + language + "</td>" );
			out.println( "<td>" + score + "</td>" );
			out.println( "</tr>" );
		}

		out.println( "</table>" );
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



