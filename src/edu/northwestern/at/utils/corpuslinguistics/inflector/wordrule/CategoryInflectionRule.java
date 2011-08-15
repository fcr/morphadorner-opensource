package edu.northwestern.at.utils.corpuslinguistics.inflector.wordrule;

/*	Please see the license information in the header below. */

/**	A rule for specifying an inflection using suffixes that only applies to
 *	a subset of words with those suffixes (a category).
 *
 *	<p>
 *	Original code written by Tom White under the Apache v2 license.
 *	Modified by Philip R. Burns for integration into MorphAdorner.
 *	</p>
 */

public class CategoryInflectionRule
	extends SuffixInflectionRule
{
	protected final String regex;

	/**	Construct a rule for <code>words</code> with suffix
	 *	<code>singularSuffix</code> which becomes <code>pluralSuffix</code>
	 *	in the plural.
	 *
	 *	@param	words			The set of words which define this category.
	 *	@param singularSuffix	The singular suffix, starting with a
	 *							"-" character
	 *	@param pluralSuffix		The plural suffix, starting with a "-"
	 *							character.
	 */

	public CategoryInflectionRule
	(
		String[] words ,
		String singularSuffix ,
		String pluralSuffix
	)
	{
		super( singularSuffix , pluralSuffix );

		this.regex =
			"(?i)" + AbstractRegexReplacementRule.disjunction( words );
	}

	/**	Tests to see if this rule applies for the given word.
	 *
	 *	@param	word	The word that is being tested
	 *
	 *	@return			<code>true</code> if this rule should be applied,
	 * 					<code>false</code> otherwise
	 */

	@Override
	public boolean applies( String word )
	{
		return word.matches( regex );
	}
}

