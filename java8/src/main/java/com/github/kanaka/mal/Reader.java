package com.github.kanaka.mal;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.kanaka.mal.value.SymbolValue;
import com.github.kanaka.mal.value.Value;

import static com.github.kanaka.mal.value.Value.*;

public class Reader {
	private static final Pattern tokenRegex = Pattern.compile("[\\s,]*(~@|[\\[\\]{}()'`~^@]|\"(?:\\\\.|[^\\\\\"])*\"|;.*|[^\\s\\[\\]{}('\"`,;)]*)");

	public static Queue<String> tokenize(String input) {
		Matcher matcher = tokenRegex.matcher(input);
		Queue<String> tokens = new ArrayDeque<>();
		while (matcher.find()) {
			String s = matcher.group(1);
			if (s.equals(""))
				continue;
			tokens.add(matcher.group(1));
		}
		return tokens;
	}

	private final Queue<String> tokens;
	
	public Reader(String input) {
		tokens = tokenize(input);
	}
	
	private String pollOrError(String errorIfNone) {
		String tok = tokens.poll();
		if (tok == null) {
			throw new SyntaxException(errorIfNone);
		}
		return tok;
	}
	
	private static final Map<String,SymbolValue> READER_MACROS = new HashMap<>();
	{
		READER_MACROS.put("'", symbol("quote"));
		READER_MACROS.put("`", symbol("quasiquote"));
		READER_MACROS.put("~", symbol("unquote"));
		READER_MACROS.put("~@", symbol("splice-unquote"));
		READER_MACROS.put("@", symbol("deref"));
	}
	
	public Value readForm() {
		String tok = tokens.peek();
		if (tok == null) {
			return null;
		} else if (tok.startsWith("(")) {
			return readList();
		} else if (tok.startsWith("[")) {
			return readVector();
		} else if (READER_MACROS.containsKey(tok)) {
			tokens.poll();
			return list(READER_MACROS.get(tok), readForm());
		} else if (tok.equals("^")) {
			tokens.poll();
			Value m = readForm();
			Value v = readForm();
			return list(symbol("with-meta"), v, m);
		} else {
			return readAtom();
		}
	}
	
	private Value readList() {
		tokens.poll();
		Value result = list(new Iterator<Value>() {
			@Override public Value next() {
				return readForm();
			}
			@Override public boolean hasNext() {
				return !tokens.isEmpty() && !tokens.peek().equals(")");
			}
		});
		pollOrError("EOF: Missing closing ')'");
		return result;
	}
	
	private Value readVector() {
		tokens.poll();
		LinkedList<Value> items = new LinkedList<>();
		while (!tokens.isEmpty() && !"]".equals(tokens.peek())) {
			items.add(readForm());
		}
		try {
			return vector(items);
		} finally {
			pollOrError("EOF: Missing closing ']'");
		}
	}
	
	private Value readAtom() {
		String tok = tokens.poll();
		char ch = tok.charAt(0);
		if ((ch >= '0' && ch <= '9')
				|| 
			(ch == '-' && tok.length() > 1 && tok.charAt(1) >= '0' && tok.charAt(1) <= '9')) {
			return integer(Integer.parseInt(tok));
		} else if (ch == ':') {
			return keyword(tok.substring(1));
		} else if (ch == '"') {
			return string(unescape(tok.substring(1, tok.length()-1)));
		} else if (tok.equals("true")) {
			return Value.TRUE;
		} else if (tok.equals("false")) {
			return Value.FALSE;
		} else if (tok.equals("nil")) {
			return Value.NIL;
		} else {
			return symbol(tok);
		}
	}
}
