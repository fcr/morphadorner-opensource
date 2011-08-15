package edu.northwestern.at.morphadorner.examples;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;

import edu.northwestern.at.morphadorner.tools.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.sentencemelder.*;

/**	Using an adorned text.
 *
 *	<p>
 *	Usage:
 *	</p>
 *
 *	<p>
 *	<code>
 *	java -Xmx256m edu.northwestern.at.morphadorner.example.UsingAnAdornedText adornedtext.xml id1 id2 id3 id4
 *	</code>
 *	</p>
 *
 *	<p>
 *	where
 *	</p>
 *
 *	<ul>
 *	<li><em>adorntext.xml</em> is a MorphAdorned XML file</li>
 *	<li>id1 is a word ID in the adorned XML file</li>
 *	<li>id2 is a word ID in the adorned XML file which follows id1</li>
 *	<li>id3 is a word ID in the adorned XML file</li>
 *	<li>id4 is a word ID in the adorned XML file which follows id4</li>
 *	</ul>
 */

public class UsingAnAdornedText
{
    /**	Adorned XML reader. */

    protected static AdornedXMLReader xmlReader;

	/**	The word IDs. */

	protected static List<String> wordIDs	=
		ListFactory.createNewList();

	/**	UTF-8 print stream. */

	protected static PrintStream printStream;

	/**	Main program. */

	public static void main( String[] args )
	{
	    try
	    {
			doit( args );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}

	/**	Read adorned file and perform extraction operations. */

	public static void doit( String[] args )
		throws Exception
	{
		printStream		=
			new PrintStream
			(
				new BufferedOutputStream( System.out ) ,
				true ,
				"utf-8"
			);
								//	Read adorned input file.

		xmlReader	= new AdornedXMLReader( args[ 0 ] );

								//	Get list of word IDs.

		wordIDs		= xmlReader.getAdornedWordIDs();

								//	Report number of words in input.
		printStream.println
		(
			"Read " +
			StringUtils.formatNumberWithCommas( wordIDs.size() ) +
			" words from " + args[ 0 ] + " ."
		);
								//	Get sentences.

		List<List<ExtendedAdornedWord>> sentences	=
			xmlReader.getSentences();

								//	Report number of sentences in input.
		printStream.println
		(
			"Read " +
			StringUtils.formatNumberWithCommas( sentences.size() ) +
			" sentences from " + args[ 0 ] + " ."
		);
								//	Display the first five sentences.
								//	We use a sentence melder.
								//	We also wrap the sentence text
								//	at column 70 for display purposes.

		printStream.println();

		printStream.println
		(
			"The first five sentences are:"
		);

		printStream.println();
		printStream.println( StringUtils.dupl( "-" , 70 ) );

		SentenceMelder melder	= new SentenceMelder();

		for ( int i = 0 ; i < Math.min( 5 , sentences.size() ) ; i++ )
		{
    							//	Get text for this sentence.

			String sentenceText	=
				melder.reconstituteSentence( sentences.get( i ) );

								//	Wrap the sentence text at column 70.

			sentenceText	=
				StringUtils.wrapText(
					sentenceText, Env.LINE_SEPARATOR , 70 );

								//	Print wrapped sentence text.

			printStream.println
			(
				( i + 1 ) + ": " +
				sentenceText
			);
		}

		printStream.println( StringUtils.dupl( "-" , 70 ) );
		printStream.println();

								//	Word information for words in the
								//	third sentence.

		if ( sentences.size() > 2 )
		{
			printStream.println();

			printStream.println
			(
				"Words in the third sentence:"
			);

			printStream.println();
			printStream.println( StringUtils.dupl( "-" , 70 ) );

			List<ExtendedAdornedWord> sentence	= sentences.get( 2 );

			for ( int i = 0 ; i < sentence.size() ; i++ )
			{
				ExtendedAdornedWord adornedWord	= sentence.get( i );

				printStream.println( "Word " + ( i + 1 ) );

				printStream.println(
					"  Word ID          : " + adornedWord.getID() );
				printStream.println(
					"  Token            : " + adornedWord.getToken() );
				printStream.println(
					"  Spelling         : " + adornedWord.getSpelling() );
				printStream.println(
					"  Lemmata          : " + adornedWord.getLemmata() );
				printStream.println(
					"  Pos tags         : " +
					adornedWord.getPartsOfSpeech() );
				printStream.println(
					"  Standard spelling: " +
					adornedWord.getStandardSpelling() );
				printStream.println(
					"  Sentence number  : " +
					adornedWord.getSentenceNumber() );
				printStream.println(
					"  Word number      : " +
					adornedWord.getWordNumber() );
				printStream.println(
					"  XML path         : " +
					adornedWord.getPath() );
				printStream.println(
					"  is EOS           : " +
					adornedWord.getEOS() );
				printStream.println(
					"  word part flag   : " +
					adornedWord.getPart() );
				printStream.println(
					"  word ordinal     : " +
					adornedWord.getOrd() );
				printStream.println(
					"  page number      : " +
					adornedWord.getPageNumber() );
				printStream.println(
					"  Main or side text: " +
					adornedWord.getMainSide() );
				printStream.println(
					"  is spoken        : " +
					adornedWord.getSpoken() );
				printStream.println(
					"  is verse         : " +
					adornedWord.getVerse() );
				printStream.println(
					"  in jump tag      : " +
					adornedWord.getInJumpTag() );
				printStream.println(
					"  is a gap         : " +
					adornedWord.getGap() );
			}

			printStream.println( StringUtils.dupl( "-" , 70 ) );
			printStream.println();
		}
								//	Generate xml for selected word ranges.

		generateXML( args[ 1 ] , args[ 2 ] );
		generateXML( args[ 3 ] , args[ 4 ] );
	}

	/**	Generate XML from one word ID to another.
	 *
	 *	@param	firstWordID		First word ID.
	 *	@param	secondWordID	Second word ID.
	 */

	public static void generateXML
	(
		String firstWordID ,
		String secondWordID
	)
	{
								//	Generate xml for selected word range.

		String xml	= xmlReader.generateXML( firstWordID , secondWordID );

								//	Display generated xml.

		printStream.println();

		printStream.println( "Generated XML for words " +
			firstWordID + " through " + secondWordID + ":" );

		printStream.println();
		printStream.println( StringUtils.dupl( "-" , 70 ) );
		printStream.println( xml );
		printStream.println( StringUtils.dupl( "-" , 70 ) );
		printStream.println();
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



