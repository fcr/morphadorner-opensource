package edu.northwestern.at.utils.corpuslinguistics.tokenizer;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;
import java.util.regex.*;

import edu.northwestern.at.utils.*;

/**	Tokenizer utilities.
 */

public class TokenizerUtils
{
	/**	Get token type.
	 *
	 *	@param	token	The token.
	 *
	 *	@return			Token type (plain, abbreviation, punctuation, etc.)
	 */

	public static String getTokenType( String token )
	{
		String result	= "token";

		if ( CharUtils.isPunctuation( token ) )
		{
			result	= "punctuation";
		}
		else if ( CharUtils.isNumber( token ) )
		{
			result	= "number";
		}
		else if ( CharUtils.isUSCurrency( token ) )
		{
			result	= "US currency";
		}
		else if ( CharUtils.isUSCurrencyCents( token ) )
		{
			result	= "US currency";
		}
		else if ( CharUtils.isCurrency( token ) )
		{
			result	= "currency";
		}
		else if ( Abbreviations.isAbbreviation( token ) )
		{
			result	= "abbreviation";
		}
		else if ( CharUtils.isSymbol( token ) )
		{
			result	= "symbol";
		}
		else if ( RomanNumeralUtils.isRomanNumeral( token ) )
		{
			if ( !token.equals( "I" ) )
			{
				result	= "Roman numeral";
			}
		}

		return result;
	}

	/** Don't allow instantiation, do allow overrides. */

	protected TokenizerUtils()
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



