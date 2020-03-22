package ru.exlmoto.digest.site.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.exlmoto.digest.entity.BotDigestEntity;
import ru.exlmoto.digest.entity.BotDigestUserEntity;
import ru.exlmoto.digest.service.DatabaseService;
import ru.exlmoto.digest.site.configuration.SiteConfiguration;
import ru.exlmoto.digest.site.form.GoToPageForm;
import ru.exlmoto.digest.site.form.SearchForm;
import ru.exlmoto.digest.site.model.DigestModel;
import ru.exlmoto.digest.site.model.PagerModel;
import ru.exlmoto.digest.site.util.SiteHelper;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import java.util.Locale;

@Controller
public class SiteController {
	private final SiteConfiguration config;
	private final DatabaseService service;
	private final LocaleHelper locale;
	private final SiteHelper helper;

	@Value("${bot.motofan-chat-id}")
	private long motofanChatId;

	public SiteController(SiteConfiguration config,
	                      DatabaseService service,
	                      LocaleHelper locale,
	                      SiteHelper helper) {
		this.config = config;
		this.service = service;
		this.locale = locale;
		this.helper = helper;
	}

	@RequestMapping(path = "/")
	public String index(@RequestParam(name = "page", required = false) String page,
	                    @RequestParam(name = "post", required = false) String post,
	                    @CookieValue(value = "lang", defaultValue = "ru") String tag,
	                    SearchForm searchForm,
	                    GoToPageForm goToPageForm,
	                    PagerModel pager,
	                    DigestModel digest,
	                    Model model) {
		Locale lang = Locale.forLanguageTag(tag);

		setTitleAndGeneralData(model, "site.title", lang, searchForm);

		int pagePosts = config.getPagePosts();
		int current = getCurrentPageAndSetPagerData(model, pager, helper.chopQuery(page),
			helper.getPageCount(service.getDigestCount(motofanChatId), pagePosts));

		setGotoFormData(model, goToPageForm, current);

		digest.setTitle(helper.getMotofanTitle(lang));
		digest.setDescription(helper.getMotofanDescription(lang));
		digest.setDigests(helper.getPosts(service.getChatDigestsSite(current, pagePosts, motofanChatId),
			post, current, service, lang));
		model.addAttribute("posts", digest);

		return "index";
	}

	@RequestMapping(path = "/jump")
	public String jump(@RequestParam(name = "id") String id) {
		Long postId = helper.getLong(id);
		if (postId != null) {
			int index = service.getDigestIndex(motofanChatId, postId);
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
	                     @CookieValue(value = "lang", defaultValue = "ru") String tag,
	                     SearchForm searchForm,
	                     GoToPageForm goToPageForm,
	                     PagerModel pager,
	                     DigestModel digest,
	                     Model model) {
		Locale lang = Locale.forLanguageTag(tag);

		setTitleAndGeneralData(model, "site.title.search", lang, searchForm);

		String find = helper.chopQuery(text);
		String member = helper.chopQuery(user);

		searchForm.setText(find);
		searchForm.setUser(member);

		int pagePosts = config.getPagePosts();

		long count;
		BotDigestUserEntity digestUser = null;
		Long userId = helper.getLong(member);
		if (userId != null) {
			digestUser = service.getDigestUserNullable(userId);
			count = service.getDigestCount(find,
				digestUser, motofanChatId);
		} else {
			count = service.getDigestCount(find, motofanChatId);
		}
		int current = getCurrentPageAndSetPagerData(model, pager,
			helper.chopQuery(page), helper.getPageCount(count, pagePosts));
		setGotoFormData(model, goToPageForm, current, find, userId);

		Page<BotDigestEntity> digestPage = (userId != null) ?
			service.getChatDigests(current, pagePosts, find, digestUser, motofanChatId) :
			service.getChatDigests(current, pagePosts, find, motofanChatId);
		digest.setTitle(helper.getMotofanTitleSearch(digestUser, find, lang));
		digest.setDescription(helper.getMotofanDescription(lang));
		digest.setDigests(helper.getPostsSearch(digestPage, current, find, service, lang));
		model.addAttribute("posts", digest);

		return "index";
	}

	@RequestMapping(path = "/language")
	public String lang(@RequestParam(name = "tag", defaultValue = "ru") String tag, HttpServletResponse response) {
		response.addCookie(new Cookie("lang", tag));

		return "redirect:/";
	}

	@RequestMapping(path = "/help")
	public String help(Model model) {
		model.addAttribute("time", System.currentTimeMillis());
		return "help";
	}

	private void setTitleAndGeneralData(Model model, String key, Locale lang, SearchForm searchForm) {
		model.addAttribute("time", System.currentTimeMillis());
		model.addAttribute("title", locale.i18nW(key, lang));
		model.addAttribute("find", searchForm);
	}

	private int getCurrentPageAndSetPagerData(Model model, PagerModel pager, String page, int pageCount) {
		int pageDeep = config.getPageDeep();
		int current = helper.getCurrentPage(page, pageCount);

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
}
