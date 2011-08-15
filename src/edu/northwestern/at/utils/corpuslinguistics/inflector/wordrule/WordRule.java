package edu.northwestern.at.utils.corpuslinguistics.inflector.wordrule;

/*	Please see the license information in the header below. */

/**	Interface for a rule to transform a word.
 *
 *	<p>
 *	Original code written by Tom White under the Apache v2 license.
 *	Modified by Philip R. Burns for integration into MorphAdorner.
 *	</p>
 */

public interface WordRule
{
	/**	Tests to see if this rule applies for the given word.
	 *
	 *	@param	word	The word that is being tested
	 *
	 *	@return			<code>true</code> if this rule should be applied,
	 * 					<code>false</code> otherwise
	 */

	public boolean applies( String word );

	/**	Applies this rule to the word, and transforming it into a new form.
	 *
	 *	@param	word	The word to which to apply this rule.
	 *
	 *	@return			The transformed word.
	 */

	public String apply( String word );
}

