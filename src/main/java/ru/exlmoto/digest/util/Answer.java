package ru.exlmoto.digest.util;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@Getter
@ToString
@EqualsAndHashCode
@Accessors(fluent = true)
public final class Answer<T> {
	private final String error;
	private final T answer;

	public Answer(@NonNull String error, @Nullable T answer) {
		this.error = error;
		this.answer = answer;
	}

	public boolean ok() {
		return error.isEmpty();
	}
}
