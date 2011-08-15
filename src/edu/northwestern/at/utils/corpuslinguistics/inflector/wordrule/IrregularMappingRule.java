package edu.northwestern.at.utils.corpuslinguistics.inflector.wordrule;

/*	Please see the license information in the header below. */

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import edu.northwestern.at.utils.MapFactory;

/**	A rule for specifying an irregular inflection using a combination of a
 *	map of singular to plural forms and a regular expression replacement.
 *
 *	<p>
 *	Subclasses can implement {@link #replace} to perform the actual
 *	replacement, which by default uses the map to substitute a plural form
 *	for the corresponding singular form found in group 0 of the regular
 *	expression match.
 *	</p>
 *
 *	<p>
 *	Original code written by Tom White under the Apache v2 license.
 *	Modified by Philip R. Burns for integration into MorphAdorner.
 *	</p>
 */

public class IrregularMappingRule
	extends AbstractRegexReplacementRule
{
	protected final Map<String , String> mappings;

	/**	Construct a rule using a regular expression and irregular forms map.
	 *
	 *	@param	wordMappings	The map of singular to plural forms.
	 *	@param	regex			The regular expression used to match words.
	 *							Match information is available to subclasses
	 *							in the {@link #replace} method.
	 */

	public IrregularMappingRule
	(
		Map<String , String> wordMappings ,
		String regex
	)
	{
		super( regex );
		this.mappings = wordMappings;
	}

	@Override
	public String replace( Matcher m )
	{
		return mappings.get( m.group( 0 ).toLowerCase() );
	}

	/**	Convert an array of String array mapping pairs into a map.
	 *
	 *	@param	wordMappings	Two-dimensional array of strings.
	 *							First entry is singular form,
	 *							second entry is plural form.
	 *
	 *	@return					A map of singular to plural forms.
	 */

	public static Map<String , String> toMap( String[][] wordMappings )
	{
		Map<String , String> mappings = MapFactory.createNewMap();

		for ( int i = 0; i < wordMappings.length; i++ )
		{
			String singular	= wordMappings[ i ][ 0 ];
			String plural	= wordMappings[ i ][ 1 ];

			mappings.put( singular , plural );
		}

		return mappings;
	}
}

