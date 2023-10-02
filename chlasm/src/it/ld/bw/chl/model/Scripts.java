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

import it.ld.bw.chl.exceptions.InvalidScriptIdException;

public class Scripts extends StructArray<Script> {
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
	
	@Override
	public String toString() {
		StringBuffer s = new StringBuffer(items.size() * 22);
		for (Script script : items) {
			s.append(script.toString() + "\r\n");
		}
		return s.toString();
	}
}
