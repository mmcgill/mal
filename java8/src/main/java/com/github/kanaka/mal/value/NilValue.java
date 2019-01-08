package com.github.kanaka.mal.value;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class NilValue extends ValueSequence {
	public static final NilValue NIL = new NilValue();
	private static List<Value> EMPTY = Collections.unmodifiableList(new LinkedList<>());

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
	protected List<Value> readOnlyItems() {
		return EMPTY;
	}
}
