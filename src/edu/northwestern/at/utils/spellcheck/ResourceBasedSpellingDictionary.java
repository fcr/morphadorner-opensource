package edu.northwestern.at.utils.spellcheck;

import java.io.*;
import java.util.*;
import edu.northwestern.at.utils.UnicodeReader;

/** ResourceBasedSpellingDictionary -- implements spelling checker dictionary loaded from a resource.
 */

public class ResourceBasedSpellingDictionary extends HashMapSpellingDictionary
{
	/** Create ResourceBasedSpellingDictionary given resource path.
	 *
	 *	@param	resourcePath	The resource path to the dictionary.
	 */

	public ResourceBasedSpellingDictionary( String resourcePath )
		throws IOException
	{
		super();

		read
		(
			new BufferedReader(
				new UnicodeReader(
					this.getClass().getClassLoader().getResourceAsStream( resourcePath )
				)
			)
		);
	}
}

