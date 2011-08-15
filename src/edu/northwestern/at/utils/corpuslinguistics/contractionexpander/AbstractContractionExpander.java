package edu.northwestern.at.utils.corpuslinguistics.contractionexpander;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.logger.*;

/**	Abstract ContractionExpander.
 */

abstract public class AbstractContractionExpander
	extends IsCloseableObject
	implements ContractionExpander, UsesLogger
{
	/**	Contracted spelings.
	 *
	 *	<p>
	 *	Contracted spellings are stored as TaggedStrings.
	 *	The tag is the contracted form.
	 *	The value is the uncontracted forn.
	 *	</p>
	 */

	protected TaggedStrings	contractedSpellings	=
		new TaggedStringsMap();

	/**	Logger used for output. */

	protected Logger logger	= new DummyLogger();

	/**	Create abstract contraction expander.
	 */

	public AbstractContractionExpander()
	{
	}

	/**	Load map from contracted to expanded spellings.
	 *
	 *	@param	reader			Reader from which to read contractions.
	 *
	 *	@throws	IOException		When contracted spellings cannot be read.
	 */

	protected void loadContractedSpellings( Reader reader )
		throws IOException
	{
		String line = null;

		String contraction	= "";
		String expanded		= "";
		String[] tokens		= new String[ 2 ];

		BufferedReader bufferedReader	=
			new BufferedReader( reader );

		while ( ( line = bufferedReader.readLine() ) != null )
		{
			line	= line.trim();

			if ( line.length() == 0 ) continue;

			if ( line.charAt( 0 ) == '#' ) continue;

			tokens	= line.split( "\t" );

			if ( tokens.length >= 2 )
			{
				contractedSpellings.putTag( tokens[ 0 ] , tokens[ 1 ] );
			}
		}

		bufferedReader.close();
	}

	/**	Returns an expanded spelling for a contracted spelling.
	 *
	 *	@param	spelling	The spelling.
	 *
	 *	@return				The expanded form of the contraction.
	 */

	public String expandContraction( String spelling )
	{
		String result	= spelling;

		if ( contractedSpellings.containsString( spelling.toLowerCase() ) )
		{
			result	= contractedSpellings.getTag( spelling.toLowerCase() );
		}

		return result;
	}

	/**	Get the logger.
	 *
	 *	@return		The logger.
	 */

	public Logger getLogger()
	{
		return logger;
	}

	/**	Set the logger.
	 *
	 *	@param	logger		The logger.
	 */

	public void setLogger( Logger logger )
	{
		this.logger	= logger;
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



