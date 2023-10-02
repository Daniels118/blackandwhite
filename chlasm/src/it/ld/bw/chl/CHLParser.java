/* Copyright (c) 2023 Daniele Lombardi / Daniels118
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.ld.bw.chl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import it.ld.bw.chl.exceptions.ParseException;
import it.ld.bw.chl.lexer.CHLLexer;
import it.ld.bw.chl.lexer.Token;
import it.ld.bw.chl.model.CHLFile;
import it.ld.bw.chl.model.DataSection;
import it.ld.bw.chl.model.GlobalVariables;
import it.ld.bw.chl.model.Header;
import it.ld.bw.chl.model.Instruction;
import it.ld.bw.chl.model.Script;

public class CHLParser {
	private static final int INITIAL_BUFFER_SIZE = 16 * 1024;
	private static final int MAX_BUFFER_SIZE = 2 * 1024 * 1024;
	
	private int firstScriptID = 0;
	private PrintStream out;
	
	private CHLFile chl;
	private GlobalVariables globalVariables;
	private ByteBuffer dataBuffer;
	private List<Instruction> instructions;
	private List<Script> scripts;
	private List<Integer> autoStartScripts;
	private DataSection dataSection;
	
	public CHLParser() {
		this(System.out);
	}
	
	public CHLParser(PrintStream out) {
		this.out = out;
	}
	
	public int getFirstScriptID() {
		return firstScriptID;
	}
	
	public void setFirstScriptID(int firstScriptID) {
		this.firstScriptID = firstScriptID;
	}
	
	public CHLFile parse(List<File> files) throws IOException, ParseException {
		chl = new CHLFile();
		globalVariables = chl.getGlobalVariables();
		dataBuffer = ByteBuffer.allocate(INITIAL_BUFFER_SIZE);
		instructions = chl.getCode().getItems();
		scripts = chl.getScriptsSection().getItems();
		autoStartScripts = chl.getAutoStartScripts().getScripts();
		dataSection = chl.getDataSection();
		dataBuffer.order(ByteOrder.LITTLE_ENDIAN);
		chl.getHeader().setVersion(Header.BW1);
		globalVariables.setOffset(chl.getHeader().getLength());
		for (File file : files) {
			parse(file);
		}
		return chl;
	}
	
	private void parse(File file) throws FileNotFoundException, IOException, ParseException {
		CHLLexer tokenizer = new CHLLexer();
		SourceSection section = SourceSection.DEFAULT;
		List<Token> tokens = tokenizer.tokenize(file);
		for (Token token : tokens) {
			switch (section) {
				case DEFAULT:
					//token.type
					break;
				case SCRIPT_LOCAL:
					
					break;
				case SCRIPT_BODY:
					
					break;
			}
		}
		//TODO
	}
	
	
	private enum SourceSection {
		DEFAULT, SCRIPT_LOCAL, SCRIPT_BODY
	}
}
