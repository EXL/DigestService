/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2020 EXL <exlmotodev@gmail.com>
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

package ru.exlmoto.digest.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import ru.exlmoto.digest.entity.BotDigestEntity;
import ru.exlmoto.digest.entity.BotDigestUserEntity;
import ru.exlmoto.digest.entity.BotSubDigestEntity;
import ru.exlmoto.digest.entity.BotSubMotofanEntity;
import ru.exlmoto.digest.entity.BotSubGreetingEntity;
import ru.exlmoto.digest.entity.BotSubCovidEntity;
import ru.exlmoto.digest.entity.BotSetupEntity;
import ru.exlmoto.digest.entity.ExchangeRateEntity;
import ru.exlmoto.digest.entity.MemberEntity;
import ru.exlmoto.digest.entity.FlatSetupEntity;
import ru.exlmoto.digest.repository.BotDigestRepository;
import ru.exlmoto.digest.repository.BotDigestUserRepository;
import ru.exlmoto.digest.repository.BotSubDigestRepository;
import ru.exlmoto.digest.repository.BotSubMotofanRepository;
import ru.exlmoto.digest.repository.BotSubGreetingRepository;
import ru.exlmoto.digest.repository.BotSubCovidRepository;
import ru.exlmoto.digest.repository.BotSetupRepository;
import ru.exlmoto.digest.repository.ExchangeRateRepository;
import ru.exlmoto.digest.repository.MemberRepository;
import ru.exlmoto.digest.repository.FlatSetupRepository;

import java.util.List;
import java.util.Optional;

@Service
public class DatabaseService {
	private final Logger log = LoggerFactory.getLogger(DatabaseService.class);

	private final BotDigestRepository digestRepository;
	private final BotDigestUserRepository digestUserRepository;
	private final BotSubMotofanRepository subMotofanRepository;
	private final BotSubDigestRepository subDigestRepository;
	private final BotSubGreetingRepository subGreetingRepository;
	private final BotSubCovidRepository subCovidRepository;
	private final BotSetupRepository setupRepository;
	private final ExchangeRateRepository exchangeRateRepository;
	private final MemberRepository memberRepository;
	private final FlatSetupRepository flatSetupRepository;

	public DatabaseService(BotDigestRepository digestRepository,
	                       BotDigestUserRepository digestUserRepository,
	                       BotSubMotofanRepository subMotofanRepository,
	                       BotSubDigestRepository subDigestRepository,
	                       BotSubGreetingRepository subGreetingRepository,
	                       BotSubCovidRepository subCovidRepository,
	                       BotSetupRepository setupRepository,
	                       ExchangeRateRepository exchangeRateRepository,
	                       MemberRepository memberRepository,
	                       FlatSetupRepository flatSetupRepository) {
		this.digestRepository = digestRepository;
		this.digestUserRepository = digestUserRepository;
		this.subMotofanRepository = subMotofanRepository;
		this.subDigestRepository = subDigestRepository;
		this.subGreetingRepository = subGreetingRepository;
		this.subCovidRepository = subCovidRepository;
		this.setupRepository = setupRepository;
		this.exchangeRateRepository = exchangeRateRepository;
		this.memberRepository = memberRepository;
		this.flatSetupRepository = flatSetupRepository;
	}

	public Optional<BotDigestEntity> getDigest(long id) {
		return digestRepository.findById(id);
	}

	public Page<BotDigestEntity> getAllDigests(int page, int size) {
		return digestRepository.findAll(PageRequest.of(page - 1, size, Sort.by(Sort.Order.desc("id"))));
	}

	public Page<BotDigestEntity> getChatDigestsCommand(int page, int size, long chatId) {
		return getChatDigests(page - 1, size, Sort.by(Sort.Order.desc("id")), chatId);
	}

	public Page<BotDigestEntity> getChatDigestsSite(int page, int size, long chatId) {
		return getChatDigests(page - 1, size, Sort.by(Sort.Order.asc("id")), chatId);
	}

	private Page<BotDigestEntity> getChatDigests(int page, int size, Sort sort, long chatId) {
		return digestRepository.findBotDigestEntitiesByChat(PageRequest.of(page, size, sort), chatId);
	}

	public Page<BotDigestEntity> getChatDigests(int page, int size, String find, long chatId) {
		return digestRepository.findBotDigestEntitiesByChat(
			PageRequest.of(page - 1, size, Sort.by(Sort.Order.asc("id"))), find, chatId
		);
	}

	public Page<BotDigestEntity> getChatDigests(int page,
	                                            int size,
	                                            String find,
	                                            BotDigestUserEntity user,
	                                            long chatId) {
		return digestRepository.findBotDigestEntitiesByChat(
			PageRequest.of(page - 1, size, Sort.by(Sort.Order.asc("id"))), find, user, chatId
		);
	}

	public void dropObsoleteDigests(long date, long chatId) {
		digestRepository.dropObsoleteDigests(date, chatId);
	}

	public List<Long> getAllUserIds() {
		return digestRepository.allUserIds();
	}

	public List<BotDigestUserEntity> getAllUsersByChat(Long chatId) {
		return digestRepository.allUsersByChat(chatId);
	}

	public void deleteDigest(long digestId) {
		digestRepository.deleteById(digestId);
	}

	public void saveDigest(BotDigestEntity digest) {
		digestRepository.save(digest);
	}

	public long getDigestCount(BotDigestUserEntity user, long chatId) {
		return digestRepository.countBotDigestEntitiesByChat(user, chatId);
	}

	public long getDigestCount(long chatId) {
		return digestRepository.countBotDigestEntitiesByChat(chatId);
	}

	public long getDigestCount(String find, long chatId) {
		return digestRepository.countBotDigestEntitiesByChat(find, chatId);
	}

	public long getDigestCount(String find, BotDigestUserEntity user, long chatId) {
		return digestRepository.countBotDigestEntitiesByChat(find, user, chatId);
	}

	public long getDigestCount() {
		return digestRepository.count();
	}

	public int getDigestIndex(long chatId, long postId) {
		return digestRepository.findBotDigestEntitiesByChat(Sort.by(Sort.Order.asc("id")), chatId)
			.indexOf(new BotDigestEntity(postId));
	}

	public List<BotDigestUserEntity> getAllDigestUsers() {
		return digestUserRepository.findAll();
	}

	public void deleteDigestUser(long userId) {
		digestUserRepository.deleteById(userId);
	}

	public List<BotDigestUserEntity> getDigestUsersWithUsername() {
		return digestUserRepository.findUsersWithUsername();
	}

	public void saveDigestUser(BotDigestUserEntity digestUser) {
		digestUserRepository.save(digestUser);
	}

	public Optional<BotDigestUserEntity> getDigestUser(long userId) {
		return digestUserRepository.findById(userId);
	}

	public BotDigestUserEntity getDigestUserNullable(long userId) {
		return digestUserRepository.getBotDigestUserEntityById(userId);
	}

	public BotDigestUserEntity getDigestUserNullable(String usernameWithAt) {
		return digestUserRepository.getBotDigestUserEntityByUsername(usernameWithAt);
	}

	public List<BotSubMotofanEntity> getAllMotofanSubs() {
		return subMotofanRepository.findAll();
	}

	public BotSubMotofanEntity getMotofanSub(long subscription) {
		return subMotofanRepository.findBotSubMotofanEntityBySubscription(subscription);
	}

	public void deleteMotofanSub(long subscription) {
		subMotofanRepository.deleteBotSubMotofanEntityBySubscription(subscription);
	}

	public void saveMotofanSub(BotSubMotofanEntity motofanSub) {
		subMotofanRepository.save(motofanSub);
	}

	public List<BotSubDigestEntity> getAllDigestSubs() {
		return subDigestRepository.findAll();
	}

	public BotSubDigestEntity getDigestSub(long subscription) {
		return subDigestRepository.findBotSubDigestEntityBySubscription(subscription);
	}

	public void deleteDigestSub(long subscription) {
		subDigestRepository.deleteBotSubDigestEntityBySubscription(subscription);
	}

	public void saveDigestSub(BotSubDigestEntity digestSub) {
		subDigestRepository.save(digestSub);
	}

	public List<BotSubGreetingEntity> getAllGreetingSubs() {
		return subGreetingRepository.findAll();
	}

	public boolean checkGreeting(long chatId) {
		return subGreetingRepository.findBotSubGreetingEntityByIgnored(chatId) == null;
	}

	public void deleteChatFromGreetingIgnores(long chatId) {
		subGreetingRepository.deleteBotSubGreetingEntityByIgnored(chatId);
	}

	public void addChatToGreetingIgnores(long chatId) {
		subGreetingRepository.save(new BotSubGreetingEntity(chatId));
	}

	public List<BotSubCovidEntity> getAllCovidSubs() {
		return subCovidRepository.findAll();
	}

	public BotSubCovidEntity getCovidSub(long subscription) {
		return subCovidRepository.findBotSubCovidEntityBySubscription(subscription);
	}

	public void deleteCovidSub(long subscription) {
		subCovidRepository.deleteBotSubCovidEntityBySubscription(subscription);
	}

	public void saveCovidSub(BotSubCovidEntity covidSub) {
		subCovidRepository.save(covidSub);
	}

	public Optional<BotSetupEntity> getSettings() {
		return setupRepository.getSetupBot();
	}

	public void saveSettings(BotSetupEntity settings) {
		setupRepository.save(settings);
	}

	public Optional<ExchangeRateEntity> getBankRu() {
		return exchangeRateRepository.getBankRu();
	}

	public Optional<ExchangeRateEntity> getBankUa() {
		return exchangeRateRepository.getBankUa();
	}

	public Optional<ExchangeRateEntity> getBankBy() {
		return exchangeRateRepository.getBankBy();
	}

	public Optional<ExchangeRateEntity> getBankKz() {
		return exchangeRateRepository.getBankKz();
	}

	public Optional<ExchangeRateEntity> getMetalRu() {
		return exchangeRateRepository.getMetalRu();
	}

	public void saveExchange(ExchangeRateEntity rate) {
		if (rate != null) {
			exchangeRateRepository.save(rate);
		} else {
			log.error("Cannot save ExchangeRateEntity, because rate is null.");
		}
	}

	public List<MemberEntity> getAllMembers() {
		return memberRepository.findAll();
	}

	public MemberEntity getMember(Long id) {
		return (id != null) ? memberRepository.findMemberEntityById(id) : null;
	}

	public MemberEntity getMember(String username) {
		return (StringUtils.hasText(username)) ? memberRepository.findMemberEntityByUsername(username) : null;
	}

	public void deleteMember(long id) {
		memberRepository.deleteMemberEntityById(id);
	}

	public void saveMember(MemberEntity member) {
		memberRepository.save(member);
	}

	public Optional<FlatSetupEntity> getFlatSettings() {
		return flatSetupRepository.getSetupFlat();
	}

	public void saveFlatSettings(FlatSetupEntity settings) {
		flatSetupRepository.save(settings);
	}
}
