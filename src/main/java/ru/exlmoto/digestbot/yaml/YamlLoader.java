package ru.exlmoto.digestbot.yaml;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.util.ResourceUtils;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Map;

public class YamlLoader {
	private final String YAML_EXTENSION = ".yml";

	protected Map<String, ArrayList<String>> loadYamlFromResources(final String aGeneralFilename) {
		try {
			final File lYamlFile = ResourceUtils.getFile(getResourcePathToYamlFile(aGeneralFilename));
			return new Yaml().load(new String(Files.readAllBytes(lYamlFile.toPath())));
		} catch (IOException e) {
			throw new BeanCreationException("loadYamlFromResources", "Cannot create YamlLoader Bean!", e);
		}
	}

	private String getResourcePathToYamlFile(final String aGeneralFilename) {
		return "classpath:digestbot/" + aGeneralFilename + YAML_EXTENSION;
	}
}
