package edu.northwestern.at.utils.corpuslinguistics.spellingstandardizer;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.spellcheck.*;
import edu.northwestern.at.utils.corpuslinguistics.phonetics.*;
import edu.northwestern.at.utils.corpuslinguistics.stringsimilarity.*;

/**	Gap Filler: Finds candidate words to match words with gaps.
 */

public class GapFiller
{
	/**	Ternary trie to hold word list to search. */

	protected TernaryTrie trie;

	/** Create GapFiller from a map containing words.
	 *
	 *	@param	wordsMap	Map with words to add to the filler.
	 */

	public GapFiller( Map<String, String> wordsMap )
	{
		try
		{
			this.trie	= new TernaryTrie( wordsMap , true );
		}
		catch ( Exception e )
		{
		}
	}

	/** Create GapFiller from a set containing words.
	 *
	 *	@param	wordsSet	Set with words to add to the filler.
	 */

	public GapFiller( Set<String> wordsSet )
	{
		try
		{
			this.trie	= new TernaryTrie( wordsSet );
		}
		catch ( Exception e )
		{
		}
	}

	/** Create GapFiller from a list containing words.
	 *
	 *	@param	wordsList	List with words to add to the filler.
	 */

	public GapFiller( List<String> wordsList )
	{
		try
		{
			this.trie	= new TernaryTrie( wordsList );
		}
		catch ( Exception e )
		{
		}
	}

	/** Create GapFiller from a tagged strings list.
	 *
	 *	@param	wordsList	Tagged strings list with words
	 *						to add to the filler.
	 */

	public GapFiller( TaggedStrings wordsList )
	{
		try
		{
			this.trie	= new TernaryTrie( wordsList , true );
		}
		catch ( Exception e )
		{
		}
	}

	/** Create GapFiller from an existing trie.
	 *
	 *	@param	trie	Trie.
	 */

	public GapFiller( TernaryTrie trie )
	{
		this.trie	= trie;
	}

	/** Add a word to the filler.
	 *
	 *	@param	word		The word to add to the filler.
	 */

	protected void addWordPrivate( String word )
	{
		trie.put( word , word );
	}

	/** Add a word to the filler.
	 *
	 *	@param	word		The word to add to the filler.
	 */

	public void addWord( String word )
	{
		addWordPrivate( word );
	}

	/** Add multiple words to the filler from string array.
	 *
	 *	@param	words		The words to add to the filler.
	 */

	public void addWords( String[] words )
	{
		for ( int i = 0; i < words.length; i++ )
		{
			addWord( words[ i ] );
		}
	}

	/** Add multiple words to the filler from string collection.
	 *
	 *	@param	words		The words to add to the filler.
	 */

	public void addWords( Collection<String> words )
	{
		Iterator<String> iterator	= words.iterator();

		while ( iterator.hasNext() )
		{
			addWord( iterator.next() );
		}
	}

	/**	Get list of candidate words matching a word with oen or more gaps.
	 *
	 *	@param	word		Word with gaps.
	 *	@param	gapChar	Gap marker character.
	 *
	 *	@return					String list of candidate matching words.
	 *							May be empty if no matching words.
	 */

	public List<String> getMatchingWords( String word , char gapChar )
	{
		List<String> result	= ListFactory.createNewList();

		if ( trie != null )
		{
			String dotWord	= word.replaceAll( gapChar + "" , "." );

			result.addAll( trie.partialSearch( dotWord ) );

			Iterator<String> iterator	= result.iterator();

			while ( iterator.hasNext() )
			{
				if ( iterator.next().indexOf( "~" ) >= 0 )
				{
					iterator.remove();
				}
			}
		}

		return result;
	}

	/**	Get list of candidate words matching a word with one or more gaps.
	 *
	 *	@param	word	Word with gaps.
	 *
	 *	@return				String list of candidate matching words.
	 *						May be empty if no matching words.
	 *
	 *	<p>
	 *	Assumes either the Unicode blackcircle or the
	 *	solid circle are gap character markers.
	 *	</p>
	 */

	public List<String> getMatchingWords( String word )
	{
		return getMatchingWords
		(
			word ,
			CharUtils.CHAR_GAP_MARKER
		);
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



