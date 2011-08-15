package edu.northwestern.at.morphadorner.servlets;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.ServletConfig;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.lemmatizer.*;
import edu.northwestern.at.utils.corpuslinguistics.stemmer.*;
import edu.northwestern.at.utils.servlets.*;

/**	Finds lemma for a spelling.
 */

public class LemmatizerServlet extends BaseAdornerServlet
{
	/**	Servlet not ready message. */

	protected String notReadyMessage		=
		"Lemmatizer still initializing, please try again later.";

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
		boolean lemmatize	= ( request.getParameter( "lemmatize" ) != null );
		boolean fromForm	= clear || lemmatize;
		String adornerName	= request.getParameter( "adornername" );

								//	Get which adorner to use.

		AdornerInfo adornerInfo	= getAdornerInfo( adornerName );

								//	Get servlet session.

		HttpSession session 	= request.getSession( true );

								//	See if we're just returning the
								//	output of the previous invocation.

		String lemmatizerResults	=
			(String)session.getAttribute( "lemmatizerresults" );

								//	If so, just pipe the output back
								//	to the client.

		if ( ( lemmatizerResults != null ) && !fromForm )
		{
			session.setAttribute( "lemmatizerresults" , null );

			result	=
				new ServletResult
				(
					false ,
					lemmatizerResults ,
					"Lemmatizer Example" ,
					"/morphadorner/lemmatizer/example/" ,
					"lemmatizerresults"
				);
		}
		else
		{						//	Get string output writer.

			StringPrintWriter out	= new StringPrintWriter();

								//	See if we have a spelling to
								//	lemmatize.

			String spelling			= "";
			String wordClass		= "";
			String wordClass2		= "";
			String standardize		= "";
			String lemma			= "";
			String porterStem		= "";
        	String lancasterStem	= "";

			if ( lemmatize )
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
								//	See if we have a word class.

				wordClass	= request.getParameter( "wordclass" );

				if ( wordClass == null )
				{
					wordClass	= "";
				}
				else
				{
					wordClass	= wordClass.trim();
				}
								//	See if we have a secondary word class.

				wordClass2	= request.getParameter( "wordclass2" );

				if ( wordClass2 == null )
				{
					wordClass2	= "";
				}
				else
				{
					wordClass2	= wordClass2.trim();
				}
								//	See if we're to standardize
								//	the spelling before lemmatization.

				standardize	= request.getParameter( "standardize" );

								//	Get lemma if we have a spelling.

				if ( spelling.length() > 0 )
				{
					if ( standardize != null )
					{
						spelling	=
							adornerInfo.standardizer.standardizeSpelling(
								spelling , "" );
					}

					if ( wordClass.length() > 0 )
					{
						if ( wordClass2.length() > 0 )
						{
							lemma	=
								lemmatizer.lemmatize
								(
									spelling ,
									wordClass + "," + wordClass2
								);
						}
						else
						{
							lemma	=
								lemmatizer.lemmatize
								(
									spelling ,
									wordClass
								);
						}
					}
					else
					{
						lemma	= lemmatizer.lemmatize( spelling );
					}
								//	Get Porter and Lancaster stems.

					porterStem		= porterStemmer.stem( spelling );
					lancasterStem	= lancasterStemmer.stem( spelling );
				}
			}
								//	Output form and results.

			outputForm
			(
				out ,
				spelling ,
				lemma ,
				wordClass ,
				wordClass2 ,
				standardize ,
				porterStem ,
				lancasterStem ,
				adornerName
			);
								//	Create results.
			result	=
				new ServletResult
				(
					fromForm ,
					out.getString() ,
					"Lemmatizer Example" ,
					"/morphadorner/lemmatizer/example/" ,
					"lemmatizerresults"
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
		String lemma ,
		String wordClass ,
		String wordClass2 ,
		String standardize ,
		String porterStem ,
		String lancasterStem ,
		String adornerName
	)
	{
		out.println( "<p>" );
		out.println( "Enter a spelling and optionally a word class in the ");
		out.println( "input boxes below. " );
		out.println( "Press <strong>Lemmatize</strong> to see " );
		out.println( "the lemmatized form. " );
		out.println( "Check <strong>Standardize spelling</strong> to standardize nonstandard spellings before lemmatization." );
		out.println( "</p>" );

		out.println(
			"<form method=\"post\" action=\"/morphadorner/lemmatizer/example/Lemmatizer\" name=\"spell\">" );
		out.println( "<table cellpadding=\"0\" cellspacing=\"5\">" );

		out.println( "<tr>" );
		out.println( "<td><strong>Spelling:</strong></td>" );
		out.println( "<td><input type=\"text\" name=\"spelling\"" +
			"size = \"20\" value=\"" + spelling + "\" /></td>" );
		out.println( "</tr>" );

		String checkedStandardize	=
			( ( standardize == null ) ? "" : "checked=\"checked\"" );

		out.println( "<tr>" );
		out.println( "<td>" );

		out.println( "<td><input type=\"checkbox\" name=\"standardize\"" +
				"value=\"standardize\" " + checkedStandardize +
				" />Standardize spelling</td>" );

		out.println( "</td>" );
		out.println( "<td>&nbsp;</td>" );
		out.println( "</tr>" );

		out.println( "<tr>" );
		out.println( "<td><strong>Primary word class:</strong></td>" );
		out.println( "<td>" );
		out.println( "<select name=\"wordclass\">" );

		outputSelect( out , "" , ( wordClass.length() == 0 ) );
		outputSelect( out , "adjective" , wordClass.equals( "adjective" ) );
		outputSelect( out , "adverb" , wordClass.equals( "adverb" ) );
		outputSelect( out , "compound" , wordClass.equals( "compound" ) );
		outputSelect( out , "conjunction" , wordClass.equals( "conjunction" ) );
		outputSelect( out , "infinitive-to" , wordClass.equals( "infinitive-to" ) );
		outputSelect( out , "noun" , wordClass.equals( "noun" ) );
		outputSelect( out , "noun-possessive" , wordClass.equals( "possessive-noun" ) );
		outputSelect( out , "preposition" , wordClass.equals( "preposition" ) );
		outputSelect( out , "pronoun" , wordClass.equals( "pronoun" ) );
		outputSelect( out , "pronoun-possessive" , wordClass.equals( "pronoun-possessive" ) );
		outputSelect( out , "pronoun-possessive-determiner" , wordClass.equals( "pronoun-possessive-determiner" ) );
		outputSelect( out , "verb" , wordClass.equals( "verb" ) );

        out.println( "</select>" );
		out.println( "</td>" );
		out.println( "</tr>" );

		out.println( "<tr>" );
		out.println( "<td><strong>Secondary word class:</strong></td>" );
		out.println( "<td>" );
		out.println( "<select name=\"wordclass2\">" );

		outputSelect( out , "" , ( wordClass2.length() == 0 ) );
		outputSelect( out , "adjective" , wordClass2.equals( "adjective" ) );
		outputSelect( out , "adverb" , wordClass2.equals( "adverb" ) );
		outputSelect( out , "compound" , wordClass2.equals( "compound" ) );
		outputSelect( out , "conjunction" , wordClass2.equals( "conjunction" ) );
		outputSelect( out , "infinitive-to" , wordClass2.equals( "infinitive-to" ) );
		outputSelect( out , "noun" , wordClass2.equals( "noun" ) );
		outputSelect( out , "noun-possessive" , wordClass2.equals( "possessive-noun" ) );
		outputSelect( out , "preposition" , wordClass2.equals( "preposition" ) );
		outputSelect( out , "pronoun" , wordClass2.equals( "pronoun" ) );
		outputSelect( out , "pronoun-possessive" , wordClass2.equals( "pronoun-possessive" ) );
		outputSelect( out , "pronoun-possessive-determiner" , wordClass2.equals( "pronoun-possessive-determiner" ) );
		outputSelect( out , "verb" , wordClass2.equals( "verb" ) );

        out.println( "</select>" );
		out.println( "</td>" );
		out.println( "</tr>" );

		outputAdornerSelection( out , "Lexicon:" , adornerName );

		outputSpacerRow( out , 2 );

		out.println( "<tr>" );
		out.println( "<td>" );
		out.println( "<input type=\"submit\" name=\"lemmatize\" value=\"Lemmatize\" />" );
		out.println( "<input type=\"submit\" name=\"clear\" value=\"Clear\" />" );
		out.println( "</td>" );
		out.println( "</tr>" );

		outputSpacerRow( out , 2 );

		out.println( "<tr>" );
		out.println( "<td><strong>Lemma:</strong></td>" );
		out.println( "<td><label type=\"text\" name=\"lemma\"" +
			"size = \"20\">" + lemma + "</label></td>" );
		out.println( "</tr>" );

		out.println( "<tr>" );
		out.println( "<td><strong>Porter stem:</strong></td>" );
		out.println( "<td><label type=\"text\" name=\"porter\"" +
			"size = \"20\">" + porterStem + "</label></td>" );
		out.println( "</tr>" );

		out.println( "<tr>" );
		out.println( "<td><strong>Lancaster stem:</strong></td>" );
		out.println( "<td><label type=\"text\" name=\"lancaster\"" +
			"size = \"20\">" + lancasterStem + "</label></td>" );
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



