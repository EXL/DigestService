package ru.exlmoto.motofan.manager.helper;

import ru.exlmoto.motofan.manager.json.MotofanPost;

import java.util.Random;

public class MotofanPostHelper {
	public MotofanPost getRandomMotofanPost(Long timestamp) {
		MotofanPost motofanPost = new MotofanPost();
		motofanPost.setTimestamp(timestamp);
		motofanPost.setTime(String.valueOf(new Random().nextLong()));
		motofanPost.setTopic(new Random().nextLong());
		motofanPost.setPost(new Random().nextLong());
		motofanPost.setTopic_link(String.valueOf(new Random().nextLong()));
		motofanPost.setPost_link(String.valueOf(new Random().nextLong()));
		motofanPost.setAuthor(String.valueOf(new Random().nextLong()));
		motofanPost.setTitle(String.valueOf(new Random().nextLong()));
		motofanPost.setText(String.valueOf(new Random().nextLong()));
		return motofanPost;
	}
}
