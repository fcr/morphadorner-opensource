package edu.northwestern.at.utils.corpuslinguistics.tokenizer;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.utils.*;

/**	Simple word tokenizer which splits on whitespace only. */

public class WhitespaceWordTokenizer
	extends AbstractWordTokenizer
	implements WordTokenizer
{
	/**	Create a word tokenizer than breaks on whitespace only.
	 */

	public WhitespaceWordTokenizer()
	{
		super();
	}

	/**	Break text into word tokens.
	 *
	 *	@param	text			Text to break into word tokens.
	 *
	 *	@return					Input text broken on white space.
	 */

	 public List<String> extractWords( String text )
	 {
	 	List<String> result	= ListFactory.createNewList();

		StringTokenizer stringTokenizer	=
			new StringTokenizer( preTokenizer.pretokenize( text ) );

		while ( stringTokenizer.hasMoreTokens() )
		{
			String token	= stringTokenizer.nextToken();

			token			= preprocessToken( token , result );

								//	If the token is not empty,
								//	add it to the sentence.

			if ( token.length() > 0 )
			{
								//	Check if we need to split a token
								//	containing an internal period.

				String[] tokens	= splitToken( token );

				for ( int k = 0 ; k < tokens.length ; k++ )
				{
					if ( tokens[ k ].length() > 0 )
					{
						addWordToSentence( result ,  tokens[ k ] );
					}
				}
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



