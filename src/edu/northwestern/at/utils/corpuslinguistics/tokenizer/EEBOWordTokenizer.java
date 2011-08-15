package edu.northwestern.at.utils.corpuslinguistics.tokenizer;

/*	Please see the license information at the end of this file. */

import java.text.*;
import java.util.*;
import java.util.regex.*;

import edu.northwestern.at.utils.*;

/**	Word tokenizer for EEBO texts.
 *
 *	<p>
 *	Do not use this when EEBO texts have been converted to TEIAnalytics
 *	format.
 *	</p>
 */

public class EEBOWordTokenizer
	extends DefaultWordTokenizer
	implements WordTokenizer
{
	/**	Pattern to match number.word */

	protected static final Pattern numberDotSpellingPattern	=
		Pattern.compile( "(\\d+)\\.(\\p{L})+" );

	protected static final Matcher numberDotSpellingMatcher	=
		numberDotSpellingPattern.matcher( "" );

	/**	Pattern to match _CapCap */

	protected static Pattern underlineCapCapPattern			=
		Pattern.compile( "^_([ABCDEFGHIJKLMNOPQRSTUVWXYZ])([ABCDEFGHIJKLMNOPQRSTUVWXYZ])" );

	protected static final Matcher underlineCapCapMatcher	=
		underlineCapCapPattern.matcher( "" );

	/**	Create EEBO word tokenizer.
	 */

	public EEBOWordTokenizer()
	{
		super();
	}

	/**	Preprocess a word token.
	 *
	 *	@param	token			Token to preprocess.
	 *	@param	tokenList		List of previous tokens already issued.
	 *
	 *	@return					Preprocessed token.
	 *							The token list may also have been modified.
	 */

	public String preprocessToken( String token , List<String> tokenList )
	{
								//	Remove vertical bars.
								//	Unnecessary when EEBO texts are
								//	preprocessed to TEI-A form.

		String result	= token;

		if ( !result.equals( "|" ) )
		{
			result	= StringUtils.replaceAll( token , "|" , "" );
		}

								//	If first character of token
								//	is a "+", and the token has more
								//	then one character and is not
								//	all symbols or punctuation,
								//	remove the leading "+" and prepend
								//	the previous token to this one.
								//	Also remove the last token so
								//	the combined token will take its
								//	place.
								//
								//	Unnecessary when EEBO texts are
								//	preprocessed to TEI-A form.

		if ( result.length() > 1 )
		{
			if ( result.charAt( 0 ) == '+' )
			{
				if ( !CharUtils.isPunctuationOrSymbol( result ) )
				{
					result	= result.substring( 1 );

					if ( tokenList.size() > 0 )
					{
						result	=
							(String)tokenList.get( tokenList.size() - 1 ) +
							result;

						tokenList.remove( tokenList.size() - 1 );
					}
				}
			}
								//	If "+" appears after first
								//	character in token, just remove
								//	it.
			else
			{
				result	=
					StringUtils.replaceAll( result , "+" , "" );
			}
		}
								//	Replace _CapCap at start of word
								//	by Capcap.
								//
								//	Unnecessary when EEBO texts are
								//	preprocessed to TEI-A form.

		if ( ( result.length() > 1 ) && ( result.charAt( 0 ) == '_' ) )
		{
			underlineCapCapMatcher.reset( result );

			if ( underlineCapCapMatcher.find() )
			{
				String char1	= result.charAt( 1 ) + "";

				String char2	=
					Character.toLowerCase( result.charAt( 2 ) ) + "";

				String rest		= "";

				if ( result.length() > 3 )
				{
					rest	= result.substring( 3 );
				}

				result	= char1 + char2 + rest;
			}
		}
								//	Split tokens of the form
								//	number.word into number. word .

		if ( ( result.length() > 2 ) && ( result.indexOf( "." ) > 0 ) )
		{
			numberDotSpellingMatcher.reset( result );

			if ( numberDotSpellingMatcher.matches() )
			{
				tokenList.add( numberDotSpellingMatcher.group( 1 ) + "." );

				result	= numberDotSpellingMatcher.group( 2 );
			}
        }

		return result;
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



