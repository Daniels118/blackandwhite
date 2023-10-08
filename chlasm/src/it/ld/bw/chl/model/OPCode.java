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

import static it.ld.bw.chl.model.OPCodeAttr.*;

/* About SWAP
 * 
 * "SWAP n" swaps stack[top] with stack[top - n]. This is used to prepend a value on the stack, for example:
 *		PUSHI 346
 *		SYS CONVERT_CAMERA_POSITION	//(1, 3)
 *		PUSHF 7.0
 *		SWAPF 4
 *		SYS MOVE_CAMERA_POSITION	//(4, 0)
 * It is unclear why the value "4" isn't pushed just before calling CONVERT_CAMERA_POSITION.
 * 
 * SWAP alone is used to swap the last 2 values on the stack. This is used to insert the vertical coordinate
 * within a 2D horizontal coordinates coded in source files, for example:
 * 		[1865.61,2641.24]
 * becomes:
 *		PUSHF 1865.61
 *		CASTC
 *		PUSHF 2641.24
 *		CASTC
 *		PUSHC 0
 *		SWAPI
 * This could be avoided by implementing a proper rule in the parser.
 * 
 * 
 * About SYS2
 * SYS2 is used to call the following functions:
 *   GET_PROPERTY				*called with SYS too
 *   SET_PROPERTY
 *   SET_INTERFACE_INTERACTION
 *   RANDOM_ULONG
 *   SET_GAMESPEED
 *   GET_ALIGNMENT
 *   
 * When calling GET_PROPERTY, SYS2 is used in the following cases:
 * - when the requested property is a boolean, in which case the result must be casted to bool;
 * - in the implementation of property assignment (see 5.2.2 in instruction_examples.txt).
 * 
 * 
 * About CALL/START
 * Arguments must be pushed in the same order they appear in the script signature, and must be popped
 * in the same order. This is conceptually wrong, so I guess the LHVM automatically inverts the order
 * of the last N values on the stack (where N is the number of parameters of the script). Please note
 * that parameters of user scripts cannot be of coordinate type, so there is no risk of coordinate
 * inversion (i.e. [x,y,z] to [z,y,x]).
 * 
 * 
 * VSTACK includes:
 * - opcodes which may operate both on native types and Coord;
 * - opcodes which call user scripts or native functions.
 */

public enum OPCode {
	END(),					//0x00 Alias: RET, END
	JZ(1, 0, JUMP),			//0x01 Alias: JZ, WAIT
	PUSH(0, 1, ARG),		//0x02
	POP(1, 0, ARG),			//0x03
	ADD(VSTACK),			//0x04
	SYS(ARG|FINT|VSTACK),	//0x05 Alias: SYS, SYS2, CALL
	SUB(VSTACK),			//0x06
	NEG(1, 1),				//0x07 Alias: NEG, UMINUS
	MUL(2, 1),				//0x08 Alias: MUL, TIMES
	DIV(2, 1),				//0x09
	MOD(2, 1),				//0x0A
	NOT(1, 1),				//0x0B
	AND(2, 1),				//0x0C
	OR(2, 1),				//0x0D
	EQ(2, 1),				//0x0E
	NEQ(2, 1),				//0x0F
	GEQ(2, 1),				//0x10
	LEQ(2, 1),				//0x11
	GT(2, 1),				//0x12
	LT(2, 1),				//0x13
	JMP(JUMP),				//0x14
	SLEEP(1, 1),			//0x15 Alias: SLEEP, DLY
	EXCEPT(0, 1, IP),		//0x16 Alias: EXCEPT, BLK
	CAST(1, 1),				//0x17 Alias: CAST, ZERO
	CALL(SCRIPT|VSTACK),	//0x18 Alias: CALL, START, RUN
	ENDEXCEPT(1, 0),		//0x19 Alias: FREE, ENDB, ENDEXCEPT
	RETEXCEPT(),			//0x1A Never found
	ITEREXCEPT(),			//0x1B Alias: FAILEXCEPT, ITER
	BRKEXCEPT(1, 0),		//0x1C Alias: BRKEXCEPT, ENDC
	SWAP(ARG|FINT|VSTACK);	//0x1D 
	//LINE(ARG);			//0x1E Never found
	
	/**This field maps the tuple {opcode, flags, datatype} to the respective mnemonic.
	 * Access it as keywords[opcode][flags][dataType]
	 */
	static final String[][][] keywords = new String[][][] {
/*00*/	{{"END"}},
/*01*/	{{null, "JZ"}, {null, "JZ"}},
/*02*/	{{null, "PUSHI", "PUSHF", "PUSHC", "PUSHO", null, "PUSHB"}, {null, "PUSHI", "PUSHF", "PUSHC", "PUSHO", null, "PUSHB"}},
/*03*/	{{null, "POPI", "POPF", "POPC", "POPO", null, "POPB"}, {null, null, "POPF"}},
/*04*/	{{null, "ADDI", "ADDF", "ADDC"}},
/*05*/	{{"SYS", null, "SYS2"}},
/*06*/	{{null, "SUBI", "SUBF", "SUBC"}},
/*07*/	{{null, null, "NEG"}},
/*08*/	{{null, null, "MUL"}},
/*09*/	{{null, null, "DIV"}},
/*0A*/	{{null, "MOD"}},
/*0B*/	{{null, "NOT"}},
/*0C*/	{{null, "AND"}},
/*0D*/	{{null, "OR"}},
/*0E*/	{{null, null, "EQ"}},
/*0F*/	{{null, null, "NEQ"}},
/*10*/	{{null, null, "GEQ"}},
/*11*/	{{null, null, "LEQ"}},
/*12*/	{{null, null, "GT"}},
/*13*/	{{null, null, "LT"}},
/*14*/	{{null, "JMP"}, {null, "JMP"}},
/*15*/	{{null, null, "SLEEP"}},
/*16*/	{{null, "EXCEPT"}},
/*17*/	{{null, "CASTI", "CASTF", "CASTC", "CASTO", null, "CASTB"}, {null, null, "ZERO"}},
/*18*/	{{null, "CALL"}, {null, "START"}},
/*19*/	{{null, "ENDEXCEPT"}, {null, "FREE"}},
/*1A*/	null,	//RETEXCEPT
/*1B*/	{{null, "ITEREXCEPT"}},
/*1C*/	{{null, "BRKEXCEPT"}},
/*1D*/	{{null, "SWAPI", "SWAPF"}},
/*1E*/	null	//LINE
	};
	
	/**Tells if this opcode expects an immediate value.*/
	public final boolean hasArg;
	/**Tells if the argument for this opcode is an instruction index.*/
	public final boolean isIP;
	/**Tells if this opcode is a jump, meaning that its operand is an instruction index. This
	 * must also be taken into account to set the FORWARD flag.*/
	public final boolean isJump;
	/**Tells if the argument for this opcode is a scriptID.*/
	public final boolean isScript;
	/**Tells if the argument for this opcode must be coded as an int regardless of the datatype.*/
	public final boolean forceInt;
	/**Tells if this opcode allows to push or pop a variable number of values on the stack.*/
	public final boolean varStack;
	
	/**Number of values popped from the stack, unless varStack is true.*/
	public final int pop;
	/**Number of values pushed to the stack, unless varStack is true.*/
	public final int push;
	
	OPCode() {
		this(0, 0, 0);
	}
	
	OPCode(int attr) {
		this(0, 0, attr);
	}
	
	OPCode(int pop, int push) {
		this(pop, push, 0);
	}
	
	OPCode(int pop, int push, int attr) {
		this.pop = pop;
		this.push = push;
		this.hasArg = (attr & ARG) == ARG;
		this.isIP = (attr & IP) == IP;
		this.isJump = (attr & JUMP) == JUMP;
		this.isScript = (attr & SCRIPT) == SCRIPT;
		this.forceInt = (attr & FINT) == FINT;
		this.varStack = (attr & VSTACK) == VSTACK;
	}
	
	/**Gets the mnemonic to code an instruction for the given opcode, flags and datatype.
	 * @param opcode
	 * @param flags
	 * @param dataType
	 * @return
	 */
	public static String getKeyword(int opcode, int flags, int dataType) {
		if (opcode < 0 || opcode >= keywords.length) throw new IllegalArgumentException("Invalid instruction code");
		String[][] t = keywords[opcode];
		if (t == null || flags < 0 || flags >= t.length) throw new IllegalArgumentException("Invalid flags");
		String[] t2 = t[flags];
		if (t2 == null || dataType < 0 || dataType >= t2.length || t2[dataType] == null) throw new IllegalArgumentException("Invalid datatype");
		return t2[dataType];
	}
}
