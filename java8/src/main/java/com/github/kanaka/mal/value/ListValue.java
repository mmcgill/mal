package com.github.kanaka.mal.value;

import java.util.Iterator;
import java.util.stream.Collectors;

import com.github.kanaka.mal.Environment;
import com.github.kanaka.mal.Special;

public class ListValue extends ValueSequence implements MetaHolder<ListValue> {
	public static final ListValue EMPTY = new ListValue(null, null, Value.NIL);

	private final Value head;
	private final ListValue tail;
	private final int size;
	
	public Value getHead() {
		return head;
	}
	
	public ListValue getTail() {
		return tail;
	}
	
	@Override
	public ListValue coerceToList() {
		return this;
	}
	
	private ListValue(Value head, ListValue tail, Value meta) {
		super(meta);
		this.head = head;
		this.tail = tail;
		if (head == null) {
			this.size = 0;
		} else {
			this.size = 1+tail.size;
		}
	}
	
	@Override
	public ListValue withMeta(Value meta) {
		return new ListValue(head, tail, meta);
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
	public Iterator<Value> reverseIterator() {
		return reverse().iterator();
	}
	
	@Override
	public ListValue castToList() {
		return this;
	}
	
	@Override
	public MetaHolder<ListValue> castToMetaHolder() {
		return this;
	}
	
	@Override
	public ValueSequence conj(Value v) {
		return cons(v);
	}
	
	public ListValue cons(Value v) {
		return new ListValue(v, this, meta());
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
	public boolean isMacroCall(Environment env) {
		if (!(head instanceof SymbolValue))
			return false;
		env = env.find(head.castToSymbol());
		if (env == null)
			return false;
		Value hv = env.get(head.castToSymbol());
		if (!(hv instanceof FuncValue))
			return false;
		return hv.castToFn().isMacro;
	}
	
	@Override
	protected EvalResult internalEval(Environment env) {
		if (head == null) {
			return EvalResult.done(this);
		} else  {
			Value v = Special.macroexpand(env, this);
			if (!(v instanceof ListValue))
				return EvalResult.done(v.evalAst(env));
			ListValue ast = v.castToList();
			Special special = (ast.getHead() instanceof SymbolValue) ? Special.get((SymbolValue)ast.getHead()) : null;
			if (special != null) {
				return special.apply(env, ast.getTail().toArray());
			} else {
				ListValue l = ast.evalAst(env);
				FuncValue f = l.head.castToFn();
				return f.apply(l.tail.toArray());
			}
		}
	}
	
	@Override
	public ListValue evalAst(Environment env) {
		return Value.list(stream().map(v -> v.eval(env)).iterator()).withMeta(meta());
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
