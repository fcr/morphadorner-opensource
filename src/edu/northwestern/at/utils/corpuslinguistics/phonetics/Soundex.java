package edu.northwestern.at.utils.corpuslinguistics.phonetics;

/**	Soundex: Implements the Soundex Algorithm.
 *
 *	<p>
 *	Soundex hashes words to a smaller space using a simple model
 *	which approximates the sound of the word as produced by a native
 *	American English speaker.  The hash is a four (usually) character
 *	string in which the first character is an uppercase letter and the
 *	remaining characters are digits.  Soundex was originally
 *	intended only for encoding proper last names, but occasionally
 *	finds other uses as well.  The Soundex algorithm was devised and
 *	patented by Margaret K. Odell and Robert C. Russell in 1918.
 *	</p>
 */

public class Soundex
{
	/*	The Soundex mapping from letters to digits. */

	public static final char[] US_ENGLISH_SOUNDEX_MAPPING	=
		"01230120022455012623010202".toCharArray();
//		 ABCDEFGHIJKLMNOPQRSTUVWXYZ

	/*	Maximum length of a Soundex code.  4 is the usual value. */

	public static final int MAXSOUNDEXLENGTH	= 4;

	/**	Get Soundex code for a string.
	 *
	 *	@param	s	The string for which the soundex code is desired.
	 *
	 *	@return		The Soundex code for "s".  Returns an empty string
	 *				when the Soundex code cannot be found.  In particular,
	 *				a Soundex code cannot be found if the first
	 *				character in "s" is not a letter (a-z, A-Z).
	 */

	public static String soundex( String s )
	{
								//	If the input string is null or empty,
								//	we cannot find a Soundex code.

		if ( ( s == null ) || ( s.length() == 0 ) ) return "";

								//	String buffer holds generated
								//	Soundex code.

		StringBuffer result	= new StringBuffer();

								//	Convert input string to upper case.

		String sUpperCase	= s.toUpperCase();

								//	Tracks the previous character in
								//	the string being processed.

		char previousC			= '0';

								//	The current character being processsed.
		char c;
								//	If the first character of the
								//	string to encode is not a letter,
								//	we cannot find a Soundex code.

		c	= sUpperCase.charAt( 0 );

		if ( ( c < 'A' ) || ( c > 'Z' ) ) return "";

								//	First letter is appended to result
								// unchanged except for case.

		result.append( c );
								//	Convert remaining characters using
								//	the Soundex mapped values until
								//	a Soundex code of MAXSOUNDLENGTH
								//	is reached, or the input string
								//	is exhausted.

		for	(	int i = 1 ;
				( i < sUpperCase.length() ) &&
				( result.length() < MAXSOUNDEXLENGTH ) ;
				i++
			)
		{
								//	Pick up the next character in the
								//	input string.

			c	= sUpperCase.charAt( i );

								//	Ignore this character if is not
								//	a letter or is the same as
								//	the previous character.

			if ( ( c >='A' ) && ( c <= 'Z' ) && ( c != previousC ) )
			{
								//	Set the previous character to this
								//	character.

				previousC		= c;

								//	Get the Soundex value for this
								//	character.

				char mappedC	= US_ENGLISH_SOUNDEX_MAPPING[ c - 'A' ];

								//	Append the Soundex map value
								//	to the Soundex code if the map
								//	character is not a '0'.

				if ( mappedC != '0' )
				{
					result.append( mappedC );
				}
			}
		}
									//	Pad Soundex code with trailing
									//	'0' characters to bring the length
									//	up to MAXSOUNDEXLENGTH.

		if ( result.length() > 0 )
		{
			for ( int i = result.length() ; i < MAXSOUNDEXLENGTH ; i++ )
			{
				result.append( '0' );
			}
		}
									//	Return resulting Soundex string.

		return result.toString();
	}
}

