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
package it.ld.bw.chl.lang;

import static it.ld.bw.chl.model.OPCodeFlag.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import it.ld.bw.chl.exceptions.ParseException;
import it.ld.bw.chl.model.CHLFile;
import it.ld.bw.chl.model.DataSection;
import it.ld.bw.chl.model.DataType;
import it.ld.bw.chl.model.Header;
import it.ld.bw.chl.model.InitGlobal;
import it.ld.bw.chl.model.Instruction;
import it.ld.bw.chl.model.NativeFunction;
import it.ld.bw.chl.model.OPCode;
import it.ld.bw.chl.model.OPCodeFlag;
import it.ld.bw.chl.model.Script;
import it.ld.bw.chl.model.ScriptType;

public class ASMCompiler implements Compiler {
	private static final Charset ASCII = Charset.forName("US-ASCII");
	private static final int INITIAL_BUFFER_SIZE = 16 * 1024;
	private static final int MAX_BUFFER_SIZE = 2 * 1024 * 1024;
	
	private PrintStream out;
	private boolean verboseEnabled;
	
	private final CHLFile chl = new CHLFile();
	private final List<String> globalVariables;
	private ByteBuffer dataBuffer = ByteBuffer.allocate(INITIAL_BUFFER_SIZE);
	private final List<Instruction> instructions;
	private final List<Script> scripts;
	private final List<Integer> autoStartScripts;
	private final DataSection dataSection;
	private final List<InitGlobal> initGlobals;
	private final HashMap<Integer, String> labels = new HashMap<>();
	private final Map<String, SourceConst> globalConstants = new HashMap<>();
	private final Map<String, SourceConst> localConstants = new HashMap<>();
	private final Map<String, Integer> globalMap = new HashMap<>();
	private final Map<String, Integer> labelMap = new HashMap<>();
	private final Map<String, Integer> scriptMap = new HashMap<>();
	private final List<LabelToResolve> labelsToResolve = new LinkedList<>();
	private final List<ScriptToResolve> scriptsToResolve = new LinkedList<>();
	private int[] scriptsUsageCount = null;
	
	private boolean sealed = false;
	
	public ASMCompiler() {
		this(System.out);
	}
	
	public ASMCompiler(PrintStream out) {
		this.out = out;
		//
		globalVariables = chl.getGlobalVariables().getNames();
		instructions = chl.getCode().getItems();
		scripts = chl.getScriptsSection().getItems();
		autoStartScripts = chl.getAutoStartScripts().getScripts();
		dataSection = chl.getDataSection();
		initGlobals = chl.getInitGlobals().getItems();
		initGlobals.add(new InitGlobal("Null variable", 0));
		dataBuffer.order(ByteOrder.LITTLE_ENDIAN);
		chl.getHeader().setVersion(Header.BWCI);
		chl.getGlobalVariables().setOffset(chl.getHeader().getLength());
	}
	
	public boolean isVerboseEnabled() {
		return verboseEnabled;
	}
	
	public void setVerboseEnabled(boolean verboseEnabled) {
		this.verboseEnabled = verboseEnabled;
	}
	
	private void warning(String s) {
		out.println(s);
	}
	
	private void notice(String s) {
		if (verboseEnabled) {
			out.println(s);
		}
	}
	
	private void info(String s) {
		if (verboseEnabled) {
			out.println(s);
		}
	}
	
	public void defineConstant(String name, Object value) {
		SourceConst newConst = new SourceConst(name, value);
		SourceConst oldConst = globalConstants.get(name);
		if (oldConst == null) {
			globalConstants.put(newConst.name, newConst);
		} else if (!(oldConst.value.equals(newConst.value))) {
			warning("WARNING: redefinition of global constant "+name);
			globalConstants.put(newConst.name, newConst);
		}
	}
	
	public void loadHeader(File headerFile) throws FileNotFoundException, IOException, ParseException {
		info("loading "+headerFile.getName()+"...");
		CHeaderParser parser = new CHeaderParser();
		Map<String, Integer> hconst = parser.parse(headerFile);
		for (Entry<String, Integer> e : hconst.entrySet()) {
			defineConstant(e.getKey(), e.getValue());
		}
	}
	
	public void loadInfo(File infoFile) throws FileNotFoundException, IOException, ParseException {
		info("loading "+infoFile.getName()+"...");
		InfoParser2 parser = new InfoParser2();
		Map<String, Integer> hconst = parser.parse(infoFile);
		for (Entry<String, Integer> e : hconst.entrySet()) {
			defineConstant(e.getKey(), e.getValue());
		}
	}
	
	/**Finalize the CHL file. No more files can be parsed after finalization.
	 * @throws ParseException
	 */
	public CHLFile seal() throws ParseException {
		if (!sealed) {
			info("building...");
			if (scriptsUsageCount == null) {
				scriptsUsageCount = new int[scripts.size()];
			}
			//Resolve labels
			for (LabelToResolve label : labelsToResolve) {
				Integer ip = labelMap.get(label.name);
				if (ip == null) {
					throw new ParseException("Undefined label '"+label.name+"'", label.file, label.instr.lineNumber);
				}
				label.instr.intVal = ip;
				if (label.instr.opcode.isJump && ip > label.index) {
					label.instr.flags = FORWARD;
				}
			}
			//Resolve scripts
			for (ScriptToResolve script : scriptsToResolve) {
				Integer scriptID = scriptMap.get(script.name);
				if (scriptID == null) {
					throw new ParseException("Undefined script '"+script.name+"'", script.file, script.instr.lineNumber);
				}
				script.instr.intVal = scriptID;
				scriptsUsageCount[scriptID - 1]++;	//Script IDs must start from 1
			}
			//Store data
			dataBuffer.flip();
			dataSection.setData(new byte[dataBuffer.limit()]);
			dataBuffer.get(dataSection.getData());
			//Print unused scripts
			for (int i = 0; i < scripts.size(); i++) {
				if (scriptsUsageCount[i] == 0) {
					Script script = scripts.get(i);
					String fmt = "NOTICE: script %1$ 4d %2$s is never used (instruction address: %3$08X)";
					String msg = String.format(fmt, script.getScriptID(), script.getName(), script.getInstructionAddress());
					notice(msg);
				}
			}
			//
			chl.checkCodeCoverage(out);
			sealed = true;
			info("done.");
		}
		return chl;
	}
	
	/**Be aware that this implicitly seals the CHL file. No more files can be parsed.
	 * @return
	 * @throws ParseException
	 */
	public CHLFile getCHLFile() throws ParseException {
		if (!sealed) {
			seal();
		}
		return chl;
	}
	
	public CHLFile compile(Project project) throws IOException, ParseException {
		for (File file : project.cHeaders) {
			loadHeader(file);
		}
		for (File file : project.infoFiles) {
			loadInfo(file);
		}
		return compile(project.sources);
	}
	
	public CHLFile compile(List<File> files) throws IOException, ParseException {
		for (File file : files) {
			parse(file);
		}
		seal();
		return chl;
	}
	
	public void parse(File file) throws ParseException, IOException, IllegalStateException {
		if (sealed) {
			throw new IllegalStateException("CHL file already sealed");
		}
		char section = 'H';	// H=header, D=data, G=global, S=script, A=autorun
		try (BufferedReader str = new BufferedReader(new FileReader(file));) {
			String sourceFilename = file.getName();
			Script script = null;
			String line = str.readLine();
			int lineno = 1;
			while (line != null) {
				if (line.startsWith("SOURCE ")) {
					sourceFilename = line.substring(line.indexOf(' ')).trim();
				} else if (".DATA".equals(line)) {
					section = 'D';
				} else if (".GLOBALS".equals(line)) {
					section = 'G';
				} else if (".AUTORUN".equals(line)) {
					scriptsUsageCount = new int[scripts.size()];
					section = 'A';
				} else if (".SCRIPTS".equals(line)) {
					section = 'S';
				} else {
					switch (section) {
						case 'G':
							line = removeComment(line).trim();
							if (!line.isEmpty()) {
								String[] tks = split2(line);
								String keyword = tks[0];
								if ("global".equals(keyword)) {				//Global variable declaration
									if (tks.length < 2) {
										throw new ParseException("Expected variable name or 'constant' after 'global'", file, lineno);
									}
									String[] tksb = split2(tks[1]);
									if ("constant".equals(tksb[0])) {
										if (tksb.length < 2) {
											throw new ParseException("Expected constant name after 'constant'", file, lineno);
										}
										try {
											SourceConst c = parseConst(tksb[1]);
											globalConstants.put(c.name, c);
										} catch (Exception e) {
											throw new ParseException(e.getMessage(), file, lineno);
										}
									} else {
										String name = tks[1];
										int size = 1;
										//Parse array
										if (name.indexOf('[') >= 0) {
											int p = name.indexOf('[');
											size = Integer.parseInt(name.substring(p + 1, name.length() - 1));
											name = name.substring(0, p);
										}
										//Parse init
										float val = 0;
										if (name.indexOf('=') >= 0) {
											int p = name.indexOf('=');
											String sVal = name.substring(p + 1).trim();
											name = name.substring(0, p).trim();
											val = Float.parseFloat(sVal);
										}
										//Add var and init
										if (!isValidIdentifier(name)) {
											throw new ParseException("Invalid variable identifier", file, lineno);
										}
										globalVariables.add(name);
										initGlobals.add(new InitGlobal(name, val));
										globalMap.putIfAbsent(name, globalVariables.size());	//Global variables are indexed starting from 1
										//Add array items and init
										for (int i = 1; i < size; i++) {
											globalVariables.add("LHVMA");
											initGlobals.add(new InitGlobal("LHVMA", 0f));
										}
									}
								} else {
									throw new ParseException("Unexpected keyword: "+keyword+". Expected: global", file, lineno);
								}
							}
							break;
						case 'S':
							line = removeComment(line).trim();
							if (!line.isEmpty()) {
								String[] tks = split2(line);
								String keyword = tks[0];
								if (sourceFilename == null) {
									throw new ParseException("Source filename not set", file, lineno);
								} else if ("begin".equals(keyword)) {		//Script signature
									if (tks.length < 2) {
										throw new ParseException("Expected script type after 'begin'", file, lineno);
									}
									script = new Script();
									script.setScriptID(scripts.size() + 1);	//Script IDs must start from 1
									script.setGlobalCount(globalVariables.size());
									script.setSourceFilename(sourceFilename);
									script.setInstructionAddress(instructions.size());
									String expr = tks[1];
									int p1 = expr.indexOf('(');
									if (p1 < 0) p1 = expr.length();
									int p0 = expr.lastIndexOf(' ', p1);
									if (p0 < 0) throw new ParseException("Expected script name", file, lineno);
									String sType = expr.substring(0, p0).trim();
									try {
										script.setScriptType(ScriptType.fromKeyword(sType));
									} catch (IllegalArgumentException e) {
										throw new ParseException("Invalid script type '"+sType+"'", file, lineno);
									}
									String name = expr.substring(p0 + 1, p1);
									if (!isValidIdentifier(name)) {
										throw new ParseException("Invalid script name", file, lineno);
									}
									script.setName(name);
									if (p1 < expr.length()) {
										int p2 = expr.indexOf(')', p1);
										String sArgs = expr.substring(p1 + 1, p2).trim();
										if (!sArgs.isEmpty()) {
											String[] args = sArgs.split("\\s*,\\s*");
											for (String arg : args) {
												if (!isValidIdentifier(arg)) {
													throw new ParseException("Invalid argument name", file, lineno);
												}
												script.getVariables().add(arg);
											}
											script.setParameterCount(args.length);
										}
									}
									scripts.add(script);
									scriptMap.put(name, script.getScriptID());
									localConstants.clear();
								} else if (script == null) {				//Integrity check
									throw new ParseException("Instruction outside script", file, lineno);
								} else if ("local".equals(keyword)) {		//Local variables
									if (tks.length < 2) {
										throw new ParseException("Expected identifier after 'Local'", file, lineno);
									}
									String name = tks[1];
									int size = 1;
									//Parse array
									if (name.indexOf('[') >= 0) {
										int p = name.indexOf('[');
										size = Integer.parseInt(name.substring(p + 1, name.length() - 1));
										name = name.substring(0, p);
									}
									if (!isValidIdentifier(name)) {
										throw new ParseException("Invalid variable identifier", file, lineno);
									}
									script.getVariables().add(name);
									for (int i = 1; i < size; i++) {
										script.getVariables().add("LHVMA");
									}
								} else if ("constant".equals(tks[0])) {
									if (tks.length < 2) {
										throw new ParseException("Expected constant name after 'constant'", file, lineno);
									}
									try {
										SourceConst c = parseConst(tks[1]);
										localConstants.put(c.name, c);
									} catch (Exception e) {
										throw new ParseException(e.getMessage(), file, lineno);
									}
								} else if (keyword.endsWith(":")) {			//Labels
									int ip = instructions.size();
									String label = keyword.substring(0, keyword.length() - 1);
									labels.put(ip, label);
									labelMap.put(label, ip);
								} else {									//Instructions
									String operand = tks.length < 2 ? null : tks[1];
									Instruction instr = Instruction.fromKeyword(keyword);
									if (instr == null) {
										throw new ParseException("Unknown opcode '" + keyword + "'", file, lineno);
									}
									instr.lineNumber = lineno;
									if (instr.opcode.hasArg || instr.isZero()) {
										if (instr.opcode == OPCode.POP) {
											if (operand != null) {
												if (instr.dataType == DataType.FLOAT) {
													instr.flags = OPCodeFlag.REF;	//Weird, but it works this way...
												}
												if (!isValidIdentifier(operand)) {
													throw new ParseException("Invalid variable name", file, lineno);
												} else {
													String base = operand;
													int offset = 0;
													if (base.indexOf('+') >= 0) {
														int p = base.indexOf('+');
														offset = Integer.parseInt(base.substring(p + 1).trim());
														base = base.substring(0, p).trim();
													}
													int varIndex = script.getLocalVarIndex(base);
													if (varIndex >= 0) {
														instr.intVal = script.getGlobalCount() + 1 + varIndex + offset;
													} else {
														varIndex = globalMap.getOrDefault(base, -1);
														if (varIndex < 0) {
															throw new ParseException("Undefined variable "+base, file, lineno);
														}
														instr.intVal = varIndex + offset;
													}
												}
											}
										} else if (instr.opcode == OPCode.SYS) {
											try {
												instr.intVal = NativeFunction.valueOf(operand).ordinal();
											} catch (IllegalArgumentException e) {
												throw new ParseException("Unknown native function '" + operand + "'", file, lineno);
											}
										} else if (instr.opcode == OPCode.SWAP) {
											if (operand != null) {	//Default = 0
												Integer iv = parseInt(operand);
												if (iv != null) {
													instr.intVal = iv;
												} else {
													throw new ParseException("Expected integer value", file, lineno);
												}
											}
										} else {
											if (operand == null) throw new ParseException(keyword + " requires an operand", file, lineno);
											if (instr.opcode.isIP) {
												Integer ip = parseInt(operand);
												if (ip != null) {
													instr.intVal = ip;
													if (instr.opcode.isJump && ip > instructions.size()) {
														instr.flags = FORWARD;
													}
												} else if (!isValidIdentifier(operand)) {
													throw new ParseException("Invalid label", file, lineno);
												} else {
													labelsToResolve.add(new LabelToResolve(file, instructions.size(), instr, operand));
												}
											} else if (instr.opcode.isScript) {
												Integer iv = parseInt(operand);
												if (iv != null) {
													instr.intVal = iv;
												} else if (!isValidIdentifier(operand)) {
													throw new ParseException("Invalid script name", file, lineno);
												} else {
													scriptsToResolve.add(new ScriptToResolve(file, instr, operand));
												}
											} else if (operand.startsWith("[")) {
												if (!operand.endsWith("]")) {
													throw new ParseException("Expected ']'", file, lineno);
												}
												instr.flags = REF;
												String v = operand.substring(1, operand.length() - 1);
												String base = v;
												int offset = 0;
												if (base.indexOf('+') >= 0) {
													int p = base.indexOf('+');
													offset = Integer.parseInt(base.substring(p + 1).trim());
													base = base.substring(0, p).trim();
												}
												Integer iv = parseInt(base);
												if (iv != null) {
													instr.intVal = iv;
												} else if (!isValidIdentifier(base)) {
													throw new ParseException("Invalid identifier: "+base, file, lineno);
												} else {
													int varIndex = script.getLocalVarIndex(base);
													if (varIndex >= 0) {
														instr.intVal = script.getGlobalCount() + 1 + varIndex + offset;
													} else {
														varIndex = globalMap.getOrDefault(base, -1);
														if (varIndex < 0) {
															throw new ParseException("Undefined variable "+base, file, lineno);
														}
														instr.intVal = varIndex + offset;
													}
												}
											} else if (instr.dataType == DataType.FLOAT) {
												Float v = parseImmed(Float.class, operand, localConstants);
												if (v == null) {
													throw new ParseException("Invalid float value", file, lineno);
												}
												instr.floatVal = v;
											} else if (instr.dataType == DataType.INT || instr.dataType == DataType.NONE) {
												Integer v = parseImmed(Integer.class, operand, localConstants);
												if (v == null) {
													throw new ParseException("Invalid int value", file, lineno);
												}
												instr.intVal = v;
											} else if (instr.dataType == DataType.BOOLEAN) {
												Boolean v = parseImmed(Boolean.class, operand, localConstants);
												if (v == null) {
													throw new ParseException("Invalid boolean value", file, lineno);
												}
												instr.boolVal = v;
											} else if (instr.dataType == DataType.VAR) {
												String base = operand;
												int offset = 0;
												if (base.indexOf('+') >= 0) {
													int p = base.indexOf('+');
													offset = Integer.parseInt(base.substring(p + 1).trim());
													base = base.substring(0, p).trim();
												}
												Integer iv = parseInt(base);
												if (iv != null) {
													instr.intVal = iv;
												} else if (!isValidIdentifier(base)) {
													throw new ParseException("Invalid identifier: "+base, file, lineno);
												} else {
													int varIndex = script.getLocalVarIndex(base);
													if (varIndex >= 0) {
														instr.intVal = script.getGlobalCount() + 1 + varIndex + offset;
													} else {
														varIndex = globalMap.getOrDefault(base, -1);
														if (varIndex < 0) {
															throw new ParseException("Undefined variable "+base, file, lineno);
														}
														instr.intVal = varIndex + offset;
													}
												}
											}
										}
									} else if (operand != null) {
										throw new ParseException("Instruction " + keyword + " must have no operands", file, lineno);
									}
									assert instr.dataType != null: "Data type not specified at "+file.getName()+":"+lineno;
									instructions.add(instr);
									if (instr.opcode == OPCode.END) {
										script = null;					//To force integrity check
									}
								}
							}
							break;
						case 'A':
							line = removeComment(line).trim();
							if (!line.isEmpty()) {
								if (line.startsWith("run script ")) {
									String name = line.substring(11).trim();
									if (name.isEmpty()) {
										throw new ParseException("Expected script name after 'run script'", file, lineno);
									}
									if (!isValidIdentifier(name)) {
										throw new ParseException("Invalid script name", file, lineno);
									}
									Integer scriptID = scriptMap.get(name);
									if (scriptID == null) {
										throw new ParseException("Script does not exist", file, lineno);
									}
									autoStartScripts.add(scriptID);
									scriptsUsageCount[scriptID - 1]++;
								} else {
									throw new ParseException("Expected 'run script' command", file, lineno);
								}
							}
							break;
						case 'D':
							String[] tks = split2(line);
							String keyword = removeComment(tks[0]);
							if (!keyword.isEmpty()) {
								if (tks.length < 2) {
									throw new ParseException("Expected identifier after datatype", file, lineno);
								}
								tks = removeComment(tks[1]).split("\\s*=\\s*", 2);
								String name = tks[0].trim();
								if (!isValidIdentifier(name)) {
									throw new ParseException("Invalid identifier", file, lineno);
								}
								if (tks.length < 2) {
									throw new ParseException("Expected '=' after identifier", file, lineno);
								}
								String expr = tks[1].trim();
								if (expr.isEmpty()) {
									throw new ParseException("Expected expression after '='", file, lineno);
								}
								while (true) {
									try {
										if (DataSection.ConstType.BYTE.keyword.equals(keyword)) {
											dataBuffer.put(Byte.valueOf(expr));
										} else if (DataSection.ConstType.STRING.keyword.equals(keyword)) {
											String value = expr.substring(1, expr.length() - 1);
											value = value.replace("\\\"", "\"");
											value = value.replace("\\\\", "\\");
											dataBuffer.put(value.getBytes(ASCII));
											dataBuffer.put((byte)0);
										} else {
											throw new ParseException("Unknown datatype '"+keyword+"'", file, lineno);
										}
										break;
									} catch (NumberFormatException e) {
										throw new ParseException(e, file, lineno);
									} catch (BufferOverflowException e) {
										int capacity = dataBuffer.capacity() * 2;
										if (capacity > MAX_BUFFER_SIZE) {
											throw new ParseException("Data exceeds "+MAX_BUFFER_SIZE+" bytes limit", file, lineno);
										}
										info("Data buffer full, increasing capacity to " + capacity);
										dataBuffer = resize(dataBuffer, capacity);
									}
								}
							}
							break;
						default:
							String s = line.split("//", 2)[0].trim();
							if (!s.isEmpty()) {
								throw new ParseException("Section not set", file, lineno, 1);
							}
					}
				}
				line = str.readLine();
				lineno++;
			}
		}
	}
	
	private SourceConst parseConst(String s) throws Exception {
		String[] tks = removeComment(s).split("\\s*=\\s*", 2);
		String name = tks[0].trim();
		if (!isValidIdentifier(name)) {
			throw new Exception("Invalid identifier");
		}
		if (tks.length < 2) {
			throw new Exception("Expected '=' after identifier");
		}
		String expr = tks[1].trim();
		if (expr.isEmpty()) {
			throw new Exception("Expected expression after '='");
		}
		if (isValidIdentifier(expr)) {
			SourceConst c = globalConstants.get(expr);
			if (c == null) {
				throw new Exception(expr + " has not been defined previously");
			}
			return new SourceConst(name, c.value);
		} else {
			Object value = parseImmed(expr);
			if (value == null) {
				throw new Exception("Invalid value");
			}
			return new SourceConst(name, value);
		}
	}
	
	@SuppressWarnings("unchecked")
	private <E> E parseImmed(Class<E> type, String s, Map<String, SourceConst> localConstants) {
		Object r = parseImmed(s, localConstants);
		if (r == null) return null;
		if (type.isAssignableFrom(r.getClass())) return (E) r;
		return null;
	}
	
	private Object parseImmed(String s, Map<String, SourceConst> localConstants) {
		if (isValidIdentifier(s)) {
			if (localConstants != null) {
				SourceConst c = localConstants.get(s);
				if (c != null) return c.value;
			}
			if (globalConstants != null) {
				SourceConst c = globalConstants.get(s);
				if (c != null) return c.value;
			}
		}
		return parseImmed(s);
	}
	
	private static Object parseImmed(String s) {
		if ("true".equals(s)) return Boolean.TRUE;
		if ("false".equals(s)) return Boolean.FALSE;
		Object v = parseString(s);
		if (v != null) return v;
		if (s.indexOf('.') >= 0) {
			v = parseFloat(s);
			if (v != null) return v;
		}
		v = parseInt(s);
		return v;
	}
	
	private static String parseString(String s) {
		if (!s.startsWith("\"") || !s.endsWith("\"")) return null;
		s = s.substring(1, s.length() - 1);
		s = s.replace("\\\\", "\\");
		s = s.replace("\\\"", "\"");
		s = s.replace("\\r", "\r");
		s = s.replace("\\n", "\n");
		s = s.replace("\\t", "\t");
		return s;
	}
	
	private static Float parseFloat(String s) {
		try {
	        return Float.parseFloat(s);
	    } catch (NumberFormatException e) {
	        return null;
	    }
	}
	
	private static Integer parseInt(String s) {
		try {
			if (s.startsWith("0x")) {
				return Integer.parseInt(s.substring(2), 16);
			} else {
				return Integer.parseInt(s);
			}
	    } catch (NumberFormatException e) {
	        return null;
	    }
	}
	
	private static boolean isValidIdentifier(String s) {
	    if (s.isEmpty()) return false;
	    if ("true".equals(s)) return false;
	    if ("false".equals(s)) return false;
	    if (!Character.isJavaIdentifierStart(s.charAt(0))) return false;
	    for (int i = 1; i < s.length(); i++) {
	        if (!Character.isJavaIdentifierPart(s.charAt(i))) return false;
	    }
	    return true;
	}
	
	private static String[] split2(String s) {
		return s.split("\\s+", 2);
	}
	
	private static String removeComment(String s) {
		int p = s.indexOf("//");
		if (p < 0) return s;
		return s.substring(0, p);
	}
	
	private static ByteBuffer resize(ByteBuffer buffer, int capacity) {
        ByteBuffer newBuffer = ByteBuffer.allocate(capacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }
	
	
	private static class LabelToResolve {
		public final File file;
		public final int index;
		public final Instruction instr;
		public final String name;
		
		public LabelToResolve(File file, int index, Instruction instr, String name) {
			this.file = file;
			this.index = index;
			this.instr = instr;
			this.name = name;
		}
	}
	
	
	private static class ScriptToResolve {
		public final File file;
		public final Instruction instr;
		public final String name;
		
		public ScriptToResolve(File file, Instruction instr, String name) {
			this.file = file;
			this.instr = instr;
			this.name = name;
		}
	}
	
	
	private static class SourceConst {
		public final String name;
		public final Object value;
		
		public SourceConst(String name, Object value) {
			this.name = name;
			this.value = value;
		}
		
		@Override
		public String toString() {
			if (value instanceof String) {
				String t = (String)value;
				t = t.replace("\\", "\\\\");
				t = t.replace("\"", "\\\"");
				return "\"" + t + "\"";
			} else {
				return String.valueOf(value);
			}
		}
	}
}
