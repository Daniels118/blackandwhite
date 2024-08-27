package it.ld.bw.chl.lang;

import it.ld.bw.chl.model.DataType;

public class Var {
	public final String name;
	public final int id;
	public final DataType type;
	public final boolean varargs;
	
	public Var(String name, int id, DataType type, boolean varargs) {
		this.name = name;
		this.id = id;
		this.type = type;
		this.varargs = varargs;
	}
	
	@Override
	public String toString() {
		String s0 = type != null ? type.keyword : "variant";
		String s1 = varargs ? "..." : " ";
		return s0 + s1 + name;
	}
}