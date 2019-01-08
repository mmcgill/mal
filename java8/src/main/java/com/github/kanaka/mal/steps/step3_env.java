package com.github.kanaka.mal.steps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import com.github.kanaka.mal.Core;
import com.github.kanaka.mal.Environment;
import com.github.kanaka.mal.MalException;
import com.github.kanaka.mal.Reader;
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

	public static void main(String[] args) {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		try {
			Environment repl_env = new Environment();
			repl_env.set(symbol("+"), fn(Core::add));
			repl_env.set(symbol("-"), fn(Core::subtract));
			repl_env.set(symbol("*"), fn(Core::multiply));
			repl_env.set(symbol("/"), fn(Core::divide));
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
