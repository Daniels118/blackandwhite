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
import java.util.HashMap;
import java.util.Map;

import it.ld.bw.chl.exceptions.ParseException;

/**This is a very simple parser for C header files, specifically designed for B&W header files.
 * It can parse only enums, can handle single line comments and skips empty lines and compiler directives.
 * Supports both enums with implicit or explicit values, optionally split on multiple lines.
 */
public class CHeaderParser {
	public Map<String, Integer> parse(File file) throws FileNotFoundException, IOException, ParseException {
		Map<String, Integer> res = new HashMap<>();
		int lineno = 0;
		String wholeline = "";
		String sVal = "";
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));) {
			int val = 0;
			String line = "";
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				lineno++;
				line = line.split("//")[0].trim();
				if (line.isEmpty()
						|| line.startsWith("#")
						|| line.equals("{")) continue;
				if (line.startsWith("enum")) {
					val = 0;
				} else {
					wholeline += line;
					if (wholeline.endsWith(",") || wholeline.contains("}")) {
						int p = Math.max(wholeline.indexOf(","), wholeline.indexOf("}"));
						wholeline = wholeline.substring(0, p).trim();
						String[] parts = wholeline.split("=");
						String name = parts[0].trim();
						if (parts.length == 2) {
							sVal = parts[1].trim();
							val = Integer.parseInt(sVal);
						}
						res.put(name, val);
						val++;
						wholeline = "";
					}
				}
			}
		} catch (NumberFormatException e) {
			throw new ParseException("Cannot parse \""+sVal+"\" as int", file, lineno, 1);
		}
		return res;
	}
}
