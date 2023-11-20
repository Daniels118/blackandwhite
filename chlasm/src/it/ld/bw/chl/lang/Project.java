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
package it.ld.bw.chl.lang;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.ld.bw.chl.exceptions.ParseException;

public class Project {
	private static final Charset ASCII = Charset.forName("windows-1252");
	
	public Map<String, Integer> constants = new HashMap<>();
	
	public Path sourcePath;
	public List<File> sources = new LinkedList<>();
	public List<File> cHeaders = new LinkedList<>();
	public List<File> infoFiles = new LinkedList<>();
	
	public static Project load(File projectFile) throws ParseException, FileNotFoundException, IOException {
		Project project = new Project();
		int lineno = 0;
		Path prjPath = projectFile.getParentFile().toPath();
		project.sourcePath = prjPath;
		Path headersPath = prjPath;
		Path infoPath = prjPath;
		try (BufferedReader str = new BufferedReader(new FileReader(projectFile, ASCII));) {
			String line = str.readLine();
			while (line != null) {
				lineno++;
				line = line.trim();
				if (!line.isEmpty() && !line.startsWith("#") && !line.startsWith("//")) {
					String[] parts = line.split("\\s+", 2);
					if (parts.length != 2) {
						throw new ParseException("Invalid line", projectFile, lineno);
					}
					String type = parts[0];
					String sVal = parts[1];
					if ("define".equals(type)) {
						parts = parts[1].split("\\s+", 2);
						String name = parts[0];
						int val = Integer.parseInt(parts[1]);
						project.constants.put(name, val);
					} else if ("source".equals(type)) {
						File file = project.sourcePath.resolve(sVal).toFile();
						if (!file.exists()) throw new ParseException("File not found: "+sVal, projectFile, lineno);
						project.sources.add(file);
					} else if ("sourcelist".equals(type)) {
						File file = project.sourcePath.resolve(sVal).toFile();
						if (!file.exists()) throw new ParseException("File not found: "+sVal, projectFile, lineno);
						sourcelist(file, project);
					} else if ("header".equals(type)) {
						File file = headersPath.resolve(sVal).toFile();
						if (!file.exists()) throw new ParseException("File not found: "+sVal, projectFile, lineno);
						project.cHeaders.add(file);
					} else if ("info".equals(type)) {
						File file = infoPath.resolve(sVal).toFile();
						if (!file.exists()) throw new ParseException("File not found: "+sVal, projectFile, lineno);
						project.infoFiles.add(file);
					} else if ("source_path".equals(type)) {
						project.sourcePath = prjPath.resolve(sVal);
					} else if ("headers_path".equals(type)) {
						headersPath = prjPath.resolve(sVal);
					} else if ("info_path".equals(type)) {
						infoPath = prjPath.resolve(sVal);
					} else {
						throw new ParseException("Invalid type: "+type, projectFile, lineno);
					}
				}
				line = str.readLine();
			}
		}
		return project;
	}
	
	private static void sourcelist(File file, Project project) throws FileNotFoundException, IOException, ParseException {
		int lineno = 0;
		try (BufferedReader str = new BufferedReader(new FileReader(file, ASCII));) {
			String line = str.readLine();
			while (line != null) {
				lineno++;
				line = line.trim();
				if (!line.isEmpty() && !line.startsWith("//")) {
					File subFile = project.sourcePath.resolve(line).toFile();
					if (!subFile.exists()) {
						throw new ParseException("File not found: "+line, file, lineno);
					}
					if (!subFile.isFile()) {
						throw new ParseException("Invalid file: "+line, file, lineno);
					}
					project.sources.add(subFile);
				}
				line = str.readLine();
			}
		}
	}
}
