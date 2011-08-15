package edu.northwestern.at.utils;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Set;

/**	A set of bits. The set automatically grows as more bits are needed.
 *
 *	@version 	1.00, 25 Jul 1999
 *	@author C. Scott Ananian
 *
 *	<p>
 *	Modications by Philip R. "Pib" Burns to add missing BitSet methods.
 *	</p>
 */

public class SparseBitSet implements XCloneable, Serializable
{
	/** Sorted array of bit-block offsets. */

	protected int offs[];

	/** Array of bit-blocks; each holding BITS bits. */

	protected long bits[];

	/** Number of blocks currently in use. */

	protected int size;

	/** log base 2 of BITS, for the identity: x/BITS == x >> LG_BITS */

	protected static final int LG_BITS = 6;

	/** Number of bits in a block. */

	protected static final int BITS = ( 1 << LG_BITS );

	/** BITS-1, using the identity: x % BITS == x & (BITS-1) */

	protected static final int BITS_M1 = BITS - 1;

	/**	Create an empty set.
	 */

	public SparseBitSet()
	{
		bits	= new long[ 4 ];
		offs	= new int[ 4 ];
		size	= 0;
	}

	/**	Create an empty set with the specified size.
	 *
	 *	@param	nbits	The size of the set.
	 */

	public SparseBitSet( int nbits )
	{
		this();
	}

	/**	Create an empty set with the same size as the given set.
	 */

	public SparseBitSet( SparseBitSet set )
	{
		bits	= new long[ set.size ];
		offs	= new int[ set.size ];
		size	= 0;
	}

	protected void new_block( int bnum )
	{
		new_block( bsearch( bnum ) , bnum );
	}

	protected void new_block( int idx , int bnum )
	{
		if ( size == bits.length )
		{
			long[] nbits	= new long[ size * 3 ];
			int[] noffs 		= new int[ size * 3 ];

			System.arraycopy( bits , 0 , nbits , 0 , size );
			System.arraycopy( offs , 0 , noffs , 0 , size );

			bits	= nbits;
			offs	= noffs;
		}

		insert_block( idx , bnum );
	}

	protected void insert_block( int idx , int bnum )
	{
		System.arraycopy( bits , idx , bits , idx + 1 , size - idx );
		System.arraycopy( offs , idx , offs , idx + 1 , size - idx );

		offs[ idx ]	= bnum;
		bits[ idx ]	= 0;

		size++;
	}

	protected int bsearch( int bnum )
	{
		int l = 0 , r = size; // search interval is [l, r)

		while ( l < r )
		{
			int p	= ( l + r ) / 2;

			if ( bnum < offs[ p ] ) r = p;
			else if ( bnum > offs[ p ] ) l = p + 1;
			else return p;
		}

		return l; // index at which the bnum *should* be, if it's not.
	}

	/**	Set a bit.
	 *
	 *	@param	bit		The bit to set.
	 */

	public void set( int bit )
	{
		int bnum	= ( bit >> LG_BITS );
		int idx		= bsearch( bnum );

		if ( ( idx >= size ) || ( offs[ idx ] != bnum ) )
		{
			new_block( idx , bnum );
		}

		bits[ idx ] |= ( 1L << ( bit & BITS_M1 ) );
	}

	/**	Clear a bit.
	 *
	 *	@param	bit		The bit to be cleared.
	 */

	public void clear( int bit )
	{
		int bnum	= ( bit >> LG_BITS );
		int idx		= bsearch( bnum );

		if ( ( idx >= size ) || ( offs[ idx ] != bnum ) )
		{
			new_block( idx , bnum );
		}

		bits[ idx ]	&= ~( 1L << ( bit & BITS_M1 ) );
	}

	/**	Clear all bits.
	 */

	public void clear()
	{
		size	= 0;
	}

	/** Clears a range of bits.
	 *
	 *	@param	fromIndex		Index of first bit to be cleared.
	 *	@param	toIndex	 	Index + 1 of last bit to be cleared.
	 */

	public void clear( int fromIndex , int toIndex )
	{
		for ( int bit = fromIndex ; bit < toIndex ; bit++ )
		{
			clear( bit );
		}
	}

	/** Flip a bit value.
	 *
	 *	@param	bit	Bit to flip.
	 */

	public void flip( int bit )
	{
		if ( get( bit ) )
		{
			clear( bit );
		}
		else
		{
			set( bit );
		}
	}

	/** Flips a range of bits.
	 *
	 *	@param	fromIndex		Index of first bit to be flipped.
	 *	@param	toIndex	 	Index + 1 of last bit to be flipped.
	 */

	public void flip( int fromIndex , int toIndex )
	{
		for ( int bit = fromIndex ; bit < toIndex ; bit++ )
		{
			flip( bit );
		}
	}

	/**	Get bit value.
	 *
	 *	@param	bit		Bit whose value is to be retrieved.
	 *
	 *	@return			true if bit set, false otherwise.
	 */

	public boolean get( int bit )
	{
		int bnum	= ( bit >> LG_BITS );
		int idx		= bsearch( bnum );

		if ( ( idx >= size ) || ( offs[ idx ] != bnum ) )
		{
			return false;
		}

		return ( 0 != ( bits[ idx ] & ( 1L << ( bit & BITS_M1 ) ) ) );
	}

	/**	Logically AND this bit set with another.
	 *
	 *	@param	otherSet	The other bit set to AND against.
	 */

	public void and( SparseBitSet otherSet )
	{
		binop( this , otherSet , AND );
	}

	/**	Logically OR this bit set with another.
	 *
	 *	@param	otherSet	The other bit set to be ORed against.
	 */

	public void or( SparseBitSet otherSet )
	{
		binop( this , otherSet , OR );
	}

	/**	Logically XOR this bit set with another.
	 *
	 *	@param	otherSet	The other bit set to be XORed against.
	 */

	public void xor( SparseBitSet otherSet )
	{
		binop( this , otherSet , XOR );
	}

	/**	Logically ANDNOT this bit set with another.
	 *
	 *	@param	otherSet	The other bit set to be ANDNOTed against.
	 */

	public void andNot( SparseBitSet otherSet )
	{
		binop( this , otherSet , ANDNOT );
	}

	// BINARY OPERATION MACHINERY

	protected static interface BinOp
	{
		public long op( long a , long b );
	}

	protected static final BinOp AND =
		new BinOp()
		{
			public final long op( long a , long b )
			{
				return a & b;
			}
		};

	protected static final BinOp OR = new BinOp()
		{
			public final long op(long a, long b)
			{
				return a | b;
			}
		};

	protected static final BinOp XOR = new BinOp()
		{
			public final long op(long a, long b)
			{
				return a ^ b;
			}
		};

	protected static final BinOp ANDNOT = new BinOp()
		{
			public final long op( long a , long b )
			{
				return a & ~b;
			}
		};

	protected static final void binop
	(
		SparseBitSet a ,
		SparseBitSet b ,
		BinOp op
	)
	{
		int  nsize	= a.size + b.size;
		long[] nbits;
		int[] noffs;
		int a_zero, a_size;

								//	Be very clever and avoid allocating
								//	more memory if we can.

		if (a.bits.length < nsize)
		{
								//	Need to make working space.

			nbits	= new long[ nsize ];
			noffs	= new int[ nsize ];
			a_zero	= 0;
			a_size	= a.size;
		}
		else
		{
								//	Reduce, reuse, recycle!
			nbits	= a.bits;
			noffs	= a.offs;
			a_zero	= a.bits.length - a.size;
			a_size	= a.bits.length;

			System.arraycopy( a.bits , 0 , a.bits , a_zero , a.size );
			System.arraycopy( a.offs , 0 , a.offs , a_zero , a.size );
		}
								//	Crunch through and binop those sets!
		nsize	= 0;

		for ( int i = a_zero , j = 0 ; i < a_size || j < b.size ; )
		{
			long nb;
			int no;

			if	(	( i < a_size ) &&
					( ( j >= b.size ) || ( a.offs[ i ] < b.offs[ j ] ) )
				)
			{
				nb	= op.op( a.bits[i], 0);
				no	= a.offs[i];
				i++;
			}
			else if
			(
				( j < b.size ) &&
				( ( i >= a_size ) || ( a.offs[ i ] > b.offs[ j ] ) )
			)
			{
				nb	= op.op( 0 , b.bits[ j ] );
				no	= b.offs[ j ];
				j++;
			}
			else
			{
								//	Equal keys; merge.

				nb	= op.op( a.bits[ i ] , b.bits[ j ] );
				no	= a.offs[ i ];
				i++;
				j++;
			}

			if ( nb != 0 )
			{
				nbits[ nsize ]	= nb;
				noffs[ nsize ]	= no;
				nsize++;
			}
		}

		a.bits	= nbits;
		a.offs	= noffs;
		a.size	= nsize;
	}

	/**	Return hash code for set.
	 *
	 *	@return		The hash code.
	 */

	public int hashCode()
	{
		long hash = 1234;

		for ( int i = 0 ; i < size ; i++ )
		{
			hash ^= bits[ i ] * offs[ i ];
        }

		return (int)( ( hash >> 32) ^ hash );
	}

	/**	Return set's size.
	 */

	public int size()
	{
		return ( size == 0 ) ? 0 :  ( ( 1 + offs[ size - 1 ] ) << LG_BITS );
	}

	/**	Compare this set against another.
	 *
	 *	@param	object		The object to compare against.
	 *
	 *	@return				true the objects are equal, false otherwise.
	 */

	public boolean equals( Object object )
	{
		boolean result	= false;

		if ( (object != null ) && ( object instanceof SparseBitSet ) )
		{
			result	= equals( this , (SparseBitSet)object );
		}

		return result;
	}

	/**	Compare two SparseBitSets for equality.
	 *
	 *	@return		true if sets are equal, false otherwise.
	 */

	public static boolean equals( SparseBitSet a , SparseBitSet b )
	{
		for ( int i = 0 , j = 0 ; ( ( i < a.size ) || ( j < b.size ) ) ; )
		{
			if	(	( i < a.size ) &&
					( ( j >= b.size ) || ( a.offs[ i ] < b.offs[ j ] ) )
				)
			{
				if ( a.bits[ i++ ] != 0 ) return false;
			}

			else if	(	( j < b.size ) &&
						( ( i >= a.size ) || ( a.offs[ i ] > b.offs[ j ] ) )
					)
			{
				if ( b.bits[ j++ ] != 0 ) return false;
			}
			else
			{
				if ( a.bits[ i++ ] != b.bits[ j++ ] ) return false;
			}
		}

		return true;
	}

	/**	Return cardinality (# of bits set on) in this bit set.
	 *
	 *	@return	Cardinality of this bit set.
	 */

	public int cardinality()
	{
		int result	= 0;

		Enumeration enumeration	= elements();

		while ( enumeration.hasMoreElements() )
		{
			enumeration.nextElement();
			result++;
		}

		return result;
	}

	/**	Check if this set and another share at least one set bit.
	 *
	 *	@param	otherSet		The other set to check for intersection
	 *
	 *	@return	true			If this set and the other intersect.
	 */

	public boolean intersects( SparseBitSet otherSet )
	{
		boolean result	= false;

		int i	= Math.min(	size , otherSet.size );

		while ( ( i > 0 ) && !result )
		{
			result	= ( ( bits[ i ] & otherSet.bits[ i ] ) != 0 );
			i--;
		}

		return result;
	}

	/**	Returns index of highest set bit in this set plus one.
	 *
	 *	@return		The logical size of this set.
	 */

	public int length()
	{
		int result	= size();

		while ( !get( result ) && ( result >= 0 ) )
		{
			result--;
		}

		return result + 1;
	}

	/**	Returns index of first bit set to true that occurs on or after specified starting index.
	 *
	 *	@param	fromIndex		Index to start checking from (inclusive).
	 *
	 *	@return					Index of next bit set.
	 *
	 *	@throws	IndexOutOfBoundsException	if specified index
	 *												is negative.
	 *
	 *	<p>
	 *	If no such bit exists then -1 is returned.
	 *	To iterate over the true bits in a BitSet, use the following loop:
	 *	</p>
	 *
	 *	<p>
	 *	<code>
	 *	for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i+1))
	 *	{
	 *		// operate on index i here
	 *	}
	 *	</code>
	 *	</p>
	 */

	public int nextSetBit( int fromIndex )
	{
		if ( fromIndex < 0 )
		{
			throw new IndexOutOfBoundsException( "Index is negative" );
		}

		int result	= -1;
		int l			= length();

		for ( int i = fromIndex + 1 ; i < l ; i++ )
		{
			if ( get( i ) )
			{
				return i;
			}
		}

		return -1;
	}

	/**	Returns index of first bit set to false that occurs on or after specified starting index.
	 *
	 *	@param	fromIndex		Index to start checking from (inclusive).
	 *
	 *	@return					Index of next clear bit.
	 *
	 *	@throws	IndexOutOfBoundsException	if specified index
	 *												is negative.
	 *
	 *	<p>
	 *	If no such bit exists then -1 is returned.
	 *	To iterate over the false bits in a BitSet, use the following loop:
	 *	</p>
	 *
	 *	<p>
	 *	<code>
	 *	for (int i = bs.nextClearBit(0); i >= 0; i = bs.nextClearBit(i+1))
	 *	{
	 *		// operate on index i here
	 *	}
	 *	</code>
	 *	</p>
	 */

	public int nextClearBit( int fromIndex )
	{
		if ( fromIndex < 0 )
		{
			throw new IndexOutOfBoundsException( "Index is negative" );
		}

		int result	= -1;
		int l			= length();

		for ( int i = fromIndex + 1 ; i < l ; i++ )
		{
			if ( !get( i ) )
			{
				return i;
			}
		}

		return -1;
	}

	/**	Clone this set.
	 *
	 *	@return		Clone of this bit set.
	 */

	public Object clone()
	{
		SparseBitSet set	= new SparseBitSet();

		set.bits				= (long[])bits.clone();
		set.offs			= (int[])offs.clone();
		set.size			= size;

		return set;
	}

	/**	Return an <code>Enumeration</code> of <code>Integer</code>s
	 *	indices of bits set on in this SparseBitSet.
	 */

	public Enumeration elements()
	{
		return
			new Enumeration()
			{
				int idx = -1, bit = BITS;
				{
					advance();
				}

				public boolean hasMoreElements()
				{
					return ( idx < size );
				}

				public Object nextElement()
				{
					int r = bit + ( offs[ idx ] << LG_BITS );

					advance();

					return new Integer( r );
				}

				protected void advance()
				{
					while ( idx < size )
					{
						while ( ++bit < BITS )
						{
							if ( 0 != (bits[ idx ] & ( 1L << bit ) ) )
							{
								return;
							}
						}

						idx++;
						bit	= -1;
					}
				}
			};
	}

	/**	Converts the SparseBitSet to a String.
	 */

	public String toString()
	{
		StringBuffer sb	= new StringBuffer();

		sb.append( '{' );

		for ( Enumeration e = elements() ; e.hasMoreElements(); )
		{
			if ( sb.length() > 1 ) sb.append( ", " );
			sb.append( e.nextElement() );
		}

		sb.append( '}' );

		return sb.toString();
	}

	/** Check validity. */

	protected boolean isValid()
	{
		if ( bits.length != offs.length ) return false;

		if ( size > bits.length ) return false;

		if ( ( size != 0 ) && ( 0 <= offs[ 0 ] ) ) return false;

		for ( int i = 1 ; i < size ; i++ )
		{
			if ( offs[ i ] < offs[ i - 1 ] )
			{
				return false;
			}
		}

		return true;
	}
}

