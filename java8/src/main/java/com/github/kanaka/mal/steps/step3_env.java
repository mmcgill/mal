package com.github.kanaka.mal.steps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import com.github.kanaka.mal.Environment;
import com.github.kanaka.mal.MalException;
import com.github.kanaka.mal.Reader;
import com.github.kanaka.mal.value.IntValue;
import com.github.kanaka.mal.value.Value;

import static com.github.kanaka.mal.value.Value.*;

public class step3_env {

	static String print(Value input) {
		return input.toString();
	}
	
	static List<String> rep(Environment env, String inputLine) {
		List<String> results = new LinkedList<>();
		try {
			Reader reader = new Reader(inputLine);
			for (Value form = reader.readForm(); form != null; form = reader.readForm()) {
				try {
					results.add(print(form.eval(env)));
				} catch (MalException ex) {
					results.add(ex.getMessage());
				}
			}
		} catch (MalException ex) {
			results.add(ex.getMessage());
		}
		return results;
	}
	
	private static IntValue add(Value[] inputs) {
		IntValue result = integer(0);
		for (Value v : inputs) {
			result = result.add(v.castToInt());
		}
		return result;
	}
	
	private static IntValue subtract(Value[] inputs) {
		if (inputs.length == 0) {
			throw new MalException("function requires at least 1 argument");
		} else {
			IntValue result = inputs[0].castToInt();
			for (int i=1; i < inputs.length; ++i)
				result = result.subtract(inputs[i].castToInt());
			return result;
		}
	}
	
	private static IntValue multiply(Value[] inputs) {
		IntValue result = integer(1);
		for (Value v : inputs) {
			result = result.multiply(v.castToInt());
		}
		return result;
	}
	
	private static IntValue divide(Value[] inputs) {
		if (inputs.length == 0) {
			throw new MalException("function requires at least 1 argument");
		} else {
			IntValue result = inputs[0].castToInt();
			for (int i=1; i < inputs.length; ++i)
				result = result.divide(inputs[i].castToInt());
			return result;
		}
	}

	public static void main(String[] args) {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		try {
			Environment repl_env = new Environment();
			repl_env.set(symbol("+"), fn(step3_env::add));
			repl_env.set(symbol("-"), fn(step3_env::subtract));
			repl_env.set(symbol("*"), fn(step3_env::multiply));
			repl_env.set(symbol("/"), fn(step3_env::divide));
			while (true) {
				System.out.print("user> ");
				System.out.flush();
				String inputLine = in.readLine();
				if (inputLine == null) {
					System.out.println("\nBye!");
					break;
				}
				for (String result : rep(repl_env, inputLine)) {
					System.out.println(result);
				}
				System.out.flush();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}
}
