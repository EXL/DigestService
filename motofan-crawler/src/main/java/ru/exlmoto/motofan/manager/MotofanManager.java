package ru.exlmoto.motofan.manager;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import ru.exlmoto.motofan.MotofanConfiguration;
import ru.exlmoto.motofan.generator.HtmlGenerator;
import ru.exlmoto.motofan.manager.json.MotofanPost;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class MotofanManager {
	private final RestManager restManager;
	private final HtmlGenerator htmlGenerator;
	private final MotofanConfiguration config;

	private Long timestamp = 0L;
	private Long topic = 0L;
	private String author = null;
	private String text = null;

	public MotofanPost[] getMotofanPostObjects() {
		return restManager.getLastMotofanPosts(config.getLastPostUrl());
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
				if (!author.equals("palach")) {
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
