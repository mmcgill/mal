package com.github.kanaka.mal.value;

public class NilValue extends Value {
	public static final NilValue NIL = new NilValue();
	private NilValue() {
	}
	@Override
	public String toString() {
		return "nil";
	}
}
