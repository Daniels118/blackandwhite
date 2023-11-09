/* Copyright (c) 2023 Daniele Lombardi / Daniels118
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.ld.utils;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class EndianDataInputStream extends InputStream implements DataInput {
    DataInputStream dataIn;
    private ByteBuffer buffer = ByteBuffer.allocate(8);
    private byte[] raw = new byte[8];
    
    public EndianDataInputStream(InputStream stream){
        dataIn = new DataInputStream(stream);
    }
    
    public EndianDataInputStream order(ByteOrder o){
        buffer.order(o);
        return this;
    }
    
    @Override
    public int read(byte[] b) throws IOException {
        return dataIn.read(b);
    }
    
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return dataIn.read(b, off, len);
    }
    
    @Deprecated
    @Override
    public String readLine() throws IOException {
        return dataIn.readLine();
    }
    
    @Override
    public boolean readBoolean() throws IOException {
        return dataIn.readBoolean();
    }
    
    @Override
    public byte readByte() throws IOException {
        return dataIn.readByte();
    }
    
    @Override
    public int read() throws IOException {
        return readByte();
    }
    
    @Override
    public boolean markSupported(){
        return dataIn.markSupported();
    }
    
    @Override
    public void mark(int readlimit) {
        dataIn.mark(readlimit);
    }
    
    @Override
    public void reset() throws IOException {
        dataIn.reset();
    }
    
    @Override
    public char readChar() throws IOException {
        return dataIn.readChar();
    }
    
    @Override
    public void readFully(byte[] b) throws IOException {
        dataIn.readFully(b);
    }
    
    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        dataIn.readFully(b, off, len);
    }
    
    @Override
    public String readUTF() throws IOException {
        return dataIn.readUTF();
    }
    
    @Override
    public int skipBytes(int n) throws IOException {
        return dataIn.skipBytes(n);
    }
    
    @Override
    public double readDouble() throws IOException {
    	buffer.clear();
    	dataIn.readNBytes(raw, 0, 8);
    	buffer.put(raw, 0, 8);
    	buffer.flip();
        return buffer.getDouble();
    }

    @Override
    public float readFloat() throws IOException {
    	buffer.clear();
    	dataIn.readNBytes(raw, 0, 4);
    	buffer.put(raw, 0, 4);
    	buffer.flip();
        return buffer.getFloat();
    }

    @Override
    public int readInt() throws IOException {
    	buffer.clear();
    	dataIn.readNBytes(raw, 0, 4);
    	buffer.put(raw, 0, 4);
    	buffer.flip();
        return buffer.getInt();
    }

    @Override
    public long readLong() throws IOException {
    	buffer.clear();
    	dataIn.readNBytes(raw, 0, 8);
    	buffer.put(raw, 0, 8);
    	buffer.flip();
        return buffer.getLong();
    }

    @Override
    public short readShort() throws IOException {
    	buffer.clear();
    	dataIn.readNBytes(raw, 0, 2);
    	buffer.put(raw, 0, 2);
    	buffer.flip();
        return buffer.getShort();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return (int)dataIn.readByte();
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return (int)readShort();
    }
}
