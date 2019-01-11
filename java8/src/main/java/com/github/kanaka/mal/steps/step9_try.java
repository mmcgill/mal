package com.github.kanaka.mal.steps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.github.kanaka.mal.Core;
import com.github.kanaka.mal.Environment;
import com.github.kanaka.mal.MalException;
import com.github.kanaka.mal.Reader;
import com.github.kanaka.mal.value.Value;

import static com.github.kanaka.mal.value.Value.*;

public class step9_try {

	static void rep(Environment env, String inputLine, Consumer<Value> onResult, BiConsumer<Value,MalException> onError) {
		try {
			Reader reader = new Reader(inputLine);
			for (Value form = reader.readForm(); form != null; form = reader.readForm()) {
				try {
					onResult.accept(form.eval(env));
				} catch (MalException ex) {
					onError.accept(form, ex);
				}
			}
		} catch (MalException ex) {
			System.err.println(ex.getMessage());
			System.err.flush();
		}
	}
	
	static void rep(Environment env, String inputLine) {
		rep(env, inputLine, v -> {}, (f,ex) -> {});
	}
	
	private static void onResult(Value result) {
		System.out.println(result.prStr(true));
		System.out.flush();
	}
	
	private static void onError(Value form, MalException ex) {
		System.err.println("Exception: "+ex.value.prStr(true));
		System.err.flush();
	}

	public static void main(String[] args) {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		try {
			Environment repl_env = new Environment(Core.NS);
			repl_env.set(symbol("eval"), fn1((input) -> {
				return input.eval(repl_env);
			}));
			rep(repl_env, "(def! not (fn* [a] (if a false true)))");
			rep(repl_env, "(def! load-file (fn* [f] (eval (read-string (str \"(do \" (slurp f) \")\")))))");
			rep(repl_env, "(load-file \"src/main/resources/step8.mal\")");
			String targetFile = (args.length > 0) ? args[0] : null;
			List<Value> targetArgs = new LinkedList<Value>();
			for (int i=1; i < args.length; ++i) {
				targetArgs.add(string(args[i]));
			}
			repl_env.set(symbol("*ARGV*"), list(targetArgs.iterator()));
			
			if (targetFile != null) {
				rep(repl_env, "(load-file \""+Value.escape(targetFile)+"\")",
						(v) -> {},
						step9_try::onError);
				System.exit(0);
			}

			while (true) {
				System.out.print("user> ");
				System.out.flush();
				String inputLine = in.readLine();
				if (inputLine == null) {
					System.out.println("\nBye!");
					break;
				}
				rep(repl_env, inputLine, step9_try::onResult, step9_try::onError);
				System.out.flush();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}
}
