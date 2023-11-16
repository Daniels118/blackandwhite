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

import it.ld.utils.EndianDataInputStream;

public class Code extends StructArray<Instruction> {
	public static boolean traceEnabled = false;
	
	private final CHLFile chl;
	
	private Script script = null;
	
	public Code(CHLFile chl) {
		this.chl = chl;
	}

	@Override
	public Class<Instruction> getItemClass() {
		return Instruction.class;
	}
	
	@Override
	public int getLength() {
		return 4 + items.size() * Instruction.LENGTH;
	}
	
	@Override
	protected Instruction readItem(EndianDataInputStream str, int index) throws Exception {
		Instruction instr = super.readItem(str, index);
		//
		if (traceEnabled) {
			if (script == null
					|| index < script.getInstructionAddress()
					|| index > script.getLastInstructionAddress()) {
				if (script != null) {
					System.out.println();
				}
				script = chl.getScriptsSection().getScriptFromInstruction(index);
				System.out.println(script);
				for (String name : script.getVariablesWithoutParameters()) {
					System.out.println("local "+name);
				}
			}
			System.out.print(instr.toString(chl, script, null));
			if (instr.opcode == OPCode.SYS) {
				NativeFunction f = NativeFunction.fromCode(instr.intVal);
				System.out.print("\t//" + f.getInfoString());
			}
			System.out.println();
		}
		//
		return instr;
	}
	
	@Override
	public String toString() {
		int n = Math.min(items.size(), 1000);
		StringBuffer s = new StringBuffer(n * 22);
		int i = 0;
		for (Instruction instr : items) {
			String offset = String.format("0x%1$08X", i);
			s.append(offset + ": " + instr.toString() + "\r\n");
			if (--n <= 0) break;
			i++;
		}
		return s.toString();
	}
}
