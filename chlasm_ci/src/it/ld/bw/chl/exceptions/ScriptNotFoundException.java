package it.ld.bw.chl.exceptions;

public class ScriptNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private final String name;
	
	public ScriptNotFoundException(String name) {
		super("Script " + name + " not found");
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
