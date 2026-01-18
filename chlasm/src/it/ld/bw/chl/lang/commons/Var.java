package it.ld.bw.chl.lang.commons;

import it.ld.bw.chl.model.DataType;

public class Var {
	public final String name;
	public final int id;
	public DataType type;
	public String typename;
	public boolean isFinal;
	public final boolean varargs;
	
	public Var(String name, int id, DataType type, String typename, boolean isFinal, boolean varargs) {
		this.name = name;
		this.id = id;
		this.type = type;
		this.typename = typename;
		this.isFinal = isFinal;
		this.varargs = false;
	}
	
	public Var(String name, int id, DataType type, boolean varargs) {
		this.name = name;
		this.id = id;
		this.type = type;
		this.typename = null;
		this.varargs = varargs;
	}
	
	@Override
	public String toString() {
		String s0 = typename != null ? typename : (type != null ? type.keyword : "variant");
		String s1 = varargs ? "..." : " ";
		return s0 + s1 + name;
	}
}