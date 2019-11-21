package ru.exlmoto.digestbot.workers;

import org.slf4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.exlmoto.digestbot.repos.IDigestEntriesRepository;

@Service
@Transactional
public class DigestShredder {
	private final IDigestEntriesRepository mIDigestEntriesRepository;

	private final Long mChatId;

	private Logger mBotLogger = null;

	@Autowired
	public DigestShredder(final IDigestEntriesRepository aIDigestEntriesRepository,
	                      @Value("${digestbot.chat.motofan}") final Long aChatId) {
		mIDigestEntriesRepository = aIDigestEntriesRepository;
		mChatId = aChatId;
	}

	// Digest delay.
	// 45 sec for debug.
	// 43 200 for 12-hours.
	// 86 400 for 24-hours.
	// 172 800 for 48-hours.
	// 604 800 for a week.
	// https://stackoverflow.com/a/732043
	@Scheduled(cron = "${digestbot.digest.shredder.cron}")
	public void dropObsoleteDigests() {
		mBotLogger.info("=> Start dropping obsolete digest from database.");
		mIDigestEntriesRepository.deleteAllByDateIsLessThanAndChatIsNot(
				(System.currentTimeMillis() / 1000L) - (604800 + 43200), mChatId);
		mBotLogger.info("=> End dropping obsolete digest from database.");
	}

	public void setBotLogger(final Logger aBotLogger) {
		mBotLogger = aBotLogger;
	}
}
