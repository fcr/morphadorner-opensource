package edu.northwestern.at.utils.corpuslinguistics.tokenizer;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

import edu.northwestern.at.utils.*;

/**	Abbreviation lists and pattern matchers.
 *
 *	<p>
 *	Holds a list of common abbreviations along with information about
 *	whether each abbreviation can normally end a sentence or not.
 *	Also provides patterns and methods for determining if a string is
 *	a possible abbreviation.
 *	</p>
 */

public class Abbreviations
{
								//	Compiled regular expression to match
								//	an abbreviation.

	protected static Pattern abbreviationPattern	=
		Pattern.compile
		(
//			"([A-Z,a-z]\\.([A-Z,a-z,0-9]\\.)*)|([A-Z][bcdfghj-np-tvxz]+\\.)"
			"^([A-Za-z]\\.([A-Za-z0-9]\\.)+|[A-Z]\\.|[A-Z][bcdfghj-np-tvxz]+\\.)$"
		);
								//	Abbreviation pattern matcher.

 	protected static Matcher abbreviationMatcher	=
 		abbreviationPattern.matcher( "" );

								//	Compiled regular expression to
								//	match an initial.

	protected static Pattern initialPattern	=
		Pattern.compile( "[A-Z][.]" );

								//	Initial pattern matcher.

	protected static Matcher initialMatcher	=
		initialPattern.matcher( "" );

								//	Compiled regular expression to
								//	match a possessive initial.

	protected static Pattern possessiveInitialPattern	=
		Pattern.compile( "[A-Z][.]'[s|S]" );

								//	Initial pattern matcher.

	protected static Matcher possessiveInitialMatcher	=
		possessiveInitialPattern.matcher( "" );

								//	Defined abbreviations.

	protected static UTF8Properties abbreviations	= null;

								//	Path to abbreviations list resource.

	protected final static String defaultAbbreviationsFileName	=
		"resources/abbreviations.txt";

	/**	Load abbreviations list from resource properties file.
	 *
	 *	<p>
	 *	Each line in the UTF8 abbreviations property file takes
	 *	the form:
	 *	</p>
	 *
	 *	<p>
	 *	<code>abbrev.=n</code>
	 *	</p>
	 *
	 *	<p>
	 *	where a value of 1 for n indicates the abbreviation can normally
	 *	end a sentence and a value of 0 for n indicates the abbreviation
	 *	normally cannot end a sentence.
	 *	</p>
	 */

	protected static void loadDefaultAbbreviations()
	{
								//	If abbreviations already loaded,
								//	don't load them again,

		if ( abbreviations != null ) return;

								//	Create properties object to
								//	hold abbreviations.

		abbreviations	= new UTF8Properties();

								//	Load abbreviations from resource file.
		try
		{
			abbreviations.load
			(
				Abbreviations.class.getResourceAsStream
				(
					defaultAbbreviationsFileName
				)
			);
		}
		catch ( IOException ioe )
		{
//			ioe.printStackTrace();
		}
	}

	/**	Load abbreviations list from a properties file.
	 *
	 *	@param	abbreviationsURL	Abbreviations URL.
	 *
	 *	@return						true if abbreviations loaded OK,
	 *								false if error occurred.
	 *
	 *	<p>
	 *	Each line in the UTF8 abbreviations property file takes
	 *	the form:
	 *	</p>
	 *
	 *	<p>
	 *	<code>abbrev.=n</code>
	 *	</p>
	 *
	 *	<p>
	 *	where a value of 1 for n indicates the abbreviation can normally
	 *	end a sentence and a value of 0 for n indicates the abbreviation
	 *	normally cannot end a sentence.
	 *	</p>
	 */

	public static boolean loadAbbreviations( String abbreviationsURL )
	{
		boolean result	= false;

								//	Create properties object to
								//	hold abbreviations if not
								//	already created.

		if ( abbreviations == null )
		{
			abbreviations	= new UTF8Properties();
		}
								//	Load abbreviations from file.
		try
		{
			abbreviations	=
				UTF8PropertyUtils.loadUTF8Properties
				(
					new URL( abbreviationsURL ) ,
					abbreviations
				);

			result	= true;
		}
		catch ( IOException ioe )
		{
//			ioe.printStackTrace();
		}

		return result;
	}

	/**	Checks if string is a known abbreviation.
	 *
	 *	@param	str		The string to check.
	 *
	 *	@return			true if "str" is on the known abbreviations list.
	 */

	public static boolean isKnownAbbreviation( String str )
	{
		return abbreviations.containsKey( str );
 	}

	/**	Checks if string is a probable abbreviation.
	 *
	 *	@param	str		The string to check.
	 *
	 *	@return			true if "str" is probably an abbreviation .
	 *
	 *	<p>
	 *	A string is declared to be a probable abbreviation if if
	 *	appears in the abbreviation list or matches the abbreviation
	 *	pattern.
	 *	</p>
	 */

	public static boolean isAbbreviation( String str )
	{
		boolean result	= isKnownAbbreviation( str );

		if ( !result )
		{
			abbreviationMatcher.reset( str );

			result	= abbreviationMatcher.matches();
		}

		return result;
 	}

	/**	Checks if string is an abbreviation on which a sentence can end.
	 *
	 *	@param	str		The string to check.
	 *
	 *	@return			true if "str" is an possible sentence-ending
	 *						abbreviation .
	 *
	 *	<p>
	 *	A string is declared to be a probable sentence-ending abbreviation
	 *	if it appears in the abbreviation list and it has a
	 *	sentence-ending value of 1.
	 *	</p>
	 */

	public static boolean isEOSAbbreviation( String str )
	{
		return
			abbreviations.getProperty( str , "0" ).equals( "1" );
 	}

	/**	Checks if string is an initial.
	 *
	 *	@param	str		The string to check.
	 *
	 *	@return			true if "str" is an abbreviation .
	 *
	 *	<p>
	 *	A string is an initial when it takes the form "L." where
	 *	L is a capital letter.
	 *	</p>
	 */

	public static boolean isInitial( String str )
	{
		initialMatcher.reset( str );

		return initialMatcher.matches();
	}

	/**	Checks if string is a possible possessive initial.
	 *
	 *	@param	str		The string to check.
	 *
	 *	@return			true if "str" is a possible possessive initial .
	 *
	 *	<p>
	 *	A string is an possible possessive initial when it takes the form
	 *	"L.'s" where L is a capital letter.
	 *	</p>
	 */

	public static boolean isPossessiveInitial( String str )
	{
		possessiveInitialMatcher.reset( str );

		return possessiveInitialMatcher.matches();
	}

	/**	Get count of known abbreviations.
	 *
	 *	@return		Count of known abbreviations.
	 */

	public static int getAbbreviationsCount()
	{
		int result	= 0;

		if ( abbreviations != null )
		{
			result	= abbreviations.size();
		}

		return result;
	}

	/**	Allow overrides but not instantiation.
	 */

	protected Abbreviations()
	{
	}

	/**	Static initializer loads default abbreviations. */

	static
	{
								//	Load default abbreviations.

		loadDefaultAbbreviations();
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



