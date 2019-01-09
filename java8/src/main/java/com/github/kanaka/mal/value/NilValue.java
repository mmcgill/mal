package com.github.kanaka.mal.value;

import java.util.Iterator;

public class NilValue extends ValueSequence {
	public static final NilValue NIL = new NilValue();

	private NilValue() {
	}
	
	@Override
	public String prStr(boolean printReadably) {
		return "nil";
	}

	@Override
	public ValueSequence castToValueSequence() {
		return this;
	}
	
	@Override
	public int getSize() {
		return 0;
	}
	
	@Override
	public Iterator<Value> iterator() {
		return new Iterator<Value>() {
			@Override public boolean hasNext() { return false; }
			@Override public Value next() { return null; }
		};
	}
}
