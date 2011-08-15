package edu.northwestern.at.utils.corpuslinguistics.postagger.smoothing.contextual;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.utils.corpuslinguistics.lexicon.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.transitionmatrix.*;
import edu.northwestern.at.utils.math.*;

/**	DeletedInterpolation contextual smoother.
 *
 *	<p>
 *	A contextual smoother which a weighted combination of
 *	unigram and bigram probabilities (for a bigram tagger) or
 *	unigram bigram, and trigram probabilities (for a trigram
 *	tagger).
 *	</p>
 */

public class DeletedInterpolationContextualSmoother
	extends AbstractContextualSmoother
	implements ContextualSmoother
{
	/**	Bigram weights for lexical smoothing.
	 */

	protected double[] bigramWeights	= null;

	/**	Trigram weights for lexical smoothing.
	 */

	protected double[] trigramWeights	= null;

	/**	Create a deleted interpolation contextual smoother.
	 */

	public DeletedInterpolationContextualSmoother()
	{
	}

	/**	Compute contextual probability of a tag given the previous tag.
	 *
	 *	@param	tag				The current tag.
	 *	@param	previousTag		The previous tag.
	 *
	 *	@return					The probability of the current tag given
	 *							the previous tag, e.g,
	 *							p( tag | previousTag ).
	 *
	 *	<p>
	 *	We compute the contextual probability p( tag | previousTag )
	 *	using weights determined by deleted interpolation.  The
	 *	smoothed contextual probability is given by:
	 *	</p>
	 *
	 *	<blockquote>
	 *	<p>
	 *	p*( tag | previousTag )	=
	 *	l1 * p( tag ) + l2 * p( previousTag , tag )
	 *	</p>
	 *	</blockquote>
	 *
	 *	<p>
	 *	where l1 and l2 are the unigram and bigram weights respectively.
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

		if ( cachedContextualProbabilities != null )
		{
			result	=
				cachedContextualProbabilities.get( previousTag , tag , "*" );
		}
								//	If the probability isn't in the
								//	cache, compute it.
		if ( result == null )
		{
								//	Get bigram weights if we don't
								//	already have them.

			TransitionMatrix transitionMatrix	=
				partOfSpeechTagger.getTransitionMatrix();

			if ( bigramWeights == null )
			{
				bigramWeights	= transitionMatrix.getBigramWeights();
			}

			double prob	=
				transitionMatrix.getProbability( previousTag , tag ) *
				bigramWeights[ 1 ] +
				transitionMatrix.getProbability( tag ) *
				bigramWeights[ 0 ];

								//	Store computed probability in
								//	the cache.

			result	= new Probability( prob );

			if ( cachedContextualProbabilities != null )
			{
				cachedContextualProbabilities.put(
					previousTag , tag , "*" , result );
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
	 *	We compute the contextual probability
	 *	p( tag | previousPreviousTag , previousTag )
	 *	using weights determined by deleted interpolation.  The
	 *	smoothed contextual probability is given by:
	 *	</p>
	 *
	 *	<blockquote>
	 *	<p>
	 *	p*( tag | previousPreviousTag , previousTag )	=
	 *	l1 * p( tag ) + l2 * p( previousTag , tag ) +
	 *	l3 * o( previousPreviousTag , previousTag , tag )
	 *	</p>
	 *	</blockquote>
	 *
	 *	<p>
	 *	where l1, l2, and l3 are the unigram, bigram, and trigram weights
	 *	respectively.
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
								//	Get trigram weights if
								//	we don't already have them.

			TransitionMatrix transitionMatrix	=
				partOfSpeechTagger.getTransitionMatrix();

			if ( trigramWeights == null )
			{
				trigramWeights	= transitionMatrix.getTrigramWeights();
			}

			double prob	=
				transitionMatrix.getProbability(
					previousPreviousTag , previousTag , tag ) *
					trigramWeights[ 2 ] +
				transitionMatrix.getProbability( previousTag , tag ) *
					trigramWeights[ 1 ] +
				transitionMatrix.getProbability( tag ) *
					trigramWeights[ 0 ];

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
		StringBuffer sb	= new StringBuffer();

		sb.append
		(
			"Using weights computed by deleted interpolation " +
			"for contextual smoothing."
		);

		sb.append( "\n" );

		sb.append
		(
			"Bigram weights: lambda1=" + bigramWeights[ 0 ] +
			", lambda2=" + bigramWeights[ 1 ]
        );

		sb.append( "\n" );

		sb.append
		(
			"Trigram weights: lambda1=" + trigramWeights[ 0 ] +
			", lambda2=" + trigramWeights[ 1 ]  +
			", lambda3=" + trigramWeights[ 2 ]
		);

		sb.append( "\n" );

		return sb.toString();
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



