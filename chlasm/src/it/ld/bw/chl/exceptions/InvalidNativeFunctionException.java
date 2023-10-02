package it.ld.bw.chl.exceptions;

public class InvalidNativeFunctionException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private final int code;
	
	public InvalidNativeFunctionException(int code) {
		super(code + " isn't a valid native function ID");
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
}
