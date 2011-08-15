package edu.northwestern.at.utils.corpuslinguistics.namestandardizer;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.net.*;
import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.lexicon.*;
import edu.northwestern.at.utils.corpuslinguistics.partsofspeech.*;
import edu.northwestern.at.utils.corpuslinguistics.stringsimilarity.*;
import edu.northwestern.at.utils.logger.*;

/**	Abstract Name Standardizer.
 */

abstract public class AbstractNameStandardizer
	extends IsCloseableObject
	implements NameStandardizer, UsesLogger
{
	/**	Proper name trie. */

	protected static TernaryTrie nameTrie;

	/**	Consonant trie. */

	protected static TernaryTrie consonantTrie;

	/**	Logger used for output. */

	protected Logger logger	= new DummyLogger();

	/**	Create abstract name matcher. */

	public AbstractNameStandardizer()
	{
								//	Create name trie.

		nameTrie		=  new TernaryTrie();

								//	Create consonant trie.

		consonantTrie	=  new TernaryTrie();
	}

	/**	Load name data from lexicon file.
	 *
	 *	@param	lexiconFileName		Lexicon containing name data.
	 */

	public void loadNames( String lexiconFileName )
		throws IOException
	{
		Lexicon lexicon	= new DefaultLexicon();

		lexicon.loadLexicon
		(
			new File( lexiconFileName ).toURI().toURL() , "utf-8"
		);

		loadNamesFromLexicon( lexicon );
	}

	/**	Load names from a lexicon.
	 *
	 *	@param	lexicon		The lexicon from which to load names.
	 */

	public void loadNamesFromLexicon( Lexicon lexicon )
		throws IOException
	{
		if ( lexicon != null )
		{
								//	Get parts of speech.

			PartOfSpeechTags posTags	= lexicon.getPartOfSpeechTags();

								//	Get singular and plural proper noun
								//	tags.

			String singularTag	= posTags.getSingularProperNounTag();
			String pluralTag	= posTags.getPluralProperNounTag();

								//	Get Lexicon entries.

			String[] entries	= lexicon.getEntries();

								//	Loop over lexicon entries.

			for ( int i = 0 ; i < entries.length ; i++ )
			{
								//	Get next lexicon entry.

				String entry	= entries[ i ];

								//	Get parts of speech for this entry.

				Set categories	= lexicon.getCategoriesForEntry( entry );

								//	If this word can be a singular
								//	or plural proper noun, add it to
								//	the name trie.

				if	(	categories.contains( singularTag ) ||
						categories.contains( pluralTag )
					)
				{
					String lcEntry	= entry.toLowerCase();

					nameTrie.put( lcEntry , lcEntry );

								//	Strip vowels from name and add
								//	it to the consonant trie.

					String noVowels	=
						StringUtils.stripChars( lcEntry , "aeiouy" );

					@SuppressWarnings("unchecked")
					Set<String> names	=
						(Set<String>)consonantTrie.get( noVowels );

					if ( names == null )
					{
						names	= new TreeSet<String>();
						names.add( lcEntry );
						consonantTrie.put( noVowels , names );
					}
    				else
    				{
						names.add( lcEntry );
    				}
				}
			}
		}
	}

	/**	Return number of names.
	 *
	 *	@return		Number of names in names trie.
	 */

	public int getNumberOfNames()
	{
		return nameTrie.size();
	}

	/**	Check if we should not standardize a name.
	 *
	 *	@param	properName	Name to check.
	 *
	 *	@return				True to avoid standardizing name.
	 *
	 *	<p>
	 *	Names that contain periods are not standardized by default.
	 *	</p>
	 */

	public boolean dontStandardize( String properName )
	{
		return ( properName.indexOf( "." ) >= 0 );
	}

	/**	Returns standardized proper name given a proper name.
	 *
	 *	@param	properName	The proper name.
	 *
	 *	@return				The standard proper name.
	 */

	public String standardizeProperName( String properName )
	{
								//	See if we should avoid
								//	standardizing this name.

		if ( dontStandardize( properName ) ) return properName;

		String result	= preprocessProperName( properName );
		String lcName	= result.toLowerCase();

								//	Find names "near" (in edit distance
								//	terms) to given name.

		List<String> nearNames	= nameTrie.nearSearch( lcName , 2 );

								//	Get string similarity scores for
								//	near names and add to sorted
								//	list of candidate names.

		SortedArrayList<ScoredString> scoredNames	=
			new SortedArrayList<ScoredString>();

		if ( nearNames.size() > 0 )
		{
			for ( int j = 0 ; j < nearNames.size() ; j++ )
			{
				double similarity	=
					LetterPairSimilarity.letterPairSimilarity
					(
						lcName ,
						nearNames.get( j )
					);

				scoredNames.add
				(
					new ScoredString( nearNames.get( j ) , similarity )
				);
			}
		}
								//	If we didn't get any candidate names,
								//	look for near consonant matches, and
								//	get similarity scores for those names.
		else
		{
			String noVowels	=
				StringUtils.stripChars( lcName , "aeiouy" );

			nearNames	=
				consonantTrie.nearSearch( noVowels , 3 );

			for ( int j = 0 ; j < nearNames.size() ; j++ )
			{
				@SuppressWarnings("unchecked")
				Set<String> nearNamesSet	=
					(Set<String>)consonantTrie.get( nearNames.get( j ) );

				for ( String name : nearNamesSet )
				{
					double similarity	=
						LetterPairSimilarity.letterPairSimilarity
						(
							lcName ,
							name
						);

					if ( similarity >= 0.75D )
					{
						scoredNames.add
						(
							new ScoredString( name , similarity )
						);
					}
				}
			}
		}
								//	Get the highest scoring candidate name,
								//	if any, or just return the original
								//	name if none found.

		if ( scoredNames.size() > 0 )
		{
			result	= scoredNames.get( 0 ).getString();
			result	= CharUtils.makeCaseMatch( result , properName );
		}

		return result;
	}

	/**	Preprocess proper name.
	 *
	 *	@param	properName	Proper name to preprocess.
	 *
	 *	@return				Preprocessed proper name.
	 *
	 *	<p>
	 *	By default, no preprocessing is applied; the original proper name
	 *	is returned unchanged.
	 *	</p>
	 */

	public String preprocessProperName( String properName )
	{
		return properName;
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



