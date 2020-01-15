package ru.exlmoto.digest.util.file;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.io.UncheckedIOException;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ResourceHelperTest {
	@Value("classpath:chart/charts.yaml")
	private Resource resourceOk;

	@Value("classpath:unknown-file.ext")
	private Resource resourceFail;

	@Autowired
	private ResourceHelper resourceHelper;

	@Test
	public void testReadFileToString() {
		String res = resourceHelper.readFileToString("classpath:chart/charts.yaml");
		assertThat(res).isNotEmpty();
		System.out.println(res.substring(0, 40));

		assertThrows(UncheckedIOException.class,
			() -> resourceHelper.readFileToString("classpath:unknown-file.ext"));
	}

	@Test
	public void testAsString() {
		String res = resourceHelper.asString(resourceOk);
		assertThat(res).isNotEmpty();
		System.out.println(res.substring(0, 40));

		assertThrows(UncheckedIOException.class, () -> resourceHelper.asString(resourceFail));
	}
}
