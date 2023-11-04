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
import java.util.List;
import java.util.Set;

import it.ld.bw.chl.lang.ASMParser;
import it.ld.bw.chl.lang.ASMWriter;
import it.ld.bw.chl.lang.CHLCompiler;
import it.ld.bw.chl.model.CHLFile;
import it.ld.bw.chl.model.NativeFunction;
import it.ld.utils.CmdLine;

public class Main {
	public static void main(String[] args) throws Exception {
		CmdLine cmd = new CmdLine(args);
		if (cmd.getArgFlag("-chlasm")) {
			chlToAsm(cmd);
		} else if (cmd.getArgFlag("-asmchl")) {
			asmToChl(cmd);
		} else if (cmd.getArgFlag("-compile")) {
			compile(cmd);
		} else if (cmd.getArgFlag("-info")) {
			info(cmd);
		} else if (cmd.getArgFlag("-cmp")) {
			compare(cmd);
		} else if (cmd.getArgFlag("-prref")) {
			printInstructionReference(cmd);
		} else if (cmd.getArgFlag("-prnatfn")) {
			printNatives(cmd);
		} else {
			String topic = cmd.getArgVal("-help");
			if (topic == null) {
				printHelp("help.txt");
			} else {
				if (topic.startsWith("-")) topic = topic.substring(1);
				if (in(topic, "chlasm", "asmchl", "compile", "info", "cmp", "prref", "prnatfn")) {
					printHelp("help_" + topic + ".txt");
				} else {
					System.out.println("Unknown option: " + topic);
				}
			}
			System.exit(1);
		}
	}
	
	private static void chlToAsm(CmdLine cmd) throws Exception {
		File inp = mandatory(cmd.getArgFile("-i"), "-i");
		File out = cmd.getArgFile("-o");
		File prj = cmd.getArgFile("-p");
		if (prj == null) {
			if (out == null) throw new Exception("Please specify either -o or -p");
		} else {
			if (out != null) throw new Exception("Please specify either -o or -p");
			if (!prj.isDirectory()) throw new Exception("-p must be a directory");
		}
		boolean printSourceLineNumbers = cmd.getArgFlag("-prlno");
		File srcPath = cmd.getArgFile("-prsrc");
		//
		System.out.println("Loading compiled CHL...");
		CHLFile chl = new CHLFile();
		chl.read(inp);
		chl.checkCodeCoverage(System.out);
		chl.validate(System.out);
		System.out.println("Writing ASM sources...");
		ASMWriter writer = new ASMWriter();
		writer.setPrintSourceLinenoEnabled(printSourceLineNumbers);
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
		ASMParser parser = new ASMParser();
		File prj = cmd.getArgFile("-p");
		List<File> inp = cmd.getArgFiles("-i");
		if (prj == null) {
			if (inp.isEmpty()) throw new Exception("Please specify either -i or -p");
		} else {
			if (!inp.isEmpty()) throw new Exception("Please specify either -i or -p");
			Project project = Project.load(prj);
			inp = project.sources;
			for (File file : project.cHeaders) {
				parser.loadHeader(file);
			}
			for (File file : project.infoFiles) {
				parser.loadInfo(file);
			}
		}
		File out = mandatory(cmd.getArgFile("-o"), "-o");
		//
		System.out.println("Parsing ASM sources...");
		CHLFile chl = parser.parse(inp);
		System.out.println("Writing compiled CHL...");
		chl.write(out);
		System.out.println("Done.");
	}
	
	private static void compile(CmdLine cmd) throws Exception {
		CHLCompiler compiler = new CHLCompiler();
		File prj = cmd.getArgFile("-p");
		List<File> inp = cmd.getArgFiles("-i");
		if (prj == null) {
			if (inp.isEmpty()) throw new Exception("Please specify either -i or -p");
		} else {
			if (!inp.isEmpty()) throw new Exception("Please specify either -i or -p");
			Project project = Project.load(prj);
			inp = project.sources;
			for (File file : project.cHeaders) {
				compiler.loadHeader(file);
			}
			for (File file : project.infoFiles) {
				compiler.loadInfo(file);
			}
		}
		File out = mandatory(cmd.getArgFile("-o"), "-p");
		//
		System.out.println("Parsing CHL sources...");
		CHLFile chl = compiler.compile(inp);
		System.out.println("Writing compiled CHL...");
		chl.write(out);
		System.out.println("Done.");
	}
	
	private static void info(CmdLine cmd) throws Exception {
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
	
	private static void printNatives(CmdLine cmd) throws Exception {
		for (NativeFunction f : NativeFunction.values()) {
			System.out.println(f.getCStyleSignature());
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
}
