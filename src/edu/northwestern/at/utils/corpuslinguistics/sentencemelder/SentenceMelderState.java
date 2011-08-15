package edu.northwestern.at.utils.corpuslinguistics.sentencemelder;

/*	Please see the license information at the end of this file. */

import java.util.*;

import com.megginson.sax.*;

/**	Sentence melder state.
 *
 *	<p>
 *	All of the state variables have package scope.
 *	Classes outside the sentence melder
 *	package should treat the state as an opaque object.
 *	</p>
 */

public class SentenceMelderState
{
	/**	True if we're in a double quoted portion of the text. */

	boolean inDoubleQuotes	= false;

	/**	Index of the starting double quote. */

	int doubleQuoteStart		= -1;

	/**	Index of the starting single quote. */

	int singleQuoteStart		= -1;

	/**	True if we're in a single quoted portion of the text. */

	boolean inSingleQuotes	= false;

	/**	Previous word token. */

	String previousWord		= "";

	/**	Remembers the number of words processed so far. */

	int wordsDone			= 0;

	/**	Holds reconstituted sentence. */

	StringBuffer sb	= new StringBuffer();

	/** XML writer for XML output. */

	XMLWriter xmlWriter	= null;

	/**	XML element URI. */

	String elementURI	= "";

	/**	Create sentence melder state.
	 */

	SentenceMelderState()
	{
	}

	/**	Reset the melder.
	 *
	 *	<p>
	 *	Clears the internal state which tracks quote mark pairing,
	 *	word count, and the previous word encountered.  Usually called
	 *	between text divisions such as paragraphs.
	 *	</p>
	 */

	public void reset()
	{
		previousWord		= "";
		inSingleQuotes		= false;
		inDoubleQuotes		= false;
		wordsDone			= 0;
		doubleQuoteStart	= -1;
		singleQuoteStart	= -1;
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



