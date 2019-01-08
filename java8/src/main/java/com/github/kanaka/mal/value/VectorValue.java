package com.github.kanaka.mal.value;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.github.kanaka.mal.Environment;

public class VectorValue extends Value implements ValueSequence {
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
	public Iterator<Value> iterator() {
		return Arrays.asList(values).iterator();
	}
	
	@Override
	public ValueSequence castToValueSequence() {
		return this;
	}
	
	@Override
	public Value evalAst(Environment env) {
		Value[] newValues = new Value[values.length];
		for (int i=0; i < values.length; ++i)
			newValues[i] = values[i].eval(env);
		return new VectorValue(newValues, false);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(values);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VectorValue other = (VectorValue) obj;
		if (!Arrays.equals(values, other.values))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(Arrays.stream(values).map(Value::toString).collect(Collectors.joining(" ")));
		sb.append("]");
		return sb.toString();
	}
}
