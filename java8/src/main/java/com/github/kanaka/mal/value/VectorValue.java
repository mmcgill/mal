package com.github.kanaka.mal.value;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.github.kanaka.mal.Environment;

public class VectorValue extends ValueSequence {
	private final Value[] values;
	
	private VectorValue(Value[] values, boolean copy) {
		if (copy) {
			this.values = Arrays.copyOf(values, values.length);
		} else {
			this.values = values;
		}
	}

	VectorValue(Value... values) {
		this(values, true);
	}
	
	VectorValue(List<Value> values) {
		this(values.toArray(new Value[values.size()]), false);
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
	public Value evalAst(Environment env) {
		Value[] newValues = new Value[values.length];
		for (int i=0; i < values.length; ++i)
			newValues[i] = values[i].eval(env);
		return new VectorValue(newValues, false);
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
