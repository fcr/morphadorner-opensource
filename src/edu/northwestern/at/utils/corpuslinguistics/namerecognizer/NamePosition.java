package edu.northwestern.at.utils.corpuslinguistics.namerecognizer;

/*	Please see the license information at the end of this file. */

/**	Records name position in tokenized text.
 *
 *	<p>
 *	Given a text split into sentences and words, a name position
 *	specifies the position of a name string as a triple consisting
 *	of the sentence number, starting word number in the sentence,
 *	and ending word number in the sentence.
 *	</p>
 */

public class NamePosition
{
	/**	Sentence number. */

	public int sentence;

	/**	Starting word number in sentence. */

	public int startingWord;

	/**	Ending word number in sentence. */

	public int endingWord;

	/**	Proper noun count. */

	public int properNounCount;

	/**	Create empty name position.
	 *
	 */

	public NamePosition()
	{
		sentence		= 0;
		startingWord	= 0;
		endingWord		= 0;
		properNounCount	= 0;
	}

	/**	Create name position from sentence and word positions.
	 *
	 *	@param	sentence		The sentence number containing the name.
	 *	@param	startingWord	The starting word.
	 *	@param	endingWord		The ending word.
	 *	@param	properNounCount	The proper noun count.
	 */

	public NamePosition
	(
		int sentence ,
		int startingWord ,
		int endingWord ,
		int properNounCount
	)
	{
		this.sentence			= sentence;
		this.startingWord		= startingWord;
		this.endingWord			= endingWord;
		this.properNounCount	= properNounCount;
	}

	/**	Return name position as a string.
	 *
	 *	@return		String in form (sentence, starting word, ending word).
	 */

	public String toString()
	{
		return "[" + sentence + "," + startingWord + "," + endingWord + "]";
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



