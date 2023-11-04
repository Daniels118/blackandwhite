package it.ld.bw.chl.exceptions;

public class CompileException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private final String script;
	private final int instructionAddress;
	
	public CompileException(String msg) {
		super(msg);
		this.script = null;
		this.instructionAddress = 0;
	}
	
	public CompileException(Exception cause) {
		super(cause.getMessage(), cause);
		this.script = null;
		this.instructionAddress = 0;
	}
	
	public CompileException(String script, int instructionAddress, Exception cause) {
		super(cause.getMessage() + " at instruction " + instructionAddress + " in script " + script, cause);
		this.script = script;
		this.instructionAddress = instructionAddress;
	}
	
	public String getScript() {
		return script;
	}
	
	public int getInstructionAddress() {
		return instructionAddress;
	}
}
