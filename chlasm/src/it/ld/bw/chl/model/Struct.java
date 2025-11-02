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

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import it.ld.utils.EndianDataInputStream;
import it.ld.utils.EndianDataOutputStream;

/**Base class with helper methods to read and write both fixed and variable length struct.
 * 
 */
public abstract class Struct {
	protected static final Charset ASCII = Charset.forName("windows-1252");
	
	/**Read this struct from a stream.
	 * @param str
	 * @throws Exception
	 */
	public abstract void read(EndianDataInputStream str) throws Exception;
	
	/**Write this struct to a stream.
	 * @param str
	 * @throws Exception
	 */
	public abstract void write(EndianDataOutputStream str) throws Exception;
	
	/**Reads a null-terminated ASCII string from a stream.
	 * @param str
	 * @return
	 * @throws IOException
	 */
	protected static String readZString(EndianDataInputStream str) throws IOException {
		byte[] buf = new byte[256];
		int l = 0;
		byte b = str.readByte();
		while (b != 0) {
			if (l >= buf.length) {
				byte[] newBuf = new byte[buf.length * 2];
				System.arraycopy(buf, 0, newBuf, 0, l);
				buf = newBuf;
			}
			buf[l++] = b;
			b = str.readByte();
		}
		String s = new String(buf, 0, l, ASCII);
		return s;
	}
	
	/**Writes a null-terminated ASCII string to a stream.
	 * @param str
	 * @param s
	 * @throws IOException
	 */
	protected static void writeZString(EndianDataOutputStream str, String s) throws IOException {
		byte[] buf = s.getBytes(ASCII);
		str.write(buf);
		str.writeByte(0);
	}
	
	/**Reads an array of null-terminated strings from a stream.
	 * @param str
	 * @return
	 * @throws IOException
	 */
	protected static ArrayList<String> readZStringArray(EndianDataInputStream str) throws IOException {
		int count = str.readInt();
		ArrayList<String> res = new ArrayList<String>(count);
		for (int i = 0; i < count; i++) {
			String v = readZString(str);
			res.add(v);
		}
		return res;
	}
	
	/**Writes an array of null-terminated strings to a stream.
	 * @param str
	 * @param strings
	 * @throws IOException
	 */
	protected static void writeZStringArray(EndianDataOutputStream str, List<String> strings) throws IOException {
		str.writeInt(strings.size());
		for (String s : strings) {
			writeZString(str, s);
		}
	}
	
	/**Calculates the size in bytes of an array of null-terminated strings.
	 * @param strings
	 * @return
	 */
	protected static int getZStringArraySize(List<String> strings) {
		int l = 4;
		for (String s : strings) {
			l += s.length() + 1;
		}
		return l;
	}
	
	protected static LinkedHashMap<String, Integer> readMapOfStringInt(EndianDataInputStream str) throws IOException {
		int count = str.readInt();
		LinkedHashMap<String, Integer> res = new LinkedHashMap<String, Integer>(count);
		for (int i = 0; i < count; i++) {
			String key = readZString(str);
			int val = str.readInt();
			res.put(key, val);
		}
		return res;
	}
	
	protected static void writeMapOfStringInt(EndianDataOutputStream str, Map<String, Integer> map) throws IOException {
		str.writeInt(map.size());
		for (Entry<String, Integer> e : map.entrySet()) {
			writeZString(str, e.getKey());
			str.writeInt(e.getValue());
		}
	}
	
	protected static int getMapOfStringIntSize(Map<String, Integer> map) {
		int l = 4;
		for (String s : map.keySet()) {
			l += s.length() + 1 + 4;
		}
		return l;
	}
	
	protected static ArrayList<Integer> readIntArray(EndianDataInputStream str) throws IOException {
		int count = str.readInt();
		ArrayList<Integer> res = new ArrayList<Integer>(count);
		for (int i = 0; i < count; i++) {
			int val = str.readInt();
			res.add(val);
		}
		return res;
	}
	
	protected static void writeIntArray(EndianDataOutputStream str, List<Integer> vals) throws IOException {
		str.writeInt(vals.size());
		for (Integer val : vals) {
			str.writeInt(val);
		}
	}
}
