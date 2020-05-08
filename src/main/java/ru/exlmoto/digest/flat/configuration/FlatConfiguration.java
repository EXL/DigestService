package ru.exlmoto.digest.flat.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "flat")
public class FlatConfiguration {
	public int getMaxVariants() {
		return maxVariants;
	}

	public void setMaxVariants(int maxVariants) {
		this.maxVariants = maxVariants;
	}

	private int maxVariants;
}
