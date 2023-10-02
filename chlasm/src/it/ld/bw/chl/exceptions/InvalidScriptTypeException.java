package it.ld.bw.chl.exceptions;

public class InvalidScriptTypeException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private final int code;
	
	public InvalidScriptTypeException(int code) {
		super("Invalid script type code " + code);
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
}
