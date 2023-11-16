package it.ld.bw.chl.exceptions;

public class UnsupportedVersionException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	private final int code;
	
	public UnsupportedVersionException(String name, int code) {
		super("CHL for "+name+" is not supported");
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
}
