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

package ru.exlmoto.digest.motofan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.thymeleaf.util.ArrayUtils;

import ru.exlmoto.digest.motofan.generator.PostTgHtmlGenerator;
import ru.exlmoto.digest.motofan.json.MotofanPost;
import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.filter.FilterHelper;
import ru.exlmoto.digest.util.rest.RestHelper;

import java.util.ArrayList;
import java.util.List;

@Service
public class MotofanService {
	private final Logger log = LoggerFactory.getLogger(MotofanService.class);

	@Value("${motofan.last-post-url}")
	private String lastPostUrl;

	private final RestHelper rest;
	private final PostTgHtmlGenerator htmlGenerator;
	private final FilterHelper filter;

	private Long timestamp = 0L;
	private Long topic = 0L;
	private String author = null;
	private String text = null;

	public MotofanService(RestHelper rest, PostTgHtmlGenerator htmlGenerator, FilterHelper filter) {
		this.rest = rest;
		this.htmlGenerator = htmlGenerator;
		this.filter = filter;
	}

	public List<String> getLastMotofanPostsInHtml() {
		List<MotofanPost> motofanPosts = getLastMotofanPosts();
		if (motofanPosts != null) {
			List<String> motofanPostsInHtml = new ArrayList<>();
			for (MotofanPost post : motofanPosts) {
				motofanPostsInHtml.add(htmlGenerator.generateMotofanPostHtmlReport(post));
			}
			return motofanPostsInHtml;
		}
		return new ArrayList<>();
	}

	public String getMotofanBirthdays() {
		log.info("=> Start receive MotoFan.Ru user birthdays.");
		Answer<String> res = rest.getRestResponse(filter.getSiteUrlFromLink(lastPostUrl));
		log.info("=> End receive MotoFan.Ru user birthdays.");
		if (res.ok()) {
			return htmlGenerator.generateMotofanBirthdaysReport(res.answer());
		} else {
			log.error(String.format("=> Cannot get MotoFan.Ru user birthdays, error: '%s'.", res.error()));
		}
		return null;
	}

	protected List<MotofanPost> getLastMotofanPosts() {
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

	protected MotofanPost[] getMotofanPostObjects() {
		log.info("=> Start receive last MotoFan.Ru posts.");
		MotofanPost[] posts = rest.getRestResponse(lastPostUrl, MotofanPost[].class).answer();
		log.info("=> End receive last MotoFan.Ru posts.");
		if (posts != null && posts.length > 0) {
			List<MotofanPost> validPosts = new ArrayList<>();
			for (MotofanPost post : posts) {
				if (!post.isValid()) {
					log.warn(String.format("This post has not passed validation: '%s'.", post.toString()));
					continue;
				}
				validPosts.add(post);
			}
			try {
				return (MotofanPost[]) ArrayUtils.toArray(validPosts);
			} catch (ClassCastException cce) {
				log.error(String.format("Cannot cast array of valid posts to the simple array, see data: '%s'.",
					validPosts.toString()), cce);
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
