package edu.northwestern.at.utils.corpuslinguistics.postagger.smoothing.contextual;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.utils.corpuslinguistics.lexicon.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.*;
import edu.northwestern.at.utils.math.*;

/**	Interface for a contextual smoother.
 *
 *	<p>
 *	A contextual smoother computes the contextually smoothed probabability of
 *	a word given a part of speech tag, e.g., p( word | tag ).
 *	</p>
 */

public interface ContextualSmoother
{
	/**	Set the part of speech tagger for this smoother.
	 *
	 *	@param	posTagger	Part of speech tagger for which
	 *						this smoother provides probabilities.
	 */

	public void setPartOfSpeechTagger( PartOfSpeechTagger posTagger );

	/**	Get the number of cached contextual probabilities.
	 *
	 *	@return		The number of cached contextual probabilities.
	 */

	public int cachedProbabilitiesCount();

	/**	Clear cached probabilities..
	 */

	public void clearCachedProbabilities();

	/**	Compute smoothed contextual probability of a tag given the previous tag.
	 *
	 *	@param	tag				The current tag.
	 *	@param	previousTag		The previous tag.
	 *
	 *	@return					The probability of the current tag given
	 *							the previous tag, e.g,
	 *							p( tag | previousTag ).
	 */

	public Probability contextualProbability
	(
		String tag ,
		String previousTag
	);

	/**	Compute smoothed contextual probability of a tag given the previous tags.
	 *
	 *	@param	tag						The current tag.
	 *	@param	previousTag				The previous tag.
	 *	@param	previousPreviousTag		The previous tag of the previous tag.
	 *
	 *	@return					The probability of the current tag
	 *							given the previous two tags, e.g,
	 *							p( tag | prevTag , prevPrevTag ).
	 */

	public Probability contextualProbability
	(
		String tag ,
		String previousTag ,
		String previousPreviousTag
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



