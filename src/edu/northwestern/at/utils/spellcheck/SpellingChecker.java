package edu.northwestern.at.utils.spellcheck;

/** SpellingChecker -- interface for spelling checkers.
 *
 *	<p>
 *	This interface defines methods useful when constructing
 *	a spelling checker in an application.  The interface
 *	provides for looking up words in two persistent dictionaries,
 *	a global dictionary shared among many users, and a
 *	local dictionary specific to an individual.  An individual
 *	use can also add words to a transient "ignore" cache which
 *	contains words which should be ignored during the course
 *	of spell checking a specific document.
 *	</p>
 *
 *	<p>
 *	A typical SpellingChecker implementation might store
 *	the dictionaries in a file or a database.  Usually some
 *	kind of hash code is stored along with each word in the
 *	dictionary to support the notion of spelling correction.
 *	For example, an English dictionary might store the
 *	soundex or metaphone code for each word in the dictionary
 *	to ease production of a list of correctly spelled alternatives
 *	to a misspelled word.
 *	</p>
 *
 *	<p>
 *	Assuming an implementation called "MySpellingChecker",
 *	a sample sequence to spelling check a word might go as
 *	follows.
 *	</p>
 *
 *	<p>
 *	<code>
 *	MySpellingChecker myChecker =
 *		new MySpellingChecker(
 *			new FileBasedSpellingDictionary( "global.english" ) ,
 *			new FileBasedSpellingDictionary( "user.english" ) );
 *		...
 *	if ( !myChecker.checkSpelling( someWord ) )
 *	{
 *		String[] suggestions = myChecker.suggest( someWord );
 *		// Show list of suggestions, etc.
 *	}
 *	</code>
 *	</p>
 */

public interface SpellingChecker
{
	/** Select global dictionary to use to check spelling.
	 *
	 *	@param	dictionary		Identifies global dictionary,
	 *							a class implementing the
	 *							SpellingDictionary interface.
	 *							If the name is null, no global
	 *							dictionary is used.
	 *
	 *	<p>
	 *	The global dictionary is usually shared among many users.
	 *	Typically it is created by a system administrator as a
	 *	shareable resource.
	 *	</p>
	 */

	public void useGlobalDictionary( SpellingDictionary dictionary );

	/** Select local dictionary to use to check spelling.
	 *
	 *	@param	dictionary		Identifies local dictionary,
	 *							a class implementing the
	 *							SpellingDictionary interface.
	 *							If the name is null, no local
	 *							dictionary is used.
	 *
	 *	<p>
	 *	The local dictionary is usually limited to access by
	 *	a single individual.  The local dictionary generally
	 *	contains words added by an individual while checking
	 *	the spelling of one or more documents.
	 *	</p>
	 */

	public void useLocalDictionary( SpellingDictionary dictionary );

	/** Check spelling of word.
	 *
	 *	@param	word	The word whose spelling should be checked.
	 *
	 *	@return			True if the word was found in the
	 *					global or local dictionaries.
	 */

	public boolean checkSpelling( String word );

	/** Suggest alternative words for misspelled item.
	 *
	 *	@param	word		The misspelled word for which
	 *						possible alternatives are desired.
	 *
	 *	@return				String array of correctly spelled
	 *						words which are similar in some sense
	 *						to the misspelled word.
	 *
	 *	<p>
	 *	The suggested words are typically words which are similar
	 *	in sound or spelling to the misspelled word.  For English,
	 *	an implementation might return all words with the same soundex
	 *	or metaphone code as the misspelled word.
	 *	</p>
	 */

	public String[] suggest( String word );

	/** Add a word to the local dictionary.
	 *
	 *	@param	word		The word to add to the local dictionary.
	 *
	 *	@return				True if word added successfully.
	 */

	public boolean addWordToLocalDictionary( String word );

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

	public boolean addWordToGlobalDictionary( String word );

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

	public boolean addWordToIgnoreList( String word );

	/** Empties the ignore list.
	 *
	 *	<p>
	 *	Removes all words from the ignore list.  Typically this is
	 *	invoked at the start of a spelling checker session for a
	 *	new document.
	 *	</p>
	 */

	public void emptyIgnoreList();
}

