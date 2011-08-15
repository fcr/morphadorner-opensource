package edu.northwestern.at.utils.corpuslinguistics.postagger.hepple;

/*	Please see the license information in the header below. */

import java.io.*;
import java.net.URL;
import java.util.*;
import java.text.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.adornedword.*;
import edu.northwestern.at.utils.corpuslinguistics.tokenizer.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.hepple.rules.*;

/**	HeppleTagger: Mark Hepple's Part of Speech Tagger.
 *
 *	<p>
 *	Copyright (c) 2001-2005, The University of Sheffield.
 *	</p>
 *
 *	<p>
 *	This file is part of GATE (see http://gate.ac.uk/), and is free
 *	software, licenced under the GNU Library General Public License,
 *	Version 2, June 1991 (in the distribution as file licence.html,
 *	and also available at http://gate.ac.uk/gate/licence.html).
 *	</p>
 *
 *	<p>
 *	HeppleTagger was originally written by Mark Hepple.  The GATE version
 *	contains modifications by Valentin Tablan and Niraj Aswani.
 *	</p>
 *
 *	<p>
 *	This version also contains many modifications made at
 *	Northwestern University for use in the WordHoard project.
 *	</p>
 *
 *	<p>
 *	Comments:
 *	</p>
 *
 *	<p>
 *	Implements a version of the decision list based tagging method
 *	described in:
 *	</p>
 *
 *	<p>
 *	M. Hepple. 2000. Independence and Commitment: Assumptions for Rapid
 *	Training and Execution of Rule-based Part-of-Speech Taggers.
 *	Proceedings of the 38th Annual Meeting of the Association for
 *	Computational Linguistics (ACL-2000). Hong Kong, October 2000.
 *	</p>
 *
 *	<p>
 *	Modified by Philip R. Burns at Northwestern University to remove
 *	dependencies upon the Penn Treebank tag set, to allow plugable
 *	handling of unknown words, to remove all input/output for
 *	tagged text and rules to calling classes, and to allow the
 *	Hepple tagger to be used as a retagger.
 *	</p>
 */

public class HeppleTagger extends AbstractPartOfSpeechTagger
	implements PartOfSpeechTagger, PartOfSpeechRetagger
{
	/**	Tagging rules.
	 *
	 *	<p>
	 *	The tagging rules are stored in a map.  The map keys
	 *	are parts of speech.  The value for each part of speech
	 *	key is a lists of rules which apply to that part of speech.
	 *	</p>
	 *
	 *	<p>
	 *	Tagging rules are specified using the syntax proposed by
	 *	Eric Brill in his dissertation.  Rules take the general form:
	 *	</p>
	 *
	 *	<blockquote>
	 *	<p>
	 *	<code>
	 *	fromtag totag condition param1 param2
	 *	</code>
	 *	</p>
	 *	</blockquote>
	 *
	 *	<p>
	 *	where "fromtag" is the current tag for a word,
	 *	"totag" is the new tag to replace the current tag if the
	 *	"condition" is met, and "param1" and "param2" are optional
	 *	values for the condition test.  Each rule must specify at
	 *	least the fromtag. totag, and condition.  The fromtag
	 *	values are the keys for the rules map.
	 *	</p>
	 */

	protected Map<String, List<Rule>> rules	= MapFactory.createNewMap();

	/**	Marks unused positions in sliding word buffer. */

	protected static final String staart				= "STAART";

	protected static final String[] staartLex			=
		new String[]{ staart };

	protected static final AdornedWord staartWordAndTag	=
		new BaseAdornedWord( staart , staart );

	/**	Sliding word buffer. */

	public String[] wordBuff =
		{ staart, staart, staart, staart, staart, staart, staart };

	/**	Sliding tag buffer. */

	public String[] tagBuff	=
		{ staart, staart, staart, staart, staart, staart, staart };

	/**	Sliding parts of speech buffer. */

	public String[][] lexBuff	=
		{	staartLex, staartLex, staartLex, staartLex, staartLex,
			staartLex, staartLex };

	/**	Debug flag. */

	protected boolean debug	= false;

	/**	Construct a Hepple POS tagger.
	 */

	public HeppleTagger()
	{
	}

	/**	See if tagger uses context rules.
	 *
	 *	@return		True since Hepple tagger uses context rules.
	 */

	public boolean usesContextRules()
	{
		return true;
	}

	/**	Set context rules for tagging.
	 *
	 *	@param	contextRules	String array of context rules.
	 *
	 *	@throws	InvalidRuleException if a rule is bad.
	 */

	public void setContextRules( String[] contextRules )
		throws InvalidRuleException
	{
		this.contextRules	= contextRules;
		this.rules.clear();

		String line;
		Rule newRule;
								//	Loop over each context rule.

		for ( int i = 0 ; i < this.contextRules.length ; i++ )
		{
			line					= contextRules[ i ];

								//	Tokenize rule into rule parts.

			List<String> ruleParts	= ListFactory.createNewList();
			StringTokenizer tokens	= new StringTokenizer( line );

			while ( tokens.hasMoreTokens() )
			{
				ruleParts.add( tokens.nextToken() );
			}
								//	There must be at least three
								//	rule parts in a rule: a "from"
								//	tag, a "to" tag, and an operation name.
								//	Throw error if there aren't at least
								//	three.

			if ( ruleParts.size() < 3 )
			{
				throw new InvalidRuleException( line );
			}
                                //	Create new rule from rule parts.

			newRule	= createNewRule( (String)ruleParts.get( 2 ) );

			newRule.initialise( ruleParts );

                				//	Get list of existing rules for
                				//	the "from" tag.  If none,
                				//	create a new list to hold the
                				//	rules for this tag.

			List<Rule> existingRules	= rules.get( newRule.from );

			if ( existingRules == null )
			{
				existingRules	= ListFactory.createNewList();
				rules.put( newRule.from , existingRules );
			}
								//	Add the new rule to the list of
								//	rules for the "from" tag.

			existingRules.add( newRule );
		}
	}

	/**	Creates a new rule of the required type according to the provided ID.
	 *
	 *	@param	ruleId		The ID for the rule to be created
	 */

	protected Rule createNewRule( String ruleId )
		throws InvalidRuleException
	{
		String baseClassName	=
			ClassUtils.packageName( HeppleTagger.class.getName() ) +
			".rules.Rule_";

		try
		{
			String ruleClassName	= baseClassName + ruleId;

			Class ruleClass			= Class.forName( ruleClassName );

			return (Rule)ruleClass.newInstance();
		}
		catch ( Exception e )
		{
			throw new InvalidRuleException(
				"Could not create rule " + ruleId + "!\n" + e.toString() );
		}
	}

	/**	Tag an adorned word list.
	 *
	 *	@param	sentence	The sentence as an {@link edu.northwestern.at.utils.corpuslinguistics.adornedword.AdornedWord}.
	 *
	 *	@return				An {@link edu.northwestern.at.utils.corpuslinguistics.adornedword.AdornedWord}
	 *							of the words in the sentence tagged with
	 *							parts of speech.
	 *
	 *	<p>
	 *	The input sentence is a {@link edu.northwestern.at.utils.corpuslinguistics.adornedword.AdornedWord}
	 *	of words to be tagged.  The output is the same list of words with
	 *	parts of speech added.
	 *	</p>
	 */

	public<T extends AdornedWord> List<T> tagAdornedWordList
	(
		List<T> sentence
	)
	{
								//	Loop over words in sentence.

		boolean isFirstWord	= true;

		for ( int i = 0 ; i < sentence.size() ; i++ )
		{
								//	Get next word.

			AdornedWord newWord	= sentence.get( i );

								//	Tag it,

			oneStep( newWord , isFirstWord , sentence );

			isFirstWord	= false;
		}
								//	Add six more "staarts"
								//	to flush all words out of the
								//	tagging buffer.

		for ( int i = 0 ; i < 6 ; i++ )
		{
			oneStep( new BaseAdornedWord( staart ) , isFirstWord , sentence );
			isFirstWord	= false;
		}
								//	We have a new finished sentence.

		return sentence;
	}

	/**	Adds a new word to the current tagging window.
	 *
	 *	@param	word				The new word to add.
	 *	@param	isFirstWord			True if word is first in sentence.
	 *	@param	taggedSentence		A List of adorned words
	 *									representing the results of tagging
	 *									the current sentence so far.
	 *
	 *	@return						true if a full sentence is now tagged,
	 *								false otherwise.
	 *
	 *	<p>
	 *	Adds a new word to the current window of 7 words (on
	 *	the last position) and tags the word currently in the
	 *	middle (i.e. on position 3). This function also reads the
	 *	word on the first position and adds its tag to the
	 *	taggedSentence structure as this word would be lost at the
	 *	next advance. If this word completes a sentence then it
	 *	returns true otherwise it returns false.
	 *	</p>
	 */

	@SuppressWarnings("unchecked")
	protected boolean oneStep
	(
		AdornedWord word ,
		boolean isFirstWord ,
//		List<AdornedWord> taggedSentence
		List taggedSentence
	)
	{
								//	Add the new word at the end of the
								//	text window.

		for ( int i = 1 ; i < 7 ; i++ )
		{
			wordBuff[ i - 1 ]	= wordBuff[ i ];
			tagBuff[ i - 1 ]	= tagBuff[ i ];
			lexBuff[ i - 1 ]	= lexBuff[ i ];
		}

		wordBuff[ 6 ]	= word.getSpelling();

								//	This tagger assumes the first
								//	part of speech tag is the most
								//	frequently occurring, so
								//	getPartsOfSpeech returns the
								//	most frequent tag first.

		lexBuff[ 6 ]	= getPartsOfSpeech( word.getSpelling() , false );
		tagBuff[ 6 ]	= lexBuff[ 6 ][ 0 ];

								//	Apply the rules to the word in the
								//	middle of the text window.
								//	Try to fire a rule for the current
								//	lexical entry. It may be the case that
								//	no rule applies.
		if ( debug )
		{
			System.out.println(
				"===> word=" + wordBuff[ 3 ] +
				" currently tagged " + tagBuff[ 3 ] );
		}

		boolean done	= false;

		while ( !done )
		{
			String currentTag	= tagBuff[ 3 ];

			List rulesToApply	= (List)rules.get( lexBuff[ 3 ][ 0 ] );

			if ( ( rulesToApply != null ) && ( rulesToApply.size() > 0 ) )
			{
				Iterator rulesIter = rulesToApply.iterator();

								//	Find the first rule that applies,
								//	fire it, and stop.

				while (	( rulesIter.hasNext() &&
						!((Rule)rulesIter.next()).apply( this ) ) )
				{
				}
			}

//			done	= currentTag.equals( tagBuff[ 3 ] );
			done	= true;
		}
								//	Save the tagged word from the
								//	first position.

		String taggedWord	= wordBuff[ 0 ];

		if ( taggedWord != staart )
		{
			AdornedWord newWord	=
				new BaseAdornedWord( taggedWord , tagBuff[ 0 ] );

			taggedSentence.add( newWord );

			if ( wordBuff[ 1 ] == staart )
			{
								//	wordTag[ 0 ] was the end of a sentence.
				return true;
			}
		}

		return false;
	}

	/**	Retag one sentence.
	 *
	 *	@param	sentence	List of adorned words to retag.
	 *
	 *	@return				List of retagged words.
	 */

	@SuppressWarnings("unchecked")
	public<T extends AdornedWord> List<T> retagSentence
	(
		List<T> sentence
	)
	{
								//	List of (word, tag) results.

		List<T> taggedSentence	= ListFactory.createNewList();

								//	Iterate over words in sentence.

		Iterator<T> taggedWordsIter	= sentence.iterator();
		boolean isFirstWord			= true;

		while ( taggedWordsIter.hasNext() )
		{
								//	Get next word.

			T nextWord	= taggedWordsIter.next();

								//	Tag it,

			oneRetagStep( nextWord , isFirstWord , taggedSentence );

			isFirstWord	= false;
		}
								//	Add six more "staarts"
								//	to flush all words out of the
								//	tagging buffer.

		for ( int i = 0 ; i < 6 ; i++ )
		{
			oneRetagStep( (T)staartWordAndTag , isFirstWord , taggedSentence );
			isFirstWord	= false;
		}
								//	We have a new finished sentence.

		return taggedSentence;
	}

	/**	Adds a new word to the current retagging window.
	 *
	 *	@param	adornedWord		The new word and its tag.
	 *	@param	isFirstWord		True if word is first in sentence.
	 *	@param	taggedSentence	A List of adorned words
	 *							representing the results of tagging
	 *							the current sentence so far.
	 *
	 *	@return					true if a full sentence is now tagged,
	 *							false otherwise.
	 *
	 *	<p>
	 *	Adds a new word to the current window of 7 words (on
	 *	the last position) and tags the word currently in the
	 *	middle (i.e. on position 3). This function also reads the
	 *	word on the first position and adds its tag to the
	 *	taggedSentence structure as this word would be lost at the
	 *	next advance. If this word completes a sentence then it
	 *	returns true otherwise it returns false.
	 *	</p>
	 */

	@SuppressWarnings("unchecked")
	protected<T extends AdornedWord> boolean oneRetagStep
	(
		T adornedWord ,
		boolean isFirstWord ,
		List<T> taggedSentence
	)
	{
								//	Add the new word at the end of the
								//	text window.

		for ( int i = 1 ; i < 7 ; i++ )
		{
			wordBuff[ i - 1 ]	= wordBuff[ i ];
			tagBuff[ i - 1 ]	= tagBuff[ i ];
			lexBuff[ i - 1 ]	= lexBuff[ i ];
		}
								//	Get the word.

		wordBuff[ 6 ]	= adornedWord.getSpelling();

								//	Get the possible tags.

		lexBuff[ 6 ]	=
			getPartsOfSpeech( adornedWord.getSpelling() , false );

		tagBuff[ 6 ]	= adornedWord.getPartsOfSpeech();

								//	Apply the rules to the word in the
								//	middle of the text window.
								//	Try to fire a rule for the current
								//	lexical entry. It may be the case that
								//	no rule applies.

		List rulesToApply	= (List)rules.get( lexBuff[ 3 ][ 0 ] );

		if ( ( rulesToApply != null ) && ( rulesToApply.size() > 0 ) )
		{
			Iterator rulesIter = rulesToApply.iterator();

								//	Find the first rule that applies,
								//	fire it, and stop.

			while (	( rulesIter.hasNext() &&
					!((Rule)rulesIter.next()).apply( this ) ) )
			{
			}
		}
								//	Save the tagged word from the
								//	first position.

		String taggedWord	= wordBuff[ 0 ];

		if ( taggedWord != staart )
		{
			AdornedWord aWord	=
				new BaseAdornedWord( taggedWord , tagBuff[ 0 ] );

			taggedSentence.add( (T)aWord );

			if ( wordBuff[ 1 ] == staart )
			{
								//	wordTag[ 0 ] was the end of a sentence.
				return true;
			}
		}

		return false;
	}

	/** Get parts of speech for a word.
	 *
	 *	@param	word			The word to be classified.
	 *	@param	isFirstWord		True if word is first word in sentence.
	 *
	 *	@return			String array of potential parts of speech.
	 *
	 *	<p>
	 *	The lexicon must always return one or more parts of speech.
	 *	In addition, for this tagger, the most frequently occurring
	 *	tag must be the first one in the returned string array.
	 *	</p>
	 */

	protected String[] getPartsOfSpeech( String word , boolean isFirstWord )
	{
		String[] result	= new String[ 0 ];

		if ( word == staart ) return staartLex;

								//	Get all the categories for the word.

		List<String> categories	= getTagsForWord( word );

								//	Lexicon should never return null
								//	set of categories, but check anyway.

		if ( categories != null )
		{
								//	Get the categories.  Make sure the
								//	largest category is first.

			String largestCategory	= getMostCommonTag( word );

								//	Get the categories.

			result		= new String[ categories.size() ];

								//	Put largest (or only) category first.

			result[ 0 ]	= largestCategory;

								//	Copy remaining categories, if any.

			if ( categories.size() > 1 )
			{
				int k				= 1;
				Iterator<String> iterator	= categories.iterator();

				while ( iterator.hasNext() )
				{
					String category	= iterator.next();

					if ( !category.equals( largestCategory ) )
					{
						result[ k++ ]	= category;
					}
				}
			}
		}

		return result;
	}

	/**	Can retagger add or delete words in the original sentence?
	 *
	 *	@return		true if retagger can add or delete words.
	 */

	public boolean canAddOrDeleteWords()
	{
		return false;
	}

	/**	Return tagger description.
	 *
	 *	@return		Tagger description.
	 */

	public String toString()
	{
		return "Hepple tagger";
	}
}

