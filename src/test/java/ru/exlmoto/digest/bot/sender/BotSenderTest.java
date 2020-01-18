package ru.exlmoto.digest.bot.sender;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@SpringBootTest
class BotSenderTest {
	@SpyBean
	private BotSender botSender;

	@BeforeEach
	public void setUp() {
		doNothing().when(botSender).executeMethod(any());
	}

	@Test
	public void testMethods() {
		botSender.replyMarkdownMessage(0L, 0, "Test");
		botSender.sendHtmlMessage(0L, "Test");

		InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
		InlineKeyboardButton button = new InlineKeyboardButton().setText("Button");
		List<List<InlineKeyboardButton>> buttonGrid = new ArrayList<>();
		List<InlineKeyboardButton> buttonsRow = new ArrayList<>();
		buttonsRow.add(button);
		buttonGrid.add(buttonsRow);
		inlineKeyboardMarkup.setKeyboard(buttonGrid);

		botSender.editMessage(0L, 0, "Test", inlineKeyboardMarkup);
		botSender.replyMessageWithKeyboard(0L, 0, "Test", inlineKeyboardMarkup);
	}

	@Test
	public void testShrinkTest() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 4000; i++) {
			builder.append("A");
		}
		String result = botSender.shrinkText(builder.toString());
		int length = result.length();
		assertTrue(length < 3500);
		System.out.println(result.substring(length - 30));
	}
}
