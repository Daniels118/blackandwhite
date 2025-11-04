/* Copyright (c) 2025 Daniele Lombardi / Daniels118
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import it.ld.bw.chl.exceptions.InvalidObjCodeException;
import it.ld.utils.EndianDataInputStream;
import it.ld.utils.EndianDataOutputStream;

public class ObjectCode extends Struct {
	private String magic = "LHVO";
	private int version = 1;
	private LinkedHashMap<String, Integer> externalVars = new LinkedHashMap<>();
	private LinkedHashMap<String, Integer> externalScripts = new LinkedHashMap<>();
	private List<Integer> stringInstructions = new LinkedList<>();
	private final CHLFile chl = new CHLFile();
	
	public File file;
	
	public ObjectCode() {
		
	}
	
	public String getMagic() {
		return magic;
	}
	
	public void setMagic(String magic) throws InvalidObjCodeException {
		if (!("LHVO".equals(magic))) throw new InvalidObjCodeException("Invalid CHL object file (wrong magic string)");
		this.magic = magic;
	}
	
	public int getVersion() {
		return version;
	}
	
	public void setVersion(int version) throws InvalidObjCodeException {
		if (version != 1) throw new InvalidObjCodeException("CHL object file version not supported: " + version);
		this.version = version;
	}
	
	public LinkedHashMap<String, Integer> getExternalVars() {
		return this.externalVars;
	}
	
	public void setExternalVars(LinkedHashMap<String, Integer> vars) {
		this.externalVars = vars;
	}
	
	public LinkedHashMap<String, Integer> getExternalScripts() {
		return this.externalScripts;
	}
	
	public void setExternalScripts(LinkedHashMap<String, Integer> scripts) {
		this.externalScripts = scripts;
	}
	
	public List<Integer> getStringInstructions() {
		return stringInstructions;
	}
	
	public void setStringInstructions(List<Integer> stringInstructions) {
		this.stringInstructions = stringInstructions;
	}
	
	public CHLFile getChl() {
		return chl;
	}
	
	public int getExternalVarId(String name, int index) {
		String key = name + "+" + index;
		Integer id = externalVars.get(key);
		if (id == null) {
			id = externalVars.size() + 1;
			externalVars.put(key, id);
		}
		return id;
	}
	
	public int getExternalScriptId(String name, int parameterCount) {
		String key = name + "@" + parameterCount;
		Integer id = externalScripts.get(key);
		if (id == null) {
			id = externalScripts.size() + 1;
			externalScripts.put(key, id);
		}
		return id;
	}
	
	public void read(File file) throws Exception {
		this.file = file;
		try (EndianDataInputStream str = new EndianDataInputStream(new BufferedInputStream(new FileInputStream(file)));) {
			read(str);
		}
	}
	
	public void write(File file) throws Exception {
		try (EndianDataOutputStream str = new EndianDataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));) {
			write(str);
		}
	}

	@Override
	public void read(EndianDataInputStream str) throws Exception {
		setMagic(new String(str.readNBytes(4), ASCII));
		setVersion(str.readInt());
		externalVars = readMapOfStringInt(str);
		externalScripts = readMapOfStringInt(str);
		stringInstructions = readIntArray(str);
		chl.read(str);
	}

	@Override
	public void write(EndianDataOutputStream str) throws Exception {
		str.write(magic.getBytes(ASCII));
		str.writeInt(version);
		writeMapOfStringInt(str, externalVars);
		writeMapOfStringInt(str, externalScripts);
		writeIntArray(str, stringInstructions);
		chl.write(str);
	}
}
