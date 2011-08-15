package edu.northwestern.at.utils;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.math.*;
import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.math.NumberOps;

/**	Count map utilities.
 *
 *	<p>
 *	Count maps have a key representing items to count and
 *	java.lang.Number values as the counts.  In many cases, the Number values
 *	are of type java.lang.Integer, but this is not necessarily the case.
 *	For example, a count map may have strings as keys and java.lang.Double
 *	scaled word frequencies as values.
 *	</p>
 */

public class CountMapUtils
{
	/**	Get summary counts from a count map.
	 *
	 *	@param	map		The map with string keys and Number counts
	 *					as values.
	 *
	 *	@return			Three entry double array.
	 *						result[ 0 ]	= sum of counts
	 *						result[ 1 ]	= sum of squared counts
	 *						result[ 2 ]	= unique count (size of map)
	 */

	public static double[] getSummaryCountsFromCountMap
	(
		Map<? extends Object , ? extends Number> map
	)
	{
		double result[]	= new double[ 3 ];

		result[ 0 ]	= 0.0D;
		result[ 1 ]	= 0.0D;
		result[ 2 ]	= (double)map.size();

		Set<?> keyset			= map.keySet();
		Iterator<?> iterator	= keyset.iterator();

		while ( iterator.hasNext() )
		{
			double dCount	= map.get( iterator.next() ).doubleValue();

			result[ 0 ]		+=  dCount;
			result[ 1 ]		+=  dCount * dCount;
		}

		return result;
	}

	/**	Get total count of words in map.
	 *
	 *	@param	map		The map with string keys and Number counts
	 *					as values.
	 *
	 *	@return			Sum of counts as an integer.
	 */

	public static int getTotalWordCount
	(
		Map<? extends Object , ? extends Number> map
	)
	{
		int result				= 0;

		Set<?> keyset			= map.keySet();
		Iterator<?> iterator	= keyset.iterator();

		while ( iterator.hasNext() )
		{
			int count	= map.get( iterator.next() ).intValue();
			result		+= count;
		}

		return result;
	}

	/**	Get sum of cross products for counts in two maps.
	 *
	 *	@param	countMap1	First count map.
	 *	@param	countMap2	Second count map.
	 *
	 *	@return				sum of cross products as a double.
	 */

	public static double getSumOfCrossProducts
	(
		Map<? extends Object , ? extends Number> countMap1 ,
		Map<? extends Object , ? extends Number> countMap2
	)
	{
								//	Holds cross product.

		double result	= 0.0D;

								//	Iterate over shorter map for
								//	efficiency.

		if ( countMap1.size() > countMap2.size() )
		{
			for	(	Iterator<?> iterator = countMap2.keySet().iterator() ;
					iterator.hasNext() ; )
			{
				Object key	= iterator.next();

				if ( countMap1.containsKey( key ) )
				{
					Number count1	= countMap1.get( key );
					Number count2	= countMap2.get( key );

					result +=
						NumberOps.multiply( count1 , count2 ).doubleValue();
				}
			}
		}
		else
		{
			for	(	Iterator<?> iterator = countMap1.keySet().iterator() ;
					iterator.hasNext() ; )
			{
				Object key	= iterator.next();

				if ( countMap2.containsKey( key ) )
				{
					Number count1	= countMap1.get( key );
					Number count2	= countMap2.get( key );

					result +=
						NumberOps.multiply( count1 , count2 ).doubleValue();
				}
			}
		}

		return result;
	}

	/**	Convert map values to integer 1 or 0.
	 *
	 *	@param	map		Count map to booleanize.
	 *
	 *	@return			New map containing booleanized count values.
	 *
	 *	<p>
	 *	Non-zero counts are converted to integer 1, 0 counts are
	 *	converted to integer 0.
	 *	</p>
	 */

	public static<K> Map<K , Number> booleanizeCountMap
	(
		Map<K , ? extends Number> map
	)
	{
		Map<K , Number> result	= new TreeMap<K , Number>();

		Iterator<K> iterator	= map.keySet().iterator();

		while ( iterator.hasNext() )
		{
			K key			= iterator.next();

			double dCount	= map.get( key ).doubleValue();

			int binValue	= ( dCount == 0.0D ) ? 0 : 1;

			result.put( key , new Integer( binValue ) );
		}

		return result;
	}

	/**	Scale count entries in count map.
	 *
	 *	@param	map				The count map.
	 *	@param	scaleFactor		The double value by which to
	 *							multiply each count value in the count map.
	 *
	 *	@return					Map with same keys as input map and
	 *							counts scaled using scaleFactor.
	 */

	public static<K> Map<K , Number> scaleCountMap
	(
		Map<K , ? extends Number> map ,
		double scaleFactor
	)
	{
		Map<K , Number> result	= new TreeMap<K , Number>();

		Iterator<K> iterator	= map.keySet().iterator();

		while ( iterator.hasNext() )
		{
			K key			= iterator.next();

			double dCount	= map.get( key ).doubleValue();

			result.put( key , new Double( dCount * scaleFactor ) );
		}

		return result;
	}

	/**	Get words from a map.
	 *
	 *	@param	map		The map with arbitrary objects as keys and
	 *					Number counts as values.
	 *
	 *	@return			Keys as a set.
	 */

	public static<K> Set<K> getWordsFromMap
	(
		Map<K , ?> map
	)
	{
		return new TreeSet<K>( map.keySet() );
	}

	/**	Split string at tab character.
	 *
	 *	@param	s	The string to split into a key and a count.
	 *
	 *	@return		Two element string array with the key and count.
	 */

	public static String[] splitKeyedCountString( String s )
	{
		String[] tokens	= new String[ 2 ];

		tokens[ 0 ]	= "";
		tokens[ 1 ]	= "0";

		if ( s == null ) return tokens;
		if ( s.length() == 0 ) return tokens;

		int tabPos	= s.indexOf( "\t" );

		if ( tabPos < 0 )
		{
			tokens[ 0 ]	= s;
		}
		else
		{
			tokens[ 0 ]	= s.substring( 0 , tabPos ).trim();
			tokens[ 1 ]	= s.substring( tabPos + 1 ).trim();
		}

		return tokens;
	}

	/**	Add words/counts from one map to another.
	 *
	 *	@param	destinationMap		Destination map.
	 *	@param	sourceMap			Source map.
	 *
	 *	<p>
	 *	On output, the destination map is updated with words and counts
	 *	from the source map.  The key type for both input maps must be
	 *	the same for this to make sense.
	 *	</p>
	 */

	public static<K> void addCountMap
	(
		Map<K , Number> destinationMap ,
		Map<K , Number> sourceMap
	)
	{
		Iterator<K> iterator	= sourceMap.keySet().iterator();

								//	Loop over all words in source map.

		while ( iterator.hasNext() )
		{
								//	Get next word in source map.

			K key				= iterator.next();

								//	Get source map count for this word.

			Number sourceCount	= sourceMap.get( key );

								//	If the destination map does not contain
								//	this word, add it with the count from
								//	the source map.

			if ( !destinationMap.containsKey( key ) )
			{
				destinationMap.put
				(
					key ,
					NumberOps.add( sourceCount , new Double( 0.0D ) )
				);
			}
			else
			{
								//	If the destination map contains the word,
								//	pick up the current count of the word
								//	in the destination map.

				Number destCount	= destinationMap.get( key );

								//	Add the source count to the destination
								//	count in the destination map.

				destCount	= NumberOps.add( sourceCount , destCount );

				destinationMap.put
				(
					key ,
					destCount
				);
			}
		}
	}

	/**	Increment words/counts in one map from another.
	 *
	 *	@param	destinationMap		Destination map.
	 *	@param	sourceMap			Source map.
	 *
	 *	<p>
	 *	The key types for the two input maps must be the same.
	 *	</p>
	 *
	 *	<p>
	 *	On output, the destination map counts are incremented by one for
	 *	each word appearing in the source map.  If a source word does not
	 *	already appear in the destination, it is added with a count of one.
	 *	</p>
	 */

	public static<K> void incrementCountMap
	(
		Map<K , Number> destinationMap ,
		Map<K , Number> sourceMap
	)
	{
		Iterator<K> iterator	= sourceMap.keySet().iterator();

								//	Loop over all words in source map.

		while ( iterator.hasNext() )
		{
								//	Get next word in source map.

			K key				=	iterator.next();

								//	Get source map count for this word.

			Number sourceCount	= sourceMap.get( key );

								//	If the destination map does not contain
								//	this word, add it with a count of one.

			if ( !destinationMap.containsKey( key ) )
			{
				destinationMap.put( key , new Integer( 1 ) );
			}
			else
			{
								//	If the destination map contains the word,
								//	pick up the current count of the word
								//	in the destination map.

				Number destCount	= destinationMap.get( key );

								//	Increment the count by one in the
								//	destination map.

				destinationMap.put
				(
					key ,
					NumberOps.add( destCount , new Integer( 1 ) )
				);
			}
		}
	}

	/**	Subtract words/counts in one map from another.
	 *
	 *	@param	destinationMap		Destination map.
	 *	@param	sourceMap			Source map.
	 *
	 *	<p>
	 *	The key types for the two input maps must be the same.
	 *	</p>
	 *
	 *	<p>
	 *	On output, the destination map counts are updated by removing the
	 *	counts for matching words from the source map.  If the count goes
	 *	to zero for any word in the destination, that word is removed from
	 *	from the destination map.
	 *	</p>
	 */

	public static<K> void subtractCountMap
	(
		Map<K , Number> destinationMap ,
		Map<K , Number> sourceMap
	)
	{
		Iterator<K> iterator	= sourceMap.keySet().iterator();

								//	Loop over all words in source map.

		while ( iterator.hasNext() )
		{
								//	Get next word in source map.

			K key				=	iterator.next();

								//	Get source map count for this word.

			Number sourceCount	= sourceMap.get( key );

								//	If the destination map contains
								//	this word ...

			if ( destinationMap.containsKey( key ) )
			{
								//	Get the count of the word in the
								//	destination map.

				Number destCount		= destinationMap.get( key );

								//	Subtract the source count to produce
								//	the updated destination map count
								//	for this word.

				Number updatedDestCount	=
					NumberOps.subtract( destCount , sourceCount );

								//	If the count remains positive,
								//	put the updated count in the
								//	destination map.  If the count is
								//	not positive, remove the word from
								//	the destination map.

				if ( NumberOps.compareToZero( updatedDestCount ) > 0 )
				{
					destinationMap.put( key , updatedDestCount );
				}
				else
				{
					destinationMap.remove( key );
				}
			}
		}
	}

	/**	Get list of words which two count maps share.
	 *
	 *	@param	countMap1	First count map.
	 *	@param	countMap2	Second count map.
	 *
	 *	@return				List of words appearing in both maps.
	 */

	public static<K> java.util.List getWordsInCommon
	(
		Map<K , ? extends Number> countMap1 ,
		Map<K , ? extends Number> countMap2
	)
	{
								//	Holds sorted list of shared words.

		SortedArrayList<K> result	= new SortedArrayList<K>();

								//	Iterate over shorter map for
								//	efficiency.

		if ( countMap1.size() > countMap2.size() )
		{
			for ( K key : countMap2.keySet() )
			{
				if ( countMap1.containsKey( key ) )
				{
					result.add( key );
				}
			}
		}
		else
		{
			for ( K key : countMap1.keySet() )
			{
				if ( countMap2.containsKey( key ) )
				{
					result.add( key );
				}
			}
		}

		return result;
	}

	/**	Get count of words which two count maps share.
	 *
	 *	@param	countMap1	First count map.
	 *	@param	countMap2	Second count map.
	 *
	 *	@return				Count of words appearing in both maps.
	 *						The key type for the two maps should be
	 *						the same for this to make sense.
	 */

	public static<K> int getCountOfWordsInCommon
	(
		Map<K , ? extends Number> countMap1 ,
		Map<K , ? extends Number> countMap2
	)
	{
								//	Holds count shared words.
		int result	= 0;
								//	Iterate over shorter map for
								//	efficiency.

		if ( countMap1.size() > countMap2.size() )
		{
			for ( K key : countMap2.keySet() )
			{
				if ( countMap1.containsKey( key ) )
				{
					result++;
				}
			}
		}
		else
		{
			for ( K key : countMap1.keySet() )
			{
				if ( countMap2.containsKey( key ) )
				{
					result++;
				}
			}
		}

		return result;
	}

	/**	Get count for a specific word form from a count map.
	 *
	 *	@param	countMap	The word count map.
	 *	@param	word		The word text.
	 *
	 *	@return				The count for the specified word as an integer,
	 *						0 if the word does not occur.
	 */

	public static<K> int getWordCount
	(
		Map<K , ? extends Number> countMap ,
		K word
	)
	{
		int result	= 0;

		if ( countMap.containsKey( word ) )
		{
			result	= countMap.get( word ).intValue();
		}

		return result;
	}

	/**	Updates counts for a word in a map.
	 *
	 *	@param	word		The word.
	 *	@param	count		The word count.
	 *	@param	countMap	The word count map.
	 */

	public static<K> void updateWordCountMap
	(
		K word ,
		int count ,
		Map<K , Number> countMap
	)
	{
		Number newCount;

		if ( countMap.containsKey( word ) )
		{
			newCount	=
				NumberOps.add( countMap.get( word ) , new Integer( count ) );
		}
		else
		{
			newCount	= new Integer( count );
		}

		countMap.put( word , newCount );
	}

	/**	Convert keys in count map to plain strings.
	 *
	 *	@param	countMap	The count map whose keys should be
	 *						converted to strings.
	 *
	 *	@return				The count map with keys converted to plain
	 *						strings.
	 *
	 *	<p>
	 *	Each key element's toString() method is called to convert
	 *	the key object to a plain text string.  Key elements without a
	 *	toString() method will not be added to the result map.
	 *	The object values (counts) are left untouched.  Note that
	 *	no key should be null.  Null keys will be ignored.
	 *	</p>
	 */

	public static<V extends Number> Map<String , Number> convertKeysToStrings
	(
		Map<? extends Object , V> countMap
	)
	{
								//	Create a map to hold the
								//	results of the conversion.

		Map<String , Number> result	= new TreeMap<String , Number>();

								//	Get an iterator over the keys
								//	of the source map.

		Iterator<?> iterator	= countMap.keySet().iterator();

								//	Loop over all words in source map
								//	and copy each word and its count
								//	to the result map.

		while ( iterator.hasNext() )
		{
								//	Get the key element and
								//	associated count.

			Object key	= iterator.next();
			V value		= countMap.get( key );

								//	Holds the new (String) key.

			String copyKey		= null;

								//	If key is null, we will not add
								//	it to the result.  No count map
								//	should have null key elements.

			if ( key != null )
			{
								//	Convert non-null key to string.
				try
				{
					copyKey	= key.toString();
				}
				catch ( Exception e )
				{
					copyKey	= null;
				}
			}
								//	Add string key and associated
								//	count value to result map.

			if ( copyKey != null ) result.put( copyKey , value );
		}

		return result;
	}

	/**	Load strings and counts into count map from a reader.
	 *
	 *	@param	reader	The reader.
	 *
	 *	@return			Count map.
	 *
	 *	<p>
	 *	Each line of the input file has one string, followed by
	 *	an Ascii tab character, followed by an integer count.
	 *	</p>
	 */

	public static Map<String,Number> loadCountMapFromReader( Reader reader )
		throws IOException
	{
		String[] tokens;
		int count;
								//	Create a buffered reader.

        BufferedReader bufferedReader	= new BufferedReader( reader );

								//	Create count map to hold
								//	loaded strings and counts.

		Map<String, Number> result		= MapFactory.createNewMap();

								//	Read first line of input, if any.

		String countLine				= bufferedReader.readLine();

								//	While input remains ...

		while ( countLine != null )
		{
								//	Split input line into string and count.

			tokens		= countLine.split( "\t" );

								//	Convert count token to a number.

			count		= Integer.parseInt( tokens[ 1 ] );

								//	Store string and count in count map.

			result.put( tokens[ 0 ] , count );

								//	Read next input line.

			countLine	= bufferedReader.readLine();
		}
								//	Close reader.

		bufferedReader.close();
								//	Return count map.
		return result;
	}

	/**	Load strings and counts into count map from a file.
	 *
	 *	@param	file	The file.
	 *
	 *	@return			Count map.
	 *
	 *	<p>
	 *	Each line of the input file has one string, followed by
	 *	an Ascii tab character, followed by a count.  The counts
	 *	may be integers or floating point values.
	 *	</p>
	 */

	public static Map<String, Number> loadCountMapFromFile( File file )
		throws IOException
	{
		return loadCountMapFromReader( new FileReader( file ) );
	}

	/**	Get semi-deep clone of a count map.
	 *
	 *	@param	countMap	The count map to clone.
	 *
	 *	@return				Semi-deep clone of the count map.
	 *
	 *	<p>
	 *	A semi-deep clone creates a new count map by duplicating each
	 *	count value and creating new string keys using "toString()"
	 *	on the original map's key values.  For a count map with string
	 *	keys, this produces a deep clone.  For a count map with non-string
	 *	keys, this produces what might be called a semi-deep clone,
	 *	since only the string values of the original keys are duplicated.
	 *	</p>
	 */

	public static<K,V extends Number> Map<String, Number>
		semiDeepClone( Map<K,V> countMap )
	{
								//	Create a map to hold the
								//	results of the conversion.

		Map<String, Number> result	=
			MapFactory.createNewMap( countMap.size() );

								//	Get an iterator over the keys
								//	of the source map.

		Iterator<K> iterator		= countMap.keySet().iterator();

								//	Loop over all words in source map
								//	and copy each word and its count
								//	to the result map.

		while ( iterator.hasNext() )
		{
								//	Get the key element and
								//	associated count.

			K key				= iterator.next();
			Number value		= (Number)countMap.get( key );

								//	Holds the duplicated String key.

			String copyKey		= null;

								//	Holds the new count value.

			Number copyValue	= null;

								//	If key is null, ignore it.
								//	A count map should never have
								//	a null key entry.

			if ( key != null )
			{
				try
				{
					copyKey	= new String( key.toString() );
				}
				catch ( Exception e )
				{
					copyKey	= null;
				}
								//	Get a copy of the count value.
								//	Null counts are set to Integer( 0 ).

				if ( value != null )
				{
					copyValue	= NumberOps.cloneNumber( value );
				}
				else
				{
					copyValue	= new Integer( 0 );
				}
			}
								//	Add cloned entry to result map.

			if ( copyKey != null ) result.put( copyKey , copyValue );
		}

		return result;
	}

	/**	Don't allow instantiation but do allow overrides.
	 */

	protected CountMapUtils()
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



