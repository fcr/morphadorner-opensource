package edu.northwestern.at.utils.corpuslinguistics.postagger;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.affix.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.bigram.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.bigramhybrid.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.hepple.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.regexp.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.simple.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.suffix.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.trigram.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.trigramhybrid.*;
import edu.northwestern.at.utils.corpuslinguistics.postagger.unigram.*;

/**	PartOfSpeechTagger factory.
 */

public class PartOfSpeechTaggerFactory
{
	/**	Map from short to full class names for built-in taggers. */

	protected static Map<String, String> taggerClassMap	=
		MapFactory.createNewMap();

	/**	Get a partOfSpeechTagger.
	 *
	 *	@return		The partOfSpeechTagger.
	 */

	public PartOfSpeechTagger newPartOfSpeechTagger()
	{
		String className	=
			System.getProperty( "partofspeechtagger.class" );

		if ( className == null )
		{
			className	=
				ClassUtils.packageName(
					getClass().getName() ) + ".trigramtagger.TrigramTagger";
		}

		return this.newPartOfSpeechTagger( className );
	}

	/**	Get a partOfSpeechTagger of a specified class name.
	 *
	 *	@param	className	Class name for the partOfSpeechTagger.
	 *
	 *	@return				The partOfSpeechTagger.
	 */

	public PartOfSpeechTagger newPartOfSpeechTagger( String className )
	{
		PartOfSpeechTagger partOfSpeechTagger	= null;

		try
		{
			partOfSpeechTagger	=
				(PartOfSpeechTagger)Class.forName(
					className ).newInstance();
		}
		catch ( Exception e )
		{
			String fixedClassName	=
				(String)taggerClassMap.get( className );

			if ( fixedClassName != null )
			{
				try
				{
					partOfSpeechTagger	=
						(PartOfSpeechTagger)Class.forName(
							fixedClassName ).newInstance();
				}
				catch ( Exception e2 )
				{
					System.err.println(
						"Unable to create part of speech tagger of class " +
						fixedClassName + ", using trigram tagger." );

					partOfSpeechTagger	= new TrigramTagger();
				}
			}
			else
			{
				System.err.println(
					"Unable to create part of speech tagger of class " +
					className + ", using trigram tagger." );

				partOfSpeechTagger	= new TrigramTagger();
			}
		}

		return partOfSpeechTagger;
	}

	/**	Create short tagger class name -> full class names.
	 */

	static
	{
		String classPrefix	=
			ClassUtils.packageName(
				PartOfSpeechTaggerFactory.class.getName() );

		taggerClassMap.put
		(
			"AffixTagger" ,
			classPrefix + ".affix.AffixTagger"
		);

		taggerClassMap.put
		(
			"BigramTagger" ,
			classPrefix + ".bigram.BigramTagger"
		);

		taggerClassMap.put
		(
			"BigramHybridTagger" ,
			classPrefix + ".bigramhybrid.BigramHybridTagger"
		);

		taggerClassMap.put
		(
			"HeppleTagger" ,
			classPrefix + ".hepple.HeppleTagger"
		);

		taggerClassMap.put
		(
			"RegexpTagger" ,
			classPrefix + ".regexp.RegexpTagger"
		);

		taggerClassMap.put
		(
			"SimpleTagger" ,
			classPrefix + ".simple.SimpleTagger"
		);

		taggerClassMap.put
		(
			"SimpleRuleBasedTagger" ,
			classPrefix + ".simplerulebased.SimpleRuleBasedTagger"
		);

		taggerClassMap.put
		(
			"SuffixTagger" ,
			classPrefix + ".suffix.SuffixTagger"
		);

		taggerClassMap.put
		(
			"TrigramTagger" ,
			classPrefix + ".trigram.TrigramTagger"
		);

		taggerClassMap.put
		(
			"TrigramHybridTagger" ,
			classPrefix + ".trigramhybrid.TrigramHybridTagger"
		);

		taggerClassMap.put
		(
			"UnigramTagger" ,
			classPrefix + ".unigram.UnigramTagger"
		);
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



