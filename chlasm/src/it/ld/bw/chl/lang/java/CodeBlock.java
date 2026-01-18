/* Copyright (c) 2025-2026 Daniele Lombardi / Daniels118
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
package it.ld.bw.chl.lang.java;

import java.util.ArrayList;
import java.util.List;

import it.ld.bw.chl.model.DataType;
import it.ld.bw.chl.model.Instruction;
import it.ld.bw.chl.model.OPCode;

/**
 * A list of instructions which can be relocated to another offset.
 */
public class CodeBlock extends ArrayList<Instruction> {
	private static final long serialVersionUID = 1L;
	
	private int startIp = 0;
	public String typename;
	
	public CodeBlock() {}
	
	public CodeBlock(int startIp) {
		this.startIp = startIp;
	}
	
	public boolean isImmediateValue() {
		if (this.size() != 1) {
			return false;
		}
		Instruction instr = this.get(0);
		return instr.opcode == OPCode.PUSH && !instr.isReference();
	}
	
	/**
	 * If this code block is made of a single "PUSHI immed" instruction, convert it to PUSHF.
	 */
	public boolean tryConvertToFloat() {
		if (this.isImmediateValue()) {
			Instruction instr = this.get(0);
			if (instr.dataType == DataType.INT) {
				instr.floatVal = instr.intVal;
				instr.dataType = DataType.FLOAT;
				instr.intVal = 0;
				this.typename = "float";
				return true;
			}
		}
		return false;
	}
	
	public int getStartAddress() {
		return this.startIp;
	}
	
	public void relocate(int newStartIp) {
		final int offset = newStartIp - this.startIp;
		for (Instruction instr : this) {
			if (instr.opcode.isIP) {
				instr.intVal += offset;
			}
		}
		this.startIp = newStartIp;
	}
	
	/**
	 * Append this code block to the end of the target list, updating the target address of any jump instruction.
	 */
	public void appendTo(List<Instruction> target) {
		this.relocate(target.size());
		target.addAll(this);
	}
}
