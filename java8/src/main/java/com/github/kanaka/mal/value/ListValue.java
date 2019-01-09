package com.github.kanaka.mal.value;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.kanaka.mal.Environment;
import com.github.kanaka.mal.Special;

public class ListValue extends ValueSequence {
	private final LinkedList<Value> values;

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
	protected List<Value> readOnlyItems() {
		return Collections.unmodifiableList(values);
	}
	
	@Override
	public ListValue castToList() {
		return this;
	}
	
	@Override
	protected EvalResult internalEval(Environment env) {
		if (values.isEmpty()) {
			return EvalResult.done(this);
		} else  {
			Value first = values.get(0);
			Special special = (first instanceof SymbolValue) ? Special.get((SymbolValue)first) : null;
			if (special != null) {
				return special.apply(env, values.stream().skip(1).toArray((n) -> new Value[n]));
			} else {
				ListValue l = evalAst(env);
				FuncValue f = l.values.get(0).castToFn();
				Value[] args = l.values.stream().skip(1).toArray((n) -> new Value[n]);
				return f.apply(args);
			}
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
	public String prStr(boolean printReadably) {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(values.stream().map((v) -> v.prStr(printReadably)).collect(Collectors.joining(" ")));
		sb.append(")");
		return sb.toString();
	}
}
