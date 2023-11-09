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
	private PrintStream out;
	
	public CHLComparator() {
		this(System.out);
	}
	
	public CHLComparator(PrintStream out) {
		this.out = out;
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
		//Number of global variables
		List<String> globalVars1 = a.getGlobalVariables().getNames();
		List<String> globalVars2 = a.getGlobalVariables().getNames();
		if (globalVars1.size() != globalVars2.size()) {
			out.println("Number of globals: "+globalVars1.size()+" -> "+globalVars2.size());
			res = false;
		}
		//Global variable names
		Set<String> globalVars2map = new HashSet<>();
		for (int i = 0; i < globalVars2.size(); i++) {
			String name2 = globalVars2.get(i);
			globalVars2map.add(name2);
		}
		for (int i = 0; i < globalVars1.size(); i++) {
			String name1 = globalVars1.get(i);
			if (!globalVars2map.contains(name1)) {
				out.println("Global variable "+name1+" missing in file 2");
				res = false;
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
		Map<Integer, Const> data1 = map(a.getDataSection().analyze());
		Map<Integer, Const> data2 = map(b.getDataSection().analyze());
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
					/*if (script1.getScriptID() != script2.getScriptID()) {
						out.println("Script "+name+" id: "+script1.getScriptID()+" -> "+script2.getScriptID());
						res = false;
					}*/
					/*if (script1.getVarOffset() != script2.getVarOffset()) {
						out.println("Script "+name+" varOffset: "+script1.getVarOffset()+" -> "+script2.getVarOffset());
						res = false;
					}*/
					//Code
					int locMaxLen = Math.max(script1.getSourceFilename().length(), script2.getSourceFilename().length()) + 8;
					String locFmt = "%-"+locMaxLen+"s";
					boolean stop = false;
					final int offset1 = script1.getInstructionAddress();
					final int offset2 = script2.getInstructionAddress();
					ListIterator<Instruction> it1 = instructions1.listIterator(offset1);
					ListIterator<Instruction> it2 = instructions2.listIterator(offset2);
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
						if (instr1.opcode != instr2.opcode || instr1.flags != instr2.flags
								|| instr1.dataType != instr2.dataType
								|| instr1.floatVal != instr2.floatVal || instr1.boolVal != instr2.boolVal) {
							eq = false;
							stop = true;
						} else if (instr1.intVal != instr2.intVal) {
							if (instr1.opcode.isIP) {
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
							} else if (instr1.isReference()) {
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
							} else {
								/*If 2 instructions that are supposed to be functionally identical have different
								 * operands, try to resolve those operands as data pointers and check if the referred
								 * values are equal. */
								Const const1 = data1.get(instr1.intVal);
								Const const2 = data2.get(instr2.intVal);
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
								+ loc2 + instr2.toString(b, script2, null));
							out.println();
							res = false;
							if (stop) break;
						}
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
		if (!autostart1.equals(autostart2)) {
			out.println("Autostart scripts: "+autostart1+" -> "+autostart2);
			res = false;
		}
		//
		if (res) {
			out.println("Files are functionally identical!");
		} else {
			out.println("Files don't match");
		}
		return res;
	}
	
	private static Map<Integer, Const> map(List<Const> constants) {
		Map<Integer, Const> res = new HashMap<>();
		for (Const c : constants) {
			res.put(c.offset, c);
		}
		return res;
	}
}
