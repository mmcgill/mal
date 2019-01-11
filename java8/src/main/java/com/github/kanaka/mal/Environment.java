package com.github.kanaka.mal;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import com.github.kanaka.mal.value.SymbolValue;
import com.github.kanaka.mal.value.Value;

public class Environment {
	private final Map<SymbolValue,Value> contents = new HashMap<>();

	private final Environment outer;
	
	public BufferedReader stdin;
	public PrintStream stdout;

	public Environment() {
		outer = null;
	}
	
	public Environment(Environment outer) {
		this.outer = outer;
		this.stdin = outer.stdin;
		this.stdout = outer.stdout;
	}
	
	public Environment find(SymbolValue sym) {
		Environment env = this;
		while (env != null && !env.contents.containsKey(sym)) {
			env = env.outer;
		}
		return env;
	}
	
	public Value get(SymbolValue sym) {
		Environment env = find(sym);
		if (env == null)
			throw new MalException("'"+sym.toString()+"' not found");
		return env.contents.get(sym);
	}
	
	public Environment outermost() {
		Environment env = this;
		while (env.outer != null) {
			env = env.outer;
		}
		return env;
	}
	
	public void set(SymbolValue sym, Value val) {
		contents.put(sym, val);
	}
}
