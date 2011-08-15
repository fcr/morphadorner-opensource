package edu.northwestern.at.morphadorner.servers.standardizerserver;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.rmi.*;
import java.rmi.server.*;
import java.io.*;
import java.net.*;
import java.lang.*;
import java.lang.reflect.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.spellingstandardizer.*;

/**	Session remote object implementation.
 */

public class StandardizerServerSessionImpl extends UnicastRemoteObject
	implements StandardizerServerSession
{
	/**	Timeout time for idle session = two hours. */

	protected final static long MAXIDLETIME	= 2 * 60 * 60 * 1000L;

	/**	Global synchronization lock. */

	protected static Object lock = new Object();

	/**	Next available session id. */

	protected static long nextSessionId;

	/**	Maps session ids to active sessions. */

	protected static Map<Long, StandardizerServerSessionImpl> sessions =
		MapFactory.createNewMap();

	/**	Session id. */

	protected Long id;

	/**	The dotted-decimal IP address of the client host. */

	protected String host;

	/**	The date and time the session was created. */

	protected Date loginDate = new Date();

	/**	True if session active. */

	protected boolean active = true;

	/**	The time the session was last tickled by the client. */

	protected long lastTickleTime = loginDate.getTime();

	/**	Simple spelling standardizer. */

	protected static SpellingStandardizer simpleStandardizer;

	/**	Extended search spelling standardizer. */

	protected static SpellingStandardizer standardizer;

	/**	Creates a new session.
	 *
	 *	@throws	RemoteException
	 */

	StandardizerServerSessionImpl()
		throws RemoteException
	{
		super( StandardizerServerConfig.getRmiPort() );

		synchronized( lock )
		{
			id	= new Long( nextSessionId++ );

			try
			{
				host = UnicastRemoteObject.getClientHost();
			}
			catch ( ServerNotActiveException e )
			{
				StandardizerServerLogger.log
				(
					StandardizerServerLogger.ERROR,
					StandardizerServerSettings.getString
					(
						"Getclienthost"
					) ,
					e
				);
			}

			sessions.put( id , this );

			StandardizerServerLogger.log
			(
				this ,
				StandardizerServerLogger.INFO ,
				StandardizerServerSettings.getString( "Sessionbegin" ) +
					host
			);
		}
	}

	/**	Ends the session.
	 *
	 *	@throws	RemoteException
	 */

	public void endSession()
		throws RemoteException
	{
		synchronized( lock )
		{
			active	= false;

			sessions.remove( id );

			StandardizerServerLogger.log
			(
				this ,
				StandardizerServerLogger.INFO,
				StandardizerServerSettings.getString( "Sessionend" ) +
					host
			);
		}
	}

	/**	Tickles the session.
	 *
	 *	<p>Clients should tickle their sessions every 30 minutes. Sessions
	 *	which go untickled for 2 hours are considered to be dead and are
	 *	timed out and terminated.
	 *
	 *	@throws RemoteException
	 */

	public void tickle()
		throws RemoteException
	{
		lastTickleTime = System.currentTimeMillis();
	}

	/**	Logs a message.
	 *
	 *	@param	level		Log message level.
	 *
	 *	@param	msg			Log message.
	 *
	 *	@throws RemoteException
	 */

	public void logMessage( int level , String msg )
		throws RemoteException
	{
		StringTokenizer tok	= new StringTokenizer( msg , "\n\r" );
		StringBuffer buf	= new StringBuffer();

		while ( tok.hasMoreTokens() )
		{
			buf.append( tok.nextToken() + "\n" );
		}

		int len	= buf.length();

		if ( len > 0 ) buf.setLength( len - 1 );

		StandardizerServerLogger.log( this , level , buf.toString() );
	}

	/**	Returns a string representation of the session.
	 *
	 *	@return		String representation.
	 */

	public String toString()
	{
		return id + "";
	}

	/**	Times out idle sessions.
	 *
	 *	<p>This method wakes up once per minute and checks all active sessions.
	 *	Sessions which have not been tickled by their client for the last
	 *	two hours are terminated.
	 *
	 *	@throws	InterruptedException
	 */

	protected static void timeOutIdleSessions()
		throws InterruptedException
	{
		while ( true )
		{
			Thread.sleep( 60 * 1000 );

			synchronized( lock )
			{
				List<StandardizerServerSessionImpl> sessionsCopy	=
					ListFactory.createNewList( sessions.values() );

				long curTime		= System.currentTimeMillis();

				for	(	Iterator it = sessionsCopy.iterator() ;
						it.hasNext() ;
					)
				{
					StandardizerServerSessionImpl session =
						(StandardizerServerSessionImpl)it.next();

					long idleTime	= curTime - session.lastTickleTime;

					if ( idleTime > MAXIDLETIME )
					{
						session.active	= false;

						sessions.remove( session.id );

						StandardizerServerLogger.log
						(
							session ,
							StandardizerServerLogger.INFO ,
							StandardizerServerSettings.getString
							(
								"Sessiontimedout"
							)
						);
					}
				}
			}
		}
	}

	/**	Initializes the class.
	 *
	 *	@throws	Exception
	 */

	static void initialize()
		throws Exception
	{
		                        //	Initialize the spelling
		                        //	standardizers.
		initStandardizers();
								//	Start the idle session timeout thread.
		new Thread
		(
			new Runnable()
			{
				public void run()
				{
					try
					{
						timeOutIdleSessions();
					}
					catch ( InterruptedException e )
					{
						StandardizerServerLogger.log
						(
							StandardizerServerLogger.ERROR ,
							StandardizerServerSettings.getString
							(
								"Idlesessiontimeoutthread"
							) ,
							e
						);
					}
				}
			}
		).start();
	}

	/**	Initialize spelling standardizers.
	 */

	protected static void initStandardizers()
		throws Exception
	{
        						//	Get simple spelling standardizer.

		simpleStandardizer	= new SimpleSpellingStandardizer();

								//	Get extended search standardizer.

		standardizer		= new DefaultSpellingStandardizer();

								//	Load alternate/standard spelling pairs.

		standardizer.loadAlternativeSpellings
		(
			new File
			(
				StandardizerServerConfig.getMappedSpellingsFileName()
			).toURI().toURL() ,
			"utf-8" ,
			"\t"
		);

		StandardizerServerLogger.log
		(
			StandardizerServerLogger.INFO ,
			StandardizerServerSettings.getString
			(
				"Mappedspellingsloaded"
			)
		);
								//	Set pairs list into simple
								//	standardizer as well.

		simpleStandardizer.setMappedSpellings
		(
			standardizer.getMappedSpellings()
		);
	}

	/**	Returns standard spellings given a spelling.
	 *
	 *	@param	spelling	The spelling.
	 *
	 *	@return				The standard spellings as an array of String.
	 */

	public String[] standardizeSpelling( String spelling )
		throws RemoteException
	{
		return standardizer.standardizeSpelling( spelling );
	}

	/**	Returns a standard spelling given a spelling and part of speech.
	 *
	 *	@param	spelling	The spelling.
	 *
	 *	@return				The standard spelling.
	 */

	public String standardizeSpelling( String spelling , String pos )
		throws RemoteException
	{
		return standardizer.standardizeSpelling( spelling , pos );
	}

	/** Returns number of alternate spellings.
	  *
	  *	@return		The number of alternate spellings.
	  */

	public int getNumberOfAlternateSpellings()
		throws RemoteException
	{
		return standardizer.getNumberOfAlternateSpellings();
	}

	/** Returns number of alternate spellings by word class.
	  *
	  *	@return		The number of alternate spellings.
	  */

	public int[] getNumberOfAlternateSpellingsByWordClass()
		throws RemoteException
	{
		return standardizer.getNumberOfAlternateSpellingsByWordClass();
	}

	/** Returns number of standard spellings.
	  *
	  *	@return		The number of standard spellings.
	  */

	public int getNumberOfStandardSpellings()
		throws RemoteException
	{
		return standardizer.getNumberOfStandardSpellings();
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



