package edu.northwestern.at.utils.math.statistics;

/*	Please see the license information at the end of this file. */

import java.util.*;

/**	Accumulate basic statistical information for a set of doubles.
 *
 *	<p>
 *	Accumulates the following quantities.
 *	</p>
 *
 *	<ul>
 *	<li>Count</li>
 *	<li>Maximum</li>
 *	<li>Minimum</li>
 *	<li>Mean</li>
 *	<li>Sum of squares</li>
 *	</ul>
 *
 *	<p>
 *	Returns these values as well as the variance and standard deviation.
 *	</p>
 */

public class Accumulator
{
	/**	Count of elements. */

	protected long count		= 0;

	/**	Mean of elements. */

	protected double mean		= 0.0D;

    /**	Sum of squares of elements. */

	protected double sumOfSquares	= 0.0D;

	/**	Minimum of elements. */

	protected double minimum	= Double.POSITIVE_INFINITY;

    /**	Maximum of elements. */

	protected double maximum	= Double.NEGATIVE_INFINITY;

	/**	Create an empty accumulator.
	 */

	public Accumulator()
	{
	}

	/**	Add a value to the accumulation.
	 *
	 *	@param	value	The value to add.
	 */

	public void addValue( double value )
	{
								//	Increment count of values.
		count++;

		double prevMean = mean;

								//	Incrementally update
								//	mean and sum of squares.

		mean 			+= ( value - mean ) / (double)count;
		sumOfSquares	+=
			( value - mean ) * ( value - prevMean );

								//	Update minimum and maximum.

		if ( minimum > value ) minimum = value;
		if ( maximum < value ) maximum = value;
	}

	/**	Add collection of values to the accumulation.
	 *
	 *	@param	values	The collection of values to add.
	 */

	public void addValues( Collection<Double> values )
	{
		if ( values != null )
		{
			Iterator<Double> iterator	= values.iterator();

			while ( iterator.hasNext() )
			{
				double value	= iterator.next();
				addValue( value );
			}
		}
	}

	/**	Add array of values to the accumulation.
	 *
	 *	@param	values	The collection of values to add.
	 */

	public void addValues( double[] values )
	{
		if ( values != null )
		{
			for ( int i = 0 ; i < values.length ; i++ )
			{
				addValue( values[ i ] );
			}
		}
	}

	/**	Return count.
	 *
	 *	@return		Count of elements as a long.
	 */

	public long getCount()
	{
		return count;
	}

	/**	Return maximum.
	 *
	 *	@return		Maximum of elements.
	 */

	public double getMaximum()
	{
		return maximum;
	}

	/**	Return minimum.
	 *
	 *	@return		Minimum of elements.
	 */

	public double getMinimum()
	{
		return minimum;
	}

	/**	Return mean.
	 *
	 *	@return		Mean of elements.
	 */

	public double getMean()
	{
		return mean;
	}

	/**	Return variance.
	 *
	 *	@return		Variance of elements.
	 *
	 *	<p>
	 *	The variance is the sum of squares divided by the
	 *	number of values.
	 *	</p>
	 */

	public double getVariance()
	{
		return sumOfSquares / (double)count;
	}

	/**	Return standard deviation.
	 *
	 *	@return		Standard deviation of elements.
	 *
	 *	<p>
	 *	The standard deviation is the square root of the variance.
	 *	</p>
	 */

	public double getStandardDeviation()
	{
		return Math.sqrt( getVariance() );
	}

	/**	Return sum of squares.
	 *
	 *	@return		Sum of squares of elements.
	 */

	public double getSumOfSquares()
	{
		return sumOfSquares;
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


