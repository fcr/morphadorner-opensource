package edu.northwestern.at.utils.corpuslinguistics.inputter;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.net.URL;
import java.util.List;

import edu.northwestern.at.utils.corpuslinguistics.tokenizer.*;
import edu.northwestern.at.utils.PatternReplacer;

/**	Interface for retrieving text from a possibly segmented input source.
 */

public interface TextInputter
{
	/**	Reads text from a URL.
	 *
	 *	@param	url				URL from which to read text.
	 *	@param	encoding		Text encoding.
	 *
	 *	@throws	IOException		If an error occurs.
	 */

	public void loadText( URL url , String encoding )
		throws Exception;

	/**	Reads text from a URL using a specified XML schema.
	 *
	 *	@param	url					URL from which to read text.
	 *	@param	encoding			Text encoding.
	 *	@param	xmlSchemaURI		String URI specifying Xml schema.
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
		throws Exception;

	/**	Returns number of text segments.
	 *
	 *	@return		Number of text segments.
	 */

	public int getSegmentCount();

	/**	Returns name of specified segment.
	 *
	 *	@param	segmentNumber	The segment number (starts at 0).
	 *
	 *	@return					The name for the specified
	 *							segment number, or null if the
	 *							segment number is invalid.
	 */

	public String getSegmentName( int segmentNumber );

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

	public String getSegmentText( int segmentNumber );

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

	public String getSegmentText( String segmentName );

	/**	Updates specified segment of loaded text.
	 *
	 *	@param	segmentNumber	The segment number (starts at 0).
	 *	@param	segmentText		The updated segment text.
	 */

	public void setSegmentText( int segmentNumber , String segmentText );

	/**	Returns specified segment of loaded text.
	 *
	 *	@param	segmentName		The segment name.
	 *	@param	segmentText		The updated segment text.
	 */

	public void setSegmentText( String segmentName , String segmentText );

	/**	Updates specified segment of loaded text from file.
	 *
	 *	@param	segmentNumber	The segment number (starts at 0).
	 *	@param	segmentTextFile	The file containing the updated segment text.
	 */

	public void setSegmentText( int segmentNumber , File segmentTextFile );

	/**	Returns specified segment of loaded text.
	 *
	 *	@param	segmentName		The segment name.
	 *	@param	segmentTextFile	The file containing the updated segment text.
	 */

	public void setSegmentText( String segmentName , File segmentTextFile );

	/**	Enable gap element fixer.
	 *
	 *	@param	fixGaps		true to fix gap tags.
	 */

	public void enableGapFixer( boolean fixGaps );

	/**	Enable orig element fixer.
	 *
	 *	@param	fixOrigs		true to fix orig tags.
	 */

	public void enableOrigFixer( boolean fixOrigs );

	/**	Enable split words fixer.
	 *
	 *	@param	fixSplitWords		true to fix selected split words.
	 *	@param	patternReplacers	Patterns for fixing split words.
	 */

	public void enableSplitWordsFixer
	(
		boolean fixSplitWords ,
		List<PatternReplacer> patternReplacers
	);
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



