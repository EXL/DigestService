package ru.exlmoto.digest.motofan.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ru.exlmoto.digest.motofan.generator.TgHtmlGenerator;
import ru.exlmoto.digest.motofan.json.MotofanPost;
import ru.exlmoto.digest.rest.RestService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MotofanService {
	@Value("${motofan.last-post-url}")
	private String lastPostUrl;

	private final RestService restService;
	private final TgHtmlGenerator htmlGenerator;

	private Long timestamp = 0L;
	private Long topic = 0L;
	private String author = null;
	private String text = null;

	public MotofanPost[] getMotofanPostObjects() {
		return restService.getLastMotofanPosts(lastPostUrl);
	}

	public List<String> getLastMotofanPostsInHtml() {
		log.info("=> Start crawling MotoFan.Ru last posts.");
		List<MotofanPost> motofanPosts = getLastMotofanPosts();
		log.info("=> End crawling MotoFan.Ru last posts.");
		if (motofanPosts != null) {
			List<String> motofanPostsInHtml = new ArrayList<>();
			for (MotofanPost post : motofanPosts) {
				motofanPostsInHtml.add(htmlGenerator.generateHtmlReport(post));
			}
			return motofanPostsInHtml;
		}
		return new ArrayList<>();
	}

	public List<MotofanPost> getLastMotofanPosts() {
		MotofanPost[] motofanPosts = getMotofanPostObjects();
		if (motofanPosts != null) {
			if (timestamp == 0L) {
				setLastValues(motofanPosts, 0);
			} else {
				return getLastMotofanPostsAux(motofanPosts);
			}
		}
		return null;
	}

	private List<MotofanPost> getLastMotofanPostsAux(MotofanPost[] posts) {
		List<MotofanPost> motofanPostList = new ArrayList<>();
		for (int i = posts.length - 1; i >= 0; i--) {
			Long postTimestamp = posts[i].getTimestamp();
			if (postTimestamp > timestamp) {
				String postAuthor = posts[i].getAuthor();
				// HACK: Drop automatic congratulation forum messages.
				if (!postAuthor.equals("palach")) {
					Long postTopic = posts[i].getTopic();
					String postText = posts[i].getText();
					// HACK: Drop "added later" messages.
					if (!postAuthor.equals(author) || !postTopic.equals(topic) || !postText.equals(text)) {
						motofanPostList.add(posts[i]);
					}
				}
				setLastValues(posts, i);
			}
		}
		return motofanPostList.size() > 0 ? motofanPostList : null;
	}

	private void setLastValues(MotofanPost[] motofanPosts, int index) {
		timestamp = motofanPosts[index].getTimestamp();
		topic = motofanPosts[index].getTopic();
		author = motofanPosts[index].getAuthor();
		text = motofanPosts[index].getText();
	}
}
