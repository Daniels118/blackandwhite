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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.ld.utils.EndianDataInputStream;
import it.ld.utils.EndianDataOutputStream;

public class GlobalVariables extends Section {
	private List<String> names = new ArrayList<String>();
	
	@Override
	public int getLength() {
		return getZStringArraySize(names);
	}

	public List<String> getNames() {
		return names;
	}
	
	@Override
	public void read(EndianDataInputStream str) throws IOException {
		names = readZStringArray(str);
	}

	@Override
	public void write(EndianDataOutputStream str) throws IOException {
		writeZStringArray(str, names);
	}
	
	@Override
	public String toString() {
		return names.toString();
	}
	
	public String getCode() {
		StringBuffer s = new StringBuffer();
		int i = 1;
		for (String name : names) {
			s.append("GLOBAL "+name+"\t//"+(i++)+"\r\n");
		}
		return s.toString();
	}
}
