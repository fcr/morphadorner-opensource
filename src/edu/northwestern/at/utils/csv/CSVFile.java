package edu.northwestern.at.utils.csv;

/*	Please see the license information at the end of this file. */

/**	CSVFile handles files containing delimiter separated entries.
 *
 *	<p>
 *	This is the abstract base clase used by {@link CSVFileReader} and
 *	{@link CSVFileWriter}.
 *	</p>
 *
 *	<p>
 *	The following simple example converts one CSV file into another
 *	that uses a different notation for field separator and text qualifier.
 *	</p>
 *
 *	<p>
 *	<pre>
 *	import java.util.*;
 *	import java.io.*;
 *
 *	public class CSVFileExample
 *	{
 *		public static void main( String[] args )
 *			throws FileNotFoundException,IOException
 *		{
 *			CSVFileReader in = new CSVFileReader("csv_in.txt", ';', '"');
 * 			CSVFileWriter out = new CSVFileWriter("csv_out.txt", ',', '\'');
 *
 *			List fields = in.readFields();
 *
 *			while( fields != null )
 *			{
 *				out.writeFields( fields );
 *				fields = in.readFields();
 *			}
 *
 *			in.close();
 *			out.close();
 *		}
 *	}
 *	</pre>
 *	</p>
 *
 *	@author	 Fabrizio Fazzino
 *
 *	<p>
 *	Modified by Philip R. Burns at Northwestern University.
 *	</p>
 */

abstract public class CSVFile
{
	/**	The default char used as field separator.
	 */

	protected static final char DEFAULT_SEPARATOR = ',';

	/**	The default char used as text qualifier
	 */

	protected static final char DEFAULT_QUALIFIER = '"';

	/**	The current char used as field separator.
	 */

	protected char separator;

	/**	The current char used as text qualifier.
	 */

	protected char qualifier;

	/**	CSVFile constructor with the default field separator and text qualifier.
	 */

	public CSVFile()
	{
		this( DEFAULT_SEPARATOR , DEFAULT_QUALIFIER );
	}

	/**	CSVFile constructor with given field separator.
	 *
	 *	@param	separator	The field separator.
	 */

	public CSVFile( char separator )
	{
		this( separator , DEFAULT_QUALIFIER );
	}

	/**	CSVFile constructor with given field separator and text qualifier.
	 *
	 *	@param	separator	The field separator to used.
	 *	@param	qualifier	The text qualifier to be use.
	 */

	public CSVFile( char separator , char qualifier )
	{
		setSeparator( separator );
		setQualifier( qualifier );
	}

	/**	Set the field separator.
	 *
	 * @param	separator	The field separator to use.
	 */

	public void setSeparator( char separator )
	{
		this.separator = separator;
	}

	/**	Set the text qualifier.
	 *
	 * @param	qualifier	The new text qualifier to use.
	 */

	public void setQualifier( char qualifier )
	{
		this.qualifier = qualifier;
	}

	/**	Get the current field separator.
	 *
	 *	@return		The char containing the current field separator
	 */

	public char getSeparator()
	{
		return separator;
	}

	/**	Get the current text qualifier.
	 *
	 *	@return		The text qualifier character.
	 */

	public char getQualifier()
	{
		return qualifier;
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



