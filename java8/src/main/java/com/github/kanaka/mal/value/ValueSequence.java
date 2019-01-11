package com.github.kanaka.mal.value;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.github.kanaka.mal.MalException;

public abstract class ValueSequence extends Value implements Iterable<Value> {
	private final Value meta;

	protected ValueSequence(Value meta) {
		this.meta = meta;
	}
	public abstract int getSize();
	
	public Value meta() { return meta; }
	
	@Override
	public ValueSequence castToValueSequence() {
		return this;
	}
	
	public abstract Iterator<Value> reverseIterator();
	
	public abstract ListValue coerceToList();
	
	public abstract ValueSequence conj(Value v);
	
	public Stream<Value> stream() {
		Spliterator<Value> si = Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED);
		return StreamSupport.stream(si, false);
	}
	
	public Stream<Value> reverseStream() {
		Spliterator<Value> si = Spliterators.spliteratorUnknownSize(reverseIterator(), Spliterator.ORDERED);
		return StreamSupport.stream(si, false);
	}
	
	public Value nth(long n) {
		try {
			return stream().skip(n).findFirst().get();
		} catch (NoSuchElementException ex) {
			throw new MalException("nth: index "+n+" out of bounds");
		}
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
