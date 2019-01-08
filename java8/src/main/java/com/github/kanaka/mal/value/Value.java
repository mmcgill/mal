package com.github.kanaka.mal.value;

import java.util.Iterator;

public abstract class Value {
	public static IntValue integer(int v) {
		return new IntValue(v);
	}
	
	public static SymbolValue symbol(String name) {
		return new SymbolValue(name);
	}
	
	public static ListValue list(Value... values) {
		return new ListValue(new Iterator<Value>() {
			private int i=0;

			@Override public boolean hasNext() {
				return i < values.length;
			}
			@Override public Value next() {
				return values[i++];
			}
		});
	}
	
	public static ListValue list(Iterator<Value> values) {
		return new ListValue(values);
	}
	
	public static final BoolValue TRUE = BoolValue.TRUE;

	public static final BoolValue FALSE = BoolValue.FALSE;

	public static final BoolValue bool(boolean b) {
		return b ? TRUE : FALSE;
	}
	
	public static final NilValue NIL = NilValue.NIL;
	
	public static StringValue string(String s) {
		return new StringValue(s);
	}

	public static String unescape(String v) {
		StringBuilder sb = new StringBuilder(v.length());
		for (int i=0; i < v.length(); ++i) {
			char ch = v.charAt(i);
			if (ch == '\\' && i < v.length()-1) {
				switch (v.charAt(++i)) {
				case 'n': sb.append('\n'); break;
				case '\\': sb.append('\\'); break;
				case '"': sb.append('"'); break;
				}
			} else {
				sb.append(ch);
			}
		}
		return sb.toString();
	}

	public static String escape(String v) {
		StringBuilder sb = new StringBuilder(v.length());
		for (int i=0; i < v.length(); ++i) {
			char ch = v.charAt(i);
			switch (ch) {
			case '\\': sb.append("\\\\"); break;
			case '\n': sb.append("\\n"); break;
			case '"': sb.append("\\\""); break;
			default: sb.append(ch);
			}
		}
		return sb.toString();
	}
	
	public static KeywordValue keyword(String name) {
		return new KeywordValue(name);
	}
}
