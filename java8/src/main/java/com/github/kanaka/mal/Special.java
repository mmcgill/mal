package com.github.kanaka.mal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.github.kanaka.mal.value.SymbolValue;
import com.github.kanaka.mal.value.Value;
import com.github.kanaka.mal.value.ValueSequence;

import static com.github.kanaka.mal.value.Value.*;

public abstract class Special {
	public abstract Value apply(Environment env, Value[] args);
	
	public static final Special def_BANG = new Special() {
		@Override
		public Value apply(Environment env, Value[] args) {
			if (args.length != 2)
				throw new MalException("Wrong number of args: expected 2, got "+args.length);
			Value v = args[1].eval(env);
			env.set(args[0].castToSymbol(), v);
			return v;
		}
	};
	
	public static final Special let_STAR = new Special() {
		@Override
		public Value apply(Environment env, Value[] args) {
			if (args.length != 2)
				throw new MalException("Wrong number of args: expected 2, got "+args.length);
			ValueSequence bindings = args[0].castToValueSequence();
			Value body = args[1];

			Environment newEnv = new Environment(env);
			Iterator<Value> iter = bindings.iterator();
			while (iter.hasNext()) {
				SymbolValue k = iter.next().castToSymbol();
				if (!iter.hasNext()) {
					throw new MalException("Odd number of values in let* binding");
				}
				newEnv.set(k, iter.next().eval(newEnv));
			}
			return body.eval(newEnv);
		}
	};
	
	private static final Map<SymbolValue, Special> SPECIALS = new HashMap<>();

	static {
		SPECIALS.put(symbol("def!"), def_BANG);
		SPECIALS.put(symbol("let*"), let_STAR);
	}
	
	public static Special get(SymbolValue sym) {
		return SPECIALS.get(sym);
	}
}
