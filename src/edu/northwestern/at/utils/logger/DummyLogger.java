package edu.northwestern.at.utils.logger;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;

import org.apache.log4j.*;

/** Dummy logger which generates no output.
 */

public class DummyLogger implements Logger
{
	/** True if logger enabled. */

	protected boolean loggerEnabled = false;

	/** Create a dummy logger.
	 */

	public DummyLogger()
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
	}

	/** Logs a message at the INFO level.
	 *
	 *	@param	str			Log message.
	 */

	public void logInfo( String str )
	{
	}

	/** Logs a message at the WARN level.
	 *
	 *	@param	str			Log message.
	 */

	public void logWarning( String str )
	{
	}

	/** Logs a message at the ERROR level.
	 *
	 *	@param	str			Log message.
	 */

	public void logError( String str )
	{
	}

	/** Logs a error message with a stack trace.
	 *
	 *	@param	str		Log message.
	 *
	 *	@param	t		Throwable.
	 */

	public void logError( String str , Throwable t )
	{
	}

	/** Logs a message at the FATAL level.
	 *
	 *	@param	str			Log message.
	 */

	public void logFatal( String str )
	{
	}

	/** Logs a fatal message with a stack trace.
	 *
	 *	@param	str		Log message.
	 *
	 *	@param	t		Throwable.
	 */

	public void logFatal( String str , Throwable t )
	{
	}

	/** Logs a message.
	 *
	 *	@param	level		Log message level.
	 *	@param	str			Log message.
	 */

	public void log( int level , String str )
	{
	}

	/** Logs a message with a stack trace.
	 *
	 *	@param	level		Log message level.
	 *	@param	str			Log message.
	 *	@param	t			Throwable.
	 */

	public void log( int level, String str, Throwable t )
	{
	}

	/** Returns true if debugging messages are enabled.
	 *
	 *	@return		True if debugging messages are enabled.
	 */

	public boolean isDebuggingEnabled()
	{
		return false;
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



