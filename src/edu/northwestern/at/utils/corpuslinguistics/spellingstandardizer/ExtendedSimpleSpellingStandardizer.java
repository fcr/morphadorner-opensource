package edu.northwestern.at.utils.corpuslinguistics.spellingstandardizer;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.spellcheck.*;
import edu.northwestern.at.utils.corpuslinguistics.phonetics.*;
import edu.northwestern.at.utils.corpuslinguistics.stringsimilarity.*;

/**	SimpleSpellingStandardizer maps alternate spellings
 *	to standard spellings.
 */

public class ExtendedSimpleSpellingStandardizer
	extends SimpleSpellingStandardizer
	implements SpellingStandardizer
{
	/**	Gap filler.  Only allocated if needed. */

	protected GapFiller gapFiller	= null;

	/**	Create extended simple spelling standardizer.
	 */

	public ExtendedSimpleSpellingStandardizer()
	{
		super();
	}

	/**	Fix gaps in a word.
	 *
	 *	@param	word	Word with gaps.
	 *
	 *	@return				Word with gaps possibly filled.
	 *						Original word return if gaps cannot be
	 *						filled.
	 */

	protected String fixGaps( String word )
	{
		String result	= word;

//		System.out.println( "fixGaps: entered with word=<" + word + ">" );

								//	If there is no gap filler yet,
								//	create one.

		if ( gapFiller == null )
		{
								//	Add mapped spellings.

			gapFiller	= new GapFiller( mappedSpellings );

								//	Add standard spellings.

			gapFiller.addWords( standardSpellingSet );

								//	Add words in lexicon, if any.

			if ( lexicon != null )
			{
				gapFiller.addWords( lexicon.getEntries() );
			}
		}
			                    //	Get list of candidate words with
			                    //	gap filled.

		List<String> candidates	= gapFiller.getMatchingWords( result );

//		System.out.println( "fixGaps: candidates=" + candidates );

								//	if no candidates, return original
								//	word with gaps.

		if ( candidates.size() == 0 )
		{
		}
								//	If one candidate, return it.

		else if ( candidates.size() == 1 )
		{
			result	= candidates.get( 0 );
		}
								//	If two candidates, differing
								//	only in case, return the lower case
								//	version..

		else if (  ( candidates.size() == 2 ) &&
			candidates.get( 0 ).equalsIgnoreCase( candidates.get( 1 ) )
		)
		{
			result	= candidates.get( 0 ).toLowerCase();
		}
								//	Otherwise, prune list by
								//	selecting only the candidates which
								//	are standard spellings.
		else
		{
			List<String> reducedCandidates	=
				ListFactory.createNewList();

			for ( int j = 0 ; j < candidates.size() ; j++ )
			{
				String candidate	= candidates.get( j );

				if ( standardSpellingSet.contains( candidate ) )
				{
					reducedCandidates.add( candidate );
				}
			}

//			System.out.println( "fixGaps: reduced candidates=" + candidates );

              					//	If only one candidate left, that's
              					//	the one to return.

			if ( reducedCandidates.size() == 1 )
			{
				result	= reducedCandidates.get( 0 );
			}
		}

//		System.out.println( "fixGaps: exited with result=<" + result + ">" );

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
		String result	=
			EnglishDecruftifier.simpleDecruftify( spelling );

		if ( result.endsWith( "in'" ) )
		{
			result	= result.substring( 0 , result.length() - 3 ) + "ing";
		}
								//	Does the spelling contain one or
								//	more gap characters?

		if ( CharUtils.hasGapMarkers( result ) )
		{
			result	= fixGaps( result );
		}

		return result;
	}

	/**	Returns standard spellings given a spelling.
	 *
	 *	@param	spelling	The spelling.
	 *	@param	addToCache	Add standardized spelling to spelling map.
	 *
	 *	@return				The standard spellings as an array of String.
	 */

	protected String[] doStandardizeSpelling
	(
		String spelling ,
		boolean addToCache
	)
	{
		String result	=
			super.standardizeSpelling( spelling )[ 0 ];

		if ( !standardSpellingSet.contains( result.toLowerCase() ) )
		{
			String lowerCaseSpelling	= result.toLowerCase();

 			result	= preprocessSpelling( lowerCaseSpelling );

			if ( mappedSpellings != null )
			{
				if ( mappedSpellings.containsString( result ) )
			 	{
 					result	= mappedSpellings.getTag( result );
	 			}
	 			else
	 			{
		 			if ( addToCache ) addCachedSpelling( spelling , result );
	 			}
	 		}
		}

		result	= fixCapitalization( spelling , result );

		return new String[]{ result };
	}

	/**	Returns standard spellings given a spelling.
	 *
	 *	@param	spelling	The spelling.
	 *
	 *	@return				The standard spellings as an array of String.
	 */

	 public String[] standardizeSpelling( String spelling )
	 {
	 	return doStandardizeSpelling( spelling , true );
	 }

	/**	Return standardizer description.
	 *
	 *	@return		Standardizer description.
	 */

	public String toString()
	{
		return "English Extended Simple Spelling Standardizer";
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



