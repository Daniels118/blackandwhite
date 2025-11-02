package it.ld.bw.chl.lang;

import java.io.File;
import java.io.IOException;
import java.util.List;

import it.ld.bw.chl.exceptions.ParseError;
import it.ld.bw.chl.exceptions.ParseException;
import it.ld.bw.chl.model.CHLFile;

public interface Compiler {
	/**Finalize the CHL file. No more files can be parsed after finalization.
	 * @throws ParseException
	 * @throws ParseError
	 */
	public CHLFile seal() throws ParseException, ParseError;
	public CHLFile compile(Project project) throws ParseException, ParseError, IOException, IllegalStateException;
	public CHLFile compile(List<File> files) throws ParseException, ParseError, IOException, IllegalStateException;
	public void parse(File file) throws ParseException, ParseError, IOException, IllegalStateException;
}
