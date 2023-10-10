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
package it.ld.bw.chl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Stack;

import it.ld.bw.chl.exceptions.CompileException;
import it.ld.bw.chl.exceptions.InvalidScriptIdException;
import it.ld.bw.chl.model.Instruction;
import it.ld.bw.chl.model.NativeFunction;
import it.ld.bw.chl.model.OPCode;
import it.ld.bw.chl.model.Script;
import it.ld.bw.chl.model.CHLFile;
import it.ld.bw.chl.model.DataSection.Const;
import it.ld.bw.chl.model.DataType;

public class ASMWriter {
	private static final char[] ILLEGAL_CHARACTERS = {'/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':'};
	private static final Charset SRC_CHARSET = Charset.forName("ISO-8859-1");
	
	private boolean printDataHintEnabled = true;
	private boolean printNativeInfoEnabled = true;
	private boolean printSourceLinenoEnabled = false;
	private boolean printSourceLineEnabled = false;
	private Path sourcePath = null;
	private boolean printSourceCommentsEnabled = false;
	
	private String currentSourceFilename;
	private String[] source;
	
	private PrintStream out;
	
	public ASMWriter() {
		this(System.out);
	}
	
	public ASMWriter(PrintStream out) {
		this.out = out;
	}
	
	public boolean isDataHintEnabled() {
		return printDataHintEnabled;
	}
	
	public void setPrintDataHintEnabled(boolean printDataHintEnabled) {
		this.printDataHintEnabled = printDataHintEnabled;
	}
	
	public boolean isPrintNativeInfoEnabled() {
		return printNativeInfoEnabled;
	}
	
	public void setPrintNativeInfoEnabled(boolean printNativeInfoEnabled) {
		this.printNativeInfoEnabled = printNativeInfoEnabled;
	}
	
	public boolean isPrintSourceLinenoEnabled() {
		return printSourceLinenoEnabled;
	}
	
	public void setPrintSourceLinenoEnabled(boolean printSourceLinenoEnabled) {
		this.printSourceLinenoEnabled = printSourceLinenoEnabled;
	}
	
	public boolean isPrintSourceLineEnabled() {
		return printSourceLineEnabled;
	}
	
	public void setPrintSourceLineEnabled(boolean printSourceLineEnabled) {
		this.printSourceLineEnabled = printSourceLineEnabled;
	}

	public Path getSourcePath() {
		return sourcePath;
	}
	
	public void setSourcePath(Path sourcePath) {
		this.sourcePath = sourcePath;
	}
	
	public boolean isPrintSourceCommentsEnabled() {
		return printSourceCommentsEnabled;
	}
	
	public void setPrintSourceCommentsEnabled(boolean printSourceCommentsEnabled) {
		this.printSourceCommentsEnabled = printSourceCommentsEnabled;
	}
	
	public void write(CHLFile chl, File outdir) throws IOException, CompileException, InvalidScriptIdException {
		Path path = outdir.toPath();
		List<Const> constants = chl.getDataSection().analyze();
		Map<Integer, Const> constMap = mapConstants(constants);
		List<String> sources = chl.getSourceFilenames();
		Map<Integer, Label> labels = getLabels(chl.getCode().getItems());
		//
		out.println("Writing _project.txt");
		File prjFile = path.resolve("_project.txt").toFile();
		try (FileWriter str = new FileWriter(prjFile);) {
			str.write("_data.txt\r\n");
			str.write("_globals.txt\r\n");
			for (String sourceFilename : sources) {
				str.write(sourceFilename + "\r\n");
				if (!isValidFilename(sourceFilename)) {
					throw new RuntimeException("Invalid source filename: " + sourceFilename);
				}
			}
			str.write("_autorun.txt\r\n");
		}
		//
		out.println("Writing _data.txt");
		File dataFile = path.resolve("_data.txt").toFile();
		try (FileWriter str = new FileWriter(dataFile);) {
			writeHeader(chl, str);
			writeData(chl, str, constants);
		}
		//
		out.println("Writing _globals.txt");
		File globalsFile = path.resolve("_globals.txt").toFile();
		try (FileWriter str = new FileWriter(globalsFile);) {
			writeHeader(chl, str);
			writeGlobals(chl, str);
		}
		//
		out.println("Writing _autorun.txt");
		File autostartFile = path.resolve("_autorun.txt").toFile();
		try (FileWriter str = new FileWriter(autostartFile);) {
			writeHeader(chl, str);
			writeAutoStartScripts(chl, str);
		}
		//
		for (String sourceFilename : sources) {
			File sourceFile = path.resolve(sourceFilename).toFile();
			out.println("Writing "+sourceFilename);
			try (FileWriter str = new FileWriter(sourceFile);) {
				writeHeader(chl, str);
				writeScripts(chl, str, sourceFilename, labels, constMap);
			}
		}
	}
	
	public void writeMerged(CHLFile chl, File file) throws IOException, CompileException {
		List<Const> constants = chl.getDataSection().analyze();
		Map<Integer, Const> constMap = mapConstants(constants);
		Map<Integer, Label> labels = getLabels(chl.getCode().getItems());
		try (FileWriter str = new FileWriter(file);) {
			writeHeader(chl, str);
			writeData(chl, str, constants);
			writeGlobals(chl, str);
			writeScripts(chl, str, labels, constMap);
			writeAutoStartScripts(chl, str);
		}
	}
	
	private Map<Integer, Label> getLabels(List<Instruction> instructions) {
		Map<Integer, Label> labels = new HashMap<>();
		int index = 0;
		for (Instruction instr : instructions) {
			if (instr.opcode.isIP) {
				Label label = labels.get(instr.intVal);
				if (label == null) {
					String name = String.format("lbl%1$X", labels.size());
					label = new Label(name, instr.intVal > index);
					labels.put(instr.intVal, label);
				}
			}
			index++;
		}
		return labels;
	}
	
	private Map<Integer, Const> mapConstants(List<Const> constants) {
		Map<Integer, Const> constMap = new HashMap<Integer, Const>();
		for (Const c : constants) {
			constMap.put(c.offset, c);
		}
		return constMap;
	}
	
	private void writeHeader(CHLFile chl, FileWriter str) throws IOException {
		str.write("//LHVM Challenge ASM version "+chl.getHeader().getVersion()+"\r\n");
		str.write("\r\n");
	}
	
	private void writeData(CHLFile chl, FileWriter str, List<Const> constants) throws IOException {
		str.write("DATA\r\n");
		str.write(String.format("//0x%1$08X\r\n", chl.getDataSection().getOffset()));
		for (Const c : constants) {
			str.write(c.getDeclaration() + "\r\n");
		}
		str.write("\r\n");
	}
	
	private void writeGlobals(CHLFile chl, FileWriter str) throws IOException {
		str.write("GLOBALS\r\n");
		for (String name : chl.getGlobalVariables().getNames()) {
			str.write("global "+name+"\r\n");
		}
		str.write("\r\n");
	}
	
	private void writeScripts(CHLFile chl, FileWriter str, Map<Integer, Label> labels, Map<Integer, Const> constMap) throws IOException, CompileException {
		str.write("SCRIPTS\r\n");
		str.write(String.format("//0x%1$08X\r\n", chl.getScriptsSection().getOffset()));
		String prevSourceFilename = "";
		List<Script> scripts = chl.getScriptsSection().getItems();
		for (int i = 0; i < scripts.size(); i++) {
			Script script = scripts.get(i);
			if (!script.getSourceFilename().equals(prevSourceFilename)) {
				str.write("\r\n");
				str.write("source "+script.getSourceFilename()+"\r\n");
				str.write("\r\n");
				prevSourceFilename = script.getSourceFilename();
			}
			int limit = i < scripts.size() - 1 ? scripts.get(i + 1).getInstructionAddress() : Integer.MAX_VALUE;
			writeScript(chl, str, script, limit, labels, constMap);
			str.write("\r\n");
		}
		str.write("\r\n");
	}
	
	private void writeScripts(CHLFile chl, FileWriter str, String sourceFilename, Map<Integer, Label> labels, Map<Integer, Const> constMap) throws IOException, CompileException {
		str.write("SCRIPTS\r\n");
		str.write("\r\n");
		/* We need to iterate through all scripts to be sure that we can always access the next script in order to
		 * compute the last instruction to write (limit) */
		List<Script> scripts = chl.getScriptsSection().getItems();
		for (int i = 0; i < scripts.size(); i++) {
			Script script = scripts.get(i);
			if (script.getSourceFilename().equals(sourceFilename)) {
				int limit = i < scripts.size() - 1 ? scripts.get(i + 1).getInstructionAddress() : Integer.MAX_VALUE;
				writeScript(chl, str, script, limit, labels, constMap);
				str.write("\r\n");
			}
		}
	}
	
	private void writeScript(CHLFile chl, FileWriter str, Script script, int limit, Map<Integer, Label> labels, Map<Integer, Const> constMap) throws IOException, CompileException {
		if (printSourceLineEnabled) {
			setSourceFile(script.getSourceFilename());
		}
		if (script.getVarOffset() > 0) {
			//This forces the var offset to be the same of the original CHL when assembling again
			str.write("global "+script.getGlobalVar(chl, script.getVarOffset())+"\r\n");
			str.write("\r\n");
		}
		Stack<String> comments = new Stack<>();
		List<Instruction> instructions = chl.getCode().getItems();
		final int firstInstruction = script.getInstructionAddress();
		Instruction instr;
		//Script comments
		if (printSourceCommentsEnabled && source != null) {
			instr = instructions.get(firstInstruction + 1);
			if (instr.lineNumber <= 0) {
				//In case there are no parameters and local vars
				instr = instructions.get(firstInstruction + 2);
			}
			//Search for previous comments
			for (int i = instr.lineNumber - 2; i >= 0; i--) {
				String src = source[i];
				String srcT = src.trim();
				if (src.isBlank() || srcT.startsWith("//")) {
					comments.push(src);
				} else if ("start".equals(srcT) || srcT.startsWith("begin ")) {
					comments.clear();
				} else {
					break;
				}
			}
			//Write previous comments
			while (!comments.isEmpty()) {
				str.write(comments.pop() + "\r\n");
			}
		}
		//Signature
		str.write("begin "+script.getSignature()+"\r\n");
		//Local variables
		for (int i = script.getParameterCount(); i < script.getVariables().size(); i++) {
			str.write("\tLocal " + script.getVariables().get(i) + "\r\n");
		}
		//Code
		int index = firstInstruction;
		ListIterator<Instruction> it = instructions.listIterator(index);
		boolean endFound = false;
		int instrAfterEnd = 0;
		int prevSrcLine = instructions.get(index + 1).lineNumber;
		int skipSrcLines = 0;
		do {
			try {
				if (endFound) instrAfterEnd++;
				instr = it.next();
				Label label = labels.get(index);
				/* If the label is referenced by a previous instruction, then print the label before
				 * printing the original source line.
				 */
				if (label != null && label.backReferenced) {
					str.write(label + ":\r\n");
				}
				if (printSourceLineEnabled && source != null) {
					if (instr.opcode == OPCode.EXCEPT) {
						if (index > firstInstruction) {
							/*The line number for EXCEPT is the one of the end of block (i.e. "end while"),
							 * this would just cause confusion. We insert an empty comment just to separate
							 * the EXCEPT from the previous instruction. */
							str.write("//\r\n");
						}
					} else if (instr.lineNumber > 0 && instr.lineNumber <= source.length
							&& instr.lineNumber != prevSrcLine
							&& instr.opcode != OPCode.JZ) {
						if (skipSrcLines > 0) {
							skipSrcLines--;
						} else if (instr.opcode == OPCode.BRKEXCEPT) {
							skipSrcLines = 1;
						} else {
							if (printSourceCommentsEnabled) {
								//Search for previous comments
								for (int i = instr.lineNumber - 2; i >= 0; i--) {
									String src = source[i];
									if (src.isBlank() || src.trim().startsWith("//")) {
										comments.push(src);
									} else {
										break;
									}
								}
								//Write previous comments
								while (!comments.isEmpty()) {
									str.write(comments.pop() + "\r\n");
								}
							}
							//Write the statement
							str.write("//@" + source[instr.lineNumber - 1]);	//line numbers start from 1
							str.write("\t\t//#" + script.getSourceFilename() + ":" + instr.lineNumber + "\r\n");
							prevSrcLine = instr.lineNumber;
						}
					} else if (instr.isFree()) {
						str.write("//@\tstart\r\n");
					}
				}
				/* If the label is referenced by a subsequent instruction, then print the label after
				 * having printed the original source line.
				 */
				if (label != null && !label.backReferenced) {
					str.write(label + ":\r\n");
				}
				str.write("\t" + instr.toString(chl, script, labels));
				boolean isConstRef = instr.opcode == OPCode.PUSH && instr.flags == 0 && instr.dataType == DataType.INT;
				if (printDataHintEnabled && isConstRef && instr.intVal > 0 && constMap.containsKey(instr.intVal)) {
					str.write("\t//" + constMap.get(instr.intVal));
				} else if (printNativeInfoEnabled && instr.opcode == OPCode.SYS) {
					NativeFunction f = NativeFunction.fromCode(instr.intVal);
					str.write("\t//" + f.getInfoString());
				}
				if (printSourceLinenoEnabled
						&& instr.lineNumber > 0
						&& instr.lineNumber != prevSrcLine
						&& instr.opcode != OPCode.JZ && instr.opcode != OPCode.EXCEPT) {
					str.write("\t\t//#" + script.getSourceFilename() + ":" + instr.lineNumber);
					prevSrcLine = instr.lineNumber;
				}
				str.write("\r\n");
				endFound |= instr.opcode == OPCode.END;
				index++;
			} catch (Exception e) {
				throw new CompileException(script.getName(), index, e);
			}
		} while (it.hasNext() && index < limit);
		if (instrAfterEnd > 0) {
			out.println(instrAfterEnd + " instructions found after end of script " + script.getName());
		}
	}
	
	private void writeAutoStartScripts(CHLFile chl, FileWriter str) throws IOException, CompileException {
		str.write("AUTORUN\r\n");
		for (int scriptID : chl.getAutoStartScripts().getScripts()) {
			try {
				Script script = chl.getScriptsSection().getScript(scriptID);
				str.write("run script "+script.getName()+"\r\n");
			} catch (InvalidScriptIdException e) {
				String msg = "ERROR: " + e.getMessage() + "\r\n";
				str.write("//" + msg);
				throw new CompileException(e);
			}
		}
		str.write("\r\n");
	}
	
	private void setSourceFile(String sourceFilename) {
		if (!sourceFilename.equals(currentSourceFilename)) {
			Path file = sourcePath.resolve(sourceFilename);
			try {
				List<String> lines = Files.readAllLines(file, SRC_CHARSET);
				source = lines.toArray(new String[0]);
			} catch (IOException e) {
				source = null;
				out.println("WARNING: failed to read source file '" + sourceFilename + "': " + e);
			}
			currentSourceFilename = sourceFilename;
		}
	}
	
	private static boolean isValidFilename(String s) {
		for (char c : ILLEGAL_CHARACTERS) {
			if (s.indexOf(c) >= 0) return false;
		}
		return true;
	}
	
	
	private static class Label {
		public final String name;
		/**Tells whether this label is referenced by a previous instruction or not.*/
		public final boolean backReferenced;
		
		public Label(String name, boolean backReferenced) {
			this.name = name;
			this.backReferenced = backReferenced;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
}
