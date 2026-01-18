/* Copyright (c) 2023-2026 Daniele Lombardi / Daniels118
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import it.ld.bw.chl.exceptions.InvalidScriptIdException;
import it.ld.bw.chl.exceptions.ScriptNotFoundException;

public class Scripts extends StructArray<Script> {
	private final CHLFile chl;
	
	private boolean scriptsFinalized = false;
	private Map<Integer, Set<Script>> entrypointsScripts;
	
	public Scripts(CHLFile chl) {
		this.chl = chl;
	}
	
	/**Compute additional information about scripts. No modification should be made to the scripts after that this
	 * method has been called.
	 */
	public void finalizeScripts() {
		if (!scriptsFinalized) {
			ArrayList<Instruction> srcInstructions = chl.getCode().getItems();
			for (Script script : items) {
				for (int i = script.getInstructionAddress(); i <= srcInstructions.size(); i++) {
					Instruction instr = srcInstructions.get(i);
					if (instr.opcode == OPCode.END) {
						script.setLastInstructionAddress(i);
						break;
					}
				}
			}
		}
	}
	
	/**Returns the first script which includes the given instruction address, or null if no script contains the address.
	 * @param instruction
	 * @return
	 */
	public Script getScriptFromInstruction(int instruction) {
		finalizeScripts();
		for (Script script : items) {
			if (instruction >= script.getInstructionAddress() && instruction <= script.getLastInstructionAddress()) {
				return script;
			}
		}
		return null;
	}
	
	/**Returns all the scripts which have the given entry point address.
	 * @param ip
	 * @return
	 */
	public Set<Script> getScriptsFromEntrypoint(int ip) {
		if (entrypointsScripts == null) {
			entrypointsScripts = new HashMap<>();
			for (Script script : items) {
				Set<Script> entrypointScripts = entrypointsScripts.get(script.getInstructionAddress());
				if (entrypointScripts == null) {
					entrypointScripts = new HashSet<>();
					entrypointsScripts.put(script.getInstructionAddress(), entrypointScripts);
				}
				entrypointScripts.add(script);
			}
		}
		return entrypointsScripts.get(ip);
	}
	
	@Override
	public Class<Script> getItemClass() {
		return Script.class;
	}
	
	@Override
	public Script createItem() {
		return new Script(chl);
	}
	
	/**Returns the first script with the given ID.
	 * @param scriptID
	 * @return
	 * @throws InvalidScriptIdException
	 */
	public Script getScript(int scriptID) throws InvalidScriptIdException {
		for (Script script : items) {
			if (script.getScriptID() == scriptID) return script;
		}
		throw new InvalidScriptIdException(scriptID);
	}
	
	/**Returns the first script with the given name.
	 * @param scriptName
	 * @return
	 * @throws ScriptNotFoundException
	 */
	public Script getScript(String scriptName) throws ScriptNotFoundException {
		for (Script script : items) {
			if (scriptName.equals(script.getName())) return script;
		}
		throw new ScriptNotFoundException(scriptName);
	}
	
	@Override
	public String toString() {
		StringBuffer s = new StringBuffer(items.size() * 22);
		for (Script script : items) {
			s.append(script.toString() + "\r\n");
		}
		return s.toString();
	}
}
