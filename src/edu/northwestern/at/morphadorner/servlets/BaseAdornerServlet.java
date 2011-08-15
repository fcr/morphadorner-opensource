package edu.northwestern.at.morphadorner.servlets;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.net.*;

import javax.servlet.*;
import javax.servlet.http.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.html.*;
import edu.northwestern.at.utils.corpuslinguistics.inflector.*;
import edu.northwestern.at.utils.corpuslinguistics.languagerecognizer.*;
import edu.northwestern.at.utils.corpuslinguistics.lemmatizer.*;
import edu.northwestern.at.utils.corpuslinguistics.lexicon.*;
import edu.northwestern.at.utils.corpuslinguistics.namerecognizer.*;
import edu.northwestern.at.utils.corpuslinguistics.partsofspeech.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.guesser.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.transitionmatrix.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.trigram.*;
import edu.northwestern.at.utils.corpuslinguistics.sentencesplitter.*;
import edu.northwestern.at.utils.corpuslinguistics.spellingmapper.*;
import edu.northwestern.at.utils.corpuslinguistics.spellingstandardizer.*;
import edu.northwestern.at.utils.corpuslinguistics.stemmer.*;
import edu.northwestern.at.utils.corpuslinguistics.tokenizer.*;
import edu.northwestern.at.utils.servlets.*;

import net.sf.jlinkgrammar.*;

/**	Base class for MorphAdorner example servlets.
 *
 *  <p>
 *	Extends XHttpServlet with methods for MorphAdorner servlets.
 *	Also stores common objects used by multiple servlets.
 *	</p>
 */

abstract public class BaseAdornerServlet extends XHttpServlet
{
	/**	True to Output full top and bottom page HTML.
	 *	false to output only servlet output.
	 */

	protected boolean outputFullHTML	= true;

	/**	Default data directory. */

	protected static String defaultDataDirectory	= "/nupos";

	/**	Data directory. */

	protected static String dataDirectory;

	/**	19th century adorner information. */

	protected static AdornerInfo ncfAdornerInfo;

	/**	19th century fiction lexicon file name. */

	protected static String ncfWordLexiconFileName	=
		"/ncflexicon.lex";

	/**	19th century fiction lexicon file name. */

	protected static String ncfSuffixLexiconFileName	=
		"/ncfsuffixlexicon.lex";

	/**	Alternate to standard spelling pairs for
	 *	19th century fiction.
	 */

	protected static String ncfSpellingPairsFileName	=
		"/ncfmergedspellingpairs.tab";

	/**	19th century fiction transition matrix file name. */

	protected static String ncfTransitionMatrixFileName	=
		"/ncftransmat.mat";

	/**	Early modern English adorner information. */

	protected static AdornerInfo emeAdornerInfo;

	/**	Early modern English word lexicon file name. */

	protected static String emeWordLexiconFileName	=
		"/emelexicon.lex";

	/**	Early modern English suffix lexicon File name. */

	protected static String emeSuffixLexiconFileName	=
		"/emesuffixlexicon.lex";

	/**	Early modern English transition matrix file name. */

	protected static String emeTransitionMatrixFileName	=
		"/emetransmat.mat";

	/**	Early modern English alternate to standard spelling
	 *	pairs. */

	protected static String emeSpellingPairsFileName	=
		"/ememergedspellingpairs.tab";

	/**	Standard spellings file name. */

	protected static String standardSpellingsFileName	=
		"/standardspellings.txt";

	/**	The lemmatizer. */

	protected static Lemmatizer lemmatizer;

	/**	Porter stemmer. */

	protected static Stemmer porterStemmer		= new PorterStemmer();

	/**	Lancaster stemmer. */

	protected static Stemmer lancasterStemmer	= new LancasterStemmer();

	/**	Names. */

	protected static Names names				= new Names();

	/**	The language recognizer. */

	protected static LanguageRecognizer languageRecognizer	=
		new DefaultLanguageRecognizer();

	/**	English inflector. */

	protected static Inflector inflector		= new EnglishInflector();

	/**	British to US spelling mapper. */

	protected static SpellingMapper britishToUS	=
		new BritishToUSSpellingMapper();

	/**	Extra words file name. */

	protected static String extraWordsFileName	=
		"/extrawords.txt";

	/**	Latin words file name. */

	protected static String latinWordsFileName	=
		"/latinwords.txt";

	/**	Latin words list. */

	protected static TaggedStrings latinWords;

	/**	Extra words list. */

	protected static TaggedStrings extraWords;

	/**	Link grammar dictionary. */

	protected static Dictionary dictionary ;

	/**	Link grammar parser options. */

	protected static ParseOptions parseOptions ;

	/**	Link grammar parser data file directory. */

	protected static String lgParserDataDirectory	= "/lgparser";

	/**	Initialization states. */

	protected static final int INITNOTSTARTED	= 0;
	protected static final int INITINPROGRESS	= 1;
	protected static final int INITDONE			= 2;
	protected static final int INITFAILED		= 3;

	/**	Initialization complete. */

	protected static int initializationStatus	= INITNOTSTARTED;

	/**	Servlet not ready message. */

	protected static final String servletNotReadyMessage	=
		"Servlet not yet ready, please try again in a minute.";

	/**	Servlet not ready title. */

	protected static final String servletNotReadyTitle	=
		"Servlet not ready";

	/**	Initialize common objects.
	 *
	 *	@param	config	Servlet configuration.
	 */

	protected synchronized static void doInitialization
	(
		ServletConfig config
	)
	{
								//	If init done or in progress,
								//	do nothing.

		if ( initializationStatus != INITNOTSTARTED ) return;

		try
		{
								//	Remember initialization has started.

			initializationStatus	= INITINPROGRESS;

								//	Get the data directory.

			dataDirectory			= defaultDataDirectory;

			if ( config.getInitParameter( "datadirectory" ) != null )
			{
				dataDirectory	=
					config.getInitParameter( "datadirectory" ).trim();
			}

			if ( dataDirectory.length() == 0 )
			{
				File tryDir	= new File( "data" );

				if ( tryDir.exists() )
				{
					dataDirectory	= tryDir.getAbsolutePath();
					dataDirectory	=
						new File( dataDirectory ).getCanonicalPath();
				}
				else
				{
					tryDir	= new File( "../data" );

					if ( tryDir.exists() )
					{
						dataDirectory	= tryDir.getAbsolutePath();
						dataDirectory	=
							new File( dataDirectory ).getCanonicalPath();
					}
				}
			}
								//	Add data directory to file names.

			ncfWordLexiconFileName		=
				dataDirectory + ncfWordLexiconFileName;

			ncfSuffixLexiconFileName	=
				dataDirectory + ncfSuffixLexiconFileName;

			ncfSpellingPairsFileName	=
				dataDirectory + ncfSpellingPairsFileName;

			ncfTransitionMatrixFileName	=
				dataDirectory + ncfTransitionMatrixFileName;

			emeWordLexiconFileName		=
				dataDirectory + emeWordLexiconFileName;

			emeSuffixLexiconFileName	=
				dataDirectory + emeSuffixLexiconFileName;

			emeSpellingPairsFileName	=
				dataDirectory + emeSpellingPairsFileName;

			emeTransitionMatrixFileName	=
				dataDirectory + emeTransitionMatrixFileName;

			standardSpellingsFileName	=
				dataDirectory + standardSpellingsFileName;

			extraWordsFileName	=
            	dataDirectory + extraWordsFileName;

			latinWordsFileName	=
            	dataDirectory + latinWordsFileName;

			lgParserDataDirectory =
				dataDirectory + lgParserDataDirectory;

								//	Get latin words list.

			if ( latinWords	== null )
			{
				latinWords	= getLatinWordsList();
			}
								//	Get extra words list.

			if ( extraWords	== null )
			{
				extraWords	= getExtraWordsList();
			}

			TaggedStrings[] extraWordLists	=
				new TaggedStrings[]
				{
					extraWords ,
					latinWords
				};
								//	Create early modern English adorner
								//	information.

			if ( emeAdornerInfo == null )
			{
				emeAdornerInfo	=
					new AdornerInfo
					(
						emeWordLexiconFileName ,
						emeSuffixLexiconFileName ,
						emeTransitionMatrixFileName ,
						standardSpellingsFileName ,
						emeSpellingPairsFileName ,
						extraWordLists ,
						names
					);
			}
								//	Create 19th century fiction adorner
								//	information.

			if ( ncfAdornerInfo == null )
			{
				ncfAdornerInfo	=
					new AdornerInfo
					(
						ncfWordLexiconFileName ,
						ncfSuffixLexiconFileName ,
						ncfTransitionMatrixFileName ,
						standardSpellingsFileName ,
						ncfSpellingPairsFileName ,
						extraWordLists ,
						names
					);
			}
								//	Get lemmatizer.

			if ( lemmatizer == null )
			{
				lemmatizer	= new DefaultLemmatizer();

				lemmatizer.setDictionary
				(
					ncfAdornerInfo.standardizer.getStandardSpellings()
				);
			}
								//	Get link grammar parser.

			if ( parseOptions == null )
			{
				parseOptions = new ParseOptions() ;

				parseOptions.parse_options_set_short_length( 10 ) ;
				parseOptions.parse_options_set_max_null_count( 10 ) ;
				parseOptions.parse_options_set_linkage_limit( 100 ) ;
			}

			if ( dictionary == null )
			{
				dictionary =
					new Dictionary
					(
						parseOptions ,
						lgParserDataDirectory + "/4.0.dict" ,
						"4.0.knowledge" ,
						"4.0.constituent-knowledge" ,
						"4.0.affix"
					) ;
			}
								//	Initialization complete.

			initializationStatus	= INITDONE;
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			initializationStatus	= INITFAILED;
		}
	}

	/**	Initialize common objects.
	 *
	 *	@param	config	Servlet configuration.
	 */

	protected synchronized static void initialize
	(
		final ServletConfig config
	)
	{
								//	Run initializer thread.

		Thread runner = new Thread( "Servlet Initializer" )
		{
			public void run()
			{
				doInitialization( config );
			}
		};

		runner.start();
	}

	/**	Get Latin words list.
	 */

	public synchronized static TaggedStrings getLatinWordsList()
	{
								//	Load Latin words.

		TextFile latinWordsFile	=
			new TextFile
			(
				latinWordsFileName ,
				"utf-8"
			);

		SingleTagTaggedStrings latinWords	=
			new SingleTagTaggedStrings
			(
				latinWordsFile.toArray() ,
				"fw-la"
			);

		latinWordsFile	= null;

		return latinWords;
	}

	/**	Get extra words list.
	 */

	public synchronized static TaggedStrings getExtraWordsList()
	{
		UTF8Properties result	= null;

								//	Load extra words.
		try
		{
			result	= new UTF8Properties();
			result.load
			(
				new File( extraWordsFileName ).toURI().toURL().openStream()
			);
		}
		catch ( Exception e )
		{
		}

		return result;
	}

	/**	Select adorner to use.
	 *
	 *	@param	adornerName	Adorner name.
	 *
	 *	@return				AdornerInfo for specified adorner.
	 */

	public static AdornerInfo getAdornerInfo( String adornerName )
	{
		AdornerInfo result;

		if ( ( adornerName != null ) && adornerName.equals( "eme" ) )
		{
			result	= emeAdornerInfo;
		}
		else
		{
			result	= ncfAdornerInfo;
		}

		return result;
	}

	/**	Check if servlet ready for use.
	 *
	 *	@return		true if servlet ready for use.
	 */

	public static boolean isReady()
	{
		return ( initializationStatus == INITDONE );
	}

	/**	Handle servlet post requests.
	 *
	 *	@param	request		Servlet request.
	 *	@param	response	Servlet response.
	 */

	public void doPost
	(
		HttpServletRequest request ,
		HttpServletResponse response
	)
		throws ServletException, java.io.IOException
	{
		ServletResult results;

								//	If the servlet is not yet ready,
								//	output a message to that effect.
		if ( !isReady() )
		{
			results	= outputNotReady( servletNotReadyMessage );
		}
		else
		{
			results	= doHandleRequest( request , response );
		}
								//	Output results.

		if ( results.getFromForm() )
		{
								//	Get servlet session.

			HttpSession session = request.getSession( true );

			session.setAttribute
			(
				results.getSessionAttributeName() ,
				results.getResults()
			);
								//	Output accumulated results if
								//	we're constructing the results
								//	as a full web page.  Otherwise
								//	redirect to originating page
								//	to get results displayed.

			if ( outputFullHTML )
			{
				outputResults
				(
					response ,
					results.getResults() ,
					results.getTitle()
				);
			}
			else
			{
				response.sendRedirect
				(
					createRedirectURL
					(
						response,
						results.getRedirectionURL() ,
						"" , "" , ""
					)
				);
			}
		}
		else
		{
			outputResults
			(
				response ,
				results.getResults() ,
				results.getTitle()
			);
   		}
	}

	/**	Output top of page.
	 *
	 *	@param	out		PrintWriter for servlet output.
	 *	@param	title	The servlet title.
	 */

	public void outputHeader
	(
		java.io.PrintWriter out ,
		String title
	)
	{
		String docType	=
			"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"" +
			" \"DTD/xhtml1-transitional.dtd\">";

		out.println( docType );
		out.println( "<html>" );
		out.println( "<head>" );
		out.println( "<title>" );
		if ( title != null ) out.println( title );
		out.println( "</title>" );
		out.println( "<meta http-equiv=\"Content-Type\" " +
			"content=\"text/html; charset=utf-8\" />" );

		out.println( "<link type=\"text/css\" rel=\"stylesheet\" " +
			"href=\"/morphadorner/styles/mstyle.css\" />" );

		out.println( "</head>" );
		out.println( "<body class=\"nomargin\">" );
	}

	/**	Output servlet not ready message.
	 *
	 *	@param	notReadyMessage		Server not ready message.
	 */

	public ServletResult outputNotReady
	(
		String notReadyMessage
	)
	{
								//	Get string output writer.

		StringPrintWriter out	= new StringPrintWriter();

		out.println( "<h2>" + notReadyMessage + "</h2>" );

		ServletResult result	=
			new ServletResult
			(
				false ,
				out.getString() ,
				servletNotReadyTitle ,
				"",
				""
			);

		return result;
	}

	/**	Output empty table row as spacer.
	 *
	 *	@param	out			PrintWriter for servlet output.
	 *	@param	nColumns	Number of empty table columns to output.
	 */

	public void outputSpacerRow( java.io.PrintWriter out , int nColumns )
	{
		out.println( "<tr>" );

		for ( int i = 0 ; i < nColumns ; i++ )
		{
			out.println( "<td>" );
			out.println( "&nbsp;" );
			out.println( "</td>" );
		}

		out.println( "</tr>" );
	}

	/**	Output bottom of page.
	 *
	 *	@param	out		PrintWriter for servlet output.
	 */

	public void outputFooter( java.io.PrintWriter out )
	{
		out.println( "</body>" );
		out.println( "</html>" );
	}

	/**	Output a select clause.
	 *
	 *	@param	out				PrintWriter for servlet output.
	 *	@param	selectValue		The value.
	 *	@param	selected		True if selected.
	 */

	public void outputSelect
	(
		java.io.PrintWriter out ,
		String selectValue ,
		boolean selected
	)
	{
		out.print( "<option value=\"" );
		out.print( selectValue );
		out.print( "\"" );

		if ( selected )
		{
			out.print( " selected=\"selected\" " );
		}

		out.print( "\">" );
		out.print( selectValue );
		out.println( "</option>" );
	}

	/**	Output adorner selection form field.
	 *
	 *	@param	out				PrintWriter for servlet output.
	 *	@param	label			Column label.  May be empty.
	 *	@param	adornerName		Adorner name.
	 */

	public void outputAdornerSelection
	(
		java.io.PrintWriter out ,
		String label ,
		String adornerName
	)
	{
		out.println( "<tr>" );

		if ( ( label != null ) && ( label.length() > 0 ) )
		{
			out.println( "<td valign=\"top\">" );
			out.println( "<strong>" );
			out.print( label );
			out.println( "</strong>" );
			out.println( "</td>" );
		}

		out.println( "<td>" );

		String checkedEME	= "";
		String checkedNCF	= "checked=\"checked\"";

		if ( ( adornerName != null ) && ( adornerName.equals( "eme" ) ) )
		{
			checkedEME	= "checked=\"checked\"";
			checkedNCF	= "";
		}

		out.println( "<input type=\"radio\" name=\"adornername\" " +
			"value=\"eme\"" +
			checkedEME +
			">Early Modern English</input><br />" );

		out.println( "<input type=\"radio\" name=\"adornername\" " +
			"value=\"ncf\"" +
			checkedNCF +
			">Nineteenth Century Fiction</input>" );

		out.println( "</td>" );
		out.println( "</tr>" );
	}

	/**	Return stored results.
	 *
	 *	@param	response	Servlet response object.
	 *	@param	results		Result string to return to client for display.
	 *	@param	title		Title for output.
	 */

	public void outputResults
	(
		HttpServletResponse response ,
		String results ,
		String title
	)
		throws java.io.IOException
	{
		java.io.PrintWriter out	= response.getWriter();

		if ( outputFullHTML ) outputHeader( out , title );

		out.println( results );

		if ( outputFullHTML ) outputFooter( out );

		out.flush();
		out.close();
	}

	/**	Remove HTML/XML tags from text.
	 *
	 *	@param	text	The text from which to remove tags.
	 *
	 *	@return			The text with tags removed.
	 */

	public String unTag( String text )
	{
		String result	= text.trim();

		if ( HTMLUtils.isHTMLTaggedText( result ) )
		{
			result	= HTMLUtils.stripHTMLTags( result );
		}

		result	= result.replaceAll( "\\s" , " " );

		return result;
	}

	/**	Handle request.
	 *
	 *	@param	request		Servlet request.
	 *	@param	response	Servlet response.
	 *
	 *	@return				Servlet results.
	 */

	protected ServletResult doHandleRequest
	(
		HttpServletRequest request ,
		HttpServletResponse response
	)
		throws ServletException, java.io.IOException
	{
		response.setContentType( "text/html; charset=utf-8" );
		request.setCharacterEncoding( "utf8" );

		return handleRequest( request , response );
	}

	/** Gets integer parameter value.
	 *
	 *	@param	requestValue	Parameter value from request.
	 *	@param	defaultValue	Default parameter value if parameter null
	 *							or invalid.
	 *
	 *	@return					The parameter value, or the defaultValue
	 *							if paramValue is null or invalid.
	 */

	public static int getIntValue
	(
		String requestValue ,
		int defaultValue
	)
	{
		int result	= defaultValue;

		if ( requestValue != null )
		{
			try
			{
				result	= Integer.parseInt( requestValue );
			}
			catch ( NumberFormatException e )
			{
				result	= defaultValue;
			}
		}

		return result;
	}

	/**	Handle request.  Must be overridden in subclass.
	 *
	 *	@param	request		Servlet request.
	 *	@param	response	Servlet response.
	 *
	 *	@return				Servlet results.
	 */

	protected abstract ServletResult handleRequest
	(
		HttpServletRequest request ,
		HttpServletResponse response
	)
		throws ServletException, java.io.IOException;
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



