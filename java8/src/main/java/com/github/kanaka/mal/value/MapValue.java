package com.github.kanaka.mal.value;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.kanaka.mal.Environment;
import com.github.kanaka.mal.MalException;

public class MapValue extends Value implements MetaHolder<MapValue> {
	private final Map<Value,Value> values;
	private final Value meta;
	
	private MapValue(Map<Value,Value> values, Value meta) {
		this.values = values;
		this.meta = meta;
	}
	
	MapValue(Value... pairs) {
		values = new HashMap<>();
		meta = Value.NIL;
		if (pairs.length % 2 == 1) {
			throw new MalException("hash-map must have even number of elements");
		}
		for (int i=0; i < pairs.length-1; i += 2) {
			values.put(pairs[i], pairs[i+1]);
		}
	}
	
	MapValue(Iterator<Value> iter) {
		values = new HashMap<>();
		meta = Value.NIL;
		while (iter.hasNext()) {
			Value k = iter.next();
			if (!iter.hasNext()) {
				throw new MalException("hash-map must have even number of elements");
			}
			values.put(k, iter.next());
		}
	}
	
	public MapValue assoc(Value... vs) { return assoc(vs, 0); }
	
	public MapValue assoc(Value[] vs, int offset) {
		if ((vs.length-offset)%2 == 1)
			throw new MalException("wrong number of arguments to assoc");
		Map<Value,Value> newMap = new HashMap<>(values);
		for (int i=offset; i < vs.length; i += 2) {
			newMap.put(vs[i], vs[i+1]);
		}
		return new MapValue(newMap, meta);
	}
	
	public MapValue dissoc(Value... ks) { return dissoc(ks, 0); }
	
	public MapValue dissoc(Value[] ks, int offset) {
		Map<Value,Value> newMap = null;
		for (int i=offset; i < ks.length; ++i) {
			if (values.containsKey(ks[i])) {
				if (newMap == null)
					newMap = new HashMap<>(values);
				newMap.remove(ks[i]);
			}
		}
		return newMap == null ? this : new MapValue(newMap, meta);
	}
	
	public Value get(Value k) { return values.getOrDefault(k, Value.NIL); }
	
	public boolean contains(Value k) { return values.containsKey(k); }
	
	public Set<Value> keys() { return values.keySet(); }
	
	public Collection<Value> vals() { return values.values(); }
	
	@Override
	public MapValue castToMap() {
		return this;
	}
	
	@Override
	public Value meta() { return meta; }
	
	@Override
	public MapValue withMeta(Value meta) { return new MapValue(values, meta); }
	
	@Override
	public MetaHolder<MapValue> castToMetaHolder() { return this; }

	@Override
	public Value evalAst(Environment env) {
		Map<Value,Value> newValues = new HashMap<>(values.size());
		for (Entry<Value, Value> e : values.entrySet()) {
			newValues.put(e.getKey().eval(env), e.getValue().eval(env));
		}
		return new MapValue(newValues, meta);
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
