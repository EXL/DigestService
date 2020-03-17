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

	@Value("${bot.motofan-chat-id}")
	private long motofanChatId;

	@Value("${bot.motofan-chat-url}")
	private String motofanChatUrl;

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
	                                       String search,
	                                       Locale lang) {
		if (digestEntities != null) {
			List<Post> posts = new ArrayList<>();
			int count = (current - 1) * config.getPagePosts();
			for (BotDigestEntity digest : digestEntities) {
				BotDigestUserEntity user = digest.getUser();
				String username = user.getUsername();
				long userId = user.getId();
				long id = digest.getId();
				posts.add(
					new Post(
						siteHelper.highlightPost(postId, id),
						siteHelper.filterDescription(++count, id, digest.getMessageId(), lang),
						username,
						siteHelper.filterUsername(username, false),
						siteHelper.filterAvatarLink(user.getAvatar()),
						siteHelper.filterGroup(username, lang),
						siteHelper.filterDateAndTime(digest.getDate(), lang),
						siteHelper.filterDigestOrder(digest.getDigest(), search),
						userId,
						siteHelper.filterDigestCount(userId, repository.countBotDigestEntitiesByUserEqualsAndChatEquals(user, motofanChatId), lang)
					)
				);
			}
			return posts;
		}
		return new ArrayList<>();
	}
}
