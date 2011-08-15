package edu.northwestern.at.utils.xml;

/*	Please see the license information at the end of this file. */

/**	XML utilities.
 */

public class XMLUtils
{
	/**	Check that string contains valid XML characters.
	 *
	 *	@param	xml		The XML text to check.
	 *
	 *	@return			true if the string contains only characters
	 *					valid in XML, false otherwise.
	 */

	public static boolean iSValidXMLText( String xml )
	{
		boolean result	= true;

		if( xml != null )
		{
			result	=
				xml.matches
				(
								//# ASCII

					"^([\\x09\\x0A\\x0D\\x20-\\x7E]|" +

								//# non-overlong 2-byte

					"[\\xC2-\\xDF][\\x80-\\xBF]|" +

								//# excluding overlongs

					"\\xE0[\\xA0-\\xBF][\\x80-\\xBF]|" +

								//# straight 3-byte

					"[\\xE1-\\xEC\\xEE\\xEF][\\x80-\\xBF]{2}|" +

								//# excluding surrogates

					"\\xED[\\x80-\\x9F][\\x80-\\xBF]|" +

 								//# planes 1-3

					"\\xF0[\\x90-\\xBF][\\x80-\\xBF]{2}|" +

								//# planes 4-15

					"[\\xF1-\\xF3][\\x80-\\xBF]{3}|" +

								//# plane 16

					"\\xF4[\\x80-\\x8F][\\x80-\\xBF]{2})*$"
				);
		}

		return result;
	}

	/**	Strips invalid characters from XML text.
	 *
	 *	@param	xml		XML text to check for invalid characters.
	 *	@param	rep		Replacement string for invalid characters.
	 *
	 *	@return		    XML text with invalid characters removed.
	 */

	public String stripBadXMLCharacters( String xml , String rep )
	{
		StringBuffer result	= new StringBuffer();

		int codePoint;

		if ( ( xml != null ) && ( xml.length() > 0 ) )
		{
			int i = 0;

			while ( i < xml.length() )
			{
	    		codePoint	= xml.codePointAt( i );

				if	(	( codePoint == 0x9 ) ||
						( codePoint == 0xA ) ||
						( codePoint == 0xD ) ||
						( ( codePoint >= 0x20 ) && ( codePoint <= 0xD7FF ) ) ||
						( ( codePoint >= 0xE000 ) && ( codePoint <= 0xFFFD ) ) ||
						( ( codePoint >= 0x10000 ) && ( codePoint <= 0x10FFFF ) )
					)
				{
					result.append( Character.toChars( codePoint ) );
				}
				else
				{
					result.append( rep );
				}

				i += Character.charCount( codePoint );
			}
		}

		return result.toString();
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



