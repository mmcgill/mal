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
}
