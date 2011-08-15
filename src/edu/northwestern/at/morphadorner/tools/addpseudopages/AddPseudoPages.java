package edu.northwestern.at.morphadorner.tools.addpseudopages;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.text.*;
import java.util.*;

import com.megginson.sax.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import edu.northwestern.at.morphadorner.*;
import edu.northwestern.at.morphadorner.tools.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.xml.*;

/**	Add pseudopages to an adorned file.
 *
 *	<p>
 *	Usage:
 *	</p>
 *	<blockquote>
 *	<pre>
 *	java edu.northwestern.at.morphadorner.tools.addpseudopages.AddPseudoPages input.xml output.xml pseudopagesize pageendingdivtypes
 *	</pre>
 *	</blockquote>
 *	<table>
 *	<tr>
 *	<td>input.xml</td><td>Input MorphAdornerd xml file.</td>
 *	</tr>
 *	<tr>
 *	<td>output.xml</td>
 *	<td>Derived adorned file with pseudopage milestones added.
 *	N.B. Existing pseudopage milestones are deleted before the new ones are added.
 *	</td>
 *	<tr>
 *	<td>pseudopagesize</td>
 *	<td>The maximum number of words in each pseudopage.  Default: 300 .
 *	</td>
 *	</tr>
 *	<tr>
 *	<td>pageendingdivtypes</td>
 *	<td>Blank separated list of &lt;div&gt; types which force the closure of
 *	a pseudopage.  Default: chapter volume sermon
 *	</td>
 *	</tr>
 *	</table>
 *	<p>
 *	The derived adorned output file <em>output.xml</em> has pseudopage milestone
 *	elements added approximately every <em>pseudopagesize</em> words.
 *	No distinction is made between main and paratext when generating
 *	the pseudopages.  Each pseudopage starts with a milestone of the form:
 *	</p>
 *	<p>
 *	<blockquote>
 *	&lt;milestone unit="pseudopage" n="<em>n</em>" position="start"&gt;&lt;/milestone&gt;
 *	</blockquote>
 *	</p>
 *	<p>
 *	and ends with a milestone element of the form:
 *	</p>
 *	<p>
 *	<blockquote>
 *	&lt;milestone unit="pseudopage" n="<em>n</em>" position="end"&gt;&lt;/milestone&gt;
 *	</blockquote>
 *	</p>
 *	<p>
 *	The <em>n</em> is the pseudopage number.
 *	</p>
 */

public class AddPseudoPages
{
	/**	Add pseudopage milestones to an adorned file.
	 *
	 *	@param	xmlInputFileName	Input XML file.
	 *	@param	xmlOutputFileName	Output XML file.
	 *	@param	pseudoPageSize		Number of words in a pseudopage.
	 *	@param	pageEndingDivTypes	div types that end a pseudopage.
	 */

	public AddPseudoPages
	(
		String xmlInputFileName ,
		String xmlOutputFileName ,
		int pseudoPageSize ,
		String pageEndingDivTypes
	)
	{
		try
		{
			XMLFilter filter	=
				new PseudoPageAdderFilter
				(
					XMLReaderFactory.createXMLReader() ,
					pseudoPageSize ,
					pageEndingDivTypes
				);

			long startTime		= System.currentTimeMillis();

								//	Add/update pseudopage milestones.

			new FilterAdornedFile
			(
				xmlInputFileName ,
				xmlOutputFileName ,
				filter
			);

			long endTime		=
				( System.currentTimeMillis() - startTime + 999 ) / 1000;

			System.out.println
			(
				"PseudoPage milestones added in " +
				Formatters.formatLongWithCommas( endTime ) + " seconds."
			);
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}

	/**	Main program.
	 */

	public static void main( String args[] )
	{
								//	Default parameter values.

		String pageEndingDivTypes	= "volume chapter sermon";
		int pseudoPageSize			= 300;

								//	Get parameter values.

		if ( args.length < 2 )
		{
			displayUsage();

			System.exit( 0 );
		}

		if ( args.length >= 3 )
		{
			pseudoPageSize	= Integer.parseInt( args[ 2 ] );
		}

		if ( args.length >= 4 )
		{
			pageEndingDivTypes	= args[ 3 ];
		}
								//	Add pseudopage milestones.
		try
		{
			new AddPseudoPages
			(
				args[ 0 ] ,
				args[ 1 ] ,
				pseudoPageSize ,
				pageEndingDivTypes
			);
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}

	/**	Display usage.
	 */

	protected static void displayUsage()
	{
		System.out.println
		(
			"Usage: java AddPseudoPages infile outfile pseudoPageSize pageEndingDivTypes"
		);
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



