/* Copyright (c) 2023-2025 Daniele Lombardi / Daniels118
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
import java.util.LinkedList;
import java.util.List;

import it.ld.utils.EndianDataInputStream;
import it.ld.utils.EndianDataOutputStream;

public class DataSection extends Struct {
	private byte[] data;
	
	public byte[] getData() {
		return data;
	}
	
	public void setData(byte[] data) {
		this.data = data;
	}
	
	@Override
	public void read(EndianDataInputStream str) throws Exception {
		int count = str.readInt();
		data = str.readNBytes(count);
		if (data.length < count) {
			throw new EOFException("Unexpected end of file while reading data section ("+data.length+" bytes read out of "+count+")");
		}
	}

	@Override
	public void write(EndianDataOutputStream str) throws Exception {
		str.writeInt(data.length);
		str.write(data);
	}
	
	public String getString(int offset) {
		int n = 0;
		while (data[offset + n] != 0) {
			n++;
			if (offset + n >= data.length) {
				throw new RuntimeException("Missing null terminator for string at offset "+offset);
			}
		}
		return new String(data, offset, n, ASCII);
	}
	
	public List<StringData> getStrings() {
		List<StringData> res = new LinkedList<StringData>();
		int offset = 0;
		while (offset < data.length) {
			int n = getZString(data, offset);
			StringData c = new StringData(data, offset, n);
			res.add(c);
			offset += n + 1;
		}
		return res;
	}
	
	@Override
	public String toString() {
		return "[" + data.length + " bytes of data]";
	}
	
	private static boolean isPrintable(char c) {
		return c > 31;
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
	
	public static class StringData {
		private final byte[] data;
		public final int offset;
		public final int length;
		
		private String str = null;
		
		public StringData(byte[] data, int offset, int length) {
			this.data = data;
			this.offset = offset;
			this.length = length;
		}
		
		public byte[] getBytes() {
			byte[] bytes = new byte[length + 1];
			System.arraycopy(data,  offset, bytes, 0, length + 1);
			return bytes;
		}
		
		public String getString() {
			if (str == null) {
				str = new String(data, offset, length, ASCII);
			}
			return str;
		}
		
		public String getDeclaration() {
			return String.format("string c%d = ", offset) + toString();
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof StringData)) return false;
			StringData other = (StringData) obj;
			return this.getString().equals(other.getString());
		}
		
		@Override
		public int hashCode() {
			return getString().hashCode();
		}
		
		@Override
		public String toString() {
			String t = getString();
			//t = t.replace("\\", "\\\\");
			t = t.replace("\"", "\\\"");
			return "\"" + t + "\"";
		}
	}
}
