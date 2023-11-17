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
	END(),						// 0 Alias: RET, END
	JZ(1, 0, JUMP),				// 1 Alias: JZ, WAIT
	PUSH(0, 1, ARG),			// 2
	POP(1, 0, ARG),				// 3
	ADD(VSTACK),				// 4
	SYS(ARG|FINT|VSTACK),		// 5 Alias: SYS, SYS2, CALL
	SUB(VSTACK),				// 6
	NEG(1, 1),					// 7 Alias: NEG, UMINUS
	MUL(2, 1),					// 8 Alias: MUL, TIMES
	DIV(2, 1),					// 9
	MOD(2, 1),					//10
	NOT(1, 1),					//11
	AND(2, 1),					//12
	OR(2, 1),					//13
	EQ(2, 1),					//14
	NEQ(2, 1),					//15
	GEQ(2, 1),					//16
	LEQ(2, 1),					//17
	GT(2, 1),					//18
	LT(2, 1),					//19
	JMP(JUMP),					//20
	SLEEP(1, 1),				//21 Alias: SLEEP, DLY
	EXCEPT(0, 1, IP),			//22 Alias: EXCEPT, BLK
	CAST(1, 1),					//23 Alias: CAST, ZERO
	CALL(SCRIPT|VSTACK),		//24 Alias: CALL, START, RUN
	ENDEXCEPT(1, 0),			//25 Alias: FREE, ENDB, ENDEXCEPT
	RETEXCEPT(),				//26 Never found
	ITEREXCEPT(),				//27 Alias: FAILEXCEPT, ITER
	BRKEXCEPT(1, 0),			//28 Alias: BRKEXCEPT, ENDC
	SWAP(ARG|FINT|VSTACK),		//29 
	DUP(0, 1, ARG),				//30 Arg=0 means duplicate the last value on the stack; 1 the previous one and so on
	LINE(ARG),					//31
	REF_AND_OFFSET_PUSH(0, 1),	//32
	REF_AND_OFFSET_POP(3, 0),	//33
	REF_PUSH(),					//34
	REF_ADD_PUSH(),				//35
	TAN(1, 1),					//36
	SIN(1, 1),					//37
	COS(1, 1),					//38
	ATAN(1, 1),					//39
	ASIN(1, 1),					//40
	ACOS(1, 1),					//41
	ATAN2(2, 1),				//42 Not sure about the order of parameters (y,x or x,y?)
	SQRT(1, 1);					//43
	
	/**This field maps the tuple {opcode, flags, datatype} to the respective mnemonic.
	 * Access it as keywords[opcode][flags][dataType]
	 */
	static String[][][] keywords = new String[][][] {
/* 0*/	{{"END"}},
/* 1*/	{null, {null, "JZ"}, {null, "JZ"}},
/* 2*/	{null, {null, "PUSHI", "PUSHF", "PUSHC", "PUSHO", null, "PUSHB", "PUSHV"}, {null, null, null, null, null, null, null, "PUSHV"}},
/* 3*/	{null, {null, "POPI", "POPF", null, "POPO"}, {null, null, "POPF"}},
/* 4*/	{null, {null, null, "ADDF", "ADDC"}},
/* 5*/	{null, {"SYS", null, "SYS2"}},
/* 6*/	{null, {null, null, "SUBF", "SUBC"}},
/* 7*/	{null, {null, null, "NEG"}},
/* 8*/	{null, {null, null, "MUL"}},
/* 9*/	{null, {null, null, "DIV"}},
/*10*/	{null, {null, null, "MOD"}},
/*11*/	{null, {null, "NOT"}},
/*12*/	{null, {null, "AND"}},
/*13*/	{null, {null, "OR"}},
/*14*/	{null, {null, null, "EQ"}},
/*15*/	{null, {null, null, "NEQ"}},
/*16*/	{null, {null, null, "GEQ"}},
/*17*/	{null, {null, null, "LEQ"}},
/*18*/	{null, {null, null, "GT"}},
/*19*/	{null, {null, null, "LT"}},
/*20*/	{null, {null, "JMP"}, {null, "JMP"}},
/*21*/	{null, {null, null, "SLEEPF"}},
/*22*/	{null, {null, "EXCEPT"}},
/*23*/	{null, {null, "CASTI", "CASTF", "CASTC", "CASTO", null, "CASTB"}},
/*24*/	{null, {null, "CALL"}, {null, "START"}},
/*25*/	{null, {null, "ENDEXCEPT"}, {null, "FREE"}},
/*26*/	{null, {null, "RETEXCEPT"}},
/*27*/	{null, {null, "ITEREXCEPT"}},
/*28*/	{null, {null, "BRKEXCEPT"}},
/*29*/	{null, {null, "SWAP"}},
/*30*/	{{"DUP"}},
/*31*/	{null, null, {null, null, "LINE"}},
/*32*/	{null, null, {null, null, null, null, null, null, null, "REF_AND_OFFSET_PUSH"}},
/*33*/	{null, null, {null, null, "REF_AND_OFFSET_POP"}},
/*34*/	{null, {null, null, null, null, null, null, null, "REF_PUSH"}, {null, null, null, null, null, null, null, "REF_PUSH2"}},
/*35*/	{null, {null, null, "REF_ADD_PUSH"}},
/*36*/	{{"TAN"}},
/*37*/	{{"SIN"}},
/*38*/	{{"COS"}},
/*39*/	{{"ATAN"}},
/*40*/	{{"ASIN"}},
/*41*/	{{"ACOS"}},
/*42*/	{{"ATAN2"}},
/*43*/	{{"SQRT"}}
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
		if (opcode < 0 || opcode >= keywords.length) {
			throw new IllegalArgumentException("Invalid instruction code "+opcode);
		}
		String[][] t = keywords[opcode];
		if (t == null || flags < 0 || flags >= t.length || t[flags] == null) {
			throw new IllegalArgumentException("Invalid flags "+flags+" for opcode "+opcode);
		}
		String[] t2 = t[flags];
		if (dataType < 0 || dataType >= t2.length || t2[dataType] == null) {
			throw new IllegalArgumentException("Invalid datatype "+dataType+" for opcode "+opcode+" and flags "+flags);
		}
		return t2[dataType];
	}
}
