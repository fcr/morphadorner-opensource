package edu.northwestern.at.utils.corpuslinguistics.inputter;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.text.*;
import java.util.*;

import org.jdom.*;
import org.jdom.contrib.schema.*;
import org.jdom.input.*;
import org.jdom.output.*;
import org.jdom.transform.*;

import org.xml.sax.SAXException;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.PatternReplacer;
import edu.northwestern.at.utils.math.ArithUtils;
import edu.northwestern.at.utils.xml.*;

/**	Text inputter which reads text from a TEI or EEBO XML file.
 *
 *	<p>
 *	The XML file can be divided into smaller sections which are stored
 *	in a map.  MorphAdorner uses a modified XGTagger interface to adorn each
 *	section of text separately, and then merge the results to produce
 *	the final adorned XML output.
 *	</p>
 */

public class XMLTextInputter
	extends IsCloseableObject
	implements TextInputter
{
	/**	Map which holds segmented XML text.
	 *
	 *	<p>
	 *	The key is the segment name.  The value is the segment data.
	 *	The value may be something else in subclasses.
	 *	</p>
	 */

	protected Map<String, Object> segmentMap	=
		new TreeMap<String, Object>();

	/**	Text ID number for generated XML segments.
	 */

	protected int textID		= 0;

	/**	Segment names. */

	protected List<String> segmentNames	= ListFactory.createNewList();

	/**	Text encoding. */

	protected String encoding	= "utf-8";

	/**	True to split text body into segments. */

	protected boolean splitText	= true;

	/**	True to fix <gap> elements. */

	protected boolean fixGaps	= true;

	/**	True to fix <orig> elements. */

	protected boolean fixOrigs	= true;

	/**	True to fix selected split words. */

	protected boolean fixSplitWords	= false;

	/**	Pattern replacers for fixing split words. */

	protected List<PatternReplacer> fixSplitWordsPatternReplacers = null;

	/**	TEI header element pattern. */

	protected final String teiHeaderPattern	= "tei|tei\\.2|TEI|TEI\\.2";

	/**	The document text object. */

	protected Document document;

	/**	Create XML text inputter. */

	public XMLTextInputter()
	{
		super();
	}

	/**	Loads text from a URL into a map.
	 *
	 *	@param	url				URL from which to read text.
	 *	@param	encoding		Text encoding.
	 *	@param	schemaURI		XML schema URI.  Null if none.
	 *
	 *	@throws	IOException		If an I/O error occurs.
	 */

	protected void doLoadText( URL url , String encoding , String schemaURI )
		throws	JDOMException,
				IOException,
				URISyntaxException ,
				org.xml.sax.SAXException
	{
		SAXBuilder builder	= new SAXBuilder( false );

								//	Load document text.

		WhitespaceTrimmingBufferedReader bufferedReader	=
			new WhitespaceTrimmingBufferedReader
			(
				new UnicodeReader
				(
					url.openStream() ,
					"utf-8"
				)
			);

		document	= builder.build( bufferedReader );

		bufferedReader.close();

								//	If no document type was found in
								//	the file, and a schema was
								//	provided in the MorphAdorner settings,
								//	validate against the schema.

		if ( document.getDocType() == null )
		{
			if ( ( schemaURI != null ) && ( schemaURI.length() > 0 ) )
			{
				JDOMUtils.validateDocument( document , schemaURI );
			}
		}
								//	Find text root.

		Element etsNode	= document.getRootElement();

		Element teiNode;

		if ( etsNode.getName().matches( teiHeaderPattern ) )
		{
			teiNode	= etsNode;
		}
		else
		{
			teiNode	= findChild( etsNode , teiHeaderPattern );
		}

		Element eeboNode		= etsNode.getChild( "eebo" );

		if ( eeboNode == null )
		{
			eeboNode	= etsNode.getChild( "EEBO" );
		}

		Element groupTextRoot	= null;

		if ( eeboNode != null )
		{
			groupTextRoot	= eeboNode.getChild( "group" );

			if ( groupTextRoot == null )
			{
				groupTextRoot	= eeboNode.getChild( "GROUP" );
			}
		}

		Element textParent	= null;
		Element textNode	= null;

		if ( groupTextRoot != null )
		{
			textParent	= eeboNode;
			textNode	= groupTextRoot;
		}
		else
		{
			textParent	= eeboNode;

			if ( textParent == null )
			{
				textParent	= teiNode;
			}

			textNode	= findChild( textParent , "text|TEXT" );
		}
								//	Fix <gap> elements.
		if ( fixGaps )
		{
			GapFixer.fixGaps( document );
		}
								//	Fix <orig> elements.

		if ( fixOrigs )
		{
			OrigFixer.fixOrigs( document );
		}
								//	Output each child of the text
								//	portion to a separate map entry.

		writeChildren( textNode , "text" , splitText );

								//	Output remainder of text portion.

		org.jdom.output.Format format	=
			org.jdom.output.Format.getRawFormat();

		XMLOutputter xmlOut	= new XMLOutputter( format );

		putSegment( "text" , xmlOut.outputString( textNode ) );

								//	Delete text portion.

		textParent.removeContent( textNode );

								//	Output remainder = header portion.

		putSegment( "head" , xmlOut.outputString( document ) );

								//	Get list of entry names.

		Iterator<String> iterator	= segmentMap.keySet().iterator();

		while ( iterator.hasNext() )
		{
			segmentNames.add( iterator.next() );
		}
							//	Save text encoding.

		if ( ( encoding != null ) && ( encoding.length() > 0 ) )
		{
			this.encoding	= encoding;
		}
								//	Get the document type, if any.

		DocType docType		= document.getDocType();

								//	Get the DTD name URI.

		if ( docType != null )
		{
			URI uri	= new URI( docType.getSystemID() );

								//	If DTD is a local file,
								//	copy it to the temporary files
								//	directory.

			String uriScheme	= uri.getScheme();

			if	(	( uriScheme == null ) ||
					( uriScheme.equalsIgnoreCase( "file" ) )
				)
			{
								//	Get path of original document.

				String docPath	= url.getPath();

								//	Get path for DTD.

				String uriPath	= uri.getPath();

								//	If DTD path is not absolute,
								//	get its absolute path
								//	relative to the original
								//	document directory.

				File sourceFile	= new File( uriPath );

				if ( !sourceFile.isAbsolute() )
				{
					sourceFile		=
						new File
						(
							new File( docPath ).getParent() ,
							sourceFile.getPath()
						);
				}
								//	Create file name for DTD copy
								//	in temporary files directory.

				File destFile	=
					new File
					(
						FileUtils.getTemporaryFilesDirectory() ,
						sourceFile.getName()
					);

				FileUtils.copyFile
				(
					sourceFile.getAbsolutePath() ,
					destFile.getAbsolutePath()
				);

				destFile.deleteOnExit();
			}
		}
	}

	/**	Find child node name matching regular expression.
	 *
	 *	@param	parent		Node whose child we want.
	 *	@param	namePat	Regular expression for child name.
	 *
	 *	@return				The first child whose name matches
	 *							the specified namePat, or null if none
	 *							matches.
	 */

	public Element findChild( Element parent , String namePat )
	{
		Element result	= null;

		if ( parent != null )
		{
			List children	= parent.getChildren();

			for ( int i = 0 ; i < children.size() ; i++ )
			{
				Element child	= (Element)children.get( i );

				if ( child.getName().matches( namePat ) )
				{
					result	= child;
					break;
				}
			}
		}

		return result;
	}

	/**	Reads text from a URL into a string.
	 *
	 *	@param	url				URL from which to read text.
	 *	@param	encoding		Text encoding.
	 *
	 *	@throws	Exception		If an error occurs.
	 */

	public void loadText( URL url , String encoding )
		throws Exception
	{
		String result	= "";

		try
		{
			doLoadText( url , encoding , null );
		}
		catch ( JDOMException e )
		{
		}
		catch ( URISyntaxException e )
		{
		}
	}

	/**	Reads text from a URL using a specified XML schema.
	 *
	 *	@param	url					URL from which to read text.
	 *	@param	encoding			Text encoding.
	 *	@param	xmlSchemaURI		String URI specifying XML schema.
	 *
	 *	@throws	Exception			If an error occurs.
	 *
	 *	<p>
	 *	The schema and schema type should be ignored when the input
	 *	is not an XML file.
	 *	</p>
	 */

	public void loadText
	(
		URL url ,
		String encoding ,
		String xmlSchemaURI
	)
		throws Exception
	{
		String result	= "";

		try
		{
			doLoadText( url , encoding , xmlSchemaURI );
		}
		catch ( JDOMException e )
		{
		}
		catch ( URISyntaxException e )
		{
		}
	}

	/**	Returns number of text segments.
	 *
	 *	@return		Number of text segments.
	 */

	public int getSegmentCount()
	{
		return segmentNames.size();
	}

	/**	Returns name of specified segment.
	 *
	 *	@param	segmentNumber	The segment number (starts at 0).
	 *
	 *	@return					The name for the specified
	 *							segment number, or null if the
	 *							segment number is invalid.
	 */

	public String getSegmentName( int segmentNumber )
	{
		String result	= null;

		if	(	( segmentNumber >= 0 ) &&
				( segmentNumber < segmentNames.size() )
			)
		{
			result	= (String)segmentNames.get( segmentNumber );
		}

		return result;
	}

	/**	Returns specified segment of loaded text.
	 *
	 *	@param	segmentNumber	The segment number (starts at 0).
	 *
	 *	@return					The text for for the specified
	 *							segment number, or null if the
	 *							segment number is invalid.  The
	 *							returned text may be an empty string
	 *							if the segment number is valid but
	 *							the segment contains no text.
	 */

	public String getSegmentText( int segmentNumber )
	{
		String result	= null;

		if	( 	( segmentNumber >= 0 ) &&
				( segmentNumber < segmentNames.size() )
			)
		{
			result	= "";

			try
			{
				result	=
					getSegment
					(
						(String)segmentNames.get( segmentNumber )
					);
			}
			catch ( Exception IOException )
			{
			}
		}

		return result;
	}

	/**	Returns specified segment of loaded text.
	 *
	 *	@param	segmentName		The segment name.
	 *
	 *	@return					The text for for the specified
	 *							segment name, or null if the
	 *							segment name is invalid.  The
	 *							returned text may be an empty string
	 *							if the segment name is valid but
	 *							the segment contains no text.
	 */

	public String getSegmentText( String segmentName )
	{
		String result	= null;

		if	(	( segmentName != null ) &&
				( segmentMap.containsKey( segmentName ) ) )
		{
			result	= getSegment( segmentName );
		}

		return result;
	}

	/**	Updates specified segment of loaded text.
	 *
	 *	@param	segmentNumber	The segment number (starts at 0).
	 *	@param	segmentText		The updated segment text.
	 */


	public void setSegmentText( int segmentNumber , String segmentText )
	{
		if	( 	( segmentNumber >= 0 ) &&
				( segmentNumber < segmentNames.size() )
			)
		{
			putSegment
			(
				(String)segmentNames.get( segmentNumber ) ,
				segmentText
			);
		}
	}

	/**	Updates specified segment of loaded text.
	 *
	 *	@param	segmentName		The segment name.
	 *	@param	segmentText		The updated segment text.
	 */

	public void setSegmentText( String segmentName , String segmentText )
	{
		if	(	( segmentName != null ) &&
				( segmentMap.containsKey( segmentName ) ) )
		{
			putSegment( segmentName , segmentText );
		}
	}

	/**	Updates specified segment of loaded text from file.
	 *
	 *	@param	segmentNumber	The segment number (starts at 0).
	 *	@param	segmentTextFile	The file containing the updated segment text.
	 */

	public void setSegmentText( int segmentNumber , File segmentTextFile )
	{
		try
		{
			setSegmentText
			(
				segmentNumber ,
				FileUtils.readTextFile( segmentTextFile , "utf-8" )
			);
		}
		catch ( Exception e )
		{
		}
	}

	/**	Updates specified segment of loaded text from file.
	 *
	 *	@param	segmentName		The segment name.
	 *	@param	segmentTextFile	The file containing the updated segment text.
	 */

	public void setSegmentText( String segmentName , File segmentTextFile )
	{
		try
		{
			setSegmentText
			(
				segmentName ,
				FileUtils.readTextFile( segmentTextFile , "utf-8" )
			);
		}
		catch ( Exception e )
		{
		}
	}

	/**	Get next text ID.
	 *
	 *	@return		The next text ID.
	 */

	protected int getNextTextID()
	{
		return ++textID;
	}

	/**	Store children of a DOM element.
	 *
	 *	@param	element			The DOM element to store.
	 *	@param	baseFileName	The base file name for entry names
	 *								generated from the DOM element.
	 *	@param	splitText		True to split body text into segments.
	 */

	protected void writeChildren
	(
		Element element ,
		String baseFileName ,
		boolean splitText
	)
	{
		if ( element == null ) return;

		NumberFormat formatter	= NumberFormat.getInstance();

		formatter.setGroupingUsed( false );
		formatter.setMinimumIntegerDigits( 5 );

		org.jdom.output.Format format	=
			org.jdom.output.Format.getRawFormat();

		XMLOutputter xmlOut	= new XMLOutputter( format );

		List textChildren	= element.getChildren();

		while ( textChildren.size() > 0 )
		{
			Element child	= (Element)textChildren.get( 0 );

			int nextID		= getNextTextID();

			if ( splitText && child.getName().equalsIgnoreCase( "body" ) )
			{
				writeChildren
				(
					child ,
					baseFileName ,
					splitText
				);
			}

			String segmentText	= xmlOut.outputString( child );

			if ( fixSplitWords )
			{
				segmentText	=
					XMLTextReplacer.performReplacements
					(
						segmentText ,
						fixSplitWordsPatternReplacers
					);
			}

			putSegment
			(
				baseFileName + formatter.format( nextID ) ,
				segmentText
			);

			element.removeContent( child );
		}
	}

	/**	Get segment text.
	 *
	 *	@param	segmentName		Segment name.

	 *	@return					Segment text.
	 */

	protected String getSegment
	(
		String segmentName
	)
	{
		String result	= "";

		if ( segmentMap.containsKey( segmentName ) )
		{
			result	= (String)segmentMap.get( segmentName );
		}

		return result;
	}

	/**	Save segment text.
	 *
	 *	@param	segmentName		Segment name.
	 *	@param	segmentText		Segment text.
	 */

	protected void putSegment
	(
		String segmentName ,
		String segmentText
	)
	{
		segmentMap.put(
			segmentName , segmentText.replaceAll( "[\r\n]" , " " ) );
	}

	/**	Enable gap element fixer.
	 *
	 *	@param	fixGaps		true to fix gap tags.
	 */

	public void enableGapFixer( boolean fixGaps )
	{
		this.fixGaps	= fixGaps;
	}

	/**	Enable orig element fixer.
	 *
	 *	@param	fixOrigs			true to fix orig tags.
	 */

	public void enableOrigFixer( boolean fixOrigs )
	{
		this.fixOrigs			= fixOrigs;
	}

	/**	Enable split words fixer.
	 *
	 *	@param	fixSplitWords		true to fix selected split words.
	 *	@param	patternReplacers	Patterns for fixing split words.
	 */

	public void enableSplitWordsFixer
	(
		boolean fixSplitWords ,
		List<PatternReplacer> patternReplacers
	)
	{
		this.fixSplitWords	=
			fixSplitWords &&
			( patternReplacers != null ) &&
			( patternReplacers.size() > 0 );

		this.fixSplitWordsPatternReplacers	= patternReplacers;
	}

	/**	Close inputter.
	 */

	public void close()
	{
		segmentMap.clear();
		segmentNames.clear();

		segmentMap		= null;
		segmentNames	= null;

		document		= null;

		super.close();
	}

	/**	Finalize,
	 */

	public void finalize()
		throws Throwable
	{
		close();

		super.finalize();
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



