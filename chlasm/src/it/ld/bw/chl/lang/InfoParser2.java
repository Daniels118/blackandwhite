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
package it.ld.bw.chl.lang;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.nio.charset.Charset;
import java.util.Map;

import it.ld.bw.chl.exceptions.ParseError;
import it.ld.bw.chl.exceptions.ParseException;

/**This is a very simple parser for text info files that can be found in B&W 1 for MacOS.
 * Supports ASCII and UTF16 with BOM encodings.
 */
public class InfoParser2 {
	private static final Charset ASCII = Charset.forName("windows-1252");
	private static final Charset UTF16 = Charset.forName("UTF-16");
	
	private static final String BAD_EOL = new String(new byte[] {0x0A, 0x0D}, UTF16);
	
	public void parse(File file, Map<String, Integer> dst) throws FileNotFoundException, IOException, ParseException {
		int lineno = 0;
		String name = "";
		String sVal = "";
		try (PushbackInputStream inp = new PushbackInputStream(new FileInputStream(file), 2);) {
			//Handle the BOM
			Charset charset = ASCII;
			byte[] bom = new byte[2];
			inp.read(bom);
			if (bom[0] == (byte)0xFF && bom[1] == (byte)0xFE) {
				charset = UTF16;
			}
			inp.unread(bom);
			//Read the file
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(inp, charset));) {
				String line = "";
				while ((line = reader.readLine()) != null) {
					line = line.trim();
					lineno++;
					if (line.isEmpty() || line.startsWith("#")) continue;
					if (line.endsWith(BAD_EOL)) {
						line = line.substring(0, line.length() - 1);
					}
					//
					if (line.startsWith("NUM_ENTRIES(")) {
						//NUM_ENTRIES(IDENTIFIER)
						
					} else if (line.startsWith("SLONG ")) {
						//SLONG IDENTIFIER
					} else if (line.contains("=")) {
						//IDENTIFIER = NUMBER
						String[] tokens = line.split("\\s*=\\s*");
						if (tokens.length != 2) {
							throw new ParseError("Invalid syntax", file, lineno);
						}
						name = tokens[0];
						sVal = tokens[1];
						int val = Integer.parseInt(sVal);
						//
						Integer oldVal = dst.get(name);
						if (oldVal == null) {
							dst.put(name, val);
						} else if (oldVal != val) {
							throw new ParseError("Redefinition of constant "+name+" with different value", file, lineno);
						}
					} else if (line.startsWith("ADD_TEXT(")) {
						//ADD_TEXT(NUMBER, IDENTIFIER, STRING, STRING)
						
					} else {
						//IDENTIFIER NUMBER
						String[] tokens = line.split("\\s+");
						if (tokens.length < 2) {
							continue;
						}
						if ("value".equalsIgnoreCase(tokens[1])) {
							continue;
						}
						name = tokens[0];
						sVal = tokens[1];
						int val = Integer.parseInt(sVal);
						//
						Integer oldVal = dst.get(name);
						if (oldVal == null) {
							dst.put(name, val);
						} else if (oldVal != val) {
							throw new ParseError("Redefinition of constant "+name+" with different value", file, lineno);
						}
					}
				}
			}
		} catch (NumberFormatException e) {
			throw new ParseException("Cannot parse \""+sVal+"\" as int declaring constant "+name, file, lineno, 1);
		}
	}
}
