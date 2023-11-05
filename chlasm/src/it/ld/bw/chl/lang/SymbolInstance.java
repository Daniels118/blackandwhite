package it.ld.bw.chl.lang;

import java.util.LinkedList;
import java.util.List;

class SymbolInstance {
	public static final SymbolInstance EOF = new SymbolInstance(Syntax.EOF, null);
	
	public Symbol symbol;
	public final Token token;
	public final List<SymbolInstance> expression;
	
	public SymbolInstance(Symbol symbol) {
		this.symbol = symbol;
		this.token = null;
		this.expression = new LinkedList<>();
	}
	
	public SymbolInstance(Symbol symbol, Token token) {
		this.symbol = symbol;
		this.token = token;
		this.expression = null;
	}
	
	public boolean is(TokenType type) {
		return token != null && token.type == type;
	}
	
	public boolean is(String keyword) {
		return token != null && token.type == TokenType.KEYWORD && keyword.equals(token.value);
	}
	
	@Override
	public String toString() {
		if (this == EOF) {
			return "EOF";
		} else if (token != null) {
			return token.type == TokenType.EOL ? "EOL" : token.value;
		} else if (expression.isEmpty()) {
			return "<not initialized>";
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
			return "<not initialized>";
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