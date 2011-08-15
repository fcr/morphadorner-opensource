package edu.northwestern.at.utils.corpuslinguistics.adornedword;

/*	Please see the license information at the end of this file. */

/**	Interface for a word adorned with addition morphological information.
 *
 *	<p>
 *	An {@link edu.northwestern.at.utils.corpuslinguistics.adornedword.AdornedWord}
 *	represents a single word spelling,
 *	symbol, or punctuation mark in text.
 *	</p>
 *
 *	<p>
 *	An adorned word records the following information.
 *	</p>
 *
 *	<ul>
 *		<li>The original form of the token as it appears in input text.
 *			</li>
 *		<li>The spelling form of the token with transcription errors
 *			corrected.  This is the form used by MorphAdorner for
 *			subsequent morphological processing.
 *			</li>
 *		<li>A standard spelling.  This is the standard modern form
 *			of the spelling for a word.  If the word is obsolete, the
 *			standard form is the most recent commonly used spelling.
 *			</li>
 *		<li>A lemma.  This is the standard dictionary headword form
 *			of the spelling.  Some spellings such as contractions
 *			contain multiple lemmata.  In this case, the lemma
 *			consists of a string of individual lemmata separated
 *			by a separator character, usually a vertical bar "|".
 *			For example, the lemma form of
 *			the contraction "isn't" is "be|not".
 *			</li>
 *		<li>A part of speech.  Each lemma form in the spelling
 *			has an associated part of speech.  Depending upon
 *			the tag set, the individual parts of speech may be
 *			separated by colons, slashes, vertical bars, etc.
 *			</li>
 *		<li>The token type.  This gives the broad orthographic class
 *			of the spelling such as word, symbol, currency, number,
 *			etc.
 *			</li>
 *	</ul>
 */

public interface AdornedWord
{
	/**	Get the original token.
	 *
	 *	@return		The original token.
	 */

	public String getToken();

	/**	Set the original token.
	 *
	 *	@param	token	The original token.
	 */

	public void setToken( String token );

	/**	Get the spelling.
	 *
	 *	@return		The spelling.
	 */

	public String getSpelling();

	/**	Set the spelling.
	 *
	 *	@param	spelling	The spelling.
	 */

	public void setSpelling( String spelling );

	/**	Get the standard spelling.
	 *
	 *	@return		The standard spelling.
	 */

	public String getStandardSpelling();

	/**	Set the standard spelling.
	 *
	 *	@param	standardSpelling	The standard spelling.
	 */

	public void setStandardSpelling( String standardSpelling );

	/**	Get the lemmata.
	 *
	 *	@return		The lemmata.
	 *
	 *	<p>
	 *	Compound lemmata are separated by a separator tring.
	 *	</p>
	 */

	public String getLemmata();

	/**	Set the lemmata.
	 *
	 *	@param	lemmata		The lemmata.
	 */

	public void setLemmata( String lemmata );

	/**	Get the parts of speech.
	 *
	 *	@return		The parts of speech.
	 */

	public String getPartsOfSpeech();

	/**	Set the parts of speech.
	 *
	 *	@param	partsOfSpeech	The parts of speech, separated by
	 *								a tag set dependent separator
	 *								string.
	 */

	public void setPartsOfSpeech( String partsOfSpeech );

	/**	Get the token type.
	 *
	 *	@return		The token type.
	 */

	public int getTokenType();

	/**	Set the token type.
	 *
	 *	@param	tokenType	The token type.
	 */

	public void setTokenType( int tokenType );
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



