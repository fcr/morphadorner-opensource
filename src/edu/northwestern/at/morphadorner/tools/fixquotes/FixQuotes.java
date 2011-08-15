package edu.northwestern.at.morphadorner.tools.fixquotes;

/*	Please see the license information at the end of this file. */

import java.net.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.lexicon.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.guesser.*;
import edu.northwestern.at.utils.corpuslinguistics.sentencesplitter.*;
import edu.northwestern.at.utils.corpuslinguistics.tokenizer.*;

/**	Fix quotes in ascii text files.
 *
 *	<p>
 *	Usage:
 *	</p>
 *	<blockquote>
 *	<pre>
 *	java edu.northwestern.at.morphadorner.tools.fixquotes.FixQuotes input.txt output.txt
 *	</pre>
 *	</blockquote>
 *	<p>
 *	input.txt -- input text file.<br />
 *	output.txt	-- output text file with quotes fixed.<br />
 *	</p>
 *
 *	<p>
 *	Based in part on the Perl and PHP "SmartyPants" programs by
 *	John Gruber, Brad Choate, and Michel Fortin.
 *	</p>
 *
 *	<p>
 *	Since the "quotification" relies on heuristics, not all quotes will be
 *	converted correctly.
 *	</p>
 */

public class FixQuotes
{
	/**	Left single quote replacement text. */

	protected static final String lsquo	= "&lsquo;";

	/**	Left double quote replacement text. */

	protected static final String ldquo	= "&ldquo;";

	/**	Right single quote replacement text. */

	protected static final String rsquo	= "&rsquo;";

	/**	Right double quote replacement text. */

	protected static final String rdquo	= "&rdquo;";

	/**	Apostrophereplacement text. */

	protected static final String apos	= "&apos;";

	/**	Temporary single quote marker. */

	protected static final String sq	= "\uE060";

	/**	Temporary double quote marker. */

	protected static final String dq	= "\uE061";

	/**	Main program. */

	public static void main( String[] args )
	{
		String s	= "";
								//	Load text to fix.
		try
		{
			s	= FileUtils.readTextFile( args[ 0 ] , "utf-8" );
       	}
       	catch ( Exception e )
       	{
			e.printStackTrace();
       	}
								//	Load contractions.

		TaggedStrings contractions	=
			loadContractions( "resources/contractions.txt" );

								//	Build contractions pattern.

		Pattern contractionsPattern	=
			buildContractionsPattern( contractions );

								//	Get a contractions matcher.

		Matcher contractionsMatcher	=
			contractionsPattern.matcher( "" );

								//	Fix quotes.

		s	= repairQuotes( s , contractionsMatcher , contractions );

								//	Output fixed text.
		try
		{
			FileUtils.writeTextFile( args[ 1 ] , false , s , "utf-8" );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}

	/**	Fix quotes and apostrophes in text.
	 *
	 *	@param	s						The text to fix.
	 *	@param	contractionsMatcher		Matcher for known contractions.
	 *
	 *	@return							The fixed text.
	 *
	 *	<p>
	 *	The following operations are performed on the input
	 *	text to generate the fixed output text.
	 *	</p>
	 *
	 *	<ul>
	 *		<li>Single (ordinary) quotes used as apostrophes
	 *			 are converted to an apostrophe entry string
	 *			 (&apos;).
	 *			</li>
	 *		<li>Single quotes used as left single quotes are converted
	 *			 to a left single quote entity string (&lsquo;).
	 *			</li>
	 *		<li>Single quotes used as right single quotes are converted
	 *			 to a right single quote entity string (&rsquo;).
	 *			 </li>
	 *		<li>Double quotes used as left double quotes are converted
	 *			 to a left double quote entity string (&ldquo;).
	 *			 </li>
	 *		<li>Double quotes used as left quotes re converted
	 *			 to a left single quote entity string (&rdquo;).
	 *			 </li>
	 *	</ul>
	 */

	public static String repairQuotes
	(
		String s ,
		Matcher contractionsMatcher ,
		TaggedStrings contractions
	)
	{
		String result	= s;
								//	Convert two backticks in a row
								//	to a left double quote.

		result	= result.replaceAll( "``" , ldquo );

								//	Convert remaining backticks to
								//	a left single quote.

		result	= result.replaceAll( "`" , lsquo );

								//	Handle whitespace-surrounded
								//	double quotes.  Map these to a
								//	weird Unicode character.  We will
								//	map them to proper quotes after
								//	all the other quotes are substituted.
		result	=
			result.replaceAll
			(
				"(\\s)\"(?=\\s)",
				"$1" + dq
			);
								//	Handle whitespace-surrounded
								//	single quotes.  Map these to a
								//	weird Unicode character.  We will
								//	map them to proper quotes after
								//	all the other quotes are substituted.
		result	=
			result.replaceAll
			(
				"(\\s)'(?=\\s)",
				"$1" + sq
			);
								//	Convert single quotes to
								//	apostrophes in known contractions.

		if ( contractionsMatcher != null )
		{
			contractionsMatcher.reset( result );

			StringBuffer sb	= new StringBuffer();

			while ( contractionsMatcher.find() )
			{
				String group1		=
					contractionsMatcher.group( 1 );

				String group2		=
					contractionsMatcher.group( 2 );

				if ( contractions.containsString( group2 ) )
				{
					group2	= group2.replaceAll( "'" , apos );
                }

				String s2	= group1 + group2;

				contractionsMatcher.appendReplacement( sb , s2 );
			}

	 		contractionsMatcher.appendTail( sb );

 			result	= sb.toString();
		}
								//	If the first character is a quote
								//	followed by punctuation at a
								//	non-word-break, close the quote.
		result		=
			result.replaceAll( "^'(?=\\p{Punct}\\B)" , lsquo );

		result		=
			result.replaceAll( "^\"(?=\\p{Punct}\\B)" , ldquo );

								//	Handle double sets of quotes, e.g.,
								//	double quote followed by single quote
								//	or vice versa.
		result	=
			result.replaceAll( "\"'(?=\\w)" , ldquo + lsquo );

		result	=
			result.replaceAll( "\"'(?=\\W)" , rdquo + rsquo );

		result	=
			result.replaceAll( "'\"(?=\\w)" , lsquo + ldquo );

		result	=
			result.replaceAll( "'\"(?=\\W)" , rsquo + rdquo );

								//	Handle decade abbreviation of the form
								//	'80s .
		result	=
			result.replaceAll( "'(?=\\d{2}s)" , apos );

		String closeClass	= "[^\\ \\t\\r\\n\\[\\{\\(\\-\u2013\u2014]";

								//	Handle most left single quotes.
		result	=
			result.replaceAll
			(
				"(\\s|-{1,}|\u2013{1,}|\u2014{1,})'(?=\\w)",
				"$1" + lsquo
			);
								//	Handle internal single quote as
								//	apostrophe.
		result	=
			result.replaceAll
			(
				"(\\w)?'(\\w)" ,
				"$1" + apos + "$2"
			);
								//	Handle trailing word+s' (plural
								//	possessive) as apostrophe.  Note:
								//	This will incorrectly mark some
								//	words with apostrophes instead of
								//	right quotes.  Use with caution.
/*
		result	=
			result.replaceAll
			(
				"(\\w)?(s|S)'(\\W)" ,
				"$1$2" + apos + "$3"
			);
*/
								//	Handle right single quotes.
		result	=
			result.replaceAll
			(
				"(" + closeClass + ")?'" ,
				"$1" + rsquo
			);

		result	=
			result.replaceAll
			(
				"'(?=\\s|s\\b|S\\b)" ,
				rsquo
			);
								//	Remaining single quotes should
								//	be left single quotes.
		result	=
			StringUtils.replaceAll( result , "'" , lsquo );

								//	Handle most left double quotes.
		result	=
			result.replaceAll
			(
				"(\\s|-{1,}|\u2013{1,}|\u2014{1,})\"(?=\\w)",
				"$1" + ldquo
			);
								//	Handle right double quotes.
		result	=
			result.replaceAll
			(
				"(" + closeClass + ")?\"" ,
				"$1" + rdquo
			);

		result	=
			result.replaceAll
			(
				"\"(?=\\s)" ,
				rdquo
			);
								//	Remaining double quotes should
								//	be left double quotes.
		result	=
			StringUtils.replaceAll( result , "\"" , ldquo );

								//	Fixup the single quotes we
								//	mapped to a weird Unicode character.
								//	Even count quotes are converted
								//	to a right quote, odd quotes to a
								//	left quote.

		int iPos		= result.indexOf( sq );
		boolean even	= false;

		while ( iPos >= 0 )
		{
			result	=
				result.substring( 0 , iPos ) +
				( even ? rsquo : lsquo ) +
				result.substring( iPos + 1 );

			even	= !even;
			iPos	= result.indexOf( sq );
		}
								//	Fixup the double quotes we
								//	mapped to a weird Unicode character.
								//	Even count quotes are converted
								//	to a right quote, odd quotes to a
								//	left quote.

		iPos	= result.indexOf( dq );
		even	= false;

		while ( iPos >= 0 )
		{
			result	=
				result.substring( 0 , iPos ) +
				( even ? rdquo : ldquo ) +
				result.substring( iPos + 1 );

			even	= !even;
			iPos	= result.indexOf( dq );
		}

		return result;
	}

	/**	Load list of non-breakable words and contractions.
	 */

	public static TaggedStrings loadContractions( String contractionsURL )
	{
		TextFile contractionsFile	=
			new TextFile
			(
				DefaultWordTokenizer.class.getResourceAsStream(
					contractionsURL ) ,
				"utf-8"
			);

		TaggedStrings contractions	=
			new SingleTagTaggedStrings
			(
				contractionsFile.toArray() ,
				"1"
			);

		return contractions;
	}

	/**	Build contractions pattern.
	 *
	 *	@param	contractions	Contractions as a tagged strings list.
	 *
	 *	@return					Regular expression for matching
	 *							contractions.
	 */

	public static Pattern buildContractionsPattern
	(
		TaggedStrings contractions
	)
	{
								//	Build contraction pattern.
		return
			Pattern.compile
			(
				"(\\W)(\'\\w*|\\w*\')(?=\\W)"
			);
	}

	/**	Allow overrides but not instantiation.
	 */

	protected FixQuotes()
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



