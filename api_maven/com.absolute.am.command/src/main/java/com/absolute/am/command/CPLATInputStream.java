/**
 * 
 */
package com.absolute.am.command;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.absolute.util.PropertyList;
import com.absolute.util.StringUtilities;

/**
 * @author dlavin
 *
 */
public class CPLATInputStream extends FilterInputStream {

	/**
	 * @param is the input stream to read from
	 */
	public CPLATInputStream(InputStream is) {
		super(is);
	}
	
	public byte readByte() throws IOException {		
		int val = in.read();
		if (-1 == val) {
			throw new EOFException("Not enough data to read a Byte.");
		}
		return (byte)val;
	}

	public short readShort() throws IOException {
		byte[] localBuf = new byte[2];
		int ret = read(localBuf);
		if (ret != localBuf.length) {
			throw new EOFException("Not enough data to read a Short.");
		}
		return ByteBuffer.wrap(localBuf).getShort();
	}
	
	public int readInt() throws IOException {
		byte[] localBuf = new byte[4];
		int ret = read(localBuf);
		if (ret != localBuf.length) {
			throw new EOFException("Not enough data to read an Int.");
		}
		return ByteBuffer.wrap(localBuf).getInt();
	}

	public long readLong() throws IOException {
		byte[] localBuf = new byte[8];
		int ret = read(localBuf);
		if (ret != localBuf.length) {
			throw new EOFException("Not enough data to read a Long.");
		}
		return ByteBuffer.wrap(localBuf).getLong();
	}
	
	public String readString() throws IOException {
		int len = this.readInt();
		if ((len%2) != 0) {
			throw new RuntimeException("Bad string length in stream, not divisible by 2.");
		}
		
		byte[] localBuf = new byte[len];
		int lenRead = read(localBuf);
		if (lenRead != len) {
			throw new EOFException("Not enough data (" + lenRead + ") to read complete (" + len + ") String.");
		}
		return new String(localBuf, "UTF-16BE");
	}

	public UUID readUUID() throws IOException {
		byte[] uuidAsBinary = new byte[16];
		int len = this.read(uuidAsBinary);
		if (len != uuidAsBinary.length) {
			throw new EOFException("Not enough data to read UUID.");
		}
		// Convert to hex and re-insert the hyphens
        String uuidWithoutHyphens = StringUtilities.toHexString(uuidAsBinary);

        StringBuilder sb = new StringBuilder();
        sb.append(uuidWithoutHyphens.substring(0,8));
        sb.append('-');
        sb.append(uuidWithoutHyphens.substring(8,12));
        sb.append('-');
        sb.append(uuidWithoutHyphens.substring(12,16));
        sb.append('-');
        sb.append(uuidWithoutHyphens.substring(16,20));
        sb.append('-');
        sb.append(uuidWithoutHyphens.substring(20,32));

        UUID retVal = UUID.fromString(sb.toString());
        
		return retVal;
	}

	/**
	 * Attempts to read a PropertyList from the stream.
	 * @return
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * @throws IOExceptin
	 */
	public PropertyList readPropertyList() throws IOException, ParserConfigurationException, SAXException {

		int magic = readInt();
        if (magic != CPLATOutputStream.PropertyListSerializedMagicNumber) {
            throw new RuntimeException("PropertyList magic byte sequence not found.");
        }
        // TODO is there a limit that can be used to sanity check the length.
        int utf8BytesToRead = readInt();
        
		// read from the stream in blocks, until nothing remains
        // Note: based on testing, the input streams use internal buffering and the max bytes a read will return is the size of that
        // internal buffer. As a result, we have to read chunks in a loop until we have all the data we are looking for.
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[128];
		int amountThisRead = Math.min(utf8BytesToRead, buf.length);
		int i = read(buf, 0, amountThisRead);
		while ((amountThisRead > 0) && (i != -1)) {
			baos.write(buf, 0, i);
			utf8BytesToRead -= i;
			amountThisRead = Math.min(utf8BytesToRead, buf.length);
			i = read(buf, 0, amountThisRead);
		}
		if (utf8BytesToRead > 0) {
			throw new EOFException("Failed to read all of PropertyList, " + utf8BytesToRead + " bytes missing.");
		}
		
		baos.flush();
		byte[] xmlUtf8 = baos.toByteArray();             
//        
//        byte[] xmlUtf8 = new byte[utf8BytesToRead];
//        int lengthRead = read(xmlUtf8);
//        if (lengthRead != utf8BytesToRead) {
//        	throw new EOFException("Insufficient bytes to read PropertyList, read " + lengthRead + " expected " + utf8BytesToRead);
//        }
        
        PropertyList retVal = PropertyList.fromByteArray(xmlUtf8);
        return retVal;

	}
}
