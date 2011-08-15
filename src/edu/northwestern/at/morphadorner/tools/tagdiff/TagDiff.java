package edu.northwestern.at.morphadorner.tools.tagdiff;

/*	Please see the license information at the end of this file. */

import java.net.*;
import java.io.*;
import java.text.*;
import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.math.*;

/**	Compares two tagged files and reports discrepancies.
 */

public class TagDiff
{
	/**	Map of incorrect tags and counts.
	 */

	protected static Map<String,TagCount> incorrectTags	=
		MapFactory.createNewMap();

	/**	Map of tags along with correct and incorrect counts.
	 */

	protected static Map<String,TagCount> tagCounts		=
		MapFactory.createNewMap();

	/**	Main program. */

	public static void main( String[] args )
	{
		try
		{
			compareTaggedTexts
			(
				args[ 0 ] ,
				Integer.parseInt( args[ 1 ] ) ,
				args[ 2 ] ,
				Integer.parseInt( args[ 3 ] )
			);
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}

	/**	Update tag counts.
	 *
	 *	@param	map			The map to update.
	 *	@param	tag			The tag whose counts should be updated.
	 *	@param	correct		# of correct tags.
	 *	@param	incorrect	# of incorrect tags.
	 */

	protected static void updateTagCount
	(
		Map<String,TagCount> map ,
		String tag ,
		int correct ,
		int incorrect ,
		int comparisonType
	)
	{
		TagCount tagCount	= map.get( tag );

		if ( tagCount == null )
		{
			tagCount	= new TagCount( tag );

			tagCount.setComparisonType( comparisonType );

			map.put( tag , tagCount );
		}

		tagCount.update( correct , incorrect );
	}

	/**	Compare expected and generated tags.
	 *
	 *	@param	taggedFile1		The "correctly" tagged file.
	 *	@param	taggedFile2		The "incorrectly" tagged file.
	 */

	protected static void compareTaggedTexts
	(
		String taggedFile1 ,
		int tagColFile1 ,
		String taggedFile2 ,
		int tagColFile2
	)
		throws MalformedURLException, IOException
	{
								//	Open the tagged files.

		BufferedReader tagged1Reader	=
			new BufferedReader(
				new UnicodeReader(
					new FileInputStream( taggedFile1 ) , "utf-8" ) );

		BufferedReader tagged2Reader	=
			new BufferedReader(
				new UnicodeReader(
					new FileInputStream( taggedFile2 ) , "utf-8" ) );

		BufferedOutputStream bufferedStream	=
			new BufferedOutputStream( System.out );

								//	Open the output to which to write
								//	the results.

		OutputStreamWriter outWriter	=
			new OutputStreamWriter( bufferedStream , "utf-8" );

								//	Read first line from each
								//	tagged file.

		String line1	= tagged1Reader.readLine();
		String line2	= tagged2Reader.readLine();

								//	Count of lines read from each
								//	tagged file.
		int lineCount1	= 1;
		int lineCount2	= 1;

								//	Total number of matches and
								//	mismatches.
		int matches		= 0;
		int mismatches	= 0;

								//	Loop over both tagged files.
								//	They must contain the same
								//	number of words in the same order.
								//	The first entry on each line is
								//	the word spelling, and the
								//	second is the part of speech tag.
								//

		while ( ( line1 != null ) && ( line2 != null ) )
		{
								//	Ignore empty lines.

			line1	= line1.trim();
			line2	= line2.trim();

			boolean emptyline1	= ( line1.length() == 0 );
			boolean emptyline2	= ( line2.length() == 0 );

			if ( !emptyline1 && !emptyline2 )
			{
								//	Make sure the current word
								//	is the same in both files.

				String[] tokens1	= line1.split( "\t" );
				String[] tokens2	= line2.split( "\t" );

				if ( !tokens1[ 0 ].equals( tokens2[ 0 ] ) )
				{
					System.err.println(
						"Mismatched words " +
						tokens1[ 0 ] + " and " +
						tokens2[ 0 ] + " at line " + lineCount1 +
						" in first tagged file and line " +
						lineCount2 + " in second tagged file." );

					System.err.flush();

					System.exit( 1 );
				}
								//	See if the two tags match.
								//	If not, generate
								//	a confusion matrix entry.

				if	( 	tokens1[ tagColFile1 ].equalsIgnoreCase(
							tokens2[ tagColFile2 ]  ) ||
						CharUtils.isPunctuation( tokens1[ 0 ] )
					)
				{
					matches++;

					updateTagCount(
						tagCounts , tokens1[ tagColFile1 ] , 1 , 0 , 3 );
				}
				else
				{
					mismatches++;

					updateTagCount(
						tagCounts , tokens1[ tagColFile1 ] , 0 , 1 , 3 );

					String badTags	=
						tokens2[ tagColFile2 ] + " instead of " +
						tokens1[ tagColFile1 ];

					updateTagCount( incorrectTags , badTags , 0 , 1 , 2 );
				}
			}
								//	Read the next line from each
								//	each tagged file.

			if ( !emptyline2 )
			{
				line1	= tagged1Reader.readLine();
				lineCount1++;
			}

			if ( !emptyline1 )
			{
				line2	= tagged2Reader.readLine();
				lineCount2++;
			}
		}
								//	Close the input files.

		tagged1Reader.close();
		tagged2Reader.close();

								//	Sort the confusion matrix entries
								//	into descending order by error count.

		SortedArrayList<TagCount> incorrectTagCounts	=
			new SortedArrayList<TagCount>();

		for ( String tag : incorrectTags.keySet() )
		{
			TagCount tagCount	= incorrectTags.get( tag );

			incorrectTagCounts.add( tagCount );
		}
								//	Total number of words is the
								//	number of matches plus the number
								//	of mismatches.

		int wordCount	= matches + mismatches;

								//	Display the confusion matrix
								//	linearly.  Each output line
								//	contains the error count,
								//	error %, and the tags confused.

		outWriter.write( Env.LINE_SEPARATOR );
		outWriter.write( Env.LINE_SEPARATOR );
		outWriter.write( "Counts of tagging errors." );
		outWriter.write( Env.LINE_SEPARATOR );
		outWriter.write( Env.LINE_SEPARATOR );
		outWriter.write( "            Pct." );
		outWriter.write( Env.LINE_SEPARATOR );
		outWriter.write( "     Count  Error   Tags confused" );
		outWriter.write( Env.LINE_SEPARATOR );
		outWriter.write( Env.LINE_SEPARATOR );

		TagCount tagCount;

		for ( int i = 0 ; i < incorrectTagCounts.size() ; i++ )
		{
			tagCount	= (TagCount)incorrectTagCounts.get( i );

			if ( tagCount.incorrect == 0 ) continue;

			String s	=
				StringUtils.lpad
				(
					Formatters.formatIntegerWithCommas(
						tagCount.incorrect ) ,
					10
				);

			outWriter.write( s );

			double pctOfError	=
				tagCount.incorrect / (double)mismatches;

			pctOfError			=
				Math.round( pctOfError * 1000.0D ) / 10.0D;

			s					=
				StringUtils.lpad
				(
					Formatters.formatDouble( pctOfError , 1 ) ,
					5
				);

			outWriter.write( s );
			outWriter.write( "%    " );

			outWriter.write( tagCount.tag );

			outWriter.write( Env.LINE_SEPARATOR );
		}

		double pctMatched	=
			(double)matches / (double)wordCount;

		pctMatched		=
			Math.round( pctMatched * 1000.0D ) / 10.0D;

		double pctNotMatched	=
			(double)mismatches / (double)wordCount;

		pctNotMatched	=
			Math.round( pctNotMatched * 1000.0D ) / 10.0D;

		outWriter.write( Env.LINE_SEPARATOR );

		outWriter.write(
			"Total number of words    : " + wordCount );

		outWriter.write( Env.LINE_SEPARATOR );

		outWriter.write(
			"Correctly tagged words   : " +
			matches + " (" + pctMatched + "%)" );

		outWriter.write( Env.LINE_SEPARATOR );

		outWriter.write(
			"Incorrectly tagged words : " +
			mismatches + " (" + pctNotMatched + "%)" );

		outWriter.write( Env.LINE_SEPARATOR );

		outWriter.flush();
		outWriter.close();
	}

	/**	Allow overrides but not instantiation.
	 */

	protected TagDiff()
	{
	}

	/**	Class to hold counts of correct and incorrect tags.
	 */

	static class TagCount implements Comparable
	{
		/**	The tag string.
		 */

		public String tag;

		/**	Correctly tagged count.
		 */

		public int correct;

		/**	Incorrectly tagged count.
		 */

		public int incorrect;

		/**	Comparison type.
		 *
		 * 	= 0: compare tags
		 *	= 1: compare correct counts
		 *	= 2: compare incorrect counts
		 *	= 3: compare incorrect percent
		 */

		public int comparisonType;

		/**	Create tag count object.
		 *
		 *	@param	tag		The tag string.
		 */

		public TagCount( String tag )
		{
			this.tag			= tag;
			this.correct		= 0;
			this.incorrect		= 0;
			this.comparisonType	= 0;
		}

		/**	Create tag count object with given counts.
		 *
		 *	@param	tag			The tag string.
		 *	@param	correct		Correct tag count.
		 *	@param	incorrect	Incorrect tag count.
		 */

		public TagCount( String tag , int correct , int incorrect )
		{
			this.tag			= tag;
			this.correct		= correct;
			this.incorrect		= incorrect;
			this.comparisonType	= 0;
		}

		/**	Set comparison type.
		 */

		public void setComparisonType( int comparisonType )
		{
			this.comparisonType	=
				Math.min( Math.max( comparisonType , 0 ) , 2 );
		}

		/**	Update counts.
		 *
		 *	@param	correct		Number of correct entries to add.
		 *	@param	incorrect	Number of incorrect entries to add.
		 */

		public void update( int correct , int incorrect )
		{
			this.correct	+= correct;
			this.incorrect	+= incorrect;
		}

		/**	Get percentage of incorrect tags.
		 *
		 *	@return		correct / incorrect as a percentage.
		 */

		public double percentageIncorrect()
		{
			return (double)incorrect / ( correct + incorrect );
		}

		/**	Convert to string.
		 */

		public String toString()
		{
			return tag + " " + correct + " " + incorrect;
		}

		/**	Compare this object to another.
		 *
		 *	@param	object	Other object.
		 *
		 *	@return			< 0 if the other object is greater than this one,
		 *					= 0 if the two objects are equal,
	 	 *					> 0 if the other object is less than this one.
		 *
		 *	<p>
		 *	We only compare the tags.
		 *	</p>
		 */

		public int compareTo( Object object )
		{
			int result	= Integer.MIN_VALUE;

			if ( ( object != null ) && ( object instanceof TagCount ) )
			{
				TagCount otherTagCount	= (TagCount)object;

				switch ( comparisonType )
				{
					case 1		:
					{
						result	=
							-Compare.compare(
								correct , otherTagCount.correct );

						if ( result == 0 )
						{
							result	=
								Compare.compare( tag , otherTagCount.tag );
						}

						break;
                    }

					case 2		:
					{
						result	=
							-Compare.compare(
								incorrect , otherTagCount.incorrect );

						if ( result == 0 )
						{
							result	=
								Compare.compare( tag , otherTagCount.tag );
						}

						break;
                    }

					case 3		:
					{
						result	=
							-Compare.compare(
								percentageIncorrect() ,
								otherTagCount.percentageIncorrect() );

						if ( result == 0 )
						{
							result	=
								Compare.compare( tag , otherTagCount.tag );
						}

						break;
                    }

					default	:
					{
						result	=
							Compare.compare( tag , otherTagCount.tag );
					}
				}
			}

			return result;
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



