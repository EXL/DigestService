package ru.exlmoto.digest.bot.sender;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.bot.configuration.BotConfiguration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(properties = "bot.silent=true")
class BotSenderTest {
	@Autowired
	private BotSender sender;

	@Autowired
	private BotConfiguration config;

	@Test
	public void testBotSenderMethods() {
		sender.replySimple(0L, 0, "Fake text");
		sender.replyMarkdown(0L, 0, "*Fake text*");
		sender.replySticker(0L, 0, "Fake stickerId");
		sender.replyPhoto(0L, 0, "Fake uri", "Fake title");
		System.out.println("===");

		sender.sendHtml(0L, "<b>Fake text</b>");
		sender.sendCallbackQueryAnswer("0", "Fake callbackQueryAnswer");
		System.out.println("===");

		sender.sendSimpleToChat(0L, "Fake text", 0L, 0);
		sender.sendStickerToChat(0L, "Fake stickerId", 0L, 0);
		sender.sendPhotoToChat(0L, "Fake uri", 0L, 0);
		System.out.println("===");

		InlineKeyboardButton[][] buttons = new InlineKeyboardButton[2][2];
		buttons[0][0] = new InlineKeyboardButton("Button 1");
		buttons[0][1] = new InlineKeyboardButton("Button 2");
		buttons[1][1] = new InlineKeyboardButton("Button 3");
		InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(buttons);

		sender.replyMarkdown(0L, 0, "*Fake text*", inlineKeyboardMarkup);
		sender.editMarkdown(0L, 0, "*Fake text*", inlineKeyboardMarkup);
		sender.editMarkdown(0L, 0, "*Fake text*", null);
		System.out.println("===");

		sender.replyHtml(0L, 0, "<b>Fake text</b>", inlineKeyboardMarkup);
		sender.editHtml(0L, 0, "<b>Fake text</b>", inlineKeyboardMarkup);
		sender.editHtml(0L, 0, "<b>Fake text</b>", null);
		System.out.println("===");

		sender.sendSimpleToChat(0L, "Fake text");
		sender.sendStickerToChat(0L, "Fake stickerId");
		sender.sendPhotoToChat(0L, "Fake uri");
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

	@Test
	public void testIsUserChatAdministrator() {
		assertFalse(sender.isUserChatAdministrator(config.getMotofanChatId(), 100L));
		assertTrue(sender.isUserChatAdministrator(config.getMotofanChatId(), 87336977L));
	}
}
