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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.util.LinkedList;
import java.util.List;

import it.ld.bw.chl.exceptions.ParseException;

public class CHLLexer {
	private enum Status {
		DEFAULT, IDENTIFIER, NUMBER, STRING, COMMENT, BLOCK_COMMENT, BLANK
	}
	
	private int tabSize = 4;
	
	public int getTabSize() {
		return tabSize;
	}
	
	public void setTabSize(int tabSize) {
		this.tabSize = tabSize;
	}
	
	private void add(List<Token> tokens, Token token) {
		//if (token.type.important) {
			tokens.add(token);
		//}
	}
	
	public List<Token> tokenize(File file) throws FileNotFoundException, IOException, ParseException {
		List<Token> tokens = new LinkedList<>();
		Status status = Status.DEFAULT;
		StringBuffer buffer = new StringBuffer();
		boolean escape = false;
		int depth = 0;
		int numDots = 0;
		int line = 1;
		int col = 0;
		Token token = null;
		try (PushbackReader str = new PushbackReader(new FileReader(file));) {
			char c = (char) str.read();
			col++;
			while (c != 0xFFFF) {
				switch (status) {
					case DEFAULT:
						if (c == '\n') {
							add(tokens, new Token(line, col, TokenType.EOL, System.lineSeparator()));
							line++;
							col = 0;
						} else if (c == '"') {
							status = Status.STRING;
							token = new Token(line, col, TokenType.STRING);
							buffer.append(c);
						} else if (c == '+') {
							char c2 = (char) str.read();
							if (c2 == '+') {
								add(tokens, new Token(line, col, TokenType.KEYWORD, "++"));
								col++;
							} else if (c2 == '=') {
								add(tokens, new Token(line, col, TokenType.KEYWORD, "+="));
								col++;
							} else {
								str.unread(c2);
								add(tokens, new Token(line, col, TokenType.KEYWORD, c));
							}
						} else if (c == '-') {
							char c2 = (char) str.read();
							if (c2 == '-') {
								add(tokens, new Token(line, col, TokenType.KEYWORD, "--"));
								col++;
							} else if (c2 == '=') {
								add(tokens, new Token(line, col, TokenType.KEYWORD, "-="));
								col++;
							} else {
								str.unread(c2);
								add(tokens, new Token(line, col, TokenType.KEYWORD, c));
							}
						} else if (c == '*') {
							char c2 = (char) str.read();
							if (c2 == '=') {
								add(tokens, new Token(line, col, TokenType.KEYWORD, "*="));
								col++;
							} else {
								str.unread(c2);
								add(tokens, new Token(line, col, TokenType.KEYWORD, c));
							}
						} else if (c == '/') {
							char c2 = (char) str.read();
							if (c2 == '=') {
								add(tokens, new Token(line, col, TokenType.KEYWORD, "/="));
								col++;
							} else if (c2 == '/') {
								col++;
								status = Status.COMMENT;
								token = new Token(line, col, TokenType.COMMENT);
								buffer.append("//");
							} else if (c == '*') {
								status = Status.BLOCK_COMMENT;
								token = new Token(line, col, TokenType.BLOCK_COMMENT);
								col++;
								buffer.append("/*");
							} else {
								str.unread(c2);
								add(tokens, new Token(line, col, TokenType.KEYWORD, c));
							}
						} else if (c == '%') {
							char c2 = (char) str.read();
							if (c2 == '=') {
								add(tokens, new Token(line, col, TokenType.KEYWORD, "%="));
								col++;
							} else {
								str.unread(c2);
								add(tokens, new Token(line, col, TokenType.KEYWORD, c));
							}
						} else if (c == '=') {
							char c2 = (char) str.read();
							if (c2 == '=') {
								add(tokens, new Token(line, col, TokenType.KEYWORD, "=="));
								col++;
							} else {
								str.unread(c2);
								add(tokens, new Token(line, col, TokenType.KEYWORD, c));
							}
						} else if (c == '<' || c == '>') {
							char c2 = (char) str.read();
							if (c2 == '=') {
								add(tokens, new Token(line, col, TokenType.KEYWORD, c+"="));
								col++;
							} else {
								str.unread(c2);
								add(tokens, new Token(line, col, TokenType.KEYWORD, c));
							}
						} else if (c == '!') {
							char c2 = (char) str.read();
							if (c2 == '=') {
								add(tokens, new Token(line, col, TokenType.KEYWORD, c+"="));
								col++;
							} else {
								throw new ParseException("Expected '=' after '!'", file, line, col);
							}
						} else if (c == ',') {
							add(tokens, new Token(line, col, TokenType.KEYWORD, c));
							buffer.setLength(0);
						} else if (c == '(') {
							add(tokens, new Token(line, col, TokenType.KEYWORD, c));
							buffer.setLength(0);
						} else if (c == ')') {
							add(tokens, new Token(line, col, TokenType.KEYWORD, c));
							buffer.setLength(0);
						} else if (c == '[') {
							add(tokens, new Token(line, col, TokenType.KEYWORD, c));
							buffer.setLength(0);
						} else if (c == ']') {
							add(tokens, new Token(line, col, TokenType.KEYWORD, c));
							buffer.setLength(0);
						} else if (Character.isJavaIdentifierStart(c)) {
							status = Status.IDENTIFIER;
							token = new Token(line, col, TokenType.IDENTIFIER);
							buffer.append(c);
						} else if (Character.isDigit(c)) {
							status = Status.NUMBER;
							token = new Token(line, col, TokenType.NUMBER);
							numDots = 0;
							buffer.append(c);
						} else if (c == ' ') {
							status = Status.BLANK;
							token = new Token(line, col, TokenType.BLANK);
							buffer.append(c);
						} else if (c == '\t') {
							status = Status.BLANK;
							token = new Token(line, col, TokenType.BLANK);
							buffer.append(c);
							col = (int)Math.ceil((double)col / tabSize) * tabSize;
						} else if (c == '\r') {
							//NOP
						} else {
							throw new ParseException("Unexpected "+((int)c)+" character", file, line, col);
						}
						break;
					case COMMENT:
						if (c == '\n') {
							add(tokens, token.setValue(buffer.toString()));
							buffer.setLength(0);
							status = Status.DEFAULT;
							add(tokens, new Token(line, col, TokenType.EOL, System.lineSeparator()));
							line++;
							col = 0;
						} else {
							buffer.append(c);
						}
						break;
					case BLOCK_COMMENT:
						if (c == '\n') {
							line++;
							col = 0;
							buffer.append(c);
						} else if (c == '/') {
							buffer.append(c);
							char c2 = (char) str.read();
							col++;
							buffer.append(c);
							if (c2 == '*') depth++;
						} else if (c == '*') {
							buffer.append(c);
							char c2 = (char) str.read();
							col++;
							buffer.append(c);
							if (c2 == '/') {
								depth--;
								if (depth == 0) {
									add(tokens, token.setValue(buffer.toString()));
									buffer.setLength(0);
									status = Status.DEFAULT;
								}
							}
						} else {
							buffer.append(c);
						}
						break;
					case IDENTIFIER:
						if (Character.isJavaIdentifierPart(c)) {
							buffer.append(c);
						} else {
							str.unread(c);
							col--;
							add(tokens, token.setValue(buffer.toString()));
							if (Syntax.isKeyword(token.value)) token.type = TokenType.KEYWORD;
							buffer.setLength(0);
							status = Status.DEFAULT;
						}
						break;
					case NUMBER:
						if (Character.isDigit(c) || c == '.') {
							if (c == '.') {
								numDots++;
								if (numDots > 1) throw new ParseException("Invalid number", file, line, col);
							}
							buffer.append(c);
						} else if (Character.isJavaIdentifierPart(c)) {	//This is required to handle keywords starting with numbers such as "3d"
							token.type = TokenType.IDENTIFIER;
							buffer.append(c);
						} else {
							str.unread(c);
							col--;
							add(tokens, token.setValue(buffer.toString()));
							buffer.setLength(0);
							status = Status.DEFAULT;
						}
						break;
					case STRING:
						if (escape) {
							buffer.append(c);
							escape = false;
						} else if (c == '\\') {
							escape = true;
							buffer.append(c);
						} else if (c == '"') {
							buffer.append(c);
							add(tokens, token.setValue(buffer.toString()));
							buffer.setLength(0);
							status = Status.DEFAULT;
						} else {
							buffer.append(c);
						}
						break;
					case BLANK:
						if (c == ' ' || c == '\t') {
							buffer.append(c);
						} else {
							str.unread(c);
							col--;
							add(tokens, token.setValue(buffer.toString()));
							buffer.setLength(0);
							status = Status.DEFAULT;
						}
						break;
				}
				c = (char) str.read();
				col++;
			}
		}
		if (status != Status.DEFAULT) {
			throw new ParseException("Unexpected end of file", file, line, col);
		}
		return tokens;
	}
}
