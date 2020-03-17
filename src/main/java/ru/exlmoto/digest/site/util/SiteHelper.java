package ru.exlmoto.digest.site.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
public class SiteHelper {
	private final Logger log = LoggerFactory.getLogger(SiteHelper.class);

	public int getCurrentPage(String page, int pageCount) {
		try {
			int parsed = -1;
			if (page != null) {
				parsed = Integer.parseInt(page);
			}
			return (parsed > 0 && parsed < pageCount) ? parsed : pageCount;
		} catch (NumberFormatException nfe) {
			log.warn(String.format("Cannot convert '%s' page to int.", page), nfe);
			return pageCount;
		}
	}

	public Long getLong(String id) {
		if (id != null && !id.isEmpty()) {
			try {
				return Long.parseLong(id);
			} catch (NumberFormatException nfe) {
				log.warn(String.format("Cannot convert '%s' id to long.", id), nfe);
			}
		}
		return null;
	}

	public int getPageCount(long count, int pagePosts) {
		return ((((int) Math.max(1, Math.min(Integer.MAX_VALUE, count))) - 1) / Math.max(1, pagePosts)) + 1;
	}
}
