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
	private static final String TMP1 = "__Tmp1";
	
	private File file;
	private String sourceFilename;
	private LinkedList<SymbolInstance> symbols;
	private ListIterator<SymbolInstance> it;
	private int line;
	private int col;
	
	private boolean optimizeAssignmentEnabled = false;
	private boolean fixBugsEnabled = false;
	
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
	
	public boolean isOptimizeAssignmentEnabled() {
		return optimizeAssignmentEnabled;
	}
	
	public void setOptimizeAssignmentEnabled(boolean optimizeAssignmentEnabled) {
		this.optimizeAssignmentEnabled = optimizeAssignmentEnabled;
	}
	
	public boolean isFixBugsEnabled() {
		return fixBugsEnabled;
	}
	
	public void setFixBugsEnabled(boolean fixBugsEnabled) {
		this.fixBugsEnabled = fixBugsEnabled;
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
		if (fixBugsEnabled) {
			declareGlobalVar(TMP1);
		}
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
		return replace(start, "FILE");
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
		outStream.println("Source filename set to: "+sourceFilename);
		return replace(start, "source STRING EOL");
	}
	
	private SymbolInstance parseChallenge() throws ParseException {
		final int start = it.nextIndex();
		SymbolInstance symbol = parse("challenge IDENTIFIER EOL")[1];
		challengeId = getConstant(symbol);
		return replace(start, "challenge IDENTIFIER EOL");
	}
	
	private void declareGlobalVar(String name) {
		Integer varId = globalVars.get(name);
		if (varId == null) {
			varId = globalVars.size();
			globalVars.put(name, varId);
		}
		varOffset = Math.max(varOffset, varId);
	}
	
	private SymbolInstance parseGlobal() throws ParseException {
		final int start = it.nextIndex();
		accept("global");
		SymbolInstance symbol = next();
		if (symbol.is("constant")) {
			//global constant IDENTIFIER = CONSTANT
			symbol = accept(TokenType.IDENTIFIER);
			String name = symbol.token.value;
			accept("=");
			symbol = next();
			int val;
			if (symbol.is(TokenType.NUMBER) || symbol.is(TokenType.IDENTIFIER)) {
				val = getConstant(symbol);
			} else {
				throw new ParseException("Expected: CONSTANT", file, symbol.token.line, symbol.token.col);
			}
			accept(TokenType.EOL);
			Integer oldVal = constants.put(name, val);
			if (oldVal != null && oldVal != val) {
				outStream.println("WARNING: redefinition of global constant: "+name+" at "+file+":"+symbol.token.line);
			}
			return replace(start, "GLOBAL_CONST_DECL");
		} else {
			//global IDENTIFIER
			verify(symbol, TokenType.IDENTIFIER);
			String name = symbol.token.value;
			accept(TokenType.EOL);
			declareGlobalVar(name);
			return replace(start, "GLOBAL_VAR_DECL");
		}
	}
	
	private SymbolInstance parseAutorun() throws ParseException {
		final int start = it.nextIndex();
		//run script IDENTIFIER
		SymbolInstance symbol = parse("run script IDENTIFIER EOL")[2];
		String name = symbol.token.value;
		ScriptToResolve toResolve = new ScriptToResolve(file, line, null, name);
		if (autoruns.put(name, toResolve) != null) {
			throw new ParseException("Duplicate autorun definition: "+name, file, symbol.token.line, symbol.token.col);
		}
		return replace(start, "run script IDENTIFIER EOL");
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
			chl.getScriptsSection().getItems().add(script);
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
			return replace(start, "SCRIPT");
		} finally {
			localVars.clear();
			localConst.clear();
		}
	}
	
	private SymbolInstance parseScriptType() throws ParseException {
		final int start = it.nextIndex();
		SymbolInstance symbol = next();
		if (symbol.is("help")) {
			accept("script");
			return replace(start, "help script");
		} else if (symbol.is("challenge")) {
			parse("help script");
			return replace(start, "challenge help script");
		} else if (symbol.is("temple")) {
			symbol = next();
			if (symbol.is("help")) {
				accept("script");
				return replace(start, "temple help script");
			} else if (symbol.is("special")) {
				accept("script");
				return replace(start, "temple special script");
			} else {
				throw new ParseException("Unexpected token: "+symbol, file, symbol.token.line, symbol.token.col);
			}
		} else if (symbol.is("multiplayer")) {
			parse("help script");
			return replace(start, "multiplayer help script");
		} else if (symbol.is("script")) {
			return replace(start, "script");
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
		return replace(start, "{LOCAL_DECL}");
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
			//IDENTIFIER = EXPRESSION
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
			addLocalVar(var);
			popf(var);
			return replace(start, "LOCAL_DECL");
		} else if (symbol.is("constant")) {
			//constant IDENTIFIER = CONSTANT
			parse("IDENTIFIER =");
			String constant = symbol.token.value;
			if (localConst.containsKey(constant)) {
				throw new ParseException("Duplicate constant: "+constant, file, symbol.token.line, symbol.token.col);
			}
			symbol = next();
			if (symbol.is(TokenType.NUMBER) || symbol.is(TokenType.IDENTIFIER)) {
				int val = getConstant(symbol);
				localConst.put(constant, val);
			} else {
				throw new ParseException("Expected: CONSTANT", file, symbol.token.line, symbol.token.col);
			}
			accept(TokenType.EOL);
			return replace(start, "CONST_DECL");
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
		return replace(start, "STATEMENTS");
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
		return replace(start, "STATEMENT");
	}
	
	private SymbolInstance parseRemove() throws ParseException {
		final int start = it.nextIndex();
		parse("remove resource CONST_EXPR EXPRESSION from OBJECT EOL");
		//remove resource CONST_EXPR EXPRESSION from OBJECT
		sys(REMOVE_RESOURCE);
		popf();
		return replace(start, "STATEMENT");
	}
	
	private SymbolInstance parseAdd() throws ParseException {
		final int start = it.nextIndex();
		accept("add");
		SymbolInstance symbol = peek();
		if (symbol.is("for")) {
			//add for building OBJECT to OBJECT
			parse("for building OBJECT to OBJECT EOL");
			throw new ParseException("Statement not implemented", file, line, col);
			//return replace(start, "STATEMENT");
		} else if (symbol.is("resource")) {
			//add resource CONST_EXPR EXPRESSION from OBJECT
			parse("resource CONST_EXPR EXPRESSION from OBJECT EOL");
			sys(ADD_RESOURCE);
			popf();
			return replace(start, "STATEMENT");
		} else {
			parse("OBJECT target");
			symbol = next();
			if (symbol.is("at")) {
				parse("COORD_EXPR EOL");
				//add OBJECT target at COORD_EXPR
				sys(ADD_SPOT_VISUAL_TARGET_POS);
				return replace(start, "STATEMENT");
			} else if (symbol.is("on")) {
				parse("OBJECT EOL");
				//add OBJECT target on OBJECT
				sys(ADD_SPOT_VISUAL_TARGET_OBJECT);
				return replace(start, "STATEMENT");
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
			return replace(start, "STATEMENT");
		} else if (symbol.is("game")) {
			parse("game time EXPRESSION time EXPRESSION EOL");
			//move game time EXPRESSION time EXPRESSION
			sys(MOVE_GAME_TIME);
			return replace(start, "STATEMENT");
		} else if (symbol.is("music")) {
			parse("music from OBJECT to OBJECT EOL");
			//move music from OBJECT to OBJECT
			throw new ParseException("Statement not implemented", file, line, col);
			//return replace(start, "STATEMENT");
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
					return replace(start, "STATEMENT");
				} else if (symbol.is("follow")) {
					parse("follow OBJECT EOL");
					//move camera position follow OBJECT
					sys(POSITION_FOLLOW);
					return replace(start, "STATEMENT");
				} else {
					parse("COORD_EXPR docus COORD_EXPR lens EXPRESSION time EXPRESSION EOL");
					//move camera position COORD_EXPR focus COORD_EXPR lens EXPRESSION time EXPRESSION
					throw new ParseException("Statement not implemented", file, line, col);
					//return replace(start, "STATEMENT");
				}
			} else if (symbol.is("focus")) {
				accept("focus");
				symbol = peek();
				if (symbol.is("to")) {
					parse("to COORD_EXPR time EXPRESSION EOL");
					//move camera focus to COORD_EXPR time EXPRESSION
					sys(MOVE_CAMERA_FOCUS);
					return replace(start, "STATEMENT");
				} else if (symbol.is("follow")) {
					parse("follow OBJECT EOL");
					//move camera focus follow OBJECT
					sys(FOCUS_FOLLOW);
					return replace(start, "STATEMENT");
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
					return replace(start, "STATEMENT");
				} else {
					//move camera to CONSTANT time EXPRESSION
					symbol = acceptAny(TokenType.NUMBER, TokenType.IDENTIFIER);
					int val = getConstant(symbol);
					pushi(val);
					sys(CONVERT_CAMERA_FOCUS);
					pushi(val);
					sys(CONVERT_CAMERA_POSITION);
					parse("time EXPRESSION EOL");
					if (fixBugsEnabled) {	//See notes in 3.18.23
						popf(TMP1);
						pushf(TMP1);
						swapf(4);
						pushf(TMP1);
					} else {
						swapf(4);
					}
					sys(MOVE_CAMERA_POSITION);
					sys(MOVE_CAMERA_FOCUS);
					return replace(start, "STATEMENT");
				}
			} else {
				throw new ParseException("Unexpected token: "+symbol, file, symbol.token.line, symbol.token.col);
			}
		} else {
			parse("OBJECT position to COORD_EXPR [radius EXPRESSION] EOL");
			//move OBJECT position to COORD_EXPR [radius EXPRESSION]
			sys(MOVE_GAME_THING);
			return replace(start, "STATEMENT");
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
			return replace(start, "STATEMENT");
		} else if (symbol.is("player")) {
			parse("player EXPRESSION ally with player EXPRESSION percentage EXPRESSION EOL");
			//set player EXPRESSION ally with player EXPRESSION percentage EXPRESSION
			sys(SET_PLAYER_ALLY);
			return replace(start, "STATEMENT");
		} else if (symbol.is("computer")) {
			parse("computer player EXPRESSION");
			symbol = peek();
			if (symbol.is("position")) {
				parse("position to COORD_EXPR EOL");
				//set computer player EXPRESSION position to COORD_EXPR
				pushb(false);
				sys(SET_COMPUTER_PLAYER_POSITION);
				return replace(start, "STATEMENT");
			} else if (symbol.is("personality")) {
				parse("personality STRING EXPRESSION EOL");
				//set computer player EXPRESSION personality STRING EXPRESSION
				sys(SET_COMPUTER_PLAYER_PERSONALITY);
				return replace(start, "STATEMENT");
			} else if (symbol.is("suppression")) {
				parse("suppression STRING EXPRESSION EOL");
				//set computer player EXPRESSION suppression STRING EXPRESSION
				throw new ParseException("Statement not implemented", file, line, col);
				//return replace(start, "STATEMENT");
			} else if (symbol.is("speed")) {
				parse("speed EXPRESSION EOL");
				//set computer player EXPRESSION speed EXPRESSION
				sys(SET_COMPUTER_PLAYER_SPEED);
				return replace(start, "STATEMENT");
			} else if (symbol.is("attitude")) {
				parse("attitude to player EXPRESSION to EXPRESSION EOL");
				//set computer player EXPRESSION attitude to player EXPRESSION to EXPRESSION
				sys(SET_COMPUTER_PLAYER_ATTITUDE);
				return replace(start, "STATEMENT");
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
					//return replace(start, "STATEMENT");
				} else {
					parse("EXPRESSION EOL");
					//set game time EXPRESSION
					sys(SET_GAME_TIME);
					return replace(start, "STATEMENT");
				}
			} else if (symbol.is("speed")) {
				parse("speed to EXPRESSION EOL");
				//set game speed to EXPRESSION
				sys2(SET_GAMESPEED);
				return replace(start, "STATEMENT");
			} else {
				throw new ParseException("Unexpected token: "+symbol, file, symbol.token.line, symbol.token.col);
			}
		} else if (symbol.is("interaction")) {
			parse("interaction CONST_EXPR EOL");
			//set interaction CONST_EXPR
			sys2(SET_INTERFACE_INTERACTION);
			return replace(start, "STATEMENT");
		} else if (symbol.is("fade")) {
			accept("fade");
			symbol = peek();
			if (symbol.is("red")) {
				parse("red EXPRESSION green EXPRESSION blue EXPRESSION time EXPRESSION EOL");
				//set fade red EXPRESSION green EXPRESSION blue EXPRESSION time EXPRESSION
				sys(SET_FADE);
				return replace(start, "STATEMENT");
			} else if (symbol.is("in")) {
				parse("in time EXPRESSION EOL");
				//set fade in time EXPRESSION
				sys(SET_FADE_IN);
				return replace(start, "STATEMENT");
			} else {
				throw new ParseException("Unexpected token: "+symbol, file, symbol.token.line, symbol.token.col);
			}
		} else if (symbol.is("bookmark")) {
			parse("bookmark EXPRESSION to COORD_EXPR EOL");
			//set bookmark EXPRESSION to COORD_EXPR
			throw new ParseException("Statement not implemented", file, line, col);
			//return replace(start, "STATEMENT");
		} else if (symbol.is("draw")) {
			parse("draw text colour red EXPRESSION green EXPRESSION blue EXPRESSION EOL");
			//set draw text colour red EXPRESSION green EXPRESSION blue EXPRESSION
			sys(SET_DRAW_TEXT_COLOUR);
			return replace(start, "STATEMENT");
		} else if (symbol.is("clipping")) {
			parse("clipping window across EXPRESSION down EXPRESSION width EXPRESSION height EXPRESSION EOL");
			//set clipping window across EXPRESSION down EXPRESSION width EXPRESSION height EXPRESSION
			sys(SET_CLIPPING_WINDOW);
			return replace(start, "STATEMENT");
		} else if (symbol.is("camera")) {
			accept("camera");
			symbol = peek();
			if (symbol.is("zones")) {
				parse("zones to STRING EOL");
				//set camera zones to STRING
				sys(SET_CAMERA_ZONE);
				return replace(start, "STATEMENT");
			} else if (symbol.is("lens")) {
				parse("lens EXPRESSION [time EXPRESSION] EOL");
				//set camera lens EXPRESSION [time EXPRESSION]
				sys(MOVE_CAMERA_LENS);
				return replace(start, "STATEMENT");
			} else if (symbol.is("position")) {
				accept("position");
				symbol = peek();
				if (symbol.is("to")) {
					parse("to COORD_EXPR EOL");
					//set camera position to COORD_EXPR
					sys(SET_CAMERA_POSITION);
					return replace(start, "STATEMENT");
				} else if (symbol.is("follow")) {
					accept("follow");
					symbol = peek();
					if (symbol.is("computer")) {
						parse("computer player EXPRESSION EOL");
						//set camera position follow computer player EXPRESSION
						sys(SET_POSITION_FOLLOW_COMPUTER_PLAYER);
						return replace(start, "STATEMENT");
					} else {
						parse("OBJECT EOL");
						//set camera position follow OBJECT
						sys(SET_POSITION_FOLLOW);
						return replace(start, "STATEMENT");
					}
				} else {
					parse("COORD_EXPR focus COORD_EXPR lens EXPRESSION EOL");
					//set camera position COORD_EXPR focus COORD_EXPR lens EXPRESSION
					throw new ParseException("Statement not implemented", file, line, col);
					//return replace(start, "STATEMENT");
				}
			} else if (symbol.is("focus")) {
				accept("focus");
				symbol = peek();
				if (symbol.is("to")) {
					parse("to COORD_EXPR EOL");
					//set camera focus to COORD_EXPR
					sys(SET_CAMERA_FOCUS);
					return replace(start, "STATEMENT");
				} else if (symbol.is("follow")) {
					accept("follow");
					symbol = peek();
					if (symbol.is("computer")) {
						parse("computer player EXPRESSION EOL");
						//set camera focus follow computer player EXPRESSION
						sys(SET_FOCUS_FOLLOW_COMPUTER_PLAYER);
						return replace(start, "STATEMENT");
					} else {
						parse("OBJECT EOL");
						//set camera focus follow OBJECT
						sys(SET_FOCUS_FOLLOW);
						return replace(start, "STATEMENT");
					}
				} else {
					throw new ParseException("Unexpected token: "+symbol, file, symbol.token.line, symbol.token.col);
				}
			} else if (symbol.is("to")) {
				accept("to");
				symbol = peek();
				if (symbol.is("face")) {
					parse("face OBJECT distance EXPRESSION EOL");
					//set camera to face OBJECT distance EXPRESSION
					throw new ParseException("Statement not implemented", file, line, col);
					//return replace(start, "STATEMENT");
				} else {
					//set camera to CONSTANT
					symbol = parse("CONSTANT EOL")[0];
					int val = getConstant(symbol);
					sys(CONVERT_CAMERA_FOCUS);
					pushi(val);
					sys(CONVERT_CAMERA_POSITION);
					sys(SET_CAMERA_POSITION);
					sys(SET_CAMERA_FOCUS);
					return replace(start, "STATEMENT");
				}
			} else if (symbol.is("follow")) {
				parse("follow OBJECT distance EXPRESSION EOL");
				//set camera follow OBJECT distance EXPRESSION
				sys(SET_FOCUS_AND_POSITION_FOLLOW);
				return replace(start, "STATEMENT");
			} else if (symbol.is("properties")) {
				parse("properties distance EXPRESSION speed EXPRESSION angle EXPRESSION enable|disable behind EOL");
				//set camera properties distance EXPRESSION speed EXPRESSION angle EXPRESSION enable|disable behind
				sys(CAMERA_PROPERTIES);
				return replace(start, "STATEMENT");
			} else {
				throw new ParseException("Unexpected token: "+symbol, file, symbol.token.line, symbol.token.col);
			}
		} else if (symbol.is("dual")) {
			parse("dual camera to OBJECT OBJECT EOL");
			//set dual camera to OBJECT OBJECT
			sys(UPDATE_DUAL_CAMERA);
			return replace(start, "STATEMENT");
		} else {
			symbol = parseObject(false);
			if (symbol != null) {
				symbol = peek();
				if (symbol.is("position")) {
					parse("position to COORD_EXPR EOL");
					//set OBJECT position to COORD_EXPR
					sys(SET_CAMERA_POSITION);
					return replace(start, "STATEMENT");
				} else if (symbol.is("disciple")) {
					parse("disciple CONST_EXPR");
					parseOption("with sound");
					accept(TokenType.EOL);
					//set OBJECT disciple CONST_EXPR [with sound]
					sys(SET_DISCIPLE);
					return replace(start, "STATEMENT");
				} else if (symbol.is("focus")) {
					accept("focus");
					symbol = peek();
					if (symbol.is("to")) {
						parse("to COORD_EXPR EOL");
						//set OBJECT focus to COORD_EXPR
						sys(SET_FOCUS);
						return replace(start, "STATEMENT");
					} else if (symbol.is("on")) {
						parse("on OBJECT EOL");
						//set OBJECT focus on OBJECT
						sys(SET_FOCUS_ON_OBJECT);
						return replace(start, "STATEMENT");
					} else {
						throw new ParseException("Expected: to|on", file, symbol.token.line, symbol.token.col);
					}
				} else if (symbol.is("anim")) {
					parse("anim CONST_EXPR EOL");
					//set OBJECT anim CONST_EXPR
					sys(OVERRIDE_STATE_ANIMATION);
					return replace(start, "STATEMENT");
				} else if (symbol.is("properties")) {
					accept("properties");
					symbol = peek();
					if (symbol.is("inner")) {
						//set OBJECT properties inner EXPRESSION outer EXPRESSION [calm EXPRESSION]
						parse("inner EXPRESSION outer EXPRESSION [calm EXPRESSION] EOL");
						sys(CHANGE_INNER_OUTER_PROPERTIES);
						return replace(start, "STATEMENT");
					} else if (symbol.is("town")) {
						parse("town OBJECT flock position COORD_EXPR distance EXPRESSION radius EXPRESSION flock OBJECT EOL");
						//set OBJECT properties town OBJECT flock position COORD_EXPR distance EXPRESSION radius EXPRESSION flock OBJECT
						sys(VORTEX_PARAMETERS);
						return replace(start, "STATEMENT");
					} else if (symbol.is("degrees")) {
						parse("degrees EXPRESSION rainfall EXPRESSION snowfall EXPRESSION overcast EXPRESSION speed EXPRESSION EOL");
						//set OBJECT properties degrees EXPRESSION rainfall EXPRESSION snowfall EXPRESSION overcast EXPRESSION speed EXPRESSION
						sys(CHANGE_WEATHER_PROPERTIES);
						return replace(start, "STATEMENT");
					} else if (symbol.is("time")) {
						parse("time EXPRESSION fade EXPRESSION EOL");
						//set OBJECT properties time EXPRESSION fade EXPRESSION
						sys(CHANGE_TIME_FADE_PROPERTIES);
						return replace(start, "STATEMENT");
					} else if (symbol.is("clouds")) {
						parse("clouds EXPRESSION shade EXPRESSION height EXPRESSION EOL");
						//set OBJECT properties clouds EXPRESSION shade EXPRESSION height EXPRESSION
						sys(CHANGE_CLOUD_PROPERTIES);
						return replace(start, "STATEMENT");
					} else if (symbol.is("sheetmin")) {
						parse("sheetmin EXPRESSION sheetmax EXPRESSION forkmin EXPRESSION forkmax EXPRESSION EOL");
						//set OBJECT properties sheetmin EXPRESSION sheetmax EXPRESSION forkmin EXPRESSION forkmax EXPRESSION
						sys(CHANGE_LIGHTNING_PROPERTIES);
						return replace(start, "STATEMENT");
					} else {
						throw new ParseException("Expected: inner|town|degrees|time|clouds|sheetmin", file, symbol.token.line, symbol.token.col);
					}
				} else if (symbol.is("text")) {
					parse("text property text CONST_EXPR category CONST_EXPR EOL");
					//set OBJECT text property text CONST_EXPR category CONST_EXPR
					sys(HIGHLIGHT_PROPERTIES);
					return replace(start, "STATEMENT");
				} else if (symbol.is("velocity")) {
					parse("velocity heading COORD_EXPR speed EXPRESSION EOL");
					//set OBJECT velocity heading COORD_EXPR speed EXPRESSION
					sys(SET_HEADING_AND_SPEED);
					return replace(start, "STATEMENT");
				} else if (symbol.is("target")) {
					parse("target COORD_EXPR time EXPRESSION EOL");
					//set OBJECT target COORD_EXPR time EXPRESSION
					sys(SET_TARGET);
					return replace(start, "STATEMENT");
				} else if (symbol.is("time")) {
					parse("set OBJECT time to EXPRESSION second|seconds EOL");
					//set OBJECT time to EXPRESSION second|seconds
					sys(SET_TIMER_TIME);
					return replace(start, "STATEMENT");
				} else if (symbol.is("radius")) {
					parse("radius EXPRESSION EOL");
					//set OBJECT radius EXPRESSION
					sys(SET_MAGIC_RADIUS);
					return replace(start, "STATEMENT");
				} else if (symbol.is("mana")) {
					parse("mana EXPRESSION EOL");
					//set OBJECT mana EXPRESSION
					sys(GAME_SET_MANA);
					return replace(start, "STATEMENT");
				} else if (symbol.is("temperature")) {
					parse("temperature EXPRESSION EOL");
					//set OBJECT temperature EXPRESSION
					sys(SET_TEMPERATURE);
					return replace(start, "STATEMENT");
				} else if (symbol.is("forward") || symbol.is("reverse")) {
					parse("forward|reverse walk path CONST_EXPR from EXPRESSION to EXPRESSION EOL");
					//set OBJECT forward|reverse walk path CONST_EXPR from EXPRESSION to EXPRESSION
					sys(WALK_PATH);
					return replace(start, "STATEMENT");
				} else if (symbol.is("desire")) {
					accept("desire");
					symbol = peek();
					if (symbol.is("maximum")) {
						parse("maximum CONST_EXPR to EXPRESSION EOL");
						//TODO set OBJECT desire maximum CONST_EXPR to EXPRESSION
						return replace(start, "STATEMENT");
					} else if (symbol.is("boost")) {
						parse("boost TOWN_DESIRE_INFO EXPRESSION EOL");
						//TODO set OBJECT desire boost TOWN_DESIRE_INFO EXPRESSION
						return replace(start, "STATEMENT");
					} else {
						parseConstExpr(true);
						symbol = peek();
						if (symbol.is("to")) {
							parse("EXPRESSION EOL");
							//TODO set OBJECT desire CONST_EXPR to EXPRESSION
							return replace(start, "STATEMENT");
						} else {
							parse("CONST_EXPR EOL");
							//TODO set OBJECT desire CONST_EXPR CONST_EXPR
							return replace(start, "STATEMENT");
						}
					}
				} else if (symbol.is("only")) {
					parse("only desire CONST_EXPR EOL");
					//TODO set OBJECT only desire CONST_EXPR
					return replace(start, "STATEMENT");
				} else if (symbol.is("disable")) {
					parse("disable only desire EOL");
					//TODO set OBJECT disable only desire
					return replace(start, "STATEMENT");
				} else if (symbol.is("magic")) {
					parse("set OBJECT magic properties MAGIC_TYPE [time EXPRESSION] EOL");
					//TODO set OBJECT magic properties MAGIC_TYPE [time EXPRESSION]
					return replace(start, "STATEMENT");
				} else if (symbol.is("all")) {
					parse("all desire CONST_EXPR EOL");
					//TODO set OBJECT all desire CONST_EXPR
					return replace(start, "STATEMENT");
				} else if (symbol.is("priority")) {
					parse("priority EXPRESSION EOL");
					//TODO set OBJECT priority EXPRESSION
					return replace(start, "STATEMENT");
				} else if (symbol.is("home")) {
					parse("home position COORD_EXPR EOL");
					//TODO set OBJECT home position COORD_EXPR
					return replace(start, "STATEMENT");
				} else if (symbol.is("creed")) {
					parse("creed properties hand HAND_GLOW scale EXPRESSION power EXPRESSION time EXPRESSION EOL");
					//TODO set OBJECT creed properties hand CONST_EXPR scale EXPRESSION power EXPRESSION time EXPRESSION
					return replace(start, "STATEMENT");
				} else if (symbol.is("name")) {
					parse("name CONST_EXPR EOL");
					//TODO set OBJECT name CONST_EXPRset OBJECT name CONST_EXPR
					return replace(start, "STATEMENT");
				} else if (symbol.is("fade")) {
					accept("fade");
					symbol = peek();
					if (symbol.is("start")) {
						parse("start scale EXPRESSION end scale EXPRESSION start transparency EXPRESSION end transparency EXPRESSION time EXPRESSION EOL");
						//TODO set OBJECT fade start scale EXPRESSION end scale EXPRESSION start transparency EXPRESSION end transparency EXPRESSION time EXPRESSION
						return replace(start, "STATEMENT");
					} else if (symbol.is("in")) {
						parse("in time EXPRESSION EOL");
						//TODO set OBJECT fade in time EXPRESSION
						return replace(start, "STATEMENT");
					} else {
						throw new ParseException("Expected: start|in", file, symbol.token.line, symbol.token.col);
					}
				} else if (symbol.is("belief")) {
					parse("belief scale EXPRESSION EOL");
					//TODO set OBJECT belief scale EXPRESSION
					return replace(start, "STATEMENT");
				} else if (symbol.is("player")) {
					parseExpression(true);
					symbol = peek();
					if (symbol.is("relative")) {
						parse("relative belief EOL");
						//TODO set OBJECT player EXPRESSION relative belief
						return replace(start, "STATEMENT");
					} else if (symbol.is("")) {
						parse("belief EXPRESSION EOL");
						//TODO set OBJECT player EXPRESSION belief EXPRESSION
						return replace(start, "STATEMENT");
					} else {
						throw new ParseException("Expected: start|in", file, symbol.token.line, symbol.token.col);
					}
				} else if (symbol.is("building")) {
					parse("set OBJECT building properties ABODE_NUMBER size EXPRESSION EOL");
					parseOption("destroys when placed");
					//TODO set OBJECT building properties ABODE_NUMBER size EXPRESSION [destroys when placed]
					return replace(start, "STATEMENT");
				} else if (symbol.is("carrying")) {
					parse("carrying CARRIED_OBJECT EOL");
					//TODO set OBJECT carrying CARRIED_OBJECT
					return replace(start, "STATEMENT");
				} else if (symbol.is("music")) {
					parse("music position to COORD_EXPR EOL");
					//TODO set OBJECT music position to COORD_EXPR
					return replace(start, "STATEMENT");
				} else {
					parse("CONST_EXPR development EOL");
					return replace(start, "STATEMENT");
					//TODO set OBJECT CONST_EXPR development
				}
			} else {
				symbol = parseExpression(true);
				if (symbol != null) {
					parse("land balance EXPRESSION EOL");
					//TODO set EXPRESSION land balance EXPRESSION
					return replace(start, "STATEMENT");
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
			//TODO delete all weather at COORD_EXPR radius EXPRESSION
			return replace(start, "STATEMENT");
		} else {
			parseObject(true);
			parseOption("with fade");
			accept(TokenType.EOL);
			//TODO delete OBJECT [with fade]
			return replace(start, "STATEMENT");
		}
	}
	
	private SymbolInstance parseRelease() throws ParseException {
		final int start = it.nextIndex();
		accept("release");
		SymbolInstance symbol = peek();
		if (symbol.is("computer")) {
			parse("computer player EXPRESSION EOL");
			return replace(start, "STATEMENT");
			//TODO release computer player EXPRESSION
		} else {
			parse("OBJECT focus EOL");
			return replace(start, "STATEMENT");
			//TODO release OBJECT focus
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
				return replace(start, "STATEMENT");
				//TODO enable|disable leash on OBJECT
			} else if (symbol.is("draw")) {
				accept("draw EOL");
				return replace(start, "STATEMENT");
				//TODO enable|disable leash draw
			} else {
				throw new ParseException("Expected: on|draw", file, symbol.token.line, symbol.token.col);
			}
		} else if (symbol.is("player")) {
			parse("player EXPRESSION");
			symbol = peek();
			if (symbol.is("wind")) {
				parse("wind resistance EOL");
				return replace(start, "STATEMENT");
				//TODO enable|disable player EXPRESSION wind resistance
			} else if (symbol.is("virtual")) {
				parse("virtual influence EOL");
				return replace(start, "STATEMENT");
				//TODO enable|disable player EXPRESSION virtual influence
			} else {
				throw new ParseException("Expected: wind|virtual", file, symbol.token.line, symbol.token.col);
			}
		} else if (symbol.is("fight")) {
			parse("fight exit EOL");
			return replace(start, "STATEMENT");
			//TODO enable|disable fight exit
		} else if (symbol.is("computer")) {
			parse("computer player EXPRESSION EOL");
			return replace(start, "STATEMENT");
			//TODO enable|disable computer player EXPRESSION
		} else if (symbol.is("game")) {
			parse("game time EOL");
			return replace(start, "STATEMENT");
			//TODO enable|disable game time
		} else if (symbol.is("help")) {
			parse("help system EOL");
			return replace(start, "STATEMENT");
			//TODO enable|disable help system
		} else if (symbol.is("creature")) {
			accept("creature");
			symbol = peek();
			if (symbol.is("sound")) {
				parse("sound EOL");
				return replace(start, "STATEMENT");
				//TODO enable|disable creature sound
			} else if (symbol.is("in")) {
				parse("in temple EOL");
				return replace(start, "STATEMENT");
				//TODO enable|disable creature in temple
			} else {
				throw new ParseException("Expected: sound|in", file, symbol.token.line, symbol.token.col);
			}
		} else if (symbol.is("sound")) {
			parse("sound effects EOL");
			return replace(start, "STATEMENT");
			//TODO enable|disable sound effects
		} else if (symbol.is("constant")) {
			parse("constant avi sequence EOL");
			return replace(start, "STATEMENT");
			//TODO enable|disable constant avi sequence
		} else if (symbol.is("spell")) {
			accept("spell");
			symbol = peek();
			if (symbol.is("constant")) {
				parse("constant in OBJECT EOL");
				return replace(start, "STATEMENT");
				//TODO enable|disable spell constant in OBJECT
			} else {
				parse("CONST_EXPR for player EXPRESSION EOL");
				return replace(start, "STATEMENT");
				//TODO enable|disable spell CONST_EXPR for player EXPRESSION
			}
		} else if (symbol.is("angle")) {
			parse("angle sound EOL");
			return replace(start, "STATEMENT");
			//TODO enable|disable angle sound
		} else if (symbol.is("pitch")) {
			parse("pitch sound EOL");
			return replace(start, "STATEMENT");
			//TODO enable|disable pitch sound
		} else if (symbol.is("highlight")) {
			parse("highlight draw EOL");
			return replace(start, "STATEMENT");
			//TODO enable|disable highlight draw
		} else if (symbol.is("intro")) {
			parse("intro building EOL");
			return replace(start, "STATEMENT");
			//TODO enable|disable intro building
		} else if (symbol.is("temple")) {
			parse("temple EOL");
			return replace(start, "STATEMENT");
			//TODO enable|disable temple
		} else if (symbol.is("climate")) {
			accept("climate");
			symbol = peek();
			if (symbol.is("weather")) {
				parse("weather EOL");
				return replace(start, "STATEMENT");
				//TODO enable|disable climate weather
			} else if (symbol.is("create")) {
				parse("create storms EOL");
				return replace(start, "STATEMENT");
				//TODO enable|disable climate create storms
			} else {
				throw new ParseException("Expected: weather|create", file, symbol.token.line, symbol.token.col);
			}
		} else if (symbol.is("music")) {
			parse("music on OBJECT EOL");
			return replace(start, "STATEMENT");
			//TODO enable|disable music on OBJECT
		} else if (symbol.is("alignment")) {
			parse("alignment music EOL");
			return replace(start, "STATEMENT");
			//TODO enable|disable alignment music
		} else if (symbol.is("clipping")) {
			parse("clipping distance EXPRESSION EOL");
			return replace(start, "STATEMENT");
			//TODO enable|disable clipping distance EXPRESSION
		} else if (symbol.is("camera")) {
			parse("camera fixed rotation at COORD_EXPR EOL");
			return replace(start, "STATEMENT");
			//TODO enable|disable camera fixed rotation at COORD_EXPR
		} else if (symbol.is("jc")) {
			parse("jc special on OBJECT EOL");
			return replace(start, "STATEMENT");
			//TODO enable|disable jc special on OBJECT
		} else {
			symbol = parseObject(false);
			if (symbol != null) {
				symbol = peek();
				if (symbol.is("active")) {
					parse("active EOL");
					return replace(start, "STATEMENT");
					//TODO enable|disable OBJECT active
				} else if (symbol.is("attack")) {
					parse("attack own town EOL");
					return replace(start, "STATEMENT");
					//TODO enable|disable OBJECT attack own town
				} else if (symbol.is("reaction")) {
					parse("reaction EOL");
					return replace(start, "STATEMENT");
					//TODO enable|disable OBJECT reaction
				} else if (symbol.is("development")) {
					parse("development script EOL");
					return replace(start, "STATEMENT");
					//TODO enable|disable OBJECT development script
				} else if (symbol.is("spell")) {
					parse("spell reversion EOL");
					return replace(start, "STATEMENT");
					//TODO enable|disable OBJECT spell reversion
				} else if (symbol.is("anim")) {
					parse("anim time modify EOL");
					return replace(start, "STATEMENT");
					//TODO enable|disable OBJECT anim time modify
				} else if (symbol.is("friends")) {
					parse("friends with OBJECT EOL");
					return replace(start, "STATEMENT");
					//TODO enable|disable OBJECT friends with OBJECT
				} else if (symbol.is("auto")) {
					accept("auto");
					symbol = peek();
					if (symbol.is("fighting")) {
						parse("fighting EOL");
						return replace(start, "STATEMENT");
						//TODO enable|disable OBJECT auto fighting
					} else if (symbol.is("scale")) {
						parse("scale EXPRESSION EOL");
						return replace(start, "STATEMENT");
						//TODO enable|disable OBJECT auto scale EXPRESSION
					} else {
						throw new ParseException("Expected: fighting|scale", file, symbol.token.line, symbol.token.col);
					}
				} else if (symbol.is("only")) {
					parse("only for scripts EOL");
					return replace(start, "STATEMENT");
					//TODO enable|disable OBJECT only for scripts
				} else if (symbol.is("poisoned")) {
					parse("poisoned EOL");
					return replace(start, "STATEMENT");
					//TODO enable|disable OBJECT poisoned
				} else if (symbol.is("build")) {
					parse("build worship site EOL");
					return replace(start, "STATEMENT");
					//TODO enable|disable OBJECT build worship site
				} else if (symbol.is("skeleton")) {
					parse("skeleton EOL");
					return replace(start, "STATEMENT");
					//TODO enable|disable OBJECT skeleton
				} else if (symbol.is("indestructible")) {
					parse("indestructible EOL");
					return replace(start, "STATEMENT");
					//TODO enable|disable OBJECT indestructible
				} else if (symbol.is("hurt")) {
					parse("hurt by fire EOL");
					return replace(start, "STATEMENT");
					//TODO enable|disable OBJECT hurt by fire
				} else if (symbol.is("set")) {
					parse("set on fire EOL");
					return replace(start, "STATEMENT");
					//TODO enable|disable OBJECT set on fire
				} else if (symbol.is("on")) {
					parse("on fire EXPRESSION EOL");
					return replace(start, "STATEMENT");
					//TODO enable|disable OBJECT on fire EXPRESSION
				} else if (symbol.is("moveable")) {
					parse("moveable EOL");
					return replace(start, "STATEMENT");
					//TODO enable|disable OBJECT moveable
				} else if (symbol.is("pickup")) {
					parse("pickup EOL");
					return replace(start, "STATEMENT");
					//TODO enable|disable OBJECT pickup
				} else if (symbol.is("high")) {
					parse("high graphics|gfx detail EOL");
					return replace(start, "STATEMENT");
					//TODO enable|disable OBJECT high graphics detail
				} else if (symbol.is("affected")) {
					parse("affected by wind EOL");
					return replace(start, "STATEMENT");
					//TODO enable|disable OBJECT affected by wind
				} else {
					throw new ParseException("Unexpected token: "+symbol, file, symbol.token.line, symbol.token.col);
				}
			} else {
				symbol = parseConstExpr(false);
				if (symbol != null) {
					parse("avi sequence EOL");
					return replace(start, "STATEMENT");
					//TODO enable|disable CONST_EXPR avi sequence
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
			return replace(start, "STATEMENT");
			//TODO close dialogue
		} else {
			parse("open|close OBJECT EOL");
			return replace(start, "STATEMENT");
			//TODO open|close OBJECT
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
				return replace(start, "STATEMENT");
				//TODO teach OBJECT all excluding CONST_EXPR
			} else {
				accept(TokenType.EOL);
				return replace(start, "STATEMENT");
				//TODO teach OBJECT all
			}
		} else {
			parse("CONST_EXPR CONST_EXPR CONST_EXPR CONST_EXPR EOL");
			return replace(start, "STATEMENT");
			//TODO teach OBJECT CONST_EXPR CONST_EXPR CONST_EXPR CONST_EXPR
		}
	}
	
	private SymbolInstance parseForce() throws ParseException {
		final int start = it.nextIndex();
		accept("force");
		SymbolInstance symbol = peek();
		if (symbol.is("action")) {
			parse("action OBJECT finish EOL");
			return replace(start, "STATEMENT");
			//TODO force action OBJECT finish
		} else if (symbol.is("computer")) {
			parse("computer player EXPRESSION action STRING OBJECT OBJECT EOL");
			return replace(start, "STATEMENT");
			//TODO force computer player EXPRESSION action STRING OBJECT OBJECT
		} else {
			parse("OBJECT CONST_EXPR OBJECT");
			symbol = peek();
			if (symbol.is("with")) {
				parse("with OBJECT");
			} else {
				//TODO default object: 0
			}
			accept(TokenType.EOL);
			return replace(start, "STATEMENT");
			//TODO force OBJECT CONST_EXPR OBJECT [with OBJECT]
		}
	}
	
	private SymbolInstance parseInitialise() throws ParseException {
		final int start = it.nextIndex();
		parse("initialise number of constant for OBJECT EOL");
		return replace(start, "STATEMENT");
		//TODO initialise number of constant for OBJECT
	}
	
	private SymbolInstance parseClear() throws ParseException {
		final int start = it.nextIndex();
		accept("clear");
		SymbolInstance symbol = peek();
		if (symbol.is("dropped")) {
			parse("dropped by OBJECT EOL");
			return replace(start, "STATEMENT");
			//TODO clear dropped by OBJECT
		} else if (symbol.is("computer")) {
			parse("computer player EXPRESSION actions EOL");
			return replace(start, "STATEMENT");
			//TODO clear computer player EXPRESSION actions
		} else if (symbol.is("clicked")) {
			symbol = peek();
			if (symbol.is("object")) {
				parse("object EOL");
				return replace(start, "STATEMENT");
				//TODO clear clicked object
			} else if (symbol.is("position")) {
				parse("position EOL");
				return replace(start, "STATEMENT");
				//TODO clear clicked position
			} else {
				throw new ParseException("Expected: object|position", file, symbol.token.line, symbol.token.col);
			}
		} else if (symbol.is("hit")) {
			parse("hit object EOL");
			return replace(start, "STATEMENT");
			//TODO clear hit object
		} else if (symbol.is("player")) {
			parse("player EXPRESSION spell charging EOL");
			return replace(start, "STATEMENT");
			//TODO clear player EXPRESSION spell charging
		} else if (symbol.is("dialogue")) {
			parse("dialogue EOL");
			return replace(start, "STATEMENT");
			//TODO clear dialogue
		} else if (symbol.is("clipping")) {
			parse("clipping window time EXPRESSION EOL");
			return replace(start, "STATEMENT");
			//TODO clear clipping window time EXPRESSION
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
			return replace(start, "STATEMENT");
			//TODO attach reaction OBJECT ENUM_REACTION
		} else if (symbol.is("music")) {
			parse("music CONST_EXPR to OBJECT EOL");
			return replace(start, "STATEMENT");
			//TODO attach music CONST_EXPR to OBJECT
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
			return replace(start, "STATEMENT");
			//TODO attach [3d] sound tag CONST_EXPR [CONST_EXPR] to OBJECT
		} else {
			parseObject(true);
			symbol = peek();
			if (symbol.is("leash")) {
				parse("leash to");
				symbol = peek();
				if (symbol.is("hand")) {
					parse("hand EOL");
					return replace(start, "STATEMENT");
					//TODO attach OBJECT leash to hand
				} else {
					parse("OBJECT EOL");
					return replace(start, "STATEMENT");
					//TODO attach OBJECT leash to OBJECT
				}
			} else if (symbol.is("to")) {
				accept("to");
				symbol = peek();
				if (symbol.is("game")) {
					parse("game OBJECT for PLAYING_SIDE team EOL");
					return replace(start, "STATEMENT");
					//TODO attach OBJECT to game OBJECT for PLAYING_SIDE team
				} else {
					parseObject(true);
					parseOption("as leader");
					accept(TokenType.EOL);
					return replace(start, "STATEMENT");
					//TODO attach OBJECT to OBJECT [as leader]
				}
			} else {
				throw new ParseException("Expected: leash|to", file, symbol.token.line, symbol.token.col);
			}
		}
	}
	
	private SymbolInstance parseToggle() throws ParseException {
		final int start = it.nextIndex();
		parse("toggle player EXPRESSION leash EOL");
		return replace(start, "STATEMENT");
		//TODO toggle player EXPRESSION leash
	}
	
	private SymbolInstance parseDetach() throws ParseException {
		final int start = it.nextIndex();
		accept("detach");
		SymbolInstance symbol = peek();
		if (symbol.is("player")) {
			parse("player from OBJECT from PLAYING_SIDE team EOL");
			return replace(start, "STATEMENT");
			//TODO detach player from OBJECT from PLAYING_SIDE team
		} else if (symbol.is("sound")) {
			parse("sound tag CONST_EXPR");
			symbol = peek();
			if (symbol.is("from")) {
				//TODO default
			} else {
				parseConstExpr(true);
			}
			parse("from OBJECT EOL");
			return replace(start, "STATEMENT");
			//TODO detach sound tag CONST_EXPR [CONST_EXPR] from OBJECT
		} else if (symbol.is("reaction")) {
			parse("detach reaction OBJECT EOL");
			return replace(start, "STATEMENT");
			//TODO detach reaction OBJECT
		} else if (symbol.is("music")) {
			parse("music from OBJECT EOL");
			return replace(start, "STATEMENT");
			//TODO detach music from OBJECT
		} else if (symbol.is("from")) {
			//TODO default OBJECT
			parse("from OBJECT EOL");
			return replace(start, "STATEMENT");
			//TODO detach [OBJECT] from OBJECT
		} else {
			parseObject(true);
			symbol = peek();
			if (symbol.is("leash")) {
				parse("leash EOL");
				return replace(start, "STATEMENT");
				//TODO detach OBJECT leash
			} else if (symbol.is("in")) {
				parse("in game OBJECT from PLAYING_SIDE team EOL");
				return replace(start, "STATEMENT");
				//TODO detach OBJECT in game OBJECT from PLAYING_SIDE team
			} else if (symbol.is("from")) {
				parse("from OBJECT EOL");
				return replace(start, "STATEMENT");
				//TODO detach [OBJECT] from OBJECT
			} else {
				throw new ParseException("Expected: leash|in|from", file, symbol.token.line, symbol.token.col);
			}
		}
	}
	
	private SymbolInstance parseSwap() throws ParseException {
		final int start = it.nextIndex();
		parse("swap creature from OBJECT to OBJECT EOL");
		return replace(start, "STATEMENT");
		//TODO swap creature from OBJECT to OBJECT
	}
	
	private SymbolInstance parseQueue() throws ParseException {
		final int start = it.nextIndex();
		accept("queue");
		SymbolInstance symbol = peek();
		if (symbol.is("computer")) {
			parse("computer player EXPRESSION action STRING OBJECT OBJECT EOL");
			return replace(start, "STATEMENT");
			//TODO queue computer player EXPRESSION action STRING OBJECT OBJECT
		} else {
			parse("OBJECT fight");
			symbol = peek();
			if (symbol.is("move")) {
				parse("move CONST_EXPR EOL");
				return replace(start, "STATEMENT");
				//TODO queue OBJECT fight move FIGHT_MOVE
			} else if (symbol.is("step")) {
				parse("step CONST_EXPR EOL");
				return replace(start, "STATEMENT");
				//TODO queue OBJECT fight step CONST_EXPR
			} else if (symbol.is("spell")) {
				parse("spell CONST_EXPR EOL");
				return replace(start, "STATEMENT");
				//TODO queue OBJECT fight spell CONST_EXPR
			} else {
				throw new ParseException("Expected: move|step|spell", file, symbol.token.line, symbol.token.col);
			}
		}
	}
	
	private SymbolInstance parsePauseUnpause() throws ParseException {
		final int start = it.nextIndex();
		parse("pause|unpause computer player EXPRESSION EOL");
		return replace(start, "STATEMENT");
		//TODO pause|unpause computer player EXPRESSION
	}
	
	private SymbolInstance parseLoad() throws ParseException {
		final int start = it.nextIndex();
		accept("load");
		SymbolInstance symbol = peek();
		if (symbol.is("computer")) {
			parse("computer player EXPRESSION personality STRING EOL");
			return replace(start, "STATEMENT");
			//TODO load computer player EXPRESSION personality STRING
		} else if (symbol.is("map")) {
			parse("map STRING EOL");
			return replace(start, "STATEMENT");
			//TODO load map STRING
		} else if (symbol.is("my_creature")) {
			parse("my_creature at COORD_EXPR EOL");
			return replace(start, "STATEMENT");
			//TODO load my_creature at COORD_EXPR
		} else if (symbol.is("creature")) {
			parse("creature CONST_EXPR STRING player EXPRESSION at COORD_EXPR EOL");
			return replace(start, "STATEMENT");
			//TODO load creature CONST_EXPR STRING player EXPRESSION at COORD_EXPR
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
			return replace(start, "STATEMENT");
			//TODO save computer player EXPRESSION personality STRING
		} else if (symbol.is("game")) {
			parse("game in slot EXPRESSION EOL");
			return replace(start, "STATEMENT");
			//TODO save game in slot EXPRESSION
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
				return replace(start, "STATEMENT");
				//TODO stop all games for OBJECT
			} else if (symbol.is("scripts")) {
				parse("scripts excluding");
				symbol = peek();
				if (symbol.is("files")) {
					parse("files STRING EOL");
					return replace(start, "STATEMENT");
					//TODO stop all scripts excluding files STRING
				} else {
					parse("STRING EOL");
					return replace(start, "STATEMENT");
					//TODO stop all scripts excluding STRING
				}
			} else if (symbol.is("immersion")) {
				parse("immersion EOL");
				return replace(start, "STATEMENT");
				//TODO stop all immersion
			} else {
				throw new ParseException("Expected: games|scripts|immersion", file, symbol.token.line, symbol.token.col);
			}
		} else if (symbol.is("script")) {
			parse("script STRING EOL");
			return replace(start, "STATEMENT");
			//TODO stop script STRING
		} else if (symbol.is("scripts")) {
			parse("scripts in");
			symbol = peek();
			if (symbol.is("files")) {
				parse("files STRING EOL");
				return replace(start, "STATEMENT");
				//TODO stop scripts in files STRING
			} else if (symbol.is("file")) {
				parse("file STRING excluding STRING EOL");
				return replace(start, "STATEMENT");
				//TODO stop scripts in file STRING excluding STRING
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
			return replace(start, "STATEMENT");
			//TODO stop sound CONST_EXPR [CONST_EXPR]
		} else if (symbol.is("immersion")) {
			parse("immersion CONST_EXPR EOL");
			return replace(start, "STATEMENT");
			//TODO stop immersion IMMERSION_EFFECT_TYPE
		} else if (symbol.is("music")) {
			parse("music EOL");
			return replace(start, "STATEMENT");
			//TODO stop music
		} else {
			parse("SPIRIT_TYPE spirit");
			symbol = peek();
			if (symbol.is("pointing")) {
				parse("pointing EOL");
				return replace(start, "STATEMENT");
				//TODO stop SPIRIT_TYPE spirit pointing
			} else if (symbol.is("looking")) {
				parse("looking EOL");
				return replace(start, "STATEMENT");
				//TODO stop SPIRIT_TYPE spirit looking
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
			return replace(start, "STATEMENT");
			//TODO start say [extra] sound CONST_EXPR [at COORD_EXPR]
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
			return replace(start, "STATEMENT");
			//TODO start sound CONST_EXPR [CONST_EXPR] [at COORD_EXPR]
		} else if (symbol.is("immersion")) {
			parse("immersion CONST_EXPR EOL");
			return replace(start, "STATEMENT");
			//TODO start immersion IMMERSION_EFFECT_TYPE
		} else if (symbol.is("music")) {
			parse("music CONST_EXPR EOL");
			return replace(start, "STATEMENT");
			//TODO start music CONST_EXPR
		} else if (symbol.is("hand")) {
			parse("hand demo STRING");
			parseOption("with pause");
			parseOption("without hand modify");
			accept(TokenType.EOL);
			return replace(start, "STATEMENT");
			//TODO start hand demo STRING [with pause] [without hand modify]
		} else if (symbol.is("jc")) {
			parse("jc special CONST_EXPR EOL");
			return replace(start, "STATEMENT");
			//TODO start jc special CONST_EXPR
		} else {
			parseObject(true);
			symbol = peek();
			if (symbol.is("with")) {
				parse("with OBJECT as referee EOL");
				return replace(start, "STATEMENT");
				//TODO start OBJECT with OBJECT as referee
			} else if (symbol.is("fade")) {
				parse("fade out EOL");
				return replace(start, "STATEMENT");
				//TODO start OBJECT fade out
			} else {
				throw new ParseException("Expected: with|fade", file, symbol.token.line, symbol.token.col);
			}
		}
	}
	
	private SymbolInstance parseDisband() throws ParseException {
		final int start = it.nextIndex();
		parse("disband OBJECT EOL");
		return replace(start, "STATEMENT");
		//TODO disband OBJECT
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
		return replace(start, "STATEMENT");
		//TODO populate OBJECT with EXPRESSION CONST_EXPR [CONST_EXPR]
	}
	
	private SymbolInstance parseAffect() throws ParseException {
		final int start = it.nextIndex();
		parse("affect alignment by EXPRESSION EOL");
		return replace(start, "STATEMENT");
		//TODO affect alignment by EXPRESSION
	}
	
	private SymbolInstance parseSnapshot() throws ParseException {
		final int start = it.nextIndex();
		SymbolInstance script = parse("snapshot quest|challenge [success EXPRESSION] [alignment EXPRESSION] CONST_EXPR IDENTIFIER")[7];
		String scriptName = script.token.value;
		int argc = 0;
		SymbolInstance symbol = peek();
		if (symbol.is("(")) {
			argc = parseParameters();
		}
		accept(TokenType.EOL);
		return replace(start, "STATEMENT");
		//TODO snapshot quest|challenge [success EXPRESSION] [alignment EXPRESSION] CONST_EXPR SCRIPT[(PARAMETERS)]
	}
	
	private SymbolInstance parseUpdate() throws ParseException {
		final int start = it.nextIndex();
		parse("update snapshot");
		SymbolInstance symbol = peek();
		if (symbol.is("details")) {
			parse("details [success EXPRESSION] [alignment EXPRESSION] CONST_EXPR");
			parseOption("taking picture");
			accept(TokenType.EOL);
			return replace(start, "STATEMENT");
			//TODO update snapshot details [success EXPRESSION] [alignment EXPRESSION] CONST_EXPR [taking picture]
		} else {
			SymbolInstance script = parse("[success EXPRESSION] [alignment EXPRESSION] CONST_EXPR IDENTIFIER")[5];
			String scriptName = script.token.value;
			int argc = 0;
			symbol = peek();
			if (symbol.is("(")) {
				argc = parseParameters();
			}
			accept(TokenType.EOL);
			return replace(start, "STATEMENT");
			//TODO update snapshot [success EXPRESSION] [alignment EXPRESSION] CONST_EXPR SCRIPT[(PARAMETERS)]
		}
	}
	
	private SymbolInstance parseBuild() throws ParseException {
		final int start = it.nextIndex();
		parse("build building at COORD_EXPR desire EXPRESSION EOL");
		return replace(start, "STATEMENT");
		//TODO build building at COORD_EXPR desire EXPRESSION
	}
	
	private SymbolInstance parseRun() throws ParseException {
		final int start = it.nextIndex();
		accept("run");
		SymbolInstance symbol = peek();
		if (symbol.is("script")) {
			SymbolInstance script = parse("script IDENTIFIER")[1];
			String scriptName = script.token.value;
			int argc = 0;
			symbol = peek();
			if (symbol.is("(")) {
				argc = parseParameters();
			}
			accept(TokenType.EOL);
			return replace(start, "STATEMENT");
			//TODO run script IDENTIFIER[(PARAMETERS)]
		} else if (symbol.is("map")) {
			parse("map script line STRING EOL");
			return replace(start, "STATEMENT");
			//TODO run map script line STRING
		} else if (symbol.is("background")) {
			SymbolInstance script = parse("background script IDENTIFIER")[2];
			String scriptName = script.token.value;
			int argc = 0;
			symbol = peek();
			if (symbol.is("(")) {
				argc = parseParameters();
			}
			accept(TokenType.EOL);
			return replace(start, "STATEMENT");
			//TODO run background script IDENTIFIER[(PARAMETERS)]
		} else {
			parse("CONST_EXPR developer function EOL");
			return replace(start, "STATEMENT");
			//TODO run CONST_EXPR developer function
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
		return replace(start, "STATEMENT");
		//TODO wait until CONDITION
	}
	
	private SymbolInstance parseEnterExit() throws ParseException {
		final int start = it.nextIndex();
		parse("enter|exit temple EOL");
		return replace(start, "STATEMENT");
		//TODO enter|exit temple
	}
	
	private SymbolInstance parseRestart() throws ParseException {
		final int start = it.nextIndex();
		accept("restart");
		SymbolInstance symbol = peek();
		if (symbol.is("music")) {
			parse("music on OBJECT EOL");
			return replace(start, "STATEMENT");
			//TODO restart music on OBJECT
		} else {
			parse("OBJECT EOL");
			return replace(start, "STATEMENT");
			//TODO restart OBJECT
		}
	}
	
	private SymbolInstance parseState() throws ParseException {
		final int start = it.nextIndex();
		parse("state OBJECT CONST_EXPR EOL position COORD_EXPR EOL");
		
		parse("float EXPRESSION EOL");
		
		parse("ulong EXPRESSION , EXPRESSION EOL");
		
		return replace(start, "STATEMENT");
		//TODO state OBJECT CONST_EXPR position COORD_EXPR float EXPRESSION ulong EXPRESSION, EXPRESSION
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
					return replace(start, "STATEMENT");
					//TODO make SPIRIT_TYPE spirit point to OBJECT [in world]
				} else if (symbol.is("at")) {
					parse("at COORD_EXPR EOL");
					return replace(start, "STATEMENT");
					//TODO make SPIRIT_TYPE spirit point at COORD_EXPR
				} else {
					throw new ParseException("Expected: to|at", file, symbol.token.line, symbol.token.col);
				}
			} else if (symbol.is("play")) {
				parse("play across EXPRESSION down EXPRESSION CONST_EXPR [speed EXPRESSION] EOL");
				return replace(start, "STATEMENT");
				//TODO make SPIRIT_TYPE spirit play across EXPRESSION down EXPRESSION CONST_EXPR [speed EXPRESSION]
			} else if (symbol.is("cling")) {
				parse("cling across EXPRESSION down EXPRESSION EOL");
				return replace(start, "STATEMENT");
				//TODO make SPIRIT_TYPE spirit cling across EXPRESSION down EXPRESSION
			} else if (symbol.is("fly")) {
				parse("fly across EXPRESSION down EXPRESSION EOL");
				return replace(start, "STATEMENT");
				//TODO make SPIRIT_TYPE spirit fly across EXPRESSION down EXPRESSION
			} else if (symbol.is("look")) {
				parse("look at");
				symbol = parseObject(false);
				if (symbol != null) {
					accept(TokenType.EOL);
					return replace(start, "STATEMENT");
					//TODO make SPIRIT_TYPE spirit look at OBJECT
				} else {
					symbol = parseCoordExpr(false);
					if (symbol != null) {
						accept(TokenType.EOL);
						return replace(start, "STATEMENT");
						//TODO make SPIRIT_TYPE spirit look at COORD_EXPR
					} else {
						symbol = peek();
						throw new ParseException("Expected: OBJECT|COORD_EXPR", file, symbol.token.line, symbol.token.col);
					}
				}
			} else if (symbol.is("appear")) {
				parse("appear EOL");
				return replace(start, "STATEMENT");
				//TODO make SPIRIT_TYPE spirit appear
			} else {
				throw new ParseException("Expected: point|play|cling|fly|look|appear", file, symbol.token.line, symbol.token.col);
			}
		} else {
			parse("OBJECT dance CONST_EXPR around COORD_EXPR time EXPRESSION EOL");
			return replace(start, "STATEMENT");
			//TODO make OBJECT dance CONST_EXPR around COORD_EXPR time EXPRESSION
		}
	}
	
	private SymbolInstance parseEject() throws ParseException {
		final int start = it.nextIndex();
		parse("eject SPIRIT_TYPE spirit EOL");
		return replace(start, "STATEMENT");
		//TODO eject SPIRIT_TYPE spirit
	}
	
	private SymbolInstance parseDisappear() throws ParseException {
		final int start = it.nextIndex();
		parse("disappear SPIRIT_TYPE spirit EOL");
		return replace(start, "STATEMENT");
		//TODO disappear SPIRIT_TYPE spirit
	}
	
	private SymbolInstance parseSend() throws ParseException {
		final int start = it.nextIndex();
		parse("send SPIRIT_TYPE spirit home EOL");
		return replace(start, "STATEMENT");
		//TODO send SPIRIT_TYPE spirit home
	}
	
	private SymbolInstance parseSay() throws ParseException {
		//This syntax is ambiguous for a top-down parser, so we do some look ahead and swap as workaround
		final int start = it.nextIndex();
		accept("say");
		SymbolInstance symbol = peek();
		if (symbol.is("sound")) {
			parse("sound CONST_EXPR playing EOL");
			return replace(start, "STATEMENT");
			//TODO say sound CONST_EXPR playing
		} else if (symbol.is(TokenType.STRING) && peek(1).is("with") && peek(2).is("number")) {
			parse("STRING with number EXPRESSION EOL");
			return replace(start, "STATEMENT");
			//TODO say STRING with number EXPRESSION
		} else if (symbol.is("single")) {
			parseOption("single line");
			symbol = peek();
			if (symbol.is(TokenType.STRING)) {
				parseString();
				parseOption("with interaction");
				accept(TokenType.EOL);
				return replace(start, "STATEMENT");
				//TODO say [single line] STRING [with interaction]
			} else {
				parseConstExpr(true);
				parseOption("with interaction");
				accept(TokenType.EOL);
				return replace(start, "STATEMENT");
				//TODO say [single line] CONST_EXPR [with interaction]
			}
		} else if (symbol.is(TokenType.STRING)) {
			parseOption("single line");
			parseString();
			parseOption("with interaction");
			accept(TokenType.EOL);
			return replace(start, "STATEMENT");
			//TODO say [single line] STRING [with interaction]
		} else {
			parseConstExpr(true);
			if (peek().is("with") && peek(1).is("number")) {
				parse("with number EXPRESSION EOL");
				return replace(start, "STATEMENT");
				//TODO say CONST_EXPR with number EXPRESSION
			} else {
				//TODO push [single line]=false and swap it with previous CONST_EXPR
				parseOption("with interaction");
				accept(TokenType.EOL);
				return replace(start, "STATEMENT");
				//TODO say [single line] CONST_EXPR [with interaction]
			}
		}
	}
	
	private SymbolInstance parseDraw() throws ParseException {
		final int start = it.nextIndex();
		parse("draw text");
		SymbolInstance symbol = peek();
		if (symbol.is(TokenType.STRING)) {
			parse("STRING across EXPRESSION down EXPRESSION width EXPRESSION height EXPRESSION size EXPRESSION fade in time EXPRESSION second|seconds EOL");
			return replace(start, "STATEMENT");
			//TODO draw text STRING across EXPRESSION down EXPRESSION width EXPRESSION height EXPRESSION size EXPRESSION fade in time EXPRESSION second|seconds
		} else {
			parse("CONST_EXPR across EXPRESSION down EXPRESSION width EXPRESSION height EXPRESSION size EXPRESSION fade in time EXPRESSION second|seconds EOL");
			return replace(start, "STATEMENT");
			//TODO draw text CONST_EXPR across EXPRESSION down EXPRESSION width EXPRESSION height EXPRESSION size EXPRESSION fade in time EXPRESSION second|seconds
		}
	}
	
	private SymbolInstance parseFade() throws ParseException {
		final int start = it.nextIndex();
		accept("fade");
		SymbolInstance symbol = peek();
		if (symbol.is("all")) {
			parse("all draw text time EXPRESSION second|seconds EOL");
			return replace(start, "STATEMENT");
			//TODO fade all draw text time EXPRESSION second|seconds
		} else if (symbol.is("ready")) {
			parse("ready EOL");
			return replace(start, "STATEMENT");
			//TODO fade ready
		} else {
			throw new ParseException("Expected: all|ready", file, symbol.token.line, symbol.token.col);
		}
	}
	
	private SymbolInstance parseStore() throws ParseException {
		final int start = it.nextIndex();
		parse("store camera details EOL");
		return replace(start, "STATEMENT");
		//TODO store camera details
	}
	
	private SymbolInstance parseRestore() throws ParseException {
		final int start = it.nextIndex();
		parse("restore camera details EOL");
		return replace(start, "STATEMENT");
		//TODO restore camera details
	}
	
	private SymbolInstance parseReset() throws ParseException {
		final int start = it.nextIndex();
		parse("reset camera lens EOL");
		return replace(start, "STATEMENT");
		//TODO reset camera lens
	}
	
	private SymbolInstance parseCamera() throws ParseException {
		final int start = it.nextIndex();
		accept("camera");
		SymbolInstance symbol = peek();
		if (symbol.is("follow")) {
			parse("follow OBJECT distance EXPRESSION EOL");
			return replace(start, "STATEMENT");
			//TODO camera follow OBJECT distance EXPRESSION
		} else if (symbol.is("path")) {
			parse("path CONST_EXPR EOL");
			return replace(start, "STATEMENT");
			//TODO camera path CONST_EXPR
		} else if (symbol.is("ready")) {
			parse("ready EOL");
			return replace(start, "STATEMENT");
			//TODO camera ready
		} else if (symbol.is("not")) {
			parse("not ready EOL");
			return replace(start, "STATEMENT");
			//TODO camera not ready
		} else if (symbol.is("position")) {
			parse("position EOL");
			return replace(start, "STATEMENT");
			//TODO camera position
		} else if (symbol.is("focus")) {
			parse("focus EOL");
			return replace(start, "STATEMENT");
			//TODO camera focus
		} else {
			parse("CONST_EXPR EOL");
			return replace(start, "STATEMENT");
			//TODO camera CONST_EXPR
		}
	}
	
	private SymbolInstance parseShake() throws ParseException {
		final int start = it.nextIndex();
		parse("shake camera at COORD_EXPR radius EXPRESSION amplitude EXPRESSION time EXPRESSION EOL");
		return replace(start, "STATEMENT");
		//TODO shake camera at COORD_EXPR radius EXPRESSION amplitude EXPRESSION time EXPRESSION
	}
	
	//TODO optimize assignments
	private SymbolInstance parseAssignment() throws ParseException {
		final int start = it.nextIndex();
		SymbolInstance symbol = peek(1);
		if (symbol.is("of")) {
			//CONSTANT of OBJECT = EXPRESSION
			SymbolInstance[] symbols = parse("CONSTANT of VARIABLE =");
			int constant = getConstant(symbols[0]);
			String var = symbols[2].token.value;
			if (!optimizeAssignmentEnabled) {
				pushi(constant);
				pushf(var);
				sys2(GET_PROPERTY);
				popi();
			}
			parse("EXPRESSION EOL");
			sys2(SET_PROPERTY);
			return replace(start, "STATEMENT");
		} else if (symbol.is("=")) {
			//IDENTIFIER = EXPRESSION
			String var;
			if (optimizeAssignmentEnabled) {
				symbol = parse("IDENTIFIER =")[0];
				var = symbol.token.value;
			} else {
				symbol = parse("VARIABLE =")[0];
				var = symbol.token.value;
				popi();
			}
			symbol = parseExpression(false);
			if (symbol == null) {
				symbol = parseObject(false);
				if (symbol == null) {
					symbol = peek();
					throw new ParseException("Expected: EXPRESSION|OBJECT", file, symbol.token.line, symbol.token.col);
				}
			}
			accept(TokenType.EOL);
			popf(var);
			return replace(start, "STATEMENT");
		} else if (symbol.is("+=")) {
			//VARIABLE += EXPRESSION
			symbol = parse("VARIABLE += EXPRESSION EOL")[0];
			String var = symbol.token.value;
			addf();
			popf(var);
			return replace(start, "STATEMENT");
		} else if (symbol.is("-=")) {
			//VARIABLE -= EXPRESSION
			symbol = parse("VARIABLE -= EXPRESSION EOL")[0];
			String var = symbol.token.value;
			subf();
			popf(var);
			return replace(start, "STATEMENT");
		} else if (symbol.is("*=")) {
			//VARIABLE *= EXPRESSION
			symbol = parse("VARIABLE *= EXPRESSION EOL")[0];
			String var = symbol.token.value;
			mul();
			popf(var);
			return replace(start, "STATEMENT");
		} else if (symbol.is("/=")) {
			//VARIABLE /= EXPRESSION
			symbol = parse("VARIABLE /= EXPRESSION EOL")[0];
			String var = symbol.token.value;
			div();
			popf(var);
			return replace(start, "STATEMENT");
		} else if (symbol.is("++")) {
			//VARIABLE++
			symbol = parse("VARIABLE ++ EOL")[0];
			String var = symbol.token.value;
			pushf(1);
			addf();
			popf(var);
			return replace(start, "STATEMENT");
		} else if (symbol.is("--")) {
			//VARIABLE--
			symbol = parse("VARIABLE -- EOL")[0];
			String var = symbol.token.value;
			pushf(1);
			subf();
			popf(var);
			return replace(start, "STATEMENT");
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
		return replace(start, "IF_ELSIF_ELSE");
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
		return replace(start, "WHILE");
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
			return replace(start, "LOOP");
			//TODO LOOP
		} else if (symbol.is("cinema")) {
			parse("cinema EOL");
			parseStatements();
			parseExceptions();
			parse("end cinema EOL");
			return replace(start, "begin cinema");
			//TODO begin cinema STATEMENTS EXCEPTIONS end cinema
		} else if (symbol.is("camera")) {
			parse("camera EOL");
			parseStatements();
			parseExceptions();
			parse("end camera EOL");
			return replace(start, "begin camera");
			//TODO begin camera STATEMENTS EXCEPTIONS end camera
		} else if (symbol.is("dialogue")) {
			parse("dialogue EOL");
			parseStatements();
			parseExceptions();
			parse("end dialogue EOL");
			return replace(start, "begin dialogue");
			//TODO begin dialogue STATEMENTS EXCEPTIONS end dialogue
		} else if (symbol.is("known")) {
			symbol = peek();
			if (symbol.is("dialogue")) {
				parse("dialogue EOL");
				parseStatements();
				parseExceptions();
				parse("end dialogue EOL");
				return replace(start, "begin known dialogue");
				//TODO begin known dialogue STATEMENTS EXCEPTIONS end dialogue
			} else if (symbol.is("cinema")) {
				parse("cinema EOL");
				parseStatements();
				parseExceptions();
				parse("end cinema EOL");
				return replace(start, "begin known cinema");
				//TODO begin known cinema STATEMENTS EXCEPTIONS end cinema
			} else {
				throw new ParseException("Expected: dialogue|cinema", file, symbol.token.line, symbol.token.col);
			}
		} else if (symbol.is("dual")) {
			parse("dual camera to OBJECT OBJECT EOL");
			parseStatements();
			parseExceptions();
			parse("end dual camera EOL");
			return replace(start, "begin dual camera");
			//TODO begin dual camera to OBJECT OBJECT EOL STATEMENTS EXCEPTIONS EOL end dual camera
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
		return replace(start, "EXCEPTIONS");
		//TODO EXCEPTIONS
	}
	
	private SymbolInstance parseException() throws ParseException {
		final int start = it.nextIndex();
		SymbolInstance symbol = peek();
		if (symbol.is("when")) {
			parse("when CONDITION EOL");
			parseStatements();
			return replace(start, "WHEN");
			//TODO EXCEPTION
		} else if (symbol.is("until")) {
			parse("until CONDITION EOL");
			return replace(start, "UNTIL");
			//TODO EXCEPTION
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
				return replace(start, "EXPRESSION");
			} else if (symbol.is("/")) {
				parseExpression1();
				//EXPRESSION / EXPRESSION
				div();
				return replace(start, "EXPRESSION");
			} else if (symbol.is("%")) {
				parseExpression1();
				//EXPRESSION % EXPRESSION
				mod();
				return replace(start, "EXPRESSION");
			} else if (symbol.is("+")) {
				parseExpression(true);
				//EXPRESSION + EXPRESSION
				addf();
				return replace(start, "EXPRESSION");
			} else if (symbol.is("-")) {
				parseExpression(true);
				//EXPRESSION - EXPRESSION
				subf();
				return replace(start, "EXPRESSION");
			} else if (symbol.is(TokenType.EOL)) {
				seek(start);
				return peek();
			}
		} else if (symbol.is("remove")) {
			parse("remove resource CONST_EXPR EXPRESSION from OBJECT");
			return replace(start, "STATEMENT");
			//TODO remove resource CONST_EXPR EXPRESSION from OBJECT
		} else if (symbol.is("add")) {
			parse("add resource CONST_EXPR EXPRESSION from OBJECT");
			return replace(start, "STATEMENT");
			//TODO add resource CONST_EXPR EXPRESSION from OBJECT
		} else if (symbol.is("alignment")) {
			parse("alignment of player");
			return replace(start, "EXPRESSION");
			//TODO alignment of player
		} else if (symbol.is("raw") || symbol.is("influence")) {
			parseOption("raw");
			parse("influence at COORD_EXPR");
			return replace(start, "EXPRESSION");
			//TODO [raw] influence at COORD_EXPR
		} else if (symbol.is("get")) {
			accept("get");
			symbol = peek();
			if (symbol.is("player")) {
				parse("player EXPRESSION");
				symbol = peek();
				if (symbol.is("influence")) {
					parse("influence at COORD_EXPR");
					return replace(start, "EXPRESSION");
					//TODO get player EXPRESSION influence at COORD_EXPR
				} else if (symbol.is("town")) {
					parse("town total");
					return replace(start, "EXPRESSION");
					//TODO get player EXPRESSION town total
				} else if (symbol.is("time")) {
					parse("time since last spell cast");
					return replace(start, "EXPRESSION");
					//TODO get player EXPRESSION time since last spell cast
				} else if (symbol.is("ally")) {
					parse("ally percentage with player EXPRESSION");
					return replace(start, "EXPRESSION");
					//TODO get player EXPRESSION ally percentage with player EXPRESSION
				}
			} else if (symbol.is("time")) {
				parse("time since");
				symbol = peek();
				if (symbol.is("player")) {
					parse("player EXPRESSION attacked OBJECT");
					return replace(start, "EXPRESSION");
					//TODO get time since player EXPRESSION attacked OBJECT
				} else {
					parse("CONST_EXPR event");
					return replace(start, "EXPRESSION");
					//TODO get time since CONST_EXPR event
				}
			} else if (symbol.is("resource")) {
				parse("resource CONST_EXPR in OBJECT");
				return replace(start, "EXPRESSION");
				//TODO get resource CONST_EXPR in OBJECT
			} else if (symbol.is("number")) {
				parse("number of CONST_EXPR for OBJECT");
				return replace(start, "EXPRESSION");
				//TODO get number of CONST_EXPR for OBJECT
			} else if (symbol.is("inclusion")) {
				parse("inclusion distance");
				return replace(start, "EXPRESSION");
				//TODO get inclusion distance
			} else if (symbol.is("slowest")) {
				parse("slowest speed in OBJECT");
				return replace(start, "EXPRESSION");
				//TODO get slowest speed in OBJECT
			} else if (symbol.is("distance")) {
				parse("distance from COORD_EXPR to COORD_EXPR");
				return replace(start, "EXPRESSION");
				//TODO get distance from COORD_EXPR to COORD_EXPR
			} else if (symbol.is("mana")) {
				parse("mana for spell CONST_EXPR");
				return replace(start, "EXPRESSION");
				//TODO get mana for spell CONST_EXPR
			} else if (symbol.is("building")) {
				parse("building and villager health total in OBJECT");
				return replace(start, "EXPRESSION");
				//TODO get building and villager health total in OBJECT
			} else if (symbol.is("size")) {
				parse("size of OBJECT PLAYING_SIDE team");
				return replace(start, "EXPRESSION");
				//TODO get size of OBJECT PLAYING_SIDE team
			} else if (symbol.is("worship")) {
				parse("worship deaths in OBJECT");
				return replace(start, "EXPRESSION");
				//TODO get worship deaths in OBJECT
			} else if (symbol.is("computer")) {
				parse("computer player EXPRESSION attitude to player EXPRESSION");
				return replace(start, "EXPRESSION");
				//TODO get computer player EXPRESSION attitude to player EXPRESSION
			} else if (symbol.is("moon")) {
				parse("moon percentage");
				return replace(start, "EXPRESSION");
				//TODO get moon percentage
			} else if (symbol.is("game")) {
				parse("game time");
				return replace(start, "EXPRESSION");
				//TODO get game time
			} else if (symbol.is("real")) {
				accept("real");
				symbol = peek();
				if (symbol.is("time")) {
					accept("time");
					return replace(start, "EXPRESSION");
					//TODO get real time
				} else if (symbol.is("day")) {
					accept("day");
					return replace(start, "EXPRESSION");
					//TODO get real day
				} else if (symbol.is("weekday")) {
					accept("weekday");
					return replace(start, "EXPRESSION");
					//TODO get real weekday
				} else if (symbol.is("month")) {
					accept("month");
					return replace(start, "EXPRESSION");
					//TODO get real month
				} else if (symbol.is("year")) {
					accept("year");
					return replace(start, "EXPRESSION");
					//TODO get real year
				}
			} else {
				final int checkpoint = it.nextIndex();
				final int checkpointIp = getIp();
				symbol = parseObject(false);
				if (symbol != null) {
					symbol = peek();
					if (symbol.is("music")) {
						parse("music distance");
						return replace(start, "EXPRESSION");
						//TODO get OBJECT music distance
					} else if (symbol.is("interaction")) {
						parse("interaction magnitude");
						return replace(start, "EXPRESSION");
						//TODO get OBJECT interaction magnitude
					} else if (symbol.is("time")) {
						accept("time");
						symbol = peek();
						if (symbol.is("remaining")) {
							accept("remaining");
							return replace(start, "EXPRESSION");
							//TODO get OBJECT time remaining
						} else if (symbol.is("since")) {
							parse("since set");
							return replace(start, "EXPRESSION");
							//TODO get OBJECT time since set
						}
					} else if (symbol.is("fight")) {
						parse("fight queue hits");
						return replace(start, "EXPRESSION");
						//TODO get OBJECT fight queue hits
					} else if (symbol.is("walk")) {
						parse("walk path percentage");
						return replace(start, "EXPRESSION");
						//TODO get OBJECT walk path percentage
					} else if (symbol.is("mana")) {
						parse("mana total");
						return replace(start, "EXPRESSION");
						//TODO get OBJECT mana total
					} else if (symbol.is("played")) {
						parse("played percentage");
						return replace(start, "EXPRESSION");
						//TODO get OBJECT played percentage
					} else if (symbol.is("belief")) {
						parse("belief for player EXPRESSION");
						return replace(start, "EXPRESSION");
						//TODO get OBJECT belief for player EXPRESSION
					} else if (symbol.is("help")) {
						accept("help");
						return replace(start, "EXPRESSION");
						//TODO get OBJECT help
					} else if (symbol.is("first")) {
						parse("first help");
						return replace(start, "EXPRESSION");
						//TODO get OBJECT first help
					} else if (symbol.is("last")) {
						parse("last help");
						return replace(start, "EXPRESSION");
						//TODO get OBJECT last help
					} else if (symbol.is("fade")) {
						accept("fade");
						return replace(start, "EXPRESSION");
						//TODO get OBJECT fade
					} else if (symbol.is("info")) {
						parse("info bits");
						return replace(start, "EXPRESSION");
						//TODO get OBJECT info bits
					} else if (symbol.is("desire")) {
						parse("desire CONST_EXPR");
						return replace(start, "EXPRESSION");
						//TODO get OBJECT desire CONST_EXPR
					} else if (symbol.is("sacrifice")) {
						parse("sacrifice total");
						return replace(start, "EXPRESSION");
						//TODO get OBJECT sacrifice total
					}
					revert(checkpoint, checkpointIp);
				}
				symbol = parseConstExpr(false);
				if (symbol != null) {
					symbol = peek();
					if (symbol.is("music")) {
						parse("music distance");
						return replace(start, "EXPRESSION");
						//TODO get CONST_EXPR music distance
					} else if (symbol.is("events")) {
						parse("events per second");
						return replace(start, "EXPRESSION");
						//TODO get CONSTANT events per second
					} else if (symbol.is("total")) {
						parse("total events|events");
						return replace(start, "EXPRESSION");
						//TODO get CONSTANT total events|events
					}
					revert(checkpoint, checkpointIp);
				}
			}
		} else if (symbol.is("land")) {
			parse("land height at COORD_EXPR");
			return replace(start, "EXPRESSION");
			//TODO land height at COORD_EXPR
		} else if (symbol.is("time")) {
			accept("time");
			return replace(start, "EXPRESSION");
			//TODO time
		} else if (symbol.is("number")) {
			accept("number");
			symbol = peek();
			if (symbol.is("from")) {
				parse("from EXPRESSION to EXPRESSION");
				//number from EXPRESSION to EXPRESSION
				sys(RANDOM);
				return replace(start, "EXPRESSION");
			} else if (symbol.is("of")) {
				accept("of");
				symbol = peek();
				if (symbol.is("mouse")) {
					parse("mouse buttons");
					return replace(start, "EXPRESSION");
					//TODO number of mouse buttons
				} else if (symbol.is("times")) {
					parse("times action CONST_EXPR by OBJECT");
					return replace(start, "EXPRESSION");
					//TODO number of times action CONST_EXPR by OBJECT
				}
			}
		} else if (symbol.is("size")) {
			parse("size of OBJECT");
			return replace(start, "EXPRESSION");
			//TODO size of OBJECT
		} else if (symbol.is("adult")) {
			accept("adult");
			symbol = peek();
			if (symbol.is("size")) {
				parse("size of OBJECT");
				return replace(start, "EXPRESSION");
				//TODO adult size of OBJECT
			} else if (symbol.is("capacity")) {
				parse("capacity of OBJECT");
				return replace(start, "EXPRESSION");
				//TODO adult capacity of OBJECT
			}
		} else if (symbol.is("capacity")) {
			parse("capacity of OBJECT");
			return replace(start, "EXPRESSION");
			//TODO capacity of OBJECT
		} else if (symbol.is("poisoned")) {
			parse("poisoned size of OBJECT");
			return replace(start, "EXPRESSION");
			//TODO poisoned size of OBJECT
		} else if (symbol.is("square")) {
			parse("square root EXPRESSION");
			return replace(start, "EXPRESSION");
			//TODO square root EXPRESSION
		} else if (symbol.is("-")) {
			parse("- EXPRESSION");
			//-EXPRESSION
			neg();
			return replace(start, "EXPRESSION");
		} else if (symbol.is("variable")) {
			parse("variable CONST_EXPR");
			//variable CONST_EXPR
			castf();
			return replace(start, "EXPRESSION");
		} else if (symbol.is("(")) {
			parse("( EXPRESSION )");
			//(EXPRESSION)
			return replace(start, "EXPRESSION");
		} else if (symbol.is(TokenType.NUMBER)) {
			symbol = accept(TokenType.NUMBER);
			float val = symbol.token.floatVal();
			//NUMBER
			pushf(val);
			return replace(start, "EXPRESSION");
		} else if (symbol.is(TokenType.IDENTIFIER)) {
			SymbolInstance id1 = accept(TokenType.IDENTIFIER);
			symbol = peek();
			if (symbol.is("of")) {
				SymbolInstance id2 = parse("of IDENTIFIER")[1];
				//CONSTANT of OBJECT
				int property = getConstant(id1.token.value);
				String object = id2.token.value;
				pushi(property);
				pushf(object);
				sys(GET_PROPERTY);
				return replace(start, "EXPRESSION");
			} else {
				//IDENTIFIER
				String var = id1.token.value;
				pushf(var);
				return replace(start, "EXPRESSION");
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
				return replace(start, "CONDITION");
			} else if (symbol.is("or")) {
				parseCondition(true);
				//CONDITION or CONDITION
				or();
				return replace(start, "CONDITION");
			}
		} else if (symbol.is("key")) {
			parse("key CONST_EXPR down");
			return replace(start, "CONDITION");
			//TODO key CONST_EXPR down
		} else if (symbol.is("inside")) {
			parse("inside temple");
			return replace(start, "CONDITION");
			//TODO inside temple
		} else if (symbol.is("within")) {
			parse("within rotation");
			return replace(start, "CONDITION");
			//TODO within rotation
		} else if (symbol.is("hand")) {
			parse("hand demo");
			symbol = peek();
			if (symbol.is("played")) {
				accept("played");
				return replace(start, "CONDITION");
				//TODO hand demo played
			} else if (symbol.is("trigger")) {
				accept("trigger");
				return replace(start, "CONDITION");
				//TODO hand demo trigger
			} else {
				throw new ParseException("Expected: played|trigger", file, symbol.token.line, symbol.token.col);
			}
		} else if (symbol.is("jc")) {
			parse("jc special CONST_EXPR played");
			return replace(start, "CONDITION");
			//TODO jc special CONST_EXPR played
		} else if (symbol.is("fire")) {
			parse("fire near COORD_EXPR radius EXPRESSION");
			return replace(start, "CONDITION");
			//TODO fire near COORD_EXPR radius EXPRESSION
		} else if (symbol.is("spell")) {
			symbol = peek();
			if (symbol.is("wind")) {
				parse("wind near COORD_EXPR radius EXPRESSION");
				return replace(start, "CONDITION");
				//TODO spell wind near COORD_EXPR radius EXPRESSION
			} else if (symbol.is("charging")) {
				accept("charging");
				return replace(start, "CONDITION");
				//TODO spell charging
			} else {
				parse("CONST_EXPR for player EXPRESSION");
				return replace(start, "CONDITION");
				//TODO spell CONST_EXPR for player EXPRESSION
			}
		} else if (symbol.is("camera")) {
			parse("camera");
			symbol = peek();
			if (symbol.is("ready")) {
				accept("ready");
				return replace(start, "CONDITION");
				//TODO camera ready
			} else if (symbol.is("not")) {
				parse("not ready");
				return replace(start, "CONDITION");
				//TODO camera not ready
			} else if (symbol.is("position")) {
				parse("position near COORD_EXPR radius EXPRESSION");
				return replace(start, "CONDITION");
				//TODO camera position near COORD_EXPR radius EXPRESSION
			} else {
				throw new ParseException("Expected: ready|not", file, symbol.token.line, symbol.token.col);
			}
		} else if (symbol.is("widescreen")) {
			parse("widescreen ready");
			return replace(start, "CONDITION");
			//TODO widescreen ready
		} else if (symbol.is("fade")) {
			parse("fade ready");
			return replace(start, "CONDITION");
			//TODO fade ready
		} else if (symbol.is("dialogue")) {
			accept("dialogue");
			symbol = peek();
			if (symbol.is("ready")) {
				accept("ready");
				return replace(start, "CONDITION");
				//TODO dialogue ready
			} else if (symbol.is("not")) {
				parse("not ready");
				return replace(start, "CONDITION");
				//TODO dialogue not ready
			} else {
				throw new ParseException("Expected: ready|not", file, symbol.token.line, symbol.token.col);
			}
		} else if (symbol.is("computer")) {
			parse("computer player EXPRESSION ready");
			return replace(start, "CONDITION");
			//TODO computer player EXPRESSION ready
		} else if (symbol.is("player")) {
			accept("player");
			symbol = peek();
			if (symbol.is("has")) {
				parse("has mouse wheel");
				return replace(start, "CONDITION");
				//TODO player has mouse wheel
			} else {
				parse("EXPRESSION wind resistance");
				return replace(start, "CONDITION");
				//TODO player EXPRESSION wind resistance
			}
		} else if (symbol.is("creature")) {
			parse("creature CONST_EXPR is available");
			return replace(start, "CONDITION");
			//TODO creature CONST_EXPR is available
		} else if (symbol.is("get")) {
			parse("get desire of OBJECT is CONST_EXPR");
			return replace(start, "CONDITION");
			//TODO get desire of OBJECT is CONST_EXPR
		} else if (symbol.is("read")) {
			accept("read");
			return replace(start, "CONDITION");
			//TODO read
		} else if (symbol.is("help")) {
			parse("help system on");
			return replace(start, "CONDITION");
			//TODO help system on
		} else if (symbol.is("immersion")) {
			parse("immersion exists");
			return replace(start, "CONDITION");
			//TODO immersion exists
		} else if (symbol.is("sound")) {
			accept("sound");
			symbol = peek();
			if (symbol.is("exists")) {
				accept("exists");
				return replace(start, "CONDITION");
				//TODO sound exists
			} else {
				parseConstExpr(true);
				symbol = peek();
				if (symbol.is("playing")) {
					//TODO default CONST_EXPR
				} else {
					parseConstExpr(true);
				}
				accept("playing");
				return replace(start, "CONDITION");
				//TODO sound CONST_EXPR [CONST_EXPR] playing
			}
		} else if (symbol.is("specific")) {
			parse("specific spell charging");
			//TODO specific spell charging
			return replace(start, "CONDITION");
		} else if (symbol.is("music")) {
			parse("music line EXPRESSION");
			//TODO music line EXPRESSION
			return replace(start, "CONDITION");
		} else if (symbol.is("not")) {
			parse("not CONDITION");
			//not CONDITION
			not();
			return replace(start, "CONDITION");
		} else if (symbol.is("say")) {
			parse("say sound CONST_EXPR playing");
			//TODO say sound CONST_EXPR playing
			return replace(start, "CONDITION");
		} else if (symbol.is("(")) {
			parse("( CONDITION )");
			//(CONDITION)
			return replace(start, "CONDITION");
		} else if (peek(1).is("spirit")) {
			parse("SPIRIT_TYPE spirit");
			symbol = peek();
			if (symbol.is("played")) {
				accept("played");
				//TODO SPIRIT_TYPE spirit played
				return replace(start, "CONDITION");
			} else if (symbol.is("speaks")) {
				parse("speaks CONST_EXPR");
				//TODO SPIRIT_TYPE spirit speaks CONST_EXPR
				return replace(start, "CONDITION");
			}
		} else {
			final int checkpoint = it.nextIndex();
			final int checkpointIp = getIp();
			symbol = parseObject(false);
			if (symbol != null) {
				symbol = peek();
				if (symbol.is("active")) {
					accept("active");
					//TODO OBJECT active
					return replace(start, "CONDITION");
				} else if (symbol.is("viewed")) {
					accept("viewed");
					//TODO OBJECT viewed
					return replace(start, "CONDITION");
				} else if (symbol.is("can")) {
					parse("can view camera in EXPRESSION degrees");
					//TODO OBJECT can view camera in EXPRESSION degrees
					return replace(start, "CONDITION");
				} else if (symbol.is("within")) {
					parse("within flock distance");
					//TODO OBJECT within flock distance
					return replace(start, "CONDITION");
				} else if (symbol.is("clicked")) {
					accept("clicked");
					//TODO OBJECT clicked
					return replace(start, "CONDITION");
				} else if (symbol.is("hit")) {
					accept("hit");
					//TODO OBJECT hit
					return replace(start, "CONDITION");
				} else if (symbol.is("locked")) {
					parse("within locked interaction");
					//TODO OBJECT locked interaction
					return replace(start, "CONDITION");
				} else if (symbol.is("not")) {
					accept("not");
					symbol = peek();
					if (symbol.is("clicked")) {
						accept("clicked");
						//TODO OBJECT not clicked
						return replace(start, "CONDITION");
					} else if (symbol.is("viewed")) {
						accept("viewed");
						//TODO OBJECT not viewed
						return replace(start, "CONDITION");
					} else if (symbol.is("in")) {
						parse("in OBJECT");
						symbol = peek();
						if (symbol.is("hand")) {
							accept("hand");
							//TODO OBJECT not in OBJECT hand
							return replace(start, "CONDITION");
						} else {
							//TODO OBJECT not in OBJECT 
							return replace(start, "CONDITION");
						}
					} else if (symbol.is("exists")) {
						accept("exists");
						//TODO OBJECT not exists
						return replace(start, "CONDITION");
					}
				} else if (symbol.is("played")) {
					accept("played");
					//TODO OBJECT played
					return replace(start, "CONDITION");
				} else if (symbol.is("music")) {
					parse("music played");
					//TODO OBJECT music played
					return replace(start, "CONDITION");
				} else if (symbol.is("cast")) {
					parse("cast by OBJECT");
					//TODO OBJECT cast by OBJECT
					return replace(start, "CONDITION");
				} else if (symbol.is("poisoned")) {
					accept("poisoned");
					//TODO OBJECT poisoned
					return replace(start, "CONDITION");
				} else if (symbol.is("skeleton")) {
					accept("skeleton");
					//TODO OBJECT skeleton
					return replace(start, "CONDITION");
				} else if (symbol.is("type")) {
					parse("type CONST_EXPR");
					symbol = parseConstExpr(false);
					if (symbol == null) {
						//TODO default CONST_EXPR=SCRIPT_FIND_TYPE_ANY
					}
					//TODO OBJECT type TYPE [CONST_EXPR]
					return replace(start, "CONDITION");
				} else if (symbol.is("on")) {
					parse("on fire");
					//TODO OBJECT on fire
					return replace(start, "CONDITION");
				} else if (symbol.is("in")) {
					parse("in OBJECT");
					symbol = peek();
					if (symbol.is("hand")) {
						accept("hand");
						//TODO OBJECT in OBJECT hand
						return replace(start, "CONDITION");
					} else {
						//TODO OBJECT in OBJECT
						return replace(start, "CONDITION");
					}
				} else if (symbol.is("interacting")) {
					parse("interacting with OBJECT");
					//TODO OBJECT interacting with OBJECT
					return replace(start, "CONDITION");
				} else if (symbol.is("is")) {
					accept("is");
					symbol = peek();
					if (symbol.is("not")) {
						parse("not CONST_EXPR");
						//TODO OBJECT is not CONST_EXPR
						return replace(start, "CONDITION");
					} else {
						parseConstExpr(true);
						//TODO OBJECT is CONST_EXPR
						return replace(start, "CONDITION");
					}
				} else if (symbol.is("exists")) {
					accept("exists");
					//TODO OBJECT exists
					return replace(start, "CONDITION");
				} else if (symbol.is("affected")) {
					parse("affected by spell CONST_EXPR");
					//TODO OBJECT affected by spell CONST_EXPR
					return replace(start, "CONDITION");
				} else if (symbol.is("leashed")) {
					accept("leashed");
					//TODO OBJECT leashed
					return replace(start, "CONDITION");
				} else if (symbol.is("fighting")) {
					accept("fighting");
					//TODO OBJECT fighting
					return replace(start, "CONDITION");
				} else if (symbol.is("male")) {
					accept("male");
					//TODO OBJECT male
					return replace(start, "CONDITION");
				}
				revert(checkpoint, checkpointIp);
			}
			symbol = parseCoordExpr(false);
			if (symbol != null) {
				symbol = peek();
				if (symbol.is("viewed")) {
					accept("viewed");
					//TODO COORD_EXPR viewed
					return replace(start, "CONDITION");
				} else if (symbol.is("valid")) {
					parse("valid for creature");
					//TODO COORD_EXPR valid for creature
					return replace(start, "CONDITION");
				} else if (symbol.is("clicked")) {
					parse("clicked radius EXPRESSION");
					//TODO COORD_EXPR clicked radius EXPRESSION
					return replace(start, "CONDITION");
				} else if (symbol.is("near")) {
					parse("near COORD_EXPR radius EXPRESSION");
					//TODO COORD_EXPR near COORD_EXPR radius EXPRESSION
					return replace(start, "CONDITION");
				} else if (symbol.is("at")) {
					parse("at COORD_EXPR");
					//TODO COORD_EXPR at COORD_EXPR
					return replace(start, "CONDITION");
				} else if (symbol.is("not")) {
					accept("not");
					symbol = peek();
					if (symbol.is("viewed")) {
						accept("viewed");
						//TODO COORD_EXPR not viewed
						return replace(start, "CONDITION");
					} else if (symbol.is("near")) {
						parse("near COORD_EXPR radius EXPRESSION");
						//TODO COORD_EXPR not near COORD_EXPR radius EXPRESSION
						return replace(start, "CONDITION");
					} else if (symbol.is("at")) {
						parse("at COORD_EXPR");
						//TODO COORD_EXPR not at COORD_EXPR
						return replace(start, "CONDITION");
					}
				}
				revert(checkpoint, checkpointIp);
			}
			symbol = parseExpression(false);
			if (symbol != null) {
				symbol = peek();
				if (symbol.is("second") || symbol.is("seconds")) {
					parse("second|seconds");
					//EXPRESSION second|seconds
					sleep();
					return replace(start, "CONDITION");
				} else if (symbol.is("==")) {
					parse("== EXPRESSION");
					//EXPRESSION == EXPRESSION
					eq();
					return replace(start, "CONDITION");
				} else if (symbol.is("!=")) {
					parse("!= EXPRESSION");
					//EXPRESSION != EXPRESSION
					neq();
					return replace(start, "CONDITION");
				} else if (symbol.is(">=")) {
					parse(">= EXPRESSION");
					//EXPRESSION >= EXPRESSION
					geq();
					return replace(start, "CONDITION");
				} else if (symbol.is("<=")) {
					parse("<= EXPRESSION");
					//EXPRESSION <= EXPRESSION
					leq();
					return replace(start, "CONDITION");
				} else if (symbol.is(">")) {
					parse("> EXPRESSION");
					//EXPRESSION > EXPRESSION
					gt();
					return replace(start, "CONDITION");
				} else if (symbol.is("<")) {
					parse("< EXPRESSION");
					//EXPRESSION < EXPRESSION
					lt();
					return replace(start, "CONDITION");
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
				return replace(start, "OBJECT");
				//TODO create random villager of tribe CONST_EXPR at COORD_EXPR
			} else if (symbol.is("highlight")) {
				parse("highlight CONST_EXPR at COORD_EXPR");
				return replace(start, "OBJECT");
				//TODO create highlight HIGHLIGHT_INFO at COORD_EXPR
			} else if (symbol.is("mist")) {
				parse("mist at COORD_EXPR scale EXPRESSION red EXPRESSION green EXPRESSION blue EXPRESSION transparency EXPRESSION height ratio EXPRESSION");
				return replace(start, "OBJECT");
				//TODO create mist at COORD_EXPR scale EXPRESSION red EXPRESSION green EXPRESSION blue EXPRESSION transparency EXPRESSION height ratio EXPRESSION
			} else if (symbol.is("with")) {
				parse("with angle EXPRESSION and scale EXPRESSION CONST_EXPR CONST_EXPR at COORD_EXPR");
				return replace(start, "OBJECT");
				//TODO create with angle EXPRESSION and scale EXPRESSION CONST_EXPR CONST_EXPR at COORD_EXPR
			} else if (symbol.is("timer")) {
				parse("timer for EXPRESSION second|seconds");
				return replace(start, "OBJECT");
				//TODO create timer for EXPRESSION second|seconds
			} else if (symbol.is("influence")) {
				pushi(0);
				accept("influence");
				symbol = peek();
				if (symbol.is("on")) {
					parse("on OBJECT radius EXPRESSION");
					//create influence on OBJECT radius EXPRESSION
					sys(INFLUENCE_OBJECT);
					return replace(start, "OBJECT");
				} else if (symbol.is("at")) {
					parse("at COORD_EXPR radius EXPRESSION");
					//create influence at COORD_EXPR radius EXPRESSION
					sys(INFLUENCE_POSITION);
					return replace(start, "OBJECT");
				}
			} else if (symbol.is("anti")) {
				accept("anti");
				pushi(1);
				accept("influence");
				symbol = peek();
				if (symbol.is("on")) {
					parse("on OBJECT radius EXPRESSION");
					//create anti influence on OBJECT radius EXPRESSION
					sys(INFLUENCE_OBJECT);
					return replace(start, "OBJECT");
				} else if (symbol.is("at")) {
					parse("at position COORD_EXPR radius EXPRESSION");
					//create anti influence at position COORD_EXPR radius EXPRESSION
					sys(INFLUENCE_POSITION);
					return replace(start, "OBJECT");
				}
			} else if (symbol.is("special")) {
				parse("special effect CONST_EXPR");
				symbol = peek();
				if (symbol.is("at")) {
					parse("at COORD_EXPR time EXPRESSION");
					return replace(start, "OBJECT");
					//TODO create special effect CONST_EXPR at COORD_EXPR time EXPRESSION
				} else if (symbol.is("to")) {
					parse("to OBJECT time EXPRESSION");
					return replace(start, "OBJECT");
					//TODO create special effect CONST_EXPR to OBJECT time EXPRESSION
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
				return replace(start, "OBJECT");
				//TODO create CONST_EXPR [CONST_EXPR] at COORD_EXPR
			}
		} else if (symbol.is("create_creature_from_creature")) {
			throw new IllegalStateException("Parsing of \""+symbol+"\" not implemented yet");
			
		} else if (symbol.is("marker")) {
			parse("marker at");
			symbol = parseCoordExpr(false);
			if (symbol != null) {
				return replace(start, "OBJECT");
				//TODO marker at COORD_EXPR
			} else {
				symbol = parseConstExpr(false);
				if (symbol != null) {
					return replace(start, "OBJECT");
					//TODO marker at CONST_EXPR
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
			return replace(start, "OBJECT");
			//TODO VARIABLE
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
				return replace(start, "CONST_EXPR");
				//TODO constant from CONST_EXPR to CONST_EXPR
			} else {
				parseExpression(fail);
				return replace(start, "CONST_EXPR");
				//TODO constant EXPRESSION
			}
		} else if (symbol.is("get")) {
			accept("get");
			symbol = peek();
			if (symbol.is("action")) {
				parse("action text for OBJECT");
				return replace(start, "CONST_EXPR");
				//TODO get action text for OBJECT
			} else if (symbol.is("hand")) {
				parse("hand state");
				return replace(start, "CONST_EXPR");
				//TODO get hand state
			} else if (symbol.is("player")) {
				parse("player EXPRESSION last spell cast");
				return replace(start, "CONST_EXPR");
				//TODO get player EXPRESSION last spell cast
			} else {
				symbol = parseObject(false);
				if (symbol != null) {
					symbol = peek();
					if (symbol.is("type")) {
						accept("type");
						return replace(start, "CONST_EXPR");
						//TODO get OBJECT type
					} else if (symbol.is("sub")) {
						parse("sub type");
						return replace(start, "CONST_EXPR");
						//TODO get OBJECT sub type
					} else if (symbol.is("leash")) {
						parse("leash type");
						return replace(start, "CONST_EXPR");
						//TODO get OBJECT leash type
					} else if (symbol.is("fight")) {
						parse("fight action");
						return replace(start, "CONST_EXPR");
						//TODO get OBJECT fight action
					}
				} else {
					symbol = parseConstExpr(false);
					if (symbol != null) {
						parse("opposite creature type");
						return replace(start, "CONST_EXPR");
						//TODO get CONST_EXPR opposite creature type
					} else if (fail) {
						symbol = peek();
						throw new ParseException("Expected: OBJECT|CONST_EXPR", file, symbol.token.line, symbol.token.col);
					}
				}
			}
		} else if (symbol.is("variable")) {
			parse("variable state of OBJECT");
			return replace(start, "CONST_EXPR");
			//TODO variable state of OBJECT
		} else if (symbol.is("(")) {
			parse("( CONST_EXPR )");
			return replace(start, "CONST_EXPR");
			//TODO (CONST_EXPR)
		} else if (symbol.is(TokenType.NUMBER) || symbol.is(TokenType.IDENTIFIER)) {
			acceptAny(TokenType.NUMBER, TokenType.IDENTIFIER);
			return replace(start, "CONST_EXPR");
			//TODO CONSTANT
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
				//COORD_EXPR / EXPRESSION
				throw new ParseException("Statement not implemented", file, line, col);
				//return replace(start, "COORD_EXPR");
			} else if (mode.is("+") || mode.is("-")) {
				next();
				next();
				parseCoordExpr(true);
				if (mode.is("+")) {
					//COORD_EXPR + COORD_EXPR
					addc();
				} else if (mode.is("-")) {
					//COORD_EXPR - COORD_EXPR
					subc();
				}
				return replace(start, "COORD_EXPR");
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
				sys(GET_POSITION);
				//[OBJECT]
				return replace(start, "COORD_EXPR");
			} else {
				parseExpression(true);
				castc();
				parse(", EXPRESSION");
				castc();
				symbol = peek();
				if (symbol.is(",")) {
					parse(", EXPRESSION");
					castc();
					accept("]");
					//[EXPRESSION, EXPRESSION, EXPRESSION]
					return replace(start, "COORD_EXPR");
				} else if (symbol.is("]")) {
					accept("]");
					pushc(0);
					swapi();
					//[EXPRESSION, EXPRESSION]
					return replace(start, "COORD_EXPR");
				} else {
					throw new ParseException("Unexpected token: "+symbol, file, symbol.token.line, symbol.token.col);
				}
			}
		} else if (symbol.is("camera")) {
			accept("camera");
			symbol = peek();
			if (symbol.is("position")) {
				accept("position");
				//TODO camera position
				return replace(start, "COORD_EXPR");
			} else if (symbol.is("focus")) {
				accept("focus");
				//TODO camera focus
				return replace(start, "COORD_EXPR");
			} else {
				parseConstExpr(true);
				//TODO camera CONST_EXPR
				return replace(start, "COORD_EXPR");
			}
		} else if (symbol.is("stored")) {
			parse("stored camera");
			symbol = peek();
			if (symbol.is("position")) {
				accept("position");
				return replace(start, "COORD_EXPR");
				//TODO stored camera position
			} else if (symbol.is("focus")) {
				accept("focus");
				return replace(start, "COORD_EXPR");
				//TODO stored camera focus
			} else {
				throw new ParseException("Unexpected token: "+symbol, file, symbol.token.line, symbol.token.col);
			}
		} else if (symbol.is("hand")) {
			parse("hand position");
			return replace(start, "COORD_EXPR");
			//TODO hand position
		} else if (symbol.is("facing")) {
			parse("facing camera position distance EXPRESSION");
			return replace(start, "COORD_EXPR");
			//TODO facing camera position distance EXPRESSION
		} else if (symbol.is("computer")) {
			parse("computer player EXPRESSION position");
			return replace(start, "COORD_EXPR");
			//TODO computer player EXPRESSION position
		} else if (symbol.is("last")) {
			parse("last player EXPRESSION spell cast position");
			return replace(start, "COORD_EXPR");
			//TODO last player EXPRESSION spell cast position
		} else if (symbol.is("get")) {
			parse("get target from COORD_EXPR to COORD_EXPR distance EXPRESSION angle EXPRESSION");
			return replace(start, "COORD_EXPR");
			//TODO get target from COORD_EXPR to COORD_EXPR distance EXPRESSION angle EXPRESSION
		} else if (symbol.is("arse")) {
			parse("arse position of OBJECT");
			return replace(start, "COORD_EXPR");
			//TODO arse position of OBJECT
		} else if (symbol.is("belly")) {
			parse("belly position of OBJECT");
			return replace(start, "COORD_EXPR");
			//TODO belly position of OBJECT
		} else if (symbol.is("destination")) {
			parse("destination of OBJECT");
			return replace(start, "COORD_EXPR");
			//TODO destination of OBJECT
		} else if (symbol.is("player")) {
			parse("player EXPRESSION temple");
			if (symbol.is("position")) {
				accept("position");
				return replace(start, "COORD_EXPR");
				//TODO player EXPRESSION temple position
			} else if (symbol.is("entrance")) {
				parse("entrance position radius EXPRESSION height EXPRESSION");
				return replace(start, "COORD_EXPR");
				//TODO player EXPRESSION temple entrance position radius EXPRESSION height EXPRESSION
			} else {
				throw new ParseException("Unexpected token: "+symbol, file, symbol.token.line, symbol.token.col);
			}
		} else if (symbol.is("-")) {
			parse("- COORD_EXPR");
			//-COORD_EXPR
			throw new ParseException("Statement not implemented", file, line, col);
			//return replace(start, "COORD_EXPR");
		} else if (symbol.is("(")) {
			parse("( COORD_EXPR )");
			//(COORD_EXPR)
			return replace(start, "COORD_EXPR");
		} else {
			final int checkpoint = it.nextIndex();
			final int checkpointIp = getIp();
			symbol = parseExpression(false);
			if (symbol != null) {
				SymbolInstance mode = next();
				if (mode.is("*")) {
					parseCoordExpr(true);
					//EXPRESSION * COORD_EXPR
					throw new ParseException("Statement not implemented", file, line, col);
					//return replace(start, "COORD_EXPR");
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
	
	/**If the given symbol is a number then returns its value; if the symbol is an identifier then
	 * returns the value of the constant with the given name.
	 * @param symbol
	 * @return
	 * @throws ParseException
	 */
	private int getConstant(SymbolInstance symbol) throws ParseException {
		if (symbol.is(TokenType.IDENTIFIER)) {
			String name = symbol.token.value;
			return getConstant(name);
		} else if (symbol.is(TokenType.NUMBER)) {
			return symbol.token.intVal();
		} else {
			throw new ParseException(symbol + " is not a valid constant", file, symbol.token.line, symbol.token.col);
		}
	}
	
	/**Returns the value of the given constant.
	 * @param name
	 * @return
	 * @throws ParseException
	 */
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
	
	/**Returns the ID of the given variable.
	 * @param name
	 * @return
	 * @throws ParseException
	 */
	private int getVarId(String name) throws ParseException {
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
	
	private int storeStringData(String value) {
		Integer strptr = strings.get(value);
		if (strptr == null) {
			strptr = dataSize;
			strings.put(value, strptr);
			dataSize += value.length() + 1;
		}
		return strptr;
	}
	
	private SymbolInstance parseString() throws ParseException {
		SymbolInstance sInst = next();
		if (!sInst.is(TokenType.STRING)) {
			throw new ParseException("Expected: STRING", file, sInst.token.line, sInst.token.col);
		}
		String value = sInst.token.stringVal();
		int strptr = storeStringData(value);
		//STRING
		pushi(strptr);
		return sInst;
	}
	
	private SymbolInstance[] parse(String expression) throws ParseException {
		String[] symbols = expression.split(" ");
		SymbolInstance[] r = new SymbolInstance[symbols.length];
		for (int i = 0; i < symbols.length; i++) {
			String symbol = symbols[i];
			if ("EXPRESSION".equals(symbol)) {
				r[i] = parseExpression(true);
			} else if ("COORD_EXPR".equals(symbol)) {
				r[i] = parseCoordExpr(true);
			} else if ("CONST_EXPR".equals(symbol)) {
				r[i] = parseConstExpr(true);
			} else if ("OBJECT".equals(symbol)) {
				r[i] = parseObject(true);
			} else if ("CONDITION".equals(symbol)) {
				r[i] = parseCondition(true);
			} else if ("IDENTIFIER".equals(symbol)) {
				r[i] = accept(TokenType.IDENTIFIER);
			} else if ("VARIABLE".equals(symbol)) {
				r[i] = accept(TokenType.IDENTIFIER);
				String name = r[i].token.value;
				pushf(name);
			} else if ("CONSTANT".equals(symbol)) {
				r[i] = acceptAny(TokenType.NUMBER, TokenType.IDENTIFIER);
				int val = getConstant(r[i]);
				pushi(val);
			} else if ("STRING".equals(symbol)) {
				r[i] = parseString();
			} else if ("EOL".equals(symbol)) {
				SymbolInstance sInst = next();
				if (!sInst.is(TokenType.EOL)) {
					throw new ParseException("Unexpected token: "+sInst+". Expected: EOL", file, sInst.token.line, sInst.token.col);
				}
				r[i] = sInst;
			} else if ("SPIRIT_TYPE".equals(symbol)) {
				r[i] = parseSpiritType();
			} else if ("PLAYING_SIDE".equals(symbol)) {
				r[i] = parsePlayingSide();
			} else if (symbol.indexOf("|") >= 0) {
				if ("enable|disable".equals(symbol)) {
					r[i] = parseEnableDisableKeyword();
				} else if ("forward|reverse".equals(symbol)) {
					r[i] = parseForwardReverseKeyword();
				} else if ("open|close".equals(symbol)) {
					r[i] = parseOpenCloseKeyword();
				} else if ("pause|unpause".equals(symbol)) {
					r[i] = parsePauseUnpauseKeyword();
				} else if ("quest|challenge".equals(symbol)) {
					r[i] = parseQuestChallengeKeyword();
				} else if ("enter|exit".equals(symbol)) {
					r[i] = parseEnterExitKeyword();
				} else if ("second|seconds".equals(symbol)) {
					SymbolInstance sInst = next();
					if (!sInst.is("second") && !sInst.is("seconds")) {
						throw new ParseException("Unexpected token: "+sInst+". Expected: second|seconds", file, sInst.token.line, sInst.token.col);
					}
					r[i] = sInst;
				} else if ("event|events".equals(symbol)) {
					SymbolInstance sInst = next();
					if (!sInst.is("event") && !sInst.is("events")) {
						throw new ParseException("Unexpected token: "+sInst+". Expected: event|events", file, sInst.token.line, sInst.token.col);
					}
					r[i] = sInst;
				} else if ("graphics|gfx".equals(symbol)) {
					SymbolInstance sInst = next();
					if (!sInst.is("graphics") && sInst.is("gfx")) {
						throw new ParseException("Unexpected token: "+sInst+". Expected: graphics|gfx", file, sInst.token.line, sInst.token.col);
					}
					r[i] = sInst;
				} else {
					throw new IllegalArgumentException("Unknown symbol: "+symbol);
				}
			} else if (symbol.startsWith("[")) {
				String keyword = symbols[i].substring(1);
				String expr = symbols[i + 1];
				if (!expr.endsWith("]")) {
					throw new IllegalArgumentException("Invalid optional symbol in \""+expression+"\"");
				}
				expr = expr.substring(0, expr.length() - 1);
				if ("EXPRESSION".equals(expr)) {
					SymbolInstance sInst = peek();
					if (sInst.is(keyword)) {
						r[i] = accept(keyword);
						r[i + 1] = parseExpression(true);
					} else {
						pushf(0);
					}
				} else if ("CONST_EXPR".equals(expr)) {
					SymbolInstance sInst = peek();
					if (sInst.is(keyword)) {
						r[i] = accept(keyword);
						r[i + 1] = parseConstExpr(true);
					} else {
						pushf(0);
					}
				} else {
					throw new IllegalArgumentException("Invalid optional symbol in \""+expression+"\"");
				}
				i++;
			} else {
				SymbolInstance sInst = next();
				if (!sInst.is(symbol)) {
					throw new ParseException("Unexpected token "+sInst+". Expected: "+symbol, file, sInst.token.line, sInst.token.col);
				}
				r[i] = sInst;
			}
		}
		return r;
	}
	
	private SymbolInstance accept(String keyword) throws ParseException {
		SymbolInstance symbol = next();
		if (!symbol.is(keyword)) {
			throw new ParseException("Expected: "+keyword, file, symbol.token.line, symbol.token.col);
		}
		return symbol;
	}
	
	private SymbolInstance acceptAny(TokenType...types) throws ParseException {
		SymbolInstance symbol = next();
		for (TokenType type : types) {
			if (symbol.token.type == type) {
				return symbol;
			}
		}
		throw new ParseException("Expected: "+join("|", types), file, symbol.token.line, symbol.token.col);
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
		instruction.intVal = getVarId(variable);
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void pusho(String variable) throws ParseException {
		Instruction instruction = Instruction.fromKeyword("PUSHO");
		instruction.flags = OPCodeFlag.REF;
		instruction.intVal = getVarId(variable);
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
		instruction.intVal = getVarId(variable);
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
		return instruction;
	}
	
	private void sleep() {
		Instruction instruction = Instruction.fromKeyword("SLEEP");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private Instruction except() {
		Instruction instruction = Instruction.fromKeyword("EXCEPT");
		instruction.lineNumber = line;
		instructions.add(instruction);
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
	
	private static String join(String sep, Object[] items) {
		if (items.length == 0) return "";
		String r = String.valueOf(items[0]);
		for (int i = 1; i < items.length; i++) {
			r += sep + String.valueOf(items[i]);
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
}
