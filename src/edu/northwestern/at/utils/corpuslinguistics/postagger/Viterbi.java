package edu.northwestern.at.utils.corpuslinguistics.postagger;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.logger.*;
import edu.northwestern.at.utils.math.*;

/**	Viterbi algorithm.
 *
 *	<p>
 *	The Viterbi trellis is a (# of words) x (# of part-of-speech-tags)
 *	matrix containing transition probability states from one
 *	word/tag to the next for each tag.  Because this is a very sparse
 *	matrix, we use a HashMap2D to hold the probability values.
 *	In addition, we use another HashMap2D to hold the back trace tags.
 *	This allows use to produce the optimal (most probable) path
 *	from the last tag in a sentence back to the first.
 *	</p>
 *
 *	<p>
 *	To improve performance, we define a beam width which allows us
 *	to eliminate trellis states which are highly unlikely to be
 *	part of the optimal tag path.  The default beam width s 1000
 *	which appears to work well in practice.
 *	</p>
 */

public class Viterbi
{
	/**	Viterbi probability trellis.
	 */

	protected Map2D<Integer, String, Probability> trellis;

	/**	Viterbi traceback for tags.
	 */

	protected Map2D<Integer, String, String> tracebackTags;

	/**	Beam width for beam search.
	 */

	protected double beamWidth			= Math.log( 1000.0D );

	/**	Count of tags rejected by beam search.
	 */

	protected int beamSearchRejections	= 0;

	/**	Logger used for output. */

	protected Logger logger;

	/**	Create Viterbi object.
	 */

	public Viterbi()
	{
								//	Set initial state.
		reset();
	}

	/**	Reset viterbi to clean state.
	 */

	public void reset()
	{
								//	Set initial state.  Use "." as
								//	the initial tag(s).

		String startState	= ".";

		trellis				= Map2DFactory.createNewMap2D();

		tracebackTags		= Map2DFactory.createNewMap2D();

		trellis.put(
			new Integer( -2 ) , startState , Probability.ONE_PROBABILITY );

		trellis.put(
			new Integer( -1 ) , startState , Probability.ONE_PROBABILITY );

		beamSearchRejections	= 0;

								//	Create dummy logger.

		logger	= new DummyLogger();
	}

	/**	Get probability value for a specified word index and tag.
	 *
	 *	@param	index	Word index.
	 *	@param	tag		Part of speech tag.
	 *
	 *	@return			Probability.  If (index,tag) not found, returns 0.
 	 */

 	public Probability getScore( int index , String tag )
 	{
 		Probability result	= Probability.ZERO_PROBABILITY;

 		Probability score	=
 			(Probability)trellis.get( new Integer( index ) , tag );

 		if ( score != null )
 		{
 			result	= score;
 		}

		return result;
 	}

	/**	Get traceback tag for a specified tag and word index.
	 *
	 *	@param	index	Word index.
	 *	@param	tag		Part of speech tag.
	 *
	 *	@return			Part of speech tag.  Returns "*" if (index,tag)
	 *					not found, which should never happen, unless the
	 *					part of speech guesser fails to return any
	 *					parts of speech for a word (which should also
	 *					never happen).
 	 */

 	public String getTracebackTag( int index , String tag )
 	{
		String result	=
			(String)tracebackTags.get( new Integer( index ) , tag );

		if ( result == null ) result = "*";

 		return result;
 	}

	/**	Store Viterbi score and traceback tag for a specified word index.
	 *
	 *	@param	index			Word index.
	 *	@param	tag				Part of speech tag.
	 *	@param	tracebackTag	Traceback tag to store.
	 *	@param	score			Score to store.
	 */

	public void setScore
	(
		int index ,
		String tag ,
		String tracebackTag ,
		Probability score
	)
	{
		trellis.put( new Integer( index ) , tag , score );
		tracebackTags.put( new Integer( index ) , tag , tracebackTag );
	}

	/**	Perform Viterbi scoring for bigram.
	 *
	 *	@param	wordIndex		Word index for current word.
	 *	@param	lexicalProbs	Array of lexical probabilities.
	 *							Entries match corresponding tags
	 *							in "tags" parameter.
	 *	@param	contextualProbs	HashMap2D mapping words and tags
	 *							to contextual probabilities.
	 *	@param	tags			Possible tags for current word.
	 *	@param	prevTags		Possible tags for previous word.
	 *
	 *	@return					Tags passing beam search criterion.
	 */

	public List<String> updateScore
	(
		int wordIndex ,
		Probability[] lexicalProbs ,
		Map2D contextualProbs ,
		List<String> tags ,
		List<String> prevTags
	)
	{
		Probability bestScore	= Probability.ZERO_PROBABILITY;

		for ( int i = 0 ; i < tags.size() ; i++ )
		{
			String tag	= tags.get( i );

			Probability lexicalProb	= lexicalProbs[ i ];

			for ( int j = 0 ; j < prevTags.size() ; j++ )
			{
				String prevTag	= prevTags.get( j );

				Probability scorem1			=
					getScore( wordIndex - 1 , prevTag );

				Probability contextualProb	=
					(Probability)contextualProbs.get( tag , prevTag );

				Probability score	=
					scorem1.multiply( lexicalProb , contextualProb );

				if ( score.compareTo( getScore( wordIndex , tag ) ) > 0 )
				{
					bestScore	= score;

					setScore( wordIndex , tag , prevTag , score );
				}
			}
		}
								//	Prune tags using beam width.

		return pruneTags( wordIndex , tags , bestScore );
	}

	/**	Perform Viterbi scoring for trigram.
	 *
	 *	@param	wordIndex		Word index for current word.
	 *	@param	lexicalProbs	Array of lexical probabilities.
	 *							Entries match corresponding tags
	 *							in "tags" parameter.
	 *	@param	contextualProbs	HashMap3D mapping words and tags
	 *							to contextual probabilities.
	 *	@param	tags			Possible tags for current word.
	 *	@param	prevTags		Possible tags for previous word.
	 *	@param	prevPrevTags	Possible tags for previous word of
	 *							previous word.
	 *
	 *	@return					Tags passing beam search criterion.
	 */

	public List<String> updateScore
	(
		int wordIndex ,
		Probability[] lexicalProbs ,
		Map3D contextualProbs ,
		List<String> tags ,
		List<String> prevTags ,
		List<String> prevPrevTags
	)
	{
		Probability bestScore	= Probability.ZERO_PROBABILITY;

		for ( int i = 0 ; i < tags.size() ; i++ )
		{
			String tag					= tags.get( i );

			Probability lexicalProb		= lexicalProbs[ i ];
			Probability currentScore	= getScore( wordIndex , tag );

			for ( int j = 0 ; j < prevTags.size() ; j++ )
			{
				String prevTag	= prevTags.get( j );

				Probability scorem1	=
					getScore( wordIndex - 1 , prevTag ).multiply(
						lexicalProb );

				for ( int k = 0 ; k < prevPrevTags.size() ; k++ )
				{
					Probability contextualProb	=
						(Probability)contextualProbs.get
						(
							tag ,
							prevTag ,
							prevPrevTags.get( k )
						);

					Probability score	=
						scorem1.multiply( contextualProb );

					if ( score.compareTo( currentScore ) > 0 )
					{
						bestScore		= score;
                        currentScore	= score;

						setScore
						(
							wordIndex , tag , prevTag , currentScore
						);
					}
				}
			}
		}
								//	Prune tags using beam width.

		return pruneTags( wordIndex , tags , bestScore );
	}

	/**	Prune tags using beam search.
	 *
	 *	@param	wordIndex	The word index.
	 *	@param	tags		The tags to prune.
	 *	@param	bestScore	The best score for this word and set of tags.
	 *
	 *	@return				The pruned list of tags.
	 *
	 *	<p>
	 *	Compares the ratio of the best score scross all the tags to each
	 *	individual tag's score.  Tags with a ratio larger than the beam
	 *	width are removed from further consideration in succeeding
	 *	states.
	 *	</p>
	 */

	protected List<String> pruneTags
	(
		int wordIndex ,
		List<String> tags ,
		Probability bestScore
	)
	{
								//	Holds list of tags passing
								//	beam search criterion.

		List<String> passedTags	= ListFactory.createNewList();

								//	Get log(best score).  We work in
								//	log space to avoid underflows.

		double dBestScore		= bestScore.getLogProbability();

								//	Loop over tags.

		for ( int i = 0 ; i < tags.size() ; i++ )
		{
			String tag				= tags.get( i );

								//	Get this tag's score.

			Probability tagScore	= getScore( wordIndex , tag );
			double dTagScore		= tagScore.getLogProbability();

								//	Subtract log(tag score) from
								//	log(best score), and compare to
								//	log(beam width).  If the log
								//	difference is greater than the
								//	log of the beam width, remove this
								//	tag from the trellis, and increment
								//	the count of tags rejected by the
								//	beam search.

			if ( ( dBestScore - dTagScore ) > beamWidth )
			{
				trellis.remove( new Integer( wordIndex ) , tag );

				beamSearchRejections++;
			}
								//	Tag not excluded by beam search
								//	criterion.  Add to list of tags
								//	to pass on to further steps.
			else
			{
				passedTags.add( tag );
			}
		}
								//	Return possibly pruned list of tags.
		return passedTags;
	}

	/**	Get optimal set of tags via backtracking.
	 *
	 *	@param	nWords	Number of words.
	 *	@param	tags	Final state tags.
	 *
	 *	@return			Optimal list of tags.
	 */

	public List<String> optimalTags( int nWords , List<String> tags )
	{
		List<String> tagList	= ListFactory.createNewList();

								//	Get optimal end state.
								//	Only iterate over tags we have seen
								//	since unseen tags cannot contribute
								//	to joint probability.

		int wordIndex			= nWords - 1;

		String bestTag			= ".";
		Probability bestScore	= Probability.ZERO_PROBABILITY;

		for ( int i = 0 ; i < tags.size() ; i++ )
		{
			String tag	= tags.get( i );

			if ( getScore( wordIndex , tag ).compareTo( bestScore ) > 0 )
			{
				bestScore	= getScore( wordIndex , tag );
				bestTag		= tag;
			}
		}
		                        //	Back track to get optimal tags.

		while ( wordIndex >= 0 )
		{
			tagList.add( 0 , bestTag );

			bestTag		= getTracebackTag( wordIndex-- , bestTag );
		}

		return tagList;
	}

	/**	Return number of entries rejected by beam search.
	 *
	 *	@return		Number of entries rejected by beam search.
	 */

	public int getBeamSearchRejections()
	{
		return beamSearchRejections;
	}

	/**	Get the beam width.
	 *
	 *	@return		The beam width.
	 */

	public double beamWidth()
	{
		return beamWidth;
	}

	/**	Set the beam width.
	 *
	 *	@param	beamWidth	The beam width.
	 */

	public void beamWidth( double beamWidth )
	{
		this.beamWidth	= beamWidth;
	}

	/**	Get the logger.
	 *
	 *	@return		The logger.
	 */

	public Logger getLogger()
	{
		return logger;
	}

	/**	Set the logger.
	 *
	 *	@param	logger		The logger.
	 */

	public void setLogger( Logger logger )
	{
		this.logger	= logger;
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



