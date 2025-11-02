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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.ld.utils.EndianDataInputStream;
import it.ld.utils.EndianDataOutputStream;

public class GlobalVariables extends Struct {
	private List<String> names = new ArrayList<String>();
	
	private Map<String, Integer> varsMap = new HashMap<>();
	
	public List<String> getNames() {
		return names;
	}
	
	public void setNames(List<String> names) {
		this.names = names;
	}
	
	public int getVarId(String name) {
		//Fast "running-cache" algorithm
		for (int i = varsMap.size(); i < names.size(); i++) {
			String tName = names.get(i);
			varsMap.put(tName, i + 1);
		}
		Integer id = varsMap.get(name);
		if (id != null) return id;
		return -1;
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
