package edu.northwestern.at.utils.corpuslinguistics.partsofspeech;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.net.*;
import java.util.*;

import edu.northwestern.at.utils.IsCloseable;
import edu.northwestern.at.utils.IsCloseableObject;
import edu.northwestern.at.utils.MapFactory;
import edu.northwestern.at.utils.SortedArrayList;
import edu.northwestern.at.utils.StringUtils;
import edu.northwestern.at.utils.TextFile;
import edu.northwestern.at.utils.UTF8Properties;
import edu.northwestern.at.utils.UTF8PropertyUtils;

/**	AbstractPartOfSpeechTags: Base class for PartOfSpeechTags implementations.
 */

abstract public class AbstractPartOfSpeechTags
	extends IsCloseableObject
	implements PartOfSpeechTags, IsCloseable
{
	/**	Stores information about part of speech tags.
 	 *
	 *	<p>
	 *	Maps part of speech tags to tag information.
	 *	</p>
	 */

	protected Map<String, PartOfSpeech> partOfSpeechData	=
		MapFactory.createNewMap();

	/**	Tag to general tag name map. */

	protected Map<String, String> generalTagNames	=
		MapFactory.createNewMap();

	/**	Character separating multiple part of speech tags. */

	protected char tagSeparator			= '|';
	protected String tagSeparatorString	= "|";

	/**	Add a part of speech tag.
	 *
	 *	@param	tag				Tag name.
	 *	@param	wordClass		The word class.
	 *	@param	majorWordClass	The major word class.
	 *	@param	lemmaWordClass	The lemma word class.
	 *	@param	generalTagName	The general tag name.
	 *	@param	description		The description.
	 */

	public void addTag
	(
		String tag ,
		String wordClass ,
		String majorWordClass ,
		String lemmaWordClass ,
		String generalTagName ,
		String description
	)
	{
								//	Allocate part of speech object
								//	to hold data.

		PartOfSpeech partOfSpeech	=
			new PartOfSpeech
			(
				tag ,
				wordClass ,
				majorWordClass ,
				lemmaWordClass ,
				generalTagName ,
				description
			);
								//	Map and store tag to part of speech
								//	information.

		partOfSpeechData.put( tag , partOfSpeech );

								//	Set general tag name if found.

		if ( generalTagName != null )
		{
			generalTagNames.put
			(
				generalTagName ,
				tag
			);
		}
	}

	/**	Add a part of speech.
	 *
	 *	@param	partOfSpeech 	The part of speech to add.
	 */

	public void addPartOfSpeech( PartOfSpeech partOfSpeech )
	{
		if ( partOfSpeech == null ) return;

								//	Add part of speech.

		partOfSpeechData.put( partOfSpeech.getTag() , partOfSpeech );

								//	Set general tag name if found.

		if ( partOfSpeech.getGeneralTagName() != null )
		{
			generalTagNames.put
			(
				partOfSpeech.getGeneralTagName() ,
				partOfSpeech.getTag()
			);
		}
	}

	/**	Load part of speech tags.
	 *
	 *	@param	tagsInputStream		Input stream for part of speech tags
	 *								information.
	 *	@param	encoding			Character encoding for stream.
	 *
	 *	@throws	IOException			When input stream cannot be read.
	 */

	public void loadTags( InputStream tagsInputStream , String encoding )
		throws IOException
	{
								//	Read tag information from file.

		UTF8Properties tagProperties	= new UTF8Properties();

		tagProperties.load( tagsInputStream );

								//	Create PartOfSpeech object
								//	for each tag and add to the
								//	result list.
		int tagNumber	= 0;

		String tagKey	=
			"tag" + StringUtils.intToStringWithZeroFill( tagNumber , 3 );

		String tagName	= tagKey + ".name";

		while ( tagProperties.getProperty( tagName ) != null )
		{
								//	Get tag data.

			String tag	=
				UTF8Properties.convertInputString(
					tagProperties.getProperty( tagName ) );

			String wordClass		=
				tagProperties.getProperty( tagKey + ".wordclass" );

			String majorWordClass	=
				tagProperties.getProperty( tagKey + ".majorwordclass" );

			String generalTagName	=
				tagProperties.getProperty( tagKey + ".generalwordclass" );

			String description		=
				tagProperties.getProperty( tagKey + ".description" );

			String lemmaWordClass	=
				tagProperties.getProperty( tagKey + ".lemmawordclass" );

								//	Add tag.
			addTag
			(
				tag ,
				wordClass ,
				majorWordClass ,
				lemmaWordClass ,
				generalTagName ,
				description
			);
            					//	Get next tag name.
			tagNumber++;

			tagKey	=
				"tag" + StringUtils.intToStringWithZeroFill( tagNumber , 3 );

			tagName	= tagKey + ".name";
		}
	}

	/**	Save part of speech tags to* a properties file.
	 *
	 *	@param	fileName		Output file name.

	 *	@throws	IOException		When input stream cannot be read.
	 */

	public void saveTags( String fileName )
		throws IOException
	{
		UTF8Properties posTagProperties	= new UTF8Properties();

		int i	= 0;

		for ( PartOfSpeech posTag : getTags() )
		{
			String key	=
				 "tag" + StringUtils.intToStringWithZeroFill( i , 3 );

			String name	= posTag.getTag();

			String escapedName	= name;

			if ( name.equals( "'" ) )
			{
				escapedName	= "\\" + "u0027";
			}
			else if ( name.equals( "\"" ) )
			{
				escapedName	= "\\" + "u0022";
			}
			else if ( name.equals( ":" ) )
			{
				escapedName	= "\\" + "u003A";
			}
			else if ( ( name.length() == 1 ) && ( name.charAt( 0 ) > 255 ) )
			{
				String sc		= Integer.toString( name.charAt( 0 ) , 16 );
				sc				= StringUtils.zeroPad( sc , 4 );
				escapedName	=  "\\" + "u" + sc;
			}

			posTagProperties.put( key + ".name" , escapedName );

			posTagProperties.put(
				key + ".wordclass" , posTag.getWordClass() );

			posTagProperties.put(
				key + ".majorwordclass" , posTag.getMajorWordClass() );

			String lemmaWordClass	= posTag.getLemmaWordClass();

			posTagProperties.put(
				key + ".lemmawordclass" , lemmaWordClass );

			String description	= posTag.getDescription();

			if ( ( description != null ) && ( description.length() > 0 ) )
			{
				posTagProperties.put( key + ".description" , description );
			}

			String generalWordClass	=
				posTag.getGeneralTagName();

			if
			(
				( generalWordClass != null ) &&
				( generalWordClass.length() > 0 )
			)
			{
				posTagProperties.put(
					key + ".generalwordclass" , generalWordClass );
			}

			i++;
		}

		UTF8PropertyUtils.saveUTF8Properties
		(
			posTagProperties,
			fileName ,
			""
        );
	}

	/**	Get the part of speech tag for a singular noun.
	 *
	 *	@return		The part of speech tag for a singular noun.
	 */

	public String getSingularNounTag()
	{
		return generalTagNames.get( SINGULAR_NOUN );
	}

	/**	Get the part of speech tag for a plural noun.
	 *
	 *	@return		The part of speech tag for a plural noun.
	 */

	public String getPluralNounTag()
	{
		return generalTagNames.get( PLURAL_NOUN );
	}

	/**	Get the part of speech tag for a possessive singular noun.
	 *
	 *	@return		The part of speech tag for a possessive singular noun.
	 */

	public String getPossessiveSingularNounTag()
	{
		return generalTagNames.get( POSSESSIVE_SINGULAR_NOUN );
	}

	/**	Get the part of speech tag for a possessive plural noun.
	 *
	 *	@return		The part of speech tag for a possessive plural noun.
	 */

	public String getPossessivePluralNounTag()
	{
		return generalTagNames.get( POSSESSIVE_PLURAL_NOUN );
	}

	/**	Get the part of speech tag for a singular proper noun.
	 *
	 *	@return		The part of speech tag for a singular proper noun.
	 */

	public String getSingularProperNounTag()
	{
		return generalTagNames.get( SINGULAR_PROPER_NOUN );
	}

	/**	Get the part of speech tag for a plural proper noun.
	 *
	 *	@return		The part of speech tag for a plural proper noun.
	 */

	public String getPluralProperNounTag()
	{
		return generalTagNames.get( PLURAL_PROPER_NOUN );
	}

	/**	Get the part of speech tag for a possessive singular proper noun.
	 *
	 *	@return		The part of speech tag for a possessive singular
	 *				proper noun.
	 */

	public String getPossessiveSingularProperNounTag()
	{
		return generalTagNames.get(
			POSSESSIVE_SINGULAR_PROPER_NOUN );
	}

	/**	Get the part of speech tag for a possessive plural proper noun.
	 *
	 *	@return		The part of speech tag for a possessive plural
	 *				proper noun.
	 */

	public String getPossessivePluralProperNounTag()
	{
		return generalTagNames.get(
			POSSESSIVE_PLURAL_PROPER_NOUN );
	}

	/**	Get the part of speech tag for a cardinal number.
	 *
	 *	@return		The part of speech tag for a cardinal number.
	 */

	public String getCardinalNumberTag()
	{
		return generalTagNames.get( CARDINAL_NUMERAL );
	}

	/**	Get the part of speech tag for an ordinal number.
	 *
	 *	@return		The part of speech tag for an ordinal number.
	 */

	public String getOrdinalNumberTag()
	{
		return generalTagNames.get( ORDINAL_NUMERAL );
	}

	/**	Get the part of speech tag for an adverb.
	 *
	 *	@return		The part of speech tag for an adverb.
	 */

	public String getAdverbTag()
	{
		return generalTagNames.get( ADVERB );
	}

	/**	Get the part of speech tag for an adjective.
	 *
	 *	@return		The part of speech tag for an adjective.
	 */

	public String getAdjectiveTag()
	{
		return generalTagNames.get( ADJECTIVE );
	}

	/**	Get the part of speech tag for an interjection.
	 *
	 *	@return		The part of speech tag for an interjection.
	 */

	public String getInterjectionTag()
	{
		return generalTagNames.get( INTERJECTION );
	}

	/**	Get the part of speech tag for a verb.
	 *
	 *	@return		The part of speech tag for a verb.
	 */

	public String getVerbTag()
	{
		return generalTagNames.get( VERB );
	}

	/**	Get the part of speech tag for a verb past tense.
	 *
	 *	@return		The part of speech tag for a verb past tense.
	 */

	public String getVerbPastTag()
	{
		return generalTagNames.get( VERB_PAST );
	}

	/**	Get the part of speech tag for a verbal past participle
	 *
	 *	@return		The part of speech tag for a verbal past participle.
	 */

	public String getPastParticipleTag()
	{
		return generalTagNames.get( VERB_PAST_PARTICIPLE );
	}

	/**	Get the part of speech tag for a verbal present participle
	 *
	 *	@return		The part of speech tag for a verbal present participle.
	 */

	public String getPresentParticipleTag()
	{
		return generalTagNames.get( VERB_PRESENT_PARTICIPLE );
	}

	/**	Get the part of speech tag for a symbol.
	 *
	 *	@return		The part of speech tag for a symbol.
	 */

	public String getSymbolTag()
	{
		return generalTagNames.get( SYMBOL );
	}

	/**	Get the part of speech tag for a specified foreign language
	 *
	 *	@param		language	The foreign language.
	 *
	 *	@return		The part of speech tag for the specified foreign language.
	 */

	public String getForeignWordTag( String language )
	{
		String tag	= generalTagNames.get( "foreign-word" );

		if ( language.equals( "latin" ) )
		{
			tag	= generalTagNames.get( "foreign-latin" );
		}
		else if ( language.equals( "german" ) )
		{
			tag	= generalTagNames.get( "foreign-german" );
		}
		else if ( language.equals( "greek" ) )
		{
			tag	= generalTagNames.get( "foreign-greek" );
		}
		else if ( language.equals( "hebrew" ) )
		{
			tag	= generalTagNames.get( "foreign-hebrew" );
		}
		else if ( language.equals( "french" ) )
		{
			tag	= generalTagNames.get( "foreign-french" );
		}
		else if ( language.equals( "italian" ) )
		{
			tag	= generalTagNames.get( "foreign-italian" );
		}

		if ( tag == null )
		{
			tag	= generalTagNames.get( "foreign-word" );
		}

		return tag;
	}

	/**	Get the description for the part of speech.
	 *
	 *	@param	tag		The part of speech tag.
	 *
	 *	@return			The description of the part of speech.
	 */

	public String getDescription( String tag )
	{
		String result	= "";

		PartOfSpeech partOfSpeech	=
			(PartOfSpeech)partOfSpeechData.get( tag );

		if ( partOfSpeech != null )
		{
			result	= partOfSpeech.getDescription();
		}

		return result;
	}

	/**	Get word class for a tag.
	 *
	 *	@param	tag		The part of speech tag.
	 *
	 *	@return			The word class for a tag.
	 */

	public String getWordClass( String tag )
	{
		String result	= "";

		if ( tag != null )
		{
			if	(	(	tag.indexOf( tagSeparatorString ) >= 0 ) &&
						!tag.equals( tagSeparatorString )
				)
			{
				result	= "multiple";
			}
			else
			{
				PartOfSpeech partOfSpeech	=
					(PartOfSpeech)partOfSpeechData.get( tag );

				if ( partOfSpeech != null )
				{
					result	= partOfSpeech.getWordClass();
				}
			}
		}

		return result;
	}

	/**	Get major word class for a tag.
	 *
	 *	@param	tag		The part of speech tag.
	 *
	 *	@return			The major word class for a tag.
	 */

	public String getMajorWordClass( String tag )
	{
		String result	= "";

		if	(	(	tag.indexOf( tagSeparatorString ) >= 0 ) &&
					!tag.equals( tagSeparatorString )
			)
		{
			result	= "multiple";
		}
		else
		{
			PartOfSpeech partOfSpeech	=
				(PartOfSpeech)partOfSpeechData.get( tag );

			if ( partOfSpeech != null )
			{
				result	= partOfSpeech.getMajorWordClass();
			}
		}

		return result;
	}

	/**	Get lemma class for a tag.
	 *
	 *	@param	tag		The part of speech tag.
	 *
	 *	@return				The lemma class for a tag.
	 */

	public String getLemmaWordClass( String tag )
	{
		String result	= "";
/*
		if ( isCompoundTag( tag ) )
		{
			result	= "compound";
		}
		else
		{
*/
			PartOfSpeech partOfSpeech	=
				(PartOfSpeech)partOfSpeechData.get( tag );

			if ( partOfSpeech != null )
			{
				result	= partOfSpeech.getLemmaWordClass();

				if ( result == null ) result	= "none";
			}
/*
		}
*/
		return result;
	}

	/**	Convert proper noun tag to common noun tag.
	 *
	 *	@param	tag		The part of speech tag.
	 *
	 *	@return			Input tag, or common noun tag if input is proper noun tag.
	 */

	public String getCorrespondingCommonNounTag( String tag )
	{
		String result	= tag;

		if ( tag.equals( generalTagNames.get( SINGULAR_PROPER_NOUN ) ) )
		{
			result	= generalTagNames.get( SINGULAR_NOUN );
		}
		else if ( tag.equals( generalTagNames.get( PLURAL_PROPER_NOUN ) ) )
		{
			result	= generalTagNames.get( PLURAL_NOUN );
		}
		else if ( tag.equals( generalTagNames.get(
			POSSESSIVE_SINGULAR_PROPER_NOUN ) ) )
		{
			result	= generalTagNames.get( POSSESSIVE_SINGULAR_NOUN );
		}
		else if ( tag.equals( generalTagNames.get(
			POSSESSIVE_PLURAL_PROPER_NOUN ) ) )
		{
			result	= generalTagNames.get( POSSESSIVE_PLURAL_NOUN );
		}

		return result;
	}

	/**	Check if major word class is a specified value.
	 *
	 *	@param	tag				The part of speech tag.
	 *	@param	majorWordClass	Major word class to check,
	 *
	 *	@return					true if major word class for tag is
	 *							specified value.
	 */

	public boolean majorWordClassEquals( String tag , String majorWordClass )
	{
		boolean result	= false;

		PartOfSpeech partOfSpeech	=
			(PartOfSpeech)partOfSpeechData.get( tag );

		if ( partOfSpeech != null )
		{
			result	=
				partOfSpeech.getMajorWordClass().equalsIgnoreCase(
					majorWordClass );
		}

		return result;
	}

	/**	Get undetermined part of speech tag.
	 *
	 *	@return		Undetermined part of speech tag.
	 */

	public String getUndeterminedTag()
	{
		return generalTagNames.get( UNDETERMINED );
	}

	/**	Is tag for a proper noun.
	 *
	 *	@param	tag		The part of speech tag.
	 *
	 *	@return			true if the tag is for a proper noun.
	 */

	public boolean isProperNounTag( String tag )
	{
		boolean result	= false;

		PartOfSpeech partOfSpeech	=
			(PartOfSpeech)partOfSpeechData.get( tag );

		if ( partOfSpeech != null )
		{
			result	= partOfSpeech.getWordClass().equals( "proper noun" );
		}

		return result;
	}

	/**	Is tag for a proper adjective.
	 *
	 *	@param	tag		The part of speech tag.
	 *
	 *	@return			true if the tag is for a proper adjective.
	 */

	public boolean isProperAdjectiveTag( String tag )
	{
		boolean result	= false;

		PartOfSpeech partOfSpeech	=
			(PartOfSpeech)partOfSpeechData.get( tag );

		if ( partOfSpeech != null )
		{
			result	= partOfSpeech.getWordClass().equals( "proper adjective" );
		}

		return result;
	}

	/**	Is tag for a noun.
	 *
	 *	@param	tag		The part of speech tag.
	 *
	 *	@return			true if the tag is for a noun.
	 */

	public boolean isNounTag( String tag )
	{
		return majorWordClassEquals( tag , "noun" );
	}

	/**	Is tag for a singular noun.
	 *
	 *	@param	tag		The part of speech tag.
	 *
	 *	@return			true if the tag is for a singular noun form.
	 */

	public boolean isSingularNounTag( String tag )
	{
		boolean result	= isNounTag( tag );

		if ( result )
		{
			PartOfSpeech partOfSpeech	=
				(PartOfSpeech)partOfSpeechData.get( tag );

			if ( partOfSpeech != null )
			{
				result	=
					( partOfSpeech.getGeneralTagName().indexOf(
						"singular" ) >= 0 );
			}
		}

		return result;
	}

	/**	Is tag for a pronoun.
	 *
	 *	@param	tag		The part of speech tag.
	 *
	 *	@return			true if the tag is for a pronoun.
	 */

	public boolean isPronounTag( String tag )
	{
		return majorWordClassEquals( tag , "pronoun" );
	}

	/**	Is tag for a personal pronoun.
	 *
	 *	@param	tag		The part of speech tag.
	 *
	 *	@return			true if the tag is for a personal pronoun.
	 */

	public boolean isPersonalPronounTag( String tag )
	{
		boolean result	= false;

		PartOfSpeech partOfSpeech	=
			(PartOfSpeech)partOfSpeechData.get( tag );

		if ( partOfSpeech != null )
		{
			result	=
				partOfSpeech.getWordClass().equals( "personal pronoun" );
		}

		return result;
	}

	/**	Is tag for a verb.
	 *
	 *	@param	tag		The part of speech tag.
	 *
	 *	@return			true if the tag is for a verb.
	 */

	public boolean isVerbTag( String tag )
	{
		return majorWordClassEquals( tag , "verb" );
	}

	/**	Is tag for a determiner.
	 *
	 *	@param	tag		The part of speech tag.
	 *
	 *	@return			true if the tag is for a determiner.
	 */

	public boolean isDeterminerTag( String tag )
	{
		return majorWordClassEquals( tag , "determiner" );
	}

	/**	Is tag for a foreign word.
	 *
	 *	@param	tag		The part of speech tag.
	 *
	 *	@return			true if the tag is for a foreign word.
	 */

	public boolean isForeignWordTag( String tag )
	{
		return majorWordClassEquals( tag , "foreign" );
	}

	/**	Get list of tag entries in PartOfSpeech format.
	 *
	 *	@return		List of tag entries in PartOfSpeech format
	 *				sorted by tag name.
	 */

	public List<PartOfSpeech> getTags()
	{
								//	Contains sorted results.

		SortedArrayList<PartOfSpeech> result	=
			new SortedArrayList<PartOfSpeech>();

								//	Create PartOfSpeech object
								//	for each tag and add to the
								//	result list.

		for ( String tag : partOfSpeechData.keySet() )
		{
								//	Get PartOfSpeech object
								//	with data for this tag.

			PartOfSpeech partOfSpeech	= partOfSpeechData.get( tag );

							   	//	Add PartOfSpeech object to
							   	//	result list.

			result.add( partOfSpeech );
		}

		return result;
	}

	/**	Is tag for a number.
	 *
	 *	@param	tag		The part of speech tag.
	 *
	 *	@return			true if the tag is for a number.
	 */

	public boolean isNumberTag( String tag )
	{
		return majorWordClassEquals( tag , "numeral" );
	}

	/**	Is tag for a symbol.
	 *
	 *	@param	tag		The part of speech tag.
	 *
	 *	@return			true if the tag is for a symbol.
	 */

	public boolean isSymbolTag( String tag )
	{
		return majorWordClassEquals( tag , "symbol" );
	}

	/**	Is tag for punctuation.
	 *
	 *	@param	tag		The part of speech tag.
	 *
	 *	@return				true if the tag is for punctuation.
	 */

	public boolean isPunctuationTag( String tag )
	{
		return majorWordClassEquals( tag , "punctuation" );
	}

	/**	Check if specified tag appears in the tag list.
	 *
	 *	@param	tag		The part of speech tag.
	 *
	 *	@return			true if specified tag in the tag list.
	 */

	public boolean isTag( String tag )
	{
		return partOfSpeechData.containsKey( tag );
	}

	/**	Check of specified tag contains more than one part of speech.
	 *
	 *	@param	tag		The part of speech tag.
	 *
	 *	@return			true if specified tag contains more than one
	 *						part of speech tag.
	 */

	public boolean isCompoundTag( String tag )
	{
		return ( tag.indexOf( tagSeparatorString ) >=0 );
	}

	/**	Is part of speech tag undetermined.
	 *
	 *	@param	tag	Tag to check for being undetermined.
	 *
	 *	@return		True if given tag is undetermined part of speech tag.
	 */

	public boolean isUndeterminedTag( String tag )
	{
		return tag.equals( generalTagNames.get( UNDETERMINED ) );
	}

	/**	Get part of speech separator.
	 *
	 *	@return	Part of speech separator string.
	 */

	public String getTagSeparator()
	{
		return tagSeparatorString;
	}

	/**	Join separate tags into a compound tag..
	 *
	 *	@param	tags		String array of part of speech tags.
	 *	@param	separator	String to separate tags.
	 *
	 *	@return				String containing joined tags.
	 *							The tags are separated by the
	 *							specified separator character.
	 */

	public String joinTags( String[] tags , String separator )
	{
		String result	= "";

		for ( int i = 0 ; i < tags.length ; i++ )
		{
			if ( i > 0 )
			{
				result	+= separator;
			}

			result	+=	tags[ i ];
		}

		return result;
	}

	/**	Join separate tags into a compound tag..
	 *
	 *	@param	tags	String array of part of speech tags.
	 *
	 *	@return			String containing joined tags.
	 *					The tags are separated by the
	 *					default separator character.
	 */

	public String joinTags( String[] tags )
	{
		return joinTags( tags , tagSeparatorString );
	}

	/**	Split compound tag into separate tags.
	 *
	 *	@param	tag		The part of speech tag.
	 *
	 *	@return			String array of tags.  Only one entry if
	 *						tag is not a compound tag.
	 */

	public String[] splitTag( String tag )
	{
		String[] result	= new String[]{ tag };

		if ( tag.indexOf( tagSeparatorString ) >=0 )
		{
			result	= StringUtils.makeTokenArray( tag , tagSeparatorString );
		}

		return result;
	}

	/**	Get number of tags comprising this tag.
	 *
	 *	@param	tag		The part of speech tag.
	 *
	 *	@return			Count of individual part of speech tags
	 *					comprising this tag.
	 */

	public int countTags( String tag )
	{
								//	Most of the time the result will
								//	be one for one tag.
		int result	= 1;
								//	If the tag is just the tag
								//	separator, it represents itself
								//	instead of being the separator.

		if ( ( tag.length() == 1 ) && ( tag.charAt( 0 ) == tagSeparator ) )
		{
		}
								//	Otherwise count the number of tag
								//	separators.
		else
		{
			for ( int i = 0 ; i < tag.length() ; i++ )
			{
				if ( tag.charAt( i ) == tagSeparator )
				{
					result++;
				}
			}
		}
								//	Return tag separator count + 1
								//	as the count of tags.
		return result;
	}

	/**	Get data for a tag.
	 *
	 *	@param	tag		The tag name.
	 *
	 *	@return			Tag data.
	 */

	public PartOfSpeech getTag( String tag )
	{
		PartOfSpeech result	= null;

		if ( partOfSpeechData.containsKey( tag ) )
		{
			result	= partOfSpeechData.get( tag );
		}

		return result;
	}

	/**	Return set of tag names.
	 *
	 *	@return	Tag names as a set.
	 */

	public Set<String> getTagNames()
	{
		return new TreeSet<String>( partOfSpeechData.keySet() );
	}

	/**	Return string form of tag set.
	 *
	 *	@return		String form of tag set.
	 */

	public String toString()
	{
		return partOfSpeechData.toString();
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



