package ru.exlmoto.digest.launcher;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class DigestApplicationTest {
	@Autowired
	private DigestApplication digestApplication;

	@Test
	public void contextLoads() {
		assertThat(digestApplication).isNotNull();
	}
}
