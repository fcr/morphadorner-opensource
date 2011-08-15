package edu.northwestern.at.utils;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;
import java.text.*;

/**	Formatting utilties. */

public class Formatters {
	
	/**	Number formatter for numbers with commas. */
	
	static private final NumberFormat COMMA_FORMATTER = 
		NumberFormat.getInstance();
		
	static {
		COMMA_FORMATTER.setGroupingUsed(true);
	}

	/**	Formats an integer with commas.
	 *
	 *	@param	n		The number.
	 *
	 *	@return			The formatted number with commas.
	 */

	public static String formatIntegerWithCommas (int n) {
		return COMMA_FORMATTER.format(n);
	}

	/**	Formats a long with commas.
	 *
	 *	@param	n		The number.
	 *
	 *	@return			The formatted number with commas.
	 */

	public static String formatLongWithCommas (long n) {
		return COMMA_FORMATTER.format(n);
	}
	
	/**	Number formatter for floating point numbers. */
	
	static private final NumberFormat FLOAT_FORMATTER = 
		NumberFormat.getInstance();
		
	static {
		FLOAT_FORMATTER.setMinimumIntegerDigits(1);
	}
	
	/**	Formats a float.
	 *
	 *	<p>The formatted number always has a minimum of one digit
	 *	before the decimal point, and a fixed specified number
	 *	of digits after the decimal point.
	 *
	 *	@param	x		The number.
	 *
	 *	@param	d		Number of digits after the decimal point.
	 *
	 *	@return			The formatted number.
	 */
	 
	public static String formatFloat (float x, int d) {
		FLOAT_FORMATTER.setMinimumFractionDigits(d);
		FLOAT_FORMATTER.setMaximumFractionDigits(d);
		return FLOAT_FORMATTER.format(x);
	}
	
	/**	Formats a double.
	 *
	 *	<p>The formatted number always has a minimum of one digit
	 *	before the decimal point, and a fixed specified number
	 *	of digits after the decimal point.
	 *
	 *	@param	x		The number.
	 *
	 *	@param	d		Number of digits after the decimal point.
	 *
	 *	@return			The formatted number.
	 */
	 
	public static String formatDouble (double x, int d) {
		FLOAT_FORMATTER.setMinimumFractionDigits(d);
		FLOAT_FORMATTER.setMaximumFractionDigits(d);
		return FLOAT_FORMATTER.format(x);
	}

	/** Hides the default no-arg constructor. */

	private Formatters () {
		throw new UnsupportedOperationException();
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


