package edu.northwestern.at.utils.corpuslinguistics.spellingstandardizer;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.spellcheck.*;
import edu.northwestern.at.utils.corpuslinguistics.phonetics.*;
import edu.northwestern.at.utils.corpuslinguistics.stringsimilarity.*;

/**	NoopSpellingStandardizer returns original spelling unchanged.
 */

public class NoopSpellingStandardizer
	extends AbstractSpellingStandardizer
	implements SpellingStandardizer
{
	/**	Create noop spelling standardizer.
	 */

	public NoopSpellingStandardizer()
	{
	}

	/**	Loads alternative spellings from a reader.
	 *
	 *	@param	reader		The reader.
	 *	@param	delimChars	Delimiter characters separating spelling pairs.
	 *
	 *	<p>
	 *	Unused in this standardizer.
	 *	</p>
	 */

	public void loadAlternativeSpellings
	(
		Reader reader ,
		String delimChars
	)
		throws IOException
	{
	}

	/**	Returns standard spellings given a spelling.
	 *
	 *	@param	spelling	The spelling.
	 *
	 *	@return				The standard spellings as an array of String.
	 */

	 public String[] standardizeSpelling( String spelling )
	 {
	 	return new String[]{ spelling };
	 }

	/**	Returns a standard spelling given a standard or alternate spelling.
	 *
	 *	@param	spelling	The spelling.
	 *	@param	wordClass	The word class.
	 *
	 *	@return				The standard spelling.
	 */

	public String standardizeSpelling( String spelling , String wordClass )
	{
		return spelling;
	}

	/**	Return standardizer description.
	 *
	 *	@return		Standardizer description.
	 */

	public String toString()
	{
		return "Noop Spelling Standardizer";
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


