package edu.northwestern.at.morphadorner.examples;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.adornedword.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.*;
import edu.northwestern.at.utils.corpuslinguistics.sentencesplitter.*;
import edu.northwestern.at.utils.corpuslinguistics.tokenizer.*;

/**	PosTagString: Adorn a string with parts of speech.
 *
 *	<p>
 *	Usage:
 *	</p>
 *
 *	<p>
 *	<code>
 *	java -Xmx256m edu.northwestern.at.morphadorner.example.PosTagString "Text to adorn."
 *	</code>
 *	</p>
 *
 *	<p>
 *	where "Text to adorn." specifies one or more sentences of text to
 *	adorn with part of speech tags.  The default tokenizer,
 *	sentence splitter, lexicons, and part of speech tagger are used.
 *	</p>
 *
 *	<p>
 *	Example:
 *	</p>
 *
 *	<p>
 *	<code>
 *	java -Xmx256m edu.northwestern.at.morphadorner.example.PosTagString "Mary had a little lamb.  Its fleece was white as snow."
 *	</code>
 *	</p>
 */

public class PosTagString
{
	/**	Main program.
	 *
	 *	@param	args	Program parameters.
	 */

	public static void main( String[] args )
	{
		try
		{
			adornText( args );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}

	/**	Adorn text specified as a program parameter.
	 *
	 *	@param	args	The program parameters.
	 *
	 *	<p>
	 *	args[ 0 ] contains the text to adorn.  The text may contain
	 *	one or more sentences with punctuation.
	 *	</p>
	 */

	public static void adornText( String[] args )
		throws Exception
	{
								//	Get text to adorn.  Report error
								//	and quit if none.

		if ( args.length < 1 )
		{
			System.out.println( "No text to adorn." );
			System.exit( 1 );
		}

		String textToAdorn	= args[ 0 ];

								//	Get default part of speech tagger.

		PartOfSpeechTagger partOfSpeechTagger	=
			new DefaultPartOfSpeechTagger();

								//	Get default word tokenizer.

		WordTokenizer wordTokenizer	= new DefaultWordTokenizer();

								//	Get default sentence splitter.

		SentenceSplitter sentenceSplitter	=
			new DefaultSentenceSplitter();

								//	Get the part of speech
								//	guesser from the part of
								//	speech tagger.  Set this into
								//	sentence splitter to improve
								//	sentence boundary recognition.

		sentenceSplitter.setPartOfSpeechGuesser
		(
			partOfSpeechTagger.getPartOfSpeechGuesser()
		);
								//	Split text into sentences
								//	and words.  Here "sentences"
								//	contains a list of sentences.
								//	Each sentence is itself a list of words.

		List<List<String>> sentences	=
			sentenceSplitter.extractSentences(
				textToAdorn , wordTokenizer );

								//	Assign part of speech tags to
								//	each word in each sentence.
								//	Here "taggedSentences" contains
								//	a list of List<AdornedWord> entries,
								//	one for each sentence.

		List<List<AdornedWord>> taggedSentences	=
			partOfSpeechTagger.tagSentences( sentences );

								//	Display tagged words.

		for ( int i = 0 ; i < sentences.size() ; i++ )
		{
								//	Get the next adorned sentence.
								//	This contains a list of adorned
								//	words.  Only the spellings
								//	and part of speech tags are
								//	guaranteed to be defined.

			List<AdornedWord> sentence	= taggedSentences.get( i );

			System.out.println
			(
				"---------- Sentence " + ( i + 1 ) + "----------"
			);

								//	Print out the spelling and part(s)
								//	of speech for each word in the
								//	sentence.  Punctuation is treated
								//	as a word too.

			for ( int j = 0 ; j < sentence.size() ; j++ )
			{
				AdornedWord adornedWord	= sentence.get( j );

				System.out.println
				(
					StringUtils.rpad( ( j + 1 ) + "" , 3  ) + ": " +
					StringUtils.rpad( adornedWord.getSpelling() , 20 ) +
					adornedWord.getPartsOfSpeech()
				);
			}
		}
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



