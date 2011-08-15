package edu.northwestern.at.morphadorner.tools.findteitextlanguage;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;

import javax.xml.parsers.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;
import org.xml.sax.helpers.DefaultHandler;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.languagerecognizer.*;
import edu.northwestern.at.utils.xml.*;

/**	Find languages for TEI-encoded text.
 */

public class FindTEITextLanguage
{
	/**	Language recognizer. */

	protected static LanguageRecognizer recognizer	=
		new DefaultLanguageRecognizer();

	/**	# params before input file specs. */

	protected static final int INITPARAMS	= 1;

	/**	Holds sorted work titles and languages output. */

	protected static Set<DocData> outputSet	=
		SetFactory.createNewSortedSet();

	/**	SAX parser factory. */

	protected static SAXParserFactory parserFactory;

	/**	Number of documents to process. */

	protected static int filesToProcess		= 0;

	/**	Current document. */

	protected static int currentFileNumber	= 0;

	/**	Longest document title. */

	protected static int longestTitle		= 0;

	/**	Main program.
	 *
	 *	@param	args	Program parameters.
	 */

	public static void main( String[] args )
	{
								//	Initialize.

		if ( !initialize( args ) )
		{
			System.exit( 1 );
		}
								//	Process all files.

		long startTime		= System.currentTimeMillis();

		int filesProcessed	= processFiles( args );

								//	Print results.
		try
		{
			printResults();
		}
		catch ( Exception e )
		{
			System.err.println( "Unable to print results." );
		}
								//	Output results to tabular file.
		try
		{
			outputResults( args[ 0 ] );
		}
		catch ( Exception e )
		{
			System.err.println
			(
				"Unable to output results to " + args[ 0 ]
			);
		}

		long processingTime	=
			( System.currentTimeMillis() - startTime + 999 ) / 1000;

								//	Terminate.

		terminate( filesProcessed , processingTime );
	}

	/**	Initialize.
	 */

	protected static boolean initialize( String[] args )
	{
								//	Check for sufficient parameters.

		if ( args.length < ( INITPARAMS + 1 ) )
		{
			System.err.println( "Not enough parameters." );
			return false;
		}
								//	Create SAX parser factory.

		parserFactory	= SAXParserFactory.newInstance();

		return true;
	}

	/**	Process one file.
	 *
	 *	@param	xmlFileName		Input file name to check for language.
	 */

	protected static void processOneFile( String xmlFileName )
	{
								//	Increment count of documents
								//	processed.
		currentFileNumber++;

		System.err.println
		(
			"Processing " + xmlFileName + " (" + currentFileNumber +
			"/" + filesToProcess + ")"
		);

		try
		{
								//	Create SAX parser.

			SAXParser saxParser	= parserFactory.newSAXParser();

								//	Extract plain text from this
								//	TEI XML file.

			TEITextExtractorHandler handler	=
				new TEITextExtractorHandler();

			saxParser.parse( xmlFileName , handler );

								//	Get extracted text.
			String docText	=
				handler.getExtractedText().replaceAll( "(\\s+)" , " " );

								//	Find the languages for the text.
								//	We only save the top three for
								//	later output.

			ScoredString[] languages	=
				recognizer.recognizeLanguage( docText );

								//	Document title is just file name
								//	stripped of path.

	        String docTitle	= FileNameUtils.stripPathName( xmlFileName );

								//	Create DocData entry to hold
								//	language information for this text.
			DocData docData	=
				new DocData
				(
					xmlFileName ,
					docTitle ,
					docText.length() ,
					languages
				);
								//	Save DocData entry in output map.

			outputSet.add( docData );

								//	Remember longest title so we
								//	can space printed output nicely.

			longestTitle	= Math.max( longestTitle , docTitle.length() );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			System.err.println( "   *** " + xmlFileName + " failed" );
		}
	}

	/**	Process files.
	 */

	protected static int processFiles( String[] args )
	{
		int result	= 0;
								//	Get file name/file wildcard specs.

		String[] wildCards	= new String[ args.length - INITPARAMS ];

		for ( int i = INITPARAMS ; i < args.length ; i++ )
		{
			wildCards[ i - INITPARAMS ]	= args[ i ];
		}
								//	Expand wildcards to list of
								//	file names,

		String[] fileNames	=
			FileNameUtils.expandFileNameWildcards( wildCards );

		filesToProcess		= fileNames.length;

								//	Process each file.

		for ( int i = 0 ; i < fileNames.length ; i++ )
		{
			processOneFile( fileNames[ i ] );
		}

		return fileNames.length;
	}

	/**	Print results.
	 */

	protected static void printResults()
		throws Exception
	{
								//	Wrap standard output as utf-8 stream.

		PrintStream printStream	=
			new PrintStream
			(
				new BufferedOutputStream( System.out ) ,
				true ,
				"utf-8"
			);
								//	Iterate over document language data.

		Iterator<DocData> iterator	= outputSet.iterator();

		while ( iterator.hasNext() )
		{
								//	Get next document's language data.

			DocData docData	= iterator.next();

								//	Print title.

			printStream.print( docData.docTitle );

			printStream.print
			(
				StringUtils.dupl
				(
					" " , longestTitle - docData.docTitle.length() + 4
				)
			);
								//	Print document text length.

			String docLength	=
				Formatters.formatIntegerWithCommas(
					docData.docLength );

			docLength	= StringUtils.lpad( docLength , 9 );

			printStream.print( docLength );
			printStream.print( " " );

								//	Print languages and scores for
								//	this document.

			for ( int i = 0 ; i < docData.docLanguages.length ; i++ )
			{
				ScoredString langAndScore	=
					docData.docLanguages[ i ];

				String scoreString	= langAndScore.getString();

				printStream.print( scoreString );

				printStream.print
				(
					StringUtils.dupl( " " , 8 - scoreString.length() )
				);

				scoreString	=
					Formatters.formatDouble( langAndScore.getScore() ,  4 );

				printStream.print( scoreString );

				printStream.print
				(
					StringUtils.dupl( " " , 8 - scoreString.length() )
				);
			}

			printStream.println();
		}
	}

	/**	Output results to tabular file.
	 *
	 *	@param	outputFileName	Output file name.
	 */

	protected static void outputResults( String outputFileName )
		throws Exception
	{
								//	Open output file.

		PrintStream printStream	=
			new PrintStream
			(
				new BufferedOutputStream
				(
					new FileOutputStream( outputFileName )
				) ,
				true ,
				"utf-8"
			);
								//	Iterate over document language data.

		Iterator<DocData> iterator	= outputSet.iterator();

		while ( iterator.hasNext() )
		{
								//	Get next document's language data.

			DocData docData	= iterator.next();

								//	Output document name/title.

			printStream.print( docData.docTitle );
			printStream.print( "\t" );

								//	Output document text length.

			printStream.print( docData.docLength );

								//	Output languages and scores for
								//	this document.

			for ( int i = 0 ; i < docData.docLanguages.length ; i++ )
			{
				ScoredString langAndScore	=
					docData.docLanguages[ i ];

				String scoreString	= langAndScore.getString();

				printStream.print( "\t" );
				printStream.print( scoreString );

				scoreString	=
					Formatters.formatDouble
					(
						langAndScore.getScore() ,  4
					);

				printStream.print( "\t" );
				printStream.print( scoreString );
			}

			printStream.println();
		}
								//	Close output file.
		printStream.close();
	}

	/**	Terminate.
	 *
	 *	@param	filesProcessed	Number of files processed.
	 *	@param	processingTime	Processing time in seconds.
	 */

	protected static void terminate
	(
		int filesProcessed ,
		long processingTime
	)
	{
		System.err.println
		(
			"Processed " +
			Formatters.formatIntegerWithCommas
			(
				filesProcessed
			) +
			" files in " +
			Formatters.formatLongWithCommas
			(
				processingTime
			) +
			" seconds."
		);
	}

	/**	Hold language recognition results for one document.
	 *
	 *	<p>
	 *	Stores up to three languages for each document.
	 *	Documents with less than three languages have
	 *	empty language entries with scores of 0 added.
	 *	</p>
	 */

	public static class DocData implements Comparable
	{
		/**	Document file name. */

		public String docFileName;

		/**	Document title. */

		public String docTitle;

		/**	Document text length. */

		public int docLength;

		/**	Document languages and scores. */

		public ScoredString[] docLanguages;

		/**	Create DocData entry.
		 *
		 *	@param	docFileName		Document file name.
		 *	@param	docTitle		Document title.
		 *	@param	docLength		Document text length.
		 *	@param	docLanguages	Document languages and scores.
		 */

		public DocData
		(
			String docFileName ,
			String docTitle ,
			int docLength ,
			ScoredString[] docLanguages
		)
		{
			this.docFileName		= docFileName;
			this.docTitle			= docTitle;
			this.docLength			= docLength;

			ScoredString[] langs	= new ScoredString[ 3 ];

			if ( docLanguages != null )
			{
				for ( int i = 0 ; i < 3 ; i++ )
				{
					if ( i < docLanguages.length )
					{
						langs[ i ]	= docLanguages[ i ];
					}
					else
					{
						langs[ i ]	= new ScoredString( "" , 0.0D );
					}
				}
			}
			else
			{
				for ( int i = 0 ; i < 3 ; i++ )
				{
					langs[ i ]	= new ScoredString( "" , 0.0D );
				}
			}

			this.docLanguages	= langs;
		}

		/**	Compare this object to another.
		 *
		 *	@param	object	Other object.
		 */

		public int compareTo( Object object )
		{
			int result	= Integer.MIN_VALUE;

			if ( ( object != null ) && ( object instanceof DocData ) )
			{
				DocData otherDoc	= (DocData)object;

				for ( int i = 0 ; i < docLanguages.length ; i++ )
				{
					result	=
						docLanguages[ i ].compareTo
						(
							otherDoc.docLanguages[ i ]
						);

					if ( result != 0 ) break;
				}

				if ( result == 0 )
				{
					result	=
						Compare.compare(
							docFileName , otherDoc.docFileName );
				}
			}

			return -result;
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



