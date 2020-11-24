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

package ru.exlmoto.digest.motofan.json;

import org.springframework.util.StringUtils;

public class MotofanPost {
	private Long timestamp;
	private String time;
	private Long topic;
	private Long post;
	private String topic_link;
	private String post_link;
	private String author;
	private String title;
	private String text;

	public MotofanPost() {

	}

	public MotofanPost(Long timestamp,
	                   String time,
	                   Long topic,
	                   Long post,
	                   String topic_link,
	                   String post_link,
	                   String author,
	                   String title,
	                   String text) {
		this.timestamp = timestamp;
		this.time = time;
		this.topic = topic;
		this.post = post;
		this.topic_link = topic_link;
		this.post_link = post_link;
		this.author = author;
		this.title = title;
		this.text = text;
	}

	public boolean isValid() {
		return
			timestamp != null &&
			StringUtils.hasLength(time) &&
			topic != null &&
			post != null &&
			StringUtils.hasLength(topic_link) &&
			StringUtils.hasLength(post_link) &&
			StringUtils.hasLength(author) &&
			StringUtils.hasLength(title) &&
			StringUtils.hasLength(text);
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Long getTopic() {
		return topic;
	}

	public void setTopic(Long topic) {
		this.topic = topic;
	}

	public Long getPost() {
		return post;
	}

	public void setPost(Long post) {
		this.post = post;
	}

	public String getTopic_link() {
		return topic_link;
	}

	public void setTopic_link(String topic_link) {
		this.topic_link = topic_link;
	}

	public String getPost_link() {
		return post_link;
	}

	public void setPost_link(String post_link) {
		this.post_link = post_link;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return
			"MotofanPost{timestamp=" + timestamp +
			", time=" + time +
			", topic=" + topic +
			", post=" + post +
			", topic_link=" + topic_link +
			", post_link=" + post_link +
			", author=" + author +
			", title=" + title +
			", text=" + text +
			"}";
	}
}
