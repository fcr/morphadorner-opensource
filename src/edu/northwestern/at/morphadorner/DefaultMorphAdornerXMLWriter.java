package edu.northwestern.at.morphadorner;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.text.*;
import java.util.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import com.megginson.sax.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.partsofspeech.*;
import edu.northwestern.at.utils.xml.*;

/**	Writes an adorned XML file with added ID fields.
 */

public class DefaultMorphAdornerXMLWriter
	implements MorphAdornerXMLWriter
{
	/**	Sorted list of word IDs and word and sentence number information. */

	protected SortedArrayList<SentenceAndWordNumber> sortedWords	=
		new SortedArrayList<SentenceAndWordNumber>();

	/**	Output XML writer. */

	protected XMLWriter writer;

	/**	Create XML writer.
	 */

	public DefaultMorphAdornerXMLWriter()
	{
	}

	/**	Write XML output.
	 *
	 *	@param	inFile				The XML input file.
	 *	@param	outFile				The XML output file.
	 *	@param	maxID				The maximum ID value in the input file.
	 *	@param	posTags				The part of speech tags.
	 *	@param	splitWords			The map of (word ID, # of word parts)
	 *								for multipart words.
	 *	@param	totalWords			Total words.
	 *	@param	totalPageBreaks		Total page breaks.
	 *
	 *	@throws						IOException, SAXException
	 */

	public void writeXML
	(
		String inFile ,
		String outFile ,
		int maxID ,
		PartOfSpeechTags posTags ,
		Map<Integer, Integer> splitWords ,
		int totalWords ,
		int totalPageBreaks
	)
		throws IOException, SAXException
	{
								//	Create XML reader.

		XMLReader reader	= XMLReaderFactory.createXMLReader();

								//	Remove <w> and <c> elements from tags
								//	which should have them.

		XMLFilter stripFilter	=
			new StripWordElementsFilter
			(
				reader ,
				MorphAdornerSettings.disallowWordElementsIn
			);
								//	Add filter for updating <w>
								//	tag attribute fields.

		IDFixerFilter idFilter	=
			new IDFixerFilter
			(
				stripFilter ,
				posTags ,
				outFile ,
				maxID ,
				sortedWords ,
				splitWords ,
				totalWords ,
				totalPageBreaks
			);
								//	If we need to output word numbers,
								//	sentence numbers, or sentence
								//	boundary milestones, we write the
								//	preliminary output to a
								//	temporary file.  Otherwise we can
								//	write the final output directly.
		boolean twoStep	=
			MorphAdornerSettings.outputSentenceBoundaryMilestones ||
			MorphAdornerSettings.outputSentenceNumber ||
			MorphAdornerSettings.outputWordNumber;

								//	Create a temporary file to hold
								//	the preliminary output if we
								//	generating output in two steps.

		String tempFileName	= "";

		if ( twoStep )
		{
			File tempFile	= File.createTempFile( "mad" , null );

			tempFile.deleteOnExit();

			tempFileName	= tempFile.getAbsolutePath();

			MorphAdornerLogger.println
			(
				"Using_two_step_output"
			);
		}
								//	Create XML output writer.

		OutputStreamWriter outputStreamWriter	=
			new OutputStreamWriter
			(
				new BufferedOutputStream
				(
					new FileOutputStream
					(
						twoStep ? tempFileName : outFile
					)
				) ,
				"utf-8"
			);

		writer	=
			new IndentingXMLWriter
			(
				idFilter ,
				outputStreamWriter
			);
								//	Indent XML by two characters for
								//	each nested level.

		((IndentingXMLWriter)writer).setIndentStep( 2 );

								//	Do not map characters to attributes.

		writer.setOutputCharsAsIs( true );

								//	Set the output system (DTD) name.
		writer.setDoctype
		(
			MorphAdornerSettings.xmlDoctypeName ,
			MorphAdornerSettings.xmlDoctypeSystem
		);
								//	Set writer into filter.

		idFilter.setWriter( writer );

								//	Parse the XML file, updating
								//	the <w> attribute fields, and
								//	writing the updated XML to a file.

		long startTime	= System.currentTimeMillis();

		writer.parse( inFile );

		if ( twoStep )
		{
			MorphAdornerLogger.println
			(
				"First_output_step_completed" ,
				MorphAdorner.durationString( startTime )
			);
		}

		try
		{
			outputStreamWriter.close();
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
								//	Add word and sentence numbers,
								//	and sentence milestones, in a
								//	second pass.  The temporary
								//	output file of the first pass
								//	is processed using word and
								//	sentence numbers generated here.
		if ( twoStep )
		{
			startTime	= System.currentTimeMillis();

			getWordAndSentenceNumbers();

								//	Add word and sentence numbers
								//	and sentence milestones.

			new SentenceNumberAdder( tempFileName , outFile , sortedWords );

								//	Delete the first step temporary
								//	file.

			FileUtils.deleteFile( tempFileName );

			MorphAdornerLogger.println
			(
				"Second_output_step_completed" ,
				MorphAdorner.durationString( startTime )
			);
		}
	}

	/**	Get word and sentence numbers.
	 */

	protected void getWordAndSentenceNumbers()
	{
								//	Initialize sentence and word numbers.

		int sentenceNumber		= 0;
		int wordNumber			= 0;
		int runningWordNumber	= 0;

								//	Loop over all sorted word IDs.

		for ( int i = 0 ; i < sortedWords.size() ; i++ )
		{
								//	Get information for this word ID.

			SentenceAndWordNumber swn	= sortedWords.get( i );

								//	If the current sentence is empty,
								//	we are starting a new sentence.

			if ( swn.isFirstPart() )
			{
				if ( wordNumber == 0 )
				{
					sentenceNumber++;
				}
								//	Increment word number.
				wordNumber++;
				runningWordNumber++;
			}
								//	Save word and sentence number.

			swn.setSentenceAndWordNumber
			(
				sentenceNumber ,
				MorphAdornerSettings.outputRunningWordNumbers ?
					runningWordNumber : wordNumber
			);
								//	If this word is the last word in the
								//	sentence, set the word number to zero
								//	so we will start a new sentence on
								//	the next word.

			if ( swn.getEOS() )
			{
				wordNumber	= 0;
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



