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
import edu.northwestern.at.utils.servlets.*;

import net.sf.jlinkgrammar.*;

/**	Parses English sentences using a link grammar parser.
 */

public class ParserServlet extends BaseAdornerServlet
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
		boolean parse		= ( request.getParameter( "parse" ) != null );
		boolean fromForm	= clear || parse;

								//	Get servlet session.

		HttpSession session = request.getSession( true );

								//	See if we're just returning the
								//	output of the previous invocation.

		String parserResults	=
			(String)session.getAttribute( "parserresults" );

								//	If so, just pipe the output back
								//	to the client.

		if ( ( parserResults != null ) && !fromForm )
		{
			session.setAttribute( "parserresults" , null );

			result	=
				new ServletResult
				(
					false ,
					parserResults ,
					"Parser Example" ,
					"/morphadorner/parser/example/" ,
					"parserresults"
				);
		}
		else
		{
								//	Get string output writer.

			StringPrintWriter out	= new StringPrintWriter();

								//	See if we're parsing text.
								//	If not, clear the text and
								//	return an empty form.

			String text			= "";
			String parsedText	= "";

			if ( parse )
			{
								//	See if we have text to parse.

				text	= request.getParameter( "text" );

				if ( text == null )
				{
					text	= "";
				}
				else
				{
					text	= unTag( text );
				}
								//	Parse text.

				if ( text.length() > 0 )
				{
					Sentence sentence = parse( text ) ;

					if ( sentence.sentence_num_linkages_found() < 1 )
					{
						parsedText	= "No linkage was found." ;
					}
					else
					{
						Linkage link = getLinkage( sentence , 0 ) ;

						parsedText	= link.linkage_print_diagram() + "\n";

						parsedText	=
							parsedText +
								fixOutput
								(
									link.linkage_print_links_and_domains()
								);
					}
				}
			}
								//	Output form.

			outputForm( out , text );

								//	Output parsed text.

			outputParsedText( out , parsedText );

								//	Create results.
			result	=
				new ServletResult
				(
					fromForm ,
					out.getString() ,
					"Parser Example" ,
					"/morphadorner/parser/example/" ,
					"parserresults"
				);
		}

		return result;
	}

	/**	Output form.
	 *
	 *	@param	out				PrintWriter for servlet output.
	 *	@param	text			Text to parse .
	 */

	public void outputForm
	(
		java.io.PrintWriter out ,
		String text
	)
	{
		out.println( "<p>" );
		out.println( "Enter sentence to parse in the ");
		out.println( "input field below.<br />" );
		out.println( "</p>" );

		out.println(
			"<form method=\"post\" " +
			"action=\"/morphadorner/parser/example/Parser\"" +
			" name=\"parser\">" );

		out.println( "<table cellpadding=\"0\" cellspacing=\"5\">" );

		out.println( "<tr>" );
		out.print  ( "<td colspan=\"2\"><textarea name=\"text\" rows=\"10\"" +
			" cols=\"50\">" );

		out.print  ( text );
		out.println( "</textarea>" );

		out.println( "</td>" );
		out.println( "</tr>" );

		out.println( "<tr>" );
		out.println( "<td colspan=\"2\">" );

		out.println(
			"<input type=\"submit\" name=\"parse\" value=\"Parse\" />" );

		out.println(
			"<input type=\"submit\" name=\"clear\" value=\"Clear\" />" );

		out.println( "</td>" );
		out.println( "</tr>" );

		out.println( "</table>" );
		out.println( "</form>" );
	}

	/**	Output the parsed text.
	 *
	 *	@param	out				PrintWriter for servlet output.
	 *	@param	parsedText		Parsed text.
	 */

	public void outputParsedText
	(
		java.io.PrintWriter out ,
		String parsedText
	)
	{
		if ( parsedText == null ) return;

		out.println( "<hr noshade=\"noshade\" />" );
		out.println( "<table width=\"100%\">");

		out.println( "<tr>" );
		out.println( "<th>" );
		out.println( "Parsed Text" );
		out.println( "</th>" );
		out.println( "</tr>" );

		out.println( "<tr>" );
		out.println( "<td>" );
		out.println( "<pre>" );
		out.println( parsedText );
		out.println( "</pre>" );
		out.println( "</td>" );
		out.println( "</tr>" );

		out.println( "</table>");
	}

	public Linkage getLinkage( Sentence sentence , int index )
	{
		return new Linkage( index , sentence , parseOptions ) ;
	}

	public Sentence parse( String s )
	{
		Sentence sentence = new Sentence( s , dictionary , parseOptions ) ;

		sentence.sentence_parse( parseOptions ) ;

		return sentence ;
	}

	protected static String fixOutput( String s )
	{
		int[] colWidths	= new int[ 6 ];

		for ( int i =0 ; i < colWidths.length ; i++ )
		{
			colWidths[ i ]	= 0;
		}

		String[] lines	= s.split( "\n" );

		StringBuffer sb	= new StringBuffer();

		for ( int i = 0 ; i < lines.length ; i++ )
		{
			String line	= lines[ i ];

			if ( !line.equals( "\n" ) )
			{
				StringTokenizer tokenizer	=
					new StringTokenizer( line );

				int j	= 0;

				if ( line.charAt( 0 ) == ' ' )
				{
					j++;
				}

				while ( tokenizer.hasMoreTokens() )
				{
					String token	= tokenizer.nextToken().trim();

					colWidths[ j ]	=
						Math.max( colWidths[ j ] , token.length() );

					j++;
				}
			}
		}

		for ( int i = 0 ; i < colWidths.length ; i++ )
		{
			colWidths[ i ] += 2;
		}

		for ( int i = 0 ; i < lines.length ; i++ )
		{
			String line	= lines[ i ];

			if ( !line.equals( "\n" ) )
			{
				StringTokenizer tokenizer	=
					new StringTokenizer( line );

				int j	= 0;

				if ( line.charAt( 0 ) == ' ' )
				{
					sb.append( StringUtils.dupl( " " ,  colWidths[ j++ ] ) );
				}

				while ( tokenizer.hasMoreTokens() )
				{
					String token	= tokenizer.nextToken();

					sb.append( StringUtils.rpad( token , colWidths[ j++ ] ) );
				}

				sb.append( "\n" );
			}
		}

		return sb.toString();
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



