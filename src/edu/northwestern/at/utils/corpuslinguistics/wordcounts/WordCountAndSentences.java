package edu.northwestern.at.utils.corpuslinguistics.wordcounts;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.net.*;
import java.util.*;

import edu.northwestern.at.utils.*;

/**	Contains a word, its count, and sentence numbers in which it appears.
 */

public class WordCountAndSentences implements Comparable
{
	/**	The word.  Usually a String or AdornedWord. */

	public Comparable word;

	/**	The word count. */

	public int count;

	/**	Set of sentence numbers in which the word appears. */

	public Set<Integer> sentences;

	/**	Create empty word count and sentences.
	 */

	public WordCountAndSentences()
	{
		this.word		= null;
		this.count		= 0;
		this.sentences	= new TreeSet<Integer>();
	}

	/**	Create word count and sentences from word.
	 */

	public WordCountAndSentences( Comparable word )
	{
		this.word		= word;
		this.count		= 0;
		this.sentences	= new TreeSet<Integer>();
	}

 	/**	Compare this count data object with another.
 	 *
 	 *	@param	other	The count data object
 	 *
	 *	@return			< 0 if this count data object is less than the other,
	 *					= 0 if the two count data objects are equal,
	 *					> 0 if this count data object is greater than the other.
 	 */

	public int compareTo( Object other )
	{
		int result	= 0;

		if ( ( other == null ) ||
			!( other instanceof WordCountAndSentences ) )
		{
			result	= Integer.MIN_VALUE;
		}
		else
		{
			WordCountAndSentences otherCa	= (WordCountAndSentences)other;

			result	= -Compare.compare( count , otherCa.count );

			if ( result == 0 )
			{
				result	=
					Compare.compare
					(
						sentences.size() ,
						otherCa.sentences.size()
					);
			}

			if ( result == 0 )
			{
				if ( ( word != null ) && ( otherCa.word != null ) )
				{
					result	=
						Compare.compare( word , otherCa.word );
				}
			}
		}

		return result;
	}

	/** Return string representation of word counts and sentences.
	 *
	 *	@return		String representation.
	 */

	public String toString()
	{
		String result;

		if ( word == null )
		{
			result	= "(null) (0) in []";
		}
		else
		{
			result	=
				word.toString() + " (" + count + ") in " + this.sentences;
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



