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

public enum DataType {
	NONE("", "void"),		//0
	INT("I", "int"),		//1
	FLOAT("F", "float"),	//2
	COORDS("C", "vec3"),	//3
	OBJECT("O", "Object"),	//4
	UNK5("5", "void"),		//5
	BOOLEAN("B", "bool"),	//6
	VAR("V", "void");		//7 since CI. This means that the parameter is the variable ID coded as float
	
	public final String modifierChar;
	public final String keyword;
	
	DataType(String modifierChar, String keyword) {
		this.modifierChar = modifierChar;
		this.keyword = keyword;
	}
	
	public static DataType fromModifier(String m) {
		if ("".equals(m)) return NONE;
		if ("I".equals(m)) return INT;
		if ("F".equals(m)) return FLOAT;
		if ("C".equals(m)) return COORDS;
		if ("O".equals(m)) return OBJECT;
		if ("5".equals(m)) return UNK5;
		if ("B".equals(m)) return BOOLEAN;
		if ("V".equals(m)) return VAR;
		throw new IllegalArgumentException("Unknown modifier: " + m);
	}
}
