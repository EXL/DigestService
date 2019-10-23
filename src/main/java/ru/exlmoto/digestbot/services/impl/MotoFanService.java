package ru.exlmoto.digestbot.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import ru.exlmoto.digestbot.services.RestTextService;

@Service
public class MotoFanService extends RestTextService {
	final private String mRequestUrl;

	@Autowired
	public MotoFanService(final RestTemplateBuilder aRestTemplateBuilder,
	                      @Value("${digestbot.crawler.motofan.link}") final String aRequestUrl,
	                      @Value("${digestbot.request.timeout}") final Integer aRequestTimeout) {
		super(aRestTemplateBuilder, aRequestTimeout);
		mRequestUrl = aRequestUrl;
	}

	public Pair<Boolean, String> receiveObject() {
		return super.receiveObject(mRequestUrl);
	}
}
