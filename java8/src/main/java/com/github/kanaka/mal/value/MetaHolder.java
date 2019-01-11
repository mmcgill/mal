package com.github.kanaka.mal.value;

public interface MetaHolder<T extends Value> {
	public Value meta();
	public T withMeta(Value meta);
}
