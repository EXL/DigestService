package ru.exlmoto.digestbot.workers;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.exlmoto.digestbot.repos.IDigestUsersRepository;
import ru.exlmoto.digestbot.services.impl.AvatarService;

@Service
public class AvatarUpdater {
	private final IDigestUsersRepository mIDigestUsersRepository;
	private final AvatarService mAvatarService;

	private final Long mChatId;

	private Logger mBotLogger = null;

	@Autowired
	public AvatarUpdater(final IDigestUsersRepository aIDigestUsersRepository,
	                     final AvatarService aAvatarService,
	                     @Value("${digestbot.chat.motofan}") final Long aChatId) {
		mIDigestUsersRepository = aIDigestUsersRepository;
		mAvatarService = aAvatarService;
		mChatId = aChatId;
	}

	@Scheduled(cron = "${digestbot.avatar.updater.cron}")
	public void dropObsoleteDigests() {
		mBotLogger.info("=> Start updating user avatars.");

		mIDigestUsersRepository.findAllByUsernameOkIsTrue().forEach(iDigestUserEntity -> {
			final String lUsername = iDigestUserEntity.getUsername();
			mBotLogger.info("==> Update avatar for: @" + lUsername);
			iDigestUserEntity.setAvatarLink(mAvatarService.getAvatarUrlByUserName(lUsername).getSecond());
		});

		mBotLogger.info("=> End updating user avatars.");
	}

	public void setBotLogger(final Logger aBotLogger) {
		mBotLogger = aBotLogger;
	}
}
