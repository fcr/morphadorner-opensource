package edu.northwestern.at.morphadorner.servlets;

import java.io.*;

public class DebugOut
{
	protected static PrintWriter debugWriter;

	public synchronized static PrintWriter debugWriter()
	{
		return debugWriter;
	}

	protected DebugOut()
	{
	}

	public synchronized static void println( String s )
	{
		debugWriter.println( s );
		debugWriter.flush();
	}

	protected void destroy()
	{
		try
		{
			debugWriter.flush();
			debugWriter.close();
		}
		catch( Exception e )
		{
		}
	}

	static
	{
		try
		{
			debugWriter	=
				new PrintWriter( new FileOutputStream( "c:/debug.txt" ) );
		}
		catch ( Exception e )
		{
		}
	}
}

