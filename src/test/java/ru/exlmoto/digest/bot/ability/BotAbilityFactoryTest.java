package ru.exlmoto.digest.bot.ability;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.bot.ability.keyboard.Keyboard;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class BotAbilityFactoryTest {
	@Autowired
	private BotAbilityFactory abilityFactory;

	@Test
	public void testMessageAbility() {
		assertTrue(abilityFactory.getMessageAbility("/hi").isPresent());
		assertFalse(abilityFactory.getMessageAbility("/unknown-ability").isPresent());
	}

	@Test
	public void testKeyboardAbility() {
		assertTrue(abilityFactory.getKeyboardAbility(Keyboard.chart.withName()).isPresent());
		assertFalse(abilityFactory.getKeyboardAbility(Keyboard.chart.withName()).isPresent());
	}
}
