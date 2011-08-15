package edu.northwestern.at.utils.corpuslinguistics.spellingstandardizer;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.net.*;
import java.rmi.*;
import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.logger.*;
import edu.northwestern.at.morphadorner.servers.standardizerserver.*;

/**	Remote Spelling Standardizer.
 *
 *	<p>
 *	This spelling standardizer uses RMI calls to a spelling
 *	standardizer to find standardized spellings.
 *	</p>
 */

public class RemoteSpellingStandardizer
	implements SpellingStandardizer, UsesLogger
{
	/**	The spelling standarizer server session, or null if none. */

	protected StandardizerServerSession session	= null;

	/**	Logger used for output. */

	protected Logger logger;

	/**	Create spelling standardizer that uses a remote standardizer server.
	 */

	public RemoteSpellingStandardizer()
	{
		initializeServerSession();

		logger	= new DummyLogger();
	}

	/**	Initializes the server session.
	 */

	protected void initializeServerSession()
	{
								//	Get server configuration.

		StandardizerServerSettings.initializeSettings();

								//	Get the bpootstrap object
								//	from the RMI server so we can
								//	start a spelling standardizer
								//	server session.
		try
		{
			StandardizerServerBootstrap bootstrap	=
				(StandardizerServerBootstrap)Naming.lookup
				(
//					StandardizerServerSettings.getRmiServerURL()
					"//localhost:1100/SpellingStandardizer"
				);

			session	= bootstrap.startSession();
		}
		catch ( Exception e )
		{
			// ignore failures to connect to server.
			e.printStackTrace();
		}
	}

	/**	Loads alternate spellings from a URL.
	 *
	 *	@param	url		URL containing alternate spellings to
	 *					standard spellings mappings.
	 *
	 *	<p>
	 *	Does nothing in this implementation.
	 *	</p>
	 */

	public void loadAlternativeSpellings
	(
		URL url ,
		String encoding ,
		String delimChars
	)
		throws IOException
	{
	}

	/**	Load alternate to standard spellings by word class.
	 *
	 *	@param	spellingsURL	URL of alternative spellings by word class.
	 *
	 *	<p>
	 *	Does nothing in this implementation.
	 *	</p>
	 */

	public void loadAlternativeSpellingsByWordClass
	(
		URL spellingsURL ,
	 	String encoding
	)
		throws IOException
	{
	}

	/**	Loads alternative spellings from a reader.
	 *
	 *	@param	reader		The reader.
	 *	@param	delimChars	Delimiter characters separating spelling pairs.
	 *
	 *	<p>
	 *	Does nothing in this implementation.
	 *	</p>
	 */

	public void loadAlternativeSpellings
	(
		Reader reader ,
		String delimChars
	)
		throws IOException
	{
	}

	/**	Loads standard spellings from a URL.
	 *
	 *	@param	url			URL containing standard spellings
	 *	@param	encoding	Character set encoding for spellings
	 */

	public void loadStandardSpellings
	(
		URL url ,
		String encoding
	)
		throws IOException
	{
	}

	/**	Loads standard spellings from a reader.
	 *
	 *	@param	reader		The reader.
	 */

	public void loadStandardSpellings
	(
		Reader reader
	)
		throws IOException
	{
	}

	/**	Add a mapped spelling.
	 *
	 *	@param	alternateSpelling	The alternate spelling.
	 *	@param	standardSpelling	The corresponding standard spelling.
	 */

	public void addMappedSpelling
	(
		String alternateSpelling ,
		String standardSpelling
	)
	{
	}

	/**	Add a standard spelling.
	 *
	 *	@param	standardSpelling	A standard spelling.
	 */

	public void addStandardSpelling
	(
		String standardSpelling
	)
	{
	}

	/**	Add standard spellings from a collection.
	 *
	 *	@param	standardSpellings	A collection of standard spellings.
	 *
	 *	<p>
	 *	Does nothing in this implementation.
	 *	</p>
	 */

	public void addStandardSpellings
	(
		Collection standardSpellings
	)
	{
	}

	/**	Sets map which maps alternate spellings to standard spellings.
	 *
	 *	@param	standardMappedSpellings		TaggedStrings with alternate
	 *										spellings as keys and standard
	 *										spellings as tag values.
	 *
	 *	<p>
	 *	Does nothing in this implementation.
	 *	</p>
	 */

	public void setMappedSpellings( TaggedStrings standardMappedSpellings )
	{
	}

	/**	Sets standard spellings.
	 *
	 *	@param	standardSpellings	Set of standard spellings.
	 *
	 *	<p>
	 *	Does nothing in this implementation.
	 *	</p>
	 */

	public void setStandardSpellings( Set<String> standardSpellings )
	{
	}

	/**	Returns standard spellings given a spelling.
	 *
	 *	@param	spelling	The spelling.
	 *
	 *	@return				The standard spellings as an array of String.
	 */

	 public String[] standardizeSpelling( String spelling )
	 {
								//	Assume input spelling as result.

	 	String[] result	= new String[]{ spelling };

								//	If we connected successfully to
								//	the remote standardizer server,
								//	ask the remote server for the
								//	standardized spelling.
	 	if ( session != null )
	 	{
	 		try
	 		{
	 			result	= session.standardizeSpelling( spelling );
	 		}
	 		catch ( RemoteException e )
	 		{
	 		}
	 	}

	 	return result;
	 }

	/**	Returns a standard spelling given a standard or alternate spelling.
	 *
	 *	@param	spelling	The spelling.
	 *	@param	wordClass	The word class.
	 *
	 *	@return				The standard spelling.
	 */

	public String standardizeSpelling( String spelling , String wordClass )
	{
									//	Assume input spelling as result.

	 	String result	= spelling;

	 	if ( session != null )
	 	{
	 		try
	 		{
	 			result	=
	 				session.standardizeSpelling( spelling , wordClass );
	 		}
	 		catch ( RemoteException e )
	 		{
	 		}
	 	}

		return result;
	 }

	 /** Returns number of alternate spellings.
	  *
	  *	@return		The number of alternate spellings.
	  */

	public int getNumberOfAlternateSpellings()
	{
		int	result	= 0;

	 	if ( session != null )
	 	{
	 		try
	 		{
	 			result	= session.getNumberOfAlternateSpellings();
	 		}
	 		catch ( RemoteException e )
	 		{
	 		}
	 	}

		return result;
	}

	 /** Returns number of alternate spellings by word class.
	  *
	  *	@return		int array with two entries.
	  *				[0]	=	The number of alternate spellings word classes.
	  *				[1]	=	The number of alternate spellings in the
	  *						word classes.
	  */

	public int[] getNumberOfAlternateSpellingsByWordClass()
	{
		int[] result	= new int[ 2 ];

		result[ 0 ]		= 0;
		result[ 1 ]		= 0;

	 	if ( session != null )
	 	{
	 		try
	 		{
	 			result	= session.getNumberOfAlternateSpellingsByWordClass();
	 		}
	 		catch ( RemoteException e )
	 		{
	 		}
	 	}

		return result;
	}

	 /** Returns number of standard spellings.
	  *
	  *	@return		The number of standard spellings.
	  */

	public int getNumberOfStandardSpellings()
	{
		int	result	= 0;

	 	if ( session != null )
	 	{
	 		try
	 		{
	 			result	= session.getNumberOfStandardSpellings();
	 		}
	 		catch ( RemoteException e )
	 		{
	 		}
	 	}

		return result;
	}

	/**	Return the spelling map.
	 *
	 *	@return		Null since this implementation does not use a
	 *				local map.
	 */

	public TaggedStrings getMappedSpellings()
	{
		return null;
	}

	/**	Return the standard spellings.
	 *
	 *	@return		Always null.
	 */

	public Set<String> getStandardSpellings()
	{
		return null;
	}

	/**	Preprocess spelling.
	 *
	 *	@param	spelling	Spelling to preprocess.
	 *
	 *	@return				Preprocessed spelling.
	 *
	 *	<p>
	 *	Unused in this standardizer.
	 *	</p>
	 */

	public String preprocessSpelling( String spelling )
	{
		return spelling;
	}

	/**	Fix capitalization of standardized spelling.
	 *
	 *	@param	spelling			The original spelling.
	 *	@param	standardSpelling	The candidate standard spelling.
	 *
	 *	@return						Standard spelling with initial
	 *								capitalization matching original
	 *								spelling.
	 *
	 *	<p>
	 *	Unused in this standardizer.
	 *	</p>
	 */

	public String fixCapitalization
	(
		String spelling ,
		String standardSpelling
	)
	{
    	return standardSpelling;
	}

	/**	Close standardizer.
 	 */

	public void close()
	{
		try
		{
			if ( session != null ) session.endSession();
		}
		catch ( Exception e )
		{
		}
	}

	/**	Get the logger.
	 *
	 *	@return		The logger.
	 */

	public Logger getLogger()
	{
		return logger;
	}

	/**	Set the logger.
	 *
	 *	@param	logger		The logger.
	 */

	public void setLogger( Logger logger )
	{
		this.logger	= logger;
	}

	/**	Return standardizer description.
	 *
	 *	@return		Standardizer description.
	 */

	public String toString()
	{
		return "Remote Spelling Standardizer";
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



