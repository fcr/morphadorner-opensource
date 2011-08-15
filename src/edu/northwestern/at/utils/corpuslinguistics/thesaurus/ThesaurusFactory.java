package edu.northwestern.at.utils.corpuslinguistics.thesaurus;

/*	Please see the license information at the end of this file. */

import edu.northwestern.at.utils.ClassUtils;

/**	Thesaurus factory.
 */

public class ThesaurusFactory
{
	/**	Get a thesaurus.
	 *
	 *	@return		The thesaurus.
	 */

	public Thesaurus newThesaurus()
	{
		String className	=
			System.getProperty( "thesaurus.class" );

		if ( className == null )
		{
			className	= "DefaultThesaurus";
		}

		return this.newThesaurus( className );
	}

	/**	Get a thesaurus of a specified class name.
	 *
	 *	@param	className	Class name for the thesaurus.
	 *
	 *	@return				The thesaurus.
	 */

	public Thesaurus newThesaurus( String className )
	{
		Thesaurus thesaurus	= null;

		try
		{
			thesaurus	=
				(Thesaurus)Class.forName( className ).newInstance();
		}
		catch ( Exception e )
		{
			String fixedClassName	=
				ClassUtils.packageName( this.getClass().getName() ) +
				"." + className;

			try
			{
				thesaurus	=
					(Thesaurus)Class.forName(
						fixedClassName ).newInstance();
			}
			catch ( Exception e2 )
			{
				System.err.println(
					"Unable to create thesaurus of class " +
					fixedClassName + ", using default." );

				try
				{
					thesaurus	= new DefaultThesaurus();
				}
				catch ( Exception e3 )
				{
								//	Assume higher-level code will
								//	catch null thesaurus.
/*
					System.err.println(
						"Unable to create thesaurus, " +
						"MorphAdorner cannot continue." );

					System.exit( 1 );
*/
				}
			}
		}

		return thesaurus;
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



