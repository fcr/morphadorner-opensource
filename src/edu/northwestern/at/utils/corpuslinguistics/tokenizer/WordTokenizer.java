package edu.northwestern.at.utils.corpuslinguistics.tokenizer;

/*	Please see the license information at the end of this file. */

import java.util.*;

/**	Interface for tokenizing a string into "words".
 */

public interface WordTokenizer
{
	/**	Get the preTokenizer.
	 *
	 *	@return			The preTokenizer.
	 */

	public PreTokenizer getPreTokenizer();

	/**	Set the preTokenizer.
	 *
	 *	@param	preTokenizer	The preTokenizer.
	 */

	public void setPreTokenizer( PreTokenizer preTokenizer );

	 /**	Add word to list of words in sentence.
	  *
	  *	@param	sentence	Result sentence.
	  *	@param	word		Word to add.
	  */

	public void addWordToSentence( List<String> sentence , String word );

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

	public List<String> extractWords( String text );

	/**	Find starting offsets of words in a sentence.
	 *
	 *	@param	sentenceText	Text from which tokens were
	 *							extracted.
	 *
	 *	@param	words			List of words extracted from
	 *							sentence text.
	 *
	 *							N.B.  If the words aren't from
	 *							the specified sentence text,
	 *							the resulting offsets will be
	 *							meaningless.
	 *
	 *	@return					int array of starting offsets in
	 *							sentenceText for each word.
	 *							The first offset starts at 0.
	 *							There is one more offset
	 *							than the number of words -- the
	 *							last offset is where the word
	 *							after the last word would start.
	 */

	public int[] findWordOffsets( String sentenceText , List<?> words );

	/**	Preprocess a word token.
	 *
	 *	@param	token			Token to preprocess.
	 *	@param	tokenList		List of previous tokens already issued.
	 *
	 *	@return					Preprocessed token.
	 *							The token list may also have been modified.
	 */

	public String preprocessToken( String token , List<String> tokenList );

	/** Close down the word tokenizer.
	 */

	public void close();
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



