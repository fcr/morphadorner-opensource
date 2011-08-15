package edu.northwestern.at.utils.corpuslinguistics.inputter;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.*;
import java.text.*;

import edu.northwestern.at.utils.IsCloseableObject;
import edu.northwestern.at.utils.UnicodeReader;

/**	Text inputter which reads only the first token in each line from a URL. */

public class FirstTokenURLTextInputter
	extends URLTextInputter
	implements TextInputter
{
	/**	Loads text from a URL, keeping only first token on each line.
	 *
	 *	@param	url				URL from which to read text.
	 *	@param	encoding		Text encoding.
	 *
	 *	@throws	IOException		If an output error occurs.
	 */

	 public void loadText( URL url , String encoding )
	 	throws IOException
	 {
		BufferedReader reader	=
			new BufferedReader
			(
				new UnicodeReader
				(
					url.openStream() ,
					encoding
				)
			);

        StringBuffer loadedTextBuffer	= new StringBuffer();

		String line	= reader.readLine();

		while ( line != null )
		{
			String[] tokens	= line.split( "\t" );
			loadedTextBuffer.append( tokens[ 0 ] );
			loadedTextBuffer.append( " " );
			line = reader.readLine();
		}

		reader.close();

		loadedText	= loadedTextBuffer.toString();
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



