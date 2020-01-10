package ru.exlmoto.motofan.service;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import ru.exlmoto.motofan.MotofanConfigurationTest;

public class MotofanServiceTest extends MotofanConfigurationTest {
	@Autowired
	private MotofanService motofanService;

	@Test
	public void getData() {
		System.out.println(motofanService.getData());
	}

	@SpringBootApplication(scanBasePackageClasses = { MotofanConfigurationTest.class })
	public static class ExchangeConfigurationCommon {

	}
}