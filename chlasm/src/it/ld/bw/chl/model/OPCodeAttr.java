package it.ld.bw.chl.model;

final class OPCodeAttr {
	/**
	 * The instruction expects one or more operands.
	 */
	public static final int ARG = 1;
	/**
	 * The instruction expects an instruction pointer.
	 */
	public static final int IP = 2 | ARG;
	/**
	 * The instruction expects a script name as a string pointer.
	 */
	public static final int SCRIPT = 4 | ARG;
	/**
	 * The instruction is a jump and it expects an instruction pointer.
	 */
	public static final int JUMP = 8 | ARG | IP;
	/**
	 * The operand must be treated as an int regardless of the instruction datatype.
	 */
	public static final int FINT = 16;
	/**
	 * The number of values read from the stack is not fixed.
	 */
	public static final int VSTACK = 32;
	
	private OPCodeAttr() {}
}
