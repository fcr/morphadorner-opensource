package edu.northwestern.at.morphadorner;

import edu.northwestern.at.utils.*;

/*	Please see the license information at the end of this file. */

/**	Holds sentence and word number data for a word. */

public class SentenceAndWordNumber implements Comparable
{
	/**	The word ID. */

	protected String id;

	/**	The word ordinal. */

	protected int ord;

	/**	Word part flag.
	 *
	 *	<p>
	 *	N for an unsplit word.
	 *	I for the the first part of a split word.
	 *	M for the middle part(s) of a split word.
	 *	F for the final part of a split word.
	 *	</p>
	 */

	protected String part;

	/**	The end-of-sentence flag. */

	protected boolean eos;

	/**	The word number within the sentence. */

	protected int wordNumber;

	/**	The sentence number for the word. */

	protected int sentenceNumber;

	/**	Create SentenceAndWordNumber entry.
	 *
	 *	@param	id		The word ID.
	 *	@param	ord		The word ordinal.
	 *	@param	part	The word part flag.
	 *	@param	eos		True if word at end of sentence.
	 */

	public SentenceAndWordNumber
	(
		String id ,
		int ord ,
		String part ,
		boolean eos
	)
	{
		this.id				= id;
		this.ord			= ord;
		this.part			= part;
		this.eos			= eos;
		this.sentenceNumber	= -1;
		this.wordNumber		= -1;
	}

	/**	Set sentence number and word number.
	 *
	 *	@param	wordNumber		The word number within the sentence.
	 *	@param	sentenceNumber	The sentence number.
	 */

	public void setSentenceAndWordNumber
	(
		int sentenceNumber ,
		int wordNumber
	)
	{
		this.sentenceNumber	= sentenceNumber;
		this.wordNumber		= wordNumber;
	}

	/**	Get word ID.
	 *
	 *	@return		The word ID.
	 */

	public String getID()
	{
		return id;
	}

	/**	Set word ID.
	 *
	 *	@param	id	The word ID.
	 */

	public void setID( String id )
	{
		this.id	= id;
	}

	/**	Get word ordinal.
	 *
	 *	@return		The word ordinal.
	 */

	public int getOrd()
	{
		return ord;
	}

	/**	Check if word is first (or only) part of a split word.
	 *
	 *	@return		True if word is first part of a split word or
	 *				only part of a non-split word.
	 */

	public boolean isFirstPart()
	{
		return part.equals( "N" ) || part.equals( "I" );
	}

	/**	Check if word is middle (or only) part of a split word.
	 *
	 *	@return		True if word is middle part of a split word or
	 *				only part of a non-split word.
	 */

	public boolean isMiddlePart()
	{
		return part.equals( "N" ) || part.equals( "M" );
	}

	/**	Check if word is last (or only) part of a split word.
	 *
	 *	@return		True if word is last part of a split word or
	 *				only part of a non-split word.
	 */

	public boolean isLastPart()
	{
		return part.equals( "N" ) || part.equals( "F" );
	}

	/**	Get sentence number for word.
	 *
	 *	@return		The sentence number in which this word appears.
	 */

	public int getSentenceNumber()
	{
		return sentenceNumber;
	}

	/**	Get word number in sentence.
	 *
	 *	@return		The word number in the sentence.
	 */

	public int getWordNumber()
	{
		return wordNumber;
	}

	/**	Get end of sentence flag.
	 *
 	 *	@return		The end of sentence flag.
 	 */

	public boolean getEOS()
	{
		return eos;
	}

 	/**	Compare this object with another.
	 *
	 *	@param	object	The other object.
 	 *
	 *	@return			< 0 if the other object is less than this one,
	 *					= 0 if the two objects are equal,
	 *					> 0 if the other object is greater than this one.
 	 *
	 *	<p>
 	 *	We compare the word IDs only.
 	 *	</p>
 	 */

	public int compareTo( Object object )
	{
		int result	= 0;

		if ( ( object == null ) ||
			!( object instanceof SentenceAndWordNumber ) )
		{
			result	= Integer.MIN_VALUE;
		}
		else
		{
			result	=
				Compare.compare
				(
					id , ((SentenceAndWordNumber)object).getID()
				);
		}

		return result;
	}

	/**	Hash code is hash value for word ID.
	 *
	 *	@return		The hash value.
	 */

	public int hashCode()
	{
		return id.hashCode();
	}

	/**	Return a string representation of this object.
	 *
     *	@return		A string representation of this object.
	 */

	public String toString()
	{
		return
			"id=" + id +
			", ord=" + ord +
			", sn=" + sentenceNumber +
			", wn=" + wordNumber +
			", eos=" + eos;
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



