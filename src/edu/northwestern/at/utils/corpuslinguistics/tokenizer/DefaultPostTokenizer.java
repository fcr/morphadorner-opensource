package edu.northwestern.at.utils.corpuslinguistics.tokenizer;

/*	Please see the license information at the end of this file. */

import edu.northwestern.at.utils.*;

/**	Default post tokenizer which processes tokens after extraction.
 */

public class DefaultPostTokenizer
	extends AbstractPostTokenizer
	implements PostTokenizer
{
	/**	Create a default postTokenizer.
	 */

	public DefaultPostTokenizer()
	{
		super();
	}

	/**	Process a token after tokenization.
	 *
	 *	@param	token	The token to process after tokenization.
	 *
	 *	@return			Array of two strings.
	 *					[0]	= the token minimally processed.
	 *					[1]	= the token maximally processed.
	 *
	 *	<p>
	 *	The minimally processed token is typically results in an original
	 *	spelling.
	 *	</p>
	 *	<p>
	 *	The maximally processed token typically results in a
	 *	partially or completely standardized spelling.
	 *	</p>
	 *
	 *	<p>
	 *	These may be identical.
	 *	</p>
	 *
	 */

	public String[] postTokenize( String token )
	{
		String fixedToken	=
			StringUtils.replaceAll
			(
				token ,
				CharUtils.CHAR_FAKE_SOFT_HYPHEN_STRING ,
				""
			);

		return new String[]{ fixedToken , fixedToken };
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



