package edu.northwestern.at.utils.corpuslinguistics.textsummarizer;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.stopwords.*;
import edu.northwestern.at.utils.corpuslinguistics.wordcounts.*;

/**	SimpleTextSummarizer: Simple text summarizer.
 *
 *	<p>
 *	This summarizer produces a summary by finding the (up to) 100 most
 *	commonly used words in a text (not including stop words) and outputting
 *	the first sentence containing each common word.  This works adequately
 *	for news articles or blog posting, but rather badly for literature.
 *	</p>
 */

public class SimpleTextSummarizer
	extends AbstractTextSummarizer
	implements TextSummarizer
{
	/**	Create the default summarizer.
	 */

	public SimpleTextSummarizer()
	{
		super();
	}

	/** Summarize text.
	 *
	 *	@param	sentences			Tokenized sentences to summarize.
	 *	@param	summarySentences	Maximum number of sentences to return
	 *								in the summary.
	 *
	 *	@return						Summary of the input text.
	 */

	public <T extends Comparable> List<Integer> summarize
	(
		List<List<T>> sentences ,
		int summarySentences
	)
	{
								//	Get word counts ignoring stop words.

		Map<String , WordCountAndSentences> wordCounts	=
			WordCountUtils.countWordsInSentences
			(
				sentences ,
				new BuckleyAndSaltonStopWords()
			);
								//	Sort the counts into descending
								//	order by count.

		List<WordCountAndSentences> wcsData	=
			new SortedArrayList<WordCountAndSentences>();

		Iterator<String> iterator	= wordCounts.keySet().iterator();

		while ( iterator.hasNext() )
		{
			wcsData.add( wordCounts.get( iterator.next() ) );
		}
								//	Holds summary sentence indices.

		Set<Integer> summarySentencesSet	= new TreeSet<Integer>();

								//	Use up to 100 most commonl used words.

		int maxWords	= Math.min( 100 , wcsData.size() );

								//	For each commonly word word,
								//	find the first sentence in which
								//	that word appears, and add it to the
								//	summary sentences collection.
		for
		(
			int i = 0 ;
			( i < wcsData.size() ) &&
				( summarySentencesSet.size() < summarySentences ) ;
			i++
		)
		{
			WordCountAndSentences wcs	= wcsData.get( i );

			if ( CharUtils.isNumber( wcs.word.toString() ) ) continue;
			if ( CharUtils.hasDigit( wcs.word.toString() ) ) continue;

			Integer [] sentenceNumbers	=
				(Integer[])wcs.sentences.toArray
				(
					new Integer[ wcs.sentences.size() ]
				);

			summarySentencesSet.add( sentenceNumbers[ 0 ] );
		}
								//	Return indices of selected
								//	summary sentences.

		return new ArrayList<Integer>( summarySentencesSet );
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



