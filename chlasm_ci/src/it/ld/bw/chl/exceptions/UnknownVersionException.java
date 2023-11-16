package it.ld.bw.chl.exceptions;

public class UnknownVersionException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	private final int code;
	
	public UnknownVersionException(int code) {
		super("Unknown version code: " + code);
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
}
