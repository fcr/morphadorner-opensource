package edu.northwestern.at.utils.spellcheck;

import java.io.*;
import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.phonetics.*;
import edu.northwestern.at.utils.corpuslinguistics.stringsimilarity.*;

/** TernaryTrieBasedSpellingChecker -- A very simple minded spelling checker.
 *
 *	<p>
 *	Implements a very simple minded spelling checker using HashMaps
 *	to hold the dictionaries.  The dictionaries are read as combined
 *	word lists and metaphone codes from text files into two hashmaps.
 *	One hashmap contains each word as a key and has the metaphone code
 *	for that word as a value.  The second hashmap uses the metaphone values
 *	as keys and the list of words mapping to that metaphone as values.
 *	This allows presentation of suggested spellings for misspelled words.
 *	The list of suggestions may optionally be pruned by using
 *	a measure of the Levenstein distance between the original
 *	misspelling and each suggested spelling.
 *	</p>
 *
 *	<p>
 *	Words beginning with a digit are assumed to be numbers and
 *	therefore spelled correctly.
 *	</p>
 *
 *	<p>
 *	When creating a list of suggested replacements for a misspelled
 *	word, the first letter of the suggestions is capitalized if
 *	the first letter of the original misspelled word is capitalized.
 *	</p>
 *
 *	<p>
 *	This class is not intended for production use because of
 *	the time needed to load and save dictionaries and the amount
 *	of memory used to hold the dictionaries in memory. This class
 *	serves as a reference implementation for the SpellingChecker
 *	interface as well as a testbed for applications needing a
 *	spelling checker during development.
 *	</p>
 */

public class TernaryTrieBasedSpellingChecker implements SpellingChecker
{
	/** The global dictionary. */

	protected SpellingDictionary globalDictionary = null;

	/** The local dictionary. */

	protected SpellingDictionary localDictionary = null;

	/** Ignore list. */

	protected SpellingDictionary ignoreList = new HashMapSpellingDictionary();

	/** Create spelling checker specifying global and local dictionaries.
	 */

	public TernaryTrieBasedSpellingChecker
	(
		SpellingDictionary globalDictionary ,
		SpellingDictionary localDictionary
	)
	{
		this.globalDictionary	= globalDictionary;
		this.localDictionary	= localDictionary;
	}

	/** Create spelling checker specifying global dictionary.
	 */

	public TernaryTrieBasedSpellingChecker
	(
		SpellingDictionary globalDictionary
	)
	{
		this.globalDictionary	= globalDictionary;
		this.localDictionary	=  null;
	}

	/** Create spelling checked without loading any dictionaries. */

	public TernaryTrieBasedSpellingChecker()
	{
		this.globalDictionary	= null;
		this.localDictionary	= null;
	}

	/** Select global dictionary to use to check spelling.
	 *
	 *	@param	dictionary		Identifies dictionary.
	 *							The dictionary class must implement
	 *							the SpellingDictionary interface.
	 *
	 *	<p>
	 *	The global dictionary is usually shared among many users.
	 *	Typically it is created by a system administrator as a
	 *	shareable resource.
	 *	</p>
	 */

	public void useGlobalDictionary( SpellingDictionary dictionary )
	{
		this.globalDictionary = dictionary;
	}

	/** Select local dictionary to use to check spelling.
	 *
	 *	@param	dictionary		The dictionary to use.
	 *							If the name is null, no local
	 *							dictionary is used.
	 *							The dictionary class must implement
	 *							the SpellingDictionary interface.
	 *
	 *	<p>
	 *	The local dictionary is usually limited to access by
	 *	a single individual.  The local dictionary generally
	 *	contains words added by an individual while checking
	 *	the spelling of one or more documents.
	 *	</p>
	 */

	public void useLocalDictionary( SpellingDictionary dictionary )
	{
		this.localDictionary = dictionary;
	}

	/** Add word to a dictionary.
	 *
	 *	@param	word				The word to add.
	 *	@param	dictionary			The dictionary.
	 *
	 *	@return						True if word added.
	 */

	private boolean addWordToDictionary
	(
		String word ,
		SpellingDictionary dictionary
	)
	{
        return dictionary.addWord( word );
	}

	/** Check spelling of word.
	 *
	 *	@param	word		The word whose spelling should be checked.
	 *
	 *	@return				True if the word was found in the
	 *						global or local dictionaries.
	 */

	public boolean checkSpelling( String word )
	{
							// Consider null or empty word to be spelled
							// correctly.

		if ( ( word == null ) || ( word.length() <= 0 ) ) return true;

							// Assume word which is all upper case to be
							// spelled correctly.

		if ( word.equals( word.toUpperCase() ) ) return true;

                            // If word starts with a digit,
                            // assume it is a number and
                            // say it is spelled OK.

		if ( Character.isDigit( word.charAt( 0 ) ) )
			return true;
							// Convert word to lower case for
							// dictionary lookups.

		String lowerCaseWord = word.toLowerCase();

							// Otherwise look up word in
							// ignores list, local dictionary,
							// and finally, global dictionary.

		return	ignoreList.lookupWord( lowerCaseWord ) ||
				( ( localDictionary == null ) ?
					false : localDictionary.lookupWord( lowerCaseWord ) ) ||
				( ( globalDictionary == null ) ?
					false : globalDictionary.lookupWord( lowerCaseWord ) );
	}

	/** Prunes list of suggestions to those most like the original word.
	 *
	 *	@param	word		The original misspelled word.
	 *	@param	suggestions	List of suggested spellings.
	 *
	 *	@return				Possibly pruned list of suggested words.
	 *						The suggestions are pruned using the
	 * 						Levenstein distance.
	 */

	private List<String> pruneSuggestions
	(
		String word ,
		List<String> suggestions
	)
	{
		int nSuggestions = suggestions.size();

		List<String> result = ListFactory.createNewList( nSuggestions );

		for ( int i = 0; i < nSuggestions; i++ )
		{
			String suggestedWord = suggestions.get( i );

			if ( LevensteinDistance.areAlike( word , suggestedWord ) )
			{
				result.add( suggestedWord );
			}
		}

		return result;
	}

	/** Adds list of words to a Map.
	 *
	 *	@param	map			The map.
	 *	@param	words		The arraylist of words to add to map.
	 */

	private void addWords( Map<String, Integer> map , List<String> words )
	{
		if ( ( words == null ) || ( words.size() == 0 ) ) return;

		for ( int i = 0; i < words.size(); i++ )
		{
			String word = words.get( i );

			if ( !map.containsKey( word ) )
			{
				map.put( word , new Integer( 1 ) );
			}
		}
	}

	/** Suggest alternative words for misspelled item.
	 *
	 *	@param	word		The misspelled word for which
	 *						possible alternatives are desired.
	 *
	 *	@param	prune		True to prune suggestions using
	 *						Levenstein distance.
	 *
	 *	@return				Sorted array of correctly spelled
	 *						words which are similar in some sense
	 *						to the misspelled word.
	 *
	 *	<p>
	 *	The suggested words are typically words which are similar
	 *	in sound or spelling to the misspelled word.  If the misspelled
	 *	word begins with a capital letter, the suggestions are also
	 *	capitalized to match.
	 *	</p>
	 *
	 */

	public String[] suggest( String word , boolean prune )
	{
							// Get suggestions from dictionary.

		Set<String> suggestions	= globalDictionary.getRelatedWords( word );

		if ( localDictionary != null )
		{
			suggestions.addAll( localDictionary.getRelatedWords( word ) );
		}

		return (String[])suggestions.toArray(
			new String[ suggestions.size() ] );
	}

	public String[] suggestMore( String word , boolean prune )
	{
							// Convert word to lower case for
							// dictionary lookups.

		Set<String> suggestions	=
			((TernaryTrieSpellingDictionary)globalDictionary).getMoreRelatedWords( word );

		if ( localDictionary != null )
		{
			suggestions.addAll
			(
				((TernaryTrieSpellingDictionary)localDictionary).getMoreRelatedWords( word )
			);
		}

		return (String[])suggestions.toArray( new String[]{} );
	}

	/** Suggest alternative words for misspelled item.
	 *
	 *	@param	word		The misspelled word for which
	 *						possible alternatives are desired.
	 *
	 *	<p>
	 *	The suggested words are typically words which are similar
	 *	in sound or spelling to the misspelled word.
	 *	</p>
	 *
	 */

	public String[] suggest( String word )
	{
		return suggest( word , false );
	}

	public String[] suggestMore( String word )
	{
		return suggestMore( word , false );
	}

	/** Add a word to the local dictionary.
	 *
	 *	@param	word		The word to add to the local dictionary.
	 *
	 *	@return				True if word added successfully.
	 */

	public boolean addWordToLocalDictionary( String word )
	{
		if ( localDictionary != null )
			return localDictionary.addWord( word );
		else
			return false;
	}

	/** Add a word to the global dictionary.
	 *
	 *	@param	word		The word to add to the global dictionary.
	 *
	 *	@return				True if word added successfully.
	 *
	 *	<p>
	 *	Typically this function is retricted to system administrators.
	 *	</p>
	 */

	public boolean addWordToGlobalDictionary( String word )
	{
//		return globalDictionary.addWord( word );
		return false;
	}

	/** Add word to ignore list.
	 *
	 *	@param	word	Word to ignore.
	 *
	 *	@return			True if word successfully added to ignore list.
	 *
	 *	<p>
	 *	The ignore list contains words which an individual marks as ignorable
	 *	while checking the spelling of one of more documents.  The ignore
	 *	list is transient.  If an ignored word is to persist across multiple
	 *	spelling check sessions, the word should be added to the local
	 *	dictionary.
	 *	</p>
	 */

	public boolean addWordToIgnoreList( String word )
	{
		return ignoreList.addWord( word );
	}

	/** Empties the ignore list.
	 *
	 *	<p>
	 *	Removes all words from the ignore list.  Typically this is
	 *	invoked at the start of a spelling checker session for a
	 *	new document.
	 *	</p>
	 */

	public void emptyIgnoreList()
	{
		ignoreList.clear();
	}
}

