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
			File inp = cmd.getArgFile("-i");
			File out = cmd.getArgFile("-o");
			boolean merge = cmd.getArgFlag("-merge");
			chlToAsm(inp, out, merge);
		} else if (cmd.getArgFlag("-asmchl")) {
			File prj = cmd.getArgFile("-p");
			List<File> inp = cmd.getArgFiles("-i");
			File out = cmd.getArgFile("-o");
			if (prj == null) {
				if (inp.isEmpty()) throw new Exception("Please specify either -i or -p");
			} else {
				if (!inp.isEmpty()) throw new Exception("Please specify either -i or -p");
				inp = readProject(prj);
			}
			asmToChl(inp, out);
		} else if (cmd.getArgFlag("-compile")) {
			File prj = cmd.getArgFile("-p");
			List<File> inp = cmd.getArgFiles("-i");
			File out = cmd.getArgFile("-o");
			if (prj == null) {
				if (inp.isEmpty()) throw new Exception("Please specify either -i or -p");
			} else {
				if (!inp.isEmpty()) throw new Exception("Please specify either -i or -p");
				inp = readProject(prj);
			}
			compile(inp, out);
		} else if (cmd.getArgFlag("-cmp")) {
			File f1 = cmd.getArgFile("-f1");
			File f2 = cmd.getArgFile("-f2");
			compare(f1, f2);
		} else if (cmd.getArgFlag("-prref")) {
			File inp = cmd.getArgFile("-i");
			printInstructionReference(inp);
		} else if (cmd.getArgFlag("-prnatfn")) {
			printNatives();
		} else {
			printHelp();
			System.exit(1);
		}
	}
	
	private static void chlToAsm(File inp, File out, boolean merge) throws Exception {
		System.out.println("Loading compiled CHL...");
		CHLFile chl = new CHLFile();
		chl.read(inp);
		chl.checkCodeCoverage(System.out);
		chl.validate(System.out);
		System.out.println("Writing ASM sources...");
		ASMWriter writer = new ASMWriter();
		if (merge) {
			writer.writeMerged(chl, out);
		} else {
			writer.write(chl, out);
		}
		System.out.println("Done.");
	}
	
	private static void asmToChl(List<File> inp, File out) throws Exception {
		System.out.println("Parsing ASM sources...");
		ASMParser parser = new ASMParser();
		CHLFile chl = parser.parse(inp);
		System.out.println("Writing compiled CHL...");
		chl.write(out);
		System.out.println("Done.");
	}
	
	private static void compile(List<File> inp, File out) throws Exception {
		System.out.println("Parsing CHL sources...");
		CHLParser parser = new CHLParser();
		CHLFile chl = parser.parse(inp);
		System.out.println("Writing compiled CHL...");
		//TODO chl.write(out);
		System.out.println("Done.");
	}
	
	private static void compare(File f1, File f2) throws Exception {
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
	
	private static void printInstructionReference(File inp) throws Exception {
		System.out.println("Loading compiled CHL...");
		CHLFile chl = new CHLFile();
		chl.read(inp);
		System.out.println("Done.");
		chl.printInstructionReference(System.out);
	}
	
	private static void printNatives() throws Exception {
		for (NativeFunction f : NativeFunction.values()) {
			System.out.println(f.ordinal() + ": " + f.name() + f.getInfoString());
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
	
	private static void printHelp() {
		System.out.println("CHL Compiler");
		System.out.println("Version 0.1");
		System.out.println("Developer by Daniele Lombardi (alias Daniels118)");
		System.out.println();
		System.out.println("Syntax");
		System.out.println("  chlasm -chlasm -i filename -o filename [-merge]");
		System.out.println("  chlasm -asmchl -i files|-p filename -o filename");
		//System.out.println("  chlasm -compile -i files|-p filename -o filename");
		System.out.println("  chlasm -cmp -f1 filename -f2 filename");
		System.out.println("  chlasm -prref -i filename");
		System.out.println("  chlasm -prnatfn");
		System.out.println();
		System.out.println("Arguments");
		System.out.println("  -chlasm  convert chl file to asm");
		System.out.println("  -asmchl  convert asm file to chl");
		//System.out.println("  -compile compile source files into asm file");
		System.out.println("  -cmp     compare chl files ignoring line numbers");
		System.out.println("  -prref   analyze the instructions in a chl file and print a table summary");
		System.out.println("  -prnatfn prints the list of native functions");
	}
}
