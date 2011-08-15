package edu.northwestern.at.utils.corpuslinguistics.statistics;

/*	Please see the license information at the end of this file. */

import java.util.*;
import edu.northwestern.at.utils.math.*;
import edu.northwestern.at.utils.math.distributions.*;

/**	Computes frequency-based statistics for comparing corpora.
 */

public class Frequency
{
	/**	Compute log-likelihood statistic for comparing frequencies in two corpora.
	 *
	 *	@param	sampleCount		Count of word/lemma appearance in sample.
	 *	@param	refCount		Count of word/lemma appearance in reference
	 *							corpus.
	 *	@param	sampleSize		Total words/lemmas in the sample.
	 *	@param	refSize			Total words/lemmas in reference corpus.
	 *	@param	computeLLSig	Compute significance of log likelihood.

	 *	@return					A double array containing frequency comparison
	 *							statistics.
	 *
	 *	<p>
	 *	The contents of the result array are as follows.
	 *	</p>
	 *	<p>
	 *	(0)	Count of word/lemma appearance in sample.<br />
	 *  (1)	Percent of word/lemma appearance in sample.<br />
	 *	(2)	Count of word/lemma appearance in reference.<br />
	 *	(3) Percent of word/lemma appearance in reference.<br />
	 *	(4)	Log-likelihood measure.<br />
	 *	(5) Significance of log-likelihood.<br />
	 *	</p>
	 *
	 *	<p>
	 *	The results of any zero divides are set to zero.
	 *	</p>
	 */

	public static double[] logLikelihoodFrequencyComparison
	(
		int sampleCount ,
		int refCount ,
		int sampleSize ,
		int refSize ,
		boolean computeLLSig
	)
	{
		double result[]	= new double[ 6 ];

		double a		= sampleCount;
		double b		= refCount;
		double c		= sampleSize;
		double d		= refSize;

		double e1		= c * ( a + b ) / ( c + d );
		double e2		= d * ( a + b ) / ( c + d );

		double ae1		= 0.0D;

		if ( e1 != 0.0D )
		{
			ae1	= a / e1;
		}

		double be2		= 0.0D;

		if ( e2 != 0.0D )
		{
			be2	= b / e2;
		}

		double logLike	=
			2.0D * ( ( a * ArithUtils.safeLog( ae1 ) ) +
			( b * ArithUtils.safeLog( be2 ) ) );

		result[ 0 ]	= a;

		if ( c == 0.0D )
		{
			result[ 1 ]	= 0.0D;
		}
		else
		{
			result[ 1 ]	= 100.0D * ( a / c );
		}

		result[ 2 ]	= b;

		if ( d == 0.0D )
		{
			result[ 3 ]	= 0.0D;
		}
		else
		{
			result[ 3 ]	= 100.0D * ( b / d );
		}

		result[ 4 ]	= logLike;
		result[ 5 ]	= 0.0D;

		if ( computeLLSig ) result[ 5 ]	= Sig.chisquare( logLike , 1 );

		return result;
	}

	/**	Compute log-likelihood statistic for comparing frequencies in two corpora.
	 *
	 *	@param	sampleCount		Count of word/lemma appearance in sample.
	 *	@param	refCount		Count of word/lemma appearance in reference
	 *							corpus.
	 *	@param	sampleSize		Total words/lemmas in the sample.
	 *	@param	refSize			Total words/lemmas in reference corpus.

	 *	@return					A double array containing frequency comparison
	 *							statistics.
	 *
	 *	<p>
	 *	The contents of the result array are as follows.
	 *	</p>
	 *	<p>
	 *	(0)	Count of word/lemma appearance in sample.<br />
	 *  (1)	Percent of word/lemma appearance in sample.<br />
	 *	(2)	Count of word/lemma appearance in reference.<br />
	 *	(3) Percent of word/lemma appearance in reference.<br />
	 *	(4)	Log-likelihood measure.<br />
	 *	(5) Significance of log-likelihood.<br />
	 *	</p>
	 */

	public static double[] logLikelihoodFrequencyComparison
	(
		int sampleCount ,
		int refCount ,
		int sampleSize ,
		int refSize
	)
	{
		return logLikelihoodFrequencyComparison(
			sampleCount , refCount , sampleSize , refSize , true );
	}

	/**	Don't allow instantiation but do allow overrides.
	 */

	protected Frequency()
	{
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


