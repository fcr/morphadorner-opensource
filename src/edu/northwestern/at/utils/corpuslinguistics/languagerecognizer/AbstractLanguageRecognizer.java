package edu.northwestern.at.utils.corpuslinguistics.languagerecognizer;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.net.*;
import java.util.*;

import de.spieleck.app.cngram.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.logger.*;

/**	Abstract Language Recognizer.
 */

abstract public class AbstractLanguageRecognizer
	extends IsCloseableObject
	implements LanguageRecognizer, UsesLogger
{
	/** Holds NGram profiles. */

	protected NGramProfiles nps;
	protected NGramProfiles.Ranker ranker;
	protected NGramProfiles.RankResult rankResult;

	/**	Logger used for output. */

	protected Logger logger;

	/**	Create a language recognizer.
	 */

	public AbstractLanguageRecognizer()
	{
		try
		{
			nps			= new NGramProfiles();
			ranker		= nps.getRanker();
			logger		= new DummyLogger();
		}
		catch ( Exception e )
		{
		}
	}

	/**	Create a language recognizer with list of languages to recognize.
	 *
	 *	@param	languages	List of names of languages to recognize.
	 *
	 *	<p>
	 *	The list of languages references the fingerprint entry names.
	 *	Specify each language without the ".ngp" extension, e.g.,
	 *	use "italian" for Italian.
	 *	</p>
	 */

	public AbstractLanguageRecognizer( List<String> languages )
	{
		try
		{
			nps			= new NGramProfiles( languages );
			ranker		= nps.getRanker();
			logger		= new DummyLogger();
		}
		catch ( Exception e )
		{
		}
	}

	/**	Get the logger.
	 *
	 *	@return		The logger.
	 */

	public Logger getLogger()
	{
		return logger;
	}

	/**	Set the logger.
	 *
	 *	@param	logger		The logger.
	 */

	public void setLogger( Logger logger )
	{
		this.logger	= logger;
	}

	/**	Returns a scored list of possible languages for a text string.
	 *
	 *	@param	text	The text for which to determine the language.
	 *
	 *	@return			Array of ScoredList entries of language names and
	 *					scores sorted in descending order by score.
	 *					Null if language cannot be determined.
	 */

	 public ScoredString[] recognizeLanguage( String text )
	 {
	 	ScoredString[] result	= null;

		List<ScoredString> resultList	=
			ListFactory.createNewList();

		try
		{
		 	StringReader reader	= new StringReader( text );

			ranker.reset();
			ranker.account( reader );

			rankResult	= ranker.getRankResult();

			for ( int i = 0 ; i < rankResult.getLength() ; i++ )
        	{
            	if ( rankResult.getScore( i ) > 0.0 )
            	{
					resultList.add
					(
						new ScoredString
						(
							rankResult.getName( i ) ,
							rankResult.getScore( i )
						)
					);
				}
        	}

			result	=
				(ScoredString[])resultList.toArray
				(
					new ScoredString[ resultList.size() ]
				);
		}
		catch ( IOException e )
		{
			result	= null;
		}

		return result;
	 }

	/** Close the language recognizer.
	 */

	public void close()
	{
		rankResult	= null;
		ranker		= null;
		nps			= null;

		System.gc();
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



