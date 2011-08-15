package edu.northwestern.at.utils;

/*	Please see the license information at the end of this file. */

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.io.*;

/** DynamicCall.
 *
 *	<p>DynamicCall provides methods for dynamically calling methods in classes
 *	with specified arguments.  The class may be specified by its class type or
 *	by name.  The method is provided by name.  The list of parameters
 *	is passed as an {@link ArgumentList}.  DynamicCall uses Java reflection.</p>
 */

public class DynamicCall
{
	/** Use reflection to call a named method from a specified class.
	 *
	 *	@param	classToCall		The class containing the method to call.
	 *	@param	methodName		The method name to call.
	 *	@param	argumentList	The argument list.
	 */

	public static Object dynamicCall(
		Object classToCall,
		String methodName,
		ArgumentList argumentList )
		    throws 	NoSuchMethodException,
		    		InvocationTargetException,
	    			IllegalAccessException
	{
		Method methodToCall =
			classToCall.getClass().getMethod(
				methodName , argumentList.getArgumentClasses() );
		return ( methodToCall.invoke( classToCall , argumentList.getArguments() ) );
	}

	/** Use reflection to call a named method from a named class.
	 *
	 *	@param	className		The class name containing the method to call.
	 *	@param	methodName		The method name to call.
	 *	@param	argumentList	The argument list.
	 */

	public static Object dynamicCall(
		String className,
		String methodName,
		ArgumentList argumentList )
		    throws 	NoSuchMethodException,
		    		InvocationTargetException,
	    			IllegalAccessException,
	    			ClassNotFoundException,
					InstantiationException
	{
		Object result = null;

		try
		{
			java.lang.Class classToCall	= Class.forName( className );

			result	=
				dynamicCall(
					classToCall.newInstance() , methodName , argumentList );
		}
		catch ( ClassNotFoundException e )
		{
			throw new ClassNotFoundException();
		}

		return result;
	}

	/** Don't allow instantiation, do allow overrides. */

	protected DynamicCall()
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


