package it.ld.bw.chl.model;

final class OPCodeAttr {
	public static final int ARG = 1;
	public static final int IP = 2 | ARG;
	public static final int SCRIPT = 4 | ARG;
	public static final int JUMP = 8 | ARG | IP;
	public static final int FINT = 16;
	public static final int VSTACK = 32;
	
	private OPCodeAttr() {}
}
