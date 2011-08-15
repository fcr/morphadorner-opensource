package edu.northwestern.at.morphadorner.tools;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import edu.northwestern.at.morphadorner.WordAttributeNames;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.csv.*;
import edu.northwestern.at.utils.xml.*;

/**	Filter to extract XML word information during XML parsing.
  */

public class ExtendedAdornedWordFilter extends ExtendedXMLFilterImpl
{
	/**	Map word ID to list of adorned word information objects. */

	protected Map<String , ExtendedAdornedWord> idToWordInfo;

	/**	List of String word IDs. */

	protected List<String> idList;

	/**	List of tags for determining node ancestry of each word. */

	protected List<String> tagList	= ListFactory.createNewList();

	/**	List of current last word for a text section. */

	protected List<ExtendedAdornedWord> lastWordList	=
		ListFactory.createNewList();

	/**	List of current last word part for a text section. */

	protected List<ExtendedAdornedWord> lastWordPartList	=
		ListFactory.createNewList();

	/**	Last word encountered.  Reset for each hard/jump tag. */

	protected ExtendedAdornedWord lastWordInfo	= null;

	/**	Last word part encountered for a split word. */

	protected ExtendedAdornedWord lastWordPartInfo	= null;

	/**	XML tag classes. */

	protected  XMLTagClassifier tagClassifier;

	/**	Generate words for gaps. */

	protected boolean generateGapWords	= false;

	/**	Last word ID. */

	protected String lastID	= "";

	/**	Gap count for generating IDs. */

	protected int gapCount	= 0;

	/**	Running page number.  Starts at 0. */

	protected int pageNumber	= 0;

	/**	Gap words which appear before first real word. */

	protected Map<String, ExtendedAdornedWord> leadingGapWords	=
		MapFactory.createNewLinkedMap();

	/**	First word ID found in text. */

	protected String firstWordID	= "";

	/**	Create adorned word info filter.
	  *
	  *	@param	reader	XML input reader to which this filter applies.
	  */

	public ExtendedAdornedWordFilter
	(
		XMLReader reader
	)
	{
		this( reader , new TEITagClassifier() , false );
	}

	/**	Create adorned word info filter.
	  *
	  *	@param	reader					XML input reader to which
	  *									this filter applies.
	  *	@param	generateGapWords		true to generate "words"
	  *									for <gap> elements.
	  */

	public ExtendedAdornedWordFilter
	(
		XMLReader reader ,
		boolean generateGapWords
	)
	{
		this( reader , new TEITagClassifier() , generateGapWords );
	}

	/**	Create adorned word info filter.
	  *
	  *	@param	reader					XML input reader to which
	  *									this filter applies.
	  *	@param	tagClassifier			XML tag class.
	  *	@param	generateGapWords		true to generate "words"
	  *									for <gap> elements.
	  */

	public ExtendedAdornedWordFilter
	(
		XMLReader reader ,
		XMLTagClassifier tagClassifier ,
		boolean generateGapWords
	)
	{
		super( reader );
								//	Save tag class.

		this.tagClassifier		= tagClassifier;

								//	Remember if we're to
								//	generate word entries
								//	corresponding to gaps.

		this.generateGapWords	= generateGapWords;

								//	No IDs found yet.

		this.lastID				= "";

								//	Maps word IDs to word information.

		idToWordInfo			= MapFactory.createNewLinkedMap( 25000 );
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
								//	Pick up data for
								//	"<w>" = word elements.

		boolean isW		= qName.equals( "w" );
		boolean isGap	= qName.equals( "gap" ) && generateGapWords;
		boolean isPB	= qName.equals( "pb" );

		String id				= "";
		boolean isSpoken		= false;
		boolean isVerse			= false;
		boolean inJumpTag		= false;
		AttributesImpl newAtts	= new AttributesImpl( atts );

								//	Handle page break.
		if ( isPB )
		{
			pageNumber++;
		}
								//	Handle word or gap.

		else if ( isW || isGap )
		{
								//	Get main/side.

			MainFront mainFront	= getMainFront();

								//	If gap, generate a "word" for it.
			if ( isGap )
			{
				id	= lastID + "-gap" + gapCount;

				gapCount++;

				String path		=
					newAtts.getValue( WordAttributeNames.p );

				String unit		= newAtts.getValue( "unit" );
				String extent	= newAtts.getValue( "extent" );
				String reason	= newAtts.getValue( "reason" );

				String gapText	= "{gap";

				if ( unit != null )
				{
					gapText	= gapText + "-" + unit;
				}

				if ( extent != null )
				{
					gapText	= gapText + "-" + extent;
				}

				if ( reason != null )
				{
					gapText	= gapText + "-" + reason;
				}

				gapText	= gapText + "}";

				newAtts	= new AttributesImpl();

				setAttributeValue(
					newAtts , WordAttributeNames.id , id );

				setAttributeValue(
					newAtts , WordAttributeNames.eos , "0" );

				setAttributeValue(
					newAtts , WordAttributeNames.lem , gapText );

				setAttributeValue(
					newAtts , WordAttributeNames.ord , -1 );

				setAttributeValue(
					newAtts , WordAttributeNames.part , "N" );

				setAttributeValue(
					newAtts , WordAttributeNames.p , path );

				setAttributeValue(
					newAtts , WordAttributeNames.pos , "zz" );

				setAttributeValue(
					newAtts , WordAttributeNames.reg , gapText );

				setAttributeValue(
					newAtts , WordAttributeNames.spe , gapText );

				setAttributeValue(
					newAtts , WordAttributeNames.tok , gapText );
			}
			else
			{
								//	Get word ID.

				id	= newAtts.getValue( WordAttributeNames.id );

				if ( id == null )
				{
					id	= newAtts.getValue( "id" );
				}

				if ( firstWordID.length() == 0 )
				{
					firstWordID	= id;
				}

				lastID		= id;
				gapCount	= 0;

				isSpoken	= getSpoken();
				isVerse		= getVerse();
				inJumpTag	= getInJumpTag();
			}
								//	Store data for this word.

			ExtendedAdornedWord wordInfo	=
				new ExtendedAdornedWord
				(
					"" ,
					newAtts ,
					mainFront.frontMiddleBack ,
					mainFront.mainSide ,
					null ,
					pageNumber ,
					isSpoken ,
					isVerse ,
					inJumpTag ,
					lastWordInfo ,
					lastWordPartInfo
				);
				        		//	Set gap flag.

			wordInfo.setGap( isGap );

								//	Check if this is a new word
								//	or a continued part of a
								//	split word.

			if ( wordInfo.isFirstPart() )
			{
								//	Set previous word's next word
								//	pointer to this word.

				if ( lastWordInfo != null )
				{
					lastWordInfo.setNextWord( wordInfo );
				}
								//	This word is now the new last word.

				lastWordInfo	= wordInfo;
			}
 								//	If the previous word was a split word,
								//	we need to set all its parts
								//	to point to this word.

			if ( lastWordInfo != null )
			{
				if ( lastWordInfo.isSplitWord() )
				{
					ExtendedAdornedWord nextPart	=
						lastWordInfo.getNextWordPart();

					while ( nextPart != null )
					{
						nextPart.setNextWord( wordInfo );

						nextPart	= nextPart.getNextWordPart();
					}
				}
			}
								//	If this is a split word, set the
								//	previous split word part to this
								//	word part.

			if ( wordInfo.isSplitWord() )
			{
				if ( wordInfo.isFirstPart() )
				{
					wordInfo.setPreviousWordPart( null );
				}
				else
				{
					if ( lastWordPartInfo != null )
					{
						lastWordPartInfo.setNextWordPart( wordInfo );
					}
				}

				lastWordPartInfo	= wordInfo;
			}
			else
			{
				lastWordPartInfo	= null;
				wordInfo.setPreviousWordPart( null );
			}
								//	Store word index in the
								//	ID -> index map.

			wordInfo.setWordIndex( idToWordInfo.size() );

								//	Store word info in the
								//	ID -> attributes map.

			idToWordInfo.put( id ,  wordInfo );

								//	If we have not found a real word
								//	yet, we don't have a word ID to which
								//	to tack on the gap word's ID.
								//	In this case we add the gap word
								//	to the map of leading gap words.

			if ( isGap && ( lastID.length() == 0 ) )
			{
				leadingGapWords.put( id , wordInfo );
			}
		}
		else if ( !tagClassifier.isSoftTag( qName ) )
		{
								//	If this is a hard tag, set the
								//	saved last word to null, since
								//	each hard tag starts a new
								//	sequence of words.
								//	If this is a jump tag, set the
								//	saved last word to the current
								//	last word, since once the jump
								//	tag is closed, the previous sequence
								//	of words continues.

			if ( tagClassifier.isJumpTag( qName ) )
			{
				lastWordList.add( lastWordInfo );
				lastWordPartList.add( lastWordPartInfo );
			}
			else
			{
				lastWordList.add( null );
				lastWordPartList.add( null );
			}
								//	Reset the last word found to
								//	null if this is a jump or hard tag.

			lastWordInfo		= null;
			lastWordPartInfo	= null;
		}
								//	Add this tag to the
								//	current stack of tags so we
								//	can determine if a word is in
								//	the front, middle, or back text,
								//	if the word appears in
								//	the main/side text, and if the word
								//	appears in spoken text.

		tagList.add( qName );

		super.startElement( uri , localName , qName , atts );
	}

    /**	Handle character data.
     *
     *	@param	ch		Array of characters.
     *	@param	start	The starting position in the array.
     *	@param	length	The number of characters.
     *
     *	@throws	org.xml.sax.SAXException If there is an error.
     */

	public void characters( char ch[] , int start , int length )
		throws SAXException
	{
		String currentTag	= tagList.get( tagList.size() - 1 );

		if ( currentTag.equals( "w" ) && ( lastWordInfo != null ) )
		{
			lastWordInfo.appendWordText( ch , start , length );
		}

		super.characters( ch , start , length );
	}

    /**	Handle whitespace.
     *
     *	@param	ch		Array of characters.
     *	@param	start	The starting position in the array.
     *	@param	length	The number of characters.
     *
     *	@throws	org.xml.sax.SAXException If there is an error.
     */

	public void ignorableWhitespace( char ch[] , int start , int length )
		throws SAXException
	{
		super.ignorableWhitespace( ch , start , length );
	}

	/**	Handle end of an element.
	  *
	  *	@param	uri			The XML element's URI.
	  *	@param	localName	The XML element's local name.
	  *	@param	qName		The XML element's qname.
	  */

	public void endElement
	(
		String uri ,
		String localName ,
		String qName
	)
		throws SAXException
	{
								//	Pop the tag stack.

		tagList.remove( tagList.size() - 1 );

								//	Pop the "last word" stack if
								//	this was not a soft tag.

		if ( !tagClassifier.isSoftTag( qName ) )
		{
			int last			= lastWordList.size() - 1;

			lastWordInfo		=
				(ExtendedAdornedWord)lastWordList.get( last );

			lastWordList.remove( last );

			last				= lastWordPartList.size() - 1;

			lastWordPartInfo	=
				(ExtendedAdornedWord)lastWordPartList.get( last );

			lastWordPartList.remove( last );
		}

		super.endElement( uri , localName , qName );
	}

	/**	End of document found.
	 *
	 *	@throws	SaxException
	 */

	public void endDocument()
		throws SAXException
	{
		super.endDocument();
								//	Correct word IDs for gap words
								//	which appeared before any real word.

		if ( leadingGapWords.size() > 0 )
		{
			fixLeadingGapWords();

								//	Get list of old word IDs, including
								//	"bad" leading gap word IDs.

			List<String> idList2	=
				ListFactory.createNewList( idToWordInfo.keySet() );

								//	Create list of corrected IDs.

			idList	= ListFactory.createNewList();

			for ( int i = 0 ; i < idList2.size() ; i++ )
			{
				ExtendedAdornedWord word	=
					idToWordInfo.get( idList2.get( i ) );

				idList.add( word.getID() );
			}
								//	Evict bad leading gap word IDs
								//	from idToWordInfo map and add the
								//	corrected ones.
								//
								//	NB:  the IDs will no longer be in
								//	proper insertion order, but that
								//	won't matter since we always rely
								//	on idList for the order of the word
								//	IDs from now on.

			for ( String oldID : leadingGapWords.keySet() )
			{
								//	Get corresponding adorned word info,
								//	which already has the corrected
								//	gap word ID.

				ExtendedAdornedWord word	= leadingGapWords.get( oldID );

								//	Evict the old ID to word info mapping.

				idToWordInfo.remove( oldID );

								//	Add the new ID to word info mapping.

				idToWordInfo.put( word.getID() , word );
			}
		}
		else
		{
								//	Create list of word IDs.

			idList	= ListFactory.createNewList( idToWordInfo.keySet() );
		}
								//	Generate missing word/sentence/EOS/path
								//	information.

		generateMissingExtendedAdornedWordInformation();
	}

	/**	Get main/side and front/main/back text divisions.
	 *
	 *	@return		MainFront object with main/side and front/middle/back entries.
	 */

	protected MainFront getMainFront()
	{
		ExtendedAdornedWord.FrontMiddleBack frontMiddleBack	=
			ExtendedAdornedWord.FrontMiddleBack.MIDDLE;

		ExtendedAdornedWord.MainSide main	=
			ExtendedAdornedWord.MainSide.MAIN;

		int iTagList			= tagList.size() - 1;
		String ww				= tagList.get( iTagList );
		String root				= tagList.get( 0 );

		while ( !ww.equals( root ) )
		{
			String wwName	= ww;
			String wwValue	= tagClassifier.getTagClass( wwName );

			if ( wwValue != null )
			{
				if ( wwValue.equals( "front" ) )
				{
					frontMiddleBack	=
						ExtendedAdornedWord.FrontMiddleBack.FRONT;
				}
				else if ( wwValue.equals( "back" ) )
				{
					frontMiddleBack	=
						ExtendedAdornedWord.FrontMiddleBack.BACK;
				}
				else
				{
					if ( wwValue.equals( "side" ) )
					{
						main	= ExtendedAdornedWord.MainSide.SIDE;
					}
				}
			}

			iTagList--;

			ww	= tagList.get( iTagList );
		}

		return new MainFront( frontMiddleBack , main );
	}

	/**	Get number of words read.
	 *
	 *	@return		Number of words read.
	 */

	public int getNumberOfWords()
	{
		return idList.size();
	}

	/**	Get spoken/not spoken word flag.
	 *
	 *	@return		true if word appears as descendant of <said> tag,
	 *				false otherwise.
	 */

	protected boolean getSpoken()
	{
								//	Assume word is not spoken text
								//	by default.

		boolean result	= false;

								//	Look at previous tags and see if
								//	one of them is a "said" element.
								//	If so, this word is spoken text.

		for ( int iTag = tagList.size() -  1 ; iTag >= 0 ; iTag-- )
		{
			result	= result || tagList.get( iTag ).equals( "said" );
			if ( result ) break;
		}

		return result;
	}

	/**	Get verse flag.
	 *
	 *	@return		true if word appears as descendant of <l> tag,
	 *				false otherwise.
	 */

	protected boolean getVerse()
	{
								//	Assume word is not verse
								//	by default.

		boolean result	= false;

								//	Look at ancestral tags and see if
								//	one of them is an "l" element.
								//	If so, this word is verse.

		for ( int iTag = tagList.size() -  1 ; iTag >= 0 ; iTag-- )
		{
			result	= result || tagList.get( iTag ).equals( "l" );
			if ( result ) break;
		}

		return result;
	}

	/**	Get in jump tag flag.
	 *
	 *	@return		true if word appears as descendant of a jump tag,
	 *				false otherwise.
	 */

	protected boolean getInJumpTag()
	{
								//	Assume word is not in jump tag
								//	by default.

		boolean result	= false;

								//	Look at ancestral tags and see if
								//	one of them is a jump tag.
								//	If so, this word is in a jump tag.

		for ( int iTag = tagList.size() -  1 ; iTag >= 0 ; iTag-- )
		{
			result	=
				result || tagClassifier.isJumpTag( tagList.get( iTag ) );

			if ( result ) break;
		}

		return result;
	}

	/**	Return list of adorned word IDs.
	 *
	 *	@return	List of String adorned word IDs.
	 */

	public List<String> getAdornedWordIDs()
	{
		return idList;
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
		return idToWordInfo.get( id );
	}

	/**	Get adorned word information for a word index.
	 *
	 *	@param	index	The word index.
	 *
	 *	@return			ExtendedAdornedWord for the specified index.
	 *					Returns null if index not found.
	 */

	public ExtendedAdornedWord getExtendedAdornedWord( int index )
	{
		ExtendedAdornedWord result	= null;

		String id	= idList.get( index );

		if ( id != null )
		{
			result	= idToWordInfo.get( id );
		}

		return result;
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
		int result	= -1;

		if ( idToWordInfo.containsKey( id ) )
		{
			result	= idToWordInfo.get( id ).getWordIndex();
		}

		return result;
	}

	/**	Get adorned word IDs in reading context order.
	 *
	 *	@return		List of word IDs in reading context order.
	 */

	public List<String> getAdornedWordIDsInReadingContextOrder()
	{
								//	Accumulates word IDs in
								//	reading context order.

		List<String> sortedIDList	= ListFactory.createNewList();

								//	Accumulates word IDs in
								//	jump tags.  Words in jump tags
								//	are moved so that they do not
								//	interrupt the flow of text.

		List<String> jumpTagIDList	= ListFactory.createNewList();

								//	Loop over all word IDs.

		for ( String id : idList )
		{
                                //	Pick up word information for
                                //	this word ID.

			ExtendedAdornedWord word	= idToWordInfo.get( id );

								//	If this word is in a jump tag,
								//	add it to the list of jump tag
								//	words.

			if ( word.getInJumpTag() )
			{
				jumpTagIDList.add( id );
			}
			else
			{
								//	This word is not in a jump tag.
								//	Add it to the output list of
								//	words in reading context order.

				sortedIDList.add( id );

								//	If this word is the end of a
								//	sentence, emit any accumulated
								//	jump tag words.

				if ( word.getEOS() && ( jumpTagIDList.size() > 0 ) )
				{
					sortedIDList.addAll( jumpTagIDList );
					jumpTagIDList.clear();
				}
			}
		}
								//	Emit any remaining jump tag words.

		if ( jumpTagIDList.size() > 0 )
		{
			sortedIDList.addAll( jumpTagIDList );
		}
								//	Return the list of IDs in reading
								//	context order.
		return sortedIDList;
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
		return getSentencesFromSentenceNumbers();
	}

	/**	Get adorned words as a list of sentences using EOS attributes.
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

	protected List<List<ExtendedAdornedWord>> getSentencesFromEOS()
	{
								//	Holds one sentence.

		List<ExtendedAdornedWord> sentence	= ListFactory.createNewList();

								//	Get list of word IDs sorted in
								//	ascending (reading context) order.

		List<String> wordIDs	=
			getAdornedWordIDsInReadingContextOrder();

								//	Create sorted map with index
								//	of first word in sentence as the
								//	key and a list of sentence words as the
								//	data value.

		Map<Integer,List<ExtendedAdornedWord>> sentenceMap	=
			MapFactory.createNewSortedMap();

								//	Index of first word of sentence
								//	in list of word IDs.  This corresponds
								//	to order of appearance of the word
								//	in the text.

		int startingWordIndex	= 0;

								//	Loop over all word IDs.

		for ( int i = 0 ; i < wordIDs.size() ; i++ )
		{
								//	Get adorned word information for
								//	this word word ID.

			ExtendedAdornedWord wordInfo	=
				getExtendedAdornedWord( wordIDs.get( i ) );

								//	Only use one part for split words,
								//	since all parts have the same
								//	joined word information.

			if ( !wordInfo.isFirstPart() )
			{
				continue;
			}
								//	If the current sentence is empty,
								//	we are starting a new sentence.
								//	Remember the starting word index.

			if ( sentence.size() == 0 )
			{
				startingWordIndex	=
					getAdornedWordIndexByID( wordIDs.get( i ) );
			}
								//	Add this word's information to the
								//	current sentence.

			sentence.add( wordInfo );

								//	If this word is the last word in the
								//	sentence, add the sentence to the
								//	sentence map using the starting word
								//	index as a key.  This allows us to
								//	emit the list of sentences in
								//	the proper order of appearance.

			if ( wordInfo.getEOS() )
			{
				sentenceMap.put( startingWordIndex , sentence );

								//	Get a new empty sentence holder.

				sentence	= ListFactory.createNewList();
			}
		}
								//	Add any pending final sentence.

		if ( sentence.size() > 0 )
		{
			sentenceMap.put( startingWordIndex , sentence );
		}
								//	Return list of sentences,
								//	sorted by appearance position
								//	of first word of sentence in text.

		List<List<ExtendedAdornedWord>> sentences	=
			ListFactory.createNewList();

		for ( Integer sentenceIndex : sentenceMap.keySet() )
		{
			sentences.add( sentenceMap.get( sentenceIndex ) );
		}

		return sentences;
	}

	/**	Get adorned words as a list of sentences using sentence numbers.
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

	protected List<List<ExtendedAdornedWord>> getSentencesFromSentenceNumbers()
	{
								//	Holds one sentence.

		List<ExtendedAdornedWord> sentence;

								//	Get list of word IDs sorted in
								//	reading context order.

		List<String> wordIDs	=
			getAdornedWordIDsInReadingContextOrder();

								//	Create sorted map with sentence number
								//	as the key and a list of sentence words
								//	as the data value.

		Map<Integer,List<ExtendedAdornedWord>> sentenceMap	=
			MapFactory.createNewSortedMap();

								//	Loop over all word IDs.

		for ( int i = 0 ; i < wordIDs.size() ; i++ )
		{
								//	Get adorned word information for
								//	this word word ID.

			ExtendedAdornedWord wordInfo	=
				getExtendedAdornedWord( wordIDs.get( i ) );

								//	Only use one part for split words,
								//	since all parts have the same
								//	joined word information.

			if ( !wordInfo.isFirstPart() ) continue;

								//	Get sentence number for this word.

			int sentenceNumber	= wordInfo.getSentenceNumber();

								//	See if this sentence already exists.
								//	If not, create a new empty sentence.

			sentence			= sentenceMap.get( sentenceNumber );

			if ( sentence == null )
			{
				sentence	= ListFactory.createNewList();
				sentenceMap.put( sentenceNumber , sentence );
			}
								//	Get word number for this word.
								//	If not given, generate one based
								//	upon number of words so far added
								//	to sentence or the running word number.
								//
								//	N.B.  If the word number is given
								//	in the XML, we do not change it from
								//	within-sentence to running, or
								//	running to within-sentence.  If the
								//	word number is not given in the
								//	XML, we always generate within-
								//	sentence numbers, e.g., the word
								//	number starts at 1 for each new
								//	sentence.

			int wordNumber	= wordInfo.getWordNumber();

			if ( wordNumber == -1 )
			{
								//	Add this word's information to the
								//	current sentence.

				wordInfo.setWordNumber( sentence.size() + 1 );

				sentence.add( wordInfo );
			}
			else
			{
				try
				{
					sentence.add( wordNumber - 1 , wordInfo );
				}
				catch ( Exception e )
				{
				}
			}
		}
								//	Return list of sentences,
								//	sorted by appearance position
								//	of first word of sentence in text.

		List<List<ExtendedAdornedWord>> sentences	=
			ListFactory.createNewList();

		for ( Integer sentenceNumber : sentenceMap.keySet() )
		{
			sentences.add( sentenceMap.get( sentenceNumber ) );
		}

		return sentences;
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
		List<String> result	= null;

								//	See if word with this
								//	word ID exists.

		ExtendedAdornedWord adornedWordInfo	=
			getExtendedAdornedWord( wordID );

								//	If so, back up to
								//	first part for this word.

		if ( adornedWordInfo != null )
		{
			result	= ListFactory.createNewList();

			ExtendedAdornedWord prevPart	= adornedWordInfo;

			while ( prevPart != null )
			{
				adornedWordInfo	= prevPart;
				prevPart		= adornedWordInfo.getPreviousWordPart();
			}
								//	Now go forward and pick up
								//	all parts, adding the word ID
								//	for each part to the result list.

			while ( adornedWordInfo != null )
			{
				result.add( adornedWordInfo.getID() );
				adornedWordInfo	= adornedWordInfo.getNextWordPart();
			}
		}

		return result;
	}

	/**	Get related adorned words.
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
		List<ExtendedAdornedWord> result	= null;

								//	Back up to first part for this word.

		if ( adornedWordInfo != null )
		{
			ExtendedAdornedWord wordInfo	= adornedWordInfo;
			result							= ListFactory.createNewList();

			ExtendedAdornedWord prevPart	= wordInfo;

			while ( prevPart != null )
			{
				wordInfo			= prevPart;
				prevPart			= wordInfo.getPreviousWordPart();
			}
						//  Now go forward and pick up
						//  all parts, adding the word info
						//  for each part to the result list.

			while ( wordInfo != null )
			{
				result.add( wordInfo );
				wordInfo	= wordInfo.getNextWordPart();
			}
		}

		return result;
	}

	/**	Trim tag number from XML tag.
	 *
	 *	@param	tag		XML tag to trim.
	 *
	 *	@return			Trimmed tag.
	 */

	public String trimTag( String tag )
	{
		String result	= tag.trim();

								//	Remove bracketed text leaving plain tag.

		int lbPos		= result.indexOf( "[" );

		if ( lbPos >= 0 )
		{
			result	= result.substring( 0 , lbPos );
		}

		return result;
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
								//	Split path into tags at backslashes.

		String[] tags	= path.split( "\\\\" );

		String[] result	= new String[ tags.length - 1 ];

		int j	= 0;
								//	Skip the trailing word tag.

		for ( int i = 0 ; i < tags.length - 1 ; i++ )
		{
			result[ j++ ]	= tags[ i ];
		}

		return result;
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
								//	Get split tags with leading
								//	document name included.

		String[] tags	= splitPathFull( path );

								//	Now remove the leading slash and
								//	document name.

		String[] result	= new String[ tags.length - 2 ];

		int j	= 0;
								//	Skip the leading slash and
								//	work name, as well as the trailing
								//	word tag.

		for ( int i = 2 ; i < tags.length ; i++ )
		{
			result[ j++ ]	= tags[ i ];
		}

		return result;
	}

	/**	Remove trailing soft tags from a path.
	 *
	 *	@param	path	Path from which to remove trailing soft tags.
	 *
	 *	@return			Path with trailing soft tags removed.
	 */

	public String trimTrailingSoftTags( String path )
	{
								//	Split up path in separate tags.
								//	Leave the document as the first
								//	tag element.

		String[] pathTags	= splitPathFull( path );

								//	Move backwards in tags until
								//	we find the first non-soft tag.
								//	That defines the sibling level.

		int l = pathTags.length - 2;

		for ( l = pathTags.length - 2 ; l > 0 ; l-- )
		{
			String tag	= trimTag( pathTags[ l ] );

			if ( !tagClassifier.isSoftTag( tag ) ) break;
		}
								//	Reconstitute the abbreviated path.

		StringBuffer sb	= new StringBuffer();

		for ( int i = 0 ; i <= l ; i++ )
		{
			if ( i > 0 )
			{
				sb.append( "\\" );
			}

			sb.append( pathTags[ i ] );
		}

		return sb.toString();
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
								//	String list to hold results.

		List<String> result			= ListFactory.createNewList();

								//	Get word from ID.

		ExtendedAdornedWord word	= getExtendedAdornedWord( wordID );

								//	Get path for word ID.

		String path					= word.getPath();

								//	If we found the path ...
		if ( path != null )
		{
								//	Now search the paths for all words
								//	that match.

			result	=
				findWordsByMatchingLeadingPath
				(
					trimTrailingSoftTags( path )
				);
		}

		return result;
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
								//	Create string list to hold
								//	resulting word IDs.

		List<String> result	= ListFactory.createNewList();

								//	Compile the input pattern.

		Pattern pat		= Pattern.compile( pattern );
		Matcher matcher	= pat.matcher( "" );

								//	For each word path ...

		for ( int i = 0 ; i < idList.size() ; i++ )
		{
			ExtendedAdornedWord word	=
				getExtendedAdornedWord( idList.get( i ) );

			String path					= word.getPath();

								//	See if it contains the pattern.

			matcher.reset( path );

			if ( matcher.find() )
			{
								//	Add word ID to list of matches if so.

				result.add( idList.get( i ) );
			}
		}

		return result;
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
								//	Create string list to hold
								//	resulting word IDs.

		List<String> result	= ListFactory.createNewList();

								//	For each word path ...

		for ( int i = 0 ; i < idList.size() ; i++ )
		{
			ExtendedAdornedWord word	= getExtendedAdornedWord( idList.get( i ) );
			String path					= word.getPath();

								//	See if it starts with the pattern.

			if ( path.startsWith( pattern ) )
			{
								//	Add word ID to list of matches if so.

				result.add( idList.get( i ) );
			}
		}

		return result;
	}

	/**	Get list of selected word IDs from specified ID range.
	 *
	 *	@param	startingWordID		Starting word ID.
	 *	@param	endingWordID		Ending word ID.
	 *
	 *	@return						String list of selected word IDs.
	 */

	protected List<String> getSelectedWordIDs
	(
		String startingWordID ,
		String endingWordID
	)
	{
		List<String> result	= ListFactory.createNewList();

		int startingIndex	= getAdornedWordIndexByID( startingWordID );
		int endingIndex		= getAdornedWordIndexByID( endingWordID );

		if	(	( startingIndex >= 0 ) &&
				( endingIndex >= 0 ) &&
				( endingIndex >= startingIndex )
			)
		{
			for ( int i = startingIndex ; i <= endingIndex ; i++ )
			{
				result.add( idList.get( i ) );
			}
		}

		return result;
	}

	/**	Generate missing word ordinals.
	 */

	protected void addWordOrdinals()
	{
		int wordOrd	= 1;

		for ( int i = 0 ; i < idList.size() ; i++ )
		{
			ExtendedAdornedWord wordInfo	=
				getExtendedAdornedWord( idList.get( i ) );

			if ( !wordInfo.isFirstPart() )
			{
				continue;
			}

			wordInfo.setOrd( wordOrd );

			List<ExtendedAdornedWord> relatedWords	=
				getRelatedSplitWords( wordInfo );

			if ( relatedWords.size() > 1 )
			{
				for ( int k = 0 ; k < relatedWords.size() ; k++ )
				{
					ExtendedAdornedWord relWordInfo	=
						relatedWords.get( k );

					relWordInfo.setOrd( wordOrd );

					idToWordInfo.put
					(
						relWordInfo.getID() ,
						relWordInfo
					);
				}
			}

			wordOrd++;
		}
	}

	/**	Correct IDs for leading gap words.
	 */

	protected void fixLeadingGapWords()
	{
								//	If all the words are gap words
								//	(which is a rather bogus text),
								//	create an "all zeros" base ID.

		String baseGapID	= "";

		if ( firstWordID.length() == 0 )
		{
			baseGapID	= "0";
		}
		else
		{
								//	Get root ID for leading
								//	gap words.

			char[] idChars	= firstWordID.toCharArray();

			int i	= idChars.length - 1;

			while	(	( i > 0 ) &&
						( idChars[ i ] != '-' ) &&
						Character.isDigit( idChars[ i ] )
				)
			{
				idChars[ i ] 	= '0';
				i--;
			}

			baseGapID	= new String( idChars );
		}
								//	Fix IDs for leading gap words.

		int gapCount	= 0;

		for ( String gapID : leadingGapWords.keySet() )
		{
			ExtendedAdornedWord gapWord	= leadingGapWords.get( gapID );

			String id	= baseGapID + "-gap" + gapCount++;

			gapWord.setID( id );
		}
	}

	/**	Generate missing adorned word information.
	 *
	 *	<p>
	 *	Generates any missing word and sentence numbers, end of sentence
	 *	flags, word paths, gap IDs, etc.
	 *	</p>
	 */

	protected void generateMissingExtendedAdornedWordInformation()
	{
								//	Get list of word IDs sorted in
								//	reading context order.

		List<String> wordIDs	=
			getAdornedWordIDsInReadingContextOrder();

								//	See if word and sentence numbers
								//	missing by checking if the first word
								//	doesn't have them.

		ExtendedAdornedWord wordInfo	=
			getExtendedAdornedWord( wordIDs.get( 0 ) );

		if ( wordInfo == null ) return;

		boolean needWordNumbers		=
			( wordInfo.getWordNumber() == -1 );

		boolean needSentenceNumbers	=
			( wordInfo.getSentenceNumber() == -1 );

								//	Count number of true EOS values.
								//	If none, we need to add them.

		int eosFound		= 0;

								//	Count number of ord values.
								//	If none, we need to add them.

		int ordFound		= 0;

								//	Loop over all word IDs.

		for ( int i = 0 ; i < wordIDs.size() ; i++ )
		{
								//	Get adorned word information for
								//	this word word ID.

			wordInfo	= getExtendedAdornedWord( wordIDs.get( i ) );

								//	Increment count of true EOS values.

			if ( wordInfo.getEOS() )
			{
				eosFound++;
			}
								//	Increment count of ord values
								//	greater than 0.

			if ( wordInfo.getOrd() > 0 )
			{
				ordFound++;
			}
		}
								//	If no ord values greater than zero,
								//	ord values are missing.  Add them here.

		if ( ordFound == 0 )
		{
			addWordOrdinals();
		}
								//	If no EOS values set to true, we
								//	need to generate them.

		if ( eosFound == 0 )
		{
								//	Generate EOS from word and sentence
								//	numbers.  Start by generating
								//	sentences.

			List<List<ExtendedAdornedWord>> sentences	=
				getSentencesFromSentenceNumbers();

								//	Loop over sentences and set
								//	EOS flag true for last word in
								//	each sentence.

			for ( int i = 0 ; i < sentences.size() ; i++ )
			{
				List<ExtendedAdornedWord> sentence	= sentences.get( i );

				wordInfo	= sentence.get( sentence.size() - 1 );

				wordInfo.setEOS( true );

				List<ExtendedAdornedWord> relatedWords	=
					getRelatedSplitWords( wordInfo );

				if ( relatedWords.size() > 1 )
				{
					for ( int k = 0 ; k < relatedWords.size() ; k++ )
					{
						ExtendedAdornedWord relWordInfo	=
							relatedWords.get( k );

						relWordInfo.setEOS( true );

						idToWordInfo.put
						(
							relWordInfo.getID() ,
							relWordInfo
						);
					}
				}
			}
		}
		else if ( needWordNumbers || needSentenceNumbers )
		{
								//	Generate word and sentence numbers.
								//	Start by generating
								//	sentences from EOS values.

			List<List<ExtendedAdornedWord>> sentences	=
				getSentencesFromEOS();

								//	Loop over sentences and add word and
								//	sentence numbers.

			for ( int i = 0 ; i < sentences.size() ; i++ )
			{
				List<ExtendedAdornedWord> sentence	= sentences.get( i );

				for ( int j = 0 ; j < sentence.size() ; j++ )
				{
					wordInfo	= sentence.get( j );

					wordInfo.setWordNumber( j + 1 );
					wordInfo.setSentenceNumber( i + 1 );

					idToWordInfo.put( wordInfo.getID() , wordInfo );

								//	For split words, make the
								//	word and sentence numbers the
								//	same for each part.

					List<ExtendedAdornedWord> relatedWords	=
						getRelatedSplitWords( wordInfo );

					if ( relatedWords.size() > 1 )
					{
						for ( int k = 0 ; k < relatedWords.size() ; k++ )
						{
							ExtendedAdornedWord relWordInfo	=
								relatedWords.get( k );

							relWordInfo.setWordNumber( j + 1 );
							relWordInfo.setSentenceNumber( i + 1 );

							idToWordInfo.put
							(
								relWordInfo.getID() ,
								relWordInfo
							);
						}
					}
				}
			}
		}
	}

	/**	Holds main/front information.
	 */

	class MainFront
	{
		/*	Main/side. */

		ExtendedAdornedWord.MainSide mainSide ;

		/**	Front/middle/back. */

		ExtendedAdornedWord.FrontMiddleBack frontMiddleBack;

		/**	Create main/front object.
		 *
		 *	@param	frontMiddleBack		Word location in text.
		 *	@param	mainSide			Word in main or side text.
		 */

		public MainFront
		(
			ExtendedAdornedWord.FrontMiddleBack frontMiddleBack ,
			ExtendedAdornedWord.MainSide mainSide
		)
		{
			this.frontMiddleBack	= frontMiddleBack;
			this.mainSide			= mainSide;
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



