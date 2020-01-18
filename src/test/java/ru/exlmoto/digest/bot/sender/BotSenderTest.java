package ru.exlmoto.digest.bot.sender;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "bot.debug-silent=true")
class BotSenderTest {
	@Autowired
	private BotSender botSender;

	@Test
	public void testBotSenderMethods() {
		botSender.replyMessage(0L, 0, "Test");
		botSender.replySticker(0L, 0, "Test");
		botSender.replyPhoto(0L, 0, "fake-uri", "Test");
		System.out.println("===");

		botSender.sendHtmlMessage(0L, "Test");
		botSender.sendAnswer("0", "Test");
		System.out.println("===");

		botSender.sendMessageToChat(0L, "Test", 0L, 0);
		botSender.sendStickerToChat(0L, "Test", 0L, 0);
		botSender.sendPhotoToChat(0L, "fake-uri", "Test", 0L, 0);
		System.out.println("===");

		InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
		InlineKeyboardButton button = new InlineKeyboardButton().setText("Button");
		List<List<InlineKeyboardButton>> buttonGrid = new ArrayList<>();
		List<InlineKeyboardButton> buttonsRow = new ArrayList<>();
		buttonsRow.add(button);
		buttonGrid.add(buttonsRow);
		inlineKeyboardMarkup.setKeyboard(buttonGrid);

		botSender.editMessage(0L, 0, "Test", inlineKeyboardMarkup);
		botSender.replyMessageWithKeyboard(0L, 0, "Test", inlineKeyboardMarkup);
		System.out.println("===");
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
