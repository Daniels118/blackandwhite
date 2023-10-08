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
		int v = str.readInt();
		if (v < 0 || v >= OPCode.values().length) throw new InvalidOPCodeException(v);
		opcode = OPCode.values()[v];
		//
		flags = str.readInt();
		//
		v = str.readInt();
		if (v < 0 || v >= DataType.values().length) throw new InvalidDataTypeException(v);
		dataType = DataType.values()[v];
		//
		if ((flags & REF) != 0 || opcode.forceInt) {
			intVal = str.readInt();	//Address of variables, system functions index and swap count are always int, regardless of the datatype
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
				default:
					intVal = str.readInt();
			}
		}
		//
		lineNumber = str.readInt();
		//
		if (opcode == OPCode.SYS) {
			NativeFunction.fromCode(intVal);
		}
	}
	
	@Override
	public void write(EndianDataOutputStream str) throws Exception {
		str.writeInt(opcode.ordinal());
		str.writeInt(flags);
		str.writeInt(dataType.ordinal());
		if ((flags & REF) != 0 || opcode.forceInt) {
			str.writeInt(intVal);	//Address of variables, system functions index, and swap count are always int, regardless of the datatype
		} else {
			switch (dataType) {
				case FLOAT:
					str.writeFloat(floatVal);
					break;
				case BOOLEAN:
					str.writeInt(boolVal ? 1 : 0);
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
				if (func.varargs) return -1;
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
	public String toString(CHLFile chl, Script script, Map<Integer, ?> labels) {
		String s = getKeyword();
		boolean popNull = opcode == OPCode.POP && intVal == 0;
		boolean isZero = opcode == OPCode.CAST && (flags & ZERO) != 0;
		boolean swapZero = opcode == OPCode.SWAP && intVal == 0;
		if (opcode.hasArg && !popNull && !swapZero || isZero) {
			s += " ";
			if (opcode == OPCode.SYS) {
				try {
					NativeFunction f = NativeFunction.fromCode(intVal);
					s += f.name();
				} catch (InvalidNativeFunctionException e) {
					s += intVal;
				}
			} else if (opcode == OPCode.CALL && chl != null) {
				List<Script> scripts = chl.getScriptsSection().getItems();
				if (intVal >= 0 && intVal < scripts.size()) {
					s += scripts.get(intVal).getName();
				} else {
					s += intVal;
				}
			} else if (opcode.isIP) {
				Object label = null;
				if (labels != null) label = labels.get(intVal);
				s += (label != null) ? label : String.format("0x%1$08X", intVal);
			} else if ((flags & REF) != 0) {
				if (chl != null && script != null) {
					String varName;
					try {
						varName = script.getVar(chl, intVal);
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
						s += String.format("0x%1$08X", intVal);
					} else {
						s += String.format("[0x%1$08X]", intVal);
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
					default:
						s += intVal;
				}
			}
		}
		return s;
	}
	
	public void validate() throws InvalidInstructionAddressException, InvalidScriptIdException, InvalidNativeFunctionException {
		validate(null, null);
	}
	
	public void validate(CHLFile chl, Script script) throws InvalidInstructionAddressException, InvalidScriptIdException, InvalidNativeFunctionException {
		boolean popNull = opcode == OPCode.POP && intVal == 0;
		boolean isZero = opcode == OPCode.CAST && (flags & ZERO) != 0;
		boolean swapZero = opcode == OPCode.SWAP && intVal == 0;
		if (opcode.hasArg && !popNull && !swapZero || isZero) {
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
				} else if ((flags & REF) != 0) {
					script.getVar(chl, intVal);
				}
			}
		}
	}
	
	public boolean isFree() {
		return opcode == OPCode.ENDEXCEPT && (flags & FREE) == FREE;
	}
	
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
	 *   - at least one decimal digit, even if more then 7 total.
	 * It isn't perfect, but very close.
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
