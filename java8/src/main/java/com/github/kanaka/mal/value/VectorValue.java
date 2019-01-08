package com.github.kanaka.mal.value;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class VectorValue extends Value {
	private final Value[] values;

	VectorValue(Value... values) {
		this.values = Arrays.copyOf(values, values.length);
	}
	
	VectorValue(List<Value> values) {
		this.values = values.toArray(new Value[values.size()]);
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
