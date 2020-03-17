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
import ru.exlmoto.digest.repository.BotDigestUserRepository;
import ru.exlmoto.digest.site.configuration.SiteConfiguration;
import ru.exlmoto.digest.site.form.GoToPageForm;
import ru.exlmoto.digest.site.form.SearchForm;
import ru.exlmoto.digest.site.model.DigestModel;
import ru.exlmoto.digest.site.model.PagerModel;
import ru.exlmoto.digest.site.model.post.Post;
import ru.exlmoto.digest.site.util.SiteHelper;
import ru.exlmoto.digest.util.filter.FilterHelper;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class SiteController {
	private final Logger log = LoggerFactory.getLogger(SiteController.class);

	private final SiteConfiguration config;
	private final BotDigestRepository repository;
	private final BotDigestUserRepository userRepository;
	private final LocaleHelper locale;

	private final BotHelper botHelper;
	private final SiteHelper siteHelper;
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
	                      BotDigestUserRepository userRepository,
	                      LocaleHelper locale,
	                      BotHelper botHelper,
	                      SiteHelper siteHelper,
	                      FilterHelper filter) {
		this.config = config;
		this.repository = repository;
		this.userRepository = userRepository;
		this.locale = locale;
		this.botHelper = botHelper;
		this.siteHelper = siteHelper;
		this.filter = filter;
	}

	@RequestMapping(path = "/")
	public String index(@RequestParam(name = "page", required = false) String page,
	                    @RequestParam(name = "post", required = false) String post,
	                    SearchForm searchForm,
	                    GoToPageForm goToPageForm,
	                    PagerModel pager,
	                    DigestModel digest,
	                    Model model,
	                    Locale lang) {
		setTitleAndGeneralData(model, "site.title", lang, searchForm);

		int pagePosts = config.getPagePosts();
		int current = getCurrentPageAndSetPagerData(model, pager, checkQueryLength(page),
			siteHelper.getPageCount(repository.countBotDigestEntitiesByChat(motofanChatId), pagePosts));

		setGotoFormData(model, goToPageForm, current);

		digest.setTitle(getMotofanTitle(lang));
		digest.setDescription(getMotofanDescription(lang));
		digest.setDigests(getDigestEntities(repository.findBotDigestEntitiesByChat(PageRequest.of(current - 1,
			pagePosts, Sort.by(Sort.Order.asc("id"))), motofanChatId), post, current, null, lang));
		model.addAttribute("posts", digest);

		return "index";
	}

	@RequestMapping(path = "/jump")
	public String jump(@RequestParam(name = "id") String id) {
		Long postId = siteHelper.getLong(id);
		if (postId != null) {
			int index = repository.findBotDigestEntitiesByChat(Sort.by(Sort.Order.asc("id")),
				motofanChatId).indexOf(new BotDigestEntity(postId));
			if (index != -1) {
				return String.format(
					"redirect:/?page=%1$d&post=%2$s#%2$s", (index / config.getPagePosts() + 1), id
				);
			}
		}

		return "redirect:/";
	}

	@RequestMapping(path = "/search")
	public String search(@RequestParam(name = "page", required = false) String page,
	                     @RequestParam(name = "text", required = false, defaultValue = "") String text,
	                     @RequestParam(name = "user", required = false) String user,
	                     SearchForm searchForm,
	                     GoToPageForm goToPageForm,
	                     PagerModel pager,
	                     DigestModel digest,
	                     Model model,
	                     Locale lang) {
		setTitleAndGeneralData(model, "site.title.search", lang, searchForm);

		String find = checkQueryLength(text);
		String member = checkQueryLength(user);

		searchForm.setText(find);
		searchForm.setUser(member);

		int pagePosts = config.getPagePosts();

		long count;
		BotDigestUserEntity digestUser = null;
		Long userId = siteHelper.getLong(member);
		if (userId != null) {
			digestUser = userRepository.getBotDigestUserEntityById(userId);
			count = repository.countBotDigestEntitiesByDigestContainingIgnoreCaseAndUserEqualsAndChatEquals(find,
				digestUser, motofanChatId);
		} else {
			count = repository.countBotDigestEntitiesByDigestContainingIgnoreCaseAndChatEquals(find, motofanChatId);
		}
		int current = getCurrentPageAndSetPagerData(model, pager,
			checkQueryLength(page), siteHelper.getPageCount(count, pagePosts));
		setGotoFormData(model, goToPageForm, current, find, userId);

		Page<BotDigestEntity> digestPage = (userId != null) ?
			repository.findByDigestContainingIgnoreCaseAndUserEqualsAndChatEquals(PageRequest.of(current - 1,
				pagePosts, Sort.by(Sort.Order.asc("id"))), find, digestUser, motofanChatId) :
			repository.findByDigestContainingIgnoreCaseAndChatEquals(PageRequest.of(current - 1, pagePosts,
				Sort.by(Sort.Order.asc("id"))), find, motofanChatId);
		digest.setTitle(getMotofanTitleSearch(digestUser, find, lang));
		digest.setDescription(getMotofanDescription(lang));
		digest.setDigests(getDigestEntities(digestPage, null, current, find, lang));
		model.addAttribute("posts", digest);

		return "index";
	}

	private void setTitleAndGeneralData(Model model, String key, Locale lang, SearchForm searchForm) {
		model.addAttribute("title", locale.i18nW(key, lang));
		model.addAttribute("lang", lang.toLanguageTag());
		model.addAttribute("find", searchForm);
	}

	private String checkQueryLength(String query) {
		final int MAX_QUERY_LENGTH = 100;
		if ((query != null) && (query.length() > MAX_QUERY_LENGTH)) {
			String chopped = query.substring(0, MAX_QUERY_LENGTH);
			log.warn(String.format("Too long some query: '%s', chopped to (0, %d) characters.",
				chopped, MAX_QUERY_LENGTH));
			return chopped;
		}
		return query;
	}

	private int getCurrentPageAndSetPagerData(Model model, PagerModel pager, String page, int pageCount) {
		int pageDeep = config.getPageDeep();
		int current = siteHelper.getCurrentPage(page, pageCount);

		pager.setCurrent(current);
		pager.setAll(pageCount);
		pager.setStartAux(current - ((pageDeep / 2) + 1));
		pager.setEndAux(current + (pageDeep / 2));
		model.addAttribute("pager", pager);

		return current;
	}

	private void setGotoFormData(Model model, GoToPageForm form, int current) {
		setGotoFormData(model, form, current, null, null);
	}

	private void setGotoFormData(Model model, GoToPageForm form, int current, String text, Long userId) {
		form.setPage(String.valueOf(current));
		form.setText(text);
		if (userId != null) {
			form.setUser(String.valueOf(userId));
			form.setPath("/search?text=" + text + "&user=" + userId);
		} else if (text != null) {
			form.setPath("/search?text=" + text);
		} else {
			form.setPath("/");
		}
		model.addAttribute("goto", form);
	}

	protected String getMotofanTitle(Locale lang) {
		return locale.i18nW("site.content.head.title", lang);
	}

	protected String getMotofanTitleSearch(BotDigestUserEntity user, String text, Locale lang) {
		final int length = 20;
		String query = "";
		if (text != null && !text.isEmpty()) {
			query = filter.ellipsisRight(text, length);
		}
		if (user != null) {
			String username = filter.ellipsisRight(user.getUsername(), length);
			if (!query.isEmpty()) {
				return String.format(locale.i18nW("site.content.head.title.search.user.text", lang),
					query, username);
			} else {
				return String.format(locale.i18nW("site.content.head.title.search.user", lang), username);
			}
		}
		return String.format(locale.i18nW("site.content.head.title.search", lang), query);
	}

	protected String getMotofanDescription(Locale lang) {
		return String.format(locale.i18nW("site.content.head.description", lang),
			motofanChatId, motofanChatUrl, config.getMotofanChatSlug());
	}

	protected List<Post> getDigestEntities(Page<BotDigestEntity> digestEntities,
	                                       String postId,
	                                       int current,
	                                       String text,
	                                       Locale lang) {
		if (digestEntities != null) {
			List<Post> posts = new ArrayList<>();
			int count = (current - 1) * config.getPagePosts();
			for (BotDigestEntity digest : digestEntities) {
				BotDigestUserEntity user = digest.getUser();
				String username = user.getUsername();
				long id = digest.getId();
				posts.add(
					new Post(
						highlightPost(postId, id),
						filterDescription(++count, id, digest.getMessageId(), lang),
						username,
						filterUsername(username, false),
						filterAvatarLink(user.getAvatar()),
						filterGroup(username, lang),
						filterDateAndTime(digest.getDate(), lang),
						activateUsers(activateLinks(filterHighlight(digest.getDigest(), text))),
						user.getId(),
						filterDigestCount(user, lang)
					)
				);
			}
			return posts;
		}
		return new ArrayList<>();
	}

	private String filterHighlight(String message, String highlight) {
		if (highlight == null || highlight.isEmpty()) {
			return message;
		}
		String[] words = message.split("\\s+");
		StringJoiner joiner = new StringJoiner(" ");
		for (String word : words) {
			String trim = word.trim();
			if (!trim.isEmpty()) {
				joiner.add(checkLinkAndUsername(word, highlight));
			}
		}
		return joiner.toString();
	}

	private String checkLinkAndUsername(String word, String highlight) {
		String wordCase = word.toLowerCase();
		String highlightCase = highlight.toLowerCase();

		if (wordCase.contains(highlightCase) && !word.contains("@") && !word.contains("://")) {
			StringBuilder stringBuffer = new StringBuilder(word);
			int start = wordCase.indexOf(highlightCase);
			int end = start + highlight.length();
			String text = word.substring(start, end);
			stringBuffer.replace(start, end, "<span class=\"text-highlight\">" + text + "</span>");
			return stringBuffer.toString();
		}
		return word;
	}

	private String filterDigestCount(BotDigestUserEntity user, Locale lang) {
		return String.format(locale.i18nW("site.content.user.digests", lang),
			filter.checkLink(config.getAddress()) + "search?user=" + user.getId(),
			repository.countBotDigestEntitiesByUserEqualsAndChatEquals(user, motofanChatId));
	}

	protected String filterDescription(int count, long id, Long messageId, Locale lang) {
		String link = String.format(locale.i18nW("site.content.description", lang),
			filter.checkLink(config.getAddress()) + "jump?id=" + id, count, id);
		if (messageId != null) {
			return String.format(locale.i18nW("site.content.chat.link", lang),
				filter.checkLink(motofanChatUrl) + messageId) + link;
		}
		return link;
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
		final int length = 100;
		Matcher matcher =
			Pattern.compile("((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)",
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL).matcher(digest);
		StringBuffer stringBuffer = new StringBuffer();
		while (matcher.find()) {
			String url = matcher.group(0).trim();
			String shortUrl = filter.ellipsisMiddle(url, length);
			matcher.appendReplacement(stringBuffer,
				String.format("<a href=\"%1$s\" title=\"%1$s\" target=\"_blank\">%2$s</a>", url, shortUrl));
		}
		matcher.appendTail(stringBuffer);
		return stringBuffer.toString();
	}

	protected String filterUsername(String username, boolean at) {
		switch (checkGroup(username)) {
			default:
			case Guest: {
				return filter.ellipsisRight(username, 20);
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

	protected String filterGroup(String username, Locale lang) {
		switch (checkGroup(username)) {
			default:
			case Guest: {
				return locale.i18nW("site.content.group.guest", lang);
			}
			case User: {
				return locale.i18nW("site.content.group.user", lang);
			}
			case Moderator: {
				return locale.i18nW("site.content.group.moderator", lang);
			}
			case Administrator: {
				return locale.i18nW("site.content.group.administrator", lang);
			}
		}
	}

	protected String filterDateAndTime(long timestamp, Locale lang) {
		String[] dateAndTime = filter.getDateFromTimeStamp(dateFormat, timestamp).split(" ");
		if (dateAndTime.length == 2) {
			return String.format(locale.i18nW("site.content.date.time", lang), dateAndTime[0], dateAndTime[1]);
		}
		return locale.i18nW("site.content.date.time.wrong", lang);
	}

	protected String getUsernameLink(String username, String className, boolean at) {
		String usernameWithoutAt = dropAt(username);
		String name = (at) ? username : usernameWithoutAt;
		return String.format("<a href=\"%s\" title=\"%s\" target=\"_blank\"><span class=\"%s\">%s</span></a>",
			filter.checkLink(telegramShortUrl) + usernameWithoutAt, username, className,
			filter.ellipsisRight(name, 20));
	}

	protected Group checkGroup(String username) {
		if (botHelper.isUserAdmin(dropAt(username))) {
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

	protected boolean highlightPost(String postId, long id) {
		Long post = siteHelper.getLong(postId);
		return (post != null && post.equals(id));
	}
}
