package edu.northwestern.at.morphadorner;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.net.URL;
import java.net.MalformedURLException;
import java.text.*;
import java.util.*;
import java.util.regex.*;

import org.w3c.dom.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import edu.northwestern.at.morphadorner.tools.*;
import edu.northwestern.at.morphadorner.xgtagger.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.adornedword.*;
import edu.northwestern.at.utils.corpuslinguistics.inputter.*;
import edu.northwestern.at.utils.corpuslinguistics.lemmatizer.*;
import edu.northwestern.at.utils.corpuslinguistics.lexicon.*;
import edu.northwestern.at.utils.corpuslinguistics.namerecognizer.*;
import edu.northwestern.at.utils.corpuslinguistics.namestandardizer.*;
import edu.northwestern.at.utils.corpuslinguistics.outputter.*;
import edu.northwestern.at.utils.corpuslinguistics.partsofspeech.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.guesser.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.hepple.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.hepple.rules.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.noopretagger.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.transitionmatrix.*;
import edu.northwestern.at.utils.corpuslinguistics.sentencemelder.*;
import edu.northwestern.at.utils.corpuslinguistics.sentencesplitter.*;
import edu.northwestern.at.utils.corpuslinguistics.spellingmapper.*;
import edu.northwestern.at.utils.corpuslinguistics.spellingstandardizer.*;
import edu.northwestern.at.utils.corpuslinguistics.tokenizer.*;
import edu.northwestern.at.utils.html.*;
import edu.northwestern.at.utils.logger.*;
import edu.northwestern.at.utils.xml.*;

/**	Morphological Adorner.
 *
 *	<p>
 *	Given an input text, the morphological adorner adorns each word with
 *	morphological information such as part of speech, lemma and
 *	standardized spelling.
 *	</p>
 */

public class MorphAdorner
{
	/**	Number of chararacters in a KWIC line. */

	protected static int defaultKWICWidth		= 80;

	/**	Latin words file. */

	protected static String latinWordsFileName	=
		"resources/latinwords.txt";

	/**	Extra words file. */

	protected static String extraWordsFileName	=
		"resources/extrawords.txt";

	/**	Extra words. */

	protected static TaggedStrings extraWords	= null;

	/**	Pattern to match _CapCap */

	protected static Pattern underlineCapCapPattern			=
		Pattern.compile(
			"^_([ABCDEFGHIJKLMNOPQRSTUVWXYZ])([ABCDEFGHIJKLMNOPQRSTUVWXYZ])" );

	protected static final Matcher underlineCapCapMatcher	=
		underlineCapCapPattern.matcher( "" );

	/**	Spelling tokenizer for lemmatization.
	 */

	protected static WordTokenizer spellingTokenizer	=
		new PennTreebankTokenizer();

	/**	Part of speech tags. */

	protected static PartOfSpeechTags partOfSpeechTags;

	/**	Part of speech tagger. */

	protected static PartOfSpeechTagger tagger;

	/**	Part of speech retagger. */

	protected static PartOfSpeechRetagger retagger;

	/**	Word lexicon. */

	protected static Lexicon wordLexicon;

	/**	Part of speech guesser. */

	protected static PartOfSpeechGuesser partOfSpeechGuesser;

	/**	Suffix lexicon. */

	protected static Lexicon suffixLexicon;

	/**	Transition matrix. */

	protected static TransitionMatrix transitionMatrix;

	/**	Spelling standardizer. */

	protected static SpellingStandardizer spellingStandardizer;

	/**	Spelling mapper. */

	protected static SpellingMapper spellingMapper;

	/**	Proper name standardizer. */

	protected static NameStandardizer nameStandardizer;

	/**	Lemmatizer. */

	protected static Lemmatizer lemmatizer;

	/**	Sentence splitter factory. */

	protected static SentenceSplitterFactory sentenceSplitterFactory;

	/**	Outputter factory. */

	protected static AdornedWordOutputterFactory outputterFactory;

	/**	Word tokenizer factory. */

	protected static WordTokenizerFactory wordTokenizerFactory;

	/** Text file inputter factory. */

	protected static TextInputterFactory inputterFactory;

	/**	Proper names. */

	protected static Names names	= new Names();

	/**	Part of speech tag separator. */

	protected static String tagSeparator	= "|";

	/*	Separates lemmata in compound lemma forms. */

	protected static String lemmaSeparator	= "|";

	/**	Runtime system. */

	protected static Runtime runTime	= Runtime.getRuntime();

	/**	Log current memory usage.
	 *
	 *	@param	title	Title.
	 */

	protected static void logMemoryUsage( String title )
	{
		long freeMem	= runTime.freeMemory();
		long totalMem	= runTime.totalMemory();

		MorphAdornerLogger.println
		(
			"Memory_used" ,
			new Object[]
			{
				title ,
				Formatters.formatLongWithCommas( freeMem ) ,
				Formatters.formatLongWithCommas( totalMem )
			}
		);
    }

	/**	Loads the word lexicon.
	 *
	 *	@return		The word lexicon.
	 */

	protected static Lexicon loadWordLexicon()
		throws IOException
	{
								//	Load word lexicon if given.

		long startTime					= System.currentTimeMillis();

		LexiconFactory lexiconFactory	= new LexiconFactory();
		Lexicon wordLexicon				= lexiconFactory.newLexicon();

		if ( MorphAdornerSettings.wordLexiconURL != null )
		{
			wordLexicon.loadLexicon(
				MorphAdornerSettings.wordLexiconURL , "utf-8" );
		}

		MorphAdornerLogger.println
		(
			"Loaded_word_lexicon" ,
			new Object[]
			{
				Formatters.formatIntegerWithCommas
				(
					wordLexicon.getLexiconSize()
				) ,
				durationString( startTime )
			}
		);
								//	Set logger into word lexicon.

		((UsesLogger)wordLexicon).setLogger(
			MorphAdornerLogger.getLogger() );

		return wordLexicon;
	}

	/**	Loads the suffix lexicon.
	 *
	 *	@return				The suffix lexicon.
	 */

	protected static Lexicon loadSuffixLexicon()
		throws IOException
	{
								//	Load suffix lexicon if given.

		long startTime	= System.currentTimeMillis();

		LexiconFactory lexiconFactory	= new LexiconFactory();
		Lexicon suffixLexicon			= lexiconFactory.newLexicon();

		if ( MorphAdornerSettings.suffixLexiconURL != null )
		{
			suffixLexicon.loadLexicon(
				MorphAdornerSettings.suffixLexiconURL , "utf-8" );
		}

		MorphAdornerLogger.println
		(
			"Loaded_suffix_lexicon" ,
			new Object[]
			{
				Formatters.formatIntegerWithCommas
				(
					suffixLexicon.getLexiconSize()
				) ,
				durationString( startTime )
			}
		);
								//	Set logger into suffix lexicon.

		((UsesLogger)suffixLexicon).setLogger(
			MorphAdornerLogger.getLogger() );

		return suffixLexicon;
	}

	/**	Loads the transition matrix.
	 *
	 *	@param		tagger	Part of speech tagger.
	 *
	 *	@return		The transition matrix.
	 */

	protected static TransitionMatrix loadTransitionMatrix
	(
		PartOfSpeechTagger tagger
	)
		throws IOException
	{
		TransitionMatrix transitionMatrix	= new TransitionMatrix();

								//	Load transition matrix if given.

		if	( 	( MorphAdornerSettings.transitionMatrixURL != null ) &&
				( tagger.usesTransitionProbabilities() ) )
		{
			long startTime	= System.currentTimeMillis();

			transitionMatrix.loadTransitionMatrix
			(
				MorphAdornerSettings.transitionMatrixURL ,
				"utf-8" ,
				'\t'
			);
								//	Set transition matrix into
								//	part of speech tagger.

			tagger.setTransitionMatrix( transitionMatrix );

			MorphAdornerLogger.println
			(
				"Loaded_transition_matrix" ,
				new Object[]{ durationString( startTime ) }
			);
								//	Set logger into transition matrix.

			transitionMatrix.setLogger( MorphAdornerLogger.getLogger() );
		}

		return transitionMatrix;
	}

	/**	Loads part of speech tagger rules.
	 *
	 *	@param		tagger	Part of speech tagger.
	 */

	protected static void loadTaggerRules
	(
		PartOfSpeechTagger tagger
	)
		throws InvalidRuleException, IOException
	{
		if	(	( MorphAdornerSettings.contextRulesURL != null ) &&
				( tagger.usesContextRules() ) )
		{
			String[] contextRules	=
				new TextFile
				(
					MorphAdornerSettings.contextRulesURL ,
					"utf-8"
				).toArray();

			tagger.setContextRules( contextRules );
		}

		if	(	( MorphAdornerSettings.lexicalRulesURL != null ) &&
				( tagger.usesLexicalRules() ) )
		{
			String[] lexicalRules	=
				new TextFile
				(
					MorphAdornerSettings.lexicalRulesURL ,
					"utf-8"
				).toArray();

			tagger.setLexicalRules( lexicalRules );
		}
	}

	/**	Create spelling standardizer.
	 *
	 *	@return				The spelling standardizer.
	 */

	protected static SpellingStandardizer createSpellingStandardizer
	(
		Lexicon wordLexicon
	)
		throws IOException
	{
								//	Get a spelling standardizer factory.

		SpellingStandardizerFactory spellingStandardizerFactory	=
			new SpellingStandardizerFactory();

								//	Create a spelling standardizer.

		SpellingStandardizer spellingStandardizer	=
			spellingStandardizerFactory.newSpellingStandardizer();

								//	If standardizer created successfully ...

		if	( spellingStandardizer != null )
		{
			long startTime	= System.currentTimeMillis();

								//	Set word lexicon into standardizer.

			if ( spellingStandardizer instanceof UsesLexicon )
			{
				((UsesLexicon)spellingStandardizer).setLexicon( wordLexicon );
			}
								//	Remember number of standard spellings
								//	for display.

			int numberOfStandardSpellings	= 0;

								//	Load standard spellings.

			if ( MorphAdornerSettings.spellingsURL != null )
			{
				spellingStandardizer.loadStandardSpellings
				(
					MorphAdornerSettings.spellingsURL ,
					"utf-8"
				);

				numberOfStandardSpellings	=
					spellingStandardizer.getNumberOfStandardSpellings();

				MorphAdornerLogger.println
				(
					"Loaded_standard_spellings" ,
					new Object[]
					{
						Formatters.formatIntegerWithCommas
						(
							numberOfStandardSpellings
						) ,
						durationString( startTime )
					}
				);
			}
                                //	Add name lists to standard spellings.

			spellingStandardizer.addStandardSpellings(
				names.getFirstNames() );

			spellingStandardizer.addStandardSpellings(
				names.getSurnames() );

			spellingStandardizer.addStandardSpellings(
				names.getPlaceNames().keySet() );

                                //	Load alternate spellings.

			if ( MorphAdornerSettings.alternateSpellingsURLs != null )
			{
				int altSpellingsCount	= 0;

				for (	int i = 0 ;
						i < MorphAdornerSettings.alternateSpellingsURLs.length ;
						i++
					)
				{
					startTime	= System.currentTimeMillis();

					spellingStandardizer.loadAlternativeSpellings
					(
						MorphAdornerSettings.alternateSpellingsURLs[ i ] ,
						"utf-8" ,
						"\t"
					);

					MorphAdornerLogger.println
					(
						"Loaded_alternate_spellings" ,
						new Object[]
						{
							Formatters.formatIntegerWithCommas
							(
								spellingStandardizer.
									getNumberOfAlternateSpellings() -
										altSpellingsCount
							) ,
							durationString( startTime )
						}
					);

					altSpellingsCount	=
						spellingStandardizer.getNumberOfAlternateSpellings();
				}
			}
                                //	Load alternate spellings by word class.

			if ( MorphAdornerSettings.alternateSpellingsByWordClassURLs != null )
			{
				int[] altCountsCum	= new int[]{ 0 , 0 };

				for (	int i = 0 ;
						i < MorphAdornerSettings.alternateSpellingsByWordClassURLs.length ;
						i++
					)
				{
					startTime	= System.currentTimeMillis();

					spellingStandardizer.loadAlternativeSpellingsByWordClass
					(
						MorphAdornerSettings.alternateSpellingsByWordClassURLs[ i ] ,
						"utf-8"
					);

					int[] altCounts	=
						spellingStandardizer.getNumberOfAlternateSpellingsByWordClass();

					MorphAdornerLogger.println
					(
						"Loaded_alternate_spellings_by_word_class" ,
						new Object[]
						{
							Formatters.formatIntegerWithCommas
							(
								altCounts[ 1 ] - altCountsCum[ 1 ]
							) ,
							Formatters.formatIntegerWithCommas
							(
								altCounts[ 0 ] - altCountsCum[ 0 ]
							) ,
							durationString( startTime )
						}
					);

					altCountsCum[ 0 ]	= altCounts[ 0 ];
					altCountsCum[ 1 ]	= altCounts[ 1 ];
				}
			}
								//	Set logger into standardizer.

			if ( nameStandardizer instanceof UsesLogger )
			{
				((UsesLogger)spellingStandardizer).setLogger(
					MorphAdornerLogger.getLogger() );
			}
		}

		return spellingStandardizer;
	}

	/**	Create spelling mapper.
	 *
	 *	@return				The spelling mapper.
	 */

	protected static SpellingMapper createSpellingMapper()
		throws IOException
	{
		SpellingMapperFactory spellingMapperFactory	=
			new SpellingMapperFactory();

		return spellingMapperFactory.newSpellingMapper();
	}

	/**	Create proper name standardizer.
	 *
	 *	@param	wordLexicon	The word lexicon containing names.
	 *
	 *	@return				The name standardizer.
	 */

	protected static NameStandardizer createNameStandardizer
	(
		Lexicon wordLexicon
	)
		throws IOException
	{
		NameStandardizerFactory nameStandardizerFactory	=
			new NameStandardizerFactory();

		NameStandardizer nameStandardizer	=
			nameStandardizerFactory.newNameStandardizer();

		if	( nameStandardizer != null )
		{
								//	Load names from word lexicon
								//	into standardizer.

			if ( wordLexicon != null )
			{
				long startTime	= System.currentTimeMillis();

				nameStandardizer.loadNamesFromLexicon( wordLexicon );

				int numberOfNames	=
					nameStandardizer.getNumberOfNames();

				MorphAdornerLogger.println
				(
					"Loaded_names" ,
					new Object[]
					{
						Formatters.formatIntegerWithCommas
						(
							numberOfNames
						) ,
						durationString( startTime )
					}
				);
			}
								//	Set logger into standardizer.

			if ( nameStandardizer instanceof UsesLogger )
			{
				((UsesLogger)nameStandardizer).setLogger(
					MorphAdornerLogger.getLogger() );
			}
		}

		return nameStandardizer;
	}

	/**	Initialize adornment classes.
	 */

	protected static void initializeAdornment()
	{
		try
		{
								//	Create a sentence splitter factory.

			sentenceSplitterFactory		= new SentenceSplitterFactory();

								//	Create an outputter factory.

			outputterFactory			= new AdornedWordOutputterFactory();

								//	Create a word tokenizer factory.

			wordTokenizerFactory		= new WordTokenizerFactory();

								//	Text file inputter factory.

			inputterFactory				= new TextInputterFactory();

    								//	Create part of speech
    								//	tags list.

			PartOfSpeechTagsFactory partOfSpeechTagsFactory	=
				new PartOfSpeechTagsFactory();

			partOfSpeechTags	=
				partOfSpeechTagsFactory.newPartOfSpeechTags();

								//	Get part of speech tag separator.

			tagSeparator		= partOfSpeechTags.getTagSeparator();

								//	Create a part of speech tagger.

			PartOfSpeechTaggerFactory taggerFactory =
				new PartOfSpeechTaggerFactory();

			tagger = taggerFactory.newPartOfSpeechTagger();

								//	Create a part of speech retagger.

			PartOfSpeechRetaggerFactory retaggerFactory =
				new PartOfSpeechRetaggerFactory();

			retagger = retaggerFactory.newPartOfSpeechRetagger();

								//	Set logger into tagger.

			((UsesLogger)tagger).setLogger(
				MorphAdornerLogger.getLogger() );

								//	Set logger into retagger.

			((UsesLogger)retagger).setLogger(
				MorphAdornerLogger.getLogger() );

								//	Set retagger into tagger.

			tagger.setRetagger( retagger );

								//	Display what types of tagger and
								//	retagger we are using.

			MorphAdornerLogger.println( "Using" , new Object[]{ tagger.toString() } );
			MorphAdornerLogger.println( "Using" , new Object[]{ retagger.toString() } );

								//	Load word lexicon.

			wordLexicon	= loadWordLexicon();

								//	Set parts of speech into lexicon.

			wordLexicon.setPartOfSpeechTags( partOfSpeechTags );

								//	Get a part of speech guesser
								//	for words not in the lexicon.

			partOfSpeechGuesser	=
				new PartOfSpeechGuesserFactory().newPartOfSpeechGuesser();

								//	Set check possessives flag.

			boolean checkPossessives	=
				MorphAdornerSettings.getBooleanProperty(
					"partofspeechguesser.check_possessives" , false );


			partOfSpeechGuesser.setCheckPossessives( checkPossessives );

								//	Set guesser into tagger. */

			tagger.setPartOfSpeechGuesser( partOfSpeechGuesser );

								//	Set guesser into word lexicon. */

			partOfSpeechGuesser.setWordLexicon( wordLexicon );

								//	Set logger into guesser.

			((UsesLogger)partOfSpeechGuesser).setLogger(
				MorphAdornerLogger.getLogger() );

								//	Load suffix lexicon if given.

			suffixLexicon	= loadSuffixLexicon();

								//	Set suffix lexicon into guesser.

			partOfSpeechGuesser.setSuffixLexicon( suffixLexicon );

								//	Add extra words.
			extraWords	=
				getExtraWordsList
				(
					extraWordsFileName ,
					partOfSpeechTags.getSingularProperNounTag() ,
					"Loaded_extra_words"
				);

			partOfSpeechGuesser.addAuxiliaryWordList( extraWords );

								//	Add name lists.

			partOfSpeechGuesser.addAuxiliaryWordList
			(
				new TaggedStringsSet
				(
					names.getPlaceNames().keySet() ,
					partOfSpeechTags.getSingularProperNounTag()
				)
			);

			partOfSpeechGuesser.addAuxiliaryWordList
			(
				new TaggedStringsSet
				(
					names.getFirstNames() ,
					partOfSpeechTags.getSingularProperNounTag()
				)
			);

			partOfSpeechGuesser.addAuxiliaryWordList
			(
				new TaggedStringsSet
				(
					names.getSurnames() ,
					partOfSpeechTags.getSingularProperNounTag()
				)
			);
								//	Add latin words.

			if ( MorphAdornerSettings.useLatinWordList )
			{
				partOfSpeechGuesser.addAuxiliaryWordList
				(
					getWordList
					(
						latinWordsFileName ,
						partOfSpeechTags.getForeignWordTag( "latin" ) ,
						"Loaded_latin_words"
					)
				);
			}
								//	Add extra abbreviations.

			if ( MorphAdornerSettings.abbreviationsURL.length() > 0 )
			{
				loadAbbreviations
				(
					URLUtils.getURLFromFileNameOrURL
					(
						MorphAdornerSettings.abbreviationsURL
					).toString() ,
					"Loaded_abbreviations"
				);
			}
								//	Set tagger to use lexicon.

			tagger.setLexicon( wordLexicon );

								//	Load tagger rules if given.

			loadTaggerRules( tagger );

								//	Load transition matrix if given.

			transitionMatrix		= loadTransitionMatrix( tagger );

								//	Create a spelling standardizer.

			spellingStandardizer	= createSpellingStandardizer( wordLexicon );

								//	Create a spelling mapper.

			spellingMapper			= createSpellingMapper();

								//	Create a name standardizer.

			nameStandardizer		= createNameStandardizer( wordLexicon );

								//	Add spelling standardizer to
								//	part of speech guesser.

			if ( spellingStandardizer != null )
			{
				partOfSpeechGuesser.setSpellingStandardizer(
					spellingStandardizer );
			}
								//	Create a lemmatizer.

			LemmatizerFactory lemmatizerFactory	=
				new LemmatizerFactory();

			lemmatizer	= lemmatizerFactory.newLemmatizer();

								//	Get lemma separator.

			lemmaSeparator	= lemmatizer.getLemmaSeparator();

								//	Set lexicon for lemmatizer.

			lemmatizer.setLexicon( wordLexicon );

								//	Set standard word list for lemmatizer.

			lemmatizer.setDictionary
			(
				spellingStandardizer.getStandardSpellings()
			);
								//	Set logger into lemmatizer.

			((UsesLogger)lemmatizer).setLogger(
				MorphAdornerLogger.getLogger() );

		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}

	/**	Process list of files containing text to adorn.
	 */

	protected static void processInputFiles()
	{
		long processStartTime	= System.currentTimeMillis();

								//	Display # of files to process.

		switch ( MorphAdornerSettings.fileNames.length )
		{
			case 0	:	MorphAdornerLogger.println
						(
							"No_files_to_process"
						);
						break;

			case 1	:	MorphAdornerLogger.println
						(
							"One_file_to_process"
						);
						break;

			default	:	MorphAdornerLogger.println
						(
							"Number_of_files_to_process" ,
							new Object[]
							{
								Formatters.formatIntegerWithCommas
								(
									MorphAdornerSettings.fileNames.length
								)
							}
						);
						break;
		}
								//	Note if we're using our XML handler.

		boolean useXMLHandler	=
			MorphAdornerSettings.getBooleanProperty(
				"adorner.handle_xml" , false );

		logMemoryUsage( "Before processing input texts: " );

								//	Loop over the input file names.

		for ( int i = 0 ; i < MorphAdornerSettings.fileNames.length ; i++ )
		{
								//	Get the next input file name.

			String inputFileName	= MorphAdornerSettings.fileNames[ i ];

								//	Say we're processing it.

			MorphAdornerLogger.println
			(
				"Processing_file" ,
				new Object[]{ inputFileName }
			);

			try
			{
								//	Are we using XGTagger to process
								//	input XML?

				if ( useXMLHandler )
				{
								//	See if input file is already adorned.
								//	If so, we will readorn it keeping
								//	the existing word IDs.

					if ( isAdorned( inputFileName , 500 ) )
					{
						readorn( inputFileName );
					}
					else
					{
						adornXML( inputFileName );
					}
				}
								//	Not using XML handler -- adorn
								//	as plain text.
				else
				{
					adornFile( inputFileName );
				}
			}
			catch ( Exception e )
			{
				e.printStackTrace();
			}
		}
								//	Display total processing time.

		if ( MorphAdornerSettings.fileNames.length > 0 )
		{
			MorphAdornerLogger.println
			(
				"All_files_adorned" ,
				new Object[]{ durationString( processStartTime ) }
			);
		}
	}

	/**	Adorn XML file.
	 *
	 *	@param	inputFileName	File name of XML file to adorn.
	 *
	 *	@throws	Exception		For variety of errors.
	 */

	public static void adornXML( String inputFileName )
		throws Exception
	{
								//	Skip adornment if output file
								//	already exists and appropriate
								//	option is set.

		if	(	!MorphAdornerSettings.adornExistingXMLFiles &&
				doesOutputFileNameExist( inputFileName )
			)
		{
			MorphAdornerLogger.println
			(
				"Skipping_file_which_is_already_adorned" ,
				new Object[]
				{
					inputFileName
				}
			);

			return;
		}
								//	Create a new text inputter.

		TextInputter inputter	= inputterFactory.newTextInputter();

								//	Enable gap fixer.

		inputter.enableGapFixer( MorphAdornerSettings.fixGapTags );

								//	Enable orig fixer.

		inputter.enableOrigFixer( MorphAdornerSettings.fixOrigTags );

								//	Enable split words fixer.

		inputter.enableSplitWordsFixer
		(
			MorphAdornerSettings.fixSplitWords ,
			MorphAdornerSettings.fixSplitWordsPatternReplacers
		);
								//	Load input text.  May be
								//	split into multiple segments.

		URL inputFileURL	=
			URLUtils.getURLFromFileNameOrURL( inputFileName );

		inputter.loadText
		(
			inputFileURL ,
			"utf-8" ,
			MorphAdornerSettings.xmlSchema
		);
								//	Report number of segments.

		int nSegments		= inputter.getSegmentCount();

		String sSegments	=
			Formatters.formatIntegerWithCommas( nSegments );

		MorphAdornerLogger.println
		(
			"Input_file_split" ,
			new Object[]{ inputFileName , sSegments }
		);
								//	Save running word ID from one
								//	segment to another.

		int runningWordID   = 0;

								//	Track ID for multipart words.

		Map<Integer, Integer> splitWords	= MapFactory.createNewMap();

								//	Total number of words adorned.

		int totalWords		= 0;

								//	Total number of <pb> elements.

		int totalPageBreaks	= 0;

								//	Adorn each segment separately.

		for ( int j = 0 ; j < nSegments ; j++ )
		{
								//	Get next segment name.

			String segmentName	= inputter.getSegmentName( j );

								//	Only adorn text segments.

			if ( !segmentName.startsWith( "text" ) ) continue;
			if ( segmentName.equals( "text" ) ) continue;

								//	Report which segment is being
								//	adorned.

			MorphAdornerLogger.println
			(
				"Processing_segment" ,
				new Object[]
				{
					segmentName ,
					Formatters.formatIntegerWithCommas( j + 1 ) ,
					sSegments
				}
			);
								//	Get segment text.

			String segmentText	= inputter.getSegmentText( segmentName );

								//	Join some split words.

//			segmentText			= fixSplitWords( segmentText );

								//	Convert XML text to DOM document.

			Document document	=
				XGParser.textToDOM
				(
					MorphAdornerSettings.xgOptions ,
					segmentText
				);
								//	Fix empty soft tags.

			fixEmptySoftTags( document );

								//	Fix sup tags.

			fixSupTags( document );

								//	Add page break count this segment
								//	to total.

			totalPageBreaks	+=	countPageBreaks( document );

								//	Extract plain text for adornment.
			Object[] o	=
				XGParser.extractText
				(
					MorphAdornerSettings.xgOptions ,
					document
				);
								//	Set running word ID.

			XGParser xgParser	= (XGParser)o[ 1 ];

			xgParser.setRunningWordID( runningWordID );

								//	Create adorned output.

			AdornedWordOutputter outputter	=
				adornText( (String)o[ 0 ] , null );

								//	Merged adornments with original
								//	XML text.

			MorphAdornerLogger.println
			(
				"Inserting_adornments_into_xml"
			);

			long startTime	= System.currentTimeMillis();

			Map<Integer, Integer> segmentSplitWords	=
				XGParser.mergeAdornments
				(
					MorphAdornerSettings.xgOptions ,
					(XGParser)o[ 1 ] ,
					document ,
					segmentName ,
					outputter ,
					inputter
				);
								//	Add split words from this segment
								//	to overall map of split words.

			for ( int wid : segmentSplitWords.keySet() )
			{
				if ( segmentSplitWords.get( wid ) > 1 )
				{
					splitWords.put
					(
						wid ,
						segmentSplitWords.get( wid )
					);
				}
			}
								//	Report adornment merge complete.

			MorphAdornerLogger.println
			(
				"Inserted_adornments_into_xml" ,
				new Object[]{ durationString( startTime ) }
			);
								//	Save running word ID for
								//	processing next segment.

			runningWordID	= xgParser.getRunningWordID();

								//	Add count of adorned words to total
								//	for this document.

			totalWords		+= xgParser.getNumberOfAdornedWords();

			xgParser	= null;
			document	= null;
			outputter	= null;
			o[ 0 ]		= null;
			o[ 1 ]		= null;
			o			= null;
		}
								//	Create name of output file to
								//	which to write merged adorned XML.

		String outputFileName	= getOutputFileName( inputFileName );

		long startTime	= System.currentTimeMillis();

		MorphAdornerLogger.println ( "Merging_adorned" );

								//	Merged adorned XML segments to
								//	temporary file.

		File file	= File.createTempFile( "mad" , null );

		file.deleteOnExit();

		String tempFileName	= file.getAbsolutePath();

		mergeXML( inputter , tempFileName );

								//	Read the merged XML file to add
								//	revised IDs and format the XML nicely.

		MorphAdornerLogger.println
		(
			"Writing_merged" ,
			new Object[]{ outputFileName }
		);
								//	Create XML writer facory/

		MorphAdornerXMLWriterFactory xmlWriterFactory	=
			new MorphAdornerXMLWriterFactory();

								//	Create XML writer.

		MorphAdornerXMLWriter xmlWriter	=
			xmlWriterFactory.newMorphAdornerXMLWriter();

								//	Write XML.
		xmlWriter.writeXML
		(
			tempFileName ,
			outputFileName ,
			runningWordID ,
			partOfSpeechTags ,
			splitWords ,
			totalWords ,
			totalPageBreaks
		);
								//	Delete temporary XML file.
								//	May not work on some systems,
								//	but the file will be deleted when
								//	MorphAdorner exits anyway.
		try
		{
			file.delete();
		}
		catch ( Exception e )
		{
//e.printStackTrace();
		}
								//	Report the updated XML has been
								//	written out.

		MorphAdornerLogger.println
		(
			"Adorned_XML_written" ,
			new Object[]
			{
				outputFileName ,
				durationString( startTime )
			}
		);
								//	Close inputter.

		((IsCloseableObject)inputter).close();

		inputter	= null;
		splitWords	= null;
		xmlWriter	= null;

		logMemoryUsage( "After completing " + inputFileName + ": " );
	}

	/**	Generate output file name for adorned output.
	 *
	 *	@param		inputFileName	The input file name.
	 *
	 *	@return						The output file name.
	 *
	 *	@throws		IOException if output directory cannot be created.
	 */

	public static String getOutputFileName( String inputFileName )
		throws IOException
	{
		String result	=
			FileNameUtils.stripPathName( inputFileName );

		result			=
			new File
			(
				MorphAdornerSettings.outputDirectoryName ,
				result
			).getPath();

		if ( !FileUtils.createPathForFile( result ) )
		{
			throw new IOException
			(
				MorphAdornerSettings.getString
				(
					"Unable_to_create_output_directory"
				)
			);
		};

		result	= FileNameUtils.createVersionedFileName( result );

		return result;
	}

	/**	Check if output file name for adorned output already exists.
	 *
	 *	@param		inputFileName	The input file name.
	 *
	 *	@return						True if output file name already exists.
	 */

	public static boolean doesOutputFileNameExist( String inputFileName )
	{
		String outputFileName	=
			FileNameUtils.stripPathName( inputFileName );

		return
			new File
			(
				MorphAdornerSettings.outputDirectoryName ,
				outputFileName
			).exists();
	}

	/**	Perform word adornment processes for a single input file.
	 *
	 *	@param	fileName	Input file name.
	 *
	 *	@throws				Exception if an error occurs.
	 */

	public static AdornedWordOutputter adornFile( String fileName )
		throws IOException
	{
		MorphAdornerLogger.println( "Tagging" , new Object[]{ fileName } );

								//	Get URL for file name.

		URL fileURL	= URLUtils.getURLFromFileNameOrURL( fileName );

								//	Report error if URL bad.
		if ( fileURL == null )
		{
			MorphAdornerLogger.println
			(
				"Bad_file_name_or_URL" ,
				new Object[]{ fileName }
			);

			return null;
		}
								//	Read file text into a string.
                    			//	Report error if we cannot.

		String fileText	= "";

		long startTime	= System.currentTimeMillis();

		try
		{
								//	Create text inputter.

			TextInputter inputter	= inputterFactory.newTextInputter();

								//	Enable gap fixer.

			inputter.enableGapFixer( MorphAdornerSettings.fixGapTags );

								//	Enable orig fixer.

			inputter.enableOrigFixer( MorphAdornerSettings.fixOrigTags );

								//	Load text to adorn.

			inputter.loadText
			(
				fileURL ,
				"utf-8" ,
				MorphAdornerSettings.xmlSchema
			);
								//	Get text.

			fileText	= inputter.getSegmentText( 0 );

			((IsCloseableObject)inputter).close();
		}
		catch ( Exception e )
		{
			MorphAdornerLogger.println
			(
				"Unable_to_read_text" ,
				new Object[]{ fileName }
			);

			return null;
		}

		MorphAdornerLogger.println
		(
			"Loaded_text" ,
			new Object[]{ fileName , durationString( startTime ) }
		);

		return adornText( fileText , fileURL );
	}

	/**	Perform word adornment processes for a single input file.
	 *
	 *	@param	textToAdorn		Text to adorn.
	 *	@param	outputURL		URL for output.
	 *
	 *	@throws					Exception if an error occurs.
	 */

	public static AdornedWordOutputter adornText
	(
		String textToAdorn ,
		URL outputURL
	)
		throws IOException
	{
		long startTime	= System.currentTimeMillis();

								//	Get a sentence splitter.

		SentenceSplitter sentenceSplitter	=
			sentenceSplitterFactory.newSentenceSplitter();

								//	Set logger into splitter.

		((UsesLogger)sentenceSplitter).setLogger(
			MorphAdornerLogger.getLogger() );

								//	Set guesser into splitter.

		sentenceSplitter.setPartOfSpeechGuesser( partOfSpeechGuesser );

								//	Extract the sentences and
								//	words in the sentences.

		List<List<String>> sentences	=
			sentenceSplitter.extractSentences
			(
				textToAdorn ,
				wordTokenizerFactory.newWordTokenizer()
			);
								//	Get count of sentences and words.

       	int[] wordAndSentenceCounts	= getWordAndSentenceCounts( sentences );

		int wordsToTag	= wordAndSentenceCounts[ 1 ];

		MorphAdornerLogger.println
		(
			"Extracted_words" ,
			new Object[]
			{
				Formatters.formatIntegerWithCommas( wordsToTag ) ,
				Formatters.formatIntegerWithCommas( wordAndSentenceCounts[ 0 ] ) ,
				durationString( startTime )
			}
		);
								//	See if we should use standard
								//	spellings to help guess parts of
								//	speech for unknow  words.

		if ( partOfSpeechGuesser != null )
		{
			partOfSpeechGuesser.setTryStandardSpellings(
				MorphAdornerSettings.tryStandardSpellings );
		}
								//	Can't output lemma without a
								//	lemmatizer.

		boolean doOutputLemma	=
			MorphAdornerSettings.outputLemma &&
			( lemmatizer != null );

								//	Can't output standard spelling
								//	without a standardizer.

		boolean doOutputStandardSpelling	=
			MorphAdornerSettings.outputStandardSpelling &&
				( spellingStandardizer != null );

								//	Must output original token if
								//	internal XML handling used.

		boolean doOutputOriginalToken	=
			MorphAdornerSettings.outputOriginalToken ||
			MorphAdornerSettings.useXMLHandler;

								//	Set word attribute names.

		MorphAdornerSettings.setXMLWordAttributes
		(
			doOutputOriginalToken ,
			doOutputLemma ,
			doOutputStandardSpelling
		);
								//	Get part of speech tags for
								//	each word in each sentence.

		startTime		= System.currentTimeMillis();

		List<List<AdornedWord>> result	= tagger.tagSentences( sentences );

		double elapsed	=
			( System.currentTimeMillis() - startTime );

		int taggingRate	= (int)( ( wordsToTag / elapsed ) * 1000.0D );

		MorphAdornerLogger.println
		(
			"Tagging_complete" ,
			new Object[]
			{
				durationString( startTime ) ,
				Formatters.formatIntegerWithCommas( taggingRate )
			}
		);
								//	Generate tagged word output.

		MorphAdornerLogger.println( "Generating_other_adornments" );

		startTime	= System.currentTimeMillis();

								//	Create a tagged text output writer.

		AdornedWordOutputter outputter	=
			outputterFactory.newAdornedWordOutputter();

		outputter.setWordAttributeNames
		(
			MorphAdornerSettings.getXMLWordAttributes()
		);

		if ( outputURL != null )
		{
			outputter.createOutputFile
			(
				getOutputFileName
				(
					URLUtils.getFileNameFromURL
					(
						outputURL ,
						MorphAdornerSettings.outputDirectoryName
					)
				) ,
				"utf-8" ,
				'\t'
			);
		}
		else
		{
			File file	= File.createTempFile( "mad" , null );

			file.deleteOnExit();

			String tempFileName	= file.getAbsolutePath();

			outputter.createOutputFile
			(
				 tempFileName ,
				"utf-8" ,
				'\t'
			);
		}
								//	Figure out what we are to output.

		int sentenceNumber		= 0;
		int wordNumber			= 0;

		String lemma				= "";
		String correctedSpelling	= "";
		String standardizedSpelling	= "";
		AdornedWord adornedWord;
		String sSentenceNumber		= "";
		String sWordNumber			= "";
		String eosFlag				= "";
		String originalToken		= "";
		String partOfSpeechTag		= "";
		String xmlSurroundMarker	=
			MorphAdornerSettings.xgOptions.getSurroundMarker().trim();

								//	Get undetermined part of speech tag.

		String undeterminedPosTag	= partOfSpeechTags.getUndeterminedTag();

								//	Holds output for each word.

		List<String> outputAdornments	= ListFactory.createNewList();

								//	Output the tagged words.

		Iterator<List<AdornedWord>> iterator	= result.iterator();

								//	Loop over tagged sentences.

		while ( iterator.hasNext() )
		{
								//	Get next sentence.

			List<AdornedWord> sentenceFromTagger	= iterator.next();

								//	Get sentence number.

			sentenceNumber++;
			sSentenceNumber	= sentenceNumber + "";

			int sentenceSizeM1	= sentenceFromTagger.size() - 1;

								//	Reset word numbers for each
								//	sentence if running word numbers
								//	not requested.

			if ( !MorphAdornerSettings.outputRunningWordNumbers )
			{
				wordNumber	= 0;
			}
								//	Loop over words in the sentence.

			for ( int j = 0 ; j < sentenceFromTagger.size() ; j++ )
			{
								//	Clear output list for this word.

				outputAdornments.clear();

								//	Add sentence number to output.

				if ( MorphAdornerSettings.outputSentenceNumber )
				{
					outputAdornments.add( sSentenceNumber );
				}
								//	Add word number to output.

				wordNumber++;

				if ( MorphAdornerSettings.outputWordNumber )
				{
					sWordNumber	= wordNumber + "";
					outputAdornments.add( sWordNumber );
				}
								//	Get the word and its part of speech
								//	tag.

				adornedWord	= sentenceFromTagger.get( j );

								//	Add original spelling to output.

				originalToken	= adornedWord.getToken();

				if ( MorphAdornerSettings.outputOriginalToken )
				{
					outputAdornments.add( originalToken );
				}
								//	Add corrected spelling to output.

				correctedSpelling		= adornedWord.getSpelling();

				standardizedSpelling	=
					adornedWord.getStandardSpelling();

				if ( MorphAdornerSettings.outputSpelling )
				{
					outputAdornments.add( correctedSpelling );
				}
								//	Get part of speech to output.

				partOfSpeechTag	= adornedWord.getPartsOfSpeech();

								//	Get standardized spelling to output.

				if ( doOutputStandardSpelling )
				{
					standardizedSpelling	=
						getStandardizedSpelling
						(
							spellingStandardizer ,
							nameStandardizer ,
							correctedSpelling ,
							standardizedSpelling ,
							partOfSpeechTag
						);

					if ( spellingMapper != null )
					{
						standardizedSpelling	=
							spellingMapper.mapSpelling(
								standardizedSpelling );
					}
				}
								//	Get lemma to output.

				if ( doOutputLemma )
				{
								//	Try lexicon first unless we're ignoring
								//	lemma entries in the lexicon.

					if ( !MorphAdornerSettings.ignoreLexiconEntriesForLemmatization )
					{
						lemma	=
							wordLexicon.getLemma
							(
								correctedSpelling ,
								partOfSpeechTag
							);
					}
					else
					{
						lemma	= "*";
					}
								//	Lemma not found in word lexicon.
								//	Use lemmatizer.

					if	(	lemma.equals( "*" ) && ( lemmatizer != null ) )
					{
						if ( standardizedSpelling.length() > 0 )
						{
							lemma	=
								getLemma
								(
									lemmatizer ,
									standardizedSpelling ,
									partOfSpeechTag
								);
						}
						else
						{
							lemma	=
								getLemma
								(
									lemmatizer ,
									correctedSpelling ,
									partOfSpeechTag
								);
						}
					}
								//	Force lemma to lowercase except
								//	for proper noun tagged word.

					if ( lemma.indexOf( lemmaSeparator ) < 0 )
					{
						if ( !partOfSpeechTags.isProperNounTag(
							partOfSpeechTag ) )
						{
							lemma	= lemma.toLowerCase();
						}
					}
				}
								//	Rectify # of individual lemmata
								//	with # of parts of speech for word.
								//	if they don't match, set the
								//	parts of speech to undetermined,
								//	the standard spelling to the
								//	original spelling,
								//	and the lemma to the lowercase
								//	original spelling.

				if ( lemmatizer != null )
				{
					if	(	partOfSpeechTags.countTags( partOfSpeechTag ) !=
							lemmatizer.countLemmata( lemma )
						)
					{
						partOfSpeechTag	= undeterminedPosTag;
					}

					if	(	partOfSpeechTag.equals( undeterminedPosTag ) ||
						    ( lemma.length() == 0 )
						)
					{
						lemma					=
							correctedSpelling.toLowerCase();

						standardizedSpelling	= correctedSpelling;
						partOfSpeechTag			= undeterminedPosTag;
					}
				}
								//	Output rectified part of speech,
								//	lemma, and standard spelling.

				if ( MorphAdornerSettings.outputPartOfSpeech)
				{
					outputAdornments.add( partOfSpeechTag );
				}

				if ( doOutputStandardSpelling )
				{
					outputAdornments.add( standardizedSpelling );
				}

				if ( doOutputLemma )
				{
					outputAdornments.add( lemma );
				}
								//	Add end of sentence flag.

				if ( MorphAdornerSettings.outputEOSFlag )
				{
					if ( MorphAdornerSettings.useXMLHandler )
					{
						eosFlag	= "0";

						if ( j < sentenceSizeM1 )
						{
							AdornedWord nextAdornedWord	=
								sentenceFromTagger.get( j + 1 );

							if	( nextAdornedWord.getToken().equals(
									xmlSurroundMarker )
								)
							{
								if	( 	originalToken.endsWith( "." ) ||
										originalToken.endsWith( "!" ) ||
										originalToken.endsWith( "?" ) ||
										originalToken.endsWith( "'" ) ||
										originalToken.endsWith( "\"" ) ||
										originalToken.endsWith(
											CharUtils.RSQUOTE_STRING ) ||
										originalToken.endsWith(
											CharUtils.RDQUOTE_STRING  ) ||
										originalToken.endsWith( "}" ) ||
										originalToken.endsWith( "]" ) ||
										originalToken.endsWith( ")" )
									)
								{
									eosFlag	= "1";
								}
							}
						}
						else
						{
							eosFlag	= "1";
						}
					}
					else
					{
						eosFlag	= ( j >= sentenceSizeM1 ) ? "1" : "0";
					}

					outputAdornments.add( eosFlag );
				}
								//	Add KWIC window to output.

				if ( MorphAdornerSettings.outputKWIC )
				{
					String[] kwics		=
						getKWIC
						(
							(List<AdornedWord>)sentenceFromTagger ,
							j ,
							MorphAdornerSettings.outputKWICWidth
						);

					outputAdornments.add( kwics[ 0 ] );
					outputAdornments.add( kwics[ 2 ] );
				}

				outputter.outputWordAndAdornments( outputAdornments );
			}
		}

		outputter.close();

		if ( outputURL != null )
		{
			MorphAdornerLogger.println
			(
				"Adornments_written_to" ,
				new Object[]
				{
					getOutputFileName
					(
						URLUtils.getFileNameFromURL
						(
							outputURL ,
							MorphAdornerSettings.outputDirectoryName
						)
					) ,
					durationString( startTime )
				}
			);
		}
		else
		{
			MorphAdornerLogger.println
			(
				"Adornments_generated" ,
				new Object[]
				{
					durationString( startTime )
				}
			);
		}

		sentences.clear();
		result.clear();

		sentences	= null;
		result		= null;

		return outputter;
	}

	/**	Readorn adorned XML file.
	 *
	 *	@param	inputFileName	Input XML file name.
	 *
	 *	@throws	SAXException
	 */

	public static void readorn( String inputFileName )
		throws SAXException, IOException, FileNotFoundException
	{
		MorphAdornerLogger.println( "Loading_previously_adorned" );

		long startTime	= System.currentTimeMillis();

		ExtendedAdornedWordFilter wordInfoFilter	=
			new ExtendedAdornedWordFilter
			(
				XMLReaderFactory.createXMLReader()
			);

		XMLFilter stripFilter	=
			new StripWordAttributesFilter( wordInfoFilter );

		File file	= File.createTempFile( "mad" , null );

		file.deleteOnExit();

		String tempFileName	= file.getAbsolutePath();

		new FilterAdornedFile
		(
			inputFileName ,
			tempFileName ,
			stripFilter
		);
								//	Get sentences.

		List<List<ExtendedAdornedWord>> sentences	=
			wordInfoFilter.getSentences();

		MorphAdornerLogger.println
		(
			"Loaded_existing_words" ,
			new Object[]
			{
				Formatters.formatIntegerWithCommas
				(
					wordInfoFilter.getNumberOfWords()
				) ,
				Formatters.formatIntegerWithCommas
				(
					sentences.size()
				) ,
				durationString( startTime )
			}
		);
								//	Disable retagger if it can add or
								//	delete words.

		PartOfSpeechRetagger savedRetagger	= null;

		if ( retagger.canAddOrDeleteWords() )
		{
			savedRetagger	= retagger;

			tagger.setRetagger( new NoopRetagger() );

			MorphAdornerLogger.println
			(
				"Disabling_retagger" ,
				new Object[]{ retagger.toString() }
			);
		}
								//	Retag the sentences.

		startTime		= System.currentTimeMillis();

		tagger.tagAdornedWordSentences( sentences );

								//	Restore saved retagger.

		if ( savedRetagger != null )
		{
			tagger.setRetagger( savedRetagger );
		}
								//	Display tagging rate.
		double elapsed	=
			( System.currentTimeMillis() - startTime );

		int taggingRate	=
			(int)( ( wordInfoFilter.getNumberOfWords() / elapsed ) * 1000.0D );

		MorphAdornerLogger.println
		(
			"Tagging_complete" ,
			new Object[]
			{
				durationString( startTime ) ,
				Formatters.formatIntegerWithCommas( taggingRate )
			}
		);
								//	Generate other adornments.

		MorphAdornerLogger.println( "Generating_other_adornments" );

		startTime	= System.currentTimeMillis();

		String lemma				= "";
		String correctedSpelling	= "";
		String standardizedSpelling	= "";
		AdornedWord adornedWord;
		String originalToken		= "";
		String partOfSpeechTag		= "";

								//	Loop over sentences.

		for ( int i = 0 ; i < sentences.size() ; i++ )
		{
								//	Get next sentence.

			List<ExtendedAdornedWord> sentence	= sentences.get( i );

								//	Update adornments for each word
								//	in sentence.

			for ( int j = 0 ; j < sentence.size() ; j++ )
			{
								//	Get next word in sentence.

				adornedWord				= sentence.get( j );

								//	Update standard spelling.

				originalToken			= adornedWord.getToken();
				correctedSpelling		= adornedWord.getSpelling();
				standardizedSpelling	= adornedWord.getStandardSpelling();
				partOfSpeechTag			= adornedWord.getPartsOfSpeech();

				standardizedSpelling	=
					getStandardizedSpelling
					(
						spellingStandardizer ,
						nameStandardizer ,
						correctedSpelling ,
						standardizedSpelling ,
						partOfSpeechTag
					);

				if ( spellingMapper != null )
				{
					standardizedSpelling	=
						spellingMapper.mapSpelling
						(
							standardizedSpelling
						);
				}
								//	Update lemma.

				if ( !MorphAdornerSettings.ignoreLexiconEntriesForLemmatization )
				{
					lemma	=
						wordLexicon.getLemma
						(
							correctedSpelling ,
							partOfSpeechTag
						);
				}
				else
				{
					lemma	= "*";
				}
								//	Lemma not found in word lexicon.
								//	Use lemmatizer.

				if	(	lemma.equals( "*" ) && ( lemmatizer != null ) )
				{
					if ( standardizedSpelling.length() > 0 )
					{
						lemma	=
							getLemma
							(
								lemmatizer ,
								standardizedSpelling ,
								partOfSpeechTag
							);
					}
					else
					{
						lemma	=
							getLemma
							(
								lemmatizer ,
								correctedSpelling ,
								partOfSpeechTag
							);
					}
				}
								//	Force lemma to lowercase except
								//	for proper noun tagged word.

				if ( lemma.indexOf( lemmaSeparator ) < 0 )
				{
					if ( !partOfSpeechTags.isProperNounTag(
						partOfSpeechTag ) )
					{
						lemma	= lemma.toLowerCase();
					}
				}
			}
		}

		MorphAdornerLogger.println
		(
			"Adornments_generated" ,
			new Object[]
			{
				durationString( startTime )
			}
		);
								//	Create filter to add updated
								//	word attributes.

		AddWordAttributesFilter addFilter	=
			new AddWordAttributesFilter
			(
				XMLReaderFactory.createXMLReader() ,
				wordInfoFilter
			);

		XMLFilter filter	= addFilter;

								//	If we're adding pseudopages,
								//	create a pseudopage adder filter.

		if ( MorphAdornerSettings.outputPseudoPageBoundaryMilestones )
		{
			PseudoPageAdderFilter pseudoPageFilter	=
				new PseudoPageAdderFilter
				(
					addFilter ,
					MorphAdornerSettings.pseudoPageSize ,
					MorphAdornerSettings.pseudoPageContainerDivTypes
				);

			filter	= pseudoPageFilter;
		}
								//	Create name of output file to
								//	which to write merged adorned XML.

		String outputFileName	= getOutputFileName( inputFileName );

		MorphAdornerLogger.println
		(
			"Writing_merged" ,
			new Object[]{ outputFileName }
		);

		startTime	= System.currentTimeMillis();

		new FilterAdornedFile( tempFileName , outputFileName , filter );

		MorphAdornerLogger.println
		(
			"Adorned_XML_written" ,
			new Object[]
			{
				outputFileName ,
				durationString( startTime )
			}
		);
								//	Delete temporary XML file.
								//	May not work on some systems,
								//	but the file will be deleted when
								//	MorphAdorner exits anyway.
		try
		{
			file.delete();
		}
		catch ( Exception e )
		{
		}
	}

	/**	Check if file is already adorned.
	 *
	 *	@param	xmlFileName			File to check for being adorned.
	 *	@param	maxLinesToCheck		Maximum # of lines to read looking
	 *								for a "<w" element.
	 */

	protected static boolean isAdorned
	(
		String xmlFileName ,
		int maxLinesToCheck
	)
	{
								//	Assume file is not adorned.

		boolean result	= false;

								//	Open file.

		try
		{
			BufferedReader bufferedReader	=
				new BufferedReader
				(
					new UnicodeReader
					(
						new FileInputStream( xmlFileName ) ,
						"utf-8"
					)
				);

								//	Lines read so far.

			int linesRead	= 0;

								//	Loop until we find a word element
								//	or we exceed the number of lines
								//	to check.

			String line	= bufferedReader.readLine();

			while
			(
				( line != null ) &&
				!result &&
				( linesRead < maxLinesToCheck )
			)
			{
				linesRead++;
								//	Does input line contain <w> tag?

				int wPos	= line.indexOf( "<w " );

								//	If line contains <w> tag ...

				if ( wPos >= 0 )
				{
								//	Split <w> text into attributes
								//	and word text.

					String[] groupValues	=
						WordAttributePatterns.wReplacer.matchGroups( line );

					try
					{
								//	Extract word ID.

						String[] idValues		=
							WordAttributePatterns.idReplacer.matchGroups(
								groupValues[ WordAttributePatterns.ATTRS ] );

						if ( idValues != null )
						{
							String id	=
								idValues[ WordAttributePatterns.IDVALUE ];

							if ( ( id != null ) && ( id.length() > 0 ) )
							{

								//	Found legitimate word ID.
								//	File has at least one adorned word,
								//	so assume it is adorned.

								result	= true;
								break;
							}
						}
					}
					catch ( Exception e )
					{
					}
				}
								//	Read next input line.

				line	= bufferedReader.readLine();
			}
								//	Close input file.

			bufferedReader.close();
		}
        catch ( Exception e )
        {
        }

		return result;
	}

	/**	Load abbreviations.
	 *
	 *	@param	abbreviationsURL	Abbreviations URL.
	 *	@param	loadedMessage		Message to display when words loaded.
	 */

	public static void loadAbbreviations
	(
		String abbreviationsURL ,
		String loadedMessage
	)
	{
		long startTime		= System.currentTimeMillis();

		int currentCount	= Abbreviations.getAbbreviationsCount();

								//	Load abbreviations.

		Abbreviations.loadAbbreviations( abbreviationsURL );

								//	Report number added.
		int added			=
			Abbreviations.getAbbreviationsCount() - currentCount;

		MorphAdornerLogger.println
		(
			loadedMessage ,
			new Object[]
			{
				Formatters.formatIntegerWithCommas( added ) ,
				durationString( startTime )
			}
		);
	}

	/**	Get word list.
	 *
	 *	@param		wordFileName	File name of word list.
	 *	@param		posTag			Part of speech tag for each word.
	 *	@param		loadedMessage	Message to display when words loaded.
	 *
	 *	@return		Tagged strings with words.
	 */

	public static TaggedStrings getWordList
	(
		String wordFileName ,
		String posTag ,
		String loadedMessage
	)
	{
		long startTime		= System.currentTimeMillis();

								//	Load words.

		TextFile wordFile	=
			new TextFile
			(
				MorphAdorner.class.getResourceAsStream
				(
					wordFileName
				) ,
				"utf-8"
			);

		SingleTagTaggedStrings words	=
			new SingleTagTaggedStrings( wordFile.toArray() , posTag );

		wordFile	= null;

		MorphAdornerLogger.println
		(
			loadedMessage ,
			new Object[]
			{
				Formatters.formatIntegerWithCommas
				(
					words.getStringCount()
				) ,
				durationString( startTime )
			}
		);

		return words;
	}

	/**	Get extra words list.
	 *
	 *	@param		wordFileName	File name of word list.
	 *	@param		posTag			Part of speech tag for each word.
	 *	@param		loadedMessage	Message to display when words loaded.
	 *
	 *	@return		Tagged strings with words.
	 */

	public static TaggedStrings getExtraWordsList
	(
		String wordFileName ,
		String posTag ,
		String loadedMessage
	)
	{
		long startTime			= System.currentTimeMillis();

		UTF8Properties words	= null;

								//	Load extra words.
		try
		{
			words	= new UTF8Properties();

			words.load
			(
				MorphAdorner.class.getResourceAsStream
				(
					wordFileName
				) ,
				posTag
			);
		}
		catch ( Exception e )
		{
		}

		if ( words.size() > 0 )
		{
			MorphAdornerLogger.println
			(
				loadedMessage ,
				new Object[]
				{
					Formatters.formatIntegerWithCommas
					(
						words.getStringCount()
					) ,
					durationString( startTime )
				}
			);
		}

		return words;
	}

	/**	Generate a KWIC line for a word in a sentence.
	 *
	 *	@param	sentence	The sentence as an array list.
	 *	@param	wordIndex	The index of the word for which to generate
	 *						a KWIC.
	 *	@param	KWICWidth	Maximum width (in characters) of KWIC text.
	 *
	 *	@return				The KWIC sections as a String array.
	 *						[0]	= left KWIC text
	 *						[1]	= word
	 *						[2]	= right KWIC text
	 */

	public static String[] getKWIC
	(
		List<AdornedWord> sentence ,
		int wordIndex ,
		int KWICWidth
	)
	{
		String[] results		= new String[ 3 ];

		StringBuffer KWICBuffer	= new StringBuffer();

		AdornedWord KWICWord	= sentence.get( wordIndex );

		int l					= 0;

		int maxWidth			=
			( KWICWidth - 4 - KWICWord.getToken().length() ) / 2;

		int i					= wordIndex - 1;

		while ( ( l < maxWidth ) && ( i >= 0 ) )
		{
			AdornedWord adornedWord	= sentence.get( i );

			if ( KWICBuffer.length() > 0 )
			{
				KWICBuffer.insert( 0 , " " );
			}

			KWICBuffer.insert( 0 , adornedWord.getToken() );

			l	+= adornedWord.getToken().length() + 1;

			i--;
		}

		results[ 0 ]	= KWICBuffer.toString();
		results[ 1 ]	= KWICWord.getToken();

		KWICBuffer.setLength( 0 );

		i				= wordIndex + 1;
		int nWords		= sentence.size();

		while ( ( KWICBuffer.length() < maxWidth ) && ( i < nWords ) )
		{
			AdornedWord adornedWord	= sentence.get( i );

			KWICBuffer.append( adornedWord.getToken() );
			KWICBuffer.append( " " );

			i++;
		}

		results[ 2 ]	= KWICBuffer.toString();

		return results;
	}

	/**	Get count of words in a list of sentences.
	 *
	 *	@param	sentences	List of sentences each containing list of words.
	 *
	 *	@return				Total number of words in sentences.
	 */

	protected static int getWordCount( List sentences )
	{
		int result	= 0;

		for ( int i = 0 ; i < sentences.size() ; i++ )
		{
			result	+=	((List)sentences.get( i )).size();
		}

		return result;
	}

	/**	Get actual word and sentence count.
	 *
	 *	@param	sentences	List of sentences each containing list of words.
	 *
	 *	@return				Two word int array of sentence and word counts.
	 *							[0]	= # of sentences
	 *							[1]	= # of words
	 *	<p>
	 *	Sentences and words containing only the special separator marker
	 *	character are not counted.
	 *	</p>
	 */

	protected static int[] getWordAndSentenceCounts( List sentences )
	{
		int result[]	= new int[ 2 ];
		result[ 0 ]	= 0;
		result[ 1 ]	= 0;

		for ( int i = 0 ; i < sentences.size() ; i++ )
		{
			List sentence	= (List)sentences.get( i );

			int wordCount	= sentence.size();
			boolean done	= false;

			while ( !done )
			{
				String word	= (String)sentence.get( wordCount - 1 );

				if (	word.equals(
					CharUtils. CHAR_END_OF_TEXT_SECTION_STRING ) )
				{
					wordCount--;
				}
				else
				{
					break;
				}

				done	= ( wordCount < 1 );
			}

			if ( wordCount > 0 )
			{
				result[ 0 ]++;
			}

			result[ 1 ]	+=	wordCount;
		}

		return result;
	}


	/**	Get duration value for display.
	 *
	 *	@param	startTime	Start time.
	 *
	 *	@return				Duration value for display.
	 */

	public static String durationString( long startTime )
	{
		StringBuffer result	= new StringBuffer();

		long duration			=
			( System.currentTimeMillis() - startTime + 999 ) / 1000;

		String durationString	=
			Formatters.formatLongWithCommas( duration );

		if ( duration < 1 )
		{
			durationString	= "< 1";
		}

		String secondString	= ( duration > 1 ) ? "seconds" : "second";

		result.append( durationString );
		result.append( " " );
		result.append( MorphAdornerSettings.getString( secondString ) );
		result.append( "." );

		return result.toString();
	}

	/**	Merge xml fragments into one xml file.
	 */

	protected static void mergeXML
	(
		TextInputter inputter ,
		String xmlFileName
	)
	{
//		logMemoryUsage( "mergeXML: entered: " );

		try
		{
								//	Open output file.

			FileOutputStream outputStream		=
				new FileOutputStream( new File( xmlFileName ) , false );

			BufferedOutputStream bufferedStream	=
				new BufferedOutputStream( outputStream );

			OutputStreamWriter writer			=
				new OutputStreamWriter( bufferedStream , "utf-8" );

//			logMemoryUsage( "mergeXML: writer allocated: " );

								//	Get list of text entries sorted
								//	by entry name,

			SortedArrayList<String> entryNames	=
				new SortedArrayList<String>();

			int nEntries	= inputter.getSegmentCount();

			for ( int i = 0 ; i < nEntries ; i++ )
			{
				entryNames.add( inputter.getSegmentName( i ) );
			}

//			logMemoryUsage( "mergeXML: entryNames assigned: " );

								//	Write out entries in order.

			String endText	= "";
			String entryName;
			String entryText;

			for ( int i = 0 ; i < entryNames.size() ; i++ )
			{
				entryName	= entryNames.get( i ).toString();
				entryText	= inputter.getSegmentText( entryName );

								//	Split "head.xml" as needed.

				if ( entryName.equals( "head" ) )
				{
								//	Look for "</eebo" or "</TEI"
								//	and split off
								//	ending string at this point.
								//	Add it to the end of the output
								//	later.

					int iPos	=
						StringUtils.indexOfIgnoreCase(
							entryText , "</eebo" );

					if ( iPos < 0 )
					{
						iPos	= entryText.indexOf( "</TEI" );
					}

					if ( iPos >= 0 )
					{
						endText		= entryText.substring( iPos );
						entryText	= entryText.substring( 0 , iPos );
					}
				}
								//	Split "text.xml" as needed.
								//	First part output now, rest
								//	prepended to ending string and
								//	added to the end of the output later.

				else if ( entryName.equals( "text" ) )
				{
					entryText	= entryText.trim();

					entryText	=
						StringUtils.replaceAll( entryText , "/>" , ">" );

					if ( entryText.startsWith( "<group" ) )
					{
						endText	= "</group>" + endText;
					}
					else if ( entryText.startsWith( "<GROUP" ) )
					{
						endText	= "</GROUP>" + endText;
					}
					else if ( entryText.startsWith( "<text" ) )
					{
						endText	= "</text>" + endText;
					}
					else
					{
						endText	= "</TEXT>" + endText;
					}

					if	(	entryText.endsWith( "</text>" ) ||
							entryText.endsWith( "</TEXT>" )
						)
					{
						entryText	=
							entryText.substring
							(
								0 ,
								entryText.length() - 7
							);
					}
				}
								//	Change trailing " >" to ">".

				while ( entryText.endsWith( " >" ) )
				{
					entryText	=
						entryText.substring(
							0 , entryText.length() - 2 ) + ">";
				}

				writer.write(
					entryText , 0 , entryText.length() );

//				logMemoryUsage( "mergeXML: after segment " + i + ": " );

			}
								//	Output accumulated end text.

			endText	= StringUtils.replaceAll( endText , " >" , ">" );

			writer.write( endText , 0 , endText.length() );

//			logMemoryUsage( "mergeXML: after end segment: " );

								//	Close XML output file.
			writer.close();
			bufferedStream.close();
			outputStream.close();

			writer			= null;
			bufferedStream	= null;
			outputStream	= null;
			entryNames		= null;

			endText			= null;
			entryName		= null;
			entryText		= null;
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}

		System.gc();
	}

	/**	Fix the spelling.
	 *
	 *	@param	spelling	Original spelling.
	 *
	 *	@return				The fixed spelling.
	 */

	public static String fixSpelling( String spelling )
	{
								//	Remove vertical bars.

		String result	= spelling;

		if ( !result.equals( "|" ) )
		{
			result	= StringUtils.replaceAll( result , "|" , "" );
		}
								//	Remove braces.

		if ( !result.equals( "{" ) )
		{
			result	= StringUtils.replaceAll( result , "{" , "" );
		}

		if ( !result.equals( "}" ) )
		{
			result	= StringUtils.replaceAll( result , "}" , "" );
		}

		if ( !result.equals( "+" ) )
		{
			result	= StringUtils.replaceAll( result , "+" , "" );
		}
								//	Replace _CapCap at start of word
								//	by Capcap.

		if ( ( result.length() > 1 ) && ( result.charAt( 0 ) == '_' ) )
		{
			underlineCapCapMatcher.reset( result );

			if ( underlineCapCapMatcher.find() )
			{
				String char1	= result.charAt( 1 ) + "";

				String char2	=
					Character.toLowerCase( result.charAt( 2 ) ) + "";

				String rest		= "";

				if ( result.length() > 3 )
				{
					rest	= result.substring( 3 );
				}

				result	= char1 + char2 + rest;
			}
		}

		return result;
	}

	/**	Get lemma (possibly compound) for a spelling.
	 *
	 *	@param	lemmatizer		The lemmatizer.
	 *	@param	spelling			The spelling.
	 *	@param	partOfSpeech	The part of speech tag.
	 *
	 *	@return						Lemma for spelling.  May contain
	 *								compound spelling in form
	 *								"lemma1:lemma2:...".
	 */

	protected static String getLemma
	(
		Lemmatizer lemmatizer ,
		String spelling ,
		String partOfSpeech
	)
	{
		String result	= spelling;

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
			if ( partOfSpeechTags.isNumberTag( partOfSpeech ) )
			{
				if ( RomanNumeralUtils.isLooseRomanNumeral( result ) )
				{
					if ( result.charAt( 0 ) == '.' )
					{
						result	= result.substring( 1 );
					}

					if ( result.charAt( result.length() - 1 ) == '.' )
					{
						result	=
							result.substring( 0 , result.length() - 1 );
					}
				}
			}
		}
		else
		{
								//	If compound part of speech tag,
								//	see if word appears in list of
								//	known irregular lemmata.

			boolean isCompoundTag	=
				partOfSpeechTags.isCompoundTag( partOfSpeech );

			if ( isCompoundTag )
			{
				result	= lemmatizer.lemmatize( spelling , "compound" );

				if ( lemmatizer.isCompoundLemma( result ) )
				{
					return result;
				}
			}
								//	Extract individual word parts.
								//	May be more than one for a
								//	contraction.

			List wordList	=
				spellingTokenizer.extractWords( spelling );

								//	If just one word part,
								//	get its lemma.

			if ( !isCompoundTag || ( wordList.size() == 1 ) )
			{
				if ( lemmaClass.length() == 0 )
				{
					result	=
						lemmatizer.lemmatize( spelling , "compound" );

					if ( result.equals( spelling ) )
					{
						result	= lemmatizer.lemmatize( spelling );
					}
				}
				else
				{
					result	=
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
				result				= "";
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
							result	= result + lemmaSeparator;
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

						result	= result + lemmaPiece;
					}
				}
			}
		}

		return result;
	}

	/**	Get standardized spelling.
	 *
	 *	@param	standardizer			The spelling standardizer.
	 *	@param	nameStandardizer		The name standardizer.
	 *	@param	correctedSpelling		The spelling.
	 *	@param	standardizedSpelling	The initial standardized spelling.
	 *	@param	partOfSpeech			The part of speech tag.
	 *
	 *	@return							Standardized spelling.
	 */

	protected static String getStandardizedSpelling
	(
		SpellingStandardizer standardizer ,
		NameStandardizer nameStandardizer ,
		String correctedSpelling ,
		String standardizedSpelling ,
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
			if ( RomanNumeralUtils.isLooseRomanNumeral( result ) )
			{
				if ( result.charAt( 0 ) == '.' )
				{
					result	= result.substring( 1 );
				}

				if ( result.charAt( result.length() - 1 ) == '.' )
				{
					result	=
						result.substring( 0 , result.length() - 1 );
				}
			}
		}
		else
		{
			result	=
				standardizer.standardizeSpelling
				(
					spelling ,
					partOfSpeechTags.getMajorWordClass
					(
						partOfSpeech
					)
				);

			if ( result.equalsIgnoreCase( spelling ) )
			{
				result	= spelling;
			}
		}

		return result;
	}

	/**	Fix empty soft tags.
	 *
	 *	@param	document	The DOM document.
	 *
	 *	<p>
	 *	On exit, the DOM document has empty soft tags
	 *	expanded with a single blank as text.
	 *	</p>
	 */

	public static void fixEmptySoftTags( Document document )
	{
		List<Node> nodes	= DOMUtils.getDescendants( document );

		for ( Node node : nodes )
		{
			if ( MorphAdornerSettings.xgOptions.isSoftTag( node.getNodeName() ) )
			{
				String text	= DOMUtils.getText( node );

				if ( text.length() == 0 )
				{
					DOMUtils.setText( node , " " );
				}
			}
		}
	}

	/**	Fix sup tags.
	 *
	 *	@param	document	The DOM document.
	 *
	 *	<p>
	 *	Prepends a special marker character to the start of the text enclosed
	 *	in <sup> tags to allow disambiguation of old printer's marks from
	 *	other types of abbreviations.  The special marker character is
	 *	removed before the adorned XML text is written out.
	 *	</p>
	 *
	 *	<sup>E</sup>		the
	 *	y<sup>T</sup>		that
	 *	y<sup>c</sup>		the
	 *	y<sup>e</sup>		the
	 *	y<sup>en</sup>		then
	 *	y<sup>ere</sup>		there
	 *	y<sup>f</sup>		if
	 *	y<sup>i</sup>		thy
	 *	y<sup>m</sup>		them
	 *	y<sup>n</sup>		than
	 *	y<sup>o</sup>		the
	 *	y<sup>t</sup>		that
	 *	y<sup>u</sup>		thou
	 *	y<sup></sup>		that
	 *
	 *	w<sup>ch</sup>		which
	 *	w<sup>t</sup>		with
	 */

	public static void fixSupTags( Document document )
	{
								//	Extract sup tags.

		List<Node> supNodes	=
			DOMUtils.getDescendants
			(
				document ,
				"sup"
			);
								//	Look at each sup tag.

		for ( Node supNode : supNodes )
		{
								//	Get text of this sup tag.

			String supText	= DOMUtils.getText( supNode );

			if ( supText.startsWith( "^" ) ) continue;

								//	Get previous sibling of sup tag.

			Node sibling	= supNode.getPreviousSibling();

			if ( sibling != null )
			{
								//	Get text of sibling.

				String siblingText	= sibling.getTextContent();

								//	If last characters of sibling text
								//	plus characters of sup text are a
								//	printer's mark, add "^" to sup text
								//	to indicate this.

				if ( siblingText.endsWith( " y" ) )
				{
					String loSupText	= supText.toLowerCase();

					if	(	loSupText.equals( "e" ) ||
							loSupText.equals( "t" ) ||
							loSupText.equals( "c" ) ||
							loSupText.equals( "en" ) ||
							loSupText.equals( "ere" ) ||
							loSupText.equals( "f" ) ||
							loSupText.equals( "i" ) ||
							loSupText.equals( "m" ) ||
							loSupText.equals( "n" ) ||
							loSupText.equals( "o" ) ||
							loSupText.equals( "u" )
						)
					{
						supText	=
							CharUtils.CHAR_SUP_TEXT_MARKER_STRING +
								supText;

						DOMUtils.setText( supNode , supText );
					}
				}
				else if ( siblingText.endsWith( " w" ) )
				{
					String loSupText	= supText.toLowerCase();

					if	(	loSupText.equals( "ch" ) ||
							loSupText.equals( "t" )
						)
					{
						supText	=
							CharUtils.CHAR_SUP_TEXT_MARKER_STRING +
								supText;

						DOMUtils.setText( supNode , supText );
					}
				}
			}
		}
	}

	/**	Count page breaks in document.
	 *
	 *	@param	document	The DOM document.
	 *
	 *	@return				The number of page break <pb> elements.
	 */

	public static int countPageBreaks( Document document )
	{
		List<Node> pbNodes	=
			DOMUtils.getDescendants
			(
				document ,
				"pb"
			);

		return pbNodes.size();
	}

	/**	Create and run a Morphological Adorner.
	 */

	public static void main( String[] args )
	{
								//	Skip initialization if
								//	already done.

		if ( !MorphAdornerSettings.initialized )
		{
								//	Initialize program settings.

			MorphAdornerSettings.initializeSettings( args );

								//	Initialize logging.
			try
			{
				MorphAdornerLogger.initialize
				(
					"morphadornerlog.config" ,
					"/morphadorner/log"
				);
			}
			catch ( Exception e )
			{
				e.printStackTrace();
			}
                    			//	Get program settings.

			try
			{
				MorphAdornerSettings.getSettings( args );
			}
			catch ( Exception e )
			{
				e.printStackTrace();
			}
								//	Say adorner is starting.

			MorphAdornerLogger.println( "programBanner" );
			MorphAdornerLogger.println( "Initializing_please_wait" );

								//	Initialize adornment classes.

			initializeAdornment();

								//	Remember initialization complete
								//	for subsequent entries.

			MorphAdornerSettings.initialized	= true;
		}
                    			//	Run adornment processes on specified
                    			//	input files.

		if ( MorphAdornerSettings.fileNames.length > 0 )
		{
			processInputFiles();
		}
		else
		{
			MorphAdornerLogger.println( "No_files_found_to_process" );
		}
								//	Close down logging.

		MorphAdornerLogger.terminate();
	}

	/**	Allow overrides but not instantiation.
	 */

	protected MorphAdorner()
	{
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



