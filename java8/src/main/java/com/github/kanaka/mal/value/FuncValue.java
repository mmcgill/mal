package com.github.kanaka.mal.value;

import java.util.function.Function;

public class FuncValue extends Value {
	private final Function<Value[], Value> f;
	
	public FuncValue(Function<Value[], Value> f) {
		this.f = f;
	}
	
	public Value apply(Value... inputs) {
		return f.apply(inputs);
	}
	
	@Override
	public FuncValue castToFn() {
		return this;
	}

	@Override
	public String toString() {
		return "#function";
	}
}
