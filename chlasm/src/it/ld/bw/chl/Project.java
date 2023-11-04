package it.ld.bw.chl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import it.ld.bw.chl.exceptions.ParseException;

public class Project {
	public final List<File> sources = new LinkedList<>();
	public final List<File> cHeaders = new LinkedList<>();
	public final List<File> infoFiles = new LinkedList<>();
	
	public static Project load(File projectFile) throws ParseException, FileNotFoundException, IOException {
		Project res = new Project();
		int lineno = 0;
		Path path = projectFile.getParentFile().toPath();
		try (BufferedReader str = new BufferedReader(new FileReader(projectFile));) {
			String line = str.readLine();
			while (line != null) {
				lineno++;
				line = line.trim();
				if (!line.isEmpty() && !line.startsWith("#")) {
					String[] parts = line.split("\\s+", 2);
					if (parts.length != 2) {
						throw new ParseException("Invalid line", projectFile, lineno, 1);
					}
					String type = parts[0];
					File file = path.resolve(parts[1]).toFile();
					if ("source".equals(type)) {
						res.sources.add(file);
					} else if ("header".equals(type)) {
						res.cHeaders.add(file);
					} else if ("info".equals(type)) {
						res.infoFiles.add(file);
					} else {
						throw new ParseException("Invalid type: "+type, projectFile, lineno, 1);
					}
				}
				line = str.readLine();
			}
		}
		return res;
	}
}
