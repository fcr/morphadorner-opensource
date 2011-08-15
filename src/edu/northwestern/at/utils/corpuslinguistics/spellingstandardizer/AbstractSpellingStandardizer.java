package edu.northwestern.at.utils.corpuslinguistics.spellingstandardizer;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.net.*;
import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.lexicon.*;
import edu.northwestern.at.utils.logger.*;

/**	Abstract Spelling Standardizer.
 */

abstract public class AbstractSpellingStandardizer
	extends IsCloseableObject
	implements SpellingStandardizer, UsesLogger
{
	/**	The map with alternate spellings as keys and standard spellings
	 *	as values.
	 */

	protected TaggedStrings mappedSpellings	= null;

	/**	The set of standard spellings. */

	protected Set<String> standardSpellingSet	=
		SetFactory.createNewSet();

	/**	Irregular forms.
	 *
	 *	<p>
	 *	Spellings disambiguated by word class are stored in a HashMap2D.
	 *	The compound key consists of the word class and alternate spelling,
	 *	and the value is the standardized spelling.
	 *	</p>
	 */

	protected Map2D<String, String, String> spellingsByWordClass;

	/**	Word classes of alternate spellings.
	 */

	protected Set<String> alternateSpellingsWordClasses;

	/**	Path to list of irregular word forms. */

	protected static String defaultSpellingsByWordClassFileName =
		"resources/spellingsbywordclass.txt";

	/**	Logger used for output. */

	protected Logger logger	= new DummyLogger();

	/**	Lexicon associated with this standardizer.  May be null. */

	protected Lexicon lexicon;

	/**	Create abstract spelling standardizer. */

	public AbstractSpellingStandardizer()
	{
								//	Load default spellings by word class.
    	try
		{
			loadAlternativeSpellingsByWordClass
			(
				this.getClass().getResource
				(
					defaultSpellingsByWordClassFileName
				) ,
				"utf-8"
			);
		}
		catch ( Exception e )
		{
		}
	}

	/**	Load alternate to standard spellings by word class.
	 *
	 *	@param	spellingsURL	URL of alternative spellings by word class.
	 */

	public void loadAlternativeSpellingsByWordClass
	(
		URL spellingsURL ,
	 	String encoding
	)
		throws IOException
	{
		String line = null;
								//	Load irregular forms.

		BufferedReader buffer =
			new BufferedReader
			(
				new UnicodeReader
				(
					spellingsURL.openStream() ,
					encoding
				)
			);

		String wordClass	= "";
		String spelling		= "";

		String[] tokens		= new String[ 2 ];

		spellingsByWordClass			= Map2DFactory.createNewMap2D();
		alternateSpellingsWordClasses	= new TreeSet<String>();

		while ( ( line = buffer.readLine() ) != null )
		{
			tokens	= StringUtils.makeTokenArray( line );

			if ( tokens.length > 0 )
			{
				int l	= tokens[ 0 ].length();

				if ( tokens[ 0 ].charAt( l - 1 ) == ':' )
				{
					wordClass	= tokens[ 0 ].substring( 0 , l - 1 );

					alternateSpellingsWordClasses.add( wordClass );
				}
				else
				{
					if ( tokens.length > 1 )
					{
						spelling	= tokens[ 1 ];
					}
					else
					{
						spelling	= tokens[ 0 ];
					}

					spellingsByWordClass.put(
						wordClass , tokens[ 0 ] , spelling );

					if ( tokens[ 0 ].indexOf( "^" ) >= 0 )
					{
						addMappedSpelling
						(
							StringUtils.replaceAll
							(
								tokens[ 0 ] ,
								"^" ,
								CharUtils.CHAR_SUP_TEXT_MARKER_STRING
							) ,
							spelling
						);
					}
				}
			}
		}

		buffer.close();
	}

	/**	Loads alternate spellings from a URL.
	 *
	 *	@param	url			URL containing alternate spellings to
	 *						standard spellings mappings.
	 *	@param	encoding	Text encoding (utf-8, 8859_1, etc.).
	 *	@param	delimChars	Delimiter characters separating spelling pairs.
	 */

	public void loadAlternativeSpellings
	(
		URL url ,
		String encoding ,
		String delimChars
	)
		throws IOException
	{
		if ( url != null )
		{
			loadAlternativeSpellings
			(
				new UnicodeReader( url.openStream() , encoding ) ,
				delimChars
			);
		}
	}

	/**	Loads alternative spellings from a reader.
	 *
	 *	@param	reader		The reader.
	 *	@param	delimChars	Delimiter characters separating spelling pairs.
	 */

	public void loadAlternativeSpellings
	(
		Reader reader ,
		String delimChars
	)
		throws IOException
	{
		String[] tokens;

		BufferedReader bufferedReader	= new BufferedReader( reader );

		if ( mappedSpellings == null )
		{
			mappedSpellings		= new TernaryTrie();
		}

		String inputLine	= bufferedReader.readLine();

		while ( inputLine != null )
		{
			tokens		= inputLine.split( delimChars );

			if ( tokens.length > 1 )
			{
				tokens[ 0 ]	= tokens[ 0 ].trim();
				tokens[ 1 ]	= tokens[ 1 ].trim();

				addMappedSpelling( tokens[ 0 ] , tokens[ 1 ] );

				if ( tokens[ 0 ].indexOf( "^" ) >= 0 )
				{
					addMappedSpelling
					(
						StringUtils.replaceAll
						(
							tokens[ 0 ] ,
							"^" ,
							CharUtils.CHAR_SUP_TEXT_MARKER_STRING
						) ,
						tokens[ 1 ]
					);
				}
        	}

            inputLine	= bufferedReader.readLine();
		}

		bufferedReader.close();
	}

	/**	Loads standard spellings from a URL.
	 *
	 *	@param	url			URL containing standard spellings
	 *	@param	encoding	Character set encoding for spellings
	 */

	public void loadStandardSpellings
	(
		URL url ,
		String encoding
	)
		throws IOException
	{
		if ( url != null )
		{
			loadStandardSpellings
			(
				new UnicodeReader( url.openStream() , encoding )
			);
		}
	}

	/**	Loads standard spellings from a reader.
	 *
	 *	@param	reader		The reader.
	 */

	public void loadStandardSpellings
	(
		Reader reader
	)
		throws IOException
	{
		BufferedReader bufferedReader	= new BufferedReader( reader );

		String spelling	= bufferedReader.readLine();

		while ( spelling != null )
		{
			addStandardSpelling( spelling.trim() );

			spelling	= bufferedReader.readLine();
		}

		bufferedReader.close();
	}

	/**	Add a mapped spelling.
	 *
	 *	@param	alternateSpelling	The alternate spelling.
	 *	@param	standardSpelling	The corresponding standard spelling.
	 */

	public void addMappedSpelling
	(
		String alternateSpelling ,
		String standardSpelling
	)
	{
		if	(	( mappedSpellings != null ) &&
				( standardSpelling != null ) &&
				( standardSpelling.length() > 0 ) &&
				( alternateSpelling != null ) &&
				( alternateSpelling.length() > 0 )
			)
		{
			mappedSpellings.putTag(
				alternateSpelling , standardSpelling );

			mappedSpellings.putTag(
				alternateSpelling.toLowerCase() , standardSpelling );

			addStandardSpelling( standardSpelling );
		}
	}

	/**	Add a standard spelling.
	 *
	 *	@param	standardSpelling	A standard spelling.
	 */

	public void addStandardSpelling
	(
		String standardSpelling
	)
	{
		if	(	( standardSpelling != null ) &&
				( standardSpelling.length() > 0 )
			)
		{
			standardSpellingSet.add( standardSpelling );

			standardSpellingSet.add( standardSpelling.toLowerCase() );
		}
	}

	/**	Add standard spellings from a collection.
	 *
	 *	@param	standardSpellings	A collection of standard spellings.
	 */

	public void addStandardSpellings
	(
		Collection<String> standardSpellings
	)
	{
		Iterator<String> iterator	= standardSpellings.iterator();

		while ( iterator.hasNext() )
		{
			String spelling	= iterator.next();

			addStandardSpelling( spelling );
		}
	}

	/**	Cached a generated mapped spelling.
	 *
	 *	@param	alternateSpelling	The alternate spelling.
	 *	@param	standardSpelling	The corresponding standard spelling.
	 */

	public void addCachedSpelling
	(
		String alternateSpelling ,
		String standardSpelling
	)
	{
		if	(	( mappedSpellings != null ) &&
				( standardSpelling != null ) &&
				( standardSpelling.length() > 0 ) &&
				( alternateSpelling != null ) &&
				( alternateSpelling.length() > 0 )
			)
		{
			mappedSpellings.putTag(
				alternateSpelling , standardSpelling );

			mappedSpellings.putTag(
				alternateSpelling.toLowerCase() , standardSpelling );
		}
	}

	/**	Sets map which maps alternate spellings to standard spellings.
	 *
	 *	@param	mappedSpellings		Map with alternate spellings as keys
	 *							and standard spellings as values.
	 */

	public void setMappedSpellings( TaggedStrings mappedSpellings )
	{
		this.mappedSpellings	= mappedSpellings;
	}

	/**	Sets standard spellings.
	 *
	 *	@param	standardSpellings		Set of standard spellings.
	 */

	public void setStandardSpellings( Set<String> standardSpellings )
	{
		this.standardSpellingSet	= standardSpellings;
	}

	/**	Returns standard spellings given a spelling.
	 *
	 *	@param	spelling	The spelling.
	 *
	 *	@return				The standard spellings as an array of String.
	 *
	 *	<p>
	 *	If not spelling map is defined, the spelling is returned
	 *	unchanged.
	 *	</p>
	 */

	 public String[] standardizeSpelling( String spelling )
	 {
		String result				= spelling;
		String lowerCaseSpelling	= spelling.toLowerCase();

		if ( mappedSpellings != null )
		{
								//	Check if given spelling exists
								//	in spelling map.  If so, return
								//	associated standard spelling.

			if ( mappedSpellings.containsString( spelling ) )
		 	{
	 			result	= mappedSpellings.getTag( spelling );
		 	}
								//	Check if lower case form of given
								//	spelling exists in spelling map.
								//	If so, return associated standard
								//	spelling.

		 	else if	( mappedSpellings.containsString( lowerCaseSpelling ) )
	 		{
	 			result	= mappedSpellings.getTag( lowerCaseSpelling );
		 	}
		 						//	If spelling contains dashes,
		 						//	evict them and try looking up
		 						//	the resulting spelling in regular
		 						//	and lower case form.

			else if ( CharUtils.hasDash( spelling ) )
			{
				String spellingNoDashes	= CharUtils.evictDashes( spelling );

								//	Check if no-dashes spelling exists
								//	in spelling map.  If so, return
								//	associated standard spelling.

				if ( mappedSpellings.containsString( spellingNoDashes ) )
			 	{
	 				result	= mappedSpellings.getTag( spellingNoDashes );
		 		}
								//	Check if lower case form of no-dashes
								//	spelling exists in spelling map.
								//	If so, return associated standard
								//	spelling.

		 		else if	( mappedSpellings.containsString(
		 			spellingNoDashes.toLowerCase() ) )
	 			{
	 				result	=
	 					mappedSpellings.getTag(
	 						spellingNoDashes.toLowerCase() );
		 		}
			}
		}

		result	= fixCapitalization( spelling , result );

	 	return new String[]{ result };
	 }

	/**	Returns a standard spelling given a standard or alternate spelling.
	 *
	 *	@param	spelling	The spelling.
	 *	@param	wordClass	The major word class.
	 *
	 *	@return				The standard spelling.
	 */

	 public String standardizeSpelling( String spelling , String wordClass )
	 {
								//	Get lowercase form of spelling.

	 	String lcSpelling	= spelling.toLowerCase();

								//	See if we have a standard spelling
								//	defined for this word class.  Try
								//	original case first, then lower case.
		String result		=
			(String)spellingsByWordClass.get( wordClass , spelling );

		if ( result	== null )
		{
			result		=
				(String)spellingsByWordClass.get( wordClass , lcSpelling );
		}
								//	If not, get a list of suggested
								//	standard spellings without regard
								//	to word class.
		if ( result == null )
        {
		 	String[] suggestions	= standardizeSpelling( spelling );

								//	If we got any suggested spellings,
								//	choose the last (e.g., best).

			if ( suggestions.length > 0 )
			{
				result	= suggestions[ suggestions.length - 1 ];
			}
		}
                                //	No standard spelling found so far?
                                //	Return the original spelling.
		if ( result	== null )
		{
			result	= spelling;
		}

		return result;
	}

	 /** Returns number of alternate spellings.
	  *
	  *	@return		The number of alternate spellings.
	  */

	public int getNumberOfAlternateSpellings()
	{
		int	result	= 0;

		if ( mappedSpellings != null )
		{
			result	= mappedSpellings.getStringCount();
		}

		return result;
	}

	 /** Returns number of alternate spellings by word class.
	  *
	  *	@return		int array with two entries.
	  *				[0]	=	The number of alternate spellings word classes.
	  *				[1]	=	The number of alternate spellings in the
	  *						word classes.
	  */

	public int[] getNumberOfAlternateSpellingsByWordClass()
	{
		int[] result	= new int[ 2 ];

		result[ 0 ]		= 0;
		result[ 1 ]		= 0;

		if ( alternateSpellingsWordClasses != null )
		{
			result[ 0 ]	= alternateSpellingsWordClasses.size();
		}

		if ( spellingsByWordClass != null )
		{
			result[ 1 ]	= spellingsByWordClass.size();
		}

		return result;
	}

	 /** Returns number of standard spellings.
	  *
	  *	@return		The number of standard spellings.
	  */

	public int getNumberOfStandardSpellings()
	{
		int	result	= 0;

		if ( standardSpellingSet != null )
		{
			result	= standardSpellingSet.size();
		}

		return result;
	}

	/**	Return the mapped spellings.
	 *
	 *	@return		The spelling tagged strings with (alternate spelling,
	 *				standard spelling) pairs.  May be null if
	 *				this standardizer does not use such a map.
	 */

	public TaggedStrings getMappedSpellings()
	{
		return mappedSpellings;
	}

	/**	Return the standard spellings.
	 *
	 *	@return		The standard spellings as a Set.
	 *				May be null.
	 */

	public Set<String> getStandardSpellings()
	{
		return standardSpellingSet;
	}

	/**	Preprocess spelling.
	 *
	 *	@param	spelling	Spelling to preprocess.
	 *
	 *	@return				Preprocessed spelling.
	 *
	 *	<p>
	 *	By default, no preprocessing is applied; the original spelling
	 *	is returned unchanged.
	 *	</p>
	 */

	public String preprocessSpelling( String spelling )
	{
		return spelling;
	}

	/**	Fix capitalization of standardized spelling.
	 *
	 *	@param	spelling			The original spelling.
	 *	@param	standardSpelling	The candidate standard spelling.
	 *
	 *	@return						Standard spelling with initial
	 *								capitalization matching original
	 *								spelling.
	 */

	public String fixCapitalization
	(
		String spelling ,
		String standardSpelling
	)
	{
		return CharUtils.makeCaseMatch( standardSpelling , spelling );
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

	/**	Get the word lexicon.
	 *
	 *	@return		The static word lexicon.
	 */

	public Lexicon getLexicon()
	{
		return lexicon;
	}

	/**	Set the lexicon.
	 *
	 *	@param	lexicon		Lexicon used for tagging.
	 */

	public void setLexicon( Lexicon lexicon )
	{
		this.lexicon	= lexicon;
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



