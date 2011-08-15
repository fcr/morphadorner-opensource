package edu.northwestern.at.utils.spellcheck;

import java.io.*;
import java.util.*;

import edu.northwestern.at.utils.*;

/** TernaryTrieSpellingDictionary -- implements spelling dictionary using ternary trie.
 *
 *	<p>
 *	This class provides the basic methods for a spelling checker
 *	dictionary implemented using a ternary trie.
 *	</p>
 */

public class TernaryTrieSpellingDictionary implements SpellingDictionary
{
	/**	Holds the ternary search trie. */

	protected TernaryTrie trie;

	/**	Word list file which populates dictionary. */

	protected String wordListFileName;

	/**	Debugging flag.  True to debug. */

	protected boolean debug	= false;

	/**	Maximum edit distance. */

	protected final static int MAXDIFFS	= 2;

	/** Create TernaryTrieSpellingDictionary from a map containing words.
	 *
	 *	@param	wordsMap	Map with words to add to the dictionary.
	 */

	public TernaryTrieSpellingDictionary( Map<String, String> wordsMap )
	{
		this.wordListFileName	= "";

		try
		{
			this.trie	= new TernaryTrie( wordsMap , true );
		}
		catch ( Exception e )
		{
e.printStackTrace();
		}
	}

	/** Create TernaryTrieSpellingDictionary from a set containing words.
	 *
	 *	@param	wordsSet	Set with words to add to the dictionary.
	 */

	public TernaryTrieSpellingDictionary( Set<String> wordsSet )
	{
		this.wordListFileName	= "";

		try
		{
			this.trie	= new TernaryTrie( wordsSet );
		}
		catch ( Exception e )
		{
e.printStackTrace();
		}
	}

	/** Create TernaryTrieSpellingDictionary from a list containing words.
	 *
	 *	@param	wordsList	List with words to add to the dictionary.
	 */

	public TernaryTrieSpellingDictionary( List<String> wordsList )
	{
		this.wordListFileName	= "";

		try
		{
			this.trie	= new TernaryTrie( wordsList );
		}
		catch ( Exception e )
		{
e.printStackTrace();
		}
	}

	/** Create TernaryTrieSpellingDictionary from a tagged strings list.
	 *
	 *	@param	wordsList	Tagged strings list with words
	 *						to add to the dictionary.
	 */

	public TernaryTrieSpellingDictionary( TaggedStrings wordsList )
	{
		this.wordListFileName	= "";

		try
		{
			this.trie	= new TernaryTrie( wordsList , true );
		}
		catch ( Exception e )
		{
e.printStackTrace();
		}
	}

	/** Create TernaryTrieSpellingDictionary from an existing trie.
	 *
	 *	@param	trie	Trie to use for dictionary.
	 */

	public TernaryTrieSpellingDictionary( TernaryTrie trie )
	{
		this.trie	= trie;
	}

	/** Lookup word in dictionary.
	 *
	 *	@param	word	The word to lookup.
	 *
	 *	@return			True if the word was found in the
	 *					dictionary.
	 *
	 *	<p>
	 *	<strong>Note:</strong>
	 *	</p>
	 *
	 *	<p>
	 *	Any processing of the word (conversion to lower case, etc.)
	 *	should be done before calling this routine.
	 *	</p>
	 */

	public boolean lookupWord( String word )
	{
							// Consider null or empty word to be spelled
							// correctly.

		if ( ( word == null ) || ( word.length() <= 0 ) ) return true;

							//	Look for an exact match in the
							//	ternary trie for the lower-case
							//	version of the word.

		String lowerCaseWord	= word.toLowerCase();

		return trie.containsString( lowerCaseWord );
	}

	/** Add a word to the dictionary.
	 *
	 *	@param	word		The word to add to the dictionary.
	 *
	 *	@return				True if word added successfully.
	 */

	private boolean addWordPrivate( String word )
	{
		trie.put( word , new Double( 0 ) );
		return true;
	}

	/** Add a word to the dictionary.
	 *
	 *	@param	word		The word to add to the dictionary.
	 *
	 *	@return				True if word added successfully.
	 */

	public boolean addWord( String word )
	{
		return addWordPrivate( word );
	}

	/** Add multiple words to the dictionary.
	 *
	 *	@param	words		The words to add to the dictionary.
	 *
	 *	@return				True if all words added successfully.
	 */

	public boolean addWords( String[] words )
	{
		boolean result = true;

		for ( int i = 0; i < words.length; i++ )
		{
			boolean added	= addWord( words[ i ] );
			result			= result && added;
		}

		return result;
	}

	/** Get list of words which almost match given word.
	 *
	 *	@param	word		The word for which to find similar words.
	 *
	 *	@return				Set of words which are similar
	 *						to specified word.
	 */

	public Set<String> getRelatedWords( String word )
	{
		Set<String> result	= null;

		if ( ( word != null ) && ( word.length() > 0 ) )
		{
			result	= findMostSimilarSet( word );
		}

		return result;
	}

	public Set<String> findMostSimilarSet( String word )
	{
		Set<String> result	= SetFactory.createNewSet();
		int size			= word.length();

		if ( size == 0 ) return null;

		int maxSuggestions = 1;
		int i;
		int j;
		String suggestion;
		String suggestion2;
		String savedWord	= word;

								//	Start by looking for words with a
								//	maximum of MAXDIFFS letters different.

		for	(	maxSuggestions = 1;
				( maxSuggestions <= MAXDIFFS ) &&
				( size >= maxSuggestions * 2 );
				maxSuggestions++
			)
		{
			result.addAll
			(
				trie.nearSearch( word , maxSuggestions )
			);
		}
								//	Check for 1 letter removed or added,
								//	and 1 letter removed or added and
								//	1 different.

		if ( ( result.size() == 0 ) && ( size > 2 ) )
		{
			for	(	maxSuggestions = 0 ;
					( maxSuggestions <= 1 ) ;
					maxSuggestions++
				)
			{
				for ( i = size - 1 ; i >= 0 ; i-- )
				{
					result.addAll
					(
						trie.nearSearch
						(
							word.substring( 0 , i ) +
								word.substring( i + 1 , size ) ,
							maxSuggestions
					 	)
					);

					for ( j = 'a' ; j < 'z' ; j++ )
					{
						result.addAll
						(
							trie.nearSearch
							(
								word.substring( 0 , i ) + ((char)j ) +
									word.substring( i , size ) ,
								maxSuggestions
							)
						);
					}
				}
			}
		}

		if ( result.size() == 0 )
		{
			for ( i = j = 1 ; i < size ; i++ )
			{
				if ( word.charAt( i ) == word.charAt( i - 1 ) )
				{
					suggestion	=
						word.substring( 0 , j ) +
							word.substring( i + 1 , size );

					if ( trie.get( suggestion ) != null )
					{
						result.add( suggestion );
					}
				}
				else
				{
					j = i + 1;
				}
			}
		}

								//	Two consecutive letters exchanged
								//	and 1 character different.

		if ( result.size() == 0 )
		{
			StringBuffer sugBuf;

			for ( i = 0 ; i < size - 1 ; i++ )
			{
				sugBuf		= new StringBuffer( word );

				char ci		= sugBuf.charAt( i );
				char ci1	= sugBuf.charAt( i + 1 );

				sugBuf.setCharAt( i + 1 , ci );
				sugBuf.setCharAt( i , ci1 );

				suggestion	= sugBuf.toString();

				result.addAll( trie.nearSearch( suggestion , 1 ) );
			}
        }
								//	Prefixes.

		if ( result.size() == 0 )
		{
			result.addAll( trie.prefixSearch( word ) );
		}
								//	Repeated characters removed and
								//	1 character different.

		if ( result.size() == 0 )
		{
			for ( i = 1 ; i < size - 1 ; i++ )
			{
				if ( word.charAt( i ) == word.charAt( i - 1 ) )
				{
					suggestion	=
						word.substring( 0 , i ) +
						word.substring( i + 1 , size );

					result.addAll( trie.nearSearch( suggestion , 1 ) );
				}
			}
		}

		return result;
	}

	/** Retrieves all words in dictionary.
	 *
	 *	@return		ArrayList of all words in dictionary.
	 */

	public Set<String> getAllWords()
	{
		return trie.getAllStrings();
	}

	/**	Retrieves number of words in dictionary.
	 *
	 *	@return		Number of words in dictionary.
	 */

	public int getNumberOfWords()
	{
		return trie.getAllStrings().size();
	}

	/** Clear dictionary of all words and metaphone values. */

	public void clear()
	{
	}

	/** Get set of words which almost match given word.
	 *
	 *	@param	word		The word for which to find similar words.
	 *
	 *	@return				Set of words which are similar
	 *						to specified word.
	 */

	public Set<String> getMoreRelatedWords( String word )
	{
		Set<String> result	= SetFactory.createNewSet();

		if ( ( word != null ) && ( word.length() > 0 ) )
		{
			int l	= word.length();
			int k	= 0;

			for	( int i = ( MAXDIFFS + 1 ) ; i < Math.min( l / 2 , 10 ) ; i++ )
			{
				List<String> nearSearches	=
					trie.nearSearch( word.toLowerCase() , i );

				if ( nearSearches.size() > 0 )
				{
					result.addAll( nearSearches );
					k++;

					if ( k >= 2 ) break;
				}
			}
		}

		return result;
	}
}

