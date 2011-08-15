package edu.northwestern.at.utils.gate;

/*	Please see the license information at the end of this file. */

import gate.*;
import gate.creole.*;
import gate.util.*;
import gate.corpora.*;

import java.io.*;
import java.util.*;

import edu.northwestern.at.utils.*;

/**	Standalone Annie for named entity adornment.
 *
 *	<p>
 *	Adapted from the StandAloneAnnie sample program in the Gate
 *	distribution.
 *	</p>
 */

public class Annie
{
	/**	Annie controller. */

	protected SerialAnalyserController annieController;

	/**	Holds text to adorn with named entities. */

	protected Corpus corpus;

	/**	Set of named entity types wanted. */

	protected Set<String> entityTypesWanted;

	/**	Default resources path. */

	protected static String defaultAnnieResourcePath	= "data/gate";

	/**	Create Annie annotator using default resource path. */

	public Annie()
		throws Exception
	{
		initAnnie( defaultAnnieResourcePath );
	}

	/**	Create Annie annotator specifying resource path.
	 *
	 *	@param	annieResourcePath	The resource path.
	 */

	public Annie( String annieResourcePath )
		throws Exception
	{
		initAnnie( annieResourcePath );
	}

	/**	Initialize Annie.
	 *
	 *	@param	annieResourcePath	The resource path.
	 */

	public void initAnnie( String annieResourcePath )
		throws Exception
	{
								//	Initialize Gate.

		Gate.setGateHome( new File( annieResourcePath ) );

		Gate.setPluginsHome( new File( annieResourcePath ) );

		Gate.setUserConfigFile(
			new File( Gate.getGateHome() , "gate.xml" ) );

		Gate.init();
								//	Initialize Annie.

		Gate.getCreoleRegister().registerDirectories
		(
			new File( Gate.getGateHome() , "ANNIE" ).toURI().toURL()
		);

								//	Create serial analyser controller
								//	for running Annie.
		annieController =
			(SerialAnalyserController)Factory.createResource
		(
			"gate.creole.SerialAnalyserController",
			Factory.newFeatureMap(),
			Factory.newFeatureMap(),
			"ANNIE_" + Gate.genSym()
		);

								//	Load the Annie processing resources.

		for ( int i = 0 ; i < ANNIEConstants.PR_NAMES.length - 1 ; i++ )
		{
			FeatureMap params	= Factory.newFeatureMap();

			ProcessingResource processingResource	=
				(ProcessingResource)Factory.createResource
				(
					ANNIEConstants.PR_NAMES[ i ] , params
				);

			annieController.add( processingResource );
		}
								//	Create corpus to hold text/documents
								//	to adorn.

		corpus	=
			(Corpus)Factory.createResource( "gate.corpora.CorpusImpl" );

								//	Create set of named entity types
								//	with which to adorn text.

		entityTypesWanted = SetFactory.createNewSet();

		entityTypesWanted.add( "Person" );
		entityTypesWanted.add( "Location" );
		entityTypesWanted.add( "Time" );
		entityTypesWanted.add( "Organization" );
		entityTypesWanted.add( "Date" );
		entityTypesWanted.add( "Money" );
	}

	/**	Close down Annie. */

	public void close()
	{
		annieController.cleanup();
	}

	/**	Adorn text with named entities.
	 *
	 *	@param	text	Text to adorn with named entities.
	 *
	 *	@return			The text adter adornment.
	 *					May be empty if an error occurs.
	 */

	@SuppressWarnings("unchecked")
	public String adornText( String text )
	{
		String result	= "";

		try
		{
								//	Default document features are fine.

			FeatureMap params	= Factory.newFeatureMap();

								//	Create a document to hold the
								//	text to adorn.

			DocumentImpl document	=
				(DocumentImpl)Factory.createResource
				(
					"gate.corpora.DocumentImpl" ,
					params
				);
								//	Set document's text.

			document.setStringContent( text );
			document.init();

								//	Add the document to the corpus.
			corpus.clear();

			corpus.add( document );

								//	Set the corpus for processing
								//	by Annie.

			annieController.setCorpus( corpus );

								//	Tell Annie to process the corpus.

			annieController.execute();

								//	Get the set of adornments.

			AnnotationSet adornmentSet	=
				document.getAnnotations().get( entityTypesWanted );

								//	Convert the adorned document
								//	to XML format.

			result	= document.toXml( adornmentSet , false );
		}
		catch( Exception e )
		{
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



