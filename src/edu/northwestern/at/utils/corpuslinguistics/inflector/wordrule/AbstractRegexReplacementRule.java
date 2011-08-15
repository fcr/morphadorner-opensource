package edu.northwestern.at.utils.corpuslinguistics.inflector.wordrule;

/*	Please see the license information in the header below. */

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**	An abstract rule specified using a regular expression and replacement.
 *
 *	<p>
 *	Subclasses must implement {@link #replace} to perform the actual
 *	replacement.
 *	</p>
 *
 *	<p>
 *	Original code written by Tom White under the Apache v2 license.
 *	Modified by Philip R. Burns for integration into MorphAdorner.
 *	</p>
 */

public abstract class AbstractRegexReplacementRule
	implements WordRule
{
	protected final Pattern pattern;

	/**	Construct a rule using the given regular expression.
	 *
	 *	@param	regex	The regular expression used to match words.
	 *					Match information is available to subclasses in the
	 *					{@link #replace} method.
	 */

	public AbstractRegexReplacementRule( String regex )
	{
		this.pattern = Pattern.compile( regex );
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
		return pattern.matcher( word ).matches();
	}

	/**	Applies this rule to the word, and transforming it into a new form.
	 *
	 *	@param	word	The word to which to apply this rule.
	 *
	 *	@return			The transformed word.
	 *
	 *	@throws			IllegalArgumentException
	 *						when the word does not match the pattern.
	 */

	public String apply( String word )
	{
		Matcher matcher = pattern.matcher( word );

		if ( !matcher.matches() )
		{
			throw new IllegalArgumentException(
				"Word '" + word + "' does not match regex: " +
				pattern.pattern() );
		}

		return replace( matcher );
	}

	/**	Form the disjunction of the given regular expression patterns.
	 *
	 *	@param	patterns	An array of regular expression patterns.
	 *
	 *	@return				A pattern that matches if any of the input
	 *						patterns match.
	 *
	 *	<p>
	 *	For example, if "patterns" contains "a" and "b", then the
	 *	disjunction is "(a|b)", that is, "a or b".
	 *	</p>
	 */

	public static String disjunction( String[] patterns )
	{
		String regex = "";

		for ( int i = 0; i < patterns.length; i++ )
		{
			regex += patterns[ i ];
			if ( i < patterns.length - 1 )
			{
				regex += "|";
			}
		}

		return "(?:" + regex + ")";
	}

	/**	Form the disjunction of the given regular expression patterns.
	 *
	 *	@param	patterns	A set of regular expression patterns.
	 *
	 *	@return				A pattern that matches if any of the input
	 *						patterns match.
	 *
	 *	<p>
	 *	For example, if "patterns" contains "a" and "b", then the
	 *	disjunction is "(a|b)", that is, "a or b".
	 *	</p>
	 */

	public static String disjunction( Set<String> patterns )
	{
		return disjunction
		(
			patterns.toArray( new String[ patterns.size() ] )
		);
	}

	/**	Use the state in the given {@link Matcher} to perform a replacement.
	 *
	 *	@param	matcher		The matcher used to match the word.
	 *
	 *	@return				The transformed word.
	 */

	public abstract String replace( Matcher matcher );
}

