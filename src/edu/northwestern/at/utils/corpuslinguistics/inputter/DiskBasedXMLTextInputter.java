package edu.northwestern.at.utils.corpuslinguistics.inputter;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.text.*;
import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.math.ArithUtils;

/**	Text inputter which reads text from a TEI or EEBO XML fileL.
 *
 *	<p>
 *	The XML file is divided into smaller sections which are stored
 *	in temporary disk files.  MorphAdorner uses a modified XGTagger
 *	interface to adorn each section of text separately, and then merge
 *	the results to produce the final adorned XML output.
 *	</p>
 *
 *	<p>
 *	In this class, the segmentMap inherited from
 *	{@link edu.northwestern.at.utils.corpuslinguistics.inputter.XMLTextInputter}
 *	maps a segment name to the name of the temporary disk file which
 *	holds the segment text.
 *	</p>
 */

public class DiskBasedXMLTextInputter
	extends XMLTextInputter
	implements TextInputter
{
	/**	Create disk-based XML text inputter. */

	public DiskBasedXMLTextInputter()
	{
		super();
	}

	/**	Updates specified segment of loaded text from file.
	 *
	 *	@param	segmentNumber	The segment number (starts at 0).
	 *	@param	segmentTextFile	The file containing the updated segment text.
	 */

	public void setSegmentText( int segmentNumber , File segmentTextFile )
	{
		if	( 	( segmentNumber >= 0 ) &&
				( segmentNumber < segmentNames.size() )
			)
		{
			segmentMap.put
			(
				(String)segmentNames.get( segmentNumber ) ,
				segmentTextFile.getAbsolutePath()
			);
		}
	}

	/**	Updates specified segment of loaded text from file.
	 *
	 *	@param	segmentName		The segment name.
	 *	@param	segmentTextFile	The file containing the updated segment text.
	 */

	public void setSegmentText( String segmentName , File segmentTextFile )
	{
		if	(	( segmentName != null ) &&
				( segmentMap.containsKey( segmentName ) )
			)
		{
			segmentMap.put
			(
				segmentName ,
				segmentTextFile.getAbsolutePath()
			);
		}
	}

	/**	Get segment text from disk.
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
			String fileName	= (String)segmentMap.get( segmentName );

			try
			{
				result			=
					FileUtils.readTextFile
					(
						fileName ,
						encoding
					);
			}
			catch ( IOException e )
			{
			}
		}

		return result;
	}

	/**	Put segment text to disk.
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
		String fileName	= null;

		if ( segmentMap.containsKey( segmentName ) )
		{
			fileName	= (String)segmentMap.get( segmentName );
		}
		else
		{
			try
			{
				File file	= File.createTempFile( "mad" , null );

				file.deleteOnExit();

				fileName	= file.getAbsolutePath();

				segmentMap.put( segmentName , fileName );
			}
			catch ( Exception e )
			{
			}
		}

		if ( fileName != null )
		{
			try
			{
				FileUtils.writeTextFile
				(
					fileName ,
					false ,
					segmentText.replaceAll( "[\r\n]" , " " ) ,
					encoding
				);
			}
			catch ( IOException e )
			{
			}
		}
	}

	/**	Close inputter.
	 */

	public void close()
	{
								//	Erase temporary files used to hold
								//	segment data.

		if ( segmentMap != null )
		{
			Iterator<String> iterator	= segmentMap.keySet().iterator();

			while ( iterator.hasNext() )
			{
				String fileName	= iterator.next();

				FileUtils.deleteFile( fileName );
			}
		}

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



