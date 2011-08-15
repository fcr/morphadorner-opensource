package edu.northwestern.at.utils.corpuslinguistics.inflector.wordrule;

/*	Please see the license information in the header below. */

import java.util.regex.Matcher;

/**	A rule specified using a regular expression and a replacement string.
 *
 *	<p>
 *	Original code written by Tom White under the Apache v2 license.
 *	Modified by Philip R. Burns for integration into MorphAdorner.
 *	</p>
 */

public class RegexReplacementRule
	extends AbstractRegexReplacementRule
{
	protected final String replacement;

	/**	Construct a rule using the given regular expression and replacement string.
	 *
	 *	@param	regex			The regular expression used to match words.
	 *	@param	replacement		The replacement string.
	 *
	 *	<p>
	 * 	The replacement string may contain references to subsequences
	 *	captured matching.  See {@link Matcher#appendReplacement}.
	 *	</p>
	 */

	public RegexReplacementRule( String regex , String replacement )
	{
		super( regex );
		this.replacement = replacement;
	}

	@Override
	public String replace( Matcher matcher )
	{
		return matcher.replaceFirst( replacement );
	}
}

