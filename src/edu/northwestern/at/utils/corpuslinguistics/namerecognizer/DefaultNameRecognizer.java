package edu.northwestern.at.utils.corpuslinguistics.namerecognizer;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.net.*;
import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.adornedword.*;
import edu.northwestern.at.utils.corpuslinguistics.lexicon.*;
import edu.northwestern.at.utils.corpuslinguistics.partsofspeech.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.*;
import edu.northwestern.at.utils.corpuslinguistics.sentencesplitter.*;
import edu.northwestern.at.utils.corpuslinguistics.tokenizer.*;
import edu.northwestern.at.utils.logger.*;

import edu.northwestern.at.morphadorner.tools.*;

/**	DefaultNameRecognizer extracts proper names from text.
 */

public class DefaultNameRecognizer
	extends AbstractNameRecognizer
	implements NameRecognizer, UsesLogger
{
	/**	Create default name recognizer.
	 */

	public DefaultNameRecognizer()
	{
								//	Get part of speech tags.
		try
		{
			partOfSpeechTags	= new DefaultPartOfSpeechTags();
        }
        catch ( Exception e )
        {
        }
	}

	/**	Returns names from text.
	 *
	 *	@param	text	The text from which to extract names.
	 *
	 *	@return			Array of Set of names and places as strings.
	 *						[0]	= Set of proper names.
	 *						[1]	= Set of places.
	 */

	public Set<String>[] findNames( String text )
	{
								//	Make sure part of speech tagger
								//	is defined.

		if ( partOfSpeechTagger == null )
		{
			setPartOfSpeechTagger( null );
		}
								//	Extract sentences from text.
								//	Names are not allowed to cross
								//	sentence boundaries.

		List<List<String>> sentences	=
			sentenceSplitter.extractSentences( text , wordTokenizer );

								//	Get part of speech tags for each
								//	word in the text.

		List<List<AdornedWord>> taggedSentences	=
			partOfSpeechTagger.tagSentences( sentences );

								//	Get names in tagged sentences.

		return findNames( taggedSentences );
	}

	/**	Returns names from list of adorned word sentences.
	 *
	 *	@param	sentences	The list of sentences of adorned words
	 *						from which to extract names.
	 *
	 *	@return			Array of Set of names and places.
	 *					[0]	= Set of proper names.
	 *					[1]	= Set of places.
	 */

	public <T extends AdornedWord> Set<String>[] findNames
	(
	 	List<List<T>> sentences
	)
	{
								//	Get name positions.

		List<NamePosition>[] positions		= findNamePositions( sentences );

		List<NamePosition> namePositions	= positions[ 0 ];
		List<NamePosition> placePositions	= positions[ 1 ];

								//	Holds names and places extracted
								//	from text.

		Set<String> namesSet	= SetFactory.createNewSet();
		Set<String> placesSet	= SetFactory.createNewSet();

								//	Convert name positions to names.

		for ( int i = 0 ; i < namePositions.size() ; i++ )
		{
			namesSet.add
			(
				namePositionToName( sentences , namePositions.get( i ) )
			);
		}
								//	Convert place positions to place names.

		for ( int i = 0 ; i < placePositions.size() ; i++ )
		{
			placesSet.add
			(
				namePositionToName( sentences , placePositions.get( i ) )
			);
		}
								//	Return name and place sets.

		@SuppressWarnings("unchecked")
		Set<String>[] result	= (Set<String>[])new Set[ 2 ];
		result[ 0 ]				= namesSet;
		result[ 1 ]				= placesSet;

		return result;
	}

	/**	Returns name positions in list of adorned word sentences.
	 *
	 *	@param	sentences	The list of sentences of adorned words
	 *						from which to extract names.
	 *
	 *	@return			List of name positions of names and places.
	 *					[0]	= Positions of proper names.
	 *					[1]	= Position of places.
	 */

	public <T extends AdornedWord> List<NamePosition>[] findNamePositions
	(
		List<List<T>> sentences
	)
	{
								//	Holds lists of name positions
								//	extracted from text.

		List<NamePosition> namePositions	= ListFactory.createNewList();
		List<NamePosition> placePositions	= ListFactory.createNewList();

								//	Scan each tagged sentence for
								//	names.

		for ( int j = 0 ; j < sentences.size() ; j++ )
		{
								//	Get next tagged sentence.

			List<T> sentence	= sentences.get( j );

								//	Initialize name position.

			int properNounCount	= 0;
			int startingWord	= -1;
			int endingWord		= -1;
			int wordCount		= 0;

								//	Loop over each word in sentence
								//	and pick up next noun phrase.

			for ( int k = 0 ; k < sentence.size() ; k++ )
			{
								//	Get next word in sentence.

				AdornedWord word	= (AdornedWord)sentence.get( k );

								//	If word is a proper noun, or a noun that
								//	starts with a capital letter, append
								//	the word to the current noun phrase.

				String spelling		= word.getSpelling();
				String posTag		= word.getPartsOfSpeech();

				if	( 	partOfSpeechTags.isProperNounTag( posTag ) ||
						( 	partOfSpeechTags.isNounTag( posTag ) &&
							CharUtils.isFirstLetterCapital( spelling )
						)
					)
				{
					if ( startingWord == -1 )
					{
						startingWord	= k;
					}

					endingWord	= k;
            		wordCount++;

								//	If this word was a proper noun,
								//	increment the count of proper nouns
								//	in this noun phrase.

					if ( partOfSpeechTags.isProperNounTag( posTag ) )
					{
						properNounCount++;
					}
				}
								//	If the word isn't a noun, end the
								//	current noun phrase.
				else
				{
					if ( wordCount > 0 )
					{
						NamePosition namePosition	=
							new NamePosition
							(
								j ,
								startingWord ,
								endingWord ,
								properNounCount
							);

								//	In order for the noun phrase to be
								//	a name, we require at least one of
								//	the constituent words to have been
								//	a proper noun.
								//
								//	If the noun phrase is in the list of
								//	locations, add it to the set of
								//	extracted place names, else add it
								//	to the list of extracted person names.

						if ( validateNamePosition( sentences , namePosition ) )
						{
							String name	=
								namePositionToName
								(
									sentences ,
									namePosition
								);

							if ( names.isPlaceName( name ) )
							{
								placePositions.add( namePosition );
							}
							else
							{
								namePositions.add( namePosition );
							}
						}

						properNounCount	= 0;
						startingWord	= -1;
						endingWord		= -1;
						wordCount		= 0;
					}
				}
			}
                                //	Finished sentence.  Add any
                                //	remaining noun phrase to the
                                //	place name or person name set.

			if ( wordCount > 0 )
			{
				NamePosition namePosition	=
					new NamePosition
					(
						j ,
						startingWord ,
						endingWord ,
						properNounCount
					);

				if ( validateNamePosition( sentences , namePosition ) )
				{
					String name	=
						namePositionToName( sentences , namePosition );

					if ( names.isPlaceName( name ) )
					{
						placePositions.add( namePosition );
					}
					else
					{
						namePositions.add( namePosition );
					}
				}
			}
		}
								//	Return name and place lists.

		@SuppressWarnings("unchecked")
		List<NamePosition>[] result	= (List<NamePosition>[])new List[ 2 ];
		result[ 0 ]					= namePositions;
		result[ 1 ]					= placePositions;

		return result;
	}

	/**	Check name for validity.
	 *
	 *	@param	sentences		The collection of sentences.
	 *	@param	namePosition	The possibly updated name position.
	 *
	 *	@return					true if name is valid.
	 */

	public <T extends AdornedWord> boolean validateNamePosition
	(
		List<List<T>> sentences ,
		NamePosition namePosition
	)
	{
		List<T> sentence	= sentences.get( namePosition.sentence );

		if ( sentence.get( namePosition.startingWord ).toString().equals( "Will" ) )
		{
			if	(	( namePosition.endingWord > namePosition.startingWord ) &&
					names.isNamePrefix
					(
						sentence.get( namePosition.startingWord + 1 ).toString()
					)
				)
			{
				namePosition.startingWord++;
				namePosition.properNounCount--;
			}
		}

		return ( namePosition.properNounCount > 0 );
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



