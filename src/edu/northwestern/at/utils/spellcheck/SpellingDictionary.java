package edu.northwestern.at.utils.spellcheck;

import java.util.*;

/** SpellingDictionary -- interface for spelling checker dictionary.
 *
 *	<p>
 *	This interface defines methods for a spelling checker dicionary.
 *	The interface provides for looking up words in the dictionary
 *	and for adding a word to the dictionary.
 *	</p>
 */

public interface SpellingDictionary
{
	/** Lookup word in dictionary.
	 *
	 *	@param	word	The word to lookup.
	 *
	 *	@return			True if the word was found in the
	 *					dictionary.
	 */

	public boolean lookupWord( String word );

	/** Add a word to the dictionary.
	 *
	 *	@param	word		The word to add to the dictionary.
	 *
	 *	@return				True if word added successfully.
	 */

	public boolean addWord( String word );

	/** Add multiple words to the dictionary
	 *
	 *	@param	words		The words to add to the dictionary.
	 *
	 *	@return				True if all words added successfully.
	 */

	public boolean addWords( String[] words );

	/** Get related words.
	 *
	 *	@param	key		The key for the list of related words.
	 *					Typically this is a hash code like a Soundex
	 *					or Metaphone value.
	 *
	 *	@return			ArrayList of words related to the specified key.
	 */

	public Set<String> getRelatedWords( String key );

	/** Retrieves all words in dictionary.
	 *
	 *	@return		ArrayList of all words in dictionary.
	 */

	public Set<String> getAllWords();

	/**	Retrieves number of words in dictionary.
	 *
	 *	@return		Number of words in dictionary.
	 */

	public int getNumberOfWords();

	/** Clears the dictionary of all words. */

    public void clear();
}

