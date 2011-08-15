package edu.northwestern.at.utils.csv;

/*	Please see the license information in the header below. */

import java.io.*;
import java.util.*;

import edu.northwestern.at.utils.*;

/**	CSVFileReader reads and parses a delimiter separated text file.
 *
 *	<p>
 *	Adapted from a C++ original that is
 *	Copyright (C) 1999 Lucent Technologies.<br>
 *	Excerpted from 'The Practice of Programming' by Brian Kernighan and
 *	Rob Pike.
 *	</p>
 *
 *	<p>
 *	Included by permission of the
 *	<a href="http://tpop.awl.com/">Addison-Wesley</a> web site, which says:
 *	<cite>"You may use this code for any purpose, as long as you leave the
 *	copyright notice and book citation attached"</cite>.
 *  </p>
 *
 *	@author	Brian Kernighan and Rob Pike (C++ original)
 *	@author	Ian F. Darwin (translation into Java and removal of I/O)
 *	@author	Ben Ballard (rewrote handleQuotedField to handle double
 *	qualifiers and for readability)
 *	@author	Fabrizio Fazzino (added integration with CSVFile, handling of
 *	variable qualifier and ArrayList with explicit String type)
 *	@author	Philip R. Burns.  Allow character set encoding for files and
 *	List instead of Vector.
 */

public class CSVFileReader extends CSVFile
{
	/** Buffered reader for the  CSV file to be read.
	 */

	protected BufferedReader in;

	/**	Create a CSV file reader given a file name and encoding.
	 *
	 *	@param	inputFileName	Name of the CSV formatted input file.
	 *	@param	encoding		The character encoding for the file.
	 *
	 *	@throws FileNotFoundException	If input file does not exist.
	 *	@throws IOException				If input file cannot be opened.
	 */

	public CSVFileReader( String inputFileName , String encoding )
		throws IOException, FileNotFoundException
	{
		super();

		openInputFile( inputFileName , encoding );
	}

	/**	Create CSVFileReader with a given field separator.
	 *
	 *	@param	inputFileName	Name of the CSV formatted input file.
	 *	@param	encoding		Character encoding for the file.
	 *	@param 	separator		Field separator.
	 *
	 *	@throws FileNotFoundException	If input file does not exist.
	 *	@throws IOException				If input file cannot be opened.
	 */

	public CSVFileReader
	(
		String inputFileName ,
		String encoding ,
		char separator
	)
		throws IOException , FileNotFoundException
	{
		super( separator );

		openInputFile( inputFileName , encoding );
	}

	/**	Create CSVFileReader with given field separator and qualifier character.
	 *
	 *	@param	inputFileName	Name of the CSV formatted input file.
	 *	@param	encoding		Character encoding for the file.
	 *	@param 	separator		Field separator.
	 *	@param	qualifier		Qualified character.
	 *							Ascii 0 means don't use a qualifier.
	 *
	 *	@throws FileNotFoundException	If input file does not exist.
	 *	@throws IOException				If input file cannot be opened.
	 */

	public CSVFileReader
	(
		String inputFileName ,
		String encoding ,
		char separator ,
		char qualifier
	)
		throws FileNotFoundException, IOException
	{
		super( separator , qualifier );

		openInputFile( inputFileName , encoding );
	}

	/**	Open input file.
	 *
	 *	@param	inputFileName	Name of the CSV formatted input file.
	 *	@param	encoding		Character encoding for the file.
	 */

	protected void openInputFile
	(
		String inputFileName ,
		String encoding
	)
		throws IOException
	{
		UnicodeReader streamReader	= null;

		if ( ( encoding == null ) || ( encoding.length() == 0 ) )
		{
			streamReader	=
				new UnicodeReader
				(
					new FileInputStream( new File( inputFileName ) )
				);
		}
		else
		{
			streamReader	=
				new UnicodeReader
				(
					new FileInputStream( new File( inputFileName ) ) ,
					encoding
				);
		}

		in	= new BufferedReader( streamReader );
	}

	/**	Split the next line of the input CSV file into fields.
	 *
	 * @return 	            	List of strings containing each field from
	 *							the next line of the file.
	 *
	 * @throws IOException		If an error occurs while reading the new
	 *							line from the file.
	 */

	public List<String> readFields()
		throws IOException
	{
		List<String> fields	= ListFactory.createNewList();
		StringBuffer sb		= new StringBuffer();
		String line			= in.readLine();

		if ( line == null ) return null;

		if ( line.length() == 0 )
		{
			fields.add( line );
			return fields;
		}

		int i	= 0;

		do
		{
			sb.setLength( 0 );

			if	(	( i < line.length() ) &&
					( line.charAt( i ) == qualifier ) &&
					( line.charAt( i ) != 0 )
				)
			{
				i	= handleQuotedField( line , sb , ++i );
			}
			else
			{
				i	= handlePlainField( line , sb , i );
			}

			fields.add( sb.toString() );
			i++;
		}
		while ( i < line.length() );

		return fields;
	}

	/**	Close the input CSV file.
	 *
	 *	@throws	IOException		If an error occurs while closing the file.
	 */

	public void close()
		throws IOException
	{
		in.close();
	}

	/**	Handles a qualified field.
	 *
	 *	@param		s	Input string.
	 *	@param		sb	Output string buffer.
	 *	@param		i	Current offset in string s.
	 */

	protected int handleQuotedField( String s , StringBuffer sb , int i )
	{
		int j;
		int len	= s.length();

		for ( j = i ; j < len ; j++ )
		{
			if ( ( s.charAt( j ) == qualifier ) && ( ( j + 1 ) < len ) )
			{
				if ( s.charAt( j + 1 ) == qualifier )
				{
								//	Skip escape char.
					j++;
				}
				else if ( s.charAt( j + 1 ) == separator )
				{
								//	Next delimiter.
					j++;
								//	Skip end qualifiers.
					break;
				}
			}
			else if ( ( s.charAt( j ) == qualifier ) && ( ( j + 1 ) == len ) )
			{
								//	End qualifiers at end of line.
				break;
			}
								//	Regular character.

			sb.append( s.charAt( j ) );
		}

		return j;
	}

	/**	Handles an unqualified field.
	 *
	 *	@param		s	Input string.
	 *	@param		sb	Output string buffer.
	 *	@param		i	Current offset in string s.
	 *
	 *	@return		index of next separator.
	 */

	protected int handlePlainField( String s , StringBuffer sb , int i )
	{
		int result;
								//	Look for separator.

		int j	= s.indexOf( separator , i );

								//	No separator found.
								//	Append all remaining text from
								//	current position to end of input
								//	string.
		if ( j == -1 )
		{
			sb.append( s.substring( i ) );
			result	= s.length();
		}
								//	Separator found.
								//	Append all text from current
								//	position up to separator.
		else
		{
			sb.append( s.substring( i , j ) );
			result	= j;
		}

		return result;
	}
}

