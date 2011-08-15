package edu.northwestern.at.utils.corpuslinguistics.lemmatizer;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.logger.*;
import edu.northwestern.at.utils.corpuslinguistics.lexicon.*;
import edu.northwestern.at.utils.corpuslinguistics.tokenizer.*;

/**	Abstract Lemmatizer.
 */

abstract public class AbstractLemmatizer
	extends IsCloseableObject
	implements Lemmatizer, UsesLogger
{
	/**	Default lemma separator is vertical bar character, */

	protected char lemmaSeparator			= '|';
	protected String lemmaSeparatorString	= "|";

	/**	Logger used for output. */

	protected Logger logger	= new DummyLogger();

	/**	The lexicon. */

	protected Lexicon lexicon	= null;

	/**	The dictionary. */

	protected Set<String> dictionary	= null;

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

	/**	Set the lexicon.
	 *
	 *	@param	lexicon		The lexicon.
	 */

	public void setLexicon( Lexicon lexicon )
	{
		this.lexicon	= lexicon;
	}

	/**	Set the dictionary for checking lemmata.
	 *
	 *	@param	dictionary		The dictionary as a string set.
	 *							May be null.
	 */

	public void setDictionary( Set<String> dictionary )
	{
		this.dictionary	= dictionary;
	}

	/**	Returns a lemma given a spelling.
	 *
	 *	@param	spelling	The spelling.
	 *
	 *	@return				The lemma.  "*" is returned if the lemma
	 *						cannot be found.
	 */

	abstract public String lemmatize( String spelling );

	/**	Returns a lemma given a spelling and a part of speech.
	 *
	 *	@param	spelling	The spelling.
	 *	@param	wordClass	The word class.
	 *
	 *	@return				The lemma.  "*" is returned if the lemma
	 *						cannot be found.
	 *
	 *	<p>
	 *	The word class should be a major word class as defined in
	 *	{@link edu.northwestern.at.utils.corpuslinguistics.partsofspeech.PartOfSpeech}.
	 *	</p>
	 */

	abstract public String lemmatize( String spelling , String wordClass );

	/**	Check for words that cannot be lemmatized.
	 *
	 *	@param	spelling	The spelling to be lemmatized.
	 *
	 *	@return				true if spelling is not a lemmatizable
	 *						word -- e.g., it contains punctuation,
	 *						is a number, or is a Roman numeral.
	 */

	public boolean cantLemmatize( String spelling )
	{
		boolean result	= true;

		if ( spelling != null )
		{
			String trimmedSpelling	= spelling.trim();

			result	=
				( trimmedSpelling.length() == 0 ) ||
				CharUtils.isAllHyphens( trimmedSpelling ) ||
				CharUtils.hasPunctuationNotApostrophes(
					trimmedSpelling ) ||
				CharUtils.hasSymbols( trimmedSpelling ) ||
				CharUtils.isNumber( trimmedSpelling ) ||
				RomanNumeralUtils.isLooseRomanNumeral(
					trimmedSpelling ) ||
				RomanNumeralUtils.isLooseOrdinalRomanNumeral(
					trimmedSpelling );
		}

		return result;
	}

	/**	Get the lemma separator string,
	 *
	 *	@return	String to separate lemmata in compound lemma.
	 */

	public String getLemmaSeparator()
	{
		return lemmaSeparatorString;
	}

	/**	Join separate lemmata into a compound lemma.
	 *
	 *	@param	lemmata	String array of lemmata.
	 *	@param	separator	String to separate lemmata.
	 *
	 *	@return				String containing joined lemmata.
	 *							The lemmata are separated by the
	 *							specified separator character.
	 */

	public String joinLemmata( String[] lemmata , String separator )
	{
		String result	= "";

		for ( int i = 0 ; i < lemmata.length ; i++ )
		{
			if ( i > 0 )
			{
				result	+= separator;
			}

			result	+=	lemmata[ i ];
		}

		return result;
	}

	/**	Join separate lemmata into a compound lemma.
	 *
	 *	@param	lemmata	String array of part of speech lemmas.
	 *
	 *	@return				String containing joined lemmata.
	 *						The lemmata are separated by the
	 *						default separator character.
	 */

	public String joinLemmata( String[] lemmata )
	{
		return joinLemmata( lemmata , lemmaSeparatorString );
	}

	/**	Split compound lemma into separate lemmata.
	 *
	 *	@param	lemma		The compound lemma.
	 *
	 *	@return				String array of lemmata.  Only one entry if
	 *						lemma is not a compound lemma.
	 */

	public String[] splitLemma( String lemma )
	{
		return StringUtils.makeTokenArray( lemma , lemmaSeparatorString );
	}

	/**	Check if lemma is compound lemma.
	 *
	 *	@param	lemma		The lemma.
	 *
	 *	@return					true if lemma is compound lemma.
	 */

	public boolean isCompoundLemma( String lemma )
	{
		return ( lemma.indexOf( lemmaSeparatorString ) >= 0 );
	}

	/**	Get number of lemmata comprising this lemma.
	 *
	 *	@param	lemma	The lemma.
	 *
	 *	@return			Count of individual lemmata
	 *					comprising this lemma.
	 */

	public int countLemmata( String lemma )
	{
								//	Most of the time the result will
								//	be one for one lemma.
		int result	= 1;
								//	If the lemma is just the lemma
								//	separator, it represents itself
								//	instead of being the separator.

		if	(	( lemma.length() == 1 ) &&
				( lemma.charAt( 0 ) == lemmaSeparator )
			)
		{
		}
								//	Otherwise count the number of lemma
								//	separators.
		else
		{
			for ( int i = 0 ; i < lemma.length() ; i++ )
			{
				if ( lemma.charAt( i ) == lemmaSeparator )
				{
					result++;
				}
			}
		}
								//	Return lemma separator count + 1
								//	as the count of lemmata.
		return result;
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



