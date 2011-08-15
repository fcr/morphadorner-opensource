package edu.northwestern.at.utils.corpuslinguistics.tokenizer;

/*	Please see the license information at the end of this file. */

import java.util.*;
import edu.northwestern.at.utils.ListFactory;
import edu.northwestern.at.utils.PatternReplacer;

/**	Split text into tokens according the Penn Treebank tokenization rules.
 *
 *	<p>
 *	Based upon the sed script written by Robert McIntyre at
 *	http://www.cis.upenn.edu/~treebank/tokenizer.sed .
 *	</p>
 */

public class PennTreebankTokenizer
	extends AbstractWordTokenizer
	implements WordTokenizer
{
	/**	Replacement patterns for transforming original text. */

	protected static List<PatternReplacer> pennPatterns	=
		ListFactory.createNewList();

	/**	Create a simple word tokenizer.
	 */

	public PennTreebankTokenizer()
	{
	}

	public static String prepareTextForTokenization( String s )
	{
		for ( int i = 0 ; i < pennPatterns.size() ; i++ )
		{
			s	= pennPatterns.get( i ).replace( s );
		}

		return s.trim();
	}

	/**	Break text into word tokens.
	 *
	 *	@param	text			Text to break into word tokens.
	 *
	 *	@return					List of word tokens.
	 *
	 *	<p>
	 *	Word tokens may be words, numbers, punctuation, etc.
	 *	</p>
	 */

	 public List<String> extractWords( String text )
	 {
								//	Holds listof tokenized words.

	 	List<String> result	= ListFactory.createNewList();

	 							//	Prepare text for tokenization
	 							//	by splitting words and punctuation
	 							//	according to Penn Treebank rules.

	 	String fixedText	= prepareTextForTokenization( text );

	 							//	All we have to do now is pick
	 							//	up the individual "words" which
	 							//	are separated by one or more blanks.
	 							//	Use a StringTokenizer for this.

	 	StringTokenizer tokenizer	= new StringTokenizer( fixedText );

								//	Add each token to the results list.

		while ( tokenizer.hasMoreTokens() )
		{
			result.add( tokenizer.nextToken() );
		}
                    			//	Return tokenizer list of words.
		return result;
	}

	/**	Static initializer. */

	static
	{
		pennPatterns.add( new PatternReplacer( "``", "`` " ) );
		pennPatterns.add( new PatternReplacer( "''", "  ''" ) );
		pennPatterns.add( new PatternReplacer( "([?!\".,;:@#$%&])", " $1 " ) );
		pennPatterns.add( new PatternReplacer( "\\.\\.\\.", " ... " ) );
		pennPatterns.add( new PatternReplacer( "\\s+", " " ) );

		pennPatterns.add( new PatternReplacer( ",([^0-9])", " , $1" ) );

		pennPatterns.add( new PatternReplacer( "([^.])([.])([\\])}>\"']*)\\s*$", "$1 $2$3 " ) );

		pennPatterns.add( new PatternReplacer( "([\\[\\](){}<>])", " $1 " ) );
		pennPatterns.add( new PatternReplacer( "--", " -- " ) );

		pennPatterns.add( new PatternReplacer( "$", " " ) );
		pennPatterns.add( new PatternReplacer( "^", " " ) );

		pennPatterns.add( new PatternReplacer( "([^'])' ", "$1 ' " ) );
		pennPatterns.add( new PatternReplacer( "'([sSmMdD]) ", " '$1 " ) );
		pennPatterns.add( new PatternReplacer( "'ll ", " 'll " ) );
		pennPatterns.add( new PatternReplacer( "'re ", " 're " ) );
		pennPatterns.add( new PatternReplacer( "'ve ", " 've " ) );
		pennPatterns.add( new PatternReplacer( "'em ", " 'em " ) );
		pennPatterns.add( new PatternReplacer( "n't ", " n't " ) );
		pennPatterns.add( new PatternReplacer( "'LL ", " 'LL " ) );
		pennPatterns.add( new PatternReplacer( "'RE ", " 'RE " ) );
		pennPatterns.add( new PatternReplacer( "'EM ", " 'EM " ) );
		pennPatterns.add( new PatternReplacer( "'VE ", " 'VE " ) );
		pennPatterns.add( new PatternReplacer( "N'T ", " N'T " ) );

		pennPatterns.add( new PatternReplacer( " ([Cc])annot ", " $1an not " ) );
		pennPatterns.add( new PatternReplacer( " ([Dd])'ye ", " $1' ye " ) );
		pennPatterns.add( new PatternReplacer( " ([Gg])imme ", " $1im me " ) );
		pennPatterns.add( new PatternReplacer( " ([Gg])onna ", " $1on na " ) );
		pennPatterns.add( new PatternReplacer( " ([Gg])otta ", " $1ot ta " ) );
		pennPatterns.add( new PatternReplacer( " ([Ll])emme ", " $1em me " ) );
		pennPatterns.add( new PatternReplacer( " ([Mm])ore'n ", " $1ore 'n " ) );
		pennPatterns.add( new PatternReplacer( " '([Tt])is ", " '$1 is " ) );
		pennPatterns.add( new PatternReplacer( " '([Tt])was ", " '$1 was " ) );
		pennPatterns.add( new PatternReplacer( " ([Ww])anna ", " $1an na " ) );
		pennPatterns.add( new PatternReplacer( " ([Ww])anna ", " $1an na " ) );
		pennPatterns.add( new PatternReplacer( " ([Ww])haddya ", " $1ha dd ya " ) );
		pennPatterns.add( new PatternReplacer( " ([Ww])hatcha ", " $1ha t cha " ) );

		pennPatterns.add( new PatternReplacer( "([A-MO-Za-mo-z])'([tT])" , "$1 '$2" ) );

		pennPatterns.add( new PatternReplacer( " ([A-Z]) \\.", " $1. " ) );
		pennPatterns.add( new PatternReplacer( "\\s+", " " ) );
		pennPatterns.add( new PatternReplacer( "^\\s+", "" ) );
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



