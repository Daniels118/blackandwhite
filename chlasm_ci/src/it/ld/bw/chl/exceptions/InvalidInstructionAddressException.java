package it.ld.bw.chl.exceptions;

public class InvalidInstructionAddressException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private final int address;
	
	public InvalidInstructionAddressException(int address) {
		super(address + " is not a valid instruction address");
		this.address = address;
	}
	
	public int getAddress() {
		return address;
	}
}
