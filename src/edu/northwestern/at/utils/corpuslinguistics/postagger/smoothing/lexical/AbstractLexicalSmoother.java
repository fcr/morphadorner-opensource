package edu.northwestern.at.utils.corpuslinguistics.postagger.smoothing.lexical;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.logger.*;
import edu.northwestern.at.utils.corpuslinguistics.lexicon.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.*;
import edu.northwestern.at.utils.math.*;

/**	Abstract lexical smoother.
 *
 *	<p>
 *	An abstract lexical smoother which provides implementations of
 *	common service methods such as setting the lexicon and the
 *	transition proability matrix.  Extend this class and override
 *	the abstract method "lexicalProbability" to produce a new
 *	lexical smoother.
 *	</p>
 */

abstract public class AbstractLexicalSmoother
	extends IsCloseableObject
	implements LexicalSmoother, UsesLogger
{
	/**	The part of speech tagger for which this smoother provides
	 *	amoother contextual probabilities.
	 */

	protected PartOfSpeechTagger partOfSpeechTagger;

	/**	Cached lexical probabilities for words.
	 */

	protected Map2D<String, String, Probability> cachedLexicalProbabilities;

	/**	Logger used for output. */

	protected Logger logger;

	/**	Create an abstract lexical smoother.
	 */

	public AbstractLexicalSmoother()
	{
								//	Create cache for lexical
								//	probabilities.

		cachedLexicalProbabilities	=
			Map2DFactory.createNewMap2D( 3000 );

								//	Create dummy logger.

		logger						= new DummyLogger();
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
		this.partOfSpeechTagger	= partOfSpeechTagger;
	}

	/**	Get the number of cached lexical probabilities.
	 *
	 *	@return		The number of cached lexical probabilities.
	 */

	public int cachedProbabilitiesCount()
	{
		int result	= 0;

		if ( cachedLexicalProbabilities != null )
		{
			result	= cachedLexicalProbabilities.size();
		}

		return result;
	}

	/**	Clear cached probabilities..
	 */

	public void clearCachedProbabilities()
	{
		cachedLexicalProbabilities.clear();
	}

	/**	Get lexically smoothed probability of a word given a tag.
	 *
	 *	@param	word	The word.
	 *	@param	tag		The part of speech tag.
	 *
	 *	@return			Lexically smoothed probability of word given tag,
	 *					e.g., p( word | tag ).
	 *
 	 *	<p>
 	 *	To avoid redoing potentially expensive probability calculations,
 	 *	you can use the "cachedLexicalProbabilities" HashMap2D to store
 	 *	probabilities once they are calculated.  Your lexicalProbability
 	 *	method should look to see if the cache contains the needed
 	 *	lexical probability.  If so, just retrieve it without recomputing it.
 	 *	If the cache does not contain the probability, compute it, and
	 *	store it in the cache for future use.
 	 *	</p>
 	 *
 	 *	<p>
 	 *	Here is what a lexicalProbability method should look like, using
 	 *	the cache.
	 *	<p>
	 *
	 *	<p>
	 *	<pre>
	 *	protected Probability lexicalProbability( String word , String tag )
	 *	{
	 *								//	See if the lexical probability
	 *								//	p( word | tag ) is in the cache.
	 *
	 *		Probability result	=
	 *			(Probability)cachedLexicalProbabilities.get( word , tag );
     *
	 *								//	If the probability isn't in the
	 *								//	cache, compute it.
	 *
	 *		if ( result == null )
	 *		{
	 *			double prob		= <em>compute smoothed probability value</em>
	 *
	 *								//	Store computed probability in
	 *								//	the cache.
	 *
	 *			result	= new Probability( prob );
	 *
	 *			cachedLexicalProbabilities.put( word , tag , result );
	 *		}
	 *
	 *		return result;
	 *	}
	 *	</pre>
	 *	</p>
	 */

	 public abstract Probability lexicalProbability
	 (
	 	String word ,
	 	String tag
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



