package edu.northwestern.at.morphadorner.servers.standardizerserver;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;

import org.apache.log4j.*;

/**	Server log manager.
 *
 *	<p>This static class is a simple wrapper around a Jakarta Commons
 *	Logging log4j logger named "edu.northwestern.at.standardizerserver".
 *	</p>
 *
 *	<p>The file named "log.config" in the server directory is used to
 *	configure the logger.
 *	</p>
 */

public class StandardizerServerLogger
{
	/**	Fatal logging level. */

	static public final int FATAL = 0;

	/**	Error logging level. */

	static public final int ERROR = 1;

	/**	Warn logging level. */

	static public final int WARN = 2;

	/**	Info logging level. */

	static public final int INFO = 3;

	/**	Debug logging level. */

	static public final int DEBUG = 4;

	/**	Server logger. */

	static private org.apache.log4j.Logger logger =
		org.apache.log4j.Logger.getLogger(
			"edu.northwestern.at.standardizerserver" );

	/**	Initializes the logger.
	 *
	 *	@param	message	Initial log message.
	 *
	 *	<p>Reads the "log.config" configuraton file and configures
	 *	the server logger.
	 *
	 *	<p>The configuration file is preprocessed to force the log
	 *	file to be located in the server directory. The server directory
	 *	path is prepended to the log file name.
	 *
	 *	@throws	Exception
	 */

	static void initialize (String message)
		throws FileNotFoundException, IOException
	{
		String logConfigFilePath = StandardizerServerConfig.getLogConfigFilePath();
		Properties properties = new Properties();
		properties.load(new FileInputStream(logConfigFilePath));
		for (Enumeration enumeration = properties.propertyNames();
			enumeration.hasMoreElements(); )
		{
			String key = (String)enumeration.nextElement();
			if (key.startsWith("log4j.appender") &&
				key.endsWith(".File"))
			{
				String val = properties.getProperty(key);
				val = StandardizerServerConfig.getPath() + File.separatorChar + val;
				properties.setProperty(key, val);
			}
		}
		PropertyConfigurator.configure(properties);
		logger.info(message);
	}

	/**	Initializes the logger.
	 *
	 *	<p>Reads the "log.config" configuraton file and configures
	 *	the server logger.
	 *
	 *	<p>The configuration file is preprocessed to force the log
	 *	file to be located in the server directory. The server directory
	 *	path is prepended to the log file name.
	 *
	 *	@throws	Exception
	 */

	static void initialize ()
		throws FileNotFoundException, IOException
	{
		initialize(
			StandardizerServerSettings.getString("Serverstarted") +
			StandardizerServerSettings.getProgramVersion());
	}

	/**	Terminates the logger.
	 */

	static void terminate () {
		logger.info(
			StandardizerServerSettings.getString("Serverstopped"));
	}

	/**	Maps a LoggerConstants level to a log4j level.
	 *
	 *	@param	level		LoggerConstants level.
	 *
	 *	@return		log4j level.
	 */

	static Priority mapLevel (int level) {
		switch (level) {
			case FATAL:
				return Level.FATAL;
			case ERROR:
				return Level.ERROR;
			case WARN:
				return Level.WARN;
			case INFO:
				return Level.INFO;
			case DEBUG:
				return Level.DEBUG;
		}
		return Level.ERROR;
	}

	/**	Logs a message.
	 *
	 *	@param	level		Log message level.
	 *
	 *	@param	str			Log message.
	 */

	static void log (int level, String str) {
		logger.log(mapLevel(level), str);
	}

	/**	Logs a message for a session.
	 *
	 *	@param	session		The session.
	 *
	 *	@param	level		Log message level.
	 *
	 *	@param	str			Log message.
	 */

	static void log (StandardizerServerSessionImpl session, int level, String str) {
		str = StandardizerServerSettings.getString("Session") + "=" + session +
			", " + str;
		logger.log(mapLevel(level), str);
	}

	/**	Logs an error message with a stack trace.
	 *
	 *	@param	level		Log message level.
	 *
	 *	@param	str			Log message.
	 *
	 *	@param	t			Throwable.
	 */

	static void log (int level, String str, Throwable t) {
		logger.log(mapLevel(level), str, t);
	}

	/**	Logs an error message for a session with a stack trace.
	 *
	 *	@param	session		The session.
	 *
	 *	@param	level		Log message level.
	 *
	 *	@param	str			Log message.
	 *
	 *	@param	t			Throwable.
	 */

	static void log (StandardizerServerSessionImpl session, int level, String str,
		Throwable t)
	{
		str =
			StandardizerServerSettings.getString( "Session" ) + "=" +
			session + ", " + str;

		logger.log( mapLevel( level ) , str , t );
	}

	/** Hides the default no-arg constructor. */

	private StandardizerServerLogger()
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



