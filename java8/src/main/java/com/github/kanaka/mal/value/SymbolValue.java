package com.github.kanaka.mal.value;

import com.github.kanaka.mal.Environment;

public class SymbolValue extends Value {
	private final String name;

	SymbolValue(String name) {
		this.name = name;
	}
	
	@Override
	public Value evalAst(Environment env) {
		return env.get(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SymbolValue other = (SymbolValue) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return name;
	}
}
