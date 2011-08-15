package edu.northwestern.at.morphadorner.servlets;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.html.*;
import edu.northwestern.at.utils.corpuslinguistics.languagerecognizer.*;
import edu.northwestern.at.utils.corpuslinguistics.lemmatizer.*;
import edu.northwestern.at.utils.corpuslinguistics.lexicon.*;
import edu.northwestern.at.utils.corpuslinguistics.namerecognizer.*;
import edu.northwestern.at.utils.corpuslinguistics.partsofspeech.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.guesser.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.propernounretagger.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.transitionmatrix.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.trigram.*;
import edu.northwestern.at.utils.corpuslinguistics.sentencesplitter.*;
import edu.northwestern.at.utils.corpuslinguistics.spellingstandardizer.*;
import edu.northwestern.at.utils.corpuslinguistics.stemmer.*;
import edu.northwestern.at.utils.corpuslinguistics.tokenizer.*;

import edu.northwestern.at.utils.servlets.*;

/**	Adorner information for MorphAdorner example servlets.
 *
 *	<p>
 *	All the fields are public.
 *	</p>
 */

public class AdornerInfo
{
	/**	The sentence splitter. */

	public SentenceSplitter extractor;

	/**	The part of speech guesser. */

	public PartOfSpeechGuesser partOfSpeechGuesser;

	/**	The word lexicon. */

	public Lexicon wordLexicon;

	/**	Maps lemma to list of spellings in lexicon. */

	public KeyedSets<String, String> lemmaToSpellings;

	/**	The suffix lexicon. */

	public Lexicon suffixLexicon;

	/**	Simple spelling standardizer. */

	public ExtendedSimpleSpellingStandardizer simpleStandardizer;

	/**	Extended search spelling standardizer. */

	public ExtendedSearchSpellingStandardizer standardizer;

	/**	Part of speech tags. */

	public PartOfSpeechTags partOfSpeechTags;

	/**	The part of speech tagger. */

	public PartOfSpeechTagger tagger;

	/**	The part of speech retagger. */

	public PartOfSpeechRetagger retagger;

	/**	Transition matrix. */

	public TransitionMatrix transitionMatrix;

	/**	The name recognizer. */

	public NameRecognizer nameRecognizer;

	/**	Create adorner info object.
	 *
	 *	@param	wordLexiconFileName			Word lexicon file name.
	 *	@param	suffixLexiconFileName		Suffix lexicon file name.
	 *	@param	transitionMatrixFileName	Part of speech transition matrix
	 *										file name.
	 *	@param	standardSpellingsFileName	Standard spellings file name.
	 *	@param	extraWordLists				TaggedStrings array of extra
	 *										word lists.
	 *	@param	names						Names list.
	 */

	public AdornerInfo
	(
		String wordLexiconFileName ,
		String suffixLexiconFileName ,
		String transitionMatrixFileName ,
		String standardSpellingsFileName ,
		String alternateSpellingsFileName ,
		TaggedStrings[] extraWordLists ,
		Names names
	)
		throws Exception
	{
								//	Get word lexicon.

		wordLexicon	= new DefaultLexicon();

								//	Load word lexicon.

		wordLexicon.loadLexicon
		(
			new File( wordLexiconFileName ).toURI().toURL() ,
			"utf-8"
		);
								//	Get suffix lexicon.

		suffixLexicon	= new DefaultSuffixLexicon();

								//	Load suffix lexicon.

		suffixLexicon.loadLexicon
		(
			new File( suffixLexiconFileName ).toURI().toURL() ,
			"utf-8"
		);
								//	Get part of speech tags.

		partOfSpeechTags	= wordLexicon.getPartOfSpeechTags();

								//	Get part of speech guessers.

		partOfSpeechGuesser	= new DefaultPartOfSpeechGuesser();

		partOfSpeechGuesser.setWordLexicon( wordLexicon );
		partOfSpeechGuesser.setSuffixLexicon( suffixLexicon );

								//	Get sentence splitter.

		extractor	= new DefaultSentenceSplitter();

								//	Set guesser into sentence splitter.

		extractor.setPartOfSpeechGuesser( partOfSpeechGuesser );

								//	Create trigram part of speech tagger.

		tagger		= new TrigramTagger();

								//	Add proper noun retagger.

		retagger	= new ProperNounRetagger();

		tagger.setRetagger( retagger );

								//	Add auxiliary word lists to guesser.

		for ( int i = 0 ; i < extraWordLists.length ; i++ )
		{
			partOfSpeechGuesser.addAuxiliaryWordList( extraWordLists[ i ] );
		}
								//	Set tagger to use lexicon.

		tagger.setLexicon( wordLexicon );

								//	Set guesser into tagger.

		tagger.setPartOfSpeechGuesser( partOfSpeechGuesser );

								//	Load transition matrix.

		TransitionMatrix transitionMatrix	=
			new TransitionMatrix();

		transitionMatrix.loadTransitionMatrix
		(
			new File( transitionMatrixFileName ).toURI().toURL() ,
			"utf-8",
			'\t'
		);

		tagger.setTransitionMatrix( transitionMatrix );

								//	Get extended search standardizer.

		standardizer	= new ExtendedSearchSpellingStandardizer();

								//	Load standard spellings.

		standardizer.loadStandardSpellings
		(
			new File( standardSpellingsFileName ).toURI().toURL() ,
			"utf-8"
		);
                                //	Add name lists to standard spellings.

		standardizer.addStandardSpellings( names.getFirstNames() );

		standardizer.addStandardSpellings( names.getSurnames() );

		standardizer.addStandardSpellings( names.getPlaceNames().keySet() );

								//	Load alternate/standard spelling pairs.

		standardizer.loadAlternativeSpellings
		(
			new File( alternateSpellingsFileName ).toURI().toURL() ,
			"utf-8" ,
			"\t"
		);
        						//	Get simple spelling standardizer.

		simpleStandardizer	= new ExtendedSimpleSpellingStandardizer();

								//	Set pairs list into simple
								//	standardizer as well.

		simpleStandardizer.setMappedSpellings(
			standardizer.getMappedSpellings() );

		simpleStandardizer.setStandardSpellings(
			standardizer.getStandardSpellings() );

								//	Create name recognizer.

		nameRecognizer	= new DefaultNameRecognizer();

		nameRecognizer.setPartOfSpeechTagger( tagger );

								//	Map lemmata to spellings in
								//	word lexicon.

		String[] spellings	= wordLexicon.getEntries();

		lemmaToSpellings	= new KeyedSets<String, String>();

		for ( int i = 0 ; i < spellings.length ; i++ )
		{
			String spelling		= spellings[ i ];
			String[] lemmata	= wordLexicon.getLemmata( spelling );

			for ( int j = 0 ; j < lemmata.length ; j++ )
			{
				lemmaToSpellings.add( lemmata[ j ] , spelling );
			}
		}
	}
}

