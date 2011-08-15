package edu.northwestern.at.utils.corpuslinguistics.spellingstandardizer;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.net.*;
import java.util.*;

import edu.northwestern.at.utils.*;

/**	Interface for a Spelling Standardizer.
 */

public interface SpellingStandardizer
{
	/**	Loads alternate spellings from a URL.
	 *
	 *	@param	url			URL containing alternate spellings to
	 *							standard spellings mappings.
	 *	@param	encoding	Character set encoding for spellings
	 *	@param	delimChars	Delimiter characters separating spelling pairs
	 */

	public void loadAlternativeSpellings
	(
		URL url ,
		String encoding ,
		String delimChars
	)
		throws IOException;

	/**	Loads alternative spellings from a reader.
	 *
	 *	@param	reader		The reader.
	 *	@param	delimChars	Delimiter characters separating spelling pairs.
	 */

	public void loadAlternativeSpellings
	(
		Reader reader ,
		String delimChars
	)
		throws IOException;

	/**	Load alternate to standard spellings by word class.
	 *
	 *	@param	url			URL of alternative spellings by word class.
	 *	@param	encoding	Character set encoding for spellings
	 */

	public void loadAlternativeSpellingsByWordClass
	(
		URL url ,
		String encoding
	)
		throws IOException;

	/**	Loads standard spellings from a URL.
	 *
	 *	@param	url			URL containing standard spellings
	 *	@param	encoding	Character set encoding for spellings
	 */

	public void loadStandardSpellings
	(
		URL url ,
		String encoding
	)
		throws IOException;

	/**	Loads standard spellings from a reader.
	 *
	 *	@param	reader		The reader.
	 */

	public void loadStandardSpellings
	(
		Reader reader
	)
		throws IOException;

	/**	Sets map which maps alternate spellings to standard spellings.
	 *
	 *	@param	standardMappedSpellings	TaggedStrings with alternate
	 *											spellings as keys and standard
	 *											spellings as tag values.
	 */

	public void setMappedSpellings( TaggedStrings standardMappedSpellings );

	/**	Sets standard spellings.
	 *
	 *	@param	standardSpellings	Set of standard spellings.
	 */

	public void setStandardSpellings( Set<String> standardSpellings );

	/**	Add a mapped spelling.
	 *
	 *	@param	alternateSpelling	The alternate spelling.
	 *	@param	standardSpelling	The corresponding standard spelling.
	 */

	public void addMappedSpelling
	(
		String alternateSpelling ,
		String standardSpelling
	);

	/**	Add a standard spelling.
	 *
	 *	@param	standardSpelling	A standard spelling.
	 */

	public void addStandardSpelling
	(
		String standardSpelling
	);

	/**	Add standard spellings from a collection.
	 *
	 *	@param	standardSpellings	A collection of standard spellings.
	 */

	public void addStandardSpellings
	(
		Collection<String> standardSpellings
	);

	/**	Preprocess spelling.
	 *
	 *	@param	spelling	Spelling to preprocess before standardization.
	 *
	 *	@return				Preprocessed spelling, ready for standardization.
	 */

	public String preprocessSpelling( String spelling );

	/**	Returns standard spellings given a spelling.
	 *
	 *	@param	spelling	The spelling.
	 *
	 *	@return				The standard spellings as an array of String.
	 */

	 public String[] standardizeSpelling( String spelling );

	/**	Returns a standard spelling given a standard or alternate spelling.
	 *
	 *	@param	spelling	The spelling.
	 *	@param	wordClass	The word class.
	 *
	 *	@return				The standard spelling.
	 */

	 public String standardizeSpelling( String spelling , String wordClass );

	/**	Fix capitalization of standardized spelling.
	 *
	 *	@param	spelling			The original spelling.
	 *	@param	standardSpelling	The candidate standard spelling.
	 *
	 *	@return						Standard spelling with initial
	 *								capitalization matching original
	 *								spelling.
	 */

	public String fixCapitalization
	(
		String spelling ,
		String standardSpelling
	);

	 /** Returns number of alternate spellings.
	  *
	  *	@return		The number of alternate spellings.
	  */

	public int getNumberOfAlternateSpellings();

	 /** Returns number of alternate spellings by word class.
	  *
	  *	@return		int array with two entries.
	  *				[0]	=	The number of alternate spellings word classes.
	  *				[1]	=	The number of alternate spellings in the
	  *						word classes.
	  */

	public int[] getNumberOfAlternateSpellingsByWordClass();

	 /** Returns number of standard spellings.
	  *
	  *	@return		The number of standard spellings.
	  */

	public int getNumberOfStandardSpellings();

	/**	Return the spelling map.
	 *
	 *	@return		The spelling map as a TaggedStrings object.
	 *				May be null if this standardizer does not use such a
	 *				map.
	 */

	public TaggedStrings getMappedSpellings();

	/**	Return the standard spellings.
	 *
	 *	@return		The standard spellings as a Set.  May be null.
	 */

	public Set<String> getStandardSpellings();
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



