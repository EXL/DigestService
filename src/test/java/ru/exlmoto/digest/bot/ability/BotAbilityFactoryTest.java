package ru.exlmoto.digest.bot.ability;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class BotAbilityFactoryTest {
	@Autowired
	private BotAbilityFactory abilityFactory;

	@Test
	public void testGetAbility() {
		assertTrue(abilityFactory.getAbility("/hi").isPresent());
		assertFalse(abilityFactory.getAbility("/unknown-ability").isPresent());
	}
}
