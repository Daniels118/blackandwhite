package it.ld.bw.chl.exceptions;

public class InvalidDataTypeException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private final int code;
	
	public InvalidDataTypeException(int code) {
		super("Invalid data type code: " + code);
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
}
