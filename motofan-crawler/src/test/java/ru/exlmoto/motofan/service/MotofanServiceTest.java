package ru.exlmoto.motofan.service;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import ru.exlmoto.motofan.MotofanConfiguration;
import ru.exlmoto.motofan.MotofanConfigurationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class MotofanServiceTest extends MotofanConfigurationTest {
	@Autowired
	private MotofanService motofanService;

	@Test
	public void contextLoads() {
		assertThat(motofanService).isNotNull();
	}

	@SpringBootApplication(scanBasePackageClasses = { MotofanConfiguration.class })
	public static class MotofanConfigurationCommon {

	}
}
