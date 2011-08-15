package edu.northwestern.at.utils.corpuslinguistics.lexicon;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import java.net.URL;

import edu.northwestern.at.utils.ListFactory;
import edu.northwestern.at.utils.UnicodeReader;

/**
 * A {@link java.util.HashMap} that maps from lexical entry
 * ({@link java.lang.String}) to possible POS categories
 * ({@link java.util.List}
 */

public class BrillLexicon extends HashMap<String, List<String>>
{
	/**	Create a lexicon.
	 *
	 *	@param	lexiconURL	URL for the file containing the lexicon.
	 *	@param	encoding	Character encoding of lexicon file text.
	 */

	public BrillLexicon( URL lexiconURL , String encoding )
		throws IOException
	{
		String line;

		BufferedReader lexiconReader;

		if ( encoding == null )
		{
			lexiconReader =
				new BufferedReader(
					new UnicodeReader(
						lexiconURL.openStream() ) );
		}
		else
		{
			lexiconReader	=
				new BufferedReader(
					new UnicodeReader(
						lexiconURL.openStream() , encoding ) );
		}

		line	= lexiconReader.readLine();

		String entry	= "";
		List<String> categories;

		while ( line != null )
		{
			line	= line.trim();

			if ( line.length() > 0 )
			{
				StringTokenizer tokens = new StringTokenizer( line );

				entry		= tokens.nextToken();
				categories	= ListFactory.createNewList();

				while ( tokens.hasMoreTokens() )
				{
					categories.add( tokens.nextToken() );
				}

				put( entry , categories );
			}

			line = lexiconReader.readLine();
		}
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



