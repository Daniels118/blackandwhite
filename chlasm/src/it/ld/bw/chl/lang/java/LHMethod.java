package it.ld.bw.chl.lang.java;

import java.lang.reflect.Parameter;

/**
 * A representation of a Java API method which maps to a game function.
 */
public class LHMethod {
	public final boolean isStatic;
	public final String returnType;
	public final String name;
	public final Parameter[] parameters;
	public final FunctionMapping mapping;
	public final boolean isVarArgs;
	
	private String str = null;
	
	public LHMethod(boolean isStatic, String returnType, String name, Parameter[] parameters, FunctionMapping mapping) {
		this.isStatic = isStatic;
		this.returnType = returnType;
		this.name = name;
		this.parameters = parameters;
		this.mapping = mapping;
		boolean hasVarArgs = false;
		for (Parameter parameter : parameters) {
			if (parameter.isVarArgs()) {
				hasVarArgs = true;
				break;
			}
		}
		this.isVarArgs = hasVarArgs;
	}
	
	@Override
	public String toString() {
		if (str == null) {
			String args = "";
			if (parameters.length > 0) {
				args = parameters[0].toString();
				for (int i = 1; i < parameters.length; i++) {
					args += ", " + parameters[i];
				}
			}
			str = (isStatic ? "static " : "") + returnType + " " + name + "(" + args + ") -> " + mapping;
		}
		return str;
	}
}