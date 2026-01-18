package it.ld.bw.chl.lang.java;

/**
 * This class holds a reference to a block of code which generates a value.
 */
public class ParameterCode {
	public final int ip;
	public final int end;
	public final String typename;
	
	public ParameterCode(int ip, int end, String typename) {
		this.ip = ip;
		this.end = end;
		this.typename = typename;
	}
	
	@Override
	public String toString() {
		return this.typename + "[" + this.ip+ ", " + this.end + "]";
	}
}
