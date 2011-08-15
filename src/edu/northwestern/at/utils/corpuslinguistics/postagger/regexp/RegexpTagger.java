package edu.northwestern.at.utils.corpuslinguistics.postagger.regexp;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.util.regex.*;

import edu.northwestern.at.utils.corpuslinguistics.adornedword.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.unigram.*;
import edu.northwestern.at.utils.corpuslinguistics.tokenizer.*;

/**	Regular Expression Part of Speech tagger.
 *
 *	<p>
 *	The regular expression part of speech tagger uses a
 *	regular expressions to assign a part of speech tag to a spelling.
 *	</p>
 */

public class RegexpTagger extends UnigramTagger
	implements PartOfSpeechTagger, CanTagOneWord
{
	/**	Parts of speech for each lexical rule.
	 */

	protected Pattern[] regexpPatterns;
	protected Matcher[] regexpMatchers;
	protected String[] regexpTags;

	/**	Create a suffix tagger.
	 */

	public RegexpTagger()
	{
	}

	/**	See if tagger uses lexical rules.
	 *
	 *	@return		True since this tagger uses regular expression based
	 *				lexical rules.
	 */

	public boolean usesLexicalRules()
	{
		return true;
	}

	/**	Set lexical rules for tagging.
	 *
	 *	@param	lexicalRules	String array of lexical rules.
	 *
	 *	@throws	InvalidRuleException if a rule is bad.
	 *
	 *	<p>
	 *	For the regular expression tagger, each rule takes the form:
	 *	</p>
	 *
	 *	<blockquote>
	 *	<p>
	 *	<code>
	 *	regular-expression \t part-of-speech-tag
	 *	</code>
	 *	</p>
	 *	</blockquote>
	 *
	 *	<p>
	 *	where "regular expression" is the regular expression
	 *	and "part-of-speech-tag" is the part of speech tag to
	 *	assign to a spelling matched by the regular expression.
	 *	An ascii tab character (\t) separates the pattern from
	 *	the tag.
	 *	</p>
	 */

	public void setLexicalRules( String[] lexicalRules )
		throws InvalidRuleException
	{
		this.regexpPatterns	= new Pattern[ lexicalRules.length ];
		this.regexpMatchers	= new Matcher[ lexicalRules.length ];
		this.regexpTags		= new String[ lexicalRules.length ];

		for ( int i = 0 ; i < lexicalRules.length ; i++ )
		{
			String[] tokens				= lexicalRules[ i ].split( "\t" );
			this.regexpPatterns[ i ]	= Pattern.compile( tokens[ 0 ] );
			this.regexpMatchers[ i ]	=
				this.regexpPatterns[ i ].matcher( "" );
			this.regexpTags[ i ]		= tokens[ 1 ];
		}
	}

	/**	Tag a single word.
	 *
	 *	@param	word	The word.
	 *
	 *	@return			The part of speech for the word.
	 *
	 *	<p>
	 *	Applies each of the regular expressions stored in the lexical
	 *	rules lexicon and returns the tag of associated with the first
	 *	matching regular expression.
	 *	</p>
	 */

	public String tagWord( String word )
	{
		String result	= "";
								//	Try each regular expression in turn.

		for ( int i = 0 ; i < regexpMatchers.length ; i++ )
		{
			regexpMatchers[ i ].reset( word );

			if ( regexpMatchers[ i ].find() )
			{
				result	= regexpTags[ i ];
				break;
			}
		}

		return result;
	}

	/**	Return tagger description.
	 *
	 *	@return		Tagger description.
	 */

	public String toString()
	{
		return "Regular expression tagger";
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



