package edu.northwestern.at.utils.corpuslinguistics.postagger.smoothing.lexical;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.utils.corpuslinguistics.lexicon.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.*;
import edu.northwestern.at.utils.math.*;

/**	Additive lexical smoother.
 *
 *	<p>
 *	A lexical smoother which adds a small positive value to each
 *	(word, tag) count to avoid zero probabilities.  When the value
 *	added is 1, this is Laplace smoothing.  When the value added is
 *	0.5, this is Lidstone smoothing.
 *	</p>
 *
 *	<p>
 *	This implementation uses 0.5 as the adjustment value (Lidstone
 *	smoothing).
 *	</p>
 */

public class AdditiveLexicalSmoother
	extends AbstractLexicalSmoother
	implements LexicalSmoother
{
	/**	True if debugging enabled. */

	protected boolean debug	= true;

	/**	Additive adjustment value	= 0.5 (Lidstone smoothing).
	 */

	protected double additiveAdjustmentValue	= 0.5D;

	/**	Create an additive lexical smoother.
	 */

	public AdditiveLexicalSmoother()
	{
		super();
	}

	/**	Get lexically smoothed probability of a word given a tag.
	 *
	 *	@param	word	The word.
	 *	@param	tag		The part of speech tag.
	 *
	 *	@return			Lexically smoothed probability of word given tag,
	 *					e.g., p( word | tag ).
	 */

	public Probability lexicalProbability( String word , String tag )
	{
								//	See if the lexical probability
								//	p( word | tag ) is in the cache.

		Probability result	=	null;

		if ( cachedLexicalProbabilities != null )
		{
			result	= cachedLexicalProbabilities.get( word , tag );
		}
								//	If the probability isn't in the
								//	cache, compute it.

		if ( result == null )
		{
								//	Total number of times this
								//	tag appeared in the training data.

			Lexicon lexicon	= partOfSpeechTagger.getLexicon( word );

			int tagCount	=
				Math.max( lexicon.getCategoryCount( tag ) , 1 );

								//	Total number of times this word
								//	appeared with this tag in the
								//	training data.

			int catCount	=
				partOfSpeechTagger.getTagCount( word , tag );

								//	Set of possible part of speech
								//	tags for this word.

			List<String> tags	=
				partOfSpeechTagger.getTagsForWord( word );

								//	Calculate smoothed probability
								//	using additive smoothing.

			double prob		=
				( catCount + additiveAdjustmentValue ) /
					( (double)tagCount +
						( additiveAdjustmentValue  * tags.size() ) );

								//	Store computed probability in
								//	the cache.
			try
			{
				result	= new Probability( prob );
			}
			catch ( Exception e )
			{
				if ( debug )
				{
					logger.logError
					(
						"word=" + word + ", tag=" + tag +
						", catCount=" + catCount +
						", adj=" + additiveAdjustmentValue +
						", tagCount=" + tagCount +
							", tags.size=" + tags.size() +
						", prob=" + prob
					);
				}

				result	= new Probability( 1.0 );
			}

			if ( cachedLexicalProbabilities != null )
			{
				cachedLexicalProbabilities.put( word , tag , result );
			}
		}

		return result;
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



