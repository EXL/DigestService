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

import org.thymeleaf.util.StringUtils;

import ru.exlmoto.digest.bot.worker.AvatarWorker;
import ru.exlmoto.digest.bot.worker.DigestWorker;
import ru.exlmoto.digest.bot.worker.MotofanWorker;
import ru.exlmoto.digest.entity.BotDigestEntity;
import ru.exlmoto.digest.entity.BotDigestUserEntity;
import ru.exlmoto.digest.entity.BotSetupEntity;
import ru.exlmoto.digest.entity.BotSubDigestEntity;
import ru.exlmoto.digest.entity.BotSubMotofanEntity;
import ru.exlmoto.digest.entity.BotSubCovidEntity;
import ru.exlmoto.digest.entity.ExchangeRateEntity;
import ru.exlmoto.digest.exchange.ExchangeService;
import ru.exlmoto.digest.service.DatabaseService;
import ru.exlmoto.digest.site.configuration.SiteConfiguration;
import ru.exlmoto.digest.site.form.ExchangeForm;
import ru.exlmoto.digest.site.form.DigestForm;
import ru.exlmoto.digest.site.form.GoToPageForm;
import ru.exlmoto.digest.site.form.SubscriberForm;
import ru.exlmoto.digest.site.form.SetupForm;
import ru.exlmoto.digest.site.form.UserForm;
import ru.exlmoto.digest.site.model.PagerModel;
import ru.exlmoto.digest.site.model.chat.Chat;
import ru.exlmoto.digest.site.model.digest.Digest;
import ru.exlmoto.digest.site.model.member.Member;
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
	private final DigestWorker digestWorker;
	private final AvatarWorker avatarWorker;
	private final MotofanWorker motofanWorker;
	private final ExchangeService exchange;
	private final SiteConfiguration config;

	final int LONG_TEXT = 100;

	@Value("${general.date-format}")
	private String dateFormat;

	public ObeyController(SiteHelper helper,
	                      DatabaseService service,
	                      FilterHelper filter,
	                      DigestWorker digestWorker,
	                      AvatarWorker avatarWorker,
	                      MotofanWorker motofanWorker,
	                      ExchangeService exchange,
	                      SiteConfiguration config) {
		this.helper = helper;
		this.service = service;
		this.filter = filter;
		this.digestWorker = digestWorker;
		this.avatarWorker = avatarWorker;
		this.motofanWorker = motofanWorker;
		this.exchange = exchange;
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
			Optional<BotDigestEntity> digestOptional = service.getDigest(digestId);
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
				log.error(String.format("BotDigestEntity is null. Please check id: '%s'", edit));
			}
		} else {
			digestForm.setUpdate(false);
			digestForm.setDate(filter.getCurrentUnixTime());
		}
		model.addAttribute("digestForm", digestForm);

		int pagePosts = config.getPagePostsAdmin();
		int pageDeep = config.getPageDeepAdmin();
		int current = helper.getCurrentPage(page);
		int pageCount = helper.getPageCount(service.getDigestCount(), pagePosts);
		if (current > pageCount) {
			current = pageCount;
		}

		Page<BotDigestEntity> digests = service.getAllDigests(current, pagePosts);
		List<Digest> digestList = new ArrayList<>();
		digests.forEach(digest -> {
			BotDigestUserEntity user = digest.getUser();
			digestList.add(new Digest(
				digest.getId(),
				user.getUsername(),
				user.getId(),
				digest.getChat(),
				filter.getDateFromTimeStamp(dateFormat, digest.getDate()),
				filter.ellipsisMiddle(digest.getDigest(), LONG_TEXT)
			));
		});

		model.addAttribute("digestList", digestList);

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
			log.error(String.format("Cannot delete post where id is null. Please check id: '%s'", id));
		}

		return "redirect:/obey";
	}

	@PostMapping(path = "/obey/edit")
	public String obeyEdit(DigestForm digestForm) {
		Long digestId = digestForm.getDigestId();
		Long userId = digestForm.getUserId();

		if (!digestForm.checkForm()) {
			log.error(String.format("Digest Form isn't valid! Parameters: '%s'.", digestForm.toString()));
			return "redirect:/obey";
		}

		BotDigestUserEntity user = service.getDigestUserNullable(userId);
		if (user == null) {
			log.error(String.format("BotDigestUserEntity is null. Please check id: '%d'", userId));
			return "redirect:/obey";
		}

		if (digestId != null) {
			Optional<BotDigestEntity> digestOptional = service.getDigest(digestId);
			if (digestOptional.isPresent()) {
				BotDigestEntity digest = digestOptional.get();

				digest.setUser(user);
				digest.setDigest(digestForm.getDigest());
				digest.setChat(digestForm.getChatId());
				digest.setMessageId(digestForm.getMessageId());
				digest.setDate(digestForm.getDate());

				service.saveDigest(digest);
			} else {
				log.error(String.format("Cannot edit BotDigestEntity. Please check id: '%d'", digestId));
			}
		} else {
			service.saveDigest(new BotDigestEntity(
				digestForm.getChatId(),
				digestForm.getDate(),
				digestForm.getMessageId(),
				digestForm.getDigest(),
				user
			));
		}

		return "redirect:/obey";
	}

	@RequestMapping(path = "/obey/shredder")
	public String obeyShredder() {
		digestWorker.obsoleteDataShredder();

		return "redirect:/obey";
	}

	@RequestMapping(path = "/obey/user")
	public String obeyUser(@RequestParam(name = "edit", required = false) String edit,
	                       UserForm userForm,
	                       Model model) {
		model.addAttribute("time", System.currentTimeMillis());

		Long userId = helper.getLong(edit);
		if (userId != null) {
			BotDigestUserEntity user = service.getDigestUserNullable(userId);
			if (user != null) {
				userForm.setUpdate(true);

				userForm.setId(user.getId());
				userForm.setAvatar(user.getAvatar());
				userForm.setUsername(user.getUsername());
			} else {
				log.error(String.format("BotDigestUserEntity is null. Please check id: '%s'", edit));
			}
		} else {
			userForm.setUpdate(false);
		}
		model.addAttribute("userForm", userForm);

		List<BotDigestUserEntity> users = service.getAllDigestUsers();
		helper.sortUserList(null, false, service, users);

		List<Member> userList = new ArrayList<>();
		users.forEach(user -> {
			String avatarLink = user.getAvatar();
			userList.add(new Member(
				user.getId(),
				activateAvatarLink(avatarLink, filter.ellipsisMiddle(avatarLink, LONG_TEXT)),
				user.getUsername()
			));
		});

		model.addAttribute("userList", userList);

		return "obey";
	}

	@RequestMapping(path = "/obey/user/delete/{id}")
	public String obeyUserDelete(@PathVariable(name = "id") String id) {
		Long userId = helper.getLong(id);
		if (userId != null) {
			service.deleteDigestUser(userId);
		} else {
			log.error(String.format("Cannot delete user where id is null. Please check id: '%s'", id));
		}

		return "redirect:/obey/user";
	}

	@PostMapping(path = "/obey/user/edit")
	public String obeyUserEdit(UserForm userForm) {
		if (!userForm.checkForm()) {
			log.error(String.format("Digest User Form isn't valid! Parameters: '%s'.", userForm.toString()));
			return "redirect:/obey/user";
		}

		Long userId = userForm.getId();
		if (userId == null) {
			log.error("User ID is null!");
			return "redirect:/obey/user";
		}

		BotDigestUserEntity user = service.getDigestUserNullable(userId);
		if (user != null) {
			saveDigestUser(user, userForm);
		} else {
			saveDigestUser(new BotDigestUserEntity(userId), userForm);
		}

		return "redirect:/obey/user";
	}

	@RequestMapping(path = "/obey/user/avatar")
	public String obeyUserAvatar() {
		avatarWorker.updateUserAvatars();

		return "redirect:/obey/user";
	}

	@RequestMapping(path = "/obey/setup")
	public String obeySetup(Model model, SetupForm setup) {
		model.addAttribute("time", System.currentTimeMillis());

		Optional<BotSetupEntity> settingsOptional = service.getSettings();
		if (settingsOptional.isPresent()) {
			BotSetupEntity settings = settingsOptional.get();
			setup.setLog(settings.isLogUpdates());
			setup.setGreeting(settings.isShowGreetings());
			setup.setSilent(settings.isSilentMode());
		} else {
			log.error("BotSetupEntity is null.");
		}
		model.addAttribute("setup", setup);

		return "obey";
	}

	@PostMapping(path = "/obey/setup/edit")
	public String obeySetupEdit(SetupForm setup) {
		service.getSettings().ifPresent(settings -> {
			settings.setLogUpdates(setup.isLog());
			settings.setShowGreetings(setup.isGreeting());
			settings.setSilentMode(setup.isSilent());

			service.saveSettings(settings);
		});

		return "redirect:/obey/setup";
	}

	@RequestMapping(path = "/obey/sub-digest")
	public String obeySubDigest(SubscriberForm subscriberForm, Model model) {
		model.addAttribute("time", System.currentTimeMillis());
		model.addAttribute("sub_digest", "true");

		List<Chat> chatList = new ArrayList<>();
		service.getAllDigestSubs().forEach(sub -> chatList.add(new Chat(sub.getSubscription(), sub.getName())));
		model.addAttribute("chatList", chatList);
		subscriberForm.setShowName(true);
		model.addAttribute("subscriberForm", subscriberForm);

		return "obey";
	}

	@PostMapping(path = "/obey/sub-digest/edit")
	public String obeySubDigestEdit(SubscriberForm subscriberForm) {
		Long chatId = subscriberForm.getChatId();
		if (chatId != null) {
			service.saveDigestSub(new BotSubDigestEntity(chatId, subscriberForm.getChatName()));
		} else {
			log.error("Cannot add Digest subscriber, chatId is null.");
		}

		return "redirect:/obey/sub-digest";
	}

	@RequestMapping(path = "/obey/sub-digest/delete/{id}")
	public String obeySubDigestDelete(@PathVariable(name = "id") String id) {
		Long chatId = helper.getLong(id);
		if (chatId != null) {
			service.deleteDigestSub(chatId);
		} else {
			log.error(String.format("Cannot delete chat where id is null. Please check id: '%s'", id));
		}

		return "redirect:/obey/sub-digest";
	}

	@RequestMapping(path = "/obey/sub-motofan")
	public String obeySubMotofan(SubscriberForm subscriberForm, Model model) {
		model.addAttribute("time", System.currentTimeMillis());
		model.addAttribute("sub_motofan", "true");

		List<Chat> chatList = new ArrayList<>();
		service.getAllMotofanSubs().forEach(sub -> chatList.add(new Chat(sub.getSubscription(), sub.getName())));
		model.addAttribute("chatList", chatList);
		subscriberForm.setShowName(true);
		model.addAttribute("subscriberForm", subscriberForm);

		return "obey";
	}

	@PostMapping(path = "/obey/sub-motofan/edit")
	public String obeySubMotofanEdit(SubscriberForm subscriberForm) {
		Long chatId = subscriberForm.getChatId();
		if (chatId != null) {
			service.saveMotofanSub(new BotSubMotofanEntity(chatId, subscriberForm.getChatName()));
		} else {
			log.error("Cannot add MotoFan subscriber, chatId is null.");
		}

		return "redirect:/obey/sub-motofan";
	}

	@RequestMapping(path = "/obey/sub-motofan/delete/{id}")
	public String obeySubMotofanDelete(@PathVariable(name = "id") String id) {
		Long chatId = helper.getLong(id);
		if (chatId != null) {
			service.deleteMotofanSub(chatId);
		} else {
			log.error(String.format("Cannot delete chat where id is null. Please check id: '%s'", id));
		}

		return "redirect:/obey/sub-motofan";
	}

	@RequestMapping(path = "/obey/sub-greeting")
	public String obeySubGreeting(SubscriberForm subscriberForm, Model model) {
		model.addAttribute("time", System.currentTimeMillis());
		model.addAttribute("sub_greeting", "true");

		List<Chat> chatList = new ArrayList<>();
		service.getAllGreetingSubs().forEach(sub -> chatList.add(new Chat(sub.getIgnored(), null)));
		model.addAttribute("chatList", chatList);
		subscriberForm.setShowName(false);
		model.addAttribute("subscriberForm", subscriberForm);

		return "obey";
	}

	@PostMapping(path = "/obey/sub-greeting/edit")
	public String obeySubGreetingEdit(SubscriberForm subscriberForm) {
		Long chatId = subscriberForm.getChatId();
		if (chatId != null) {
			service.addChatToGreetingIgnores(chatId);
		} else {
			log.error("Cannot add chat to greeting ignore, chatId is null.");
		}

		return "redirect:/obey/sub-greeting";
	}

	@RequestMapping(path = "/obey/sub-greeting/delete/{id}")
	public String obeySubGreetingDelete(@PathVariable(name = "id") String id) {
		Long chatId = helper.getLong(id);
		if (chatId != null) {
			service.deleteChatFromGreetingIgnores(chatId);
		} else {
			log.error(String.format("Cannot delete chat where id is null. Please check id: '%s'", id));
		}

		return "redirect:/obey/sub-greeting";
	}

	@RequestMapping(path = "/obey/sub-covid")
	public String obeySubCovid(SubscriberForm subscriberForm, Model model) {
		model.addAttribute("time", System.currentTimeMillis());
		model.addAttribute("sub_covid", "true");

		List<Chat> chatList = new ArrayList<>();
		service.getAllCovidSubs().forEach(sub -> chatList.add(new Chat(sub.getSubscription(), sub.getName())));
		model.addAttribute("chatList", chatList);
		subscriberForm.setShowName(true);
		model.addAttribute("subscriberForm", subscriberForm);

		return "obey";
	}

	@PostMapping(path = "/obey/sub-covid/edit")
	public String obeySubCovidEdit(SubscriberForm subscriberForm) {
		Long chatId = subscriberForm.getChatId();
		if (chatId != null) {
			service.saveCovidSub(new BotSubCovidEntity(chatId, subscriberForm.getChatName()));
		} else {
			log.error("Cannot add COVID-2019 subscriber, chatId is null.");
		}

		return "redirect:/obey/sub-covid";
	}

	@RequestMapping(path = "/obey/sub-covid/delete/{id}")
	public String obeySubCovidDelete(@PathVariable(name = "id") String id) {
		Long chatId = helper.getLong(id);
		if (chatId != null) {
			service.deleteCovidSub(chatId);
		} else {
			log.error(String.format("Cannot delete chat where id is null. Please check id: '%s'", id));
		}

		return "redirect:/obey/sub-covid";
	}

	@RequestMapping(path = "/obey/exchange")
	public String obeyExchange(Model model, ExchangeForm exchange) {
		model.addAttribute("time", System.currentTimeMillis());

		service.getBankRu().ifPresent(exchange::setRub);
		service.getBankUa().ifPresent(exchange::setUah);
		service.getBankBy().ifPresent(exchange::setByn);
		service.getBankKz().ifPresent(exchange::setKzt);
		service.getMetalRu().ifPresent(exchange::setMetal);
		model.addAttribute("exchange", exchange);

		return "obey";
	}

	@PostMapping(path = "/obey/exchange/edit")
	public String obeyExchangeEdit(ExchangeForm exchange) {
		service.getBankRu().ifPresent(bankRu -> service.saveExchange(copyRateValues(exchange.getRub(), bankRu)));
		service.getBankUa().ifPresent(bankUa -> service.saveExchange(copyRateValues(exchange.getUah(), bankUa)));
		service.getBankBy().ifPresent(bankBy -> service.saveExchange(copyRateValues(exchange.getByn(), bankBy)));
		service.getBankKz().ifPresent(bankKz -> service.saveExchange(copyRateValues(exchange.getKzt(), bankKz)));
		service.getMetalRu().ifPresent(metalRu -> service.saveExchange(copyRateValues(exchange.getMetal(), metalRu)));

		return "redirect:/obey/exchange";
	}

	@RequestMapping(path = "/obey/exchange/update")
	public String obeyExchangeUpdate() {
		exchange.updateAllRates();

		return "redirect:/obey/exchange";
	}

	@RequestMapping(path = "/obey/motofan/update")
	public String obeyMotofanUpdate() {
		motofanWorker.workOnMotofanPosts();

		return "redirect:/obey";
	}
	private ExchangeRateEntity copyRateValues(ExchangeRateEntity from, ExchangeRateEntity to) {
		to.setDate(from.getDate());
		to.setUsd(from.getUsd());
		to.setEur(from.getEur());
		to.setGbp(from.getGbp());
		to.setCny(from.getCny());
		to.setRub(from.getRub());
		to.setUah(from.getUah());
		to.setByn(from.getByn());
		to.setKzt(from.getKzt());
		to.setGold(from.getGold());
		to.setSilver(from.getSilver());
		to.setPlatinum(from.getPlatinum());
		to.setPalladium(from.getPalladium());
		to.setPrev(from.getPrev());
		return to;
	}

	private void saveDigestUser(BotDigestUserEntity user, UserForm userForm) {
		user.setUsername(userForm.getUsername());
		user.setAvatar(userForm.getAvatar());

		service.saveDigestUser(user);
	}

	private String activateAvatarLink(String link, String text) {
		return StringUtils.isEmptyOrWhitespace(link) ? link :
			"<a href=\"" + link + "\" target=\"_blank\">" + text + "</a>";
	}
}
