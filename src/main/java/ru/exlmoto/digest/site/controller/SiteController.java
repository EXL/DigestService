package ru.exlmoto.digest.site.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.exlmoto.digest.entity.BotDigestEntity;
import ru.exlmoto.digest.entity.BotDigestUserEntity;
import ru.exlmoto.digest.repository.BotDigestRepository;
import ru.exlmoto.digest.site.configuration.SiteConfiguration;
import ru.exlmoto.digest.site.form.GoToPageForm;
import ru.exlmoto.digest.site.model.DigestModel;
import ru.exlmoto.digest.site.model.PagerModel;
import ru.exlmoto.digest.site.model.post.Post;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SiteController {
	private final Logger log = LoggerFactory.getLogger(SiteController.class);

	private final SiteConfiguration config;
	private final BotDigestRepository repository;

	@Value("${bot.motofan-chat-id}")
	private long motofanChatId;

	@Value("${bot.motofan-chat-url}")
	private String motofanChatLink;

	public SiteController(SiteConfiguration config, BotDigestRepository repository) {
		this.config = config;
		this.repository = repository;
	}

	@RequestMapping(path = "/")
	public String index(@RequestParam(name = "page", required = false) String page, Model model) {
		int pagePosts = config.getPagePosts();
		int pageDeep = config.getPageDeep();
		int pageCount = getPageCount(repository.countBotDigestEntitiesByChat(motofanChatId), pagePosts);
		int current = getCurrentPage(page, pageCount);

		model.addAttribute("goto", new GoToPageForm(String.valueOf(current), "/"));
		model.addAttribute("pager", new PagerModel(current, pageCount,
			current - ((pageDeep / 2) + 1), current + (pageDeep / 2)));
		model.addAttribute(
			"posts",
			new DigestModel(
				getDigestEntities(
					repository.findBotDigestEntitiesByChat(
						PageRequest.of(current - 1, pagePosts, Sort.by(Sort.Order.asc("id"))), motofanChatId
					)
				),
				"Title",
				motofanChatId,
				config.getMotofanChatSlug(),
				motofanChatLink
			)
		);

		Page<BotDigestEntity> digestEntities =
			repository.findBotDigestEntitiesByChat(PageRequest.of(current - 1, pagePosts,
				Sort.by(Sort.Order.asc("id"))), motofanChatId);

		model.addAttribute("entities", digestEntities);

		return "index";
	}

	protected List<Post> getDigestEntities(Page<BotDigestEntity> digestEntities) {
		if (digestEntities != null) {
			List<Post> posts = new ArrayList<>();
			for (BotDigestEntity digest : digestEntities) {
				BotDigestUserEntity user = digest.getUser();
				posts.add(
					new Post(
						digest.getId(),
						user.getUsername(),
						user.getAvatar(),
						"Some Group",
						String.valueOf(digest.getDate()),
						String.valueOf(digest.getDate()),
						digest.getDigest()
					)
				);
			}
			return posts;
		}
		return new ArrayList<>();
	}

	protected int getPageCount(long count, int pagePosts) {
		return ((((int) Math.max(Math.min(Integer.MAX_VALUE, count), Integer.MIN_VALUE)) - 1) / pagePosts) + 1;
	}

	protected int getCurrentPage(String page, int pageCount) {
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
}
