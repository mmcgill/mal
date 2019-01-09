package com.github.kanaka.mal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.github.kanaka.mal.value.FuncValue;
import com.github.kanaka.mal.value.ListValue;
import com.github.kanaka.mal.value.SymbolValue;
import com.github.kanaka.mal.value.Value;
import com.github.kanaka.mal.value.Value.EvalResult;
import com.github.kanaka.mal.value.ValueSequence;

import static com.github.kanaka.mal.value.Value.*;

public abstract class Special {
	public abstract EvalResult apply(Environment env, Value[] args);
	
	public static final Special DEF = new Special() {
		@Override
		public EvalResult apply(Environment env, Value[] args) {
			if (args.length != 2)
				throw new MalException("Wrong number of args: expected 2, got "+args.length);
			Value v = args[1].eval(env);
			env.set(args[0].castToSymbol(), v);
			return EvalResult.done(v);
		}
	};
	
	public static final Special LET = new Special() {
		@Override
		public EvalResult apply(Environment env, Value[] args) {
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
			return EvalResult.tailCall(body, newEnv);
		}
	};
	
	public static final Special DO = new Special() {
		@Override
		public EvalResult apply(Environment env, Value[] args) {
			if (args.length == 0) {
				return EvalResult.done(Value.NIL);
			} else {
				for (int i=0; i < args.length-1; ++i) {
					args[i].eval(env);
				}
				return EvalResult.tailCall(args[args.length-1], env);
			}
		}
	};
	
	public static final Special IF = new Special() {
		@Override
		public EvalResult apply(Environment env, Value[] args) {
			if (args.length < 2 || args.length > 3)
				throw new MalException("if expects 2 or 3 forms, got "+args.length);
			Value t = args[0].eval(env);
			if (t == Value.NIL || t == Value.FALSE) {
				return EvalResult.tailCall((args.length == 3) ? args[2] : Value.NIL, env);
			} else {
				return EvalResult.tailCall(args[1], env);
			}
		}
	};
	
	private static final SymbolValue AMPERSAND = symbol("&");
	
	public static final Special FN = new Special() {
		@Override
		public EvalResult apply(Environment env, Value[] args) {
			if (args.length != 2)
				throw new MalException("fn* expects 2 forms, got "+args.length);
			List<SymbolValue> params = new LinkedList<>();
			SymbolValue tempVariadicParam = null;
			Iterator<Value> iter = args[0].castToValueSequence().iterator();
			while (iter.hasNext()) {
				SymbolValue s = iter.next().castToSymbol();
				if (AMPERSAND.equals(s)) {
					if (!iter.hasNext())
						throw new MalException("Expected symbol after '&' in function params");
					tempVariadicParam = iter.next().castToSymbol();
					break;
				} else {
					params.add(s);
				}
			}
			final SymbolValue variadicParam = tempVariadicParam;

			return EvalResult.done(new FuncValue((inputs) -> {
				if (variadicParam == null && inputs.length != params.size())
					throw new MalException("Wrong number of args: expected "+params.size()+", got "+inputs.length);
				if (variadicParam != null && inputs.length < params.size())
					throw new MalException("Wrong number of args: expected at least "+params.size()+", got "+inputs.length);
				Environment newEnv = new Environment(env);
				int i=0;
				for (SymbolValue param : params) {
					newEnv.set(param, inputs[i++]);
					if (i == params.size())
						break;
				}
				if (variadicParam != null) {
					ListValue remainder = list();
					if (i < inputs.length) {
						remainder = list(Arrays.copyOfRange(inputs, i, inputs.length));
					}
					newEnv.set(variadicParam, remainder);
				}
				return EvalResult.tailCall(args[1], newEnv);
			}));
		}
	};
	
	private static final Map<SymbolValue, Special> SPECIALS = new HashMap<>();

	static {
		SPECIALS.put(symbol("def!"), DEF);
		SPECIALS.put(symbol("let*"), LET);
		SPECIALS.put(symbol("do"), DO);
		SPECIALS.put(symbol("if"), IF);
		SPECIALS.put(symbol("fn*"), FN);
	}
	
	public static Special get(SymbolValue sym) {
		return SPECIALS.get(sym);
	}
}
