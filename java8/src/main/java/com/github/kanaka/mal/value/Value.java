package com.github.kanaka.mal.value;

import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.github.kanaka.mal.Environment;
import com.github.kanaka.mal.MalTypeException;

public abstract class Value {
	public static class EvalResult {
		public final Value value;
		public final boolean isTailCall;
		public final Environment env;

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
	
	protected EvalResult internalEval(Environment env) {
		return new EvalResult(evalAst(env), false, null);
	}
	
	public final Value eval(Environment env) {
		Value val = this;
		while (true) {
			EvalResult result = val.internalEval(env);
			if (result.isTailCall) {
				val = result.value;
				env = result.env;
			} else {
				return result.value;
			}
		}
	}
	
	public Value evalAst(Environment env) {
		return this;
	}

	public static IntValue integer(int v) {
		return new IntValue(v);
	}
	
	public static SymbolValue symbol(String name) {
		return new SymbolValue(name);
	}
	
	public static ListValue list(Value... values) {
		return new ListValue(new Iterator<Value>() {
			private int i=0;

			@Override public boolean hasNext() {
				return i < values.length;
			}
			@Override public Value next() {
				return values[i++];
			}
		});
	}
	
	public static ListValue list(Iterator<Value> values) {
		return new ListValue(values);
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
	
	public static AtomValue atom(Value innerValue) {
		return new AtomValue(innerValue);
	}
}
