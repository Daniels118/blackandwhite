package it.ld.bw.chl.exceptions;

public class InvalidVariableIdException extends IndexOutOfBoundsException {
	private static final long serialVersionUID = 1L;
	
	private final int id;
	
	public InvalidVariableIdException(int id) {
		super(id + " is not a valid variable index");
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
}
