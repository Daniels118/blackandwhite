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

/**This is a very simple parser for text info files that can be found in B&W 1 for MacOS.
 */
public class InfoParser2 {
	public Map<String, Integer> parse(File file) throws FileNotFoundException, IOException, ParseException {
		Map<String, Integer> res = new HashMap<>();
		int lineno = 0;
		String sVal = "";
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));) {
			String line = "";
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				lineno++;
				if (line.isEmpty() || line.startsWith("#")) continue;
				
				String[] tokens = line.split("\\s+");
				if (tokens.length < 2) {
					continue;
				}
				if ("value".equalsIgnoreCase(tokens[1])) {
					continue;
				}
				String name = tokens[0];
				int val = Integer.parseInt(tokens[1]);
				res.put(name, val);
			}
		} catch (NumberFormatException e) {
			throw new ParseException("Cannot parse \""+sVal+"\" as int", file, lineno, 1);
		}
		return res;
	}
}
