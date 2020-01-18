package ru.exlmoto.digest.motofan.json;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MotofanPostUnitTest {
	private final MotofanPost motofanPostFirst = new MotofanPost();
	private final MotofanPost motofanPostSecond = new MotofanPost();
	private final MotofanPost motofanPostThird = new MotofanPost();

	@Test
	public void testMotofanPost() {
		assertFalse(motofanPostFirst.isValid());

		motofanPostSecond.setAuthor("author");
		assertFalse(motofanPostSecond.isValid());

		motofanPostThird.setAuthor("author");
		motofanPostThird.setPost(1L);
		motofanPostThird.setPost_link("link");
		motofanPostThird.setText("text");
		motofanPostThird.setTime("time");
		motofanPostThird.setTimestamp(1L);
		motofanPostThird.setTitle("title");
		motofanPostThird.setTopic_link("link");
		assertFalse(motofanPostThird.isValid());

		motofanPostThird.setTopic(1L);
		assertTrue(motofanPostThird.isValid());

		assertTrue(new MotofanPostHelper().getRandomMotofanPost(1L).isValid());
	}
}
