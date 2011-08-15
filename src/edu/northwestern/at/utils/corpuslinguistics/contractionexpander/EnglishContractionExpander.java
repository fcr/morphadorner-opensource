package edu.northwestern.at.utils.corpuslinguistics.contractionexpander;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.tokenizer.*;

/**	A contraction expander for English. */

public class EnglishContractionExpander
	extends AbstractContractionExpander
	implements ContractionExpander
{
	/**	Path to list of contracted spellings.
	 *
	 *	<p>
	 *	The contracted spellings file contains
	 *	pairs of contracted forms and expanded forms
	 *	separated by a tab character.  Each line of the
	 *	file contains one pair.
	 *	</p>
	 */

	protected static String contractedSpellingsFileName =
		"resources/contractedspellings.txt";

	/**	Contraction tokenizer. */

	protected WordTokenizer contractionTokenizer	=
//		new ContractionTokenizer();
		new PennTreebankTokenizer();

	/**	Create an English contraction expander. */

	public EnglishContractionExpander()
	{
								//	Load irregular forms.
		try
		{
			Reader reader	=
				new UnicodeReader
				(
					getClass().getResourceAsStream
					(
						contractedSpellingsFileName
					),
					"utf-8"
				);

			loadContractedSpellings( reader );

			reader.close();
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}

		System.out.println(
			"# of contractions read: " +
			contractedSpellings.getStringCount() );
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
		else
		{
			List contractionSegments	=
				contractionTokenizer.extractWords( spelling );

			StringBuffer sb	= new StringBuffer();
			String segment;

			for ( int i = 0 ; i < contractionSegments.size() ; i++ )
			{
				if ( i > 0 )
				{
					sb.append( " " );
				}

				segment	= (String)contractionSegments.get( i );

				if ( contractedSpellings.containsString( segment ) )
				{
					segment	=
						contractedSpellings.getTag( segment );
				}

				sb.append( segment );
			}

			result	= sb.toString();
		}

		return result;
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



