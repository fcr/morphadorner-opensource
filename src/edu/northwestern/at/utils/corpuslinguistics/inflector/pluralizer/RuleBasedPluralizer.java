package edu.northwestern.at.utils.corpuslinguistics.inflector.pluralizer;

/*	Please see the license information in the header below. */

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.inflector.wordrule.*;

/** A {@link Pluralizer} implemented using an ordered list of {@link edu.northwestern.at.utils.corpuslinguistics.inflector.wordrule.WordRule}s.
 *
 *	<p>
 *	You may specify a fallback {@link Pluralizer} that is invoked when
 *	none of the rules match.  This allows you to override some rules of
 *	another {@link Pluralizer}.
 *	</p>
 *
 *	<p>
 *	This class preserves leading and trailing whitespace, so individual
 *	rules don't need to explicitly handle whitespace.
 *	</p>
 *
 *	<p>
 *	Case is also preserved -- that is, the output of all uppercase input
 *	is automatically uppercased, and the output of titlecase input is
 *	automatically titlecased.  This means rules can act in a
 *	case-insensitive manner.
 *	</p>
 *
 *	<p>
 *	Original code written by Tom White under the Apache v2 license.
 *	Modified by Philip R. Burns for integration into MorphAdorner.
 *	</p>
 */

public class RuleBasedPluralizer
	implements Pluralizer
{
	protected static final Pluralizer NOOP_PLURALIZER = new NoopPluralizer();

	protected List<WordRule> rules;
	protected Locale locale;
	protected Pluralizer fallbackPluralizer;

	/** Construct a pluralizer with an empty list of rules.
	 */

	public RuleBasedPluralizer()
	{
		this.rules	= ListFactory.createNewList();
		this.locale	= Locale.getDefault();
	}

	/** Construct a pluralizer with a list of rules.
	 *
	 *	@param	rules	The rules to apply, in order.
	 *	@param	locale	The locale specifying the language of the pluralizer.
	 *
	 *	<p>
	 *	A noop pluralizer is used as the fall back pluralizer when none of
	 *	the specified rules applies.
	 *	</p>
	 */

	public RuleBasedPluralizer( List<WordRule> rules , Locale locale )
	{
		this( rules , locale , NOOP_PLURALIZER );
	}

	/** Construct a pluralizer with a list of rules and a backup pluralizer.
	 *
	 *	@param	rules	The rules to apply, in order.
	 *	@param	locale	The locale specifying the language of the pluralizer.
	 *	@param	fallbackPluralizer	The pluralizer to use when no rules match.
	 *
	 *	<p>
	 *	The fall back pluralizer is invoked when none of the specified rules
	 *	applies.
	 *	</p>
	 */

	public RuleBasedPluralizer
	(
		List<WordRule> rules ,
		Locale locale ,
		Pluralizer fallbackPluralizer
	)
	{
		this.rules				= rules;
		this.locale				= locale;
		this.fallbackPluralizer	= fallbackPluralizer;
	}

	/** Get fall back pluralizer.
	 *
	 *	@return		The fall back pluralizer.
	 */

	public Pluralizer getFallbackPluralizer()
	{
		return fallbackPluralizer;
	}

	/** Set the fall back pluralizer.
	 *
	 *	@param	fallbackPluralizer	The fall back pluralizer.
	 */

	public void setFallbackPluralizer( Pluralizer fallbackPluralizer )
	{
		this.fallbackPluralizer = fallbackPluralizer;
	}

	/** Get the locale.
	 *
	 *	@return		The pluralizer locale.
	 */

	public Locale getLocale()
	{
		return locale;
	}

	/** Set the pluralizer locale.
	 *
	 *	@param	locale	The pluralizer locale.
	 */

	public void setLocale( Locale locale )
	{
		this.locale = locale;
	}

	/** Get the pluralizer rules.
	 *
	 *	@return		The pluralizer rules.
	 */

	public List<WordRule> getRules()
	{
		return rules;
	}

	/** Set the pluralizer rules.
	 *
	 *	@param	rules	The pluralizer rules.
	 */

	public void setRules( List<WordRule> rules )
	{
		this.rules = rules;
	}

	/** Pluralize a noun or pronoun.
	 *
	 *	@param	nounOrPronoun	The singular form of the noun or pronoun.
	 *
	 *	@return					The plural form of the noun or pronoun.
	 */

	public String pluralize( String nounOrPronoun )
	{
		return pluralize( nounOrPronoun , 2 );
	}

	/** Pluralize a noun or pronoun.
	 *
	 *	@param	nounOrPronoun	The singular form of the noun or pronoun.
	 *	@param	number			The number for the noun or pronoun.
	 *
	 *	@return			The form of the noun or pronoun for the specified
	 *					number.
	 */

	public String pluralize( String nounOrPronoun , int number )
	{
		if ( number == 1 )
		{
			return nounOrPronoun;
		}

		Pattern pattern = Pattern.compile( "\\A(\\s*)(.+?)(\\s*)\\Z" );
		Matcher matcher = pattern.matcher( nounOrPronoun );

		if ( matcher.matches() )
		{
			String pre			= matcher.group( 1 );
			String trimmedWord	= matcher.group( 2 );
			String post			= matcher.group( 3 );
			String plural		= pluralizeInternal( trimmedWord );

			if ( plural == null )
			{
				return fallbackPluralizer.pluralize( nounOrPronoun , number );
			}

			return pre + postProcess( trimmedWord , plural ) + post;
		}

		return nounOrPronoun;
	}

	/** Apply list of rules to a noun or pronoun.
	 *
	 *	@param	nounOrPronoun	Singular noun or pronoun.
	 *
	 *	@return		Plural form of the noun or pronoun, or <code>null</code>
	 *				when no rule matches.
	 */

	protected String pluralizeInternal( String nounOrPronoun )
	{
		for ( WordRule rule : rules )
		{
			if ( rule.applies( nounOrPronoun ) )
			{
				return rule.apply( nounOrPronoun );
			}
		}

		return null;
	}

	/** Fix case of pluralized word.
	 *
	 *	@param	trimmedWord		The input word, with leading and trailing
	 *							whitespace removed.
	 *	@param	pluralizedWord	The pluralized word.
	 *
	 *	@return					The <code>pluralizedWord</code> after
	 *							processing.
	 *
	 *	<p>
	 *	If <code>trimmedWord</code> is all uppercase, then
	 *	<code>pluralizedWord</code> is uppercased.
	 *	If <code>trimmedWord</code> is titlecase, then
	 *	<code>pluralizedWord</code> is titlecased.
	 *	</p>
	 */

	protected String postProcess
	(
		String trimmedWord ,
		String pluralizedWord
	)
	{
		if ( trimmedWord.matches( "^\\p{Lu}+$" ) )
		{
			return pluralizedWord.toUpperCase( locale );
		}
		else if ( trimmedWord.matches( "^\\p{Lu}.*" ) )
		{
			return pluralizedWord.substring( 0 , 1 ).toUpperCase( locale ) +
				pluralizedWord.substring( 1 );
		}

		return pluralizedWord;
	}
}

