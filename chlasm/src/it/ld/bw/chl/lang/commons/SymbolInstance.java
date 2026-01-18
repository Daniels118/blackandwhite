package it.ld.bw.chl.lang.commons;

import java.util.LinkedList;
import java.util.List;

public class SymbolInstance {
	public static final SymbolInstance EOF = new SymbolInstance(Syntax.EOF);
	
	public Symbol symbol;
	public final Token token;
	public final List<SymbolInstance> expression;
	public String typename;
	
	public SymbolInstance(Symbol symbol) {
		this.symbol = symbol;
		this.token = null;
		this.expression = new LinkedList<>();
		this.typename = null;
	}
	
	public SymbolInstance(Symbol symbol, String typename) {
		this.symbol = symbol;
		this.token = null;
		this.expression = new LinkedList<>();
		this.typename = typename;
	}
	
	public SymbolInstance(Symbol symbol, Token token) {
		this.symbol = symbol;
		this.token = token;
		this.expression = null;
		this.typename = null;
	}
	
	public SymbolInstance(Symbol symbol, Token token, String typename) {
		this.symbol = symbol;
		this.token = token;
		this.expression = null;
		this.typename = typename;
	}
	
	public int getLine() {
		if (this.token != null) {
			return this.token.line;
		} else {
			return this.expression.get(0).getLine();
		}
	}
	
	public int getCol() {
		if (this.token != null) {
			return this.token.col;
		} else {
			return this.expression.get(0).getCol();
		}
	}
	
	public boolean is(TokenType type) {
		return token != null && token.type == type;
	}
	
	public boolean is(String keyword) {
		return token != null && (token.type == TokenType.KEYWORD || token.type == TokenType.ANNOTATION) && keyword.equals(token.value);
	}
	
	public boolean isInt() {
		if (token != null && token.type == TokenType.NUMBER) {
			try {
				token.intVal();
				return true;
			} catch (Exception e) {}
		}
		return false;
	}
	
	@Override
	public String toString() {
		if (this == EOF) {
			return "EOF";
		} else if (token != null) {
			return token.type == TokenType.EOL ? "EOL" : token.value;
		} else if (expression.isEmpty()) {
			return "";
		} else {
			String r = expression.get(0).toString();
			for (int i = 1; i < expression.size(); i++) {
				r += " " + expression.get(i).toString();
			}
			return r;
		}
	}
	
	public String toStringBlocks() {
		if (token != null) {
			return token.value;
		} else if (expression.isEmpty()) {
			return "";
		} else if (expression.size() == 1) {
			return expression.get(0).toString();
		} else {
			String r = "{" + expression.get(0).toString() + "}";
			for (int i = 1; i < expression.size(); i++) {
				if (expression.get(i).is(TokenType.KEYWORD)) {
					r += " " + expression.get(i).toString();
				} else {
					r += " {" + expression.get(i).toString() + "}";
				}
			}
			return r;
		}
	}
}