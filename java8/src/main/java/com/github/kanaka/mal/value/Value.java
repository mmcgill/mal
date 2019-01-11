package com.github.kanaka.mal.value;

import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import com.github.kanaka.mal.Environment;
import com.github.kanaka.mal.MalTypeException;

public abstract class Value {
	public static class EvalResult {
		public Value value;
		public boolean isTailCall;
		public Environment env;

		private EvalResult(Value result, boolean isTailCall, Environment env) {
			this.value = result;
			this.isTailCall = isTailCall;
			this.env = env;
		}
		
		public static EvalResult done(Value result) {
			return new EvalResult(result, false, null);
		}
		
		public static EvalResult tailCall(Value ast, Environment env) {
			return new EvalResult(ast, true, env);
		}
		
		public Value runToCompletion() {
			while (isTailCall) {
				EvalResult res = value.internalEval(env);
				value = res.value;
				isTailCall = res.isTailCall;
				env = res.env;
			}
			return value;
		}
	}

	public abstract String prStr(boolean printReadably);

	@Override
	public String toString() {
		return this.prStr(true);
	}

	public IntValue castToInt() {
		throw new MalTypeException("Cannot cast "+this.toString()+" to integer");
	}
	
	public FuncValue castToFn() {
		throw new MalTypeException("Cannot cast "+this.toString()+" to fn");
	}
	
	public SymbolValue castToSymbol() {
		throw new MalTypeException("Cannot cast "+this.toString()+" to symbol");
	}
	
	public ListValue castToList() {
		throw new MalTypeException("Cannot cast "+this.toString()+" to list");
	}
	
	public ValueSequence castToValueSequence() {
		throw new MalTypeException("Cannot cast "+this.toString()+" to value sequence");
	}
	
	public StringValue castToString() {
		throw new MalTypeException("Cannot cast "+this.toString()+" to string");
	}
	
	public AtomValue castToAtom() {
		throw new MalTypeException("Cannot cast "+this.toString()+" to atom");
	}
	
	public KeywordValue castToKeyword() {
		throw new MalTypeException("Cannot cast "+this.toString()+" to keyword");
	}
	
	public MapValue castToMap() {
		throw new MalTypeException("Cannot cast "+this.toString()+"to map");
	}
	
	public MetaHolder<? extends Value> castToMetaHolder() {
		throw new MalTypeException("Cannot cast "+this.toString()+"to metadata holder");
	}
	
	protected EvalResult internalEval(Environment env) {
		return new EvalResult(evalAst(env), false, null);
	}
	
	public boolean isMacroCall(Environment env) {
		return false;
	}
	
	public final Value eval(Environment env) {
		return internalEval(env).runToCompletion();
	}
	
	public Value evalAst(Environment env) {
		return this;
	}

	public static IntValue integer(long v) {
		return new IntValue(v);
	}
	
	public static SymbolValue symbol(String name) {
		return new SymbolValue(name);
	}
	
	public static ListValue list(Iterator<Value> iter) {
		ListValue v = ListValue.EMPTY;
		while (iter.hasNext()) {
			v = v.cons(iter.next());
		}
		return v.reverse();
	}
	
	public static ListValue list(Value... values) {
		ListValue v = ListValue.EMPTY;
		for (int i=values.length-1; i >= 0; --i)
			v = v.cons(values[i]);
		return v;
	}
	
	public static final BoolValue TRUE = BoolValue.TRUE;

	public static final BoolValue FALSE = BoolValue.FALSE;

	public static final BoolValue bool(boolean b) {
		return b ? TRUE : FALSE;
	}
	
	public static final NilValue NIL = NilValue.NIL;
	
	public static StringValue string(String s) {
		return new StringValue(s);
	}

	public static String unescape(String v) {
		StringBuilder sb = new StringBuilder(v.length());
		for (int i=0; i < v.length(); ++i) {
			char ch = v.charAt(i);
			if (ch == '\\' && i < v.length()-1) {
				switch (v.charAt(++i)) {
				case 'n': sb.append('\n'); break;
				case '\\': sb.append('\\'); break;
				case '"': sb.append('"'); break;
				}
			} else {
				sb.append(ch);
			}
		}
		return sb.toString();
	}

	public static String escape(String v) {
		StringBuilder sb = new StringBuilder(v.length());
		for (int i=0; i < v.length(); ++i) {
			char ch = v.charAt(i);
			switch (ch) {
			case '\\': sb.append("\\\\"); break;
			case '\n': sb.append("\\n"); break;
			case '"': sb.append("\\\""); break;
			default: sb.append(ch);
			}
		}
		return sb.toString();
	}
	
	public static KeywordValue keyword(String name) {
		return new KeywordValue(name);
	}
	
	public static VectorValue vector(Value... values) {
		return new VectorValue(values);
	}
	
	public static VectorValue vector(List<Value> values) {
		return new VectorValue(values);
	}
	
	public static MapValue hashMap(Value... values) {
		return new MapValue(values);
	}
	
	public static MapValue hashMap(Iterator<Value> values) {
		return new MapValue(values);
	}
	
	public static FuncValue tcoFn(Function<Value[], EvalResult> f, int minArgs, int maxArgs) {
		return new FuncValue(f, minArgs, maxArgs);
	}
	
	public static FuncValue fn(Function<Value[], Value> f) {
		return new FuncValue((inputs) -> EvalResult.done(f.apply(inputs)), 0, Integer.MAX_VALUE);
	}
	
	public static FuncValue fn(Function<Value[], Value> f, int minArgs, int maxArgs) {
		return new FuncValue((inputs) -> EvalResult.done(f.apply(inputs)), minArgs, maxArgs);
	}
	
	public static FuncValue fn1(Function<Value, Value> f) {
		return new FuncValue((inputs) -> EvalResult.done(f.apply(inputs[0])), 1, 1);
	}
	
	public static FuncValue fn(BiFunction<Value,Value,Value> f) {
		return new FuncValue((inputs) -> EvalResult.done(f.apply(inputs[0], inputs[1])), 2, 2);
	}
	
	public static FuncValue fn(Supplier<Value> f) {
		return new FuncValue((inputs) -> EvalResult.done(f.get()), 0, 0);
	}
	
	public static AtomValue atom(Value innerValue) {
		return new AtomValue(innerValue);
	}
}
