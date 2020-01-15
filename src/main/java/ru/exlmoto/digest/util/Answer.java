package ru.exlmoto.digest.util;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@ToString
@EqualsAndHashCode
@Accessors(fluent = true)
@RequiredArgsConstructor
public final class Answer<T> {
	private final String error;
	private final T answer;

	public static <T> Answer<T> Error(String error) {
		return new Answer<>(error, null);
	}

	public static <T> Answer<T> Ok(T answer) {
		return new Answer<>("", answer);
	}

	public boolean ok() {
		return error.isEmpty();
	}
}
