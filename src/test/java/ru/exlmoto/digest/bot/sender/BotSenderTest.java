package ru.exlmoto.digest.bot.sender;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "bot.silent=true")
class BotSenderTest {
	@Autowired
	private BotSender sender;

	@Test
	public void testBotSenderMethods() {
		sender.replyMessage(0L, 0, "Fake text");
		sender.replySticker(0L, 0, "Fake stickerId");
		sender.replyPhoto(0L, 0, "Fake uri", "Fake title");
		System.out.println("===");

		sender.sendHtmlMessage(0L, "Fake html");
		sender.sendCallbackQueryAnswer("0", "Fake callbackQueryAnswer");
		System.out.println("===");

		sender.sendMessageToChat(0L, "Fake text", 0L, 0);
		sender.sendStickerToChat(0L, "Fake stickerId", 0L, 0);
		sender.sendPhotoToChat(0L, "Fake uri", "Fake title", 0L, 0);
		System.out.println("===");

		InlineKeyboardButton[][] buttons = new InlineKeyboardButton[2][2];
		buttons[0][0] = new InlineKeyboardButton("Button 1");
		buttons[0][1] = new InlineKeyboardButton("Button 2");
		buttons[1][1] = new InlineKeyboardButton("Button 3");
		InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(buttons);

		sender.replyKeyboard(0L, 0, "Fake text", inlineKeyboardMarkup);
		sender.editMessage(0L, 0, "Fake text", inlineKeyboardMarkup);
		System.out.println("===");
	}

	@Test
	public void testShrinkTest() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 4000; i++) {
			builder.append("A");
		}
		String result = sender.shrinkText(builder.toString());
		int length = result.length();
		assertTrue(length < 3500);
		System.out.println(result.substring(length - 30));
	}
}
