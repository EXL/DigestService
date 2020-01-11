package ru.exlmoto.motofan.manager.json;

import lombok.Data;

@Data
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
}
