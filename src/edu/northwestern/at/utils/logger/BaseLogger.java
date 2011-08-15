package edu.northwestern.at.utils.logger;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;

import org.apache.log4j.*;

/** Base logger which wraps Jakarta Common Logging Log manager.
 *
 *	<p>
 *	This class is a simple wrapper around Jakarta Common Logging
 *	log4j loggers.
 *	</p>
 */

public class BaseLogger implements Logger
{
	/** Log4J ogger. */

	protected org.apache.log4j.Logger logger;

	/** True if logger enabled. */

	protected boolean loggerEnabled = false;

	/** Create a logger.
	 *
	 *	@param	logClassName		The log class name.
	 *	@param	logPath				The path in which to store log files.
	 *	@param	logConfigFilePath	Path to log configuration file.
	 *
	 *	<p>
	 *	Reads the configuration file, creates a Log4J logger for
	 *	the specified class name, and configures the logger.
	 *	</p>
	 *
	 *	@throws	FileNotFoundException
	 *
	 *	@throws	IOException
	 */

	public BaseLogger
	(
		String logClassName ,
		String logPath,
		String logConfigFilePath
	)
		throws FileNotFoundException, IOException
	{
		logger	= org.apache.log4j.Logger.getLogger( logClassName );

		Properties properties = new Properties();

		properties.load( new FileInputStream( logConfigFilePath ) );

		for	(	Enumeration enumeration = properties.propertyNames();
				enumeration.hasMoreElements(); )
		{
			String key = (String)enumeration.nextElement();

			if	(	key.startsWith( "log4j.appender" ) &&
					key.endsWith( ".File" ) )
			{
				String val = properties.getProperty( key );

				val = logPath + File.separatorChar + val;

				properties.setProperty( key , val );
			}
		}

		PropertyConfigurator.configure( properties );

		loggerEnabled = true;
	}

	/** Terminates the logger.
	 */

	public void terminate()
	{
		if ( loggerEnabled )
		{
//			logger.shutdown();

			loggerEnabled = false;
		}
	}

	/** Maps a LoggerConstants level to log4j level.
	 *
	 *	@param	level		LoggerConstants level.
	 *
	 *	@return		log4j level.
	 */

	protected static Priority mapLevel( int level )
	{
		switch ( level )
		{
			case LoggerConstants.FATAL:
				return Level.FATAL;

			case LoggerConstants.ERROR:
				return Level.ERROR;

			case LoggerConstants.WARN:
				return Level.WARN;

			case LoggerConstants.INFO:
				return Level.INFO;

			case LoggerConstants.DEBUG:
				return Level.DEBUG;
		}

		return Level.ERROR;
	}

	/** Logs a message at the DEBUG level.
	 *
	 *	@param	str			Log message.
	 */

	public void logDebug( String str )
	{
		logger.log( Level.DEBUG , str );
	}

	/** Logs a message at the INFO level.
	 *
	 *	@param	str			Log message.
	 */

	public void logInfo( String str )
	{
		logger.log( Level.INFO , str );
	}

	/** Logs a message at the WARN level.
	 *
	 *	@param	str			Log message.
	 */

	public void logWarning( String str )
	{
		logger.log( Level.WARN , str );
	}

	/** Logs a message at the ERROR level.
	 *
	 *	@param	str			Log message.
	 */

	public void logError( String str )
	{
		logger.log( Level.ERROR , str );
	}

	/** Logs a error message with a stack trace.
	 *
	 *	@param	str		Log message.
	 *
	 *	@param	t		Throwable.
	 */

	public void logError( String str , Throwable t )
	{
		logger.log( Level.ERROR , str , t );
	}

	/** Logs a message at the FATAL level.
	 *
	 *	@param	str			Log message.
	 */

	public void logFatal( String str )
	{
		logger.log( Level.FATAL , str );
	}

	/** Logs a fatal message with a stack trace.
	 *
	 *	@param	str		Log message.
	 *
	 *	@param	t		Throwable.
	 */

	public void logFatal( String str , Throwable t )
	{
		logger.log( Level.FATAL , str , t );
	}

	/** Logs a message.
	 *
	 *	@param	level		Log message level.
	 *	@param	str			Log message.
	 */

	public void log( int level , String str )
	{
		logger.log( mapLevel( level ) , str );
	}

	/** Logs a message with a stack trace.
	 *
	 *	@param	level		Log message level.
	 *	@param	str			Log message.
	 *	@param	t			Throwable.
	 */

	public void log( int level, String str, Throwable t )
	{
		logger.log( mapLevel( level ), str, t );
	}

	/** Returns true if debugging messages are enabled.
	 *
	 *	@return		True if debugging messages are enabled.
	 */

	public boolean isDebuggingEnabled()
	{
		return logger.isDebugEnabled();
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


