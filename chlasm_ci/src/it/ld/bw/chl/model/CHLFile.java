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

import java.io.BufferedInputStream;
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
	public static boolean traceEnabled = false;
	
	private Header header = new Header();
	private GlobalVariables globalVariables = new GlobalVariables();
	private Code code = new Code(this);
	private AutoStartScripts autoStartScripts = new AutoStartScripts();
	private Scripts scriptsSection = new Scripts(this);
	private DataSection data = new DataSection();
	private NullSection nullSection = new NullSection(4100);	//<- since CI
	private InitGlobals initGlobals = new InitGlobals();		//<- since CI
	
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
	
	public InitGlobals getInitGlobals() {
		return initGlobals;
	}
	
	public void setInitGlobals(InitGlobals initData) {
		this.initGlobals = initData;
	}
	
	public void read(File file) throws Exception {
		//# Profiler.start();
		try (EndianDataInputStream str = new EndianDataInputStream(new BufferedInputStream(new FileInputStream(file)));) {
			str.order(ByteOrder.LITTLE_ENDIAN);
			if (traceEnabled && !str.markSupported()) {
				System.out.println("NOTICE: input stream doesn't support mark, tracing will be weak");
			}
			int offset = 0;
			if (traceEnabled) System.out.println("Reading header...");
			//# Profiler.start(ProfilerSections.PF_HEADER);
			header.read(str);
			//# Profiler.end(ProfilerSections.PF_HEADER);
			offset += header.getLength();
			//
			if (traceEnabled) System.out.println("Reading global vars...");
			//# Profiler.start(ProfilerSections.PF_GLOBALS);
			globalVariables.setOffset(offset);
			globalVariables.read(str);
			//# Profiler.end(ProfilerSections.PF_GLOBALS);
			offset += globalVariables.getLength();
			//
			if (traceEnabled && str.markSupported()) {
				System.out.println("Reading of code section postponed to empower tracing!");
				str.mark((int)file.length());
				final int codeOffset = offset;
				final int codeSize = str.readInt() * Instruction.LENGTH;
				offset += 4;
				int n = str.skipBytes(codeSize);
				if (n < codeSize) throw new IOException("Unexpected end of code section");
				offset += n;
				//
				System.out.println("Reading autostart scripts...");
				//# Profiler.start(ProfilerSections.PF_AUTOSTART);
				autoStartScripts.setOffset(offset);
				autoStartScripts.read(str);
				//# Profiler.end(ProfilerSections.PF_AUTOSTART);
				offset += autoStartScripts.getLength();
				//
				System.out.println("Reading scripts...");
				//# Profiler.start(ProfilerSections.PF_SCRIPTS);
				scriptsSection.setOffset(offset);
				scriptsSection.read(str);
				//# Profiler.end(ProfilerSections.PF_SCRIPTS);
				offset += scriptsSection.getLength();
				//
				System.out.println("Reading data...");
				//# Profiler.start(ProfilerSections.PF_DATA);
				data.setOffset(offset);
				data.read(str);
				//# Profiler.end(ProfilerSections.PF_DATA);
				offset += data.getLength();
				//
				System.out.println("Reading null section...");
				//# Profiler.start(ProfilerSections.PF_NULL);
				nullSection.setOffset(offset);
				nullSection.read(str);
				//# Profiler.end(ProfilerSections.PF_NULL);
				offset += nullSection.getLength();
				//
				System.out.println("Reading global vars initialization...");
				//# Profiler.start(ProfilerSections.PF_INIT);
				initGlobals.setOffset(offset);
				initGlobals.read(str);
				//# Profiler.end(ProfilerSections.PF_INIT);
				offset += initGlobals.getLength();
				//
				final int endOffset = offset;
				//
				System.out.println("Now reading code...");
				//# Profiler.start(ProfilerSections.PF_CODE);
				str.reset();
				offset = codeOffset;
				code.setOffset(offset);
				code.read(str);
				//# Profiler.end(ProfilerSections.PF_CODE);
				offset += code.getLength();
				//
				System.out.println("Skipping to previous point...");
				n = str.skipBytes(endOffset - offset);
				offset += n;
				if (offset < endOffset) throw new IOException("Failed to skip past data section");
			} else {
				//# Profiler.start(ProfilerSections.PF_CODE);
				code.setOffset(offset);
				code.read(str);
				//# Profiler.end(ProfilerSections.PF_CODE);
				offset += code.getLength();
				//
				//# Profiler.start(ProfilerSections.PF_AUTOSTART);
				autoStartScripts.setOffset(offset);
				autoStartScripts.read(str);
				//# Profiler.end(ProfilerSections.PF_AUTOSTART);
				offset += autoStartScripts.getLength();
				//
				//# Profiler.start(ProfilerSections.PF_SCRIPTS);
				scriptsSection.setOffset(offset);
				scriptsSection.read(str);
				//# Profiler.end(ProfilerSections.PF_SCRIPTS);
				offset += scriptsSection.getLength();
				//
				//# Profiler.start(ProfilerSections.PF_DATA);
				data.setOffset(offset);
				data.read(str);
				//# Profiler.start(ProfilerSections.PF_DATA);
				offset += data.getLength();
				//
				//# Profiler.start(ProfilerSections.PF_NULL);
				nullSection.setOffset(offset);
				nullSection.read(str);
				//# Profiler.end(ProfilerSections.PF_NULL);
				offset += nullSection.getLength();
				//
				//# Profiler.start(ProfilerSections.PF_INIT);
				initGlobals.setOffset(offset);
				initGlobals.read(str);
				//# Profiler.end(ProfilerSections.PF_INIT);
				offset += initGlobals.getLength();
			}
			//
			byte[] t = str.readAllBytes();
			if (t.length > 0) {
				throw new IOException("There are "+t.length+" bytes after the last section (at offset "+offset+")");
			}
		} finally {
			//# Profiler.end();
			//# Profiler.printReport();
		}
	}
	
	public void write(File file) throws Exception {
		//# Profiler.start();
		try (EndianDataOutputStream str = new EndianDataOutputStream(new FileOutputStream(file));) {
			str.order(ByteOrder.LITTLE_ENDIAN);
			int offset = 0;
			//# Profiler.start(ProfilerSections.PF_HEADER);
			header.write(str);
			//# Profiler.end(ProfilerSections.PF_HEADER);
			offset += header.getLength();
			//
			//# Profiler.start(ProfilerSections.PF_GLOBALS);
			globalVariables.setOffset(offset);
			globalVariables.write(str);
			//# Profiler.end(ProfilerSections.PF_GLOBALS);
			offset += globalVariables.getLength();
			//
			//# Profiler.start(ProfilerSections.PF_CODE);
			code.setOffset(offset);
			code.write(str);
			//# Profiler.end(ProfilerSections.PF_CODE);
			offset += code.getLength();
			//
			//# Profiler.start(ProfilerSections.PF_AUTOSTART);
			autoStartScripts.setOffset(offset);
			autoStartScripts.write(str);
			//# Profiler.end(ProfilerSections.PF_AUTOSTART);
			offset += autoStartScripts.getLength();
			//
			//# Profiler.start(ProfilerSections.PF_SCRIPTS);
			scriptsSection.setOffset(offset);
			scriptsSection.write(str);
			//# Profiler.end(ProfilerSections.PF_SCRIPTS);
			offset += scriptsSection.getLength();
			//
			//# Profiler.start(ProfilerSections.PF_DATA);
			data.setOffset(offset);
			data.write(str);
			//# Profiler.end(ProfilerSections.PF_DATA);
			offset += data.getLength();
			//
			//# Profiler.start(ProfilerSections.);
			nullSection.setOffset(offset);
			nullSection.write(str);
			offset += nullSection.getLength();
			//# Profiler.end(ProfilerSections.);
			//
			//# Profiler.start(ProfilerSections.);
			initGlobals.setOffset(offset);
			initGlobals.write(str);
			//# Profiler.end(ProfilerSections.);
		} finally {
			//# Profiler.end();
			//# Profiler.printReport();
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
					instr.validate(this, script, i);
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
			out.println("Autostart scripts: " + e.getMessage());
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
		int[][] map = new int[codes.length][3 + types.length];
		for (Instruction instr : code.getItems()) {
			int c = instr.opcode.ordinal();
			if (instr.flags == 0) map[c][0] |= 1;
			if (instr.flags == 1) map[c][1] |= 1;
			if (instr.flags == 2) map[c][2] |= 1;
			int flags = instr.flags == 0 ? 1 : (instr.flags << 1);
			map[c][3 + instr.dataType.ordinal()] |= flags;
		}
		out.print("OPCode\t0\t1\t2");
		for (int i = 0; i < types.length; i++) {
			out.print("\t" + types[i]);
		}
		out.println();
		final String[] sFlags = new String[] {"", "0", "1", "0,1", "2", "0,2", "1,2"};
		for (int c = 0; c < map.length; c++) {
			out.print(codes[c]);
			for (int i = 0; i < 3; i++) {
				String s = map[c][i] == 0 ? "" : "x";
				out.print("\t" + s);
			}
			for (int i = 3; i < map[c].length; i++) {
				int flags = map[c][i];
				out.print("\t" + sFlags[flags]);
			}
			out.println();
		}
	}
	
	@Override
	public String toString() {
		return header.toString();
	}
}
