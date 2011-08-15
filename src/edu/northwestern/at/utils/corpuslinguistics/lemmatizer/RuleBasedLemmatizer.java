package edu.northwestern.at.utils.corpuslinguistics.lemmatizer;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.net.*;
import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.lemmatizer.*;
import edu.northwestern.at.utils.corpuslinguistics.lexicon.*;

/**	A rule-based lemmatizer. */

public class RuleBasedLemmatizer
	extends AbstractLemmatizer
	implements Lemmatizer
{
	/**	Irregular forms.
	 *
	 *	<p>
	 *	Irregular forms are stored in a HashMap2D.
	 *	The compound key consists of the word class and irregular word form,
	 *	and the value is the lemma.
	 *	</p>
	 */

	protected Map2D<String, String, String> irregularForms	=
		Map2DFactory.createNewMap2D();

	/**	Word classes of irregular forms.
	 */

	protected Set<String> irregularFormsWordClasses	= new TreeSet<String>();

	/**	Lemmatizing rules.
	 *
	 *	<p>
	 *	The rules are stored in a map with the word class as a key
	 *	and a list of LemmatizerRule entries as the value.
	 *	</p>
	 */

	protected Map<String,List<LemmatizerRule>> rules	=
		MapFactory.createNewMap();

	/**	Word classes covered by rules.
	 */

	protected Set<String> rulesWordClasses	= new TreeSet<String>();

	/**	Create a rule-based lemmatizer. */

	public RuleBasedLemmatizer()
		throws Exception
	{
	}

	/**	Loads lemmatization rules from a URL.
	 *
	 *	@param	url			URL containing lemmatization rules.
	 *	@param	encoding	Character set encoding for rules.
	 */

	public void loadRules
	(
		URL url ,
		String encoding
	)
		throws IOException
	{
		String line = null;
								//	Load rules.

		BufferedReader buffer =
			new BufferedReader
			(
				new UnicodeReader( url.openStream() , encoding )
			);

		String posTag	= "";
		String[] tokens	= new String[ 2 ];

		List<LemmatizerRule> rulesForTag	= ListFactory.createNewList();

		while ( ( line = buffer.readLine() ) != null )
		{
			line	= line.trim();

			if ( ( line.length() > 0 ) && ( line.charAt( 0 ) != '#' ) )
			{
				tokens	= StringUtils.makeTokenArray( line );

				if ( tokens.length > 0 )
				{
					int l	= tokens[ 0 ].length();

					if ( tokens[ 0 ].charAt( l - 1 ) == ':' )
					{
						if ( rulesForTag.size() > 0 )
						{
							rules.put( posTag , rulesForTag );

							rulesForTag	= ListFactory.createNewList();
						}

						posTag	= tokens[ 0 ].substring( 0 , l - 1 );

						rulesWordClasses.add( posTag );
					}
					else
					{
						rulesForTag.add( new DefaultLemmatizerRule( line ) );
					}
				}
			}
		}

		if ( rulesForTag.size() > 0 )
		{
			rules.put( posTag , rulesForTag );
		}

		buffer.close();
	}

	/**	Loads irregular forms from a URL.
	 *
	 *	@param	url			URL containing irregular forms.
	 *	@param	encoding	Character set encoding for irregular forms.
	 */

	public void loadIrregularForms
	(
		URL url ,
		String encoding
	)
		throws IOException
	{
		String line = null;
								//	Load irregular forms.

		BufferedReader buffer =
			new BufferedReader
			(
				new UnicodeReader( url.openStream() , encoding )
			);

		String posTag	= "";
		String lemma	= "";
		String[] tokens	= new String[ 2 ];

		while ( ( line = buffer.readLine() ) != null )
		{
			line	= line.trim();

			if ( ( line.length() > 0 ) && ( line.charAt( 0 ) != '#' ) )
			{
				tokens	= StringUtils.makeTokenArray( line );

				if ( tokens.length > 0 )
				{
					int l	= tokens[ 0 ].length();

					if ( tokens[ 0 ].charAt( l - 1 ) == ':' )
					{
						posTag	= tokens[ 0 ].substring( 0 , l - 1 );

						irregularFormsWordClasses.add( posTag );
					}
					else
					{
						if ( tokens.length > 1 )
						{
							lemma	= tokens[ 1 ];
						}
						else
						{
							lemma	= tokens[ 0 ];
						}

						irregularForms.put( posTag , tokens[ 0 ] , lemma );
					}
				}
			}
		}

		buffer.close();
	}

	/**	Returns a lemma given a word and a word class.
	 *
	 *	@param	spelling	The spelling.
	 *	@param	wordClass	The word class.  Ignored if null or empty.
	 *						May contain more than one word class
	 *						separated by commas, in which case
	 *						the lemma rules for each class are applied
	 *						in order.
	 *
	 *	@return		The lemma.  The spelling is returned when a
	 *				lemma cannot be found.
	 */

	public String lemmatize( String spelling , String wordClass )
	{
		if ( wordClass == null )
		{
			return lemmatize( spelling );
		}

		String lcWordClass	= wordClass.trim().toLowerCase();

		if ( wordClass.length() == 0 )
		{
			return lemmatize( spelling );
		}
								//	Don't bother trying to lemmatize
								//	abbreviations, punctuation, etc.

		if ( cantLemmatize( spelling ) )
		{
			return spelling;
		}

		String[] lcWordClasses	= lcWordClass.split( "," );

		String lcSpelling	= spelling.toLowerCase();

		String lemma	= irregularForms.get( lcWordClass , spelling );

		if ( lemma == null )
		{
			lemma	= irregularForms.get( lcWordClass , lcSpelling );
		}

		if ( lemma == null )
		{
			lemma	= lcSpelling;

			for ( int i = 0 ; i < lcWordClasses.length ; i++ )
			{
				List<LemmatizerRule> rulesForWordClass	=
					rules.get( lcWordClasses[ i ] );

				if	(	( rulesForWordClass != null ) &&
						( rulesForWordClass.size() > 0 )
					)
				{
					LemmatizerRule[] wordClassRules	=
						(LemmatizerRule[])rulesForWordClass.toArray(
							new LemmatizerRule[ rulesForWordClass.size() ] );

					for ( int j = 0 ; j < wordClassRules.length ; j++ )
					{
						String newLemma	=
							wordClassRules[ j ].apply( lemma , dictionary );

						if ( !newLemma.equals( lemma ) )
						{
							lemma	= newLemma;
							break;
						}
					}
				}
			}
		}

		return ( ( lemma == null ) || ( lemma.length() == 0 ) ) ?
			spelling : cleanUpLemma( lemma );
	}

	/**	Clean up lemma.
	 *
	 *	@param	lemma	The lemma to clean.
	 *
	 *	@return	The clean lemma.
	 *
	 *	<p>
	 *	A lemma may contain extraneous "!" characters added to ensure
	 *	a specific ending is retained.  The "!" marks are removed here.
	 *	</p>
	 */

	public String cleanUpLemma( String lemma )
	{
		return StringUtils.replaceAll( lemma , "!" , "" );
	}

	/**	Returns a lemma given a spelling.
	 *
	 *	@param	spelling	The spelling.
	 *
	 *	@return	The lemma.  The spelling is returned if a lemma
	 *				cannot be found.
	 */

	 public String lemmatize( String spelling )
	 {
		String result			= spelling;

								//	Don't bother trying to lemmatize
								//	abbreviations or numbers.

		if ( cantLemmatize( spelling ) )
		{
			return spelling;
		}
								//	Check all irregular forms first.

		for	(	Iterator<String> iterator	=
					irregularFormsWordClasses.iterator() ;
				iterator.hasNext() ; )
		{
			String wordClass	= iterator.next().toLowerCase();

			result				= irregularForms.get( wordClass , spelling );

			if ( result != null )
			{
				if ( !result.equals( spelling ) )
				{
					return cleanUpLemma( result );
				}
			}
		}
								//	Check all rules.

		String lemma	= spelling.toLowerCase();

		for	(	Iterator<String> iterator	= rulesWordClasses.iterator() ;
				iterator.hasNext() ; )
		{
			String wordClass	= iterator.next().toLowerCase();

			List<LemmatizerRule> rulesForWordClass	=
				rules.get( wordClass );

			if	(	( rulesForWordClass != null ) &&
					( rulesForWordClass.size() > 0 )
				)
			{
				LemmatizerRule[] wordClassRules	=
					(LemmatizerRule[])rulesForWordClass.toArray(
						new LemmatizerRule[ rulesForWordClass.size() ] );

				for ( int i = 0 ; i < wordClassRules.length ; i++ )
				{
					String newLemma	=
						wordClassRules[ i ].apply( lemma , dictionary );

					if ( !newLemma.equals( lemma ) )
					{
						return cleanUpLemma( newLemma );
					}
				}
			}
		}

		if ( ( result == null ) || ( result.length() == 0 ) )
		{
			result = spelling;
		}

	 	return cleanUpLemma( result );
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



