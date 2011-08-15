package edu.northwestern.at.morphadorner.tools.taggertrainer.ngram;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.*;
import java.text.*;

import edu.northwestern.at.utils.corpuslinguistics.lexicon.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.transitionmatrix.*;
import edu.northwestern.at.utils.html.*;
import edu.northwestern.at.utils.*;

public class NGramTaggerTrainer
{
	/**	The word lexicon. */

	protected static Lexicon wordLexicon;

	/**	The input training data file to process. */

	protected static String trainingDataFileName;

	/**	Number of lines/words in training data. */

	protected static int trainingDataCount	= 0;

	/**	Part of speech transition probability matrix. */

	protected static TransitionMatrix transitionMatrix	=
		new TransitionMatrix();

	/**	Transition matrix file name. */

	protected static String transitionMatrixFileName	= null;

	/**	Separator character(s) for tokens in input text.
	 */

	protected static String sepChars	= "\t";

	/**	Get program parameters.
	 *
	 *	@param	args	Command line parameters.
	 */

	protected static void getProgramParameters( String args[] )
		throws IOException
	{
								//	Error if insufficient arguments

		if ( args.length < 3 ) help();

								//	Training text file name.

		trainingDataFileName	= args[ 0 ];

								//	Load the word lexicon.

		File file	= new File( args[ 1 ] );

		wordLexicon	= new BaseLexicon();

		wordLexicon.loadLexicon( file.toURI().toURL() , "utf-8" );

								//	Transition matrix file name.

		transitionMatrixFileName	= args[ 2 ];
	}

	/**	Load training data. */

	protected static void loadTrainingData()
		throws IOException
	{
		long startTime		= System.currentTimeMillis();

		BufferedReader input	=
			new BufferedReader
			(
				new UnicodeReader
				(
					new FileInputStream( trainingDataFileName ) ,
					"utf-8"
				)
			);

		String line;
		trainingDataCount		= 0;
		String previousPOS		= ".";
		String previousPOSM1	= ".";

		while ( ( line = input.readLine() ) != null )
		{
			line	= line.trim();

			if ( line.length() == 0 ) continue;

			StringTokenizer	tokenizer	=
				new StringTokenizer( line , sepChars );

			String spelling		= "";
			String currentPOS	= "";

			try
			{
				spelling	= tokenizer.nextToken().trim();
				currentPOS	= tokenizer.nextToken().trim();
			}
			catch ( Exception e )
			{
				if ( CharUtils.isPunctuationOrSymbol( spelling ) )
				{
					currentPOS	= spelling;
				}
				else
				{
					e.printStackTrace();
					System.out.println( "line=" + line );
				}
			}
								//	Increment unigram count.

			transitionMatrix.incrementCount( currentPOS , 1 );

								//	Increment bigram count.

			transitionMatrix.incrementCount( previousPOS , currentPOS , 1 );

								//	Increment trigram count.

			transitionMatrix.incrementCount(
				previousPOSM1 , previousPOS , currentPOS , 1 );

								//	Increment word count.

			trainingDataCount++;

								//	Previous part of speech is now
								//	this part of speech.

			previousPOSM1	= previousPOS;
			previousPOS		= currentPOS;
		}

		long endTime		= System.currentTimeMillis();
		long secs			= ( endTime - startTime + 999 ) / 1000;

		System.out.println(
			"Training data loaded in " + secs + " seconds." );
	}

	/**	Create and run a part of speech tagger trainer.
	 */

	public static void main( String[] args )
	{
                    			//	Get program parameters.
		try
		{
			getProgramParameters( args );

                    			//	Run part of speech tagger trainer.

			loadTrainingData();

								//	Save transition matrix.

			transitionMatrix.saveTransitionMatrix
			(
				transitionMatrixFileName ,
				"utf-8" ,
				'\t'
			);
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}

	/**	Get URL from file name or URL.
	 *
	 *	@param	fileNameOrURL	The file name or URL string.
	 *
	 *	@return					A URL for the specified file name or URL.
	 */

	protected static URL getURL( String fileNameOrURL )
	{
		URL fileURL;

		try
		{
			fileURL	= new URL( fileNameOrURL );
		}
		catch ( MalformedURLException e )
		{
			try
			{
				fileURL	= new File( fileNameOrURL ).toURI().toURL();
			}
			catch ( Exception e2 )
			{
				fileURL	= null;
			}
		}

		return fileURL;
	}

	/**	Prints the help message.
	 */

	protected static void help()
	{
		System.out.println(
			"java edu.northwestern.at.taggertrainer.ngram.NGramTaggerTrainer trainingdata wordlexicon outputtransitionmatrix" );

		System.exit( 1 );
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



