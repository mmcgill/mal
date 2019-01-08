package com.github.kanaka.mal.value;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.kanaka.mal.MalException;

public class MapValue extends Value {
	private final Map<Value,Value> values = new HashMap<>();
	
	MapValue(Value... pairs) {
		if (pairs.length % 2 == 1) {
			throw new MalException("hash-map must have even number of elements");
		}
		for (int i=0; i < pairs.length-1; ++i) {
			values.put(pairs[i], pairs[i+1]);
		}
	}
	
	MapValue(Iterator<Value> iter) {
		while (iter.hasNext()) {
			Value k = iter.next();
			if (!iter.hasNext()) {
				throw new MalException("hash-map must have even number of elements");
			}
			values.put(k, iter.next());
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
		MapValue other = (MapValue) obj;
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
		sb.append('{');
		sb.append(
				values.entrySet().stream()
				.flatMap((e) -> Stream.of(e.getKey(), e.getValue()))
				.map(Value::toString)
				.collect(Collectors.joining(" ")));
		sb.append('}');
		return sb.toString();
	}
}
