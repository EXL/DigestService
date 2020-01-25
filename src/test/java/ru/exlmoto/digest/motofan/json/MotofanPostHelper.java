package ru.exlmoto.digest.motofan.json;

import java.util.Random;

public class MotofanPostHelper {
	public MotofanPost getRandomMotofanPost(Long timestamp) {
		return
			new MotofanPost(
				timestamp,
				String.valueOf(new Random().nextLong()),
				new Random().nextLong(),
				new Random().nextLong(),
				String.valueOf(new Random().nextLong()),
				String.valueOf(new Random().nextLong()),
				String.valueOf(new Random().nextLong()),
				String.valueOf(new Random().nextLong()),
				String.valueOf(new Random().nextLong())
			);
	}
}
