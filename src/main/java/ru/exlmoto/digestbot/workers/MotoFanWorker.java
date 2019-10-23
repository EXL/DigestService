package ru.exlmoto.digestbot.workers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.services.impl.MotoFanService;
import ru.exlmoto.digestbot.utils.MotoFanPost;
import ru.exlmoto.digestbot.yaml.impl.YamlLocalizationHelper;

import java.io.IOException;
import java.util.ArrayList;

@Service
public class MotoFanWorker {
	private final MotoFanService mMotoFanService;

	private DigestBot mDigestBot = null;
	private Logger mBotLogger = null;
	private YamlLocalizationHelper mYamlLocalizationHelper = null;

	private Long mLatestPostTime = 0L;
	private Integer mLatestTopicId = 0;
	private String mLatestPostAuthor = null;
	private String mLatestPostText = null;

	@Autowired
	public MotoFanWorker(final MotoFanService aMotoFanService) {
		mMotoFanService = aMotoFanService;
	}

	public void setDigestBot(final DigestBot aDigestBot) {
		mDigestBot = aDigestBot;
		mBotLogger = aDigestBot.getBotLogger();
		mYamlLocalizationHelper = mDigestBot.getLocalizationHelper();
	}

	@Scheduled(cron = "${digestbot.crawler.motofan.cron}")
	public void updateLatestMotoFanPosts() {
		mBotLogger.info("=> Crawling MotoFan.Ru latest posts.");
		final Pair<Boolean, String> lServerAnswer = mMotoFanService.receiveObject();
		if (!updatePostsFromJson(lServerAnswer)) {
			mBotLogger.error(String.format("=> Error crawling MotoFan.Ru latest posts: '%s'.", lServerAnswer.getSecond()));
		} else {
			mBotLogger.info("=> End crawling MotoFan.Ru latest posts.");
		}
	}

	private boolean updatePostsFromJson(final Pair<Boolean, String> aServerAnswer) {
		if (!aServerAnswer.getFirst()) {
			return false;
		}
		final String lServerAnswer = aServerAnswer.getSecond();
		if (!lServerAnswer.startsWith("[")) {
			return false;
		}

		final ObjectMapper lObjectMapper = new ObjectMapper();
		try {
			sendLatestMessages(processMotoFanPostArray(lObjectMapper.readValue(lServerAnswer, MotoFanPost[].class)));
		} catch (IOException e) {
			mBotLogger.error(String.format("Cannot parse JSON, error:'%s'.", e.toString()));
			return false;
		}
		return true;
	}

	private ArrayList<MotoFanPost> processMotoFanPostArray(final MotoFanPost[] aMotoFanPosts) {
		final ArrayList<MotoFanPost> lMotoFanPosts = new ArrayList<>();
		final int lArraySize = aMotoFanPosts.length;
		if (mLatestPostTime == 0L) {
			setLatestVariables(aMotoFanPosts, 0);
		} else {
			for (int i = lArraySize - 1; i >= 0; --i) {
				final Long lTimeStamp = aMotoFanPosts[i].getTimestamp();
				final String lAuthor = aMotoFanPosts[i].getAuthor();
				final String lText = aMotoFanPosts[i].getText();
				final Integer lTopicId = aMotoFanPosts[i].getTopic();
				if (lTimeStamp > mLatestPostTime) {
					// HACK: Drop automatic congratulation forum messages.
					if (!lAuthor.equals("palach")) {
						// HACK: Drop "added later" messages.
						if (!lAuthor.equals(mLatestPostAuthor) ||
							!lText.equals(mLatestPostText) ||
							!lTopicId.equals(mLatestTopicId)) {
							lMotoFanPosts.add(aMotoFanPosts[i]);
						}
					}
					setLatestVariables(aMotoFanPosts, i);
				}
			}
		}
		return lMotoFanPosts;
	}

	private void setLatestVariables(final MotoFanPost[] aMotoFanPosts, final int aIndex) {
		mLatestPostTime = aMotoFanPosts[aIndex].getTimestamp();
		mLatestTopicId = aMotoFanPosts[aIndex].getTopic();
		mLatestPostAuthor = aMotoFanPosts[aIndex].getAuthor();
		mLatestPostText = aMotoFanPosts[aIndex].getText();
	}

	private void sendLatestMessages(final ArrayList<MotoFanPost> aMotoFanPosts) {
		if (aMotoFanPosts.size() > 0) {
			new Thread(() -> aMotoFanPosts.forEach((aMotoFanPost) ->
				mDigestBot.getIMotoFanSubscribersRepository().findAll().forEach((aSubscriberEntity) -> {
				mDigestBot.sendHtmlMessage(aSubscriberEntity.getSubscription(), null,
					formatMotoFanPost(aMotoFanPost));
				try {
					Thread.sleep(mDigestBot.getBotInlineCoolDown() * 1000);
				} catch (InterruptedException e) {
					mBotLogger.error(String.format("Cannot delay thread: '%s'.", e.toString()));
				}
			}))).start();
		}
	}

	private String formatMotoFanPost(final MotoFanPost aMotoFanPost) {
		return mYamlLocalizationHelper.getLocalizedString("crawler.motofan.title") + "\n\n<b>" +
			       aMotoFanPost.getAuthor() + "</b> " +
			       mYamlLocalizationHelper.getLocalizedString("crawler.motofan.writing") +
			       " (" + aMotoFanPost.getTime() + "):\n<i>" + deleteBbCodesFromText(aMotoFanPost.getText()) +
			       "</i>\n\n" + mYamlLocalizationHelper.getLocalizedString("crawler.motofan.read") +
			       " <a href=\"" + aMotoFanPost.getPost_link() + "\">" + aMotoFanPost.getTitle() + "</a>";
	}

	// https://stackoverflow.com/questions/14445386/how-to-remove-text-in-brackets-from-the-start-of-a-string
	private String deleteBbCodesFromText(final String aText) {
		return aText.replaceAll("\\[.*?\\]", "");
	}
}
