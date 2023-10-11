package it.ld.bw.chl.lang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import it.ld.bw.chl.Main;

public class CHLParser {
	private static final String SYNTAX_FILE = "syntax.txt";
	
	private static void loadSyntax() {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream(SYNTAX_FILE)));) {
			String row;
			while ((row = reader.readLine()) != null) {
				if (!row.startsWith("#")) {
					
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
