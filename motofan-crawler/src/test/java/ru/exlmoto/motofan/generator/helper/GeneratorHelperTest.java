package ru.exlmoto.motofan.generator.helper;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.NoSuchMessageException;

import ru.exlmoto.motofan.MotofanConfigurationTest;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class GeneratorHelperTest extends MotofanConfigurationTest {
	@Autowired
	private GeneratorHelper helper;

	@Test
	public void testInternationalization() {
		assertThat(helper.i18n("title")).isInstanceOf(String.class);
		assertThat(helper.i18n("wrote")).isInstanceOf(String.class);
		assertThat(helper.i18n("read")).isInstanceOf(String.class);
		assertThrows(NoSuchMessageException.class, () -> helper.i18n("unknown.value"));
	}

	@SpringBootApplication
	public static class MotofanConfigurationCommon {

	}
}
