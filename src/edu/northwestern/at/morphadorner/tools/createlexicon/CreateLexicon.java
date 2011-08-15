package edu.northwestern.at.morphadorner.tools.createlexicon;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.lexicon.*;

/**	Generate lexicons from training data.
 *
 *	<p>
 *	<code>
 *	java -Xmx512m edu.northwestern.at.morphadorner.tools.createlexicon.CreateLexicon trainingdata
 *	outputwordlexicon outputsuffixlexicon maxsuffixlength maxsuffixcount
 *	</code>
 *	</p>
 *
 *  <ul>
 *	<li>
 *	<p>
 *	<strong>trainingdata</strong> specifies the name of the file
 *	containing the part of speech training data from which the
 *	word lexicon and suffix lexicon are built.  The word lexicon
 *	contains each spelling (and standard spellings if provided),
 *	the count for each spelling, the parts of speech for each spelling,
 *	the counts for each part of speech for each spelling, and the
 *	lemma for each part of speech for each spelling (if provided).
 *	The suffix lexicon contains a list of suffixes, their counts,
 *	and the parts of speech associated with each suffix and the count
 *	of each part of speech.  Lemmata are stored as a "*' in the
 *	suffix lexicon since there are no lemmata for suffixes.
 *	</p>
 *
 *	<p>
 *	The training data resides in a utf-8 text file.  Each line contains
 *	one tab-separated spelling along with its part of speech tag and
 *	optionally its lemma and standard spelling in the form:
 *	</p>
 *
 *	<p>
 *	<code>
 *	spelling<tab>part-of-speech-tag<tab>lemma<tab>standardspelling
 *	</code>
 *	</p>
 *
 *	<p>
 *	You must specify a spelling and a part of speech tag.
 *	The lemma and standard spelling are optional.   If you wish to
 *	specify a standard spelling without specifying a lemma, enter
 *	the lemma as "*".
 *	</p>
 *
 *	<p>
 *	Blank lines are used to separate sentences.  While the blank
 *	lines are not needed for creating the lexicon, they are needed
 *	for creating probability transition matrices and for part of
 *	speech tagging.
 *	</p>
 *
 *	<p>
 *	The lexicon is built using both the spelling and the standard
 *	spelling (when provided).  The lemma is also stored when present.
 *	</p>
 *	</li>
 *
 *	<li>
 *	<p>
 *	<strong>outputwordlexicon</strong> specifies the name of the
 *	output file to receive the word lexicon.
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

public class CreateLexicon
{
	/**	Training data file name. */

	protected static String trainingDataFileName;

	/**	Output word lexicon file name. */

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

	/**	Generate lexicon.
	 */

	private static void generateLexicon()
		throws IOException
	{
		long startTime			= System.currentTimeMillis();

		System.out.println(
			"Reading training data from " + trainingDataFileName + " .");

								//	Open training data for input.

		BufferedReader reader	=
			new BufferedReader
			(
				new UnicodeReader
				(
					new FileInputStream( trainingDataFileName ) ,
					"utf-8"
				)
			);
								//	Create empty word lexicon.

		BaseLexicon wordLexicon		= new BaseLexicon();

								//	Create empty suffix lexicon.

		BaseLexicon suffixLexicon	= new BaseLexicon();

								//	Count input lines read for status
								//	display.

		int linesRead				= 0;

								//	Count number of bad input lines.

		int badLinesRead			= 0;

								//	Get first line of training data.

		String line					= reader.readLine();

		while ( line != null )
		{
								//	Trim input line.  Process it if
								//	it is not empty.

			line	= line.trim();

			if ( line.length() > 0 )
			{
								//	Split input line into tokens for
								//	spelling, part of speech,
								//	lemma (optional), and standard
								//	spelling (optional).  Also trim
								//	white space from each token.

				String[] tokens	= line.split( "\t" );

				for ( int i = 0 ; i < tokens.length ; i++ )
				{
					tokens[ i ]	= tokens[ i ].trim();
				}
								//	Each line should have at least two
								//	tokens.
								//
								//	The first is the spelling.
								//	The second is the part of speech.
								//
								//	The optional third token is the lemma.
								//	The optional fourth token is the
								//	standard spelling.
								//
								//	Lines with only a single non-blank token
								//	must be punctuation.  The second,
								//	third, and fourth tokens are set to the
								//	punctuation in this case.

				String spelling	= "";
				String pos		= "";
				String lemma	= "";
				String standard	= "";

				switch ( tokens.length )
				{
					case 1:
						spelling	= tokens[ 0 ];
						pos		= tokens[ 0 ];
						lemma	= tokens[ 0 ];
						break;

					case 2:
						spelling	= tokens[ 0 ];
						pos		= tokens[ 1 ];
						break;

					case 3:
						spelling	= tokens[ 0 ];
						pos		= tokens[ 1 ];
						lemma	= tokens[ 2 ];
						break;

					case 4:
						spelling		= tokens[ 0 ];
						pos			= tokens[ 1 ];
						lemma		= tokens[ 2 ];
						standard	= tokens[ 3 ];
						break;

					default:
						break;
				}
								//	We must have a spelling and
								//	part of speech.

			 	if ( ( spelling.length() > 0 ) && ( pos.length() > 0 ) )
				{
								//	Check if spelling is punctuation
								//	or symbol.

					boolean	isPunc	=
						CharUtils.isPunctuationOrSymbol( spelling ) &&
						!spelling.equals( "&" );

								//	If punctuation/symbol, the pos and lemma
								//	are set to the spelling, except for
								//	"&".

					if ( isPunc )
					{
						pos			= spelling;
						lemma		= spelling;
						standard	= "";
					}
					else if ( spelling.equals( "&" ) )
					{
						standard	= "";
					}
								//	Update lexicon with this
								//	spelling, pos, and lemma.

					wordLexicon.updateEntryCount(
						spelling , pos , lemma , 1 );

								//	If we have a standard spelling,
								//	update lexicon with standard
								//	spelling and the pos and lemma
								//	from the actual spelling.

					if ( standard.length() > 0 )
					{
						wordLexicon.updateEntryCount(
							standard , pos , lemma , 1 );
					}
				}
				else
				{
					System.out.println
					(
						"   Skipping bad input line <" + line + ">"
					);

					badLinesRead++;
				}
			}
								//	Increment count of lines read.

			linesRead++;
								//	Get next line of training data.

			line = reader.readLine();
		}
								//	Close training data file.
		reader.close();
								//	Tell how many input lines processed.
		System.out.println(
			"Processed " + Formatters.formatIntegerWithCommas( linesRead ) +
			" input lines." );
								//	Tell how many input lines skipped
								//	because of errors.
		if ( badLinesRead > 0 )
		{
			System.out.println(
				"Skipped " +
				Formatters.formatIntegerWithCommas( badLinesRead ) +
				" badly formed input lines." );
		}
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
								//	Output word lexicon.
		System.out.println(
			"Writing word lexicon to " + wordLexiconFileName + " .");

		wordLexicon.saveLexiconToTextFile(
			wordLexiconFileName , "utf-8" );

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
			"Lexicons generated in " +
			Formatters.formatLongWithCommas( endTime ) + " seconds." );

		System.out.println(
			"Word lexicon contains " +
			Formatters.formatIntegerWithCommas(
				wordLexicon.getLexiconSize() ) +
			" entries." );

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
		System.out.println( "java -Xmx512m edu.northwestern.at.createlexicon.CreateLexicon trainingdata" );
		System.out.println( "   outputwordlexicon outputsuffixlexicon maxsuffixlength maxsuffixcount" );
		System.out.println( "" );
		System.out.println( "-- training data contains input training data in utf-8 encoding (required)." );
		System.out.println( "-- outputwordlexicon receives output word lexicon (required)." );
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

		if ( args.length < 3 )
		{
			result	= false;

			help();
		}
		else
		{
			trainingDataFileName	= args[ 0 ];
			wordLexiconFileName	= args[ 1 ];
			suffixLexiconFileName	= args[ 2 ];

			if ( args.length > 3 )
			{
				try
				{
					maxSuffixLength	= Integer.parseInt( args[ 3 ] );
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
					maxSuffixCount	= Integer.parseInt( args[ 4 ] );
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
				generateLexicon();
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



