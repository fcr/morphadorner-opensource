package edu.northwestern.at.morphadorner.tools.namedentities;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;

import org.w3c.dom.*;
import org.w3c.dom.traversal.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.tokenizer.*;
import edu.northwestern.at.utils.gate.*;
import edu.northwestern.at.utils.xml.*;

/**	Adorn XML files with named entities.
 *
 *	<p>
 *	AdornWithNamedEntities adorns texts with named entities such as person,
 *	location, time, date, and organization.
 *	</p>
 *	<p>
 *	Usage:
 *	</p>
 *	<blockquote>
 *	<pre>
 *	java edu.northwestern.at.morphadorner.tools.namedentities.AdornWithNamedEntities outputdirectory input1.xml input2.xml ...
 *	</pre>
 *	</blockquote>
 *	<p>
 *	outputdirectory -- output directory to receive xml files adorned with named entities.<br />
 *	input*.xml -- input TEI XML files.<br />
 *	</p>
 *	<p>
 *	Note:  The named entity adorner does not always recognize entities which cross soft tags.
 *	Thus "&lt;hi&gt;Emma&lt;/hi&gt; Woodhouse" may be recognized as two separate entities.
 *	AdornedWithNamedEntities should be run on the input files before their
 *	submission to MorphAdorner.
 *	</p>
 */

public class AdornWithNamedEntities
{
	/**	DOM document. */

	protected static Document document;

	/**	# params before input file specs. */

	protected static final int INITPARAMS	= 1;

	/**	Number of documents to process. */

	protected static int docsToProcess		= 0;

	/**	Current document. */

	protected static int currentDocNumber	= 0;

	/**	Output directory. */

	protected static String outputDirectory;

	/**	Annie annotator. */

	protected static Annie annie;

	/**	Fixups list resource URL. */

	protected static String fixupsURL	= "resources/fixups.txt";

	/**	Fixups list. */

	protected static List<PatternReplacer> fixupsList	=
		ListFactory.createNewList();

	/**	TEI header element pattern. */

	protected static final String teiHeaderPattern	=
		"tei|tei\\.2|TEI|TEI\\.2";

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
		boolean result	= false;

								//	See if we have enough parameters.

		if ( args.length < 2 )
		{
			System.out.println( "Not enough parameters." );
			return result;
		}
								//	Get the output directory.

		outputDirectory	= args[ 0 ];

								//	Load fixups.

		result	= loadFixups();

								//	Initialize Annie.
		if ( result )
		{
			try
			{
				annie	= new Annie();
				result	= true;
			}
 			catch ( Exception e )
 			{
				e.printStackTrace();
 			}
		}

		return result;
	}

	/**	Load fixup definitions.
	 */

	protected static boolean loadFixups()
	{
		TextFile fixupsFile	=
			new TextFile
			(
				AdornWithNamedEntities.class.getResourceAsStream(
					fixupsURL ) ,
				"utf-8"
			);

		String[] fixups	= fixupsFile.toArray();

		for ( int i = 0 ; i < fixups.length ; i++ )
		{
			String fixupLine	= fixups[ i ].trim();

			if	( 	( fixupLine.length() > 0 ) &&
					( fixupLine.charAt( 0 ) != '#' )
				)
			{
				String[] fixup	= fixupLine.split( "\t" );

				if ( fixup.length == 2 )
				{
					fixupsList.add
					(
						new PatternReplacer( fixup[ 0 ] , fixup[ 1 ] )
					);
				}
			}
		}

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

								//	Load the XML document.
		try
		{
			long startTime	= System.currentTimeMillis();

								//	Parse the XML text to a DOM tree.

			document	= DOMUtils.parse( xmlFileName );

			long processingTime	=
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
								//	Find parent node for text node(s).

			Node textRoot	= findTextNodesParent( document );

								//	Get text children.

			List<Node> textRootChildren	=
				DOMUtils.findChildren( textRoot , "text|TEXT" );

			startTime		= System.currentTimeMillis();

								//	Traverse each text child and
								//	adorn each with named entities.

			for ( int i = 0 ; i < textRootChildren.size() ; i++ )
			{
				traverse( textRootChildren.get( i ) );
			}
								//	Convert adorned DOM document
								//	to text string.

			String docText		= DOMUtils.saveToString( document );

								//	Split document text string into
								//	header and body.

			String[] docParts	=
				splitDocumentText
				(
					docText ,
					"</teiHeader>|</temphead>|</TEMPHEAD>|</tempHead>"
				);
								//	Clean up entity references.

			docParts[ 1 ]	= docParts[ 1 ].replaceAll( "&lt;" , "<" );
			docParts[ 1 ]	= docParts[ 1 ].replaceAll( "&gt;" , ">" );

								//	Apply fixups to text body only.

			docParts[ 1 ]	= applyFixups( docParts[ 1 ] );

								//	Put document back together.

			docText			= docParts[ 0 ] + docParts[ 1 ];

								//	Report processing time.

			processingTime	=
				( System.currentTimeMillis() - startTime + 999 ) / 1000;

			System.out.println
			(
				"   Named entities added in " +
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
								//	Save updated text to file.

			String outputFileName	=
				new File( outputDirectory ,
					FileNameUtils.stripPathName(
						xmlFileName ) ).getCanonicalPath();

			FileUtils.createPathForFile( outputFileName );

			FileUtils.writeTextFile(
				outputFileName , false , docText , "utf-8" );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			System.out.println( "   *** Failed" );
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

		String[] fileNames	= FileNameUtils.
			expandFileNameWildcards( wildCards );

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
				processingTime ,
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

	/**	Traverse DOM tree and fix quotes.
	 *
	 *	@param	node		Root node of tree.
	 */

	protected static void traverse( Node node )
	{
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
								//	extract its text and annotate the
								//	named entities.

		if ( type == Node.TEXT_NODE )
		{
			Text textNode	= (Text)node;

								//	Get node text.

			String text		= textNode.getData();

								//	If we have at least one character
								//	of text ...

			if ( ( text != null ) && ( text.length() > 0 ) )
			{
								//	Update node text with revised
								//	quotes.

				text	= addNamedEntities( text );

				if ( text != null )
				{
					textNode.setData( text );
				}
			}
		}
	}

	/**	Adorn text with named entities.
	 *
	 *	@param	text	The text.
	 *
	 *	@return			The adorned text.
	 *					Null if annotation could not be done.
	 */

	protected static String addNamedEntities( String text )
	{
		return annie.adornText( text );
	}

	/**	Apply fixups.
	 *
	 *	@param	text	The text to which to apply fixups.
	 *
	 *	@return			The text after applying fixups.
	 */

	protected static String applyFixups( String text )
	{
		String result	= text;

								//	Apply basic character entity fixups.

		result	= result.replaceAll( "&amp;(\\w+);" , "&$1;" );
		result	= result.replaceAll( "&apos;" , "'" );
		result	= result.replaceAll( "&lt;" , "<" );
		result	= result.replaceAll( "&gt;" , ">" );
		result	= result.replaceAll( "&quot;" , "\"" );

								//	Apply other fixups.

		for ( int i = 0 ; i < fixupsList.size() ; i++ )
		{
			PatternReplacer fixup	= fixupsList.get( i );

			result	= fixup.replace( result );
		}

		return result;
	}

	/**	Split document text.
	 *
	 *	@param	docText		The document text.
	 *
	 *	@param	splitString	The regular expression string at which to
	 *						split the document.
	 *						If this appears more than once, the
	 *						document is split at the first appearance.
	 *
	 *	@return				Two element string array.
	 *						[0]	= document text up to
	 *						      first appearance of split string.
	 *							  Empty if split string not found.
	 *						[1]	= document text right after start of split
	 *							  string through end of document.
	 */

    protected static String[] splitDocumentText
    (
    	String docText ,
    	String splitString
    )
    {
		String[] result	= new String[ 2 ];

		Matcher matcher	= Pattern.compile( splitString ).matcher( docText );

		if ( matcher.find() )
		{
			int splitIndex	= matcher.start();

			result[ 0 ]		= docText.substring( 0 , splitIndex );
			result[ 1 ]		= docText.substring( splitIndex );
		}
		else
		{
			result[ 0 ]	= "";
			result[ 1 ]	= docText;
		}

		return result;
    }

	/**	Find parent of text nodes in a DOM document.
	 *
	 *	@param	document	The document.
	 *
	 *	@return				Node which is parent of the text nodes.
	 */

	protected static Node findTextNodesParent( Document document )
	{
								//	Get root element of document.

		Element rootNode		= document.getDocumentElement();

								//	Look for TEI node of some kind.
		Element teiNode;

		if ( rootNode.getTagName().matches( teiHeaderPattern ) )
		{
			teiNode	= rootNode;
		}
		else
		{
			teiNode	= DOMUtils.findChild( rootNode , teiHeaderPattern );
        }
								//	Look for EEBO node.

		Element eeboNode		=
			DOMUtils.findChild( rootNode , "eebo|EEBO" );

		Element groupTextRoot	= null;

								//	See if we have EEBO GROUP child node.

		if ( eeboNode != null )
		{
			groupTextRoot	= DOMUtils.findChild( eeboNode, "group|GROUP" );
        }
								//	If TEI, text node parent is
								//	TEI node.

								//	If EEBO, text node parent is either
								//	EEBO or GROUP.

		Element textParent	= null;

		if ( groupTextRoot != null )
		{
			textParent	= groupTextRoot;
		}
		else
		{
			textParent	= eeboNode;

			if ( textParent == null )
			{
				textParent	= teiNode;
			}
		}
								//	Return parent for text nodes.
		return textParent;
	}

	/**	Allow overrides but not instantiation.
	 */

	protected AdornWithNamedEntities()
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



