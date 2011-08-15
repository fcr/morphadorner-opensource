package edu.northwestern.at.utils.corpuslinguistics.sentencesplitter;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.utils.corpuslinguistics.postagger.guesser.*;
import edu.northwestern.at.utils.corpuslinguistics.tokenizer.*;

/**	Interface for splitting text into sentences. */

public interface SentenceSplitter
{
	/**	Set part of speech guesser.
	 *
	 *	@param	partOfSpeechGuesser		Part of speech guesser.
	 *
	 *	<p>
	 *	A sentence splitter may use part of speech information
	 *	to disambiguate end-of-sentence boundary conditions.
	 *	The part of speech guesser provides access to the
	 *	lexicons and guessing algorithms for determining the
	 *	possible parts of speech for a word without performing
	 *	a full part of speech tagging operation.
	 *	</p>
	 */

	public void setPartOfSpeechGuesser
	(
		PartOfSpeechGuesser partOfSpeechGuesser
	);

	/**	Set sentence splitter iterator.
	 *
	 *	@param	sentenceSplitterIterator	Sentence splitter iterator.
	 */

	public void setSentenceSplitterIterator
	(
		SentenceSplitterIterator sentenceSplitterIterator
	);

	/**	Break text into sentences and tokens.
	 *
	 *	@param	text		Text to break into sentences and tokens.
	 *	@param	tokenizer	Tokenizer to use for breaking sentences
	 *						into words.
	 *
	 *	@return				List of sentences.  Each sentence
	 *						is itself a list of word tokens.
	 *
	 *	<p>
	 *	Word tokens may be words, numbers, punctuation, etc.
	 *	</p>
	 */

	public List<List<String>> extractSentences
	(
		String text ,
		WordTokenizer tokenizer
	);

	/**	Break text into sentences and tokens.
	 *
	 *	@param	text		Text to break into sentences and tokens.
	 *
	 *	@return				List of sentences.  Each sentence
	 *						is itself a list of word tokens.
	 *
	 *	<p>
	 *	Word tokens may be words, numbers, punctuation, etc.  The default
	 *	word tokenizer is used.
	 *	</p>
	 */

	public List<List<String>> extractSentences( String text );

	/**	Find starting offsets of sentences extracted from a text.
	 *
	 *	@param	text			Text from which sentences were
	 *							extracted.
	 *
	 *	@param	sentences		List of sentences (each a list of
	 *							words) extracted from text.
	 *
	 *							N.B.  If the sentences aren't from
	 *							the specified text, the resulting
	 *							offsets will be meaningless.
	 *
	 *	@return					int array of starting offsets in text
	 *							for each sentence.  The first offset
	 *							starts at 0.  There is one more offset
	 *							than the number of sentences -- the
	 *							last offset is where the sentence
	 *							after the last sentence would start.
	 */

	public int[] findSentenceOffsets
	(
		String text ,
		List<List<String>> sentences
	);
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



