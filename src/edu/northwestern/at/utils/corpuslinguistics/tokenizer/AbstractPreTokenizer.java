package edu.northwestern.at.utils.corpuslinguistics.tokenizer;

/*	Please see the license information at the end of this file. */

import java.util.regex.*;
import java.util.StringTokenizer;

import edu.northwestern.at.utils.CharUtils;
import edu.northwestern.at.utils.IsCloseableObject;
import edu.northwestern.at.utils.logger.*;
import edu.northwestern.at.utils.PatternReplacer;
import edu.northwestern.at.utils.StringUtils;

/**	Default pretokenizes which prepares a string for tokenization.
 */

abstract public class AbstractPreTokenizer
	extends IsCloseableObject
	implements PreTokenizer, UsesLogger
{
	/**	Pattern to match three or more periods. */

	protected final static String periods	= "(\\.{3,})";

	/**	Pattern to match one or more asterisk. */

	protected final static String asterisks	= "([\\*]+)";

	/**	Pattern to match two or more hyphens in a row. */

	protected final static String hyphens	= "(-{2,})";

	/**	Pattern to match comma as a separator. */

	protected final static String commaSeparator	=
		"(,)([^0-9])";

	/**	Logger used for output. */

	protected Logger logger;

	/**	Pattern to match characters which are always separators.
	 *
	 *	<p>
	 *	Unicode \u25CF (BLACKCIRCLE) is the dot character which marks
	 *	character lacunae.  This is not a token separator.  Neither is
	 *	Unicode \u2022 (SOLIDCIRCLE) which was used in the old
	 *	EEBO format TCP files to mark character lacunae.
	 *	</p>
	 *
	 *	<p>
	 *	Unicode \u2011, the non-breaking hyphen, is not treated
	 *	as a token separator.
	 *	</p>
	 *
	 *	<p>
	 *	Unicode \u2032 (DEGREES_MARK) is degrees quote symbol.
	 *	Unicode \u2033 (MINUTES_MARK) is minutes quote symbol.
	 *	Unicode \u2034 (SECONDS_MARK) is seconds quote symbol.
	 *	These are not token separators.
	 *	</p>
	 *
	 *	<p>
	 *	Unicode \u2018 (LSQUOTE) is left single curly quote.
	 *	Unicode \u2019 (RSQUOTE) is right single curly quote.
	 *	These may or may not be token separators.  It is up
	 *	to the word tokenizer to decide.
	 *	</p>
	 *
	 *	<p>
	 *	Unicode \u201C (LDQUOTE) is left double curly quote.
	 *	Unicode \u201D (RDQUOTE) is right double curly quote.
	 *	These are token separators.
	 *	</p>
	 */

	protected final static String alwaysSeparators	=
		"(" +
			hyphens + "|" + periods + "|" +
			"[\\(\\)\\[\\]\\{\\}\";:/=`¶<>" +
			CharUtils.LDQUOTE +
			CharUtils.RDQUOTE +
			CharUtils.LONG_DASH +
			"\\" + CharUtils.VERTICAL_BAR +
			CharUtils.BROKEN_VERTICAL_BAR +
			CharUtils.LIGHT_VERTICAL_BAR +
			"[\\p{InGeneralPunctuation}&&[^" +
			CharUtils.SOLIDCIRCLE +
			CharUtils.DEGREES_MARK +
			CharUtils.MINUTES_MARK +
			CharUtils.SECONDS_MARK +
			CharUtils.LSQUOTE +
			CharUtils.RSQUOTE +
			CharUtils.SHORT_DASH +
		 	CharUtils.NONBREAKING_HYPHEN +
			"]]" +
		 	"\\p{InLetterlikeSymbols}" +
		 	"\\p{InMathematicalOperators}" +
		 	"\\p{InMiscellaneousTechnical}" +
		 	"[\\p{InGeometricShapes}&&[^" +
		 	CharUtils.BLACKCIRCLE +
		 	"]]" +
		 	"\\p{InMiscellaneousSymbols}" +
		 	"\\p{InDingbats}" +
			"\\p{InAlphabeticPresentationForms}" +
			"]" +
		")";

	/**	Always Separators replacer pattern. */

	protected static PatternReplacer alwaysSeparatorsReplacer	=
		new PatternReplacer( alwaysSeparators , " $1 ");

	/**	Comma separator replacer pattern. */

	protected static PatternReplacer commaSeparatorReplacer	=
		new PatternReplacer( commaSeparator , " $1 $2" );

	/**	Create a preTokenizer.
	 */

	public AbstractPreTokenizer()
	{
		logger	= new DummyLogger();
	}

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

	/**	Prepare text for tokenization.
	 *
	 *	@param	line	The text to prepare for tokenization,
	 *
	 *	@return			The pretokenized text.
	 */

	public String pretokenize( String line )
	{
								//	Replace tabs with single space.

		String result	=
			StringUtils.replaceAll( line , "\t" , " " );

								//	Put spaces around characters
								//	that are always separators.

		result			= alwaysSeparatorsReplacer.replace( result );

								//	Put spaces around all commas except
								//	those appearing before a digit, which
								//	presumably are part of a number.

		result			= commaSeparatorReplacer.replace( result );

		return result;
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



