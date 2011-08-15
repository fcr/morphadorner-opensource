package edu.northwestern.at.utils.corpuslinguistics.lemmatizer;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.util.regex.*;

import edu.northwestern.at.utils.*;

/**	A Lemmatizer Rule which uses regular expressions.
 *
 *	<p>
 *	A lemmarizer rule specifies a string substitution pattern
 *	used as part of the process of reducing an elaborated
 *	morphological form to its base form (lemma).
 *	</p>
 */

public class DefaultLemmatizerRule implements LemmatizerRule
{
	/**	Original rule text. */

	protected String ruleText;

	/**	Source pattern string to match. */

	protected String source;

	/**	Compiled source pattern matcher. */

	protected Pattern compiledSource;

	/**	Replacement string. */

	protected String replacement;

	/**	Compiled VCR string matcher. */

	protected static final Pattern VCRMatcher	=
		Pattern.compile( "([VCRA]+)" );

	/**	Match direction (LEFT or RIGHT). */

	public static final int LEFT	= 0;
	public static final int RIGHT	= 1;

	protected int direction;

	/**	Minimum match length. */

	protected int matchLength;

	/**	Must match dictionary entry. */

	protected boolean mustMatchDictionaryEntry;

	/**	Create a lemmatizer rule.
	 *
	 *	@param	ruleText	The rule text.
	 */

	public DefaultLemmatizerRule( String ruleText )
	{
		this.ruleText				= ruleText;
		direction					= RIGHT;
		source						= "";
		replacement					= "";
		mustMatchDictionaryEntry	= false;

		StringTokenizer tokenizer	=
			new StringTokenizer( ruleText , " \t" );

        int nTokens		= tokenizer.countTokens();

        if ( nTokens > 0 )
        {
			String[] tokens	= new String[ nTokens ];

			for ( int i = 0 ; i < nTokens ; i++ )
			{
				tokens[ i ]	= tokenizer.nextToken();
			}

			int i	= 0;

			char ch	= tokens[ 0 ].charAt( 0 );

			switch( ch )
			{
				case '<'	:
				case '>'	:
					direction	= ( ch == '<' ? LEFT : RIGHT );

					matchLength	=
						StringUtils.stringToInt
						(
							tokens[ 0 ].substring( 1 ) ,
							0
						);

					i++;
					break;

				case '+'	:
					mustMatchDictionaryEntry	= true;
					i++;
					break;

				default		: ;
			}

			source	= tokens[ i++ ];

			if ( i < nTokens )
			{
				replacement	= tokens[ i ];
			}

			int replacementCount	= 1;

			if ( matchLength > 0 )
			{
				if ( direction == RIGHT )
				{
					source		= "(..)" + source;
					replacement	= "$" + replacementCount + replacement;

					replacementCount++;
				}
				else
				{
					matchLength--;

					source		= "^(.{1," + matchLength + "})" + source;
					replacement	= "$" + replacementCount + replacement;

					replacementCount++;
				}
			}

			Matcher m	= VCRMatcher.matcher( source );

			if ( source.indexOf( "CC" ) >= 0 )
			{
				source	=
					StringUtils.replaceAll
					(
						source ,
						"CC" ,
						"([^aeiouy])\\1"
                    );

				replacement	=
					StringUtils.replaceFirst
					(
						replacement ,
						"C" ,
						"$" + replacementCount
					);
			}
			else if ( m.find() )
			{
				String phonolog	= m.group( 1 );

				int start	= m.start();
				int end		= m.end();

				source		=
					source.substring( 0 , start ) + "(" +
					phonolog + ")" +
					source.substring( end );

				source	=
//					StringUtils.replaceAll( source , "V" , "[aeiouyr]" );
					StringUtils.replaceAll( source , "V" , "[aeiouy]" );

				source	=
					StringUtils.replaceAll( source , "C" , "[^aeiouy]" );

				source	=
					StringUtils.replaceAll( source , "R" , "r" );

				source	=
					StringUtils.replaceAll( source , "A" , ".*" );

				replacement	=
					StringUtils.replaceFirst
					(
						replacement ,
						phonolog ,
						"$" + replacementCount
					);

				replacementCount++;
			}

			source	= source + "$";

			compiledSource	= Pattern.compile( source );
		}
	}

	/**	Apply a lemmatization rule to a string.
	 *
	 *	@param	s			String to which to apply rule.
	 *	@param	dictionary	List of known words.
	 *
	 *	@return				String after rule applied.
	 */

	public String apply( String s , Set<String> dictionary )
	{
		String result	= s;

		if ( compiledSource != null )
		{
			Matcher m	= compiledSource.matcher( s );

			if ( m.find() )
			{
				result	= m.replaceAll( replacement );

				if ( mustMatchDictionaryEntry )
				{
					if ( !dictionary.contains( result.toLowerCase() ) )
					{
						result	= s;
					}
				}
			}
		}

		return result;
	}

	/**	Apply a lemmatization rule to a string.
	 *
	 *	@param	s	String to which to apply rule.
	 *
	 *	@return		String after rule applied.
	 */

	public String apply( String s )
	{
		return apply( s , null );
	}

	/**	Return string version of rule.
	 *
	 *	@return		String version of rule.  Dictionary lookup not marked.
	 */

	public String toString()
	{
		return "s/" + source + "/" + replacement + "/";
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



