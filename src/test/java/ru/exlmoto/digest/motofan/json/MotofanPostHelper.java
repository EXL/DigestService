package ru.exlmoto.digest.motofan.json;

import java.util.concurrent.ThreadLocalRandom;

public class MotofanPostHelper {
	public MotofanPost getRandomMotofanPost(Long timestamp) {
		return
			new MotofanPost(
				timestamp,
				String.valueOf(ThreadLocalRandom.current().nextLong()),
				ThreadLocalRandom.current().nextLong(),
				ThreadLocalRandom.current().nextLong(),
				String.valueOf(ThreadLocalRandom.current().nextLong()),
				String.valueOf(ThreadLocalRandom.current().nextLong()),
				String.valueOf(ThreadLocalRandom.current().nextLong()),
				String.valueOf(ThreadLocalRandom.current().nextLong()),
				String.valueOf(ThreadLocalRandom.current().nextLong())
			);
	}
}
