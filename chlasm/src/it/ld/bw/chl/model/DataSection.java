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
package it.ld.bw.chl.model;

import java.io.EOFException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

import it.ld.utils.EndianDataInputStream;
import it.ld.utils.EndianDataOutputStream;

public class DataSection extends Section {
	private byte[] data;
	
	public byte[] getData() {
		return data;
	}
	
	public void setData(byte[] data) {
		this.data = data;
	}
	
	@Override
	public int getLength() {
		return 4 + data.length;
	}
	
	@Override
	public void read(EndianDataInputStream str) throws Exception {
		int count = str.readInt();
		data = str.readNBytes(count);
		if (data.length < count) throw new EOFException("End of file ("+data.length+" bytes read out of "+count+")");
	}

	@Override
	public void write(EndianDataOutputStream str) throws Exception {
		str.writeInt(data.length);
		str.write(data);
	}
	
	public List<Const> analyze() {
		List<Const> res = new LinkedList<Const>();
		int offset = 0;
		while (offset < data.length) {
			int n = getBinaryNumber(data, offset);
			if (n > 0) {
				if (n % 12 == 0) {
					for (int i = 0; i < n; i += 12, offset += 12) {
						res.add(new Const(data, offset, 12, ConstType.VEC3));
					}
				} else {
					for (int i = 0; i < n; i += 4, offset += 4) {
						Const c = new Const(data, offset, 4, ConstType.FLOAT);
						res.add(c);
					}
				}
			} else {
				n = getZString(data, offset);
				if (n > 0) {
					Const c = new Const(data, offset, n, ConstType.STRING);
					res.add(c);
					offset += n + 1;
				} else {
					n = 1;
					Const c = new Const(data, offset, 1, ConstType.BYTE);
					res.add(c);
					offset += n;
				}
			}
		}
		return res;
	}
	
	@Override
	public String toString() {
		return "[" + data.length + " bytes of data]";
	}
	
	private static boolean isPrintable(char c) {
		return c > 31 && c < 127;
	}
	
	private static int getZString(byte[] data, int offset) {
		int n = 0;
		while (data[offset] != 0) {
			if (!isPrintable((char)data[offset])) return -1;
			n++;
			offset++;
			if (offset >= data.length) return -1;
		}
		return n;
	}
	
	private static int getBinaryNumber(byte[] data, int offset) {
		int n = 0;
		int printable = 0;
		while (offset < data.length) {
			if (isPrintable((char)data[offset])) {
				printable++;
				if (printable >= 4) break;
			} else {
				printable = 0;
			}
			n++;
			offset++;
		}
		return n & 0xFFFC;	//Clear the last 2 bits to round down to a multiple of 4
	}
	
	public enum ConstType {
		BYTE("byte"),
		INT("int"),
		FLOAT("float"),
		VEC3("float[] "),
		BYTEARRAY("byte[]"),
		STRING("string");
		
		public final String keyword;
		
		ConstType(String keyword) {
			this.keyword = keyword;
		}
	}
	
	public static class Const {
		private final byte[] data;
		public final int offset;
		public final int length;
		public final ConstType type;
		
		public Const(byte[] data, int offset, int length, ConstType type) {
			this.data = data;
			this.offset = offset;
			this.length = length;
			this.type = type;
		}
		
		public byte getByte() {
			return data[offset];
		}
		
		public byte[] getByteArray() {
			byte[] res = new byte[length];
			System.arraycopy(data, offset, res, 0, length);
			return res;
		}
		
		public int getInt() {
			ByteBuffer buffer = ByteBuffer.allocate(length);
			buffer.order(ByteOrder.LITTLE_ENDIAN);
			buffer.put(data, offset, length);
			buffer.flip();
			return buffer.getInt();
		}
		
		public float getFloat() {
			ByteBuffer buffer = ByteBuffer.allocate(length);
			buffer.order(ByteOrder.LITTLE_ENDIAN);
			buffer.put(data, offset, length);
			buffer.flip();
			return buffer.getFloat();
		}
		
		public float[] getVec3() {
			ByteBuffer buffer = ByteBuffer.allocate(length);
			buffer.order(ByteOrder.LITTLE_ENDIAN);
			buffer.put(data, offset, length);
			buffer.flip();
			return new float[] {buffer.getFloat(), buffer.getFloat(), buffer.getFloat()};
		}
		
		public String getString() {
			final Charset ASCII = Charset.forName("US-ASCII");
			return new String(data, offset, length, ASCII);
		}
		
		public String getDeclaration() {
			return String.format("%1$s c%2$d = ", type.keyword, offset) + toString();
		}
		
		@Override
		public String toString() {
			switch (type) {
				case BYTE:
					return String.valueOf(getByte());
				case BYTEARRAY:
					return String.valueOf(getByteArray());
				case FLOAT:
					return String.valueOf(getFloat());
				case INT:
					return String.valueOf(getInt());
				case STRING:
					String t = getString();
					t = t.replace("\\", "\\\\");
					t = t.replace("\"", "\\\"");
					return "\"" + t + "\"";
				case VEC3:
					return String.valueOf(getVec3());
				default:
					throw new RuntimeException("Unsupported constant type: "+type);
			}
		}
	}
}
