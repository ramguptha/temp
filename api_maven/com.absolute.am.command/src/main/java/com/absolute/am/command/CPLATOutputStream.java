/**
 * 
 */
package com.absolute.am.command;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.UUID;

import com.absolute.util.PropertyList;
import com.absolute.util.StringUtilities;

/**
 * @author dlavin
 *
 */
public class CPLATOutputStream extends FilterOutputStream {

	/**
	 * @param os the output stream to write to
	 */
	public CPLATOutputStream(OutputStream os) {
		super(os);
	}

	
	public void writeByte(byte value) throws IOException {
		super.write(value);
	}
	
	/**
	 * Shorts are written in big endian order.
	 * @param value
	 * @throws IOException
	 */
	public void writeShort(short value) throws IOException {
		byte[] localBuf = new byte[2];
		
		ByteBuffer bb = ByteBuffer.wrap(localBuf);
		bb.putShort(value);
		super.write(localBuf);
	}
	
	/**
	 * Ints are written in big endian order.
	 * @param value
	 * @throws IOException
	 */
	public void writeInt(int value) throws IOException {
		// TODO: Using ByteBuffer to write an int seems like overkill. Performance test it.
		byte[] localBuf = new byte[4];
		
		ByteBuffer bb = ByteBuffer.wrap(localBuf);
		bb.putInt(value);
		super.write(localBuf);
	}
	
	/**
	 * Longs are written in big endian order.
	 * @param value
	 * @throws IOException
	 */
	public void writeLong(long value) throws IOException {
		byte[] localBuf = new byte[8];
		
		ByteBuffer bb = ByteBuffer.wrap(localBuf);
		bb.putLong(value);
		super.write(localBuf);
	}
		
	/**
	 * Writes a string in CPLAT format. This consists of a byte length, followed
	 * by 2 bytes for each character in the string.
	 * @param s the string to write.
	 * @throws IOException 
	 */
	public void writeString(String s) throws IOException {
		int length = s.length() * 2;
        writeInt(length);  // Length * 2 as each char is 2 bytes.
        
        byte[] stringAsBytes = s.getBytes("UTF-16BE");
                
        super.write(stringAsBytes);
	}
	
	/**
	 * Serialize a GUID to the stream. 
	 * AM serializes GUIDs in binary, i.e. "EC3105E1-B31A-4F80-AC7D-0110D50C144F" would be serialized as EC3105E1B31A4F80AC7D0110D50C144F.
	 * @param uuid
	 * @throws IOException
	 */
	public void writeUUID(UUID uuid) throws IOException {
		// remove the hyphens to get a plain hex string
        String uuidWithoutHyphens = uuid.toString().replace("-", "");
        // convert the hex string to an array of bytes and write that to the stream.
        byte[] uuidAsBinary = StringUtilities.fromHexString(uuidWithoutHyphens);
        super.write(uuidAsBinary);
	}

	/**
	 * A PropertyList is prefixed with a 4 byte magic number, a 4 byte length, and then
	 * the string content in UTF8.
	 * @param plist
	 * @throws IOException
	 */
	public void writePropertyList(PropertyList plist) throws IOException {
		writeInt(PropertyListSerializedMagicNumber);        
        String plistAsXML = plist.toXMLString();            
        byte[] utf8Bytes = plistAsXML.getBytes("UTF-8");
        writeInt((int)utf8Bytes.length);
        write(utf8Bytes, 0, utf8Bytes.length);
	}
	static final int PropertyListSerializedMagicNumber = (int)0xc001cafe; 
	
}
