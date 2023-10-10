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
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

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
				if (in(topic, "chlasm", "asmchl", "compile", "cmp", "prref", "prnatfn")) {
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
		File prj = cmd.getArgFile("-p");
		List<File> inp = cmd.getArgFiles("-i");
		if (prj == null) {
			if (inp.isEmpty()) throw new Exception("Please specify either -i or -p");
		} else {
			if (!inp.isEmpty()) throw new Exception("Please specify either -i or -p");
			inp = readProject(prj);
		}
		File out = mandatory(cmd.getArgFile("-o"), "-o");
		//
		System.out.println("Parsing ASM sources...");
		ASMParser parser = new ASMParser();
		CHLFile chl = parser.parse(inp);
		System.out.println("Writing compiled CHL...");
		chl.write(out);
		System.out.println("Done.");
	}
	
	private static void compile(CmdLine cmd) throws Exception {
		File prj = cmd.getArgFile("-p");
		List<File> inp = cmd.getArgFiles("-i");
		if (prj == null) {
			if (inp.isEmpty()) throw new Exception("Please specify either -i or -p");
		} else {
			if (!inp.isEmpty()) throw new Exception("Please specify either -i or -p");
			inp = readProject(prj);
		}
		File out = mandatory(cmd.getArgFile("-o"), "-p");
		//
		System.out.println("Parsing CHL sources...");
		CHLParser parser = new CHLParser();
		CHLFile chl = parser.parse(inp);
		System.out.println("Writing compiled CHL...");
		//TODO chl.write(out);
		System.out.println("Done.");
	}
	
	private static void compare(CmdLine cmd) throws Exception {
		File f1 = mandatory(cmd.getArgFile("-f1"), "-f1");
		File f2 = mandatory(cmd.getArgFile("-f2"), "-f2");
		//
		System.out.println("Loading "+f1.getName()+"...");
		CHLFile chl1 = new CHLFile();
		chl1.read(f1);
		System.out.println("Loading "+f2.getName()+"...");
		CHLFile chl2 = new CHLFile();
		chl2.read(f2);
		System.out.println("Comparing...");
		CHLComparator comparator = new CHLComparator();
		comparator.compare(chl1, chl2);
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
	
	private static List<File> readProject(File prj) throws IOException {
		List<File> res = new LinkedList<File>();
		Path path = prj.getParentFile().toPath();
		try (BufferedReader str = new BufferedReader(new FileReader(prj));) {
			String line = str.readLine();
			while (line != null) {
				line = line.trim();
				if (!line.isEmpty()) {
					File file = path.resolve(line).toFile();
					res.add(file);
				}
				line = str.readLine();
			}
		}
		return res;
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
