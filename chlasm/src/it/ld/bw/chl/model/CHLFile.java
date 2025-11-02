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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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


public class CHLFile extends Struct {
	public static boolean traceEnabled = false;
	
	public final Header header = new Header();
	public final GlobalVariables globalVars = new GlobalVariables();
	public final Code code = new Code(this);
	public final AutoStartScripts autoStartScripts = new AutoStartScripts();
	public final Scripts scripts = new Scripts(this);
	public final DataSection data = new DataSection();
	
	public Header getHeader() {
		return header;
	}
	
	public GlobalVariables getGlobalVariables() {
		return globalVars;
	}
	
	public Code getCode() {
		return code;
	}
	
	public AutoStartScripts getAutoStartScripts() {
		return autoStartScripts;
	}
	
	public Scripts getScriptsSection() {
		return scripts;
	}
	
	public DataSection getDataSection() {
		return data;
	}
	
	public void read(File file) throws Exception {
		try (EndianDataInputStream str = new EndianDataInputStream(new BufferedInputStream(new FileInputStream(file)));) {
			read(str);
		} catch (Exception e) {
			throw new Exception(e.getMessage() + ", reading " + file.getName(), e);
		}
	}
	
	@Override
	public void read(EndianDataInputStream str) throws Exception {
		//# Profiler.start();
		try {
			str.order(ByteOrder.LITTLE_ENDIAN);
			if (traceEnabled && !str.markSupported()) {
				System.out.println("NOTICE: input stream doesn't support mark, tracing will be weak");
			}
			if (traceEnabled) System.out.println("Reading header...");
			//# Profiler.start(ProfilerSections.PF_HEADER);
			header.read(str);
			//# Profiler.end(ProfilerSections.PF_HEADER);
			//
			if (traceEnabled) System.out.println("Reading global vars...");
			//# Profiler.start(ProfilerSections.PF_GLOBALS);
			globalVars.read(str);
			//# Profiler.end(ProfilerSections.PF_GLOBALS);
			//
			//# Profiler.start(ProfilerSections.PF_CODE);
			code.read(str);
			//# Profiler.end(ProfilerSections.PF_CODE);
			//
			//# Profiler.start(ProfilerSections.PF_AUTOSTART);
			autoStartScripts.read(str);
			//# Profiler.end(ProfilerSections.PF_AUTOSTART);
			//
			//# Profiler.start(ProfilerSections.PF_SCRIPTS);
			scripts.read(str);
			//# Profiler.end(ProfilerSections.PF_SCRIPTS);
			//
			//# Profiler.start(ProfilerSections.PF_DATA);
			data.read(str);
			//# Profiler.start(ProfilerSections.PF_DATA);
			//
			byte[] t = str.readAllBytes();
			if (t.length > 0) throw new IOException("There are "+t.length+" bytes after the last section");
		} finally {
			//# Profiler.end();
			//# Profiler.printReport();
		}
	}
	
	public void write(File file) throws Exception {
		try (EndianDataOutputStream str = new EndianDataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));) {
			write(str);
		} catch (Exception e) {
			throw new Exception(e.getMessage() + ", writing " + file.getName(), e);
		}
	}
	
	@Override
	public void write(EndianDataOutputStream str) throws Exception {
		//# Profiler.start();
		try {
			str.order(ByteOrder.LITTLE_ENDIAN);
			//# Profiler.start(ProfilerSections.PF_HEADER);
			header.write(str);
			//# Profiler.end(ProfilerSections.PF_HEADER);
			//
			//# Profiler.start(ProfilerSections.PF_GLOBALS);
			globalVars.write(str);
			//# Profiler.end(ProfilerSections.PF_GLOBALS);
			//
			//# Profiler.start(ProfilerSections.PF_CODE);
			code.write(str);
			//# Profiler.end(ProfilerSections.PF_CODE);
			//
			//# Profiler.start(ProfilerSections.PF_AUTOSTART);
			autoStartScripts.write(str);
			//# Profiler.end(ProfilerSections.PF_AUTOSTART);
			//
			//# Profiler.start(ProfilerSections.PF_SCRIPTS);
			scripts.write(str);
			//# Profiler.end(ProfilerSections.PF_SCRIPTS);
			//
			//# Profiler.start(ProfilerSections.PF_DATA);
			data.write(str);
			//# Profiler.end(ProfilerSections.PF_DATA);
		} finally {
			//# Profiler.end();
			//# Profiler.printReport();
		}
	}
	
	public boolean validate(PrintStream out) {
		boolean res = true;
		//Code
		boolean landControlAllFound = false;
		List<Instruction> instructions = code.getItems();
		for (Script script : scripts.getItems()) {
			if ("LandControlAll".equals(script.getName())) {
				landControlAllFound = true;
			}
			for (int i = script.getInstructionAddress(); i < instructions.size(); i++) {
				Instruction instr = instructions.get(i);
				try {
					instr.validate(this, script, i);
				} catch (Exception e) {
					res = false;
					String fmt = "%1$s in %2$s at %3$s:%4$d\r\n";
					out.printf(fmt, e.getMessage(), script.getName(), script.getSourceFilename(), instr.lineNumber);
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
		//LandControlAll
		if (!landControlAllFound) {
			res = false;
			out.println("NOTICE: Script LandControlAll not found");
		}
		return res;
	}
	
	public boolean checkCodeCoverage(PrintStream out) {
		boolean res = true;
		List<Instruction> instructions = code.getItems();
		int index = 0;
		for (Script script : scripts.getItems()) {
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
		for (Script script : scripts.getItems()) {
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
		for (Script script : scripts.getItems()) {
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
			if (instr.mode == 0) map[c][0] |= 1;
			if (instr.mode == 1) map[c][1] |= 1;
			int flags = instr.mode == 0 ? 1 : 2;
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
