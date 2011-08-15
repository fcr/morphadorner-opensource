package edu.northwestern.at.utils.corpuslinguistics.namestandardizer;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;

import edu.northwestern.at.utils.corpuslinguistics.lexicon.*;

/**	NoopNameStandardizer -- returns original proper name untouched.
 */

public class NoopNameStandardizer
	implements NameStandardizer
{
	/**	Load spelling data.
	 *
	 *	@param	properNamesFileName		File containing proper names data.
	 *
	 *	<p>
	 *	Does nothing here.
	 *	</p>
	 */

	public void loadNames( String properNamesFileName )
		throws IOException
	{
	}

	/**	Load names from a lexicon.
	 *
	 *	@param	lexicon		The lexicon from which to load names.
	 *
	 *	<p>
	 *	Does nothing here.
	 *	</p>
	 */

	public void loadNamesFromLexicon( Lexicon lexicon )
		throws IOException
	{
	}

	/**	Return number of names.
	 *
	 *	@return		Always returns 0.
	 */

	public int getNumberOfNames()
	{
		return 0;
	}

	/**	Check if we should not standardize a name.
	 *
	 *	@param	properName	Name to check.
	 *
	 *	@return				Always true to avoid standardizing name.
	 */

	public boolean dontStandardize( String properName )
	{
		return true;
	}

	/**	Preprocess proper name.
	 *
	 *	@param	properName	Proper name to preprocess before standardization.
	 *
	 *	@return				Proper name untouched.
	 */

	public String preprocessProperName( String properName )
	{
		return properName;
	}

	/**	Returns standard proper name given a proper name.
	 *
	 *	@param	properName	The proper name.
	 *
	 *	@return				The propert name untouched.
	 */

	public String standardizeProperName( String properName )
	{
		return properName;
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



