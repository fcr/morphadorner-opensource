package edu.northwestern.at.utils;

/**	A ternary trie node.
 */

public class TernaryTrieNode
{
	/** Character stored in this node. */

	protected char splitchar;

	/** True if this character is the end of a word. */

	protected boolean endOfWord;

	/** Low child of this node. */

	protected TernaryTrieNode lokid;

	/** Equal child of this node. */

	protected TernaryTrieNode eqkid;

	/** High child of this node. */

	protected TernaryTrieNode hikid;

	/** Data value for this node. */

	protected Object value;

	/**	Create a new empty node.
	 */

	public TernaryTrieNode()
	{
	}

	/**	Create a new node with a specified split char.
	 *
	 *	@param	c	Character for this node.
	 */

	public TernaryTrieNode( char c )
	{
		this.splitchar	= c;
	}

	/**	Return the trie splitting character for this node.
	 *
	 *	@return		The trie splitting character for this node.
	 */

	public char getSplitChar()
	{
		return this.splitchar;
	}

	/**	Set the trie splitting charaacter for this node.
	 *
	 *	@param	c	The trie splitting character.
	 */

	public void setSplitChar( char c )
	{
		this.splitchar	= c;
	}

	/**
	 * <p>
	 * This returns the endOfWord for this <code>TernaryTrieNode</code>.
	 * </p>
	 *
	 * @return <code>boolean</code>
	 */

	public boolean isEndOfWord()
	{
		return this.endOfWord;
	}

	/**
	 * <p>
	 * This sets the endOfWord for this <code>TernaryTrieNode</code>.
	 * </p>
	 *
	 * @param b <code>boolean</code>
	 */

	public void setEndOfWord( boolean b )
	{
		this.endOfWord = b;
	}

	/**
	 * <p>
	 * This returns the lokid of this <code>TernaryTrieNode</code>.
	 * </p>
	 *
	 * @return <code>TernaryTrieNode</code>
	 */

	public TernaryTrieNode getLokid()
	{
		return this.lokid;
	}

	/**
	 * <p>
	 * This sets the lokid of this <code>TernaryTrieNode</code>.
	 * </p>
	 *
	 * @param node <code>TernaryTrieNode</code>
	 */

	public void setLokid( TernaryTrieNode node )
	{
		this.lokid = node;
	}

	/**
	 * <p>
	 * This returns the eqkid of this <code>TernaryTrieNode</code>.
	 * </p>
	 *
	 * @return <code>TernaryTrieNode</code>
	 */

	public TernaryTrieNode getEqkid()
	{
		return this.eqkid;
	}

	/**
	 * <p>
	 * This sets the eqkid of this <code>TernaryTrieNode</code>.
	 * </p>
	 *
	 * @param node <code>TernaryTrieNode</code>
	 */

	public void setEqkid( TernaryTrieNode node )
	{
		this.eqkid = node;
	}

	/**
	 * <p>
	 * This returns the hikid of this <code>TernaryTrieNode</code>.
	 * </p>
	 *
	 * @return <code>TernaryTrieNode</code>
	 */

	public TernaryTrieNode getHikid()
	{
		return this.hikid;
	}

	/**
	 * <p>
	 * This sets the hikid of this <code>TernaryTrieNode</code>.
	 * </p>
	 *
	 * @param node <code>TernaryTrieNode</code>
	 */

	public void setHikid( TernaryTrieNode node )
	{
		this.hikid = node;
	}

	/** Set data value for node.
	 *
	 *	@param	value	The data value.
	 */

	public void setValue( Object value )
	{
		this.value	= value;
	}

	/** Get data value for node.
	 *
	 *	@return		The data value.
	 */

	public Object getValue()
	{
		return value;
	}
}

