package edu.northwestern.at.morphadorner.tools.comparestringcounts;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.statistics.*;
import edu.northwestern.at.utils.math.*;

/**	Compare string counts in two files using Dunning's log-likelihood.
 *	<p>
 *	Usage:
 *	</p>
 *
 *	<pre>
 *	java edu.northwestern.at.morphadorner.tools.comparestringcounts.CompareStringCounts analysis.tab reference.tab
 *	</pre>
 *
 *	<p>
 *	analysis.tab -- Input tab-separated file of strings and counts
 *	for an analysis text.<br />
 *	reference.tab -- Input tab-separated file of strings and counts
 *	for a reference text.<br />
 *	</p>
 *
 *	<p>
 *	The analysis.tab and reference.tab files contain strings and
 *	counts of those strings compiled from two texts or corpora.
 *	Both files contain two tab-separated columns.
 *	The first column is a string.
 *	The second column contains the count of the number
 *	of times that string occurred in the associated text.
 *	</p>
 *
 *	<p>
 *	The output contains seven tab-separated columns,
 *	sorted in descending order by log-likelihood value.
 *	One line of output appears for each string in the
 *	analysis text.
 *	</p>
 *
 *	<ol>
 *	<li>
 *	The first column contains the string.  This may be
 *	a spelling, a lemma, a part of speech, a spelling bigram,
 *	or any other string of interest.
 *	</li>
 *
 *	<li>
 *	The second column contains a "+" when the string is overused in
 *	the analysis text with respect to the reference text, a "-" when the
 *	string is underused, and a blank when the string is used the same
 *	amount in both texts.
 *	</li>
 *
 *	<li>
 *	The third column contains Dunning's log-likelihood value.
 *	</li>
 *
 *	<li>
 *	The fourth column shows the relative frequency of occurrence of the string
 *	in the analysis text as fractional parts per ten thousand.
 *	</li>
 *
 *	<li>
 *	The fifth column shows the relative frequency of occurrence of the string in
 *	the reference text as fractional parts per ten thousand.
 *	</li>
 *
 *	<li>
 *	The sixth column shows the number of times the string
 *	occurred in the analysis text.
 *	</li>
 *
 *	<li>
 *	The seventh column shows the number of times the string
 *	occurred in the reference text.
 *	</li>
 *
 *	</ol>
 *	<p>
 *	These results are written to the standard output file which can be
 *	redirected to another file.  A brief summary of the analysis is written
 *	to the standard error file.
 *	</p>
 *
 */

public class CompareStringCounts
{
	/**	Main program. */

	public static void main( String[] args )
	{
								//	Must have two input file names.
								//	Display program usage if two
								//	arguments are not provided.

		if ( args.length >= 2 )
		{
			new CompareStringCounts( args );
		}
		else
		{
			displayUsage();
			System.exit( 1 );
		}
	}

	/**	Display brief program usage.
	 */

	public static void displayUsage()
	{
		System.out.println( "" );
		System.out.println( "Compare string counts in two files using " +
			"Dunning's log-likelihood." );
		System.out.println( "Usage: " );
		System.out.println( "" );
		System.out.println(
			"   java edu.northwestern.at.morphadorner.tools.comparestringcounts." +
			"CompareStringCounts analysis.tab reference.tab" );
		System.out.println( "" );
		System.out.println( "analysis.tab -- Input tab-separated file " +
			"of strings and counts for an analysis text." );
		System.out.println( "reference.tab -- Input tab-separated file " +
		"	of strings and counts for a reference text." );
		System.out.println( "" );
	}

	/**	Supervises comparing string counts in two files.
	 *
	 *	@param	args	Command line arguments.
	 */

	public CompareStringCounts( String[] args )
	{
								//	Load analysis strings and counts.

		Map<String, Number> analysisCounts	= null;

		try
		{
			analysisCounts	=
				CountMapUtils.loadCountMapFromFile( new File( args[ 0 ] ) );
		}
		catch ( Exception e )
		{
			System.err.println(
				"Unable to open analysis counts file " + args[ 0 ] );
		}
								//	Load reference strings and counts.

		Map<String, Number> referenceCounts	= null;

		try
		{
			referenceCounts	=
				CountMapUtils.loadCountMapFromFile( new File( args[ 1 ] ) );
		}
		catch ( Exception e )
		{
			System.err.println(
				"Unable to open reference counts file " + args[ 1 ] );
		}
								//	Get total strings in sample and
								//	reference.

		int analysisTotalCount	=
			CountMapUtils.getTotalWordCount( analysisCounts );

		int refTotalCount		=
			CountMapUtils.getTotalWordCount( referenceCounts );

								//	Report string counts.
		System.err.println(
			"Comparing string counts using " + args[ 0 ] +
			" as analysis file and " + args[ 1 ] +
			" as reference file." );

		System.err.println( "" );

		System.err.println(
			"Analysis unique strings  : " + analysisCounts.size() );

		System.err.println(
			"Analysis total strings   : " + analysisTotalCount );

		System.err.println(
			"Reference unique strings : " + referenceCounts.size() );

		System.err.println(
			"Reference total strings  : " + refTotalCount );

		                        //	Get combined string list.

		Map<String, Number> combinedCounts	=
			CountMapUtils.semiDeepClone( analysisCounts );

		CountMapUtils.addCountMap( combinedCounts , referenceCounts );

								//	Holds results of analysis.
								//	Each unique input string produces
								//	one line of results.

		Map<ReverseScoredString, double[]> results	=
			new TreeMap<ReverseScoredString, double[]>();

								//	Get set of unique strings
								//	in the two input count maps
								//	combined.

		Set<String> keySet	= combinedCounts.keySet();

								//	Counts strings analyzed.

		int stringsDone		= 0;
								//	Loop over the strings and
								//	compute the frequency statistics.

		for	( String stringToAnalyze : keySet )
		{
								//	Get string count in analysis text.

			Number stringCount	= analysisCounts.get( stringToAnalyze );

			if ( stringCount == null ) stringCount	= new Integer( 0 );

								//	Get string count in reference text.

			int refCount	= 0;

			if ( referenceCounts.containsKey( stringToAnalyze ) )
			{
				refCount	=
					referenceCounts.get( stringToAnalyze ).intValue();
			}
								//	Compute frequency statistics
								//	including Dunning's log-likelihood.

			double[] freqAnal	=
				doFreq
				(
					stringToAnalyze ,
					stringCount.intValue() ,
					analysisTotalCount ,
					refCount ,
					refTotalCount
				);
						        //	Save results for later reporting.

			results.put
			(
				new ReverseScoredString( stringToAnalyze , freqAnal[ 4 ] ) ,
				freqAnal
			);
								//	Update count of strings done.
			stringsDone++;
		}
								//	Display the results.

		displayResults( results );
	}

	/**	Frequency comparison of analysis and reference works for a word.
	 *
	 *	@param	stringToAnalyze			The word to analyze.
	 *	@param	analysisCount			Count of word in analysis text.
	 *	@param	analysisTotalCount		Total number of words in analysis
	 *									text.
	 *	@param	refCount				Count of collocate in reference
	 *									text.
	 *	@param	refTotalCount			Total number of words in reference
	 *									text.
	 *
	 *	@return							Results of frequency analysis
	 *									as a double[] array.
	 *
	 *	<p>
	 *	The entries in the results array are as follows.
	 *	</p>
	 *
	 *	<p>
	 *	(0)	Count of string occurrence in analysis text.<br />
	 *  (1)	String occurrence in analysis text as parts per 10,000.<br />
	 *	(2)	Count of string occurrence in reference text.<br />
	 *	(3) String occurrence in reference text as parts per 10,000.<br />
	 *	(4)	Dunning's Log-likelihood value.<br />
	 *	</p>
	 */

	public static double[] doFreq
	(
		String stringToAnalyze ,
		int analysisCount ,
		int analysisTotalCount ,
		int refCount ,
		int refTotalCount
	)
	{
								//	Compute percents and log-likelihood.

		double freqAnal[]	=
			Frequency.logLikelihoodFrequencyComparison
			(
            	analysisCount ,
            	refCount	,
            	analysisTotalCount ,
            	refTotalCount ,
            	false
            );
								//	Convert percents to parts per 10,000.

		freqAnal[ 1 ]	= freqAnal[ 1 ] * 100.0D;
		freqAnal[ 3 ]	= freqAnal[ 3 ] * 100.0D;

		return freqAnal;
	}

	/**	Displays results of frequency analysis in a sorted table.
	 *
	 *	@param	results		The map of results to display.
	 */

	public static void displayResults
	(
		Map<ReverseScoredString, double[]> results
	)
	{
								//	Output column titles.

		System.out.println
		(
			"String" + "\t" +
			"Over/under use" + "\t" +
			"Log likelihood" + "\t" +
			"Analysis percent" + "\t" +
			"Reference percent" + "\t" +
			"Analysis count" + "\t" +
			"Reference count"
		);
								//	Output results for each word.

		Iterator<ReverseScoredString> iterator	=
			results.keySet().iterator();

		while ( iterator.hasNext() )
		{
			ReverseScoredString key	= iterator.next();

			double[] freqAnal	= (double[])results.get( key );

			String overUnderUse;

			if ( Compare.compare( freqAnal[ 1 ] , freqAnal[ 3 ] ) > 0 )
			{
				overUnderUse	= "+";
			}
			else
			{
				overUnderUse	= "-";
			}

			int places	= 2;

			System.out.println
			(
								//	String to analyze

				key.getString()  + "\t" +

								//	Over/under use

				overUnderUse + "\t" +

								//	Log likelihood

				Formatters.formatDouble( freqAnal[ 4 ] , places ) + "\t" +

                                //	Analysis parts per 10,000

				Formatters.formatDouble( freqAnal[ 1 ] , places ) + "\t" +

                                //	Reference parts per 10,000

				Formatters.formatDouble( freqAnal[ 3 ] , places ) + "\t" +

								//	Analysis count

				Formatters.formatDouble( freqAnal[ 0 ] , 0 ) + "\t" +

								//	Reference count

				Formatters.formatDouble( freqAnal[ 2 ] , 0 )
			);
		}
	}

	/**	ScoredString modified to sort results from highest to lowest.
	 */

	public static class ReverseScoredString extends ScoredString
	{
		/**	Create scored string.
		 *
	 	*	@param	string	String.
	 	*	@param	score	Score.
	 	*/

		public ReverseScoredString( String string , double score )
		{
			super( string , score );
		}

 		/**	Compare this scored string with another.
		 *
 	 	 *	@param	other	The other scored string
 	 	 *
	 	 *	@return			> 0 if this scored string is less than the other,
	 	 *					= 0 if the two scored strings are equal,
	 	 *					< 0 if this scored string is greater than the other.
 	 	 */

		public int compareTo( Object other )
		{
			return -super.compareTo( other );
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



