package com.github.kanaka.mal.value;

import java.util.Iterator;

public class NilValue extends ValueSequence {
	public static final NilValue NIL = new NilValue();

	private NilValue() {
		super(null);
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
	public ListValue coerceToList() {
		return ListValue.EMPTY;
	}
	
	@Override
	public ValueSequence conj(Value v) {
		return list(v);
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
	@Override
	public Iterator<Value> reverseIterator() {
		return new Iterator<Value>() {
			@Override public boolean hasNext() { return false; }
			@Override public Value next() { return null; }
		};
	}
}
