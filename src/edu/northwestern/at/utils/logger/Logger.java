package edu.northwestern.at.utils.logger;

/*	Please see the license information at the end of this file. */

/** Interface for a logger.
 *
 *	<p>
 *	A logger is used to write messages to a log file.
 *	Each message has an associated level of severity.
 *	In increasing order these are:
 *	</p>
 *
 *	<ol>
 *	<li>debug</li>
 *	<li>information</li>
 *	<li>warning</li>
 *	<li>error</li>
 *	<li>fatal</li>
 *	</p>
 *
 *	<p>
 *	Please see
 *	{@link edu.northwestern.at.utils.logger.LoggerConstants}
 *	for the associated numeric values for each level.
 *	</p>
 */

public interface Logger
{
	/** Log a message at the Debug level.
	 *
	 *	@param	str		Log message.
	 */

	public void logDebug( String str );

	/** Log a message at the Info level.
	 *
	 *	@param	str		Log message.
	 */

	public void logInfo( String str );

	/** Log a message at the Warn level.
	 *
	 *	@param	str		Log message.
	 */

	public void logWarning( String str );

	/** Log a message at the Error level.
	 *
	 *	@param	str		Log message.
	 */

	public void logError( String str );

	/** Logs a error message with a stack trace.
	 *
	 *	@param	str		Log message.
	 *
	 *	@param	t		Throwable.
	 */

	public void logError( String str , Throwable t );

	/** Log a message at the Fatal level.
	 *
	 *	@param	str		Log message.
	 */

	public void logFatal( String str );

	/** Logs a fatal message with a stack trace.
	 *
	 *	@param	str		Log message.
	 *
	 *	@param	t		Throwable.
	 */

	public void logFatal( String str , Throwable t );

	/** Log a message at a specified level.
	 *
	 *	@param	level	Message level.
	 *					See {@link edu.northwestern.at.utils.logger.LoggerConstants}
	 *					for the allowed values.
	 *	@param	str		Log message.
	 */

	public void log( int level , String str );

	/** Log a message with a stack trace.
	 *
	 *	@param	level	Message level.
	 *					See {@link edu.northwestern.at.utils.logger.LoggerConstants}
	 *					for the allowed values.
	 *	@param	str		Log message.
	 *	@param	t		Throwable.
	 */

	public void log( int level , String str , Throwable t );

	/** Returns true if debugging messages are enabled.
	 *
	 *	@return		True if debugging messages are enabled.
	 */

	public boolean isDebuggingEnabled();

	/** Returns true if logger is enabled.
	 *
	 *	@return		True if logger is enabled.
	 */

	public boolean isLoggerEnabled();

	/**	Terminate.logger.
	 */

	public void terminate();
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


