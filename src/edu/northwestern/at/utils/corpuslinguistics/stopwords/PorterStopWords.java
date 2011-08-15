package edu.northwestern.at.utils.corpuslinguistics.stopwords;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.utils.SetFactory;

/**	Martin Porter's stop word list.
 */

public class PorterStopWords
	extends BaseStopWords
	implements StopWords
{
	/**	Stop words. */

	protected static Set<String> porterStopWordsSet	=
		SetFactory.createNewSet();

	/**	Create the stop word filter.
	 */

	public PorterStopWords()
	{
		stopWordsSet.addAll( porterStopWordsSet );
	}

	/**	Static initializer. */

	static
	{
		porterStopWordsSet.add( "a" );
		porterStopWordsSet.add( "about" );
		porterStopWordsSet.add( "above" );
		porterStopWordsSet.add( "after" );
		porterStopWordsSet.add( "again" );
		porterStopWordsSet.add( "against" );
		porterStopWordsSet.add( "all" );
		porterStopWordsSet.add( "am" );
		porterStopWordsSet.add( "an" );
		porterStopWordsSet.add( "and" );
		porterStopWordsSet.add( "any" );
		porterStopWordsSet.add( "are" );
		porterStopWordsSet.add( "aren't" );
		porterStopWordsSet.add( "as" );
		porterStopWordsSet.add( "at" );
		porterStopWordsSet.add( "be" );
		porterStopWordsSet.add( "because" );
		porterStopWordsSet.add( "been" );
		porterStopWordsSet.add( "before" );
		porterStopWordsSet.add( "being" );
		porterStopWordsSet.add( "below" );
		porterStopWordsSet.add( "between" );
		porterStopWordsSet.add( "both" );
		porterStopWordsSet.add( "but" );
		porterStopWordsSet.add( "by" );
		porterStopWordsSet.add( "can't" );
		porterStopWordsSet.add( "cannot" );
		porterStopWordsSet.add( "could" );
		porterStopWordsSet.add( "couldn't" );
		porterStopWordsSet.add( "did" );
		porterStopWordsSet.add( "didn't" );
		porterStopWordsSet.add( "do" );
		porterStopWordsSet.add( "does" );
		porterStopWordsSet.add( "doesn't" );
		porterStopWordsSet.add( "doing" );
		porterStopWordsSet.add( "don't" );
		porterStopWordsSet.add( "down" );
		porterStopWordsSet.add( "during" );
		porterStopWordsSet.add( "each" );
		porterStopWordsSet.add( "few" );
		porterStopWordsSet.add( "for" );
		porterStopWordsSet.add( "from" );
		porterStopWordsSet.add( "further" );
		porterStopWordsSet.add( "had" );
		porterStopWordsSet.add( "hadn't" );
		porterStopWordsSet.add( "has" );
		porterStopWordsSet.add( "hasn't" );
		porterStopWordsSet.add( "have" );
		porterStopWordsSet.add( "haven't" );
		porterStopWordsSet.add( "having" );
		porterStopWordsSet.add( "he" );
		porterStopWordsSet.add( "he'd" );
		porterStopWordsSet.add( "he'll" );
		porterStopWordsSet.add( "he's" );
		porterStopWordsSet.add( "her" );
		porterStopWordsSet.add( "here" );
		porterStopWordsSet.add( "here's" );
		porterStopWordsSet.add( "hers" );
		porterStopWordsSet.add( "herself" );
		porterStopWordsSet.add( "him" );
		porterStopWordsSet.add( "himself" );
		porterStopWordsSet.add( "his" );
		porterStopWordsSet.add( "how" );
		porterStopWordsSet.add( "how's" );
		porterStopWordsSet.add( "i" );
		porterStopWordsSet.add( "i'd" );
		porterStopWordsSet.add( "i'll" );
		porterStopWordsSet.add( "i'm" );
		porterStopWordsSet.add( "i've" );
		porterStopWordsSet.add( "if" );
		porterStopWordsSet.add( "in" );
		porterStopWordsSet.add( "into" );
		porterStopWordsSet.add( "is" );
		porterStopWordsSet.add( "isn't" );
		porterStopWordsSet.add( "it" );
		porterStopWordsSet.add( "it's" );
		porterStopWordsSet.add( "its" );
		porterStopWordsSet.add( "itself" );
		porterStopWordsSet.add( "let's" );
		porterStopWordsSet.add( "me" );
		porterStopWordsSet.add( "more" );
		porterStopWordsSet.add( "most" );
		porterStopWordsSet.add( "mustn't" );
		porterStopWordsSet.add( "my" );
		porterStopWordsSet.add( "myself" );
		porterStopWordsSet.add( "no" );
		porterStopWordsSet.add( "nor" );
		porterStopWordsSet.add( "not" );
		porterStopWordsSet.add( "of" );
		porterStopWordsSet.add( "off" );
		porterStopWordsSet.add( "on" );
		porterStopWordsSet.add( "once" );
		porterStopWordsSet.add( "only" );
		porterStopWordsSet.add( "or" );
		porterStopWordsSet.add( "other" );
		porterStopWordsSet.add( "ought" );
		porterStopWordsSet.add( "our" );
		porterStopWordsSet.add( "ours" );
		porterStopWordsSet.add( "ourselves" );
		porterStopWordsSet.add( "out" );
		porterStopWordsSet.add( "over" );
		porterStopWordsSet.add( "own" );
		porterStopWordsSet.add( "same" );
		porterStopWordsSet.add( "shan't" );
		porterStopWordsSet.add( "she" );
		porterStopWordsSet.add( "she'd" );
		porterStopWordsSet.add( "she'll" );
		porterStopWordsSet.add( "she's" );
		porterStopWordsSet.add( "should" );
		porterStopWordsSet.add( "shouldn't" );
		porterStopWordsSet.add( "so" );
		porterStopWordsSet.add( "some" );
		porterStopWordsSet.add( "such" );
		porterStopWordsSet.add( "than" );
		porterStopWordsSet.add( "that" );
		porterStopWordsSet.add( "that's" );
		porterStopWordsSet.add( "the" );
		porterStopWordsSet.add( "their" );
		porterStopWordsSet.add( "theirs" );
		porterStopWordsSet.add( "them" );
		porterStopWordsSet.add( "themselves" );
		porterStopWordsSet.add( "then" );
		porterStopWordsSet.add( "there" );
		porterStopWordsSet.add( "there's" );
		porterStopWordsSet.add( "these" );
		porterStopWordsSet.add( "they" );
		porterStopWordsSet.add( "they'd" );
		porterStopWordsSet.add( "they'll" );
		porterStopWordsSet.add( "they're" );
		porterStopWordsSet.add( "they've" );
		porterStopWordsSet.add( "this" );
		porterStopWordsSet.add( "those" );
		porterStopWordsSet.add( "through" );
		porterStopWordsSet.add( "to" );
		porterStopWordsSet.add( "too" );
		porterStopWordsSet.add( "under" );
		porterStopWordsSet.add( "until" );
		porterStopWordsSet.add( "up" );
		porterStopWordsSet.add( "very" );
		porterStopWordsSet.add( "was" );
		porterStopWordsSet.add( "wasn't" );
		porterStopWordsSet.add( "we" );
		porterStopWordsSet.add( "we'd" );
		porterStopWordsSet.add( "we'll" );
		porterStopWordsSet.add( "we're" );
		porterStopWordsSet.add( "we've" );
		porterStopWordsSet.add( "were" );
		porterStopWordsSet.add( "weren't" );
		porterStopWordsSet.add( "what" );
		porterStopWordsSet.add( "what's" );
		porterStopWordsSet.add( "when" );
		porterStopWordsSet.add( "when's" );
		porterStopWordsSet.add( "where" );
		porterStopWordsSet.add( "where's" );
		porterStopWordsSet.add( "which" );
		porterStopWordsSet.add( "while" );
		porterStopWordsSet.add( "who" );
		porterStopWordsSet.add( "who's" );
		porterStopWordsSet.add( "whom" );
		porterStopWordsSet.add( "why" );
		porterStopWordsSet.add( "why's" );
		porterStopWordsSet.add( "with" );
		porterStopWordsSet.add( "won't" );
		porterStopWordsSet.add( "would" );
		porterStopWordsSet.add( "wouldn't" );
		porterStopWordsSet.add( "you" );
		porterStopWordsSet.add( "you'd" );
		porterStopWordsSet.add( "you'll" );
		porterStopWordsSet.add( "you're" );
		porterStopWordsSet.add( "you've" );
		porterStopWordsSet.add( "your" );
		porterStopWordsSet.add( "yours" );
		porterStopWordsSet.add( "yourself" );
		porterStopWordsSet.add( "yourselves" );
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



