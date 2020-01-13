package ru.exlmoto.digest.util;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import org.springframework.lang.NonNull;

@Getter
@ToString
@EqualsAndHashCode
@Accessors(fluent = true)
public final class Answer {
	private final boolean status;
	private final String answer;

	public Answer(boolean status, @NonNull String answer) {
		this.status = status;
		this.answer = answer;
	}
}
