package edu.northwestern.at.utils.corpuslinguistics.spellingstandardizer;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.util.regex.*;

import edu.northwestern.at.utils.*;

/**	Spelling decruftifier.
 */

public class EnglishDecruftifier
{
	/**	Lists of combining macron replacement patterns.
	 */

	protected static List<PatternReplacer> cmList1	=
		ListFactory.createNewList();

	protected static List<PatternReplacer> cmList2	=
		ListFactory.createNewList();

	protected static Pattern vConsonantPattern	=
		Pattern.compile( "v([^aeiouy])" );

	protected static String vConsonantPatternReplacement	= "u$1";

	protected static Pattern vuvPattern	=
		Pattern.compile( "([aeiouy])u([aeiouy])" );

	protected static String vuvPatternReplacement	= "$1v$2";

	protected static Pattern initUVowelPattern	=
		Pattern.compile( "^u([aeiouy])" );

	protected static String initUVowelPatternReplacement	= "v$1";

	protected static Pattern initPrefixvVowelPattern	=
		Pattern.compile( "^([ab|ad|con|in|per|re|sub])u([aeiouy])" );

	protected static String initPrefixvVowelPatternReplacement	= "$1v$2";

	protected static Pattern initIVowelPattern	=
		Pattern.compile( "^I([aeiouy])" );

	protected static String initIVowelPatternReplacement	= "j$1";

	protected static Pattern prefixUVowelPattern	=
		Pattern.compile( "^([ab|ad|con|in|per|re|sub])u([aeiouy])" );

	protected static String	prefixUVowelPatternReplacement	= "$1v$2";

	protected static Pattern syllableIsyllablePattern	=
		Pattern.compile( "^([ab|ad|con|in|per|re|sub])I([ect|ud|ur|uu|uv|oin|oyn])" );

	protected static String	syllableIsyllablePatternReplacement	= "$1j$2";

	protected static Pattern underlineCapCap	=
		Pattern.compile( "^_([ABCDEFGHIJKLMNOPQRSTUVWXYZ])([ABCDEFGHIJKLMNOPQRSTUVWXYZ])" );

	protected static String	underlineCapCapPatternReplacement	= "$1$2";

	/**	Demacronization map.  Key is spelling with macrons, value is
	 *	word with macrons replaced.
	 */

	protected static Map<String, String>macronMap;

	/**	Macron map resource file.
	 */

	protected static final String macronMapResourceName	=
		"resources/macronmap.tab";

	/**	Static initializer. */

	static
	{
								//	Create combining macron replacement
								//	patterns lists.

		addCombiningMacronPattern( cmList1 , "a[~\u0304]n" , "amn" );
		addCombiningMacronPattern( cmList1 , "so[~\u0304]times" , "sometimes" );
		addCombiningMacronPattern( cmList1 , "so[~\u0304]what" , "somewhat" );
		addCombiningMacronPattern( cmList1 , "instrue[~\u0304]mt" , "instruement" );
		addCombiningMacronPattern( cmList1 , "conte[~\u0304]n" , "contemn" );
		addCombiningMacronPattern( cmList1 , "do[~\u0304]$" , "dom" );
		addCombiningMacronPattern( cmList1 , "cu[~\u0304]$" , "cum" );
		addCombiningMacronPattern( cmList1 , "iu[~\u0304]$" , "ium" );
		addCombiningMacronPattern( cmList1 , "unu[~\u0304]$" , "unum" );
		addCombiningMacronPattern( cmList1 , "who[~\u0304]e$" , "whom" );
		addCombiningMacronPattern( cmList1 ,
			"(ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz)u[~\u0304]$" ,
			"$1um" );
		addCombiningMacronPattern( cmList1 , "tyra[~\u0304]n" , "tyrann" );
		addCombiningMacronPattern( cmList1 , "tira[~\u0304]n" , "tirann" );
		addCombiningMacronPattern( cmList1 , "tu[~\u0304]$" , "tum" );
		addCombiningMacronPattern( cmList1 , "tuu[~\u0304]$" , "tuum" );
		addCombiningMacronPattern( cmList1 , "ua[~\u0304]" , "uam" );
		addCombiningMacronPattern( cmList1 , "uu[~\u0304]$" , "uum" );
		addCombiningMacronPattern( cmList1 , "^([tT])he[~\u0304]n" , "$1henn" );
		addCombiningMacronPattern( cmList1 , "^([tT])he[~\u0304]c" , "$1henc" );
		addCombiningMacronPattern( cmList1 , "^([tT])he[~\u0304]s" , "$1hems" );
		addCombiningMacronPattern( cmList1 , "^(tT)he[~\u0304]" , "$1hem" );
		addCombiningMacronPattern( cmList1 , "Lo[~\u0304]do[~\u0304]" , "London" );
		addCombiningMacronPattern( cmList1 , "Macedo[~\u0304]" , "Macedon" );
		addCombiningMacronPattern( cmList1 , "Abraha[~\u0304]" , "Abraham" );


		addCombiningMacronPattern( cmList1 , "circu[~\u0304]" , "circum" );
		addCombiningMacronPattern( cmList1 , "[~\u0304]([dgknst])" , "n$1" );
		addCombiningMacronPattern( cmList1 , "[~\u0304]([bmp])" , "m$1" );
		addCombiningMacronPattern( cmList1 , "so[~\u0304]time" , "sometime" );
		addCombiningMacronPattern( cmList1 , "^who[~\u0304]$" , "whom" );
		addCombiningMacronPattern( cmList1 , "ee[~\u0304]" , "eme" );
		addCombiningMacronPattern( cmList1 , "pav[~\u0304]e" , "pave" );
		addCombiningMacronPattern( cmList1 , "invo[~\u0304]cat" , "invocat" );

		addCombiningMacronPattern( cmList2 ,  "fro[~\u0304]$" , "from" );
		addCombiningMacronPattern( cmList2 ,  "ice[~\u0304]$" , "icem" );
		addCombiningMacronPattern( cmList2 ,  "ma[~\u0304]$" , "man" );
		addCombiningMacronPattern( cmList2 ,  "me[~\u0304]$" , "men" );

		addCombiningMacronPattern( cmList2 ,  "co[~\u0304]e" , "come" );
		addCombiningMacronPattern( cmList2 ,  "co[~\u0304]for" , "comfor" );
		addCombiningMacronPattern( cmList2 ,  "co[~\u0304]l" , "coml" );
		addCombiningMacronPattern( cmList2 ,  "sco[~\u0304]f" , "scomf" );

		addCombiningMacronPattern( cmList2 ,  "co[~\u0304]" , "con" );

		addCombiningMacronPattern( cmList2 ,  "dde[~\u0304]$" , "dden" );
		addCombiningMacronPattern( cmList2 ,  "ke[~\u0304]$" , "ken" );
		addCombiningMacronPattern( cmList2 ,  "wha[~\u0304]$" , "whan" );
		addCombiningMacronPattern( cmList2 ,  "whe[~\u0304]$" , "when" );

		addCombiningMacronPattern( cmList2 ,  "o[~\u0304]u" , "onv" );

		addCombiningMacronPattern( cmList2 ,  "ione[~\u0304]$" , "ionem" );
		addCombiningMacronPattern( cmList2 ,  "tate[~\u0304]$" , "tatem" );

		addCombiningMacronPattern( cmList2 ,  "nte[~\u0304]" , "ntem" );
		addCombiningMacronPattern( cmList2 ,  "ale[~\u0304]" , "alem" );
		addCombiningMacronPattern( cmList2 ,  "ile[~\u0304]" , "ilem" );
		addCombiningMacronPattern( cmList2 ,  "dine[~\u0304]" , "dinem" );
		addCombiningMacronPattern( cmList2 ,  "gine[~\u0304]" , "ginem" );
		addCombiningMacronPattern( cmList2 ,  "ore[~\u0304]" , "orem" );
		addCombiningMacronPattern( cmList2 ,  "[~\u0304]q" , "nq" );

		addCombiningMacronPattern( cmList2 ,  "[~\u0304]ly" , "nly" );
		addCombiningMacronPattern( cmList2 ,  "ou[~\u0304]" , "oun" );
		addCombiningMacronPattern( cmList2 ,  "io[~\u0304]" , "ion" );
		addCombiningMacronPattern( cmList2 ,  "to[~\u0304]" , "ton" );
		addCombiningMacronPattern( cmList2 ,  "([aeiou])[~\u0304]" , "$1n" );

								//	Create macron map.

		macronMap	= MapFactory.createNewMap();

		try
		{
			macronMap	=
				MapUtils.loadMap
				(
					EnglishDecruftifier.class.getResource
					(
						macronMapResourceName
					) ,
					"\t" ,
					"" ,
					"utf-8"
				);
		}
		catch ( Exception e )
		{
		}
	}

	/**	Add entry to combining macrons replacement list.
	 *
	 *	@param	list				List.
	 *	@param	pattern			Pattern.
	 *	@param	replacement		Replacement.
	 */

	protected static void addCombiningMacronPattern
	(
		List<PatternReplacer> list ,
		String pattern ,
		String replacement
	)
	{
		list.add( new PatternReplacer( pattern , replacement ) );
	}

	/**	Replace combining macrons.
	 *
	 *	@param	s		String in which to replace macrons.
	 *	@param	cmList	List of combining macron replacement patterns.
	 *
	 *	@return			String with macrons replaced/expanded.
	 */

	public static String replaceCombiningMacrons
	(
		String s ,
		List<PatternReplacer> cmList
	)
	{
		String result	= s;

		if ( macronMap.containsKey( s ) )
		{
			result	= macronMap.get( s );
		}
		else
		{
			int i			= 0;

			while ( ( i < cmList.size() ) && ( result.indexOf( "\u0304" ) >= 0 ) )
			{
				result	= cmList.get( i ).replace( result );
				i++;
			}
		}

		return result;
	}

	/**	Replace patterns.
	 *
	 *	@param	s			String in which to replace patterns.
	 *	@param	patternList	List of replacement patterns.
	 *
	 *	@return					String with patterns replaced.
	 */

	public static String replacePatterns
	(
		String s ,
		List<PatternReplacer> patternList
	)
	{
		String result	= s;
		int i			= 0;

		while ( i < patternList.size() )
		{
			result	= patternList.get( i ).replace( result );
			i++;
		}

		return result;
	}

	/**	Replace string at end of spelling.
	 *
	 *	@param	spelling		The spelling.
	 *	@param	ending		The ending string to look for.
	 *	@param	replacement	The replacement string.
	 *
	 *	@return					The fixed spelling.
	 */

	public static String fixEnd
	(
		String spelling ,
		String ending ,
		String replacement
	)
	{
		String result	= spelling;

		if ( result.endsWith( ending ) )
		{
			result	=
				result.substring( 0 , result.length() - ending.length() ) +
					replacement;
		}

		return result;
	}

	/**	Remove specified leading characters from spelling.
	 *
	 *	@param	spelling		The spelling.
	 *	@param	charsToEvict	Character to evict from start of spelling.
	 *
	 *	@return					The fixed spelling.
	 */

	public static String fixStart1
	(
		String spelling ,
		String charsToEvict
	)
	{
		String result	= spelling;

		if ( spelling.length() > 0 )
		{
			for ( int i = 0 ; i < charsToEvict.length() ; i++ )
			{
				if ( result.charAt( 0 ) == charsToEvict.charAt( i ) )
				{
					result	= result.substring( 1 );
					break;
				}
			}
		}

		return result;
	}

	public static String patternReplacer
	(
		Pattern pattern ,
		String result ,
		String replacement
	)
	{
		Matcher matcher	= pattern.matcher( result );

		if ( replacement.length() == 0 )
		{
			while ( matcher.find() )
			{
				StringBuffer sb	= new StringBuffer();

				for ( int i = 0 ; i < matcher.groupCount() ; i++ )
				{
					sb.append( matcher.group( i + 1 ) );
				}

				result	= sb.toString();

				matcher	= pattern.matcher( result );
			}
		}
		else
		{
			result	= matcher.replaceAll( replacement );
		}

		return result;
	}

	public static String patternReplacer
	(
		String pattern ,
		String result ,
		String replacement
	)
	{
		return
			patternReplacer
			(
				Pattern.compile( pattern ) ,
				result ,
				replacement
			);
	}

	/**	Decruftify step one.
	 *
	 *	@param	cruftySpelling	Spelling to decruftify.
	 *
	 *	@return					CruftySpelling containing
	 *								step1 decruftication results.
	 */

	public static CruftySpelling decruftifyStep1( String cruftySpelling )
	{
		String result	= cruftySpelling;

		if ( result.matches( "'s|'S" ) )
		{
			return new CruftySpelling( result , true , false );
		}

		if ( CharUtils.isAllHyphens( result ) )
		{
			return new CruftySpelling( result , true , false );
		}

		if ( CharUtils.isAllAsterisks( result ) )
		{
			return new CruftySpelling( result , true , false );
		}

		if ( CharUtils.isPossessiveAsterisks( result ) )
		{
			return new CruftySpelling( result , true , false );
		}

		if ( CharUtils.isPossessiveDashes( result ) )
		{
			return new CruftySpelling( result , true , false );
		}

		while ( result.startsWith( "-" ) )
		{
			result	= result.substring( 1 );
		}

		result	= StringUtils.replaceAll( result , "[+*^|]" , "" );
		result	= StringUtils.replaceAll( result , "\u18C2", "u" );

		boolean isAllCaps	= CharUtils.allLettersCapital( result );

		if ( isAllCaps )
		{
			result	= CharUtils.capitalizeFirstLetter( result );
		}

		Matcher uCCMatcher	= underlineCapCap.matcher( result );

		if ( uCCMatcher.find() )
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

		result	= replaceCombiningMacrons( result , cmList1 );

		if ( result.length() > 1 )
		{
			result	= fixStart1( result , "*/_^`" );
		}

		return new CruftySpelling( result , false , isAllCaps );
	}

	/**	Decruftify step two.
	 *
	 *	@param	cruftySpelling	Spelling to decruftify.
	 *
	 *	@return					CruftySpelling containing
	 *							step2 decruftication results.
	 */

	public static CruftySpelling decruftifyStep2( String cruftySpelling )
	{
		String result	= replaceCombiningMacrons( cruftySpelling , cmList2 );

		return new CruftySpelling( result , false );
	}

	/**	Decruftify step three.
	 *
	 *	@param	cruftySpelling	Spelling to decruftify.
	 *
	 *	@return					CruftySpelling containing
	 *							step3 decruftication results.
	 */

	public static CruftySpelling decruftifyStep3( String cruftySpelling )
	{
		String result	= cruftySpelling;

		result	= result.replaceAll( "yo[~\u0304]$" , "ion" );
		result	= result.replaceAll( "cio[~\u0304]$" , "cion" );
		result	= fixEnd( result , "cion" , "tion" );
		result	= fixEnd( result , "cions" , "tions" );

// mine
		result	= fixEnd( result , "bld" , "bled" );
		result	= fixEnd( result , "ynge" , "ing" );

//--keep next line?
		result	= fixEnd( result , "yng" , "ing" );
//
		result	= StringUtils.replaceAll( result , "ioin" , "join" );
		result	= StringUtils.replaceAll( result , "ioyn" , "join" );
		result	= StringUtils.replaceAll( result , "nioi" , "njoy" );
		result	= StringUtils.replaceAll( result , "nioy" , "njoy" );

		result	= fixEnd( result , "cyal" , "cial" );

		result	= StringUtils.replaceAll( result , "quut" , "cut" );
		result	= StringUtils.replaceAll( result , "vv" , "w" );
		result	= StringUtils.replaceAll( result , "VV" , "W" );
		result	= StringUtils.replaceAll( result , "Vv" , "W" );

		result	=
			patternReplacer(
				"([aeiouy])uu" , result , "$1w" );

		result	=
			patternReplacer(
				"uu([aeiouy])" , result , "w$1" );

		result	=
			patternReplacer(
				vConsonantPattern , result , vConsonantPatternReplacement );

		result	=
			patternReplacer( vuvPattern , result , vuvPatternReplacement );

		result	=
			patternReplacer(
				initUVowelPattern , result , initUVowelPatternReplacement );

		result	=
			patternReplacer(
				initPrefixvVowelPattern , result ,
					initPrefixvVowelPatternReplacement );

		result	=
			patternReplacer(
				initIVowelPattern , result ,
					initIVowelPatternReplacement );

		result	=
			patternReplacer(
				prefixUVowelPattern , result ,
					prefixUVowelPatternReplacement );

		result	=
			patternReplacer(
				syllableIsyllablePattern , result ,
					syllableIsyllablePatternReplacement	);

//	mine

		result	= fixEnd( result , "mente" , "ment" );
		result	= fixEnd( result , "ynde" , "ind" );
		result	=
			patternReplacer( "^([^aeiouy])vu" , result , "$1uu" );

		return new CruftySpelling( result , false );
	}

	/**	Decruftify a spelling (extended).
	 *
	 *	@param	cruftySpelling	The crufty spelling.
	 *
	 *	@return					The decruftified spelling.
	 */

	public static String decruftify( String cruftySpelling )
	{
		CruftySpelling fixedSpelling	= decruftifyStep1( cruftySpelling );
		String result					= fixedSpelling.spelling;
		boolean isAllCaps				= fixedSpelling.isAllCaps;

		if ( !fixedSpelling.done )
		{
			result	= StringUtils.replaceAll( result , "`" , "'" );
			result	= fixEnd( result , "'d" , "ed" );
			result	= fixEnd( result , "'st" , "est" );
			result	= fixEnd( result , "'red" , "ered" );

			result	= decruftifyStep2( result ).spelling;
			result	= decruftifyStep3( result ).spelling;

			result	= StringUtils.replaceAll( result , "~" , "" );
			result	= StringUtils.replaceAll( result , "_" , "" );
			result	= StringUtils.replaceAll( result , "\u0304" , "" );
		}

		if ( isAllCaps )
		{
			result	= result.toUpperCase();
		}

		return result;
	}

	/**	Decruftify a spelling (simple).
	 *
	 *	@param	cruftySpelling	The crufty spelling.
	 *
	 *	@return					The decruftified spelling.
	 */

	public static String simpleDecruftify( String cruftySpelling )
	{
		CruftySpelling fixedSpelling	= decruftifyStep1( cruftySpelling );
		String result					= fixedSpelling.spelling;
		boolean isAllCaps			= fixedSpelling.isAllCaps;

		if ( !fixedSpelling.done )
		{
			fixedSpelling	= decruftifyStep2( result );

			result	= fixedSpelling.spelling;
			result	= result.replaceAll( "cio[~\u0304]$" , "cion" );
			result	= result.replaceAll( "yo[~\u0304]$" , "yon" );

			result	= StringUtils.replaceAll( result , "~" , "" );
			result	= StringUtils.replaceAll( result , "_" , "" );
			result	= StringUtils.replaceAll( result , "\u0304" , "" );
		}

		if ( isAllCaps )
		{
			result	= result.toUpperCase();
		}

		return result;
	}

	/**	Decruftify a spelling (simple and extended).
	 *
	 *	@param	cruftySpelling	The crufty spelling.
	 *
	 *	@return					Two element string array.
	 *							[0] = extended decruftified spelling.
	 *							[1] = simple decruftified spelling.
	 */

	public static String[] decruftify2( String cruftySpelling )
	{
		CruftySpelling fixedSpelling	= decruftifyStep1( cruftySpelling );
		String result1					= fixedSpelling.spelling;
		String result2					= fixedSpelling.spelling;
		boolean isAllCaps				= fixedSpelling.isAllCaps;

		if ( !fixedSpelling.done )
		{
			result1	= StringUtils.replaceAll( result1 , "`" , "'" );
			result1	= fixEnd( result1 , "'d" , "ed" );
			result1	= fixEnd( result1 , "'st" , "est" );
			result1	= fixEnd( result1 , "'red" , "ered" );

			result1	= decruftifyStep2( result1 ).spelling;
			result1	= decruftifyStep3( result1 ).spelling;

			result1	= StringUtils.replaceAll( result1 , "~" , "" );
			result1	= StringUtils.replaceAll( result1 , "_" , "" );
			result1	= StringUtils.replaceAll( result1 , "\u0304" , "" );

			fixedSpelling	= decruftifyStep2( result2 );

			result2	= fixedSpelling.spelling;
			result2	= result2.replaceAll( "cio[~\u0304]$" , "cion" );
			result2	= result2.replaceAll( "yo[~\u0304]$" , "yon" );

			result2	= StringUtils.replaceAll( result2 , "~" , "" );
			result2	= StringUtils.replaceAll( result2 , "_" , "" );
			result2	= StringUtils.replaceAll( result2 , "\u0304" , "" );
		}

		if ( isAllCaps )
		{
			result1	= result1.toUpperCase();
			result2	= result2.toUpperCase();
		}

		return new String[]{ result1 , result2 };
	}

	/**	Allow overrides but not instantiation.
	 */

	protected EnglishDecruftifier()
	{
	}

	/**	Holds spelling in process of decruftification.
	 */

	public static class CruftySpelling
	{
		/**	The spelling in process of decruftification. */

		String spelling;

		/**	True if decruftification process complete. */

		boolean done;

		/**	True if original crufty spelling is all caps. */

		boolean isAllCaps;

		/**	CruftySpelling constructor. */

		public CruftySpelling
		(
			String spelling ,
			boolean done ,
			boolean isAllCaps
		)
		{
			this.spelling		= spelling;
			this.done		= done;
			this.isAllCaps	= isAllCaps;
		}

		/**	CruftySpelling constructor. */

		public CruftySpelling( String spelling , boolean done )
		{
			this.spelling	= spelling;
			this.done	= done;
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



