package ru.exlmoto.digestbot.services.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;

import ru.exlmoto.digestbot.services.RestFileService;

@Service
public class FileService extends RestFileService {
	public FileService(final RestTemplateBuilder aRestTemplateBuilder,
	                   @Value("${digestbot.request.timeout}") final Integer aRequestTimeout) {
		super(aRestTemplateBuilder, aRequestTimeout);
	}
}
