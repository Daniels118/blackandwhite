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

import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import it.ld.bw.chl.exceptions.InvalidScriptIdException;
import it.ld.bw.chl.exceptions.InvalidVariableIdException;
import it.ld.bw.chl.exceptions.ScriptNotFoundException;
import it.ld.bw.chl.model.CHLFile;
import it.ld.bw.chl.model.DataSection.Const;
import it.ld.bw.chl.model.DataType;
import it.ld.bw.chl.model.InitGlobal;
import it.ld.bw.chl.model.Instruction;
import it.ld.bw.chl.model.OPCode;
import it.ld.bw.chl.model.Script;
import it.ld.bw.chl.model.Scripts;

/**This class compares 2 CHL binary files and tells if they are functionally identical.
 * By functionally identical we mean that they don't need to have exactly the same bytes in the same orders,
 * but that the scripts that build up those files have the same arguments, the same sequence of instructions,
 * and reference the same data.
 */
public class CHLComparator {
	private boolean strict;
	private PrintStream out;
	
	public CHLComparator() {
		this(System.out);
	}
	
	public CHLComparator(PrintStream out) {
		this.out = out;
	}
	
	public boolean isStrict() {
		return strict;
	}
	
	public void setStrict(boolean strict) {
		this.strict = strict;
	}
	
	public boolean compare(CHLFile a, CHLFile b) {
		return compare(a, b, null);
	}
	
	public boolean compare(CHLFile a, CHLFile b, Set<String> scripts) {
		boolean res = true;
		//File version
		int ver1 = a.getHeader().getVersion();
		int ver2 = b.getHeader().getVersion();
		if (ver1 != ver2) {
			out.println("Version: "+ver1+" -> "+ver2);
			return false;
		}
		//Global variables
		List<String> globalVars1 = a.getGlobalVariables().getNames();
		List<String> globalVars2 = a.getGlobalVariables().getNames();
		if (strict) {
			if (!globalVars1.equals(globalVars2)) {
				out.println("Global vars differ:");
				out.println(globalVars1.size());
				out.println(globalVars2.size());
				out.println();
				res = false;
			}
		} else {
			if (globalVars1.size() != globalVars2.size()) {
				out.println("Number of global vars: "+globalVars1.size()+" -> "+globalVars2.size());
				res = false;
			}
			//Global variable names
			Set<String> globalVars2set = new HashSet<>();
			for (int i = 0; i < globalVars2.size(); i++) {
				String name2 = globalVars2.get(i);
				globalVars2set.add(name2);
			}
			for (int i = 0; i < globalVars1.size(); i++) {
				String name1 = globalVars1.get(i);
				if (!globalVars2set.contains(name1)) {
					out.println("Global variable "+name1+" missing in file 2");
					res = false;
				}
			}
		}
		//Scripts count
		Scripts scriptsSection1 = a.getScriptsSection();
		Scripts scriptsSection2 = b.getScriptsSection();
		List<Script> scripts1 = scriptsSection1.getItems();
		List<Script> scripts2 = scriptsSection2.getItems();
		if (scripts1.size() != scripts2.size()) {
			out.println("Scripts count: "+scripts1.size()+" -> "+scripts2.size());
			res = false;
		}
		out.println();
		//Scripts list
		int maxMismatch = 20;
		List<Const> data1 = a.getDataSection().analyze();
		List<Const> data2 = b.getDataSection().analyze();
		Map<Integer, Const> dataMap1 = mapOffset(data1);
		Map<Integer, Const> dataMap2 = mapOffset(data2);
		List<Instruction> instructions1 = a.getCode().getItems();
		List<Instruction> instructions2 = b.getCode().getItems();
		for (int i = 0; i < scripts1.size(); i++) {
			Script script1 = scripts1.get(i);
			String name = script1.getName();
			if (scripts != null && !scripts.contains(name)) {
				continue;
			}
			try {
				Script script2 = b.getScriptsSection().getScript(name);
				if (script1.getParameterCount() != script2.getParameterCount()) {
					out.println("Script "+name+" parameterCount: "+script1.getParameterCount()+" -> "+script2.getParameterCount());
					out.println(script1.getSignature());
					out.println(script2.getSignature());
					out.println();
					res = false;
				} else if (!script1.getVariables().equals(script2.getVariables())) {
					out.println("Script "+name+" variables: "+script1.getVariables()+" -> "+script2.getVariables());
					out.println();
					res = false;
				} else {
					if (strict) {
						if (script1.getScriptID() != script2.getScriptID()) {
							out.println("Script "+name+" id: "+script1.getScriptID()+" -> "+script2.getScriptID());
							res = false;
						}
						if (script1.getGlobalCount() != script2.getGlobalCount()) {
							out.println("Script "+name+" global count: "+script1.getGlobalCount()+" -> "+script2.getGlobalCount());
							res = false;
						}
					}
					//Code
					int locMaxLen = Math.max(script1.getSourceFilename().length(), script2.getSourceFilename().length()) + 8;
					String locFmt = "%-"+locMaxLen+"s";
					boolean stop = false;
					final int offset1 = script1.getInstructionAddress();
					final int offset2 = script2.getInstructionAddress();
					ListIterator<Instruction> it1 = instructions1.listIterator(offset1);
					ListIterator<Instruction> it2 = instructions2.listIterator(offset2);
					int index1 = offset1;
					int index2 = offset2;
					try {
						while (it1.hasNext()) {
							Instruction instr1 = it1.next();
							Instruction instr2 = it2.next();
							if (instr2 == null) {
								out.println("Unexpected end of file 2 while comparing script "+name);
								out.println();
								res = false;
								break;
							}
							//
							boolean eq = true;
							if (strict) {
								if (instr1.opcode != instr2.opcode || instr1.flags != instr2.flags
										|| instr1.dataType != instr2.dataType
										|| instr1.floatVal != instr2.floatVal || instr1.boolVal != instr2.boolVal
										|| instr1.intVal != instr2.intVal) {
									eq = false;
									stop = true;
								}
							} else {
								if (instr1.opcode != instr2.opcode || instr1.flags != instr2.flags
										|| instr1.dataType != instr2.dataType
										|| instr1.floatVal != instr2.floatVal || instr1.boolVal != instr2.boolVal) {
									eq = false;
									stop = true;
								} else if (instr1.opcode.isIP) {
									int relDst1 = instr1.intVal - offset1;
									int relDst2 = instr2.intVal - offset2;
									if (relDst1 != relDst2) {
										eq = false;
									}
								} else if (instr1.opcode.isScript) {
									try {
										Script targetScript1 = scriptsSection1.getScript(instr1.intVal);
										try {
											Script targetScript2 = scriptsSection2.getScript(instr2.intVal);
											if (!targetScript1.getName().equals(targetScript2.getName())) {
												eq = false;
											}
										} catch (InvalidScriptIdException e) {
											out.println(e.getMessage() + " in file 1");
											eq = false;
										}
									} catch (InvalidScriptIdException e) {
										out.println(e.getMessage() + " in file 2");
										eq = false;
									}
								} else if (instr1.isReference() || instr1.opcode == OPCode.PUSH && instr1.dataType == DataType.VAR) {
									try {
										String name1 = script1.getVar(a, instr1.intVal);
										try {
											String name2 = script2.getVar(b, instr2.intVal);
											if (!name1.equals(name2)) {
												eq = false;
											}
										} catch (InvalidVariableIdException e) {
											System.err.println("Invalid variable in "+script2.getSourceFilename()+":"+instr2.lineNumber);
											e.printStackTrace();
											eq = false;
										}
									} catch (InvalidVariableIdException e) {
										System.err.println("Invalid variable in "+script1.getSourceFilename()+":"+instr1.lineNumber);
										e.printStackTrace();
										eq = false;
									}
								} else if (instr1.intVal != instr2.intVal) {
									/*If 2 instructions that are supposed to be functionally identical have different
									 * operands, try to resolve those operands as data pointers and check if the referred
									 * values are equal. */
									Const const1 = dataMap1.get(instr1.intVal);
									Const const2 = dataMap2.get(instr2.intVal);
									if (const1 == null || const2 == null || !const1.equals(const2)) {
										eq = false;
									}
								}
							}
							if (instr1.opcode == OPCode.END && instr2.opcode == OPCode.END) {
								break;
							} else if (instr1.opcode == OPCode.END || instr2.opcode == OPCode.END) {
								eq = false;
								stop = true;
							}
							if (!eq) {
								String loc1 = String.format(locFmt, script1.getSourceFilename()+":"+instr1.lineNumber+": ");
								String loc2 = String.format(locFmt, script2.getSourceFilename()+":"+instr2.lineNumber+": ");
								out.println("Instruction mismatch for script " + name + ":\r\n"
									+ loc1 + instr1.toString(a, script1, null) + "\r\n"
									+ loc2 + instr2.toString(b, script2, null) + "\r\n");
								res = false;
								if (stop) break;
								if (--maxMismatch <= 0) {
									out.println("Too many mismatches");
									out.println();
									i = scripts1.size();
									break;
								}
							}
							index1++;
							index2++;
						}
					} catch (RuntimeException e) {
						throw new RuntimeException(e.getMessage()+" at "+index1+"/"+index2, e);
					}
				}
			} catch (ScriptNotFoundException e) {
				out.println("Script "+script1.getName()+" not found in file 2");
				out.println();
				res = false;
			}
		}
		//Autostart scripts
		List<Integer> autostart1 = a.getAutoStartScripts().getScripts();
		List<Integer> autostart2 = b.getAutoStartScripts().getScripts();
		if (strict) {
			if (!autostart1.equals(autostart2)) {
				out.println("Autostart scripts: "+autostart1+" -> "+autostart2);
				res = false;
			}
		} else {
			if (autostart1.size() != autostart2.size()) {
				out.println("Number of autostart scripts: "+autostart1.size()+" -> "+autostart2.size());
				res = false;
			}
			//
			Set<String> autostartNames2 = new HashSet<>();
			for (Integer scriptId : autostart2) {
				try {
					Script script = b.getScriptsSection().getScript(scriptId);
					autostartNames2.add(script.getName());
				} catch (InvalidScriptIdException e) {
					out.println(e.getMessage() + " in autostart section of file 2");
					res = false;
				}
			}
			for (Integer scriptId : autostart1) {
				try {
					Script script = a.getScriptsSection().getScript(scriptId);
					String name = script.getName();
					if (!autostartNames2.contains(name)) {
						out.println("Autostart script "+name+" missing in file 2");
						res = false;
					}
				} catch (InvalidScriptIdException e) {
					out.println(e.getMessage() + " in autostart section of file 1");
					res = false;
				}
			}
		}
		//Data
		if (strict) {
			byte[] rawData1 = a.getDataSection().getData();
			byte[] rawData2 = b.getDataSection().getData();
			if (!Arrays.equals(rawData1, rawData2)) {
				out.println("Data sections differ:");
				out.println(dataMap1.values());
				out.println(dataMap2.values());
				out.println();
				res = false;
			}
		}
		//Init globals
		List<InitGlobal> inits1 = a.getInitGlobals().getItems();
		List<InitGlobal> inits2 = b.getInitGlobals().getItems();
		if (strict) {
			if (!inits1.equals(inits2)) {
				out.println("Init globals mismatch:");
				out.println(inits1);
				out.println(inits2);
				out.println();
				res = false;
			}
		} else {
			if (inits1.size() != inits2.size()) {
				out.println("Number of init globals: "+autostart1.size()+" -> "+autostart2.size());
				res = false;
			}
			//
			Map<String, InitGlobal> initsMap2 = mapName(inits2);
			for (InitGlobal init1 : inits1) {
				String name = init1.getName();
				InitGlobal init2 = initsMap2.get(name);
				if (init2 != null) {
					if (!init1.equals(init2)) {
						out.println("Init of "+name+" mismatch:");
						out.println(init1);
						out.println(init2);
						out.println();
					}
				} else {
					out.println("Init of "+name+" not found in file 2");
				}
			}
		}
		//
		if (res) {
			if (strict) {
				out.println("Files have a strict match!");
			} else {
				out.println("Files are functionally identical!");
			}
		} else {
			if (strict) {
				out.println("Files don't match. You may retry without strict option.");
			} else {
				out.println("Files don't match");
			}
		}
		return res;
	}
	
	private static Map<Integer, Const> mapOffset(List<Const> constants) {
		Map<Integer, Const> res = new HashMap<>();
		for (Const c : constants) {
			res.put(c.offset, c);
		}
		return res;
	}
	
	private static Map<String, InitGlobal> mapName(List<InitGlobal> inits) {
		Map<String, InitGlobal> res = new HashMap<>();
		for (InitGlobal init : inits) {
			res.put(init.getName(), init);
		}
		return res;
	}
}
