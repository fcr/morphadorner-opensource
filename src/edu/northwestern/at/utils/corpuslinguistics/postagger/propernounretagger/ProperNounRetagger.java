package edu.northwestern.at.utils.corpuslinguistics.postagger.propernounretagger;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.adornedword.*;
import edu.northwestern.at.utils.corpuslinguistics.partsofspeech.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.unigram.*;

/**	A proper noun retagger.
 *
 *	<p>
 *	This retagger applies a short list of rules to improve tagging
 *	of proper nouns.
 *	</p>
 */

public class ProperNounRetagger
	extends UnigramTagger
	implements PartOfSpeechRetagger
{
	/**	Part of speech tags. */

	protected static PartOfSpeechTags posTags;

	/**	Create proper noun retagger.
	 */

	public ProperNounRetagger()
	{
		super();
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
								//	Get noun part of speech tags
								//	if we don't already have them.

		if ( posTags == null )
		{
			posTags	= getLexicon().getPartOfSpeechTags();
		}
								//	Examine all words in sentence to
								//	see if we should change nouns
								//	to proper nouns.  We do this
								//	if a noun has an initial capital letter.
								//	Note that we skip the first word of
								//	the sentence since the capitalization
								//	check is uninformative in this case.
								//	We also change proper nouns to
								//	common nouns when the word is not
								//	capitalized.

		for ( int i = 1 ; i < sentence.size() ; i++ )
		{
								//	Get next word in sentence.

			AdornedWord adornedWord	= sentence.get( i );

                                //	Get spelling.

			String spelling			= adornedWord.getSpelling();

								//	Is spelling capitalized?

			boolean isCapitalized	=
				Character.isUpperCase( spelling.charAt( 0 ) );

								//	See if word is a proper noun.
								//	Change it to a common noun if it
								//	is not capitalized.

			if ( !isCapitalized  )
			{
				String partsOfSpeech	= adornedWord.getPartsOfSpeech();

				if ( posTags.isProperNounTag( partsOfSpeech ) )
				{
					adornedWord.setPartsOfSpeech
					(
						posTags.getCorrespondingCommonNounTag(
							partsOfSpeech )
					);
				}
				else if ( posTags.isProperAdjectiveTag( partsOfSpeech ) )
				{
					adornedWord.setPartsOfSpeech( posTags.getAdjectiveTag() );
				}
			}
		}
								//	Return updated sentence.
		return sentence;
	}

	/**	Can retagger add or delete words in the original sentence?
	 *
	 *	@return		true if retagger can add or delete words.
	 */

	public boolean canAddOrDeleteWords()
	{
		return false;
	}

	/**	Return retagger description.
	 *
	 *	@return		Retagger description.
	 */

	public String toString()
	{
		return "Proper noun retagger";
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



