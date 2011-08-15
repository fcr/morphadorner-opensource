package edu.northwestern.at.utils.corpuslinguistics.thesaurus;

/*	Please see the license information at the end of this file. */

import java.io.FileInputStream;

import java.io.*;
import java.util.*;

import edu.smu.tspell.wordnet.*;

import edu.northwestern.at.utils.SortedArrayList;

/** Implements a thesaurus using Wordnet.
 *
 *	<p>
 *	This uses the Jaws interface to WordNet written by Brett Spell.
 *	See http://engr.smu.edu/~tspell/ for details.
 *	</p>
 */

public class WordnetThesaurus
	extends AbstractThesaurus
	implements Thesaurus
{
	/**	Property specifying the location of the WordNet data directory.
	 */

	protected final static String DATABASE_DIRECTORY =
		"wordnet.database.dir";

	/**	Default data directory. */

	protected static String defaultDataDirectory	=
		"data/wordnet/3.0/dict";

	/**	WordNet data. */

	protected WordNetDatabase wordnetData;

	/** Create a WordNet object.
	 *
	 *	@throws	IOException
	 *	@throws	FileNotFoundException
	 */

	public WordnetThesaurus()
		throws IOException, FileNotFoundException
	{
								//	Get instance of Wordnet database.

		if ( getWordNetDataDirectory() == null )
		{
			setWordNetDataDirectory( defaultDataDirectory );
		}

		wordnetData	= WordNetDatabase.getFileInstance();
	}

	/** Create a WordNet object.
	 *
	 *	@param	dataDirectory	Data containing WordNet data files.
	 *
	 *	@throws	IOException
	 *	@throws	FileNotFoundException
	 */

	public WordnetThesaurus( String dataDirectory )
		throws IOException, FileNotFoundException
	{
								//	Get instance of Wordnet database.

		setWordNetDataDirectory( dataDirectory );

		wordnetData	= WordNetDatabase.getFileInstance();
	}

	/**	Set location of WordNet data files.
	 *
	 *	@param	wordNetDataDirectory	The WordNet data directory.
	 */

	protected void setWordNetDataDirectory
	(
		String wordNetDataDirectory
	)
	{
		System.setProperty( DATABASE_DIRECTORY , wordNetDataDirectory );
	}

	/**	Get location of WordNet data files.
	 *
	 *	@return		The WordNet data directory.
	 */

	protected String getWordNetDataDirectory()
	{
		return System.getProperty( DATABASE_DIRECTORY );
	}

	/**	Get synonyms.
	 *
	 *	@param	word		Word for which to find synonyms.
	 *
	 *	@return				String list containing synonyms.
	 */

	public List<String> getSynonyms( String word )
	{
		Set<String> synonyms	= new TreeSet<String>();

		Synset[] synsets		= wordnetData.getSynsets( word );

		for ( int i = 0 ; i < synsets.length ; i++ )
		{
			Synset synset		= synsets[ i ];
			String[] wordForms	= synset.getWordForms();

			for ( int j = 0 ; j < wordForms.length ; j++ )
			{
				synonyms.add( wordForms[ j ] );
			}
		}

		return new ArrayList<String>( synonyms );
	}

	/**	Get synonyms.
	 *
	 *	@param	word		Word for which to find synonyms.
	 *	@param	wordClass	Major word class.
	 *
	 *	@return				String list containing synonyms.
	 */

	public List<String> getSynonyms( String word , String wordClass )
	{
		Set<String> synonyms	= new TreeSet<String>();

		Synset[] synsets		= wordnetData.getSynsets( word );

		SynsetType wordClassSynsetType	=
			getWordClassSynsetType( wordClass );

		for ( int i = 0 ; i < synsets.length ; i++ )
		{
			Synset synset	= synsets[ i ];

			if	(	( wordClassSynsetType != null ) &&
			        ( wordClassSynsetType != synset.getType() )
				)
			{
				continue;
			}

			String[] wordForms	= synset.getWordForms();

			for ( int j = 0 ; j < wordForms.length ; j++ )
			{
				synonyms.add( wordForms[ j ] );
			}
		}

		return new ArrayList<String>( synonyms );
	}

	/**	Get antonyms.
	 *
	 *	@param	word		Word for which to find antonyms.
	 *
	 *	@return				String list containing antonyms.
	 */

	public List<String> getAntonyms( String word )
	{
		return getAntonyms( word , null );
	}

	/**	Get antonyms.
	 *
	 *	@param	word		Word for which to find antonyms.
	 *	@param	wordClass	Major word class.  Null for all word classes.
	 *
	 *	@return				String list containing antonyms.
	 */

	public List<String> getAntonyms( String word , String wordClass )
	{
		Set<String> antonyms	= new TreeSet<String>();

		Synset[] synsets		= wordnetData.getSynsets( word );

		SynsetType wordClassSynsetType	=
			getWordClassSynsetType( wordClass );

		for ( int i = 0 ; i < synsets.length ; i++ )
		{
			Synset synset			= synsets[ i ];
			SynsetType synsetType	= synsets[ i ].getType();

			if	(	( wordClassSynsetType != null ) &&
			        ( wordClassSynsetType != synsetType )
				)
			{
				continue;
			}

			WordSense[] wordSenses	= null;

			if ( synsetType == SynsetType.ADJECTIVE )
			{
				wordSenses	= ((AdjectiveSynset)synset).getAntonyms( word );
			}
			else if ( synsetType == SynsetType.ADVERB )
			{
				wordSenses	= ((AdverbSynset)synset).getAntonyms( word );
			}
			else if ( synsetType == SynsetType.NOUN )
			{
				wordSenses	= ((NounSynset)synset).getAntonyms( word );
			}
			else if ( synsetType == SynsetType.VERB )
			{
				wordSenses	= ((VerbSynset)synset).getAntonyms( word );
			}

			if ( wordSenses != null )
			{
				for ( int j = 0 ; j < wordSenses.length ; j++ )
				{
					antonyms.add( wordSenses[ j ].getWordForm() );
				}
			}
		}

		return new ArrayList<String>( antonyms );
	}

	/**	Convert word class to Wordnet word synset type.
	 *
	 *	@param	wordClass	Word class.  May be one of noun, verb,
	 *						adjective, adverb, or may be null.
	 *
	 *	@return				Wordnet synset type.
	 *						null if wordclass unrecognized or
	 *						word class was null.
	 */

	protected SynsetType getWordClassSynsetType( String wordClass )
	{
		SynsetType result	= null;

		if ( wordClass != null )
		{
			if ( wordClass.equals( "noun" ) )
			{
				result	= SynsetType.NOUN;
			}
			else if ( wordClass.equals( "verb" ) )
			{
				result	= SynsetType.VERB;
			}
			else if ( wordClass.equals( "adjective" ) )
			{
				result	= SynsetType.ADJECTIVE;
			}
			else if ( wordClass.equals( "adverb" ) )
			{
				result	= SynsetType.ADVERB;
			}
		}

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



