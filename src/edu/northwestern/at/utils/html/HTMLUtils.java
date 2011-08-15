package edu.northwestern.at.utils.html;

import edu.northwestern.at.utils.StringUtils;

/*	Please see the license information at the end of this file. */

/**	HTML utilities.
 *
 *	<p>
 *	This static class provides various utility methods for manipulating
 *	html and xml.
 *	</p>
 */

public class HTMLUtils
{
	/**	Simple-minded test for determining if string is HTML/XML tagged .
	 *
	 *	@param	text	The text to check.
	 *
	 *	@return			True if text appears to be tagged XML/HTML.
	 */

	public static boolean isHTMLTaggedText( String text )
	{
		boolean result	= false;

		if ( text != null )
		{
			result	=
				text.startsWith( "<?xml " ) ||
				(
					(	( text.indexOf( "<html" ) != -1 ) ||
						( text.indexOf( "<HTML" ) != -1 ) ) &&
					(	( text.indexOf( "</html" ) != -1 ) ||
						( text.indexOf( "</HTML" ) != -1 ) )
				);
		}

		return result;
	}

	/**	Simple-minded tag stripper for HTML/XML .
	 *
	 *	@param	taggedText	Text from which to strip tags.
	 *
	 *	@return				Text with tagged text removed.
	 */

	public static String stripHTMLTags( String taggedText )
	{
		if ( taggedText == null ) return null;

		taggedText	=
			taggedText.replaceAll( "(<script.+?</script>)+" , "" );

		taggedText	=
			taggedText.replaceAll( "(<style.+?</style>)+" , "" );

		taggedText	=
			taggedText.replaceAll( "(<applet.+?</applet>)+" , "" );

		taggedText	=
			taggedText.replaceAll( "(<object.+?</object>)+" , "" );

		StringBuffer strippedText	= new StringBuffer( "" );

		int tagCount				= 0;

		for ( int i = 0 ; i < taggedText.length() ; i++ )
		{
			if ( taggedText.charAt( i ) == '<' )
			{
				tagCount++;
			}
			else if ( taggedText.charAt( i ) == '>' )
			{
				tagCount--;

				if ( tagCount < 0 ) tagCount = 0;
			}
			else if ( tagCount == 0 )
				strippedText.append( taggedText.charAt( i ) );
		}

		return unescapeHTML( strippedText.toString() );
	}

	public static String escapeHTML( String s )
	{
		s	= StringUtils.replaceAll( s , "&" , "&amp;" );
		s	= StringUtils.replaceAll( s , "<" , "&lt;" );
		s	= StringUtils.replaceAll( s , ">" , "&gt;" );
		s	= StringUtils.replaceAll( s , "\"" , "&quot;" );
		s	= StringUtils.replaceAll( s , "'" , "&apos;" );

		return s;
	}

	public static String unescapeHTML( String s )
	{
		s	= StringUtils.replaceAll( s , "&amp;" , "&" );
		s	= StringUtils.replaceAll( s , "&lt;" , "<" );
		s	= StringUtils.replaceAll( s , "&gt;" , ">" );
		s	= StringUtils.replaceAll( s , "&quot;" , "\"" );
		s	= StringUtils.replaceAll( s , "&apos;" , "'" );
		s	= StringUtils.replaceAll( s , "&nbsp;" , " " );

		return s;
	}

	/** Don't allow instantiation, do allow overrides. */

	protected HTMLUtils()
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



