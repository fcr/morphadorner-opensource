package edu.northwestern.at.morphadorner.tools.xmltotab;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.text.*;
import java.util.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import edu.northwestern.at.morphadorner.tools.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.tokenizer.*;
import edu.northwestern.at.utils.csv.*;
import edu.northwestern.at.utils.xml.*;

/**	Convert MorphAdorner XML output to tab-separated tabular form.
 *
 *	<p>
 *	Usage:
 *	</p>
 *
 *	<p>
 *	java edu.northwestern.at.morphadorner.tools.xmltotab.XMLToTabWithGaps  input.xml output.tab<br />
 *	<br />
 *	input.xml -- input XML file.<br />
 *	output.tab -- output tab-separated values file.<br />
 *	</p>
 *
 *	<p>
 *	The attribute values for each "&lt;w&gt;" element in the input XML file
 *	are extracted and output to a tab-separated values text file.
 *	An output line contains the following information corresponding
 *	to a single word "&lt;w&gt;" element.
 *	</p>
 *
 *	<ol>
 *	<li>The work ID.</li>
 *	<li>The permanent word ID.</li>
 *	<li>The corrected original spelling.</li>
 *	<li>The corrected original spelling reversed.</li>
 *	<li>The standard spelling.</li>
 *	<li>The lemma.</li>
 *	<li>The part of speech.</li>
 *	<li>The XPath-like path to this word.
 *      The leading work ID and trailing word number are removed from
 *      the path.
 *      </li>
 *	<li>The end of sentence flag.  1 if this word ends a sentence,
 *		0 otherwise.
 *		</li>
 *	<li>The previous word's original spelling.</li>
 *	<li>The next word's original spelling.</li>
 *	<li>Up to 80 characters of text preceding the word in the text.</li>
 *	<li>Up to 80 characters of text following the word in the text.</li>
 *	</ol>
 *	<p>
 *  This tabular representation of an adorned XML text is useful for
 *  data checking purposes. The morphological attribute values for
 *  each word &lt;w&gt; element appear as columns. The 80 characters (or
 *  so) of text on either side of the word allows you to focus on
 *  particular part of speech tags and pinpoint errors from the
 *  automatic adornment process. The tab separated values may also be
 *  used to construct spreadsheets or databases of the individual
 *  word information.
 *	</p>
 */

public class XMLToTab
{
	/**	Main program. */

	public static void main( String[] args )
	{
								//	Must have input file name and
								//	output file name to perform conversion.
								//	Display program usage if two
								//	arguments are not provided.

		if ( args.length >= 2 )
		{
			new XMLToTab( args );
		}
		else
		{
			displayUsage();
			System.exit( 1 );
		}
	}

	/**	Display brief program usage.
	 */

	public static void displayUsage()
	{
		System.out.println( "Usage: " );
		System.out.println( "" );
		System.out.println(
			"   java edu.northwestern.at.morphadorner.tool.xmltotab." +
			"XMLToTab input.xml output.tab" );
		System.out.println( "" );
		System.out.println( "      input.xml -- input XML file" );
		System.out.println( "      output.tab -- output tab-separated " +
			"values file." );
	}

	/**	Supervises conversion of XML "<w>" elements to tabular form.
	 *
	 *	@param	args	Command line arguments.
	 */

	public XMLToTab( String[] args )
	{
								//	Get input XML file name.

		String xmlInputFileName	= args[ 0 ];

								//	Get output XML file name.

		String tsvOutputFileName	= args[ 1 ];

								//	Perform conversion.
		try
		{
								//	Get tab-separated file writer.

			CSVFileWriter writer	=
				new CSVFileWriter(
					tsvOutputFileName , "utf-8" , '\t' , (char)0 );

								//	Create XML reader for XML input.

			XMLReader reader		=
				XMLReaderFactory.createXMLReader();

								//	Add filter to XML reader to
								//	add path attributes to XML elements.

			AddXMLPathFilter xmlPathFilter	=
				new AddXMLPathFilter
				(
					reader ,
					FileNameUtils.changeFileExtension
					(
						FileNameUtils.stripPathName( xmlInputFileName ) ,
						""
					)
				);
								//	Get work ID.

			String workID	=
				FileNameUtils.stripPathName( xmlInputFileName );

			workID			=
				FileNameUtils.changeFileExtension( workID , "" );

								//	Add filter to XML reader to
								//	pull out "<w>" elements containing
								//	word information.

			ExtendedAdornedWordFilter wordInfoFilter	=
				new ExtendedAdornedWordFilter( xmlPathFilter , true );

								//	Parse the XML input.

			wordInfoFilter.parse( xmlInputFileName );

								//	We have collected information
								//	about each "<w>" element in
								//	the XML input.  For each of these,
								//	we write a corresponding
								//	tab-separated list of the
								//	attribute values to the output file.

			List<String> idList	= wordInfoFilter.getAdornedWordIDs();

			for ( int wordOrd = 0 ; wordOrd < idList.size() ; wordOrd++ )
			{
								//	Get next word's information.

				String id		= idList.get( wordOrd );

				ExtendedAdornedWord w		=
					wordInfoFilter.getExtendedAdornedWord( id );

								//	Only need information from
								//	last part of a multipart word.

				if ( !w.isFirstPart() ) continue;

								//	Create the tabular output line
								//	from the word information.

								//	Write work ID.

				writer.writeValue( workID );
				writer.writeSeparator();

								//	Write word ID.

				writer.writeValue( w.getID() );
				writer.writeSeparator();

								//	Write corrected original spelling.

				writer.writeValue( w.getSpelling() );
				writer.writeSeparator();

								//	Write reversed corrected original
								//	spelling.

				writer.writeValue(
					StringUtils.reverseString( w.getSpelling() ) );

				writer.writeSeparator();

								//	Write standard spelling.

				writer.writeValue( w.getStandardSpelling() );
				writer.writeSeparator();

								//	Write lemmata.

				writer.writeValue( w.getLemmata() );
				writer.writeSeparator();

								//	Write parts of speech.

				writer.writeValue( w.getPartsOfSpeech() );
				writer.writeSeparator();

								//	Write path to tag.

				writer.writeValue( fixPath( w.getPath() ) );
				writer.writeSeparator();

								//	Write end of sentence flag.

				writer.writeValue( w.getEOS() ? "1" : "0" );
				writer.writeSeparator();

								//	Previous word's spelling.

				if ( w.getPreviousWord() != null )
				{
					writer.writeValue( w.getPreviousWord().getSpelling() );
				}
				else
				{
					writer.writeValue( "" );
				}

				writer.writeSeparator();

								//	Next word's spelling.

				if ( w.getNextWord() != null )
				{
					writer.writeValue( w.getNextWord().getSpelling() );
				}
				else
				{
					writer.writeValue( "" );
				}

				writer.writeSeparator();

								//	Generate 80 characters of
								//	KWIC content to each side of
								//	word.

				String[] kwic	=
					getKWIC( w.getID() , 80 , idList , wordInfoFilter );

				writer.writeValue( kwic[ 0 ] );
				writer.writeSeparator();

				writer.writeValue( kwic[ 2 ] );

								//	Write end of line.

				writer.writeEOL();
			}
								//	Close output file after all
								//	tabular lines written.
			writer.close();
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}

	/**	Trim work ID and word number from word path.
	 *
	 *	@param	path	Full path containing work ID and word number.
	 *
	 *	@return			Path trimmed of leading work ID and trailing
	 *					word number.
	 */

	protected static String fixPath( String path )
	{
		String[] tags			= path.split( "\\\\" );
		StringBuilder result	= new StringBuilder();

		for ( int i = 2 ; i < tags.length - 1 ; i++ )
		{
			result.append( "\\" );
			result.append( tags[ i ] );
		}

		return result.toString();
	}

	/**	Generate a KWIC line for a spelling.
	 *
	 *	@param	id				Word ID the word for which to generate
	 *								a KWIC.
	 *	@param	KWICWidth		Maximum width (in characters) of
	 *								KWIC text.
	 *	@param	idList			List of word IDs
	 *	@param	wordInfoFilter	Word info filter.
	 *
	 *	@return				The KWIC sections as a String array.
	 *							[0]	= left KWIC text
	 *							[1]	= word
	 *							[2]	= right KWIC text
	 */

	public static String[] getKWIC
	(
		String id ,
		int KWICWidth ,
		List<String> idList ,
		ExtendedAdornedWordFilter wordInfoFilter
	)
	{
		String[] results		= new String[ 3 ];
		StringBuffer KWICBuffer	= new StringBuffer();

								//	Get word info for this word.

		ExtendedAdornedWord wordInfo	=
			wordInfoFilter.getExtendedAdornedWord( id );

								//	Get token for this word.

		String token	= wordInfo.getToken();
		results[ 1 ]	= token;

								//	Figure maximum width for the
								//	left and right kwic text.

		int maxWidth	= KWICWidth / 2;

								//	Accumulate the left kwic text.

		while	(	( KWICBuffer.length() < maxWidth ) &&
					( wordInfo.getPreviousWord() != null )
				)
		{
			wordInfo	= wordInfo.getPreviousWord();
			token		= wordInfo.getToken();

			if ( KWICBuffer.length() > 0 )
			{
				KWICBuffer.insert( 0 , " " );
			}

			KWICBuffer.insert( 0 , token );
		}

		results[ 0 ]	= KWICBuffer.toString();

		KWICBuffer.setLength( 0 );

								//	Get word info again for this word.

		wordInfo	= wordInfoFilter.getExtendedAdornedWord( id );

								//	Accumulate the right kwic text.

		while	(	( KWICBuffer.length() < maxWidth ) &&
					( wordInfo.getNextWord() != null )
				)
		{
			wordInfo	= wordInfo.getNextWord();
			token		= wordInfo.getToken();

			KWICBuffer.append( token );
			KWICBuffer.append( " " );
		}

		results[ 2 ]	= KWICBuffer.toString();

		return results;
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



