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
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import it.ld.bw.chl.lang.Symbol.TerminalType;

/**Syntax file rules:
 * 
 * # comment
 * 
 * SYMBOL: EXPRESSION
 * 
 * SYMBOL: {
 * 		ALTERNATIVE 1
 * 		ALTERNATIVE 2
 * 		...
 * 		ALTERNATIVE N
 * }
 * 
 * 
 * Modifiers:
 * 	[optional expression]
 * 	{repeatable expression}
 * 
 * Literal brackets and \ must be escaped with \
 * 
 */
public class Syntax {
	private static final String SYNTAX_FILE = "syntax.txt";
	
	//Special symbols defined by the parser
	public static final Symbol EOF;
	public static final Symbol EOL;
	public static final Symbol IDENTIFIER;
	public static final Symbol NUMBER;
	public static final Symbol STRING;
	
	/**Map of all symbols indexed by name. The name of implicit symbols is equivalent to their expression.*/
	private static final Map<String, Symbol> symbols = new LinkedHashMap<>();
	
	private static final Set<String> keywords = new HashSet<>();
	
	static {
		EOF = addSymbol("EOF", TerminalType.EOF, true, true);
		EOL = addSymbol("EOL", TerminalType.EOL, true, true);
		IDENTIFIER = addSymbol("IDENTIFIER", TerminalType.IDENTIFIER, true, true);
		NUMBER = addSymbol("NUMBER", TerminalType.NUMBER, true, true);
		STRING = addSymbol("STRING", TerminalType.STRING, true, true);
		load();
	}
	
	public static Symbol getSymbol(String keyword) {
		return symbols.get(keyword);
	}
	
	public static void printSymbols() {
		for (Symbol symbol : symbols.values()) {
			if (symbol.root) {
				System.out.println(symbol);
				System.out.println();
			}
		}
	}
	
	public static void printKeywords() {
		for (String s : keywords) {
			System.out.println(s);
		}
	}
	
	public static void printTree() {
		Map<Symbol, Node> tree = new LinkedHashMap<>();
		for (Symbol sym : symbols.values()) {
			if (sym.alternatives != null) {
				for (Symbol alt : sym.alternatives) {
					Node node = tree.get(alt);
					if (node == null) {
						node = new Node(null, alt);
						tree.put(alt, node);
					}
					node.targets.add(sym);
				}
			} else if (sym.expression != null) {
				Symbol t = sym.expression[0];
				Node node = tree.get(t);
				if (node == null) {
					node = new Node(null, t);
					tree.put(t, node);
				}
				for (int i = 1; i < sym.expression.length; i++) {
					t = sym.expression[i];
					Node next = node.branches.get(t);
					if (next == null) {
						next = new Node(node, t);
						node.branches.put(t, next);
						node.hasNonTerminal |= !t.terminal;
					}
					node = next;
				}
				node.targets.add(sym);
				/*if (node.targets.size() > 1) {
					System.err.println("WARNING: path \"" + node.getStringPath() + "\" leads to more than one symbol: " + node.targets);
				}*/
			}
		}
		for (Node node : tree.values()) {
			System.out.println(node.toString());
			System.out.println();
		}
	}
	
	public static boolean isKeyword(String s) {
		return keywords.contains(s);
	}
	
	private static Symbol addSymbol(String keyword, TerminalType terminalType, boolean implicit, boolean root) {
		Symbol symbol = new Symbol(keyword, terminalType, implicit, root);
		symbols.put(keyword, symbol);
		return symbol;
	}
	
	private static Symbol getOrCreateSymbol(String keyword) {
		Symbol symbol = symbols.get(keyword);
		if (symbol != null) return symbol;
		if (keyword.equals(keyword.toLowerCase())) {
			return addSymbol(keyword, TerminalType.KEYWORD, true, false);
		} else {
			return addSymbol(keyword, null, true, false);
		}
	}
	
	private static void load() {
		int lineno = 0;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(Syntax.class.getResourceAsStream(SYNTAX_FILE)));) {
			boolean inBlock = false;
			Symbol symbol = null;
			List<Symbol> alternatives = new LinkedList<>();
			String line = "";
			while ((line = reader.readLine()) != null) {
				lineno++;
				line = line.trim();
				if (line.isEmpty() || line.startsWith("#")) continue;
				if (inBlock) {
					if (line.isEmpty()) continue;
					if ("}".equals(line)) {
						inBlock = false;
						symbol.alternatives = alternatives.toArray(new Symbol[0]);
						symbol.expression = null;
						symbol = null;
						alternatives.clear();
					} else {
						alternatives.add(parseExpression(line, line));
					}
				} else {
					String[] tks = line.split(":", -1);
					if (tks.length < 2) throw new Exception("Bad syntax");
					String keyword = tks[0].trim();
					String expression = tks[1].trim();
					if ("{".equals(expression)) {
						symbol = symbols.get(keyword);
						if (symbol == null) {
							symbol = addSymbol(keyword, null, false, true);
						} else {
							symbol.terminal = false;
							symbol.implicit = false;
							symbol.root = true;
						}
						inBlock = true;
					} else {
						//Concatenate lines until an empty line is found
						while ((line = reader.readLine()) != null) {
							lineno++;
							line = line.trim();
							if (line.isEmpty()) break;
							expression += " " + line;
						}
						//
						symbol = parseExpression(keyword, expression);
						symbol.terminal = false;
						symbol.implicit = false;
						symbol.root = true;
						//
						if (symbol.expression.length == 1 && symbol.expression[0].optional) {
							//System.err.println("NOTICE: symbol "+symbol.keyword+" has been forced as optional");
							symbol.optional = true;
						}
						symbol = null;
					}
				}
			}
			//Build keyword list and pattern tree
			for (Symbol sym : symbols.values()) {
				if (!sym.optional && sym.alternatives == null && sym.expression == null
						&& sym.keyword.equals(sym.keyword.toLowerCase())) {
					sym.terminal = true;
					sym.terminalType = TerminalType.KEYWORD;
				}
				if (sym.terminal) {
					if (sym.terminalType == TerminalType.KEYWORD) {
						keywords.add(sym.keyword);
					}
				} else if (sym.expression == null && sym.alternatives == null) {
					throw new RuntimeException("Non-terminal symbol \""+sym.keyword+"\" hasn't been defined");
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage() + " at line " + lineno, e);
		}
	}
	
	private static Symbol parseExpression(String keyword, String expr) {
		String[] tokens = splitExpression(expr);
		return parseExpression(keyword, tokens, 0, tokens.length);
	}
	
	private static Symbol parseExpression(String keyword, String[] tokens, int start, int end) {
		boolean implicit = keyword == null;
		if (implicit) keyword = join(" ", tokens, start, end);
		Symbol symbol = symbols.get(keyword);
		if (symbol == null) {
			symbol = addSymbol(keyword, null, implicit, false);
		}
		List<Symbol> sequence = new LinkedList<>();
		int depth = 0;
		int nestingStart = -1;
		boolean escape = false;
		for (int i = start; i < end; i++) {
			String token = tokens[i];
			if (escape) {
				if (depth == 0) {
					sequence.add(getOrCreateSymbol(token));
				}
				escape = false;
			} else {
				if ("[".equals(token)) {
					if (depth == 0) nestingStart = i + 1;
					depth++;
				} else if ("]".equals(token)) {
					depth--;
					if (depth == 0) {
						String subKeyword = "[" + join(" ", tokens, nestingStart, i) + "]";
						Symbol subSymbol = addSymbol(subKeyword, null, true, false);
						subSymbol.expression = new Symbol[1];
						subSymbol.expression[0] = parseExpression(null, tokens, nestingStart, i);
						subSymbol.optional = true;
						sequence.add(subSymbol);
					}
				} else if ("{".equals(token)) {
					if (depth == 0) nestingStart = i + 1;
					depth++;
				} else if ("}".equals(token)) {
					depth--;
					if (depth == 0) {
						String subKeyword = "{" + join(" ", tokens, nestingStart, i) + "}";
						Symbol subSymbol = addSymbol(subKeyword, null, true, false);
						subSymbol.expression = new Symbol[1];
						subSymbol.expression[0] = parseExpression(null, tokens, nestingStart, i);
						subSymbol.optional = true;
						subSymbol.repeat = true;
						sequence.add(subSymbol);
					}
				} else if (depth == 0) {
					if ("\\".equals(token)) {
						escape = true;
					} else {
						if (i < end - 1 && "|".equals(tokens[i + 1])) {
							int lastAlt = i;
							List<Symbol> alternatives = new LinkedList<>();
							for (int j = i + 1; j <= end; j += 2) {
								lastAlt = j - 1;
								alternatives.add(getOrCreateSymbol(tokens[lastAlt]));
								if (j >= end || !"|".equals(tokens[j])) break;
							}
							String subKeyword = join("", tokens, i, lastAlt + 1);
							Symbol subSymbol = symbols.get(subKeyword);
							if (subSymbol == null) {
								subSymbol = addSymbol(subKeyword, null, true, false);
								subSymbol.alternatives = alternatives.toArray(new Symbol[0]);
							}
							sequence.add(subSymbol);
							i = lastAlt;
						} else {
							sequence.add(getOrCreateSymbol(token));
						}
					}
				}
			}
		}
		if (sequence.size() > 1 || sequence.size() == 1 && sequence.get(0) != symbol) {
			symbol.expression = sequence.toArray(new Symbol[0]);
		}
		return symbol;
	}
	
	private static String[] splitExpression(String expr) {
		List<String> tokens = new LinkedList<>();
		String token = "";
		char type = ' ';
		for (int i = 0; i < expr.length(); i++) {
			char c = expr.charAt(i);
			if ('a'<=c && c<='z' || 'A'<=c && c<='Z' || '0'<=c && c<='9' || c=='_') {
				if (type != 'w') {
					if (!token.isEmpty()) {
						tokens.add(token);
						token = "";
					}
					type = 'w';
				}
				token += String.valueOf(c);
			} else if ("()[]{}".indexOf(c) >= 0) {
				if (!token.isEmpty()) {
					tokens.add(token);
					token = "";
				}
				type = ' ';
				tokens.add(String.valueOf(c));
			} else if (" \r\r\n".indexOf(c) < 0) {
				if (type != 's') {
					if (!token.isEmpty()) {
						tokens.add(token);
						token = "";
					}
					type = 's';
				}
				token += String.valueOf(c);
			} else {
				type = ' ';
				if (!token.isEmpty()) {
					tokens.add(token);
					token = "";
				}
			}
		}
		if (!token.isEmpty()) tokens.add(token);
		return tokens.toArray(new String[0]);
	}
	
	private static String join(String delimiter, String[] tokens, int start, int end) {
		if (start >= end) return "";
		String r = tokens[start];
		for (int i = start + 1; i < end; i++) {
			r += delimiter + tokens[i];
		}
		return r;
	}
	
	
	public static class Node {
		public final Node previous;
		public final Symbol symbol;
		public final Set<Symbol> targets = new HashSet<>();
		public final LinkedHashMap<Symbol, Node> branches = new LinkedHashMap<>();
		public boolean hasNonTerminal;
		public boolean end;
		
		Node(Node previous, Symbol symbol) {
			this.previous = previous;
			this.symbol = symbol;
		}
		
		@Override
		public String toString() {
			return symbol.keyword + toString("\t");
		}
		
		private String toString(String pfx) {
			String r = "";
			if (!targets.isEmpty()) {
				if (targets.size() == 1) {
					r = " -> " + targets.iterator().next().keyword;
				} else {
					for (Symbol target : targets) {
						r += "\r\n" + pfx + "\t\t-> " + target.keyword;
					}
				}
			}
			if (branches != null) {
				for (Entry<Symbol, Node> e : branches.entrySet()) {
					Symbol symbol = e.getKey();
					Node node = e.getValue();
					String sSym = symbol.keyword;
					if (symbol.repeat) {
						sSym = "{" + sSym + "}";
					} else if (symbol.optional) {
						sSym = "[" + sSym + "]";
					}
					r += "\r\n" + pfx + symbol.keyword + node.toString(pfx + "\t");
				}
			}
			return r;
		}
		
		public List<Symbol> getPath() {
			List<Symbol> path = new LinkedList<>();
			Node node = this;
			path.add(node.symbol);
			while (node.previous != null) {
				node = node.previous;
				path.add(0, node.symbol);
			}
			return path;
		}
		
		public String getStringPath() {
			return pathToString(getPath());
		}
		
		public static String pathToString(List<Symbol> path) {
			Iterator<Symbol> it = path.iterator();
			Symbol symbol = it.next();
			String r = symbol.keyword;
			while (it.hasNext()) {
				symbol = it.next();
				r += " " + symbol.keyword;
			}
			return r;
		}
	}
}
