package edu.northwestern.at.morphadorner.tools.fixquotes;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.tokenizer.*;
import edu.northwestern.at.utils.xml.*;

import org.w3c.dom.*;
import org.w3c.dom.traversal.*;

/**	Fix quotes in TEI XML files.
 *
 *	<p>
 *	Usage:
 *	</p>
 *	<blockquote>
 *	<pre>
 *	java edu.northwestern.at.morphadorner.tools.fixquotes.FixXMLQuotes softtags.txt jumptags.txt outputdirectory input1.xml input2.xml ...
 *	</pre>
 *	</blockquote>
 *	<p>
 *	softtags.txt -- text file containing list of soft XML tags, one per line.<br />
 *	jumptags.txt -- text file containing list of jump XML tags, one per line.<br />
 *	outputdirectory -- output directory to receive xml files with quotes fixed.<br />
 *	input*.xml -- input TEI XML files.<br />
 *	</p>
 *
 *	<p>
 *	Since the "quotification" relies on heuristics, not all quotes will be
 *	converted correctly.
 *	</p>
 */

public class FixXMLQuotes
{
	/**	DOM document. */

	protected static Document document;

	/**	# params before input file specs. */

	protected static final int INITPARAMS	= 3;

	/**	Number of documents to process. */

	protected static int docsToProcess		= 0;

	/**	Current document. */

	protected static int currentDocNumber	= 0;

	/**	Output directory. */

	protected static String outputDirectory;

	/**	Contractions. */

	protected static TaggedStrings contractions;

	/**	Pattern matcher for matching contractions. */

	protected static Matcher contractionsMatcher;

	/**	Left single quote replacement text. */

	protected static final String lsquo	= "&lsquo;";

	/**	Left double quote replacement text. */

	protected static final String ldquo	= "&ldquo;";

	/**	Right single quote replacement text. */

	protected static final String rsquo	= "&rsquo;";

	/**	Right double quote replacement text. */

	protected static final String rdquo	= "&rdquo;";

	/**	Apostrophereplacement text. */

	protected static final String apos	= "&apos;";

	/**	Temporary single quote marker. */

	protected static final String sq	= "\uE060";

	/**	Temporary double quote marker. */

	protected static final String dq	= "\uE061";

	/**	Temporary apostrophe marker. */

	protected static final String ap	= "\uE062";

	/**	Previous character of last text segment. */

	protected static String prevChar	= " ";

	/**	Soft tags. */

	protected static Set<String> softTags;

	/**	Jump tags. */

	protected static Set<String> jumpTags;

	/**	True for debugging output. */

	protected static boolean debug	= false;

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

		long processingTime	=
			( System.currentTimeMillis() - startTime + 999 ) / 1000;

								//	Terminate.

		terminate( filesProcessed , processingTime );
	}

	/**	Initialize.
	 */

	protected static boolean initialize( String[] args )
	{
								//	See if we have enough parameters.

		if ( args.length < 2 )
		{
			System.out.println( "Not enough parameters." );
			return false;
		}
								//	Load soft tags.
		try
		{
			softTags	= SetUtils.loadSet( args[ 0 ] , "utf-8" );
		}
		catch ( IOException e )
		{
			return false;
		}
								//	Load jump tags.
		try
		{
			jumpTags	= SetUtils.loadSet( args[ 1 ] , "utf-8" );
		}
		catch ( IOException e )
		{
			return false;
		}
								//	Get the output directory.

		outputDirectory	= args[ 2 ];

								//	Load contractions.
		contractions	=
			FixQuotes.loadContractions( "resources/contractions.txt" );

								//	Build contractions pattern.

		Pattern contractionsPattern	=
			FixQuotes.buildContractionsPattern( contractions );

								//	Get a contractions matcher.

		contractionsMatcher	=
			contractionsPattern.matcher( "" );

		return true;
	}

	/**	Process one file.
	 *
	 *	@param	xmlFileName		XML input file name.
	 */

	protected static void processOneFile( String xmlFileName )
	{
								//	Extract words from input text.

		currentDocNumber++;

		System.out.println(
			"Processing " + xmlFileName + " (" + currentDocNumber +
			"/" + docsToProcess + ")" );

								//	Load and parse XML document
								//	to DOM tree.
		try
		{
			long startTime	= System.currentTimeMillis();

								//	Load document to a string.
			String docText	=
				FileUtils.readTextFile( xmlFileName , "utf-8" );

								//	Convert existing &apos; to
								//	special marker.  We will convert
								//	the apostrophes back later.

			docText		= docText.replaceAll( "&apos;" , ap );

								//	Parse the XML text to a DOM tree.

			document	= DOMUtils.parseText( docText );

								//	Remember the DTD name.

			DocumentType docType	= document.getDoctype();

			String dtdName			= docType.getSystemId();

								//	Report document load and parse time.

			long processingTime		=
				( System.currentTimeMillis() - startTime + 999 ) / 1000;

			System.out.println
			(
				"   Document loaded and parsed in " +
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
								//	Get text root node.

			Node textRoot	=
				DOMUtils.getChild( document , "TEI" );

								//	Get text children.

			List<Node> textRootChildren	=
				DOMUtils.getChildren( textRoot , "text" );

			startTime	= System.currentTimeMillis();

								//	Traverse each child of the
								//	text node.

			for ( int i = 0 ; i < textRootChildren.size() ; i++ )
			{
				traverse( textRootChildren.get( i ) );
			}
								//	Clean up entity references.

			docText	= DOMUtils.saveToString( document , dtdName );

			docText	= docText.replaceAll( "&amp;ldquo"	, "&ldquo" );
			docText	= docText.replaceAll( "&amp;rdquo"	, "&rdquo" );
			docText	= docText.replaceAll( "&amp;lsquo"	, "&lsquo" );
			docText	= docText.replaceAll( "&amp;rsquo"	, "&rsquo" );
			docText	= docText.replaceAll( "&amp;apos"	, "&apos" );
			docText	= docText.replaceAll( ap			, "&apos;" );

								//	Report processing time.

			processingTime	=
				( System.currentTimeMillis() - startTime + 999 ) / 1000;

			System.out.println
			(
				"   Quotes fixed in " +
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
								//	Save updated xml to output file.

			String outputFileName	=
				new File( outputDirectory ,
					FileNameUtils.stripPathName(
						xmlFileName ) ).getCanonicalPath();

			FileUtils.writeTextFile(
				outputFileName , false , docText , "utf-8" );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			System.out.println( "   *** Failed" );
		}
	}

	/**	Traverse DOM tree and fix quotes.
	 *
	 *	@param	node		Root node of tree.
	 */

	protected static void traverse( Node node )
	{
								//	Get the node tag name.

		String nodeTag	= node.getNodeName();

								//	Remember the previous character.

		String savePrevChar	= prevChar;

								//	If it is a hard tag, set the previous
								//	character to a blank, since text
								//	does not carry over from one hard
								//	tag to another.

		if ( isHardTag( nodeTag ) )
		{
			savePrevChar	= " ";
			prevChar		= " ";
		}
								//	If it is a jump tag, set the
								//	previous character to a blank, since
								//	it does not carry over into a jump tag.
								//	However, we will restore the
								//	previous character string after the
								//	jump tag is processed.

		else if ( isJumpTag( nodeTag ) )
		{
			prevChar		= " ";
		}
								//	Process child nodes.

		NodeList children = node.getChildNodes();

		if ( children != null )
		{
			for ( int i = 0 ; i < children.getLength() ; i++ )
			{
				traverse( children.item( i ) );
			}
		}
								//	Get this node's type.

		int type = node.getNodeType();

								//	If we have a text node,
								//	extract its text and fix the
								//	quotes.

		if ( type == Node.TEXT_NODE )
		{
			Text textNode	= (Text)node;

								//	Get node text.

			String text		= textNode.getData();

								//	If we have at least one character
								//	of text ...

			if ( ( text != null ) && ( text.length() > 0 ) )
			{
								//	Remember last character of this
								//	text section before processing.

				String lastChar	= text.substring( text.length() - 1 );

								//	Prefix text with previous character
								//	and add a blank to the end for
								//	context.  Appending a blank isn't
								//	really correct for soft tabs --
								//	we actually want the next character
								//	following the end of the soft tag
								//	sequence -- but it's easier to just
								//	use a blank which works most of the
								//	time anyway.
								//
								//	Fix the quotes.
				text	=
					FixQuotes.repairQuotes
					(
						prevChar + text + " " ,
						contractionsMatcher ,
						contractions
					);
								//	Remove the first and last
								//	characters we added for context.

				text	= text.substring( 1 , text.length() - 1 );

								//	Update node text with revised
								//	quotes.

				textNode.setData( text );

								//	Set previous character for next
								//	text section to last character of
								//	this text section.

				prevChar	= lastChar;
			}
        }
        else
        {
								//	Restore previous character
								//	if this was a jump tag.

			prevChar	= savePrevChar;
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
			" files in " +
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

	/**	Is tag a soft tag?
	 *
	 *	@param	tag		The XML tag.
	 *
	 *	@return			true if tag is a soft tag.
	 */

	protected static boolean isSoftTag( String tag )
	{
		return
			softTags.contains( tag ) ||
			softTags.contains( tag.toLowerCase() );
	}

	/**	Is tag a jump tag?
	 *
	 *	@param	tag		The XML tag.
	 *
	 *	@return			true if tag is a jump tag.
	 */

	protected static boolean isJumpTag( String tag )
	{
		return
			jumpTags.contains( tag ) ||
			jumpTags.contains( tag.toLowerCase() );
	}

	/**	Is tag a hard tag?
	 *
	 *	@param	tag		The XML tag.
	 *
	 *	@return			true if tag is a hard tag.
	 */

	protected static boolean isHardTag( String tag )
	{
		return !( isSoftTag( tag ) || isJumpTag( tag ) );
	}

	/**	Allow overrides but not instantiation.
	 */

	protected FixXMLQuotes()
	{
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



