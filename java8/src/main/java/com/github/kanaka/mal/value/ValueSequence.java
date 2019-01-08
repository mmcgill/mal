package com.github.kanaka.mal.value;

import java.util.Iterator;
import java.util.List;

public abstract class ValueSequence extends Value implements Iterable<Value> {
	protected abstract List<Value> readOnlyItems();

	@Override
	public Iterator<Value> iterator() {
		return readOnlyItems().iterator();
	}
	
	public int size() {
		return readOnlyItems().size();
	}
	
	@Override
	public int hashCode() {
		return readOnlyItems().hashCode();
	}
	
	@Override
	public ValueSequence castToValueSequence() {
		return this;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return true;
		if (!(obj instanceof ValueSequence))
			return false;
		if (obj == NilValue.NIL)
			return false;
		return readOnlyItems().equals(((ValueSequence)obj).readOnlyItems());
	}
}
