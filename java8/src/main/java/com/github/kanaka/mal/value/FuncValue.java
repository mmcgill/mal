package com.github.kanaka.mal.value;

import java.util.function.Function;

public class FuncValue extends Value {
	private final Function<Value[], EvalResult> f;
	
	public FuncValue(Function<Value[], EvalResult> f) {
		this.f = f;
	}
	
	public EvalResult apply(Value... inputs) {
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
