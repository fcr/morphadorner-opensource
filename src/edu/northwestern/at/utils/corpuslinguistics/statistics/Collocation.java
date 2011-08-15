package edu.northwestern.at.utils.corpuslinguistics.statistics;

/*	Please see the license information at the end of this file. */

import java.util.*;
import edu.northwestern.at.utils.math.*;

/**	Computes bigram collocation measures.
 */

public class Collocation
{
	/**	Indices of association measures in result array.
	 */

	public static final int DICE		= 0;
	public static final int LOGLIKE		= 1;
	public static final int PHISQUARED	= 2;
	public static final int SMI			= 3;
	public static final int SCP			= 4;
	public static final int T			= 5;
	public static final int Z			= 6;

	/**	Computes collocation measures.
	 *
	 *	@param	sampleCount	Count of collocation appearance in sample.
	 *	@param	refCount	Count of collocation appearance in reference
	 *						corpus.
	 *	@param	sampleSize	Number of words/lemmas in the sample.
	 *	@param	refSize		Number of words/lemmas in reference corpus.
	 *
	 *	@return				A double array containing the following
	 *						measures of collocational association.
	 *
	 *							(0)	Dice coefficient
	 *							(1) Log likelihood
	 *							(2)	Phi squared
	 *							(3)	Specific Mutual information score
	 *							(4)	Symmetric conditional probability
	 *							(5)	z score
	 *              			(6)	t score
	 */

	public static double[] association
	(
		int sampleCount ,
		int refCount ,
		int sampleSize ,
		int refSize
	)
	{
		double result[]	= new double[ Z + 1 ];

		for ( int i = 0 ; i <= Z ; i++ )
		{
			result[ i ]	= 0.0D;
		}
								//	Compute observed and expected
								//	frequencies.

		double observed	= (double)sampleCount;
        double expected	= 0.0D;
		double p		= 0.0D;
		double stdDev	= 0.0D;
		double ominuse	= 0.0D;

		if ( refSize > 0 )
		{
			p			= (double)refCount / (double)refSize;

								//	Compute expected value.

			expected	= p * (double)sampleSize;

                        		//	Compute standard deviation for
                        		//	z score.

			stdDev		= Math.sqrt( expected * ( 1.0D - p ) );

								//	Compute observed minus expected.

			ominuse		= observed - expected;
		}
								//	Compute z score.
		if ( stdDev > 0.0D )
		{
			result[ Z ]	= ominuse / stdDev;
		}
								//	Compute t score.
		if ( observed > 0 )
		{
			result[ T ]	= ominuse / Math.sqrt( observed );
		}
								//	Compute mutual information score.

		if ( expected > 0.0D )
		{
			result[ SMI ]	=
				ArithUtils.safeLog( observed / expected ) / Constants.LN2;
        }
								//	Compute Dice coefficient.

		if ( ( sampleSize + refCount ) > 0 )
		{
			result[ DICE ]	=
				( 2.0D * observed ) / ( sampleSize + refCount );
        }
								//	Compute phi squared.

		if ( ( expected > 0.0D ) && ( refSize > 0 ) )
		{
			result[ PHISQUARED ]	=
				ominuse * ominuse / expected / refSize;
		}
								//	Compute symmetric conditional
								//	probability.

		if ( ( sampleSize * refCount ) > 0 )
		{
			result[ SCP ]	=
				( observed * observed ) /
					( (double)sampleSize * (double)refCount );
		}
								//	Compute log-likelihood.

		result[ LOGLIKE	]	=
			BigramLogLikelihood.calculateLogLikelihood
			(
				(double)sampleSize ,
				(double)refCount ,
				observed ,
				(double)refSize
			);

		return result;
	}

	/**	Don't allow instantiation but do allow overrides.
	 */

	protected Collocation()
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



