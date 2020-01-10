package ru.exlmoto.motofan.service;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import ru.exlmoto.motofan.MotofanConfiguration;

@RequiredArgsConstructor
@Service
@EnableConfigurationProperties(MotofanConfiguration.class)
public class MotofanService {
	private final MotofanConfiguration config;

	public String getData() {
		return config.getLastPostUrl();
	}
}
