package it.ld.bw.chl.exceptions;

public class InvalidAccessException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private final int code;
	
	public InvalidAccessException(int code) {
		super("Invalid access flag: " + code);
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
}
