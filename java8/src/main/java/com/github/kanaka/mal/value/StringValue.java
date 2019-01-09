package com.github.kanaka.mal.value;

public class StringValue extends Value {

	public final String value;

	StringValue(String v) {
		this.value = v;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		StringValue other = (StringValue) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
	@Override
	public String prStr(boolean printReadably) {
		return (printReadably ? "\""+Value.escape(value)+"\"" : value);
	}
	
	@Override
	public StringValue castToString() {
		return this;
	}
}
