package edu.northwestern.at.utils.logger;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;

import edu.northwestern.at.utils.DebugUtils;

/** Simple logger which outputs log messages to standard error.
 */

public class StandardErrorLogger implements Logger
{
	/** True if logger enabled. */

	protected  boolean loggerEnabled = false;

	/**	Error level strings for display. */

	protected static final String[] errorLevelStrings	=
		new String[]{ "FATAL" , "ERROR" , "WARN " , "INFO " , "DEBUG" };

	/**	Log message format. */

	/** Create a standard error logger.
	 */

	public StandardErrorLogger()
	{
		loggerEnabled = true;
	}

	/** Terminates the logger.
	 */

	public void terminate()
	{
		loggerEnabled = false;
	}

	/** Logs a message at the DEBUG level.
	 *
	 *	@param	str			Log message.
	 */

	public void logDebug( String str )
	{
		log( LoggerConstants.DEBUG , str );
	}

	/** Logs a message at the INFO level.
	 *
	 *	@param	str			Log message.
	 */

	public void logInfo( String str )
	{
		log( LoggerConstants.INFO , str );
	}

	/** Logs a message at the WARN level.
	 *
	 *	@param	str			Log message.
	 */

	public void logWarning( String str )
	{
		log( LoggerConstants.WARN , str );
	}

	/** Logs a message at the ERROR level.
	 *
	 *	@param	str			Log message.
	 */

	public void logError( String str )
	{
		log( LoggerConstants.ERROR , str );
	}

	/** Logs a error message with a stack trace.
	 *
	 *	@param	str		Log message.
	 *
	 *	@param	t		Throwable.
	 */

	public void logError( String str , Throwable t )
	{
		log( LoggerConstants.ERROR , str , t );
	}

	/** Logs a message at the FATAL level.
	 *
	 *	@param	str			Log message.
	 */

	public void logFatal( String str )
	{
		log( LoggerConstants.FATAL , str );
	}

	/** Logs a fatal message with a stack trace.
	 *
	 *	@param	str		Log message.
	 *
	 *	@param	t		Throwable.
	 */

	public void logFatal( String str , Throwable t )
	{
		log( LoggerConstants.FATAL , str , t );
	}

	/**	Get string representation of error level.
	 *
	 *	@param	level	The error level.
	 *
	 *	@return			String representation of level.
	 */

	 protected static String getErrorLevelString( int level )
	 {
	 	String result	= "?    ";

	 	if	(	( level >= LoggerConstants.FATAL ) &&
	 			( level <= LoggerConstants.DEBUG ) )
	 	{
			result	= errorLevelStrings[ level ];
	 	}

	 	return result;
	 }

	/** Logs a message.
	 *
	 *	@param	level		Log message level.
	 *	@param	str			Log message.
	 */

	public void log( int level , String str )
	{
		System.err.println
		(
			LoggerUtils.getFormattedCurrentDateAndTime() + " " +
			getErrorLevelString( level ) + " - " + str
		);
	}

	/** Logs a message with a stack trace.
	 *
	 *	@param	level		Log message level.
	 *	@param	str			Log message.
	 *	@param	t			Throwable.
	 */

	public void log( int level, String str, Throwable t )
	{
		String stackTrace	=
			DebugUtils.getStackTrace( t );

		String[] stackTraceLines	= stackTrace.split( "\\n" );

		System.err.println
		(
			LoggerUtils.getFormattedCurrentDateAndTime() + " " +
			getErrorLevelString( level ) + " - " + str
		);

		for ( int i = 0 ; i < stackTraceLines.length ; i++ )
		{
			System.err.println
			(
				LoggerUtils.getFormattedCurrentDateAndTime() + " " +
				"      - " + stackTraceLines[ i ]
			);
		}
	}

	/** Returns true if debugging messages are enabled.
	 *
	 *	@return		True if debugging messages are enabled.
	 */

	public boolean isDebuggingEnabled()
	{
		return true;
	}

	/** Returns true if logger is enabled.
	 *
	 *	@return		True if logger is enabled.
	 */

	public boolean isLoggerEnabled()
	{
		return loggerEnabled;
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


