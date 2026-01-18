/* Copyright (c) 2025-2026 Daniele Lombardi / Daniels118
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
package it.ld.bw.chl.lang.java;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import chl.lang.LHEnum;

import java.util.Map.Entry;
import java.util.zip.CRC32;

import it.ld.bw.chl.exceptions.ParseError;
import it.ld.bw.chl.exceptions.ParseException;
import it.ld.bw.chl.exceptions.ScriptNotFoundException;
import it.ld.bw.chl.lang.commons.CHeaderParser;
import it.ld.bw.chl.lang.commons.InfoParser2;
import it.ld.bw.chl.lang.commons.CompilerOptions;
import it.ld.bw.chl.lang.commons.ScriptInfo;
import it.ld.bw.chl.lang.commons.ScriptToResolve;
import it.ld.bw.chl.lang.commons.Symbol;
import it.ld.bw.chl.lang.commons.SymbolInstance;
import it.ld.bw.chl.lang.commons.Syntax;
import it.ld.bw.chl.lang.commons.Token;
import it.ld.bw.chl.lang.commons.TokenType;
import it.ld.bw.chl.lang.commons.Var;
import it.ld.bw.chl.lang.java.FunctionMapping.ParameterMapping;
import it.ld.bw.chl.model.CHLFile;
import it.ld.bw.chl.model.DataType;
import it.ld.bw.chl.model.Header;
import it.ld.bw.chl.model.Instruction;
import it.ld.bw.chl.model.NativeFunction;
import it.ld.bw.chl.model.OPCode;
import it.ld.bw.chl.model.OPCodeMode;
import it.ld.bw.chl.model.ObjectCode;
import it.ld.bw.chl.model.Script;
import it.ld.bw.chl.model.ScriptType;

import static it.ld.bw.chl.model.NativeFunction.*;

/**
 * This class can parse and compile java code into CHL object code.
 */
public class JavaCompiler {
	private static final Charset ASCII = Charset.forName("US-ASCII");
	private static final int INITIAL_BUFFER_SIZE = 16 * 1024;
	
	private static final int VA_MAX = 29;
	private static final float PI_2 = (float)(Math.PI / 2);
	
	private File file;
	private String sourceFilename;
	private String pkg;
	private Map<String, String> importedClasses;
	private String className;
	private String simpleName;
	private LinkedList<SymbolInstance> symbols;
	private ListIterator<SymbolInstance> it;
	private int line;
	private int col;
	
	private CompilerOptions options = new CompilerOptions();
	
	private PrintStream out;
	
	private ObjectCode objcode = new ObjectCode();
	private CHLFile chl = new CHLFile();
	private Script currentScript;
	private List<Instruction> instructions;
	private boolean sealed = false;
	private LinkedHashMap<String, Integer> strings = new LinkedHashMap<>();
	private Map<Integer, String> stringLookup = new HashMap<>();
	private ByteArrayOutputStream dataBuffer = new ByteArrayOutputStream(INITIAL_BUFFER_SIZE);
	private Map<String, Integer> constants = new HashMap<>();
	private LinkedHashMap<String, Var> localMap = new LinkedHashMap<>();
	private Map<String, Object> classConst = new HashMap<>();
	private Map<String, Object> localConst = new HashMap<>();
	private LinkedHashMap<String, Var> globalMap = new LinkedHashMap<>();
	private Map<String, Script> scriptDefinitions = new HashMap<>();
	private Map<String, ScriptInfo> scriptsInfo = new HashMap<>();
	private LinkedHashMap<String, ScriptToResolve> autoruns = new LinkedHashMap<>();
	private List<ScriptToResolve> calls = new LinkedList<>();
	private String challengeName;
	private Integer challengeId;
	
	private boolean noYield = false;
	
	private Set<String> externalVars = new LinkedHashSet<>();
	
	private ParseException lastParseException = null;
	
	private Map<String, String> properties = new HashMap<>();
	private Set<String> sourceDirs = new HashSet<>();
	
	private final Syntax syntax;
	private final Set<String> datatypes = new HashSet<>();
	
	private List<Set<String>> closureVars = new ArrayList<>();
	private LinkedList<List<Instruction>> breakables = new LinkedList<>();
	private LinkedList<List<Instruction>> continuables = new LinkedList<>();
	private List<Instruction> returns = new LinkedList<>();
	
	private final Map<String, Callable<Void>> customActions = new HashMap<>();
	
	{
		closureVars.add(new HashSet<>());
		//Warmup
		LHClass.forName("chl.lang.GameThing");
	}
	
	public JavaCompiler() {
		this(System.out);
	}
	
	public JavaCompiler(PrintStream outStream) {
		this.syntax = Syntax.get("java");
		this.out = outStream;
		chl.getHeader().setVersion(Header.BW1);
		for (Symbol symbol : syntax.getSymbol("DATATYPE").alternatives) {
			datatypes.add(symbol.keyword);
		}
		//
		customActions.put("@null", this::_null);
		
		customActions.put("@Coord", this::Coord_Coord);
		customActions.put("@Coord.add", this::Coord_add);
		customActions.put("@Coord.sub", this::Coord_sub);
		customActions.put("@Coord.mul", this::Coord_mul);
		customActions.put("@Coord.div", this::Coord_div);
		customActions.put("@Coord.getX", this::Coord_getX);
		customActions.put("@Coord.getY", this::Coord_getY);
		customActions.put("@Coord.getZ", this::Coord_getZ);
		customActions.put("@Coord.equals", this::Coord_equals);
		
		customActions.put("@Math.abs", this::Math_abs);
		customActions.put("@Math.ceil", this::Math_ceil);
		customActions.put("@Math.copySign", this::Math_copySign);
		customActions.put("@Math.cos", this::Math_cos);
		customActions.put("@Math.floor", this::Math_floor);
		customActions.put("@Math.hypot", this::Math_hypot);
		customActions.put("@Math.max", this::Math_max);
		customActions.put("@Math.min", this::Math_min);
		customActions.put("@Math.reduceRadians", this::Math_reduceRadians);
		customActions.put("@Math.round", this::Math_round);
		customActions.put("@Math.signum", this::Math_signum);
		customActions.put("@Math.sin", this::Math_sin);
		customActions.put("@Math.toDegrees", this::Math_toDegrees);
		customActions.put("@Math.toRadians", this::Math_toRadians);
		
		customActions.put("@Task.sleep", this::Task_sleep);
	}
	
	public CompilerOptions getOptions() {
		return options;
	}
	
	public void setOptions(CompilerOptions options) {
		this.options = options;
	}
	
	private void notice(String s) {
		if (options.verbose) {
			out.println(s);
		}
	}
	
	private void info(String s) {
		if (options.verbose) {
			out.println(s);
		}
	}
	
	public void setDefinedConstants(Map<String, Integer> constants) {
		this.constants = constants;
	}
	
	public Map<String, Integer> getDefinedConstants() {
		return constants;
	}
	
	public void addConstants(Map<String, Integer> constants) {
		this.constants.putAll(constants);
	}
	
	public void setDefinedGlobalVars(Set<String> vars) {
		this.externalVars = vars;
	}
	
	public Set<String> getDefinedGlobalVars() {
		return externalVars;
	}
	
	public void addGlobalVars(Set<String> vars) {
		this.externalVars.addAll(vars);
	}
	
	private void convertToNodes(List<Token> tokens) throws ParseException {
		TokenType prevType = TokenType.EOL;
		symbols = new LinkedList<>();
		for (Token token : tokens) {
			if (token.type.important) {
				if (token.type != TokenType.EOL || prevType != TokenType.EOL) {
					symbols.add(toSymbol(token));
					prevType = token.type;
				}
			}
		}
		if (prevType != TokenType.EOL) {
			symbols.add(new SymbolInstance(Syntax.EOL, new Token(0, 0, TokenType.EOL)));
		}
		symbols.add(SymbolInstance.EOF);
	}
	
	public void loadHeader(File headerFile) throws FileNotFoundException, IOException, ParseException {
		info("loading "+headerFile.getName()+"...");
		CHeaderParser parser = new CHeaderParser();
		parser.parse(headerFile, constants);
	}
	
	public void loadInfo(File infoFile) throws FileNotFoundException, IOException, ParseException {
		info("loading "+infoFile.getName()+"...");
		InfoParser2 parser = new InfoParser2();
		parser.parse(infoFile, constants);
	}
	
	public ObjectCode compile(File file) throws IOException, ParseException {
		//Reinit
		objcode = new ObjectCode();
		chl = objcode.getChl();
		chl.header.setVersion(Header.BW1);
		currentScript = null;
		instructions = new ArrayList<Instruction>();
		chl.code.setItems((ArrayList<Instruction>)instructions);
		strings.clear();
		stringLookup.clear();
		dataBuffer.reset();
		localMap.clear();
		localConst.clear();
		classConst.clear();
		globalMap.clear();
		scriptDefinitions.clear();
		autoruns.clear();
		calls.clear();
		challengeName = null;
		challengeId = null;
		lastParseException = null;
		properties = new HashMap<>();
		sourceDirs = new HashSet<>();
		pkg = "";
		//
		parse(file);
		//Finalize data section
		info("building data section...");
		if (options.debug) {
			for (Entry<String, String> p : properties.entrySet()) {
				storeStringData(p.getKey() + "=" + p.getValue());
			}
			if (!sourceDirs.isEmpty()) {
				storeStringData("source_dirs=" + String.join(";", sourceDirs));
			}
		}
		chl.data.setData(dataBuffer.toByteArray());
		//Resolve call and start instructions
		info("resolving call and start instructions...");
		for (ScriptToResolve call : calls) {
			try {
				Script script = chl.scripts.getScript(call.name);
				if (script.getParameterCount() != call.argc) {
					throw new ParseException("The number of parameters doesn't match script declaration", call.file, call.line, 1);
				}
				call.instr.intVal = script.getScriptID();
			} catch (ScriptNotFoundException e) {
				call.instr.intVal = -objcode.getExternalScriptId(call.name, call.argc);
			}
		}
		//Resolve auto start scripts
		info("resolving autorun scripts...");
		for (ScriptToResolve call : autoruns.values()) {
			try {
				Script script = chl.scripts.getScript(call.name);
				if (script.getParameterCount() > 0) {
					throw new ParseException("Script with parameters not valid for autorun: "+call.name, call.file, call.line, 1);
				}
				chl.autoStartScripts.getScripts().add(script.getScriptID());
			} catch (ScriptNotFoundException e) {
				int id = -objcode.getExternalScriptId(call.name, call.argc);
				chl.autoStartScripts.getScripts().add(id);
			}
		}
		//Add string instructions address
		for (int i = 0; i < instructions.size(); i++) {
			Instruction instr = instructions.get(i);
			if (instr.strVal != null) {
				objcode.getStringInstructions().add(i);
			}
		}
		//
		return objcode;
	}
	
	public void parse(File file) throws ParseException, IOException {
		try {
			this.file = file;
			sourceFilename = file.getName();
			this.simpleName = sourceFilename.substring(0, sourceFilename.lastIndexOf('.'));
			info("compiling "+sourceFilename+"...");
			JavaLexer lexer = new JavaLexer();
			List<Token> tokens = lexer.tokenize(file);
			parse(tokens);
			if (options.debug) {
				String dir = file.getAbsoluteFile().getParentFile().getAbsolutePath();
				sourceDirs.add(dir);
				long hash = crc32(file);
				properties.put("crc32["+sourceFilename+"]", String.format("%08X", hash));
			}
		} finally {
			file = null;
			sourceFilename = null;
		}
	}
	
	public void parse(List<Token> tokens) throws ParseException, IllegalStateException {
		if (sealed) {
			throw new IllegalStateException("CHL file already sealed");
		}
		convertToNodes(tokens);
		importedClasses = new HashMap<>();
		importedClasses.put("String", "java.lang.String");
		challengeName = null;
		challengeId = null;
		line = 0;
		col = 0;
		//
		it = symbols.listIterator();
		parseFile();
	}
	
	private SymbolInstance parseFile() throws ParseException {
		final int start = it.nextIndex();
		SymbolInstance symbol = peek();
		if (symbol.is("package")) {
			parsePackage();
		}
		symbol = peek();
		while (symbol.is("import")) {
			parseImport();
			symbol = peek();
		}
		parseClassDef();
		symbol = next();
		if (symbol != SymbolInstance.EOF) {
			throw new ParseException("Unexpected token: "+symbol+". Expected: EOF", file, symbol.token.line, symbol.token.col);
		}
		return replace(start, "FILE");
	}
	
	private SymbolInstance parsePackage() throws ParseException {
		final int start = it.nextIndex();
		accept("package");
		String pkg = accept(TokenType.IDENTIFIER).token.value;
		SymbolInstance symbol = next();
		while (symbol.is(".")) {
			pkg += "." + accept(TokenType.IDENTIFIER).token.value;
			symbol = next();
		}
		if (!symbol.is(";")) {
			throw new ParseError("Unexpected token: "+symbol+". Expected: ;", file, symbol.token.line, symbol.token.col);
		}
		accept(TokenType.EOL);
		this.pkg = pkg;
		return replace(start, "PACKAGE");
	}
	
	private SymbolInstance parseImport() throws ParseException {
		final int start = it.nextIndex();
		SymbolInstance symbol = accept("import");
		boolean isStatic = false;
		symbol = peek();
		if (symbol.is("static")) {
			next();
			isStatic = true;
		}
		String id = accept(TokenType.IDENTIFIER).token.value;
		symbol = next();
		String lastName = symbol.token.value;
		while (symbol.is(".")) {
			symbol = next();
			if (!symbol.is(TokenType.IDENTIFIER) && !symbol.is("*")) {
				throw new ParseException("Unexpected token: "+symbol+". Expected: IDENTIFIER or *", file, symbol.token.line, symbol.token.col);
			}
			lastName = symbol.token.value;
			id += "." + lastName;
			symbol = next();
		}
		if (!symbol.is(";")) {
			throw new ParseError("Unexpected token: "+symbol+". Expected: ;", file, symbol.token.line, symbol.token.col);
		}
		if (isStatic) {
			//TODO
			throw new ParseError("Statement not implemented", file, symbol.token.line, symbol.token.col);
		} else {
			if ("chl.lang.*".equals(id)) {
				List<LHClass> classes = LHClass.getClasses();
				for (LHClass cls : classes) {
					importedClasses.put(cls.getSimpleName(), cls.getName());
				}
			} else if ("*".equals(lastName)) {
				//TODO
				throw new ParseError("Statement not implemented", file, symbol.token.line, symbol.token.col);
			} else {
				importedClasses.put(lastName, id);
			}
		}
		return replace(start, "IMPORT");
	}
	
	private SymbolInstance parseClassDef() throws ParseException {
		final int start = it.nextIndex();
		SymbolInstance symbol = peek();
		if (symbol.is("@Challenge")) {
			accept("@Challenge");
			accept("(");
			challengeName = accept(TokenType.STRING).token.stringVal();
			accept(")");
			challengeId = (Integer)getConstant("CHALLENGE_" + challengeName);
			if (challengeId == -1) {
				notice("NOTICE: challenge id "+challengeName+" is dummy, snapshot and highlight statements "
						+ "won't be available. At "+file.getName()+":"+line+":"+col);
			}
		}
		//Accept scope public or package private (no modifier)
		symbol = peek();
		if (symbol.is("public")) {
			next();
		}
		//
		String className = parse("abstract class IDENTIFIER {")[2].token.toString();
		if (!className.equals(this.simpleName)) {
			throw new ParseError("Class name must match file name", file, line, col);
		}
		this.className = this.pkg + "." + this.simpleName;
		//
		symbol = peek();
		while (!symbol.is("}")) {
			boolean isMethod = nextSymbolIsMethod();
			if (isMethod) {
				symbol = parseMethod();
			} else {
				symbol = parseField();
			}
			symbol = peek();
		}
		//
		accept("}");
		return replace(start, "CLASS_DEF");
	}
	
	private boolean nextSymbolIsMethod() {
		final int start = it.nextIndex();
		SymbolInstance symbol = next();
		while (true) {
			if (symbol.is("=") || symbol.is(";")) {
				seek(start);
				return false;
			} else if (symbol.is("{")) {
				seek(start);
				return true;
			}
			symbol = next();
		}
	}
	
	private SymbolInstance parseField() throws ParseException {
		final int start = it.nextIndex();
		SymbolInstance symbol = next();
		if (!symbol.is("public") && !symbol.is("private")) {
			throw new ParseError("Expected: public|private", file, symbol.token.line, symbol.token.col);
		}
		String scope = symbol.token.value;
		boolean isPrivate = "private".equals(scope);
		accept("static");
		symbol = next();
		boolean isFinal = false;
		if (symbol.is("final")) {
			isFinal = true;
			symbol = next();
		}
		String typename = symbol.token.value;
		if (isClassname(typename)) {
			typename = importedClasses.getOrDefault(typename, typename);
			if ("chl.lang.Coord".equals(typename)) {
				throw new ParseError("A variable cannot be of type chl.lang.Coord", file, symbol.token.line, symbol.token.col);
			}
		}
		DataType datatype = getType(typename);
		String varName = this.className + "." + accept(TokenType.IDENTIFIER).token.value;
		if (isFinal) {
			accept("=");
			symbol = next();
			if (datatype == DataType.INT && symbol.is(TokenType.NUMBER)) {
				if (isPrivate) {
					classConst.put(varName, symbol.token.intVal());
				} else {
					constants.put(varName, symbol.token.intVal());
				}
			} else {
				String classname = importedClasses.getOrDefault(symbol.token.value, symbol.token.value);
				if (LHClass.forName(classname) != null && checkAhead(". IDENTIFIER ;")) {
					if (isPrivate) {
						accept(".");
						String entry = accept(TokenType.IDENTIFIER).token.value;
						Object val = LHClass.getConstant(classname, entry);
						if (val == null) {
							throw new ParseError("Undefined entry: " + classname + "." + entry, file, symbol.token.line, symbol.token.col);
						}
						classConst.put(varName, val);
					} else {
						throw new ParseError("Enum field must be private", file, symbol.token.line, symbol.token.col);
					}
				} else {
					throw new ParseError("Final field must be numeric or enum", file, symbol.token.line, symbol.token.col);
				}
			}
		} else {
			declareGlobalVar(varName, datatype, typename);
		}
		accept(";");
		return replace(start, "FIELD");
	}
	
	private void declareGlobalVar(String name, DataType type, String typename) {
		if ("chl.lang.Coord".equals(typename)) {
			throw new ParseError("A variable cannot be of type chl.lang.Coord", file, line, col);
		}
		Var var = globalMap.get(name);
		if (var == null) {
			chl.getGlobalVariables().getNames().add(name);
			int varId = globalMap.size() + 1;	//Global variables are indexed starting from 1
			var = new Var(name, varId, type, typename, false, false);
			globalMap.put(name, var);
			externalVars.add(name);
		} else {
			throw new ParseError("Redeclaration of global var "+name, file, line, col);
		}
	}
	
	private void define(ScriptType type, String name, int parameterCount) {
		Script def = scriptDefinitions.get(name);
		if (def != null) {
			if (def.getScriptType() != type || !def.getName().equals(name) || def.getParameterCount() != parameterCount) {
				throw new ParseError("Redefinition of script "+name, file, line, col);
			}
		} else {
			def = new Script(chl);
			def.setScriptType(type);
			def.setName(name);
			def.setParameterCount(parameterCount);
			scriptDefinitions.put(name, def);
		}
	}
	
	private SymbolInstance parseMethod() throws ParseException {
		final int start = it.nextIndex();
		localMap.clear();
		localConst.clear();
		returns.clear();
		closureIn();
		try {
			final Script script = new Script(chl);
			currentScript = script;
			script.setScriptID(chl.scripts.getItems().size() + 1);
			script.setGlobalCount(chl.getGlobalVariables().getNames().size());
			script.setSourceFilename(sourceFilename);
			script.setInstructionAddress(getIp());
			
			boolean autorun = false;
			String export = null;
			ScriptType scriptType = ScriptType.SCRIPT;
			
			SymbolInstance symbol = peek();
			while (symbol.token.value.startsWith("@")) {
				if (symbol.is("@Autorun")) {
					next();
					autorun = true;
				} else if (symbol.is("@Export")) {
					next();
					accept("(");
					export = accept(TokenType.STRING).token.stringVal();
					accept(")");
				} else if (symbol.token.value.startsWith("@")) {
					next();
					if (symbol.is("@HelpScript")) {
						scriptType = ScriptType.HELP;
					} else if (symbol.is("@ChallengeHelpScript")) {
						scriptType = ScriptType.CHALLENGE_HELP;
					} else if (symbol.is("@TempleHelpScript")) {
						scriptType = ScriptType.TEMPLE_HELP;
					} else if (symbol.is("@TempleSpecialScript")) {
						scriptType = ScriptType.TEMPLE_SPECIAL;
					} else if (symbol.is("@MultiplayerHelpScript")) {
						scriptType = ScriptType.MULTIPLAYER_HELP;
					}
				}
				symbol = peek();
			}
			script.setScriptType(scriptType);
			//Accept any scope for methods
			symbol = peek();
			if (symbol.is("public") || symbol.is("private")) {
				next();
			}
			//
			String name = this.className + "." + parse("static void IDENTIFIER")[2].token.value;
			scriptsInfo.put(name, new ScriptInfo());
			script.setName(name);
			int argc = parseArguments(true, name);
			script.setParameterCount(argc);
			define(scriptType, name, argc);
			if (autorun) {
				autoruns.put(name, new ScriptToResolve(file, line, null, name, 0));
			}
			//Load parameter values from the stack
			Iterator<Var> lvars = localMap.values().iterator();
			for (int i = 0; i < argc; i++) {
				Var var = lvars.next();
				if (var.varargs) break;
				pop(var);
			}
			//
			accept("{");
			free();
			chl.getScriptsSection().getItems().add(script);
			//STATEMENTS
			parseStatements();
			//
			accept("}");
			final int endIp = getIp();
			for (Instruction retjump : returns) {
				retjump.intVal = endIp;
			}
			end();
			if (export != null) {
				name = export;
				final Script alias = new Script(chl);
				alias.setScriptID(chl.scripts.getItems().size() + 1);
				alias.setGlobalCount(script.getGlobalCount());
				alias.setSourceFilename(script.getSourceFilename());
				alias.setInstructionAddress(script.getInstructionAddress());
				alias.setScriptType(script.getScriptType());
				scriptsInfo.put(name, new ScriptInfo());
				alias.setName(name);
				alias.setParameterCount(script.getParameterCount());
				alias.setVariables(script.getVariables());
				define(scriptType, name, argc);
				chl.getScriptsSection().getItems().add(alias);
			}
			closureOut();
			return replace(start, "METHOD");
		} catch (ParseException e) {
			closureOut();
			throw e;
		} finally {
			localMap.clear();
			localConst.clear();
			returns.clear();
		}
	}
	
	private boolean isDataType(SymbolInstance symbol) {
		return getType(symbol.token.value) != null;
	}
	
	private DataType getType(String name) {
		if ("int".equals(name)) return DataType.INT;
		if ("float".equals(name)) return DataType.FLOAT;
		if ("boolean".equals(name)) return DataType.BOOLEAN;
		if (isClassname(name)) return DataType.OBJECT;
		return null;
	}
	
	private boolean parseVarargs() {
		SymbolInstance symbol = peek();
		if (!symbol.is("...")) return false;
		next();
		return true;
	}
	
	private int parseArguments(boolean addToLocalVars, String scriptName) throws ParseException {
		final int start = it.nextIndex();
		ScriptInfo scriptInfo = this.scriptsInfo.get(scriptName);
		final boolean addScriptVars = scriptInfo.vars.isEmpty();
		int argc = 0;
		accept("(");
		SymbolInstance symbol = peek();
		if (!symbol.is(")")) {
			String typename = next().token.value;
			typename = importedClasses.getOrDefault(typename, typename);
			if ("chl.lang.Coord".equals(typename)) {
				throw new ParseException("A variable cannot be of type chl.lang.Coord", file, line, col);
			}
			DataType type = getType(typename);
			if (type == null) {
				throw new ParseException("Unknown type: "+typename, file, line, col);
			}
			boolean varargs = parseVarargs();
			int varargsIndex = varargs ? 0 : -1;
			symbol = accept(TokenType.IDENTIFIER);
			String name = symbol.token.value;
			if (addToLocalVars) {
				addLocalVar(name, type, typename, false, varargs);
			}
			if (addScriptVars) {
				scriptInfo.vars.add(new Var(name, -1, type, varargs));
			} else {
				Var var = scriptInfo.vars.get(argc);
				if (var.type != type || var.varargs != varargs) {
					throw new ParseException("Argument "+argc+" doesn't match previous definition", file, line, col);
				}
			}
			argc++;
			symbol = peek();
			while (!symbol.is(")")) {
				accept(",");
				typename = next().token.value;
				if ("chl.lang.Coord".equals(typename)) {
					throw new ParseException("A variable cannot be of type chl.lang.Coord", file, line, col);
				}
				type = getType(typename);
				if (type == null) {
					throw new ParseException("Unknown type: "+typename, file, line, col);
				}
				varargs = parseVarargs();
				if (varargs) {
					if (varargsIndex >= 0) {
						throw new ParseException("Only one variadic argument is allowed", file, line, col);
					}
					varargsIndex = argc;
				}
				symbol = accept(TokenType.IDENTIFIER);
				name = symbol.token.value;
				if (addToLocalVars) {
					addLocalVar(name, type, typename, false, varargs);
				}
				if (addScriptVars) {
					scriptInfo.vars.add(new Var(name, -1, type, varargs));
				} else {
					Var var = scriptInfo.vars.get(argc);
					if (var.type != type || var.varargs != varargs) {
						throw new ParseException("Argument "+argc+" doesn't match previous definition", file, line, col);
					}
				}
				argc++;
				symbol = peek();
			}
			if (varargsIndex >= 0) {
				scriptInfo.varargs = true;
				if (!scriptInfo.checkArgc()) {
					throw new ParseException("Variadic argument must be preceded by 'int argc'", file, line, col);
				}
				//Required to force the copy the whole stack from the caller to the callee
				if (addToLocalVars) {
					while (argc < VA_MAX) {
						addLocalVar(name + "_" + (argc - varargsIndex), type, typename, false, varargs);
						argc++;
					}
				}
			}
		}
		accept(")");
		replace(start, "(ARGS)");
		return argc;
	}
	
	/**This method parses parameters without adding them to the instructions buffer.
	 * The parameters are returned as a list of {@link CodeBlock code blocks}.
	 * @return
	 * @throws ParseException
	 */
	private List<CodeBlock> parseParameters() throws ParseException {
		final int start = it.nextIndex();
		final List<CodeBlock> result = new LinkedList<>();
		accept("(");
		SymbolInstance symbol = peek();
		if (!symbol.is(")")) {
			result.add(parseParameter());
			symbol = peek();
			while (!symbol.is(")")) {
				if (symbol.is(";")) {
					throw new ParseException("Unexpected token: ;. Expected ,|)", lastParseException, file, symbol.getLine(), symbol.getCol());
				}
				accept(",");
				result.add(parseParameter());
				symbol = peek();
			}
		}
		accept(")");
		replace(start, "(PARAMETERS)");
		return result;
	}
	
	/**This method parses a single parameter without adding it to the instruction buffer.
	 * The parameter is returned as a {@linkplain CodeBlock}.
	 * This works by temporary replacing the instructions buffer with a new CodeBlock.
	 * @return
	 * @throws ParseException
	 */
	private CodeBlock parseParameter() throws ParseException {
		List<Instruction> prevInstructions = this.instructions;
		CodeBlock result = new CodeBlock();
		this.instructions = result;
		try {
			SymbolInstance symbol = parseExpression();
			if (symbol.typename == null) {
				throw new ParseException("Expression expected", lastParseException, file, symbol.getLine(), symbol.getCol());
			}
			result.typename = symbol.typename;
			return result;
		} finally {
			this.instructions = prevInstructions;
		}
	}
	
	private void loopIn() {
		closureIn();
		breakableIn();
		continuableIn();
	}
	
	private void loopOut(int continueIp, int breakIp) throws ParseException {
		continuableOut(continueIp);
		breakableOut(breakIp);
		closureOut();
	}
	
	private void breakableIn() {
		breakables.add(new LinkedList<>());
	}
	
	private void breakableOut(int targetIp) {
		List<Instruction> breaks = breakables.removeLast();
		for (Instruction instr : breaks) {
			instr.intVal = targetIp;
		}
	}
	
	private void continuableIn() {
		continuables.add(new LinkedList<>());
	}
	
	private void continuableOut(int targetIp) {
		List<Instruction> continues = continuables.removeLast();
		for (Instruction instr : continues) {
			instr.intVal = targetIp;
		}
	}
	
	private void closureIn() {
		closureVars.add(Collections.unmodifiableSet(closureVars.get(closureVars.size() - 1)));
	}
	
	private void closureOut() throws ParseException {
		if (closureVars.size() >= 3) {
			Set<String> vars = closureVars.get(closureVars.size() - 1);
			Set<String> prevVars = closureVars.get(closureVars.size() - 2);
			if (vars != prevVars && vars instanceof HashSet) {
				vars.removeAll(prevVars);
				for (String var : vars) {
					zero(var);	//Clear vars before exiting the closure, just in case they still reference objects
				}
			}
		}
		closureVars.remove(closureVars.size() - 1);
	}
	
	/**Declare var in current closure, or throw an execption if it already exists.
	 * @param name
	 * @throws ParseException
	 */
	private void addVarInClosure(String name) throws ParseException {
		Set<String> vars = closureVars.get(closureVars.size() - 1);
		if (vars.contains(name)) {
			throw new ParseException("Duplicate local variable: " + name, file, line, col);
		}
		if (!(vars instanceof HashSet)) {
			vars = new HashSet<>(vars);
			closureVars.set(closureVars.size() - 1, vars);
		}
		vars.add(name);
	}
	
	private boolean isVarInClosure(String name) {
		Set<String> vars = closureVars.get(closureVars.size() - 1);
		return vars.contains(name);
	}
	
	/**Add a local var, or update the type of an existing var if it has been declared previously in an different closure.
	 * @param name
	 * @param type
	 * @param typename
	 * @param isFinal
	 * @param varargs
	 * @throws ParseException if the var already exists in the same closure.
	 */
	private Var addLocalVar(String name, DataType type, String typename, boolean isFinal, boolean varargs) throws ParseException {
		addVarInClosure(name);
		if (localMap.containsKey(name)) {
			Var var = localMap.get(name);
			var.type = type;
			var.typename = typename;
			var.isFinal = isFinal;
			return var;
		} else {
			List<String> scriptVars = currentScript.getVariables();
			scriptVars.add(name);
			int id = currentScript.getGlobalCount() + scriptVars.size();
			Var var = new Var(name, id, type, typename, isFinal, varargs);
			localMap.put(name, var);
			return var;
		}
	}
	
	//int lastInstructionPrinted = 0;
	private SymbolInstance parseStatements() throws ParseException {
		closureIn();
		try {
			final int start = it.nextIndex();
			SymbolInstance symbol = parseStatement();
			while (symbol != null) {
				/*if (lastInstructionPrinted < instructions.size()) {
					final int firstLine = symbol.getLine();
					while (instructions.get(lastInstructionPrinted).lineNumber < firstLine) {
						Instruction instr = instructions.get(lastInstructionPrinted++);
						System.out.println(instr);
					}
					System.out.println();
					System.out.println(symbol);
					while (lastInstructionPrinted < instructions.size()) {
						Instruction instr = instructions.get(lastInstructionPrinted++);
						String details = "";
						if (instr.opcode == OPCode.SYS) {
							try {
								details = " " + NativeFunction.fromCode(instr.intVal).getInfoString();
							} catch (InvalidNativeFunctionException e) {}
						}
						System.out.println(instr + details);
					}
				}*/
				symbol = parseStatement();
			}
			closureOut();
			return replace(start, "STATEMENTS");
		} catch (ParseException e) {
			closureOut();
			throw e;
		}
	}
	
	private SymbolInstance parseStatement() throws ParseException {
		noYield = false;
		//
		SymbolInstance symbol = peek();
		while (symbol.is(TokenType.ANNOTATION)) {
			parseStatementAnnotation();
			symbol = peek();
		}
		//
		if (symbol.is("}") || symbol.is("case") || symbol.is(":") || symbol.is("default") || symbol.is("?") || symbol.is(",")) {
			return null;
		} else if (symbol.is("if")) {
			return parseIf();
		} else if (symbol.is("switch")) {
			return parseSwitch();
		} else if (symbol.is("for")) {
			return parseFor();
		} else if (symbol.is("while")) {
			return parseWhile();
		} else if (symbol.is("do")) {
			return parseDoWhile();
		} else if (symbol.is("try")) {
			return parseTry();
		} else if (symbol.is("break")) {
			return parseBreak();
		} else if (symbol.is("continue")) {
			return parseContinue();
		} else if (symbol.is("return")) {
			return parseReturn();
		} else {
			symbol = parseSimpleStatement();
			accept(";");
			return symbol;
		}
	}
	
	private SymbolInstance parseStatementAnnotation() throws ParseException {
		final int start = it.nextIndex();
		SymbolInstance symbol = accept(TokenType.ANNOTATION);
		String annotation = symbol.toString().trim();
		if ("@NoYield".equals(annotation)) {
			noYield = true;
			return replace(start, "ANNOTATION");
		}
		throw new ParseException("Unexpected annotation: "+annotation, file, symbol.token.line, symbol.token.col);
	}
	
	private SymbolInstance parseSimpleStatement() throws ParseException {
		SymbolInstance symbol = peek();
		if (symbol.is("final") || (this.isDataType(symbol) && peek(1).is(TokenType.IDENTIFIER))) {
			return parseLocalDeclaration();
		}
		//
		final int start = it.nextIndex();
		symbol = parseExpression();
		String leftExprType = symbol.symbol.keyword;
		SymbolInstance operator = peek();
		if (operator.is(";")) {
			//[*.]METHOD([PARAMETERS])
			if (symbol.typename != null && !"void".equals(symbol.typename)) {
				Instruction instr = instructions.get(instructions.size() - 1);
				if (instr.opcode == OPCode.CALL) {
					//User defined method, should never happen since these must be void
				} else if (instr.opcode == OPCode.SYS && instr.intVal != NativeFunction.GET_PROPERTY.ordinal()) {
					//Native calls are good except GET_PROPERTY
				} else {
					throw new ParseException("Unexpected expression", file, symbol.getLine(), symbol.getCol());
				}
				if ("OBJECT".equals(symbol.symbol.keyword) && !"java.lang.String".equals(symbol.typename)) {
					popo();
				} else {
					popf();
				}
			}
			return replace(start, "STATEMENT");
		} else if (operator.is("=") || operator.is("+=") || operator.is("-=") || operator.is("*=") || operator.is("/=") || operator.is("%=")
				|| operator.is("++") || operator.is("--") || operator.is("&=") || operator.is("|=")) {
			next();
			Instruction instr = instructions.get(instructions.size() - 1);
			if (symbol.typename == null) {
				throw new ParseException("Left hand operand is unassignable", file, symbol.token.line, symbol.token.col);
			}
			final String leftType = symbol.typename;
			int varId = 0;
			if (instr.opcode == OPCode.PUSH && instr.mode == OPCodeMode.REF) {
				varId = instr.intVal;
				if (operator.is("=")) {
					instructions.remove(instructions.size() - 1);
				}
			} else if (instr.opcode == OPCode.SYS && instr.intVal == NativeFunction.GET_PROPERTY.ordinal()) {
				if (operator.is("=")) {
					instructions.remove(instructions.size() - 1);	//Don't call GET_PROPERTY for simple assignment
				} else {
					instructions.remove(instructions.size() - 1);	//Temporary remove GET_PROPERTY
					copyto(2);										//Duplicate object ID on the stack
					copyto(2);										//Duplicate property ID on the stack
					instructions.add(instr);						//Restore GET_PROPERTY
				}
			} else {
				throw new ParseException("Left hand operand is unassignable", file, symbol.token.line, symbol.token.col);
			}
			if (operator.is("=")) {
				symbol = parseExpression();
				if (!LHClass.isAssignableTo(symbol.typename, leftType)) {
					throw new ParseException("Expected: " + leftType + ", got " + symbol.typename, file, symbol.getLine(), symbol.getCol());
				}
			} else if (operator.is("+=") || operator.is("-=") || operator.is("*=") || operator.is("/=") || operator.is("%=")) {
				if (!("float".equals(leftType) || "int".equals(leftType))) {
					throw new ParseException("Arithmetic expression requires numeric types", file, symbol.getLine(), symbol.getCol());
				}
				symbol = parseExpression();
				if ("float".equals(leftType) && "int".equals(symbol.typename)) {
					castf();	//Implicit cast from int to float
				} else if (!leftType.equals(symbol.typename)) {
					throw new ParseException("Expected: " + leftType, file, symbol.getLine(), symbol.getCol());
				}
			} else if (operator.is("++") || operator.is("--")) {
				if ("float".equals(leftType)) {
					pushf(1f);
				} else if ("int".equals(leftType)) {
					pushi(1);
				} else {
					throw new ParseException("Arithmetic expression requires numeric types", file, symbol.getLine(), symbol.getCol());
				}
			} else if (operator.is("&=") || operator.is("|=")) {
				if (!("boolean".equals(leftType))) {
					throw new ParseException("Logic expression requires boolean types", file, symbol.getLine(), symbol.getCol());
				}
				symbol = parseExpression();
				if (!leftType.equals(symbol.typename)) {
					throw new ParseException("Expected: " + leftType, file, symbol.getLine(), symbol.getCol());
				}
			}
			if (operator.is("+=") || operator.is("++")) {
				if ("float".equals(leftType)) {
					addf();
				} else if ("int".equals(leftType)) {
					addi();
				}
			} else if (operator.is("-=") || operator.is("--")) {
				if ("float".equals(leftType)) {
					subf();
				} else if ("int".equals(leftType)) {
					subi();
				}
			} else if (operator.is("*=")) {
				if ("float".equals(leftType)) {
					mulf();
				} else if ("int".equals(leftType)) {
					muli();
				}
			} else if (operator.is("/=")) {
				if ("float".equals(leftType)) {
					divf();
				} else if ("int".equals(leftType)) {
					divi();
				}
			} else if (operator.is("%=")) {
				if ("float".equals(leftType)) {
					modf();
				} else if ("int".equals(leftType)) {
					modi();
				}
			} else if (operator.is("&=")) {
				and();
			} else if (operator.is("|=")) {
				or();
			}
			if (instr.opcode == OPCode.PUSH && instr.mode == OPCodeMode.REF) {
				if ("int".equals(leftType) || "java.lang.String".equals(leftType)) {
					popi(varId);
				} else if ("OBJECT".equals(leftExprType) && !"chl.lang.Coord".equals(leftType)) {
					popo(varId);
				} else if ("float".equals(leftType)) {
					popf(varId);
				} else if ("boolean".equals(leftType)) {
					popb(varId);
				} else {
					throw new ParseError("Unsupported type assignment: "+leftType, file, operator.token.line, operator.token.col);
				}
			} else if (instr.opcode == OPCode.SYS && instr.intVal == NativeFunction.GET_PROPERTY.ordinal()) {
				sys(SET_PROPERTY);
			}
			return replace(start, "STATEMENT");
		} else {
			throw new ParseException("Unexpected symbol: "+operator, lastParseException, file, operator.token.line, operator.token.col);
		}
	}
	
	private SymbolInstance parseLocalDeclaration() throws ParseException {
		final int start = it.nextIndex();
		SymbolInstance symbol = next();
		boolean isFinal = false;
		if (symbol.is("final")) {
			isFinal = true;
			symbol = next();
		}
		String typename = symbol.token.value;
		if (isClassname(typename)) {
			typename = importedClasses.getOrDefault(typename, typename);
			if ("chl.lang.Coord".equals(typename)) {
				throw new ParseError("A variable cannot be of type chl.lang.Coord", file, symbol.token.line, symbol.token.col);
			}
		}
		DataType datatype = getType(typename);
		String varName = accept(TokenType.IDENTIFIER).token.value;
		Var var = addLocalVar(varName, datatype, typename, isFinal, false);
		accept("=");
		symbol = parseExpression();
		if (!LHClass.isAssignableTo(symbol.typename, typename)) {
			throw new ParseException("Expected: "+typename+", found "+symbol.typename, file, symbol.getLine(), symbol.getCol());
		}
		if ("float".equals(var.typename) && "int".equals(symbol.typename)) {
			castf();
		}
		pop(var);
		return replace(start, "VAR_DECL", typename);
	}
	
	private SymbolInstance parseIf() throws ParseException {
		final int start = it.nextIndex();
		//IF_ELSIF_ELSE
		List<Instruction> termJumps = new LinkedList<>();
		parse("if ( CONDITION ) {");
		Instruction jz_lblNextCond = jz();
		parseStatements();
		accept("}");
		SymbolInstance symbol = peek();
		while (symbol.is("else") && peek(1).is("if")) {
			termJumps.add(jmp());
			jz_lblNextCond.intVal = getIp();
			parse("else if ( CONDITION ) {");
			jz_lblNextCond = jz();
			parseStatements();
			accept("}");
			symbol = peek();
		}
		if (symbol.is("else")) {
			termJumps.add(jmp());
			jz_lblNextCond.intVal = getIp();
			parse("else {");
			parseStatements();
			accept("}");
		} else {
			jz_lblNextCond.intVal = getIp();
		}
		final int lblEndBlock = getIp();
		for (Instruction jmpEndBlock : termJumps) {
			jmpEndBlock.intVal = lblEndBlock;
		}
		return replace(start, "IF");
	}
	
	private SymbolInstance parseSwitch() throws ParseException {
		final int start = it.nextIndex();
		SymbolInstance symbol = parse("switch ( EXPRESSION ) {")[2];
		String testType = symbol.typename;
		if (!"int".equals(testType) && !LHClass.isEnum(testType)) {
			throw new ParseError("The expression in switch statement must be an int or enum", file, symbol.getLine(), symbol.getCol()); 
		}
		breakableIn();
		Instruction jumpToNextCase = null;
		List<Instruction> jumpsToStatements = new LinkedList<>();
		symbol = peek();
		while (symbol.is("case")) {
			next();
			dup();	//Create a copy of the test value
			symbol = peek();
			if (symbol.is(TokenType.NUMBER)) {
				//case NUMBER:
				symbol = parseExpression();
				if (!symbol.typename.equals(testType)) {
					throw new ParseError("Expected: " + testType + ", found " + symbol.typename, file, symbol.getLine(), symbol.getCol());
				}
			} else if (symbol.is(TokenType.IDENTIFIER)) {
				String name = next().token.value;
				if ("int".equals(testType)) {
					//case CONSTANT:
					Object val = this.getConstant(name);
					if (val instanceof Integer) {
						pushi((int)val);
					} else {
						throw new ParseError("Expected: int" + ", found " + val.getClass(), file, symbol.getLine(), symbol.getCol());
					}
				} else {
					//case ENUM_ENTRY:
					Object val = LHClass.getConstant(testType, name);
					if (val != null && testType.equals(val.getClass().getName())) {
						pushi(((LHEnum)val).value());
					} else {
						throw new ParseError("Expected: " + testType + ", found " + val.getClass(), file, symbol.getLine(), symbol.getCol());
					}
				}
			} else {
				if (symbol.is("default") || symbol.is("}")) {
					break;
				} else {
					throw new ParseError("The expression in switch statement must be an int or enum", file, symbol.getLine(), symbol.getCol());
				}
			}
			accept(":");
			eqi();
			jumpToNextCase = jz();
			symbol = peek();
			if (symbol.is("case")) {
				jumpsToStatements.add(jmp());
			} else {
				for (Instruction jump : jumpsToStatements) {
					jump.intVal = getIp();
				}
				jumpsToStatements.clear();
				parseStatements();
			}
			jumpToNextCase.intVal = getIp();
			symbol = peek();
		}
		if (symbol.is("default")) {
			parse("default :");
			parseStatements();
		}
		accept("}");
		breakableOut(getIp());
		popf();	//Remove the test value from the stack
		return replace(start, "SWITCH");
	}
	
	private SymbolInstance parseFor() throws ParseException {
		final int start = it.nextIndex();
		final boolean noYield = this.noYield;
		loopIn();
		try {
			parse("for (");
			if (checkAhead("IDENTIFIER IDENTIFIER :")) {
				//for (TYPE IDENTIFIER : EXPRESSION) {
				SymbolInstance symbol = accept(TokenType.IDENTIFIER);
				String typename = importedClasses.getOrDefault(symbol.token.value, symbol.token.value);
				if (!isClassname(typename)) {
					throw new ParseError("Unknown class " + typename, file, symbol.getLine(), symbol.getCol());
				}
				DataType datatype = getType(typename);
				String varName = accept(TokenType.IDENTIFIER).token.value;
				Var var = addLocalVar(varName, datatype, typename, false, false);
				accept(":");
				symbol = parseExpression();
				if (!LHClass.isAssignableTo(symbol.typename, "java.lang.Iterable")) {
					throw new ParseException("Expected: java.lang.Iterable, found "+symbol.typename, file, symbol.getLine(), symbol.getCol());
				}
				dup();							//container, container
				sys(GET_FIRST_IN_CONTAINER);	//container, object
				final int loopIp = getIp();
				dup();							//container, object, object
				popo(var.id);					//container, object
				sys(THING_VALID);				//container, valid
				Instruction endFor = jz();		//container
				parse(") {");
				parseStatements();
				accept("}");
				final int continueIp = getIp();
				dup();							//container, container
				pusho(var.name);				//container, container, object
				sys(GET_NEXT_IN_CONTAINER);		//container, object
				jmp(loopIp, noYield);
				endFor.intVal = getIp();
				loopOut(continueIp, getIp());
				popo();							//
			} else {
				//for (STATEMENT; CONDITION; STATEMENT) {
				parseSimpleStatement();
				SymbolInstance symbol = peek();
				while (symbol.is(",")) {
					parseSimpleStatement();
					symbol = peek();
				}
				accept(";");
				//
				final int loopIp = getIp();
				symbol = parseExpression();
				if (!"boolean".equals(symbol.typename)) {
					throw new ParseError("Expected: boolean, found " + symbol.typename, file, symbol.getLine(), symbol.getCol());
				}
				Instruction endFor = jz();
				accept(";");
				//
				List<Instruction> prevInstructions = this.instructions;
				final CodeBlock postInstructions = new CodeBlock();
				this.instructions = postInstructions;
				parseSimpleStatement();
				symbol = peek();
				while (symbol.is(",")) {
					parseSimpleStatement();
					symbol = peek();
				}
				this.instructions = prevInstructions;
				parse(") {");
				parseStatements();
				final int continueIp = getIp();
				postInstructions.appendTo(instructions);
				accept("}");
				jmp(loopIp, noYield);
				endFor.intVal = getIp();
				loopOut(continueIp, getIp());
			}
			return replace(start, "FOR");
		} catch (ParseException e) {
			loopOut(-1, -1);
			throw e;
		}
	}
	
	private SymbolInstance parseWhile() throws ParseException {
		final int start = it.nextIndex();
		final boolean noYield = this.noYield;
		//WHILE
		loopIn();
		try {
			int loopIp = getIp();
			parse("while ( CONDITION ) {");
			Instruction jz_lblEndWhile = jz();
			//STATEMENTS
			parseStatements();
			accept("}");
			final int continueIp = getIp();
			jmp(loopIp, noYield);
			jz_lblEndWhile.intVal = getIp();
			loopOut(continueIp, getIp());
			return replace(start, "WHILE");
		} catch (ParseException e) {
			loopOut(-1, -1);
			throw e;
		}
	}
	
	private SymbolInstance parseDoWhile() throws ParseException {
		final int start = it.nextIndex();
		final boolean noYield = this.noYield;
		//DO_WHILE
		loopIn();
		try {
			parse("do {");
			final int loopIp = getIp();
			//STATEMENTS
			parseStatements();
			final int continueIp = getIp();
			parse("} ( CONDITION ) ;");
			not();
			jz(loopIp, noYield);
			loopOut(continueIp, getIp());
			return replace(start, "DO_WHILE");
		} catch (ParseException e) {
			loopOut(-1, -1);
			throw e;
		}
	}
	
	private SymbolInstance parseBreak() throws ParseException {
		final int start = it.nextIndex();
		//break
		if (breakables.isEmpty()) {
			throw new ParseException("Not in a breakable block", lastParseException, file, line, col);
		}
		parse("break ;");
		Instruction jump = jmp();
		breakables.getLast().add(jump);
		return replace(start, "STATEMENT");
	}
	
	private SymbolInstance parseContinue() throws ParseException {
		final int start = it.nextIndex();
		//continue
		if (continuables.isEmpty()) {
			throw new ParseException("Not in a continuable block", lastParseException, file, line, col);
		}
		parse("continue ;");
		Instruction jump = jmp();
		continuables.getLast().add(jump);
		return replace(start, "STATEMENT");
	}
	
	private SymbolInstance parseReturn() throws ParseException {
		final int start = it.nextIndex();
		//return
		parse("return ;");
		returns.add(jmp());
		return replace(start, "STATEMENT");
	}
	
	private SymbolInstance parseTry() throws ParseException {
		closureIn();
		try {
			final int start = it.nextIndex();
			parse("try (");
			SymbolInstance symbol = parseLocalDeclaration();
			String typename = symbol.typename;
			symbol = peek();
			if (symbol.is(";")) {
				next();
			}
			if ("chl.lang.Cinema".equals(typename)) {
				final int lblRetry1 = getIp();
				sys(START_CAMERA_CONTROL);
				jz(lblRetry1);
				final int lblRetry2 = getIp();
				sys(START_DIALOGUE);
				jz(lblRetry2);
				sys(START_GAME_SPEED);
				pushb(true);
				sys(SET_WIDESCREEN);
			} else if ("chl.lang.Camera".equals(typename)) {
				final int lblRetry1 = getIp();
				sys(START_CAMERA_CONTROL);
				jz(lblRetry1);
				final int lblRetry2 = getIp();
				sys(START_DIALOGUE);
				jz(lblRetry2);
				sys(START_GAME_SPEED);
			} else if ("chl.lang.Dialogue".equals(typename)) {
				final int lblRetry1 = getIp();
				sys(START_DIALOGUE);
				jz(lblRetry1);
			} else if ("chl.lang.DualCamera".equals(typename)) {
				sys(START_DUAL_CAMERA);
			} else {
				throw new ParseException("Expected: Cinema|Camera|Dialogue|DualCamera", lastParseException, file, symbol.getLine(), symbol.getCol());
			}
			parse(") {");
			parseStatements();
			if ("chl.lang.Cinema".equals(typename)) {
				pushb(false);
				sys(SET_WIDESCREEN);
				sys(END_GAME_SPEED);
				sys(END_CAMERA_CONTROL);
				sys(END_DIALOGUE);
			} else if ("chl.lang.Camera".equals(typename)) {
				sys(END_GAME_SPEED);
				sys(END_CAMERA_CONTROL);
				sys(END_DIALOGUE);
			} else if ("chl.lang.Dialogue".equals(typename)) {
				sys(END_DIALOGUE);
			} else if ("chl.lang.DualCamera".equals(typename)) {
				sys(RELEASE_DUAL_CAMERA);
			} else {
				assert(false);
			}
			accept("}");
			closureOut();
			return replace(start, "STATEMENT");
		} catch (ParseException e) {
			closureOut();
			throw e;
		}
	}
	
	private SymbolInstance parseExpression() throws ParseException {
		final int start = it.nextIndex();
		SymbolInstance symbol = peek();
		SymbolInstance newSym = parseExpression1();
		if (newSym == null) {
			throw new ParseException("Invalid expression", lastParseException, file, symbol.token.line, symbol.token.col);
		}
		while (newSym != null && newSym != symbol) {
			symbol = newSym;
			prev();
			newSym = parseExpression1();
		}
		seek(start);
		return next();
	}
	
	private SymbolInstance parseExpression1() throws ParseException {
		final int start = it.nextIndex();
		final int startIp = getIp();
		final SymbolInstance startPreserve = peek();
		try {
			SymbolInstance symbol = peek();
			if ("float".equals(symbol.typename)) {
				next();
				SymbolInstance operator = next();
				if (operator.is("*")) {
					//EXPRESSION * EXPRESSION
					symbol = parseExpression1();
					if (symbol == null) {
						throw new ParseException("Expected: EXPRESSION", lastParseException, file, line, col);
					} else if ("int".equals(symbol.typename)) {
						castf();	//Implicit cast
					} else if (!"float".equals(symbol.typename)) {
						throw new ParseException("Expected numeric expression", lastParseException, file, line, col);
					}
					mulf();
					return replace(start, "EXPRESSION", "float");
				} else if (operator.is("/")) {
					//EXPRESSION / EXPRESSION
					symbol = parseExpression1();
					if (symbol == null) {
						throw new ParseException("Expected: EXPRESSION", lastParseException, file, line, col);
					} else if ("int".equals(symbol.typename)) {
						castf();	//Implicit cast
					} else if (!"float".equals(symbol.typename)) {
						throw new ParseException("Expected numeric expression", lastParseException, file, line, col);
					}
					divf();
					return replace(start, "EXPRESSION", "float");
				} else if (operator.is("%")) {
					//EXPRESSION % EXPRESSION
					symbol = parseExpression1();
					if (symbol == null) {
						throw new ParseException("Expected: EXPRESSION", lastParseException, file, line, col);
					} else if ("int".equals(symbol.typename)) {
						castf();	//Implicit cast
					} else if (!"float".equals(symbol.typename)) {
						throw new ParseException("Expected numeric expression", lastParseException, file, line, col);
					}
					modf();
					return replace(start, "EXPRESSION", "float");
				} else if (operator.is("+") || operator.is("-")) {
					symbol = parseExpression();
					if (symbol == null) {
						symbol = peek();
						throw new ParseException("Expected: EXPRESSION", lastParseException, file, line, col);
					} else if ("int".equals(symbol.typename)) {
						castf();	//Implicit cast
					} else if (!"float".equals(symbol.typename)) {
						throw new ParseException("Expected numeric expression", lastParseException, file, line, col);
					}
					if (operator.is("+")) {
						//EXPRESSION + EXPRESSION
						addf();
					} else {
						//EXPRESSION - EXPRESSION
						subf();
					}
					return replace(start, "EXPRESSION", "float");
				} else if (operator.is("==")) {
					//EXPRESSION == EXPRESSION
					symbol = parseExpression();
					if ("int".equals(symbol.typename)) {
						castf();	//Implicit cast
					} else if (!"float".equals(symbol.typename)) {
						throw new ParseException("Expected numeric expression, got " + symbol.typename, lastParseException, file, symbol.getLine(), symbol.getCol());
					}
					eq();
					return replace(start, "CONDITION", "boolean");
				} else if (operator.is("!=")) {
					//EXPRESSION != EXPRESSION
					symbol = parseExpression();
					if ("int".equals(symbol.typename)) {
						castf();	//Implicit cast
					} else if (!"float".equals(symbol.typename)) {
						throw new ParseException("Expected numeric expression, got " + symbol.typename, lastParseException, file, symbol.getLine(), symbol.getCol());
					}
					neq();
					return replace(start, "CONDITION", "boolean");
				} else if (operator.is("<=")) {
					//EXPRESSION <= EXPRESSION
					symbol = parseExpression();
					if ("int".equals(symbol.typename)) {
						castf();	//Implicit cast
					} else if (!"float".equals(symbol.typename)) {
						throw new ParseException("Expected numeric expression, got " + symbol.typename, lastParseException, file, symbol.getLine(), symbol.getCol());
					}
					leq();
					return replace(start, "CONDITION", "boolean");
				} else if (operator.is(">=")) {
					//EXPRESSION >= EXPRESSION
					symbol = parseExpression();
					if ("int".equals(symbol.typename)) {
						castf();	//Implicit cast
					} else if (!"float".equals(symbol.typename)) {
						throw new ParseException("Expected numeric expression, got " + symbol.typename, lastParseException, file, symbol.getLine(), symbol.getCol());
					}
					geq();
					return replace(start, "CONDITION", "boolean");
				} else if (operator.is("<")) {
					//EXPRESSION < EXPRESSION
					symbol = parseExpression();
					if ("int".equals(symbol.typename)) {
						castf();	//Implicit cast
					} else if (!"float".equals(symbol.typename)) {
						throw new ParseException("Expected numeric expression, got " + symbol.typename, lastParseException, file, symbol.getLine(), symbol.getCol());
					}
					lt();
					return replace(start, "CONDITION", "boolean");
				} else if (operator.is(">")) {
					//EXPRESSION > EXPRESSION
					symbol = parseExpression();
					if ("int".equals(symbol.typename)) {
						castf();	//Implicit cast
					} else if (!"float".equals(symbol.typename)) {
						throw new ParseException("Expected numeric expression, got " + symbol.typename, lastParseException, file, symbol.getLine(), symbol.getCol());
					}
					gt();
					return replace(start, "CONDITION", "boolean");
				} else {
					seek(start);
					return peek();
				}
			} else if ("int".equals(symbol.typename)) {
				next();
				final int leftOperand = getIp();
				SymbolInstance operator = next();
				if (operator.is("*")) {
					//EXPRESSION * EXPRESSION
					symbol = parseExpression1();
					if (symbol == null) {
						throw new ParseException("Expected: EXPRESSION", lastParseException, file, line, col);
					} else if ("int".equals(symbol.typename)) {
						muli();
						return replace(start, "EXPRESSION", "int");
					} else if ("float".equals(symbol.typename)) {
						castf(leftOperand);
						mulf();
						return replace(start, "EXPRESSION", "float");
					} else {
						throw new ParseException("Expected numeric expression", lastParseException, file, line, col);
					}
				} else if (operator.is("/")) {
					//EXPRESSION / EXPRESSION
					symbol = parseExpression1();
					if (symbol == null) {
						throw new ParseException("Expected: EXPRESSION", lastParseException, file, line, col);
					} else if ("int".equals(symbol.typename)) {
						divi();
						return replace(start, "EXPRESSION", "int");
					} else if ("float".equals(symbol.typename)) {
						castf(leftOperand);
						divf();
						return replace(start, "EXPRESSION", "float");
					} else {
						throw new ParseException("Expected numeric expression", lastParseException, file, line, col);
					}
				} else if (operator.is("%")) {
					//EXPRESSION % EXPRESSION
					symbol = parseExpression1();
					if (symbol == null) {
						throw new ParseException("Expected: EXPRESSION", lastParseException, file, line, col);
					} else if ("int".equals(symbol.typename)) {
						modi();
						return replace(start, "EXPRESSION", "int");
					} else if ("float".equals(symbol.typename)) {
						castf(leftOperand);
						modf();
						return replace(start, "EXPRESSION", "float");
					} else {
						throw new ParseException("Expected numeric expression", lastParseException, file, line, col);
					}
				} else if (operator.is("+") || operator.is("-")) {
					symbol = parseExpression();
					if (symbol == null) {
						symbol = peek();
						throw new ParseException("Expected: EXPRESSION", lastParseException, file, line, col);
					} else if ("float".equals(symbol.typename)) {
						//Implicit cast of left operand
						swap();
						castf();
						swap();
						if (operator.is("+")) {
							addf();
						} else {
							subf();
						}
						return replace(start, "EXPRESSION", "float");
					} else if ("int".equals(symbol.typename)) {
						if (operator.is("+")) {
							addi();
						} else {
							subi();
						}
						return replace(start, "EXPRESSION", "int");
					} else {
						throw new ParseException("Expected numeric expression", lastParseException, file, line, col);
					}
				} else if (operator.is("==")) {
					//EXPRESSION == EXPRESSION
					symbol = parseExpression();
					if ("int".equals(symbol.typename)) {
						eqi();
					} else if ("float".equals(symbol.typename)) {
						castf(leftOperand);
						eq();
					} else {
						throw new ParseException("Expected numeric expression, got " + symbol.typename, lastParseException, file, symbol.getLine(), symbol.getCol());
					}
					return replace(start, "CONDITION", "boolean");
				} else if (operator.is("!=")) {
					//EXPRESSION != EXPRESSION
					symbol = parseExpression();
					if ("int".equals(symbol.typename)) {
						neqi();
					} else if ("float".equals(symbol.typename)) {
						castf(leftOperand);
						neq();
					} else {
						throw new ParseException("Expected numeric expression, got " + symbol.typename, lastParseException, file, symbol.getLine(), symbol.getCol());
					}
					return replace(start, "CONDITION", "boolean");
				} else if (operator.is("<=")) {
					//EXPRESSION <= EXPRESSION
					symbol = parseExpression();
					if ("int".equals(symbol.typename)) {
						leqi();
					} else if ("float".equals(symbol.typename)) {
						castf(leftOperand);
						leq();
					} else {
						throw new ParseException("Expected numeric expression, got " + symbol.typename, lastParseException, file, symbol.getLine(), symbol.getCol());
					}
					return replace(start, "CONDITION", "boolean");
				} else if (operator.is(">=")) {
					//EXPRESSION >= EXPRESSION
					symbol = parseExpression();
					if ("int".equals(symbol.typename)) {
						geqi();
					} else if ("float".equals(symbol.typename)) {
						castf(leftOperand);
						geq();
					} else {
						throw new ParseException("Expected numeric expression, got " + symbol.typename, lastParseException, file, symbol.getLine(), symbol.getCol());
					}
					return replace(start, "CONDITION", "boolean");
				} else if (operator.is("<")) {
					//EXPRESSION < EXPRESSION
					symbol = parseExpression();
					if ("int".equals(symbol.typename)) {
						lti();
					} else if ("float".equals(symbol.typename)) {
						castf(leftOperand);
						lt();
					} else {
						throw new ParseException("Expected numeric expression, got " + symbol.typename, lastParseException, file, symbol.getLine(), symbol.getCol());
					}
					return replace(start, "CONDITION", "boolean");
				} else if (operator.is(">")) {
					//EXPRESSION > EXPRESSION
					symbol = parseExpression();
					if ("int".equals(symbol.typename)) {
						gti();
					} else if ("float".equals(symbol.typename)) {
						castf(leftOperand);
						gt();
					} else {
						throw new ParseException("Expected numeric expression, got " + symbol.typename, lastParseException, file, symbol.getLine(), symbol.getCol());
					}
					return replace(start, "CONDITION", "boolean");
				} else {
					seek(start);
					return peek();
				}
			} else if (symbol.is("-")) {
				//-EXPRESSION
				next();
				symbol = parseExpression1();
				if (symbol == null) {
					symbol = peek();
					throw new ParseException("Expected: EXPRESSION", lastParseException, file, line, col);
				} else if ("int".equals(symbol.typename)) {
					negi();
					return replace(start, "EXPRESSION", "int");
				} else if ("float".equals(symbol.typename)) {
					negf();
					return replace(start, "EXPRESSION", "float");
				} else if ("hcl.lang.Coord".equals(symbol.typename)) {
					negc();
					return replace(start, "EXPRESSION", "float");
				} else {
					throw new ParseException("Expected numeric expression", lastParseException, file, line, col);
				}
			} else if (symbol.symbol != null && "OBJECT".equals(symbol.symbol.keyword)) {
				SymbolInstance subject = next();
				SymbolInstance operator = next();
				if (operator.is(".")) {
					//OBJECT.MEMBER
					String fqClassname = importedClasses.getOrDefault(subject.typename, subject.typename);
					String member = accept(TokenType.IDENTIFIER).token.value;
					symbol = peek();
					if (symbol.is("(")) {
						//OBJECT.IDENTIFIER([PARAMETERS])
						return parseMethodCall(start, symbol, fqClassname, member, false);
					} else {
						//OBJECT.FIELD
						throw new ParseError("Statement not implemented", file, symbol.token.line, symbol.token.col);
						
					}
				} else if (operator.is("==")) {
					//OBJECT == OBJECT
					symbol = parseExpression();
					if (!"OBJECT".equals(symbol.symbol.keyword)) {
						throw new ParseException("Expected OBJECT, got " + symbol.symbol.keyword, lastParseException, file, symbol.getLine(), symbol.getCol());
					}
					if ("chl.lang.Coord".equals(symbol.typename)) {
						throw new ParseError("Type Coord isn't comparable", file, symbol.getLine(), symbol.getCol());
					} else {
						eq();
					}
					return replace(start, "CONDITION", "boolean");
				} else if (operator.is("!=")) {
					//OBJECT != OBJECT
					symbol = parseExpression();
					if (!"OBJECT".equals(symbol.symbol.keyword)) {
						throw new ParseException("Expected OBJECT, got " + symbol.symbol.keyword, lastParseException, file, symbol.getLine(), symbol.getCol());
					}
					if ("chl.lang.Coord".equals(symbol.typename)) {
						throw new ParseError("Type Coord isn't comparable", file, symbol.getLine(), symbol.getCol());
					} else {
						neq();
					}
					return replace(start, "CONDITION", "boolean");
				}
			} else if (symbol.is("new")) {
				//new CLASS([PARAMETERS])
				accept("new");
				String newClass = next().token.value;
				String fqClassname = importedClasses.getOrDefault(newClass, newClass);
				String simpleName = fqClassname.substring(fqClassname.lastIndexOf('.') + 1);
				return parseMethodCall(start, symbol, fqClassname, simpleName, true);
			} else if ("boolean".equals(symbol.typename)) {
				next();
				SymbolInstance operator = next();
				if (operator.is("&&")) {
					symbol = parseExpression();
					if (symbol == null || !"boolean".equals(symbol.typename)) {
						symbol = peek();
						throw new ParseException("Expected CONDITION, got " + symbol.typename, lastParseException, file, line, col);
					}
					//CONDITION && CONDITION
					and();
					return replace(start, "CONDITION", "boolean");
				} else if (operator.is("||")) {
					//CONDITION || CONDITION
					//parseCondition(true);	<- good, but doesn't match the original compiler behavior
					//> alternate method
					symbol = parseExpression();
					if (symbol == null || !"boolean".equals(symbol.typename)) {
						symbol = peek();
						throw new ParseException("Expected CONDITION, got " + symbol.typename, lastParseException, file, line, col);
					}
					symbol = peek();
					if (symbol.is("&&")) {
						prev();
						symbol = parseExpression();
						if (!"boolean".equals(symbol.typename)) {
							throw new ParseException("Expected CONDITION, got " + symbol.typename, lastParseException, file, symbol.getLine(), symbol.getCol());
						}
					}
					//< alternate method
					or();
					return replace(start, "CONDITION", "boolean");
				} else if (operator.is("==")) {
					//CONDITION == CONDITION
					symbol = parseExpression();
					if (!"CONDITION".equals(symbol.symbol.keyword)) {
						throw new ParseException("Expected CONDITION, got " + symbol.typename, lastParseException, file, symbol.getLine(), symbol.getCol());
					}
					eq();
					return replace(start, "CONDITION", "boolean");
				} else if (operator.is("!=")) {
					//CONDITION != CONDITION
					symbol = parseExpression();
					if (!"CONDITION".equals(symbol.symbol.keyword)) {
						throw new ParseException("Expected CONDITION, got " + symbol.typename, lastParseException, file, symbol.getLine(), symbol.getCol());
					}
					neq();
					return replace(start, "CONDITION", "boolean");
				} else if (operator.is("?")) {
					//CONDITION ? EXPRESSION : EXPRESSION
					Instruction jmpElse = jz();
					SymbolInstance exprTrue = parseExpression();
					accept(":");
					Instruction jmpEnd = jmp();
					jmpElse.intVal = getIp();
					SymbolInstance exprFalse = parseExpression();
					jmpEnd.intVal = getIp();
					if (exprTrue.typename == null) {
						throw new ParseException("Expected: EXPRESSION", lastParseException, file, exprTrue.getLine(), exprTrue.getCol());
					} else if (!exprTrue.typename.equals(exprFalse.typename)) {
						throw new ParseException("Expected: " + exprTrue.typename, lastParseException, file, exprTrue.getLine(), exprTrue.getCol());
					}
					return replace(start, exprTrue.symbol.keyword, exprTrue.typename);
				} else {
					seek(start);
					return peek();
				}
			} else if (symbol.is("!")) {
				//!EXPRESSION
				accept("!");
				symbol = parseExpression1();
				if (!"boolean".equals(symbol.typename)) {
					throw new ParseException("Expected: CONDITION", lastParseException, file, symbol.getLine(), symbol.getCol());
				}
				not();
				return replace(start, "CONDITION", "boolean");
			} else if (symbol.is("(")) {
				if (isDataType(peek(1)) && peek(2).is(")")) {
					//(DATATYPE) EXPRESSION
					accept("(");
					String leftType = next().token.value;
					accept(")");
					symbol = parseExpression1();
					String exprType;
					if ("float".equals(leftType)) {
						exprType = "EXPRESSION";
						if ("float".equals(symbol.typename)) {
							//Nothing to do
						} else if ("int".equals(symbol.typename)) {
							castf();
						} else {
							throw new ParseException("Expected numeric expression", lastParseException, file, symbol.getLine(), symbol.getCol());
						}
					} else if ("int".equals(leftType)) {
						exprType = "EXPRESSION";
						if ("float".equals(symbol.typename)) {
							casti();
						} else if ("int".equals(symbol.typename)) {
							//Nothing to do
						} else {
							throw new ParseException("Expected numeric expression", lastParseException, file, symbol.getLine(), symbol.getCol());
						}
					} else if (isClassname(leftType) && "OBJECT".equals(symbol.symbol.keyword)) {
						exprType = "OBJECT";
					} else {
						throw new ParseException("Invalid cast type", lastParseException, file, symbol.getLine(), symbol.getCol());
					}
					return replace(start, exprType, leftType);
				} else {
					//(EXPRESSION)
					accept("(");
					symbol = parseExpression();
					if (symbol.typename == null) {
						throw new ParseException("Expression expected", lastParseException, file, symbol.getLine(), symbol.getCol());
					}
					accept(")");
				}
				return replace(start, symbol.symbol.keyword, symbol.typename);
			} else if (symbol.is("true")) {
				//true
				symbol = next();
				pushb(true);
				return replace(start, "CONDITION", "boolean");
			} else if (symbol.is("false")) {
				//true
				symbol = next();
				pushb(false);
				return replace(start, "CONDITION", "boolean");
			} else if (symbol.is("null")) {
				//null
				symbol = next();
				pusho(0);
				return replace(start, "OBJECT", "null");
			} else if (symbol.is(TokenType.NUMBER)) {
				//NUMBER
				symbol = accept(TokenType.NUMBER);
				if (symbol.isInt()) {
					int val = symbol.token.intVal();
					pushi(val);
					return replace(start, "EXPRESSION", "int");
				} else {
					float val = symbol.token.floatVal();
					pushf(val);
					return replace(start, "EXPRESSION", "float");
				}
			} else if (symbol.is(TokenType.CHAR)) {
				//CHAR
				symbol = accept(TokenType.CHAR);
				int val = symbol.token.intVal();
				pushi(val);
				return replace(start, "EXPRESSION", "int");
			} else if (symbol.is(TokenType.STRING)) {
				return parseString();
			} else if (symbol.is(TokenType.IDENTIFIER)) {
				next();
				String name = symbol.token.value;
				String fqName = name.indexOf('.') >= 0 ? name : (this.className + "." + name);
				Var var = getVar(name);
				if (var != null) {
					switch (var.type) {
						case BOOLEAN:
							pushb(name);
							return replace(start, "CONDITION", var.typename);
						case COORDS:
							throw new ParseError("Variables cannot be of builtin type COORDS", file, symbol.token.line, symbol.token.col);
						case FLOAT:
							pushf(name);
							return replace(start, "EXPRESSION", var.typename);
						case INT:
							pushiVar(name);
							return replace(start, "CONST_EXPR", var.typename);
						case OBJECT:
							if ("java.lang.String".equals(var.typename)) {
								pushiVar(name);
							} else {
								pusho(name);
							}
							symbol = replace(start, "OBJECT", var.typename);
							if (peek().is(".")) {
								prev();
								symbol = parseExpression1();
							}
							return symbol;
						default:
					}
				} else if (isClassname(name)) {
					SymbolInstance operator = next();
					if (operator.is(".")) {
						//CLASS.MEMBER
						String fqClassname = importedClasses.getOrDefault(name, name);
						String member = accept(TokenType.IDENTIFIER).token.value;
						if (peek().is("(")) {
							//CLASS.METHOD([PARAMETERS])
							return parseMethodCall(start, symbol, fqClassname, member, true);
						} else {
							//CLASS.FIELD
							Object val = LHClass.getConstant(fqClassname, member);
							if (val == null) {
								throw new ParseError("Undefined constant " + fqClassname + "." + member, file, symbol.token.line, symbol.token.col);
							}
							String typename = null;
							if (val instanceof LHEnum) {
								typename = fqClassname;
								pushi(((LHEnum)val).value());
							} else if (val instanceof Integer) {
								typename = "int";
								pushi((int)val);
							} else if (val instanceof Float) {
								typename = "float";
								pushf((float)val);
							} else if (val instanceof Boolean) {
								typename = "boolean";
								pushb((boolean)val);
							} else {
								throw new ParseError("Unsupported constant type: " + val.getClass().getName(), file, symbol.token.line, symbol.token.col);
							}
							return replace(start, "EXPRESSION", typename);
						}
					}
				} else if (peek().is("(")) {
					//CLASS.METHOD([PARAMETERS])
					List<CodeBlock> parametersCode = parseParameters();
					List<String> argtypes = new ArrayList<>(parametersCode.size());
					for (CodeBlock param : parametersCode) {
						param.appendTo(instructions);
						argtypes.add(param.typename);
					}
					this.call(this.className + "." + name, argtypes);
					return replace(start, "STATEMENT", "void");
				} else {
					Object val = getConstant(fqName);
					if (val != null) {
						pushi(fqName);
						String typename = val instanceof Integer ? "int" : val.getClass().getName();
						return replace(start, "EXPRESSION", typename);
					} else {
						throw new ParseError("Unknown symbol: " + symbol, file, symbol.token.line, symbol.token.col);
					}
				}
			}
		} catch (ParseException e) {
			lastParseException = e;
		}
		revert(start, startIp, startPreserve);
		return null;
	}
	
	private SymbolInstance parseMethodCall(final int start, SymbolInstance symbol, String fqClassname, String methodName, boolean isCalledStatically) throws ParseException {
		List<CodeBlock> parameters = parseParameters();
		List<String> parametersType = new ArrayList<>(parameters.size());
		for (CodeBlock parameterCode : parameters) {
			parametersType.add(parameterCode.typename);
		}
		LHMethod method = LHClass.findMethod(fqClassname, methodName, parametersType);
		if (method != null) {
			if (!method.isStatic && isCalledStatically) {
				throw new ParseError("Instance method must be called on object", file, symbol.token.line, symbol.token.col);
			} else if (method.isStatic && !isCalledStatically) {
				throw new ParseError("A static method must be called on class", file, symbol.token.line, symbol.token.col);
			}
			if (method.mapping.isStatic && !isCalledStatically) {
				popf();
			}
			for (int i = 0; i < method.mapping.parameters.size(); i++) {
				ParameterMapping parameter = method.mapping.parameters.get(i);
				switch (parameter.type) {
					case THIS:
						if (i == 1) {
							swap();
						} else if (i > 1) {
							movefrom(i + 1);
						}
						break;
					case CONST:
						pushi(parameter.constant);
						break;
					case FLOAT:
						pushf(parameter.floatval);
						break;
					case INT:
						pushi(parameter.intval);
						break;
					case BOOL:
						pushb(parameter.boolval);
						break;
					case CODE:
						int startParam = parameter.source;
						int endParam = parameter.isVarArgs ? (parameters.size()) : (startParam + 1);
						for (int j = startParam; j < endParam; j++) {
							CodeBlock param = parameters.get(j);
							String expectedType = method.parameters[parameter.source].getType().getName();
							final boolean expectsFloat = "float".equals(expectedType);
							if (expectsFloat && "int".equals(param.typename)) {
								param.tryConvertToFloat();
							}
							param.appendTo(instructions);
							if (expectsFloat && "int".equals(param.typename)) {
								castf();
							}
						}
						break;
				}
			}
			if (method.mapping.nativeFunction != null) {
				sys(method.mapping.nativeFunction);
			} else if ("@Task.start".equals(method.mapping.customAction)) {
				String scriptName = getStringFromCode(parameters.get(0));
				if (scriptName.indexOf('.') < 0) {
					scriptName = this.className + "." + scriptName;
				}
				this.start(scriptName, parametersType.subList(1, parametersType.size()));
			} else if ("@Task.call".equals(method.mapping.customAction)) {
				String scriptName = getStringFromCode(parameters.get(0));
				this.call(scriptName, parametersType.subList(1, parametersType.size()));
			} else {
				Callable<Void> action = customActions.get(method.mapping.customAction);
				if (action != null) {
					try {
						action.call();
					} catch (Exception e) {
						throw new ParseError(e, file, symbol.getLine(), symbol.getCol());
					}
				} else {
					throw new ParseError("Unsupported action: "+method.mapping.customAction, file, symbol.getLine(), symbol.getCol());
				}
			}
			symbol = replace(start, getExpressionType(method.returnType), method.returnType);
			if (peek().is(".")) {
				prev();
				symbol = parseExpression1();
			}
			return symbol;
		} else {
			throw new ParseError("Undefined method " + fqClassname + "." + methodName + "(" + join(", ", parametersType) + ")", file, symbol.token.line, symbol.token.col);
		}
	}
	
	private String getExpressionType(String typename) {
		if ("float".equals(typename)) {
			return "EXPRESSION";
		} else if ("int".equals(typename)) {
			return "EXPRESSION";
		} else if ("boolean".equals(typename)) {
			return "CONDITION";
		} else if ("void".equals(typename)) {
			return "STATEMENT";
		} else if (isClassname(typename)) {
			return "OBJECT";
		} else {
			throw new ParseError("Unsupported expression type: " + typename, file, line, col);
		}
	}
	
	private String getStringFromCode(CodeBlock code) {
		assert("java.lang.String".equals(code.typename));
		assert(code.size() == 1);
		Instruction instr = code.get(0);
		assert(instr.opcode == OPCode.PUSH);
		assert(instr.dataType == DataType.INT);
		String string = stringLookup.get(instr.intVal);
		assert(string != null);
		return string;
	}
	
	private boolean isClassname(String name) {
		return this.className.equals(name) || importedClasses.containsKey(name) || importedClasses.containsValue(name);
	}
	
	/**Returns the value of the given constant.
	 * @param name
	 * @return
	 * @throws ParseException
	 */
	private Object getConstant(String name) throws ParseException {
		Object val = localConst.get(name);
		if (val == null) {
			val = classConst.get(name);
			if (val == null) {
				val = constants.get(name);
			}
		}
		if (val == null) {
			lastParseException = new ParseException("Undefined constant: "+name, file, line, col);
			throw lastParseException;
		} else {
			lastParseException = null;
		}
		return val;
	}
	
	private Var getVar(String name) throws ParseException {
		if (isVarInClosure(name)) {
			Var var = localMap.get(name);
			if (var != null) return var;
		}
		if (name.indexOf('.') < 0) {
			name = this.className + "." + name;
		}
		return globalMap.get(name);
	}
	
	/**Returns the ID of the given variable.
	 * @param name
	 * @return
	 * @throws ParseException
	 */
	private int getVarId(String name) throws ParseException {
		Var var = null;
		if (isVarInClosure(name)) {
			var = localMap.get(name);
		}
		if (var == null) {
			if (name.indexOf('.') < 0) {
				name = this.className + "." + name;
			}
			var = globalMap.get(name);
		}
		if (var == null) {
			if (!name.equals(name.toUpperCase()) || externalVars.contains(name)) {
				return -objcode.getExternalVarId(name, 0);
			}
			lastParseException = new ParseException("Undefined variable: "+name, file, line, col);
			throw lastParseException;
		}
		lastParseException = null;
		return var.id;
	}
	
	private SymbolInstance next() {
		return next(true);
	}
	
	private SymbolInstance next(boolean skipEol) {
		SymbolInstance r = it.next();
		if (skipEol) {
			while (r.is(TokenType.EOL)) {
				r = it.next();
			}
		}
		if (r.token != null) {
			line = r.token.line;
			col = r.token.col;
		}
		return r;
	}
	
	private SymbolInstance prev() {
		SymbolInstance r = it.previous();
		if (r.token != null) {
			line = r.token.line;
			col = r.token.col;
		}
		return r;
	}
	
	private boolean checkAhead(String expression) {
		final int start = it.nextIndex();
		String[] symbols = expression.split(" ");
		boolean match = true;
		for (String symbol : symbols) {
			if ("ANY".equals(symbol)) {
				next();
			} else if ("IDENTIFIER".equals(symbol) || "NUMBER".equals(symbol) || "STRING".equals(symbol)) {
				SymbolInstance sInst = next();
				TokenType type = TokenType.valueOf(symbol);
				if (!sInst.is(type)) {
					match = false;
					break;
				}
			} else if ("CONSTANT".equals(symbol)) {
				SymbolInstance sInst = next();
				if (!sInst.is(TokenType.IDENTIFIER) && !sInst.is(TokenType.NUMBER)) {
					match = false;
					break;
				}
			} else if ("EOL".equals(symbol)) {
				SymbolInstance sInst = next(false);
				if (!sInst.is(TokenType.EOL)) {
					match = false;
					break;
				}
			} else {
				SymbolInstance sInst = next();
				if (!sInst.is(symbol)) {
					match = false;
					break;
				}
			}
		}
		while (it.nextIndex() > start) {
			prev();
		}
		return match;
	}
	
	private SymbolInstance peek() {
		return peek(true);
	}
	
	private SymbolInstance peek(boolean skipEol) {
		final int start = it.nextIndex();
		SymbolInstance r = next(skipEol);
		while (it.nextIndex() > start) {
			prev();
		}
		return r;
	}
	
	private SymbolInstance peek(int forward) {
		final int start = it.nextIndex();
		if (forward < 0) {
			throw new IllegalArgumentException("Invalid peek offset: "+forward);
		}
		for (int i = 0; i < forward; i++) {
			next();
		}
		SymbolInstance r = next();
		while (it.nextIndex() > start) {
			prev();
		}
		return r;
	}
	
	private void seek(final int index) {
		while (it.nextIndex() < index) {
			it.next();
		}
		while (it.nextIndex() > index) {
			prev();
		}
		peek();
	}
	
	private int storeStringData(String value) throws ParseError {
		int strptr = strings.getOrDefault(value, -1);
		if (!options.sharedStrings || strptr < 0) {
			byte[] data = value.getBytes(ASCII);
			strptr = dataBuffer.size();
			strings.put(value, strptr);
			try {
				dataBuffer.write(data);
			} catch (IOException e) {
				throw new ParseError(e, file, line);
			}
			dataBuffer.write((byte)0);
		}
		stringLookup.put(strptr, value);
		return strptr;
	}
	
	private SymbolInstance parseString() throws ParseException {
		final int start = it.nextIndex();
		SymbolInstance sInst = next();
		if (sInst.is(TokenType.STRING)) {
			String value = sInst.token.stringVal();
			int strptr = storeStringData(value);
			//STRING
			pushi(strptr, value);
		} else {
			throw new ParseException("Unexpected token: "+sInst+". Expected: STRING", lastParseException, file, sInst.token.line, sInst.token.col);
		}
		return replace(start, "STRING", "java.lang.String");
	}
	
	private SymbolInstance[] parse(String expression, Object... defaults) throws ParseException {
		String[] symbols = expression.split(" ");
		SymbolInstance[] r = new SymbolInstance[symbols.length];
		int defaultIndex = 0;
		for (int i = 0; i < symbols.length; i++) {
			String symbol = symbols[i];
			if ("EXPRESSION".equals(symbol)) {
				r[i] = parseExpression();
			} else if ("CONDITION".equals(symbol)) {
				r[i] = parseExpression();
				if (!"boolean".equals(r[i].typename)) {
					lastParseException = new ParseException("Expected: CONDITION", lastParseException, file, r[i].getLine(), r[i].getCol());
					throw lastParseException;
				}
			} else if ("IDENTIFIER".equals(symbol)) {
				r[i] = accept(TokenType.IDENTIFIER);
			} else if ("VARIABLE".equals(symbol)) {
				r[i] = accept(TokenType.IDENTIFIER);
				String name = r[i].token.value;
				Var var = getVar(name);
				if (var == null) {
					lastParseException = new ParseException("Undefined variable: "+name, lastParseException, file, r[i].getLine(), r[i].getCol());
					throw lastParseException;
				}
				r[i].typename = var.typename;
				pushf(name);
			} else if ("INTVAR".equals(symbol)) {
				r[i] = accept(TokenType.IDENTIFIER);
				String name = r[i].token.value;
				pushiVar(name);
			} else if ("CONSTANT".equals(symbol)) {
				r[i] = acceptAny(TokenType.NUMBER, TokenType.IDENTIFIER);
				pushi(r[i].token.value);
			} else if ("STRING".equals(symbol)) {
				r[i] = parseString();
			} else if ("EOL".equals(symbol)) {
				SymbolInstance sInst = next(false);
				if (!sInst.is(TokenType.EOL)) {
					lastParseException = new ParseException("Unexpected token: "+sInst+". Expected: EOL", lastParseException, file, sInst.token.line, sInst.token.col);
					throw lastParseException;
				}
				r[i] = sInst;
			} else if (symbol.startsWith("[")) {	//Optional expression
				boolean ended = false;
				boolean match = true;
				boolean flag = true;	//Tells if this optional expression must generate a boolean value (the other case is a default value)
				symbols[i] = symbols[i].substring(1);
				for (int start = i; i < symbols.length && !ended; i++) {
					String expr = symbols[i];
					if (expr.endsWith("]")) {
						expr = expr.substring(0, expr.length() - 1);
						ended = true;
					}
					if ("EXPRESSION".equals(expr)) {
						flag = false;
						if (match) {
							r[i] = parseExpression();
						} else {
							float def = defaultIndex < defaults.length ? asFloat(defaults[defaultIndex]) : 0;
							pushf(def);
						}
						defaultIndex++;
					} else if (match) {
						SymbolInstance sInst = peek(false);
						if ("EOL".equals(expr) && sInst.is(TokenType.EOL)) {
							accept(TokenType.EOL);
						} else if (sInst.is(expr)) {
							accept(expr);
						} else if (i > start) {
							lastParseException = new ParseException("Unexpected token: "+sInst+". Expected: "+expr, lastParseException, file, sInst.token.line, sInst.token.col);
							throw lastParseException;
						} else {
							match = false;
						}
					}
				}
				i--;
				if (flag) {
					pushb(match);
				}
			} else {
				SymbolInstance sInst = next();
				if (!sInst.is(symbol)) {
					lastParseException = new ParseException("Unexpected token: "+sInst+". Expected: "+symbol, lastParseException, file, sInst.token.line, sInst.token.col);
					throw lastParseException;
				}
				r[i] = sInst;
			}
		}
		lastParseException = null;
		return r;
	}
	
	private static float asFloat(Object v) {
		if (v instanceof Double) {
			return ((Double)v).floatValue();
		} else if (v instanceof Float) {
			return ((Float)v).floatValue();
		} else if (v instanceof Integer) {
			return ((Integer)v).floatValue();
		} else {
			throw new IllegalArgumentException("Invalid numeric object: "+v);
		}
	}
	
	private SymbolInstance accept(String keyword) throws ParseException {
		SymbolInstance symbol = next();
		if (!symbol.is(keyword)) {
			throw new ParseException("Unexpected token: "+symbol+". Expected: "+keyword, file, symbol.token.line, symbol.token.col);
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
		throw new ParseException("Unexpected token: "+symbol+". Expected: "+join("|", types), file, symbol.token.line, symbol.token.col);
	}
	
	private SymbolInstance accept(TokenType type) throws ParseException {
		SymbolInstance symbol = next(type != TokenType.EOL);
		if (symbol.token.type != type) {
			throw new ParseException("Unexpected token: "+symbol+". Expected: "+type, file, symbol.token.line, symbol.token.col);
		}
		return symbol;
	}
	
	private SymbolInstance replace(final int index, String symbol) {
		return replace(index, symbol, null);
	}
	
	private SymbolInstance replace(final int index, String symbol, String typename) {
		SymbolInstance newInst = new SymbolInstance(syntax.getSymbol(symbol), typename);
		while (it.nextIndex() > index) {
			SymbolInstance sInst = prev();
			it.remove();
			newInst.expression.add(0, sInst);
		}
		it.add(newInst);
		return newInst;
	}
	
	private void revert(final int index, final int instructionAddress, SymbolInstance preserve) {
		while (it.nextIndex() > index) {
			SymbolInstance sInst = prev();
			if (sInst != preserve && sInst.expression != null) {
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
	
	private SymbolInstance toSymbol(Token token) throws ParseException {
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
			case CHAR:
				sInst = new SymbolInstance(Syntax.CHAR, token);
				break;
			case KEYWORD:
				sInst = new SymbolInstance(syntax.getSymbol(token.value), token);
				break;
			case ANNOTATION:
				sInst = new SymbolInstance(Syntax.ANNOTATION, token);
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
	
	private Instruction jz(int dstIp) {
		return jz(dstIp, false);
	}
	
	private Instruction jz(int dstIp, boolean noYield) {
		int ip = getIp();
		Instruction instruction = Instruction.fromKeyword("JZ");
		if (dstIp > ip || noYield) {
			instruction.mode = OPCodeMode.FORWARD;
		}
		instruction.intVal = dstIp;
		instruction.lineNumber = line;
		instructions.add(instruction);
		return instruction;
	}
	
	private Instruction jz() {
		Instruction instruction = Instruction.fromKeyword("JZ");
		instruction.mode = OPCodeMode.FORWARD;
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
	
	private void pushi(int val, String strval) {
		Instruction instruction = Instruction.fromKeyword("PUSHI");
		instruction.intVal = val;
		instruction.strVal = strval;
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	@SuppressWarnings("unused")
	private void pushChallengeId() throws ParseError {
		if (challengeId == null) {
			throw new ParseError("Challenge id not set", file, line, col);
		} else if (challengeId < 0) {
			throw new ParseError("Challenge id \""+challengeName+"\" is dummy", file, line, col);
		}
		Instruction instruction = Instruction.fromKeyword("PUSHI");
		instruction.intVal = challengeId;
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void pushf(float val) {
		Instruction instruction = Instruction.fromKeyword("PUSHF");
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
	
	private void pushb(String variable) throws ParseException {
		Instruction instruction = Instruction.fromKeyword("PUSHB");
		instruction.mode = OPCodeMode.REF;
		instruction.intVal = getVarId(variable);
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void pushi(String constant) throws ParseException {
		Instruction instruction = Instruction.fromKeyword("PUSHI");
		Object val = getConstant(constant);
		if (val instanceof Integer) {
			instruction.intVal = (int)val;
		} else if (val instanceof LHEnum) {
			instruction.intVal = ((LHEnum)val).value();
		} else {
			lastParseException = new ParseException("Unsupported type for constant: "+val, file, line, col);
			throw lastParseException;
		}
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void pushiVar(String variable) throws ParseException {
		Instruction instruction = Instruction.fromKeyword("PUSHI");
		instruction.mode = OPCodeMode.REF;
		instruction.intVal = getVarId(variable);
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void pushf(String variable) throws ParseException {
		Instruction instruction = Instruction.fromKeyword("PUSHF");
		instruction.mode = OPCodeMode.REF;
		instruction.intVal = getVarId(variable);
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void pusho(String variable) throws ParseException {
		Instruction instruction = Instruction.fromKeyword("PUSHO");
		instruction.mode = OPCodeMode.REF;
		instruction.intVal = getVarId(variable);
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void popf() {
		Instruction instruction = Instruction.fromKeyword("POPF");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void popo() {
		Instruction instruction = Instruction.fromKeyword("POPO");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void pop(Var var) throws ParseException {
		if (var.type == DataType.INT || "java.lang.String".equals(var.typename)) {
			popi(var.name);
		} else if (var.type == DataType.OBJECT) {
			popo(var.name);
		} else if (var.type == DataType.BOOLEAN) {
			popb(var.name);
		} else if (var.type == DataType.FLOAT) {
			popf(var.name);
		} else {
			throw new ParseError("Unsupported variable type: " + var.typename, file, line, col);
		}
	}
	
	private void popi(String variable) throws ParseException {
		Instruction instruction = Instruction.fromKeyword("POPI");
		instruction.mode = OPCodeMode.REF;
		instruction.intVal = getVarId(variable);
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void popb(String variable) throws ParseException {
		Instruction instruction = Instruction.fromKeyword("POPB");
		instruction.mode = OPCodeMode.REF;
		instruction.intVal = getVarId(variable);
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void popf(String variable) throws ParseException {
		Instruction instruction = Instruction.fromKeyword("POPF");
		instruction.mode = OPCodeMode.REF;
		instruction.intVal = getVarId(variable);
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void popf(int varId) throws ParseException {
		Instruction instruction = Instruction.fromKeyword("POPF");
		instruction.mode = OPCodeMode.REF;
		instruction.intVal = varId;
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void popo(int varId) throws ParseException {
		Instruction instruction = Instruction.fromKeyword("POPO");
		instruction.mode = OPCodeMode.REF;
		instruction.intVal = varId;
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void popi(int varId) throws ParseException {
		Instruction instruction = Instruction.fromKeyword("POPI");
		instruction.mode = OPCodeMode.REF;
		instruction.intVal = varId;
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void popb(int varId) throws ParseException {
		Instruction instruction = Instruction.fromKeyword("POPB");
		instruction.mode = OPCodeMode.REF;
		instruction.intVal = varId;
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void popo(String variable) throws ParseException {
		Instruction instruction = Instruction.fromKeyword("POPO");
		instruction.mode = OPCodeMode.REF;
		instruction.intVal = getVarId(variable);
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
	
	private void checkContext(NativeFunction func) {
		return;	//Not so important
	}
	
	private void sys(NativeFunction func) {
		checkContext(func);
		Instruction instruction = Instruction.fromKeyword("SYS");
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
	
	private void negi() {
		Instruction instruction = Instruction.fromKeyword("NEGI");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void negf() {
		Instruction instruction = Instruction.fromKeyword("NEGF");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void negc() {
		Instruction instruction = Instruction.fromKeyword("NEGC");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void muli() {
		Instruction instruction = Instruction.fromKeyword("MULI");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void mulf() {
		Instruction instruction = Instruction.fromKeyword("MULF");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void mulc() {
		Instruction instruction = Instruction.fromKeyword("MULC");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void divi() {
		Instruction instruction = Instruction.fromKeyword("DIVI");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void divf() {
		Instruction instruction = Instruction.fromKeyword("DIVF");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void divc() {
		Instruction instruction = Instruction.fromKeyword("DIVC");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void modi() {
		Instruction instruction = Instruction.fromKeyword("MODI");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void trunc() {
		casti();
		castf();
	}
	
	private void modf() {
		//The original MODF operator is bugged, we reimplement it with discrete operators
		swap();			//b, a
		copyto(2);		//a, b, a
		swap();			//a, a, b
		copyto(2);		//a, b, a, b
		divf();			//a, b, a/b
		trunc();		//a, b, trunc(a / b)
		swap();			//a, trunc(a / b), b
		mulf();			//a, trunc(a / b) * b
		subf();			//a - trunc(a / b) * b
		/*Instruction instruction = Instruction.fromKeyword("MODF");
		instruction.lineNumber = line;
		instructions.add(instruction);*/
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
	
	private void eqi() {
		Instruction instruction = Instruction.fromKeyword("EQI");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void neq() {
		Instruction instruction = Instruction.fromKeyword("NEQ");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void neqi() {
		Instruction instruction = Instruction.fromKeyword("NEQI");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void geq() {
		Instruction instruction = Instruction.fromKeyword("GEQ");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void geqi() {
		subi();
		dup();
		pushi(0);
		eqi();
		pushi(Integer.MAX_VALUE);
		lequ();
		or();
	}
	
	private void gequ() {
		Instruction instruction = Instruction.fromKeyword("GEQI");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void leq() {
		Instruction instruction = Instruction.fromKeyword("LEQ");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void leqi() {
		subi();
		dup();
		pushi(0);
		eqi();
		pushi(Integer.MAX_VALUE);
		gequ();
		or();
	}
	
	private void lequ() {
		Instruction instruction = Instruction.fromKeyword("LEQI");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void gt() {
		Instruction instruction = Instruction.fromKeyword("GT");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void gti() {
		subi();
		pushi(Integer.MAX_VALUE);
		ltu();
	}
	
	private void gtu() {
		Instruction instruction = Instruction.fromKeyword("GTI");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void lt() {
		Instruction instruction = Instruction.fromKeyword("LT");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void lti() {
		subi();
		pushi(Integer.MAX_VALUE);
		gtu();
	}
	
	private void ltu() {
		Instruction instruction = Instruction.fromKeyword("LTI");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private Instruction jmp(int dstIp, boolean noYield) {
		int ip = getIp();
		Instruction instruction = Instruction.fromKeyword("JMP");
		if (dstIp > ip || noYield) {
			instruction.mode = OPCodeMode.FORWARD;
		}
		instruction.intVal = dstIp;
		instruction.lineNumber = line;
		instructions.add(instruction);
		return instruction;
	}
	
	private Instruction jmp() {
		Instruction instruction = Instruction.fromKeyword("JMP");
		instruction.mode = OPCodeMode.FORWARD;
		instruction.lineNumber = line;
		instructions.add(instruction);
		return instruction;
	}
	
	private void sleep() {
		Instruction instruction = Instruction.fromKeyword("SLEEP");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	@SuppressWarnings("unused")
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
	
	/**
	 * Cast unsigned int to float. Do not use with signed int!
	 */
	private void castuf() {
		Instruction instruction = Instruction.fromKeyword("CASTF");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void castf() {
		//The builtin CASTF actually casts from unsigned int, we provide a sign-aware implementation
		Instruction j1, j2;
		dup();			//x, x
		pushi(0);		//x, x, 0
		lti();			//x, isNegative
		j1 = jz();		//x
		//x is negative, negate, cast, negate again
			negi();		//-x
			castuf();	//-x
			negf();		//x
			j2 = jmp();
		j1.intVal = getIp();
		//x is positive, castuf is fine
			castuf();	//x
		j2.intVal = getIp();
	}
	
	private void castf(int ip) {
		Instruction instruction = Instruction.fromKeyword("CASTF");
		instruction.lineNumber = line;
		instructions.add(ip, instruction);
	}
	
	private void zero(String var) throws ParseException {
		Instruction instruction = Instruction.fromKeyword("ZERO");
		instruction.intVal = getVarId(var);
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void call(String scriptname, List<String> argtypes) {
		Instruction instruction = Instruction.fromKeyword("CALL");
		instruction.lineNumber = line;
		instructions.add(instruction);
		ScriptToResolve call = new ScriptToResolve(file, line, instruction, scriptname, argtypes);
		calls.add(call);
	}
	
	private void start(String scriptname, List<String> argtypes) {
		Instruction instruction = Instruction.fromKeyword("START");
		instruction.lineNumber = line;
		instructions.add(instruction);
		ScriptToResolve call = new ScriptToResolve(file, line, instruction, scriptname, argtypes);
		calls.add(call);
	}
	
	private void free() {
		Instruction instruction = Instruction.fromKeyword("FREE");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void swap() {
		Instruction instruction = Instruction.fromKeyword("SWAP");
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	/**Shorthand for copyto(1).
	 */
	private void dup() {
		copyto(1);
	}
	
	private void copyto(int offset) {
		Instruction instruction = Instruction.fromKeyword("COPYTO");
		instruction.intVal = offset;
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private void movefrom(int offset) {
		Instruction instruction = Instruction.fromKeyword("MOVEFROM");
		instruction.intVal = offset;
		instruction.lineNumber = line;
		instructions.add(instruction);
	}
	
	private int getIp() {
		return instructions.size();
	}
	
	private Void _null() {
		pusho(0);
		return null;
	}
	
	private Void Coord_Coord() {
		//Nothing to do here
		return null;
	}
	
	private Void Coord_add() {
		addc();
		return null;
	}
	
	private Void Coord_sub() {
		subc();
		return null;
	}

	private Void Coord_mul() {
		mulc();
		return null;
	}
	
	private Void Coord_div() {
		divc();
		return null;
	}
	
	private Void Coord_getX() {
		popf();
		popf();
		return null;
	}
	
	private Void Coord_getY() {
		popf();
		swap();
		popf();
		return null;
	}
	
	private Void Coord_getZ() {
		swap();
		popf();
		swap();
		popf();
		return null;
	}
	
	private Void Coord_equals() {
		sys(GET_DISTANCE);
		pushf(0f);
		eq();
		return null;
	}
	
	private Void Math_abs() {
		dup();
		pushf(0f);
		lt();
		Instruction skipNeg = jz();
		negf();
		skipNeg.intVal = getIp();
		return null;
	}
	
	private Void Math_ceil() {
		Instruction j1, j2, j3;
		dup();			//x, x
		pushf(1f);		//x, x, 1
		modf();			//x, d
		pushf(0f);		//x, d, 0
		neq();			//x, hasDecimals
		j1 = jz();		//x
		//x has non-zero decimal digits
			dup();			//x, x
			pushf(0f);		//x, x, 0
			lt();			//x, isNeg
			j2 = jz();		//x
			//x is negative and has decimals: ceil = x - fmod(x, 1)
				dup();			//x, x
				pushf(1f);		//x, x, 1
				modf();			//x, d
				subf();			//f
			j3 = jmp();
			//x is positive and has decimal digits: ceil = x - fmod(x, 1) + 1
			j2.intVal = getIp();
				dup();			//x, x
				pushf(1f);		//x, x, 1
				modf();			//x, d
				subf();			//t
				pushf(1f);		//t, 1
				addf();			//f
			j3.intVal = getIp();
		//end
		j1.intVal = getIp();
		return null;
	}
	
	private Void Math_copySign() {
		Instruction j1;
		//m, s
		swap();			//s, m
		copyto(2);		//m, s, m
		mulf();			//m, p
		pushf(0f);		//m, p, 0
		lt();			//m, oppositeSign
		j1 = jz();		//m
		//m and s have opposite sign
			negf();			//-m
		j1.intVal = getIp();
		return null;
	}
	
	/**Approximation of cosine function using angle reduction and Taylor/Maclaurin series of degree 9.
	 * @return
	 */
	private Void Math_cos() {
		Instruction j1;
		//First reduce the angle in the range [-PI/2, +PI/2]
		Math_reduceRadians();		//x
		dup();						//x, x
		Math_reduceRadiansHalf();	//x, [x]
		copyto(2);					//[x], x, [x]
		neq();						//[x], isReduced
		swap();			//isReduced, [x]
		//
		dup();			//isReduced, [x], [x]
		mulf();			//isReduced, x2
		dup();			//isReduced, x2, x2
		dup();			//isReduced, x2, x2, x2
		dup();			//isReduced, x2, x2, x2, x2
		dup();			//isReduced, x2, x2, x2, x2, x2
		pushf(-2);		//isReduced, x2, x2, x2, x2, x2, -2
		divf();			//isReduced, x2, x2, x2, x2, -x2/2
		copyto(5);		//isReduced, -x2/2, x2, x2, x2, x2, -x2/2
		popf();			//isReduced, -x2/2, x2, x2, x2, x2
		mulf();			//isReduced, -x2/2, x2, x2, x4
		dup();			//isReduced, -x2/2, x2, x2, x4, x4
		pushf(24);		//isReduced, -x2/2, x2, x2, x4, x4, 24
		divf();			//isReduced, -x2/2, x2, x2, x4, x4/24
		copyto(4);		//isReduced, -x2/2, x4/24, x2, x2, x4, x4/24
		popf();			//isReduced, -x2/2, x4/24, x2, x2, x4
		mulf();			//isReduced, -x2/2, x4/24, x2, x6
		dup();			//isReduced, -x2/2, x4/24, x2, x6, x6
		pushf(-720);	//isReduced, -x2/2, x4/24, x2, x6, x6, -720
		divf();			//isReduced, -x2/2, x4/24, x2, x6, -x6/720
		copyto(3);		//isReduced, -x2/2, x4/24, -x6/720, x2, x6, -x6/720
		popf();			//isReduced, -x2/2, x4/24, -x6/720, x2, x6
		mulf();			//isReduced, -x2/2, x4/24, -x6/720, x8
		pushf(40320);	//isReduced, -x2/2, x4/24, -x6/720, x8, 40320
		divf();			//isReduced, -x2/2, x4/24, -x6/720, x8/40320
		addf();			//isReduced, -x2/2, x4/24, -x6/720 + x8/40320
		addf();			//isReduced, -x2/2, x4/24 + -x6/720 + x8/40320
		addf();			//isReduced, -x2/2 + x4/24 + -x6/720 + x8/40320
		pushf(1);		//isReduced, -x2/2 + x4/24 + -x6/720 + x8/40320, 1
		addf();			//isReduced, -x2/2 + x4/24 + -x6/720 + x8/40320 + 1
		//If the angle was reduced, invert the sign of the result
		swap();			//cos, isReduced
		j1 = jz();		//cos
		//[x] != x, negate the result
			negf();		//-cos
		//
		j1.intVal = getIp();
		return null;
	}
	
	private Void Math_floor() {
		Instruction j1, j2, j3;
		dup();			//x, x
		pushf(1f);		//x, x, 1
		modf();			//x, d
		pushf(0f);		//x, d, 0
		neq();			//x, hasDecimals
		j1 = jz();		//x
		//x has non-zero decimal digits
			dup();			//x, x
			pushf(0f);		//x, x, 0
			lt();			//x, isNeg
			j2 = jz();		//x
			//x is negative and has decimals: floor = x - fmod(x, 1) - 1
				dup();			//x, x
				pushf(1f);		//x, x, 1
				modf();			//x, d
				subf();			//t
				pushf(1f);		//t, 1
				subf();			//f
			j3 = jmp();
			//x is positive and has decimal digits: floor = x - fmod(x, 1)
			j2.intVal = getIp();
				dup();			//x, x
				pushf(1f);		//x, x, 1
				modf();			//x, d
				subf();			//f
			j3.intVal = getIp();
		//end
		j1.intVal = getIp();
		return null;
	}
	
	private Void Math_hypot() {
		//x, y
		dup();				//x, y, y
		mulf();				//x, y2
		swap();				//y2, x
		dup();				//y2, x, x
		mulf();				//y2, x2
		addf();				//y2+x2
		sys(SQUARE_ROOT);	//h
		return null;
	}
	
	private Void Math_max() {
		Instruction j1;
		//a, b
		copyto(2);		//b, a, b
		swap();			//b, b, a
		copyto(3);		//a, b, b, a
		gt();			//a, b, b>a
		j1 = jz();		//a, b
		//b > a, a must be on top of the stack
			swap();			//b, a
		j1.intVal = getIp();
		popf();
		return null;
	}
	
	private Void Math_min() {
		Instruction j1;
		//a, b
		copyto(2);		//b, a, b
		swap();			//b, b, a
		copyto(3);		//a, b, b, a
		lt();			//a, b, b<a
		j1 = jz();		//a, b
		//b < a, a must be on top of the stack
			swap();			//b, a
		j1.intVal = getIp();
		popf();
		return null;
	}
	
	/**Reduces any angle to the range <code>[-PI, PI]</code>.
	 * @return
	 */
	public Void Math_reduceRadians() {
		final float PI2 = (float)(2 * Math.PI);
		dup();			//x, x
		pushf(PI2);		//x, x, 2PI
		divf();			//x, x / 2PI
		Math_round();	//x, round(x / 2PI)
		pushf(-PI2);	//x, round(x / 2PI), -2PI
		mulf();			//x, -2PI * round(x / 2PI)
		addf();			//x -2PI * round(x / 2PI)
		return null;
	}
	
	/**Given an angle in the range <code>[-PI, PI]</code>, reduces it to <code>[-PI/2, PI/2]</code>.<br/>
	 * This makes no check on the input value, passing a value out of the expected range leads to unexpected results.<br/>
	 * The first reduction can be achieved with Math_reduceRadians.<br/>
	 * The reduced angle <i>r</i> has the following property:<br/>
	 *   <code>sin(r) == sin(a)</code><br/>
	 * @return
	 */
	public Void Math_reduceRadiansHalf() {
		final float PI = (float)Math.PI;
		Instruction j1, j2, j3;
		dup();			//x, x
		pushf(-PI_2);	//x, x, -PI/2
		lt();			//x, bool
		j1 = jz();		//x
		//x < -PI/2, bring it to the fourth quadrant 
			negf();			//-x
			pushf(-PI);		//-x, -PI
			addf();			//r
		j2 = jmp();
		j1.intVal = getIp();
		dup();			//x, x
		pushf(PI_2);	//x, x, PI/2
		gt();			//x, bool
		j3 = jz();		//x
		//x > PI/2, bring it to the first quadrant
			negf();			//-x
			pushf(PI)	;	//-x, PI
			addf();			//r
		//
		j3.intVal = getIp();
		j2.intVal = getIp();
		return null;
	}
	
	private Void Math_round() {
		Instruction j1, j2;
		dup();			//x, x
		pushf(1f);		//x, x, 1
		modf();			//x, d
		dup();			//x, d, d
		Math_abs();		//x, d, |d|
		pushf(0.5f);	//x, d, |d|, 0.5
		lt();			//x, d, roundDown
		j1 = jz();		//x, d
		//|d| < 0.5, round = x - fmod(x, d)
			subf();			//r
		j2 = jmp();
		j1.intVal = getIp();
		//|d| >= 0.5, round = x - fmod(x, d) + signum(x)
			swap();			//d, x
			dup();			//d, x, x
			Math_signum();	//d, x, s
			addf();			//d, x+s
			swap();			//x+s, d
			subf();			//r
		j2.intVal = getIp();
		return null;
	}
	
	private Void Math_signum() {
		Instruction j1, j2, j3, j4;
		dup();		//x, x
		pushf(0);	//x, x, 0
		lt();		//x, isNegative
		j1 = jz();	//x
		//x < 0, return -1
			popf();		//
			pushf(-1);	//-1
		j2 = jmp();
		j1.intVal = getIp();
		pushf(0);	//x, 0
		gt();		//isPositive
		j3 = jz();	//
		//x > 0, return +1
			pushf(1);	//1
		j4 = jmp();
		j3.intVal = getIp();
		//x == 0, return 0
			pushf(0);	//0
		//
		j2.intVal = getIp();
		j4.intVal = getIp();
		return null;
	}
	
	/**Approximation of sine function using angle reduction and Taylor/Maclaurin series of degree 9.
	 * @return
	 */
	private Void Math_sin() {
		//First reduce the angle in the range [-PI/2, +PI/2] to improve accuracy
		Math_reduceRadians();		//x
		Math_reduceRadiansHalf();	//x
		//
		dup();			//x, x
		dup();			//x, x, x
		dup();			//x, x, x, x
		mulf();			//x, x, x2
		dup();			//x, x, x2, x2
		dup();			//x, x, x2, x2, x2
		dup();			//x, x, x2, x2, x2, x2
		movefrom(5);	//x, x2, x2, x2, x2, x
		mulf();			//x, x2, x2, x2, x3
		dup();			//x, x2, x2, x2, x3, x3
		pushf(-6);		//x, x2, x2, x2, x3, x3, -6
		divf();			//x, x2, x2, x2, x3, -x3/6
		copyto(5);		//x, -x3/6, x2, x2, x2, x3, -x3/6
		popf();			//x, -x3/6, x2, x2, x2, x3
		mulf();			//x, -x3/6, x2, x2, x5
		dup();			//x, -x3/6, x2, x2, x5, x5
		pushf(120);		//x, -x3/6, x2, x2, x5, x5, 120
		divf();			//x, -x3/6, x2, x2, x5, x5/120
		copyto(4);		//x, -x3/6, x5/120, x2, x2, x5, x5/120
		popf();			//x, -x3/6, x5/120, x2, x2, x5
		mulf();			//x, -x3/6, x5/120, x2, x7
		dup();			//x, -x3/6, x5/120, x2, x7, x7
		pushf(-5040);	//x, -x3/6, x5/120, x2, x7, x7, -5040
		divf();			//x, -x3/6, x5/120, x2, x7, -x7/5040
		copyto(3);		//x, -x3/6, x5/120, -x7/5040, x2, x7, -x7/5040
		popf();			//x, -x3/6, x5/120, -x7/5040, x2, x7
		mulf();			//x, -x3/6, x5/120, -x7/5040, x9
		pushf(362880);	//x, -x3/6, x5/120, -x7/5040, x9, 362880
		divf();			//x, -x3/6, x5/120, -x7/5040, x9/362880
		addf();			//x, -x3/6, x5/120, -x7/5040 + x9/362880
		addf();			//x, -x3/6, x5/120 + -x7/5040 + x9/362880
		addf();			//x, -x3/6 + x5/120 + -x7/5040 + x9/362880
		addf();			//x + -x3/6 + x5/120 + -x7/5040 + x9/362880
		return null;
	}
	
	private Void Math_toDegrees() {
		pushf(57.2958f);
		mulf();
		return null;
	}
	
	private Void Math_toRadians() {
		pushf(0.0174533f);
		mulf();
		return null;
	}
	
	private Void Task_sleep() {
		int lblLoop = getIp();
		dup();			//This avoids to evaluate the argument expression at every iteration
		sleep();
		jz(lblLoop);
		popf();			//Discard the original argument value
		return null;
	}
	
	private static String join(String sep, Object[] items) {
		if (items.length == 0) return "";
		String r = String.valueOf(items[0]);
		for (int i = 1; i < items.length; i++) {
			r += sep + String.valueOf(items[i]);
		}
		return r;
	}
	
	private static String join(String sep, Iterable<? extends Object> items) {
		Iterator<?> iterator = items.iterator();
		if (!iterator.hasNext()) return "";
		String r = String.valueOf(iterator.next());
		while (iterator.hasNext()) {
			r += sep + String.valueOf(iterator.next());
		}
		return r;
	}
	
	private static long crc32(File file) throws IOException {
		byte[] data = Files.readAllBytes(file.toPath());
        CRC32 checksum = new CRC32();
        checksum.update(data);
        return checksum.getValue();
	}
}
