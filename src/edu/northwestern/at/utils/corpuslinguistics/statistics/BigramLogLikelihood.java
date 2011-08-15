package edu.northwestern.at.utils.corpuslinguistics.statistics;

/*	Please see the license information at the end of this file. */

import java.util.*;
import edu.northwestern.at.utils.math.*;
import edu.northwestern.at.utils.math.distributions.*;

/**	Computes Dunnett's log-likelihood for bigrams.
 */

public class BigramLogLikelihood
{
	/**	Compute one part of log likelihood value.
	 */

	protected static double logLike( double k , double n , double x )
	{
		return
			( k * ArithUtils.safeLog( x ) ) +
			( ( n - k ) * ArithUtils.safeLog( 1.0D - x ) );
	}

	/**	Compute log likelihood value for a bigram.
	 *
	 *	@param	c1			Count of first word in bigram.
	 *	@param	c2			Count of second word in bigram.
	 *	@param	c12			Count of bigram.
	 *	@param	n			Corpus size.
	 *
	 *	@return				The log-likelihood value.
	 */

	public static double calculateLogLikelihood
	(
		double c1 ,
		double c2 ,
		double c12 ,
		double n
	)
	{
		double p	= c2 / n;
		double p1	= c12 / c1;
		double p2	= ( c2 - c12 ) / ( n - c1 );

		double logLikelihood	=
			logLike( c12 , c1 , p ) +
			logLike( c2 - c12 , n - c1 , p ) -
			logLike( c12 , c1 , p1 ) -
			logLike( c2 - c12 , n - c1 , p2 );

		logLikelihood			= -2.0D * logLikelihood;

		return logLikelihood;
	}

	/**	Don't allow instantiation but do allow overrides.
	 */

	protected BigramLogLikelihood()
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


