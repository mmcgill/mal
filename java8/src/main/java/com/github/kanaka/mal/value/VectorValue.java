package com.github.kanaka.mal.value;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.github.kanaka.mal.Environment;

public class VectorValue extends ValueSequence implements MetaHolder<VectorValue> {
	private final Value[] values;
	
	private VectorValue(Value[] values, boolean copy, Value meta) {
		super(meta);
		if (copy) {
			this.values = Arrays.copyOf(values, values.length);
		} else {
			this.values = values;
		}
	}

	VectorValue(Value... values) {
		this(values, true, Value.NIL);
	}
	
	VectorValue(List<Value> values) {
		this(values.toArray(new Value[values.size()]), false, Value.NIL);
	}
	
	@Override
	public VectorValue withMeta(Value meta) {
		return new VectorValue(values, false, meta);
	}
	
	@Override
	public int getSize() {
		return values.length;
	}
	
	@Override
	public Iterator<Value> iterator() {
		return new Iterator<Value>() {
			private int i=0;
			@Override public boolean hasNext() { return i < values.length; }
			@Override public Value next() { return values[i++]; }
		};
	}
	
	@Override
	public Iterator<Value> reverseIterator() {
		return new Iterator<Value>() {
			private int i=values.length-1;
			@Override public boolean hasNext() { return i >= 0; }
			@Override public Value next() { return values[i--]; }
		};
	}
	
	@Override
	public ListValue coerceToList() {
		return list(values);
	}
	
	@Override
	public MetaHolder<VectorValue> castToMetaHolder() {
		return this;
	}
	
	@Override
	public ValueSequence conj(Value v) {
		Value[] vs = Arrays.copyOf(values, values.length+1);
		vs[vs.length-1] = v;
		return new VectorValue(vs);
	}
	
	@Override
	public Value evalAst(Environment env) {
		Value[] newValues = new Value[values.length];
		for (int i=0; i < values.length; ++i)
			newValues[i] = values[i].eval(env);
		return new VectorValue(newValues, false, meta());
	}
	
	@Override
	public String prStr(boolean printReadably) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(Arrays.stream(values)
				.map((v) -> v.prStr(printReadably))
				.collect(Collectors.joining(" ")));
		sb.append("]");
		return sb.toString();
	}
}
