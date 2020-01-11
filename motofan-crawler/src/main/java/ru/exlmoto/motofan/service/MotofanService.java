package ru.exlmoto.motofan.service;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import ru.exlmoto.motofan.MotofanConfiguration;
import ru.exlmoto.motofan.manager.MotofanManager;

import java.util.List;

@RequiredArgsConstructor
@Service
@EnableConfigurationProperties(MotofanConfiguration.class)
public class MotofanService {
	private final MotofanManager motofanManager;

	public List<String> htmlMotofanPostsReport() {
		return motofanManager.getLastMotofanPostsInHtml();
	}
}
