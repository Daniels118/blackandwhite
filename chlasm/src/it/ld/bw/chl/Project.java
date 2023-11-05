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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import it.ld.bw.chl.exceptions.ParseException;

public class Project {
	public List<File> sources = new LinkedList<>();
	public List<File> cHeaders = new LinkedList<>();
	public List<File> infoFiles = new LinkedList<>();
	
	public static Project load(File projectFile) throws ParseException, FileNotFoundException, IOException {
		Project res = new Project();
		int lineno = 0;
		Path prjPath = projectFile.getParentFile().toPath();
		Path sourcePath = prjPath;
		Path headersPath = prjPath;
		Path infoPath = prjPath;
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
					String sFile = parts[1];
					if ("source".equals(type)) {
						File file = sourcePath.resolve(sFile).toFile();
						res.sources.add(file);
					} else if ("header".equals(type)) {
						File file = headersPath.resolve(sFile).toFile();
						res.cHeaders.add(file);
					} else if ("info".equals(type)) {
						File file = infoPath.resolve(sFile).toFile();
						res.infoFiles.add(file);
					} else if ("source_path".equals(type)) {
						sourcePath = prjPath.resolve(sFile);
					} else if ("headers_path".equals(type)) {
						headersPath = prjPath.resolve(sFile);
					} else if ("info_path".equals(type)) {
						infoPath = prjPath.resolve(sFile);
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
