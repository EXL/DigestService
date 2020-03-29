package ru.exlmoto.digest.site.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.exlmoto.digest.entity.BotDigestEntity;
import ru.exlmoto.digest.entity.BotDigestUserEntity;
import ru.exlmoto.digest.service.DatabaseService;
import ru.exlmoto.digest.site.configuration.SiteConfiguration;
import ru.exlmoto.digest.site.form.DigestForm;
import ru.exlmoto.digest.site.form.GoToPageForm;
import ru.exlmoto.digest.site.model.PagerModel;
import ru.exlmoto.digest.site.model.digest.Digest;
import ru.exlmoto.digest.site.util.SiteHelper;
import ru.exlmoto.digest.util.filter.FilterHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
	                   @RequestParam(name = "edit", required = false) String edit,
	                   GoToPageForm goToPageForm,
	                   DigestForm digestForm,
	                   PagerModel pager,
	                   Model model) {
		model.addAttribute("time", System.currentTimeMillis());

		Long digestId = helper.getLong(edit);
		if (digestId != null) {
			Optional<BotDigestEntity> digestOptional = service.getOneDigest(digestId);
			if (digestOptional.isPresent()) {
				BotDigestEntity digest = digestOptional.get();
				BotDigestUserEntity user = digest.getUser();

				digestForm.setUpdate(true);
				digestForm.setDigestId(digest.getId());
				digestForm.setChatId(digest.getChat());
				digestForm.setDate(digest.getDate());
				digestForm.setMessageId(digest.getMessageId());
				digestForm.setDigest(digest.getDigest());
				digestForm.setUserId(user.getId());
			} else {
				log.error(String.format("BotDigestEntity is null. Please check id variable: '%s'", edit));
			}
		} else {
			digestForm.setUpdate(false);
		}

		model.addAttribute("digestForm", digestForm);

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

	@PostMapping(path = "/obey/edit")
	public String obeyEdit(DigestForm digestForm) {
		System.out.println(digestForm.getDigestId());
		System.out.println(digestForm.getDigest());

		return "redirect:/obey";
	}
}
