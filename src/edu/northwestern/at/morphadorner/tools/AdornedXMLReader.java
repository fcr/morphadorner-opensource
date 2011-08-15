package edu.northwestern.at.morphadorner.tools;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.text.*;
import java.util.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.sentencemelder.*;
import edu.northwestern.at.utils.csv.*;
import edu.northwestern.at.utils.xml.*;

/**	Reads word tag (<w>) information from MorphAdorner XML output.
 */

public class AdornedXMLReader
{
    /** Adorned word information filter. */

	protected ExtendedAdornedWordFilter wordInfoFilter;

	/**	Create adorned XML reader.
	 *
	 *	@param	xmlInputFileName	Input XML file name.
	 */

	public AdornedXMLReader( String xmlInputFileName )
		throws org.xml.sax.SAXException, java.io.IOException
	{
								//	Read XML and load word information.

		readXML( xmlInputFileName );
	}

	/**	Reads adorned XML.
	 *
	 *	@param	xmlInputFileName	XML input file name.
	 */

	protected void readXML( String xmlInputFileName )
		throws org.xml.sax.SAXException, java.io.IOException
	{
								//	Create XML reader for XML input.

		XMLReader reader		=
			XMLReaderFactory.createXMLReader();

								//	Add filter to XML reader to
								//	add path attributes to XML elements.

		AddXMLPathFilter xmlPathFilter	=
			new AddXMLPathFilter
			(
				reader ,
				FileNameUtils.changeFileExtension
				(
					FileNameUtils.stripPathName( xmlInputFileName ) ,
					""
				)
			);
								//	Add filter to XML reader to
								//	pull out "<w>" elements containing
								//	word information.

		 wordInfoFilter	=
			new ExtendedAdornedWordFilter( xmlPathFilter , true );

								//	Parse the XML input.
								//
								//	$$$PIB$$$ So you wonder why
								//	we don't just call
								//
								//	wordInfoFilter.parse( xmlInputFileName );
								//
								//	here.  At least in Java 1.6, the
								//	runtime SAX parser is too stupid to open
								//	some file names correctly.  So we
								//	create the input source ourselves.

		BufferedReader bufferedReader	=
			new BufferedReader
			(
				new UnicodeReader
				(
					new FileInputStream( xmlInputFileName ) ,
					"utf-8"
				)
			);

		InputSource inputSource	= new InputSource( bufferedReader );

		wordInfoFilter.parse( inputSource );

		bufferedReader.close();
	}

	/**	Return list of adorned word IDs.
	 *
	 *	@return	List of adorned word IDs (strings).
	 */

	public List<String> getAdornedWordIDs()
	{
		return wordInfoFilter.getAdornedWordIDs();
	}

	/**	Get adorned words as a list of sentences.
	 *
	 *	@return		List of sentences. Each sentence is a list of
	 *				ExtendedAdornedWord entries.  Sentences are returned
	 *				in reading context order.  Only one ExtendedAdornedWord
	 *				entry is returned for split words.
	 *
	 *	<p>
	 *	This method tries to return sentences in as close to their
	 *	order of appearance in the text as possible.  Sentences from
	 *	intrusive jump tags will generally appear after the text
	 *	section into which they intrude, and so may be dislodged
	 *	an arbitrary distance from their actual position in the text.
	 *	</p>
	 */

	public List<List<ExtendedAdornedWord>> getSentences()
	{
		return wordInfoFilter.getSentences();
	}

	/**	Get adorned word information for a word ID.
	 *
	 *	@param	id	The String word ID.
	 *
	 *	@return		ExtendedAdornedWord for the specified ID.
	 *				Returns null if ID not found.
	 */

	public ExtendedAdornedWord getExtendedAdornedWord( String id )
	{
		return wordInfoFilter.getExtendedAdornedWord( id );
	}

	/**	Get adorned word information for a word index.
	 *
	 *	@param	index	The word index.
	 *
	 *	@return			ExtendedAdornedWord for the specified word index.
	 *					Returns null if index not found.
	 */

	public ExtendedAdornedWord getExtendedAdornedWord( int index )
	{
		return wordInfoFilter.getExtendedAdornedWord( index );
	}

	/**	Get related adorned word IDs for a word ID of a split word.
	 *
	 *	@param	wordID	Word ID for which related IDs are wanted.
	 *
	 *	@return			List of related word IDs.
	 *
	 *	<p>
	 *	Related word IDs are the word IDs for the other parts of
	 *	a split word.  The returned list includes the
	 *	given wordID.
	 *	</p>
	 *
	 *	<p>
	 *	For unsplit words, the single given wordID is returned
	 *	in the list.
	 *	</p>
	 *
	 *	<p>
	 *	Null is returned when the wordID does not exist.
	 *	</p>
	 */

	public List<String> getRelatedSplitWordIDs( String wordID )
	{
		return wordInfoFilter.getRelatedSplitWordIDs( wordID );
	}

	/**	Get related adorned words for split words.
	 *
	 *	@param	adornedWordInfo		Adorned word for which related words
	 *								are wanted.
	 *
	 *	@return						List of related adorned words.
	 *
	 *	<p>
	 *	Related words are those corresponding to the parts of
	 *	a split word.  The returned list includes the given word.
	 *	</p>
	 *
	 *	<p>
	 *	For unsplit words, the single given adorned word is returned
	 *	in the list.
	 *	</p>
	 */

	public List<ExtendedAdornedWord> getRelatedSplitWords
	(
		ExtendedAdornedWord adornedWordInfo
	)
	{
		return wordInfoFilter.getRelatedSplitWords( adornedWordInfo );
	}

	/**	Get index for a word ID.
	 *
	 *	@param	id	The String word ID.
	 *
	 *	@return		Index of word in adorned words list.
	 *				Returns -1 ID not found.
	 */

	public int getAdornedWordIndexByID( String id )
	{
		return wordInfoFilter.getAdornedWordIndexByID( id );
	}

	/**	Get sibling words.
	 *
	 *	@param	wordID	The word ID of the word for which to find siblings.
	 *
	 *	@return			String list of sibling word IDs.
	 *					Empty list if no siblings found.
	 *
	 *	<p>
	 *	Sibling words have the same parent hard or jump tag.
	 *	</p>
	 */

	public List<String> getSiblingWordIDs( String wordID )
	{
		return wordInfoFilter.getSiblingWordIDs( wordID );
	}

	/**	Find words whose paths start with a given string.
	 *
	 *	@param	pattern		The pattern to match.
	 *
	 *	@return				String list of words matching the pattern.
	 *						Empty if no words match.
	 */

	public List<String> findWordsByMatchingLeadingPath( String pattern )
	{
		return wordInfoFilter.findWordsByMatchingLeadingPath( pattern );
	}

	/**	Find words matching a specified path regular expression pattern.
	 *
	 *	@param	pattern		The regular expression pattern to match.
	 *
	 *	@return				String list of words matching the pattern.
	 *						Empty if no words match.
	 */

	public List<String> findWordsByMatchingPath( String pattern )
	{
		return wordInfoFilter.findWordsByMatchingPath( pattern );
	}

	/**	Trim tag number from XML tag.
	 *
	 *	@param	tag		XML tag to trim.
	 *
	 *	@return			Trimmed tag.
	 */

	public String trimTag( String tag )
	{
		return wordInfoFilter.trimTag( tag );
	}

	/**	Split word path into separate tags.
	 *
	 *	@param	path	The word path.
	 *
	 *	@return			String array of split XML tags.
	 *					The trailing "w[]" element is removed.
	 */

	public String[] splitPathFull( String path )
	{
		return wordInfoFilter.splitPathFull( path );
	}

	/**	Split word path into separate tags.
	 *
	 *	@param	path	The word path.
	 *
	 *	@return			String array of split XML tags.
	 *					Both the leading document name tag and
	 *					the trailing word (w[]) tag are removed.
	 */

	public String[] splitPath( String path )
	{
		return wordInfoFilter.splitPath( path );
	}

	/**	Get list of selected word IDs from specified ID range.
	 *
	 *	@param	startingWordID		Starting word ID.
	 *	@param	endingWordID		Ending word ID.
	 *
	 *	@return						String list of selected word IDs.
	 */

	public List<String> getSelectedWordIDs
	(
		String startingWordID ,
		String endingWordID
	)
	{
		return wordInfoFilter.getSelectedWordIDs(
			startingWordID , endingWordID );
	}

	/**	Generate XML for selected word IDs.
	 *
	 *	@param	startingWordID		Starting word ID.
	 *	@param	endingWordID		Ending word ID.
	 *
	 *	@return						Reconstituted XML encompassing
	 *								the selected word IDs.  Empty string
	 *								if one or both of the word IDs
	 *								is invalid or out of order.
	 */

	public String generateXML
	(
		String startingWordID ,
		String endingWordID
	)
	{
								//	Get list of words from starting
								//	word ID through ending word ID.

		List<String> selectedWordIDs	=
			getSelectedWordIDs( startingWordID , endingWordID );

								//	If no words found, return an empty
								//	string as the result.

		if ( selectedWordIDs.size() == 0 )
		{
			return "";
		}
								//	Holds generated XML.

		StringBuffer result		= new StringBuffer();

								//	Stack to hold XML elements in progress.

		Deque<String> tagStack	= new ArrayDeque<String>();

								//	Sentence melder to reconstitute
								//	spellings into readable sentences.

		MySentenceMelder melder	= new MySentenceMelder();

								//	Start first sentence with the
								//	melder.

		melder.startSentence();

								//	Tag classifier to differentiate
								//	hard, soft, and jump tags.

		XMLTagClassifier tagClassifier	= new TEITagClassifier();

								//	Word ID for first word.

		String id	= selectedWordIDs.get( 0 );

								//	Get word.

		ExtendedAdornedWord word	= getExtendedAdornedWord( id );

								//	Word path for first word.

		String path	= word.getPath();

								//	Spelling for first word.

		String spelling	= word.getSpelling();

								//	Split word path into constituent
								//	XML tags.  We ignore the leading
								//	document name and word number.

		String[] tags	= splitPath( path );

								//	Push the leading XML tags from
								//	the word path onto the XML tag stack.

		for ( int i = 0 ; i < tags.length ; i++ )
		{
			String tag	= trimTag( tags[ i ] );

			outputTag( tag , true , melder , tagClassifier );

			tagStack.addFirst( tag );
		}
                                //	Add the first word to the sentence
                                //	output.

		melder.processWord( spelling );

								//	No longer the first word in the
								//	sentence since we just emitted it.

		boolean firstWordInSentence	= false;

								//	Keep the previous word's XML tags.
								//	We need this to determine what
								//	tags change from word to word in order
								//	to emit proper opening and closing
								//	XML tags.

		String[] prevTags	= tags;

								//	Process all the words in the range
								//	of selected words.

		for ( int i = 1 ; i < selectedWordIDs.size() ; i++ )
		{
								//	Get next word ID.

 			id			= selectedWordIDs.get( i );

								//	Get word.

			word		= getExtendedAdornedWord( id );

								//	Get next word path.

 			path		= word.getPath();

								//	Get next spelling.

 			spelling	= word.getSpelling();

								//	Output whitespace if we need to.

			if ( melder.shouldOutputBlank( spelling , firstWordInSentence ) )
			{
				melder.outputBlank();
			}
								//	Split this word's path into
								//	separate XML tags.

			prevTags	= tags;
			tags		= splitPath( path );

								//	Find where this path and the
								//	previous path diverge.

			int m	= Math.min( tags.length , prevTags.length );
			int n	= -1;

			for ( int j = 0 ; j < m ; j++ )
			{
				if ( !tags[ j ].equals( prevTags[ j ] ) )
				{
					n	= j;
					break;
				}
			}
								//	If paths match up to the point of
								//	the shortest tag set, but the new
								//	tag set is the shorter, the difference
								//	starts at the end of the new tag set.
			if ( n == -1 )
			{
				if ( prevTags.length != tags.length )
				{
					n	= m;
				}
			}
								//	If the paths differ ..
			if ( n != -1 )
			{
								//	Emit the stacked tags beyond the
								//	point of difference.

				for ( int j = n ; j < prevTags.length ; j++ )
				{
					String tag	= tagStack.removeFirst();

					outputTag( tag , false , melder , tagClassifier );
				}
								//	Add the new tags to the stack.

				for ( int j = n ; j < tags.length ; j++ )
				{
					String tag	= trimTag( tags[ j ] );

					outputTag( tag , true , melder , tagClassifier );

					tagStack.addFirst( tag );
				}
			}
            					//	Emit current word.

			melder.processWord( spelling );

                                //	If this is the last word in the
                                //	sentence, end the sentence and
                                //	emit an end-of-line.

			if ( word.getEOS() )
			{
								//	Line wrap the sentence text at
								//	column 65.
				result.append
				(
					StringUtils.wrapLine
					(
						melder.endSentence() ,
						Env.LINE_SEPARATOR ,
						65
					)
				);
								//	Add end of line.

				result.append( Env.LINE_SEPARATOR );

								//	Reset the sentence melder.

				melder.startSentence();

								//	Next word is first word in sentence.

				firstWordInSentence	= true;
			}
			else
			{
				firstWordInSentence	= false;
			}
		}
								//	Emit any remaining stacked tags.

		result.append( melder.endSentence() );
		result.append( Env.LINE_SEPARATOR );

		while ( tagStack.size() > 0 )
		{
			String tag	= tagStack.removeFirst();

			result.append( "</" + tag + ">" );

			if ( !tagClassifier.isSoftTag( tag ) )
			{
				result.append( Env.LINE_SEPARATOR );
			}
		}
								//	Return the generated XML as a string.

		return result.toString();
	}

	/**	Output an XML tag.
	 *
	 *	@param	tag				The XML tag to output.
	 *	@param	openingTag		True to generate opening tag, false otherwise.
	 *	@param	melder			XML sentence melder.
	 *	@param	tagClassifier	XML tag classifier.
	 */

	protected void outputTag
	(
		String tag ,
		boolean openingTag ,
		MySentenceMelder melder ,
		XMLTagClassifier tagClassifier
	)
	{
								//	Output the tag opening.
		if ( openingTag )
		{
			melder.outputWord( "<" );
		}
		else
		{
			melder.outputWord( "</" );
		}
								//	Output tag text and closing.

		melder.outputWord( tag + ">" );

								//	Ouput a line feed except for
								//	soft tags.

		if ( !tagClassifier.isSoftTag( tag ) )
		{
			melder.outputWord( Env.LINE_SEPARATOR );
		}
	}

	/**	Custom sentence melder. */

	public class MySentenceMelder extends SentenceMelder
	{
		public void outputWord( String word )
		{
			super.outputWord( word );
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



