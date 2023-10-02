package it.ld.bw.chl.exceptions;

import java.io.File;

public class ParseException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private final File file;
	private final int lineno;
	
	public ParseException(String msg, File file, int lineno) {
		super(msg + " at " + file.getName() + ":" + lineno);
		this.file = file;
		this.lineno = lineno;
	}
	
	public ParseException(Exception parent, File file, int lineno) {
		super(parent.getMessage() + " at " + file.getName() + ":" + lineno, parent);
		this.file = file;
		this.lineno = lineno;
	}
	
	public File getSourceFile() {
		return file;
	}
	
	public int getLineno() {
		return lineno;
	}
}
