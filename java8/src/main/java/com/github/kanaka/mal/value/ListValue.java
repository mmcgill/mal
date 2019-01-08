package com.github.kanaka.mal.value;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ListValue extends Value {
	private final List<Value> values = new LinkedList<>();

	public ListValue(Iterator<Value> iter) {
		while (iter.hasNext()) {
			values.add(iter.next());
		}
	}
	
	public ListValue(Value... vs) {
		for (Value v : vs) {
			values.add(v);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((values == null) ? 0 : values.hashCode());
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
		ListValue other = (ListValue) obj;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(values.stream().map(Value::toString).collect(Collectors.joining(" ")));
		sb.append(")");
		return sb.toString();
	}
}
