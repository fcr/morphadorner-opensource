package edu.northwestern.at.morphadorner;

/*	Please see the license information at the end of this file. */

/** MorphAdorner attribute names.
 *
 *	<p>
 *	This class provides the default names for part of speech attributes
 *	in adorned files.  The attributes have both long names and short
 *	names.  The short names are the same as the default value for
 *	the attribute name.  For example, the long attribute name for a
 *	lemma is "lemma", but the short name is "lem", which is also the
 *	default attribute value name.
 *	</p>
 */

public class WordAttributeNames
{
	/**	----- Long names. ----- */

	/**	Word ID attribute.  This should not be changed. */

	public static String wordID				= "xml:id";

	/**	Sentence number attribute. */

	public static String sentenceNumber		= "sn";

	/**	Word number attribute. */

	public static String wordNumber			= "wn";

	/**	Spelling attribute. */

	public static String spelling			= "spe";

	/**	Original token attribute. */

	public static String originalToken		= "tok";

	/**	Part of speech tag attribute. */

	public static String partOfSpeech		= "pos";

	/**	Lemma attribute. */

	public static String lemma				= "lem";

	/**	Sandard spelling attribute. */

	public static String standardSpelling	= "reg";

	/**	Left KWIC index attribute. */

	public static String leftKWIC			= "kl";

	/**	Right KWIC index attribute. */

	public static String rightKWIC			= "rl";

	/**	End of sentence flag attribute. */

	public static String eosFlag			= "eos";

	/**	Word ordinal attribute. */

	public static String wordOrdinal		= "ord";

	/**	Path attribute. */

	public static String path				= "p";

	/**	Part number.  This should not be changed. */

	public static String part				= "part";

	/**	----- Short names. ----- */

	public static String id		= wordID;
	public static String sn		= sentenceNumber;
	public static String wn		= wordNumber;
	public static String spe	= spelling;
	public static String tok	= originalToken;
	public static String pos	= partOfSpeech;
	public static String lem	= lemma;
	public static String reg	= standardSpelling;
	public static String kl		= leftKWIC;
	public static String kr		= rightKWIC;
	public static String eos	= eosFlag;
	public static String ord	= wordOrdinal;
	public static String p		= path;
//	public static String part	= part;
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



