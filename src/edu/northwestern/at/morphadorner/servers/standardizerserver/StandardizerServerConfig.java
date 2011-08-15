package edu.northwestern.at.morphadorner.servers.standardizerserver;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;

/**	The server configuration manager.
 *
 *	<p>Reads the properties file "standardizer.config" in the server directory.
 */

class StandardizerServerConfig
{
	/**	Path to the server directory. */

	protected static String path;

	/**	RMI registry port. */

	protected static int rmiRegistryPort;

	/**	RMI port. */

	protected static int rmiPort;

	/**	File with alternate to standard spelling pairs. */

	protected static String mappedSpellingsFileName	=
		"/nupos/mergedspellingpairs.tab";

	/**	Reads the server configuration file.
	 *
	 *	@param	path		Path to the server directory.
	 *
	 *	@throws	Exception
	 */

	static void read( String path )
		throws Exception
	{
								//	Get configuration settings.

		StandardizerServerConfig.path	= path;

		path					= path + "/standardizer.config";

		Properties properties	= new Properties();

		properties.load( new FileInputStream( path ) );

		String srmiRegistryPort	=
			properties.getProperty( "rmiregistry-port" );

		rmiRegistryPort =
			( srmiRegistryPort == null ) ?
				1099 : Integer.parseInt( srmiRegistryPort );

		String srmiPort	= properties.getProperty( "rmi-port" );

		rmiPort			=
			( srmiPort == null ) ? 0 : Integer.parseInt( srmiPort );

		mappedSpellingsFileName	=
			properties.getProperty( "mapped-spellings-file" );
	}

	/**	Gets the path to the server directory.
	 *
	 *	@return		The path to the server directory.
	 */

	static String getPath()
	{
		return path;
	}

	/**	Gets the log configuration file path.
	 *
	 *	@return		The log configuration file path.
	 */

	static String getLogConfigFilePath()
	{
		return path + File.separatorChar + "log.config";
	}

	/**	Gets the RMI registry port.
	 *
	 *	@return		The RMI registry port.
	 */

	static int getRmiRegistryPort()
	{
		return rmiRegistryPort;
	}

	/**	Gets the RMI port.
	 *
	 *	@return		The RMI port.
	 */

	static int getRmiPort()
	{
		return rmiPort;
	}

	/**	Gets the mapped spellings file name.
	 *
	 *	@return		The mapped spellings file name.
	 */

	static String getMappedSpellingsFileName()
	{
		return mappedSpellingsFileName;
	}

	/** Allow overrides but not instantiation. */

	protected StandardizerServerConfig()
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



