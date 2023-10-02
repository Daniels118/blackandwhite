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

public class Code extends StructArray<Instruction> {
	@Override
	public Class<Instruction> getItemClass() {
		return Instruction.class;
	}
	
	@Override
	public int getLength() {
		return 4 + items.size() * Instruction.LENGTH;
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
