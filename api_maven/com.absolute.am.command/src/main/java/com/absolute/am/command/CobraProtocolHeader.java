/**
 * 
 */
package com.absolute.am.command;

import java.io.IOException;

/**
 * @author dlavin
 *
 */
class CobraProtocolHeader {
    public byte    Version;
    public byte    Reserved1;
    public short  Flags;
    public int  Reserved2;
    public int  Reserved3;
    public int  Reserved4;

    public void Write(CPLATOutputStream os) throws IOException
    {
        os.writeByte(Version);
        os.writeByte(Reserved1);
        os.writeShort(Flags);
        os.writeInt(Reserved2);
        os.writeInt(Reserved3);
        os.writeInt(Reserved4);
    }

    public void Read(CPLATInputStream is) throws IOException
    {
        Version = is.readByte();
        Reserved1 = is.readByte();
        Flags = is.readShort();
        Reserved2 = is.readInt();
        Reserved3 = is.readInt();
        Reserved4 = is.readInt();
    }
}
