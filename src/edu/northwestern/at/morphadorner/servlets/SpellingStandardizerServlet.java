package edu.northwestern.at.morphadorner.servlets;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.ServletConfig;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.spellingstandardizer.*;
import edu.northwestern.at.utils.servlets.*;

/**	Finds standard spelling for variant spellings.
 */

public class SpellingStandardizerServlet extends BaseAdornerServlet
{
	/**	Initialize the servlet.
	 *
	 *	@param	config	Servlet configuration.
	 *
	 *	@throws			ServletException
	 */

	public void init( ServletConfig config ) throws ServletException
	{
								//	Start main initialization.
		initialize( config );
	}

	/**	Handle servlet post requests.
	 *
	 *	@param	request		Servlet request.
	 *	@param	response	Servlet response.
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

		boolean clear			=
			( request.getParameter( "clear" ) != null );

		boolean suggest			=
			( request.getParameter( "suggest" ) != null );

		boolean extendedSearch	=
			( request.getParameter( "extendedsearch" ) != null );

		boolean fromForm	= clear || suggest;

		String adornerName	=
			request.getParameter( "adornername" );

								//	Get which adorner to use.

		AdornerInfo adornerInfo	= getAdornerInfo( adornerName );

								//	Get servlet session.

		HttpSession session = request.getSession( true );

								//	Get string output writer.

		StringPrintWriter out	= new StringPrintWriter();

								//	See if we're just returning the
								//	output of the previous invocation.

		String standardizerResults	=
			(String)session.getAttribute( "spellingstandardizerresults" );

								//	If so, just pipe the output back
								//	to the client.

		if ( ( standardizerResults != null ) && !fromForm )
		{
			session.setAttribute( "spellingstandardizerresults" , null );

			result	=
				new ServletResult
				(
					false ,
					standardizerResults ,
					"Spelling Standardizer Example" ,
					"/morphadorner/spellingstandardizer/example/" ,
					"spellingstandardizerresults"
				);
		}
		else
		{
								//	See if we're standardizing a word.
								//	If not, return an empty form.

			String spelling				= "";
			String suggestion			= "";
			SortedArrayList<ScoredString> suggestions	= null;

								//	Clear list of suggestions.

			session.setAttribute( "spellingSuggestions" , null );

								//	See if we have a spelling to
								//	standardize.
			if ( suggest )
			{
				spelling		= request.getParameter( "spelling" );

				if ( spelling == null )
				{
					spelling	= "";
				}
				else
				{
					spelling	= spelling.trim();
				}
								//	If we have a spelling, get
								//	list of suggested spellings.

				if ( spelling.length() > 0 )
				{
					if ( extendedSearch )
					{
						suggestions	=
							new SortedArrayList<ScoredString>
							(
								adornerInfo.standardizer.
									getScoredSuggestedSpellings
									(
										EnglishDecruftifier.decruftify
										(
											spelling
										)
									)
							);

						suggestion	=
							suggestions.get(
								suggestions.size() - 1 ).getString();

						if ( suggestion.equals( "?" ) )
						{
							suggestion	=
								adornerInfo.standardizer.standardizeSpelling(
									spelling , "" );
						}
        			}
        			else
        			{
        				suggestions	= null;

        				suggestion	=
							adornerInfo.simpleStandardizer.standardizeSpelling
							(
								spelling ,
								""
							);
        			}

					session.setAttribute(
						"spellingSuggestions" , suggestions );
				}
		    }
								//	Output form.

			outputForm(
				out , spelling , suggestion , extendedSearch , adornerName );

		                    	//	Output scored list of suggested
		                    	//	spellings.

			outputSuggestions( out , spelling , suggestions );
		}
								//	Create results.
		result	=
			new ServletResult
			(
				fromForm ,
				out.getString() ,
				"Spelling Standardizer Example" ,
				"/morphadorner/spellingstandardizer/example/" ,
				"spellingstandardizerresults"
			);

		return result;
	}

	/**	Output suggestions.
	 *
	 *	@param	out				PrintWriter for servlet output.
	 *	@param	spelling		Spelling to standardize.
	 *	@param	suggestions		List of suggested standard spellings.
	 */

	public void outputSuggestions
	(
		java.io.PrintWriter out ,
		String spelling ,
		List<ScoredString> suggestions
	)
	{
		if ( suggestions == null ) return;
		if ( suggestions.size() < 1 ) return;

		if ( suggestions.size() == 1 )
		{
			ScoredString scoredString	= suggestions.get( 0 );

			if ( scoredString.getString().equals( "?" ) ) return;
		}

		out.println( "<hr shade=\"noshade\" />" );
		out.println( "<h3>" + suggestions.size() +
			" suggested spellings and scores for <em>" );
		out.println( spelling );
		out.println( "</em></h3>" );

		out.println( "<table cellpadding=\"2\">" );
		out.println( "<tr>" );
		out.println( "<th align=\"left\">Suggestion</th>" );
		out.println( "<th align=\"left\">Score</th>" );
		out.println( "</tr>" );

		for ( int i = suggestions.size() - 1 ; i >= 0 ; i-- )
		{
			ScoredString scoredString	= suggestions.get( i );

			String sugSpelling	= scoredString.getString() + "";
			String sugScore		=
				Formatters.formatDouble( scoredString.getScore() , 4 );

			out.println( "<tr>" );
			out.println( "<td>" + sugSpelling + "</td>" );
			out.println( "<td>" + sugScore + "</td>" );
			out.println( "</tr>" );
		}

		out.println( "</table>" );
	}

	/**	Output form.
	 *
	 *	@param	out				PrintWriter for servlet output.
	 *	@param	spelling		Spelling to standardize.
	 *	@param	suggestion		Suggested standard spelling.
	 *	@param	extendedSearch	Perform extended search for near matches.
	 *	@param	adornerName		Adorner name.
	 */

	public void outputForm
	(
		java.io.PrintWriter out ,
		String spelling ,
		String suggestion ,
		boolean extendedSearch ,
		String adornerName
	)
	{
		out.println( "<p>" );
		out.println( "Enter a spelling in the input box below." );
		out.println( "Press <strong>Suggest</strong> to see " );
		out.println( " a likely standardized spelling." );
		out.println( "</p>" );

		out.println(
			"<form method=\"post\" " +
			"action=\"/morphadorner/spellingstandardizer/example/SpellingStandardizer\" " +
			"name=\"spell\">" );
		out.println( "<table cellpadding=\"0\" cellspacing=\"5\">" );

		out.println( "<tr>" );
		out.println( "<td><strong>Spelling:</strong></td>" );
		out.println( "<td><input type=\"text\" name=\"spelling\"" +
			"size = \"20\" value=\"" + spelling + "\" /></td>" );
		out.println( "</tr>" );

		String checkedExtendedSearch	=
			( extendedSearch ? "checked=\"checked\"" : "" );

		out.println( "<tr>" );
		out.println( "<td>&nbsp;</td>" );
		out.println( "<td><input type=\"checkbox\" name=\"extendedsearch\"" +
				"value=\"extendedsearch\" " +
				checkedExtendedSearch +
				" />Perform extended search for suggested spellings</td>" );
		out.println( "</tr>" );

		outputAdornerSelection( out , "Lexicon:" , adornerName );

		out.println( "<tr>" );
		out.println( "<td>&nbsp;</td>" );
		out.println( "<td>" );
		out.println( "<input type=\"submit\" name=\"suggest\" value=\"Suggest\" />" );
		out.println( "<input type=\"submit\" name=\"clear\" value=\"Clear\" />" );
		out.println( "</td>" );
		out.println( "</tr>" );

		outputSpacerRow( out , 2 );

		out.println( "<tr>" );
		out.println( "<td><strong>Suggestion:</strong></td>" );
		out.println( "<td><label type=\"text\" name=\"suggestion\"" +
			"size = \"20\">" + suggestion + "</label></td>" );
		out.println( "</tr>" );

		out.println( "</table>" );
		out.println( "</form>" );
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



