package ru.exlmoto.digest.util;

public final class Answer<T> {
	private final String error;
	private final T answer;

	private Answer(String error, T answer) {
		this.error = error;
		this.answer = answer;
	}

	public static <T> Answer<T> Error(String error) {
		return new Answer<>(error, null);
	}

	public static <T> Answer<T> Ok(T answer) {
		return new Answer<>("", answer);
	}

	public boolean ok() {
		return error.isEmpty();
	}

	public String error() {
		return error;
	}

	public T answer() {
		return answer;
	}

	@Override
	public String toString() {
		return
			"Answer{error=" + error +
			", answer=" + answer +
			"}";
	}
}
