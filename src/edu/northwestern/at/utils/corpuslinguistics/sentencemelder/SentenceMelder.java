package edu.northwestern.at.utils.corpuslinguistics.sentencemelder;

/*	Please see the license information at the end of this file. */

import java.util.*;

/**	Sentence melder
 *
 *	<p>
 *	Constructs a formatted sentence by melding a list of sentence tokens
 *	containing words and punctuation.
 *	</p>
 *
 *	<p>
 *	Example:  Printing reconstituted sentence text.
 *	</p>
 *
 *  <p>
 *	Assume "sentences" is a java.util.List of sentences.
 *	Each entry in "sentences" is a sentence -- a list of words expressed
 *	as a java.util.list of word objects.  The word objects may be simple
 *	strings or any object implementing a "toString()" method that yields
 *	the text associated with that word.  In the following example, we
 *	use String as the type for the word objects.
 *	</p>
 *
 *	<p>
 *	<code>
 *	<pre>
 *	SentenceMelder melder	= new SentenceMelder();
 *
 *	for ( int i = 0 ; i < sentences.size() ; i++ )
 *	{
 *	    List<String> sentence	= sentences.get( i );
 *
 *	    String sentenceText	=
 *	        melder.reconstituteSentence( sentences.get( i ) );
 *
 *	    System.out.println( sentenceText );
 *	}
 *	</pre>
 *	</code>
 *	</p>
 */

public class SentenceMelder
{
	/**	Left single curly quote. */

	protected static final String LSQUOTESTR	= "\u2018";

	/**	Right single curly quote. */

	protected static final String RSQUOTESTR	= "\u2019";

	/**	Left double curly quote. */

	protected static final String LDQUOTESTR	= "\u201C";

	/**	Right double curly quote. */

	protected static final String RDQUOTESTR	= "\u201D";

	/**	Sentence melder state. */

	protected SentenceMelderState state	= new SentenceMelderState();

	/**	Create sentence melder. */

	public SentenceMelder()
	{
	}

	/**	Reconstitute a list of words to a sentence.
	 *
	 *	@param	sentence		List of words and punctuation in a sentence.
	 *							Normally each word is a plain string,
	 *							but the "toString()" operation is performed
	 *							on each, so it may be any object type
	 *							whose toString() yields a proper word
	 *							(e.g., an AdornedWord).
	 *
	 *	@return					The reconstituted sentence as a string.
	 */

	public String reconstituteSentence( List<?> sentence )
	{
								//	Current word.
		String word;
								//	Initialize sentence.
		startSentence();
								//	Loop over words in sentence.

		for ( int i = 0 ; i < sentence.size() ; i++ )
		{
								//	Get next word in sentence.

			Object wordObject	= sentence.get( i );

			if ( wordObject instanceof String )
			{
				word	= (String)wordObject;
			}
			else
			{
				word	= wordObject.toString();
			}

								//	Output blank if needed.

			if ( shouldOutputBlank( word , ( i == 0 ) ) )
			{

				outputBlank();
			}
								//	Output word.

			processWord( word );
		}

		return endSentence();
	}

	/**	Start sentence.
	 */

	public void startSentence()
	{
								//	Holds reconstituted sentence.

		state.sb			= new StringBuffer();

								//	No previous word.

		state.previousWord	= "";
	}

	/**	Add blank to sentence.
	 */

	public void outputBlank()
	{
		state.sb.append( " " );
	}

	/**	Add word to sentence.
	 *
	 *	@param	word	The word to add.
	 *
	 *	<p>
	 *	In general this should not be called directly.
	 *	</p>
	 */

	protected void outputWord( String word )
	{
								//	Append current word to output.

		state.sb.append( word );
	}

	/**	Process word.
	 *
	 *	@param	word	The word to add.
	 */

	public void processWord( String word )
	{
								//	Append current word to output.

		outputWord( word );
								//	This word is now previous word.

		state.previousWord	= word;

								//	Increment count of words done.
		state.wordsDone++;
	}

	/**	Finish sentence.
	 *
	 *	@return	Returned sentence.
	 */

	public String endSentence()
	{
								//	Get resulting reconstituted string.

		String result	= state.sb.toString().trim();

								//	Fixup multiple quote sequences.
		result			=
			result.replaceAll( "(\"'\")([^\\s])" , "$1 $2" );

		result			=
			result.replaceAll( "('\"')([^\\s])" , "$1 $2" );

								//	Return reconstituted sentence to caller.
		return result;
	}

	/**	Should blank be output.
	 *
	 *	@param	word			The word.
	 *	@param	isFirstWord		First word in sentence.
	 *
	 *	@return					True to output blank before word.
	 */

	public boolean shouldOutputBlank( String word , boolean isFirstWord )
	{
		boolean result	= false;

								//	See if it is terminal punctuation.

		boolean endingPunctuation	=
			word.equals( "?" ) ||
			word.equals( "!" ) ||
			word.equals( "." ) ||
			word.equals( "," ) ||
			word.equals( ";" ) ||
			word.equals( ":" );

								//	Handle double quotes.

		if ( word.equals( "\"" ) )
		{
			state.inDoubleQuotes	= !state.inDoubleQuotes;

			if ( state.inDoubleQuotes )
			{
				if ( !state.previousWord.equals( "'" ) )
				{
					result	= true;
				}

				state.doubleQuoteStart	= state.wordsDone;
			}
			else
			{
								//	Force closure of badly nested
								//	single quotes.

				if	(	state.inSingleQuotes &&
						( state.singleQuoteStart > state.doubleQuoteStart )
					)
				{
					state.singleQuoteStart	= -1;
					state.inSingleQuotes	= false;
				}

				state.doubleQuoteStart	= -1;
			}

			endingPunctuation	= true;
		}
								//	Handle single quotes.

		else if ( word.equals( "'" ) )
		{
			state.inSingleQuotes	= !state.inSingleQuotes;

			if ( state.inSingleQuotes )
			{
				if ( !state.previousWord.equals( "\"" ) )
				{
					result	= true;
				}

				state.singleQuoteStart	= state.wordsDone;
			}
			else
			{
								//	Force closure of badly nested
								//	double quotes.

				if	(	state.inDoubleQuotes &&
						( state.doubleQuoteStart > state.singleQuoteStart )
					)
				{
					state.doubleQuoteStart	= -1;
					state.inDoubleQuotes	= false;
				}

				state.singleQuoteStart	= -1;
			}

			endingPunctuation	= true;
		}
								//	See if we should skip adding
								//	blank before this word.

		if ( !isFirstWord && !endingPunctuation )
		{
			if 	(	state.inDoubleQuotes &&
					state.previousWord.equals( "\"" )
				)
			{
			}
			else if	(	state.inSingleQuotes &&
						state.previousWord.equals( "'" )
					)
			{
			}
			else if ( state.previousWord.equals( "(" ) )
			{
			}
			else if ( state.previousWord.equals( "[" ) )
			{
			}
			else if ( state.previousWord.equals( "{" ) )
			{
			}
			else if ( state.previousWord.equals( LDQUOTESTR ) )
			{
			}
			else if ( state.previousWord.equals( LSQUOTESTR ) )
			{
			}
			else if ( word.equals( ")" ) )
			{
			}
			else if ( word.equals( "]" ) )
			{
			}
			else if ( word.equals( "}" ) )
			{
			}
			else if ( word.equals( "-" ) && state.previousWord.equals( "-" ) )
			{
			}
			else if ( word.equals( RDQUOTESTR ) )
			{
			}
			else if ( word.equals( RSQUOTESTR ) )
			{
			}
			else
			{
				result	= true;
			}
		}

		return result;
	}

	/**	Get state.
	 *
	 *	@return	Sentence melder state.
	  */

	public SentenceMelderState getState()
	{
		return state;
	}

	/**	Set state.
	  *
	  *	@param	state	The sentence melder state.
	  */

	public void setState( SentenceMelderState state )
	{
		this.state	= state;
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
		state.reset();
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



