package edu.northwestern.at.utils;

import java.io.*;
import java.util.*;

/**
 *  Extension fo the RandomAccessFile to use buffered I/O as much as
 *  possible. Usable with the <code>com.objectwave.persist.FileBroker</code> .
 *  Publically identical to <code>java.io.RandomAccessFile</code> , except for
 *  the constuctor and <code>flush()</code> . <p>
 *
 *  <b>Note:</b> This class is not threadsafe.
 *
 * @author  Steven Sinclair
 * @version  $Date: 2005/02/20 17:27:32 $ $Revision: 2.3 $
 * @see  java.io.RandomAccessFile
 *
 *	<p>
 *	Modifications by Philip R. Burns.  2007/05/08.
 *	</p>
 */

public class BufferedRandomAccessFile implements DataInput, DataOutput
{

	protected FileBufferStruct currBuf;
	protected FileBufferStruct altBuf;

//////////////////////////////  END CUT & PASTE FROM RandomAccessFile

	RandomAccessFile delegate;

	/**
	 *  Constructor for the BufferedRandomAccessFile object
	 *
	 * @param  file Description of Parameter
	 * @param  mode Description of Parameter
	 * @param  bufferSize Description of Parameter
	 * @exception  IOException Description of Exception
	 */
	public BufferedRandomAccessFile(File file, String mode, int bufferSize) throws IOException
	{
		this(file, mode);

		if(bufferSize < 1)
		{
			throw new Error("Buffer size must be at least 1");
		}
		currBuf = new FileBufferStruct();
		altBuf = new FileBufferStruct();
		currBuf.bytes = new byte[bufferSize];
		currBuf.filePos = delegate.getFilePointer();
		currBuf.modified = false;
		altBuf.bytes = new byte[bufferSize];
		altBuf.filePos = -1;
		// not initialized
		fillBuffer();
	}

	/**
	 *  Constructor for the BufferedRandomAccessFile object
	 *
	 * @param  file Description of Parameter
	 * @param  mode Description of Parameter
	 * @exception  IOException Description of Exception
	 */
	protected BufferedRandomAccessFile(File file, String mode) throws IOException
	{
		delegate = new RandomAccessFile(file, mode);
	}

	/**
	 *  Sets the Length attribute of the BufferedRandomAccessFile object
	 *
	 * @param  newLength The new Length value
	 * @exception  IOException Description of Exception
	 */
	public void setLength(long newLength) throws IOException
	{
		// need to check altBuf, too.

		delegate.setLength(newLength);
		if(newLength < currBuf.filePos)
		{
			currBuf.filePos = newLength;
			currBuf.pos = 0;
			currBuf.dataLen = 0;
		}
		else if(newLength < currBuf.filePos + currBuf.dataLen)
		{
			currBuf.dataLen = (int) (newLength - currBuf.filePos);
			if(currBuf.dataLen > currBuf.pos)
			{
				currBuf.pos = currBuf.dataLen;
			}
		}
	}

/////////////////////////////  Support Reader & Writer

	/**
	 *  Gets the Reader attribute of the BufferedRandomAccessFile object
	 *
	 * @return  The Reader value
	 */
	public Reader getReader()
	{
		return
			new Reader()
			{
				/**
				 *  Description of the Method
				 *
				 * @exception  IOException Description of Exception
				 */
				public void close() throws IOException
				{
					BufferedRandomAccessFile.this.close();
				}
				/**
				 *  Description of the Method
				 *
				 * @param  readAhreadLimit Description of Parameter
				 * @exception  IOException Description of Exception
				 */
				public void mark(int readAhreadLimit) throws IOException
				{
					throw new IOException("mark not supported");
				}
				/**
				 *  Description of the Method
				 *
				 * @return  Description of the Returned Value
				 */
				public boolean markSupported()
				{
					return false;
				}
				/**
				 *  Description of the Method
				 *
				 * @return  Description of the Returned Value
				 * @exception  IOException Description of Exception
				 */
				public int read() throws IOException
				{
					return BufferedRandomAccessFile.this.readChar();
				}
				/**
				 *  Description of the Method
				 *
				 * @param  buf Description of Parameter
				 * @return  Description of the Returned Value
				 * @exception  IOException Description of Exception
				 */
				public int read(char[] buf) throws IOException
				{
					return read(buf, 0, buf.length);
				}
				/**
				 *  Description of the Method
				 *
				 * @param  buf Description of Parameter
				 * @param  pos Description of Parameter
				 * @param  len Description of Parameter
				 * @return  Description of the Returned Value
				 * @exception  IOException Description of Exception
				 */
				public int read(char[] buf, int pos, int len) throws IOException
				{
					for(int i = 0; i < len; i++)
					{
						buf[pos + i] = readChar();
					}
					return len;
				}
				/**
				 *  Description of the Method
				 *
				 * @return  Description of the Returned Value
				 * @exception  IOException Description of Exception
				 */
				public boolean ready() throws IOException
				{
					return (currBuf.pos < currBuf.dataLen) ||
							(length() < currBuf.filePos + currBuf.pos);
				}
				/**
				 *  Description of the Method
				 *
				 * @param  n Description of Parameter
				 * @return  Description of the Returned Value
				 * @exception  IOException Description of Exception
				 */
				public long skip(long n) throws IOException
				{
					skipBytes(n);
					return n;
				}
			};
	}

	/**
	 *  Gets the Writer attribute of the BufferedRandomAccessFile object
	 *
	 * @return  The Writer value
	 */
	public Writer getWriter()
	{
		return
			new Writer()
			{
				/**
				 *  Description of the Method
				 *
				 * @exception  IOException Description of Exception
				 */
				public void close() throws IOException
				{
					BufferedRandomAccessFile.this.close();
				}
				/**
				 *  Description of the Method
				 *
				 * @exception  IOException Description of Exception
				 */
				public void flush() throws IOException
				{
					BufferedRandomAccessFile.this.flush();
				}
				/**
				 *  Description of the Method
				 *
				 * @param  ch Description of Parameter
				 * @exception  IOException Description of Exception
				 */
				public void write(int ch) throws IOException
				{
					writeChar(ch);
				}
				/**
				 *  Description of the Method
				 *
				 * @param  ch Description of Parameter
				 * @exception  IOException Description of Exception
				 */
				public void write(char[] ch) throws IOException
				{
					write(ch, 0, ch.length);
				}
				/**
				 *  Description of the Method
				 *
				 * @param  ch Description of Parameter
				 * @param  pos Description of Parameter
				 * @param  len Description of Parameter
				 * @exception  IOException Description of Exception
				 */
				public void write(char[] ch, int pos, int len) throws IOException
				{
					for(int i = 0; i < len; i++)
					{
						writeChar(ch[pos + i]);
					}
				}
				/**
				 *  Description of the Method
				 *
				 * @param  str Description of Parameter
				 * @exception  IOException Description of Exception
				 */
				public void write(String str) throws IOException
				{
					write(str, 0, str.length());
				}
				/**
				 *  Description of the Method
				 *
				 * @param  str Description of Parameter
				 * @param  pos Description of Parameter
				 * @param  len Description of Parameter
				 * @exception  IOException Description of Exception
				 */
				public void write(String str, int pos, int len) throws IOException
				{
					for(int i = 0; i < len; i++)
					{
						writeChar(str.charAt(pos + i));
					}
				}
			};
	}

	/**
	 *  Gets the FD attribute of the BufferedRandomAccessFile object
	 *
	 * @return  The FD value
	 * @exception  IOException Description of Exception
	 */
	public FileDescriptor getFD() throws IOException
	{
		return delegate.getFD();
	}

	/**
	 *  Gets the FilePointer attribute of the BufferedRandomAccessFile object
	 *
	 * @return  The FilePointer value
	 */
	public long getFilePointer()
	{
		return currBuf.filePos + currBuf.pos;
	}

//////////////////////////////  BEGIN CUT & PASTE FROM RandomAccessFile

	/**
	 *  Description of the Method
	 *
	 * @return  Description of the Returned Value
	 * @exception  IOException Description of Exception
	 */
	public boolean readBoolean() throws IOException
	{
		return readByte() != 0;
	}

	/**
	 *  Description of the Method
	 *
	 * @return  Description of the Returned Value
	 * @exception  IOException Description of Exception
	 */
	public int readUnsignedByte() throws IOException
	{
		int b = read();
		if(b < 0)
		{
			throw new EOFException();
		}
		return b & 0xff;
	}

	/**
	 *  Description of the Method
	 *
	 * @return  Description of the Returned Value
	 * @exception  IOException Description of Exception
	 */
	public byte readByte() throws IOException
	{
		int b = read();
		if(b < 0)
		{
			throw new EOFException();
		}
		return (byte) b;
	}

	/**
	 *  Description of the Method
	 *
	 * @return  Description of the Returned Value
	 * @exception  IOException Description of Exception
	 */
	public short readShort() throws IOException
	{
		int ch1 = this.read();
		int ch2 = this.read();
		if((ch1 | ch2) < 0)
		{
			throw new EOFException();
		}
		return (short) ((ch1 << 8) + (ch2 << 0));
	}

	/**
	 *  Description of the Method
	 *
	 * @return  Description of the Returned Value
	 * @exception  IOException Description of Exception
	 */
	public int readUnsignedShort() throws IOException
	{
		int ch1 = this.read();
		int ch2 = this.read();
		if((ch1 | ch2) < 0)
		{
			throw new EOFException();
		}
		return (ch1 << 8) + (ch2 << 0);
	}

	/**
	 *  Description of the Method
	 *
	 * @return  Description of the Returned Value
	 * @exception  IOException Description of Exception
	 */
	public char readChar() throws IOException
	{
		return (char) readUnsignedShort();
	}

	/**
	 *  Description of the Method
	 *
	 * @return  Description of the Returned Value
	 * @exception  IOException Description of Exception
	 */
	public int readInt() throws IOException
	{
		int ch1 = this.read();
		int ch2 = this.read();
		int ch3 = this.read();
		int ch4 = this.read();
		if((ch1 | ch2 | ch3 | ch4) < 0)
		{
			throw new EOFException();
		}
		return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
	}

	/**
	 *  Description of the Method
	 *
	 * @return  Description of the Returned Value
	 * @exception  IOException Description of Exception
	 */
	public long readLong() throws IOException
	{
		return ((long) (readInt()) << 32) + (readInt() & 0xFFFFFFFFL);
	}

	/**
	 *  Description of the Method
	 *
	 * @return  Description of the Returned Value
	 * @exception  IOException Description of Exception
	 */
	public float readFloat() throws IOException
	{
		return Float.intBitsToFloat(readInt());
	}

	/**
	 *  Description of the Method
	 *
	 * @return  Description of the Returned Value
	 * @exception  IOException Description of Exception
	 */
	public double readDouble() throws IOException
	{
		return Double.longBitsToDouble(readLong());
	}

	/**
	 *  Description of the Method
	 *
	 * @return  Description of the Returned Value
	 * @exception  IOException Description of Exception
	 */
	public String readLine() throws IOException
	{
		StringBuffer input = new StringBuffer();
		int c = -1;
		boolean eol = false;

		while(!eol)
		{
			switch (c = read())
			{
				case -1:
				case '\n':
					eol = true;
					break;
				case '\r':
					eol = true;
					long cur = getFilePointer();
					if((read()) != '\n')
					{
						seek(cur);
					}
					break;
				default:
					input.append((char) c);
			}
		}

		if((c == -1) && (input.length() == 0))
		{
			return null;
		}
		return input.toString();
	}

	/**
	 *  Description of the Method
	 *
	 * @return  Description of the Returned Value
	 * @exception  IOException Description of Exception
	 */
	public String readUTF() throws IOException
	{
		//throw new Error("Not implemented yet");
		return DataInputStream.readUTF(this);
	}

	/**
	 *  Description of the Method
	 *
	 * @param  b Description of Parameter
	 * @exception  IOException Description of Exception
	 */
	public void writeBoolean(boolean b) throws IOException
	{
		write(b ? 1 : 0);
	}

	/**
	 *  Description of the Method
	 *
	 * @param  b Description of Parameter
	 * @exception  IOException Description of Exception
	 */
	public void writeByte(int b) throws IOException
	{
		write(b);
	}

	/**
	 *  Description of the Method
	 *
	 * @param  s Description of Parameter
	 * @exception  IOException Description of Exception
	 */
	public void writeShort(int s) throws IOException
	{
		write((s >>> 8) & 0xFF);
		write((s >>> 0) & 0xFF);
	}

	/**
	 *  Description of the Method
	 *
	 * @param  ch Description of Parameter
	 * @exception  IOException Description of Exception
	 */
	public void writeChar(int ch) throws IOException
	{
		writeShort(ch);
	}

	/**
	 *  Description of the Method
	 *
	 * @param  i Description of Parameter
	 * @exception  IOException Description of Exception
	 */
	public void writeInt(int i) throws IOException
	{
		write((i >>> 24) & 0xFF);
		write((i >>> 16) & 0xFF);
		write((i >>> 8) & 0xFF);
		write((i >>> 0) & 0xFF);
	}

	/**
	 *  Description of the Method
	 *
	 * @param  l Description of Parameter
	 * @exception  IOException Description of Exception
	 */
	public void writeLong(long l) throws IOException
	{
		write((int) (l >>> 56) & 0xFF);
		write((int) (l >>> 48) & 0xFF);
		write((int) (l >>> 40) & 0xFF);
		write((int) (l >>> 32) & 0xFF);
		write((int) (l >>> 24) & 0xFF);
		write((int) (l >>> 16) & 0xFF);
		write((int) (l >>> 8) & 0xFF);
		write((int) (l >>> 0) & 0xFF);
	}

	/**
	 *  Description of the Method
	 *
	 * @param  f Description of Parameter
	 * @exception  IOException Description of Exception
	 */
	public void writeFloat(float f) throws IOException
	{
		writeInt(Float.floatToIntBits(f));
	}

	/**
	 *  Description of the Method
	 *
	 * @param  f Description of Parameter
	 * @exception  IOException Description of Exception
	 */
	public void writeDouble(double f) throws IOException
	{
		writeLong(Double.doubleToLongBits(f));
	}

	/**
	 *  Description of the Method
	 *
	 * @param  str Description of Parameter
	 * @exception  IOException Description of Exception
	 */
	public void writeUTF(String str) throws IOException
	{
		int strlen = str.length();
		int utflen = 0;

		for(int i = 0; i < strlen; i++)
		{
			int c = str.charAt(i);
			if((c >= 0x0001) && (c <= 0x007F))
			{
				utflen++;
			}
			else if(c > 0x07FF)
			{
				utflen += 3;
			}
			else
			{
				utflen += 2;
			}
		}
		if(utflen > 65535)
		{
			throw new UTFDataFormatException();
		}
		write((utflen >>> 8) & 0xFF);
		write((utflen >>> 0) & 0xFF);
		for(int i = 0; i < strlen; i++)
		{
			int c = str.charAt(i);
			if((c >= 0x0001) && (c <= 0x007F))
			{
				write(c);
			}
			else if(c > 0x07FF)
			{
				write(0xE0 | ((c >> 12) & 0x0F));
				write(0x80 | ((c >> 6) & 0x3F));
				write(0x80 | ((c >> 0) & 0x3F));
			}
			else
			{
				write(0xC0 | ((c >> 6) & 0x1F));
				write(0x80 | ((c >> 0) & 0x3F));
			}
		}
	}

	/**
	 *  Description of the Method
	 *
	 * @param  b Description of Parameter
	 * @exception  IOException Description of Exception
	 */
	public void readFully(byte[] b) throws IOException
	{
		readFully(b, 0, b.length);
	}

	/**
	 *  Description of the Method
	 *
	 * @param  b Description of Parameter
	 * @param  pos Description of Parameter
	 * @param  len Description of Parameter
	 * @exception  IOException Description of Exception
	 */
	public void readFully(byte[] b, int pos, int len) throws IOException
	{
		int n = 0;
		while(n < len)
		{
			int count = this.read(b, pos + n, len - n);
			if(count < 0)
			{
				throw new EOFException();
			}
			n += count;
		}
	}

	/**
	 *  Description of the Method
	 *
	 * @param  s Description of Parameter
	 * @exception  IOException Description of Exception
	 */
	public void writeBytes(String s) throws IOException
	{
		byte[] b = s.getBytes();
		write(b, 0, b.length);
	}

	/**
	 *  Description of the Method
	 *
	 * @param  s Description of Parameter
	 * @exception  IOException Description of Exception
	 */
	public void writeChars(String s) throws IOException
	{
		int clen = s.length();
		int blen = 2 * clen;
		byte[] b = new byte[blen];
		char[] c = new char[clen];
		s.getChars(0, clen, c, 0);
		for(int i = 0, j = 0; i < clen; i++)
		{
			b[j++] = (byte) (c[i] >>> 8);
			b[j++] = (byte) (c[i] >>> 0);
		}
		write(b, 0, blen);
	}

	/**
	 *  Description of the Method
	 *
	 * @return  Description of the Returned Value
	 * @exception  IOException Description of Exception
	 */
	public long length() throws IOException
	{
		long fileLen = delegate.length();

		if(currBuf.filePos + currBuf.dataLen > fileLen)
		{
			return currBuf.filePos + currBuf.dataLen;
		}
		else
		{
			return fileLen;
		}
	}

	/**
	 *  Description of the Method
	 *
	 * @return  Description of the Returned Value
	 * @exception  IOException Description of Exception
	 */
	public int read() throws IOException
	{
		if(currBuf.pos < currBuf.dataLen)
		{
			// at least one byte is available in the buffer

			return currBuf.bytes[currBuf.pos++];
		}
		else
		{
			syncBuffer(currBuf.filePos + currBuf.pos);
			if(currBuf.dataLen == 0)
			{
				throw new EOFException();
			}
			return read();
			// recurse: should be trivial this time.
		}
	}

	/**
	 *  Description of the Method
	 *
	 * @param  b Description of Parameter
	 * @return  Description of the Returned Value
	 * @exception  IOException Description of Exception
	 */
	public int read(byte[] b) throws IOException
	{
		return read(b, 0, b.length);
	}

	/**
	 *  Description of the Method
	 *
	 * @param  b Description of Parameter
	 * @param  pos Description of Parameter
	 * @param  len Description of Parameter
	 * @return  Description of the Returned Value
	 * @exception  IOException Description of Exception
	 */
	public int read(byte[] b, int pos, int len) throws IOException
	{
		if(currBuf.pos + len <= currBuf.dataLen)
		{
			// enough data available in buffer

			System.arraycopy(currBuf.bytes, currBuf.pos, b, pos, len);
			currBuf.pos += len;
			return len;
		}
		else
		{
			syncBuffer(currBuf.filePos + currBuf.pos);
			// currBuf.pos had better be 0 now.
			if(currBuf.dataLen < currBuf.bytes.length)
			{
				// we have read to EOF: couldn't fill a buffer

				int readLen = Math.min(len, currBuf.dataLen);
				System.arraycopy(currBuf.bytes, currBuf.pos, b, pos, readLen);
				currBuf.pos += readLen;
				return readLen;
			}
			else
			{
				if(currBuf.dataLen <= len)
				{
					return read(b, pos, len);
					// recurse: should be trivial this time
				}
				else
				{
					// too big for a buffer: use the delegate's read.

					delegate.seek(currBuf.filePos);
					int readLen = delegate.read(b, pos, len);
					if(len > currBuf.bytes.length)
					{
						currBuf.filePos += len - currBuf.bytes.length;
					}
					currBuf.dataLen = Math.min(readLen, currBuf.bytes.length);
					System.arraycopy(b, pos, currBuf.bytes, 0, currBuf.dataLen);
					return readLen;
				}
			}
		}
	}

	/**
	 *  Description of the Method
	 *
	 * @param  pos Description of Parameter
	 * @exception  IOException Description of Exception
	 */
	public void seek(long pos) throws IOException
	{
		long newBufPos = pos - currBuf.filePos;
		if(newBufPos >= 0 && newBufPos < currBuf.dataLen)
		{
			// it falls within the buffer

			currBuf.pos = (int) newBufPos;
		}
		else
		{
			syncBuffer(pos);
		}
	}

	/**
	 *  Description of the Method
	 *
	 * @param  n Description of Parameter
	 * @return  Description of the Returned Value
	 * @exception  IOException Description of Exception
	 */
	public int skipBytes(int n) throws IOException
	{
		return (int) skipBytes((long) n);
	}

	/**
	 *  Description of the Method
	 *
	 * @param  n Description of Parameter
	 * @return  Description of the Returned Value
	 * @exception  IOException Description of Exception
	 */
	public long skipBytes(long n) throws IOException
	{
		try
		{
			seek(currBuf.filePos + currBuf.pos + n);
			return n;
		}
		catch(EOFException ex)
		{
			return -1;
		}
	}

	/**
	 *  Description of the Method
	 *
	 * @param  b Description of Parameter
	 * @exception  IOException Description of Exception
	 */
	public void write(byte[] b) throws IOException
	{
		write(b, 0, b.length);
	}

	/**
	 *  Description of the Method
	 *
	 * @param  b Description of Parameter
	 * @param  pos Description of Parameter
	 * @param  len Description of Parameter
	 * @exception  IOException Description of Exception
	 */
	public void write(byte[] b, int pos, int len) throws IOException
	{
		if(pos + len <= currBuf.bytes.length)
		{
			System.arraycopy(b, pos, currBuf.bytes, currBuf.pos, len);
			currBuf.pos += len;
			if(currBuf.pos > currBuf.dataLen)
			{
				currBuf.dataLen = currBuf.pos;
			}
		}
		else
		{
			if(len <= currBuf.bytes.length)
			{
				syncBuffer(currBuf.filePos + currBuf.pos);
				write(b, pos, len);
				// recurse: it should succeed trivially this time.
			}
			else
			{
				// write more than the buffer can contain: use delegate

				delegate.seek(currBuf.filePos + currBuf.pos);
				delegate.write(b, pos, len);
				syncBuffer(currBuf.filePos + currBuf.pos + len);
			}
		}
	}

	/**
	 *  Description of the Method
	 *
	 * @param  b Description of Parameter
	 * @exception  IOException Description of Exception
	 */
	public void write(int b) throws IOException
	{
		if(currBuf.pos < currBuf.bytes.length)
		{
			// trivial write

			currBuf.bytes[currBuf.pos++] = (byte) b;
			currBuf.modified = true;
			if(currBuf.pos > currBuf.dataLen)
			{
				currBuf.dataLen++;
			}
		}
		else
		{
			syncBuffer(currBuf.filePos + currBuf.pos);
			write(b);
			// recurse: should succeed trivially this time.
		}
	}

	// This will do more when dual buffers are implemented.
	//
	/**
	 *  Description of the Method
	 *
	 * @exception  IOException Description of Exception
	 */
	public void flush() throws IOException
	{
		commitBuffer();
		/*
		 * FileBufferStruct temp = currBuf;
		 * try
		 * {
		 * currBuf = altBuf;
		 * commitBuffer();
		 * }
		 * finally
		 * {
		 * currBuf = temp;
		 * }
		 */
	}

	/**
	 *  Description of the Method
	 *
	 * @exception  IOException Description of Exception
	 */
	public void close() throws IOException
	{
		flush();
		delegate.close();
	}

	/**
	 *  Save any changes and re-read the currBuf.bytes from the given position.
	 *  Note that the read(byte[],int,int) method assumes that this method sets
	 *  currBuf.pos to 0.
	 *
	 * @param  new_FP Description of Parameter
	 * @return  int - the number of bytes available for reading
	 * @exception  IOException Description of Exception
	 */
	protected int syncBuffer(long new_FP) throws IOException
	{
		commitBuffer();
		delegate.seek(new_FP);
		currBuf.filePos = new_FP;
		fillBuffer();
		return currBuf.dataLen;
	}

	/**
	 *  Description of the Method
	 *
	 * @exception  IOException Description of Exception
	 */
	protected void fillBuffer() throws IOException
	{
		currBuf.dataLen = delegate.read(currBuf.bytes);
		currBuf.pos = 0;
		if(currBuf.dataLen < 0)
		{
			currBuf.dataLen = 0;
		}
	}

	/**
	 *  If modified, write buffered bytes to the delegate file
	 *
	 * @exception  IOException Description of Exception
	 */
	protected void commitBuffer() throws IOException
	{
		if(currBuf.modified)
		{
			delegate.seek(currBuf.filePos);

			delegate.write(currBuf.bytes, 0, currBuf.dataLen);
			currBuf.modified = false;
		}
	}
    /*
     * Internal structure for holding data
     */
    protected class FileBufferStruct
    {
        /**
         *  Description of the Field
         */
        public byte[] bytes;
        /**
         *  Description of the Field
         */
        public int pos;
        /**
         *  Description of the Field
         */
        public int dataLen;
        /**
         *  Description of the Field
         */
        public boolean modified;
        /**
         *  Description of the Field
         */
        public long filePos;
    }
}

