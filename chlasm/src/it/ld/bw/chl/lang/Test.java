package it.ld.bw.chl.lang;

import java.io.File;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import it.ld.bw.chl.model.CHLFile;

public class Test {
	public static void main(String[] args) throws Exception {
		//Syntax.printSymbols();
		//Syntax.printKeywords();
		//Syntax.printTree();
		List<File> files = new LinkedList<>();
		files.add(new File("test/Headers.txt"));
		files.add(new File("test/constants.chl"));
		files.add(new File("test/test.chl"));
		//files.add(new File("test/test_expr.chl"));
		CHLCompiler compiler = new CHLCompiler();
		compiler.loadHeader(new File("test/AudioSFX.h"));
		compiler.loadHeader(new File("test/CreatureEnum.h"));
		compiler.loadHeader(new File("test/CreatureSpec.h"));
		compiler.loadHeader(new File("test/Enum.h"));
		compiler.loadHeader(new File("test/GStates.h"));
		compiler.loadHeader(new File("test/HitRegions.h"));
		compiler.loadHeader(new File("test/ScriptEnums.h"));
		//compiler.setTraceStream(System.out);
		CHLFile chl = compiler.compile(files);
		ASMWriter writer = new ASMWriter();
		writer.setSourcePath(Path.of("test"));
		writer.setPrintSourceCommentsEnabled(true);
		writer.setPrintSourceLinenoEnabled(true);
		writer.setPrintSourceLineEnabled(true);
		writer.writeMerged(chl, new File("test/test.asm"));
	}
}
