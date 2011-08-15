package edu.northwestern.at.utils.spellcheck.tools;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.phonetics.*;
import edu.northwestern.at.utils.spellcheck.*;

/** Creates a spelling dictionary data file. */

public class makedict
{
	public static void main( String[] args )
	{
		/** The input dictionary text. */

		String dictionaryText = args[ 0 ];

		/** The output spelling dictionary. */

		String dictionaryName = args[ 1 ];

		/** The dictionary as a treemap.  Has words as keys. */

		TreeMap<String, String>dictionary = new TreeMap<String, String>();

		/** The dictionary keyed by metaphone values. */

		TreeMap<String, java.util.List<String>> metaphones =
			new TreeMap<String, java.util.List<String>>();

		/** Metaphone encoder instance. */

		DoubleMetaphone metaphone = new DoubleMetaphone();

								// Create dictionary.
		try
		{
			String word;

			int nWords = 0;

			try
			{
				BufferedReader in =
					new BufferedReader( new FileReader( dictionaryText ) );

				while ( ( word = in.readLine() ) != null )
				{
					String lowerCaseWord	= word.toLowerCase();
	    			String metaphoneValue	= "";

					try
					{
						metaphoneValue = metaphone.encode( lowerCaseWord );
					}
					catch ( Exception e )
					{
						metaphoneValue = "";
					}

					if ( dictionary.get( lowerCaseWord ) == null )
					{
						dictionary.put( lowerCaseWord , metaphoneValue );

						java.util.List<String> words =
							metaphones.get( metaphoneValue );

						if ( words == null )
						{
							words = ListFactory.createNewList();
						}

						words.add( lowerCaseWord );

        				metaphones.put( metaphoneValue , words );

						nWords++;

						if ( ( ( nWords / 1000 ) * 1000 ) == nWords )
						{
							System.out.println( nWords );
						};
					}
				}

				in.close();
			}
			catch ( Exception e )
			{
				e.printStackTrace();
			}

			nWords		= dictionary.size();
			int nMeta	= metaphones.size();

			BufferedWriter out =
				new BufferedWriter( new FileWriter( dictionaryName ) );

			out.write( nWords + "\n" );
			out.write( nMeta + "\n" );

			java.util.List<String> keys =
				new ArrayList<String>( metaphones.keySet() );

			for ( String key : keys )
			{
				out.write( key + "\n" );

				java.util.List<String> words = metaphones.get( key );

				if ( words == null )
				{
					out.write( "0\n" );
				}
				else
				{
					out.write( words.size() + "\n" );

					for ( int i = 0; i < words.size(); i++ )
					{
						key = words.get( i );
						out.write( key + "\n" );
					}
				}
			}

			out.close();

		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}
}

