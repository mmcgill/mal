package com.github.kanaka.mal.value;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.kanaka.mal.Environment;

public class ListValue extends Value {
	private final List<Value> values; 

	ListValue(Iterator<Value> iter) {
		values = new LinkedList<>();
		while (iter.hasNext()) {
			values.add(iter.next());
		}
	}
	
	private ListValue(LinkedList<Value> values) {
		this.values = values;
	}
	
	@Override
	public Value eval(Environment env) {
		if (values.isEmpty()) {
			return this;
		} else  {
			ListValue l = evalAst(env);
			FuncValue f = l.values.get(0).castToFn();
			Value[] args = l.values.stream().skip(1).toArray((n) -> new Value[n]);
			return f.apply(args);
		}
	}
	
	@Override
	public ListValue evalAst(Environment env) {
		LinkedList<Value> evaledValues = new LinkedList<>();
		for (Value v : values) {
			evaledValues.add(v.eval(env));
		}
		return new ListValue(evaledValues);
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
