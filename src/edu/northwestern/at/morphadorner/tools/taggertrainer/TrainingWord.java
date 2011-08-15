package edu.northwestern.at.morphadorner.tools.taggertrainer;

/*	Please see the license information at the end of this file. */

/**	Training data for one word in a tagged corpus.
 *
 *	<p>
 *	The training data for a rule-based part of speech tagger consists of a
 *	tagged corpus in which each line contains the following three
 *	columns separated by an Ascii tab character.
 *	</p>
 *
 *	<ol>
 *		<li>The spelling of a word.</li>
 *		<li>The correct part of speech for a word.</li>
 *		<li>The guessed part of speech assigned by a part of speech
 *			tagger.</li>
 *		<li>A flag indicating if this word has been "covered" by a rule.
 *			</li>
 *	</ol>
 *
 *	<p>
 *	A list of such training data entries or "sites" can be used by a
 *	transformation based learning program to generate rules which
 *	attempt to correct erroneous guessed part of tags.  As rules are
 *	generated which "cover" specific training sites, these locations
 *	may need to marked as unavailable for use by other rules.
 *	We use a flag to mark training sites already covered by a
 *	correction rule, or to which no rule can apply -- for example,
 *	for words which are punctuation marks or which have only one
 *	possible part of speech tag.
 *	</p>
 */

public class TrainingWord
{
	/**	Spelling. */

	public final String spelling;

	/**	Correct tag. */

	public final String correctTag;

	/**	Guessed tag. */

	public String guessedTag;

	/**	True if word covered by a rule or not subject to change. */

	public boolean covered;

	/**	Create a training word entry.
	 *
	 *	@param	spelling	The spelling.
	 *	@param	correctTag	The correct tag.
	 *	@param	guessedTag	The guessed tag.
	 *	@param	covered		True if word tag not subject to change.
	 */

	public TrainingWord
	(
		String spelling ,
		String correctTag ,
		String guessedTag ,
		boolean covered
	)
	{
		this.spelling		= spelling;
		this.correctTag		= correctTag;
		this.guessedTag		= guessedTag;
		this.covered		= covered;
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



