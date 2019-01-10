package com.github.kanaka.mal;

import static com.github.kanaka.mal.value.Value.string;

import com.github.kanaka.mal.value.Value;

public class MalException extends RuntimeException {
	private static final long serialVersionUID = 3148836857978746291L;
	
	public final Value value;

	public MalException(String message) {
		super(message);
		value = string(message);
	}
	
	public MalException(String message, Exception cause) {
		super(message, cause);
		value = string(message);
	}
	
	public MalException(Value value) {
		super(value.prStr(true));
		this.value = value;
	}
}
