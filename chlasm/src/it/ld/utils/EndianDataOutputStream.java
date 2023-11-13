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

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class EndianDataOutputStream extends OutputStream implements DataOutput {
    DataOutputStream dataOut;
    private ByteBuffer buffer = ByteBuffer.allocate(8);
    private byte[] bytes = new byte[8];
    
    public EndianDataOutputStream(OutputStream stream){
        dataOut = new DataOutputStream(stream);
    }
    
    public EndianDataOutputStream order(ByteOrder o){
        buffer.order(o);
        return this;
    }
    
    @Override
    public void write(byte[] b) throws IOException {
        dataOut.write(b);
    }
    
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        dataOut.write(b, off, len);
    }
    
    @Override
    public void writeBoolean(boolean b) throws IOException {
        dataOut.writeBoolean(b);
    }
    
    public void writeByte(byte b) throws IOException {
        dataOut.writeByte(b);
    }
    
    public void writeChar(char c) throws IOException {
        dataOut.writeChar(c);
    }
    
    @Override
    public void writeUTF(String str) throws IOException {
        dataOut.writeUTF(str);
    }
    
    @Override
    public void writeDouble(double d) throws IOException {
    	buffer.clear();
    	buffer.putDouble(d);
    	buffer.flip();
    	buffer.get(bytes, 0, 8);
        dataOut.write(bytes, 0, 8);
    }

    @Override
    public void writeFloat(float f) throws IOException {
    	buffer.clear();
    	buffer.putFloat(f);
    	buffer.flip();
    	buffer.get(bytes, 0, 4);
        dataOut.write(bytes, 0, 4);
    }

    @Override
    public void writeInt(int i) throws IOException {
    	buffer.clear();
    	buffer.putInt(i);
    	buffer.flip();
    	buffer.get(bytes, 0, 4);
        dataOut.write(bytes, 0, 4);
    }

    @Override
    public void writeLong(long l) throws IOException {
    	buffer.clear();
    	buffer.putLong(l);
    	buffer.flip();
    	buffer.get(bytes, 0, 8);
        dataOut.write(bytes, 0, 8);
    }

    @Override
    public void writeShort(int v) throws IOException {
    	buffer.clear();
    	buffer.putShort((short)v);
    	buffer.flip();
    	buffer.get(bytes, 0, 2);
        dataOut.write(bytes, 0, 2);
    }

	@Override
	public void write(int b) throws IOException {
		dataOut.write(b);	//Byte
	}

	@Override
	public void writeByte(int v) throws IOException {
		dataOut.writeByte(v);
	}

	@Override
	public void writeChar(int v) throws IOException {
		dataOut.writeChar(v);
	}

	@Override
	public void writeBytes(String s) throws IOException {
		dataOut.writeBytes(s);
	}

	@Override
	public void writeChars(String s) throws IOException {
		dataOut.writeChars(s);
	}
	
	@Override
	public void flush() throws IOException {
		dataOut.flush();
	}
	
	@Override
	public void close() throws IOException {
		dataOut.close();
	}
}
