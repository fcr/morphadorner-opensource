package edu.northwestern.at.utils;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.io.*;
import java.text.*;
import java.util.*;

/**	Debug utilities.
 *
 *	<p>
 *	This static class provides various utility methods for printing
 *	debugging information.
 *	</p>
 */

public class DebugUtils
{
	/** Get list of active threads.
	 *
	 *	@return		String containing list of active threads.
	 */

    public static String activeThreads()
    {
		StringBuffer sb = new StringBuffer();

		Thread threads[] = new Thread[ Thread.activeCount() ];

		int num = Thread.enumerate( threads );

//		sb.append( "------- Threads -------\n" );

		for ( int i = 0; i < num; i++ )
		{
		    sb.append(  i + " : " + threads[ i ] + "\n" );
		}

//		sb.append( "------- Threads -------\n" );

		return StringUtils.trim(sb.toString());
    }

	/**	Gets a stack trace for an exception.
	 *
	 *	@param e	The exception
	 */

	public static String getStackTrace( Throwable e )
	{
		ByteArrayOutputStream traceByteArrayStream	=
			new ByteArrayOutputStream();

		PrintStream tracePrintStream	=
			new PrintStream( traceByteArrayStream );

		e.printStackTrace( tracePrintStream );

		tracePrintStream.flush();

		return traceByteArrayStream.toString();
	}

	/** Prints child components of a component.
	 *
	 *	@param	parent	The parent component.
	 */

	public static Component printChildComponents
	(
		Container parent ,
		int indent
	)
	{
		if ( parent == null ) return null;

		if ( parent instanceof Container )
		{
			Component [] comps = parent.getComponents();

			for ( int i = 0; i < comps.length; i++ )
			{
				System.out.println(
					StringUtils.dupl( ' ' , indent ) + comps[ i ].toString() );

				if ( comps[ i ] instanceof Container )
				{
					return printChildComponents(
						(Container)( comps[ i ] ) , indent + 1 );
				}
			}
		}
		else
		{
			System.out.println(
				StringUtils.dupl( ' ' , indent ) + parent.toString() );
		}

		return null;
	}

	/** Debugging support -- Print message to System.out telling if object is null or not.
	 *
	 *	@param	methodName		Name of calling method.
	 *
	 *	@param	objectName		Name of object to inspect.
	 *
	 *	@param	object			The named object to inspect.
	 */

	public static void printNullity
	(
		String methodName,
		String objectName ,
		Object object
	)
	{
		if ( object == null )
		{
			System.out.println( methodName + ": " + objectName + " is null." );
		}
		else
		{
			System.out.println( methodName + ": " + objectName + " is not null." );
		}
	}

	/** Debugging support -- Print contents of a map (HashMap, TreeMap, etc.).
	 *
	 *	@param	mapLabel		Label for map.
	 *
	 *	@param	map				The map to print.
	 *
	 *	<p>
	 *	N.B.  This method assumes both the keys and values have toString() methods.
	 *	</p>
	 */

	public static<K,V> void printMap
	(
		String mapLabel ,
		Map<K,V> map
	)
	{
		if ( map == null )
		{
			System.out.println( mapLabel + " is null." );
		}
		else if ( map.size() == 0 )
		{
			System.out.println( mapLabel + " is empty." );
		}
		else
		{
			System.out.println( mapLabel );

			Iterator<K> iterator = map.keySet().iterator();

			int i = 0;

			while ( iterator.hasNext() )
			{
				K key	= iterator.next();
				V value	= map.get( key );

				if ( key == null )
				{
					if ( value == null )
					{
						System.out.println( i + ": null=null" );
					}
					else
					{
						System.out.println( i + ": null=" + value.toString() );
					}
				}
				else
				{
					if ( value == null )
					{
						System.out.println( i + ": " + key.toString() + "=null" );
					}
					else
					{
						System.out.println( i + ": " +
											key.toString() + "=" + value.toString() );
					}
				}

				i++;
			}
		}
	}

	/** Debugging support -- Print contents of an array.
	 *
	 *	@param	arrayLabel		Label for array.
	 *
	 *	@param	array			The array to print.
	 *
	 *	<p>
	 *	N.B.  This method assumes the array values have toString() methods.
	 *	</p>
	 */

	public static void printArray
	(
		String arrayLabel ,
		Object[] array
	)
	{
		if ( array == null )
		{
			System.out.println( arrayLabel + " is null." );
		}
		else if ( array.length == 0 )
		{
			System.out.println( arrayLabel + " is empty." );
		}
		else
		{
			System.out.println( arrayLabel );

			for ( int i = 0; i < array.length; i++ )
			{
				Object value = array[ i ];

				if ( value == null )
				{
					System.out.println( i + ": null" );
				}
				else
				{
					System.out.println( i + ": " + value.toString() );
				}
			}
		}
	}

	/** Print current memory status. */

	public static void printMemoryStatus( String title )
	{
		Runtime runTime	= Runtime.getRuntime();

		long freeMem	= runTime.freeMemory();
		long totalMem	= runTime.totalMemory();

		System.out.println
		(
			( title == null ) ? "" : ( title + ": " ) +
			"Memory status: free memory=" +
			StringUtils.formatNumberWithCommas( freeMem ) +
			", total memory=" +
			StringUtils.formatNumberWithCommas( totalMem )
		);
	}

	/** Prints current thread list. */

    public static void printThreads()
    {
		System.out.println( activeThreads() );
    }

	/** Don't allow instantiation, do allow overrides. */

	protected DebugUtils()
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


