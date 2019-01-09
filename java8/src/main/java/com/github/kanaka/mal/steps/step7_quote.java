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

public class step7_quote {

	static void rep(Environment env, String inputLine, Consumer<Value> onResult, BiConsumer<Value,Exception> onError) {
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

	public static void main(String[] args) {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		try {
			Environment repl_env = new Environment(Core.NS);
			repl_env.set(symbol("eval"), fn1((input) -> {
				return input.eval(repl_env);
			}));
			rep(repl_env, "(def! not (fn* [a] (if a false true)))");
			rep(repl_env, "(def! load-file (fn* [f] (eval (read-string (str \"(do \" (slurp f) \")\")))))");
			String targetFile = (args.length > 0) ? args[0] : null;
			List<Value> targetArgs = new LinkedList<Value>();
			for (int i=1; i < args.length; ++i) {
				targetArgs.add(string(args[i]));
			}
			repl_env.set(symbol("*ARGV*"), list(targetArgs.iterator()));
			
			if (targetFile != null) {
				rep(repl_env, "(load-file \""+Value.escape(targetFile)+"\")",
						(v) -> {},
						(f, ex) -> {
							System.err.println(ex.getMessage());
							System.err.flush();
						});
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
				rep(repl_env, inputLine,
						v -> {
							System.out.println(v.prStr(true));
							System.out.flush();
						},
						(f,ex) -> {
							System.err.println(ex.getMessage());
							System.err.flush();
						});
				System.out.flush();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}
}
