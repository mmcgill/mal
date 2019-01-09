package com.github.kanaka.mal.value;

import java.util.function.Function;

public class AtomValue extends Value {
	private Value value;
	
	AtomValue(Value value) {
		this.value = value;
	}
	
	@Override
	public AtomValue castToAtom() {
		return this;
	}
	
	public synchronized Value get() {
		return value;
	}
	
	public synchronized void set(Value newValue) {
		this.value = newValue;
	}
	
	public synchronized Value swap(Function<Value,Value> f) {
		value = f.apply(value);
		return value;
	}

	@Override
	public String prStr(boolean printReadably) {
		return "(atom "+get().prStr(printReadably)+")";
	}

}
