package it.ld.bw.chl.exceptions;

public class InvalidScriptIdException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private final int code;
	
	public InvalidScriptIdException(int code) {
		super("Script " + code + " doesn't exist");
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
}
