package it.ld.bw.chl.exceptions;

import java.io.File;

public class ParseError extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	private final File file;
	private final int lineno;
	private final int col;
	
	public ParseError(String msg, int lineno) {
		this(msg, null, lineno, 1);
	}
	
	public ParseError(String msg, int lineno, int col) {
		this(msg, null, lineno, col);
	}
	
	public ParseError(String msg, File file, int lineno) {
		this(msg, file, lineno, 1);
	}
	
	public ParseError(String msg, File file, int lineno, int col) {
		super(makeMsg(msg, file, lineno, col));
		this.file = file;
		this.lineno = lineno;
		this.col = col;
	}
	
	public ParseError(Exception parent, File file, int lineno) {
		this(parent, file, lineno, 1);
	}
	
	public ParseError(Exception parent, File file, int lineno, int col) {
		super(makeMsg(parent.getMessage(), file, lineno, col), parent);
		this.file = file;
		this.lineno = lineno;
		this.col = col;
	}
	
	private static String makeMsg(String msg, File file, int lineno, int col) {
		if (file != null) {
			return msg + " at " + file.getName() + ":" + lineno + ":" + col;
		} else {
			return msg + " at line " + lineno + ", column " + col;
		}
	}
	
	public File getSourceFile() {
		return file;
	}
	
	public int getLineno() {
		return lineno;
	}
	
	public int getColumn() {
		return col;
	}
}
