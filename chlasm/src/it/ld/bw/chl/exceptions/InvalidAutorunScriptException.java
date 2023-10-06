package it.ld.bw.chl.exceptions;

import it.ld.bw.chl.model.Script;

public class InvalidAutorunScriptException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private final Script script;
	
	public InvalidAutorunScriptException(String msg, Script script) {
		super(msg);
		this.script = script;
	}
	
	public Script getScript() {
		return script;
	}
}
