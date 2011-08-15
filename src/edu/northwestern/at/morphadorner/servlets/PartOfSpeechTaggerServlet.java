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
import edu.northwestern.at.utils.corpuslinguistics.adornedword.*;
import edu.northwestern.at.utils.corpuslinguistics.lexicon.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.guesser.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.hepple.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.transitionmatrix.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.trigram.*;
import edu.northwestern.at.utils.corpuslinguistics.sentencesplitter.*;
import edu.northwestern.at.utils.corpuslinguistics.tokenizer.*;
import edu.northwestern.at.utils.servlets.*;

/**	Tags words with their parts of speech.
 */

public class PartOfSpeechTaggerServlet extends BaseAdornerServlet
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

		boolean clear			=
			( request.getParameter( "clear" ) != null );

		boolean tag				=
			( request.getParameter( "tag" ) != null );

		String adornerName		=
			request.getParameter( "adornername" );

		if ( adornerName == null ) adornerName	= "ncf";

		boolean showLemma		=
			( request.getParameter( "lemma" ) != null );

		boolean showStandard    =
			( request.getParameter( "standard" ) != null );

		boolean contWordNumbers	=
			( request.getParameter( "contword" ) != null );

		boolean fromForm		= clear || tag;

								//	Get which adorner to use.

		AdornerInfo adornerInfo	= getAdornerInfo( adornerName );

								//	Get servlet session.

		HttpSession session = request.getSession( true );

								//	See if we're just returning the
								//	output of the previous invocation.

		String taggerResults	=
			(String)session.getAttribute( "postaggerresults" );

								//	If so, just pipe the output back
								//	to the client.

		if ( ( taggerResults != null ) && !fromForm )
		{
			session.setAttribute( "postaggerresults" , null );

			result	=
				new ServletResult
				(
					false ,
					taggerResults ,
					"Part of Speech Tagger Example" ,
					"/morphadorner/postagger/example/" ,
					"postaggerresults"
				);
		}
		else
		{
								//	Get string output writer.

			StringPrintWriter out	= new StringPrintWriter();

								//	See if we're tagging text.
								//	If not, clear the text and
								//	return an empty form.

			String text								= "";
			List<List<String>> sentences			= null;
			List<List<AdornedWord>> taggedSentences	= null;

			if ( tag )
			{
								//	See if we have text to tag.

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
					sentences		=
						adornerInfo.extractor.extractSentences( text );

					taggedSentences	=
						adornerInfo.tagger.tagSentences( sentences );
				}
			}
								//	Output form.

	    	outputForm(
	    		out , text , adornerName , contWordNumbers , showStandard ,
	    		showLemma ,adornerInfo );

								//	Output tagged words.

			outputTaggedWords(
				out , taggedSentences , contWordNumbers , showStandard ,
				showLemma , adornerInfo );

								//	Create results.
			result	=
				new ServletResult
				(
					fromForm ,
					out.getString() ,
					"Part of Speech Tagger Example" ,
					"/morphadorner/postagger/example/" ,
					"postaggerresults"
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
		String text ,
		String adornerName ,
		boolean contWordNumbers ,
		boolean showStandard ,
		boolean showLemma ,
		AdornerInfo adornerInfo
	)
	{
		out.println( "<p>" );
		out.println( "Enter text to tag in the ");
		out.println( "input field below." );
		out.println( "</p>" );

		out.println(
			"<form method=\"post\"" +
			" action=\"/morphadorner/postagger/example/PartOfSpeechTagger\"" +
			" name=\"postagger\">" );

		out.println( "<table cellpadding=\"0\" cellspacing=\"5\">" );

		out.println( "<tr>" );
		out.print  ( "<td colspan=\"2\"><textarea name=\"text\" rows=\"10\"" +
			" cols=\"50\">" );
		out.print  ( text );
		out.println( "</textarea>" );
		out.println( "</td>" );
		out.println( "</tr>" );

		outputAdornerSelection( out , "Lexicon:" , adornerName );

		String checkedContinuous	=
			( contWordNumbers ? "checked=\"checked\"" : "" );

		out.println( "<tr>" );
		out.println( "<td><strong>Options:</strong></td>" );
		out.println( "<td>" +
			"<input type=\"checkbox\" name=\"contword\"" +
			"value=\"contword\" " +
			checkedContinuous +
			" />Show continuous word numbers</td>" );
		out.println( "</tr>" );

		String checkedStandard	=
			( showStandard ? "checked=\"checked\"" : "" );

		out.println( "<tr>" );

		out.println( "<td>&nbsp;</td>" );
		out.println( "<td><input type=\"checkbox\" " +
			"name=\"standard\"" +
			"value=\"standard\" " +
			checkedStandard +
			" />Show standardized spelling</td>" );

		out.println( "</tr>" );

		String checkedLemma	= ( showLemma ? "checked=\"checked\"" : "" );

		out.println( "<tr>" );

		out.println( "<td>&nbsp;</td>" );
		out.println( "<td><input type=\"checkbox\" name=\"lemma\"" +
				"value=\"lemma\" " +
				checkedLemma +
				" />Show lemma</td>" );

		out.println( "</tr>" );

		out.println( "<tr>" );
		out.println( "<td colspan=\"2\">" );
		out.println( "<input type=\"submit\" name=\"tag\" value=\"Tag\" />" );
		out.println( "<input type=\"submit\" name=\"clear\" value=\"Clear\" />" );
		out.println( "</td>" );
		out.println( "</tr>" );

		out.println( "</table>" );
		out.println( "</form>" );
	}

	/**	Output the part of speech tagged words.
	 *
	 *	@param	out			PrintWriter for servlet output.
	 *	@param	taggedSentences	List of tagged words.
	 */

	public void outputTaggedWords
	(
		java.io.PrintWriter out ,
		List<List<AdornedWord>> taggedSentences ,
		boolean contWordNumbers ,
		boolean showStandard ,
		boolean showLemma ,
		AdornerInfo adornerInfo
	)
	{
		if ( taggedSentences == null ) return;

		out.println( "<hr noshade=\"noshade\" />" );

    	out.println( "<table width=\"100%\">");

		out.println( "<tr>" );
		out.println( "<th align=\"left\" class=\"width1pct\">" );
		out.println( "S#" );
		out.println( "</th>" );
		out.println( "<th align=\"left\" class=\"width1pct\">" );
		out.println( "W#" );
		out.println( "</th>" );
		out.println( "<th align=\"left\" class=\"width48pct\">" );
		out.println( "Word" );
		out.println( "</th>" );
		out.println( "<th align=\"left\" class=\"width48pct\">" );
		out.println( "Tag" );
		out.println( "</th>" );

		if ( showStandard )
		{
			out.println( "<th align=\"left\" class=\"width48pct\">" );
			out.println( "Standard" );
			out.println( "</th>" );
		}

		if ( showLemma )
		{
			out.println( "<th align=\"left\" class=\"width48pct\">" );
			out.println( "Lemma" );
			out.println( "</th>" );
		}

		out.println( "</tr>" );

		String standardSpelling	= "";
		int wordNumber			= 0;

		for ( int i = 0 ; i < taggedSentences.size() ; i++ )
		{
			List<AdornedWord> taggedWords	= taggedSentences.get( i );

			if ( !contWordNumbers )
			{
				wordNumber	= 0;
			}

			for ( int j = 0 ; j < taggedWords.size() ; j++ )
			{
					            //	Get the word and its part of speech
					            //	tag.

				AdornedWord wordAndTag	= taggedWords.get( j );

				out.println( "<tr>" );

				out.println( "<td valign=\"top\" class=\"width1pct\">" );
				out.println( ( i + 1 ) + "" );
				out.println( "</td>" );

				out.println( "<td valign=\"top\" class=\"width1pct\">" );
				out.println( ( ++wordNumber ) + "" );
				out.println( "</td>" );

				out.println( "<td valign=\"top\" class=\"width48pct\">" );
				out.println( wordAndTag.getSpelling() );
				out.println( "</td>" );

				out.println( "<td valign=\"top\" class=\"width48pct\">" );
				out.println( wordAndTag.getPartsOfSpeech() );
				out.println( "</td>" );

				if ( showStandard )
				{
					standardSpelling	=
						adornerInfo.standardizer.standardizeSpelling
						(
							wordAndTag.getSpelling() ,
							wordAndTag.getPartsOfSpeech()
						);

					out.println( "<td valign=\"top\" class=\"width48pct\">" );
					out.println( standardSpelling );
					out.println( "</td>" );
				}

				if ( showLemma )
				{
								//	Try lexicon first.

					String lemma	=
						adornerInfo.wordLexicon.getLemma
						(
							wordAndTag.getSpelling() ,
							wordAndTag.getPartsOfSpeech()
						);

								//	Lemma not found in word lexicon.
								//	Use lemmatizer.

					if ( lemma.equals( "*" ) && ( lemmatizer != null ) )
					{
						if ( standardSpelling.length() > 0 )
						{
							lemma	=
								lemmatizer.lemmatize
								(
									standardSpelling ,
									adornerInfo.partOfSpeechTags.getLemmaWordClass(
										wordAndTag.getPartsOfSpeech() )
								);
						}
						else
						{
							lemma	=
								lemmatizer.lemmatize
								(
									wordAndTag.getSpelling() ,
									adornerInfo.partOfSpeechTags.getLemmaWordClass(
										wordAndTag.getPartsOfSpeech() )
								);
						}
					}

					out.println( "<td valign=\"top\" class=\"width48pct\">" );
					out.println( lemma );
					out.println( "</td>" );
				}

				out.println( "</tr>" );
			}

			out.println( "<tr>" );
			out.println( "<td colspan=\"4\">" );
			out.println( "<hr shade=\"noshade\">" );
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



