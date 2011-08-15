package edu.northwestern.at.morphadorner.tools.mergeenhancedbrilllexicon;

/*	Please see the license information at the end of this file. */

import java.net.*;
import java.io.*;
import java.text.*;
import java.util.*;

import edu.northwestern.at.utils.corpuslinguistics.lexicon.*;
import edu.northwestern.at.utils.corpuslinguistics.partsofspeech.*;
import edu.northwestern.at.utils.*;

/**	Merge MorphAdorner lexicon with enhanced Brill format lexicon.
 */

public class MergeEnhancedBrillLexicon
{
	/**	Merge enhanced Brill lexicon.
	 *
	 *	@param	lexiconFileName			MorphAdorner lexicon file name.
	 *	@param	brillLexiconFileName	Enhanced Brill format lexicon file name.
	 *	@param	mergedLexiconFileName	Merged lexicon file name.
	 */

	public static void mergeEnhancedBrillLexicon
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

		PartOfSpeechTags partOfSpeechTags	= new NUPOSPartOfSpeechTags();

		System.out.println
		(
			"MorphAdorner lexicon has " +
			Formatters.formatIntegerWithCommas( lexicon.getLexiconSize() ) +
			" entries."
		);
								//	Load enhanced Brill format lexicon.

		BrillLexicon brillLexicon	=
			new BrillLexicon(
				new File( brillLexiconFileName ).toURI().toURL() , "utf-8" );

		System.out.println
		(
			"Enhanced Brill lexicon has " +
			Formatters.formatIntegerWithCommas( brillLexicon.size() ) +
			" entries."
		);
								//	Loop over entries in the enhanced Brill
								//	lexicon.

		Iterator iterator	= brillLexicon.keySet().iterator();

		while ( iterator.hasNext() )
		{
								//	Get next word in enhanced Brill lexicon.

			String word		= (String)iterator.next();

								//	For each entry in the enhanced Brill
								//	lexicon that is not in the
								//	MorphAdorner lexicon, create
								//	an entry in the MorphAdorner
								//	lexicon.  We won't have counts,
								//	so assume first category has
								//	count 2 and others have count 1.

			int firstFreq	= 2;
			int otherFreq	= 1;
			String lemma	= "*";

			if ( !lexicon.containsEntry( word ) )
			{
				List posTags	= (List)brillLexicon.get( word );

								//	If there is only one Brill tag,
								//	and it is a proper noun tag,
								//	look to see if there are existing
								//	parts of speech for a lower case
								//	version of the word.  If so, create
								//	a lexicon entry with those first .

				String posTag	= (String)posTags.get( 0 );
				lemma			= (String)posTags.get( 1 );

				if	(	( posTags.size() == 2 ) &&
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

				for ( int i = 0 ; i < posTags.size() ; i = i+2 )
				{
					lexicon.updateEntryCount
					(
						word ,
						(String)posTags.get( i ) ,
						(String)posTags.get( i + 1 ) ,
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

	/**	Main program.
	 */

	public static void main( String[] args )
	{
		try
		{
			mergeEnhancedBrillLexicon( args[ 0 ] , args[ 1 ] , args[ 2 ] );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}

	/**	Allow overrides but not instantiation.
	 */

	protected MergeEnhancedBrillLexicon()
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



