package edu.northwestern.at.utils;

import java.io.PrintWriter;
import java.util.*;

/**
 * <p>
 * <code>TernaryTrie</code> is an implementation of a ternary tree.
 * Methods are provided for inserting strings and searching for strings.
 * The algorithms in this class are all recursive, and have not been
 * optimized for any particular purpose.
 * Data which is inserted is not sorted before insertion, however data
 * can be inserted beginning with the median of the supplied data.
 * </p>
 *
 * @author  <a href="mailto:dfisher@vt.edu">Daniel Fisher</a>
 * @version $Revision: 2134 $
 * $Date: 2005-03-28 14:48:04 -0500 (Mon, 28 Mar 2005) $
 */

public class TernaryTrie implements TaggedStrings
{
	/** root node of the ternary tree */

	protected TernaryTrieNode root;

	/** Count of nodes in tree. */

	protected int nodeCount = 0;

	/**	Maximum key length. */

	protected int maxKeyLength	= 0;

	/**	Construct empty ternary trie.
	 */

	public TernaryTrie()
	{
	}

	/**	Construct a ternary trie from keys and values in a map.
	 *
	 *	@param	stringsMap			The map whose keys and values are to be
	 *								added to the trie.
	 *
	 *	@param	addValuesAsKeys		True to add each value as a key
	 *								as well.
	 */

	public TernaryTrie
	(
		Map<String, String> stringsMap ,
		boolean addValuesAsKeys
	)
	{
		for ( String s : stringsMap.keySet() )
		{
			String s2	= stringsMap.get( s );

			put( s , s2 );

			if ( addValuesAsKeys )
			{
				put( s2 , s2 );
			}
		}
	}

	/**	Construct a ternary trie from values in a set.
	 *
	 *	@param	stringsSet		The set whose values are to be added
	 *							to the trie.
	 */

	public TernaryTrie( Set<String> stringsSet )
	{
		for ( String s : stringsSet )
		{
			put( s , s );
		}
	}

	/**	Construct a ternary trie from values in a list.
	 *
	 *	@param	stringsList		The list whose values are to be added
	 *							to the trie.
	 */

	public TernaryTrie( List<String> stringsList )
	{
		for ( String s : stringsList )
		{
			put( s , s );
		}
	}

	/**	Construct a ternary trie from values in a tagged strings list.
	 *
	 *	@param	stringsList			The tagged strings list whose values
	 *								are to be added to the trie.
	 *
	 *	@param	addValuesAsKeys		True to add each value as a key
	 *								as well.
	 */

	public TernaryTrie
	(
		TaggedStrings stringsList ,
		boolean addValuesAsKeys
	)
	{
		Iterator<String> iterator	=
			stringsList.getAllStrings().iterator();

		while ( iterator.hasNext() )
		{
			String s	= iterator.next();
			String s2	= stringsList.getTag( s );

			put( s , s2 );

			if ( addValuesAsKeys )
			{
				put( s2 , s2 );
			}
		}
	}

	/**	Add word and associated value to trie.
	 *
	 *	@param	word	String to insert.
	 *	@param	value	Object value.
	 *
	 *	@return			Previous value, if any, for word.
	 */

	public Object put( String word , Object value )
	{
		Object result	= null;

		if ( word != null )
		{
			TernaryTrieNode wordNode = findNode( root , word , 0 );

			if ( wordNode != null )
			{
				result	= wordNode.getValue();

				wordNode.setValue( value );
			}
			else
			{
				root	= insertNode( root , word , 0 , value );
			}
		}

		return result;
	}

	/**	Get associated value for word.
	 *
	 *	@param	word	String whose associated value we want.
	 *
	 *	@return			Associated value.
	 */

	public Object get( String word )
	{
		Object result	= null;

		if ( word != null )
		{
			TernaryTrieNode wordNode = findNode( root , word , 0 );

			if ( wordNode != null )
			{
				result	= wordNode.getValue();
			}
		}

		return result;
	}

	/**	Check if trie contains a specified key.
	 *
	 *	@param	word	The key to look up.
	 *
	 *	@return			true if trie contains specified key.
	 */

	public boolean containsKey( String word )
	{
		return searchNode( root , word , 0 );
	}

	/**
	 * <p>
	 * This will return an array of strings which partially match the supplied
	 * word.
	 * word should be of the format '.e.e.e'
	 * Where the '.' character represents any valid character.
	 * Possible results from this query include: Helene, delete, or severe
	 * Note that no substring matching occurs, results only include strings of
	 * the same length.
	 * If the supplied word does not contain the '.' character, then a regular
	 * search is preformed.
	 * </p>
	 *
	 * @param word <code>String</code> to search for
	 * @return <code>String[]</code> - of matching words
	 */

	public List<String> partialSearch( String word )
	{
		List<String> list	= ListFactory.createNewList();

		return
			partialSearchNode(
				root , list , "" , word , 0 );
	}

	public List<String> prefixSearch( String prefix )
	{
		List<String> matches	= null;

		if ( prefix != null )
		{
			String prefixPattern	=
				prefix +
					StringUtils.dupl( "." , maxKeyLength - prefix.length() );

			List<String> list	= ListFactory.createNewList();

			matches	=
				prefixSearchNode
				(
					root ,
					list ,
					"" ,
					prefix ,
					prefixPattern ,
					0
				);
		}

		return matches;
    }

	protected List<String> prefixSearchNode
	(
		TernaryTrieNode node ,
		List<String> matches ,
		String match ,
		String prefix ,
		String prefixPattern ,
		int index
	)
	{
		if ( ( node != null ) && ( index < prefixPattern.length() ) )
		{
			char c		= prefixPattern.charAt( index );
			char split	= node.getSplitChar();

			if ( ( c == '.' ) || ( c < split ) )
			{
				matches	=
					prefixSearchNode
					(
						node.getLokid() ,
						matches ,
						match ,
						prefix ,
						prefixPattern ,
						index
					);
			}

			if ( ( c == '.' ) || ( c == split ) )
			{
				String partialWord	= match + split;

				if	( node.isEndOfWord() &&
						( partialWord.startsWith( prefix ) ) )
				{
					matches.add( partialWord );
				}

				matches =
					prefixSearchNode
					(
						node.getEqkid() ,
						matches ,
						partialWord ,
						prefix ,
						prefixPattern ,
						index + 1
					);
			}

			if ( ( c == '.' ) || ( c > split ) )
			{
				matches =
					prefixSearchNode
					(
						node.getHikid() ,
						matches ,
						match ,
						prefix ,
						prefixPattern ,
						index
					);
			}
		}

		return matches;
	}

	/**
	 * <p>
	 * This will return an array of strings which are near to the supplied
	 * word by the supplied distance.
	 * For the query nearSearch("fisher", 2):
	 * Possible results include: cipher, either, fishery, kosher, sister.
	 * If the supplied distance is not > 0, then a regular
	 * search is preformed.
	 * </p>
	 *
	 * @param word <code>String</code> to search for
	 * @param distance <code>int</code> for valid match
	 * @return <code>String[]</code> - of matching words
	 */

	public List<String> nearSearch( String word , int distance )
	{
		List<String> list	= ListFactory.createNewList();

		return
			nearSearchNode
			(
				root ,
				distance ,
				list ,
				"" ,
				word ,
				0
			);
	}

	/**
	 * <p>
	 * This will return an array of all the words in this
	 * <code>TernaryTrie</code>.
	 * This is a very expensive operation, every node in the tree is traversed.
	 * </p>
	 *
	 * @return <code>String[]</code> - of words
	 */

	public List<String> getWords()
	{
		List<String> list	= ListFactory.createNewList();

		return traverseNode( root, "", list );
	}

	/**
	 * <p>
	 * This will recursively insert a word into the <code>TernaryTrie</code>
	 * one node at a time beginning at the supplied node.
	 * </p>
	 *
	 * @param node <code>TernaryTrieNode</code> to put character in
	 * @param word <code>String</code> to be inserted
	 * @param index <code>int</code> of character in word
	 * @return <code>TernaryTrieNode</code> - to insert
	 */

	protected TernaryTrieNode insertNode
	(
		TernaryTrieNode node,
		final String word,
		final int index,
		final Object value
	)
	{
		if (index < word.length())
		{
			final char c = word.charAt(index);

			if (node == null)
			{
				node = new TernaryTrieNode(c);
			}

			final char split = node.getSplitChar();

			if (c < split)
			{
				node.setLokid(insertNode(node.getLokid(), word, index, value));
			}
			else if (c == split)
			{
				if (index == word.length() - 1)
				{
					node.setEndOfWord(true);
					node.setValue(value);
					nodeCount++;
					maxKeyLength	=
						Math.max( maxKeyLength , word.length() );
				}
				node.setEqkid(insertNode(node.getEqkid(), word, index + 1, value));
			}
			else
			{
				node.setHikid(insertNode(node.getHikid(), word, index, value));
			}
		}
		return node;
	}

	/**
	 * <p>
	 * This will recursively search for a word in the <code>TernaryTrie</code>
	 * one node at a time beginning at the supplied node.
	 * </p>
	 *
	 * @param node <code>TernaryTrieNode</code> to search in
	 * @param word <code>String</code> to search for
	 * @param index <code>int</code> of character in word
	 * @return <code>boolean</code> - whether or not word was found
	 */
	protected boolean searchNode(final TernaryTrieNode node,
		final String word,
		final int index)
	{
		boolean success = false;

		if (node != null && index < word.length())
		{
			final char c = word.charAt(index);
			final char split = node.getSplitChar();

			if (c < split)
			{
				return searchNode(node.getLokid(), word, index);
			}
			else if (c > split)
			{
				return searchNode(node.getHikid(), word, index);
			}
			else
			{
				if (index == word.length() - 1)
				{
					if (node.isEndOfWord())
					{
						success = true;
					}
				}
				else
				{
					return searchNode(node.getEqkid(), word, index + 1);
				}
			}
		}
		return success;
	}

	protected TernaryTrieNode findNode
	(
		final TernaryTrieNode node ,
		final String word ,
		final int index
	)
	{
		if (node != null && index < word.length())
		{
			final char c = word.charAt(index);
			final char split = node.getSplitChar();

			if (c < split)
			{
				return findNode(node.getLokid(), word, index);
			}
			else if (c > split)
			{
				return findNode(node.getHikid(), word, index);
			}
			else
			{
				if (index == word.length() - 1)
				{
					if (node.isEndOfWord())
					{
						return node;
					}
				}
				else
				{
					return findNode(node.getEqkid(), word, index + 1);
				}
			}
		}

		return null;
	}

	/**
	 * <p>
	 * This will recursively search for a partial word in the
	 * <code>TernaryTrie</code>
	 * one node at a time beginning at the supplied node.
	 * </p>
	 *
	 * @param node <code>TernaryTrieNode</code> to search in
	 * @param matches <code>ArrayList</code> of partial matches
	 * @param match <code>String</code> the current word being examined
	 * @param word <code>String</code> to search for
	 * @param index <code>int</code> of character in word
	 * @return <code>ArrayList</code> - of matches
	 */

	protected List<String> partialSearchNode
	(
		final TernaryTrieNode node,
		List<String> matches,
		final String match,
		final String word,
		final int index
	)
	{
		if (node != null && index < word.length())
		{
			final char c = word.charAt(index);
			final char split = node.getSplitChar();

			if (c == '.' || c < split)
			{
				matches = partialSearchNode(node.getLokid(), matches,
							match, word, index);
			}
			if (c == '.' || c == split)
			{
				if (index == word.length() - 1)
				{
					if (node.isEndOfWord())
					{
						matches.add(match + split);
					}
				}
				else
				{
					matches = partialSearchNode(node.getEqkid(), matches,
								match + split,
								word, index + 1);
				}
			}
			if (c == '.' || c > split)
			{
				matches = partialSearchNode(node.getHikid(), matches,
							match, word, index);
			}
		}
		return matches;
	}

	/**
	 * <p>
	 * This will recursively search for a near match word in the
	 * <code>TernaryTrie</code>
	 * one node at a time beginning at the supplied node.
	 * </p>
	 *
	 * @param node <code>TernaryTrieNode</code> to search in
	 * @param distance <code>int</code> of a valid match, must be > 0
	 * @param matches <code>ArrayList</code> of near matches
	 * @param match <code>String</code> the current word being examined
	 * @param word <code>String</code> to search for
	 * @param index <code>int</code> of character in word
	 * @return <code>ArrayList</code> - of matches
	 */

	protected List<String> nearSearchNode
	(
		TernaryTrieNode node,
		int distance,
		List<String> matches,
		String match,
		String word,
		int index
	)
	{
		if ( ( node != null ) && ( distance >= 0 ) )
		{
			final char c;

			if ( index < word.length() )
			{
				c	= word.charAt( index );
			}
			else
			{
				c	= (char)(-1);
//				c	= (char)0;
			}

			char split	= node.getSplitChar();

			if ( ( distance > 0 ) || ( c < split ) )
			{
				matches	=
					nearSearchNode
					(
						node.getLokid() ,
						distance ,
						matches ,
						match ,
						word,
						index
					);
			}

			String newMatch	= match + split;

			if ( c == split )
			{
				if	( 	node.isEndOfWord() && ( distance >= 0 ) &&
						( ( newMatch.length() + distance ) >= word.length() )
					)
				{
					matches.add( newMatch );
//System.out.println( "newSearchNode: eq1: adding " + newMatch );
				}

				matches	=
					nearSearchNode
					(
						node.getEqkid() ,
						distance ,
						matches ,
						newMatch ,
						word ,
						index + 1
					);
			}
			else
			{
				if	(	node.isEndOfWord() && ( ( distance - 1 ) >= 0 ) &&
						( newMatch.length() + ( distance - 1 ) >= word.length() )
					)
				{
					matches.add( newMatch );
//System.out.println( "newSearchNode: eq2: adding " + newMatch );
				}

				matches	=
					nearSearchNode
					(
						node.getEqkid() ,
						distance - 1 ,
						matches ,
						newMatch ,
						word ,
						index + 1
					);
			}

			if ( ( distance > 0 ) || ( c > split ) )
			{
				matches	=
					nearSearchNode
					(
						node.getHikid() ,
						distance ,
						matches ,
						match ,
						word ,
						index
					);
			}
		}

		return matches;
	}

	/**
	 * <p>
	 * This will recursively traverse every node in the
	 * <code>TernaryTrie</code>
	 * one node at a time beginning at the supplied node.
	 * The result is a string representing every word, which is delimited by
	 * the LINE_SEPARATOR character.
	 * </p>
	 *
	 * @param node <code>TernaryTrieNode</code> to begin traversing
	 * @param s <code>String</code> of words found at the supplied node
	 * @param words <code>ArrayList</code> which will be returned
	 * (recursive function)
	 * @return <code>String</code> - containing all words from the supplied node
	 */

	protected List<String> traverseNode
	(
		TernaryTrieNode node,
		String s,
		List<String> words
	)
	{
		if ( node != null )
		{
			words		= traverseNode( node.getLokid() , s , words );

			String c	= String.valueOf( node.getSplitChar() );

			if ( node.getEqkid() != null )
			{
				if ( node.endOfWord )
				{
					words.add( s + c );
				}

				words	=
					traverseNode( node.getEqkid() , s + c , words );
			}
			else
			{
				words.add( s + c );
			}

			words	= traverseNode( node.getHikid() , s , words );
		}

		return words;
	}

	/**	Return size of trie (# of terminal nodes)
	 *
	 *	@return		Number of terminal nodes (nodes with data values).
	 */

	public int size()
	{
		return nodeCount;
	}

	/**	See if specified string exists.
	 *
	 *	@param	string	The string.
	 *
	 *	@return			True if specified string exists.
	 */

	public boolean containsString( String string )
	{
		return containsKey( string );
	}

	/**	Get the tag value associated with a string.
	 *
	 *	@param	string	The string.
	 *
	 *	@return			The tag value associated with the string.
	 *					May be null.
	 */

	public String getTag( String string )
	{
		String result	= null;

		Object value	= get( string );

		if ( value != null )
		{
			result	= value.toString();
		}

		return result;
	}

	/**	Set the tag value associated with a string.
	 *
	 *	@param	string	The string.
	 *	@param	tag		The tag.
	 */

	public void putTag( String string , String tag )
	{
		put( string , tag );
	}

	/**	Get number of strings.
	 *
	 *	@return		Number of strings.
	 */

	public int getStringCount()
	{
		return nodeCount;
	}

	/**	Get set of all unique string values.
	 *
	 *	@return		Set of all unique string values.
	 */

	public Set<String> getAllStrings()
	{
		Set<String> result	= SetFactory.createNewSet();

		List<String> list	= ListFactory.createNewList();

		result.addAll( traverseNode( root , "" , list ) );

		return result;
	}

	/**	Get set of all unique tag values as strings.
	 *
	 *	@return		Set of all unique string tag values.
	 */

	public Set<String> getAllTags()
	{
		Set<String> result	= SetFactory.createNewSet();

		List<String> list	= ListFactory.createNewList();

		List<String> stringsList	= traverseNode( root , "" , list );

		for ( String key : stringsList )
		{
			result.add( get( key ).toString() );
		}

		return result;
	}
}

