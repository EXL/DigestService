package ru.exlmoto.digestbot.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;

import ru.exlmoto.digestbot.services.RestTextService;

@Service
public class HostIpService extends RestTextService {
	@Autowired
	public HostIpService(final RestTemplateBuilder aRestTemplateBuilder,
	                     @Value("${digestbot.service.hostip}") final String aRequestUrl,
	                     @Value("${digestbot.request.timeout}") final Integer aRequestTimeout) {
		super(aRestTemplateBuilder, aRequestUrl, aRequestTimeout);
	}
}
