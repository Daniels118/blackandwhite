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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import it.ld.bw.chl.exceptions.InvalidScriptIdException;
import it.ld.bw.chl.exceptions.ScriptNotFoundException;

public class Scripts extends StructArray<Script> {
	private final CHLFile chl;
	
	private boolean scriptsFinalized = false;
	private Map<Integer, Script> entrypointScripts;
	
	public Scripts(CHLFile chl) {
		this.chl = chl;
	}
	
	public void finalizeScripts() {
		if (!scriptsFinalized) {
			int[] entrypoints = new int[items.size()];
			int i = 0;
			for (Script script : items) {
				entrypoints[i++] = script.getInstructionAddress();
			}
			Arrays.sort(entrypoints);
			//
			for (i = 0; i < entrypoints.length - 1; i++) {
				int entrypoint = entrypoints[i];
				int nextEntrypoint = entrypoints[i + 1];
				Script script = getScriptFromEntrypoint(entrypoint);
				script.setLastInstructionAddress(nextEntrypoint - 1);
			}
			Script script = getScriptFromEntrypoint(entrypoints[entrypoints.length - 1]);
			script.setLastInstructionAddress(chl.getCode().getItems().size() - 1);
		}
	}
	
	public Script getScriptFromInstruction(int instruction) {
		finalizeScripts();
		for (Script script : items) {
			if (instruction >= script.getInstructionAddress() && instruction <= script.getLastInstructionAddress()) {
				return script;
			}
		}
		return null;
	}
	
	public Script getScriptFromEntrypoint(int ip) {
		if (entrypointScripts == null) {
			entrypointScripts = new HashMap<>();
			for (Script script : items) {
				entrypointScripts.put(script.getInstructionAddress(), script);
			}
		}
		return entrypointScripts.get(ip);
	}
	
	@Override
	public Class<Script> getItemClass() {
		return Script.class;
	}
	
	public Script getScript(int scriptID) throws InvalidScriptIdException {
		for (Script script : items) {
			if (script.getScriptID() == scriptID) return script;
		}
		throw new InvalidScriptIdException(scriptID);
	}
	
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
