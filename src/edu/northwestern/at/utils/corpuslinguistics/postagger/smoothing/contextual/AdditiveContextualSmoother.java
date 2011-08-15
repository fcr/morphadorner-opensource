package edu.northwestern.at.utils.corpuslinguistics.postagger.smoothing.contextual;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.utils.corpuslinguistics.lexicon.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.transitionmatrix.*;
import edu.northwestern.at.utils.math.*;

/**	Additive contextual smoother.
 *
 *	<p>
 *	A contextual smoother which adds a small positive value to each
 *	(word, tag) count to avoid zero probabilities.  When the value
 *	added is 1, this is Laplace smoothing.  When the value added is
 *	0.5, this is Lidstone smoothing.
 *	</p>
 *
 *	<p>
 *	This implementation uses 0.001 as the additive adjustment value.
 *	This seems to work well when there is lots of training data.
 *	</p>
 */

public class AdditiveContextualSmoother
	extends AbstractContextualSmoother
	implements ContextualSmoother
{
	/**	Additive adjustment value.   We use 0.001 by default.
	 */

	protected double additiveAdjustmentValue	= 0.001D;

	/**	Create an additive contextual smoother.
	 */

	public AdditiveContextualSmoother()
	{
	}

	/**	Set the part of speech tagger for this smoother.
	 *
	 *	@param	partOfSpeechTagger	Part of speech tagger for which
	 *								this smoother provides probabilities.
	 */

	public void setPartOfSpeechTagger
	(
		PartOfSpeechTagger partOfSpeechTagger
	)
	{
		super.setPartOfSpeechTagger( partOfSpeechTagger );

	}

	/**	Get additive adjustment value.
	 *
	 *	@return		Additive adjustment value.
	 */

	public double getAdditiveAdjustmentValue()
	{
		return additiveAdjustmentValue;
	}

	/**	Set additive adjustment value.
	 *
	 *	@param	additiveAdjustmentValue		Additive adjustment value.
	 */

	public void setAdditiveAdjustmentValue( double additiveAdjustmentValue )
	{
		this.additiveAdjustmentValue	= additiveAdjustmentValue;
	}

	/**	Compute contextual probability of a tag given the previous tag.
	 *
	 *	@param	previousTag		The previous tag.
	 *	@param	tag				The current tag.
	 *
	 *	@return					The probability of the current tag given
	 *							the previous tag, e.g,
	 *							p( tag | previousTag ).
	 *
	 *	<p>
	 *	We compute the contextual probability p( tag | previousTag )
	 *	using additive smoothing.
	 *	</p>
	 */

	public Probability contextualProbability
	(
		String tag ,
		String previousTag
	)
	{
								//	See if the contextual probability
								//	for the tag sequence
								//	(previousTag, tag) is in the cache.

		Probability result	=	null;

		if( cachedContextualProbabilities != null )
		{
			result	=
				cachedContextualProbabilities.get( previousTag , tag , "*" );
		}
								//	If the probability isn't in the
								//	cache, compute it.
		if ( result == null )
		{
			TransitionMatrix transitionMatrix	=
				partOfSpeechTagger.getTransitionMatrix();

			Lexicon lexicon	= partOfSpeechTagger.getLexicon();

			double additiveDenomFactor	=
				additiveAdjustmentValue * lexicon.getLexiconSize();

			double prob	=
				( transitionMatrix.getCount( previousTag , tag ) + 0.05D ) /
					( transitionMatrix.getCount( previousTag ) +
						additiveDenomFactor );

								//	Store computed probability in
								//	the cache.

			result	= new Probability( prob );

			if ( cachedContextualProbabilities != null )
			{
				cachedContextualProbabilities.put
				(
					previousTag , tag , "*" , result
				);
			}
        }

		return result;
	}

	/**	Compute contextual probability of a tag given the previous tags.
	 *
	 *	@param	tag						The current tag.
	 *	@param	previousTag				The previous tag.
	 *	@param	previousPreviousTag		The previous tag of the previous tag.
	 *
	 *	@return					The probability of the current tag
	 *							given the previous two tags, e.g,
	 *							p( tag | prevTag , prevPrevTag ).
	 *	<p>
	 *	We compute the contextual probability p( tag | previousTag )
	 *	using additive smoothing.
	 *	</p>
	 */

	public Probability contextualProbability
	(
		String tag ,
		String previousTag ,
		String previousPreviousTag
	)
	{
								//	See if the contextual probability
								//	for the tag sequence
								//	(previousPreviousTag, previousTag , tag)
								//	is in the cache.

		Probability result	=	null;

		if ( cachedContextualProbabilities != null )
		{
			result	=
				cachedContextualProbabilities.get(
					previousPreviousTag , previousTag , tag );
		}
								//	If the probability isn't in the
								//	cache, compute it.
		if ( result == null )
		{
			TransitionMatrix transitionMatrix	=
				partOfSpeechTagger.getTransitionMatrix();

			Lexicon lexicon	= partOfSpeechTagger.getLexicon();

			double additiveDenomFactor	=
				additiveAdjustmentValue * lexicon.getLexiconSize();

			double prob	=
				( transitionMatrix.getCount(
					previousPreviousTag , previousTag, tag ) +
						additiveAdjustmentValue ) /
					( transitionMatrix.getCount(
						previousPreviousTag, previousTag ) +
					additiveDenomFactor );

								//	Store computed probability in
								//	the cache.

			result	= new Probability( prob );

			if ( cachedContextualProbabilities != null )
			{
				cachedContextualProbabilities.put(
					previousPreviousTag , previousTag , tag , result );
			}
		}

		return result;
	}

	/**	Description of this smoother for display.
	 *
	 *	@return		Description of this smoother.
	 */

	public String toString()
	{
		return
			"Using additive contextual smoothing with additive value=" +
			additiveAdjustmentValue + ".";
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



