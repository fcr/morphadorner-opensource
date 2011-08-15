package edu.northwestern.at.morphadorner.tools.addcharacteroffsets;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.text.*;
import java.util.*;

import edu.northwestern.at.utils.*;

/**	Create derived MorphAdorner files with character offsets to word tokens.
 *
 *	<p>
 *	Usage:
 *	</p>
 *	<blockquote>
 *	<pre>
 *	java edu.northwestern.at.morphadorner.tools.addcharacteroffsets.AddCharacterOffsets adornedinput.xml adornedoutput.xml unadornedoutput.xml
 *	</pre>
 *	</blockquote>
 *	<table>
 *	<tr>
 *	<td>adornedinput.xml</td>
 *	<td>Standard MorphAdorner adorned output file.</td>
 *	</tr>
 *	<tr>
 *	<td>adornedoutput.xml</td>
 *	<td>Derived adorned file with character offsets added to <w> tags.</td>
 *	</tr>
 *	<tr>
 *	<td>unadornedoutput.xml</td>
 *	<td>Derived unadorned file whose word offsets are given in adornedoutput.xml file.</td>
 *	</tr>
 *	</table>
 *	<p>
 *	The derived adorned output file <em>adornedoutput.xml</em> adds a <em>cof=</em>
 *	attribute to each &lt;w&gt; tag.  The <em>cof=</em> attribute specifies
 *	the character (not byte) offset of each word in the
 *	<em>unadornedoutput.xml</em> file.  The
 *	latter file removes the &lt;w&gt; and &lt;c&gt; tags from the adorned input
 *	file and outputs the word and whitespace text as specified by the
 *	&lt;w&gt; and &lt;c&gt; tags.
 *	</p>
 */

public class AddCharacterOffsets
{
	/** Line separator. */

	protected static final String LINE_SEPARATOR =
		System.getProperty( "line.separator" );

	/**	<w> tag pattern. */

	protected static String wPattern	=
		"^(.*)<w (.*)>(.*)</w>(.*)$";

	/**	<w> tag replacement. */

	protected static PatternReplacer wreplacer	=
		new PatternReplacer( wPattern , "" );

	/**	Matcher groups for w. */

	protected final static int LEFT		= 1;
	protected final static int ATTRS	= 2;
	protected final static int WORD		= 3;
	protected final static int RIGHT	= 4;

	/**	<c> tag pattern. */

	protected static String cPattern	= "^(.*)<c>(.*)</c>$";

	/**	Matcher groups for c. */

	protected final static int CLEFT	= 1;
	protected final static int CDATA	= 2;
	protected final static int CRIGHT	= 3;

	/**	<c> tag replacement. */

	protected static PatternReplacer creplacer	=
		new PatternReplacer( cPattern , "" );

	/**	Maximum line width. */

	protected static final int MAXLINEWIDTH	= 80;

	/**	Main program. */

	public static void main( String[] args )
	{
								//	Must have input file name, long
								//	output file name, and short output
								//	file name  to perform conversion.
								//	Display program usage if three
								//	arguments are not provided.

		if ( args.length >= 3 )
		{
			new AddCharacterOffsets( args );
		}
		else
		{
			displayUsage();

			System.exit( 1 );
		}
	}

	/**	Display program usage. */

	public static void displayUsage()
	{
		System.out.println();
		System.out.println( "Usage:" );
		System.out.println();
		System.out.println( "java edu.northwestern.at.morphadorner.tools." +
			"addcharacteroffsets.AddCharacterOffsets adornedinput.xml " +
			"adornedoutput.xml unadornedoutput.xml" );
		System.out.println();

		System.out.println(
			"adornedinput.xml -- Standard MorphAdorner " +
			"adorned output file." );
		System.out.println(
			"adornedoutput.xml -- Derived adorned file with character" );
		System.out.println(
			 "offsets added to tags." );
		System.out.println(
			"unadornedoutput.xml -- Derived unadorned file whose word" );
		System.out.println(
			 "offsets are given in adornedoutput.xml file." );
		System.out.println();

		System.out.println(
			"The derived adorned output file adornedoutput.xml adds a" );
		System.out.println(
			"cof= attribute to each <w> tag. The cof= attribute" );
		System.out.println(
			"specifies the character (not byte) offset of each word in" );
		System.out.println(
			"the unadornedoutput.xml file. The latter file removes the" );
		System.out.println(
			"<w> and <c> tags from the adorned input file and outputs" );
		System.out.println(
			"the word and whitespace text as specified by the <w> and" );
		System.out.println(
			"<c> tags." );
	}

	/**	Create derived adorned files with character offset attributes.
	 *
	 *	@param	args	Command line arguments.
	 */

	public AddCharacterOffsets( String[] args )
	{
								//	Get input XML file name.

		String inputFileName		= args[ 0 ];

								//	Get long output XML file name.

		String longOutputFileName	= args[ 1 ];

								//	Get short output XML file name.

		String shortOutputFileName	= args[ 2 ];

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
								//	text with character offset attributes
								//	added.

			FileOutputStream outputStream			=
				new FileOutputStream( longOutputFileName , false );

			BufferedOutputStream bufferedStream	=
				new BufferedOutputStream( outputStream );

			OutputStreamWriter writer	=
				new OutputStreamWriter( bufferedStream , "utf-8" );

			PrintWriter longPrintWriter		= new PrintWriter( writer );

								//	Read first line of input file.

			String line			= in.readLine();

								//	Accumulates text of derived
								//	unadorned file.

			StringBuffer sb		= new StringBuffer();

								//	Holds character offset for next
								//	word token.

			int charPos			= 0;

								//	True if EOL must be added to
								//	derived unadorned output file.

			boolean needEOL		= false;

								//	True if blanks needed for derived
								//	unadorned output.

			boolean needBlanks	= false;

								//	Current line width for derived
								//	unadorned file output.

			int lineWidth		= 0;

								//	Line width of last unadorned
								//	output line.

			int lastLineWidth	= 0;

								//	True for first word in a section.

			boolean firstWord	= true;

								//	Loop over adorned input file.

			while ( line != null )
			{
								//	Does input line contain <w> tag?

				int wPos	= line.indexOf( "<w " );

								//	Does input line contain <c> tag?

				int cPos		= line.indexOf( "<c>" );

								//	If line contains <w> tag ...

				if ( wPos >= 0 )
				{
								//	Split <w> text into  attributes
								//	and word text.

					String[] groupValues	=
						wreplacer.matchGroups( line );

								//	Get the word text.

					String wordText	= groupValues[ WORD ];

								//	Add leading blanks to unadorned
								//	output if needed.  Use the spacing
								//	which preceded the <w> tag in
								//	the adorned input file.  However,
								//	we want to avoid starting a line
								//	with punctuation if we can.

					if ( needBlanks )
					{
						if ( CharUtils.isPunctuation( wordText ) && !firstWord )
						{
								//	Backup over the end of line string.

							sb.setLength(
								sb.length() - LINE_SEPARATOR.length() );

							lineWidth	= lastLineWidth;
						}
						else
						{
							sb.append( groupValues[ LEFT ] );

							needBlanks	= false;

								//	Update line width so we can line
								//	wrap the derived unadorned text.

							lineWidth	+= groupValues[ LEFT ].length();
						}
					}
								//	Get character offset for the <w>
								//	tag's word text.

					charPos	= sb.length();

								//	Generate updated <w> tag
								//	and add token offset attribute
								//	cof= .

					line		=
						groupValues[ LEFT ] +
						"<w " +
						groupValues[ ATTRS ] +
						" cof=\"" + charPos + "\"" +
						">" +
						groupValues[ WORD ] +
						"</w>" +
						groupValues[ RIGHT ];

								//	Add word text to unadorned
								//	output.

					sb.append( wordText );

								//	Update current unadorned line length.

					lineWidth	+= wordText.length();

								//	Need to output EOL string.

					needEOL	= true;

								//	If the line width exceeds the word
								//	wrap column, add the end of line string
								//	and reset the line width.

					if ( lineWidth > MAXLINEWIDTH )
					{
						sb.append( LINE_SEPARATOR );

						lastLineWidth	= lineWidth;
						lineWidth		= 0;
						needBlanks		= true;
					}
								//	Next word will not be first word
								//	in section.

					firstWord	= false;
				}
								//	Input line contained a "c" tag.

				else if ( cPos >= 0 )
				{
								//	Split <c> tag text and get.

					String[] groupValues	=
						creplacer.matchGroups( line );

								//	Add leading blanks to unadorned
								//	output if needed.  Use the spacing
								//	which preceded the <c> tag in
								//	the adorned input file.

					if ( needBlanks )
					{
						sb.append( groupValues[ LEFT ] );

						needBlanks	= false;

						lineWidth	+= groupValues[ LEFT ].length();
					}
					else
					{
								//	Add text from <c> tag to
								//	derived unadorned output.
								//	Since <c> tag only contains
								//	blanks, we don't need to emit
								//	this text if we output leading
								//	blanks.

						sb.append( groupValues[ CDATA ] );
					}
								//	Update output line width.

					lineWidth	+= groupValues[ CDATA ].length();

								//	EOL string needed.

					needEOL	= true;

								//	If line width exceeds word wrap
								//	column, add end of line string and
								//	reset the line width.

					if ( lineWidth > MAXLINEWIDTH )
					{
						sb.append( LINE_SEPARATOR );

						lastLineWidth	= lineWidth;
						lineWidth		= 0;
						needBlanks		= true;
					}
				}
								//	Neither <w> nor <c> tag found
								//	in input line.
				else
				{
								//	Add EOL string if needed.

					if ( needEOL )
					{
						sb.append( LINE_SEPARATOR );

						needEOL	= false;
					}
								//	Add input line to derived unadorned
								//	output.

					sb.append( line );
					sb.append( LINE_SEPARATOR );

					needBlanks		= true;
					lastLineWidth	= 0;
					lineWidth		= 0;
					firstWord		= true;
				}
								//	Output updated adorned line.

				longPrintWriter.println( line );

								//	Read next input line.

				line	= in.readLine();
    		}
								//	Close input file.
    		in.close();
								//	Close derived adorned file.

    		longPrintWriter.close();

								//	Write derived unadorned text.

			FileUtils.writeTextFile(
				shortOutputFileName , false , sb.toString() , "utf-8" );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
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


