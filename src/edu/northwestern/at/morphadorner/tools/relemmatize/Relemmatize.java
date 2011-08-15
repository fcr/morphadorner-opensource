package edu.northwestern.at.morphadorner.tools.relemmatize;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.text.*;
import java.util.*;

import com.megginson.sax.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import edu.northwestern.at.morphadorner.tools.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.adornedword.*;
import edu.northwestern.at.utils.corpuslinguistics.lemmatizer.*;
import edu.northwestern.at.utils.corpuslinguistics.lexicon.*;
import edu.northwestern.at.utils.corpuslinguistics.namestandardizer.*;
import edu.northwestern.at.utils.corpuslinguistics.partsofspeech.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.*;
import edu.northwestern.at.utils.corpuslinguistics.sentencesplitter.*;
import edu.northwestern.at.utils.corpuslinguistics.spellingmapper.*;
import edu.northwestern.at.utils.corpuslinguistics.spellingstandardizer.*;
import edu.northwestern.at.utils.corpuslinguistics.tokenizer.*;
import edu.northwestern.at.utils.xml.*;

/**	Update standard spellings and lemmata in MorphAdorned files.
  */

public class Relemmatize
{
	/**	# params before input file specs. */

	protected static final int INITPARAMS	= 5;

	/**	Number of documents to process. */

	protected static int docsToProcess		= 0;

	/**	Current document. */

	protected static int currentDocNumber	= 0;

	/**	Output directory. */

	protected static String outputDirectory;

	/**	Relemmatizer filter. */

	protected static RelemmatizeFilter relemmatizeFilter;

	/**	Main program.
	 *
	 *	@param	args	Program parameters.
	 */

	public static void main( String[] args )
	{
								//	Initialize.
		try
		{
			if ( !initialize( args ) )
			{
				System.exit( 1 );
			}
								//	Process all files.

			long startTime		= System.currentTimeMillis();

			int filesProcessed	= processFiles( args );

			long processingTime	=
				( System.currentTimeMillis() - startTime + 999 ) / 1000;

								//	Terminate.

			terminate( filesProcessed , processingTime );
		}
		catch ( Exception e )
		{
			System.out.println( e.getMessage() );
		}
	}

	/**	Initialize.
	 */

	protected static boolean initialize( String[] args )
		throws Exception
	{
								//	Check number of parameters.

		if ( args.length < 6 )
		{
			System.err.println( "Not enough parameters." );
			return false;
		}
								//	Create lemmatizer.

		Lemmatizer lemmatizer		= new DefaultLemmatizer();

								//	Load word lexicon.

		Lexicon wordLexicon		= new DefaultLexicon();

								//	Load lexicon word data.

		wordLexicon.loadLexicon
		(
			new File( args[ 0 ] ).toURI().toURL() ,
			"utf-8"
		);

								//	Create name standardizer.

		NameStandardizer nameStandardizer	= new NoopNameStandardizer();

								//	Create spelling mapper.

		SpellingMapper spellingMapper		=
			new USToBritishSpellingMapper();

								//	Create spelling standardizer.

		SpellingStandardizer standardizer	=
			new ExtendedSimpleSpellingStandardizer();

								//	Load alternate/standard spelling pairs.

		standardizer.loadAlternativeSpellings
		(
			new File( args[ 1 ] ).toURI().toURL() ,
			"utf-8" ,
			"\t"
		);
								//	Load alternative spellings by word class.

		standardizer.loadAlternativeSpellingsByWordClass
		(
			new File( args[ 2 ] ).toURI().toURL() ,
			"utf-8"
		);
								//	Load standard spellings.

		standardizer.loadStandardSpellings
		(
			new File( args[ 3 ] ).toURI().toURL() ,
			"utf-8"
		);
								//	Load standard spellings into lemmatizer.

		lemmatizer.setDictionary( standardizer.getStandardSpellings() );

								//	Set lexicon for lemmatizer.

		lemmatizer.setLexicon( wordLexicon );

								//	Get the output directory.

		outputDirectory	= args[ 4 ];

								//	Create Relemmatizer filter.

		relemmatizeFilter	=
			new RelemmatizeFilter
			(
				XMLReaderFactory.createXMLReader() ,
				wordLexicon ,
				lemmatizer ,
				nameStandardizer ,
				standardizer ,
				spellingMapper
			);

		return true;
	}

	/**	Process one file.
	 *
	 *	@param	xmlInputFileName	Input file name relemmatize.
	 */

	protected static void processOneFile( String xmlInputFileName )
	{
		try
		{
								//	Output XML file name.

			String xmlOutputFileName	=
				new File
				(
					outputDirectory ,
					FileNameUtils.stripPathName( xmlInputFileName )
				).getAbsolutePath();

								//	Make sure output directory
								//	exists for XML output file.

			FileUtils.createPathForFile( xmlOutputFileName );

								//	Relemmatize input file.

			new FilterAdornedFile
			(
				xmlInputFileName ,
				xmlOutputFileName ,
				relemmatizeFilter
			);
								//	Report number of words and
								//	lemmata changed in this file.

			System.out.println
			(
				"File " + xmlInputFileName + " contains " +
				Formatters.formatIntegerWithCommas
				(
					relemmatizeFilter.getWordsProcessed()
				) +
				" word elements."
			);

			System.out.println
			(
				Formatters.formatIntegerWithCommas
				(
					relemmatizeFilter.getStandardChanged()
				) +
				" standard spellings updated."
			);

			System.out.println
			(
				Formatters.formatIntegerWithCommas
				(
					relemmatizeFilter.getLemmataChanged()
				) +
				" lemmata updated."
			);
		}
		catch ( Exception e )
		{
			System.out.println( xmlInputFileName + " failed" );
            System.out.println( e.getMessage() );
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

		docsToProcess		= fileNames.length;

								//	Process each file.

		for ( int i = 0 ; i < fileNames.length ; i++ )
		{
			processOneFile( fileNames[ i ] );
		}

		return fileNames.length;
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
		System.out.println
		(
			"Processed " +
			Formatters.formatIntegerWithCommas
			(
				filesProcessed
			) +
			StringUtils.pluralize
			(
				filesProcessed ,
				" file in " ,
				" files in "
			) +
			Formatters.formatLongWithCommas
			(
				processingTime
			) +
			StringUtils.pluralize
			(
				processingTime ,
				" second." ,
				" seconds."
			)
		);
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



