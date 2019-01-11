package com.github.kanaka.mal.value;

import java.util.function.Function;

import com.github.kanaka.mal.MalException;

public class FuncValue extends Value implements MetaHolder<FuncValue> {
	private final Function<Value[], EvalResult> f;
	private final int minArgs;
	private final int maxArgs;
	private final Value meta;
	public boolean isMacro;
	
	FuncValue(Function<Value[], EvalResult> f, int minArgs, int maxArgs) {
		this.minArgs = minArgs;
		this.maxArgs = maxArgs;
		this.f = f;
		this.meta = Value.NIL;
		this.isMacro = false;
	}
	
	FuncValue(FuncValue f, Value meta) {
		this.minArgs = f.minArgs;
		this.maxArgs = f.maxArgs;
		this.f = f.f;
		this.isMacro = f.isMacro;
		this.meta = meta;
	}
	
	@Override
	public Value meta() { return meta; }
	
	@Override
	public FuncValue withMeta(Value meta) { return new FuncValue(this, meta); }
	
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
	public MetaHolder<FuncValue> castToMetaHolder() {
		return this;
	}
	
	@Override
	public String prStr(boolean printReadably) {
		return "#function";
	}
}
