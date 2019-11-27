package ru.exlmoto.digestbot.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import ru.exlmoto.digestbot.services.RestTextService;

@Service
public class AvatarService extends RestTextService {
	private final Boolean mUseNginxProxy;

	@Autowired
	public AvatarService(final RestTemplateBuilder aRestTemplateBuilder,
	                     @Value("${digestbot.request.timeout}") final Integer aRequestTimeout,
	                     @Value("${digestbot.nginx.proxy}") final Boolean aUseNginxProxy) {
		super(aRestTemplateBuilder, aRequestTimeout);
		mUseNginxProxy = aUseNginxProxy;
	}

	// TODO: Move hardcodes to Globals
	public Pair<Boolean, String> getAvatarUrlByUserName(final String aUsername) {
		if (aUsername != null) {
			final Pair<Boolean, String> lContentPage = receiveObject("https://t.me/" + aUsername);
			if (lContentPage.getFirst()) {
				final String lAvatarUrl = getAvatarLinkFromRawHtml(lContentPage.getSecond());
				if (checkAvatarLinkExtension(lAvatarUrl)) {
					final String lUrlPrefix = (mUseNginxProxy) ? "https://lab.exlmoto.ru/proxy/" : "https://";
					return Pair.of(true, lUrlPrefix + lAvatarUrl);
				}
			}
		}
		return Pair.of(false, "https://exlmoto.ru/Images/DigestBot/Telegram_Logo_Orange.png");
	}

	private String getAvatarLinkFromRawHtml(final String aPageContent) {
		final String lSubString = aPageContent.substring(
				aPageContent.indexOf("<meta property=\"og:image\""),
				aPageContent.indexOf("<meta property=\"og:site_name\""));
		return lSubString.substring(lSubString.indexOf("://"), lSubString.indexOf("\">")).substring(3);
	}

	private Boolean checkAvatarLinkExtension(final String aAvatarLink) {
		final String[] mImageExtensions = new String[]{".jpg", ".JPG", ".png", ".PNG", ".gif", ".GIF"};
		for (String iImageExtensions : mImageExtensions) {
			if (aAvatarLink.endsWith(iImageExtensions)) {
				return true;
			}
		}
		return false;
	}
}
