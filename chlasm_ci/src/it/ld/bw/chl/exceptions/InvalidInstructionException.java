package it.ld.bw.chl.exceptions;

public class InvalidInstructionException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public InvalidInstructionException(String msg) {
		super(msg);
	}
}
