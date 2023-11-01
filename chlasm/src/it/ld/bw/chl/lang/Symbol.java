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

class Symbol {
	public enum TerminalType {
		EOF, EOL, KEYWORD, IDENTIFIER, NUMBER, STRING
	}
	
	public String keyword;
	public boolean terminal;
	public TerminalType terminalType;
	public boolean optional;
	public boolean repeat;
	public Symbol[] alternatives;
	public Symbol[] expression;
	public boolean implicit;
	public boolean root;
	
	public Symbol(String keyword, TerminalType terminalType, boolean implicit, boolean root) {
		this.keyword = keyword;
		this.terminal = terminalType != null;
		this.terminalType = terminalType;
		this.implicit = implicit;
		this.root = root;
	}
	
	public boolean canRepresent(Symbol symbol) {
		for (Symbol alt : alternatives) {
			if (alt == symbol) return true;
		}
		return false;
	}
	
	public boolean canRepresent(Symbol[] symbols) {
		for (Symbol symbol : symbols) {
			if (canRepresent(symbol)) return true;
		}
		return false;
	}
	
	public boolean isOneOf(Symbol[] symbols) {
		for (Symbol symbol : symbols) {
			if (this == symbol) return true;
		}
		return false;
	}
	
	public boolean isOneOf(Iterable<Symbol> symbols) {
		for (Symbol symbol : symbols) {
			if (this == symbol) return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return keyword.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Symbol)) return false;
		Symbol other = (Symbol) obj;
		return keyword.equals(other.keyword);
	}
	
	@Override
	public String toString() {
		String r;
		if (alternatives != null) {
			r = "{\r\n\t" + join("\r\n\t", alternatives) + "\r\n}";
		} else if (expression != null) {
			r = join(" ", expression);
		} else {
			r = keyword;
		}
		if (repeat) {
			r = "{" + r + "}";
		} else if (optional) {
			r = "[" + r + "]";
		}
		if (!terminal) {
			r = keyword + ": " + r;
		}
		return r;
	}
	
	private static String join(String delimiter, Symbol[] symbols) {
		if (symbols.length == 0) return "";
		String r = symbols[0].keyword;
		for (int i = 1; i < symbols.length; i++) {
			r += delimiter + symbols[i].keyword;
		}
		return r;
	}
}