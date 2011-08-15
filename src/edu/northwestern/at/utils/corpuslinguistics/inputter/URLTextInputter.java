package edu.northwestern.at.utils.corpuslinguistics.inputter;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.*;
import java.text.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.html.*;

/**	Text inputter which reads text from a URL.
 *
 *	<p>
 *	Strips tags naively from files which appear to contain HTML text.
 *	</p>
 */

public class URLTextInputter
	extends IsCloseableObject
	implements TextInputter
{
	/**	The loaded text. */

	protected String loadedText	= null;

	/**	Create URL text inputter. */

	public URLTextInputter()
	{
		super();
	}

	/**	Loads text from a URL into a string.
	 *
	 *	@param	url				URL from which to read text.
	 *	@param	encoding		Text encoding.
	 *
	 *	@throws	IOException		If an output error occurs.
	 */

	public void loadText( URL url , String encoding )
		throws IOException
	{
		BufferedReader reader	=
			new BufferedReader
			(
				new UnicodeReader
				(
					url.openStream() ,
					encoding
				)
			);

        StringBuffer loadedTextBuffer	= new StringBuffer();

		String line	= reader.readLine();

		while ( line != null )
		{
			loadedTextBuffer.append( line );
			loadedTextBuffer.append( " " );
			line = reader.readLine();
		}

		reader.close();

		loadedText	= loadedTextBuffer.toString();

								//	Remove HTML/XML tags.

		if ( HTMLUtils.isHTMLTaggedText( loadedText ) )
		{
			loadedText	= HTMLUtils.stripHTMLTags( loadedText );
		}
	}

	/**	Loads text from a URL into a string.
	 *
	 *	@param	url					URL from which to read text.
	 *	@param	encoding			Text encoding.
	 *	@param	xmlSchemaURI		XML schema (ignored).
	 *
	 *	@throws	IOException			If an output error occurs.
	 */

	public void loadText
	(
		URL url ,
		String encoding ,
		String xmlSchemaURI
	)
		throws IOException
	{
		loadText( url , encoding );
	}

	/**	Returns number of text segments.
	 *
	 *	@return		Number of text segments.
	 */

	public int getSegmentCount()
	{
		return 1;
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

		if ( segmentNumber == 0 )
		{
			result	= "text";
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

		if ( segmentNumber == 0 )
		{
			result	= loadedText;
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

		if ( ( segmentName != null ) && ( segmentName.equals( "text" ) ) )
		{
			result	= loadedText;
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
		if ( segmentNumber == 0 )
		{
			this.loadedText	= segmentText;
		}
	}

	/**	Returns specified segment of loaded text.
	 *
	 *	@param	segmentName		The segment name.
	 *	@param	segmentText		The updated segment text.
	 */

	public void setSegmentText( String segmentName , String segmentText )
	{
		if ( ( segmentName != null ) && ( segmentName.equals( "text" ) ) )
		{
			this.loadedText	= segmentText;
		}
	}

	/**	Updates specified segment of loaded text from file.
	 *
	 *	@param	segmentNumber	The segment number (starts at 0).
	 *	@param	segmentTextFile	The file containing the updated segment text.
	 */

	public void setSegmentText( int segmentNumber , File segmentTextFile )
	{
		if ( segmentNumber == 0 )
		{
			try
			{
				this.loadedText	=
					FileUtils.readTextFile( segmentTextFile , "utf-8" );
			}
			catch ( Exception e )
			{
			}
		}
	}

	/**	Returns specified segment of loaded text.
	 *
	 *	@param	segmentName		The segment name.
	 *	@param	segmentTextFile	The file containing the updated segment text.
	 */

	public void setSegmentText( String segmentName , File segmentTextFile )
	{
		if ( ( segmentName != null ) && ( segmentName.equals( "text" ) ) )
		{
			try
			{
				this.loadedText	=
					FileUtils.readTextFile( segmentTextFile , "utf-8" );
			}
			catch ( Exception e )
			{
			}
		}
	}

	/**	Enable gap element fixer.
	 *
	 *	@param	fixGaps		true to fix gap tags.
	 *
	 *	<p>
	 *	No-op here.
	 *	</p>
	 */

	public void enableGapFixer( boolean fixGaps )
	{
	}

	/**	Enable orig element fixer.
	 *
	 *	@param	fixOrig		true to fix orig tags.
	 *
	 *	<p>
	 *	No-op here.
	 *	</p>
	 */

	public void enableOrigFixer( boolean fixOrig )
	{
	}

	/**	Enable split words fixer.
	 *
	 *	@param	fixSplitWords		true to fix selected split words.
	 *	@param	patternReplacers	Patterns for fixing split words.
	 *
	 *	<p>
	 *	No-op here.
	 *	</p>
	 */

	public void enableSplitWordsFixer
	(
		boolean fixSplitWords ,
		List<PatternReplacer> patternReplacers
	)
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



