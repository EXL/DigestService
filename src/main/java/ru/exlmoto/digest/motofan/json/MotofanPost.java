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
			!StringUtils.isEmpty(time) &&
			topic != null &&
			post != null &&
			!StringUtils.isEmpty(topic_link) &&
			!StringUtils.isEmpty(post_link) &&
			!StringUtils.isEmpty(author) &&
			!StringUtils.isEmpty(title) &&
			!StringUtils.isEmpty(text);
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
