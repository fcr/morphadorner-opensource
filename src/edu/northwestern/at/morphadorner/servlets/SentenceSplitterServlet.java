package edu.northwestern.at.morphadorner.servlets;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.ServletConfig;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.html.*;
import edu.northwestern.at.utils.corpuslinguistics.lexicon.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.guesser.*;
import edu.northwestern.at.utils.corpuslinguistics.sentencemelder.*;
import edu.northwestern.at.utils.corpuslinguistics.sentencesplitter.*;
import edu.northwestern.at.utils.corpuslinguistics.tokenizer.*;
import edu.northwestern.at.utils.servlets.*;

/**	Splits text into sentences.
 */

public class SentenceSplitterServlet extends BaseAdornerServlet
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

								//	Determine if we got here from a form
								//	submission or an SSI include.

		boolean clear		= ( request.getParameter( "clear" ) != null );
		boolean split		= ( request.getParameter( "split" ) != null );
		boolean fromForm	= clear || split;
		String adornerName	=
			request.getParameter( "adornername" );

								//	Get which adorner to use.

		AdornerInfo adornerInfo	= getAdornerInfo( adornerName );

								//	Get servlet session.

		HttpSession session = request.getSession( true );

								//	See if we're just returning the
								//	output of the previous invocation.

		String splitterResults	=
			(String)session.getAttribute( "sentencesplitterresults" );

								//	If so, just pipe the output back
								//	to the client.

		if ( ( splitterResults != null ) && !fromForm )
		{
			session.setAttribute( "sentencesplitterresults" , null );

			result	=
				new ServletResult
				(
					false ,
					splitterResults ,
					"Sentence Splitter Example" ,
					"/morphadorner/sentencesplitter/example/" ,
					"sentencesplitterresults"
				);
		}
		else
		{
								//	Get string output writer.

			StringPrintWriter out	= new StringPrintWriter();

								//	See if we're splitting text.
								//	If not, clear the text and
								//	return an empty form.

			String text						= "";
			List<List<String>> sentences	= null;

			if ( split )
			{
								//	See if we have a text to split into
								//	sentences.

				text	= request.getParameter( "text" );

				if ( text == null )
				{
					text	= "";
				}
				else
				{
					text	= unTag( text );
				}
								//	Get sentences.

				if ( text.length() > 0 )
				{
					sentences	=
						adornerInfo.extractor.extractSentences( text );
				}
			}
								//	Output form.

			outputForm( out , text , adornerName );

								//	Output sentences.

			outputSentences( out , sentences );

								//	Create results.
			result	=
				new ServletResult
				(
					fromForm ,
					out.getString() ,
					"Sentence Splitter Example" ,
					"/morphadorner/sentencesplitter/example/" ,
					"sentencesplitterresults"
				);
		}

		return result;
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
		out.println( "Enter text to split into sentences in the ");
		out.println( "input field below.<br />" );
		out.println( "</p>" );

		out.println(
			"<form method=\"post\" " +
			"action=\"/morphadorner/sentencesplitter/example/SentenceSplitter\"" +
			" name=\"splitter\">" );

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

		out.println(
			"<input type=\"submit\" name=\"split\" value=\"Split\" />" );

		out.println(
			"<input type=\"submit\" name=\"clear\" value=\"Clear\" />" );

		out.println( "</td>" );
		out.println( "</tr>" );

		out.println( "</table>" );
		out.println( "</form>" );
	}

	/**	Output the split sentences.
	 *
	 *	@param	out			PrintWriter for servlet output.
	 *	@param	sentences	List of sentences.
	 */

	public void outputSentences
	(
		java.io.PrintWriter out ,
		List<List<String>> sentences
	)
	{
		if ( sentences == null ) return;

		out.println( "<hr noshade=\"noshade\" />" );

		switch ( sentences.size() )
		{
			case 0:
				out.println( "<h3>No sentences found.</h3>" );
				return;

			case 1:
				out.println( "<h3>1 sentence found.</h3>" );
				break;

			default:
				out.println( "<h3>" + sentences.size() +
					" sentences found.</h3>" );
				break;
		}

		out.println( "<table width=\"100%\">");

		out.println( "<tr>" );
		out.println( "<th class=\"width1pct\" align=\"left\">" );
		out.println( "S#" );
		out.println( "</th>" );
		out.println( "<th class=\"width99pct\" align=\"left\">" );
		out.println( "Sentence Text" );
		out.println( "</th>" );
		out.println( "</tr>" );

		SentenceMelder melder	= new SentenceMelder();

		for ( int i = 0 ; i < sentences.size() ; i++ )
		{
			List<String> sentence	= sentences.get( i );

			String sentenceText	=
				melder.reconstituteSentence( sentence );

			out.println( "<tr>" );
			out.println( "<td valign=\"top\" class=\"width1pct\">" );
			out.println( ( i + 1 ) + "" );
			out.println( "</td>" );

			out.println( "<td class=\"width99pct\">" );
			out.println( sentenceText );
			out.println( "</td>" );
			out.println( "</tr>" );
		}

		out.println( "</table>");
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



