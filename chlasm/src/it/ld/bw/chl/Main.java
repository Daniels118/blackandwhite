/* Copyright (c) 2023-2026 Daniele Lombardi / Daniels118
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import it.ld.bw.chl.exceptions.ParseError;
import it.ld.bw.chl.exceptions.ParseException;
import it.ld.bw.chl.lang.asm.ASMCompiler;
import it.ld.bw.chl.lang.asm.ASMWriter;
import it.ld.bw.chl.lang.chl.CHLCompiler;
import it.ld.bw.chl.lang.commons.CompilerOptions;
import it.ld.bw.chl.lang.commons.Project;
import it.ld.bw.chl.lang.commons.Syntax;
import it.ld.bw.chl.lang.java.JavaCompiler;
import it.ld.bw.chl.model.CHLFile;
import it.ld.bw.chl.model.Code;
import it.ld.bw.chl.model.NativeFunction;
import it.ld.utils.CmdLine;

public class Main {
	private static boolean verbose = false;
	
	static {
		Syntax.register("chl", CHLCompiler.class.getResourceAsStream("syntax.txt"));
		Syntax.register("java", JavaCompiler.class.getResourceAsStream("syntax.txt"));
	}
	
	public static void main(String[] args) {
		boolean printJavaStackTrace = true;
		try {
			CmdLine cmd = new CmdLine(args);
			printJavaStackTrace = cmd.getArgFlag("-jst");
			verbose = cmd.getArgFlag("-v");
			if (cmd.getArgFlag("-trace")) {
				CHLFile.traceEnabled = true;
				Code.traceEnabled = true;
			}
			if (cmd.getArgFlag("-chlasm")) {
				chlToAsm(cmd);
			} else if (cmd.getArgFlag("-asmchl")) {
				asmToChl(cmd);
			} else if (cmd.getArgFlag("-compile")) {
				compile(cmd);
			} else if (cmd.getArgFlag("-chlinfo")) {
				chlinfo(cmd);
			} else if (cmd.getArgFlag("-cmp")) {
				compare(cmd);
			} else if (cmd.getArgFlag("-prref")) {
				printInstructionReference(cmd);
			} else if (cmd.getArgFlag("-info")) {
				printInfo(cmd);
			} else {
				String topic = cmd.getArgVal("-help");
				if (topic == null) {
					printHelp("help.txt");
				} else {
					if (topic.startsWith("-")) topic = topic.substring(1);
					if (in(topic, "chlasm", "asmchl", "compile", "chlinfo", "cmp", "prref", "info")) {
						printHelp("help_" + topic + ".txt");
					} else {
						System.out.println("Unknown option: " + topic);
					}
				}
				System.exit(1);
			}
		} catch (ParseException|ParseError e) {
			Exception e2 = getCause(e);
			if (printJavaStackTrace) {
				e2.printStackTrace();
			} else {
				System.out.println(e2.getMessage());
			}
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private static void chlToAsm(CmdLine cmd) throws Exception {
		ASMWriter writer = new ASMWriter();
		File inp = mandatory(cmd.getArgFile("-i"), "-i");
		File out = cmd.getArgFile("-o");
		File prj = cmd.getArgFile("-p");
		if (prj == null) {
			if (out == null) throw new Exception("Please specify either -o or -p");
		} else {
			if (out != null) throw new Exception("Please specify either -o or -p");
			if (!prj.isDirectory()) throw new Exception("-p must be a directory");
		}
		writer.setPrintSourceLinenoEnabled(cmd.getArgFlag("-prlno"));
		writer.setPrintBinInfoEnabled(cmd.getArgFlag("-prbin"));
		File srcPath = cmd.getArgFile("-prsrc");
		//
		System.out.println("Loading compiled CHL...");
		CHLFile chl = new CHLFile();
		chl.read(inp);
		chl.checkCodeCoverage(System.out);
		chl.validate(System.out);
		System.out.println("Writing ASM sources...");
		if (srcPath != null) {
			writer.setSourcePath(srcPath.toPath());
			writer.setPrintSourceLineEnabled(true);
			writer.setPrintSourceCommentsEnabled(true);
		}
		if (out != null) {
			writer.writeMerged(chl, out);
		} else {
			writer.write(chl, prj);
		}
		System.out.println("Done.");
	}
	
	private static void asmToChl(CmdLine cmd) throws Exception {
		ASMCompiler compiler = new ASMCompiler();
		compiler.setVerboseEnabled(verbose);
		File prj = cmd.getArgFile("-p");
		Project project;
		if (prj == null) {
			project = new Project();
			project.sources = cmd.getArgFiles("-i");
			project.cHeaders = cmd.getArgFiles("-h");
			project.infoFiles = cmd.getArgFiles("-ih");
			if (project.sources.isEmpty()) throw new Exception("Please specify either -i or -p");
		} else {
			if (cmd.getArgFlag("-i")) throw new Exception("Please specify either -i or -p");
			project = Project.load(prj);
		}
		File out = mandatory(cmd.getArgFile("-o"), "-o");
		//
		System.out.println("Parsing ASM sources...");
		CHLFile chl = compiler.compile(project);
		System.out.println("Writing compiled CHL...");
		chl.write(out);
		System.out.println("Done.");
	}
	
	private static void compile(CmdLine cmd) throws Exception {
		Make make = new Make();
		CompilerOptions compilerOptions = make.getCompilerOptions();
		CHLLinker.Options linkerOptions = make.getLinkerOptions();
		compilerOptions.verbose = verbose;
		linkerOptions.verbose = verbose;
		Project project;
		if (cmd.getArgFlag("-path")) {
			//Original command syntax
			project = new Project();
			File path = cmd.getArgFile("-path");
			for (File f : path.listFiles()) {
				if (f.getName().endsWith(".h")) {
					project.cHeaders.add(f);
				}
			}
			File scriptpath = new File(path, mandatory(cmd.getArgVal("-scriptpath"), "-scriptpath"));
			project.sourcePath = scriptpath.toPath();
			for (String inputfileName : mandatory(cmd.getArgVals("-inputfile"), "-inputfile")) {
				if (inputfileName.endsWith(".chl")) {
					project.output = new File(scriptpath, inputfileName);
				} else {
					File inputfile = new File(scriptpath, inputfileName);
					try (BufferedReader reader = new BufferedReader(new FileReader(inputfile));) {
						String line;
						while ((line = reader.readLine()) != null) {
							if (!line.startsWith("//") && !line.isBlank()) {
								project.sources.add(new File(scriptpath, line));
							}
						}
					}
				}
			}
			if (project.output == null) {
				throw new RuntimeException("A chl output file as last parameter is mandatory");
			}
		} else {
			//Our syntax
			File prj = mandatory(cmd.getArgFile("-p"), "-p");
			project = Project.load(prj);
			project.output = cmd.getArgFile("-o", project.output);
		}
		File outAsm = cmd.getArgFile("-oasm");
		compilerOptions.sharedStrings = !cmd.getArgFlag("-noshr");
		linkerOptions.sharedStrings = compilerOptions.sharedStrings;
		compilerOptions.staticArrayCheck = !cmd.getArgFlag("-nosac");
		compilerOptions.extendedSyntax = cmd.getArgFlag("-ext");
		compilerOptions.returnEnabled = cmd.getArgFlag("-ret");
		compilerOptions.debug = cmd.getArgFlag("-dbg");
		linkerOptions.debug = compilerOptions.debug;
		project.clean |= cmd.getArgFlag("-clean");
		//
		CHLFile chl = make.make(project);
		if (outAsm != null) {
			System.out.println("Writing ASM sources...");
			ASMWriter writer = new ASMWriter();
			writer.setPrintSourceLinenoEnabled(true);
			if (project.sourcePath != null) {
				writer.setSourcePath(project.sourcePath);
				writer.setPrintSourceLineEnabled(true);
				writer.setPrintSourceCommentsEnabled(true);
			}
			writer.writeMerged(chl, outAsm);
		}
		System.out.println("Done.");
	}
	
	private static void chlinfo(CmdLine cmd) throws Exception {
		File f1 = mandatory(cmd.getArgFile("-i"), "-i");
		//
		System.out.println("Loading "+f1.getName()+"...");
		CHLFile chl1 = new CHLFile();
		chl1.read(f1);
		CHLInfoExtractor extractor = new CHLInfoExtractor();
		extractor.printInfo(chl1);
	}
	
	private static void compare(CmdLine cmd) throws Exception {
		File f1 = mandatory(cmd.getArgFile("-f1"), "-f1");
		File f2 = mandatory(cmd.getArgFile("-f2"), "-f2");
		boolean strict = cmd.getArgFlag("-strict");
		Set<String> scripts = new HashSet<>(cmd.getArgVals("-s"));
		if (scripts.isEmpty()) scripts = null;
		//
		System.out.println("Loading "+f1.getName()+"...");
		CHLFile chl1 = new CHLFile();
		chl1.read(f1);
		System.out.println("Loading "+f2.getName()+"...");
		CHLFile chl2 = new CHLFile();
		chl2.read(f2);
		System.out.println("Comparing...");
		CHLComparator comparator = new CHLComparator();
		comparator.setStrict(strict);
		comparator.compare(chl1, chl2, scripts);
	}
	
	private static void printInstructionReference(CmdLine cmd) throws Exception {
		File inp = mandatory(cmd.getArgFile("-i"), "-i");
		//
		System.out.println("Loading compiled CHL...");
		CHLFile chl = new CHLFile();
		chl.read(inp);
		System.out.println("Done.");
		chl.printInstructionReference(System.out);
	}
	
	private static void printInfo(CmdLine cmd) {
		String lang = cmd.getArgVal("-lang", "chl");
		Syntax syntax = Syntax.get(lang);
		String arg = mandatory(cmd.getArgVal("-info"), "-info");
		if ("keywords".equals(arg)) {
			syntax.printKeywords();
		} else if ("syntax".equals(arg)) {
			syntax.printSymbols();
		} else if ("syntax_tree".equals(arg)) {
			syntax.printTree();
		} else if ("native_functions".equals(arg)) {
			for (NativeFunction f : NativeFunction.values()) {
				System.out.println(f.getCStyleSignature());
			}
		} else {
			throw new RuntimeException("Invalid arg: " + arg);
		}
	}
	
	private static void printHelp(String filename) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream(filename)));) {
			String row;
			while ((row = reader.readLine()) != null) {
				if (!row.startsWith("//")) {
					System.out.println(row);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static boolean in(String needle, String...haystack) {
		for (String s : haystack) {
			if (s.equals(needle)) return true;
		}
		return false;
	}
	
	private static <T> T mandatory(T value, String name) {
		if (value == null) throw new RuntimeException(name + " is mandatory");
		return value;
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends Throwable> T getCause(T e) {
		Throwable cause = e.getCause();
		while (cause != null && cause != e && cause.getClass().isAssignableFrom(e.getClass())) {
			e = (T)e.getCause();
			cause = e.getCause();
		}
		return e;
	}
}
