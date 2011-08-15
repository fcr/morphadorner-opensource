package edu.northwestern.at.morphadorner.tools.relemmatize;

/*	Please see the license information at the end of this file. */

import java.io.*;

import java.text.*;
import java.util.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import edu.northwestern.at.morphadorner.WordAttributeNames;
import edu.northwestern.at.morphadorner.tools.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.adornedword.*;
import edu.northwestern.at.utils.corpuslinguistics.lemmatizer.*;
import edu.northwestern.at.utils.corpuslinguistics.lexicon.*;
import edu.northwestern.at.utils.corpuslinguistics.namestandardizer.*;
import edu.northwestern.at.utils.corpuslinguistics.partsofspeech.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.*;
import edu.northwestern.at.utils.corpuslinguistics.sentencesplitter.*;
import edu.northwestern.at.utils.corpuslinguistics.spellingmapper.*;
import edu.northwestern.at.utils.corpuslinguistics.spellingstandardizer.*;
import edu.northwestern.at.utils.corpuslinguistics.tokenizer.*;
import edu.northwestern.at.utils.xml.*;

/**	Filter to update standard spellings and lemmata in adorned file.
  */

public class RelemmatizeFilter extends ExtendedXMLFilterImpl
{
	/**	Word lexicon. */

	protected Lexicon wordLexicon;

	/**	Lemmatizer. */

	protected Lemmatizer lemmatizer;

	/**	Name standardizer. */

	protected NameStandardizer nameStandardizer;

	/**	Spelling standardizer. */

	protected SpellingStandardizer standardizer;

	/**	Spelling mapper. */

	protected SpellingMapper spellingMapper;

	/**	Part of speech tags. */

	protected PartOfSpeechTags partOfSpeechTags;

	/**	Spelling tokenizer. */

	protected WordTokenizer spellingTokenizer;

	/**	Lemma separator. */

	protected String lemmaSeparator;

	/**	Number of lemmata changed. */

	protected int lemmataChanged	= 0;

	/**	Number of standard spellings changed. */

	protected int standardChanged	= 0;

	/**	Number of words processed. */

	protected int wordsProcessed	= 0;

	/**	Create adorned word info filter.
	  *
	  *	@param	reader	XML input reader to which this filter applies.
	  */

	public RelemmatizeFilter
	(
		XMLReader reader ,
		Lexicon wordLexicon ,
		Lemmatizer lemmatizer ,
		NameStandardizer nameStandardizer ,
		SpellingStandardizer standardizer ,
		SpellingMapper spellingMapper
	)
	{
		super( reader );

		this.wordLexicon		= wordLexicon;
		this.lemmatizer			= lemmatizer;
		this.nameStandardizer	= nameStandardizer;
		this.standardizer		= standardizer;
		this.spellingMapper		= spellingMapper;

								//	Get lemma separator.

		lemmaSeparator			= lemmatizer.getLemmaSeparator();

								//	Get the part of speech tags from
								//	the word lexicon.

		partOfSpeechTags		= wordLexicon.getPartOfSpeechTags();

								//	Get spelling tokenizer.

		spellingTokenizer		= new PennTreebankTokenizer();
	}

	/**	Handle start of an XML element.
	  *
	  *	@param	uri			The XML element's URI.
	  *	@param	localName	The XML element's local name.
	  *	@param	qName		The XML element's qname.
	  *	@param	atts		The XML element's attributes.
	  */

	public void startElement
	(
		String uri ,
		String localName ,
		String qName ,
		Attributes atts
	)
		throws SAXException
	{
		if ( qName.equals( "w" ) )
		{
								//	Increment count of words processed.

			wordsProcessed++;
								//	Get word attributes.

			AttributesImpl newAttrs	= new AttributesImpl( atts );

								//	Get current lemma and
								//	standard spellings.

			String oldLemma		=
				newAttrs.getValue( WordAttributeNames.lem );

			String partOfSpeech	=
				newAttrs.getValue( WordAttributeNames.pos );

			String oldStandard	=
				newAttrs.getValue( WordAttributeNames.reg );

			String spelling		=
				newAttrs.getValue( WordAttributeNames.spe );

								//	Update standard spelling.

			String standard		=
				getStandardizedSpelling( spelling , partOfSpeech );

								//	Update lemma.

			String lemma		= getLemma( spelling , partOfSpeech );

			setAttributeValue(
				newAttrs , WordAttributeNames.reg , standard );

			setAttributeValue(
				newAttrs , WordAttributeNames.lem , lemma );

								//	Count number of changed standard
								//	spellings.

			if ( !oldStandard.equals( standard ) )
			{
				standardChanged++;
			}
								//	Count number of changed lemmata.

			if ( !oldLemma.equals( lemma ) )
			{
				lemmataChanged++;
			}

			super.startElement( uri , localName , qName , newAttrs );
		}
		else if ( qName.equals( "c" ) )
		{
			AttributesImpl newAttrs	= new AttributesImpl();

			removeAttribute( newAttrs , WordAttributeNames.part );

			super.startElement( uri , localName , qName , newAttrs );
		}
		else
		{
			super.startElement( uri , localName , qName , atts );
		}
	}

	/**	Get lemma for a word.
	 *
	 *	@param	spelling		The word spelling.
	 *	@param	partOfSpeech	The part of speech.
	 *
	 *	<p>
	 *	On output, sets the lemma field of the adorned word
	 *	We look in the word lexicon first for the lemma.
	 *	If the lexicon does not contain the lemma, we
	 *	use the lemmatizer.
	 *	</p>
	 */

	public String getLemma
	(
		String spelling ,
		String partOfSpeech
	)
	{
								//	Look up lemma in lexicon first.
		String lemmata		=
			wordLexicon.getLemma( spelling , partOfSpeech );

								//	If lemma not in lexicon, use
								//	lemmatizer.

		if ( lemmata.equals( "*" ) )
		{
								//	Get lemmatization word class
								//	for part of speech.
			String lemmaClass	=
				partOfSpeechTags.getLemmaWordClass( partOfSpeech );

								//	Do not lemmatize words which
								//	should not be lemmatized,
								//	including proper names.

			if	(	lemmatizer.cantLemmatize( spelling ) ||
					lemmaClass.equals( "none" )
				)
			{
			}
			else
			{
								//	Extract individual word parts.
								//	May be more than one for a
								//	contraction.

				List wordList	=
					spellingTokenizer.extractWords( spelling );

								//	If just one word part,
								//	get its lemma.

				if	(	!partOfSpeechTags.isCompoundTag( partOfSpeech ) ||
						( wordList.size() == 1 )
					)
				{
					if ( lemmaClass.length() == 0 )
					{
						lemmata	=
							lemmatizer.lemmatize( spelling , "compound" );

						if ( lemmata.equals( spelling ) )
						{
							lemmata	= lemmatizer.lemmatize( spelling );
						}
					}
					else
					{
						lemmata	=
							lemmatizer.lemmatize( spelling , lemmaClass );
					}
				}
								//	More than one word part.
								//	Get lemma for each part and
								//	concatenate them with the
								//	lemma separator to form a
								//	compound lemma.
				else
				{
					lemmata				= "";
					String lemmaPiece	= "";
					String[] posTags	=
						partOfSpeechTags.splitTag( partOfSpeech );

					if ( posTags.length == wordList.size() )
					{
						for ( int i = 0 ; i < wordList.size() ; i++ )
						{
							String wordPiece	= (String)wordList.get( i );

							if ( i > 0 )
							{
								lemmata	= lemmata + lemmaSeparator;
							}

							lemmaClass	=
								partOfSpeechTags.getLemmaWordClass
								(
									posTags[ i ]
								);

							lemmaPiece	=
								lemmatizer.lemmatize
								(
									wordPiece ,
									lemmaClass
								);

							lemmata	= lemmata + lemmaPiece;
						}
					}
				}
			}
		}
								//	Use spelling if lemmata not defined.

		if ( lemmata.equals( "*" ) )
		{
			lemmata	= spelling;
		}
								//	Force lemma to lowercase except
								//	for proper noun tagged word.

		if ( lemmata.indexOf( lemmaSeparator ) < 0 )
		{
			if ( !partOfSpeechTags.isProperNounTag( partOfSpeech ) )
			{
				lemmata	= lemmata.toLowerCase();
			}
		}

		return lemmata;
	}

	/**	Get standardized spelling.
	 *
	 *	@param	correctedSpelling		The spelling.
	 *	@param	partOfSpeech			The part of speech tag.
	 *
	 *	@return							Standardized spelling.
	 */

	protected String getStandardizedSpelling
	(
		String correctedSpelling ,
		String partOfSpeech
	)
	{
		String spelling	= correctedSpelling;
		String result	= correctedSpelling;

		if ( partOfSpeechTags.isProperNounTag( partOfSpeech ) )
 		{
 			result	= nameStandardizer.standardizeProperName( spelling );
		}
		else if (	partOfSpeechTags.isNounTag( partOfSpeech )  &&
					CharUtils.hasInternalCaps( spelling ) )
 		{
		}
		else if ( partOfSpeechTags.isForeignWordTag( partOfSpeech ) )
		{
		}
		else if ( partOfSpeechTags.isNumberTag( partOfSpeech ) )
		{
		}
		else
		{
			result	=
				standardizer.standardizeSpelling
				(
					spelling ,
					partOfSpeechTags.getMajorWordClass( partOfSpeech )
				);

			if ( result.equalsIgnoreCase( spelling ) )
			{
				result	= spelling;
			}
		}

		return spellingMapper.mapSpelling( result );
	}

	/**	Return number of lemmata changed.
	 *
	 *	@return		Number of lemmata changed.
	 */

	public int getLemmataChanged()
	{
		return lemmataChanged;
	}

	/**	Return number of standard spellings changed.
	 *
	 *	@return		Number of standard spellings changed.
	 */

	public int getStandardChanged()
	{
		return standardChanged;
	}

	/**	Return number of words processed.
	 *
	 *	@return		Number of words processed.
	 */

	public int getWordsProcessed()
	{
		return wordsProcessed;
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



