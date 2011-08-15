package edu.northwestern.at.utils.corpuslinguistics.lexicon;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import java.net.URL;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.logger.*;
import edu.northwestern.at.utils.corpuslinguistics.outputter.*;
import edu.northwestern.at.utils.corpuslinguistics.partsofspeech.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.guesser.*;
import edu.northwestern.at.utils.corpuslinguistics.spellingstandardizer.*;

/**	AbstractLexicon: stores spellings and their possible parts of speech.
 *
 *	<p>
 *	Each line in the main lexicon file takes the following form:
 *	</p>
 *
 *	<blockquote>
 *	<p>
 *	<code>spelling countspelling pos1 countpos1 pos2 countpos2 ...
 *	</p>
 *	</blockquote>
 *
 *	<p>
 *	where <strong>spelling</strong> is the spelling for a word,
 *	<strong>countspelling</strong> is the number of times the spelling
 *	appeared in the training data, <strong>pos1</strong> is the tag
 *	corresponding to the most commonly occurring part of speech
 *	for this spelling, <strong>countpos1</strong> is the number
 *	of times the <strong>pos1</strong> tag appeared, and
 *	<strong>pos2</strong>, <strong>countpos2</strong>, etc.
 *	are the other possible parts of speech and their counts.
 *	</p>
 *
 *	<p>
 *	The raw counts are stored rather than probabilities so that
 *	new training data can be used to update the lexicon easily,
 *	and so that individual part of speech taggers can apply different
 *	methods of count smoothing.
 *	</p>
 */

abstract public class AbstractLexicon
	extends IsCloseableObject
	implements Lexicon, UsesLogger
{
	/**	Map in which to store lexicon entries.
	 *
	 *	<p>
	 *	An entry (e.g., word spelling) is the key, and a
	 *	LexiconEntry is the value.
	 *	</p>
	 */

	protected Map<String, LexiconEntry> lexiconMap;

	/**	Map from part of speech tags to their frequency in the lexicon.
	 */

	protected Map<String, MutableInteger> categoryCountsMap;

	/**	Map from part of speech tags to frequency of unique word entries
	 *	in the lexicon with each tag.
	 */

	protected Map<String, MutableInteger> uniqueEntryCountForCategoryMap;

	/**	Length (in characters) of the longest entry in the lexicon.
	 */

	protected int longestEntryLength;

	/**	Length (in characters) of the shortest entry in the lexicon.
	 */

	protected int shortestEntryLength;

	/**	Part of Speech tag set used by lexicon.
	 *
	 *	<p>
	 *	Note: all tags in the lexicon must appear in this list!
	 *	</p>
	 */

	protected PartOfSpeechTags partOfSpeechTags;

	/**	Logger used for output. */

	protected Logger logger;

	/**	Create an empty lexicon.
	 */

	public AbstractLexicon()
	{
								//	Create maps.

		lexiconMap						= MapFactory.createNewMap();

		categoryCountsMap				= MapFactory.createNewMap();

		uniqueEntryCountForCategoryMap	= MapFactory.createNewMap();

								//	Create dummy logger.

		logger							= new DummyLogger();

								//	Shortest and longest entries.

		longestEntryLength				= 0;
		shortestEntryLength				= 0;
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

	/**	Add or update category counts map.
	 *
	 *	@param	category	Category for which to add/update count.
	 *	@param	count		Category count to add to entry.
	 *							May be negative.
	 */

	protected void updateCategoryCount( String category , int count )
	{
		MutableInteger currentCount	=
			categoryCountsMap.get( category );

		if ( ( currentCount == null ) && ( count > 0 ) )
		{
			categoryCountsMap.put( category , new MutableInteger( count ) );
		}
		else
		{
			currentCount.setValue( currentCount.intValue() + count );

			if ( currentCount.intValue() <= 0 )
			{
				categoryCountsMap.remove( category );
			}
		}
	}

	/**	Increment number of unique entries for a category.
	 *
	 *	@param	category	Category for which to increment count.
	 */

	protected void incrementUniqueEntryCountForCategory( String category )
	{
		MutableInteger currentCount	=
			uniqueEntryCountForCategoryMap.get( category );

		if ( currentCount == null )
		{
			uniqueEntryCountForCategoryMap.put(
				category , new MutableInteger( 1 ) );
		}
		else
		{
			currentCount.setValue( currentCount.intValue() + 1 );
		}
	}

	/**	Update entry count in lexicon for a given category.
	 *
	 *	@param	entry		The entry.
	 *	@param	category	The category.
	 *	@param	lemma		The lemma.
	 *	@param	entryCount	The entry count to add to the current count.
	 *							Must be positive.
	 */

	public void updateEntryCount
	(
		String entry ,
		String category ,
		String lemma ,
		int entryCount
	)
	{
								//	Ignore entry counts that are not
								//	positive.

		if ( entryCount <= 0 ) return;

								//	Get the existing lexicon entry,
								//	if any.

		LexiconEntry lexiconEntry	= lexiconMap.get( entry );

								//	If none, create a new entry.

		if ( lexiconEntry == null )
		{
			Map<String, MutableInteger> categoriesAndCounts	=
				MapFactory.createNewMap();

			Map<String, String> lemmata	= MapFactory.createNewMap();

			lexiconEntry	=
				new LexiconEntry
				(
					entry ,
					entry ,
					0 ,
					categoriesAndCounts ,
					lemmata
				);
		}
								//	Update total count for entry.

		lexiconEntry.entryCount	+= entryCount;

								//	Update count for specified category.

		lexiconEntry.updateCategoryAndCount( category , entryCount );

								//	Update the lemma for this category.

		lexiconEntry.updateLemma( category , lemma );

								//	Update overall category count.

		updateCategoryCount( category , entryCount );

								//	Determine largest category for entry.

		lexiconEntry.determineLargestCategory();

								//	Add new or updated lexicon entry
								//	to the lexicon map.

		lexiconMap.put( entry , lexiconEntry );
	}

	/**	Remove given category for an entry.
	 *
	 *	@param	entry		The entry.
	 *	@param	category	The category to remove.
	 *
	 *	<p>
	 *	If the entry has no remaining categories, the entry it removed from
	 *	the lexicon.
	 *	</p>
	 */

	public void removeEntryCategory
	(
		String entry ,
		String category
	)
	{
								//	Get lexicon entry.

		LexiconEntry lexiconEntry	= lexiconMap.get( entry );

								//	If found ...

		if ( lexiconEntry != null )
		{
								//	Get current count.

			int count		= lexiconEntry.getCategoryCount( category );

								//	Set count to 0.  This removes
								//	the category.

			lexiconEntry.updateCategoryAndCount( category , -count );

								//	Update overall entry count.

			lexiconEntry.entryCount	+= -count;

								//	Update overall category count.

			updateCategoryCount( category , -count );

								//	If the lexicon entry has no remaining
								//	categories, remove the lexicon entry.

			if ( lexiconEntry.getCategories().length == 0 )
			{
				removeEntry( entry );
			}
		}
	}

	/**	Remove entry.
	 *
	 *	@param	entry		The entry to remove.
	 */

	public void removeEntry
	(
		String entry
	)
	{
		lexiconMap.remove( entry );
	}

	/**	Load entries into a lexicon.
	 *
	 *	@param	lexiconURL	URL for the file containing the lexicon.
	 *	@param	encoding	Character encoding of lexicon file text.
	 */

	public void loadLexicon( URL lexiconURL , String encoding )
		throws IOException
	{
		String line;
								//	Open lexicon for input.

		BufferedReader lexiconReader;

		if ( encoding == null )
		{
			lexiconReader =
				new BufferedReader(
					new UnicodeReader(
						lexiconURL.openStream() ) );
		}
		else
		{
			lexiconReader	=
				new BufferedReader(
					new UnicodeReader(
						lexiconURL.openStream() , encoding ) );
		}

		String[] tokens;
		String entry;
		String sCount;
		String lemma;
		int count;
		List categories;
		String category;
		int categoryCount;
		boolean wordFound;
		longestEntryLength	= 0;
		shortestEntryLength	= 99999;

		uniqueEntryCountForCategoryMap.clear();

								//	Read first line of lexicon.

		line	= lexiconReader.readLine();

								//	Process each line of the lexicon.

		while ( line != null )
		{
								//	Tokenizer to extract entry and
								//	categories from the lexicon.

			tokens	= line.split( "\t" );

			if ( tokens.length > 3 )
			{
								//	The entry is the first token
								//	in the input line.

				entry	= tokens[ 0 ];

								//	The count for the entry is the
								//	second token.

				count	= Integer.parseInt( tokens[ 1 ] );

								//	Remember longest and shortest entries
								//	seen so far.

				longestEntryLength	=
					Math.max( longestEntryLength , entry.length() );

				shortestEntryLength	=
					Math.min( shortestEntryLength , entry.length() );

								//	Get the existing lexicon entry,
								//	if any.

				LexiconEntry lexiconEntry	= lexiconMap.get( entry );

								//	If none, create a new entry.

				if ( lexiconEntry == null )
				{
					Map<String, MutableInteger> categoriesAndCounts	=
						MapFactory.createNewMap();

					Map<String, String> lemmata	= MapFactory.createNewMap();

					lexiconEntry	=
						new LexiconEntry
						(
							entry ,
							entry ,
							0 ,
							categoriesAndCounts ,
							lemmata
						);
				}
								//	Update total count for entry.

				lexiconEntry.entryCount	+= count;

								//	The remaining tokens on the input
								//	line are (category , category count )
								//	pairs.
				try
				{
					for ( int i = 2 ; i < tokens.length ; i = i + 3 )
					{
								//	Get next category.

						category	= tokens[ i ];

								//	Get lemma for this category.

						lemma		= tokens[ i + 1 ];

								//	Get category count.

						count		= Integer.parseInt( tokens[ i + 2 ] );

								//	Update category and count map.

						lexiconEntry.updateCategoryAndCount(
							category , count );

								//	Update lemma.

						lexiconEntry.updateLemma( category , lemma );

								//	Update overall category count.

						updateCategoryCount( category , count );
					}
				}
				catch ( Exception e )
				{
/*
//					e.printStackTrace();
					System.out.println( "----------" );
					System.out.println( "Bad lexicon entry: line=<" +
						line + ">" );
					System.out.println( "----------" );
*/
				}
								//	Determine largest category for entry.

				lexiconEntry.determineLargestCategory();

								//	Add new or updated lexicon entry
								//	to the lexicon map.

				lexiconMap.put( entry , lexiconEntry );
			}
								//	Read next line from lexicon.

			line = lexiconReader.readLine();
		}
								//	Close lexicon source.
		try
		{
			if ( lexiconReader != null ) lexiconReader.close();
		}
		catch ( IOException e )
		{
		}
								//	Compute number of lexicon entries
								//	for each category.

		computeUniqueEntryCountsForCategories();
	}

	/**	Compute number of lexicon entries for each category.
	 */

	protected void computeUniqueEntryCountsForCategories()
	{
								//	Iterate over lexicon entries.

		Iterator<String> iterator	= lexiconMap.keySet().iterator();

		while ( iterator.hasNext() )
		{
								//	Get next lexicon entry.

			String entry	= iterator.next();

								//	Get categories for this entry.

			LexiconEntry lexiconEntry	= lexiconMap.get( entry );

			Set<String> categories		=
				lexiconEntry.categoriesAndCounts.keySet();

								//	Increment count of unique lexicon
								//	entries for each of these
								//	categories.

			Iterator<String> categoryIterator	= categories.iterator();

			while ( categoryIterator.hasNext() )
			{
				String category	= categoryIterator.next();

				incrementUniqueEntryCountForCategory( category );
			}
		}
	}

	/**	Get number of entries in Lexicon.
	 *
	 *	@return		Number of entries in Lexicon.
	 *
	 *	<p>
	 *	Returns number of fixed entries
	 *	</p>
	 */

	public int getLexiconSize()
	{
		return lexiconMap.keySet().size();
	}

	/**	Get the entries, sorted in ascending order.
	 *
	 *	@return		The sorted entry strings as an array of string.
	 */

	public String[] getEntries()
	{
								//	Get entry strings.

		Set<String> entrySet	= lexiconMap.keySet();

								//	Store entries in a String array.

		String[] entries		=
			(String[])entrySet.toArray( new String[ entrySet.size() ] );

								//	Sort the entries.

		Arrays.sort( entries );

								//	Return sorted entries.
		return entries;
	}

	/**	Get the categories, sorted in ascending order.
	 *
	 *	@return		The sorted category strings as an array of string.
	 */

	public String[] getCategories()
	{
								//	Get category strings.

		Set<String> categorySet	= categoryCountsMap.keySet();

								//	Store categories in a String array.

		String[] categories		=
			(String[])categorySet.toArray(
				new String[ categorySet.size() ] );

								//	Sort the categories.

		Arrays.sort( categories );

								//	Return sorted categories.
		return categories;
	}

	/**	Checks if lexicon contains an entry.
	 *
	 *	@param	entry	Entry to look up.
	 *
	 *	@return			true if lexicon contains entry.
	 *					Only an exact match is considered.
	 */

	public boolean containsEntry( String entry )
	{
		return ( lexiconMap.get( entry ) != null );
	}

	/**	Get a lexicon entry.
	 *
	 *	@param	entry	Entry for which to get lexicon information.
	 *
	 *	@return			LexiconEntry for entry, or null if not found.
	 *
	 *	<p>
	 *	Note: this does NOT call the part of speech guesser.
	 *	</p>
	 */

	public LexiconEntry getLexiconEntry( String entry )
	{
								//	Look for entry with exact
								//	matching case.

		LexiconEntry lexiconEntry	=
			(LexiconEntry)lexiconMap.get( entry );

								//	Not found.
								//	If word is all caps, try looking up
								//	word with first letter only capitalized.

		if ( lexiconEntry == null )
		{
			if ( CharUtils.allLettersCapital( entry ) )
			{
				lexiconEntry	=
					(LexiconEntry)lexiconMap.get
					(
						CharUtils.capitalizeFirstLetter( entry )
					);
			}
		}
								//	Not found.
								//	Try looking up all lowercase entry.

		if ( lexiconEntry == null )
		{
			lexiconEntry	=
				(LexiconEntry)lexiconMap.get( entry.toLowerCase() );
		}

		return lexiconEntry;
	}

	/**	Set a lexicon entry.
	 *
	 *	@param	entry		Entry for which to get lexicon information.
	 *	@param	entryData	The lexicon entry data.
	 *
	 *	@return				Previous lexicon data for entry, if any.
	 */

	public LexiconEntry setLexiconEntry
	(
		String entry ,
		LexiconEntry entryData
	)
	{
		return (LexiconEntry)lexiconMap.put( entry , entryData );
	}

	/**	Get categories for an entry in the lexicon.
	 *
	 *	@param	entry	Entry to look up.
	 *
	 *	@return			Set of categories.
	 *					Null if entry not found in lexicon.
	 */

	public Set<String> getCategoriesForEntry( String entry )
	{
								//	Get details for entry.

		LexiconEntry lexiconEntry	= getLexiconEntry( entry );

		Set<String> categories	= null;

		if ( lexiconEntry != null )
		{
			categories	= lexiconEntry.categoriesAndCounts.keySet();
		}
								//	Not found?  Try punctuation or symbol.

		if ( categories == null )
		{
			if ( CharUtils.isPunctuationOrSymbol( entry ) )
			{
				categories	= new TreeSet<String>();
				categories.add( entry );
			}
		}

		return categories;
	}

	/**	Get categories for an entry in a sentence.
	 *
	 *	@param	sentence	List of entries in sentence.
	 *	@param	entryIndex	Index within sentence (0-based) of entry.
	 *
	 *	@return				Set of categories.
	 *						Null if entry not found in lexicon.
	 */

	public Set<String> getCategoriesForEntry
	(
		List<String> sentence ,
		int entryIndex
	)
	{
								//	Get entry from sentence.

		String entry	= sentence.get( entryIndex );

		return getCategoriesForEntry( entry );
	}

	/**	Get categories for an entry.
	 *
	 *	@param	entry			Entry to look up.
	 *	@param	isFirstEntry	True if entry is first in sentence.
	 *
	 *	@return					Set of categories.
	 *							Null if entry not found in lexicon.
	 */

	public Set<String> getCategoriesForEntry
	(
		String entry ,
		boolean isFirstEntry
	)
	{
								//	Try getting categories from lexicon.

		return getCategoriesForEntry( entry );
	}

	/**	Get number of categories for an entry.
	 *
	 *	@param	entry	Entry for which to find number of categories.
	 *
	 *	@return			Number of categories for entry.
	 */

	public int getNumberOfCategoriesForEntry( String entry )
	{
		int result	= 0;

		LexiconEntry lexiconEntry	= getLexiconEntry( entry );

		if ( lexiconEntry != null )
		{
			result	= lexiconEntry.categoriesAndCounts.keySet().size();
		}

		return result;
	}

	/**	Get category with largest count for an entry.
	 *
	 *	@param	entry	Entry to look up.
	 *
	 *	@return			Category with largest count.
	 *					Null if entry not found in lexicon.
	 */

	public String getLargestCategory( String entry )
	{
		String result	= "";

		LexiconEntry lexiconEntry	= getLexiconEntry( entry );

		if ( lexiconEntry != null )
		{
			result	= lexiconEntry.largestCategory;
		}

		return result;
	}

	/**	Get category count.
	 *
	 *	@param	category	Get number of times category appears in lexicon.
	 *
	 *	@return					Category count.
	 */

	public int getCategoryCount( String category )
	{
		int result	= 0;

		if ( categoryCountsMap.get( category ) != null )
		{
			result	=
				categoryCountsMap.get( category ).intValue();
		}

		return result;
	}

	/**	Get unique entry count for a category.
	 *
	 *	@param	category	Category.
     *
	 *	@return				Count of unique entries with this category.
	 */

	public int getUniqueEntryCountForCategory( String category )
	{
		int result	= 0;

		if ( uniqueEntryCountForCategoryMap.get( category ) != null )
		{
			result	=
				((MutableInteger)uniqueEntryCountForCategoryMap.get(
					category )).intValue();
		}

		return result;
	}

	/**	Get count for an entry in a specific category.
	 *
	 *	@param	entry		Entry to look up.
	 *	@param	category	Category for which to retrieve count.
	 *
	 *	@return				Number of occurrences of entry in category.
	 */

	public int getCategoryCount( String entry , String category )
	{
		int result	= 0;

		LexiconEntry lexiconEntry	= getLexiconEntry( entry );

		if ( lexiconEntry != null )
		{
			MutableInteger count	=
				(MutableInteger)lexiconEntry.categoriesAndCounts.get(
					category );

			if ( count != null )
			{
				result	= count.intValue();
			}
		}

		return result;
	}

	/**	Get lemma for an entry.
	 *
	 *	@param	entry		Entry to look up.
	 *
	 *	@return				Lemma form of entry.  A "*' is returned
	 *						if the lemma cannot be found.
	 *
	 *	<p>
	 *	Some spellings may have multiple lemmata depending upon
	 *	the part of speech.  This method returns the lemma associated
	 *	with the most frequently occurring part of speech.
	 *	</p>
	 */

	public String getLemma( String entry )
	{
		String result	= "*";

		LexiconEntry lexiconEntry	= getLexiconEntry( entry );

		if ( lexiconEntry != null )
		{
			String lemma	=
				lexiconEntry.getLemma( lexiconEntry.largestCategory );

			if ( lemma != null )
			{
				result	= lemma;
			}
		}

		return result;
	}

	/**	Get all lemmata for an entry.
	 *
	 *	@param	entry		Entry to look up.
	 *
	 *	@return				Lemmata forms of entry.
	 */

	public String[] getLemmata( String entry )
	{
		String[] result	= new String[]{ "*" };

		LexiconEntry lexiconEntry	= getLexiconEntry( entry );
		Set<String> lemmataSet		= new TreeSet<String>();

		if ( lexiconEntry != null )
		{
			Iterator<String> iterator	=
				lexiconEntry.lemmata.keySet().iterator();

			String lemma;

			while ( iterator.hasNext() )
			{
				lemma	= lexiconEntry.lemmata.get( iterator.next() );
				if ( lemma != null ) lemmataSet.add( lemma );
			}

			result	=
				(String[])lemmataSet.toArray(
					new String[ lemmataSet.size() ] );
		}

		return result;
	}

	/**	Get lemma for an entry in a specific category.
	 *
	 *	@param	entry		Entry to look up.
	 *	@param	category	Category for which to retrieve lemma.
	 *
	 *	@return				Lemma form of entry.  An "*' is returned
	 *							if the lemma cannot be found.
	 */

	public String getLemma( String entry , String category )
	{
		String result	= "*";

		LexiconEntry lexiconEntry	= getLexiconEntry( entry );

		if ( lexiconEntry != null )
		{
			String lemma	= lexiconEntry.getLemma( category );

			if ( lemma != null )
			{
				result	= lemma;
			}
		}

		return result;
	}

	/**	Get category counts.
	 *
	 *	@return		Category counts map.
	 */

	public Map<String, MutableInteger> getCategoryCounts()
	{
		return categoryCountsMap;
	}

	/**	Get number of categories.
	 *
	 *	@return		Number of categories.
	 */

	public int getNumberOfCategories()
	{
		return categoryCountsMap.keySet().size();
	}

	/**	Get category counts for an entry.
	 *
	 *	@param	entry	Entry to look up.
	 *
	 *	@return			Map of counts for each category.  String keys are
	 *					tags, MutableInteger counts are values.
	 *
	 *					Null if entry not found in lexicon.
	 */

	public Map<String, MutableInteger> getCategoryCountsForEntry
	(
		String entry
	)
	{
		Map<String, MutableInteger> result	= null;

		LexiconEntry lexiconEntry	= getLexiconEntry( entry );

		if ( lexiconEntry != null )
		{
			result	= lexiconEntry.categoriesAndCounts;
		}

		return result;
	}

	/**	Get total count for an entry.
	 *
	 *	@param	entry		Entry to look up.
	 *
	 *	@return				Count of occurrences of entry.
	 */

	public int getEntryCount( String entry )
	{
		int result	= 0;

		LexiconEntry lexiconEntry	= getLexiconEntry( entry );

		if ( lexiconEntry != null )
		{
			result	= lexiconEntry.entryCount;
		}

		return result;
	}

	/**	Save lexicon to a file.
	 *
	 *	@param	lexiconFileName		File containing the lexicon.
	 *	@param	encoding			Character encoding of lexicon file text.
	 */

	public void saveLexiconToTextFile
	(
		String lexiconFileName ,
		String encoding
	)
		throws IOException
	{
								//	Get an outputter that writes
								//	tab separated values.

		AdornedWordOutputter outputter	=
			new PrintStreamAdornedWordOutputter();

		outputter.createOutputFile( lexiconFileName , encoding , '\t' );

								//	Sort the entries.

		Map<String, LexiconEntry> sortedLexiconMap	=
			new TreeMap<String, LexiconEntry>( lexiconMap );

								//	Loop over entries.

		for ( String entry : sortedLexiconMap.keySet() )
		{
								//	Get data for entry.

			LexiconEntry lexiconEntry	= lexiconMap.get( entry );

								//	Output entry to tab-separated file.

			outputter.outputWordAndAdornments
			(
				lexiconEntry.getLexiconEntryData()
			);
		}
								//	Close the outputter.
		outputter.close();
	}

	/**	Get the longest entry length in the lexicon.
	 *
	 *	@return		The longest entry length in the lexicon.
	 */

	public int getLongestEntryLength()
	{
		return longestEntryLength;
	}

	/**	Get the shortest entry length in the lexicon.
	 *
	 *	@return		The shortest entry length in the lexicon.
	 */

	public int getShortestEntryLength()
	{
		return shortestEntryLength;
	}

	/**	Check that all the tags in the lexicon appear in
	 *	the designated part of speech tags list.
	 *
	 *	@return		true if all tags used in lexicon appear in
	 *				designated part of speech list.
	 */

	protected boolean checkCategoriesList()
	{
		boolean result			= true;

		String[] usedCategories	= getCategories();

		for	(	int i = 0 ;
				( i < usedCategories.length ) && result;
				i++
			)
		{
			String category	= (String)usedCategories[ i ];
			result	= result && partOfSpeechTags.isTag( category );
		}

		return result;
	}

	/**	Get the part of speech tags list used by the lexicon.
	 *
	 *	@return		Part of speech tags list.
	 */

	public PartOfSpeechTags getPartOfSpeechTags()
	{
		return partOfSpeechTags;
	}

	/**	Set the part of speech tags list used by the lexicon.
	 *
	 *	@param	partOfSpeechTags	Part of speech tags list.
	 *
	 *	@return			true if all categories in lexicon appear in
	 *					the part of speech tags list.
	 *
	 *	<p>
	 *	For the check to work, the part of speech tags list should
	 *	be set after the lexicon is loaded.
	 *	</p>
	 */

	public boolean setPartOfSpeechTags( PartOfSpeechTags partOfSpeechTags )
	{
		this.partOfSpeechTags	= partOfSpeechTags;

		return checkCategoriesList();
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



