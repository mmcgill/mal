package com.github.kanaka.mal.value;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.kanaka.mal.Environment;
import com.github.kanaka.mal.MalException;

public class MapValue extends Value {
	private final Map<Value,Value> values;
	
	private MapValue(Map<Value,Value> values) {
		this.values = values;
	}
	
	MapValue(Value... pairs) {
		values = new HashMap<>();
		if (pairs.length % 2 == 1) {
			throw new MalException("hash-map must have even number of elements");
		}
		for (int i=0; i < pairs.length-1; ++i) {
			values.put(pairs[i], pairs[i+1]);
		}
	}
	
	MapValue(Iterator<Value> iter) {
		values = new HashMap<>();
		while (iter.hasNext()) {
			Value k = iter.next();
			if (!iter.hasNext()) {
				throw new MalException("hash-map must have even number of elements");
			}
			values.put(k, iter.next());
		}
	}
	
	@Override
	public Value evalAst(Environment env) {
		Map<Value,Value> newValues = new HashMap<>(values.size());
		for (Entry<Value, Value> e : values.entrySet()) {
			newValues.put(e.getKey().eval(env), e.getValue().eval(env));
		}
		return new MapValue(newValues);
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
	public String prStr(boolean printReadably) {
		StringBuilder sb = new StringBuilder();
		sb.append('{');
		sb.append(
				values.entrySet().stream()
				.flatMap((e) -> Stream.of(e.getKey(), e.getValue()))
				.map((v) -> v.prStr(printReadably))
				.collect(Collectors.joining(" ")));
		sb.append('}');
		return sb.toString();
	}
}
