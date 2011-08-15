package edu.northwestern.at.utils.corpuslinguistics.adornedword;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.utils.corpuslinguistics.stopwords.*;

/**	An Adorned word filter for Martin Porter's stop word list.
 *
 *	<p>
 *	These English stop words were suggested by Martin Porter.
 *	</p>
 */

public class PorterStopWordFilter implements AdornedWordFilter
{
	/**	Stop words. */

	protected static StopWords stopWords	=
		new PorterStopWords();

	/**	Create the stop word filter.
	 */

	public PorterStopWordFilter()
	{
	}

	/**	Tests if a specified adorned word should be included.
	 *
	 *	@param	adornedWord		The adorned word to check.
	 *
	 *	@return					true to accept the adorned word.
	 */

	public boolean accept( AdornedWord adornedWord )
	{
								//	Get spelling in lowercase.

		String spelling	= adornedWord.getSpelling().toLowerCase();

								//	Accept the spelling only if it does
								//	not appear in the list of stop words.

		return !stopWords.isStopWord( spelling );
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



