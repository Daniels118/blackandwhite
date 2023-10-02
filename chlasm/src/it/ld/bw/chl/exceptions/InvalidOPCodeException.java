package it.ld.bw.chl.exceptions;

public class InvalidOPCodeException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private final int code;
	
	public InvalidOPCodeException(int code) {
		super("Invalid opcode: " + code);
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
}
