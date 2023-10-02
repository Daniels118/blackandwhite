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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import it.ld.utils.EndianDataInputStream;
import it.ld.utils.EndianDataOutputStream;


public class CHLFile {
	private Header header = new Header();
	private GlobalVariables globalVariables = new GlobalVariables();
	private Code code = new Code();
	private AutoStartScripts autoStartScripts = new AutoStartScripts();
	private Scripts scriptsSection = new Scripts();
	private DataSection data = new DataSection();
	
	public Header getHeader() {
		return header;
	}
	
	public void setHeader(Header header) {
		this.header = header;
	}
	
	public GlobalVariables getGlobalVariables() {
		return globalVariables;
	}
	
	public void setGlobalVariables(GlobalVariables globalVariables) {
		this.globalVariables = globalVariables;
	}
	
	public Code getCode() {
		return code;
	}
	
	public void setCode(Code code) {
		this.code = code;
	}
	
	public AutoStartScripts getAutoStartScripts() {
		return autoStartScripts;
	}
	
	public void setAutoStartScripts(AutoStartScripts autoStartScripts) {
		this.autoStartScripts = autoStartScripts;
	}
	
	public Scripts getScriptsSection() {
		return scriptsSection;
	}
	
	public void setScriptsSection(Scripts scripts) {
		this.scriptsSection = scripts;
	}
	
	public DataSection getDataSection() {
		return data;
	}
	
	public void setDataSection(DataSection data) {
		this.data = data;
	}
	
	public void read(File file) throws Exception {
		try (EndianDataInputStream str = new EndianDataInputStream(new FileInputStream(file));) {
			str.order(ByteOrder.LITTLE_ENDIAN);
			int offset = 0;
			header = new Header();
			header.setOffset(offset);
			header.read(str);
			offset += header.getLength();
			//
			globalVariables = new GlobalVariables();
			globalVariables.setOffset(offset);
			globalVariables.read(str);
			offset += globalVariables.getLength();
			//
			code = new Code();
			code.setOffset(offset);
			code.read(str);
			offset += code.getLength();
			//
			autoStartScripts = new AutoStartScripts();
			autoStartScripts.setOffset(offset);
			autoStartScripts.read(str);
			offset += autoStartScripts.getLength();
			//
			scriptsSection = new Scripts();
			scriptsSection.setOffset(offset);
			scriptsSection.read(str);
			offset += scriptsSection.getLength();
			//
			data = new DataSection();
			data.setOffset(offset);
			data.read(str);
			//
			byte[] t = str.readAllBytes();
			if (t.length > 0) throw new IOException("There are "+t.length+" bytes after the last section");
		}
	}
	
	public void write(File file) throws Exception {
		try (EndianDataOutputStream str = new EndianDataOutputStream(new FileOutputStream(file));) {
			str.order(ByteOrder.LITTLE_ENDIAN);
			int offset = 0;
			header.write(str);
			offset += header.getLength();
			//
			globalVariables.setOffset(offset);
			globalVariables.write(str);
			offset += globalVariables.getLength();
			//
			code.setOffset(offset);
			code.write(str);
			offset += code.getLength();
			//
			autoStartScripts.setOffset(offset);
			autoStartScripts.write(str);
			offset += autoStartScripts.getLength();
			//
			scriptsSection.setOffset(offset);
			scriptsSection.write(str);
			offset += scriptsSection.getLength();
			//
			data.setOffset(offset);
			data.write(str);
		}
	}
	
	public boolean validate(PrintStream out) {
		boolean res = true;
		//Code
		List<Instruction> instructions = code.getItems();
		for (Script script : scriptsSection.getItems()) {
			for (int i = script.getInstructionAddress(); i < instructions.size(); i++) {
				Instruction instr = instructions.get(i);
				try {
					instr.validate(this, script);
				} catch (Exception e) {
					res = false;
					int offset = code.getOffset() + 4 + i * Instruction.LENGTH;
					String fmt = "%1$s in %2$s at %3$s:%4$d (0x%5$08X)\r\n";
					out.printf(fmt, e.getMessage(), script.getName(), script.getSourceFilename(), instr.lineNumber, offset);
				}
				if (instr.opcode == OPCode.END) break;
			}
		}
		//Autostart scripts
		try {
			autoStartScripts.validate(this);
		} catch (Exception e) {
			res = false;
			out.println("Autostart scripts: " + e.getMessage() + "\r\n");
		}
		return res;
	}
	
	public boolean checkCodeCoverage(PrintStream out) {
		boolean res = true;
		List<Instruction> instructions = code.getItems();
		int index = 0;
		for (Script script : scriptsSection.getItems()) {
			if (index != script.getInstructionAddress()) {
				out.println("WARNING: there are unused instructions before script "+script.getName());
				res = false;
			}
			index = script.getInstructionAddress();
			while (instructions.get(index++).opcode != OPCode.END) {}
		}
		if (index < instructions.size()) {
			out.println("WARNING: there are unused instructions after last script");
			res = false;
		}
		return res;
	}
	
	public List<String> getSourceFilenames() {
		List<String> res = new ArrayList<String>();
		String prev = "";
		for (Script script : scriptsSection.getItems()) {
			String scrName = script.getSourceFilename();
			if (!scrName.equals(prev)) {
				res.add(scrName);
				prev = scrName;
			}
		}
		return res;
	}
	
	public List<Script> getScripts(String sourceFilename) {
		List<Script> res = new ArrayList<Script>();
		for (Script script : scriptsSection.getItems()) {
			if (sourceFilename.equals(script.getSourceFilename())) {
				res.add(script);
			}
		}
		return res;
	}
	
	public void printInstructionReference(PrintStream out) {
		OPCode[] codes = OPCode.values();
		DataType[] types = DataType.values();
		int[][] map = new int[codes.length][2 + types.length];
		for (Instruction instr : code.getItems()) {
			int c = instr.opcode.ordinal();
			if (instr.flags == 0) map[c][0] |= 1;
			if (instr.flags == 1) map[c][1] |= 1;
			int flags = instr.flags == 0 ? 1 : 2;
			map[c][2 + instr.dataType.ordinal()] |= flags;		//1=flags=0, 2=flags=1, 3=both
		}
		out.print("OPCode\t0\t1");
		for (int i = 0; i < types.length; i++) {
			out.print("\t" + types[i]);
		}
		out.println();
		for (int c = 0; c < map.length; c++) {
			out.print(codes[c]);
			for (int i = 0; i < map[c].length; i++) {
				int flags = map[c][i];
				out.print("\t" + (flags == 0 ? "" : flags));
			}
			out.println();
		}
	}
	
	@Override
	public String toString() {
		return header.toString();
	}
}
