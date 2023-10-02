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

import it.ld.bw.chl.exceptions.InvalidScriptTypeException;

public enum ScriptType {
	SCRIPT(1, "script"),
	HELP(2, "help script"),
	CHALLENGE_HELP(4, "challenge help script"),
	TEMPLE_HELP(8, "temple help script"),
	TEMPLE_SPECIAL(16, "temple special script"),
	MULTIPLAYER_HELP(64, "multiplayer help script");
	
	public final int code;
	public final String keyword;
	
	ScriptType(int code, String keyword) {
		this.code = code;
		this.keyword = keyword;
	}
	
	public static ScriptType fromCode(int code) throws InvalidScriptTypeException {
		for (ScriptType t : values()) {
			if (t.code == code) return t;
		}
		throw new InvalidScriptTypeException(code);
	}
	
	public static ScriptType fromKeyword(String keyword) {
		for (ScriptType t : values()) {
			if (t.keyword.equals(keyword)) return t;
		}
		throw new IllegalArgumentException("Invalid script type '" + keyword + "'");
	}
}
