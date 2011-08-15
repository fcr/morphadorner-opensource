package edu.northwestern.at.morphadorner.tools.mergebrilllexicon;

/*	Please see the license information at the end of this file. */

import java.net.*;
import java.io.*;
import java.text.*;
import java.util.*;

import edu.northwestern.at.utils.corpuslinguistics.lemmatizer.*;
import edu.northwestern.at.utils.corpuslinguistics.lexicon.*;
import edu.northwestern.at.utils.corpuslinguistics.partsofspeech.*;
import edu.northwestern.at.utils.corpuslinguistics.tokenizer.*;
import edu.northwestern.at.utils.*;

/**	Merge Brill format lexicon into MorphAdorner format lexicon.
 */

public class MergeBrillLexicon
{
	/**	Spelling tokenizer. */

	protected static WordTokenizer spellingTokenizer	=
		new PennTreebankTokenizer();

	/**	Lemmatizer. */

	protected static Lemmatizer lemmatizer;

	/**	Part of speech tags. */

	protected static PartOfSpeechTags partOfSpeechTags;

	/**	Lemma separator character. */

	protected static String lemmaSeparator	= "|";

	/**	Merge Brill lexicon.
	 *
	 *	@param	lexiconFileName			MorphAdorner lexicon file name.
	 *	@param	brillLexiconFileName	Brill format lexicon file name.
	 *	@param	mergedLexiconFileName	Merged lexicon file name.
	 */

	public static void mergeBrillLexicon
	(
		String lexiconFileName ,
		String brillLexiconFileName ,
		String mergedLexiconFileName
	)
		throws IOException
	{
								//	Load the MorphAdorner format lexicon.

		Lexicon lexicon	= new BaseLexicon();

		lexicon.loadLexicon
		(
			(new File( lexiconFileName )).toURI().toURL() ,
			"utf-8"
		);
								//	Load parts of speech.

		partOfSpeechTags	= new NUPOSPartOfSpeechTags();

								//	Create lemmatizer.
		try
		{
			lemmatizer	= new DefaultLemmatizer();
        }
        catch ( Exception e )
        {
        	System.out.println( "Unable to create lemmatizer." );
        	System.exit( 1 );
        }

		System.out.println
		(
			"MorphAdorner lexicon has " +
			Formatters.formatIntegerWithCommas( lexicon.getLexiconSize() ) +
			" entries."
		);
								//	Load Brill format lexicon.

		BrillLexicon brillLexicon	=
			new BrillLexicon
			(
				new File( brillLexiconFileName ).toURI().toURL() ,
				"utf-8"
			);

		System.out.println
		(
			"Brill lexicon has " +
			Formatters.formatIntegerWithCommas( brillLexicon.size() ) +
			" entries."
		);
								//	Loop over entries in the Brill
								//	lexicon.

		Iterator<String> iterator	= brillLexicon.keySet().iterator();

		while ( iterator.hasNext() )
		{
								//	Get next word in Brill lexicon.

			String word		= iterator.next();

								//	For each entry in the Brill
								//	lexicon that is not in the
								//	MorphAdorner lexicon, create
								//	an entry in the MorphAdorner
								//	lexicon.  We won't have counts,
								//	so assume first category has
								//	count 2 and others have count 1.

			int firstFreq	= 2;
			int otherFreq	= 1;
			String lemma	= word;

			if ( !lexicon.containsEntry( word ) )
			{
				List<String> posTags	= brillLexicon.get( word );

								//	If there is only one Brill tag,
								//	and it is a proper noun tag,
								//	look to see if there are existing
								//	parts of speech for a lower case
								//	version of the word.  If so, create
								//	a lexicon entry with those first .

				String posTag	= posTags.get( 0 );

				if	(	( posTags.size() == 1 ) &&
				        ( partOfSpeechTags.isProperNounTag( posTag ) )
					)
				{
					String lowerCaseWord	= word.toLowerCase();

					if ( lexicon.containsEntry( lowerCaseWord ) )
					{
						LexiconEntry lexEntry	=
							lexicon.getLexiconEntry( lowerCaseWord ).deepClone();

						lexEntry.entry	= word;

						lexicon.setLexiconEntry( word , lexEntry );
					}

					firstFreq	= 1;
				}
								//	Now add the word and the parts of
								//	speech from the Brill lexicon to
								//	the MorphAdorner lexicon.

				for ( int i = 0 ; i < posTags.size() ; i++ )
				{
					lemma	= getLemma( word , posTags.get( i ) );

					lexicon.updateEntryCount
					(
						word ,
						posTags.get( i ) ,
						lemma ,
						( i == 0 ) ? firstFreq : otherFreq
					);
				}
			}
		}
								//	Saved merged lexicon.

		lexicon.saveLexiconToTextFile( mergedLexiconFileName , "utf-8" );

		System.out.println
		(
			"Merged lexicon has " +
			Formatters.formatIntegerWithCommas( lexicon.getLexiconSize() ) +
			" entries."
		);
	}

	/**	Get lemma for a word.
	 *
	 *	@param	spelling		The word spelling.
	 *	@param	partOfSpeech	The part of speech tag.
	 *
	 *	@return					The lemma.
	 */

	public static String getLemma
	(
		String spelling ,
		String partOfSpeech
	)
	{
		String lemmata	= spelling;

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
								//	Try compound word exceptions
								//	list first.

			lemmata	= lemmatizer.lemmatize( spelling , "compound" );

								//	If lemma not found, keep trying.

			if ( lemmata.equals( spelling ) )
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
						lemmata	= lemmatizer.lemmatize( spelling );
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

		return lemmata;
	}

	/**	Main program.
	 */

	public static void main( String[] args )
	{
		try
		{
			mergeBrillLexicon( args[ 0 ] , args[ 1 ] , args[ 2 ] );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}

	/**	Allow overrides but not instantiation.
	 */

	protected MergeBrillLexicon()
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




