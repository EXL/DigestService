package ru.exlmoto.digest.site.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.exlmoto.digest.entity.BotDigestEntity;
import ru.exlmoto.digest.service.DatabaseService;
import ru.exlmoto.digest.site.configuration.SiteConfiguration;
import ru.exlmoto.digest.site.form.GoToPageForm;
import ru.exlmoto.digest.site.model.PagerModel;
import ru.exlmoto.digest.site.model.digest.Digest;
import ru.exlmoto.digest.site.util.SiteHelper;
import ru.exlmoto.digest.util.filter.FilterHelper;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ObeyController {
	private final Logger log = LoggerFactory.getLogger(ObeyController.class);

	private final SiteHelper helper;
	private final DatabaseService service;
	private final FilterHelper filter;
	private final SiteConfiguration config;

	@Value("${general.date-format}")
	private String dateFormat;

	public ObeyController(SiteHelper helper, DatabaseService service, FilterHelper filter, SiteConfiguration config) {
		this.helper = helper;
		this.service = service;
		this.filter = filter;
		this.config = config;
	}

	@RequestMapping(path = "/obey")
	public String obey(@RequestParam(name = "page", required = false) String page,
	                   @CookieValue(value = "lang", defaultValue = "ru") String tag,
	                   GoToPageForm goToPageForm,
	                   PagerModel pager,
	                   Model model) {
		model.addAttribute("time", System.currentTimeMillis());

		final int DIGEST_LENGTH = 120;

		int pagePosts = config.getPagePostsAdmin();
		int pageDeep = config.getPageDeepAdmin();
		int current = helper.getCurrentPage(page);
		int pageCount = helper.getPageCount(service.getDigestCount(), pagePosts);
		if (current > pageCount) {
			current = pageCount;
		}

		Page<BotDigestEntity> digests = service.getAllDigests(current, pagePosts);
		List<Digest> digestList = new ArrayList<>();
		digests.forEach(digest -> digestList.add(new Digest(
			digest.getId(),
			digest.getUser().getUsername(),
			digest.getChat(),
			filter.getDateFromTimeStamp(dateFormat, digest.getDate()),
			filter.ellipsisMiddle(digest.getDigest(), DIGEST_LENGTH)
		)));

		model.addAttribute("digest", digestList);

		goToPageForm.setPage(String.valueOf(current));
		model.addAttribute("goto", goToPageForm);

		pager.setCurrent(current);
		pager.setAll(digests.getTotalPages());
		pager.setStartAux(current - ((pageDeep / 2) + 1));
		pager.setEndAux(current + (pageDeep / 2));
		model.addAttribute("pager", pager);
		model.addAttribute("pager", pager);

		return "obey";
	}

	@RequestMapping(path = "/obey/delete/{id}")
	public String obeyDelete(@PathVariable(name = "id") String id) {
		Long digestId = helper.getLong(id);
		if (digestId != null) {
			service.deleteDigest(digestId);
		} else {
			log.error(String.format("Cannot delete post where id is null. Please check id variable: '%s'", id));
		}
		return "redirect:/obey";
	}
}
