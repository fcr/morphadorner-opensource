package edu.northwestern.at.morphadorner;

/*	Please see the license information at the end of this file. */

import java.util.regex.*;
import edu.northwestern.at.utils.PatternReplacer;

/** MorphAdorner word attribute patterns.
 *
 *	<p>
 *	This class provides patterns for matching some of the
 *	MorphAdorner word attributes.
 *	</p>
 */

public class WordAttributePatterns
{
	/**	<w> tag pattern. */

	public static String wPattern	=
		"^(.*)<w (.*)>(.*)</w>(.*)$";

	/**	<w> tag replacement. */

	public static PatternReplacer wReplacer	=
		new PatternReplacer( wPattern , "" );

	/**	Matcher groups for w. */

	public final static int LEFT	= 1;
	public final static int ATTRS	= 2;
	public final static int WORD	= 3;
	public final static int RIGHT	= 4;

	/**	ID pattern. */

	public static String idPattern	=
		"^(.*)" +
		WordAttributeNames.id +
		"=\\\"([a-z,A-Z,0-9,\\-\\._]*)\\\"(.*)$";

	public final static int IDLEFT	= 1;
	public final static int IDVALUE	= 2;
	public final static int IDRIGHT	= 3;

	/**	ID replacement. */

	public static PatternReplacer idReplacer	=
		new PatternReplacer( idPattern , "" );

	/**	path pattern. */

	public static String pathPattern	=
		"^(.*) " +
		WordAttributeNames.p +
		"=\\\"(.*)\\\" (.*)$";

	public final static int PATHLEFT		= 1;
	public final static int PATHVALUE	= 2;
	public final static int PATHRIGHT	= 3;

	/**	path replacement. */

	public static PatternReplacer pathReplacer	=
		new PatternReplacer( pathPattern , "" );
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



