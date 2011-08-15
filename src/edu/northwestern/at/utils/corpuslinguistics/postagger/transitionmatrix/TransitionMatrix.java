package edu.northwestern.at.utils.corpuslinguistics.postagger.transitionmatrix;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.net.*;
import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.logger.*;

/**	Probability transition matrix.
 *
 *	<p>
 *	Holds the unigram, bigram, and trigram counts and probabilities.
 *	</p>
 *
 *	<p>
 *	Call calculateProbabilities() to calculate tag transition probabilities.
 *	Weights for the ngrams are computed using deleted interpolation.
 *	</p>
 */

public class TransitionMatrix
	extends IsCloseableObject
	implements UsesLogger
{
	/**	True if debugging output enabled.
	 */

	protected static boolean debug		= true;

	/**	HashMaps with part of speech tags as the keys and
	 *	counts as the values.
	 */

	protected Map<String, Integer> unigramCountMap	=
		MapFactory.createNewMap();

	protected Map2D<String, String, Integer> bigramCountMap	=
		Map2DFactory.createNewMap2D();

	protected Map3D<String, String, String, Integer> trigramCountMap	=
		Map3DFactory.createNewMap3D();

	/**	HashMaps with part of speech tags as the keys and
	 *	transition probability as the values.
	 */

	protected Map<String, Double> unigramProbMap	=
		MapFactory.createNewMap();

	protected Map2D<String, String, Double> bigramProbMap	=
		Map2DFactory.createNewMap2D();

	protected Map3D<String, String, String, Double> trigramProbMap	=
		Map3DFactory.createNewMap3D();

	/**	Total ngram tag counts.
	 */

	protected int[] totalNGrams			= new int[]{ 0 , 0 , 0 };

	/**	Unique ngram tag counts.
	 */

	protected int[] uniqueNGrams		= new int[]{ 0 , 0 , 0 };

	/**	Total number of words.
	 */

	protected int totalWords			= 0;

	/**	True if probabilities calculated.
	 */

	protected boolean haveProbabilities	= false;

	/**	Bigram weights from deleted interpolation.
	 */

	protected double[] bigramWeights	= null;

	/**	Trigram weights from deleted interpolation.
	 */

	protected double[] trigramWeights	= null;

	/**	Constants for clarification.
	 */

	protected static final int UNIGRAM			= 0;
	protected static final int BIGRAM			= 1;
	protected static final int TRIGRAM			= 2;

	/**	Logger used for output. */

	protected Logger logger	= new DummyLogger();

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

	/**	Increment unigram tag count.
	 *
	 *	@param	tag			The part of speech tag.
	 *	@param	increment	The increment.
	 */

	public void incrementCount( String tag , int increment )
	{
								//	Get current count for these tags.

		Integer count	= unigramCountMap.get( tag );

		int newCount	= increment;

		if ( count != null )
		{
			newCount	= count.intValue() + increment;
		}
		else
		{
			uniqueNGrams[ UNIGRAM ]++;
		}

		totalNGrams[ UNIGRAM ]	+=	increment;

										//	Store incremented count.

		unigramCountMap.put( tag , new Integer( newCount ) );

										//	Update total words.

		totalWords			+= increment;

		                            	//	Probabilities are invalid.
		haveProbabilities	= false;
	}

	/**	Increment bigram tag count.
	 *
	 *	@param	tag1		The first part of speech tag.
	 *	@param	tag2		The second part of speech tag.
	 *	@param	increment	The increment.
	 */

	public void incrementCount( String tag1 , String tag2 , int increment )
	{
								//	Get current count for these tags.

		Integer count	= (Integer)bigramCountMap.get( tag1 , tag2 );

		int newCount	= increment;

		if ( count != null )
		{
			newCount	= count.intValue() + increment;
		}
		else
		{
			uniqueNGrams[ BIGRAM ]++;
		}
										//	Update total words.

		totalNGrams[ BIGRAM ]	+= increment;

										//	Store incremented count.
		bigramCountMap.put(
			tag1 , tag2 , new Integer( newCount ) );

		                            	//	Probabilities are invalid.
		haveProbabilities	= false;
	}

	/**	Increment trigram tag count.
	 *
	 *	@param	tag1		The first part of speech tag.
	 *	@param	tag2		The second part of speech tag.
	 *	@param	tag3		The third part of speech tag.
	 *	@param	increment	The increment.
	 */

	public void incrementCount
	(
		String tag1 ,
		String tag2 ,
		String tag3 ,
		int increment
	)
	{
								//	Get current count for these tags.

		Integer count	=
			(Integer)trigramCountMap.get( tag1 , tag2 , tag3 );

		int newCount	= increment;

		if ( count != null )
		{
			newCount	= count.intValue() + increment;
		}
		else
		{
			uniqueNGrams[ TRIGRAM ]++;
		}
										//	Update total words.

		totalNGrams[ TRIGRAM ]	+= increment;

										//	Store incremented count.

		trigramCountMap.put( tag1 , tag2 , tag3 , new Integer( newCount ) );

		                            	//	Probabilities are invalid.
		haveProbabilities	= false;
	}

	/**	Safely divide two counts.
	 *
	 *	@param	numerator	The undiscounted numerator value.
	 *	@param	denominator	The undiscounted denominator value.
	 *
	 *	@return				numerator / denominator ,
	 *						or 0 if denominator <= 0.
	 */

	public double safelyDivideCount( int numerator , int denominator )
	{
		double result	= 0.0D;

		if ( denominator > 0.0D )
		{
			result	= numerator / (double)denominator;
		}

		return result;
	}

	/**	Safely divide two counts.
	 *
	 *	@param	numerator	The undiscounted numerator value.
	 *	@param	denominator	The undiscounted denominator value.
	 *
	 *	@return				(numerator - 1 ) / ( denominator - 1 ),
	 *						or 0 if ( denominator - 1 ) <= 0.
	 */

	public double safelyDivideSmoothedCount
	(
		int numerator ,
		int denominator
	)
	{
		double result	= 0.0D;

		if ( denominator > 1 )
		{
			result	= ( numerator - 1 ) / (double)( denominator - 1 );
		}

		return result;
	}

	/**	Calculate transition probabilities from counts.
	 */

	public void calculateProbabilities()
	{
								//	Calculate bigram weights using
								//	deleted interpolation.

		computeBigramWeights();

								//	Calculate trigram weights using
								//	deleted interpolation.

		computeTrigramWeights();

        						//	The deleted interpolation methods
        						//	compute the probabilities.

		haveProbabilities	= true;
	}

	/**	Calculate trigram weights for contextual smoothing.
	 *
	 *	<p>
	 *	The trigram weights are computed using deleted interpolation.
	 *	</p>
	 */

	protected void computeTrigramWeights()
	{

		double lambda1		= 0.0D;
		double lambda2		= 0.0D;
		double lambda3		= 0.0D;

		int n1				= 0;
		int n2				= 0;
		int n3				= 0;
		int cnt				= 0;
		int nEntries			= totalNGrams[ 0 ];

								//	Get iterator over trigram tags.

		Iterator<CompoundKey> iterator	= trigramCountMap.iterator();

		while ( iterator.hasNext() )
		{
			cnt++;
			CompoundKey compoundKey	= iterator.next();

			Object[] keyValues		= compoundKey.getKeyValues();

			String[] tags			= new String[ keyValues.length ];

			for ( int i = 0 ; i < tags.length ; i++ )
			{
				tags[ i ]	= keyValues[ i ].toString();
			}

			int trigramCount	=
				getCount( tags[ 0 ] , tags[ 1 ] , tags[ 2 ] );

			if ( trigramCount <= 0 ) continue;

			double unigramP	=
				safelyDivideCount( getCount( tags[ 2 ] ) , nEntries );

			double bigramP	=
				safelyDivideCount
				(
					getCount( tags[ 1 ] , tags[ 2 ] ) ,
					getCount( tags[ 1 ] )
				);

			double trigramP	=
				safelyDivideCount
				(
					trigramCount ,
					getCount( tags[ 0 ] , tags[ 1 ] )
				);

			unigramProbMap.put(
				tags[ 2 ] , new Double( unigramP ) );

			bigramProbMap.put(
				tags[ 1 ] , tags[ 2 ] , new Double( bigramP ) );

			trigramProbMap.put(
				tags[ 0 ] , tags[ 1 ] , tags[ 2 ] ,
				new Double( trigramP ) );

			double maxP	=
				Math.max( Math.max( unigramP , bigramP ) , trigramP );

			if ( maxP	== unigramP )
			{
				lambda1	+=	trigramCount;
			}
			else if ( maxP	== bigramP )
			{
				lambda2	+=	trigramCount;
			}
			else
			{
				lambda3	+=	trigramCount;
			}
		}

		double sum	= lambda1 + lambda2 + lambda3;

		if ( sum > 0 )
		{
			lambda1	= lambda1 / sum;
			lambda2	= lambda2 / sum;
			lambda3	= lambda3 / sum;
		}

		trigramWeights	=
			new double[]{ lambda1 , lambda2 , lambda3 };
	}

	/**	Calculate bigram weights for contextual smoothing.
	 *
	 *	<p>
	 *	The bigram weights are computed using deleted interpolation.
	 *	</p>
	 */


	protected void computeBigramWeights()
	{
		double lambda1		= 0.0D;
		double lambda2		= 0.0D;

		int n1				= 0;
		int n2				= 0;

		int nEntries		= totalNGrams[ 0 ];

								//	Get iterator over bigram tags.

		Iterator<CompoundKey> iterator	= bigramCountMap.keySet().iterator();

		while ( iterator.hasNext() )
		{
			CompoundKey compoundKey	= iterator.next();

			Object[] keyValues		= compoundKey.getKeyValues();

			String[] tags			= new String[ keyValues.length ];

			for ( int i = 0 ; i < tags.length ; i++ )
			{
				tags[ i ]	= keyValues[ i ].toString();
			}

			int bigramCount	=
				getCount( tags[ 0 ] , tags[ 1 ] );

			if ( bigramCount <= 0 ) continue;
//			if ( bigramCount <= 1 ) continue;

			double unigramP	=
				safelyDivideSmoothedCount
				(
					getCount( tags[ 1 ] ) ,
					nEntries
				);

			double bigramP	=
				safelyDivideSmoothedCount
				(
					bigramCount ,
					getCount( tags[ 0 ] )
				);

			if ( unigramP > bigramP )
			{
				lambda1	+=	bigramCount;
				n1++;
			}
			else
			{
				lambda2	+=	bigramCount;
				n2++;
			}
        }

		double sum		= lambda1 + lambda2;

		if ( sum > 0 )
		{
			lambda1	= lambda1 / sum;
			lambda2	= lambda2 / sum;
		}

		lambda1	= 0.03;
		lambda2	= 0.97;

		bigramWeights	=
			new double[]{ lambda1 , lambda2 };
	}

	/**	Look up unigram count.
	 *
	 *	@param	tag		The part of speech tag.
	 *
	 *	@return			Count of tag.
	 */

	public int getCount( String tag )
	{
		int result		= 0;

		Integer count	= (Integer)unigramCountMap.get( tag );

		if ( count != null )
		{
			result	= count.intValue();
		}

		return result;
	}

	/**	Look up bigram count.
	 *
	 *	@param	tag1	The first part of speech tag.
	 *	@param	tag2	The second part of speech tag.
	 *
	 *	@return			Count of tag1 followed by tag2.
	 */

	public int getCount( String tag1 , String tag2 )
	{
		int result		= 0;

		Integer count	= (Integer)bigramCountMap.get( tag1 , tag2 );

		if ( count != null )
		{
			result	= count.intValue();
		}

		return result;
	}

	/**	Look up trigram count.
	 *
	 *	@param	tag1	The first part of speech tag.
	 *	@param	tag2	The second part of speech tag.
	 *	@param	tag3	The third part of speech tag.
	 *
	 *	@return			Count of tag1 followed by tag2 folowed by tag3.
	 */

	public int getCount( String tag1 , String tag2 , String tag3 )
	{
		int result		= 0;

		Integer count	=
			(Integer)trigramCountMap.get( tag1 , tag2 , tag3 );

		if ( count != null )
		{
			result	= count.intValue();
		}

		return result;
	}

	/**	Look up unigram probability.
	 *
	 *	@param	tag		The part of speech tag.
	 *
	 *	@return			Probability of tag.
	 */

	public double getProbability( String tag )
	{
		if ( !haveProbabilities )
		{
			calculateProbabilities();
		}

		Double prob		= (Double)unigramProbMap.get( tag );

		double result	= 0.0D;

		if ( prob != null )
		{
			result	= prob.doubleValue();
		}

		return result;
	}

	/**	Look up bigram probability.
	 *
	 *	@param	tag1	The first part of speech tag.
	 *	@param	tag2	The second part of speech tag.
	 *
	 *	@return			Transition probability of tag1 followed by tag2.
	 */

	public double getProbability( String tag1 , String tag2 )
	{
		if ( !haveProbabilities )
		{
			calculateProbabilities();
		}

		Double prob		= (Double)bigramProbMap.get( tag1 , tag2 );

		double result	= 0.0D;

		if ( prob != null )
		{
			result	= prob.doubleValue();
		}

		return result;
	}

	/**	Look up trigram probability.
	 *
	 *	@param	tag1	The first part of speech tag.
	 *	@param	tag2	The second part of speech tag.
	 *	@param	tag3	The third part of speech tag.
	 *
	 *	@return			Transition probability of tag1 followed by tag2
	 *					followed by tag3.
	 */

	public double getProbability( String tag1 , String tag2 , String tag3 )
	{
		if ( !haveProbabilities )
		{
			calculateProbabilities();
		}

		Double prob		= (Double)trigramProbMap.get( tag1 , tag2 , tag3 );

		double result	= 0.0D;

		if ( prob != null )
		{
			result	= prob.doubleValue();
		}

		return result;
	}

	/**	Get row key set.
	 *
	 *		@return 	row key set.
	 */

	public Set<String> rowKeySet()
	{
		return trigramCountMap.rowKeySet();
	}

	/**	Get column  key set.
	 *
	 *		@return 	column key set.
	 */

	public Set<String> columnKeySet()
	{
		return trigramCountMap.columnKeySet();
	}

	/**	Get slice  key set.
	 *
	 *		@return 	slice key set.
	 */

	public Set<String> sliceKeySet()
	{
		return trigramCountMap.sliceKeySet();
	}

	/**	Get total number of words.
	 *
	 *	@return		Total number of words.
	 */

	public int getTotalWordCount()
	{
		return totalWords;
	}

	/**	Load transition matrix from a URL.
	 *
	 *	@param	url			URL from which to load transition matrix.
	 *	@param	encoding	Character encoding for file text.
	 *	@param	delimChar	Column separator character.
	 *						Usually a tab (\t).
	 *
	 *	@throws	IOException when an I/O error occurs.
	 */

	public void loadTransitionMatrix
	(
		URL url ,
		String encoding ,
		char delimChar
	)
		throws IOException
	{
								//	Create reader over URL input stream.

		Reader reader	=
			new UnicodeReader( url.openStream() , encoding );

								//	Load transition matrix using reader.

		loadTransitionMatrix( reader , delimChar );
	}

	/**	Load transition matrix from a reader.
	 *
	 *	@param	reader		Reader from which to read transition
	 *						matrix.
	 *
	 *	@param	delimChar	Column separator character.
	 *						Usually a tab (\t).
	 *
	 *	@throws	IOException when an I/O error occurs.
	 */

	public void loadTransitionMatrix
	(
		Reader reader,
		char delimChar
	)
		throws IOException
	{
		String line		= "";
		String delim	= delimChar + "";

								//	Count ngrams of each size.

		totalNGrams[ UNIGRAM ]	= 0;
		totalNGrams[ BIGRAM ]	= 0;
		totalNGrams[ TRIGRAM ]	= 0;

		uniqueNGrams[ UNIGRAM ]	= 0;
		uniqueNGrams[ BIGRAM ]	= 0;
		uniqueNGrams[ TRIGRAM ]	= 0;

								//	Count total words.

		totalWords				= 0;

								//	Wrap reader with BufferedReader
								//	so we can read lines.

		BufferedReader bufferedReader	= new BufferedReader( reader );

								//	Read input lines until no more left.

		while ( ( line = bufferedReader.readLine() ) != null )
		{
								//	Tokenize input line
								//	using the specified delimiter.

			String[] tokens	= line.split( delim );

								//	We must have between 2 and 4
								//	tokens.  Ignore the line if not.

			switch ( tokens.length )
			{
								//	Unigram tag and count.
				case 2:
				{
					int count	= Integer.parseInt( tokens[ 1 ] );

					incrementCount( tokens[ 0 ] , count );

					break;
				}

								//	Bigram tags and count.
				case 3:
				{
					int count	= Integer.parseInt( tokens[ 2 ] );

					incrementCount( tokens[ 0 ] , tokens[ 1 ] , count );

					break;
				}
								//	Trigram tags and count.
				case 4:
				{
					int count	= Integer.parseInt( tokens[ 3 ] );

					incrementCount
					(
						tokens[ 0 ] ,
						tokens[ 1 ] ,
						tokens[ 2 ] ,
						count
					);

					break;
				}

				default:
				{
				}
			}
		}
								//	Close the reader.

		bufferedReader.close();

								//	Calculate probabilities from counts.

		calculateProbabilities();

								//	Display ngram counts.

//		displayNGramCounts();
	}

	/**	Display the ngram counts.
	 */

	public void displayNGramCounts()
	{
		logger.logDebug( "" );
		logger.logDebug( "Transition matrix total ngram counts" );
		logger.logDebug( "" );
		logger.logDebug( "   Unigram    Bigram   Trigram" );
		logger.logDebug( "" );

		String s	=
			StringUtils.lpad
			(
				Formatters.formatIntegerWithCommas( totalNGrams[ UNIGRAM ] ) ,
				10
			);

		s	=
			s +
			StringUtils.lpad
			(
				Formatters.formatIntegerWithCommas( totalNGrams[ BIGRAM ] ) ,
				10
			);

		s	=
			s +
			StringUtils.lpad
			(
				Formatters.formatIntegerWithCommas( totalNGrams[ TRIGRAM ] ) ,
				10
			);

		logger.logDebug( s );

		logger.logDebug( "" );
		logger.logDebug( "Transition matrix unique ngram counts" );
		logger.logDebug( "" );
		logger.logDebug( "   Unigram    Bigram   Trigram" );
		logger.logDebug( "" );

		s	=
			StringUtils.lpad
			(
				Formatters.formatIntegerWithCommas( uniqueNGrams[ UNIGRAM ] ) ,
				10
			);

		s	=
			s +
			StringUtils.lpad
			(
				Formatters.formatIntegerWithCommas( uniqueNGrams[ BIGRAM ] ) ,
				10
			);

		s	=
			s +
			StringUtils.lpad
			(
				Formatters.formatIntegerWithCommas( uniqueNGrams[ TRIGRAM ] ) ,
				10
			);

		logger.logDebug( s );
	}

	/**	Save transition matrix to a file.
	 *
	 *	@param	transitionFileName	File to receive the transition matrix.
	 *	@param	encoding			Character encoding for file text.
	 *	@param	delimChar			Column separator character.
	 *								Usually a tab (\t).
	 *
	 *	@throws	IOException 		when an I/O error occurs.
	 *
	 *	<p>
	 *	Each unigram, bigram, and trigram entry in the transition
	 *	matrix is saved in a columnar format with the specified
	 *	delimiter character acting as the column separator.  The
	 *	counts are saved, not the probabilities, so that different
	 *	smoothing methods can be applied without requiring the
	 *	training date be recreated.
	 *	</p>
	 *
	 *	<p>
	 *	<code>
	 *	tag count<br />
	 *	tag1 tag2 count<br />
	 *	tag1 tag2 tag3 count<br />
	 *	</code>
	 *	</p>
	 */

	public void saveTransitionMatrix
	(
		String transitionFileName ,
		String encoding ,
		char delimChar
	)
		throws IOException
	{
		displayNGramCounts();

		FileOutputStream fileOutputStream			=
			new FileOutputStream( transitionFileName , false );

		OutputStreamWriter writer	=
			new OutputStreamWriter( fileOutputStream , encoding );

		saveTransitionMatrix( writer , delimChar );
	}

	/**	Save transition matrix to a writer.
	 *
	 *	@param	writer		Writer to use to save transition matrix.
	 *	@param	delimChar	Column separator character.  Usually a tab (\t).
	 *
	 *	@throws	IOException 		when an I/O error occurs.
	 */

	public void saveTransitionMatrix( Writer writer , char delimChar )
		throws IOException
	{
								//	Wrap writer in buffered writer.

		BufferedWriter bufferedWriter	= new BufferedWriter( writer );

								//	Get row tag set.

		String[] rowTags				=
			(String[])unigramCountMap.keySet().toArray( new String[]{} );

		Arrays.sort( rowTags );

								//	Get column tag set.

		Set<String> columnSet	=
			(Set<String>)bigramCountMap.columnKeySet();

		String[] columnTags		=
			(String[])columnSet.toArray( new String[]{} );

		Arrays.sort( columnTags );

								//	Get slice tag set.

		Set<String> sliceSet	=
			(Set<String>)trigramCountMap.sliceKeySet();

		String[] sliceTags		=
			(String[])sliceSet.toArray( new String[]{} );

		Arrays.sort( sliceTags );

								//	Loop over unigrams.

		for ( int i = 0 ; i < rowTags.length ; i++ )
		{
								//	Output unigram count.

			int count	= getCount( rowTags[ i ] );

			if ( count > 0 )
			{
				bufferedWriter.write( rowTags[ i ] + delimChar + count );
				bufferedWriter.newLine();
			}
								//	Loop over columns/bigrams.

			for ( int j = 0 ; j < columnTags.length ; j++ )
			{
								//	Output bigram count.

				count	=
					getCount( rowTags[ i ] , columnTags[ j ] );

				if ( count > 0 )
				{
					bufferedWriter.write
					(
						rowTags[ i ]  +  delimChar +
						columnTags[ j ] + delimChar +
						count
					);

					bufferedWriter.newLine();
				}

				for ( int k = 0 ; k < sliceTags.length ; k++ )
				{
					if ( sliceTags.equals( "slice" ) ) continue;

								//	Output trigram count.

					count	=
						getCount
						(
							rowTags[ i ] ,
							columnTags[ j ] ,
							sliceTags[ k ]
						);

					if ( count > 0 )
					{
						bufferedWriter.write
						(
							rowTags[ i ]  +  delimChar +
							columnTags[ j ] + delimChar +
							sliceTags[ k ] + delimChar +
							count
						);

						bufferedWriter.newLine();
					}
				}
			}
		}

		bufferedWriter.flush();
		bufferedWriter.close();
	}

	/**	Return weights for bigrams using deleted interpolation.
	 *
	 *	@return		Two element double array "lambda" of ngram weights.
	 *				lambda[0]	= bigram weight
	 *				lambda[1]	= unigram weight
	 *
	 *	<p>
	 *	The sum of the lambda values is 1.0 .  The adjusted probability
	 *	for a bigram is computed from the maximum likelihood
	 *	probabilities (i.e., undiscounted) as follows.
	 *	</p>
	 *
	 *	<p>
	 *	<code>
	 *	p*( tag2 | tag1 ) =< br />
	 *		lambda[0] * p( tag2 | tag1 ) +<br />
	 *		lambda[1] * p( tag2 )
	 *	</p>
	 */

	public double[] getBigramWeights()
	{
		if ( bigramWeights == null )
		{
			calculateProbabilities();
		}

		return bigramWeights;
	}

	/**	Return weights for trigrams using deleted interpolation.
	 *
	 *	@return		Three element double array "lambda" of ngram weights.
	 *				lambda[0]	= trigram weight
	 *				lambda[1]	= bigram weight
	 *				lambda[2]	= unigram weight
	 *
	 *	<p>
	 *	The sum of the lambda values is 1.0 .  The adjusted probability
	 *	for a trigram is computed from the maximum likelihood
	 *	probabilities (i.e., undiscounted) as follows.
	 *	</p>
	 *
	 *	<p>
	 *	<code>
	 *	p*( tag3 | tag1 , tag2 ) =< br />
	 *		lambda[0] * p( tag3 | tag1 , tag2 ) +<br />
	 *		lambda[1] * p( tag3 | tag2 ) +<br />
	 *		lambda[2] * p( tag3 )
	 *	</p>
	 */

	public double[] getTrigramWeights()
	{
		if ( trigramWeights == null )
		{
			calculateProbabilities();
		}

		return trigramWeights;
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



