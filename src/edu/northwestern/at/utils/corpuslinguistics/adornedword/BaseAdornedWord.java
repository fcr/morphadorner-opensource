package edu.northwestern.at.utils.corpuslinguistics.adornedword;

/*	Please see the license information at the end of this file. */

import java.io.Serializable;
import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.tokenizer.*;

/**	A word adorned with addition morphological information.
 *
 *	<p>
 *	An {@link edu.northwestern.at.utils.corpuslinguistics.adornedword.AdornedWord}
 *	represents a single word spelling,
 *	symbol, or punctuation mark in text.
 *	</p>
 *
 *	<p>
 *	An adorned word records the following information.
 *	</p>
 *
 *	<ul>
 *		<li>The original form of the token as it appears in input text.
 *			</li>
 *		<li>The spelling form of the token with transcription errors
 *			corrected.  This is the form used by MorphAdorner for
 *			subsequent morphological processing.
 *			</li>
 *		<li>A standard spelling.  This is the standard modern form
 *			of the spelling for a word.  If the word is obsolete, the
 *			standard form is the most recent commonly used spelling.
 *			</li>
 *		<li>A lemma.  This is the standard dictionary headword form
 *			of the spelling.  Some spellings such as contractions
 *			contain multiple lemmata.  In this case, the lemma
 *			consists of a string of individual lemmata separated
 *			by a separator character, usually a vertical bar "|".
 *			For example, the lemma form of
 *			the contraction "isn't" is "be|not".
 *			</li>
 *		<li>A part of speech.  Each lemma form in the spelling
 *			has an associated part of speech.  Depending upon
 *			the tag set, the individual parts of speech may be
 *			separated by colons, slashes, vertical bars, etc.
 *			</li>
 *		<li>The token type.  This gives the broad orthographic class
 *			of the spelling such as word, symbol, currency, number,
 *			etc.
 *			</li>
 *	</ul>
 */

public class BaseAdornedWord
	implements AdornedWord , XCloneable , Comparable , Serializable
{
	/**	Token form of spelling. */

	protected String token;

	/**	Corrected spelling. */

	protected String spelling;

	/**	Standardized spelling. */

	protected String standardSpelling;

	/**	Lemmata for spelling. */

	protected String lemmata;

	/**	Parts of speech for spelling. */

	protected String partsOfSpeech;

	/**	Token type. */

	protected int tokenType;

	/**	Create an empty adorned word.
	 */

	public BaseAdornedWord()
	{
		this.token				= "";
		this.spelling			= "";
		this.standardSpelling	= "";
		this.lemmata			= "";
		this.partsOfSpeech		= "";
		this.tokenType			= 0;
	}

	/**	Create adorned word from a spelling.
	 *
	 *	@param	spelling	The spelling.
	 *
	 *	<p>
	 *	The token, the spelling, and the standard spelling
	 *	are all set the the spelling parameter value.
	 *	</p>
	 */

	public BaseAdornedWord( String spelling )
	{
		this.token				= spelling;
		this.spelling			= spelling;
		this.standardSpelling	= spelling;
		this.lemmata			= "";
		this.partsOfSpeech		= "";
		this.tokenType			= 0;
	}

	/**	Create adorned word from a spelling and a part of speech.
	 *
	 *	@param	spelling		The spelling.
	 *	@param	partOfSpeech	The part of speech.
	 */

	public BaseAdornedWord( String spelling , String partOfSpeech )
	{
		this.token				= spelling;
		this.spelling			= spelling;
		this.standardSpelling	= spelling;
		this.lemmata			= "";
		this.partsOfSpeech		= partOfSpeech;
		this.tokenType			= 0;
	}

	/**	Create adorned word from a token, spelling, and part of speech.
	 *
	 *	@param	token			The token.
	 *	@param	spelling		The spelling.
	 *	@param	partOfSpeech	The part of speech.
	 */

	public BaseAdornedWord
	(
		String token ,
		String spelling ,
		String partOfSpeech
	)
	{
		this.token				= token;
		this.spelling			= spelling;
		this.standardSpelling	= spelling;
		this.lemmata			= "";
		this.partsOfSpeech		= partOfSpeech;
		this.tokenType			= 0;
	}

	/**	Create a adorned word from another adorned word.
	 *
	 *	@param	adornedWord		A adorned word.
	 */

	public BaseAdornedWord( AdornedWord adornedWord )
	{
		this.token				= adornedWord.getToken();
		this.spelling			= adornedWord.getSpelling();
		this.standardSpelling	= adornedWord.getStandardSpelling();
		this.lemmata			= adornedWord.getLemmata();
		this.partsOfSpeech		= adornedWord.getPartsOfSpeech();
		this.tokenType			= adornedWord.getTokenType();
	}

	/**	Get the original token.
	 *
	 *	@return		The original token.
	 */

	public String getToken()
	{
		return token;
	}

	/**	Set the original token.
	 *
	 *	@param	token	The original token.
	 */

	public void setToken( String token )
	{
		this.token	= token;
	}

	/**	Get the spelling.
	 *
	 *	@return		The spelling.
	 */

	public String getSpelling()
	{
		return spelling;
	}

	/**	Set the spelling.
	 *
	 *	@param	spelling	The spelling.
	 */

	public void setSpelling( String spelling )
	{
		this.spelling	= spelling;
	}

	/**	Get the standard spelling.
	 *
	 *	@return		The standard spelling.
	 */

	public String getStandardSpelling()
	{
		return standardSpelling;
	}

	/**	Set the standard spelling.
	 *
	 *	@param	standardSpelling	The standard spelling.
	 */

	public void setStandardSpelling( String standardSpelling )
	{
		this.standardSpelling	= standardSpelling;
	}

	/**	Get the lemmata.
	 *
	 *	@return		The lemmata.
	 *
	 *	<p>
	 *	Compound lemmata are separated by a separator tring.
	 *	</p>
	 */

	public String getLemmata()
	{
		return lemmata;
	}

	/**	Set the lemmata.
	 *
	 *	@param	lemmata		The lemmata.
	 */

	public void setLemmata( String lemmata )
	{
		this.lemmata	= lemmata;
	}

	/**	Get the parts of speech.
	 *
	 *	@return		The parts of speech.
	 */

	public String getPartsOfSpeech()
	{
		return partsOfSpeech;
	}

	/**	Set the parts of speech.
	 *
	 *	@param	partsOfSpeech	The parts of speech, separated by
	 *								a tag set dependent separator
	 *								string.
	 */

	public void setPartsOfSpeech( String partsOfSpeech )
	{
		this.partsOfSpeech	= partsOfSpeech;
	}

	/**	Get the token type.
	 *
	 *	@return		The token type.
	 */

	public int getTokenType()
	{
		return tokenType;
	}

	/**	Set the token type.
	 *
	 *	@param	tokenType	The token type.
	 */

	public void setTokenType( int tokenType )
	{
		this.tokenType	= tokenType;
	}

    /**	Check if another object is equal to this one.
     *
     *	@param	object  Other object to test for equality.
     *
     *	@return			true if other object is equal to this one.
     *
     *	<p>
     *	Two word objects are equal if their spellings, lemmata, and
     *	parts of speech are equal.
     *	</p>
     */

	public boolean equals( Object object )
	{
		boolean result	= false;

		if ( object instanceof BaseAdornedWord )
		{
			AdornedWord otherAdornedWord	= (AdornedWord)object;

			result	=
				( spelling.equals( otherAdornedWord.getSpelling() ) ) &&
				( lemmata.equals( otherAdornedWord.getLemmata() ) ) &&
				( partsOfSpeech.equals( otherAdornedWord.getPartsOfSpeech() ) );
		}

		return result;
	}

 	/**	Compare this key with another.
 	 *
 	 *	@param	object		The other CompoundKey.
 	 *
 	 *	@return				-1, 0, 1 depending opne whether this
 	 *						adorned word is less than, equal to, or
 	 *						greater than another adorned word.
 	 *
 	 *	<p>
     *	Two word objects are compared first on their spellings,
     *	then their lemmata, and finally their parts of speech.
 	 *	</p>
 	 */

	public int compareTo( Object object )
	{
		int result	= 0;

		if ( ( object == null ) ||
			!( object instanceof AdornedWord ) )
		{
			result	= Integer.MIN_VALUE;
		}
		else
		{
			AdornedWord otherAdornedWord	= (AdornedWord)object;

			result	= spelling.compareTo( otherAdornedWord.getSpelling() );

			if ( result == 0 )
			{
				result	= lemmata.compareTo( otherAdornedWord.getLemmata() );
			}

			if ( result == 0 )
			{
				result	=
					partsOfSpeech.compareTo(
						otherAdornedWord.getPartsOfSpeech() );
			}
		}

		return result;
	}

    /**	Get the hash code of the keys.
     *
     *	@return		The hash code.
     */

	public int hashCode()
	{
		return
			spelling.hashCode() ^
			lemmata.hashCode() ^
			partsOfSpeech.hashCode();
	}

	/**	Return a string representation of this object.
	 *
	 *	@return		A string representation of this object = original spelling.
	 */

	public String toString()
	{
		return spelling;
	}

	/**	Return a shallow cloned copy of this object.
	 *
	 *	@return		Shallow clone of this object.
	 *
	 *	@throws		CloneNotSupportedException which should never happen.
	 */

	public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch ( CloneNotSupportedException e )
		{
			throw new InternalError();
		}
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



