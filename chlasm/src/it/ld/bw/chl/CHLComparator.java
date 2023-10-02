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
import java.util.List;

import it.ld.bw.chl.model.AutoStartScripts;
import it.ld.bw.chl.model.CHLFile;
import it.ld.bw.chl.model.Code;
import it.ld.bw.chl.model.DataSection;
import it.ld.bw.chl.model.Instruction;
import it.ld.bw.chl.model.Script;
import it.ld.bw.chl.model.Scripts;
import it.ld.bw.chl.model.Section;

public class CHLComparator {
	private PrintStream out;
	
	public CHLComparator() {
		this(System.out);
	}
	
	public CHLComparator(PrintStream out) {
		this.out = out;
	}
	
	public boolean compare(CHLFile a, CHLFile b) {
		boolean res = true;
		//File version
		int v1 = a.getHeader().getVersion();
		int v2 = b.getHeader().getVersion();
		if (v1 != v2) {
			out.println("Version: "+v1+" -> "+v2);
			return false;
		}
		//Number of global variables
		List<String> g1 = a.getGlobalVariables().getNames();
		List<String> g2 = a.getGlobalVariables().getNames();
		if (g1.size() != g2.size()) {
			out.println("Number of globals: "+g1.size()+" -> "+g2.size());
			return false;
		}
		//Global variable names
		for (int i = 0; i < g1.size(); i++) {
			String n1 = g1.get(i);
			String n2 = g2.get(i);
			if (!n1.equals(n2)) {
				out.println("Globals["+i+"]: "+n1+" -> "+n2);
				return false;
			}
		}
		//Scripts offset
		Scripts sss1 = a.getScriptsSection();
		Scripts sss2 = a.getScriptsSection();
		out.println("Scripts section offset: "+getOffset(sss1)+" -> "+getOffset(sss2));
		if (sss1.getOffset() != sss2.getOffset()) return false;
		//Scripts count
		List<Script> ss1 = sss1.getItems();
		List<Script> ss2 = sss2.getItems();
		if (ss1.size() != ss2.size()) {
			out.println("Scripts count: "+ss1.size()+" -> "+ss2.size());
			return false;
		}
		//Scripts list
		for (int i = 0; i < ss1.size(); i++) {
			Script s1 = ss1.get(i);
			Script s2 = ss2.get(i);
			if (!s1.getName().equals(s2.getName())) {
				out.println("Scripts["+i+"].name: "+s1.getName()+" -> "+s2.getName());
				out.println(s1.getSignature());
				out.println(s2.getSignature());
				return false;
			}
			if (s1.getScriptID() != s2.getScriptID()) {
				out.println("Scripts["+i+"].scriptID: "+s1.getScriptID()+" -> "+s2.getScriptID());
				out.println(s1.getSignature());
				out.println(s2.getSignature());
				return false;
			}
			if (s1.getParameterCount() != s2.getParameterCount()) {
				out.println("Scripts["+i+"].parameterCount: "+s1.getParameterCount()+" -> "+s2.getParameterCount());
				out.println(s1.getSignature());
				out.println(s2.getSignature());
				return false;
			}
			if (s1.getVarOffset() != s2.getVarOffset()) {
				out.println("Scripts["+i+"].varOffset: "+s1.getVarOffset()+" -> "+s2.getVarOffset());
				out.println(s1.getSignature());
				out.println(s2.getSignature());
				return false;
			}
			if (!s1.getVariables().equals(s2.getVariables())) {
				out.println("Scripts["+i+"].variables: "+s1.getVariables()+" -> "+s2.getVariables());
				out.println(s1.getSignature());
				out.println(s2.getSignature());
				return false;
			}
			if (s1.getInstructionAddress() != s2.getInstructionAddress()) {
				out.println("Scripts["+i+"].instructionAddress: "+s1.getInstructionAddress()+" -> "+s2.getInstructionAddress());
				out.println(s1.getSignature());
				out.println(s2.getSignature());
				return false;
			}
		}
		//Autostart scripts offset
		AutoStartScripts asss1 = a.getAutoStartScripts();
		AutoStartScripts asss2 = a.getAutoStartScripts();
		out.println("Autostart scripts offset: "+getOffset(asss1)+" -> "+getOffset(asss2));
		if (asss1.getOffset() != asss2.getOffset()) return false;
		//Autostart scripts count
		List<Integer> ass1 = asss1.getScripts();
		List<Integer> ass2 = asss2.getScripts();
		if (ass1.size() != ass2.size()) {
			out.println("Autostart scripts count: "+ass1.size()+" -> "+ass2.size());
			return false;
		}
		//Autostart scripts list
		for (int i = 0; i < ass1.size(); i++) {
			int as1 = ass1.get(i);
			int as2 = ass2.get(i);
			if (as1 != as2) {
				out.println("Autostart scripts["+i+"]: "+as1+" -> "+as2);
				return false;
			}
		}
		//Data offset
		DataSection d1 = a.getDataSection();
		DataSection d2 = b.getDataSection();
		out.println("Data offset: "+getOffset(d1)+" -> "+getOffset(d2));
		if (d1.getOffset() != d2.getOffset()) return false;
		//Data length
		byte[] r1 = d1.getData();
		byte[] r2 = d2.getData();
		if (r1.length != r2.length) {
			out.println("Data length: "+r1.length+" -> "+r2.length);
			res = false;
		}
		//Data
		for (int i = 0; i < d1.getData().length; i++) {
			byte b1 = r1[i];
			byte b2 = r2[i];
			if (b1 != b2) {
				String offset = String.format("0x%1$08X", d1.getOffset() + 4 + i);
				out.println(offset+" Data["+i+"]: "+b1+" -> "+b2);
				return false;
			}
		}
		//Code offset
		Code c1 = a.getCode();
		Code c2 = b.getCode();
		out.println("Code offset: "+getOffset(c1)+" -> "+getOffset(c2));
		if (c1.getOffset() != c2.getOffset()) return false;
		//Number of instructions
		List<Instruction> iss1 = c1.getItems();
		List<Instruction> iss2 = c2.getItems();
		if (iss1.size() != iss2.size()) {
			out.println("Number of instructions: "+iss1.size()+" -> "+iss2.size());
			return false;
		}
		//Instructions
		for (int i = 0; i < iss1.size(); i++) {
			Instruction i1 = iss1.get(i);
			Instruction i2 = iss2.get(i);
			if (i1.opcode != i2.opcode) {
				out.println(getOffset(a, b, i)+" Instructions["+i+"].opcode: "+i1.opcode+" -> "+i2.opcode);
				res = false;
			}
			if (i1.flags != i2.flags) {
				out.println(getOffset(a, b, i)+" Instructions["+i+"].flags: "+i1.flags+" -> "+i2.flags);
				res = false;
			}
			if (i1.dataType != i2.dataType) {
				out.println(getOffset(a, b, i)+" Instructions["+i+"].dataType: "+i1.dataType+" -> "+i2.dataType);
				res = false;
			}
			if (i1.intVal != i2.intVal) {
				out.println(getOffset(a, b, i)+" Instructions["+i+"].val: "+i1.intVal+" -> "+i2.intVal);
				res = false;
			} else if (i1.floatVal != i2.floatVal) {
				out.println(getOffset(a, b, i)+" Instructions["+i+"].val: "+i1.floatVal+" -> "+i2.floatVal);
				res = false;
			} else if (i1.boolVal != i2.boolVal) {
				out.println(getOffset(a, b, i)+" Instructions["+i+"].val: "+i1.boolVal+" -> "+i2.boolVal);
				res = false;
			}
		}
		//
		if (res) out.println("Files match!");
		return res;
	}
	
	private static String getOffset(Section section) {
		return String.format("0x%1$08X", section.getOffset());
	}
	
	private static String getOffset(CHLFile a, CHLFile b, int index) {
		final String fmt = "%1$s:%2$d 0x%3$08X, %4$s:%5$d 0x%6$08X";
		Instruction i1 = a.getCode().getItems().get(index);
		Instruction i2 = b.getCode().getItems().get(index);
		String f1 = "";
		for (Script script : a.getScriptsSection().getItems()) {
			if (script.getInstructionAddress() <= index) {
				f1 = script.getSourceFilename();
			} else {
				break;
			}
		}
		String f2 = "";
		for (Script script : b.getScriptsSection().getItems()) {
			if (script.getInstructionAddress() <= index) {
				f2 = script.getSourceFilename();
			} else {
				break;
			}
		}
		int offset1 = a.getDataSection().getOffset() + 4 + index * Instruction.LENGTH;
		int offset2 = b.getDataSection().getOffset() + 4 + index * Instruction.LENGTH;
		return String.format(fmt, f1, i1.lineNumber, offset1, f2, i2.lineNumber, offset2);
	}
}
