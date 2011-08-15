package edu.northwestern.at.utils.csv;

/*	Please see the license information in the header below. */

import java.util.*;
import java.io.*;

import edu.northwestern.at.utils.*;

/**	CSVFileWriter writes text fields to a delimiter separate file.
 *
 *	@author	Fabrizio Fazzino
 *
 *	<p>
 *	Modifications by Philip R. Burns at Northwestern University.
 *	</p>
 *
 *	<ol>
 *	<li>Allow output column selection and ordering.</li>
 *	<li>Allow specification of file character encoding.<li>
 *	<li>Use List instead of Vector.</li>
 *	</ol>
 */

public class CSVFileWriter extends CSVFile
{
	/**	Print writer for the CSV file contents.
	 */

	protected OutputStreamWriter outputWriter;

	/**	Create CSVFileWriter with specified file name and encding,
	 *
	 *	@param	outputFileName	Name of CSV file for output.
	 *	@param	encoding		Character encoding for file output.
	 *
	 *	@throws FileNotFoundException	If file is not found.
	 *	@throws	IOException		If an error occurs while writing the file.
	 */

	public CSVFileWriter( String outputFileName , String encoding )
		throws IOException
	{
		super();

		openOutputFile( outputFileName , encoding );
	}

	/**	Create CSVFileWriter with specified separator character.
	 *
	 *	@param	outputFileName	Name of the CSV file to write to.
	 *	@param	encoding		Character encoding for file output.
	 *	@param	separator       Field separator character.
	 8
	 *	@throws FileNotFoundException	If file is not found.
	 *	@throws	IOException		If an error occurs while writing the file.
	 */

	public CSVFileWriter
	(
		String outputFileName ,
		String encoding ,
		char separator
	)
		throws IOException
	{
		super( separator );

		openOutputFile( outputFileName , encoding );
	}

	/**	CSVFileWriter constructor with given field separator and text qualifier.
	 *
	 *	@param	outputFileName	Name of the CSV file to write to.
	 *	@param	encoding		Character encoding for file output.
	 *	@param	separator		Field separator.
	 *	@param	qualifier		Qualifier character.  Ascii 0 means no qualifier.
	 *
	 *	@throws FileNotFoundException	If file is not found.
	 *	@throws	IOException				If an error occurs while writing the file.
	 */

	public CSVFileWriter
	(
		String outputFileName ,
		String encoding ,
		char separator ,
		char qualifier
	)
		throws FileNotFoundException , IOException
	{
		super( separator , qualifier );

		openOutputFile( outputFileName , encoding );
	}

	/**	Open output file.
	 *
	 *	@param	outputFileName	Name of the CSV file to write to.
	 *	@param	encoding		Character encoding for file output.
	 *
	 *	@throws FileNotFoundException	If file is not found.
	 *	@throws	IOException		If an error occurs while writing the file.
	 */

	protected void openOutputFile
	(
		String outputFileName ,
		String encoding
	)
		throws FileNotFoundException , IOException
	{
		FileOutputStream outputStream			=
			new FileOutputStream( new File( outputFileName ) , false );

		BufferedOutputStream bufferedStream	=
			new BufferedOutputStream( outputStream );

		if ( ( encoding != null ) && ( encoding.length() > 0 ) )
		{
			outputWriter	=
				new OutputStreamWriter
				(
					bufferedStream ,
					encoding
				);
		}
		else
		{
			outputWriter	= new OutputStreamWriter( bufferedStream );
		}
	}

	/**	Close the output CSV file.
	 *
	 *	@throws	IOException		If an error occurs while closing the file.
	 */

	public void close() throws IOException
	{
		outputWriter.flush();
		outputWriter.close();
	}

	/**	Output a value.
	 *
	 *	@param	fieldValue	The field value to output.
	 *
	 *	@throws	IOException		If an error occurs while closing the file.
	 */

	public void writeValue( String fieldValue )
		throws IOException
	{
		boolean addQualifier	=
			( qualifier != 0 ) &&
			( fieldValue.indexOf( separator ) >= 0 );

		if ( addQualifier )
		{
			outputWriter.write( qualifier );
		}

		outputWriter.write( ( fieldValue == null ) ? "null" : fieldValue );

		if ( addQualifier )
		{
			outputWriter.write( qualifier );
		}
	}

	/**	Write all fields to a new line in the CSV file.
	 *
	 *	@param	fields		The array of entries for this line.
	 */

	public void writeFields( Object[] fields )
		throws IOException
	{
		int n	= fields.length;

		for ( int i = 0 ; i < n ; i++ )
		{
			if ( i > 0 ) outputWriter.write( separator );

			writeValue( fields[ i ].toString() );
		}

		outputWriter.write( Env.LINE_SEPARATOR );
	}

	/**	Write all fields to a new line in the CSV file.
	 *
	 *	@param	fields		The list of entries for this line.
	 */

	public void writeFields( List<?> fields )
		throws IOException
	{
		int n	= fields.size();

		for ( int i = 0 ; i < n ; i++ )
		{
			if ( i > 0 ) outputWriter.write( separator );

			writeValue( fields.get( i ).toString() );
		}

		outputWriter.write( Env.LINE_SEPARATOR );
	}

	/**	Write selected fields to a new line in the CSV file.
	 *
	 *	@param	fields		The list of entries for this line.
	 *	@param	indices		List of integer indices of entries to output.
	 *
	 *	<p>
	 *	The integer "indices" specify which entries to write out,
	 *	and the order.  For example, if "indices" contains the
	 *	values 3, 4, 1 then entries 3, 4, and 1 from "fields"
	 *	are written out in that order.  Bad indices result in an
	 *	empty field being output.
	 *	</p>
	 */

	public void writeFields( List<?> fields , List<Integer> indices )
		throws IOException
	{
		if ( ( indices == null ) || ( indices.size() == 0 ) )
		{
			writeFields( fields );
			return;
		}

		int nFields		= fields.size();
		int nIndices	= indices.size();
		int nWritten	= 0;

		Iterator<Integer> iterator	= indices.iterator();

		while ( iterator.hasNext() )
		{
			String fieldValue	= "";
			int j				= iterator.next().intValue();

			if ( ( j >= 0 ) && ( j < nFields ) )
			{
				fieldValue	= fields.get( j ).toString();
			}

			if ( nWritten++ > 0 ) outputWriter.write( separator );

			writeValue( fieldValue );
		}

		outputWriter.write( Env.LINE_SEPARATOR );
	}

	/**	Write selected fields to a new line in the CSV file.
	 *
	 *	@param	fields		The list of entries for this line.
	 *	@param	indices		Array of integer indices of entries to output.
	 *
	 *	<p>
	 *	The integer "indices" specify which entries to write out,
	 *	and the order.  For example, if "indices" contains the
	 *	values 3, 4, 1 then entries 3, 4, and 1 from "fields"
	 *	are written out in that order.  Bad indices result in an
	 *	empty field being output.
	 *	</p>
	 */

	public void writeFields( List<?> fields , int[] indices )
		throws IOException
	{
		if ( ( indices == null ) || ( indices.length == 0 ) )
		{
			writeFields( fields );
			return;
		}

		int nFields		= fields.size();
		int nIndices	= indices.length;
		int nWritten	= 0;

		for ( int i = 0 ; i < nIndices ; i++ )
		{
			String fieldValue	= "";
			int j				= indices[ i ];

			if ( ( j >= 0 ) && ( j < nFields ) )
			{
				fieldValue	= fields.get( j ).toString();
			}

			if ( nWritten++ > 0 ) outputWriter.write( separator );

			writeValue( fieldValue );
		}

		outputWriter.write( Env.LINE_SEPARATOR );
	}

	/**	Output an end of line.
	 *
	 *	@throws	IOException		If an error occurs while closing the file.
	 */

	public void writeEOL()
		throws IOException
	{
		outputWriter.write( Env.LINE_SEPARATOR );
	}

	/**	Output a value separator.
	 *
	 *	@throws	IOException		If an error occurs while closing the file.
	 */

	public void writeSeparator()
		throws IOException
	{
		outputWriter.write( separator );
	}

	/**	Return output writer.
	 *
	 *	@return		The output writer.
	 */

	public OutputStreamWriter getOutputWriter()
	{
		return outputWriter;
	}
}

