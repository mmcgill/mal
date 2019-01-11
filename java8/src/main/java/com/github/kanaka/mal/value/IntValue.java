package com.github.kanaka.mal.value;

import com.github.kanaka.mal.MalException;

public class IntValue extends Value {
	public final long value;
	
	IntValue(long value) {
		this.value = value;
	}
	
	public IntValue add(IntValue v) {
		return new IntValue(value+v.value);
	}
	
	public IntValue subtract(IntValue v) {
		return new IntValue(value-v.value);
	}
	
	public IntValue multiply(IntValue v) {
		return new IntValue(value*v.value);
	}
	
	public IntValue divide(IntValue v) {
		if (v.value == 0) {
			throw new MalException("Divide by zero");
		}
		return new IntValue(value/v.value);
	}
	
	@Override
	public IntValue castToInt() {
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Long.hashCode(value);
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
		IntValue other = (IntValue) obj;
		if (value != other.value)
			return false;
		return true;
	}
	
	@Override
	public String prStr(boolean printReadably) {
		return Long.toString(value);
	}
}
