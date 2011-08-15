package edu.northwestern.at.morphadorner.tools.taggertrainer;

/*	Please see the license information at the end of this file. */

import java.util.*;

/**	Abstract Part of Speech Tagger rule.
 *
 *	<p>
 *	This defines an interface for rule-based part of speech tag
 *	replacment in a tagged corpus.  Each rule locates all the words
 *	in a corpus tagged with a specific original part of speech tag and
 *	satisfying a specific condition.  When the condition is satisfied,
 *	the rule replaces the original tag with a replacement tag.
 *	</p>
 */

public class AbstractTaggerRule implements TaggerRule
{
	/**	The original tag which this rule may change. */

	 protected String originalTag;

	/**	The replacement tag which this rule may emit. */

	protected String replacementTag;

	/**	Apply this rule to all training data.
	  *
	  *	@param	trainingData	Training data as a list.
	  *
	  *	@return					int count of sites to which rule was
	  *								successfully applied.
	  */

	public int apply( List trainingData )
	{
		return 0;
	}

	/**	Apply this rule to a specific training site.
	 *
	 *	@param	site				Index (0-based) of training site
	 *								at which to apply rule.
	 *	@param	trainingData	Training data as a list.
	 *
	 *	@return					true if rule was successfully applied.
	 */

	public boolean apply( int site , List trainingData )
	{
		return true;
	}

	/**	Apply this rule to a a list of training sites.
	 *
	 *	@param	sites			Indices (0-based) of training sites
	 *								at which to apply rule.
	 *	@param	trainingData	Training data as a list.
	 *
	 *	@return					count of sites to which rule was
	 *								successfully applied.
	 */

	public int apply( int[] sites , List trainingData )
	{
		int result	= 0;

		for ( int i = 0 ; i < sites.length ; i++ )
		{
			if ( apply( sites[ i ] , trainingData ) )
			{
				result++;
			}
		}

		return result;
	}

	/**	See if this rule applies to a training site.
	 *
	 *	@param	site				Index (0-based) of training site
	 *								at which to check that rule applies.
	 *	@param	trainingData	Training data.
	 *
	 *	@return					= -1:	rule changes correct tag to
	 *										incorrect.
	 *								= 0:	rule leaves correct tag alone,
	 *										or rule does not apply to
	 *										specified site.
	 *								= 1: 	rule changes incorrect
	 *										tag to correct.
	 */

	public int applies( int site , List trainingData )
	{
		return 0;
	}

	/**	Return original tag.
	 *
	 *	@return		The tag which this rule may replace.
	 */

	 public String getOriginalTag()
	 {
	 	return originalTag;
	 }

	/**	Return replacement tag.
	 *
	 *	@return		The tag with which this rule replace the
	 *					existing tag.
	 */

	 public String getReplacementTag()
	 {
	 	return replacementTag;
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



