package ru.exlmoto.digestbot.workers;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.exlmoto.digestbot.repos.IDigestEntriesRepository;

@Service
@Transactional
public class DigestShredder {
	private final IDigestEntriesRepository mIDigestEntriesRepository;

	public DigestShredder(final IDigestEntriesRepository aIDigestEntriesRepository) {
		mIDigestEntriesRepository = aIDigestEntriesRepository;
	}

	@Scheduled(cron = "${digestbot.digest.shredder.cron}")
	public void dropShit() {
		System.out.println("===>>> RUN");
		mIDigestEntriesRepository.deleteAllByDateIsLessThanAndChatIsNot(10000L, -1001148683293L);
		System.out.println("===>>> FINISH");
	}
}
