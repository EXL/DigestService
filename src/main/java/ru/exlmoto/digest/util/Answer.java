package ru.exlmoto.digest.util;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Getter
@ToString
@EqualsAndHashCode
@Accessors(fluent = true)
@RequiredArgsConstructor
public final class Answer {
	@NotNull
	private final Boolean status;

	@NotNull
	private final String answer;
}
