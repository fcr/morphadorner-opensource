package edu.northwestern.at.utils;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;

/** Execute system commands.
 */

public class ExecUtils
{
	/** Execute file/command and wait for command to complete.
	 *
	 *	@param	command		The command or file name to execute.
	 *	@param	doWait		True to wait for the command to return.
	 *
	 *	@return				An array list of the output written
	 *						by the program to standard output, if doWait
	 *						is true.
	 *	<p>
	 *	Examples:
	 *	</p>
	 *
	 *	<p>
	 *	Windows only directory listing:
	 *	</p>
	 *	<code>
	 *		List<String> outputLinesList = execAndWait( "dir" , true );
	 *	</code>
	 *
	 *	<p>
	 *	Directory listing for MacOSX and similar Unixen:
	 *	</p>
	 *
	 *	<code>
	 *		List<String> outputLinesList = execAndWait( "ls -la" , true );
	 *	</code>
	 *
	 *	<p>
	 *	Open Acrobat file in Acrobat reader, if installed (Windows and MacOSX):
	 *	</p>
	 *
	 *	<code>
	 *		List<String> outputLinesList = execAndWait( "mydoc.pdf" , false );
	 *	</code>
	 *
	 *	<p>
	 *	Note:	On some Unixen and possibly MacOSX, you may need to
	 *			add a path specifier to get the file "executed."
	 *			E.g., if the document is in the current directory:
	 *	</p>
	 *	<code>
	 *		List<String> outputLinesList = execAndWait( "./mydoc.pdf" , false );
	 *	</code>
	 *
	 *	<p>
	 *	When running a file that causes the associated application
	 *	to start, and doWait is true, execAndWait may not return until
	 *	that associated application is closed.  You should invoke execAndWait
	 *	on a separate thread if you want the execute the command and allow
	 *	your program to continue executing concurrently as well.
	 *	</p>
	 */

	public static List[] execAndWait( String command , boolean doWait )
	{
								// Get runtime so we can execute the program.

		Runtime runtime = Runtime.getRuntime();

                         		// Java exec returns a process.  We use
                         		// that to get the output of the command,
                         		// if any, and to wait for the command to finish.

		Process process = null;

								//	Process output stream grabbers.

		StreamGrabber outputGrabber	= null;
		StreamGrabber errorGrabber	= null;

                                // Start the program.  We choose the command
                                // processor differently based upon what system
                                // we are running under.
		try
		{
								// Use "open" under Mac OSX.
			if ( Env.MACOSX )
			{
				process = runtime.exec( new String[]{ "open", command } );
			}
								// Use command.com or cmd.exe under Windows.

			else if ( Env.WINDOWSOS )
			{
				String shell = "cmd.exe";

				if ( Env.OSNAME.indexOf( "windows 9" ) > -1 )
				    shell = "command.exe";
				else if ( Env.OSNAME.indexOf( "windows ME" ) > -1 )
				    shell = "command.exe";

				process =
					runtime.exec( new String[]{ shell , " /c " + command } );
			}
								// Just try executing the command directly
								// on other systems.  This needs work.
			else
			{
				process = runtime.exec( command );
			}
		}
		catch ( IOException e )
		{
		}
								// If the command process started successfully,
								// we should wait for its output to finish
								// to avoid weird process hangs.  However, if
								// "doWait" is false, don't bother to wait.

		if ( ( process != null ) && doWait )
		{
								//	Grab output from process output
								//	and error streams.

			outputGrabber	=
				new StreamGrabber( process.getInputStream() );

			errorGrabber	=
				new StreamGrabber( process.getErrorStream() );

			outputGrabber.start();
			errorGrabber.start();

								// Wait for command to finish.
			try
			{
				process.waitFor();
			}
			catch ( InterruptedException e )
			{
				Thread.currentThread().interrupt();
			}
		}
								// Return process output lines.

		if ( doWait )
		{
			return new List[]
			{
				outputGrabber.getGrabbedTextLines() ,
				errorGrabber.getGrabbedTextLines()
			};
		}
		else
		{
			return new List[]
			{
				ListFactory.createNewList() ,
				ListFactory.createNewList()
			};
		}
	}

	/** Don't allow instantiation, do allow overrides. */

	protected ExecUtils()
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


