package it.ld.bw.chl.model;

/* Although there is just one bit flag in the instructions, it gets different meaning
 * depending on the opcode. For this reason we define different constants with the same value,
 * so that they reflect the proper meaning making the code more readable.
 */

public final class OPCodeFlag {
	//Normal flags
	public static final int REF = 1;
	public static final int FORWARD = 1;
	
	//Alternate mnemonic flags
	public static final int ASYNC = 1;
	public static final int ZERO = 1;
	public static final int FREE = 1;
	
	private OPCodeFlag() {}
}
