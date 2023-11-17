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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import it.ld.bw.chl.exceptions.ParseError;
import it.ld.bw.chl.exceptions.ParseException;
import it.ld.bw.chl.lang.ASMCompiler;
import it.ld.bw.chl.lang.ASMWriter;
import it.ld.bw.chl.lang.CHLCompiler;
import it.ld.bw.chl.lang.Project;
import it.ld.bw.chl.lang.Syntax;
import it.ld.bw.chl.model.CHLFile;
import it.ld.bw.chl.model.Code;
import it.ld.bw.chl.model.NativeFunction;
import it.ld.utils.CmdLine;

public class Main {
	private static boolean verbose = false;
	private static boolean trace = false;
	
	public static void main(String[] args) {
		boolean printJavaStackTrace = true;
		try {
			CmdLine cmd = new CmdLine(args);
			printJavaStackTrace = cmd.getArgFlag("-jst");
			verbose = cmd.getArgFlag("-v");
			if (cmd.getArgFlag("-trace")) {
				trace = true;
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
		CHLCompiler compiler = new CHLCompiler();
		compiler.setVerboseEnabled(verbose);
		File prj = cmd.getArgFile("-p");
		Project project = Project.load(prj);
		File out = mandatory(cmd.getArgFile("-o"), "-p");
		File outAsm = cmd.getArgFile("-oasm");
		compiler.setSharedStringsEnabled(!cmd.getArgFlag("-noshr"));
		//
		CHLFile chl = compiler.compile(project);
		System.out.println("Writing compiled CHL...");
		chl.write(out);
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
		String arg = mandatory(cmd.getArgVal("-info"), "-info");
		if ("keywords".equals(arg)) {
			Syntax.printKeywords();
		} else if ("syntax".equals(arg)) {
			Syntax.printSymbols();
		} else if ("syntax_tree".equals(arg)) {
			Syntax.printTree();
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
