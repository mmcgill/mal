package com.github.kanaka.mal;

import static com.github.kanaka.mal.value.Value.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import com.github.kanaka.mal.value.AtomValue;
import com.github.kanaka.mal.value.BoolValue;
import com.github.kanaka.mal.value.FuncValue;
import com.github.kanaka.mal.value.IntValue;
import com.github.kanaka.mal.value.ListValue;
import com.github.kanaka.mal.value.NilValue;
import com.github.kanaka.mal.value.StringValue;
import com.github.kanaka.mal.value.Value;

public class Core {
	public static final Environment NS = new Environment();
	static {
		NS.set(symbol("+"), fn(Core::add));
		NS.set(symbol("-"), fn(Core::subtract));
		NS.set(symbol("*"), fn(Core::multiply));
		NS.set(symbol("/"), fn(Core::divide));

		NS.set(symbol("list"), fn(Value::list));
		NS.set(symbol("list?"), fn(Core::isList));
		NS.set(symbol("empty?"), fn(Core::isEmpty));
		NS.set(symbol("count"), fn(Core::count));

		NS.set(symbol("="), fn(Core::isEqual));
		NS.set(symbol("<"), fn(Core::lt));
		NS.set(symbol("<="), fn(Core::lte));
		NS.set(symbol(">"), fn(Core::gt));
		NS.set(symbol(">="), fn(Core::gte));

		NS.set(symbol("pr-str"), fn(Core::prStr));
		NS.set(symbol("prn"), fn(Core::prn));
		NS.set(symbol("str"), fn(Core::str));
		NS.set(symbol("println"), fn(Core::println));
		
		NS.set(symbol("read-string"), fn(Core::readString));
		NS.set(symbol("slurp"), fn(Core::slurp));
		
		NS.set(symbol("atom"), fn(Core::atom));
		NS.set(symbol("atom?"), fn(Core::isAtom));
		NS.set(symbol("deref"), fn(Core::deref));
		NS.set(symbol("reset!"), fn(Core::reset));
		NS.set(symbol("swap!"), fn(Core::swap));
	}
	
	public static IntValue add(Value[] inputs) {
		IntValue result = integer(0);
		for (Value v : inputs) {
			result = result.add(v.castToInt());
		}
		return result;
	}
	
	public static IntValue subtract(Value[] inputs) {
		if (inputs.length == 0) {
			throw new MalException("function requires at least 1 argument");
		} else {
			IntValue result = inputs[0].castToInt();
			for (int i=1; i < inputs.length; ++i)
				result = result.subtract(inputs[i].castToInt());
			return result;
		}
	}
	
	public static IntValue multiply(Value[] inputs) {
		IntValue result = integer(1);
		for (Value v : inputs) {
			result = result.multiply(v.castToInt());
		}
		return result;
	}
	
	public static IntValue divide(Value[] inputs) {
		if (inputs.length == 0) {
			throw new MalException("function requires at least 1 argument");
		} else {
			IntValue result = inputs[0].castToInt();
			for (int i=1; i < inputs.length; ++i)
				result = result.divide(inputs[i].castToInt());
			return result;
		}
	}
	
	public static BoolValue isList(Value[] inputs) {
		if (inputs.length != 1)
			throw new MalException("function expects 1 argument, got "+inputs.length);
		return bool(inputs[0] instanceof ListValue);
	}
	
	public static BoolValue isEmpty(Value[] inputs) {
		if (inputs.length != 1)
			throw new MalException("function expects 1 argument, got "+inputs.length);
		return bool(inputs[0].castToValueSequence().size() == 0);
	}
	
	public static IntValue count(Value[] inputs) {
		if (inputs.length != 1)
			throw new MalException("function expects 1 argument, got "+inputs.length);
		return integer(inputs[0].castToValueSequence().size());
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
	
	public static Value readString(Value[] inputs) {
		if (inputs.length != 1)
			throw new MalException("Expected 1 argument, got "+inputs.length);
		Value v = new Reader(inputs[0].castToString().value).readForm();
		return (v == null) ? Value.NIL : v;
	}
	
	public static StringValue slurp(Value[] inputs) {
		if (inputs.length != 1)
			throw new MalException("Expected 1 argument, got "+inputs.length);
		String filePath = inputs[0].castToString().value;

		try {
			return string(new String(Files.readAllBytes(Paths.get(filePath))));
		} catch (IOException ex) {
			throw new MalException("Failed to read file "+filePath+": "+ex.getMessage());
		}
	}
	
	public static AtomValue atom(Value[] inputs) {
		if (inputs.length != 1)
			throw new MalException("Expected 1 argument, got "+inputs.length);
		return Value.atom(inputs[0]);
	}
	
	public static BoolValue isAtom(Value[] inputs) {
		if (inputs.length != 1)
			throw new MalException("Expected 1 argument, got "+inputs.length);
		return bool(inputs[0] instanceof AtomValue);
	}
	
	public static Value deref(Value[] inputs) {
		if (inputs.length != 1)
			throw new MalException("Expected 1 argument, got "+inputs.length);
		return inputs[0].castToAtom().get();
	}
	
	public static Value reset(Value[] inputs) {
		if (inputs.length != 2)
			throw new MalException("Expected 2 arguments, got "+inputs.length);
		inputs[0].castToAtom().set(inputs[1]);
		return inputs[1];
	}
	
	public static Value swap(Value[] inputs) {
		if (inputs.length < 2)
			throw new MalException("Expected 2 arguments, got "+inputs.length);
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
}
