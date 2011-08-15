package edu.northwestern.at.utils.corpuslinguistics.inflector.wordrule;

/*	Please see the license information in the header below. */

import edu.northwestern.at.utils.corpuslinguistics.inflector.wordrule.WordRule;

/**	A rule for specifying an inflection using suffixes.
 *
 *	<p>
 *	For example, the English nouns which have the suffix -y, generally
 *	change the suffix to -ies in the plural. Such a rule would
 *	be expressed as <code>new SuffixInflectionRule("-y", "-ies")</code>.
 *	</p>
 *
 *	<p>
 *	Original code written by Tom White under the Apache v2 license.
 *	Modified by Philip R. Burns for integration into MorphAdorner.
 *	</p>
 */

public class SuffixInflectionRule
	implements WordRule
{
	protected final String regex;
	protected final String singularSuffix;
	protected final String pluralSuffix;

	/**	Construct a rule for a suffix <code>singularSuffix</code> which
	 * becomes <code>pluralSuffix</code> in the plural.
	 *
	 *	@param	singularSuffix		The singular suffix, starting with a
	 *								"-" character.
	 *	@param	pluralSuffix		The plural suffix, starting with a
	 *								"-" character.
	 */

	public SuffixInflectionRule
	(
		String singularSuffix ,
		String pluralSuffix
	)
	{
		this( singularSuffix , singularSuffix , pluralSuffix );
	}

	/**	Construct a rule for words with suffix <code>suffix</code>, where
	 *	<code>singularSuffix</code> becomes <code>pluralSuffix</code>
	 *	in the plural.
	 *
	 *	@param	suffix			The suffix, starting with a "-" character,
	 *							which the end of the word must match.
	 *							Regular expression patterns may be used.
	 *
	 *	@param	singularSuffix	The singular suffix, starting with a "-"
	 *							character.
	 *							The <code>suffix</code> must end with
	 *							<code>singularSuffix</code>.
	 *
	 *	@param	pluralSuffix	The plural suffix, starting with a "-"
	 *							character.
	 */

	public SuffixInflectionRule
	(
		String suffix ,
		String singularSuffix ,
		String pluralSuffix
	)
	{
		// TODO: check suffix ends with singularSuffix?

		this.regex			= "(?i).*" + suffix.substring( 1 ) + "$";
		this.singularSuffix	= singularSuffix;
		this.pluralSuffix	= pluralSuffix;
	}

	/**	Tests to see if this rule applies for the given word.
	 *
	 *	@param	word	The word that is being tested
	 *
	 *	@return			<code>true</code> if this rule should be applied,
	 * 					<code>false</code> otherwise
	 */

	public boolean applies( String word )
	{
		return word.matches( regex );
	}

	/**	Applies this rule to the word, and transforming it into a new form.
	 *
	 *	@param	word	The word to which to apply this rule.
	 *
	 *	@return			The transformed word.
	 */

	public String apply( String word )
	{
		int i = word.lastIndexOf( singularSuffix.substring( 1 ) );

		// TODO: check i
		// TODO: make case insensitive
		return word.substring( 0 , i ) + pluralSuffix.substring( 1 );
	}
}

