package edu.northwestern.at.utils.spellcheck;

import java.io.*;
import java.util.*;

/** FileBasedSpellingDictionary -- implements spelling checker dictionary loaded from a file.
 */

public class FileBasedSpellingDictionary extends HashMapSpellingDictionary
{
	/** The dictionary file name. */

	private String dictionaryFileName = "";

	/** Constructs a new FileBasedSpellingDictionary.
	 *
	 *	@param	dictionaryFileName	The dictionary file name.
	 *
	 *	@throws	IOException
	 */

	public FileBasedSpellingDictionary( String dictionaryFileName )
		throws IOException
	{
		super();
		this.dictionaryFileName = dictionaryFileName;
		try {
			read(
				new BufferedReader(
					new FileReader(dictionaryFileName)
				)
			);
		} catch (FileNotFoundException e) {
			// ignore - use empty dictionary if file doesn't exist.
		}
	}

	/** Outputs the dictionary.
	 *
	 *	@throws	IOException
	 */

	private void outputDictionary()
		throws IOException
	{
		write(
			new BufferedWriter(
				new FileWriter(dictionaryFileName)
			)
		);
	}

	/** Updates the dictionary.
	 *
	 *	@param	word		The word to add to the dictionary.
	 *
	 *	@return				True if word added successfully.
	 */

	public boolean addWord( String word )
	{
		if ( super.addWord( word ) )
		{
			try {
				outputDictionary();
			} catch (Exception e) {
				return false;
			}
			return true;
		}

		return false;
	}
}
