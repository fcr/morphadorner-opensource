package edu.northwestern.at.morphadorner.tools.createsuffixlexicon;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.lexicon.*;

/**	Generate suffix lexicon from a word lexicon.
 *
 *	<p>
 *	<code>
 *	java -Xmx512m edu.northwestern.at.morphadorner.tools.createsuffixlexicon.CreateSuffixLexicon
 *	inputwordlexicon outputsuffixlexicon maxsuffixlength maxsuffixcount
 *	</code>
 *	</p>
 *
 *  <ul>
 *	<li>
 *	<p>
 *	<strong>inputwordlexicon</strong> specifies the name of the
 *	input file containng the word lexicon from which to extract
 *	a suffix lexicon.
 *	</p>
 *	</li>
 *
 *	<li>
 *	<p>
 *	<strong>outputsuffixlexicon</strong> specifies the name of the
 *	output file to receive tthe suffix lexicon.
 *	</p>
 *	</li>
 *
 *	<li>
 *	<p>
 *	<strong>maxsuffixlength</strong> specifies the maximum length
 *	suffix generated for the suffix lexicon.  The default is 6.
 *	</p>
 *	</li>
 *
 *	<li>
 *	<p>
 *	<strong>maxsuffixcount</strong> specifies the maximum number
 *	of times a spelling can appear in order for its suffix to be
 *	added to the suffix lexicon.  The default is to include
 *	all words regardless of count.
 *	</p>
 *
 *	<p>
 *	For some applications you may want to restrict the
 *	suffix lexicon to contain suffixes only for infrequently
 *	occurring words.  Values of 10 (only include spellings which appear
 *	10 or less times in the training data) or 1 (only include spellings
 *	which appear once in the training data) are popular choices.
 *	</p>
 *	</li>
 *	</ul>
 */

public class CreateSuffixLexicon
{
	/**	Input word lexicon file name. */

	protected static String wordLexiconFileName;

	/**	Output suffix lexicon file name. */

	protected static String suffixLexiconFileName;

	/**	Only use words less than maxSuffixCount to generate
	 *	suffix lexicon.
	 *
	 *	<p>
	 *	The default is to use all words regardless of word count.
	 *	</p>
	 */

	protected static int maxSuffixCount	= Integer.MAX_VALUE;

	/**	Maximum and minimum length suffixes to generated.
	 */

	protected static int maxSuffixLength		= 6;
	protected static int minSuffixLength		= 1;

	/**	Generate suffix lexicon.
	 */

	private static void generateSuffixLexicon()
		throws IOException
	{
		long startTime			= System.currentTimeMillis();

								//	Load word lexicon.

		BaseLexicon wordLexicon			=
			new BaseLexicon();
								//	Load default word lexicon.

		wordLexicon.loadLexicon
		(
			new File( wordLexiconFileName ).toURI().toURL() ,
			"utf-8"
		);
								//	Create empty suffix lexicon.

		BaseLexicon suffixLexicon	= new BaseLexicon();

								//	Generate suffix lexicon from
								//	word lexicon.  We can't do this
								//	in parallel with the word lexicon
								//	creation because we need the
								//	word counts.

		String[] entries	= wordLexicon.getEntries();

		for ( int i = 0 ; i < entries.length ; i++ )
		{
			String entry		= entries[ i ];
			int entryCount	= wordLexicon.getEntryCount( entry );

			if ( entryCount <= maxSuffixCount )
			{
				String lowerCaseEntry	= entry.toLowerCase();

				int l						= lowerCaseEntry.length();

				Map<String, MutableInteger> categoryCounts		=
					wordLexicon.getCategoryCountsForEntry( entry );

								//	Loop over each category for the
								//	entry.

				Iterator<String> iterator	=
					categoryCounts.keySet().iterator();

				while ( iterator.hasNext() )
				{
								//	Get category name and count.

					String categoryName	= iterator.next();

								//	Get the category count.

					int categoryCount	=
						categoryCounts.get( categoryName ).intValue();

								//	Extract suffixes of decreasing length
								//	and add to suffix lexicon.

					for	(	int j = maxSuffixLength ;
						 	j > minSuffixLength - 1 ;
						 	j--
						)
					{
						if ( lowerCaseEntry.length() > j )
						{
							suffixLexicon.updateEntryCount
							(
								lowerCaseEntry.substring( l - j , l ) ,
								categoryName ,
								"*" ,
								categoryCount
							);
						}
					}
				}
			}
		}
								//	Output suffix lexicon.
		System.out.println(
			"Writing suffix lexicon to " + suffixLexiconFileName + " .");

		System.out.println(
			"   Maximum suffix length is " + maxSuffixLength + " .");

		if ( maxSuffixCount == Integer.MAX_VALUE )
		{
			System.out.println(
				"   Suffixes generated from all spellings." );
		}
		else
		{
			System.out.println(
				"   Suffixes generated from spellings appearing no more than " +
				Formatters.formatIntegerWithCommas( maxSuffixCount ) +
				" time" + ( maxSuffixCount == 1 ? "" : "s" ) +
				"." );
		}

		suffixLexicon.saveLexiconToTextFile(
			suffixLexiconFileName , "utf-8" );

								//	Report time and statistics for
								//	creation.
		long endTime	=
			( System.currentTimeMillis() - startTime + 999 ) / 1000;

		System.out.println(
			"Lexicon generated in " +
			Formatters.formatLongWithCommas( endTime ) + " seconds." );

		System.out.println(
			"Suffix lexicon contains " +
			Formatters.formatIntegerWithCommas(
				suffixLexicon.getLexiconSize() ) +
			" entries." );

		wordLexicon.close();
		suffixLexicon.close();
	}

	/**	Display brief help.
	 */

	protected static void help()
	{
		System.out.println( "Usage: " );
		System.out.println( "" );
		System.out.println( "java -Xmx512m edu.northwestern.at.createlexicon.CreateSuffixLexicon ^" );
		System.out.println( "   inputwordlexicon outputsuffixlexicon maxsuffixlength maxsuffixcount" );
		System.out.println( "" );
		System.out.println( "-- inputwordlexicon specifies the input word lexicon (required)." );
		System.out.println( "-- outputsuffixlexicon receives output suffix lexicon (required)." );
		System.out.println( "-- maxsuffixlength is maximum length suffix to generate (optional, default is 6)." );
		System.out.println( "-- maxsuffixcount is maximum count for spelling to include in suffix lexicon (optional, default is no maximum)." );
	}

	/**	Initialize.
	 *
	 *	@param	args	Command line arguments.
	 */

	protected static boolean initialize( String[] args )
	{
		boolean result	= true;

		if ( args.length < 2 )
		{
			result	= false;

			help();
		}
		else
		{
			wordLexiconFileName	= args[ 0 ];
			suffixLexiconFileName	= args[ 1 ];

			if ( args.length > 2 )
			{
				try
				{
					maxSuffixLength	= Integer.parseInt( args[ 2 ] );
				}
				catch ( Exception e )
				{
					result	= false;

					System.out.println( "Bad maximum suffix length." );
				}
			}

			if ( args.length > 4 )
			{
				try
				{
					maxSuffixCount	= Integer.parseInt( args[ 3 ] );
				}
				catch ( Exception e )
				{
					result	= false;

					System.out.println( "Bad maximum suffix count." );
				}
			}
		}

		return result;
	}

	/**	Main program.
	 *
	 *	@param	args	Command line arguments.
	 */

	public static void main( String args[] )
	{
								//	If initialization succeeds ...
		int returnCode	= 0;

		if ( initialize( args ) )
		{
								//	Generate the lexicons.
			try
			{
				generateSuffixLexicon();
			}
			catch ( Exception e )
			{
				e.printStackTrace();

				returnCode	= 1;
			}
		}
								//	Halt with error 1 if any error.

		if ( returnCode != 0 )
		{
			System.exit( returnCode );
		}
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



