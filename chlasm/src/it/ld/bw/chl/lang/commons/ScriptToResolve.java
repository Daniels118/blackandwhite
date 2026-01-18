package it.ld.bw.chl.lang.commons;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import it.ld.bw.chl.model.Instruction;

public class ScriptToResolve {
	public final File file;
	public final int line;
	public final Instruction instr;
	public final String name;
	public final int argc;
	public final List<String> argtypes;
	
	public ScriptToResolve(File file, int line, Instruction instr, String name, int argc) {
		this.file = file;
		this.line = line;
		this.instr = instr;
		this.name = name;
		this.argc = argc;
		this.argtypes = null;
	}
	
	public ScriptToResolve(File file, int line, Instruction instr, String name, List<String> argtypes) {
		this.file = file;
		this.line = line;
		this.instr = instr;
		this.name = name;
		this.argc = argtypes.size();
		this.argtypes = new ArrayList<>(argtypes);
	}
}