package com.github.kanaka.mal;

import static com.github.kanaka.mal.value.Value.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import com.github.kanaka.mal.value.AtomValue;
import com.github.kanaka.mal.value.BoolValue;
import com.github.kanaka.mal.value.FuncValue;
import com.github.kanaka.mal.value.IntValue;
import com.github.kanaka.mal.value.KeywordValue;
import com.github.kanaka.mal.value.ListValue;
import com.github.kanaka.mal.value.MapValue;
import com.github.kanaka.mal.value.NilValue;
import com.github.kanaka.mal.value.StringValue;
import com.github.kanaka.mal.value.SymbolValue;
import com.github.kanaka.mal.value.Value;
import com.github.kanaka.mal.value.ValueSequence;
import com.github.kanaka.mal.value.VectorValue;

public class Core {
	public static final Environment NS = new Environment();
	static {
		NS.set(symbol("+"), fn(Core::add));
		NS.set(symbol("-"), fn(Core::subtract, 1, Integer.MAX_VALUE));
		NS.set(symbol("*"), fn(Core::multiply));
		NS.set(symbol("/"), fn(Core::divide, 1, Integer.MAX_VALUE));

		NS.set(symbol("list"), fn(Value::list, 0, Integer.MAX_VALUE));
		NS.set(symbol("list?"), fn1(Core::isList));
		NS.set(symbol("empty?"), fn1(Core::isEmpty));
		NS.set(symbol("count"), fn1(Core::count));

		NS.set(symbol("="), fn(Core::isEqual));
		NS.set(symbol("<"), fn(Core::lt));
		NS.set(symbol("<="), fn(Core::lte));
		NS.set(symbol(">"), fn(Core::gt));
		NS.set(symbol(">="), fn(Core::gte));

		NS.set(symbol("pr-str"), fn(Core::prStr));
		NS.set(symbol("prn"), fn(Core::prn));
		NS.set(symbol("str"), fn(Core::str));
		NS.set(symbol("println"), fn(Core::println));
		
		NS.set(symbol("read-string"), fn1(Core::readString));
		NS.set(symbol("slurp"), fn1(Core::slurp));
		
		NS.set(symbol("atom"), fn1(Core::atom));
		NS.set(symbol("atom?"), fn1(Core::isAtom));
		NS.set(symbol("deref"), fn1(Core::deref));
		NS.set(symbol("reset!"), fn(Core::reset));
		NS.set(symbol("swap!"), fn(Core::swap, 2, Integer.MAX_VALUE));
		
		NS.set(symbol("cons"), fn(Core::cons));
		NS.set(symbol("concat"), fn(Core::concat, 0, Integer.MAX_VALUE));
		
		NS.set(symbol("nth"), fn(Core::nth));
		NS.set(symbol("first"), fn1(Core::first));
		NS.set(symbol("rest"), fn1(Core::rest));
		
		NS.set(symbol("throw"), fn1(Core::malThrow));
		NS.set(symbol("apply"), tcoFn(Core::apply, 2, Integer.MAX_VALUE));
		NS.set(symbol("map"), fn(Core::map));
		NS.set(symbol("nil?"), fn1(Core::isNil));
		NS.set(symbol("true?"), fn1(Core::isTrue));
		NS.set(symbol("false?"), fn1(Core::isFalse));
		NS.set(symbol("symbol?"), fn1(Core::isSymbol));
		
		NS.set(symbol("symbol"), fn1(Core::symbolFn));
		NS.set(symbol("keyword"), fn1(Core::keywordFn));
		NS.set(symbol("keyword?"), fn1(Core::isKeyword));
		NS.set(symbol("vector"), fn(Core::vectorFn));
		NS.set(symbol("vector?"), fn1(Core::isVector));
		NS.set(symbol("hash-map"), fn(Core::hashMapFn));
		NS.set(symbol("map?"), fn1(Core::isMap));
		NS.set(symbol("assoc"), fn(Core::assoc));
		NS.set(symbol("dissoc"), fn(Core::dissoc));
		NS.set(symbol("get"), fn(Core::get));
		NS.set(symbol("contains?"), fn(Core::contains));
		NS.set(symbol("keys"), fn1(Core::keys));
		NS.set(symbol("vals"), fn1(Core::vals));
		NS.set(symbol("sequential?"), fn1(Core::isSequential));
	}
	
	public static IntValue add(Value[] inputs) {
		IntValue result = integer(0);
		for (Value v : inputs) {
			result = result.add(v.castToInt());
		}
		return result;
	}
	
	public static IntValue subtract(Value[] inputs) {
		IntValue result = inputs[0].castToInt();
		for (int i=1; i < inputs.length; ++i)
			result = result.subtract(inputs[i].castToInt());
		return result;
	}
	
	public static IntValue multiply(Value[] inputs) {
		IntValue result = integer(1);
		for (Value v : inputs) {
			result = result.multiply(v.castToInt());
		}
		return result;
	}
	
	public static IntValue divide(Value[] inputs) {
		IntValue result = inputs[0].castToInt();
		for (int i=1; i < inputs.length; ++i)
			result = result.divide(inputs[i].castToInt());
		return result;
	}
	
	public static BoolValue isList(Value input) {
		return bool(input instanceof ListValue);
	}
	
	public static BoolValue isEmpty(Value input) {
		return bool(input.castToValueSequence().getSize() == 0);
	}
	
	public static IntValue count(Value input) {
		return integer(input.castToValueSequence().getSize());
	}
	
	private static BoolValue reduceToBool(BiFunction<Value,Value,Boolean> op, Value[] inputs) {
		boolean result = true;
		for (int i=0; result && i < inputs.length-1; ++i) {
			result = op.apply(inputs[i], inputs[i+1]);
		}
		return bool(result);
	}
	
	public static BoolValue isEqual(Value[] inputs) {
		return reduceToBool(Value::equals, inputs);
	}
	
	public static BoolValue lt(Value[] inputs) {
		return reduceToBool((a, b) -> a.castToInt().value < b.castToInt().value, inputs);
	}
	
	public static BoolValue lte(Value[] inputs) {
		return reduceToBool((a, b) -> a.castToInt().value <= b.castToInt().value, inputs);
	}
	
	public static BoolValue gt(Value[] inputs) {
		return reduceToBool((a, b) -> a.castToInt().value > b.castToInt().value, inputs);
	}
	
	public static BoolValue gte(Value[] inputs) {
		return reduceToBool((a, b) -> a.castToInt().value >= b.castToInt().value, inputs);
	}
	
	public static StringValue prStr(Value[] inputs) {
		return string(Arrays.stream(inputs)
				.map((v) -> v.prStr(true))
				.collect(Collectors.joining(" ")));
	}
	
	public static NilValue prn(Value[] inputs) {
		System.out.println(prStr(inputs).value);
		return Value.NIL;
	}
	
	public static StringValue str(Value[] inputs) {
		return string(Arrays.stream(inputs)
				.map((v) -> v.prStr(false))
				.collect(Collectors.joining("")));
	}
	
	public static NilValue println(Value[] inputs) {
		System.out.println(Arrays.stream(inputs)
				.map((v) -> v.prStr(false))
				.collect(Collectors.joining(" ")));
		return Value.NIL;
	}
	
	public static Value readString(Value input) {
		Value v = new Reader(input.castToString().value).readForm();
		return (v == null) ? Value.NIL : v;
	}
	
	public static StringValue slurp(Value input) {
		String filePath = input.castToString().value;

		try {
			return string(new String(Files.readAllBytes(Paths.get(filePath))));
		} catch (IOException ex) {
			throw new MalException("Failed to read file "+filePath+": "+ex.getMessage());
		}
	}
	
	public static AtomValue atom(Value input) { return Value.atom(input); } 

	public static BoolValue isAtom(Value input) { return bool(input instanceof AtomValue); }
	
	public static Value deref(Value input) { return input.castToAtom().get(); }
	
	public static Value reset(Value atom, Value value) {
		atom.castToAtom().set(value);
		return value;
	}
	
	public static Value swap(Value[] inputs) {
		AtomValue a = inputs[0].castToAtom();
		FuncValue f = inputs[1].castToFn();
		Value[] args = new Value[1+(inputs.length-2)];
		for (int i=0; i < inputs.length-2; i++) {
			args[i+1] = inputs[i+2];
		}
		return a.swap((v) -> {
			args[0] = v;
			EvalResult result = f.apply(args);
			if (result.isTailCall) {
				return result.value.eval(result.env);
			} else {
				return result.value;
			}
		});
	}
	
	public static Value cons(Value v, Value l) {
		return l.castToValueSequence().coerceToList().cons(v);
	}
	
	public static ListValue concat(Value... inputs) {
		ListValue result = list();
		for (int i=inputs.length-1; i >= 0; --i) {
			result = inputs[i]
					.castToValueSequence()
					.reverseStream()
					.map(v -> list(v))
					.reduce(result, (r,l) -> r.cons(l.getHead()));
		}
		return result;
	}
	
	public static Value nth(Value v, Value n) {
		return v.castToValueSequence().nth(n.castToInt().value);
	}
	
	public static Value first(Value v) {
		Iterator<Value> iter = v.castToValueSequence().iterator();
		return iter.hasNext() ? iter.next() : Value.NIL;
	}
	
	public static ListValue rest(Value v) {
		if (v == Value.NIL)
			return ListValue.EMPTY;
		ListValue t = v.castToValueSequence().coerceToList().getTail();
		return t == null ? ListValue.EMPTY : t;
	}
	
	public static Value malThrow(Value v) {
		throw new MalException(v);
	}
	
	public static EvalResult apply(Value[] inputs) {
		FuncValue f = inputs[0].castToFn();
		ValueSequence s = inputs[inputs.length-1].castToValueSequence();
		Value[] newArgs = new Value[s.getSize() + (inputs.length-2)];
		int i=0;
		for (; i < inputs.length-2; ++i) {
			newArgs[i] = inputs[i+1];
		}
		for (Value v : s) {
			newArgs[i++] = v;
		}
		return f.apply(newArgs);
	}
	
	public static ListValue map(Value v1, Value v2) {
		FuncValue f = v1.castToFn();
		ListValue result = ListValue.EMPTY;
		for (Value v : v2.castToValueSequence()) {
			result = result.cons(f.apply(v).runToCompletion());
		}
		return result.reverse();
	}
	
	public static BoolValue isNil(Value v) { return bool(v == Value.NIL); }
	
	public static BoolValue isTrue(Value v) { return bool(v == Value.TRUE); }

	public static BoolValue isFalse(Value v) { return bool(v == Value.FALSE); }

	public static BoolValue isSymbol(Value v) { return bool(v instanceof SymbolValue); }
	
	public static SymbolValue symbolFn(Value v) { return symbol(v.castToString().value); }
	
	public static KeywordValue keywordFn(Value v) {
		if (v instanceof KeywordValue) return v.castToKeyword();
		return Value.keyword(v.castToString().value);
	}
	
	public static BoolValue isKeyword(Value v) { return bool(v instanceof KeywordValue); }
	
	public static VectorValue vectorFn(Value[] inputs) { return vector(inputs); }
	
	public static BoolValue isVector(Value v) { return bool(v instanceof VectorValue); }
	
	public static MapValue hashMapFn(Value[] inputs) { return hashMap(inputs); }
	
	public static BoolValue isMap(Value v) { return bool(v instanceof MapValue); }
	
	public static MapValue assoc(Value[] inputs) { return inputs[0].castToMap().assoc(inputs, 1); }
	
	public static MapValue dissoc(Value[] inputs) { return inputs[0].castToMap().dissoc(inputs, 1); }
	
	public static Value get(Value m, Value k) { return (m == Value.NIL) ? Value.NIL : m.castToMap().get(k); }
	
	public static BoolValue contains(Value m, Value k) { return bool(m.castToMap().contains(k)); }
	
	public static ListValue keys(Value m) { return list(m.castToMap().keys().iterator()); }
	
	public static ListValue vals(Value m) { return list(m.castToMap().vals().iterator()); }
	
	public static BoolValue isSequential(Value v) { return bool(v != Value.NIL && v instanceof ValueSequence); }
}
