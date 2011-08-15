package edu.northwestern.at.utils.corpuslinguistics.tokenizer;

/*	Please see the license information at the end of this file. */

import java.util.*;
import edu.northwestern.at.utils.ListFactory;

/**	Split text containing contraction into separate tokens.
 */

public class ContractionTokenizer
	extends AbstractWordTokenizer
	implements WordTokenizer
{
	/**	Create a contraction tokenizer.
	 */

	public ContractionTokenizer()
	{
	}

	public static String prepareTextForTokenization( String str )
	{
		str = str.replaceAll("([^'])' ", "$1 ' ");
		str = str.replaceAll("'([sSmMdD]) ", " '$1 ");
		str = str.replaceAll("'ll ", " 'll ");
		str = str.replaceAll("'re ", " 're ");
		str = str.replaceAll("'ve ", " 've ");
		str = str.replaceAll("n't ", " n't ");
		str = str.replaceAll("'t ", " 't ");
		str = str.replaceAll("'s ", " 's ");
		str = str.replaceAll("'LL ", " 'LL ");
		str = str.replaceAll("'RE ", " 'RE ");
		str = str.replaceAll("'VE ", " 'VE ");
		str = str.replaceAll("N'T ", " N'T ");
		str = str.replaceAll("'T ", " 'T ");
		str = str.replaceAll("'S ", " 'S ");

		str = str.replaceAll(" ([Cc])annot ", " $1an not ");
		str = str.replaceAll(" ([Dd])'ye ", " $1' ye ");
		str = str.replaceAll(" ([Gg])imme ", " $1im me ");
		str = str.replaceAll(" ([Gg])onna ", " $1on na ");
		str = str.replaceAll(" ([Gg])otta ", " $1ot ta ");
		str = str.replaceAll(" ([Ll])emme ", " $1em me ");
		str = str.replaceAll(" ([Mm])ore'n ", " $1ore 'n ");
		str = str.replaceAll(" '([Tt])is ", " '$1 is ");
		str = str.replaceAll(" '([Tt])was ", " '$1 was ");
		str = str.replaceAll(" ([Ww])anna ", " $1an na ");

		str = str.trim();

		return str;
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
								//	Holds list of tokenized words.

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


