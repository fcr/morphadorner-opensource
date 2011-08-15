package edu.northwestern.at.morphadorner.servlets;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.ServletConfig;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.lexicon.*;
import edu.northwestern.at.utils.servlets.*;

/**	Display lexicon entry data for a spelling.
 */

public class LexiconLookupServlet extends BaseAdornerServlet
{
	/**	Servlet not ready message. */

	protected String notReadyMessage		=
		"Lexicon lookup still initializing, please try again later.";

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
		boolean lookup		= ( request.getParameter( "lookup" ) != null );
		boolean fromForm	= clear || lookup;
		String adornerName	= request.getParameter( "adornername" );

								//	Get which adorner to use.

		AdornerInfo adornerInfo	= getAdornerInfo( adornerName );

								//	Get servlet session.

		HttpSession session 	= request.getSession( true );

								//	See if we're just returning the
								//	output of the previous invocation.

		String lexiconLookupResults	=
			(String)session.getAttribute( "lexiconlookupresults" );

								//	If so, just pipe the output back
								//	to the client.

		if ( ( lexiconLookupResults != null ) && !fromForm )
		{
			session.setAttribute( "lemmatizerresults" , null );

			result	=
				new ServletResult
				(
					false ,
					lexiconLookupResults ,
					"Lexicon Lookup Example" ,
					"/morphadorner/lexiconlookup/example/" ,
					"lexiconlookupresults"
				);
		}
		else
		{						//	Get string output writer.

			StringPrintWriter out	= new StringPrintWriter();

								//	See if we have a spelling to
								//	lookup.

			String spelling				= "";
			LexiconEntry lexiconEntry	= null;

			if ( lookup )
			{
				spelling	= request.getParameter( "spelling" );

				if ( spelling == null )
				{
					spelling	= "";
				}
				else
				{
					spelling	= spelling.trim();
				}
								//	Get lexicon data if we have a spelling.

				if ( spelling.length() > 0 )
				{
					Lexicon lexicon	= adornerInfo.wordLexicon;

					lexiconEntry	= lexicon.getLexiconEntry( spelling );
				}
			}
								//	Output form.
			outputForm
			(
				out ,
				spelling ,
				adornerName
			);
								//	Output lexicon data.

			outputLexiconEntry
			(
				out ,
				spelling ,
				adornerName ,
				lexiconEntry ,
				adornerInfo.lemmaToSpellings
			);

								//	Create results.
			result	=
				new ServletResult
				(
					fromForm ,
					out.getString() ,
					"Lexicon Lookup Example" ,
					"/morphadorner/lexiconlookup/example/" ,
					"lexiconlookupresults"
				);
		}

   		return result;
	}

	/**	Output form.
	 */

	public void outputForm
	(
		java.io.PrintWriter out ,
		String spelling ,
		String adornerName
	)
	{
		out.println( "<p>" );
		out.println( "Enter a spelling in the input box below. " );
		out.println( "Press <strong>Lookup</strong> to see " );
		out.println( "the associated information in the lexicon. " );
		out.println( "</p>" );

		out.println(
			"<form method=\"post\" action=\"/morphadorner/lexiconlookup/example/LexiconLookup\" name=\"lookup\">" );
		out.println( "<table cellpadding=\"0\" cellspacing=\"5\">" );

		out.println( "<tr>" );
		out.println( "<td><strong>Spelling:</strong></td>" );
		out.println( "<td><input type=\"text\" name=\"spelling\"" +
			"size = \"20\" value=\"" + spelling + "\" /></td>" );
		out.println( "</tr>" );

		out.println( "</td>" );
		out.println( "<td>&nbsp;</td>" );
		out.println( "</tr>" );

		outputAdornerSelection( out , "Lexicon:" , adornerName );

		outputSpacerRow( out , 2 );

		out.println( "<tr>" );
		out.println( "<td>" );
		out.println( "<input type=\"submit\" name=\"lookup\" value=\"Lookup\" />" );
		out.println( "<input type=\"submit\" name=\"clear\" value=\"Clear\" />" );
		out.println( "</td>" );
		out.println( "</tr>" );

		out.println( "</table>" );
		out.println( "</form>" );
	}

	/**	Output lexicon entry data.
	 */

	public void outputLexiconEntry
	(
		java.io.PrintWriter out ,
		String spelling ,
		String corpusName ,
		LexiconEntry lexiconEntry ,
		KeyedSets<String, String> lemmaToSpellings
	)
	{
		if ( ( lexiconEntry == null ) && ( spelling.length() == 0 ) )
		{
			return;
		}

		out.println( "<hr noshade=\"noshade\" />" );

		if ( lexiconEntry == null )
		{
			out.println( "<h3><em>" + spelling + "</em> does not appear in " +
				"the " + corpusName + " corpus.</h3>" );

			return;
		}
		else
		{
			out.println( "<h3><em>" + spelling +
				"</em> appears " +
				Formatters.formatIntegerWithCommas( lexiconEntry.entryCount ) +
				" time" + ( lexiconEntry.entryCount == 1 ? "" : "s" ) +
				" in the " +
				corpusName + " corpus.</h3>" );
		}

    	out.println( "<table width=\"100%\">");

		out.println( "<tr>" );
		out.println( "<th align=\"left\" class=\"width1pct\">" );
		out.println( "Part of Speech" );
		out.println( "</th>" );
		out.println( "<th align=\"left\" class=\"width1pct\">" );
		out.println( "Lemma" );
		out.println( "</th>" );
		out.println( "<th align=\"left\" class=\"width1pct\">" );
		out.println( "Count" );
		out.println( "</th>" );
		out.println( "</tr>" );

		Iterator<String> iterator	=
			lexiconEntry.categoriesAndCounts.keySet().iterator();

		Set<String> lemmata	= new TreeSet<String>();

		while ( iterator.hasNext() )
		{
			String posTag	= iterator.next();

			String lemma	= lexiconEntry.lemmata.get( posTag );

			lemmata.add( lemma );

			int count		=
				lexiconEntry.categoriesAndCounts.get( posTag ).intValue();

			out.println( "<tr>" );

			out.println( "<td valign=\"top\" class=\"width1pct\">" );
			out.println( posTag );
			out.println( "</td>" );

			out.println( "<td valign=\"top\" class=\"width1pct\">" );
			out.println( lemma );
			out.println( "</td>" );

			out.println( "<td valign=\"top\" class=\"width1pct\">" );
			out.println
			(
				Formatters.formatIntegerWithCommas( count )
			);
			out.println( "</td>" );

			out.println( "</tr>" );
		}

    	out.println( "</table>");

    	iterator	= lemmata.iterator();

    	while ( iterator.hasNext() )
    	{
			String theLemma	= iterator.next();

    		Set<String> spellingsSet	=
    			lemmaToSpellings.get( theLemma );

    		spellingsSet.remove( spelling );

			if ( spellingsSet.size() > 0 )
			{
				out.println
				(
					"<h4>Other spellings taking " + theLemma +
					" as the lemma.</h4>"
				);

				StringBuffer sb	= new StringBuffer();

				String[] spellings	=
					(String[])spellingsSet.toArray(
						new String[ spellingsSet.size() ] );

				sb.append( spellings[ 0 ] );

				for ( int i = 1 ; i < spellings.length ; i++ )
				{
					sb.append( ", " );
					sb.append( spellings[ i ] );
				}

				out.println( "<blockquote>" );
				out.println( sb );
				out.println( "</blockquote>" );
			}
    	}
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



