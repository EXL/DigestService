package ru.exlmoto.chart.manager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import ru.exlmoto.chart.ChartConfiguration;
import ru.exlmoto.chart.ChartConfigurationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class RestFileManagerTest extends ChartConfigurationTest {
	@Autowired
	private RestFileManager restFileManager;

	@Test
	public void testRestFileManager() {
		System.out.println(restFileManager.receiveFile("https://api.z-lab.me/charts/mmvb.png"));
		System.out.println(restFileManager.receiveFile("https://www.linux.org.ru/tracker/"));
		System.out.println(restFileManager.receiveFile("https://exlmoto.ru/"));
		assertThat(restFileManager.receiveFile("https://exlmoto.ru/404")).isNull();
	}

	@Test
	public void testRestFileManagerLargeFileDrop() {
		assertThat(restFileManager.receiveFile(
			"https://mirror.yandex.ru/astra/current/orel/iso/orel-current.iso"
		)).isNull();
	}

	@SpringBootApplication(scanBasePackageClasses = { ChartConfiguration.class })
	public static class ChartCommonConfiguration {

	}
}