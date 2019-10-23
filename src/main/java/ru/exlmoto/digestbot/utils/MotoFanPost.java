package ru.exlmoto.digestbot.utils;

public class MotoFanPost {
	private Long timestamp;
	private String time;
	private Integer topic;
	private Integer post;
	private String topic_link;
	private String post_link;
	private String author;
	private String title;
	private String text;

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

	public Integer getTopic() {
		return topic;
	}

	public void setTopic(Integer topic) {
		this.topic = topic;
	}

	public Integer getPost() {
		return post;
	}

	public void setPost(Integer post) {
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
		return "MotoFanPost{" +
			       "timestamp=" + timestamp +
			       ", time='" + time + '\'' +
			       ", topic=" + topic +
			       ", topic_link='" + topic_link + '\'' +
			       ", post_link='" + post_link + '\'' +
			       ", author='" + author + '\'' +
			       ", title='" + title + '\'' +
			       ", text='" + text + '\'' +
			       '}';
	}
}