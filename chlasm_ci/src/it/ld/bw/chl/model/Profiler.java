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
package it.ld.bw.chl.model;

/**Helper class to analyze the performance of the program.
 */
public class Profiler {
	public static final long[] elapsed = new long[13];
	
	private static final long[] start = new long[13];
	
	public static void reset() {
		for (int section = 0; section < elapsed.length; section++) {
			elapsed[section] = 0;
			start[section] = 0;
		}
	}
	
	public static void start() {
		reset();
		start[ProfilerSections.PF_FILE] = System.nanoTime();
	}
	
	public static void start(int section) {
		start[section] = System.nanoTime();
	}
	
	public static void end(int section) {
		elapsed[section] += System.nanoTime() - start[section];
		start[section] = 0;
	}
	
	public static void end() {
		long t = System.nanoTime();
		for (int section = 0; section < elapsed.length; section++) {
			if (start[section] > 0) {
				elapsed[section] += t - start[section];
				start[section] = 0;
			}
		}
	}
	
	public static void printReport() {
		System.out.println("### Profiler report ###");
		println("Total elapsed", elapsed[ProfilerSections.PF_FILE]);
		println("Header", elapsed[ProfilerSections.PF_HEADER]);
		println("Globals", elapsed[ProfilerSections.PF_GLOBALS]);
		println("Code", elapsed[ProfilerSections.PF_CODE]);
		println("Autostart", elapsed[ProfilerSections.PF_AUTOSTART]);
		println("Scripts", elapsed[ProfilerSections.PF_SCRIPTS]);
		println("Data", elapsed[ProfilerSections.PF_DATA]);
		println("Null", elapsed[ProfilerSections.PF_NULL]);
		println("Init", elapsed[ProfilerSections.PF_INIT]);
		println("Instr opcode", elapsed[ProfilerSections.PF_INSTR_OPCODE]);
		println("Instr flags", elapsed[ProfilerSections.PF_INSTR_FLAGS]);
		println("Instr datatype", elapsed[ProfilerSections.PF_INSTR_DATATYPE]);
		println("Instr operand", elapsed[ProfilerSections.PF_INSTR_OPERAND]);
		println("Instr lineno", elapsed[ProfilerSections.PF_INSTR_LINENO]);
		System.out.println();
	}
	
	private static void println(String label, long v) {
		if (v > 0) {
			System.out.printf("%-15s %s\r\n", label+":", format(v));
		}
	}
	
	private static String format(long time) {
		double v = time;
		String unit = "ns";
		if (v > 1000) {
			v /= 1000;
			unit = "us";
			if (v > 1000) {
				v /= 1000;
				unit = "ms";
				if (v > 1000) {
					v /= 1000;
					unit = "s ";
				}
			}
		}
		String sv = String.format("%.1f %s", v, unit);
		String percent = Math.round((double)time / elapsed[ProfilerSections.PF_FILE] * 100) + "%";
		return String.format("%9s %4s", sv, percent);
	}
}
