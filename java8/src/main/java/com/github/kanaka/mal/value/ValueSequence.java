package com.github.kanaka.mal.value;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class ValueSequence extends Value implements Iterable<Value> {
	public abstract int getSize();
	
	@Override
	public ValueSequence castToValueSequence() {
		return this;
	}
	
	public Stream<Value> stream() {
		Spliterator<Value> si = Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED);
		return StreamSupport.stream(si, false);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		Iterator<Value> iter = iterator();
		while (iter.hasNext())
			result = prime * result + iter.next().hashCode();
		return result;
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
		ValueSequence that = (ValueSequence)obj;

		if (getSize() != that.getSize())
			return false;

		Iterator<Value> a = this.iterator();
		Iterator<Value> b = that.iterator();
		while (a.hasNext()) {
			if (!b.hasNext())
				return false;
			Value a1 = a.next();
			Value b1 = b.next();
			if (!a1.equals(b1))
				return false;
		}
		if (b.hasNext()) return false;
		return true;
	}
}
