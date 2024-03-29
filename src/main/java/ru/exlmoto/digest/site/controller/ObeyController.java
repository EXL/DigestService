/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2021 EXL <exlmotodev@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ru.exlmoto.digest.site.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.worker.AvatarWorker;
import ru.exlmoto.digest.bot.worker.CallbackQueriesWorker;
import ru.exlmoto.digest.bot.worker.DigestWorker;
import ru.exlmoto.digest.bot.worker.MotofanWorker;
import ru.exlmoto.digest.bot.worker.CovidWorker;
import ru.exlmoto.digest.bot.worker.FlatWorker;
import ru.exlmoto.digest.bot.worker.RateWorker;
import ru.exlmoto.digest.entity.BotDigestEntity;
import ru.exlmoto.digest.entity.BotDigestUserEntity;
import ru.exlmoto.digest.entity.BotSubDigestEntity;
import ru.exlmoto.digest.entity.BotSubMotofanEntity;
import ru.exlmoto.digest.entity.BotSubCovidEntity;
import ru.exlmoto.digest.entity.BotSubCovidUaEntity;
import ru.exlmoto.digest.entity.BotSubRateEntity;
import ru.exlmoto.digest.entity.MemberEntity;
import ru.exlmoto.digest.entity.FlatSetupEntity;
import ru.exlmoto.digest.exchange.ExchangeService;
import ru.exlmoto.digest.service.DatabaseService;
import ru.exlmoto.digest.site.configuration.SiteConfiguration;
import ru.exlmoto.digest.site.form.ExchangeForm;
import ru.exlmoto.digest.site.form.DigestForm;
import ru.exlmoto.digest.site.form.GoToPageForm;
import ru.exlmoto.digest.site.form.MemberForm;
import ru.exlmoto.digest.site.form.SubscriberForm;
import ru.exlmoto.digest.site.form.SetupForm;
import ru.exlmoto.digest.site.form.UserForm;
import ru.exlmoto.digest.site.form.SendForm;
import ru.exlmoto.digest.site.form.FlatForm;
import ru.exlmoto.digest.site.model.PagerModel;
import ru.exlmoto.digest.site.model.chat.Chat;
import ru.exlmoto.digest.site.model.digest.Digest;
import ru.exlmoto.digest.site.model.member.Member;
import ru.exlmoto.digest.site.model.participant.Participant;
import ru.exlmoto.digest.site.util.SiteHelper;
import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.Role;
import ru.exlmoto.digest.util.file.ImageHelper;
import ru.exlmoto.digest.util.filter.FilterHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
	private final CallbackQueriesWorker callbackQueriesWorker;
	private final CovidWorker covidWorker;
	private final FlatWorker flatWorker;
	private final RateWorker rateWorker;
	private final ExchangeService exchange;
	private final BotSender sender;
	private final ImageHelper rest;
	private final SiteConfiguration config;
	private final BotConfiguration botConfig;

	private final int LONG_TEXT = 45;
	private final int LONG_AVATAR_URL = 100;

	@Value("${general.date-format}")
	private String dateFormat;

	@Value("${bot.motofan-chat-id}")
	private Long motofanChatId;

	public ObeyController(SiteHelper helper,
	                      DatabaseService service,
	                      FilterHelper filter,
	                      DigestWorker digestWorker,
	                      AvatarWorker avatarWorker,
	                      MotofanWorker motofanWorker,
	                      CallbackQueriesWorker callbackQueriesWorker,
	                      CovidWorker covidWorker,
	                      FlatWorker flatWorker,
	                      RateWorker rateWorker,
	                      ExchangeService exchange,
	                      BotSender sender, ImageHelper rest, SiteConfiguration config, BotConfiguration botConfig) {
		this.helper = helper;
		this.service = service;
		this.filter = filter;
		this.digestWorker = digestWorker;
		this.avatarWorker = avatarWorker;
		this.motofanWorker = motofanWorker;
		this.callbackQueriesWorker = callbackQueriesWorker;
		this.covidWorker = covidWorker;
		this.flatWorker = flatWorker;
		this.rateWorker = rateWorker;
		this.exchange = exchange;
		this.sender = sender;
		this.rest = rest;
		this.config = config;
		this.botConfig = botConfig;
	}

	@RequestMapping(path = "/obey")
	public String obey(@RequestParam(name = "page", required = false) String page,
	                   @RequestParam(name = "edit", required = false) String edit,
	                   GoToPageForm goToPageForm,
	                   DigestForm digestForm,
	                   PagerModel pager,
	                   Model model) {
		model.addAttribute("time", System.currentTimeMillis());

		int pagePosts = config.getPagePostsAdmin();
		int pageDeep = config.getPageDeepAdmin();
		int current = helper.getCurrentPage(page);
		int pageCount = helper.getPageCount(service.getDigestCount(), pagePosts);
		if (current > pageCount) {
			current = pageCount;
		}

		Page<BotDigestEntity> digests = service.getAllDigests(current, pagePosts);
		model.addAttribute("digestList", fillDigestList(digests));
		model.addAttribute("goto", fillGoToForm(goToPageForm, current));
		model.addAttribute("pager", fillPager(pager, digests, current, pageDeep));
		model.addAttribute("callbackQueriesMapSize", callbackQueriesWorker.getCallbackQueriesMapSize());
		model.addAttribute("digestForm", fillDigestForm(edit, digestForm));

		return "obey";
	}

	@RequestMapping(path = "/obey/delete/{id}")
	public String obeyDelete(@PathVariable(name = "id") String id) {
		Optional.of(helper.getLong(id)).ifPresent(service::deleteDigest);

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

		service.getDigestUser(userId).ifPresent(user -> addOrEditDigest(digestId, digestForm, user));

		return "redirect:/obey";
	}

	@RequestMapping(path = "/obey/shredder")
	public String obeyShredder() {
		digestWorker.obsoleteDataShredder();

		return "redirect:/obey";
	}

	@RequestMapping(path = "/obey/birthday")
	public String obeyBirthday() {
		motofanWorker.sendGoodMorningWithBirthdays();

		return "redirect:/obey";
	}

	@RequestMapping(path = "/obey/callback")
	public String obeyCallback() {
		callbackQueriesWorker.clearCallbackQueriesMap();

		return "redirect:/obey";
	}

	@RequestMapping(path = "/obey/user")
	public String obeyUser(@RequestParam(name = "edit", required = false) String edit,
	                       UserForm userForm,
	                       Model model) {
		model.addAttribute("time", System.currentTimeMillis());

		model.addAttribute("userForm", fillUserForm(userForm, edit));
		model.addAttribute("userList", fillUserList());

		return "obey";
	}

	@RequestMapping(path = "/obey/user/delete/{id}")
	public String obeyUserDelete(@PathVariable(name = "id") String id) {
		Optional.of(helper.getLong(id)).ifPresent(service::deleteDigestUser);

		return "redirect:/obey/user";
	}

	@PostMapping(path = "/obey/user/edit")
	public String obeyUserEdit(UserForm userForm) {
		if (!userForm.checkForm()) {
			log.error(String.format("Digest User Form isn't valid! Parameters: '%s'.", userForm.toString()));

			return "redirect:/obey/user";
		}

		Optional.of(userForm.getId()).ifPresent(userId -> addOrEditDigestUser(userId, userForm));

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

		service.getSettings().ifPresent(settings -> {
			setup.setLog(settings.isLogUpdates());
			setup.setGreeting(settings.isShowGreetings());
			setup.setBirthday(settings.isSendMotofanBirthdays());
			setup.setCaptcha(settings.isUseButtonCaptcha());
			setup.setSilent(settings.isSilentMode());
		});
		model.addAttribute("setup", setup);

		return "obey";
	}

	@PostMapping(path = "/obey/setup/edit")
	public String obeySetupEdit(SetupForm setup) {
		service.getSettings().ifPresent(settings -> {
			boolean log = setup.isLog();
			boolean greeting = setup.isGreeting();
			boolean birthday = setup.isBirthday();
			boolean captcha = setup.isCaptcha();
			boolean silent = setup.isSilent();

			botConfig.setLogUpdates(log);
			botConfig.setShowGreetings(greeting);
			botConfig.setSendMotofanBirthdays(birthday);
			botConfig.setUseButtonCaptcha(captcha);
			botConfig.setSilent(silent);

			settings.setLogUpdates(log);
			settings.setShowGreetings(greeting);
			settings.setSendMotofanBirthdays(birthday);
			settings.setUseButtonCaptcha(captcha);
			settings.setSilentMode(silent);
			service.saveSettings(settings);
		});

		return "redirect:/obey/setup";
	}

	@RequestMapping(path = "/obey/sub-digest")
	public String obeySubDigest(SubscriberForm subscriberForm, Model model) {
		model.addAttribute("time", System.currentTimeMillis());

		subscriberForm.setShowName(true);

		model.addAttribute("sub_digest", "true");
		model.addAttribute("chatList", fillSubDigestList());
		model.addAttribute("subscriberForm", subscriberForm);

		return "obey";
	}

	@PostMapping(path = "/obey/sub-digest/edit")
	public String obeySubDigestEdit(SubscriberForm subscriberForm) {
		Optional.of(subscriberForm.getChatId()).ifPresent(chatId ->
			service.saveDigestSub(new BotSubDigestEntity(chatId, subscriberForm.getChatName())));

		return "redirect:/obey/sub-digest";
	}

	@RequestMapping(path = "/obey/sub-digest/delete/{id}")
	public String obeySubDigestDelete(@PathVariable(name = "id") String id) {
		Optional.of(helper.getLong(id)).ifPresent(service::deleteDigestSub);

		return "redirect:/obey/sub-digest";
	}

	@RequestMapping(path = "/obey/sub-motofan")
	public String obeySubMotofan(SubscriberForm subscriberForm, Model model) {
		model.addAttribute("time", System.currentTimeMillis());

		subscriberForm.setShowName(true);

		model.addAttribute("sub_motofan", "true");
		model.addAttribute("chatList", fillSubMotofanList());
		model.addAttribute("subscriberForm", subscriberForm);

		return "obey";
	}

	@PostMapping(path = "/obey/sub-motofan/edit")
	public String obeySubMotofanEdit(SubscriberForm subscriberForm) {
		Optional.of(subscriberForm.getChatId()).ifPresent(chatId ->
			service.saveMotofanSub(new BotSubMotofanEntity(chatId, subscriberForm.getChatName())));

		return "redirect:/obey/sub-motofan";
	}

	@RequestMapping(path = "/obey/sub-motofan/delete/{id}")
	public String obeySubMotofanDelete(@PathVariable(name = "id") String id) {
		Optional.of(helper.getLong(id)).ifPresent(service::deleteMotofanSub);

		return "redirect:/obey/sub-motofan";
	}

	@RequestMapping(path = "/obey/motofan/update")
	public String obeyMotofanUpdate() {
		motofanWorker.workOnMotofanPosts();

		return "redirect:/obey/sub-motofan";
	}

	@RequestMapping(path = "/obey/sub-greeting")
	public String obeySubGreeting(SubscriberForm subscriberForm, Model model) {
		model.addAttribute("time", System.currentTimeMillis());

		subscriberForm.setShowName(false);

		model.addAttribute("sub_greeting", "true");
		model.addAttribute("chatList", fillSubGreetingList());
		model.addAttribute("subscriberForm", subscriberForm);

		return "obey";
	}

	@PostMapping(path = "/obey/sub-greeting/edit")
	public String obeySubGreetingEdit(SubscriberForm subscriberForm) {
		Optional.of(subscriberForm.getChatId()).ifPresent(service::addChatToGreetingIgnores);

		return "redirect:/obey/sub-greeting";
	}

	@RequestMapping(path = "/obey/sub-greeting/delete/{id}")
	public String obeySubGreetingDelete(@PathVariable(name = "id") String id) {
		Optional.of(helper.getLong(id)).ifPresent(service::deleteChatFromGreetingIgnores);

		return "redirect:/obey/sub-greeting";
	}

	@RequestMapping(path = "/obey/sub-covid")
	public String obeySubCovid(SubscriberForm subscriberForm, Model model) {
		model.addAttribute("time", System.currentTimeMillis());

		subscriberForm.setShowName(true);

		model.addAttribute("sub_covid", "true");
		model.addAttribute("chatList", fillSubCovidList());
		model.addAttribute("subscriberForm", subscriberForm);

		return "obey";
	}

	@PostMapping(path = "/obey/sub-covid/edit")
	public String obeySubCovidEdit(SubscriberForm subscriberForm) {
		Optional.of(subscriberForm.getChatId()).ifPresent(chatId ->
			service.saveCovidSub(new BotSubCovidEntity(chatId, subscriberForm.getChatName())));

		return "redirect:/obey/sub-covid";
	}

	@RequestMapping(path = "/obey/sub-covid/delete/{id}")
	public String obeySubCovidDelete(@PathVariable(name = "id") String id) {
		Optional.of(helper.getLong(id)).ifPresent(service::deleteCovidSub);

		return "redirect:/obey/sub-covid";
	}

	@RequestMapping(path = "/obey/covid/send")
	public String obeyCovidSend() {
		covidWorker.workOnCovidReport();

		return "redirect:/obey/sub-covid";
	}

	@RequestMapping(path = "/obey/sub-covid-ua")
	public String obeySubCovidUa(SubscriberForm subscriberForm, Model model) {
		model.addAttribute("time", System.currentTimeMillis());

		subscriberForm.setShowName(true);

		model.addAttribute("sub_covid_ua", "true");
		model.addAttribute("chatList", fillSubCovidUaList());
		model.addAttribute("subscriberForm", subscriberForm);

		return "obey";
	}

	@PostMapping(path = "/obey/sub-covid-ua/edit")
	public String obeySubCovidUaEdit(SubscriberForm subscriberForm) {
		Optional.of(subscriberForm.getChatId()).ifPresent(chatId ->
			service.saveCovidUaSub(new BotSubCovidUaEntity(chatId, subscriberForm.getChatName())));

		return "redirect:/obey/sub-covid-ua";
	}

	@RequestMapping(path = "/obey/sub-covid-ua/delete/{id}")
	public String obeySubCovidUaDelete(@PathVariable(name = "id") String id) {
		Optional.of(helper.getLong(id)).ifPresent(service::deleteCovidUaSub);

		return "redirect:/obey/sub-covid-ua";
	}

	@RequestMapping(path = "/obey/sub-rate")
	public String obeySubRate(SubscriberForm subscriberForm, Model model) {
		model.addAttribute("time", System.currentTimeMillis());

		subscriberForm.setShowName(true);

		model.addAttribute("sub_rate", "true");
		model.addAttribute("chatList", fillSubRateList());
		model.addAttribute("subscriberForm", subscriberForm);

		return "obey";
	}

	@PostMapping(path = "/obey/sub-rate/edit")
	public String obeySubRateEdit(SubscriberForm subscriberForm) {
		Optional.of(subscriberForm.getChatId()).ifPresent(chatId ->
			service.saveRateSub(new BotSubRateEntity(chatId, subscriberForm.getChatName())));

		return "redirect:/obey/sub-rate";
	}

	@RequestMapping(path = "/obey/sub-rate/delete/{id}")
	public String obeySubRateDelete(@PathVariable(name = "id") String id) {
		Optional.of(helper.getLong(id)).ifPresent(service::deleteRateSub);

		return "redirect:/obey/sub-rate";
	}

	@RequestMapping(path = "/obey/exchange")
	public String obeyExchange(Model model, ExchangeForm exchange) {
		model.addAttribute("time", System.currentTimeMillis());

		service.getBankRu().ifPresent(exchange::setRub);
		service.getBankUa().ifPresent(exchange::setUah);
		service.getBankBy().ifPresent(exchange::setByn);
		service.getBankKz().ifPresent(exchange::setKzt);
		service.getMetalRu().ifPresent(exchange::setMetal);
		service.getBitcoin().ifPresent(exchange::setBitcoin);
		model.addAttribute("exchange", exchange);

		return "obey";
	}

	@PostMapping(path = "/obey/exchange/edit")
	public String obeyExchangeEdit(ExchangeForm exchange) {
		service.getBankRu().ifPresent(bankRu -> service.saveExchange(helper.copyRateValues(exchange.getRub(), bankRu)));
		service.getBankUa().ifPresent(bankUa -> service.saveExchange(helper.copyRateValues(exchange.getUah(), bankUa)));
		service.getBankBy().ifPresent(bankBy -> service.saveExchange(helper.copyRateValues(exchange.getByn(), bankBy)));
		service.getBankKz().ifPresent(bankKz -> service.saveExchange(helper.copyRateValues(exchange.getKzt(), bankKz)));
		service.getMetalRu().ifPresent(metalRu ->
			service.saveExchange(helper.copyRateValues(exchange.getMetal(), metalRu)));
		service.getBitcoin().ifPresent(bitcoin ->
			service.saveExchange(helper.copyRateValues(exchange.getBitcoin(), bitcoin)));

		return "redirect:/obey/exchange";
	}

	@RequestMapping(path = "/obey/exchange/update")
	public String obeyExchangeUpdate() {
		exchange.updateAllRates();

		return "redirect:/obey/exchange";
	}

	@RequestMapping(path = "/obey/exchange/send")
	public String obeyExchangeSend() {
		rateWorker.sendExchangeRatesToSubs();

		return "redirect:/obey/sub-rate";
	}

	@RequestMapping(path = "/obey/send")
	public String obeySend(Model model, SendForm send) {
		model.addAttribute("time", System.currentTimeMillis());

		send.setSendChatId(motofanChatId);
		send.setStickerChatId(motofanChatId);
		send.setImageChatId(motofanChatId);
		model.addAttribute("send", send);

		return "obey";
	}

	@PostMapping(path = "/obey/send/chat")
	public String obeySendChat(SendForm send) {
		if (send.checkSend()) {
			sender.sendSimpleToChat(send.getSendChatId(), send.getSendChatArg());
		}
		if (send.checkSticker()) {
			sender.sendStickerToChat(send.getStickerChatId(), send.getStickerChatArg());
		}
		if (send.checkImage()) {
			String imageLink = send.getImageChatArg();
			Answer<String> imageRes = rest.getImageByLink(imageLink);
			if (imageRes.ok()) {
				sender.sendPhotoToChat(send.getImageChatId(), imageRes.answer());
			} else {
				log.error(String.format("Cannot get image by link '%s', reason: '%s'.", imageLink, imageRes.error()));
			}
		}

		return "redirect:/obey/send";
	}

	@RequestMapping(path = "/obey/member")
	public String obeyMember(@RequestParam(name = "edit", required = false) String edit,
	                         MemberForm memberForm,
	                         Authentication authentication,
	                         Model model) {
		model.addAttribute("time", System.currentTimeMillis());

		boolean isAuthorizedForChanging = isUserOwner(authentication);

		if (isAuthorizedForChanging) {
			model.addAttribute("memberList", fillMemberList());
			model.addAttribute("memberForm", fillMemberForm(memberForm, edit));
			model.addAttribute("roleList", Arrays.asList(Role.values()));
		}
		model.addAttribute("owner", isAuthorizedForChanging);

		return "obey";
	}

	@PostMapping(path = "/obey/member/edit")
	public String obeyMemberEdit(MemberForm memberForm, Authentication authentication) {
		if (!isUserOwner(authentication)) {
			log.error(String.format("Access denied for non owner (member-edit)! User: '%s'", authentication.getName()));

			return "redirect:/obey";
		}
		if (!memberForm.checkForm()) {
			log.error(String.format("Member Form isn't valid! Parameters: '%s'.", memberForm.toString()));

			return "redirect:/obey/member";
		}

		addOrEditMember(memberForm.getId(), memberForm);

		return "redirect:/obey/member";
	}

	@RequestMapping(path = "/obey/member/delete/{id}")
	public String obeyMemberDelete(@PathVariable(name = "id") String id, Authentication authentication) {
		if (!isUserOwner(authentication)) {
			log.error(String.format("Access denied for non owner! User: '%s'", authentication.getName()));

			return "redirect:/obey";
		}

		Optional.of(helper.getLong(id)).ifPresent(service::deleteMember);

		return "redirect:/obey/member";
	}

	@RequestMapping(path = "/obey/flat")
	public String obeyFlat(Model model, FlatForm flat, Authentication authentication) {
		model.addAttribute("time", System.currentTimeMillis());

		boolean isAuthorizedForChanging = isUserOwner(authentication);
		if (isAuthorizedForChanging) {
			service.getFlatSettings().ifPresent(settings -> {
				flat.setMaxVariants(settings.getMaxVariants());
				flat.setSubscriberIds(settings.getSubscribeIds());
				flat.setApiCianUrl(settings.getApiCianUrl());
				flat.setApiN1Url(settings.getApiN1Url());
				flat.setViewCianUrl(settings.getViewCianUrl());
				flat.setViewN1Url(settings.getViewN1Url());
			});
			model.addAttribute("flat", flat);
		}
		model.addAttribute("owner", isAuthorizedForChanging);

		return "obey";
	}

	@PostMapping(path = "/obey/flat/edit")
	public String obeyFlatEdit(FlatForm flat, Authentication authentication) {
		if (!isUserOwner(authentication)) {
			log.error(String.format("Access denied for non owner (flat-edit)! User: '%s'", authentication.getName()));

			return "redirect:/obey";
		}

		saveOrUpdateFlatSettings(flat);

		return "redirect:/obey/flat";
	}

	@RequestMapping(path = "/obey/flat/send")
	public String obeyFlatSend(Authentication authentication) {
		if (!isUserOwner(authentication)) {
			log.error(String.format("Access denied for non owner (flat-send)! User: '%s'", authentication.getName()));

			return "redirect:/obey";
		}
		flatWorker.workOnFlatReport();

		return "redirect:/obey/flat";
	}

	private DigestForm fillDigestForm(String edit, DigestForm digestForm) {
		Long digestId = helper.getLong(edit);
		if (digestId != null) {
			service.getDigest(digestId).ifPresent(digest -> {
				digestForm.setUpdate(true);
				digestForm.setDigestId(digest.getId());
				digestForm.setChatId(digest.getChat());
				digestForm.setDate(digest.getDate());
				digestForm.setMessageId(digest.getMessageId());
				digestForm.setDigest(digest.getDigest());
				digestForm.setUserId(digest.getUser().getId());
			});
		} else {
			digestForm.setUpdate(false);
			digestForm.setDate(filter.getCurrentUnixTime());
		}
		return digestForm;
	}

	private List<Digest> fillDigestList(Page<BotDigestEntity> digests) {
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
		return digestList;
	}

	private PagerModel fillPager(PagerModel pager, Page<BotDigestEntity> digests, int current, int pageDeep) {
		pager.setCurrent(current);
		pager.setAll(digests.getTotalPages());
		pager.setStartAux(current - ((pageDeep / 2) + 1));
		pager.setEndAux(current + (pageDeep / 2));
		return pager;
	}

	private GoToPageForm fillGoToForm(GoToPageForm goToPageForm, int current) {
		goToPageForm.setPage(String.valueOf(current));
		return goToPageForm;
	}

	private void addOrEditDigest(Long digestId, DigestForm digestForm, BotDigestUserEntity user) {
		if (digestId != null) {
			service.getDigest(digestId).ifPresent(digest -> saveDigest(digest, digestForm, user));
		} else {
			saveDigest(new BotDigestEntity(), digestForm, user);
		}
	}

	private void saveDigest(BotDigestEntity digest, DigestForm digestForm, BotDigestUserEntity user) {
		digest.setUser(user);
		digest.setDigest(digestForm.getDigest());
		digest.setChat(digestForm.getChatId());
		digest.setMessageId(digestForm.getMessageId());
		digest.setDate(digestForm.getDate());
		service.saveDigest(digest);
	}

	private UserForm fillUserForm(UserForm userForm, String edit) {
		Long userId = helper.getLong(edit);
		if (userId != null) {
			service.getDigestUser(userId).ifPresent(user -> {
				userForm.setUpdate(true);
				userForm.setId(user.getId());
				userForm.setAvatar(user.getAvatar());
				userForm.setUsername(user.getUsername());
			});
		} else {
			userForm.setUpdate(false);
		}
		return userForm;
	}

	private List<Member> fillUserList() {
		List<BotDigestUserEntity> users = service.getAllDigestUsers();
		helper.sortUserList(null, false, service, users);

		List<Member> userList = new ArrayList<>();
		users.forEach(user -> {
			String avatarLink = user.getAvatar();
			userList.add(new Member(
				user.getId(),
				filter.activateLink(avatarLink, filter.ellipsisMiddle(avatarLink, LONG_AVATAR_URL)),
				user.getUsername()
			));
		});
		return userList;
	}

	private void addOrEditDigestUser(Long userId, UserForm userForm) {
		BotDigestUserEntity user = service.getDigestUserNullable(userId);
		if (user != null) {
			saveDigestUser(user, userForm);
		} else {
			saveDigestUser(new BotDigestUserEntity(userId), userForm);
		}
	}

	private void saveDigestUser(BotDigestUserEntity user, UserForm userForm) {
		user.setUsername(userForm.getUsername());
		user.setAvatar(userForm.getAvatar());
		service.saveDigestUser(user);
	}

	private List<Chat> fillSubDigestList() {
		List<Chat> chatList = new ArrayList<>();
		service.getAllDigestSubs().forEach(sub -> chatList.add(new Chat(sub.getSubscription(), sub.getName())));
		return chatList;
	}

	private List<Chat> fillSubMotofanList() {
		List<Chat> chatList = new ArrayList<>();
		service.getAllMotofanSubs().forEach(sub -> chatList.add(new Chat(sub.getSubscription(), sub.getName())));
		return chatList;
	}

	private List<Chat> fillSubGreetingList() {
		List<Chat> chatList = new ArrayList<>();
		service.getAllGreetingSubs().forEach(sub -> chatList.add(new Chat(sub.getIgnored(), null)));
		return chatList;
	}

	private List<Chat> fillSubCovidList() {
		List<Chat> chatList = new ArrayList<>();
		service.getAllCovidSubs().forEach(sub -> chatList.add(new Chat(sub.getSubscription(), sub.getName())));
		return chatList;
	}

	private List<Chat> fillSubCovidUaList() {
		List<Chat> chatList = new ArrayList<>();
		service.getAllCovidUaSubs().forEach(sub -> chatList.add(new Chat(sub.getSubscription(), sub.getName())));
		return chatList;
	}

	private List<Chat> fillSubRateList() {
		List<Chat> chatList = new ArrayList<>();
		service.getAllRateSubs().forEach(sub -> chatList.add(new Chat(sub.getSubscription(), sub.getName())));
		return chatList;
	}

	private List<Participant> fillMemberList() {
		List<Participant> participantList = new ArrayList<>();
		service.getAllMembers().forEach(member -> participantList.add(
			new Participant(member.getId(), member.getUsername(), member.getRole(), member.isEnable()))
		);
		participantList.sort(Comparator.comparing(Participant::getId));
		return participantList;
	}

	private void addOrEditMember(Long memberId, MemberForm memberForm) {
		MemberEntity member = service.getMember(memberId);

		String username = memberForm.getUsername();
		String password = memberForm.getPassword();
		Role role = memberForm.getRole();
		boolean enabled = memberForm.isEnabled();

		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

		if (member != null) {
			member.setUsername(username);
			member.setPassword(passwordEncoder.encode(password));
			member.setRole(role);
			member.setEnable(enabled);
			service.saveMember(member);
		} else {
			if (isNameUnique(username)) {
				service.saveMember(new MemberEntity(username, passwordEncoder.encode(password), role, enabled));
			}
		}
	}

	private boolean isNameUnique(String username) {
		List<MemberEntity> members = service.getAllMembers();
		for (MemberEntity member : members) {
			if (member.getUsername().equals(username)) {
				log.error(String.format("Username '%s' isn't unique!", username));
				return false;
			}
		}
		return true;
	}

	private MemberForm fillMemberForm(MemberForm memberForm, String edit) {
		Long memberId = helper.getLong(edit);
		// Enable member by default.
		memberForm.setEnabled(true);
		if (memberId != null) {
			MemberEntity member = service.getMember(memberId);
			if (member != null) {
				memberForm.setUpdate(true);
				memberForm.setId(member.getId());
				memberForm.setUsername(member.getUsername());
				// Drop filling password hash.
				// memberForm.setPassword(member.getPassword());
				memberForm.setRole(member.getRole());
				memberForm.setEnabled(member.isEnable());
			}
		} else {
			memberForm.setUpdate(false);
		}
		return memberForm;
	}

	private boolean isUserOwner(Authentication authentication) {
		return authentication.getAuthorities().contains(new SimpleGrantedAuthority(Role.Owner.name()));
	}

	private void saveOrUpdateFlatSettings(FlatForm flat) {
		Optional<FlatSetupEntity> settingsOptional = service.getFlatSettings();
		if (settingsOptional.isPresent()) {
			fillFlatSettings(settingsOptional.get(), flat);
		} else {
			fillFlatSettings(new FlatSetupEntity(FlatSetupEntity.FLAT_ROW), flat);
		}
	}

	private void fillFlatSettings(FlatSetupEntity settings, FlatForm flat) {
		settings.setMaxVariants(flat.getMaxVariants());
		settings.setSubscribeIds(filter.strip(flat.getSubscriberIds()));
		settings.setApiCianUrl(filter.strip(flat.getApiCianUrl()));
		settings.setApiN1Url(filter.strip(flat.getApiN1Url()));
		settings.setViewCianUrl(filter.strip(flat.getViewCianUrl()));
		settings.setViewN1Url(filter.strip(flat.getViewN1Url()));
		service.saveFlatSettings(settings);
	}
}
