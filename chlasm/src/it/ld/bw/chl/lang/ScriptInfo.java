package it.ld.bw.chl.lang;

import java.util.ArrayList;
import java.util.List;

import it.ld.bw.chl.model.DataType;

public class ScriptInfo {
	public boolean varargs;
	public final List<Var> vars = new ArrayList<>(16);
	
	public boolean checkArgc() {
		if (vars.size() < 2) return false;
		Var var = vars.get(vars.size() - 2);
		return var.type == DataType.INT && "argc".equals(var.name);
	}
	
	public int getVariadicPos() {
		return varargs ? (vars.size() - 2) : Integer.MAX_VALUE;
	}
	
	public DataType getVariadicType() {
		return vars.get(vars.size() - 1).type;
	}
}
