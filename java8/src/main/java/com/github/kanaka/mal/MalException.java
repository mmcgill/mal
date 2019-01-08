package com.github.kanaka.mal;

public class MalException extends RuntimeException {
	private static final long serialVersionUID = 3148836857978746291L;

	public MalException() {
		super();
	}
	
	public MalException(String message) {
		super(message);
	}
	
	public MalException(String message, Exception cause) {
		super(message, cause);
	}
}
