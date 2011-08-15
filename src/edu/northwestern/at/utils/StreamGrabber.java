package edu.northwestern.at.utils;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;
import edu.northwestern.at.utils.ListFactory;

/**	Grabs output from a stream and stores the output in a list of text lines.
 *
 *	<p>
 *	StreamGrabber asynchronously retrieves the output from an
 *	existing input stream in a thread, and stores the output as an ArrayList
 *	of Strings.  StreamGrabber is useful for retrieving the
 *	output from multiple streams simultaneously.  For example,
 *	StreamGrabber is used by ExecUtils.ExecAndWait to retrieve
 *	standard output and standard error from an executed program.
 *	You provide the existing input stream and character encoding of the
 *	input stream when creating the StreamGrabber.
 *	</p>
 */

public class StreamGrabber extends Thread
{
	 /** Input stream to read.
	 */

	protected InputStream inputStream	= null;

	/**	Encoding for input stream.  Default is utf-8.
	 */

	protected String encoding			= "utf-8";

	/**	ArrayList of text lines read from stream.
	 */

	protected List<String> textLinesList	=
		ListFactory.createNewList();

	/**	Exception which occurred during grabber execution.  Null if none.
	 */

	protected Exception exception		= null;

	/**	Create a stream grabber.
	 *
	 *	@param	inputStream		The stream whose lines
	 *							should be retrieved.
	 */

	public StreamGrabber( InputStream inputStream )
	{
		this.inputStream	= inputStream;
	}

	/**	Create a stream grabber with specified character encoding.
	 *
	 *	@param	inputStream		The stream whose lines
	 *							should be retrieved.
	 *
	 *	@param	encoding		Character encoding for stream.
	 */

	public StreamGrabber( InputStream inputStream , String encoding )
	{
		this.inputStream	= inputStream;
		this.encoding		= encoding;
	}

    /**	Run the stream grabber thread.
     */

	public void run()
	{
		BufferedReader bufferedReader	= null;

		try
		{
								//	Create input stream reader for
								//	specified stream.

			UnicodeReader inputStreamReader	=
				new UnicodeReader( inputStream , encoding );

								//	Wrap reader with a buffered reader
								//	so we can read lines.

			bufferedReader		=
				new BufferedReader( inputStreamReader );

								//	While the input stream still has
								//	text lines, read and store them
								//	in an array list.

			String line	=	null;

			while ( ( line = bufferedReader.readLine() ) != null )
			{
				textLinesList.add( line );
			}
		}
		catch ( Exception e )
		{
			this.exception	= e;
		}
		finally
		{
								//	Close reader when there are no
								//	more text lines to read.
			try
			{
				bufferedReader.close();
			}
			catch ( Exception ignored )
			{
			}
		}
	}

	/**	Get stored exception, if any.
	 *
	 *	@return		Stored exception if any occurred during grabber execution.
	 */

	public Exception getException()
	{
		return exception;
	}

	/**	Get store text lines from the stream.
	 *
	 *	@return		Output text lines stored as an ArrayList of String.
	 */

	public List<String> getGrabbedTextLines()
	{
		return textLinesList;
	}
}

