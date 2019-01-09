package com.github.kanaka.mal.value;

import java.util.function.Function;

import com.github.kanaka.mal.MalException;

public class FuncValue extends Value {
	private final Function<Value[], EvalResult> f;
	private final int minArgs;
	private final int maxArgs;
	
	FuncValue(Function<Value[], EvalResult> f, int minArgs, int maxArgs) {
		this.minArgs = minArgs;
		this.maxArgs = maxArgs;
		this.f = f;
	}
	
	public EvalResult apply(Value... inputs) {
		if (inputs.length < minArgs) {
			throw new MalException("Expected at least "+minArgs+" args, got only "+inputs.length);
		}
		if (maxArgs < inputs.length) {
			throw new MalException("Expected at most "+maxArgs+" args, got "+inputs.length);
		}
		return f.apply(inputs);
	}
	
	@Override
	public FuncValue castToFn() {
		return this;
	}
	
	@Override
	public String prStr(boolean printReadably) {
		return "#function";
	}
}
