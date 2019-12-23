package ru.exlmoto.digestbot.yaml;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.core.io.ClassPathResource;

import org.springframework.util.ResourceUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Map;

public class YamlLoader {
	protected final int K_TITLE_RU = 0;
	protected final int K_TITLE_EN = 1;

	protected final String K_RUSSIAN_LANG = "ru";
	protected final String K_ENGLISH_LANG = "en";

	private final String YAML_EXTENSION = ".yml";

	protected Map<String, ArrayList<String>> loadYamlFromResources(final String aGeneralFilename) {
		try {
			final InputStream lInputStream =
					new ClassPathResource(getResourcePathToYamlFile(aGeneralFilename)).getInputStream();
			return new Yaml().load(lInputStream);
		} catch (IOException e) {
			try {
				final File lYamlFile = ResourceUtils.getFile(getResourcePathToYamlFile(aGeneralFilename));
				return new Yaml().load(new String(Files.readAllBytes(lYamlFile.toPath())));
			} catch (IOException ex) {
				throw new BeanCreationException("loadYamlFromResources", "Cannot create YamlLoader Bean!", ex);
			}
		}
	}

	private String getResourcePathToYamlFile(final String aGeneralFilename) {
		return "classpath:digestbot/" + aGeneralFilename + YAML_EXTENSION;
	}
}
