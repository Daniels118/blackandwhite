/* Copyright (c) 2022-2023 Daniele Lombardi / Daniels118
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
package it.ld.utils;

import java.io.Console;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class CmdLine {
	public static boolean DEBUG_PROGRESS_BAR = false;
	
	private static Console console = System.console();
	private static Scanner scanner = null;
	private static Timer spinnerTimer = null;
	private static SpinnerTask spinnerTask = null;
	
	protected String args[];
	
	public CmdLine(String args[]) {
		this.args = args;
	}
	
	public static Scanner getInputScanner() {
		if (scanner == null) scanner = new Scanner(System.in);
		return scanner;
	}
	
	public File getArgFile(String name) {
		return getArgFile(name, null);
	}
	
	public File getArgFile(String name, String def) {
		String v = getArgVal(name, null);
		if (v == null) return def == null ? null : new File(def);
		return new File(v);
	}
	
	public List<File> getArgFiles(String name) {
		List<String> vals = getArgVals(name);
		List<File> res = new ArrayList<File>(vals.size());
		for (String v : vals) {
			res.add(new File(v));
		}
		return res;
	}
	
	public Integer getArgInt(String name) {
		return getArgInt(name, null);
	}
	
	public Integer getArgInt(String name, Integer def) {
		String v = getArgVal(name, null);
		if (v == null) return def;
		return Integer.valueOf(v);
	}
	
	public String reqArgVal(String name) throws MissingArgumentException {
		String r = getArgVal(name, null);
		if (r == null) throw new MissingArgumentException(name);
		return r;
	}
	
	/**
	 * Restituisce il valore del parametro indicato. Equivale a getArgVal(name, null).
	 */
	public String getArgVal(String name) {
		return getArgVal(name, null);
	}
	
	/**
	 * Restituisce il valore del parametro indicato. Equivale a getArgVal(name, def, 1).
	 */
	public String getArgVal(String name, String def) {
		return getArgVal(name, def, 1);
	}
	
	/**
	 * Restituisce il valore del parametro indicato. Equivale a getArgVal(name, null, occurrence).
	 */
	public String getArgVal(String name, int occurrence) {
		return getArgVal(name, null, occurrence);
	}
	
	/**
	 * Restituisce i valore del parametro indicato.
	 */
	public List<String> getArgVals(String name) {
		List<String> res = new LinkedList<String>();
		String prefix = name.substring(0, 1);
		boolean addUnknown = false;
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals(name)) {
				if (i + 1 < args.length) res.add(args[++i]);
				addUnknown = true;
			} else if (addUnknown) {
				if (args[i].startsWith(prefix)) {
					addUnknown = false;
				} else {
					res.add(args[i]);
				}
			}
		}
		return res;
	}
	
	/**
	 * Restituisce il valore del parametro indicato.
	 * @param name il nomde del parametro cercato
	 * @param def il valore di restituito nel caso il parametro non venga trovato
	 * @param occurrence l'occorrenza del parametro nel caso sia possibile avere parametri duplicati;
	 *                   se il valore � negativo, il conteggio parte dall'ultima occorrenza.
	 */
	public String getArgVal(String name, String def, int occurrence) {
		int i, c = 0;
		if (occurrence > 0) {
			for (i = 0; i < args.length - 1; i++) {
				if (args[i].equals(name)) {
					if (++c == occurrence) return args[i + 1];
				}
			}
		} else {
			for (i = args.length - 2; i >= 0; i--) {
				if (args[i].equals(name)) {
					if (--c == occurrence) return args[i + 1];
				}
			}
		}
		return def;
	}
	
	public boolean getArgFlag(String name) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals(name)) return true;
		}
		return false;
	}
	
	public static String readLine(String msg) {
		System.out.print(msg);
        String r = CmdLine.getInputScanner().next();
        return r;
	}
	
	public static String readPassword(String msg) {
		if (console == null) {
	    	System.out.print(msg);
	    	String pass = CmdLine.getInputScanner().next();
	        return pass;
	    } else {
	    	return String.valueOf(console.readPassword(msg));
	    }
	}
	
	public static void progress(double value, double total, int width) {
		progress(value / total, width);
	}
	
	public static void progress(double value, int width) {
		if (console == null) {
			if (DEBUG_PROGRESS_BAR) System.out.println(value);
			return;
		}
		stopSpinner();
		char[] buf = new char[width + 6];
		int p = (int) (value * width);
		int c = 0;
		buf[c++] = '\r';
		for (int i = 0; i < p; i++) {
			buf[c++] = '\u2588';
		}
		for (int i = p; i < width; i++) {
			buf[c++] = '\u2592';
		}
		char[] pc = String.valueOf((int) (value * 100)).toCharArray();
		for (int i = 0; i < 4 - pc.length; i++) {
			buf[c++] = ' ';
		}
		System.arraycopy(pc, 0, buf, c, pc.length);
		c += pc.length;
		buf[c++] = '%';
		try {
        	System.out.print(buf);
        } catch (Exception e) {}
	}
	
	public static void startSpinner(final double vMin, final double vMax, final int width) {
		if (console == null) return;
		if (spinnerTimer == null) {
			spinnerTimer = new Timer();
			spinnerTask = new SpinnerTask(vMin, vMax, width);
			spinnerTimer.schedule(spinnerTask, 0, 150);
		} else {
			spinnerTask.set(vMin, vMax);
		}
	}
	
	public static void stopSpinner() {
		if (spinnerTimer == null) return;
		spinnerTimer.cancel();
		spinnerTimer = null;
	}
	
	private static void printSpinner(double vMin, double vMax, int width, int offset) {
		if (console == null) return;
		char[] buf = new char[width + 6];
		int p0 = (int) (vMin * width);
		int p1 = (int) (vMax * width);
		int c = 0;
		buf[c++] = '\r';
		for (int i = 0; i < p0; i++) {
			buf[c++] = '\u2588';
		}
		for (int i = p0; i < p1; i++) {
			buf[c++] = ((i - offset) % 3) == 0 ? '\u2591' : '\u2592';
		}
		for (int i = p1; i < width; i++) {
			buf[c++] = '\u2592';
		}
		if (vMin == 0 && vMax == 1) {
			while (c < buf.length) {
				buf[c++] = ' ';
			}
		} else {
			char[] pc = String.valueOf((int) (vMin * 100)).toCharArray();
			for (int i = 0; i < 4 - pc.length; i++) {
				buf[c++] = ' ';
			}
			System.arraycopy(pc, 0, buf, c, pc.length);
			c += pc.length;
			buf[c++] = '%';
		}
		try {
        	System.out.print(buf);
        } catch (Exception e) {}
	}
	
	private static class SpinnerTask extends TimerTask {
		private volatile double vMin;
		private volatile double vMax;
		private int width;
		
		private int offset = 0;
		
		public SpinnerTask(double vMin, double vMax, int width) {
			this.vMin = vMin;
			this.vMax = vMax;
			this.width = width;
		}
		
		public void set(double vMin, double vMax) {
			this.vMin = vMin;
			this.vMax = vMax;
		}
		
		@Override
		public void run() {
			offset++;
			printSpinner(vMin, vMax, width, offset);
		}
	}
	
	public static class MissingArgumentException extends Exception {
		private static final long serialVersionUID = 1L;
		
		private final String arg;
		
		public MissingArgumentException(String arg) {
			super("Required argument "+arg+" missing");
			this.arg = arg;
		}
		
		public String getArgName() {
			return arg;
		}
	}
}
