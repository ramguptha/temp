package com.absolute.util;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;

/**
 * Specialized CipherOutputStream that pads the last block with zeros.
 * This stream keeps track of the total number of bytes written. When
 * flush() is called, it pads the output to an even number of blocks.
 * The size of the block is determined from Cipher.getBlockSize().  
 */
public class ZeroPaddedCipherOutputStream extends CipherOutputStream {
	
	private int bytesWritten = 0;
	private final int blockSize;

	/**
	 * Construct the output stream, wrapping the given stream (os) and use
	 * the given cipher for encryption. 
	 * @param os the output stream to write the cipher text to
	 * @param c the cipher to use to generate the cipher text
	 */
	public ZeroPaddedCipherOutputStream(OutputStream os, Cipher c) {
		super(os, c);
		this.blockSize = c.getBlockSize();
	}
	
	@Override
	public void close() 
		throws IOException {
		super.close();
	}

	/**
	 * The padding happens when this method is called. This method may invoke write() to
	 * write any padding.  
	 */
	@Override
	public void flush() 
		throws IOException {		

		//m_logger.debug("ZeroPaddedCipherOutputStream.flush() bytesWritten=" + bytesWritten + " bytesWritten % " + blockSize + " = " + bytesWritten % blockSize );
		// If the data written so far is not an even block size, then pad the data with zeros to make it even.
		if (bytesWritten % blockSize > 0) {
			int padLen = blockSize - (bytesWritten % blockSize);
			//m_logger.debug("ZeroPaddedCipherOutputStream.flush() padding with " + padLen + " bytes.");
			byte[] padding = new byte[padLen];
			Arrays.fill(padding, (byte)0);
			this.write(padding);
		}
		
		super.flush();
	}

	/**
	 * The number of bytes written is stored.
	 */
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		//m_logger.debug("ZeroPaddedCipherOutputStream.write(byte[] b, " + off + ", " + len + ")");
		super.write(b, off, len);
		bytesWritten += len;
	}

	/**
	 * The number of bytes written is stored.
	 */
	@Override
	public void write(byte[] b) throws IOException {
		//m_logger.debug("ZeroPaddedCipherOutputStream.write(byte[] b) // b.length=" + b.length);
		write(b, 0, b.length);
		// Don't increment bytesWritten because write(byte[], int off, int len) increments it.
	}

	/**
	 * The number of bytes written is stored.
	 */
	@Override
	public void write(int b) throws IOException {
		//m_logger.debug("ZeroPaddedCipherOutputStream.write(int b)");
		super.write(b);
		bytesWritten += 1;
	}
	
}
