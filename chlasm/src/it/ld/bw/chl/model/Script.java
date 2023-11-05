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

import java.util.LinkedList;
import java.util.List;

import it.ld.bw.chl.exceptions.InvalidVariableIdException;
import it.ld.utils.EndianDataInputStream;
import it.ld.utils.EndianDataOutputStream;

public class Script extends Section {
	private String name;
	private String sourceFilename;
	private ScriptType scriptType;
	/**The highest index of global variables used by this script. Values greater than this are mapped to local parameters/variables*/
	private int varOffset;
	/**Parameters + local variables*/
	private List<String> variables = new LinkedList<String>();
	/**Index of the first instruction in the instructions array*/
	private int instructionAddress;
	/**How many local variables are parameters*/
	private int parameterCount;
	private int scriptID;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getSourceFilename() {
		return sourceFilename;
	}
	
	public void setSourceFilename(String sourceFilename) {
		this.sourceFilename = sourceFilename;
	}
	
	public ScriptType getScriptType() {
		return scriptType;
	}
	
	public void setScriptType(ScriptType scriptType) {
		this.scriptType = scriptType;
	}
	
	public int getVarOffset() {
		return varOffset;
	}
	
	public void setVarOffset(int varOffset) {
		this.varOffset = varOffset;
	}
	
	public List<String> getVariables() {
		return variables;
	}
	
	public void setVariables(List<String> variables) {
		this.variables = variables;
	}
	
	public int getInstructionAddress() {
		return instructionAddress;
	}
	
	public void setInstructionAddress(int instructionAddress) {
		this.instructionAddress = instructionAddress;
	}
	
	public int getParameterCount() {
		return parameterCount;
	}
	
	public void setParameterCount(int parameterCount) {
		this.parameterCount = parameterCount;
	}
	
	public int getScriptID() {
		return scriptID;
	}
	
	public void setScriptID(int scriptID) {
		this.scriptID = scriptID;
	}
	
	@Override
	public int getLength() {
		return 	  name.length() + 1
				+ sourceFilename.length() + 1
				+ 4 //scriptType
				+ 4 //varOffset
				+ getZStringArraySize(variables)
				+ 4 //instructionAddress
				+ 4 //parameterCount
				+ 4;//scriptID
	}
	
	@Override
	public void read(EndianDataInputStream str) throws Exception {
		name = readZString(str);
		sourceFilename = readZString(str);
		scriptType = ScriptType.fromCode(str.readInt());
		varOffset = str.readInt();
		variables = readZStringArray(str);
		instructionAddress = str.readInt();
		parameterCount = str.readInt();
		scriptID = str.readInt();
	}
	
	@Override
	public void write(EndianDataOutputStream str) throws Exception {
		writeZString(str, name);
		writeZString(str, sourceFilename);
		str.writeInt(scriptType.code);
		str.writeInt(varOffset);
		writeZStringArray(str, variables);
		str.writeInt(instructionAddress);
		str.writeInt(parameterCount);
		str.writeInt(scriptID);
	}
	
	public boolean isGlobalVar(int varId) {
		return varId >= 1 && varId <= varOffset;
	}
	
	public boolean isLocalVar(int varId) {
		return varId > varOffset;
	}
	
	public String getLocalVar(int varId) throws InvalidVariableIdException {
		if (!isLocalVar(varId)) throw new InvalidVariableIdException(varId);
		int index = varId - varOffset - 1;
		if (index < 0 || index >= variables.size()) {
			throw new InvalidVariableIdException(varId);
		}
		return variables.get(index);
	}
	
	public String getGlobalVar(CHLFile chl, int varId) {
		if (!isGlobalVar(varId)) throw new InvalidVariableIdException(varId);
		return chl.getGlobalVariables().getNames().get(varId - 1);
	}
	
	public String getVar(CHLFile chl, int varId) {
		if (isLocalVar(varId)) {
			return getLocalVar(varId);
		} else {
			return getGlobalVar(chl, varId);
		}
	}
	
	/**Returns the signature of this script in the following format:
	 * script_type script_name(parameter1, parameter2...)
	 * If there are no parameters, the parentheses are omitted.
	 * @return
	 */
	public String getSignature() {
		String res = scriptType.keyword + " " + name;
		if (parameterCount > 0) {
			String[] argNames = new String[parameterCount];
			for (int i = 0; i < parameterCount; i++) {
				argNames[i] = variables.get(i);
			}
			res += "(" + String.join(", ", argNames) + ")";
		}
		return res;
	}
	
	@Override
	public String toString() {
		String addr = String.format("0x%1$08X", instructionAddress);
		return "script[" + scriptID + "]: " + getSignature() + " at " + addr + " in " + sourceFilename;
	}
}
