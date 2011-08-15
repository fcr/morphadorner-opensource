package edu.northwestern.at.utils.corpuslinguistics.postagger.unigram;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.adornedword.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.*;
import edu.northwestern.at.utils.corpuslinguistics.tokenizer.*;

/**	Unigram Part of Speech tagger.
 *
 *	<p>
 *	The unigram part of speech tagger uses a lexicon to assign
 *	the most frequently occurring part of speech tag to a spelling.
 *	</p>
 */

public class UnigramTagger extends AbstractPartOfSpeechTagger
	implements PartOfSpeechTagger, CanTagOneWord
{
	/**	Create a unigram tagger.
	 */

	public UnigramTagger()
	{
	}

	/**	Tag an adorned word list.
	 *
	 *	@param	sentence	The sentence as a list of adorned words.
	 *
	 *	@return				An {@link edu.northwestern.at.utils.corpuslinguistics.adornedword.AdornedWord}
	 *							of the words in the sentence tagged with
	 *							parts of speech.
	 *
	 *	<p>
	 *	The input sentence is a
	 *	{@link edu.northwestern.at.utils.corpuslinguistics.adornedword.AdornedWord}
	 *	of words to tag.  The output is the same list
	 *	with parts of speech added.
	 *	</p>
	 */

	public<T extends AdornedWord> List<T> tagAdornedWordList
	(
		List<T> sentence
	)
	{
								//	Iterate over words in sentence.

		for ( int i = 0 ; i < sentence.size() ; i++ )
		{
								//	Get next word.

			AdornedWord word	= sentence.get( i );

								//	Get part of speech tag for word.

			tagWord( word );
		}
								//	We have a new finished sentence.

		return sentence;
	}

	/**	Tag a single word.
	 *
	 *	@param	word	The word.
	 *
	 *	@return			The part of speech for the word.
	 *
	 *	<p>
	 *	Returns most common tag for word.
	 *	</p>
	 */

	public String tagWord( String word )
	{
		return getMostCommonTag( word );
	}

	/**	Tag a single word.
	 *
	 *	@param	word	The word as an AdornedWord.
	 *
	 *	@return			The part of speech for the word.
	 *					The part of speech is also set in the
	 *					input AdornedWord.
	 *
	 *	<p>
	 *	Returns most common tag for word.
	 *	</p>
	 */

	public String tagWord( AdornedWord word )
	{
		String result	= tagWord( word.getSpelling() );

		word.setPartsOfSpeech( result );

		return result;
	}

	/**	Return tagger description.
	 *
	 *	@return		Tagger description.
	 */

	public String toString()
	{
		return "Unigram tagger";
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



