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

import org.thymeleaf.util.ArrayUtils;

import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.entity.BotDigestEntity;
import ru.exlmoto.digest.entity.BotDigestUserEntity;
import ru.exlmoto.digest.repository.BotDigestRepository;
import ru.exlmoto.digest.site.configuration.SiteConfiguration;
import ru.exlmoto.digest.site.form.GoToPageForm;
import ru.exlmoto.digest.site.model.DigestModel;
import ru.exlmoto.digest.site.model.PagerModel;
import ru.exlmoto.digest.site.model.post.Post;
import ru.exlmoto.digest.util.filter.FilterHelper;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class SiteController {
	private final Logger log = LoggerFactory.getLogger(SiteController.class);

	private final SiteConfiguration config;
	private final BotDigestRepository repository;
	private final LocaleHelper locale;

	private final BotHelper helper;
	private final FilterHelper filter;

	private enum Group {
		Guest,
		User,
		Moderator,
		Administrator
	}

	@Value("${general.date-format}")
	private String dateFormat;

	@Value("${bot.motofan-chat-id}")
	private long motofanChatId;

	@Value("${bot.motofan-chat-url}")
	private String motofanChatUrl;

	@Value("${bot.telegram-short-url}")
	private String telegramShortUrl;

	public SiteController(SiteConfiguration config,
	                      BotDigestRepository repository,
	                      LocaleHelper locale,
	                      BotHelper helper, FilterHelper filter) {
		this.config = config;
		this.repository = repository;
		this.locale = locale;
		this.helper = helper;
		this.filter = filter;
	}

	@RequestMapping(path = "/search")
	public String search(@RequestParam(name = "page", required = false) String page,
	                     @RequestParam(name = "text", required = false) String text,
	                     Model model) {
		int pagePosts = config.getPagePosts();
		int pageDeep = config.getPageDeep();
		int pageCount = getPageCount(repository.countBotDigestEntitiesByDigestContainingIgnoreCaseAndChatEquals(text,
			motofanChatId), pagePosts);
		int current = getCurrentPage(page, pageCount);

		model.addAttribute("goto", new GoToPageForm(String.valueOf(current), "/search?text=" + text));
		model.addAttribute("pager", new PagerModel(current, pageCount,
			current - ((pageDeep / 2) + 1), current + (pageDeep / 2)));
		model.addAttribute(
			"posts",
			new DigestModel(
				getDigestEntities(
					repository.findByDigestContainingIgnoreCaseAndChatEquals(
						PageRequest.of(current - 1, pagePosts, Sort.by(Sort.Order.asc("id"))),
						text,
						motofanChatId
					),
					current
				),
				getMotofanTitle(),
				getMotofanDescription()
			)
		);

		return "index";
	}

	@RequestMapping(path = "/jump")
	public String jump(@RequestParam(name = "id") String id) {
		Long postId = getCurrentPost(id);
		if (postId != null) {
			int index = repository.findAll(Sort.by(Sort.Order.asc("id"))).indexOf(new BotDigestEntity(postId));
			if (index != -1) {
				return "redirect:/?page=" + (index / config.getPagePosts() + 1) + "#" + id;
			}
		}
		return "redirect:/";
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
					),
					current
				),
				getMotofanTitle(),
				getMotofanDescription()
			)
		);

		return "index";
	}

	protected String getMotofanTitle() {
		return locale.i18n("site.content.head.title");
	}

	protected String getMotofanDescription() {
		return String.format(locale.i18n("site.content.head.description"),
			motofanChatId, motofanChatUrl, config.getMotofanChatSlug());
	}

	protected List<Post> getDigestEntities(Page<BotDigestEntity> digestEntities, int current) {
		if (digestEntities != null) {
			List<Post> posts = new ArrayList<>();
			int count = (current - 1) * config.getPagePosts();
			for (BotDigestEntity digest : digestEntities) {
				BotDigestUserEntity user = digest.getUser();
				String username = user.getUsername();
				posts.add(
					new Post(
						filterDescription(++count, digest.getId()),
						username,
						filterUsername(username, false),
						filterAvatarLink(user.getAvatar()),
						filterGroup(username),
						filterDateAndTime(digest.getDate()),
						activateUsers(activateLinks(digest.getDigest()))
					)
				);
			}
			return posts;
		}
		return new ArrayList<>();
	}

	protected String filterDescription(int count, long id) {
		return String.format(locale.i18n("site.content.description"),
			filter.checkLink(config.getAddress()) + "jump?id=" + id, count, id);
	}

	protected String activateUsers(String digest) {
		Matcher matcher = Pattern.compile("\\B@[a-z0-9_-]+",
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL).matcher(digest);
		StringBuffer stringBuffer = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(stringBuffer, filterUsername(matcher.group(0).trim(), true));
		}
		matcher.appendTail(stringBuffer);
		return stringBuffer.toString();
	}

	// https://stackoverflow.com/a/28269120
	protected String activateLinks(String digest) {
		Matcher matcher =
			Pattern.compile("((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)",
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL).matcher(digest);
		StringBuffer stringBuffer = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(stringBuffer,
				String.format("<a href=\"%1$s\" title=\"%1$s\" target=\"_blank\">%1$s</a>", matcher.group(0).trim()));
		}
		matcher.appendTail(stringBuffer);
		return stringBuffer.toString();
	}

	protected String filterUsername(String username, boolean at) {
		switch (checkGroup(username)) {
			default:
			case Guest: {
				return username;
			}
			case User: {
				return getUsernameLink(username, "member-user", at);
			}
			case Moderator: {
				return getUsernameLink(username, "member-moderator", at);
			}
			case Administrator: {
				return getUsernameLink(username, "member-administrator", at);
			}
		}
	}

	protected String filterGroup(String username) {
		switch (checkGroup(username)) {
			default:
			case Guest: {
				return locale.i18n("site.content.group.guest");
			}
			case User: {
				return locale.i18n("site.content.group.user");
			}
			case Moderator: {
				return locale.i18n("site.content.group.moderator");
			}
			case Administrator: {
				return locale.i18n("site.content.group.administrator");
			}
		}
	}

	protected String filterDateAndTime(long timestamp) {
		String[] dateAndTime = filter.getDateFromTimeStamp(dateFormat, timestamp).split(" ");
		if (dateAndTime.length == 2) {
			return String.format(locale.i18n("site.content.date.time"), dateAndTime[0], dateAndTime[1]);
		}
		return locale.i18n("site.content.date.time.wrong");
	}

	protected String getUsernameLink(String username, String className, boolean at) {
		String usernameWithoutAt = dropAt(username);
		return String.format("<a href=\"%s\" title=\"%s\" target=\"_blank\"><span class=\"%s\">%s</span></a>",
			filter.checkLink(telegramShortUrl) + usernameWithoutAt, username, className,
			(at) ? username :usernameWithoutAt);
	}

	protected Group checkGroup(String username) {
		if (helper.isUserAdmin(dropAt(username))) {
			return Group.Administrator;
		} else if (ArrayUtils.contains(config.getModerators(), dropAt(username))) {
			return Group.Moderator;
		} else if (username.startsWith("@")) {
			return Group.User;
		}
		return Group.Guest;
	}

	protected String dropAt(String username) {
		if (username.startsWith("@")) {
			return username.substring(1);
		}
		return username;
	}

	protected String filterAvatarLink(String avatarLink) {
		if (avatarLink != null) {
			if (config.isProxyEnabled()) {
				return filter.checkLink(config.getProxy()) + avatarLink.substring(avatarLink.indexOf("://") + 3);
			} else {
				return avatarLink;
			}
		}
		return null;
	}

	protected int getPageCount(long count, int pagePosts) {
		return ((((int) Math.max(Math.min(Integer.MAX_VALUE, count), Integer.MIN_VALUE)) - 1) / pagePosts) + 1;
	}

	protected Long getCurrentPost(String id) {
		if (id != null) {
			try {
				return Long.parseLong(id);
			} catch (NumberFormatException nfe) {
				log.warn(String.format("Cannot convert '%s' post id to long.", id), nfe);
			}
		}
		return null;
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
