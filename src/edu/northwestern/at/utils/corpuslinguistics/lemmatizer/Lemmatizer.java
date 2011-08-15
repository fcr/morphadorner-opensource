package edu.northwestern.at.utils.corpuslinguistics.lemmatizer;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.utils.corpuslinguistics.lexicon.*;
import edu.northwestern.at.utils.corpuslinguistics.tokenizer.*;

/**	Interface for a Lemmatizer.
 */

public interface Lemmatizer
{
	/**	Set the lexicon which may provide lemmata.
	 *
	 *	@param	lexicon		The lexicon.
	 */

	public void setLexicon( Lexicon lexicon );

	/**	Set the dictionary for checking lemmata.
	 *
	 *	@param	dictionary		The dictionary as a string set.
	 *							May be null.
	 */

	public void setDictionary( Set<String> dictionary );

	/**	Returns a lemma given a spelling.
	 *
	 *	@param	spelling	The spelling.
	 *
	 *	@return				The lemma.  "*" is returned if the lemma
	 *						cannot be found.
	 */

	public String lemmatize( String spelling );

	/**	Returns a lemma given a spelling and a part of speech.
	 *
	 *	@param	spelling	The spelling.
	 *	@param	wordClass	The word class.
	 *
	 *	@return				The lemma.  "*" is returned if the lemma
	 *						cannot be found.
	 *
	 *	<p>
	 *	The word class should be a major word class as defined in
	 *	{@link edu.northwestern.at.utils.corpuslinguistics.partsofspeech.PartOfSpeech}.
	 *	</p>
	 */

	public String lemmatize( String spelling , String wordClass );

	/**	Check for words that cannot be lemmatized.
	 *
	 *	@param	spelling	The spelling to be lemmatized.
	 *
	 *	@return				true if spelling is not a lemmatizable
	 *							word -- e.g., it contains punctuation,
	 *							is a number, or is a Roman numeral.
	 */

	public boolean cantLemmatize( String spelling );

	/**	Get the lemma separator string,
	 *
	 *	@return	String to separate lemmata in compound lemma.
	 */

	public String getLemmaSeparator();

	/**	Join separate lemmata into a compound lemma.
	 *
	 *	@param	lemmata	String array of lemmata.
	 *	@param	separator	String to separate lemmata.
	 *
	 *	@return				String containing joined lemmata.
	 *							The lemmata are separated by the
	 *							specified separator character.
	 */

	public String joinLemmata( String[] lemmata , String separator );

	/**	Join separate lemmata into a compound lemma.
	 *
	 *	@param	lemmata	String array of part of speech lemmas.
	 *
	 *	@return				String containing joined lemmata.
	 *							The lemmata are separated by the
	 *							default separator character.
	 */

	public String joinLemmata( String[] lemmata );

	/**	Split compound lemma into separate lemmata.
	 *
	 *	@param	lemma		The compound lemma.
	 *
	 *	@return				String array of lemmata.  Only one entry if
	 *							lemma is not a compound lemma.
	 */

	public String[] splitLemma( String lemma );

	/**	Check if lemma is compound lemma.
	 *
	 *	@param	lemma		The lemma.
	 *
	 *	@return					true if lemma is compound lemma.
	 */

	public boolean isCompoundLemma( String lemma );

	/**	Get number of lemmata comprising this lemma.
	 *
	 *	@param	lemma	The lemma.
	 *
	 *	@return			Count of individual lemmata
	 *					comprising this lemma.
	 */

	public int countLemmata( String lemma );
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



