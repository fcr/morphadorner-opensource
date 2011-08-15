package edu.northwestern.at.morphadorner.examples;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.text.*;
import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.lexicon.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.guesser.*;
import edu.northwestern.at.utils.corpuslinguistics.sentencesplitter.*;
import edu.northwestern.at.utils.corpuslinguistics.tokenizer.*;

/**	SentenceAndTokenOffsets: Display sentence and token offsets in text.
 *
 *	<p>
 *	Usage:
 *	</p>
 *
 *	<p>
 *	<code>
 *	java -Xmx256m edu.northwestern.at.morphadorner.example.SentenceAndTokenOffsets InputFileName
 *	</code>
 *	</p>
 *
 *	<p>
 *	where "InputFileName" specifies the name of a text file to split
 *	into sentences and word tokens.  The default sentence splitter,
 *	tokenizer, part of speech guesser, and word and suffix lexicons
 *	are used.
 *	</p>
 *
 *	<p>
 *	Example:
 *	</p>
 *
 *	<p>
 *	<code>
 *	java -Xmx256m edu.northwestern.at.morphadorner.example.AdornAString mytext.txt
 *	</code>
 *	</p>
 *
 *	<p>
 *	The output displays each extracted sentence along with its starting and
 *	ending offset in the text read from the specified input file.
 *	For each sentence, a list of the extracted tokens in that sentence
 *	is displayed along with each token's starting and ending offset
 *	relative to the start of the sentence text.
 *	</p>
 */

public class SentenceAndTokenOffsets
{
	/**	Main program.
	 *
	 *	@param	args	Command line arguments.
	 */

	public static void main( String[] args )
	{
		try
		{
			if ( args.length > 0 )
			{
				displayOffsets( args[ 0 ] );
			}
			else
			{
				System.err.println(
					"Usage: SentenceAndTokenOffsets inputFileName" );
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}

	/**	Display sentence and token offsets in text.
	 *
	 *	@param	inputFileName	Input file name.
	 */

	public static void displayOffsets( String inputFileName )
		throws Exception
	{
								//	Wrap standard output as utf-8.

		PrintStream printOut	=
			new PrintStream
			(
				new BufferedOutputStream( System.out ) ,
				true ,
				"utf-8"
			);
								//	Load text to split into
								//	sentences and tokens.

		String sampleText	=
			FileUtils.readTextFile( inputFileName , "utf-8" );

								//	Convert all whitespace characters
								//	into blanks.  (Not necessary,
								//	but makes the display cleaner below.)

		sampleText	= sampleText.replaceAll( "\\s" , " " );

								//	Create default sentence splitter.

		SentenceSplitter splitter	= new DefaultSentenceSplitter();

								//	Create part of speech guesser
								//	for use by splitter.

		PartOfSpeechGuesser	partOfSpeechGuesser	=
			new DefaultPartOfSpeechGuesser();

								//	Get default word lexicon for
								//	use by part of speech guesser.

		Lexicon lexicon	= new DefaultWordLexicon();

								//	Set lexicon into guesser.

		partOfSpeechGuesser.setWordLexicon( lexicon );

								//	Get default suffix lexicon for
								//	use by part of speech guesser.

		Lexicon suffixLexicon		= new DefaultSuffixLexicon();

								//	Set suffix lexicon into guesser.

		partOfSpeechGuesser.setSuffixLexicon( suffixLexicon );

								//	Set guesser into sentence splitter.

		splitter.setPartOfSpeechGuesser( partOfSpeechGuesser );

								//	Create default word tokenizer.

		WordTokenizer tokenizer	= new DefaultWordTokenizer();

								//	Split input text into sentences
								//	and words.

		List<List<String>> sentences	=
			splitter.extractSentences
			(
				sampleText ,
				tokenizer
			);
								//	Get sentence start and end
								//	offsets in input text.

		int[] sentenceOffsets	=
			splitter.findSentenceOffsets( sampleText , sentences );

								//	Loop over sentences.

		for ( int i = 0 ; i < sentences.size() ; i++ )
		{
								//	Get start and end offset of
								//	sentence text.  Note:  the
								//	end is the end + 1 since that
								//	is what substring wants.

			int start		= sentenceOffsets[ i ];
			int end			= sentenceOffsets[ i + 1 ];

								//	Get sentence text.

			String sentence	=
				sampleText.substring( start , end );

								//	Display sentence number,
								//	start, end, and text.

			printOut.println(
				i + " [" + start + "," + ( end - 1 ) + "]: " + sentence );

								//	Get word tokens in this sentence.

			List words	= sentences.get( i );

								//	Get offsets for each word token
								//	relative to this sentence.

			int[] wordOffsets	=
				tokenizer.findWordOffsets( sentence , words  );

								//	Loop over word tokens.

			for ( int j = 0 ; j < words.size() ; j++ )
			{
								//	Get start and end offset of
								//	this word token.  Note:  the
								//	end is the end + 1 since that
								//	is what substring wants.

				start	= wordOffsets[ j ];
				end		=
					wordOffsets[ j ] + words.get( j ).toString().length();

								//	Display token number,
								//	start, end, and text.

				printOut.println
				(
					"          " + j + " [" + start + "," +
					( end - 1 ) + "]: " +
					sentence.substring( start , end )
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



