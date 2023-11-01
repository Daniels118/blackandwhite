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
package it.ld.bw.chl.lang;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import it.ld.bw.chl.exceptions.ParseException;
import it.ld.bw.chl.model.CHLFile;
import it.ld.bw.chl.model.Header;
import it.ld.bw.chl.model.Instruction;
import it.ld.bw.chl.model.NativeFunction;
import it.ld.bw.chl.model.OPCodeFlag;
import it.ld.bw.chl.model.Script;
import it.ld.bw.chl.model.ScriptType;

import static it.ld.bw.chl.model.NativeFunction.*;

public class CHLCompiler {
	private static final int traceMaxStrLen = 50;
	
	private File file;
	private String sourceFilename;
	private LinkedList<SymbolInstance> symbols;
	private ListIterator<SymbolInstance> it;
	private int line;
	private int col;
	
	private PrintStream traceStream;
	private PrintStream outStream;
	
	private final CHLFile chl = new CHLFile();
	private final List<Instruction> instructions;
	private boolean sealed = false;
	private LinkedHashMap<String, Integer> strings = new LinkedHashMap<>();
	private int dataSize = 0;
	private Map<String, Integer> constants = new HashMap<>();
	private LinkedHashMap<String, Integer> localVars = new LinkedHashMap<>();
	private Map<String, Integer> localConst = new HashMap<>();
	private LinkedHashMap<String, Integer> globalVars = new LinkedHashMap<>();
	private LinkedHashMap<String, ScriptToResolve> autoruns = new LinkedHashMap<>();
	private List<ScriptToResolve> calls = new LinkedList<>();
	private Integer challengeId;
	private int varOffset;
	private int scriptId = 0;
	//private LinkedList<Instruction> jmpStack = new LinkedList<>();
	//private LinkedList<Instruction> exceptStack = new LinkedList<>();
	
	public CHLCompiler() {
		this(System.out);
	}
	
	public CHLCompiler(PrintStream outStream) {
		this.outStream = outStream;
		chl.getHeader().setVersion(Header.BW1);
		instructions = chl.getCode().getItems();
		/* This will help the disassembler when guessing string values, because it increases the pointer
		 * of the first string (low values are too common and would lead to a lot of bad guessing) */
		storeStringData("Compiled with CHL Compiler developed by Daniele Lombardi");
	}
	
	public void setTraceStream(PrintStream traceStream) {
		this.traceStream = traceStream;
	}
	
	public void setFirstScriptId(int id) throws IllegalStateException {
		if (!chl.getScriptsSection().getItems().isEmpty()) {
			throw new IllegalStateException("Some scripts have already been parsed");
		}
		scriptId = id;
	}
	
	/**Finalize the CHL file. No more files can be parsed after finalization.
	 * @throws ParseException
	 */
	public void seal() throws ParseException {
		if (!sealed) {
			outStream.println("building...");
			//Script map
			Map<String, Script> scriptMap = new HashMap<>();
			for (Script script : chl.getScriptsSection().getItems()) {
				scriptMap.put(script.getName(), script);
			}
			//Data section
			trace("building data section...", 0);
			final Charset ASCII = Charset.forName("US-ASCII");
			byte[] data = new byte[dataSize];
			for (Entry<String, Integer> e : strings.entrySet()) {
				String value = e.getKey();
				int strptr = e.getValue();
				byte[] bytes = value.getBytes(ASCII);
				System.arraycopy(bytes, 0, data, strptr, bytes.length);
				strptr += bytes.length;
				data[strptr] = 0;
			}
			chl.getDataSection().setData(data);
			//Global variables
			trace("building global variables section...", 0);
			List<String> chlVars = chl.getGlobalVariables().getNames();
			for (String name : globalVars.keySet()) {
				chlVars.add(name);
			}
			//call and start
			trace("resolving call and start instructions...", 0);
			for (ScriptToResolve call : calls) {
				Script script = scriptMap.get(call.name);
				if (script == null) {
					throw new ParseException("Script not found: "+call.name, call.file, call.line, 1);
				}
				call.instr.intVal = script.getScriptID();
			}
			//Auto start scripts
			trace("resolving autorun scripts...", 0);
			for (ScriptToResolve call : autoruns.values()) {
				Script script = scriptMap.get(call.name);
				if (script == null) {
					throw new ParseException("Script not found: "+call.name, call.file, call.line, 1);
				}
				if (script.getParameterCount() > 0) {
					throw new ParseException("Script with parameters not valid for autorun: "+call.name, call.file, call.line, 1);
				}
				chl.getAutoStartScripts().getScripts().add(script.getScriptID());
			}
			//
			sealed = true;
			outStream.println("done...");
		}
	}
	
	/**Be aware that this implicitly seals the CHL file. No more files can be parsed.
	 * @return
	 * @throws ParseException
	 */
	public CHLFile getCHLFile() throws ParseException {
		if (!sealed) {
			seal();
		}
		return chl;
	}
	
	private void trace(String s, int depth) {
		if (traceStream != null) {
			traceStream.print("\t".repeat(depth));
			traceStream.println(s);
		}
	}
	
	private void convertToNodes(List<Token> tokens) throws ParseException {
		TokenType prevType = TokenType.EOL;
		symbols = new LinkedList<>();
		for (int pos = 0; pos < tokens.size(); pos++) {
			Token token = tokens.get(pos);
			if (token.type.important) {
				if (token.type != TokenType.EOL || prevType != TokenType.EOL) {
					symbols.add(toSymbol(pos, token));
					prevType = token.type;
				}
			}
		}
		symbols.add(SymbolInstance.EOF);
	}
	
	public void loadHeader(File headerFile) throws FileNotFoundException, IOException, ParseException {
		CHeaderParser parser = new CHeaderParser();
		Map<String, Integer> hconst = parser.parse(headerFile);
		constants.putAll(hconst);
	}
	
	public CHLFile compile(List<File> files) throws IOException, ParseException {
		for (File file : files) {
			parse(file);
		}
		seal();
		return getCHLFile();
	}
	
	public void parse(File file) throws ParseException, IOException {
		try {
			this.file = file;
			sourceFilename = file.getName();
			outStream.println("compiling "+sourceFilename+"...");
			CHLLexer lexer = new CHLLexer();
			List<Token> tokens = lexer.tokenize(file);
			parse(tokens);
		} finally {
			file = null;
			sourceFilename = null;
		}
	}
	
	public void parse(List<Token> tokens) throws ParseException, IllegalStateException {
		if (sealed) {
			throw new IllegalStateException("Parser already sealed");
		}
		convertToNodes(tokens);
		challengeId = null;
		varOffset = 0;
		line = 0;
		col = 0;
		//
		it = symbols.listIterator();
		parseFile();
		trace("SUCCESS!", 0);
	}
	
	private SymbolInstance parseFile() throws ParseException {
		final int start = it.nextIndex();
		while (it.hasNext()) {
			SymbolInstance symbol = peek();
			if (symbol.is("challenge")) {
				parseChallenge();
			} else if (symbol.is("global")) {
				parseGlobal();
			} else if (symbol.is("run")) {
				parseAutorun();
			} else if (symbol.is("begin")) {
				parseScript();
			} else if (symbol.is("source")) {	// <- custom statement
				parseSource();
			} else {
				break;
			}
		}
		SymbolInstance symbol = next();
		if (symbol != SymbolInstance.EOF) {
			throw new ParseException("Unexpected token: "+symbol+". Expected: EOF", file, symbol.token.line, symbol.token.col);
		}
		SymbolInstance file = replace(start, "FILE");
		return file;
	}
	
	/**This is a custom statement that permits to compile from merged source files.
	 * @return
	 * @throws ParseException
	 */
	private SymbolInstance parseSource() throws ParseException {
		final int start = it.nextIndex();
		accept("source");
		SymbolInstance symbol = accept(TokenType.STRING);
		sourceFilename = symbol.token.stringVal();
		accept(TokenType.EOL);
		SymbolInstance newInst = replace(start, "source STRING EOL");
		outStream.println("Source filename set to: "+sourceFilename);
		return newInst;
	}
	
	private SymbolInstance parseChallenge() throws ParseException {
		final int start = it.nextIndex();
		SymbolInstance symbol = parse("challenge IDENTIFIER EOL");
		challengeId = getConstant(symbol);
		SymbolInstance newInst = replace(start, "challenge IDENTIFIER EOL");
		return newInst;
	}
	
	private SymbolInstance parseGlobal() throws ParseException {
		final int start = it.nextIndex();
		accept("global");
		SymbolInstance symbol = next();
		if (symbol.is("constant")) {
			symbol = accept(TokenType.IDENTIFIER);
			String name = symbol.token.value;
			accept("=");
			symbol = next();
			int val;
			if (symbol.token.type == TokenType.IDENTIFIER) {
				val = getConstant(symbol);
			} else if (symbol.token.type == TokenType.NUMBER) {
				val = symbol.token.intVal();
			} else {
				throw new ParseException("Expected: IDENTIFIER|NUMBER", file, symbol.token.line, symbol.token.col);
			}
			accept(TokenType.EOL);
			Integer oldVal = constants.put(name, val);
			if (oldVal != null && oldVal != val) {
				outStream.println("WARNING: redefinition of global constant: "+name+" at "+file+":"+symbol.token.line);
			}
			SymbolInstance newInst = replace(start, "GLOBAL_CONST_DECL");
			return newInst;
		} else {
			verify(symbol, TokenType.IDENTIFIER);
			String name = symbol.token.value;
			Integer varId = globalVars.get(name);
			if (varId == null) {
				varId = globalVars.size();
				globalVars.put(name, varId);
			}
			varOffset = Math.max(varOffset, varId);
			accept(TokenType.EOL);
			SymbolInstance newInst = replace(start, "GLOBAL_VAR_DECL");
			return newInst;
		}
	}
	
	private SymbolInstance parseAutorun() throws ParseException {
		final int start = it.nextIndex();
		SymbolInstance symbol = parse("run script IDENTIFIER EOL");
		String name = symbol.token.value;
		ScriptToResolve toResolve = new ScriptToResolve(file, line, null, name);
		if (autoruns.put(name, toResolve) != null) {
			throw new ParseException("Duplicate autorun definition: "+name, file, symbol.token.line, symbol.token.col);
		}
		SymbolInstance newInst = replace(start, "run script IDENTIFIER EOL");
		return newInst;
	}
	
	private SymbolInstance parseScript() throws ParseException {
		final int start = it.nextIndex();
		localVars.clear();
		localConst.clear();
		try {
			Script script = new Script();
			script.setScriptID(scriptId++);
			script.setSourceFilename(sourceFilename);
			script.setInstructionAddress(getIp());
			accept("begin");
			SymbolInstance scriptTypeSymbol = parseScriptType();
			ScriptType scriptType = ScriptType.fromKeyword(scriptTypeSymbol.toString());
			script.setScriptType(scriptType);
			SymbolInstance symbol = accept(TokenType.IDENTIFIER);
			String name = symbol.token.value;
			script.setName(name);
			int argc = 0;
			symbol = peek();
			if (symbol.is("(")) {
				argc = parseArguments();
			}
			accept(TokenType.EOL);
			script.setParameterCount(argc);
			//Start the exception handler and load parameter values from the stack
			Instruction except_lblExceptionHandler = except();
			Iterator<String> lvars = localVars.keySet().iterator();
			for (int i = 0; i < argc; i++) {
				String var = lvars.next();
				popf(var);
			}
			//
			symbol = peek();
			if (!symbol.is("start")) {
				parseLocals();
			}
			accept("start");
			accept(TokenType.EOL);
			free();
			script.setVarOffset(varOffset);
			script.getVariables().addAll(localVars.keySet());
			//STATEMENTS
			parseStatements();
			//EXCEPTIONS
			endexcept();
			Instruction jmp_lblEnd = jmp();
			except_lblExceptionHandler.intVal = getIp();
			symbol = peek();
			if (symbol.is("when") || symbol.is("until")) {
				parseExceptions();
			}
			iterexcept();
			int lblEnd = getIp();
			jmp_lblEnd.intVal = lblEnd;
			//
			try {
				parse("end script");
				end();
			} catch (ParseException e) {
				symbol = peek();
				throw new ParseException("Unrecognized statement", file, symbol.token.line, symbol.token.col);
			}
			symbol = accept(TokenType.IDENTIFIER);
			if (!symbol.token.value.equals(name)) {
				throw new ParseException("The script name at \"end script\" must match the one at \"begin script\"", file, symbol.token.line, symbol.token.col);
			}
			accept(TokenType.EOL);
			SymbolInstance newInst = replace(start, "SCRIPT");
			//
			chl.getScriptsSection().getItems().add(script);
			return newInst;
		} catch (ParseException e) {
			localVars.clear();
			localConst.clear();
			throw e;
		}
	}
	
	private SymbolInstance parseScriptType() throws ParseException {
		final int start = it.nextIndex();
		SymbolInstance symbol = next();
		if (symbol.is("help")) {
			accept("script");
			SymbolInstance newInst = replace(start, "help script");
			return newInst;
		} else if (symbol.is("challenge")) {
			parse("help script");
			SymbolInstance newInst = replace(start, "challenge help script");
			return newInst;
		} else if (symbol.is("temple")) {
			symbol = next();
			if (symbol.is("help")) {
				accept("script");
				SymbolInstance newInst = replace(start, "temple help script");
				return newInst;
			} else if (symbol.is("special")) {
				accept("script");
				SymbolInstance newInst = replace(start, "temple special script");
				return newInst;
			} else {
				throw new ParseException("Unexpected token: "+symbol, file, symbol.token.line, symbol.token.col);
			}
		} else if (symbol.is("multiplayer")) {
			parse("help script");
			SymbolInstance newInst = replace(start, "multiplayer help script");
			return newInst;
		} else if (symbol.is("script")) {
			SymbolInstance newInst = replace(start, "script");
			return newInst;
		} else {
			throw new ParseException("Unexpected token: "+symbol, file, symbol.token.line, symbol.token.col);
		}
	}
	
	private int parseArguments() throws ParseException {
		final int start = it.nextIndex();
		int argc = 0;
		accept("(");
		SymbolInstance symbol = peek();
		if (!symbol.is(")")) {
			symbol = accept(TokenType.IDENTIFIER);
			String name = symbol.token.value;
			argc++;
			addLocalVar(name);
			symbol = peek();
			while (!symbol.is(")")) {
				accept(",");
				symbol = accept(TokenType.IDENTIFIER);
				name = symbol.token.value;
				argc++;
				addLocalVar(name);
				symbol = peek();
			}
		}
		accept(")");
		replace(start, "(ARGS)");
		return argc;
	}
	
	private int parseParameters() throws ParseException {
		final int start = it.nextIndex();
		int argc = 0;
		accept("(");
		SymbolInstance symbol = peek();
		if (!symbol.is(")")) {
			parseExpression(true);
			argc++;
			symbol = peek();
			while (!symbol.is(")")) {
				accept(",");
				parseExpression(true);
				argc++;
				symbol = peek();
			}
		}
		accept(")");
		replace(start, "(PARAMETERS)");
		return argc;
	}
	
	private SymbolInstance parseLocals() throws ParseException {
		final int start = it.nextIndex();
		SymbolInstance symbol = peek();
		while (symbol.is(TokenType.IDENTIFIER) || symbol.is("constant")) {
			parseLocal();
			symbol = peek();
		}
		SymbolInstance newInst = replace(start, "{LOCAL_DECL}");
		return newInst;
	}
	
	private void addLocalVar(String name) throws ParseException {
		if (localVars.containsKey(name)) {
			throw new ParseException("Duplicate local variable: "+name, file, line, col);
		}
		localVars.put(name, varOffset + 1 + localVars.size());
	}
	
	private SymbolInstance parseLocal() throws ParseException {
		final int start = it.nextIndex();
		SymbolInstance symbol = next();
		if (symbol.is(TokenType.IDENTIFIER)) {
			String var = symbol.token.value;
			accept("=");
			symbol = parseExpression(false);
			if (symbol == null) {
				symbol = parseObject(false);
				if (symbol == null) {
					symbol = peek();
					throw new ParseException("Expected: EXPRESSION|OBJECT", file, symbol.token.line, symbol.token.col);
				}
			}
			accept(TokenType.EOL);
			SymbolInstance newInst = replace(start, "LOCAL_DECL");
			addLocalVar(var);
			//ASSIGNMENT
			popf(var);
			return newInst;
		} else if (symbol.is("constant")) {
			parse("IDENTIFIER =");
			String constant = symbol.token.value;
			if (localConst.containsKey(constant)) {
				throw new ParseException("Duplicate constant: "+constant, file, symbol.token.line, symbol.token.col);
			}
			symbol = next();
			if (symbol.token.type == TokenType.IDENTIFIER) {
				int val = getConstant(symbol);
				localConst.put(constant, val);
			} else if (symbol.token.type == TokenType.NUMBER) {
				localConst.put(constant, symbol.token.intVal());
			} else {
				throw new ParseException("Expected: IDENTIFIER|NUMBER", file, symbol.token.line, symbol.token.col);
			}
			accept(TokenType.EOL);
			SymbolInstance newInst = replace(start, "GLOBAL_CONST_DECL");
			return newInst;
		} else {
			throw new ParseException("Expected: IDENTIFIER|constant", file, symbol.token.line, symbol.token.col);
		}
	}
	
	private SymbolInstance parseStatements() throws ParseException {
		final int start = it.nextIndex();
		SymbolInstance symbol = parseStatement();
		while (symbol != null) {
			symbol = parseStatement();
		}
		SymbolInstance newInst = replace(start, "STATEMENTS");
		return newInst;
	}
	
	private SymbolInstance parseStatement() throws ParseException {
		SymbolInstance symbol = peek();
		if (symbol.is("challenge")) {
			return parseChallenge();
		} else if (symbol.is("remove")) {
			return parseRemove();
		} else if (symbol.is("add")) {
			return parseAdd();
		} else if (symbol.is("move")) {
			return parseMove();
		} else if (symbol.is("set")) {
			return parseSet();
		} else if (symbol.is("delete")) {
			return parseDelete();
		} else if (symbol.is("release")) {
			return parseRelease();
		} else if (symbol.is("enable") || symbol.is("disable")) {
			return parseEnableDisable();
		} else if (symbol.is("open") || symbol.is("close")) {
			return parseOpenClose();
		} else if (symbol.is("teach")) {
			return parseTeach();
		} else if (symbol.is("force")) {
			return parseForce();
		} else if (symbol.is("initialise")) {
			return parseInitialise();
		} else if (symbol.is("clear")) {
			return parseClear();
		} else if (symbol.is("attach")) {
			return parseAttach();
		} else if (symbol.is("toggle")) {
			return parseToggle();
		} else if (symbol.is("detach")) {
			return parseDetach();
		} else if (symbol.is("swap")) {
			return parseSwap();
		} else if (symbol.is("queue")) {
			return parseQueue();
		} else if (symbol.is("pause") || symbol.is("unpause")) {
			return parsePauseUnpause();
		} else if (symbol.is("load")) {
			return parseLoad();
		} else if (symbol.is("save")) {
			return parseSave();
		} else if (symbol.is("stop")) {
			return parseStop();
		} else if (symbol.is("start")) {
			return parseStart();
		} else if (symbol.is("disband")) {
			return parseDisband();
		} else if (symbol.is("populate")) {
			return parsePopulate();
		} else if (symbol.is("affect")) {
			return parseAffect();
		} else if (symbol.is("snapshot")) {
			return parseSnapshot();
		} else if (symbol.is("update")) {
			return parseUpdate();
		} else if (symbol.is("build")) {
			return parseBuild();
		} else if (symbol.is("run")) {
			return parseRun();
		} else if (symbol.is("wait")) {
			return parseWait();
		} else if (symbol.is("enter") || symbol.is("exit")) {
			return parseEnterExit();
		} else if (symbol.is("restart")) {
			return parseRestart();
		} else if (symbol.is("state")) {
			return parseState();
		} else if (symbol.is("make")) {
			return parseMake();
		} else if (symbol.is("eject")) {
			return parseEject();
		} else if (symbol.is("disappear")) {
			return parseDisappear();
		} else if (symbol.is("send")) {
			return parseSend();
		} else if (symbol.is("say")) {
			return parseSay();
		} else if (symbol.is("draw")) {
			return parseDraw();
		} else if (symbol.is("fade")) {
			return parseFade();
		} else if (symbol.is("store")) {
			return parseStore();
		} else if (symbol.is("restore")) {
			return parseRestore();
		} else if (symbol.is("reset")) {
			return parseReset();
		} else if (symbol.is("camera")) {
			return parseCamera();
		} else if (symbol.is("shake")) {
			return parseShake();
		} else if (symbol.is("if")) {
			return parseIf();
		} else if (symbol.is("while")) {
			return parseWhile();
		} else if (symbol.is("begin")) {
			return parseBegin();
		} else if (symbol.is(TokenType.IDENTIFIER)) {
			symbol = peek(1);
			if (symbol.is("play")) {
				return parseObjectPlay();
			} else {
				return parseAssignment();
			}
		}
		return null;
	}
	
	private SymbolInstance parseObjectPlay() throws ParseException {
		final int start = it.nextIndex();
		//OBJECT play CONST_EXPR loop EXPRESSION
		SymbolInstance symbol = accept(TokenType.IDENTIFIER);
		String var = symbol.token.value;
		pushf(var);
		pusho(var);
		parse("play CONST_EXPR loop EXPRESSION EOL");
		casti();
		sys(SET_SCRIPT_ULONG);
		pushi(200);
		sys(SET_SCRIPT_STATE);
		SymbolInstance newInst = replace(start, "STATEMENT");
		return newInst;
	}
	
	private SymbolInstance parseRemove() throws ParseException {
		final int start = it.nextIndex();
		parse("remove resource CONST_EXPR EXPRESSION from OBJECT EOL");
		//remove resource CONST_EXPR EXPRESSION from OBJECT
		sys(REMOVE_RESOURCE);
		popf();
		SymbolInstance newInst = replace(start, "STATEMENT");
		return newInst;
	}
	
	private SymbolInstance parseAdd() throws ParseException {
		final int start = it.nextIndex();
		accept("add");
		SymbolInstance symbol = peek();
		if (symbol.is("for")) {
			parse("for building OBJECT to OBJECT EOL");
			//add for building OBJECT to OBJECT
			throw new ParseException("Statement not implemented", file, line, col);
			//SymbolInstance newInst = replace(start, "STATEMENT");
			//return newInst;
		} else if (symbol.is("resource")) {
			parse("resource CONST_EXPR EXPRESSION from OBJECT EOL");
			//add resource CONST_EXPR EXPRESSION from OBJECT
			sys(ADD_RESOURCE);
			popf();
			SymbolInstance newInst = replace(start, "STATEMENT");
			return newInst;
		} else {
			parse("OBJECT target");
			symbol = next();
			if (symbol.is("at")) {
				parse("COORD_EXPR EOL");
				//add OBJECT target at COORD_EXPR
				sys(ADD_SPOT_VISUAL_TARGET_POS);
				SymbolInstance newInst = replace(start, "STATEMENT");
				return newInst;
			} else if (symbol.is("on")) {
				parse("OBJECT EOL");
				//add OBJECT target on OBJECT
				sys(ADD_SPOT_VISUAL_TARGET_OBJECT);
				SymbolInstance newInst = replace(start, "STATEMENT");
				return newInst;
			} else {
				throw new ParseException("Unexpected token: "+symbol, file, symbol.token.line, symbol.token.col);
			}
		}
	}

	private SymbolInstance parseMove() throws ParseException {
		final int start = it.nextIndex();
		accept("move");
		SymbolInstance symbol = peek();
		if (symbol.is("computer")) {
			parse("computer player EXPRESSION to COORD_EXPR speed EXPRESSION");
			parseOption("with fixed height");
			accept(TokenType.EOL);
			//move computer player EXPRESSION to COORD_EXPR speed EXPRESSION [with fixed height]
			sys(MOVE_COMPUTER_PLAYER_POSITION);
			SymbolInstance newInst = replace(start, "STATEMENT");
			return newInst;
		} else if (symbol.is("game")) {
			parse("game time EXPRESSION time EXPRESSION EOL");
			//move game time EXPRESSION time EXPRESSION
			sys(MOVE_GAME_TIME);
			SymbolInstance newInst = replace(start, "STATEMENT");
			return newInst;
		} else if (symbol.is("music")) {
			parse("music from OBJECT to OBJECT EOL");
			//move music from OBJECT to OBJECT
			throw new ParseException("Statement not implemented", file, line, col);
			/*SymbolInstance newInst = replace(start, "STATEMENT");
			return newInst;*/
		} else if (symbol.is("camera")) {
			accept("camera");
			symbol = peek();
			if (symbol.is("position")) {
				accept("position");
				symbol = peek();
				if (symbol.is("to")) {
					parse("to COORD_EXPR time EXPRESSION EOL");
					//move camera position to COORD_EXPR time EXPRESSION
					sys(MOVE_CAMERA_POSITION);
					SymbolInstance newInst = replace(start, "STATEMENT");
					return newInst;
				} else if (symbol.is("follow")) {
					parse("follow OBJECT EOL");
					//move camera position follow OBJECT
					sys(POSITION_FOLLOW);
					SymbolInstance newInst = replace(start, "STATEMENT");
					return newInst;
				} else {
					parse("COORD_EXPR docus COORD_EXPR lens EXPRESSION time EXPRESSION EOL");
					//move camera position COORD_EXPR focus COORD_EXPR lens EXPRESSION time EXPRESSION
					throw new ParseException("Statement not implemented", file, line, col);
					/*SymbolInstance newInst = replace(start, "STATEMENT");
					return newInst;*/
				}
			} else if (symbol.is("focus")) {
				accept("focus");
				symbol = peek();
				if (symbol.is("to")) {
					parse("to COORD_EXPR time EXPRESSION EOL");
					//move camera focus to COORD_EXPR time EXPRESSION
					sys(MOVE_CAMERA_FOCUS);
					SymbolInstance newInst = replace(start, "STATEMENT");
					return newInst;
				} else if (symbol.is("follow")) {
					parse("follow OBJECT EOL");
					//move camera focus follow OBJECT
					sys(FOCUS_FOLLOW);
					SymbolInstance newInst = replace(start, "STATEMENT");
					return newInst;
				} else {
					throw new ParseException("Unexpected token: "+symbol, file, symbol.token.line, symbol.token.col);
				}
			} else if (symbol.is("to")) {
				accept("to");
				symbol = peek();
				if (symbol.is("face")) {
					parse("face OBJECT distance EXPRESSION time EXPRESSION EOL");
					//move camera to face OBJECT distance EXPRESSION time EXPRESSION
					sys(MOVE_CAMERA_TO_FACE_OBJECT);
					SymbolInstance newInst = replace(start, "STATEMENT");
					return newInst;
				} else {
					//move camera to CONST_EXPR time EXPRESSION
					//TODO bug? See notes in 3.18.23
					symbol = accept(TokenType.IDENTIFIER);
					String var = symbol.token.value;
					int val = getConstant(var);
					pushi(val);
					sys(CONVERT_CAMERA_FOCUS);
					pushi(val);
					sys(CONVERT_CAMERA_POSITION);
					parse("time EXPRESSION EOL");
					swapf(4);
					sys(MOVE_CAMERA_POSITION);
					sys(MOVE_CAMERA_FOCUS);
					SymbolInstance newInst = replace(start, "STATEMENT");
					return newInst;
				}
			} else {
				throw new ParseException("Unexpected token: "+symbol, file, symbol.token.line, symbol.token.col);
			}
		} else {
			parseObject(true);
			parse("position to COORD_EXPR");
			parseExpression("radius", 0);
			accept(TokenType.EOL);
			//move OBJECT position to COORD_EXPR [radius EXPRESSION]
			sys(MOVE_GAME_THING);
			SymbolInstance newInst = replace(start, "STATEMENT");
			return newInst;
		}
	}
	
	private SymbolInstance parseSet() throws ParseException {
		final int start = it.nextIndex();
		accept("set");
		SymbolInstance symbol = peek();
		if (symbol.is("player_creature")) {
			parse("player_creature to OBJECT EOL");
			//set player_creature to OBJECT
			sys(CREATURE_SET_PLAYER);
			SymbolInstance newInst = replace(start, "STATEMENT");
			return newInst;
		} else if (symbol.is("player")) {
			parse("player EXPRESSION ally with player EXPRESSION percentage EXPRESSION EOL");
			//set player EXPRESSION ally with player EXPRESSION percentage EXPRESSION
			sys(SET_PLAYER_ALLY);
			SymbolInstance newInst = replace(start, "STATEMENT");
			return newInst;
		} else if (symbol.is("computer")) {
			parse("computer player EXPRESSION");
			symbol = peek();
			if (symbol.is("position")) {
				parse("position to COORD_EXPR EOL");
				//set computer player EXPRESSION position to COORD_EXPR
				pushb(false);
				sys(SET_COMPUTER_PLAYER_POSITION);
				SymbolInstance newInst = replace(start, "STATEMENT");
				return newInst;
			} else if (symbol.is("personality")) {
				parse("personality STRING EXPRESSION EOL");
				//set computer player EXPRESSION personality STRING EXPRESSION
				sys(SET_COMPUTER_PLAYER_PERSONALITY);
				SymbolInstance newInst = replace(start, "STATEMENT");
				return newInst;
			} else if (symbol.is("suppression")) {
				parse("suppression STRING EXPRESSION EOL");
				//set computer player EXPRESSION suppression STRING EXPRESSION
				throw new ParseException("Statement not implemented", file, line, col);
				/*SymbolInstance newInst = replace(start, "STATEMENT");
				return newInst;*/
			} else if (symbol.is("speed")) {
				parse("speed EXPRESSION EOL");
				//set computer player EXPRESSION speed EXPRESSION
				sys(SET_COMPUTER_PLAYER_SPEED);
				SymbolInstance newInst = replace(start, "STATEMENT");
				return newInst;
			} else if (symbol.is("attitude")) {
				parse("attitude to player EXPRESSION to EXPRESSION EOL");
				//set computer player EXPRESSION attitude to player EXPRESSION to EXPRESSION
				sys(SET_COMPUTER_PLAYER_ATTITUDE);
				SymbolInstance newInst = replace(start, "STATEMENT");
				return newInst;
			} else {
				throw new ParseException("Unexpected token: "+symbol, file, symbol.token.line, symbol.token.col);
			}
		} else if (symbol.is("game")) {
			accept("game");
			symbol = peek();
			if (symbol.is("time")) {
				accept("time");
				symbol = peek();
				if (symbol.is("properties")) {
					parse("properties duration EXPRESSION percentage night EXPRESSION percentage dawn dusk EXPRESSION EOL");
					//set game time properties duration EXPRESSION percentage night EXPRESSION percentage dawn dusk EXPRESSION
					throw new ParseException("Statement not implemented", file, line, col);
					/*SymbolInstance newInst = replace(start, "STATEMENT");
					return newInst;*/
				} else {
					parse("EXPRESSION EOL");
					//set game time EXPRESSION
					sys(SET_GAME_TIME);
					SymbolInstance newInst = replace(start, "STATEMENT");
					return newInst;
				}
			} else if (symbol.is("speed")) {
				parse("speed to EXPRESSION EOL");
				//set game speed to EXPRESSION
				sys2(SET_GAMESPEED);
				SymbolInstance newInst = replace(start, "STATEMENT");
				return newInst;
			} else {
				throw new ParseException("Unexpected token: "+symbol, file, symbol.token.line, symbol.token.col);
			}
		} else if (symbol.is("interaction")) {
			parse("interaction CONST_EXPR EOL");
			//set interaction CONST_EXPR
			sys2(SET_INTERFACE_INTERACTION);
			SymbolInstance newInst = replace(start, "STATEMENT");
			return newInst;
		} else if (symbol.is("fade")) {
			accept("fade");
			symbol = peek();
			if (symbol.is("red")) {
				parse("red EXPRESSION green EXPRESSION blue EXPRESSION time EXPRESSION EOL");
				//set fade red EXPRESSION green EXPRESSION blue EXPRESSION time EXPRESSION
				sys(SET_FADE);
				SymbolInstance newInst = replace(start, "STATEMENT");
				return newInst;
			} else if (symbol.is("in")) {
				parse("in time EXPRESSION EOL");
				//set fade in time EXPRESSION
				sys(SET_FADE_IN);
				SymbolInstance newInst = replace(start, "STATEMENT");
				return newInst;
			} else {
				throw new ParseException("Unexpected token: "+symbol, file, symbol.token.line, symbol.token.col);
			}
		} else if (symbol.is("bookmark")) {
			parse("bookmark EXPRESSION to COORD_EXPR EOL");
			//set bookmark EXPRESSION to COORD_EXPR
			throw new ParseException("Statement not implemented", file, line, col);
			/*SymbolInstance newInst = replace(start, "STATEMENT");
			return newInst;*/
		} else if (symbol.is("draw")) {
			parse("draw text colour red EXPRESSION green EXPRESSION blue EXPRESSION EOL");
			//set draw text colour red EXPRESSION green EXPRESSION blue EXPRESSION
			sys(SET_DRAW_TEXT_COLOUR);
			SymbolInstance newInst = replace(start, "STATEMENT");
			return newInst;
		} else if (symbol.is("clipping")) {
			parse("clipping window across EXPRESSION down EXPRESSION width EXPRESSION height EXPRESSION EOL");
			//set clipping window across EXPRESSION down EXPRESSION width EXPRESSION height EXPRESSION
			sys(SET_CLIPPING_WINDOW);
			SymbolInstance newInst = replace(start, "STATEMENT");
			return newInst;
		} else if (symbol.is("camera")) {
			accept("camera");
			symbol = peek();
			if (symbol.is("zones")) {
				parse("zones to STRING EOL");
				SymbolInstance newInst = replace(start, "STATEMENT");
				//TODO set camera zones to STRING
				return newInst;
			} else if (symbol.is("lens")) {
				parse("lens EXPRESSION");
				symbol = peek();
				if (symbol.is("time")) {
					parse("time EXPRESSION");
				} else {
					//TODO default time=0
				}
				accept(TokenType.EOL);
				SymbolInstance newInst = replace(start, "STATEMENT");
				//TODO set camera lens EXPRESSION [time EXPRESSION]
				return newInst;
			} else if (symbol.is("position")) {
				accept("position");
				symbol = peek();
				if (symbol.is("to")) {
					parse("to COORD_EXPR EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO set camera position to COORD_EXPR
					return newInst;
				} else if (symbol.is("follow")) {
					accept("follow");
					symbol = peek();
					if (symbol.is("computer")) {
						parse("computer player EXPRESSION EOL");
						SymbolInstance newInst = replace(start, "STATEMENT");
						//TODO set camera position follow computer player EXPRESSION
						return newInst;
					} else {
						parse("OBJECT EOL");
						SymbolInstance newInst = replace(start, "STATEMENT");
						//TODO set camera position follow OBJECT
						return newInst;
					}
				} else {
					parse("COORD_EXPR focus COORD_EXPR lens EXPRESSION EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO set camera position COORD_EXPR focus COORD_EXPR lens EXPRESSION
					return newInst;
				}
			} else if (symbol.is("focus")) {
				accept("focus");
				symbol = peek();
				if (symbol.is("to")) {
					parse("to COORD_EXPR EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO set camera focus to COORD_EXPR
					return newInst;
				} else if (symbol.is("follow")) {
					accept("follow");
					symbol = peek();
					if (symbol.is("computer")) {
						parse("computer player EXPRESSION EOL");
						SymbolInstance newInst = replace(start, "STATEMENT");
						//TODO set camera focus follow computer player EXPRESSION
						return newInst;
					} else {
						parse("OBJECT EOL");
						SymbolInstance newInst = replace(start, "STATEMENT");
						//TODO set camera focus follow OBJECT
						return newInst;
					}
				} else {
					throw new ParseException("Unexpected token: "+symbol, file, symbol.token.line, symbol.token.col);
				}
			} else if (symbol.is("to")) {
				accept("to");
				symbol = peek();
				if (symbol.is("face")) {
					parse("face OBJECT distance EXPRESSION EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO set camera to face OBJECT distance EXPRESSION
					return newInst;
				} else {
					parse("CONST_EXPR EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO set camera to CONST_EXPR
					return newInst;
				}
			} else if (symbol.is("follow")) {
				parse("follow OBJECT distance EXPRESSION EOL");
				SymbolInstance newInst = replace(start, "STATEMENT");
				//TODO set camera follow OBJECT distance EXPRESSION
				return newInst;
			} else if (symbol.is("properties")) {
				parse("properties distance EXPRESSION speed EXPRESSION angle EXPRESSION enable|disable behind EOL");
				SymbolInstance newInst = replace(start, "STATEMENT");
				//TODO set camera properties distance EXPRESSION speed EXPRESSION angle EXPRESSION enable|disable behind
				return newInst;
			} else {
				throw new ParseException("Unexpected token: "+symbol, file, symbol.token.line, symbol.token.col);
			}
		} else if (symbol.is("dual")) {
			parse("dual camera to OBJECT OBJECT EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO set dual camera to OBJECT OBJECT
			return newInst;
		} else {
			symbol = parseObject(false);
			if (symbol != null) {
				symbol = peek();
				if (symbol.is("position")) {
					parse("position to COORD_EXPR EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO set OBJECT position to COORD_EXPR
					return newInst;
				} else if (symbol.is("disciple")) {
					parse("disciple CONST_EXPR");
					parseOption("with sound");
					accept(TokenType.EOL);
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO set OBJECT disciple CONST_EXPR [with sound]
					return newInst;
				} else if (symbol.is("focus")) {
					accept("focus");
					symbol = peek();
					if (symbol.is("to")) {
						parse("to COORD_EXPR EOL");
						SymbolInstance newInst = replace(start, "STATEMENT");
						//TODO set OBJECT focus to COORD_EXPR
						return newInst;
					} else if (symbol.is("on")) {
						parse("on OBJECT EOL");
						SymbolInstance newInst = replace(start, "STATEMENT");
						//TODO set OBJECT focus on OBJECT
						return newInst;
					} else {
						throw new ParseException("Expected: to|on", file, symbol.token.line, symbol.token.col);
					}
				} else if (symbol.is("anim")) {
					parse("anim CONST_EXPR EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO set OBJECT anim CONST_EXPR
					return newInst;
				} else if (symbol.is("properties")) {
					accept("properties");
					symbol = peek();
					if (symbol.is("inner")) {
						parse("inner EXPRESSION outer EXPRESSION");
						symbol = peek();
						if (symbol.is("calm")) {
							parse("calm EXPRESSION");
						} else {
							//TODO default calm=0
						}
						accept(TokenType.EOL);
						SymbolInstance newInst = replace(start, "STATEMENT");
						//TODO set OBJECT properties inner EXPRESSION outer EXPRESSION [calm EXPRESSION]
						return newInst;
					} else if (symbol.is("town")) {
						parse("town OBJECT flock position COORD_EXPR distance EXPRESSION radius EXPRESSION flock OBJECT EOL");
						SymbolInstance newInst = replace(start, "STATEMENT");
						//TODO set OBJECT properties town OBJECT flock position COORD_EXPR distance EXPRESSION radius EXPRESSION flock OBJECT
						return newInst;
					} else if (symbol.is("degrees")) {
						parse("degrees EXPRESSION rainfall EXPRESSION snowfall EXPRESSION overcast EXPRESSION speed EXPRESSION EOL");
						SymbolInstance newInst = replace(start, "STATEMENT");
						//TODO set OBJECT properties degrees EXPRESSION rainfall EXPRESSION snowfall EXPRESSION overcast EXPRESSION speed EXPRESSION
						return newInst;
					} else if (symbol.is("time")) {
						parse("time EXPRESSION fade EXPRESSION EOL");
						SymbolInstance newInst = replace(start, "STATEMENT");
						//TODO set OBJECT properties time EXPRESSION fade EXPRESSION
						return newInst;
					} else if (symbol.is("clouds")) {
						parse("clouds EXPRESSION shade EXPRESSION height EXPRESSION EOL");
						SymbolInstance newInst = replace(start, "STATEMENT");
						//TODO set OBJECT properties clouds EXPRESSION shade EXPRESSION height EXPRESSION
						return newInst;
					} else if (symbol.is("sheetmin")) {
						parse("sheetmin EXPRESSION sheetmax EXPRESSION forkmin EXPRESSION forkmax EXPRESSION EOL");
						SymbolInstance newInst = replace(start, "STATEMENT");
						//TODO set OBJECT properties sheetmin EXPRESSION sheetmax EXPRESSION forkmin EXPRESSION forkmax EXPRESSION
						return newInst;
					} else {
						throw new ParseException("Expected: inner|town|degrees|time|clouds|sheetmin", file, symbol.token.line, symbol.token.col);
					}
				} else if (symbol.is("text")) {
					parse("text property text CONST_EXPR category CONST_EXPR EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO set OBJECT text property text CONST_EXPR category CONST_EXPR
					return newInst;
				} else if (symbol.is("velocity")) {
					parse("velocity heading COORD_EXPR speed EXPRESSION EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO set OBJECT velocity heading COORD_EXPR speed EXPRESSION
					return newInst;
				} else if (symbol.is("target")) {
					parse("target COORD_EXPR time EXPRESSION EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO set OBJECT target COORD_EXPR time EXPRESSION
					return newInst;
				} else if (symbol.is("time")) {
					parse("set OBJECT time to EXPRESSION second|seconds EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO set OBJECT time to EXPRESSION second|seconds
					return newInst;
				} else if (symbol.is("radius")) {
					parse("radius EXPRESSION EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO set OBJECT radius EXPRESSION
					return newInst;
				} else if (symbol.is("mana")) {
					parse("mana EXPRESSION EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO set OBJECT mana EXPRESSION
					return newInst;
				} else if (symbol.is("temperature")) {
					parse("temperature EXPRESSION EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO set OBJECT temperature EXPRESSION
					return newInst;
				} else if (symbol.is("forward") || symbol.is("reverse")) {
					parse("forward|reverse walk path CONST_EXPR from EXPRESSION to EXPRESSION EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO set OBJECT forward|reverse walk path CONST_EXPR from EXPRESSION to EXPRESSION
					return newInst;
				} else if (symbol.is("desire")) {
					accept("desire");
					symbol = peek();
					if (symbol.is("maximum")) {
						parse("maximum CONST_EXPR to EXPRESSION EOL");
						SymbolInstance newInst = replace(start, "STATEMENT");
						//TODO set OBJECT desire maximum CONST_EXPR to EXPRESSION
						return newInst;
					} else if (symbol.is("boost")) {
						parse("boost TOWN_DESIRE_INFO EXPRESSION EOL");
						SymbolInstance newInst = replace(start, "STATEMENT");
						//TODO set OBJECT desire boost TOWN_DESIRE_INFO EXPRESSION
						return newInst;
					} else {
						parseConstExpr(true);
						symbol = peek();
						if (symbol.is("to")) {
							parseExpression(true);
							accept(TokenType.EOL);
							SymbolInstance newInst = replace(start, "STATEMENT");
							//TODO set OBJECT desire CONST_EXPR to EXPRESSION
							return newInst;
						} else {
							parseConstExpr(true);
							accept(TokenType.EOL);
							SymbolInstance newInst = replace(start, "STATEMENT");
							//TODO set OBJECT desire CONST_EXPR CONST_EXPR
							return newInst;
						}
					}
				} else if (symbol.is("only")) {
					parse("only desire CONST_EXPR EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO set OBJECT only desire CONST_EXPR
					return newInst;
				} else if (symbol.is("disable")) {
					parse("disable only desire EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO set OBJECT disable only desire
					return newInst;
				} else if (symbol.is("magic")) {
					parse("set OBJECT magic properties MAGIC_TYPE");
					symbol = peek();
					if (symbol.is("time")) {
						parse("time EXPRESSION");
					} else {
						//TODO default time=0
					}
					accept(TokenType.EOL);
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO set OBJECT magic properties MAGIC_TYPE [time EXPRESSION]
					return newInst;
				} else if (symbol.is("all")) {
					parse("all desire CONST_EXPR EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO set OBJECT all desire CONST_EXPR
					return newInst;
				} else if (symbol.is("priority")) {
					parse("priority EXPRESSION EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO set OBJECT priority EXPRESSION
					return newInst;
				} else if (symbol.is("home")) {
					parse("home position COORD_EXPR EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO set OBJECT home position COORD_EXPR
					return newInst;
				} else if (symbol.is("creed")) {
					parse("creed properties hand HAND_GLOW scale EXPRESSION power EXPRESSION time EXPRESSION EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO set OBJECT creed properties hand CONST_EXPR scale EXPRESSION power EXPRESSION time EXPRESSION
					return newInst;
				} else if (symbol.is("name")) {
					parse("name CONST_EXPR EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO set OBJECT name CONST_EXPRset OBJECT name CONST_EXPR
					return newInst;
				} else if (symbol.is("fade")) {
					accept("fade");
					symbol = peek();
					if (symbol.is("start")) {
						parse("start scale EXPRESSION end scale EXPRESSION start transparency EXPRESSION end transparency EXPRESSION time EXPRESSION EOL");
						SymbolInstance newInst = replace(start, "STATEMENT");
						//TODO set OBJECT fade start scale EXPRESSION end scale EXPRESSION start transparency EXPRESSION end transparency EXPRESSION time EXPRESSION
						return newInst;
					} else if (symbol.is("in")) {
						parse("in time EXPRESSION EOL");
						SymbolInstance newInst = replace(start, "STATEMENT");
						//TODO set OBJECT fade in time EXPRESSION
						return newInst;
					} else {
						throw new ParseException("Expected: start|in", file, symbol.token.line, symbol.token.col);
					}
				} else if (symbol.is("belief")) {
					parse("belief scale EXPRESSION EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO set OBJECT belief scale EXPRESSION
					return newInst;
				} else if (symbol.is("player")) {
					parseExpression(true);
					symbol = peek();
					if (symbol.is("relative")) {
						parse("relative belief EOL");
						SymbolInstance newInst = replace(start, "STATEMENT");
						//TODO set OBJECT player EXPRESSION relative belief
						return newInst;
					} else if (symbol.is("")) {
						parse("belief EXPRESSION EOL");
						SymbolInstance newInst = replace(start, "STATEMENT");
						//TODO set OBJECT player EXPRESSION belief EXPRESSION
						return newInst;
					} else {
						throw new ParseException("Expected: start|in", file, symbol.token.line, symbol.token.col);
					}
				} else if (symbol.is("building")) {
					parse("set OBJECT building properties ABODE_NUMBER size EXPRESSION EOL");
					parseOption("destroys when placed");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO set OBJECT building properties ABODE_NUMBER size EXPRESSION [destroys when placed]
					return newInst;
				} else if (symbol.is("carrying")) {
					parse("carrying CARRIED_OBJECT EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO set OBJECT carrying CARRIED_OBJECT
					return newInst;
				} else if (symbol.is("music")) {
					parse("music position to COORD_EXPR EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO set OBJECT music position to COORD_EXPR
					return newInst;
				} else {
					parse("CONST_EXPR development EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO set OBJECT CONST_EXPR development
					return newInst;
				}
			} else {
				symbol = parseExpression(true);
				if (symbol != null) {
					parse("land balance EXPRESSION EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO set EXPRESSION land balance EXPRESSION
					return newInst;
				} else {
					symbol = peek();
					throw new ParseException("Expected: EXPRESSION|OBJECT", file, symbol.token.line, symbol.token.col);
				}
			}
		}
	}
	
	private SymbolInstance parseDelete() throws ParseException {
		final int start = it.nextIndex();
		accept("delete");
		SymbolInstance symbol = peek();
		if (symbol.is("all")) {
			parse("all weather at COORD_EXPR radius EXPRESSION EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO delete all weather at COORD_EXPR radius EXPRESSION
			return newInst;
		} else {
			parseObject(true);
			parseOption("with fade");
			accept(TokenType.EOL);
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO delete OBJECT [with fade]
			return newInst;
		}
	}
	
	private SymbolInstance parseRelease() throws ParseException {
		final int start = it.nextIndex();
		accept("release");
		SymbolInstance symbol = peek();
		if (symbol.is("computer")) {
			parse("computer player EXPRESSION EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO release computer player EXPRESSION
			return newInst;
		} else {
			parse("OBJECT focus EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO release OBJECT focus
			return newInst;
		}
	}
	
	private SymbolInstance parseEnableDisable() throws ParseException {
		final int start = it.nextIndex();
		parseEnableDisableKeyword();
		SymbolInstance symbol = peek();
		if (symbol.is("leash")) {
			accept("leash");
			symbol = peek();
			if (symbol.is("on")) {
				parse("on OBJECT EOL");
				SymbolInstance newInst = replace(start, "STATEMENT");
				//TODO enable|disable leash on OBJECT
				return newInst;
			} else if (symbol.is("draw")) {
				accept("draw EOL");
				SymbolInstance newInst = replace(start, "STATEMENT");
				//TODO enable|disable leash draw
				return newInst;
			} else {
				throw new ParseException("Expected: on|draw", file, symbol.token.line, symbol.token.col);
			}
		} else if (symbol.is("player")) {
			parse("player EXPRESSION");
			symbol = peek();
			if (symbol.is("wind")) {
				parse("wind resistance EOL");
				SymbolInstance newInst = replace(start, "STATEMENT");
				//TODO enable|disable player EXPRESSION wind resistance
				return newInst;
			} else if (symbol.is("virtual")) {
				parse("virtual influence EOL");
				SymbolInstance newInst = replace(start, "STATEMENT");
				//TODO enable|disable player EXPRESSION virtual influence
				return newInst;
			} else {
				throw new ParseException("Expected: wind|virtual", file, symbol.token.line, symbol.token.col);
			}
		} else if (symbol.is("fight")) {
			parse("fight exit EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO enable|disable fight exit
			return newInst;
		} else if (symbol.is("computer")) {
			parse("computer player EXPRESSION EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO enable|disable computer player EXPRESSION
			return newInst;
		} else if (symbol.is("game")) {
			parse("game time EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO enable|disable game time
			return newInst;
		} else if (symbol.is("help")) {
			parse("help system EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO enable|disable help system
			return newInst;
		} else if (symbol.is("creature")) {
			accept("creature");
			symbol = peek();
			if (symbol.is("sound")) {
				parse("sound EOL");
				SymbolInstance newInst = replace(start, "STATEMENT");
				//TODO enable|disable creature sound
				return newInst;
			} else if (symbol.is("in")) {
				parse("in temple EOL");
				SymbolInstance newInst = replace(start, "STATEMENT");
				//TODO enable|disable creature in temple
				return newInst;
			} else {
				throw new ParseException("Expected: sound|in", file, symbol.token.line, symbol.token.col);
			}
		} else if (symbol.is("sound")) {
			parse("sound effects EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO enable|disable sound effects
			return newInst;
		} else if (symbol.is("constant")) {
			parse("constant avi sequence EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO enable|disable constant avi sequence
			return newInst;
		} else if (symbol.is("spell")) {
			accept("spell");
			symbol = peek();
			if (symbol.is("constant")) {
				parse("constant in OBJECT EOL");
				SymbolInstance newInst = replace(start, "STATEMENT");
				//TODO enable|disable spell constant in OBJECT
				return newInst;
			} else {
				parse("CONST_EXPR for player EXPRESSION EOL");
				SymbolInstance newInst = replace(start, "STATEMENT");
				//TODO enable|disable spell CONST_EXPR for player EXPRESSION
				return newInst;
			}
		} else if (symbol.is("angle")) {
			parse("angle sound EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO enable|disable angle sound
			return newInst;
		} else if (symbol.is("pitch")) {
			parse("pitch sound EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO enable|disable pitch sound
			return newInst;
		} else if (symbol.is("highlight")) {
			parse("highlight draw EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO enable|disable highlight draw
			return newInst;
		} else if (symbol.is("intro")) {
			parse("intro building EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO enable|disable intro building
			return newInst;
		} else if (symbol.is("temple")) {
			parse("temple EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO enable|disable temple
			return newInst;
		} else if (symbol.is("climate")) {
			accept("climate");
			symbol = peek();
			if (symbol.is("weather")) {
				parse("weather EOL");
				SymbolInstance newInst = replace(start, "STATEMENT");
				//TODO enable|disable climate weather
				return newInst;
			} else if (symbol.is("create")) {
				parse("create storms EOL");
				SymbolInstance newInst = replace(start, "STATEMENT");
				//TODO enable|disable climate create storms
				return newInst;
			} else {
				throw new ParseException("Expected: weather|create", file, symbol.token.line, symbol.token.col);
			}
		} else if (symbol.is("music")) {
			parse("music on OBJECT EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO enable|disable music on OBJECT
			return newInst;
		} else if (symbol.is("alignment")) {
			parse("alignment music EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO enable|disable alignment music
			return newInst;
		} else if (symbol.is("clipping")) {
			parse("clipping distance EXPRESSION EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO enable|disable clipping distance EXPRESSION
			return newInst;
		} else if (symbol.is("camera")) {
			parse("camera fixed rotation at COORD_EXPR EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO enable|disable camera fixed rotation at COORD_EXPR
			return newInst;
		} else if (symbol.is("jc")) {
			parse("jc special on OBJECT EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO enable|disable jc special on OBJECT
			return newInst;
		} else {
			symbol = parseObject(false);
			if (symbol != null) {
				symbol = peek();
				if (symbol.is("active")) {
					parse("active EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO enable|disable OBJECT active
					return newInst;
				} else if (symbol.is("attack")) {
					parse("attack own town EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO enable|disable OBJECT attack own town
					return newInst;
				} else if (symbol.is("reaction")) {
					parse("reaction EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO enable|disable OBJECT reaction
					return newInst;
				} else if (symbol.is("development")) {
					parse("development script EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO enable|disable OBJECT development script
					return newInst;
				} else if (symbol.is("spell")) {
					parse("spell reversion EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO enable|disable OBJECT spell reversion
					return newInst;
				} else if (symbol.is("anim")) {
					parse("anim time modify EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO enable|disable OBJECT anim time modify
					return newInst;
				} else if (symbol.is("friends")) {
					parse("friends with OBJECT EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO enable|disable OBJECT friends with OBJECT
					return newInst;
				} else if (symbol.is("auto")) {
					accept("auto");
					symbol = peek();
					if (symbol.is("fighting")) {
						parse("fighting EOL");
						SymbolInstance newInst = replace(start, "STATEMENT");
						//TODO enable|disable OBJECT auto fighting
						return newInst;
					} else if (symbol.is("scale")) {
						parse("scale EXPRESSION EOL");
						SymbolInstance newInst = replace(start, "STATEMENT");
						//TODO enable|disable OBJECT auto scale EXPRESSION
						return newInst;
					} else {
						throw new ParseException("Expected: fighting|scale", file, symbol.token.line, symbol.token.col);
					}
				} else if (symbol.is("only")) {
					parse("only for scripts EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO enable|disable OBJECT only for scripts
					return newInst;
				} else if (symbol.is("poisoned")) {
					parse("poisoned EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO enable|disable OBJECT poisoned
					return newInst;
				} else if (symbol.is("build")) {
					parse("build worship site EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO enable|disable OBJECT build worship site
					return newInst;
				} else if (symbol.is("skeleton")) {
					parse("skeleton EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO enable|disable OBJECT skeleton
					return newInst;
				} else if (symbol.is("indestructible")) {
					parse("indestructible EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO enable|disable OBJECT indestructible
					return newInst;
				} else if (symbol.is("hurt")) {
					parse("hurt by fire EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO enable|disable OBJECT hurt by fire
					return newInst;
				} else if (symbol.is("set")) {
					parse("set on fire EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO enable|disable OBJECT set on fire
					return newInst;
				} else if (symbol.is("on")) {
					parse("on fire EXPRESSION EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO enable|disable OBJECT on fire EXPRESSION
					return newInst;
				} else if (symbol.is("moveable")) {
					parse("moveable EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO enable|disable OBJECT moveable
					return newInst;
				} else if (symbol.is("pickup")) {
					parse("pickup EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO enable|disable OBJECT pickup
					return newInst;
				} else if (symbol.is("high")) {
					parse("high graphics|gfx detail EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO enable|disable OBJECT high graphics detail
					return newInst;
				} else if (symbol.is("affected")) {
					parse("affected by wind EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO enable|disable OBJECT affected by wind
					return newInst;
				} else {
					throw new ParseException("Unexpected token: "+symbol, file, symbol.token.line, symbol.token.col);
				}
			} else {
				symbol = parseConstExpr(false);
				if (symbol != null) {
					parse("avi sequence EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO enable|disable CONST_EXPR avi sequence
					return newInst;
				} else {
					symbol = peek();
					throw new ParseException("Unexpected token: "+symbol, file, symbol.token.line, symbol.token.col);
				}
			}
		}
	}
	
	private SymbolInstance parseOpenClose() throws ParseException {
		final int start = it.nextIndex();
		if (peek().is("close") && peek(1).is("dialogue")) {
			parse("close dialogue EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO close dialogue
			return newInst;
		} else {
			parse("open|close OBJECT EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO open|close OBJECT
			return newInst;
		}
	}
	
	private SymbolInstance parseTeach() throws ParseException {
		final int start = it.nextIndex();
		parse("teach OBJECT");
		SymbolInstance symbol = peek();
		if (symbol.is("all")) {
			accept("all");
			symbol = peek();
			if (symbol.is("excluding")) {
				parse("excluding CONST_EXPR EOL");
				SymbolInstance newInst = replace(start, "STATEMENT");
				//TODO teach OBJECT all excluding CONST_EXPR
				return newInst;
			} else {
				accept(TokenType.EOL);
				SymbolInstance newInst = replace(start, "STATEMENT");
				//TODO teach OBJECT all
				return newInst;
			}
		} else {
			parse("CONST_EXPR CONST_EXPR CONST_EXPR CONST_EXPR EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO teach OBJECT CONST_EXPR CONST_EXPR CONST_EXPR CONST_EXPR
			return newInst;
		}
	}
	
	private SymbolInstance parseForce() throws ParseException {
		final int start = it.nextIndex();
		accept("force");
		SymbolInstance symbol = peek();
		if (symbol.is("action")) {
			parse("action OBJECT finish EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO force action OBJECT finish
			return newInst;
		} else if (symbol.is("computer")) {
			parse("computer player EXPRESSION action STRING OBJECT OBJECT EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO force computer player EXPRESSION action STRING OBJECT OBJECT
			return newInst;
		} else {
			parse("OBJECT CONST_EXPR OBJECT");
			symbol = peek();
			if (symbol.is("with")) {
				parse("with OBJECT");
			} else {
				//TODO default object: 0
			}
			accept(TokenType.EOL);
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO force OBJECT CONST_EXPR OBJECT [with OBJECT]
			return newInst;
		}
	}
	
	private SymbolInstance parseInitialise() throws ParseException {
		final int start = it.nextIndex();
		parse("initialise number of constant for OBJECT EOL");
		SymbolInstance newInst = replace(start, "STATEMENT");
		//TODO initialise number of constant for OBJECT
		return newInst;
	}
	
	private SymbolInstance parseClear() throws ParseException {
		final int start = it.nextIndex();
		accept("clear");
		SymbolInstance symbol = peek();
		if (symbol.is("dropped")) {
			parse("dropped by OBJECT EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO clear dropped by OBJECT
			return newInst;
		} else if (symbol.is("computer")) {
			parse("computer player EXPRESSION actions EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO clear computer player EXPRESSION actions
			return newInst;
		} else if (symbol.is("clicked")) {
			symbol = peek();
			if (symbol.is("object")) {
				parse("object EOL");
				SymbolInstance newInst = replace(start, "STATEMENT");
				//TODO clear clicked object
				return newInst;
			} else if (symbol.is("position")) {
				parse("position EOL");
				SymbolInstance newInst = replace(start, "STATEMENT");
				//TODO clear clicked position
				return newInst;
			} else {
				throw new ParseException("Expected: object|position", file, symbol.token.line, symbol.token.col);
			}
		} else if (symbol.is("hit")) {
			parse("hit object EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO clear hit object
			return newInst;
		} else if (symbol.is("player")) {
			parse("player EXPRESSION spell charging EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO clear player EXPRESSION spell charging
			return newInst;
		} else if (symbol.is("dialogue")) {
			parse("dialogue EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO clear dialogue
			return newInst;
		} else if (symbol.is("clipping")) {
			parse("clipping window time EXPRESSION EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO clear clipping window time EXPRESSION
			return newInst;
		} else {
			throw new ParseException("Unexpected token: "+symbol, file, symbol.token.line, symbol.token.col);
		}
	}
	
	private SymbolInstance parseAttach() throws ParseException {
		final int start = it.nextIndex();
		accept("attach");
		SymbolInstance symbol = peek();
		if (symbol.is("reaction")) {
			parse("reaction OBJECT ENUM_REACTION EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO attach reaction OBJECT ENUM_REACTION
			return newInst;
		} else if (symbol.is("music")) {
			parse("music CONST_EXPR to OBJECT EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO attach music CONST_EXPR to OBJECT
			return newInst;
		} else if (symbol.is("3d") || symbol.is("sound")) {
			parseOption("3d");
			parse("sound tag CONST_EXPR");
			symbol = peek();
			if (symbol.is("to")) {
				//TODO default soundbank=?
			} else {
				parseConstExpr(true);
			}
			parse("to OBJECT EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO attach [3d] sound tag CONST_EXPR [CONST_EXPR] to OBJECT
			return newInst;
		} else {
			parseObject(true);
			symbol = peek();
			if (symbol.is("leash")) {
				parse("leash to");
				symbol = peek();
				if (symbol.is("hand")) {
					parse("hand EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO attach OBJECT leash to hand
					return newInst;
				} else {
					parse("OBJECT EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO attach OBJECT leash to OBJECT
					return newInst;
				}
			} else if (symbol.is("to")) {
				accept("to");
				symbol = peek();
				if (symbol.is("game")) {
					parse("game OBJECT for PLAYING_SIDE team EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO attach OBJECT to game OBJECT for PLAYING_SIDE team
					return newInst;
				} else {
					parseObject(true);
					parseOption("as leader");
					accept(TokenType.EOL);
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO attach OBJECT to OBJECT [as leader]
					return newInst;
				}
			} else {
				throw new ParseException("Expected: leash|to", file, symbol.token.line, symbol.token.col);
			}
		}
	}
	
	private SymbolInstance parseToggle() throws ParseException {
		final int start = it.nextIndex();
		parse("toggle player EXPRESSION leash EOL");
		SymbolInstance newInst = replace(start, "STATEMENT");
		//TODO toggle player EXPRESSION leash
		return newInst;
	}
	
	private SymbolInstance parseDetach() throws ParseException {
		final int start = it.nextIndex();
		accept("detach");
		SymbolInstance symbol = peek();
		if (symbol.is("player")) {
			parse("player from OBJECT from PLAYING_SIDE team EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO detach player from OBJECT from PLAYING_SIDE team
			return newInst;
		} else if (symbol.is("sound")) {
			parse("sound tag CONST_EXPR");
			symbol = peek();
			if (symbol.is("from")) {
				//TODO default
			} else {
				parseConstExpr(true);
			}
			parse("from OBJECT EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO detach sound tag CONST_EXPR [CONST_EXPR] from OBJECT
			return newInst;
		} else if (symbol.is("reaction")) {
			parse("detach reaction OBJECT EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO detach reaction OBJECT
			return newInst;
		} else if (symbol.is("music")) {
			parse("music from OBJECT EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO detach music from OBJECT
			return newInst;
		} else if (symbol.is("from")) {
			//TODO default OBJECT
			parse("from OBJECT EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO detach [OBJECT] from OBJECT
			return newInst;
		} else {
			parseObject(true);
			symbol = peek();
			if (symbol.is("leash")) {
				parse("leash EOL");
				SymbolInstance newInst = replace(start, "STATEMENT");
				//TODO detach OBJECT leash
				return newInst;
			} else if (symbol.is("in")) {
				parse("in game OBJECT from PLAYING_SIDE team EOL");
				SymbolInstance newInst = replace(start, "STATEMENT");
				//TODO detach OBJECT in game OBJECT from PLAYING_SIDE team
				return newInst;
			} else if (symbol.is("from")) {
				parse("from OBJECT EOL");
				SymbolInstance newInst = replace(start, "STATEMENT");
				//TODO detach [OBJECT] from OBJECT
				return newInst;
			} else {
				throw new ParseException("Expected: leash|in|from", file, symbol.token.line, symbol.token.col);
			}
		}
	}
	
	private SymbolInstance parseSwap() throws ParseException {
		final int start = it.nextIndex();
		parse("swap creature from OBJECT to OBJECT EOL");
		SymbolInstance newInst = replace(start, "STATEMENT");
		//TODO swap creature from OBJECT to OBJECT
		return newInst;
	}
	
	private SymbolInstance parseQueue() throws ParseException {
		final int start = it.nextIndex();
		accept("queue");
		SymbolInstance symbol = peek();
		if (symbol.is("computer")) {
			parse("computer player EXPRESSION action STRING OBJECT OBJECT EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO queue computer player EXPRESSION action STRING OBJECT OBJECT
			return newInst;
		} else {
			parse("OBJECT fight");
			symbol = peek();
			if (symbol.is("move")) {
				parse("move CONST_EXPR EOL");
				SymbolInstance newInst = replace(start, "STATEMENT");
				//TODO queue OBJECT fight move FIGHT_MOVE
				return newInst;
			} else if (symbol.is("step")) {
				parse("step CONST_EXPR EOL");
				SymbolInstance newInst = replace(start, "STATEMENT");
				//TODO queue OBJECT fight step CONST_EXPR
				return newInst;
			} else if (symbol.is("spell")) {
				parse("spell CONST_EXPR EOL");
				SymbolInstance newInst = replace(start, "STATEMENT");
				//TODO queue OBJECT fight spell CONST_EXPR
				return newInst;
			} else {
				throw new ParseException("Expected: move|step|spell", file, symbol.token.line, symbol.token.col);
			}
		}
	}
	
	private SymbolInstance parsePauseUnpause() throws ParseException {
		final int start = it.nextIndex();
		parse("pause|unpause computer player EXPRESSION EOL");
		SymbolInstance newInst = replace(start, "STATEMENT");
		//TODO pause|unpause computer player EXPRESSION
		return newInst;
	}
	
	private SymbolInstance parseLoad() throws ParseException {
		final int start = it.nextIndex();
		accept("load");
		SymbolInstance symbol = peek();
		if (symbol.is("computer")) {
			parse("computer player EXPRESSION personality STRING EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO load computer player EXPRESSION personality STRING
			return newInst;
		} else if (symbol.is("map")) {
			parse("map STRING EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO load map STRING
			return newInst;
		} else if (symbol.is("my_creature")) {
			parse("my_creature at COORD_EXPR EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO load my_creature at COORD_EXPR
			return newInst;
		} else if (symbol.is("creature")) {
			parse("creature CONST_EXPR STRING player EXPRESSION at COORD_EXPR EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO load creature CONST_EXPR STRING player EXPRESSION at COORD_EXPR
			return newInst;
		} else {
			throw new ParseException("Expected: computer|map|my_creature|creature", file, symbol.token.line, symbol.token.col);
		}
	}
	
	private SymbolInstance parseSave() throws ParseException {
		final int start = it.nextIndex();
		accept("save");
		SymbolInstance symbol = peek();
		if (symbol.is("computer")) {
			parse("computer player EXPRESSION personality STRING EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO save computer player EXPRESSION personality STRING
			return newInst;
		} else if (symbol.is("game")) {
			parse("game in slot EXPRESSION EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO save game in slot EXPRESSION
			return newInst;
		} else {
			throw new ParseException("Expected: computer|game", file, symbol.token.line, symbol.token.col);
		}
	}
	
	private SymbolInstance parseStop() throws ParseException {
		final int start = it.nextIndex();
		accept("stop");
		SymbolInstance symbol = peek();
		if (symbol.is("all")) {
			accept("all");
			symbol = peek();
			if (symbol.is("games")) {
				parse("games for OBJECT EOL");
				SymbolInstance newInst = replace(start, "STATEMENT");
				//TODO stop all games for OBJECT
				return newInst;
			} else if (symbol.is("scripts")) {
				parse("scripts excluding");
				symbol = peek();
				if (symbol.is("files")) {
					parse("files STRING EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO stop all scripts excluding files STRING
					return newInst;
				} else {
					parse("STRING EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO stop all scripts excluding STRING
					return newInst;
				}
			} else if (symbol.is("immersion")) {
				parse("immersion EOL");
				SymbolInstance newInst = replace(start, "STATEMENT");
				//TODO stop all immersion
				return newInst;
			} else {
				throw new ParseException("Expected: games|scripts|immersion", file, symbol.token.line, symbol.token.col);
			}
		} else if (symbol.is("script")) {
			parse("script STRING EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO stop script STRING
			return newInst;
		} else if (symbol.is("scripts")) {
			parse("scripts in");
			symbol = peek();
			if (symbol.is("files")) {
				parse("files STRING EOL");
				SymbolInstance newInst = replace(start, "STATEMENT");
				//TODO stop scripts in files STRING
				return newInst;
			} else if (symbol.is("file")) {
				parse("file STRING excluding STRING EOL");
				SymbolInstance newInst = replace(start, "STATEMENT");
				//TODO stop scripts in file STRING excluding STRING
				return newInst;
			} else {
				throw new ParseException("Expected: files|file", file, symbol.token.line, symbol.token.col);
			}
		} else if (symbol.is("sound")) {
			parse("stop sound CONST_EXPR");
			symbol = peek();
			if (symbol.is(TokenType.EOL)) {
				//TODO default
			} else {
				parseConstExpr(true);
			}
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO stop sound CONST_EXPR [CONST_EXPR]
			return newInst;
		} else if (symbol.is("immersion")) {
			parse("immersion CONST_EXPR EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO stop immersion IMMERSION_EFFECT_TYPE
			return newInst;
		} else if (symbol.is("music")) {
			parse("music EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO stop music
			return newInst;
		} else {
			parse("SPIRIT_TYPE spirit");
			symbol = peek();
			if (symbol.is("pointing")) {
				parse("pointing EOL");
				SymbolInstance newInst = replace(start, "STATEMENT");
				//TODO stop SPIRIT_TYPE spirit pointing
				return newInst;
			} else if (symbol.is("looking")) {
				parse("looking EOL");
				SymbolInstance newInst = replace(start, "STATEMENT");
				//TODO stop SPIRIT_TYPE spirit looking
				return newInst;
			} else {
				throw new ParseException("Expected: pointing|looking", file, symbol.token.line, symbol.token.col);
			}
		}
	}
	
	private SymbolInstance parseStart() throws ParseException {
		final int start = it.nextIndex();
		accept("start");
		SymbolInstance symbol = peek();
		if (symbol.is("say")) {
			accept("say");
			parseOption("extra");
			parse("sound CONST_EXPR");
			symbol = peek();
			if (symbol.is("at")) {
				parse("at COORD_EXPR");
			} else {
				//TODO default position
			}
			accept(TokenType.EOL);
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO start say [extra] sound CONST_EXPR [at COORD_EXPR]
			return newInst;
		} else if (symbol.is("sound")) {
			parse("sound CONST_EXPR");
			symbol = peek();
			if (symbol.is(TokenType.EOL)) {
				//TODO default CONST_EXPR
				//TODO default COORD_EXPR
			} else if (symbol.is("at")) {
				//TODO default CONST_EXPR
				accept("at");
				parseCoordExpr(true);
			} else {
				parseConstExpr(true);
				symbol = peek();
				if (symbol.is("at")) {
					accept("at");
					parseCoordExpr(true);
				} else {
					//TODO default COORD_EXPR
				}
			}
			accept(TokenType.EOL);
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO start sound CONST_EXPR [CONST_EXPR] [at COORD_EXPR]
			return newInst;
		} else if (symbol.is("immersion")) {
			parse("immersion CONST_EXPR EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO start immersion IMMERSION_EFFECT_TYPE
			return newInst;
		} else if (symbol.is("music")) {
			parse("music CONST_EXPR EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO start music CONST_EXPR
			return newInst;
		} else if (symbol.is("hand")) {
			parse("hand demo STRING");
			parseOption("with pause");
			parseOption("without hand modify");
			accept(TokenType.EOL);
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO start hand demo STRING [with pause] [without hand modify]
			return newInst;
		} else if (symbol.is("jc")) {
			parse("jc special CONST_EXPR EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO start jc special CONST_EXPR
			return newInst;
		} else {
			parseObject(true);
			symbol = peek();
			if (symbol.is("with")) {
				parse("with OBJECT as referee EOL");
				SymbolInstance newInst = replace(start, "STATEMENT");
				//TODO start OBJECT with OBJECT as referee
				return newInst;
			} else if (symbol.is("fade")) {
				parse("fade out EOL");
				SymbolInstance newInst = replace(start, "STATEMENT");
				//TODO start OBJECT fade out
				return newInst;
			} else {
				throw new ParseException("Expected: with|fade", file, symbol.token.line, symbol.token.col);
			}
		}
	}
	
	private SymbolInstance parseDisband() throws ParseException {
		final int start = it.nextIndex();
		parse("disband OBJECT EOL");
		SymbolInstance newInst = replace(start, "STATEMENT");
		//TODO disband OBJECT
		return newInst;
	}
	
	private SymbolInstance parsePopulate() throws ParseException {
		final int start = it.nextIndex();
		parse("populate OBJECT with EXPRESSION CONST_EXPR");
		SymbolInstance symbol = peek();
		if (symbol.is(TokenType.EOL)) {
			//TODO default CONST_EXPR
		} else {
			parseConstExpr(true);
		}
		accept(TokenType.EOL);
		SymbolInstance newInst = replace(start, "STATEMENT");
		//TODO populate OBJECT with EXPRESSION CONST_EXPR [CONST_EXPR]
		return newInst;
	}
	
	private SymbolInstance parseAffect() throws ParseException {
		final int start = it.nextIndex();
		parse("affect alignment by EXPRESSION EOL");
		SymbolInstance newInst = replace(start, "STATEMENT");
		//TODO affect alignment by EXPRESSION
		return newInst;
	}
	
	private SymbolInstance parseSnapshot() throws ParseException {
		final int start = it.nextIndex();
		parse("snapshot quest|challenge");
		SymbolInstance symbol = peek();
		if (symbol.is("success")) {
			parse("success EXPRESSION");
		} else {
			//TODO default EXPRESSION
		}
		symbol = peek();
		if (symbol.is("alignment")) {
			parse("alignment EXPRESSION");
		} else {
			//TODO default EXPRESSION
		}
		SymbolInstance script = parse("CONST_EXPR IDENTIFIER");
		String scriptName = script.token.value;
		int argc = 0;
		symbol = peek();
		if (symbol.is("(")) {
			argc = parseParameters();
		}
		accept(TokenType.EOL);
		SymbolInstance newInst = replace(start, "STATEMENT");
		//TODO snapshot quest|challenge [success EXPRESSION] [alignment EXPRESSION] CONST_EXPR SCRIPT[(PARAMETERS)]
		return newInst;
	}
	
	private SymbolInstance parseUpdate() throws ParseException {
		final int start = it.nextIndex();
		parse("update snapshot");
		SymbolInstance symbol = peek();
		if (symbol.is("details")) {
			accept("details");
			symbol = peek();
			if (symbol.is("success")) {
				parse("success EXPRESSION");
			} else {
				//TODO default EXPRESSION
			}
			symbol = peek();
			if (symbol.is("alignment")) {
				parse("alignment EXPRESSION");
			} else {
				//TODO default EXPRESSION
			}
			parseConstExpr(true);
			parseOption("taking picture");
			accept(TokenType.EOL);
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO update snapshot details [success EXPRESSION] [alignment EXPRESSION] CONST_EXPR [taking picture]
			return newInst;
		} else {
			symbol = peek();
			if (symbol.is("success")) {
				parse("success EXPRESSION");
			} else {
				//TODO default EXPRESSION
			}
			symbol = peek();
			if (symbol.is("alignment")) {
				parse("alignment EXPRESSION");
			} else {
				//TODO default EXPRESSION
			}
			SymbolInstance script = parse("CONST_EXPR IDENTIFIER");
			String scriptName = script.token.value;
			int argc = 0;
			symbol = peek();
			if (symbol.is("(")) {
				argc = parseParameters();
			}
			accept(TokenType.EOL);
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO update snapshot [success EXPRESSION] [alignment EXPRESSION] CONST_EXPR SCRIPT[(PARAMETERS)]
			return newInst;
		}
	}
	
	private SymbolInstance parseBuild() throws ParseException {
		final int start = it.nextIndex();
		parse("build building at COORD_EXPR desire EXPRESSION EOL");
		SymbolInstance newInst = replace(start, "STATEMENT");
		//TODO build building at COORD_EXPR desire EXPRESSION
		return newInst;
	}
	
	private SymbolInstance parseRun() throws ParseException {
		final int start = it.nextIndex();
		accept("run");
		SymbolInstance symbol = peek();
		if (symbol.is("script")) {
			SymbolInstance script = parse("script IDENTIFIER");
			String scriptName = script.token.value;
			int argc = 0;
			symbol = peek();
			if (symbol.is("(")) {
				argc = parseParameters();
			}
			accept(TokenType.EOL);
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO run script IDENTIFIER[(PARAMETERS)]
			return newInst;
		} else if (symbol.is("map")) {
			parse("map script line STRING EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO run map script line STRING
			return newInst;
		} else if (symbol.is("background")) {
			SymbolInstance script = parse("background script IDENTIFIER");
			String scriptName = script.token.value;
			int argc = 0;
			symbol = peek();
			if (symbol.is("(")) {
				argc = parseParameters();
			}
			accept(TokenType.EOL);
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO run background script IDENTIFIER[(PARAMETERS)]
			return newInst;
		} else {
			parse("CONST_EXPR developer function EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO run CONST_EXPR developer function
			return newInst;
		}
	}
	
	private SymbolInstance parseWait() throws ParseException {
		final int start = it.nextIndex();
		accept("wait");
		SymbolInstance symbol = peek();
		if (symbol.is("until")) {
			next();	//skip
		}
		parse("CONDITION EOL");
		SymbolInstance newInst = replace(start, "STATEMENT");
		//TODO wait until CONDITION
		return newInst;
	}
	
	private SymbolInstance parseEnterExit() throws ParseException {
		final int start = it.nextIndex();
		parse("enter|exit temple EOL");
		SymbolInstance newInst = replace(start, "STATEMENT");
		//TODO enter|exit temple
		return newInst;
	}
	
	private SymbolInstance parseRestart() throws ParseException {
		final int start = it.nextIndex();
		accept("restart");
		SymbolInstance symbol = peek();
		if (symbol.is("music")) {
			parse("music on OBJECT EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO restart music on OBJECT
			return newInst;
		} else {
			parse("OBJECT EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO restart OBJECT
			return newInst;
		}
	}
	
	private SymbolInstance parseState() throws ParseException {
		final int start = it.nextIndex();
		parse("state OBJECT CONST_EXPR EOL position COORD_EXPR EOL");
		
		parse("float EXPRESSION EOL");
		
		parse("ulong EXPRESSION , EXPRESSION EOL");
		
		SymbolInstance newInst = replace(start, "STATEMENT");
		//TODO state OBJECT CONST_EXPR position COORD_EXPR float EXPRESSION ulong EXPRESSION, EXPRESSION
		return newInst;
	}
	
	private SymbolInstance parseMake() throws ParseException {
		final int start = it.nextIndex();
		accept("make");
		SymbolInstance symbol = peek(1);
		if (symbol.is("spirit")) {
			parse("SPIRIT_TYPE spirit");
			symbol = peek();
			if (symbol.is("point")) {
				accept("point");
				if (symbol.is("to")) {
					parse("to OBJECT");
					parseOption("in world");
					accept(TokenType.EOL);
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO make SPIRIT_TYPE spirit point to OBJECT [in world]
					return newInst;
				} else if (symbol.is("at")) {
					parse("at COORD_EXPR EOL");
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO make SPIRIT_TYPE spirit point at COORD_EXPR
					return newInst;
				} else {
					throw new ParseException("Expected: to|at", file, symbol.token.line, symbol.token.col);
				}
			} else if (symbol.is("play")) {
				parse("play across EXPRESSION down EXPRESSION CONST_EXPR");
				symbol = peek();
				if (symbol.is("speed")) {
					parse("speed EXPRESSION");
				} else {
					//TODO default EXPRESSION
				}
				accept(TokenType.EOL);
				SymbolInstance newInst = replace(start, "STATEMENT");
				//TODO make SPIRIT_TYPE spirit play across EXPRESSION down EXPRESSION CONST_EXPR [speed EXPRESSION]
				return newInst;
			} else if (symbol.is("cling")) {
				parse("cling across EXPRESSION down EXPRESSION EOL");
				SymbolInstance newInst = replace(start, "STATEMENT");
				//TODO make SPIRIT_TYPE spirit cling across EXPRESSION down EXPRESSION
				return newInst;
			} else if (symbol.is("fly")) {
				parse("fly across EXPRESSION down EXPRESSION EOL");
				SymbolInstance newInst = replace(start, "STATEMENT");
				//TODO make SPIRIT_TYPE spirit fly across EXPRESSION down EXPRESSION
				return newInst;
			} else if (symbol.is("look")) {
				parse("look at");
				symbol = parseObject(false);
				if (symbol != null) {
					accept(TokenType.EOL);
					SymbolInstance newInst = replace(start, "STATEMENT");
					//TODO make SPIRIT_TYPE spirit look at OBJECT
					return newInst;
				} else {
					symbol = parseCoordExpr(false);
					if (symbol != null) {
						accept(TokenType.EOL);
						SymbolInstance newInst = replace(start, "STATEMENT");
						//TODO make SPIRIT_TYPE spirit look at COORD_EXPR
						return newInst;
					} else {
						symbol = peek();
						throw new ParseException("Expected: OBJECT|COORD_EXPR", file, symbol.token.line, symbol.token.col);
					}
				}
			} else if (symbol.is("appear")) {
				parse("appear EOL");
				SymbolInstance newInst = replace(start, "STATEMENT");
				//TODO make SPIRIT_TYPE spirit appear
				return newInst;
			} else {
				throw new ParseException("Expected: point|play|cling|fly|look|appear", file, symbol.token.line, symbol.token.col);
			}
		} else {
			parse("OBJECT dance CONST_EXPR around COORD_EXPR time EXPRESSION EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO make OBJECT dance CONST_EXPR around COORD_EXPR time EXPRESSION
			return newInst;
		}
	}
	
	private SymbolInstance parseEject() throws ParseException {
		final int start = it.nextIndex();
		parse("eject SPIRIT_TYPE spirit EOL");
		SymbolInstance newInst = replace(start, "STATEMENT");
		//TODO eject SPIRIT_TYPE spirit
		return newInst;
	}
	
	private SymbolInstance parseDisappear() throws ParseException {
		final int start = it.nextIndex();
		parse("disappear SPIRIT_TYPE spirit EOL");
		SymbolInstance newInst = replace(start, "STATEMENT");
		//TODO disappear SPIRIT_TYPE spirit
		return newInst;
	}
	
	private SymbolInstance parseSend() throws ParseException {
		final int start = it.nextIndex();
		parse("send SPIRIT_TYPE spirit home EOL");
		SymbolInstance newInst = replace(start, "STATEMENT");
		//TODO send SPIRIT_TYPE spirit home
		return newInst;
	}
	
	private SymbolInstance parseSay() throws ParseException {
		//This syntax is ambiguous for a top-down parser, so we do some look ahead and swap as workaround
		final int start = it.nextIndex();
		accept("say");
		SymbolInstance symbol = peek();
		if (symbol.is("sound")) {
			parse("sound CONST_EXPR playing EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO say sound CONST_EXPR playing
			return newInst;
		} else if (symbol.is(TokenType.STRING) && peek(1).is("with") && peek(2).is("number")) {
			parse("STRING with number EXPRESSION EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO say STRING with number EXPRESSION
			return newInst;
		} else if (symbol.is("single")) {
			parseOption("single line");
			symbol = peek();
			if (symbol.is(TokenType.STRING)) {
				parseString();
				parseOption("with interaction");
				accept(TokenType.EOL);
				SymbolInstance newInst = replace(start, "STATEMENT");
				//TODO say [single line] STRING [with interaction]
				return newInst;
			} else {
				parseConstExpr(true);
				parseOption("with interaction");
				accept(TokenType.EOL);
				SymbolInstance newInst = replace(start, "STATEMENT");
				//TODO say [single line] CONST_EXPR [with interaction]
				return newInst;
			}
		} else if (symbol.is(TokenType.STRING)) {
			parseOption("single line");
			parseString();
			parseOption("with interaction");
			accept(TokenType.EOL);
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO say [single line] STRING [with interaction]
			return newInst;
		} else {
			parseConstExpr(true);
			if (peek().is("with") && peek(1).is("number")) {
				parse("with number EXPRESSION EOL");
				SymbolInstance newInst = replace(start, "STATEMENT");
				//TODO say CONST_EXPR with number EXPRESSION
				return newInst;
			} else {
				//TODO push [single line]=false and swap it with previous CONST_EXPR
				parseOption("with interaction");
				accept(TokenType.EOL);
				SymbolInstance newInst = replace(start, "STATEMENT");
				//TODO say [single line] CONST_EXPR [with interaction]
				return newInst;
			}
		}
	}
	
	private SymbolInstance parseDraw() throws ParseException {
		final int start = it.nextIndex();
		parse("draw text");
		SymbolInstance symbol = peek();
		if (symbol.is(TokenType.STRING)) {
			parse("STRING across EXPRESSION down EXPRESSION width EXPRESSION height EXPRESSION size EXPRESSION fade in time EXPRESSION second|seconds EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO draw text STRING across EXPRESSION down EXPRESSION width EXPRESSION height EXPRESSION size EXPRESSION fade in time EXPRESSION second|seconds
			return newInst;
		} else {
			parse("CONST_EXPR across EXPRESSION down EXPRESSION width EXPRESSION height EXPRESSION size EXPRESSION fade in time EXPRESSION second|seconds EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO draw text CONST_EXPR across EXPRESSION down EXPRESSION width EXPRESSION height EXPRESSION size EXPRESSION fade in time EXPRESSION second|seconds
			return newInst;
		}
	}
	
	private SymbolInstance parseFade() throws ParseException {
		final int start = it.nextIndex();
		accept("fade");
		SymbolInstance symbol = peek();
		if (symbol.is("all")) {
			parse("all draw text time EXPRESSION second|seconds EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO fade all draw text time EXPRESSION second|seconds
			return newInst;
		} else if (symbol.is("ready")) {
			parse("ready EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO fade ready
			return newInst;
		} else {
			throw new ParseException("Expected: all|ready", file, symbol.token.line, symbol.token.col);
		}
	}
	
	private SymbolInstance parseStore() throws ParseException {
		final int start = it.nextIndex();
		parse("store camera details EOL");
		SymbolInstance newInst = replace(start, "STATEMENT");
		//TODO store camera details
		return newInst;
	}
	
	private SymbolInstance parseRestore() throws ParseException {
		final int start = it.nextIndex();
		parse("restore camera details EOL");
		SymbolInstance newInst = replace(start, "STATEMENT");
		//TODO restore camera details
		return newInst;
	}
	
	private SymbolInstance parseReset() throws ParseException {
		final int start = it.nextIndex();
		parse("reset camera lens EOL");
		SymbolInstance newInst = replace(start, "STATEMENT");
		//TODO reset camera lens
		return newInst;
	}
	
	private SymbolInstance parseCamera() throws ParseException {
		final int start = it.nextIndex();
		accept("camera");
		SymbolInstance symbol = peek();
		if (symbol.is("follow")) {
			parse("follow OBJECT distance EXPRESSION EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO camera follow OBJECT distance EXPRESSION
			return newInst;
		} else if (symbol.is("path")) {
			parse("path CONST_EXPR EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO camera path CONST_EXPR
			return newInst;
		} else if (symbol.is("ready")) {
			parse("ready EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO camera ready
			return newInst;
		} else if (symbol.is("not")) {
			parse("not ready EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO camera not ready
			return newInst;
		} else if (symbol.is("position")) {
			parse("position EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO camera position
			return newInst;
		} else if (symbol.is("focus")) {
			parse("focus EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO camera focus
			return newInst;
		} else {
			parse("CONST_EXPR EOL");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO camera CONST_EXPR
			return newInst;
		}
	}
	
	private SymbolInstance parseShake() throws ParseException {
		final int start = it.nextIndex();
		parse("shake camera at COORD_EXPR radius EXPRESSION amplitude EXPRESSION time EXPRESSION EOL");
		SymbolInstance newInst = replace(start, "STATEMENT");
		//TODO shake camera at COORD_EXPR radius EXPRESSION amplitude EXPRESSION time EXPRESSION
		return newInst;
	}
	
	private SymbolInstance parseAssignment() throws ParseException {
		final int start = it.nextIndex();
		SymbolInstance symbol = peek(1);
		if (symbol.is("of")) {
			symbol = parse("IDENTIFIER of");
			String constantName = symbol.token.value;
			symbol = parse("IDENTIFIER =");
			String objVarName = symbol.token.value;
			symbol = parseExpression(false);
			if (symbol == null) {
				symbol = parseObject(false);
				if (symbol == null) {
					symbol = peek();
					throw new ParseException("Expected EXPRESSION or OBJECT", file, symbol.token.line, symbol.token.col);
				}
			}
			accept(TokenType.EOL);
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO PROPERTY of OBJECT = EXPRESSION
			return newInst;
		} else if (symbol.is("=")) {
			symbol = parse("IDENTIFIER =");
			String var = symbol.token.value;
			pushf(var);
			popi();
			symbol = parseExpression(false);
			if (symbol == null) {
				symbol = parseObject(false);
				if (symbol == null) {
					symbol = peek();
					throw new ParseException("Expected: EXPRESSION|OBJECT", file, symbol.token.line, symbol.token.col);
				}
			}
			accept(TokenType.EOL);
			//VARIABLE = EXPRESSION
			SymbolInstance newInst = replace(start, "STATEMENT");
			popf(var);
			return newInst;
		} else if (symbol.is("+=")) {
			symbol = parse("IDENTIFIER += EXPRESSION EOL");
			String varName = symbol.token.value;
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO VARIABLE += EXPRESSION
			return newInst;
		} else if (symbol.is("-=")) {
			symbol = parse("IDENTIFIER -= EXPRESSION EOL");
			String varName = symbol.token.value;
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO VARIABLE -= EXPRESSION
			return newInst;
		} else if (symbol.is("*=")) {
			symbol = parse("IDENTIFIER *= EXPRESSION EOL");
			String varName = symbol.token.value;
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO VARIABLE *= EXPRESSION
			return newInst;
		} else if (symbol.is("/=")) {
			symbol = parse("IDENTIFIER /= EXPRESSION EOL");
			String varName = symbol.token.value;
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO VARIABLE /= EXPRESSION
			return newInst;
		} else if (symbol.is("++")) {
			symbol = parse("IDENTIFIER ++ EOL");
			String varName = symbol.token.value;
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO VARIABLE++
			return newInst;
		} else if (symbol.is("--")) {
			symbol = parse("IDENTIFIER -- EOL");
			String varName = symbol.token.value;
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO VARIABLE--
			return newInst;
		} else {
			throw new ParseException("Expected: =|+=|-=|*=|/=|++|--", file, symbol.token.line, symbol.token.col);
		}
	}
	
	private SymbolInstance parseIf() throws ParseException {
		final int start = it.nextIndex();
		//IF_ELSIF_ELSE
		List<Instruction> jumps_lblEndIf = new LinkedList<>();
		parse("if CONDITION EOL");
		Instruction jz_lblNextCond = jz();
		parseStatements();
		SymbolInstance symbol = peek();
		while (symbol.is("elsif")) {
			jumps_lblEndIf.add(jmp());
			jz_lblNextCond.intVal = getIp();
			parse("elsif CONDITION EOL");
			jz_lblNextCond = jz();
			parseStatements();
			symbol = peek();
		}
		if (symbol.is("else")) {
			jumps_lblEndIf.add(jmp());
			jz_lblNextCond.intVal = getIp();
			parse("else EOL");
			parseStatements();
		}
		parse("end if EOL");
		int lblEndIf = getIp();
		jz_lblNextCond.intVal = lblEndIf;
		for (Instruction jump : jumps_lblEndIf) {
			jump.intVal = lblEndIf;
		}
		SymbolInstance newInst = replace(start, "IF_ELSIF_ELSE");
		return newInst;
	}
	
	private SymbolInstance parseWhile() throws ParseException {
		final int start = it.nextIndex();
		//WHILE
		Instruction except_lblExceptionHandler = except();
		int lblStartWhile = getIp();
		parse("while CONDITION EOL");
		Instruction jz_lblEndWhile = jz();
		//STATEMENTS
		parseStatements();
		jmp(lblStartWhile);
		int lblEndWhile = getIp();
		jz_lblEndWhile.intVal = lblEndWhile;
		endexcept();
		Instruction jmp_lblEndExcept = jmp();
		//EXCEPTIONS
		int lblExceptionHandler = getIp();
		except_lblExceptionHandler.intVal = lblExceptionHandler;
		parseExceptions();
		parse("end while EOL");
		iterexcept();
		int lblEndExcept = getIp();
		jmp_lblEndExcept.intVal = lblEndExcept;
		SymbolInstance newInst = replace(start, "WHILE");
		return newInst;
	}
	
	private SymbolInstance parseBegin() throws ParseException {
		final int start = it.nextIndex();
		accept("begin");
		SymbolInstance symbol = peek();
		if (symbol.is("loop")) {
			parse("loop EOL");
			parseStatements();
			parseExceptions();
			parse("end loop EOL");
			SymbolInstance newInst = replace(start, "LOOP");
			//TODO LOOP
			return newInst;
		} else if (symbol.is("cinema")) {
			parse("cinema EOL");
			parseStatements();
			parseExceptions();
			parse("end cinema EOL");
			SymbolInstance newInst = replace(start, "begin cinema");
			//TODO begin cinema STATEMENTS EXCEPTIONS end cinema
			return newInst;
		} else if (symbol.is("camera")) {
			parse("camera EOL");
			parseStatements();
			parseExceptions();
			parse("end camera EOL");
			SymbolInstance newInst = replace(start, "begin camera");
			//TODO begin camera STATEMENTS EXCEPTIONS end camera
			return newInst;
		} else if (symbol.is("dialogue")) {
			parse("dialogue EOL");
			parseStatements();
			parseExceptions();
			parse("end dialogue EOL");
			SymbolInstance newInst = replace(start, "begin dialogue");
			//TODO begin dialogue STATEMENTS EXCEPTIONS end dialogue
			return newInst;
		} else if (symbol.is("known")) {
			symbol = peek();
			if (symbol.is("dialogue")) {
				parse("dialogue EOL");
				parseStatements();
				parseExceptions();
				parse("end dialogue EOL");
				SymbolInstance newInst = replace(start, "begin known dialogue");
				//TODO begin known dialogue STATEMENTS EXCEPTIONS end dialogue
				return newInst;
			} else if (symbol.is("cinema")) {
				parse("cinema EOL");
				parseStatements();
				parseExceptions();
				parse("end cinema EOL");
				SymbolInstance newInst = replace(start, "begin known cinema");
				//TODO begin known cinema STATEMENTS EXCEPTIONS end cinema
				return newInst;
			} else {
				throw new ParseException("Expected: dialogue|cinema", file, symbol.token.line, symbol.token.col);
			}
		} else if (symbol.is("dual")) {
			parse("dual camera to OBJECT OBJECT EOL");
			parseStatements();
			parseExceptions();
			parse("end dual camera EOL");
			SymbolInstance newInst = replace(start, "begin dual camera");
			//TODO begin dual camera to OBJECT OBJECT EOL STATEMENTS EXCEPTIONS EOL end dual camera
			return newInst;
		} else {
			throw new ParseException("Expected: loop|cinema|camera|dialogue|known|dual", file, symbol.token.line, symbol.token.col);
		}
	}
	
	private SymbolInstance parseExceptions() throws ParseException {
		final int start = it.nextIndex();
		SymbolInstance symbol = peek();
		while (symbol.is("when") || symbol.is("until")) {
			parseException();
			symbol = peek();
		}
		SymbolInstance newInst = replace(start, "EXCEPTIONS");
		//TODO EXCEPTIONS
		return newInst;
	}
	
	private SymbolInstance parseException() throws ParseException {
		final int start = it.nextIndex();
		SymbolInstance symbol = peek();
		if (symbol.is("when")) {
			parse("when CONDITION EOL");
			parseStatements();
			SymbolInstance newInst = replace(start, "WHEN");
			//TODO EXCEPTION
			return newInst;
		} else if (symbol.is("until")) {
			parse("until CONDITION EOL");
			SymbolInstance newInst = replace(start, "UNTIL");
			//TODO EXCEPTION
			return newInst;
		} else {
			throw new ParseException("Expected: when|until", file, symbol.token.line, symbol.token.col);
		}
	}
	
	private SymbolInstance parseExpression(boolean fail) throws ParseException {
		final int start = it.nextIndex();
		SymbolInstance symbol = peek();
		SymbolInstance newSym = parseExpression1();
		while (newSym != null && newSym != symbol) {
			symbol = newSym;
			it.previous();
			newSym = parseExpression1();
		}
		seek(start);
		symbol = peek();
		if ("EXPRESSION".equals(symbol.symbol.keyword)) {
			next();
			return symbol;
		}
		if (fail) {
			symbol = peek();
			throw new ParseException("Unexpected token: "+symbol, file, symbol.token.line, symbol.token.col);
		} else {
			seek(start);
			return null;
		}
	}
	
	private SymbolInstance parseExpression1() throws ParseException {
		final int start = it.nextIndex();
		SymbolInstance symbol = peek();
		if ("EXPRESSION".equals(symbol.symbol.keyword)) {
			next();
			symbol = next();
			if (symbol.is("*")) {
				parseExpression1();
				//EXPRESSION * EXPRESSION
				mul();
				SymbolInstance newInst = replace(start, "EXPRESSION");
				trace(newInst.toStringBlocks(), 0);
				return newInst;
			} else if (symbol.is("/")) {
				parseExpression1();
				//EXPRESSION / EXPRESSION
				div();
				SymbolInstance newInst = replace(start, "EXPRESSION");
				trace(newInst.toStringBlocks(), 0);
				return newInst;
			} else if (symbol.is("%")) {
				parseExpression1();
				//EXPRESSION % EXPRESSION
				mod();
				SymbolInstance newInst = replace(start, "EXPRESSION");
				return newInst;
			} else if (symbol.is("+")) {
				parseExpression(true);
				//EXPRESSION + EXPRESSION
				addf();
				SymbolInstance newInst = replace(start, "EXPRESSION");
				trace(newInst.toStringBlocks(), 0);
				return newInst;
			} else if (symbol.is("-")) {
				parseExpression(true);
				//EXPRESSION - EXPRESSION
				subf();
				SymbolInstance newInst = replace(start, "EXPRESSION");
				trace(newInst.toStringBlocks(), 0);
				return newInst;
			} else if (symbol.is(TokenType.EOL)) {
				seek(start);
				return peek();
			}
		} else if (symbol.is("remove")) {
			parse("remove resource CONST_EXPR EXPRESSION from OBJECT");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO remove resource CONST_EXPR EXPRESSION from OBJECT
			return newInst;
		} else if (symbol.is("add")) {
			parse("add resource CONST_EXPR EXPRESSION from OBJECT");
			SymbolInstance newInst = replace(start, "STATEMENT");
			//TODO add resource CONST_EXPR EXPRESSION from OBJECT
			return newInst;
		} else if (symbol.is("alignment")) {
			parse("alignment of player");
			SymbolInstance newInst = replace(start, "EXPRESSION");
			//TODO alignment of player
			return newInst;
		} else if (symbol.is("raw") || symbol.is("influence")) {
			parseOption("raw");
			parse("influence at COORD_EXPR");
			SymbolInstance newInst = replace(start, "EXPRESSION");
			//TODO [raw] influence at COORD_EXPR
			return newInst;
		} else if (symbol.is("get")) {
			accept("get");
			symbol = peek();
			if (symbol.is("player")) {
				parse("player EXPRESSION");
				symbol = peek();
				if (symbol.is("influence")) {
					parse("influence at COORD_EXPR");
					SymbolInstance newInst = replace(start, "EXPRESSION");
					//TODO get player EXPRESSION influence at COORD_EXPR
					return newInst;
				} else if (symbol.is("town")) {
					parse("town total");
					SymbolInstance newInst = replace(start, "EXPRESSION");
					//TODO get player EXPRESSION town total
					return newInst;
				} else if (symbol.is("time")) {
					parse("time since last spell cast");
					SymbolInstance newInst = replace(start, "EXPRESSION");
					//TODO get player EXPRESSION time since last spell cast
					return newInst;
				} else if (symbol.is("ally")) {
					parse("ally percentage with player EXPRESSION");
					SymbolInstance newInst = replace(start, "EXPRESSION");
					//TODO get player EXPRESSION ally percentage with player EXPRESSION
					return newInst;
				}
			} else if (symbol.is("time")) {
				parse("time since");
				symbol = peek();
				if (symbol.is("player")) {
					parse("player EXPRESSION attacked OBJECT");
					SymbolInstance newInst = replace(start, "EXPRESSION");
					//TODO get time since player EXPRESSION attacked OBJECT
					return newInst;
				} else {
					parse("CONST_EXPR event");
					SymbolInstance newInst = replace(start, "EXPRESSION");
					//TODO get time since CONST_EXPR event
					return newInst;
				}
			} else if (symbol.is("resource")) {
				parse("resource CONST_EXPR in OBJECT");
				SymbolInstance newInst = replace(start, "EXPRESSION");
				//TODO get resource CONST_EXPR in OBJECT
				return newInst;
			} else if (symbol.is("number")) {
				parse("number of CONST_EXPR for OBJECT");
				SymbolInstance newInst = replace(start, "EXPRESSION");
				//TODO get number of CONST_EXPR for OBJECT
				return newInst;
			} else if (symbol.is("inclusion")) {
				parse("inclusion distance");
				SymbolInstance newInst = replace(start, "EXPRESSION");
				//TODO get inclusion distance
				return newInst;
			} else if (symbol.is("slowest")) {
				parse("slowest speed in OBJECT");
				SymbolInstance newInst = replace(start, "EXPRESSION");
				//TODO get slowest speed in OBJECT
				return newInst;
			} else if (symbol.is("distance")) {
				parse("distance from COORD_EXPR to COORD_EXPR");
				SymbolInstance newInst = replace(start, "EXPRESSION");
				//TODO get distance from COORD_EXPR to COORD_EXPR
				return newInst;
			} else if (symbol.is("mana")) {
				parse("mana for spell CONST_EXPR");
				SymbolInstance newInst = replace(start, "EXPRESSION");
				//TODO get mana for spell CONST_EXPR
				return newInst;
			} else if (symbol.is("building")) {
				parse("building and villager health total in OBJECT");
				SymbolInstance newInst = replace(start, "EXPRESSION");
				//TODO get building and villager health total in OBJECT
				return newInst;
			} else if (symbol.is("size")) {
				parse("size of OBJECT PLAYING_SIDE team");
				SymbolInstance newInst = replace(start, "EXPRESSION");
				//TODO get size of OBJECT PLAYING_SIDE team
				return newInst;
			} else if (symbol.is("worship")) {
				parse("worship deaths in OBJECT");
				SymbolInstance newInst = replace(start, "EXPRESSION");
				//TODO get worship deaths in OBJECT
				return newInst;
			} else if (symbol.is("computer")) {
				parse("computer player EXPRESSION attitude to player EXPRESSION");
				SymbolInstance newInst = replace(start, "EXPRESSION");
				//TODO get computer player EXPRESSION attitude to player EXPRESSION
				return newInst;
			} else if (symbol.is("moon")) {
				parse("moon percentage");
				SymbolInstance newInst = replace(start, "EXPRESSION");
				//TODO get moon percentage
				return newInst;
			} else if (symbol.is("game")) {
				parse("game time");
				SymbolInstance newInst = replace(start, "EXPRESSION");
				//TODO get game time
				return newInst;
			} else if (symbol.is("real")) {
				accept("real");
				symbol = peek();
				if (symbol.is("time")) {
					accept("time");
					SymbolInstance newInst = replace(start, "EXPRESSION");
					//TODO get real time
					return newInst;
				} else if (symbol.is("day")) {
					accept("day");
					SymbolInstance newInst = replace(start, "EXPRESSION");
					//TODO get real day
					return newInst;
				} else if (symbol.is("weekday")) {
					accept("weekday");
					SymbolInstance newInst = replace(start, "EXPRESSION");
					//TODO get real weekday
					return newInst;
				} else if (symbol.is("month")) {
					accept("month");
					SymbolInstance newInst = replace(start, "EXPRESSION");
					//TODO get real month
					return newInst;
				} else if (symbol.is("year")) {
					accept("year");
					SymbolInstance newInst = replace(start, "EXPRESSION");
					//TODO get real year
					return newInst;
				}
			} else {
				final int checkpoint = it.nextIndex();
				final int checkpointIp = getIp();
				symbol = parseObject(false);
				if (symbol != null) {
					symbol = peek();
					if (symbol.is("music")) {
						parse("music distance");
						SymbolInstance newInst = replace(start, "EXPRESSION");
						//TODO get OBJECT music distance
						return newInst;
					} else if (symbol.is("interaction")) {
						parse("interaction magnitude");
						SymbolInstance newInst = replace(start, "EXPRESSION");
						//TODO get OBJECT interaction magnitude
						return newInst;
					} else if (symbol.is("time")) {
						accept("time");
						symbol = peek();
						if (symbol.is("remaining")) {
							accept("remaining");
							SymbolInstance newInst = replace(start, "EXPRESSION");
							//TODO get OBJECT time remaining
							return newInst;
						} else if (symbol.is("since")) {
							parse("since set");
							SymbolInstance newInst = replace(start, "EXPRESSION");
							//TODO get OBJECT time since set
							return newInst;
						}
					} else if (symbol.is("fight")) {
						parse("fight queue hits");
						SymbolInstance newInst = replace(start, "EXPRESSION");
						//TODO get OBJECT fight queue hits
						return newInst;
					} else if (symbol.is("walk")) {
						parse("walk path percentage");
						SymbolInstance newInst = replace(start, "EXPRESSION");
						//TODO get OBJECT walk path percentage
						return newInst;
					} else if (symbol.is("mana")) {
						parse("mana total");
						SymbolInstance newInst = replace(start, "EXPRESSION");
						//TODO get OBJECT mana total
						return newInst;
					} else if (symbol.is("played")) {
						parse("played percentage");
						SymbolInstance newInst = replace(start, "EXPRESSION");
						//TODO get OBJECT played percentage
						return newInst;
					} else if (symbol.is("belief")) {
						parse("belief for player EXPRESSION");
						SymbolInstance newInst = replace(start, "EXPRESSION");
						//TODO get OBJECT belief for player EXPRESSION
						return newInst;
					} else if (symbol.is("help")) {
						accept("help");
						SymbolInstance newInst = replace(start, "EXPRESSION");
						//TODO get OBJECT help
						return newInst;
					} else if (symbol.is("first")) {
						parse("first help");
						SymbolInstance newInst = replace(start, "EXPRESSION");
						//TODO get OBJECT first help
						return newInst;
					} else if (symbol.is("last")) {
						parse("last help");
						SymbolInstance newInst = replace(start, "EXPRESSION");
						//TODO get OBJECT last help
						return newInst;
					} else if (symbol.is("fade")) {
						accept("fade");
						SymbolInstance newInst = replace(start, "EXPRESSION");
						//TODO get OBJECT fade
						return newInst;
					} else if (symbol.is("info")) {
						parse("info bits");
						SymbolInstance newInst = replace(start, "EXPRESSION");
						//TODO get OBJECT info bits
						return newInst;
					} else if (symbol.is("desire")) {
						parse("desire CONST_EXPR");
						SymbolInstance newInst = replace(start, "EXPRESSION");
						//TODO get OBJECT desire CONST_EXPR
						return newInst;
					} else if (symbol.is("sacrifice")) {
						parse("sacrifice total");
						SymbolInstance newInst = replace(start, "EXPRESSION");
						//TODO get OBJECT sacrifice total
						return newInst;
					}
					revert(checkpoint, checkpointIp);
				}
				symbol = parseConstExpr(false);
				if (symbol != null) {
					symbol = peek();
					if (symbol.is("music")) {
						parse("music distance");
						SymbolInstance newInst = replace(start, "EXPRESSION");
						//TODO get CONST_EXPR music distance
						return newInst;
					} else if (symbol.is("events")) {
						parse("events per second");
						SymbolInstance newInst = replace(start, "EXPRESSION");
						//TODO get CONSTANT events per second
						return newInst;
					} else if (symbol.is("total")) {
						parse("total events|events");
						SymbolInstance newInst = replace(start, "EXPRESSION");
						//TODO get CONSTANT total events|events
						return newInst;
					}
					revert(checkpoint, checkpointIp);
				}
			}
		} else if (symbol.is("land")) {
			parse("land height at COORD_EXPR");
			SymbolInstance newInst = replace(start, "EXPRESSION");
			//TODO land height at COORD_EXPR
			return newInst;
		} else if (symbol.is("time")) {
			accept("time");
			SymbolInstance newInst = replace(start, "EXPRESSION");
			//TODO time
			return newInst;
		} else if (symbol.is("number")) {
			accept("number");
			symbol = peek();
			if (symbol.is("from")) {
				parse("from EXPRESSION to EXPRESSION");
				//number from EXPRESSION to EXPRESSION
				sys(RANDOM);
				SymbolInstance newInst = replace(start, "EXPRESSION");
				return newInst;
			} else if (symbol.is("of")) {
				accept("of");
				symbol = peek();
				if (symbol.is("mouse")) {
					parse("mouse buttons");
					SymbolInstance newInst = replace(start, "EXPRESSION");
					//TODO number of mouse buttons
					return newInst;
				} else if (symbol.is("times")) {
					parse("times action CONST_EXPR by OBJECT");
					SymbolInstance newInst = replace(start, "EXPRESSION");
					//TODO number of times action CONST_EXPR by OBJECT
					return newInst;
				}
			}
		} else if (symbol.is("size")) {
			parse("size of OBJECT");
			SymbolInstance newInst = replace(start, "EXPRESSION");
			//TODO size of OBJECT
			return newInst;
		} else if (symbol.is("adult")) {
			accept("adult");
			symbol = peek();
			if (symbol.is("size")) {
				parse("size of OBJECT");
				SymbolInstance newInst = replace(start, "EXPRESSION");
				//TODO adult size of OBJECT
				return newInst;
			} else if (symbol.is("capacity")) {
				parse("capacity of OBJECT");
				SymbolInstance newInst = replace(start, "EXPRESSION");
				//TODO adult capacity of OBJECT
				return newInst;
			}
		} else if (symbol.is("capacity")) {
			parse("capacity of OBJECT");
			SymbolInstance newInst = replace(start, "EXPRESSION");
			//TODO capacity of OBJECT
			return newInst;
		} else if (symbol.is("poisoned")) {
			parse("poisoned size of OBJECT");
			SymbolInstance newInst = replace(start, "EXPRESSION");
			//TODO poisoned size of OBJECT
			return newInst;
		} else if (symbol.is("square")) {
			parse("square root EXPRESSION");
			SymbolInstance newInst = replace(start, "EXPRESSION");
			//TODO square root EXPRESSION
			return newInst;
		} else if (symbol.is("-")) {
			parse("- EXPRESSION");
			//-EXPRESSION
			neg();
			SymbolInstance newInst = replace(start, "EXPRESSION");
			return newInst;
		} else if (symbol.is("variable")) {
			parse("variable CONST_EXPR");
			//variable CONST_EXPR
			castf();
			SymbolInstance newInst = replace(start, "EXPRESSION");
			return newInst;
		} else if (symbol.is("(")) {
			parse("( EXPRESSION )");
			//(EXPRESSION)
			SymbolInstance newInst = replace(start, "EXPRESSION");
			return newInst;
		} else if (symbol.is(TokenType.NUMBER)) {
			symbol = accept(TokenType.NUMBER);
			float val = symbol.token.floatVal();
			//NUMBER
			pushf(val);
			SymbolInstance newInst = replace(start, "EXPRESSION");
			return newInst;
		} else if (symbol.is(TokenType.IDENTIFIER)) {
			SymbolInstance id1 = accept(TokenType.IDENTIFIER);
			symbol = peek();
			if (symbol.is("of")) {
				SymbolInstance id2 = parse("of IDENTIFIER");
				//CONSTANT of OBJECT
				int property = getConstant(id1.token.value);
				String object = id2.token.value;
				pushi(property);
				pushf(object);
				sys(GET_PROPERTY);
				SymbolInstance newInst = replace(start, "EXPRESSION");
				return newInst;
			} else {
				//IDENTIFIER
				String var = id1.token.value;
				pushf(var);
				SymbolInstance newInst = replace(start, "EXPRESSION");
				return newInst;
			}
		}
		seek(start);
		return null;
	}
	
	private SymbolInstance parseCondition(boolean fail) throws ParseException {
		final int start = it.nextIndex();
		SymbolInstance symbol = peek();
		SymbolInstance newSym = parseCondition1();
		while (newSym != null && newSym != symbol) {
			symbol = newSym;
			it.previous();
			newSym = parseCondition1();
		}
		seek(start);
		symbol = peek();
		if ("CONDITION".equals(symbol.symbol.keyword)) {
			next();
			return symbol;
		}
		if (fail) {
			symbol = peek();
			throw new ParseException("Unexpected token: "+symbol, file, symbol.token.line, symbol.token.col);
		} else {
			seek(start);
			return null;
		}
	}
	
	private SymbolInstance parseCondition1() throws ParseException {
		final int start = it.nextIndex();
		SymbolInstance symbol = peek();
		if ("CONDITION".equals(symbol.symbol.keyword)) {
			next();
			symbol = next();
			if (symbol.is("and")) {
				parseCondition1();
				//CONDITION and CONDITION
				and();
				SymbolInstance newInst = replace(start, "CONDITION");
				trace(newInst.toStringBlocks(), 0);
				return newInst;
			} else if (symbol.is("or")) {
				parseCondition(true);
				//CONDITION or CONDITION
				or();
				SymbolInstance newInst = replace(start, "CONDITION");
				trace(newInst.toStringBlocks(), 0);
				return newInst;
			}
		} else if (symbol.is("key")) {
			parse("key CONST_EXPR down");
			SymbolInstance newInst = replace(start, "CONDITION");
			//TODO key CONST_EXPR down
			return newInst;
		} else if (symbol.is("inside")) {
			parse("inside temple");
			SymbolInstance newInst = replace(start, "CONDITION");
			//TODO inside temple
			return newInst;
		} else if (symbol.is("within")) {
			parse("within rotation");
			SymbolInstance newInst = replace(start, "CONDITION");
			//TODO within rotation
			return newInst;
		} else if (symbol.is("hand")) {
			parse("hand demo");
			symbol = peek();
			if (symbol.is("played")) {
				accept("played");
				SymbolInstance newInst = replace(start, "CONDITION");
				//TODO hand demo played
				return newInst;
			} else if (symbol.is("trigger")) {
				accept("trigger");
				SymbolInstance newInst = replace(start, "CONDITION");
				//TODO hand demo trigger
				return newInst;
			} else {
				throw new ParseException("Expected: played|trigger", file, symbol.token.line, symbol.token.col);
			}
		} else if (symbol.is("jc")) {
			parse("jc special CONST_EXPR played");
			SymbolInstance newInst = replace(start, "CONDITION");
			//TODO jc special CONST_EXPR played
			return newInst;
		} else if (symbol.is("fire")) {
			parse("fire near COORD_EXPR radius EXPRESSION");
			SymbolInstance newInst = replace(start, "CONDITION");
			//TODO fire near COORD_EXPR radius EXPRESSION
			return newInst;
		} else if (symbol.is("spell")) {
			symbol = peek();
			if (symbol.is("wind")) {
				parse("wind near COORD_EXPR radius EXPRESSION");
				SymbolInstance newInst = replace(start, "CONDITION");
				//TODO spell wind near COORD_EXPR radius EXPRESSION
				return newInst;
			} else if (symbol.is("charging")) {
				accept("charging");
				SymbolInstance newInst = replace(start, "CONDITION");
				//TODO spell charging
				return newInst;
			} else {
				parse("CONST_EXPR for player EXPRESSION");
				SymbolInstance newInst = replace(start, "CONDITION");
				//TODO spell CONST_EXPR for player EXPRESSION
				return newInst;
			}
		} else if (symbol.is("camera")) {
			parse("camera");
			symbol = peek();
			if (symbol.is("ready")) {
				accept("ready");
				SymbolInstance newInst = replace(start, "CONDITION");
				//TODO camera ready
				return newInst;
			} else if (symbol.is("not")) {
				parse("not ready");
				SymbolInstance newInst = replace(start, "CONDITION");
				//TODO camera not ready
				return newInst;
			} else if (symbol.is("position")) {
				parse("position near COORD_EXPR radius EXPRESSION");
				SymbolInstance newInst = replace(start, "CONDITION");
				//TODO camera position near COORD_EXPR radius EXPRESSION
				return newInst;
			} else {
				throw new ParseException("Expected: ready|not", file, symbol.token.line, symbol.token.col);
			}
		} else if (symbol.is("widescreen")) {
			parse("widescreen ready");
			SymbolInstance newInst = replace(start, "CONDITION");
			//TODO widescreen ready
			return newInst;
		} else if (symbol.is("fade")) {
			parse("fade ready");
			SymbolInstance newInst = replace(start, "CONDITION");
			//TODO fade ready
			return newInst;
		} else if (symbol.is("dialogue")) {
			accept("dialogue");
			symbol = peek();
			if (symbol.is("ready")) {
				accept("ready");
				SymbolInstance newInst = replace(start, "CONDITION");
				//TODO dialogue ready
				return newInst;
			} else if (symbol.is("not")) {
				parse("not ready");
				SymbolInstance newInst = replace(start, "CONDITION");
				//TODO dialogue not ready
				return newInst;
			} else {
				throw new ParseException("Expected: ready|not", file, symbol.token.line, symbol.token.col);
			}
		} else if (symbol.is("computer")) {
			parse("computer player EXPRESSION ready");
			SymbolInstance newInst = replace(start, "CONDITION");
			//TODO computer player EXPRESSION ready
			return newInst;
		} else if (symbol.is("player")) {
			accept("player");
			symbol = peek();
			if (symbol.is("has")) {
				parse("has mouse wheel");
				SymbolInstance newInst = replace(start, "CONDITION");
				//TODO player has mouse wheel
				return newInst;
			} else {
				parse("EXPRESSION wind resistance");
				SymbolInstance newInst = replace(start, "CONDITION");
				//TODO player EXPRESSION wind resistance
				return newInst;
			}
		} else if (symbol.is("creature")) {
			parse("creature CONST_EXPR is available");
			SymbolInstance newInst = replace(start, "CONDITION");
			//TODO creature CONST_EXPR is available
			return newInst;
		} else if (symbol.is("get")) {
			parse("get desire of OBJECT is CONST_EXPR");
			SymbolInstance newInst = replace(start, "CONDITION");
			//TODO get desire of OBJECT is CONST_EXPR
			return newInst;
		} else if (symbol.is("read")) {
			accept("read");
			SymbolInstance newInst = replace(start, "CONDITION");
			//TODO read
			return newInst;
		} else if (symbol.is("help")) {
			parse("help system on");
			SymbolInstance newInst = replace(start, "CONDITION");
			//TODO help system on
			return newInst;
		} else if (symbol.is("immersion")) {
			parse("immersion exists");
			SymbolInstance newInst = replace(start, "CONDITION");
			//TODO immersion exists
			return newInst;
		} else if (symbol.is("sound")) {
			accept("sound");
			symbol = peek();
			if (symbol.is("exists")) {
				accept("exists");
				SymbolInstance newInst = replace(start, "CONDITION");
				//TODO sound exists
				return newInst;
			} else {
				parseConstExpr(true);
				symbol = peek();
				if (symbol.is("playing")) {
					//TODO default CONST_EXPR
				} else {
					parseConstExpr(true);
				}
				accept("playing");
				SymbolInstance newInst = replace(start, "CONDITION");
				//TODO sound CONST_EXPR [CONST_EXPR] playing
				return newInst;
			}
		} else if (symbol.is("specific")) {
			parse("specific spell charging");
			SymbolInstance newInst = replace(start, "CONDITION");
			//TODO specific spell charging
			return newInst;
		} else if (symbol.is("music")) {
			parse("music line EXPRESSION");
			SymbolInstance newInst = replace(start, "CONDITION");
			//TODO music line EXPRESSION
			return newInst;
		} else if (symbol.is("not")) {
			parse("not CONDITION");
			SymbolInstance newInst = replace(start, "CONDITION");
			//TODO not CONDITION
			return newInst;
		} else if (symbol.is("say")) {
			parse("say sound CONST_EXPR playing");
			SymbolInstance newInst = replace(start, "CONDITION");
			//TODO say sound CONST_EXPR playing
			return newInst;
		} else if (symbol.is("(")) {
			parse("( CONDITION )");
			SymbolInstance newInst = replace(start, "CONDITION");
			//TODO (CONDITION)
			return newInst;
		} else if (peek(1).is("spirit")) {
			parse("SPIRIT_TYPE spirit");
			symbol = peek();
			if (symbol.is("played")) {
				accept("played");
				SymbolInstance newInst = replace(start, "CONDITION");
				//TODO SPIRIT_TYPE spirit played
				return newInst;
			} else if (symbol.is("speaks")) {
				parse("speaks CONST_EXPR");
				SymbolInstance newInst = replace(start, "CONDITION");
				//TODO SPIRIT_TYPE spirit speaks CONST_EXPR
				return newInst;
			}
		} else {
			final int checkpoint = it.nextIndex();
			final int checkpointIp = getIp();
			symbol = parseObject(false);
			if (symbol != null) {
				symbol = peek();
				if (symbol.is("active")) {
					accept("active");
					SymbolInstance newInst = replace(start, "CONDITION");
					//TODO OBJECT active
					return newInst;
				} else if (symbol.is("viewed")) {
					accept("viewed");
					SymbolInstance newInst = replace(start, "CONDITION");
					//TODO OBJECT viewed
					return newInst;
				} else if (symbol.is("can")) {
					parse("can view camera in EXPRESSION degrees");
					SymbolInstance newInst = replace(start, "CONDITION");
					//TODO OBJECT can view camera in EXPRESSION degrees
					return newInst;
				} else if (symbol.is("within")) {
					parse("within flock distance");
					SymbolInstance newInst = replace(start, "CONDITION");
					//TODO OBJECT within flock distance
					return newInst;
				} else if (symbol.is("clicked")) {
					accept("clicked");
					SymbolInstance newInst = replace(start, "CONDITION");
					//TODO OBJECT clicked
					return newInst;
				} else if (symbol.is("hit")) {
					accept("hit");
					SymbolInstance newInst = replace(start, "CONDITION");
					//TODO OBJECT hit
					return newInst;
				} else if (symbol.is("locked")) {
					parse("within locked interaction");
					SymbolInstance newInst = replace(start, "CONDITION");
					//TODO OBJECT locked interaction
					return newInst;
				} else if (symbol.is("not")) {
					accept("not");
					symbol = peek();
					if (symbol.is("clicked")) {
						accept("clicked");
						SymbolInstance newInst = replace(start, "CONDITION");
						//TODO OBJECT not clicked
						return newInst;
					} else if (symbol.is("viewed")) {
						accept("viewed");
						SymbolInstance newInst = replace(start, "CONDITION");
						//TODO OBJECT not viewed
						return newInst;
					} else if (symbol.is("in")) {
						parse("in OBJECT");
						symbol = peek();
						if (symbol.is("hand")) {
							accept("hand");
							SymbolInstance newInst = replace(start, "CONDITION");
							//TODO OBJECT not in OBJECT hand
							return newInst;
						} else {
							SymbolInstance newInst = replace(start, "CONDITION");
							//TODO OBJECT not in OBJECT 
							return newInst;
						}
					} else if (symbol.is("exists")) {
						accept("exists");
						SymbolInstance newInst = replace(start, "CONDITION");
						//TODO OBJECT not exists
						return newInst;
					}
				} else if (symbol.is("played")) {
					accept("played");
					SymbolInstance newInst = replace(start, "CONDITION");
					//TODO OBJECT played
					return newInst;
				} else if (symbol.is("music")) {
					parse("music played");
					SymbolInstance newInst = replace(start, "CONDITION");
					//TODO OBJECT music played
					return newInst;
				} else if (symbol.is("cast")) {
					parse("cast by OBJECT");
					SymbolInstance newInst = replace(start, "CONDITION");
					//TODO OBJECT cast by OBJECT
					return newInst;
				} else if (symbol.is("poisoned")) {
					accept("poisoned");
					SymbolInstance newInst = replace(start, "CONDITION");
					//TODO OBJECT poisoned
					return newInst;
				} else if (symbol.is("skeleton")) {
					accept("skeleton");
					SymbolInstance newInst = replace(start, "CONDITION");
					//TODO OBJECT skeleton
					return newInst;
				} else if (symbol.is("type")) {
					parse("type CONST_EXPR");
					symbol = parseConstExpr(false);
					if (symbol == null) {
						//TODO default CONST_EXPR=SCRIPT_FIND_TYPE_ANY
					}
					SymbolInstance newInst = replace(start, "CONDITION");
					//TODO OBJECT type TYPE [CONST_EXPR]
					return newInst;
				} else if (symbol.is("on")) {
					parse("on fire");
					SymbolInstance newInst = replace(start, "CONDITION");
					//TODO OBJECT on fire
					return newInst;
				} else if (symbol.is("in")) {
					parse("in OBJECT");
					symbol = peek();
					if (symbol.is("hand")) {
						accept("hand");
						SymbolInstance newInst = replace(start, "CONDITION");
						//TODO OBJECT in OBJECT hand
						return newInst;
					} else {
						SymbolInstance newInst = replace(start, "CONDITION");
						//TODO OBJECT in OBJECT
						return newInst;
					}
				} else if (symbol.is("interacting")) {
					parse("interacting with OBJECT");
					SymbolInstance newInst = replace(start, "CONDITION");
					//TODO OBJECT interacting with OBJECT
					return newInst;
				} else if (symbol.is("is")) {
					accept("is");
					symbol = peek();
					if (symbol.is("not")) {
						parse("not CONST_EXPR");
						SymbolInstance newInst = replace(start, "CONDITION");
						//TODO OBJECT is not CONST_EXPR
						return newInst;
					} else {
						parseConstExpr(true);
						SymbolInstance newInst = replace(start, "CONDITION");
						//TODO OBJECT is CONST_EXPR
						return newInst;
					}
				} else if (symbol.is("exists")) {
					accept("exists");
					SymbolInstance newInst = replace(start, "CONDITION");
					//TODO OBJECT exists
					return newInst;
				} else if (symbol.is("affected")) {
					parse("affected by spell CONST_EXPR");
					SymbolInstance newInst = replace(start, "CONDITION");
					//TODO OBJECT affected by spell CONST_EXPR
					return newInst;
				} else if (symbol.is("leashed")) {
					accept("leashed");
					SymbolInstance newInst = replace(start, "CONDITION");
					//TODO OBJECT leashed
					return newInst;
				} else if (symbol.is("fighting")) {
					accept("fighting");
					SymbolInstance newInst = replace(start, "CONDITION");
					//TODO OBJECT fighting
					return newInst;
				} else if (symbol.is("male")) {
					accept("male");
					SymbolInstance newInst = replace(start, "CONDITION");
					//TODO OBJECT male
					return newInst;
				}
				revert(checkpoint, checkpointIp);
			}
			symbol = parseCoordExpr(false);
			if (symbol != null) {
				symbol = peek();
				if (symbol.is("viewed")) {
					accept("viewed");
					SymbolInstance newInst = replace(start, "CONDITION");
					//TODO COORD_EXPR viewed
					return newInst;
				} else if (symbol.is("valid")) {
					parse("valid for creature");
					SymbolInstance newInst = replace(start, "CONDITION");
					//TODO COORD_EXPR valid for creature
					return newInst;
				} else if (symbol.is("clicked")) {
					parse("clicked radius EXPRESSION");
					SymbolInstance newInst = replace(start, "CONDITION");
					//TODO COORD_EXPR clicked radius EXPRESSION
					return newInst;
				} else if (symbol.is("near")) {
					parse("near COORD_EXPR radius EXPRESSION");
					SymbolInstance newInst = replace(start, "CONDITION");
					//TODO COORD_EXPR near COORD_EXPR radius EXPRESSION
					return newInst;
				} else if (symbol.is("at")) {
					parse("at COORD_EXPR");
					SymbolInstance newInst = replace(start, "CONDITION");
					//TODO COORD_EXPR at COORD_EXPR
					return newInst;
				} else if (symbol.is("not")) {
					accept("not");
					symbol = peek();
					if (symbol.is("viewed")) {
						accept("viewed");
						SymbolInstance newInst = replace(start, "CONDITION");
						//TODO COORD_EXPR not viewed
						return newInst;
					} else if (symbol.is("near")) {
						parse("near COORD_EXPR radius EXPRESSION");
						SymbolInstance newInst = replace(start, "CONDITION");
						//TODO COORD_EXPR not near COORD_EXPR radius EXPRESSION
						return newInst;
					} else if (symbol.is("at")) {
						parse("at COORD_EXPR");
						SymbolInstance newInst = replace(start, "CONDITION");
						//TODO COORD_EXPR not at COORD_EXPR
						return newInst;
					}
				}
				revert(checkpoint, checkpointIp);
			}
			symbol = parseExpression(false);
			if (symbol != null) {
				symbol = peek();
				if (symbol.is("second") || symbol.is("seconds")) {
					parse("second|seconds");
					SymbolInstance newInst = replace(start, "CONDITION");
					//TODO EXPRESSION second|seconds
					return newInst;
				} else if (symbol.is("==")) {
					parse("== EXPRESSION");
					//EXPRESSION == EXPRESSION
					eq();
					SymbolInstance newInst = replace(start, "CONDITION");
					return newInst;
				} else if (symbol.is("!=")) {
					parse("!= EXPRESSION");
					//EXPRESSION != EXPRESSION
					neq();
					SymbolInstance newInst = replace(start, "CONDITION");
					return newInst;
				} else if (symbol.is(">=")) {
					parse(">= EXPRESSION");
					//EXPRESSION >= EXPRESSION
					geq();
					SymbolInstance newInst = replace(start, "CONDITION");
					return newInst;
				} else if (symbol.is("<=")) {
					parse("<= EXPRESSION");
					//EXPRESSION <= EXPRESSION
					leq();
					SymbolInstance newInst = replace(start, "CONDITION");
					return newInst;
				} else if (symbol.is(">")) {
					parse("> EXPRESSION");
					//EXPRESSION > EXPRESSION
					gt();
					SymbolInstance newInst = replace(start, "CONDITION");
					return newInst;
				} else if (symbol.is("<")) {
					parse("< EXPRESSION");
					//EXPRESSION < EXPRESSION
					lt();
					SymbolInstance newInst = replace(start, "CONDITION");
					return newInst;
				}
				revert(checkpoint, checkpointIp);
			}
		}
		seek(start);
		return null;
	}
	
	private SymbolInstance parseObject(boolean fail) throws ParseException {
		final int start = it.nextIndex();
		SymbolInstance symbol = peek();
		if (symbol.is("get")) {
			throw new IllegalStateException("Parsing of \""+symbol+"\" not implemented yet");
			
		} else if (symbol.is("create")) {
			accept("create");
			symbol = peek();
			if (symbol.is("random")) {
				parse("random villager of tribe CONST_EXPR at COORD_EXPR");
				SymbolInstance newInst = replace(start, "OBJECT");
				//TODO create random villager of tribe CONST_EXPR at COORD_EXPR
				return newInst;
			} else if (symbol.is("highlight")) {
				parse("highlight CONST_EXPR at COORD_EXPR");
				SymbolInstance newInst = replace(start, "OBJECT");
				//TODO create highlight HIGHLIGHT_INFO at COORD_EXPR
				return newInst;
			} else if (symbol.is("mist")) {
				parse("mist at COORD_EXPR scale EXPRESSION red EXPRESSION green EXPRESSION blue EXPRESSION transparency EXPRESSION height ratio EXPRESSION");
				SymbolInstance newInst = replace(start, "OBJECT");
				//TODO create mist at COORD_EXPR scale EXPRESSION red EXPRESSION green EXPRESSION blue EXPRESSION transparency EXPRESSION height ratio EXPRESSION
				return newInst;
			} else if (symbol.is("with")) {
				parse("with angle EXPRESSION and scale EXPRESSION CONST_EXPR CONST_EXPR at COORD_EXPR");
				SymbolInstance newInst = replace(start, "OBJECT");
				//TODO create with angle EXPRESSION and scale EXPRESSION CONST_EXPR CONST_EXPR at COORD_EXPR
				return newInst;
			} else if (symbol.is("timer")) {
				parse("timer for EXPRESSION second|seconds");
				SymbolInstance newInst = replace(start, "OBJECT");
				//TODO create timer for EXPRESSION second|seconds
				return newInst;
			} else if (symbol.is("influence")) {
				//TODO push 0 (not anti)
				accept("influence");
				symbol = peek();
				if (symbol.is("on")) {
					parse("on OBJECT radius EXPRESSION");
					SymbolInstance newInst = replace(start, "OBJECT");
					//TODO create influence on OBJECT radius EXPRESSION
					return newInst;
				} else if (symbol.is("at")) {
					parse("at COORD_EXPR radius EXPRESSION");
					SymbolInstance newInst = replace(start, "OBJECT");
					//TODO create influence at COORD_EXPR radius EXPRESSION
					return newInst;
				}
			} else if (symbol.is("anti")) {
				accept("anti");
				//TODO push 1 (anti)
				accept("influence");
				symbol = peek();
				if (symbol.is("on")) {
					parse("on OBJECT radius EXPRESSION");
					SymbolInstance newInst = replace(start, "OBJECT");
					//TODO create anti influence on OBJECT radius EXPRESSION
					return newInst;
				} else if (symbol.is("at")) {
					parse("at position COORD_EXPR radius EXPRESSION");
					SymbolInstance newInst = replace(start, "OBJECT");
					//TODO create anti influence at COORD_EXPR radius EXPRESSION
					return newInst;
				}
			} else if (symbol.is("special")) {
				parse("special effect CONST_EXPR");
				symbol = peek();
				if (symbol.is("at")) {
					parse("at COORD_EXPR time EXPRESSION");
					SymbolInstance newInst = replace(start, "OBJECT");
					//TODO create special effect CONST_EXPR at COORD_EXPR time EXPRESSION
					return newInst;
				} else if (symbol.is("to")) {
					parse("to OBJECT time EXPRESSION");
					SymbolInstance newInst = replace(start, "OBJECT");
					//TODO create special effect CONST_EXPR to OBJECT time EXPRESSION
					return newInst;
				}
			} else {
				parseConstExpr(true);
				symbol = peek();
				if (symbol.is("at")) {
					//TODO default CONST_EXPR
				} else {
					parseConstExpr(true);
				}
				parse("at COORD_EXPR");
				SymbolInstance newInst = replace(start, "OBJECT");
				//TODO create CONST_EXPR [CONST_EXPR] at COORD_EXPR
				return newInst;
			}
		} else if (symbol.is("create_creature_from_creature")) {
			throw new IllegalStateException("Parsing of \""+symbol+"\" not implemented yet");
			
		} else if (symbol.is("marker")) {
			parse("marker at");
			symbol = parseCoordExpr(false);
			if (symbol != null) {
				SymbolInstance newInst = replace(start, "OBJECT");
				//TODO marker at COORD_EXPR
				return newInst;
			} else {
				symbol = parseConstExpr(false);
				if (symbol != null) {
					SymbolInstance newInst = replace(start, "OBJECT");
					//TODO marker at CONST_EXPR
					return newInst;
				} else if (fail) {
					symbol = peek();
					throw new ParseException("Expected COORD_EXPR or CONST_EXPR", file, symbol.token.line, symbol.token.col);
				}
			}
		} else if (symbol.is("reward")) {
			throw new IllegalStateException("Parsing of \""+symbol+"\" not implemented yet");
			
		} else if (symbol.is("flock")) {
			throw new IllegalStateException("Parsing of \""+symbol+"\" not implemented yet");
			
		} else if (symbol.is("make")) {
			throw new IllegalStateException("Parsing of \""+symbol+"\" not implemented yet");
			
		} else if (symbol.is("cast")) {
			throw new IllegalStateException("Parsing of \""+symbol+"\" not implemented yet");
			
		} else if (symbol.is("attach")) {
			throw new IllegalStateException("Parsing of \""+symbol+"\" not implemented yet");
			
		} else if (symbol.is("detach")) {
			throw new IllegalStateException("Parsing of \""+symbol+"\" not implemented yet");
			
		} else if (symbol.is(TokenType.IDENTIFIER)) {
			accept(TokenType.IDENTIFIER);
			SymbolInstance newInst = replace(start, "OBJECT");
			//TODO VARIABLE
			return newInst;
		}
		if (fail) {
			symbol = peek();
			throw new ParseException("Unexpected token: "+symbol, file, symbol.token.line, symbol.token.col);
		} else {
			seek(start);
			return null;
		}
	}
	
	private SymbolInstance parseConstExpr(boolean fail) throws ParseException {
		final int start = it.nextIndex();
		SymbolInstance symbol = peek();
		if (symbol.is("constant")) {
			accept("constant");
			symbol = peek();
			if (symbol.is("from")) {
				parse("from CONST_EXPR to CONST_EXPR");
				SymbolInstance newInst = replace(start, "CONST_EXPR");
				//TODO constant from CONST_EXPR to CONST_EXPR
				return newInst;
			} else {
				parseExpression(fail);
				SymbolInstance newInst = replace(start, "CONST_EXPR");
				//TODO constant EXPRESSION
				return newInst;
			}
		} else if (symbol.is("get")) {
			accept("get");
			symbol = peek();
			if (symbol.is("action")) {
				parse("action text for OBJECT");
				SymbolInstance newInst = replace(start, "CONST_EXPR");
				//TODO get action text for OBJECT
				return newInst;
			} else if (symbol.is("hand")) {
				parse("hand state");
				SymbolInstance newInst = replace(start, "CONST_EXPR");
				//TODO get hand state
				return newInst;
			} else if (symbol.is("player")) {
				parse("player EXPRESSION last spell cast");
				SymbolInstance newInst = replace(start, "CONST_EXPR");
				//TODO get player EXPRESSION last spell cast
				return newInst;
			} else {
				symbol = parseObject(false);
				if (symbol != null) {
					symbol = peek();
					if (symbol.is("type")) {
						accept("type");
						SymbolInstance newInst = replace(start, "CONST_EXPR");
						//TODO get OBJECT type
						return newInst;
					} else if (symbol.is("sub")) {
						parse("sub type");
						SymbolInstance newInst = replace(start, "CONST_EXPR");
						//TODO get OBJECT sub type
						return newInst;
					} else if (symbol.is("leash")) {
						parse("leash type");
						SymbolInstance newInst = replace(start, "CONST_EXPR");
						//TODO get OBJECT leash type
						return newInst;
					} else if (symbol.is("fight")) {
						parse("fight action");
						SymbolInstance newInst = replace(start, "CONST_EXPR");
						//TODO get OBJECT fight action
						return newInst;
					}
				} else {
					symbol = parseConstExpr(false);
					if (symbol != null) {
						parse("opposite creature type");
						SymbolInstance newInst = replace(start, "CONST_EXPR");
						//TODO get CONST_EXPR opposite creature type
						return newInst;
					} else if (fail) {
						symbol = peek();
						throw new ParseException("Expected: OBJECT|CONST_EXPR", file, symbol.token.line, symbol.token.col);
					}
				}
			}
		} else if (symbol.is("variable")) {
			parse("variable state of OBJECT");
			SymbolInstance newInst = replace(start, "CONST_EXPR");
			//TODO variable state of OBJECT
			return newInst;
		} else if (symbol.is("(")) {
			parse("( CONST_EXPR )");
			SymbolInstance newInst = replace(start, "CONST_EXPR");
			//TODO (CONST_EXPR)
			return newInst;
		} else if (symbol.is(TokenType.NUMBER)) {
			accept(TokenType.NUMBER);
			SymbolInstance newInst = replace(start, "CONST_EXPR");
			//TODO NUMBER
			return newInst;
		} else if (symbol.is(TokenType.IDENTIFIER)) {
			accept(TokenType.IDENTIFIER);
			SymbolInstance newInst = replace(start, "CONST_EXPR");
			//TODO CONSTANT
			return newInst;
		}
		if (fail) {
			symbol = peek();
			throw new ParseException("Unexpected token: "+symbol, file, symbol.token.line, symbol.token.col);
		} else {
			seek(start);
			return null;
		}
	}
	
	private SymbolInstance parseCoordExpr(boolean fail) throws ParseException {
		final int start = it.nextIndex();
		SymbolInstance symbol = peek();
		SymbolInstance newSym = parseCoordExpr1();
		while (newSym != null && newSym != symbol) {
			symbol = newSym;
			it.previous();
			newSym = parseCoordExpr1();
		}
		symbol = peek();
		if ("COORD_EXPR".equals(symbol.symbol.keyword)) {
			next();
			return symbol;
		}
		if (fail) {
			symbol = peek();
			throw new ParseException("Unexpected token: "+symbol, file, symbol.token.line, symbol.token.col);
		} else {
			seek(start);
			return null;
		}
	}
	
	private SymbolInstance parseCoordExpr1() throws ParseException {
		final int start = it.nextIndex();
		SymbolInstance symbol = peek();
		if ("COORD_EXPR".equals(symbol.symbol.keyword)) {
			SymbolInstance mode = peek(1);
			if (mode.is("/")) {
				next();
				next();
				parseExpression(true);
				SymbolInstance newInst = replace(start, "COORD_EXPR");
				//TODO COORD_EXPR / EXPRESSION
				return newInst;
			} else if (mode.is("+") || mode.is("-")) {
				next();
				next();
				parseCoordExpr(true);
				if (mode.is("+")) {
					//TODO COORD_EXPR + COORD_EXPR
				} else if (mode.is("-")) {
					//TODO COORD_EXPR - COORD_EXPR
				}
				SymbolInstance newInst = replace(start, "COORD_EXPR");
				return newInst;
			} else {
				return symbol;
			}
		} else if (symbol.token.type == TokenType.EOL) {
			return null;
		} else if (symbol.is("[")) {
			accept("[");
			symbol = parseObject(false);
			if (symbol != null) {
				accept("]");
				//[OBJECT]
				sys(GET_POSITION);
				SymbolInstance newInst = replace(start, "COORD_EXPR");
				return newInst;
			} else {
				parseExpression(true);
				castc();
				accept(",");
				parseExpression(true);
				castc();
				symbol = peek();
				if (symbol.is(",")) {
					accept(",");
					parseExpression(true);
					castc();
					accept("]");
					SymbolInstance newInst = replace(start, "COORD_EXPR");
					//[EXPRESSION, EXPRESSION, EXPRESSION]
					return newInst;
				} else if (symbol.is("]")) {
					accept("]");
					pushc(0);
					swapi();
					SymbolInstance newInst = replace(start, "COORD_EXPR");
					//[EXPRESSION, EXPRESSION]
					return newInst;
				} else {
					throw new ParseException("Unexpected token: "+symbol, file, symbol.token.line, symbol.token.col);
				}
			}
		} else if (symbol.is("camera")) {
			accept("camera");
			symbol = peek();
			if (symbol.is("position")) {
				accept("position");
				SymbolInstance newInst = replace(start, "COORD_EXPR");
				//TODO camera position
				return newInst;
			} else if (symbol.is("focus")) {
				accept("focus");
				SymbolInstance newInst = replace(start, "COORD_EXPR");
				//TODO camera focus
				return newInst;
			} else {
				parseConstExpr(true);
				SymbolInstance newInst = replace(start, "COORD_EXPR");
				//TODO camera CONST_EXPR
				return newInst;
			}
		} else if (symbol.is("stored")) {
			parse("stored camera");
			symbol = peek();
			if (symbol.is("position")) {
				accept("position");
				SymbolInstance newInst = replace(start, "COORD_EXPR");
				//TODO stored camera position
				return newInst;
			} else if (symbol.is("focus")) {
				accept("focus");
				SymbolInstance newInst = replace(start, "COORD_EXPR");
				//TODO stored camera focus
				return newInst;
			} else {
				throw new ParseException("Unexpected token: "+symbol, file, symbol.token.line, symbol.token.col);
			}
		} else if (symbol.is("hand")) {
			parse("hand position");
			SymbolInstance newInst = replace(start, "COORD_EXPR");
			//TODO hand position
			return newInst;
		} else if (symbol.is("facing")) {
			parse("facing camera position distance EXPRESSION");
			SymbolInstance newInst = replace(start, "COORD_EXPR");
			//TODO facing camera position distance EXPRESSION
			return newInst;
		} else if (symbol.is("computer")) {
			parse("computer player EXPRESSION position");
			SymbolInstance newInst = replace(start, "COORD_EXPR");
			//TODO computer player EXPRESSION position
			return newInst;
		} else if (symbol.is("last")) {
			parse("last player EXPRESSION spell cast position");
			SymbolInstance newInst = replace(start, "COORD_EXPR");
			//TODO last player EXPRESSION spell cast position
			return newInst;
		} else if (symbol.is("get")) {
			parse("get target from COORD_EXPR to COORD_EXPR distance EXPRESSION angle EXPRESSION");
			SymbolInstance newInst = replace(start, "COORD_EXPR");
			//TODO get target from COORD_EXPR to COORD_EXPR distance EXPRESSION angle EXPRESSION
			return newInst;
		} else if (symbol.is("arse")) {
			parse("arse position of OBJECT");
			SymbolInstance newInst = replace(start, "COORD_EXPR");
			//TODO arse position of OBJECT
			return newInst;
		} else if (symbol.is("belly")) {
			parse("belly position of OBJECT");
			SymbolInstance newInst = replace(start, "COORD_EXPR");
			//TODO belly position of OBJECT
			return newInst;
		} else if (symbol.is("destination")) {
			parse("destination of OBJECT");
			SymbolInstance newInst = replace(start, "COORD_EXPR");
			//TODO destination of OBJECT
			return newInst;
		} else if (symbol.is("player")) {
			parse("player EXPRESSION temple");
			if (symbol.is("position")) {
				accept("position");
				SymbolInstance newInst = replace(start, "COORD_EXPR");
				//TODO player EXPRESSION temple position
				return newInst;
			} else if (symbol.is("entrance")) {
				parse("entrance position radius EXPRESSION height EXPRESSION");
				SymbolInstance newInst = replace(start, "COORD_EXPR");
				//TODO player EXPRESSION temple entrance position radius EXPRESSION height EXPRESSION
				return newInst;
			} else {
				throw new ParseException("Unexpected token: "+symbol, file, symbol.token.line, symbol.token.col);
			}
		} else if (symbol.is("-")) {
			parse("- COORD_EXPR");
			SymbolInstance newInst = replace(start, "COORD_EXPR");
			//TODO -COORD_EXPR
			return newInst;
		} else if (symbol.is("(")) {
			parse("( COORD_EXPR )");
			SymbolInstance newInst = replace(start, "COORD_EXPR");
			//TODO (COORD_EXPR)
			return newInst;
		} else {
			final int checkpoint = it.nextIndex();
			final int checkpointIp = getIp();
			symbol = parseExpression(false);
			if (symbol != null) {
				SymbolInstance mode = next();
				if (mode.is("*")) {
					parseCoordExpr(true);
					SymbolInstance newInst = replace(start, "COORD_EXPR");
					//TODO EXPRESSION * COORD_EXPR
					return newInst;
				}
				revert(checkpoint, checkpointIp);
			}
		}
		seek(start);
		return null;
	}
	
	private boolean parseOption(String expression) throws ParseException {
		String[] symbols = expression.split(" ");
		int c = 0;
		for (String symbol : symbols) {
			SymbolInstance sInst = next();
			if (!sInst.is(symbol)) break;
			c++;
		}
		if (c == symbols.length) {
			pushb(true);
			return true;
		} else {
			for (; c >= 0; c--) {
				it.previous();
			}
			pushb(false);
			return false;
		}
	}
	
	private SymbolInstance parseEnableDisableKeyword() throws ParseException {
		SymbolInstance symbol = next();
		if (symbol.is("enable")) {
			pushb(true);
		} else if (symbol.is("disable")) {
			pushb(false);
		} else {
			throw new ParseException("Expected: enable|disable", file, symbol.token.line, symbol.token.col);
		}
		return symbol;
	}
	
	private SymbolInstance parseForwardReverseKeyword() throws ParseException {
		SymbolInstance symbol = next();
		if (symbol.is("forward")) {
			pushb(true);
		} else if (symbol.is("reverse")) {
			pushb(false);
		} else {
			throw new ParseException("Expected: forward|reverse", file, symbol.token.line, symbol.token.col);
		}
		return symbol;
	}
	
	private SymbolInstance parseOpenCloseKeyword() throws ParseException {
		SymbolInstance symbol = next();
		if (symbol.is("open")) {
			pushb(true);
		} else if (symbol.is("close")) {
			pushb(false);
		} else {
			throw new ParseException("Expected: open|close", file, symbol.token.line, symbol.token.col);
		}
		return symbol;
	}
	
	private SymbolInstance parsePauseUnpauseKeyword() throws ParseException {
		SymbolInstance symbol = next();
		if (symbol.is("pause")) {
			pushb(true);
		} else if (symbol.is("unpause")) {
			pushb(false);
		} else {
			throw new ParseException("Expected: pause|unpause", file, symbol.token.line, symbol.token.col);
		}
		return symbol;
	}
	
	private SymbolInstance parseQuestChallengeKeyword() throws ParseException {
		SymbolInstance symbol = next();
		if (symbol.is("quest")) {
			pushb(true);
		} else if (symbol.is("challenge")) {
			pushb(false);
		} else {
			throw new ParseException("Expected: quest|challenge", file, symbol.token.line, symbol.token.col);
		}
		return symbol;
	}
	
	private SymbolInstance parseEnterExitKeyword() throws ParseException {
		SymbolInstance symbol = next();
		if (symbol.is("enter")) {
			pushb(true);
		} else if (symbol.is("exit")) {
			pushb(false);
		} else {
			throw new ParseException("Expected: enter|exit", file, symbol.token.line, symbol.token.col);
		}
		return symbol;
	}
	
	private SymbolInstance parseSpiritType() throws ParseException {
		SymbolInstance symbol = next();
		if (symbol.is("good")) {
			pushi(1);
		} else if (symbol.is("evil")) {
			pushi(2);
		} else if (symbol.is("last")) {
			pushi(3);
		} else {
			throw new ParseException("Expected: good|evil|last", file, symbol.token.line, symbol.token.col);
		}
		return symbol;
	}
	
	private SymbolInstance parsePlayingSide() throws ParseException {
		SymbolInstance symbol = next();
		if (symbol.is("away")) {
			throw new ParseException("PLAYING_SIDE not implemented", file, line, col);
		} else {
			throw new ParseException("Expected: away", file, symbol.token.line, symbol.token.col);
		}
		//return symbol;
	}
	
	private int getConstant(SymbolInstance symbol) throws ParseException {
		String name = symbol.token.value;
		return getConstant(name);
	}
	
	private int getConstant(String name) throws ParseException {
		Integer val = localConst.get(name);
		if (val == null) {
			val = constants.get(name);
		}
		if (val == null) {
			SymbolInstance symbol = peek(-1);
			throw new ParseException("Undefined constant: "+name, file, symbol.token.line, symbol.token.col);
		}
		return val;
	}
	
	private int getVar(String name) throws ParseException {
		Integer varId = localVars.get(name);
		if (varId == null) {
			varId = globalVars.get(name);
		}
		if (varId == null) {
			throw new ParseException("Undefined variable: "+name, file, line, col);
		}
		return varId;
	}
	
	private SymbolInstance next() {
		SymbolInstance r = it.next();
		if (r.token != null) {
			line = r.token.line;
			col = r.token.col;
		}
		return r;
	}
	
	private SymbolInstance peek() {
		SymbolInstance r = it.next();
		if (r.token != null) {
			line = r.token.line;
			col = r.token.col;
		}
		it.previous();
		return r;
	}
	
	private SymbolInstance peek(int forward) {
		for (int i = 0; i < forward; i++) {
			it.next();
		}
		for (int i = 0; i > forward; i--) {
			it.previous();
		}
		SymbolInstance r = it.next();
		if (r.token != null) {
			line = r.token.line;
			col = r.token.col;
		}
		it.previous();
		for (int i = 0; i < forward; i++) {
			it.previous();
		}
		for (int i = 0; i > forward; i--) {
			it.next();
		}
		return r;
	}
	
	private void seek(final int index) {
		while (it.nextIndex() < index) {
			it.next();
		}
		while (it.nextIndex() > index) {
			it.previous();
		}
		peek();
	}
	
	private SymbolInstance parseIdentifier() throws ParseException {
		SymbolInstance sInst = next();
		if (!sInst.is(TokenType.IDENTIFIER)) {
			throw new ParseException("Expected: IDENTIFIER", file, sInst.token.line, sInst.token.col);
		}
		//TODO IDENTIFIER
		return sInst;
	}
	
	private int storeStringData(String value) {
		Integer strptr = strings.get(value);
		if (strptr == null) {
			strptr = dataSize;
			strings.put(value, strptr);
			dataSize += value.length() + 1;
		}
		return strptr;
	}
	
	private int parseString() throws ParseException {
		SymbolInstance sInst = next();
		if (!sInst.is(TokenType.STRING)) {
			throw new ParseException("Expected: STRING", file, sInst.token.line, sInst.token.col);
		}
		String value = sInst.token.stringVal();
		int strptr = storeStringData(value);
		//STRING
		pushi(strptr);
		return strptr;
	}
	
	private void parseConstExpr(String keyword, int dflt) throws ParseException, IllegalArgumentException {
		SymbolInstance sInst = peek();
		if (sInst.is(keyword)) {
			accept(keyword);
			parseConstExpr(true);
		} else {
			pushi(dflt);
		}
	}
	
	private void parseExpression(String keyword, float dflt) throws ParseException, IllegalArgumentException {
		SymbolInstance sInst = peek();
		if (sInst.is(keyword)) {
			accept(keyword);
			parseExpression(true);
		} else {
			pushf(dflt);
		}
	}
	
	private SymbolInstance parse(String expression) throws ParseException {
		SymbolInstance r = null;
		String[] symbols = expression.split(" ");
		for (String symbol : symbols) {
			if ("EXPRESSION".equals(symbol)) {
				parseExpression(true);
			} else if ("COORD_EXPR".equals(symbol)) {
				parseCoordExpr(true);
			} else if ("CONST_EXPR".equals(symbol)) {
				parseConstExpr(true);
			} else if ("OBJECT".equals(symbol)) {
				parseObject(true);
			} else if ("CONDITION".equals(symbol)) {
				parseCondition(true);
			} else if ("IDENTIFIER".equals(symbol)) {
				r = parseIdentifier();
			} else if ("STRING".equals(symbol)) {
				parseString();
			} else if ("EOL".equals(symbol)) {
				SymbolInstance sInst = next();
				if (!sInst.is(TokenType.EOL)) {
					throw new ParseException("Unexpected token: "+sInst+". Expected: EOL", file, sInst.token.line, sInst.token.col);
				}
			} else if ("SPIRIT_TYPE".equals(symbol)) {
				parseSpiritType();
			} else if ("PLAYING_SIDE".equals(symbol)) {
				parsePlayingSide();
			} else if (symbol.indexOf("|") >= 0) {
				if ("enable|disable".equals(symbol)) {
					parseEnableDisableKeyword();
				} else if ("forward|reverse".equals(symbol)) {
					parseForwardReverseKeyword();
				} else if ("open|close".equals(symbol)) {
					parseOpenCloseKeyword();
				} else if ("pause|unpause".equals(symbol)) {
					parsePauseUnpauseKeyword();
				} else if ("quest|challenge".equals(symbol)) {
					parseQuestChallengeKeyword();
				} else if ("enter|exit".equals(symbol)) {
					parseEnterExitKeyword();
				} else if ("second|seconds".equals(symbol)) {
					SymbolInstance sInst = next();
					if (!sInst.is("second") && !sInst.is("seconds")) {
						throw new ParseException("Unexpected token: "+sInst+". Expected: second|seconds", file, sInst.token.line, sInst.token.col);
					}
				} else if ("event|events".equals(symbol)) {
					SymbolInstance sInst = next();
					if (!sInst.is("event") && !sInst.is("events")) {
						throw new ParseException("Unexpected token: "+sInst+". Expected: event|events", file, sInst.token.line, sInst.token.col);
					}
				} else if ("graphics|gfx".equals(symbol)) {
					SymbolInstance sInst = next();
					if (!sInst.is("graphics") && sInst.is("gfx")) {
						throw new ParseException("Unexpected token: "+sInst+". Expected: graphics|gfx", file, sInst.token.line, sInst.token.col);
					}
				} else {
					throw new IllegalArgumentException("Unknown symbol: "+symbol);
				}
			} else {
				SymbolInstance sInst = next();
				if (!sInst.is(symbol)) {
					throw new ParseException("Unexpected token "+sInst+". Expected: "+symbol, file, sInst.token.line, sInst.token.col);
				}
			}
		}
		return r;
	}
	
	private void accept(String keyword) throws ParseException {
		SymbolInstance symbol = next();
		if (!symbol.is(keyword)) {
			throw new ParseException("Expected: "+keyword, file, symbol.token.line, symbol.token.col);
		}
	}
	
	private SymbolInstance accept(TokenType type) throws ParseException {
		SymbolInstance symbol = next();
		if (symbol.token.type != type) {
			throw new ParseException("Expected: "+type, file, symbol.token.line, symbol.token.col);
		}
		return symbol;
	}
	
	private void verify(SymbolInstance symbol, TokenType type) throws ParseException {
		if (symbol.token.type != type) {
			throw new ParseException("Expected: "+type, file, symbol.token.line, symbol.token.col);
		}
	}
	
	private SymbolInstance replace(final int index, String symbol) {
		SymbolInstance newInst = new SymbolInstance(Syntax.getSymbol(symbol));
		while (it.nextIndex() > index) {
			SymbolInstance sInst = it.previous();
			it.remove();
			newInst.expression.add(0, sInst);
		}
		it.add(newInst);
		return newInst;
	}
	
	private void revert(final int index, final int instructionAddress) {
		while (it.nextIndex() > index) {
			SymbolInstance sInst = it.previous();
			if (sInst.expression != null) {
				it.remove();
				for (SymbolInstance sym : sInst.expression) {
					it.add(sym);
				}
			}
		}
		while (instructions.size() > instructionAddress) {
			instructions.remove(instructions.size() - 1);
		}
	}
	
	private SymbolInstance toSymbol(int pos, Token token) throws ParseException {
		SymbolInstance sInst = null;
		switch (token.type) {
			case EOL:
				sInst = new SymbolInstance(Syntax.EOL, token);
				break;
			case IDENTIFIER:
				sInst = new SymbolInstance(Syntax.IDENTIFIER, token);
				break;
			case NUMBER:
				sInst = new SymbolInstance(Syntax.NUMBER, token);
				break;
			case STRING:
				sInst = new SymbolInstance(Syntax.STRING, token);
				break;
			case KEYWORD:
				sInst = new SymbolInstance(Syntax.getSymbol(token.value), token);
				break;
			default:
				throw new ParseException("Unrecognized symbol: "+token.value, file, token.line, token.col);
		}
		return sInst;
	}
	
	private void end() {
		Instruction instruction = Instruction.fromKeyword("END");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void jz(int ip) {
		Instruction instruction = Instruction.fromKeyword("JZ");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private Instruction jz() {
		Instruction instruction = Instruction.fromKeyword("JZ");
		instruction.flags = OPCodeFlag.FORWARD;
		instruction.lineNumber = line;
		instructions.add(instruction);
		//jmpStack.add(instruction);
		return instruction;
	}
	
	private void pushi(int val) {
		Instruction instruction = Instruction.fromKeyword("PUSHI");
		instruction.intVal = val;
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void pushf(float val) {
		Instruction instruction = Instruction.fromKeyword("PUSHF");
		instruction.floatVal = val;
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void pushc(float val) {
		Instruction instruction = Instruction.fromKeyword("PUSHC");
		instruction.floatVal = val;
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void pusho(int val) {
		Instruction instruction = Instruction.fromKeyword("PUSHO");
		instruction.intVal = val;
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void pushb(boolean val) {
		Instruction instruction = Instruction.fromKeyword("PUSHB");
		instruction.boolVal = val;
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void pushi(String constant) throws ParseException {
		Instruction instruction = Instruction.fromKeyword("PUSHI");
		instruction.flags = OPCodeFlag.REF;
		instruction.intVal = getConstant(constant);
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void pushf(String variable) throws ParseException {
		Instruction instruction = Instruction.fromKeyword("PUSHF");
		instruction.flags = OPCodeFlag.REF;
		instruction.intVal = getVar(variable);
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void pusho(String variable) throws ParseException {
		Instruction instruction = Instruction.fromKeyword("PUSHO");
		instruction.flags = OPCodeFlag.REF;
		instruction.intVal = getVar(variable);
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void popi() {
		Instruction instruction = Instruction.fromKeyword("POPI");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void popf() {
		Instruction instruction = Instruction.fromKeyword("POPF");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void popc() {
		Instruction instruction = Instruction.fromKeyword("POPC");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void popo() {
		Instruction instruction = Instruction.fromKeyword("POPO");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void popb() {
		Instruction instruction = Instruction.fromKeyword("POPB");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void popf(String variable) throws ParseException {
		Instruction instruction = Instruction.fromKeyword("POPF");
		instruction.flags = OPCodeFlag.REF;
		instruction.intVal = getVar(variable);
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void addi() {
		Instruction instruction = Instruction.fromKeyword("ADDI");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void addf() {
		Instruction instruction = Instruction.fromKeyword("ADDF");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void addc() {
		Instruction instruction = Instruction.fromKeyword("ADDC");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void sys(NativeFunction func) {
		Instruction instruction = Instruction.fromKeyword("SYS");
		instruction.intVal = func.ordinal();
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void sys2(NativeFunction func) {
		Instruction instruction = Instruction.fromKeyword("SYS2");
		instruction.intVal = func.ordinal();
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void subi() {
		Instruction instruction = Instruction.fromKeyword("SUBI");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void subf() {
		Instruction instruction = Instruction.fromKeyword("SUBF");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void subc() {
		Instruction instruction = Instruction.fromKeyword("SUBC");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void neg() {
		Instruction instruction = Instruction.fromKeyword("NEG");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void mul() {
		Instruction instruction = Instruction.fromKeyword("MUL");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void div() {
		Instruction instruction = Instruction.fromKeyword("DIV");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void mod() {
		Instruction instruction = Instruction.fromKeyword("MOD");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void not() {
		Instruction instruction = Instruction.fromKeyword("NOT");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void and() {
		Instruction instruction = Instruction.fromKeyword("AND");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void or() {
		Instruction instruction = Instruction.fromKeyword("OR");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void eq() {
		Instruction instruction = Instruction.fromKeyword("EQ");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void neq() {
		Instruction instruction = Instruction.fromKeyword("NEQ");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void geq() {
		Instruction instruction = Instruction.fromKeyword("GEQ");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void leq() {
		Instruction instruction = Instruction.fromKeyword("LEQ");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void gt() {
		Instruction instruction = Instruction.fromKeyword("GT");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void lt() {
		Instruction instruction = Instruction.fromKeyword("LT");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void jmp(int ip) {
		Instruction instruction = Instruction.fromKeyword("JMP");
		instruction.intVal = ip;
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private Instruction jmp() {
		Instruction instruction = Instruction.fromKeyword("JMP");
		instruction.flags = OPCodeFlag.FORWARD;
		instruction.lineNumber = line;
		instructions.add(instruction);
		//jmpStack.add(instruction);
		return instruction;
	}
	
	private void sleep() {
		Instruction instruction = Instruction.fromKeyword("SLEEP");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void except(int ip) {
		Instruction instruction = Instruction.fromKeyword("EXCEPT");
		instruction.intVal = ip;
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private Instruction except() {
		Instruction instruction = Instruction.fromKeyword("EXCEPT");
		instruction.lineNumber = line;
		instructions.add(instruction);
		//exceptStack.add(instruction);
		return instruction;
	}
	
	private void casti() {
		Instruction instruction = Instruction.fromKeyword("CASTI");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void castf() {
		Instruction instruction = Instruction.fromKeyword("CASTF");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void castc() {
		Instruction instruction = Instruction.fromKeyword("CASTC");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void casto() {
		Instruction instruction = Instruction.fromKeyword("CASTO");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void castb() {
		Instruction instruction = Instruction.fromKeyword("CASTB");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void zero() {
		Instruction instruction = Instruction.fromKeyword("ZERO");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void call(String scriptname) {
		Instruction instruction = Instruction.fromKeyword("CALL");
		instruction.lineNumber = line;
		instructions.add(instruction);
		ScriptToResolve call = new ScriptToResolve(file, line, instruction, scriptname);
		calls.add(call);
	}
	
	private void start(String scriptname) {
		Instruction instruction = Instruction.fromKeyword("START");
		instruction.lineNumber = line;
		instructions.add(instruction);
		ScriptToResolve call = new ScriptToResolve(file, line, instruction, scriptname);
		calls.add(call);
	}
	
	private void endexcept() {
		Instruction instruction = Instruction.fromKeyword("ENDEXCEPT");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void free() {
		Instruction instruction = Instruction.fromKeyword("FREE");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void retexcept() {
		Instruction instruction = Instruction.fromKeyword("RETEXCEPT");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void iterexcept() {
		Instruction instruction = Instruction.fromKeyword("ITEREXCEPT");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void brkexcept() {
		Instruction instruction = Instruction.fromKeyword("BRKEXCEPT");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void swapi() {
		swapi(0);
	}
	
	private void swapi(int offset) {
		Instruction instruction = Instruction.fromKeyword("SWAPI");
		instruction.intVal = offset;
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void swapf() {
		swapf(0);
	}
	
	private void swapf(int offset) {
		Instruction instruction = Instruction.fromKeyword("SWAPF");
		instruction.intVal = offset;
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private int getIp() {
		return instructions.size();
	}
	
	private static String join(String sep, Symbol[] items) {
		if (items.length == 0) return "";
		String r = items[0].keyword;
		for (int i = 1; i < items.length; i++) {
			r += sep + items[i].keyword;
		}
		return r;
	}
	
	private static String join(String sep, String[] items) {
		if (items.length == 0) return "";
		String r = items[0];
		for (int i = 1; i < items.length; i++) {
			r += sep + items[i];
		}
		return r;
	}
	
	private static String shortify(Object r) {
		return shortify(String.valueOf(r));
	}
	
	private static String shortify(String r) {
		if (r.length() > traceMaxStrLen) {
			final int prefixLen = (int) (0.75 * traceMaxStrLen);
			final int suffixLen = traceMaxStrLen - prefixLen;
			r = r.substring(0, prefixLen) + "..." + r.substring(r.length() - suffixLen);
		}
		return r;
	}
	
	
	private static class ScriptToResolve {
		public final File file;
		public final int line;
		public final Instruction instr;
		public final String name;
		
		public ScriptToResolve(File file, int line, Instruction instr, String name) {
			this.file = file;
			this.line = line;
			this.instr = instr;
			this.name = name;
		}
	}
	
	
	private static class SymbolInstance {
		public static final SymbolInstance EOF = new SymbolInstance(Syntax.EOF, null);
		
		public Symbol symbol;
		public final Token token;
		public final List<SymbolInstance> expression;
		
		public SymbolInstance(Symbol symbol) {
			this.symbol = symbol;
			this.token = null;
			this.expression = new LinkedList<>();
		}
		
		public SymbolInstance(Symbol symbol, Token token) {
			this.symbol = symbol;
			this.token = token;
			this.expression = null;
		}
		
		public boolean is(TokenType type) {
			return token != null && token.type == type;
		}
		
		public boolean is(String keyword) {
			return token != null && token.type == TokenType.KEYWORD && keyword.equals(token.value);
		}
		
		@Override
		public String toString() {
			if (token != null) {
				return token.value;
			} else if (expression.isEmpty()) {
				return "<not initialized>";
			} else {
				String r = expression.get(0).toString();
				for (int i = 1; i < expression.size(); i++) {
					r += " " + expression.get(i).toString();
				}
				return r;
			}
		}
		
		public String toStringBlocks() {
			if (token != null) {
				return token.value;
			} else if (expression.isEmpty()) {
				return "<not initialized>";
			} else if (expression.size() == 1) {
				return expression.get(0).toString();
			} else {
				String r = "{" + expression.get(0).toStringBlocks1() + "}";
				for (int i = 1; i < expression.size(); i++) {
					if (expression.get(i).is(TokenType.KEYWORD)) {
						r += " " + expression.get(i).toStringBlocks1();
					} else {
						r += " {" + expression.get(i).toStringBlocks1() + "}";
					}
				}
				return r;
			}
		}
		
		private String toStringBlocks1() {
			if (token != null) {
				return token.value;
			} else if (expression.isEmpty()) {
				return "<not initialized>";
			} else if (expression.size() == 1) {
				return expression.get(0).toString();
			} else {
				String r = expression.get(0).toStringBlocks1();
				for (int i = 1; i < expression.size(); i++) {
					r += " " + expression.get(i).toStringBlocks1();
				}
				return r;
			}
		}
	}
}
