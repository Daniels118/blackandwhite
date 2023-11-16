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
package it.ld.bw.chl.lang;

public class Token {
	public enum ValueType {
		INT, FLOAT, BOOL, STRING
	}
	
	public int line;
	public int col;
	public TokenType type;
	public ValueType valueType;
	public String value;
	
	public Token(int line, int col, TokenType type) {
		this(line, col, type, null);
	}
	
	public Token(int line, int col, TokenType type, char value) {
		this(line, col, type, String.valueOf(value));
	}
	
	public Token(int line, int col, TokenType type, String value) {
		this.line = line;
		this.col = col;
		this.type = type;
		this.value = value;
	}
	
	public Token setValue(String value) {
		this.value = value;
		return this;
	}
	
	@Override
	public String toString() {
		return value;
	}
	
	public int intVal() {
		return Integer.parseInt(value);
	}
	
	public float floatVal() {
		return Float.parseFloat(value);
	}
	
	public String stringVal() {
		String s = value;
		s = s.substring(1, s.length() - 1);
		s = s.replace("\\\\", "\\");
		s = s.replace("\\\"", "\"");
		s = s.replace("\\r", "\r");
		s = s.replace("\\n", "\n");
		s = s.replace("\\t", "\t");
		return s;
	}
}
