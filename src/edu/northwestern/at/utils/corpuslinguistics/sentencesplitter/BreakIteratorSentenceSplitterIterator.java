package edu.northwestern.at.utils.corpuslinguistics.sentencesplitter;

/*	Please see the license information at the end of this file. */

import java.text.BreakIterator;
import java.util.Locale;

/**	BreakIterator-based sentence splitter iterator. */

public class BreakIteratorSentenceSplitterIterator
	implements SentenceSplitterIterator
{
	/**	BreakIterator used to iterate over sentences.
	 */

	protected BreakIterator sentenceExtractor;

	/**	Start of current sentence. */

	protected int start	= BreakIterator.DONE;

	/**	End of current sentence. */

	protected int end	= BreakIterator.DONE;

	/**	Text to break up. */

	protected String text;

	/**	Create sentence iterator.
	 */

	public BreakIteratorSentenceSplitterIterator()
	{
		sentenceExtractor	=
			BreakIterator.getSentenceInstance( Locale.US );
	}

	/**	Create sentence iterator with specified locale.
	 *
	 *	@param	locale	The locale.
	 */

	public BreakIteratorSentenceSplitterIterator( Locale locale )
	{
		sentenceExtractor	=
			BreakIterator.getSentenceInstance( locale );
	}

	/**	Create sentence iterator over text.
	 *
	 *	@param	text	The text from which to extract sentences.
	 */

	public BreakIteratorSentenceSplitterIterator( String text )
	{
		sentenceExtractor	=
			BreakIterator.getSentenceInstance( Locale.US );

		setText( text );
	}

	/**	Create sentence iterator over text with specified locale.
	 *
	 *	@param	text	The text from which to extract sentences.
	 *	@param	locale	The locale.
	 */

	public BreakIteratorSentenceSplitterIterator( String text , Locale locale )
	{
		sentenceExtractor	=
			BreakIterator.getSentenceInstance( locale );

		setText( text );
	}

	/**	Set the text to split.
	 *
	 *	@param	text	Text to split.
	 */

	public void setText( String text )
	{
		this.text	= text;

		sentenceExtractor.setText( this.text );

		start	= sentenceExtractor.first();
		end 	= sentenceExtractor.next();
	}

	/**	Check if there is another sentence available.
	 *
	 *	@return	true if another sentence is available.
	 */

	public boolean hasNext()
	{
		return ( end != BreakIterator.DONE );
	}

	/**	Return next sentence.
	 *
	 *	@return	next sentence, or null if none.
	 */

	public String next()
	{
		String result	= null;

		if ( end != BreakIterator.DONE )
		{
			result	= text.substring( start , end );
			start	= end;
			end 	= sentenceExtractor.next();
		}

		return result;
	}

	/**	Return next sentence without advancing sentence pointer.
	 *
	 *	@return	next sentence, or null if none.
	 */

	public String peek()
	{
		String result	= null;

		if ( end != BreakIterator.DONE )
		{
			result	= text.substring( start , end );
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



