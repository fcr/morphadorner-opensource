package edu.northwestern.at.utils.corpuslinguistics.spellingstandardizer;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.spellcheck.*;
import edu.northwestern.at.utils.corpuslinguistics.phonetics.*;
import edu.northwestern.at.utils.corpuslinguistics.stringsimilarity.*;

/**	ExtendedSearchSpellingStandardizer: extended search spelling standardizer.
 *
 *	<p>
 *	ExtendedSearchSpellingStandardizer uses spelling correction
 *	methods to try to find a good list of suggested standardized
 *	spellings.
 *	</p>
 */

public class ExtendedSearchSpellingStandardizer
	extends ExtendedSimpleSpellingStandardizer
	implements SpellingStandardizer
{
	/**	Spelling checker. */

	protected SpellingChecker spellingChecker	=
		new TernaryTrieBasedSpellingChecker();

	/**	Double metaphone encoder. */

	protected DoubleMetaphone doubleMetaphone	= new DoubleMetaphone();

	/**	Create extended search spelling standardizer.
	 */

	public ExtendedSearchSpellingStandardizer()
	{
		super();
	}

	/**	Creates dictionaries from spelling lists.
	 */

	public void createDictionaries()
	{
		SpellingDictionary globalDictionary	= null;
		SpellingDictionary localDictionary	= null;

		long startTime = System.currentTimeMillis();

		try
		{
			globalDictionary		=
				new TernaryTrieSpellingDictionary
				(
					mappedSpellings
				);

			localDictionary			=
				new TernaryTrieSpellingDictionary
				(
					standardSpellingSet
				);
		}
		catch ( Exception e )
		{
//			e.printStackTrace();

//			System.err.println(
//				"Unable to load dictionaries of alternate and standard spellings" );
		}

		long seconds	=
			( System.currentTimeMillis() - startTime + 999 ) / 1000;

								//	Set dictionaries into spelling checker.

		spellingChecker.useGlobalDictionary( globalDictionary );
		spellingChecker.useLocalDictionary( localDictionary );
	}

	/**	Loads alternative spellings from a reader.
	 *
	 *	@param	reader		The reader.
	 *	@param	delimChars	Delimiter characters separating spelling pairs.
	 */

	public void loadAlternativeSpellings
	(
		Reader reader ,
		String delimChars
	)
		throws IOException
	{
		super.loadAlternativeSpellings( reader , delimChars );

		createDictionaries();
	}

	/**	Loads standard spellings from a reader.
	 *
	 *	@param	reader		The reader.
	 */

	public void loadStandardSpellings
	(
		Reader reader
	)
		throws IOException
	{
		super.loadStandardSpellings( reader );

		try
		{
			SpellingDictionary localDictionary			=
				new TernaryTrieSpellingDictionary
				(
					standardSpellingSet
				);
								//	Set dictionary into spelling checker.

			spellingChecker.useLocalDictionary( localDictionary );
		}
		catch ( Exception e )
		{
			e.printStackTrace();

			System.err.println(
				"Unable to load dictionary of standard spellings." );
		}
	}

	/**	Apply heuristics to spellings to see if we can find a match..
	 *
	 *	@param	spelling	Spelling to which to apply heuristics.
	 *
	 *	@return				Near matches after applying heuristics.
	 */

	public String[] applyHeuristics( String spelling )
	{
		String[] results	= new String[ 0 ];

		if ( spelling.length() > 1 )
		{
			if	(	( spelling.charAt( 0 ) == 'y' ) &&
			 		( !CharUtils.isEnglishVowel( spelling.charAt( 1 ) ) ) )
			{
				String newSpelling	=
					'i' + spelling.substring( 1 , spelling.length() );

				results	= spellingChecker.suggest( newSpelling );
			}

			if ( results.length == 0 )
			{
				if	(	( spelling.charAt( 0 ) == 'v' ) &&
				 		( !CharUtils.isEnglishVowel( spelling.charAt( 1 ) ) ) )
				{
					String newSpelling	=
						'u' + spelling.substring( 1 , spelling.length() );

					results	= spellingChecker.suggest( newSpelling );
				}
			}

			if ( results.length == 0 )
			{
				if ( spelling.indexOf( "uu" ) >= 0 )
				{
					String newSpelling	=
						StringUtils.replaceAll( spelling , "uu" , "w" );

					results	= spellingChecker.suggest( newSpelling );
				}
			}

			if ( results.length == 0 )
			{
				if	( spelling.endsWith( "blind" ) &&
					( spelling.length() > 5 ) )
				{
					String newSpelling	=
						spelling.substring( 0 , spelling.length() - 5 ) +
						"-blind";

					results	= spellingChecker.suggest( newSpelling );
				}
			}

			if ( results.length == 0 )
			{
				results	=
					((TernaryTrieBasedSpellingChecker)spellingChecker).suggestMore( spelling );
			}
		}

		return results;
	}

	/**	Apply simple string replacement.
	 *
	 *	@param	spelling		The spelling.
	 *	@param	pattern			String of characters to look for in spelling.
	 *	@param	replacement		Replacement characters.
	 *
	 *	@return					If revised spelling in spelling map,
	 *							return revised spelling.  Otherwise
	 *							return empty string.
	 */

	public String simpleReplacement
	(
		String spelling ,
		String pattern ,
		String replacement
	)
	{
		String result = "";

		if	( spelling.indexOf( pattern ) >= 0 )
		{
			String newSpelling	=
				spelling.replaceAll( pattern , replacement );

		 	if( mappedSpellings.containsString( newSpelling ) )
	 		{
	 			result	= mappedSpellings.getTag( newSpelling );
	 		}
		}

		return result;
	}

	/**	Apply "long s" heuristics to a spelling.
	 *
	 *	@param	spelling	Spelling suggestion to which to apply heuristics.
	 *
	 *	@return				Revised spelling.
	 */

	public String longSVariant( String spelling )
	{
/*
try ff for ss
try ff for sf or fs
try ss for ff
try ss for sf or fs
*/
		String result	= simpleReplacement( spelling , "ff" , "ss" );

		if ( result.length() == 0 )
		{
			result	= simpleReplacement( spelling , "sf" , "ss" );
		}

		if ( result.length() == 0 )
		{
			result	= simpleReplacement( spelling , "fs" , "ss" );
		}

		if ( result.length() == 0 )
		{
			result	= simpleReplacement( spelling , "ss" , "ff" );
		}

		if ( result.length() == 0 )
		{
			result	= simpleReplacement( spelling , "sf" , "ff" );
		}

		if ( result.length() == 0 )
		{
			result	= simpleReplacement( spelling , "fs" , "ff" );
		}

		return result;
	}

	/**	Preprocess spelling.
	 *
	 *	@param	spelling	Spelling to preprocess.
	 *
	 *	@return				Preprocessed spelling.
	 */

	public String preprocessSpelling( String spelling )
	{
		return EnglishDecruftifier.decruftify( spelling );
	}

	/**	Returns standard spellings given a spelling.
	 *
	 *	@param	spelling	The spelling.
	 *
	 *	@return				The standard spellings as an array of String.
	 */

	 public String[] standardizeSpelling( String spelling )
	 {
    	String result	= doStandardizeSpelling( spelling , false )[ 0 ];
		String lcResult	= result.toLowerCase();

		if	(	!mappedSpellings.containsString( lcResult ) &&
				!standardSpellingSet.contains( lcResult )
			)
		{
 			String best	= getBestSuggestedSpelling( result );

			if ( !best.equals( "?" ) )
			{
			 	if ( mappedSpellings.containsString( best ) )
				{
					result	= mappedSpellings.getTag( best );
		 		}
				else
				{
					result	= best;

					addCachedSpelling( spelling , result );
				}
		 	}
		}

		result	= fixCapitalization( spelling , result );

	 	return new String[]{ result };
	 }

	/**	Get best suggested spelling.
	 *
	 *	@param	spelling		The spelling for which to return suggestion.
	 *
	 *	@return					Best (by score) suggested spellings.
	 */

	public String getBestSuggestedSpelling( String spelling )
	{
		String result		= spelling;

								//	Get scored list of suggested spellings.

		List<ScoredString> suggestions	=
			new SortedArrayList<ScoredString>
			(
				getScoredSuggestedSpellings( spelling )
			);
								//	If we got suggestions, return the
								//	one with the highest score.

		if ( suggestions.size() > 0 )
		{
			ScoredString suggestion	=
				(ScoredString)suggestions.get( suggestions.size() - 1 );

			result	= suggestion.getString();
        }

 		return result;
	}

	/**	Return suggested spellings.
	 *
	 *	@param	spelling		The spelling for which to return suggestions.
	 *
	 *	@return					List suggested spellings with scores.
	 */

	public List<ScoredString> getScoredSuggestedSpellings( String spelling )
	{
								//	Result list with scored suggestions.

		List<ScoredString> result	= ListFactory.createNewList();

								//	Maximum edit distance score.

		double maxScore			= 0.0D;

								//	Maximum score adjusted for
								//	phonetic match.

		double maxAdjScore		= 0.0D;

								//	Index of best suggestion matching
								//	the spelling.

		int choice				= 0;

								//	Get list of suggested
								//	spellings.

		String[] suggestions	= getSuggestedSpellings( spelling );

								//	Get phonetic values for spelling.

		doubleMetaphone.encode( spelling );

		String dm1	= doubleMetaphone.getPrimary();
		String dm2	= doubleMetaphone.getAlternate();

								//	Run over list of suggestions
								//	and score each for similarity
								//	with the original spelling.

 		for ( int i = 0 ; i < suggestions.length ; i++ )
 		{
								//	Compute string similarity of
								//	spelling to suggestion based upon
								//	edit distance.

			double score	=
				JaroWinkler.jwSimilarity( spelling , suggestions[ i ] )
				+
				LetterPairSimilarity.letterPairSimilarity(
					spelling , suggestions[ i ] )
				;

			score	= score / 2.0D;

								//	Remember maximum score.

 			maxScore	= Math.max( maxScore , score );

								//	Get phonetic values for
								//	suggestion.

			doubleMetaphone.encode( suggestions[ i ] );

								//	If suggestion matches one of the
								//	phonetic values for the spelling,
								//	raise the score of the suggestion.

			if (	dm1.equals( doubleMetaphone.getPrimary() ) ||
				    dm2.equals( doubleMetaphone.getAlternate() ) )
			{
				score	+= score + 0.2D;
			}
								//	Give a little more weight to
								//	suggestions of same length as
								//	original.

			if ( suggestions[ i ].length() == spelling.length() )
			{
				score	+= 0.1D;
			}

			double denomLength	=
				3.0D * Math.min(
					spelling.length() , suggestions[ i ].length() );

			double tieBreaker	=
				LetterEquivalence.letterEquivalence(
					spelling , suggestions[ i ] ) +
				StringUtils.matchingInitialCharacters(
					spelling , suggestions[ i ] ) +
				StringUtils.matchingFinalCharacters(
					spelling , suggestions[ i ] );

			score	+= ( tieBreaker / denomLength );

								//	Remember maximum adjusted score.

 			if ( score > maxAdjScore )
 			{
				maxAdjScore	= Math.max( maxAdjScore , score );

								//	If this suggestion has the highest
								//	adjusted score so far, make it the
								//	candidate best choice for
								//	standard spelling.

 				choice		= i;
 			}
								//	Add scored suggestion to results.

			result.add( new ScoredString( suggestions[ i ] , score ) );
 		}
								//	If the adjusted maximum score
								//	is greater than the maximum score,
								//	normalize all the scores using
								//	the factor (maxScore / adjMaxScore).

		if ( maxAdjScore > maxScore )
		{
			double factor	= ( maxScore / maxAdjScore );

	 		for ( int i = 0 ; i < result.size() ; i++ )
 			{
 				ScoredString scoredString	= (ScoredString)result.get( i );

 				scoredString.setScore( scoredString.getScore() * factor );
 			}
		}
								//	Return results list.
 		return result;
	}

	/**	Return suggested spellings.
	 *
	 *	@param	spelling		The spelling for which to return suggestions.
	 *
	 *	@return					Array of strings of suggested spellings.
	 */

	public String[] getSuggestedSpellings( String spelling )
	{
		String[] suggestions	= new String[]{ "?" };

		if ( !spellingChecker.checkSpelling( spelling ) )
		{
			suggestions	= spellingChecker.suggest( spelling );

			if ( suggestions.length == 0 )
			{
				suggestions	= applyHeuristics( spelling );
			}

			switch ( suggestions.length )
			{
				case 0	:	break;

				case 1	:	String suggestion	= suggestions[ 0 ];

							if ( mappedSpellings.getTag( suggestion ) != null )
							{
								suggestion	=
									mappedSpellings.getTag( suggestion );

								suggestions	=
									new String[]{ suggestion };
							}

							break;

				default	:	TreeSet<String> sugs	= new TreeSet<String>();

							for
							(
								int j = 0 ;
								j < suggestions.length ;
								j++
							)
							{
								suggestion	= suggestions[ j ];

								if ( mappedSpellings.getTag( suggestion ) != null )
								{
									suggestion	=
										mappedSpellings.getTag( suggestion );
								}

								sugs.add( suggestion );
							}

							suggestions	=
								(String[])sugs.toArray( new String[]{} );

							break;
            }
		}

		return suggestions;
	}

	/**	Return standardizer description.
	 *
	 *	@return		Standardizer description.
	 */

	public String toString()
	{
		return "Extended Search Spelling Standardizer";
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



