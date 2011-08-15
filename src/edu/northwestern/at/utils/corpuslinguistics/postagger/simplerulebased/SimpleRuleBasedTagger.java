package edu.northwestern.at.utils.corpuslinguistics.postagger.simplerulebased;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.adornedword.*;
import edu.northwestern.at.utils.corpuslinguistics.partsofspeech.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.unigram.*;
import edu.northwestern.at.utils.corpuslinguistics.tokenizer.*;

/**	Simple Rule-Based Part of Speech tagger.
 *
 *	<p>
 *	The simple rule-based part of speech tagger assigns the most
 *	commonly occurring part of speech to all words and then
 *	applies a small set of contextual rules to "fix up" the tagging.
 *	It's kind of a "Brill light."
 *	</p>
 *
 *	<p>
 *	This simple tagger is useful when very fast tagging without
 *	high accuracy is useful, e.g., in sentence splitting.
 *	</p>
 */

public class SimpleRuleBasedTagger
	extends UnigramTagger
	implements PartOfSpeechTagger, PartOfSpeechRetagger
{
	/**	Create a simple rule-based tagger.
	 */

	public SimpleRuleBasedTagger()
	{
	}

	/**	Tag a sentence.
	 *
	 *	@param	sentence	The sentence as a list of string words.
	 *
	 *	@return				An {@link edu.northwestern.at.utils.corpuslinguistics.adornedword.AdornedWord}
	 *						of the words in the sentence tagged with
	 *						parts of speech.
	 *
	 *	<p>
	 *	The input sentence is a {@link java.util.List} of
	 *	string words to be tagged.  The output is
	 *	{@link edu.northwestern.at.utils.corpuslinguistics.adornedword.AdornedWord}
	 *	of the words with parts of speech added.
	 *	</p>
	 */

	public List<AdornedWord> tagSentence( List<String> sentence )
	{
								//	Get unigram tagger results.
								//	This assigns the most common tag
								//	to each word.

		List<AdornedWord> taggedSentence	= super.tagSentence( sentence );

								//	Apply the fixup rules.

		return retagWords( taggedSentence );
	}

	/**	Retag words in a tagged sentence.
	 *
	 *	@param	taggedSentence	The tagged sentence.
	 *
	 *	@return					The retagged sentence.
	 *
	 *	<p>
	 *	This method applies the short list of fixup rules.
	 *	The resultant tagging is crude but good enough
	 *	for tasks like sentence boundary detection.
	 *	</p>
	 */

	public<T extends AdornedWord> List<T> retagWords
	(
		List<T> taggedSentence
	)
	{
								//	Get part of speech tags used
								//	for tagging this sentence.

		PartOfSpeechTags posTags	= lexicon.getPartOfSpeechTags();

								//	Previous word in sentence.

		T previousWord		= null;

								//	Loop over words in sentence.

		for ( int i = 0 ; i < taggedSentence.size() ; i++ )
		{
								//	Get current word in sentence.

			T word	= taggedSentence.get( i );

								//	Get spelling and parts of speech
								//	for word.

			String spelling	= word.getSpelling();
			String posTag	= word.getPartsOfSpeech();

								//	Rule 1:
								//  determiner followed by
								//	verb -> determiner followed
								//	by noun.

            if ( previousWord != null )
            {
            	if	(	posTags.isDeterminerTag(
            				previousWord.getPartsOfSpeech() ) &&
            			posTags.isVerbTag( posTag )
            		)
        	    {
					word.setPartsOfSpeech( posTags.getSingularNounTag() );
				}
			}
				            	//	Rule 2: convert a noun to a
				            	//	past participle if word ends with "ed"

			if	(	(	posTags.isNounTag( posTag ) &&
						!posTags.isProperNounTag( posTag )
					) && spelling.endsWith( "ed" )
				)
			{
				word.setPartsOfSpeech( posTags.getPastParticipleTag() );
			}

            					//	Rule 3: convert any type to adverb
            					//	if it ends in "ly".

			if ( spelling.endsWith( "ly" ) )
			{
				word.setPartsOfSpeech( posTags.getAdverbTag() );
			}
								//	Rule 4: convert a common noun to an
								//	adjective if it ends with "al".

			if	(	(	posTags.isNounTag( posTag ) &&
						!posTags.isProperNounTag( posTag )
					) && spelling.endsWith( "al" )
				)
			{
				word.setPartsOfSpeech( posTags.getAdjectiveTag() );
            }
								//	Rule 5: convert a noun to a verb
								//	if the preceeding word is "would".

			if ( previousWord != null )
			{
				if	(	(	posTags.isNounTag( posTag ) &&
							!posTags.isProperNounTag( posTag )
						) &&
						previousWord.getSpelling().equalsIgnoreCase( "would" )
					)
				{
					word.setPartsOfSpeech( posTags.getVerbTag() );
        	    }
            }
								//	Rule 6: if a word has been categorized
								//	as a common noun and it ends with "s",
								//	set its part of speech to plural
								//	common noun.

			if	(	posTag.equals( posTags.getSingularNounTag() ) &&
					spelling.endsWith( "s" )
				)
			{
				word.setPartsOfSpeech( posTags.getPluralNounTag() );
            }
								//	Rule 7: convert a common noun to a
								//	present participle verb.

			if ( spelling.endsWith( "ing" ) )
			{
				if	(	posTags.isNounTag( posTag ) &&
						!posTags.isProperNounTag( posTag )
					)
				{
					word.setPartsOfSpeech(
						posTags.getPresentParticipleTag() );
            	}
            }
								//	Set previous word to current word.

        	previousWord	= word;

								//	Replace word with updated word.

        	taggedSentence.remove( i );
        	taggedSentence.add( i , word );
		}

		return taggedSentence;
	}

	/**	Retag a sentence.
	 *
	 *	@param	sentence	The sentence as an
	 *						{@link edu.northwestern.at.utils.corpuslinguistics.adornedword.AdornedWord} .
	 *
	 *	@return				The sentence with words retagged.
	 */

	public<T extends AdornedWord> List<T> retagSentence
	(
		List<T> sentence
	)
	{
		return retagWords( sentence );
	}

	/**	Can retagger add or delete words in the original sentence?
	 *
	 *	@return		true if retagger can add or delete words.
	 */

	public boolean canAddOrDeleteWords()
	{
		return true;
	}

	/**	Return tagger description.
	 *
	 *	@return		Tagger description.
	 */

	public String toString()
	{
		return "Simple rule based tagger";
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



