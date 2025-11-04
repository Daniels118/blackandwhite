package it.ld.bw.chl.exceptions;

import java.io.File;

public class LinkError extends Exception {
	private static final long serialVersionUID = 1L;
	
	private final File file;
	
	public LinkError(String msg, File file) {
		super(msg + " in " + file.getName());
		this.file = file;
	}
	
	public LinkError(Exception e, File file) {
		super(e.getMessage() + " in " + file.getName(), e);
		this.file = file;
	}
	
	public File getFile() {
		return file;
	}
}
