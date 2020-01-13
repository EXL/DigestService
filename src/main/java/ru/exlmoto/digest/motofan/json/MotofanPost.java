package ru.exlmoto.digest.motofan.json;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
			time != null &&
			topic != null &&
			post != null &&
			topic_link != null &&
			post_link != null &&
			author != null &&
			title != null &&
			text != null;
	}
}
