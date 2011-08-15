package edu.northwestern.at.morphadorner.servers.standardizerserver;

/*	Please see the license information at the end of this file. */

import java.net.InetAddress;
import java.rmi.*;
import java.rmi.registry.*;

/**	The spelling standardizer server main program.
 *
 *	<p>
 *	<code>StandardizerServer (start path | stop path)</code>
 *	</p>
 *
 *	<p>The main program is used to both start and stop the Standardizer
 *	server.
 *	</p>
 *
 *	<p>The first argument is required and specifies the operation: start
 *	or stop.
 *	</p>
 *
 *	<p>The second argument is required and specifies
 *	the path to the server directory. This directory contains the server
 *	configuration file "standardizer.config" and the log configuration file
 *	"log.config". The server log file is also written to this directory.
 *	</p>
 *
 *	<p>On startup the server creates a {@link StandardizerServerBootstrap}
 *	object and registers it in the RMI registry using the bind name
 *	"SpellingStandardizer". On shutdown this registry entry is removed.
 *	</p>
 *
 *	<p>Shutdown requests must originate from the local host, or
 *	they are ignored.
 *	</p>
 */

public class StandardizerServer
{
	/**	Starts up the server.
	 *
	 *	@param	path		Path to server directory.
	 */

	private static void startup( String path )
	{
		try
		{
			StandardizerServerSettings.initializeSettings();

			StandardizerServerConfig.read( path );

			StandardizerServerLogger.initialize();

			StandardizerServerSessionImpl.initialize();

			StandardizerServerBootstrap bootstrap =
				new StandardizerServerBootstrapImpl();

			Registry registry =
				LocateRegistry.createRegistry
				(
					StandardizerServerConfig.getRmiRegistryPort()
				);

			registry.rebind( "SpellingStandardizer" , bootstrap );

			System.out.println
			(
				StandardizerServerSettings.getString
				(
					"Standardizerserverstarted"
				)
			);
		}
		catch ( Exception e )
		{
			System.out.println
			(
				StandardizerServerSettings.getString
				(
					"Standardizerserverstartupfailure"
				)
			);

			e.printStackTrace();

			System.exit( 1 );
		}
	}

	/**	Shuts down the server.
	 *
	 *	<p>Shutdown requests must originate from the local host, or
	 *	they are ignored.
	 */

	private static void shutdown( String path )
	{
		try
		{
			StandardizerServerSettings.initializeSettings();

			StandardizerServerConfig.read( path );

			StandardizerServerLogger.initialize
			(
				StandardizerServerSettings.getString( "Servershuttingdown" )
			);

			String uri =
				"//localhost:" +
				StandardizerServerConfig.getRmiRegistryPort() +
				"/SpellingStandardizer";

			StandardizerServerBootstrap bootstrap =
				(StandardizerServerBootstrap)Naming.lookup( uri );

			bootstrap.shutdown( uri );

			System.out.println
			(
				StandardizerServerSettings.getString
				(
					"Standardizerserverstopped"
				)
			);
		}
		catch ( NotBoundException e )
		{
			e.printStackTrace();

			System.out.println
			(
				StandardizerServerSettings.getString
				(
					"Thestandardizerserverwasnotrunning"
				)
			);
		}
		catch ( Exception e )
		{
			StandardizerServerLogger.log(
				StandardizerServerLogger.ERROR ,
				StandardizerServerSettings.getString( "Shutdown" ) ,
				e );

			StandardizerServerLogger.terminate();
		}
	}

	/**	Main program.
	 *
	 *	@param	args	Command-line arguments.
	 */

	public static void main( String args[] )
	{
		if ( args.length == 2 && args[0].equalsIgnoreCase("start") )
		{
			startup( args[ 1 ] );
		}
		else if ( args.length == 2 && args[ 0 ].equalsIgnoreCase( "stop" ) )
		{
			shutdown( args[ 1 ] );
		}
		else
		{
			System.out.println
			(
				StandardizerServerSettings.getString( "ServerUsage" )
			);

			System.exit( 1 );
		}
	}

	/** Allow overrides but not instantiation. */

	protected StandardizerServer()
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



