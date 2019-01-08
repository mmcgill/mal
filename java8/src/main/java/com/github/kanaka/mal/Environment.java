package com.github.kanaka.mal;

import java.util.HashMap;
import java.util.Map;

import com.github.kanaka.mal.value.SymbolValue;
import com.github.kanaka.mal.value.Value;

public class Environment {
	private final Map<SymbolValue,Value> contents = new HashMap<>();

	public Environment() {}
	
	public Value get(SymbolValue sym) {
		Value result = contents.get(sym);
		if (result == null)
			throw new MalException("Symbol "+sym.toString()+" not bound");
		return result;
	}
	
	public void set(SymbolValue sym, Value val) {
		contents.put(sym, val);
	}
}
