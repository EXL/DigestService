package ru.exlmoto.digest.motofan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ru.exlmoto.digest.motofan.generator.TgHtmlGenerator;
import ru.exlmoto.digest.motofan.json.MotofanPost;
import ru.exlmoto.digest.util.rest.RestHelper;

import java.util.ArrayList;
import java.util.List;

@Service
public class MotofanService {
	private final Logger log = LoggerFactory.getLogger(MotofanService.class);

	@Value("${motofan.last-post-url}")
	private String lastPostUrl;

	private final RestHelper rest;
	private final TgHtmlGenerator htmlGenerator;

	private Long timestamp = 0L;
	private Long topic = 0L;
	private String author = null;
	private String text = null;

	public MotofanService(RestHelper rest, TgHtmlGenerator htmlGenerator) {
		this.rest = rest;
		this.htmlGenerator = htmlGenerator;
	}

	public MotofanPost[] getMotofanPostObjects() {
		MotofanPost[] posts = rest.getRestResponse(lastPostUrl, MotofanPost[].class).answer();
		if (posts != null && posts.length > 0) {
			for (MotofanPost post : posts) {
				if (!post.isValid()) {
					return null;
				}
			}
			return posts;
		}
		return null;
	}

	public List<String> getLastMotofanPostsInHtml() {
		List<MotofanPost> motofanPosts = getLastMotofanPosts();
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
		log.info("=> Start receive last MotoFan.Ru posts.");
		MotofanPost[] motofanPosts = getMotofanPostObjects();
		log.info("=> End receive last MotoFan.Ru posts.");
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
