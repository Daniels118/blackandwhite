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

import java.util.ArrayList;
import java.util.List;

import it.ld.bw.chl.exceptions.InvalidAutorunScriptException;
import it.ld.bw.chl.exceptions.InvalidScriptIdException;
import it.ld.utils.EndianDataInputStream;
import it.ld.utils.EndianDataOutputStream;

public class AutoStartScripts extends Section {
	private List<Integer> scripts = new ArrayList<Integer>();
	
	public List<Integer> getScripts() {
		return scripts;
	}
	
	public void setScripts(List<Integer> scripts) {
		this.scripts = scripts;
	}
	
	@Override
	public int getLength() {
		return 4 + scripts.size() * 4;
	}
	
	@Override
	public void read(EndianDataInputStream str) throws Exception {
		int count = str.readInt();
		scripts = new ArrayList<Integer>(count);
		for (int i = 0; i < count; i++) {
			int id = str.readInt();
			scripts.add(id);
		}
	}

	@Override
	public void write(EndianDataOutputStream str) throws Exception {
		str.writeInt(scripts.size());
		for (Integer id : scripts) {
			str.writeInt(id);
		}
	}
	
	@Override
	public String toString() {
		return scripts.toString();
	}
	
	public void validate(CHLFile chl) throws InvalidScriptIdException, InvalidAutorunScriptException {
		for (int scriptID : scripts) {
			Script script = chl.getScriptsSection().getScript(scriptID);
			if (script == null) {
				throw new InvalidScriptIdException(scriptID);
			} else if (script.getParameterCount() != 0) {
				String msg = "Script " + script.getName() + " expects some arguments, cannot be used as autorun script";
				throw new InvalidAutorunScriptException(msg, script);
			}
		}
	}
}
