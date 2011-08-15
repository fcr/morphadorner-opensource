package edu.northwestern.at.morphadorner;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;

import edu.northwestern.at.utils.logger.*;

/**	MorphAdorner log manager.
 *
 *	<p>
 *	This static class wraps a singleton instance of a
 *	{@link edu.northwestern.at.utils.logger.BaseLogger}
 *	for the class "edu.northwestern.at.morphadorner".
 *	</p>
 */

public class MorphAdornerLogger
{
	/**	The logger. */

	protected static Logger logger	= null;

	/**	Initializes the logger.
	 *
	 *	@param	configFileName		Log configuration file name.
	 *	@param	logFileDirectory	Directory into which to write log.
	 *
	 *	<p>
	 *	Reads the log configuration file and configures
	 *	the logger.
	 *  </p>
	 *
	 *	@throws	Exception
	 */

	public static void initialize
	(
		String configFileName ,
		String logFileDirectory
	)
		throws FileNotFoundException, IOException
	{
		logger	=
			new BaseLogger
			(
				"edu.northwestern.at.MorphAdorner" ,
				logFileDirectory ,
				configFileName
			);
	}

	/**	Terminates the logger.
	 */

	public static void terminate()
	{
		logger.terminate();
	}

	/**	Logs Debug message.
	 *
	 *	@param	str			Log message.
	 */

	public static void logDebug( String str )
	{
		logger.logDebug( str );
	}

	/**	Logs Info message.
	 *
	 *	@param	str			Log message.
	 */

	public static void logInfo( String str )
	{
		logger.logInfo( str );
	}

	/**	Logs error message.
	 *
	 *	@param	str			Log message.
	 */

	public static void logError( String str )
	{
		logger.logError( str );
	}

	/**	Get the logger.
	 *
	 *	@return		The logger.
	 */

	public static Logger getLogger()
	{
		return logger;
	}

	/**	Print a string to log file.
	 *
	 *	@param	formatString	Resource string name to print.
	 */

	public static void println( String formatString )
	{
		MorphAdornerLogger.logInfo
		(
			MorphAdornerSettings.getString( formatString )
		);
	}

	/**	Print a string to log file.
	 *
	 *	@param	formatString	Resource string name to print.
	 *	@param	value			String to output in format fornatString
	 */

	public static void println
	(
		String formatString ,
		String value
	)
	{
		StringBuilder sb	= new StringBuilder();

		new Formatter( sb ).format
		(
			MorphAdornerSettings.getString( formatString ) ,
			value
		);

		MorphAdornerLogger.logInfo( sb.toString() );
	}

	/**	Print formatted elements to log file.
	 *
	 *	@param	formatString	Format string name.
	 *	@param	objects			Objects to output in format fornatString
	 */

	public static void println( String formatString , Object[] objects )
	{
		StringBuilder sb	= new StringBuilder();

		new Formatter( sb ).format
		(
			MorphAdornerSettings.getString( formatString ) ,
			objects
		);

		MorphAdornerLogger.logInfo( sb.toString() );
	}

	/** Allow override but not instantiation. */

	protected MorphAdornerLogger()
	{
		throw new UnsupportedOperationException();
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



