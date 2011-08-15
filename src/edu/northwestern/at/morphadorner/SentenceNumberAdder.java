package edu.northwestern.at.morphadorner;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.text.*;
import java.util.*;

import edu.northwestern.at.utils.*;

/**	Add word numbers, sentence numbers, and sentence milestones to adorned text.
 *
 *	<p>
 *	Reads an adorned XML file and adds word and sentence numbers
 *	numbers to each <w> tag using a list of SentenceAndWordNumber
 *	entries.  Also adds <milestone> elements for sentences.
 *	</p>
 */

public class SentenceNumberAdder
{
	/** Line separator. */

	protected static final String LINE_SEPARATOR =
		System.getProperty( "line.separator" );

	/**	Milestone formats. */

	protected static String milestoneFormat	=
		"%s<milestone unit=\"sentence\" n=\"%s\" position=\"%s\"/>";

	protected static String milestoneWithPathFormat	=
		"%s<milestone unit=\"sentence\" n=\"%s\" position=" +
		"\"%s\" " + WordAttributeNames.p + "=\"%s\"/>";

	/**	Add sentence and word numbers to adorned output.
	 *
	 *	@param	inputFileName	Adorned input file name.
	 *	@param	outputFileName	Updated adorned output file name.
	 *	@param	sortedWords		Sentence and word number data.
	 */

	public SentenceNumberAdder
	(
		String inputFileName ,
		String outputFileName ,
		SortedArrayList<SentenceAndWordNumber> sortedWords
	)
	{
								//	Perform conversion.
		try
		{
								//	Open input file.

			UnicodeReader streamReader	=
				new UnicodeReader
				(
					new FileInputStream( new File( inputFileName ) ) ,
					"utf-8"
				);

			BufferedReader in	= new BufferedReader( streamReader );

								//	Open output file for adorned
								//	text with word and sentence numbers
								//	added.

			FileOutputStream outputStream			=
				new FileOutputStream( outputFileName , false );

			BufferedOutputStream bufferedStream	=
				new BufferedOutputStream( outputStream );

			OutputStreamWriter writer	=
				new OutputStreamWriter( bufferedStream , "utf-8" );

			PrintWriter printWriter		= new PrintWriter( writer );

								//	Read first line of input file.

			String line			= in.readLine();

			boolean needLine	= true;

			int wIndex			= -1;

			SentenceAndWordNumber lookupSWN	=
				new SentenceAndWordNumber
				(
					"" , 0 , "" , false
				);
								//	Loop over adorned input file.

			int totalWords		= 0;
			int lookedUpWords	= 0;

			while ( line != null )
			{
								//	Only process non-empty lines.

				if ( line.trim().length() > 0 )
				{
								//	Does input line contain <w> tag?

					int wPos	= line.indexOf( "<w " );

								//	If line contains <w> tag ...

					if ( wPos >= 0 )
					{
						totalWords++;

								//	Split <w> text into  attributes
								//	and word text.

						String[] wValues	=
							WordAttributePatterns.wReplacer.matchGroups(
								line );

								//	Extract word ID.

						String[] idValues	=
							WordAttributePatterns.idReplacer.matchGroups(
								wValues[ WordAttributePatterns.ATTRS ] );

						String id	=
							idValues[ WordAttributePatterns.IDVALUE ];

								//	Extract path.

						String[] pathValues	=
							WordAttributePatterns.pathReplacer.matchGroups(
								wValues[ WordAttributePatterns.ATTRS ] );

						String path	= "";

						if ( pathValues != null )
						{
							path	=
								pathValues[ WordAttributePatterns.PATHVALUE ];
                        }
								//	Get the word text.

						String wordText	=
							wValues[ WordAttributePatterns.WORD ];

								//	Get sentence and word numbers.
								//	Most of the time the next entry
								//	in the sortedWords list will be
								//	the one we want if the word IDs
								//	are in reading context order.
								//	If not -- the word IDs don't match --
								//	do a lookup for the word ID.

						SentenceAndWordNumber swn	=
							sortedWords.get( ++wIndex );

						if ( !swn.getID().equals( id ) )
						{
							lookupSWN.setID( id );

							wIndex	= sortedWords.indexOf( lookupSWN );

							swn		= sortedWords.get( wIndex );

							lookedUpWords++;
						}

						int sentenceNumber	= swn.getSentenceNumber();
						int wordNumber		= swn.getWordNumber();
						boolean isEOS		= swn.getEOS();
						boolean isFirstPart	= swn.isFirstPart();
						boolean isLastPart	= swn.isLastPart();

								//	Output start sentence milestone
								//	if requested and this is the first
								//	word in a sentence.

						if	(	( wordNumber == 1 ) && isFirstPart &&
								MorphAdornerSettings.outputSentenceBoundaryMilestones
							)
						{
							printWriter.println
							(
								makeMilestone
								(
									"start" ,
									sentenceNumber ,
									wValues[ WordAttributePatterns.LEFT ] ,
									path
								)
							);
						}
								//	Generate updated <w> tag
								//	and add word and sentence numbers.

						StringBuilder sb	= new StringBuilder();

						sb.append( wValues[ WordAttributePatterns.LEFT ] );
						sb.append( "<w " );
						sb.append( wValues[ WordAttributePatterns.ATTRS ] );

						if ( MorphAdornerSettings.outputSentenceNumber )
						{
							sb.append( " " );
							sb.append( MorphAdornerSettings.outputSentenceNumberAttribute );
							sb.append( "=\"" );
							sb.append( sentenceNumber );
							sb.append( "\"" );
						}

						if ( MorphAdornerSettings.outputWordNumber )
						{
							sb.append( " " );
							sb.append( MorphAdornerSettings.outputWordNumberAttribute );
							sb.append( "=\"" );
							sb.append( wordNumber );
							sb.append( "\"" );
						}

						sb.append( ">" );
						sb.append( wValues[ WordAttributePatterns.WORD ] );
						sb.append( "</w>" );
						sb.append( wValues[ WordAttributePatterns.RIGHT ] );

						printWriter.println( sb );

								//	Output end sentence milestone
								//	if requested and this was the last
								//	word in a sentence.

						if	(	isEOS && isLastPart &&
								MorphAdornerSettings.outputSentenceBoundaryMilestones
							)
						{
								//	Before emitting the milestone,
								//	output any <c> elements that appear
								//	after the last word in the sentence.

							line	= in.readLine();

							while	(	( line != null ) &&
							            line.trim().startsWith( "<c>" )
									)
							{
								printWriter.println( line );

								line	= in.readLine();
							}

							needLine	= false;

								//	Now emit the milestone for the end
								//	of the sentence.

							printWriter.println
							(
								makeMilestone
								(
									"end" ,
									sentenceNumber ,
									wValues[ WordAttributePatterns.LEFT ] ,
									path
								)
							);
						}
					}
					else
					{
								//	Output adorned line.

						printWriter.println( line );
					}
                }
								//	Read next input line.

				if ( needLine )
				{
					line	= in.readLine();
				}

				needLine	= true;
    		}
								//	Close input file.
    		in.close();
								//	Close derived adorned file.

    		printWriter.close();
/*
			System.err.println
			(
				"Total words=" + totalWords +
				", looked up words=" + lookedUpWords
			);
*/
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}

	/**	Make a milestone marker for a sentence.
	 *
	 *	@param	position	Position ("start" or "end").
	 *	@param	number		The sentence number.
	 *	@param	indent		Leading indentation.
	 *	@param	path		Associated word path.
	 */

	protected String makeMilestone
	(
		String position ,
		int number ,
		String indent ,
		String path
	)
	{
								//	Create "p=" attribute for milestone
								//	from path for associated word.

		String sentencePath	= "";

		if ( ( path != null ) && ( path.length() > 0 ) )
		{
			sentencePath	= path;

			int lastSlash	= sentencePath.lastIndexOf( "\\" );

			if ( lastSlash >= 0 )
			{
				sentencePath	= sentencePath.substring( 0 , lastSlash );
			}
		}

		StringBuilder sb	= new StringBuilder();

		if ( sentencePath.length() > 0 )
		{
			new Formatter( sb ).format
			(
				milestoneWithPathFormat ,
				new Object[]{ indent , number + "" , position , sentencePath }
			);
        }
        else
        {
			new Formatter( sb ).format
			(
				milestoneFormat ,
				new Object[]{ indent , number + "" , position }
			);
        }

		return sb.toString();
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



