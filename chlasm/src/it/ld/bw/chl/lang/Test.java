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
		files.add(new File("test/constants.txt"));
		files.add(new File("test/test.txt"));
		//files.add(new File("test/test_expr.chl"));
		CHLCompiler compiler = new CHLCompiler();
		compiler.setIgnoreMissingScriptsEnabled(true);
		//compiler.loadHeader(new File("test/AllMeshes.h"));
		compiler.loadHeader(new File("test/AudioMusic.h"));
		compiler.loadHeader(new File("test/AudioSFX.h"));
		compiler.loadHeader(new File("test/CreatureEnum.h"));
		compiler.loadHeader(new File("test/CreatureSpec.h"));
		compiler.loadHeader(new File("test/Enum.h"));
		compiler.loadHeader(new File("test/GStates.h"));
		//compiler.loadHeader(new File("test/HelpTextEnums.h"));
		compiler.loadHeader(new File("test/HitRegions.h"));
		compiler.loadHeader(new File("test/ScriptEnums.h"));
		//
		compiler.loadInfo(new File("test/info1.txt"));
		compiler.loadInfo(new File("test/InfoScript1.txt"));
		//compiler.setTraceStream(System.out);
		CHLFile chl = compiler.compile(files);
		chl.write(new File("test/test.chl"));
		ASMWriter writer = new ASMWriter();
		writer.setSourcePath(Path.of("test"));
		writer.setPrintSourceCommentsEnabled(true);
		writer.setPrintSourceLinenoEnabled(true);
		writer.setPrintSourceLineEnabled(true);
		writer.writeMerged(chl, new File("test/test.asm"));
	}
}
