package edu.northwestern.at.utils.corpuslinguistics.postagger.guesser;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.cache.*;
import edu.northwestern.at.utils.corpuslinguistics.lexicon.*;
import edu.northwestern.at.utils.corpuslinguistics.namerecognizer.*;
import edu.northwestern.at.utils.corpuslinguistics.partsofspeech.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.*;
import edu.northwestern.at.utils.corpuslinguistics.spellingstandardizer.*;
import edu.northwestern.at.utils.corpuslinguistics.tokenizer.*;
import edu.northwestern.at.utils.logger.*;

/**	Abstract PartOfSpeechGuesser.
 *
 *	<p>
 *	A part of speech guesser "guesses" the probable part(s) of speech
 *	for a word which does not appear in the main lexicon.  Alternate
 *	spellings, lexical rules based upon word prefixes or suffixes,
 *	and many other approaches may be used to find potential
 *	part of speech.  This AbstractPartOfSpeechGuesser holds the
 *	references to a word lexicon, suffix lexicon, and spelling
 *	standardizer.  Subclasses must override the abstract method
 *	guessPartsOfSpeech.
 *	</p>
 *
 *	<p>
 *	Some of the heuristics here only work reliably for English
 *	language text.
 *	</p>
 */

abstract public class AbstractPartOfSpeechGuesser
	extends IsCloseableObject
	implements PartOfSpeechGuesser, UsesLogger
{
	/**	True to enable debugging output. */

	protected boolean debug = true;

	/**	Logger used for output. */

	protected Logger logger	= new DummyLogger();

	/**	Cache parts of speech for unknown words.
	 *
	 *	<p>
	 *	The key is the word spelling, the value is a map of
	 *	parts of speech and associated counts for the spelling.
	 *	</p>
	 */

	protected Cache<String, Map<String, MutableInteger>> cachedWords	=
		new LRUCache<String, Map<String, MutableInteger>>( 2000 );

	/**	Cache lexicon for unknown words.
	 *
	 *	<p>
	 *	The key is the word spelling, the value is the lexicon to use
	 *	to retrieve counts for probability calculations.
	 *	Normally a cache entry is only created when the
	 *	lexicon is the suffix lexicon.  The word lexicon is
	 *	assumed by default otherwise.
	 *	</p>
	 */

	protected Map<String, Lexicon> cachedLexicons	=
		MapFactory.createNewMap();

	/**	The word lexicon.
	 */

	protected Lexicon wordLexicon;

	/**	The affix/suffix lexicon.
	 */

	protected Lexicon suffixLexicon;

	/**	The principal spelling standardizer.
	 */

	protected SpellingStandardizer spellingStandardizer;

	/**	The auxiliary spelling standardizer.
	 */

	protected SpellingStandardizer auxiliarySpellingStandardizer	=
		new ExtendedSimpleSpellingStandardizer();

	/**	Auxiliary word lists. */

	protected List<TaggedStrings> auxiliaryWordLists	=
		ListFactory.createNewList();

	/**	Proper names. */

	protected Names names	= new Names();

	/**	Try standardized spellings when guessing parts of speech. */

	protected boolean tryStandardSpellings	= true;

	/**	Check for possessives of known nouns when guessing parts of speech.
	 */

	protected boolean checkPossessives		= false;

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

	/**	Get spelling standardizer.
	 *
	 *	@return		The spelling standardizer.
	 */

	public SpellingStandardizer getSpellingStandardizer()
	{
		return spellingStandardizer;
	}

	/**	Set spelling standardizer.
	 *
	 *	@param	spellingStandardizer	The spelling standardizer.
	 */

	public void setSpellingStandardizer
	(
		SpellingStandardizer spellingStandardizer
	)
	{
		this.spellingStandardizer	= spellingStandardizer;

		this.auxiliarySpellingStandardizer.setMappedSpellings(
			spellingStandardizer.getMappedSpellings() );
	}

	/**	Get the word lexicon.
	 *
	 *	@return		The word lexicon.
	 */

	public Lexicon getWordLexicon()
	{
		return wordLexicon;
	}

	/**	Set the word lexicon.
	 *
	 *	@param	wordLexicon		The word lexicon.
	 */

	public void setWordLexicon( Lexicon wordLexicon )
	{
		this.wordLexicon	= wordLexicon;
	}

	/**	Get the suffix lexicon.
	 *
	 *	@return		The suffix lexicon.
	 */

	public Lexicon getSuffixLexicon()
	{
		return suffixLexicon;
	}

	/**	Set the suffix lexicon.
	 *
	 *	@param	suffixLexicon	The suffix lexicon.
	 */

	public void setSuffixLexicon( Lexicon suffixLexicon )
	{
		this.suffixLexicon	= suffixLexicon;
	}

	/**	Add word to cache.
	 *
	 *	@param	word	The word.
	 *	@param	tagMap	Tag map for the word.
	 */

	protected void addCachedWord
	(
		String word ,
		Map<String, MutableInteger> tagMap
	)
	{
		cachedWords.put( word , tagMap );
	}

	/**	Create map with one (pos, count) entry.
	 *
	 *	@param	posTag	The part of speech tag.
	 *
	 *	@return			The map.
	 *
	 *	<p>
	 *	The count associated with the part of speech tag
	 *	is the count of that tag in the word lexicon.
	 *	</p>
	 */

	protected Map<String, MutableInteger> posTagToMap( String posTag )
	{
		Map<String, MutableInteger> result	=
			MapFactory.createNewMap();

		int count	=
			Math.max( 1 , wordLexicon.getCategoryCount( posTag ) );

		result.put( posTag , new MutableInteger( count ) );

		return result;
	}

	/**	Create map from array of part of speech tags.
	 *
	 *	@param	posTags		The part of speech tags.
	 *
	 *	@return				The map with tags as keys and counts as values.
	 *
	 *	<p>
	 *	The count associated with the part of speech tags
	 *	is the count for each tag in the word lexicon.
	 *	This is probably not the best choice but absent any
	 *	other information, it is at least consistent.
	 *	</p>
	 *
	 */

	protected Map<String, MutableInteger> posTagsToMap( String[] posTags )
	{
		Map<String, MutableInteger> result	=
			MapFactory.createNewMap();

		for ( int i = 0 ; i < posTags.length ; i++ )
		{
			int count	=
				Math.max( 1 , wordLexicon.getCategoryCount( posTags[ i ] ) );

			result.put( posTags[ i ] , new MutableInteger( count ) );
		}

		return result;
	}

	/**	Clone pos tag map.
	 *
	 *	@param	posTagMap	The pos tag map to clone.
	 *
	 *	@return				The cloned map.
	 */

	protected Map<String, MutableInteger> clonePosTagMap
	(
		Map<String, MutableInteger> posTagMap
	)
	{
		Map<String, MutableInteger> result	=	null;

		if ( posTagMap != null )
		{
			result	= MapFactory.createNewMap( posTagMap.size() );

			for ( String tag: posTagMap.keySet() )
			{
				MutableInteger count	= posTagMap.get( tag );

				result.put
				(
					new String( tag ) ,
					new MutableInteger( count.intValue() )
				);
			}
		}

		return result;
	}

	/**	Add an auxiliary word list.
	 */

	public void addAuxiliaryWordList( TaggedStrings wordList )
	{
		if ( ( wordList != null ) && ( wordList.getStringCount() > 0 ) )
		{
			auxiliaryWordLists.add( wordList );
		}
	}

	/**	Get auxiliary word lists.
	 */

	public List<TaggedStrings> getAuxiliaryWordLists()
	{
		return auxiliaryWordLists;
	}

	/**	Get cached lexicon for a word.
	 *
	 *	@param		word	The word.
	 *
	 *	@return		The lexicon.
	 *
	 *	<p>
	 *	Most words do not have an associated cached lexicon,
	 *	so the word lexicon is returned.  Words whose
	 *	category counts result from a suffix analysis will
	 *	have a cached entry pointing to the suffix lexicon.
	 *	</p>
	 */

	public Lexicon getCachedLexiconForWord( String word )
	{
		Lexicon result	= cachedLexicons.get( word );

		if ( result == null )
		{
			result	= wordLexicon;
		}

		return result;
	}

	/**	See if we have part of speech for a cached word.
	 *
	 *	@param	word			The word.
	 *
	 *	@return					Guessed parts of speech tags
	 *							if word found in cache, else null.
	 */

	public Map<String, MutableInteger> checkCachedWord( String word )
	{
		Map<String, MutableInteger>	result	= null;

								//	Is word in cache?  Return
								//	existing parts of speech if so.

		if ( cachedWords.containsKey( word ) )
		{
			result	= cachedWords.get( word );
		}
		else
		{
								//	Get lower case for word.

			String lowerCaseWord	= word.toLowerCase();

								//	Is lower case word in cache?
								//	Return existing parts of speech
								//	if so.

			if ( cachedWords.containsKey( lowerCaseWord ) )
			{
				result	= cachedWords.get( lowerCaseWord );
			}
		}
								//	Return null if word not found
								//	in cache.
		return result;
	}

	/**	See if word is a name.
	 *
	 *	@param	word				The word.
	 *
	 *	@return						Guessed parts of speech
	 *								if word is a name, else null.
	 *
	 *	<p>
	 *	Note:  Only capitalized versions of names are considered.
	 *	</p>
	 */

	public Map<String, MutableInteger> checkName( String word )
	{
	 	Map<String, MutableInteger> result	= null;

								//	See if word is a proper name.

		if ( names.isNameOrPlace( word ) )
		{
			result	=
				posTagToMap
				(
					wordLexicon.getPartOfSpeechTags().
						getSingularProperNounTag()
				);

			addCachedWord( word , result );
		}
		else if ( CharUtils.isAllCaps( word ) )
		{
			String fixedWord	=
				CharUtils.capitalizeFirstLetter( word );

			if ( names.isNameOrPlace( fixedWord ) )
			{
				result	=
					posTagToMap
					(
						wordLexicon.getPartOfSpeechTags().
							getSingularProperNounTag()
					);

				addCachedWord( word , result );
			}
		}

		return result;
	}

	/**	See if word is a possessive noun.
	 *
	 *	@param	word	The word.
	 *
	 *	@return			Guessed parts of speech
	 *					if word is a possessive noun, else null.
	 */

	public Map<String, MutableInteger> checkPossessiveNoun( String word )
	{
	 	Map<String, MutableInteger> result	= null;

								//	See if word ends with 's.

		if	(	checkPossessives &&
				CharUtils.endsWithSingleQuoteS( word )
			)
		{
								//	Strip 's from end od word.

			String strippedWord	=
				word.substring( 0 , word.length() - 2 );

								//	Is stripped word a name?
								//	Return possessive proper noun
								//	tag if so.

			if ( names.isNameOrPlace( strippedWord ) )
			{
				result	=
					posTagToMap
					(
						wordLexicon.getPartOfSpeechTags().
							getPossessiveSingularProperNounTag()
					);
			}
			else
			{
				if ( Character.isUpperCase( strippedWord.charAt( 0 ) ) )
				{
					result	=
						posTagToMap
						(
							wordLexicon.getPartOfSpeechTags().
								getPossessiveSingularProperNounTag()
						);
				}
				else
				{
					result	=
						posTagToMap
						(
							wordLexicon.getPartOfSpeechTags().
								getPossessiveSingularNounTag()
						);
				}
			}
		}

		if ( result != null )
		{
			addCachedWord( word , result );
		}

		return result;
	}

	/**	See if word is all uppercase.
	 *
	 *	@param	word	The word.
	 *
	 *	@return			Guessed parts of speech
	 *					if word is all uppercase, else null.
	 */

	public Map<String, MutableInteger> checkAllUpperCase( String word )
	{
	 	Map<String, MutableInteger> result	= null;

								//	Check if word is all uppercase.
								//	Assume it is a proper noun if so.

		if ( CharUtils.allLettersCapital( word ) )
		{
			result	=
				posTagToMap(
					wordLexicon.getPartOfSpeechTags().
						getSingularProperNounTag() );

			addCachedWord( word , result );
		}

		return result;
	}

	/**	See if word is a number.
	 *
	 *	@param	word	The word.
	 *
	 *	@return			Guessed parts of speech
	 *						if word is a number, else null.
	 */

	public Map<String, MutableInteger> checkNumber( String word )
	{
	 	Map<String, MutableInteger> result	= null;

								//	Check if word is a number.

		String fixedWord	= word.replaceAll( "," , "" );

		if ( CharUtils.isNumber( fixedWord ) )
		{
			result	=
				posTagToMap(
					wordLexicon.getPartOfSpeechTags().getCardinalNumberTag() );

			addCachedWord( word , result );
		}
		else if ( CharUtils.isOrdinal( fixedWord ) )
		{
			result	=
				posTagToMap(
					wordLexicon.getPartOfSpeechTags().getOrdinalNumberTag() );

			addCachedWord( word , result );
		}

		return result;
	}

	/**	See if word is currency value.
	 *
	 *	@param	word	The word.
	 *
	 *	@return			Guessed parts of speech
	 *					if word is currency value, else null.
	 */

	public Map<String, MutableInteger> checkCurrency( String word )
	{
	 	Map<String, MutableInteger> result	= null;

		if	(	CharUtils.isCurrency( word )||
				CharUtils.isUSCurrency( word ) ||
				CharUtils.isUSCurrencyCents( word )
			)
		{
			result	=
				posTagToMap(
					wordLexicon.getPartOfSpeechTags().getCardinalNumberTag() );

			addCachedWord( word , result );
		}

		return result;
	}

	/**	See if word is Roman numeral.
	 *
	 *	@param	word	The word.
	 *
	 *	@return			Guessed parts of speech tags
	 *					if word is a Roman Numeral, else null.
	 */

	public Map<String, MutableInteger> checkRomanNumeral( String word )
	{
	 	Map<String, MutableInteger> result	= null;

								//	See if word is a Roman numeral.

		if ( RomanNumeralUtils.isLooseRomanNumeral( word ) )
		{
		                                        //  If possible Roman numeral
		                                        //  could be initial, return both
		                                        //  proper noun and cardinal as possible
		                                        //  parts of speech.  Otherwise return
		                                        //  only cardinal number.

			if ( Abbreviations.isInitial( word ) )
			{
				result	=
					posTagsToMap
					(
						new String[]
						{
							wordLexicon.getPartOfSpeechTags().
								getCardinalNumberTag() ,

							wordLexicon.getPartOfSpeechTags().
								getSingularProperNounTag()
						}
					);
			}
			else
			{
				result	=
					posTagToMap(
						wordLexicon.getPartOfSpeechTags().
							getCardinalNumberTag() );
                	}

			addCachedWord( word , result );
		}
		else if ( RomanNumeralUtils.isLooseOrdinalRomanNumeral(
			word ) )
		{
			result	=
				posTagToMap(
					wordLexicon.getPartOfSpeechTags().
						getOrdinalNumberTag() );

			addCachedWord( word , result );
		}

		return result;
	}

	/**	See if word is defined in an auxiliary word list.
	 *
	 *	@param	word	The word.
	 *
	 *	@return			Guessed parts of speech as an array of tags
	 *					else null.
	 */

	public Map<String, MutableInteger> checkAuxiliaryWordLists( String word )
	{
	 	Map<String, MutableInteger> result	= null;

								//	Loop over lists.

		for ( int i = 0 ; i < auxiliaryWordLists.size() ; i++ )
		{
								//	Get next word list.

			TaggedStrings wordList	=
				(TaggedStrings)auxiliaryWordLists.get( i );

								//	Is word in this list?

			if ( wordList.containsString( word ) )
			{
								//	Yes -- return part of speech tag(s).
				result	=
					posTagsToMap
					(
						StringUtils.makeTokenArray
						(
							(String)wordList.getTag( word )
						)
					);

				break;
			}
			else
			{
								//	Is lower case word in this list?

				String lowerWord	= word.toLowerCase();

			 	if ( wordList.containsString( lowerWord ) )
				{
								//	Yes -- return part of speech tag(s).

					result	=
						posTagsToMap
						(
							StringUtils.makeTokenArray
							(
								(String)wordList.getTag( lowerWord )
							)
						);

					break;
				}
			}
		}

		return result;
	}

	/**	Check if word is punctuation.
	 *
	 *	@param	word	The word.
	 *
	 *	@return			Punctuation part of speech tag
	 *					or null if word is not punctuation.
	 */

	public Map<String, MutableInteger> checkPunctuation( String word )
	{
	 	Map<String, MutableInteger> result	= null;

		if ( CharUtils.isPunctuation( word ) )
		{
			result	= posTagToMap( word );

			addCachedWord( word , result );
		}

		return result;
	}

	/**	Check if word is symbol.
	 *
	 *	@param	word	The word.
	 *
	 *	@return			Symbol part of speech tag
	 *					or null if word is not symbols.
	 */

	public Map<String, MutableInteger> checkSymbol( String word )
	{
	 	Map<String, MutableInteger> result	= null;

		if ( CharUtils.isSymbol( word ) )
		{
			result	=
				posTagToMap
				(
					wordLexicon.getPartOfSpeechTags().getSymbolTag()
				);

			addCachedWord( word , result );
		}

		return result;
	}

	/**	Check if word is abbreviation.
	 *
	 *	@param	word	The word.
	 *
	 *	@return			Noun or proper noun part of speech if
	 *					word appears to be abbreviation.
	 *
	 *	<p>
	 *	A proper noun tag is emitted when the abbreviation begins
	 *	with a capital letter.
	 *	</p>
	 */

	public Map<String, MutableInteger> checkAbbreviation( String word )
	{
	 	Map<String, MutableInteger> result	= null;

		if ( Abbreviations.isAbbreviation( word ) )
		{
			if ( Character.isUpperCase( word.charAt( 0 ) ) )
			{
				result	=
					posTagToMap(
						wordLexicon.getPartOfSpeechTags().
							getSingularProperNounTag() );
 			}
			else
			{
				result	=
					posTagToMap(
						wordLexicon.getPartOfSpeechTags().
							getSingularNounTag() );
			}

			addCachedWord( word , result );
		}

		return result;
	}

	/**	Check if word contains a hyphen.
	 *
	 *	@param	word	The word.
	 *
	 *	@return			Part of speech.
	 *
	 *	<p>
	 *	If word contains a dash, extract the part after the last dash.
	 *	If that is a word in the lexicon, use its part of speech.
	 *	Otherwise return with no part of speech assign and let the
	 *	subsequent suffix analysis deal with the word.
	 *	</p>
	 *
	 *	<p>
	 *	The following cases are treated specially.
	 *	</p>
	 *
	 *	<ul>
	 *	<li>a letter followed by ---'s is considered a possessive noun.
	 *		</li>
	 *	<li>---'s or ---'S is considered a possessive noun.
	 *		</li>
	 *	<li>a letter followed by --- is considered a proper or common
	 *		noun, or an exclamation.
	 *		</li>
	 *	</ul>
	 */

	public Map<String, MutableInteger> checkHyphenatedWord( String word )
	{
	 	Map<String, MutableInteger> result	= null;

		int dashPos	= word.lastIndexOf( '-' );

		if ( dashPos >= 0 )
		{
								//	L---'s or L---'S is
								//	a possessive noun, "L" is an
								//	uppercase letter.

			if ( word.matches( "[A-Z](--|---)'(s|S)" ) )
			{
				String properPossessiveNounTag	=
					wordLexicon.getPartOfSpeechTags().
						getPossessiveSingularProperNounTag();

				result	=
					posTagsToMap
					(
						new String[]
						{
							properPossessiveNounTag
						}
					);
			}
								//	l(--|---)'s or l(--|---)'S is
								//	a possessive noun, "l" is a
								//	lowercase letter.

			else if ( word.matches( "[a-z](--|---)'(s|S)" ) )
			{
				String commonPossessiveNounTag	=
					wordLexicon.getPartOfSpeechTags().
						getPossessiveSingularNounTag();

				result	=
					posTagsToMap
					(
						new String[]
						{
							commonPossessiveNounTag
						}
					);
			}
								//	(--|---)'s or (--|---)'S is a possessive noun.

			else if ( word.matches( "(--|---)'(s|S)" ) )
			{
				String commonPossessiveNounTag	=
					wordLexicon.getPartOfSpeechTags().
						getPossessiveSingularNounTag();

				String properPossessiveNounTag	=
					wordLexicon.getPartOfSpeechTags().
						getPossessiveSingularProperNounTag();

				result	=
					posTagsToMap
					(
						new String[]
						{
							commonPossessiveNounTag ,
							properPossessiveNounTag
						}
					);
			}
								//	L(--|---) or L(--|---) is
								//	a proper noun or interjection ,
								//	"L" is an uppercase letter.

			else if ( word.matches( "[A-Z](--|---)" ) )
			{
				String properNounTag	=
					wordLexicon.getPartOfSpeechTags().
						getSingularProperNounTag();

				String interjectionTag	=
					wordLexicon.getPartOfSpeechTags().
						getInterjectionTag();

				result	=
					posTagsToMap
					(
						new String[]
						{
							properNounTag ,
							interjectionTag
						}
					);
			}
								//	l(--|---) or l(--|---) is
								//	a common noun or interjection ,
								//	"l" is an lowercase letter.

			else if ( word.matches( "[a-z](--|---)" ) )
			{
				String commonNounTag	=
					wordLexicon.getPartOfSpeechTags().
						getSingularNounTag();

				String interjectionTag	=
					wordLexicon.getPartOfSpeechTags().
						getInterjectionTag();

				result	=
					posTagsToMap
					(
						new String[]
						{
							commonNounTag ,
							interjectionTag
						}
					);
			}

			else if ( dashPos < ( word.length() - 1 ) )
			{
				String suffix	= word.substring( dashPos + 1 );

				result			=
					wordLexicon.getCategoryCountsForEntry( suffix );

				if ( result == null )
				{
					result			=
						clonePosTagMap
						(
							wordLexicon.getCategoryCountsForEntry
							(
								suffix.toLowerCase()
							)
						);
				}
			}
/*
								//	Assigning adjective part of speech
								//	doesn't work very well even for
								//	19th century text.  Drop through and
								//	let subsequent word form analysis
								//	look for a part part speech.

			if ( result == null )
			{
				String adjectiveTag	=
					wordLexicon.getPartOfSpeechTags().getAdjectiveTag();

				result	= posTagToMap( adjectiveTag );
			}
*/
			if ( result != null )
			{
				addCachedWord( word , result );
			}
		}

		return result;
	}

	/**	Get standardized spellings for a word.
	 *
	 *	@param	word	The word.
	 *
	 *	@return			The standardized spellings.
	 */

	protected String[] getStandardizedSpellings( String word )
	{
		String[] result	= null;

	 	if ( tryStandardSpellings )
	 	{
			if ( spellingStandardizer != null )
			{
				result	=
					spellingStandardizer.standardizeSpelling( word );

				if ( result.length == 0 )
				{
					result	=
						spellingStandardizer.standardizeSpelling(
							word.toLowerCase() );
				}
			}
		}
		else
		{
			if ( auxiliarySpellingStandardizer != null )
			{
				result	=
					auxiliarySpellingStandardizer.standardizeSpelling( word );

				if ( result.length == 0 )
				{
					result	=
						auxiliarySpellingStandardizer.standardizeSpelling(
							word.toLowerCase() );
				}
			}
		}

		return result;
	}

	/**	Try to get tags using standardized spellings.
	 *
	 *	@param	word				The word.
	 *	@param	standardSpellings	Standard spellings on output, or null
	 *								if none.
	 *
	 *	@return						Parts of speech for standardized
	 *								spellings, or null if not found.
	 */

	protected Map<String, MutableInteger> checkStandardSpellings
	(
		String word ,
		String[] standardSpellings
	)
	{
	 	Map<String, MutableInteger> result	= null;

								//	See if we have any standardized
								//	spellings for this word.  If so,
								//	and the word is in the word lexicon,
								//	return the parts of speech for
								//	the standardized spellings.

		if ( spellingStandardizer != null )
		{
			switch ( standardSpellings.length )
			{
				case 0	:
					break;

//				case 1	:
				default	:
					String standardSpelling	= standardSpellings[ 0 ];

					if ( wordLexicon.getEntryCount( standardSpelling ) > 0 )
					{
						result	=
							clonePosTagMap
							(
								wordLexicon.getCategoryCountsForEntry
								(
									standardSpelling
								)
							);

						addCachedWord( word , result );

						return result;
					}

					result	= checkName( standardSpelling );

					break;

//$$$PIB$$$	Need to handle ordering categories so most frequent is first.
/*
				default:
					result	= MapFactory.createNewMap();

					for ( int i = 0 ; i < standardSpellings.length ; i++ )
					{
						standardSpelling	= standardSpellings[ i ];

						if	(	wordLexicon.getEntryCount(
									standardSpelling ) > 0
							)
						{
							partsOfSpeech.add
							(
								wordLexicon.getCategoriesForEntry(
									standardSpelling )
							);
						}
					}

					if ( partsOfSpeech.size() > 0 )
					{
						result	= new ArrayList( partsOfSpeech );

						addCachedWord( word , result );

						return result;
					}
*/
			}
		}

	 	return result;
	}

	/**	Remove proper noun and proper adjective tags from tag map.
	 *
	 *	@param	posTagsMap	Map of potential tags.
	 *
	 *	<p>
	 *	Removes proper noun or proper adjective tags from tag map.
	 *	Map is set to null if it becomes empty.
	 *	</p>
	 */

	public void removeProperNounTags
	(
		Map<String, MutableInteger> posTagsMap
	)
	{
		PartOfSpeechTags posTags	= wordLexicon.getPartOfSpeechTags();

		Iterator<String> iterator	= posTagsMap.keySet().iterator();

		while ( iterator.hasNext() )
		{
			String tag	= iterator.next();

			if	(	posTags.isProperNounTag( tag ) ||
			        posTags.isProperAdjectiveTag( tag )
				)
			{
				iterator.remove();
			}
		}

		if ( posTagsMap.size() == 0 )
		{
			posTagsMap	= null;
		}
	}

	/**	Remove compound part of speech tags from tag map.
	 *
	 *	@param	posTagsMap	Map of potential tags.
	 *
	 *	<p>
	 *	Removes compound part of speech tags from tag map.
	 *	Map is set to null if it becomes empty.
	 *	</p>
	 */

	public void removeCompoundTags
	(
		Map<String, MutableInteger> posTagsMap
	)
	{
		PartOfSpeechTags posTags	= wordLexicon.getPartOfSpeechTags();

		Iterator<String> iterator	= posTagsMap.keySet().iterator();

		while ( iterator.hasNext() )
		{
			String tag	= iterator.next();

			if ( posTags.isCompoundTag( tag ) )
			{
				iterator.remove();
			}
		}

		if ( posTagsMap.size() == 0 )
		{
			posTagsMap	= null;
		}
	}

	/**	Try to get tags using suffix analysis.
	 *
	 *	@param	word				The word.
	 *
	 *	@return						Parts of speech from suffix analysis or
	 *								null if not found.
	 */

	public Map<String, MutableInteger> checkSuffixes
	(
		String word
	)
	{
	 	Map<String, MutableInteger> result	= null;

								//	Get maximum and minimum
								//	suffix lengths.

		int maxSuffixLength	= suffixLexicon.getLongestEntryLength();
		int minSuffixLength	= suffixLexicon.getShortestEntryLength();

								//	Get length of word on which
								//	to perform suffix analysis.

		int l	= word.length();
                                //	Does word contain a capital letter?

		boolean hasCap	= CharUtils.hasCapitalLetter( word );

								//	Does word contain an apostrophe?

		boolean hasApos	= CharUtils.hasApostrophe( word );

								//	Check successively shorter
								//	suffixes looking for a match
								//	with the word.
		String suffix;

		for	(	int i = Math.min( maxSuffixLength , l ) ;
				i > minSuffixLength - 1 ;
				i--
			)
		{
								//	Get suffix from word.

			suffix	= word.substring( l - i , l );

								//	If suffix lexicon contains
								//	this suffix, get the parts
								//	of speech associated with the
								//	suffix.

			if ( suffixLexicon.getEntryCount( suffix ) > 0 )
			{
				result	=
					clonePosTagMap
					(
						suffixLexicon.getCategoryCountsForEntry( suffix )
					);
								//	Do not allow proper noun tags
								//	unless the word contains a
								//	capital letter.  Need not be
								//	the first letter -- e.g., deBrower
								//	is a valid proper name that
								//	does not start with a capital
								//	letter.
				if ( !hasCap )
				{
					removeProperNounTags( result );
				}
								//	Remove tags containing multiple
								//	parts of speech unless the word
								//	contains an apostrophe.  This isn't
								//	always correct but it is correct
								//	most of the time time.

				if ( !hasApos )
				{
					removeCompoundTags( result );
				}
								//	If we found at least one tag,
								//	return it.

				if ( ( result != null ) && ( result.size() > 0 ) )
				{
					addCachedWord( word , result );
					cachedLexicons.put( word , suffixLexicon );

					return result;
				}
			}
		}

		if ( ( result != null ) && ( result.size() == 0 ) )
		{
			result	= null;
		}

		return result;
	}

	/**	Try to get tags using suffix analysis.
	 *
	 *	@param	word				The word.
	 *	@param	standardSpellings	List of standard spellings, or null
	 *									if none.
	 *
	 *	@return						Parts of speech from suffix analysis or
	 *								null if not found.
	 */

	public Map<String, MutableInteger> checkSuffixes
	(
		String word ,
		String[] standardSpellings
	)
	{
								//	Assume we find no results.

	 	Map<String, MutableInteger> result	= null;

								//	Does word contain apostrophe
								//	other than terminal "'s"?
								//	If so and we have standard
								//	spellings, try suffix analysis
								//	on standard spellings first.

		boolean hasApos	= CharUtils.hasApostrophe( word );

		if	(	hasApos &&
				!word.endsWith( "'s" ) &&
				tryStandardSpellings &&
				( standardSpellings != null ) &&
				( standardSpellings.length > 0 )
			)
		{
			for ( int i = 0 ; i < standardSpellings.length ; i++ )
			{
				result	= checkSuffixes( standardSpellings[ i ] );

				if ( result != null )
				{
					break;
				}
			}
								//	If no results from standard
								//	spellings, try original spelling.

			if ( result == null )
			{
				result	= checkSuffixes( word );
			}
		}
								//	If no apostrophe in word,
								//	try suffix analysis on original
								//	spelling first.
		else
		{
			result	= checkSuffixes( word );

								//	If no results from original
								//	spelling, try standard spellings
								//	if we have them.

			if	(	( result == null ) &&
					tryStandardSpellings &&
					( standardSpellings != null ) &&
					( standardSpellings.length > 0 )
				)
			{
				for ( int i = 0 ; i < standardSpellings.length ; i++ )
				{
					result	= checkSuffixes( standardSpellings[ i ] );

					if ( result != null )
					{
						break;
					}
				}
			}
		}

		return result;
	}

	/**	Get tags for a noun.
	 *
	 *	@param	word	The word.
	 *
	 *	@return			A noun type tag.
	 *
	 *	<p>
	 *	A proper noun tag is emitted when the abbreviation begins
	 *	with a capital letter.  A plural noun tag is emitted when
	 *	the word ends with an "s".
	 *	</p>
	 */

	public Map<String, MutableInteger> getNoun( String word )
	{
	 	Map<String, MutableInteger> result	= null;

								//	Assume word is a proper noun
								//	if it begins with a capital letter,
								//	otherwise assume it is an ordinary
								//	noun.

		char firstChar	= word.charAt( 0 );
		char lastChar	= word.charAt( word.length() - 1 );

		if ( Character.isUpperCase( firstChar ) )
		{
			if ( ( lastChar == 's' ) || ( lastChar == 'S' ) )
			{
				result	=
					posTagToMap(
						wordLexicon.getPartOfSpeechTags().
							getPluralProperNounTag() );
			}
			else
			{
				result	=
					posTagToMap(
						wordLexicon.getPartOfSpeechTags().
							getSingularProperNounTag() );
			}
		}
		else
		{
			if ( ( lastChar == 's' ) || ( lastChar == 'S' ) )
			{
				result	=
					posTagToMap(
						wordLexicon.getPartOfSpeechTags().
							getPluralNounTag() );
			}
			else
			{
				result	=
					posTagToMap(
						wordLexicon.getPartOfSpeechTags().
							getSingularNounTag() );
			}
		}

		addCachedWord( word , result );

		return result;
	}

	/**	Guesses part of speech for a word.
	 *
	 *	@param	word			The word.
	 *	@param	isFirstWord		If word is first word in a sentence.
	 *
	 *	@return					Guessed parts of speech as an array of tags.
	 */

	public Map<String, MutableInteger> guessPartsOfSpeech
	(
		String word ,
		boolean isFirstWord
	)
	{
		return guessPartsOfSpeech( word );
	}

	/**	Try using standardized spellings when guessing parts of speech.
	 */

	public void setTryStandardSpellings( boolean tryStandardSpellings )
	{
		this.tryStandardSpellings	= tryStandardSpellings;
	}

	/**	Check for possessives of known nouns when guessing parts of speech.
	 */

	public void setCheckPossessives( boolean checkPossessives )
	{
		this.checkPossessives	= checkPossessives;
	}

	/**	Guesses part of speech for a word.
	 *
	 *	@param	word			The word.
	 *
	 *	@return					Guessed parts of speech.
	 */

	 abstract public Map<String, MutableInteger> guessPartsOfSpeech
	 (
	 	String word
	 );
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



