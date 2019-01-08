package com.github.kanaka.mal.value;

public class BoolValue extends Value {
	public static final BoolValue TRUE = new BoolValue(true);
	public static final BoolValue FALSE = new BoolValue(false);

	private final boolean value;

	private BoolValue(boolean value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		return value ? 1 : 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		return false;
	}

	@Override
	public String prStr(boolean printReadably) {
		return Boolean.toString(value);
	}
}
