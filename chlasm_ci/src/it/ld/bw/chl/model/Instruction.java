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

import it.ld.bw.chl.exceptions.InvalidBooleanException;
import it.ld.bw.chl.exceptions.InvalidDataTypeException;
import it.ld.bw.chl.exceptions.InvalidInstructionAddressException;
import it.ld.bw.chl.exceptions.InvalidInstructionException;
import it.ld.bw.chl.exceptions.InvalidNativeFunctionException;
import it.ld.bw.chl.exceptions.InvalidOPCodeException;
import it.ld.bw.chl.exceptions.InvalidScriptIdException;
import it.ld.bw.chl.exceptions.InvalidVariableIdException;
import it.ld.utils.EndianDataInputStream;
import it.ld.utils.EndianDataOutputStream;

import static it.ld.bw.chl.model.OPCodeFlag.*;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Instruction extends Struct {
	public static final int LENGTH = 5 * 4;	// 5 fields of 4 bytes
	
	private static final int SIGNIFICANT_DIGITS = 8;
	private static final DecimalFormat decimalFormat = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
	
	/**This holds a mapping between mnemonics and sample instructions.*/
	private static final Map<String, Instruction> model = new HashMap<>();
	
	static {
		decimalFormat.setMinimumFractionDigits(1);
		//Build the mapping between mnemonics and sample instructions
		for (int iCode = 0; iCode < OPCode.keywords.length; iCode++) {
			final String[][] t = OPCode.keywords[iCode];
			if (t != null) {
				for (int flags = 0; flags < t.length; flags++) {
					final String[] t2 = t[flags];
					if (t2 != null) {
						for (int iType = 0; iType < t2.length; iType++) {
							final String keyword = t2[iType];
							if (keyword != null) {
								Instruction instr = new Instruction();
								instr.opcode = OPCode.values()[iCode];
								instr.flags = flags;
								instr.dataType = DataType.values()[iType];
								model.putIfAbsent(keyword, instr);
							}
						}
					}
				}
			}
		}
	}
	
	public OPCode opcode;
	public int flags;
	public DataType dataType;
	public int intVal;
	public float floatVal;
	public boolean boolVal;
	public int lineNumber;
	
	@Override
	public int getLength() {
		return LENGTH;
	}
	
	@Override
	public void read(EndianDataInputStream str) throws Exception {
		//# Profiler.start(ProfilerSections.PF_INSTR_OPCODE);
		int v = str.readInt();
		if (v < 0 || v >= OPCode.values().length) {
			throw new InvalidOPCodeException(v);
		}
		opcode = OPCode.values()[v];
		//# Profiler.end(ProfilerSections.PF_INSTR_OPCODE);
		//
		//# Profiler.start(ProfilerSections.PF_INSTR_FLAGS);
		flags = str.readInt();
		//# Profiler.end(ProfilerSections.PF_INSTR_FLAGS);
		//
		//# Profiler.start(ProfilerSections.PF_INSTR_DATATYPE);
		v = str.readInt();
		if (v < 0 || v >= DataType.values().length) throw new InvalidDataTypeException(v);
		dataType = DataType.values()[v];
		//# Profiler.end(ProfilerSections.PF_INSTR_DATATYPE);
		//
		//# Profiler.start(ProfilerSections.PF_INSTR_OPERAND);
		if (isReference() || opcode.forceInt) {
			//Address of variables, system functions index and swap count are always int, regardless of the datatype
			if (dataType == DataType.VAR) {
				intVal = (int)str.readFloat();
			} else {
				intVal = str.readInt();
			}
		} else {
			switch (dataType) {
				case FLOAT:
					floatVal = str.readFloat();
					break;
				case BOOLEAN:
					v = str.readInt();
					if (v != 0 && v != 1) throw new InvalidBooleanException(v);
					boolVal = v != 0;
					break;
				case VAR:
					intVal = (int)str.readFloat();
					break;
				default:
					intVal = str.readInt();
			}
		}
		//# Profiler.end(ProfilerSections.PF_INSTR_OPERAND);
		//
		//# Profiler.start(ProfilerSections.PF_INSTR_LINENO);
		lineNumber = str.readInt();
		//# Profiler.end(ProfilerSections.PF_INSTR_LINENO);
		//
		if (!opcode.hasArg && (intVal != 0 || floatVal != 0 || boolVal != false)) {
			System.out.println(this + " " + intVal + " " + floatVal + " " + boolVal);
			throw new RuntimeException("Invalid operand for noarg opcode");
		}
		if (opcode == OPCode.SYS) {
			NativeFunction.fromCode(intVal);
		}
	}
	
	@Override
	public void write(EndianDataOutputStream str) throws Exception {
		str.writeInt(opcode.ordinal());
		str.writeInt(flags);
		str.writeInt(dataType.ordinal());
		if (isReference() || opcode.forceInt) {
			//Address of variables, system functions index, and swap count are always int, regardless of the datatype
			if (dataType == DataType.VAR) {
				str.writeFloat(intVal);
			} else {
				str.writeInt(intVal);
			}
		} else {
			switch (dataType) {
				case FLOAT:
					str.writeFloat(floatVal);
					break;
				case BOOLEAN:
					str.writeInt(boolVal ? 1 : 0);
					break;
				case VAR:
					str.writeFloat(intVal);
					break;
				default:
					str.writeInt(intVal);
			}
		}
		str.writeInt(lineNumber);
	}
	
	/**Gets the mnemonic used to code this instruction.
	 * @return
	 */
	public String getKeyword() {
		return OPCode.getKeyword(opcode.ordinal(), flags, dataType.ordinal());
	}
	
	/**Gets the number of values this instruction pops from the stack.
	 * @param chl
	 * @return the number of values popped from the stack, or -1 if this cannot be determined at compile time.
	 * @throws InvalidScriptIdException
	 * @throws InvalidNativeFunctionException
	 */
	public int getPopCount(CHLFile chl) throws InvalidScriptIdException, InvalidNativeFunctionException {
		switch (opcode) {
			case SYS:
				NativeFunction func = NativeFunction.fromCode(intVal);
				if (func.varargs) return -1;	//Cannot determine the number of parameter for varargs
				return func.pop;
			case CALL:
				return chl.getScriptsSection().getScript(intVal).getParameterCount();
			case SWAP:
				return dataType == DataType.COORDS ? 6 : 2;
			case ADD:
			case SUB:
				return dataType == DataType.COORDS ? 6 : 2;
			default:
				assert !opcode.varStack: "Variable input not set for " + opcode;
				return opcode.pop;
		}
	}
	
	/**Gets the number of values pushed to the stack after the execution of this instruction.
	 * @param chl
	 * @return
	 * @throws InvalidNativeFunctionException
	 */
	public int getPushCount(CHLFile chl) throws InvalidNativeFunctionException {
		switch (opcode) {
		case SYS:
			return NativeFunction.fromCode(intVal).push;
		case CALL:
			return 0;	//User defined scripts cannot return values
		case SWAP:
			return dataType == DataType.COORDS ? 6 : 2;
		case ADD:
		case SUB:
			return dataType == DataType.COORDS ? 3 : 1;
		default:
			assert !opcode.varStack: "Variable output not set for " + opcode;
			return opcode.pop;
		}
	}
	
	@Override
	public String toString() {
		return toString(null, null, null);
	}
	
	/**Gets the string representation of this instruction. If additional parameters are
	 * specified, this method tries to replace raw values with known symbols where possible.
	 * @param chl may be null
	 * @param script may be null
	 * @param labels may be null
	 * @return
	 */
	public String toString(CHLFile chl, Script script, Map<Integer, ? extends ILabel> labels) {
		String s = getKeyword();
		boolean popNull = opcode == OPCode.POP && intVal == 0;
		boolean swapZero = opcode == OPCode.SWAP && intVal == 0;
		if (opcode.hasArg && !popNull && !swapZero || isZero()) {
			s += " ";
			if (opcode == OPCode.SYS) {
				try {
					NativeFunction f = NativeFunction.fromCode(intVal);
					s += f.name();
				} catch (InvalidNativeFunctionException e) {
					s += intVal;
				}
			} else if (opcode == OPCode.CALL && chl != null) {
				try {
					Script calledScript = chl.getScriptsSection().getScript(intVal);
					s += calledScript.getName();
				} catch (InvalidScriptIdException e) {
					s += intVal;
				}
			} else if (opcode.isIP) {
				ILabel label = null;
				if (labels != null) label = labels.get(intVal);
				if (label != null) {
					s += label.toString(this);
				} else if (script != null) {
					int relIp = intVal - script.getInstructionAddress();
					s += script.getInstructionAddress() + "+" + relIp;
				} else {
					s += intVal;
				}
			} else if (isReference()) {
				if (chl != null && script != null) {
					String varName;
					try {
						//varName = script.getVar(chl, intVal);
						varName = getVar(chl, script, intVal);
					} catch (InvalidVariableIdException e) {
						varName = String.valueOf(intVal);
					}
					if (opcode == OPCode.POP) {
						s += varName;
					} else {
						s += "[" + varName + "]";
					}
				} else {
					if (opcode == OPCode.POP) {
						s += intVal;
					} else {
						s += "[" + intVal + "]";
					}
				}
			} else if (opcode.forceInt) {
				s += intVal;	//The SWAP argument is always an integer regardless of the datatype
			} else {
				switch (dataType) {
					case FLOAT:
						s += format(floatVal);
						break;
					case BOOLEAN:
						s += (boolVal ? "true" : "false");
						break;
					case VAR:
						if (chl != null && script != null) {
							String varName;
							try {
								varName = script.getVar(chl, intVal);
								//varName = getVar(chl, script, intVal);
							} catch (InvalidVariableIdException e) {
								varName = String.valueOf(intVal);
							}
							s += varName;
						} else {
							s += intVal;
						}
						break;
					default:
						s += intVal;
				}
			}
		}
		return s;
	}
	
	private static String getVar(CHLFile chl, Script script, int id) {
		List<String> names;
		if (id > script.getGlobalCount()) {
			id -= script.getGlobalCount() + 1;
			names = script.getVariables();
		} else {
			id--;
			names = chl.getGlobalVariables().getNames();
		}
		String name = names.get(id);
		if ("LHVMA".equals(name)) {
			int index = 0;
			do {
				id--;
				index++;
				name = names.get(id);
			} while ("LHVMA".equals(name));
			return name+"+"+index;
		} else {
			id++;
			if (id < names.size() && "LHVMA".equals(names.get(id))) {
				return name+"+0";
			} else {
				return name;
			}
		}
	}
	
	/**Verify the correctness of this instruction.
	 * @throws InvalidInstructionAddressException
	 * @throws InvalidScriptIdException
	 * @throws InvalidNativeFunctionException
	 * @throws InvalidInstructionException 
	 */
	public void validate() throws InvalidInstructionAddressException, InvalidScriptIdException, InvalidNativeFunctionException, InvalidInstructionException {
		validate(null, null, -1);
	}
	
	/**Verify the correctness of this instruction, taking into account the context where it resides.
	 * @param chl
	 * @param script
	 * @throws InvalidInstructionAddressException
	 * @throws InvalidScriptIdException
	 * @throws InvalidNativeFunctionException
	 * @throws InvalidInstructionException 
	 */
	public void validate(CHLFile chl, Script script, int index) throws InvalidInstructionAddressException, InvalidScriptIdException, InvalidNativeFunctionException, InvalidInstructionException {
		boolean popNull = opcode == OPCode.POP && intVal == 0;
		boolean swapZero = opcode == OPCode.SWAP && intVal == 0;
		if (opcode.hasArg && !popNull && !swapZero || isZero()) {
			if (opcode == OPCode.SYS) {
				NativeFunction.fromCode(intVal);
			}
			if (chl != null) {
				if (opcode == OPCode.CALL) {
					chl.getScriptsSection().getScript(intVal);
				} else if (opcode.isIP) {
					if (intVal < 0 || intVal >= chl.getCode().getItems().size()) {
						throw new InvalidInstructionAddressException(intVal);
					}
					if (opcode.isJump) {
						if (isForward()) {
							if (intVal < index) {
								throw new InvalidInstructionException("The FORWARD flag is set, but the target address is lower than the current address");
							}
						} else {
							if (intVal > index) {
								throw new InvalidInstructionException("The FORWARD flag is not set, but the target address is greater than the current address");
							}
						}
					}
				} else if (isReference()) {
					script.getVar(chl, intVal);
				}
			}
		}
	}
	
	/**Tells if the operand value is a reference to a variable.
	 * @return
	 */
	public boolean isReference() {
		return (opcode == OPCode.PUSH || opcode == OPCode.POP || opcode == OPCode.CAST) && (flags & REF) == REF;
	}
	
	/**For a jump instruction, tells if the target address is greater than the current address.
	 * @return
	 */
	public boolean isForward() {
		return opcode.isJump && (flags & FORWARD) == FORWARD;
	}
	
	/**Tells if this is a START instruction. This is a shorthand to test if the opcode is CALL and
	 * the ASYNC flag is set.
	 * @return
	 */
	public boolean isStart() {
		return opcode == OPCode.CALL && (flags & ASYNC) == ASYNC;
	}
	
	/**Tells if this is a ZERO instruction. This is a shorthand to test if the opcode is CAST and
	 * the ZERO flag is set.
	 * @return
	 */
	public boolean isZero() {
		return opcode == OPCode.CAST && (flags & ZERO) == ZERO;
	}
	
	/**Tells if this is a FREE instruction. This is a shorthand to test if the opcode is ENDEXCEPT and
	 * the FREE flag is set.
	 * @return
	 */
	public boolean isFree() {
		return opcode == OPCode.ENDEXCEPT && (flags & FREE) == FREE;
	}
	
	/**Creates an Instruction instance based on the given mnemonic. The instruction has the opcode, flags and
	 * datatype already initialized. Flags and datatype may require further modifications depending on the operand.
	 * @param keyword
	 * @return
	 */
	public static Instruction fromKeyword(String keyword) {
		Instruction m = model.get(keyword);
		if (m == null) return null;
		Instruction r = new Instruction();
		r.opcode = m.opcode;
		r.flags = m.flags;
		r.dataType = m.dataType;
		return r;
	}
	
	/**This method tries to format float numbers like they where coded in the original source scripts, i.e.:
	 *   - simple decimal format (no scientific notation, etc.);
	 *   - at most 7 significant digits shared between int and decimal part;
	 *   - at least one decimal digit, even if more then 7 int digits.
	 * Of course it can't always succeed, but most of times does or gets very close.
	 * @param v
	 * @return
	 */
	private static String format(float v) {
		decimalFormat.setMaximumFractionDigits(SIGNIFICANT_DIGITS - 1);
		String r = decimalFormat.format(v);
		int nInt = r.indexOf('.');	//Compute the number of int digits
		if (nInt > 1) {
			int nDec = Math.max(1, Math.min(SIGNIFICANT_DIGITS - nInt, SIGNIFICANT_DIGITS - 1));
			decimalFormat.setMaximumFractionDigits(nDec);
			r = decimalFormat.format(v);
		}
		return r;
	}
}
