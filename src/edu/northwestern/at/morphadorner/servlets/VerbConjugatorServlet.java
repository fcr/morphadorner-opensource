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

/**	Conjugates a verb.
 */

public class VerbConjugatorServlet extends BaseAdornerServlet
{
	/**	Servlet not ready message. */

	protected String notReadyMessage		=
		"Conjugator still initializing, please try again later.";

	/**	Pronouns for conjugation table. */

	protected static final String[] pronouns	=
		new String[]
		{
			"I" ,
			"You" ,
			"He" ,
			"We" ,
			"You" ,
			"They"
		};

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
		boolean conjugate	= ( request.getParameter( "conjugate" ) != null );
		boolean fromForm	= clear || conjugate;

								//	Get servlet session.

		HttpSession session 	= request.getSession( true );

								//	See if we're just returning the
								//	output of the previous invocation.

		String conjugatorResults	=
			(String)session.getAttribute( "conjugatorresults" );

								//	If so, just pipe the output back
								//	to the client.

		if ( ( conjugatorResults != null ) && !fromForm )
		{
			session.setAttribute( "conjugatoresults" , null );

			result	=
				new ServletResult
				(
					false ,
					conjugatorResults ,
					"Verb Conjugator Example" ,
					"/morphadorner/conjugator/example" ,
					"conjugatoresults"
				);
		}
		else
		{						//	Get string output writer.

			StringPrintWriter out	= new StringPrintWriter();

								//	See if we have an infinitive to
								//	conjugate.

			String infinitive			= "";
			String past					= "";
			String pastParticiple		= "";
			String presentParticiple	= "";
			String present3rd			= "";

								//	See if we're to prefer American
								//	spellings in conjugation.

			String american	= request.getParameter( "american" );

			if ( conjugate )
			{
				infinitive	= request.getParameter( "infinitive" );

				if ( infinitive == null )
				{
					infinitive	= "";
				}
				else
				{
					infinitive	= infinitive.trim();
				}
								//	Conjugate verb if we have an infinitive.

				if ( infinitive.length() > 0 )
				{
					present3rd			=
						inflector.conjugate
						(
							infinitive ,
							VerbTense.PRESENT ,
							Person.THIRD_PERSON_SINGULAR
						);

					presentParticiple	=
						inflector.conjugate
						(
							infinitive ,
							VerbTense.PRESENT_PARTICIPLE ,
							Person.THIRD_PERSON_SINGULAR
						);

					past				=
						inflector.conjugate
						(
							infinitive ,
							VerbTense.PAST ,
							Person.THIRD_PERSON_SINGULAR
						);

					pastParticiple	=
						inflector.conjugate
						(
							infinitive ,
							VerbTense.PAST_PARTICIPLE ,
							Person.THIRD_PERSON_SINGULAR
						);
				}
			}
								//	Output form and results.
			outputForm
			(
				out ,
				infinitive ,
				past ,
				pastParticiple ,
				presentParticiple ,
				present3rd ,
				american
			);
								//	Return results.
			result	=
				new ServletResult
				(
					fromForm ,
					out.getString() ,
					"Conjugator Example" ,
					"/morphadorner/conjugator/example/" ,
					"conjugatoresults"
				);
		}

   		return result;
	}

	/**	Output form.
	 */

	public void outputForm
	(
		java.io.PrintWriter out ,
		String infinitive ,
		String past ,
		String pastParticiple ,
		String presentParticiple ,
		String present3rd ,
		String american
	)
	{
		out.println( "<p>" );
		out.println( "Enter an infinitive in the input box below.");
		out.println( "Press <strong>Conjugate</strong> to see " );
		out.println( "the conjugated forms of the verb. " );
		out.println( "Check <strong>American spellings</strong> to display " +
						"American instead of British spellings." );
		out.println( "</p>" );

		out.println(
			"<form method=\"post\" action=\"/morphadorner/conjugator/example/VerbConjugator\" name=\"conjugator\">" );
		out.println( "<table cellpadding=\"0\" cellspacing=\"5\">" );

		out.println( "<tr>" );
		out.println( "<td><strong>Infinitive:</strong></td>" );
		out.println( "<td><input type=\"text\" name=\"infinitive\"" +
			"size = \"20\" value=\"" + infinitive + "\" /></td>" );
		out.println( "</tr>" );

		out.println( "<tr>" );
		out.println( "<td>&nbsp;</td>" );

		String checkedAmerican	=
			( ( american == null ) ? "" : "checked=\"checked\"" );

		out.println( "<td><input type=\"checkbox\" name=\"american\"" +
				"value=\"american\" " + checkedAmerican +
				"/>American spellings</td>" );
		out.println( "</tr>" );

		outputSpacerRow( out , 2 );

		out.println( "<tr>" );
		out.println( "<td>" );
		out.println( "<input type=\"submit\" name=\"conjugate\" value=\"Conjugate\" />" );
		out.println( "<input type=\"submit\" name=\"clear\" value=\"Clear\" />" );
		out.println( "</td>" );
		out.println( "</tr>" );

		outputSpacerRow( out , 2 );

		if ( ( infinitive != null ) && ( infinitive.length() > 0 ) )
		{
			doConjugation(
				out , american , infinitive , VerbTense.PRESENT , "Present: " );

			doConjugation(
				out , american , infinitive , VerbTense.PAST , "Past: " );

			if ( american != null )
			{
				presentParticiple	=
					britishToUS.mapSpelling( presentParticiple );

				pastParticiple	=
					britishToUS.mapSpelling( pastParticiple );
			}

			out.println( "<tr>" );
			out.println( "<td><strong>Present Participle:</strong></td>" );
			out.println( "<td><label type=\"text\" name=\"presentparticiple\"" +
				"size = \"20\">" + presentParticiple + "</label></td>" );
			out.println( "</tr>" );

			out.println( "<tr>" );
			out.println( "<td><strong>Past Participle:</strong></td>" );
			out.println( "<td><label type=\"text\" name=\"pastparticiple\"" +
				"size = \"20\">" + pastParticiple + "</label></td>" );
			out.println( "</tr>" );
		}

		out.println( "</table>" );
		out.println( "</form>" );
	}

	protected static void doConjugation
	(
		java.io.PrintWriter out ,
		String american ,
		String verb ,
		VerbTense verbTense ,
		String title
	)
	{
		int i = 0;

		out.println( "<tr>" );
		out.println( "<td valign=\"top\">" );
		out.println( "<strong>" );
		out.println( title );
		out.println( "</strong>" );
		out.println( "</td>" );
		out.println( "<td valign=\"top\">" );

		for ( Person person : Person.values() )
		{
			String conjugatedVerb	=
				inflector.conjugate( verb , verbTense , person );

			if ( american != null )
			{
				conjugatedVerb	= britishToUS.mapSpelling( conjugatedVerb );
			}

			if ( verbTense == VerbTense.PRESENT_PARTICIPLE )
			{
				out.println
				(
					" " + pronouns[ i++ ] + " " +
					inflector.conjugate( "be" , VerbTense.PRESENT , person ) +
					" " +
					conjugatedVerb
				);
			}
			else if ( verbTense == VerbTense.PAST_PARTICIPLE )
			{
				out.println
				(
					" " + pronouns[ i++ ] + " " +
					inflector.conjugate( "be" , VerbTense.PAST , person ) +
					" " +
					conjugatedVerb
				);
			}
			else
			{
				out.println
				(
					" " + pronouns[ i++ ] + " " +
					conjugatedVerb
				);
			}

			out.println( "<br />" );
		}

		out.println( "</td>" );
		out.println( "</tr>" );
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



