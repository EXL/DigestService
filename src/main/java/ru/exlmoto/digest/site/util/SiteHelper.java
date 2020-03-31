package ru.exlmoto.digest.site.util;

import org.nibor.autolink.LinkExtractor;
import org.nibor.autolink.LinkSpan;
import org.nibor.autolink.Span;
import org.nibor.autolink.LinkType;

import org.owasp.encoder.Encode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import org.thymeleaf.util.ArrayUtils;

import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.entity.BotDigestEntity;
import ru.exlmoto.digest.entity.BotDigestUserEntity;
import ru.exlmoto.digest.entity.ExchangeRateEntity;
import ru.exlmoto.digest.service.DatabaseService;
import ru.exlmoto.digest.site.configuration.SiteConfiguration;
import ru.exlmoto.digest.site.model.post.Post;
import ru.exlmoto.digest.site.model.user.User;
import ru.exlmoto.digest.util.filter.FilterHelper;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

import java.util.List;
import java.util.Locale;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.StringJoiner;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SiteHelper {
	private final Logger log = LoggerFactory.getLogger(SiteHelper.class);

	private final int CHOP_LINK = 100;

	private final SiteConfiguration config;
	private final LocaleHelper locale;
	private final FilterHelper filter;
	private final BotHelper helper;

	private enum Group {
		Guest,
		User,
		Moderator,
		Administrator
	}

	@Value("${general.date-format}")
	private String dateFormat;

	@Value("${bot.telegram-short-url}")
	private String telegramShortUrl;

	@Value("${bot.motofan-chat-url}")
	private String motofanChatUrl;

	@Value("${bot.motofan-chat-id}")
	private long motofanChatId;

	public SiteHelper(SiteConfiguration config, LocaleHelper locale, FilterHelper filter, BotHelper helper) {
		this.config = config;
		this.locale = locale;
		this.filter = filter;
		this.helper = helper;
	}

	public List<Post> getPosts(Page<BotDigestEntity> page,
	                           String postId,
	                           int current,
	                           DatabaseService service,
	                           Locale lang) {
		return getPostsAux(page, postId, current, null, service, lang);
	}

	public List<Post> getPostsSearch(Page<BotDigestEntity> page,
	                                 int current,
	                                 String search,
	                                 DatabaseService service,
	                                 Locale lang) {
		return getPostsAux(page, null, current, search, service, lang);
	}

	public List<User> getUsers(DatabaseService service, String sort, boolean desc, Locale lang) {
		List<User> userList = new ArrayList<>();

		List<BotDigestUserEntity> users = service.getAllUsersByChat(motofanChatId);
		sortUserList(sort, desc, service, users);

		int index = 0;
		for (BotDigestUserEntity user : users) {
			String username = user.getUsername();
			long userId = user.getId();
			userList.add(new User(
				++index,
				filterAvatarLink(user.getAvatar()),
				username,
				filterUsername(username, false),
				userId,
				filterGroup(username, lang),
				filterDigestCount(userId, service.getDigestCount(user, motofanChatId), lang)
			));
		}

		return userList;
	}

	public void sortUserList(String sort, boolean desc, DatabaseService service, List<BotDigestUserEntity> users) {
		String sorted = (sort != null) ? sort : "";
		switch (sorted) {
			case "id":
				users.sort(Comparator.comparing(BotDigestUserEntity::getId));
				Collections.reverse(users);
				break;
			case "group":
				users.sort(Comparator.comparing(digestUserEntity -> checkGroup(digestUserEntity.getUsername())));
				break;
			case "post":
				users.sort(Comparator.comparingLong(digestUserEntity ->
					service.getDigestCount(digestUserEntity, motofanChatId)));
				break;
			default:
				users.sort((first, second) ->
					String.CASE_INSENSITIVE_ORDER.compare(dropAt(first.getUsername()), dropAt(second.getUsername())));
				break;
		}
		if (desc) {
			Collections.reverse(users);
		}
	}

	private List<Post> getPostsAux(Page<BotDigestEntity> page,
	                               String postId,
	                               int current,
	                               String search,
	                               DatabaseService service,
	                               Locale lang) {
		if (page != null) {
			List<Post> posts = new ArrayList<>();
			int count = (current - 1) * config.getPagePosts();
			for (BotDigestEntity digest : page) {
				BotDigestUserEntity user = digest.getUser();
				String username = user.getUsername();
				long userId = user.getId();
				long digestId = digest.getId();
				posts.add(
					new Post(
						highlightPost(postId, digestId),
						filterDescription(++count, digestId, digest.getMessageId(), lang),
						username,
						filterUsername(username, false),
						filterAvatarLink(user.getAvatar()),
						filterGroup(username, lang),
						filterDateAndTime(digest.getDate(), lang),
						filterDigestOrder(digest.getDigest(), search),
						userId,
						filterDigestCount(userId, service.getDigestCount(user, motofanChatId), lang)
					)
				);
			}
			return posts;
		}
		return new ArrayList<>();
	}

	public String getMotofanTitle(Locale lang) {
		return locale.i18nW("site.content.head.title", lang);
	}

	public String getMotofanTitleSearch(BotDigestUserEntity user, String text, Locale lang) {
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

	public String getMotofanDescription(Locale lang) {
		return String.format(locale.i18nW("site.content.head.description", lang),
			motofanChatId, motofanChatUrl, config.getMotofanChatSlug());
	}

	public boolean highlightPost(String postId, long id) {
		Long post = getLong(postId);
		return (post != null && post.equals(id));
	}

	public String filterDescription(int count, long id, Long messageId, Locale lang) {
		String link = String.format(locale.i18nW("site.content.description", lang),
			filter.checkLink(config.getAddress()) + "jump?id=" + id, count, id);
		if (messageId != null) {
			return String.format(locale.i18nW("site.content.chat.link", lang),
				filter.checkLink(motofanChatUrl) + messageId) + link;
		}
		return link;
	}

	public String filterUsername(String username, boolean at) {
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

	public String filterAvatarLink(String avatarLink) {
		final String DEL = "://";
		if (avatarLink != null && !avatarLink.isEmpty() && avatarLink.contains(DEL) && config.isProxyEnabled()) {
			return filter.checkLink(config.getProxy()) + avatarLink.substring(avatarLink.indexOf(DEL) + DEL.length());
		}
		return avatarLink;
	}

	public String filterGroup(String username, Locale lang) {
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

	public String filterDateAndTime(long timestamp, Locale lang) {
		String[] dateAndTime = filter.getDateFromTimeStamp(dateFormat, timestamp).split(" ");
		if (dateAndTime.length == 2) {
			return String.format(locale.i18nW("site.content.date.time", lang), dateAndTime[0], dateAndTime[1]);
		}
		return locale.i18nW("site.content.date.time.wrong", lang);
	}

	public String filterDigestOrder(String digest, String search) {
		return activateUsers(activateLinks(activateHighlight(digest, search)));
	}

	public String filterDigestCount(long userId, long count, Locale lang) {
		return String.format(locale.i18nW("site.content.user.digests", lang),
			filter.checkLink(config.getAddress()) + "search?user=" + userId, count);
	}

	protected String activateUsers(String digest) {
		if (digest != null && !digest.isEmpty()) {
			Matcher matcher = Pattern.compile("\\B@[\\w_-]+",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL).matcher(digest);
			StringBuffer stringBuffer = new StringBuffer();
			while (matcher.find()) {
				matcher.appendReplacement(stringBuffer, filterUsername(matcher.group(0).trim(), true));
			}
			return matcher.appendTail(stringBuffer).toString();
		}
		return digest;
	}

	protected String activateLinks(String digest) {
		return (digest != null && !digest.isEmpty()) ?
			(config.isAutolinkerEnabled()) ? activateLinksViaAutolinker(digest) : activateLinksViaRegExp(digest) :
			digest;
	}

	// Source: https://stackoverflow.com/a/28269120
	private String activateLinksViaRegExp(String digest) {
		Matcher matcher =
			Pattern.compile("((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?+-=\\\\.&]*)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL).matcher(digest);
		StringBuffer stringBuffer = new StringBuffer();
		while (matcher.find()) {
			String url = matcher.group(0).trim();
			String shortUrl = filter.ellipsisMiddle(url, CHOP_LINK);
			matcher.appendReplacement(stringBuffer,
				String.format("<a href=\"%1$s\" title=\"%1$s\" target=\"_blank\">%2$s</a>", url, shortUrl));
		}
		return matcher.appendTail(stringBuffer).toString();
	}

	private String activateLinksViaAutolinker(String digest) {
		LinkExtractor linkExtractor = LinkExtractor.builder()
			.linkTypes(EnumSet.of(LinkType.URL, LinkType.WWW, LinkType.EMAIL))
			.build();
		Iterable<Span> spans = linkExtractor.extractSpans(digest);
		StringBuilder stringBuilder = new StringBuilder();
		for (Span span : spans) {
			String chunk = digest.substring(span.getBeginIndex(), span.getEndIndex());
			if (span instanceof LinkSpan) {
				String link = filter.ellipsisMiddle(Encode.forHtml(chunk), CHOP_LINK);
				String attr = Encode.forHtmlAttribute(chunk);

				stringBuilder.append("<a href=\"");
				stringBuilder.append(((LinkSpan) span).getType().equals(LinkType.EMAIL) ? "mailto:" + attr : attr);
				stringBuilder.append("\" title=\"");
				stringBuilder.append(attr);
				stringBuilder.append("\" target=\"_blank\">");
				stringBuilder.append(link);
				stringBuilder.append("</a>");
			} else {
				stringBuilder.append(chunk);
			}
		}
		return stringBuilder.toString();
	}

	protected String activateHighlight(String message, String highlight) {
		if (message != null && !message.isEmpty() && highlight != null && !highlight.isEmpty()) {
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
		return message;
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

	private String getUsernameLink(String username, String className, boolean at) {
		String usernameWithoutAt = dropAt(username);
		String name = (at) ? username : usernameWithoutAt;
		return String.format("<a href=\"%s\" title=\"%s\" target=\"_blank\"><span class=\"%s\">%s</span></a>",
			filter.checkLink(telegramShortUrl) + usernameWithoutAt, username, className,
			filter.ellipsisRight(name, 20));
	}

	private int getCurrentPage(String page, int pageCount, boolean asc) {
		try {
			int parsed = -1;
			if (page != null) {
				parsed = Integer.parseInt(page);
			}
			return (asc) ?
				(parsed > 0 && parsed < pageCount) ? parsed : pageCount :
				(parsed > 0) ? parsed : 1;
		} catch (NumberFormatException nfe) {
			log.warn(String.format("Cannot convert '%s' page to int.", page), nfe);
			return (asc) ? pageCount : 1;
		}
	}

	public int getCurrentPage(String page) {
		return getCurrentPage(page, 1, false);
	}

	public int getCurrentPage(String page, int pageCount) {
		return getCurrentPage(page, pageCount, true);
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

	public String dropAt(String username) {
		return isUsername(username) ? username.substring(1) : username;
	}

	private Group checkGroup(String username) {
		if (helper.isUserAdmin(dropAt(username))) {
			return Group.Administrator;
		} else if (ArrayUtils.contains(config.getModerators(), dropAt(username))) {
			return Group.Moderator;
		} else if (isUsername(username)) {
			return Group.User;
		}
		return Group.Guest;
	}

	private boolean isUsername(String username) {
		return username != null && !username.isEmpty() && username.startsWith("@");
	}

	public String chopQuery(String query) {
		final int MAX_QUERY_LENGTH = 100;
		if ((query != null) && (query.length() > MAX_QUERY_LENGTH)) {
			String chopped = query.substring(0, MAX_QUERY_LENGTH);
			log.warn(String.format("Too long some query: '%s', chopped to (0, %d) characters.",
				chopped, MAX_QUERY_LENGTH));
			return chopped;
		}
		return query;
	}

	public ExchangeRateEntity copyRateValues(ExchangeRateEntity fromRate, ExchangeRateEntity toRate) {
		toRate.setDate(fromRate.getDate());
		toRate.setUsd(fromRate.getUsd());
		toRate.setEur(fromRate.getEur());
		toRate.setGbp(fromRate.getGbp());
		toRate.setCny(fromRate.getCny());
		toRate.setRub(fromRate.getRub());
		toRate.setUah(fromRate.getUah());
		toRate.setByn(fromRate.getByn());
		toRate.setKzt(fromRate.getKzt());
		toRate.setGold(fromRate.getGold());
		toRate.setSilver(fromRate.getSilver());
		toRate.setPlatinum(fromRate.getPlatinum());
		toRate.setPalladium(fromRate.getPalladium());
		toRate.setPrev(fromRate.getPrev());
		return toRate;
	}
}
