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

/**	Abstract Name Recognizer.
 */

abstract public class AbstractNameRecognizer
	extends IsCloseableObject
	implements NameRecognizer, UsesLexicon, UsesLogger
{
	/**	Lexicon used by name recognizer. */

	protected Lexicon lexicon						= null;

	/**	Part of speech tagger used to find noun phrases. */

	protected PartOfSpeechTagger partOfSpeechTagger	= null;

	/**	Word tokenizer. */

	protected WordTokenizer wordTokenizer			= null;

	/**	Sentence splitter. */

	protected SentenceSplitter sentenceSplitter		= null;

	/**	Part of speech tags. */

	protected PartOfSpeechTags partOfSpeechTags		= null;

	/**	Built-in name lists. */

	protected Names names	= new Names();

	/**	Logger used for output. */

	protected Logger logger	= new DummyLogger();

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

	/**	Get the word lexicon.
	 *
	 *	@return		The static word lexicon.
	 */

	public Lexicon getLexicon()
	{
		return lexicon;
	}

	/**	Set the lexicon.
	 *
	 *	@param	lexicon		Lexicon used for tagging.
	 */

	public void setLexicon( Lexicon lexicon )
	{
		this.lexicon	= lexicon;
	}

	/**	Get part of speech tagger.
	 *
	 *	@return		The part of speech tagger.
	 *
	 */

	public PartOfSpeechTagger getPartOfSpeechTagger()
	{
		return partOfSpeechTagger;
	}

	/**	Set part of speech tagger used to find noun phrases.
	 *
	 *	@param	posTagger	The part of speech tagger.
	 *
	 */

	public void setPartOfSpeechTagger
	(
		PartOfSpeechTagger posTagger
	)
	{
								//	Get default part of speech tagger
								//	if none specified.

		if ( posTagger == null )
		{
			try
			{
				partOfSpeechTagger	= new DefaultPartOfSpeechTagger();
        	}
        	catch ( Exception e )
        	{
        	}
		}
		else
		{
			partOfSpeechTagger	= posTagger;
		}
								//	Get default word tokenizer.

		wordTokenizer		= new DefaultWordTokenizer();

								//	Get default sentence splitter.

		sentenceSplitter	= new DefaultSentenceSplitter();

								//	Get the part of speech
								//	guesser from the part of
								//	speech tagger.  Set this into
								//	sentence splitter to improve
								//	sentence boundary recognition.

		sentenceSplitter.setPartOfSpeechGuesser
		(
			partOfSpeechTagger.getPartOfSpeechGuesser()
		);

								//	Get part of speech tags.
		partOfSpeechTags	=
			partOfSpeechTagger.getLexicon().getPartOfSpeechTags();

								//	Get default word lexicon from
								//	part of speech tagger.

		setLexicon( partOfSpeechTagger.getLexicon() );
	}

	/**	Get name from name position.
	 *
	 *	@param	sentences		The collection of sentences.
	 *	@param	namePosition	The name position.
	 *
	 *	@return					The name string.
	 */

	public static <T extends AdornedWord> String namePositionToName
	(
		List<List<T>> sentences ,
		NamePosition namePosition
	)
	{
		String result		= "";

		List<T> sentence	= sentences.get( namePosition.sentence );

		for	(	int i = namePosition.startingWord ;
				i <= namePosition.endingWord ;
				i++
			)
		{
			result	+= sentence.get( i ) + " ";
		}

		return result.trim();
	}

	/**	Returns names from text.
	 *
	 *	@param	text	The text from which to extract names.
	 *
	 *	@return			Array of Set of names and places as strings.
	 *						[0]	= Set of proper names.
	 *						[1]	= Set of places.
	 */

	abstract public Set<String>[] findNames( String text );

	/**	Returns names from list of adorned word sentences.
	 *
	 *	@param	sentences	The collection of sentences.
	 *
	 *	@return				Array of Set of names and places.
	 *						[0]	= Set of proper names.
	 *						[1]	= Set of places.
	 */

	abstract public <T extends AdornedWord> Set<String>[] findNames
	(
		List<List<T>> sentences
	);

	/**	Returns name positions in list of adorned word sentences.
	 *
	 *	@param	sentences		The collection of sentences.
	 *
	 *	@return					Array of Set of names and places.
	 *							[0]	= Set of proper names.
	 *							[1]	= Set of places.
	 */

	abstract public <T extends AdornedWord> List<NamePosition>[] findNamePositions
	(
		List<List<T>> sentences
	);

	/** Close the name recognizer.
	 */

	public void close()
	{
		names	= null;
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



