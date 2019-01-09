package com.github.kanaka.mal.value;

import java.util.Iterator;
import java.util.stream.Collectors;

import com.github.kanaka.mal.Environment;
import com.github.kanaka.mal.Special;

public class ListValue extends ValueSequence {
	public static final ListValue EMPTY = new ListValue(null, null);

	private final Value head;
	private final ListValue tail;
	private final int size;
	
	private ListValue(Value head, ListValue tail) {
		this.head = head;
		this.tail = tail;
		if (head == null) {
			this.size = 0;
		} else {
			this.size = 1+tail.size;
		}
	}
	
	@Override
	public int getSize() {
		return size;
	}
	
	private static class ListIterator implements Iterator<Value> {
		private ListValue v;
		ListIterator(ListValue start) {
			v = start;
		}
		@Override
		public boolean hasNext() {
			return v.head != null;
		}
		@Override
		public Value next() {
			try {
				return v.head;
			} finally {
				v = v.tail;
			}
		}
	}
	
	@Override
	public Iterator<Value> iterator() {
		return new ListIterator(this);
	}
	
	@Override
	public ListValue castToList() {
		return this;
	}
	
	public ListValue cons(Value v) {
		return new ListValue(v, this);
	}
	
	public Value[] toArray() {
		Value[] result = new Value[size];
		int i=0;
		ListValue v = this;
		while (v.head != null) {
			result[i++] = v.head;
			v = v.tail;
		}
		return result;
	}
	
	public ListValue reverse() {
		ListValue result = EMPTY;
		ListValue v = this;
		while (v.head != null) {
			result = result.cons(v.head);
			v = v.tail;
		}
		return result;
	}
	
	@Override
	protected EvalResult internalEval(Environment env) {
		if (head == null) {
			return EvalResult.done(this);
		} else  {
			Special special = (head instanceof SymbolValue) ? Special.get((SymbolValue)head) : null;
			if (special != null) {
				return special.apply(env, tail.toArray());
			} else {
				ListValue l = evalAst(env);
				FuncValue f = l.head.castToFn();
				return f.apply(l.tail.toArray());
			}
		}
	}
	
	@Override
	public ListValue evalAst(Environment env) {
		return Value.list(stream().map(v -> v.eval(env)).iterator());
	}

	@Override
	public String prStr(boolean printReadably) {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(stream().map((v) -> v.prStr(printReadably)).collect(Collectors.joining(" ")));
		sb.append(")");
		return sb.toString();
	}
}
