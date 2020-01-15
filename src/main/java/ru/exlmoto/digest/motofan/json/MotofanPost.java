package ru.exlmoto.digest.motofan.json;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.springframework.util.StringUtils;

@Setter
@Getter
@ToString
@NoArgsConstructor
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
}
