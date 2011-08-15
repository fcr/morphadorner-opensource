package edu.northwestern.at.utils.spellcheck;

import java.io.*;
import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.phonetics.*;

/** HashMapSpellingDictionary -- implements hash map based spelling dictionary.
 *
 *	<p>
 *	This class provides the basic methods for a spelling checker
 *	dictionary implemented using hash maps.
 *	</p>
 */

public class HashMapSpellingDictionary implements SpellingDictionary
{
	/** The dictionary keyed by metaphone values. */

	protected Map<String, List<String>> metaphoneDictionary =
		MapFactory.createNewMap();

	/** Metaphone encoder instance. */

	private DoubleMetaphone metaphone = new DoubleMetaphone();

	/** Create HashMapSpellingDictionary. */

	public HashMapSpellingDictionary()
	{
	}

	/**	Reads the dictionary from a buffered reader.
	 *
	 *	@param	in		The buffered reader.
	 *
	 *	@throws	IOException
	 */

	public void read( BufferedReader in )
		throws IOException
	{
		String word;
		String metaphoneValue;

								// Read # of words in dictionary

		String sWords	= in.readLine();
		int nWords		= new Integer( sWords ).intValue();

								// Read # of metaphone values

		String sMeta	= in.readLine();
		int nMeta		= new Integer( sMeta ).intValue();

								// Set hash map capacities to match.

		metaphoneDictionary = MapFactory.createNewMap();

								// Pick up next metaphone value in
								// dictionary.

		while ( ( metaphoneValue = in.readLine() ) != null )
		{
								// Pick up the number of words having
								// this metaphone value.

			String sWordsThisMeta	= in.readLine();

			int nWordsThisMeta		= new Integer( sWordsThisMeta ).intValue();

								// Read the words matching this
								// metaphone value.

			List<String> words = ListFactory.createNewList();

			for ( int i = 0; i < nWordsThisMeta; i++ )
			{
				word = in.readLine();

				words.add( word );
			}
								// Make sure word list is sorted.
								// We need this so that binary searches
								// work later on when we lookup words.

			Collections.sort( words );

								// Add metaphone value.

        	metaphoneDictionary.put( metaphoneValue , words );
		}

		in.close();
	}

	/** Counts words in the dictionary.
	 *
	 *	@return		The number of words in the dictionary.
	 */

	public int wordCount()
	{
		int result = 0;

						// Get list of metaphone values.

		java.util.List<String> keys =
			ListFactory.createNewList( metaphoneDictionary.keySet() );

						// Loop over each metaphone value.

		for ( String key : keys )
		{
						// Get word list for this metaphone value.

			List<String> words = metaphoneDictionary.get( key );

                        // If not null, add # of words in this list
						// to total.

			if ( words != null )
			{
				result += words.size();
			}
		}

		return result;
	}

	/**	Writes the dictionary to a buffered writer.
	 *
	 *	@param	out		The buffered writer.
	 *
	 *	@throws	IOException
	 */

	public void write( BufferedWriter out )
		throws IOException
	{
		int nWords	= wordCount();
		int nMeta	= metaphoneDictionary.size();

						// Output # of words and # of metaphone values.

		out.write( nWords + "\n" );
		out.write( nMeta + "\n" );

						// Get list of metaphone values.

		java.util.List<String> keys =
			ListFactory.createNewList( metaphoneDictionary.keySet() );

						// Loop over each metaphone value.

		for ( String key : keys )
		{
						// Get word list for this metaphone value.

			List<String> words = metaphoneDictionary.get( key );

						// Output the metaphone value.

			out.write( key + "\n" );

						// Output the number of words keyed to this
						// metaphone value.

			if ( words == null )
			{
				out.write( "0\n" );
			}
			else
			{
						// Output the words keyed to this metaphone value.

				out.write( words.size() + "\n" );

				for ( int i = 0; i < words.size(); i++ )
				{
					out.write( words.get( i ) + "\n" );
				}
			}
		}

		out.close();
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

							// Get metaphone for word.

		String lowerCaseWord	= word.toLowerCase();
		String metaphoneValue	= "";

		try
		{
			metaphoneValue = metaphone.encode( lowerCaseWord );
		}
		catch ( Exception e )
		{
			metaphoneValue = "";
		}
							// Get list of words for this metaphone value.

		Set<String> words = getRelatedWords( metaphoneValue );

							// If none, this word can't be in the
							// dictionary, so say it's not found.

		if ( words == null )
		{
			return false;
		}
							// See if the word is in the list of
							// words for the metaphone value.  If not,
							// it's not in the dictionary.
		else
		{
			return words.contains( lowerCaseWord );
		}
	}

	/** Add a word to the dictionary.
	 *
	 *	@param	word		The word to add to the dictionary.
	 *
	 *	@return				True if word added successfully.
	 */

	private boolean addWordPrivate( String word )
	{
		String lowerCaseWord	= word.toLowerCase();
		String metaphoneValue	= "";
		String metaphoneValue2	= "";

		if ( lookupWord( lowerCaseWord ) ) return false;

		try
		{
			metaphoneValue = metaphone.encode( lowerCaseWord );
		}
		catch ( Exception e )
		{
			metaphoneValue = "";
		}

		List<String> words = metaphoneDictionary.get( metaphoneValue );

		if ( words == null )
		{
			words = ListFactory.createNewList();
		}

		words.add( lowerCaseWord );

		Collections.sort( words );

		metaphoneDictionary.put( metaphoneValue , words );

		try
		{
			metaphoneValue2 = metaphone.getAlternate();
		}
		catch ( Exception e )
		{
			metaphoneValue2 = "";
		}

		if ( !metaphoneValue2.equals( metaphoneValue ) )
		{
			words = metaphoneDictionary.get( metaphoneValue2 );

			if ( words == null )
			{
				words = ListFactory.createNewList();
			}

			words.add( lowerCaseWord );

			Collections.sort( words );

			metaphoneDictionary.put( metaphoneValue2 , words );
        }

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

	/** Get list of words with a specified metaphone value.
	 *
	 *	@param	metaphone	The metaphone value.
	 *
	 *	@return				Set of words with specified metaphone value.
	 *						May be empty if no words with matching
	 *						metaphone value found.
	 */

	public Set<String> getRelatedWords( String metaphone )
	{
		Set<String> result	= SetFactory.createNewSet();

		List<String> list = metaphoneDictionary.get( metaphone );

		if ( list != null )
		{
			result.addAll( list );
		}

		return result;
	}

	/** Retrieves all words in dictionary.
	 *
	 *	@return		ArrayList of all words in dictionary.
	 */

	public Set<String> getAllWords()
	{
		Set<String> result = new TreeSet<String>();

						// Get list of metaphone values.

		java.util.List<String> keys =
			ListFactory.createNewList( metaphoneDictionary.keySet() );

						// Loop over each metaphone value.

		for ( String key : keys )
		{
						// Get word list for this metaphone value.

			List<String> words = metaphoneDictionary.get( key );

                        // If not null, add # of words in this list
						// to total.

			if ( words != null )
			{
				result.addAll( words );
			}
		}

		return result;
	}

	/**	Retrieves number of words in dictionary.
	 *
	 *	@return		Number of words in dictionary.
	 */

	public int getNumberOfWords()
	{
		return wordCount();
	}

	/** Clear dictionary of all words and metaphone values. */

	public void clear()
	{
		metaphoneDictionary.clear();
	}
}
