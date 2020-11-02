/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2020 EXL <exlmotodev@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ru.exlmoto.digest.bot.ability.message.impl;

import com.pengrad.telegrambot.model.Message;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.ability.message.MessageAbility;
import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.file.ImageHelper;
import ru.exlmoto.digest.util.filter.FilterHelper;
import ru.exlmoto.digest.util.i18n.LocaleHelper;
import ru.exlmoto.digest.util.udp.UdpSender;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class GameCommand extends MessageAbility {
	private final int QUAKE_2_SERVER_PORT = 27910;
	private final String QUAKE_2_INFO_COMMAND = "info 34";

	private final BotConfiguration config;
	private final ImageHelper rest;
	private final UdpSender udp;
	private final FilterHelper filter;

	public GameCommand(BotConfiguration config, ImageHelper rest, UdpSender udp, FilterHelper filter) {
		this.config = config;
		this.rest = rest;
		this.udp = udp;
		this.filter = filter;
	}

	@Override
	protected void execute(BotHelper helper, BotSender sender, LocaleHelper locale, Message message) {
		final String urlGame = config.getUrlGame();
		if (config.isUrlGameImage()) {
			Answer<String> res = rest.getImageByLink(urlGame);
			if (res.ok()) {
				sender.replyPhoto(message.chat().id(), message.messageId(), res.answer(),
					locale.i18n("bot.command.game"));
			} else {
				sender.replyMarkdown(message.chat().id(), message.messageId(),
					String.format(locale.i18n("bot.error.game"), res.error()));
			}
		} else {
			Answer<String> res = udp.sendCommandAndGetAnswer(urlGame, QUAKE_2_SERVER_PORT, QUAKE_2_INFO_COMMAND);
			if (res.ok()) {
				sender.replyMarkdown(message.chat().id(), message.messageId(),
					String.format(locale.i18n("bot.command.game.quake2"),
						locale.i18n("bot.command.game.online"), parseInfoQuake2(locale, res.answer())) + "\n" +
					String.format(locale.i18n("bot.command.game.quake2.info"), urlGame));
			} else {
				sender.replyMarkdown(message.chat().id(), message.messageId(),
					String.format(locale.i18n("bot.command.game.quake2"),
						locale.i18n("bot.command.game.offline"), res.error()));
			}
		}
	}

	protected String parseInfoQuake2(LocaleHelper locale, String answer) {
		String res = answer.substring(answer.indexOf('\n') + 1); // Drop first line.
		res = res.replace("/ ", "/"); // Drop space after slash character.
		res = filter.strip(res); // Strip string from spaces.
		String[] tokens = res.split("\\s+");
		return generateTable(locale, tokens);
	}

	protected String generateTable(LocaleHelper locale, String[] tokens) {
		Map<String, String> map = new LinkedHashMap<>();
		Map<String, String> formattedMap = new LinkedHashMap<>();
		map.put(locale.i18n("bot.command.game.host"), tokens[0]);
		map.put(locale.i18n("bot.command.game.map"), tokens[1]);
		map.put(locale.i18n("bot.command.game.players"), tokens[2]);
		StringBuilder stringBuilder = new StringBuilder();
		map.forEach((k, v) -> {
			int kLength = k.length();
			int vLength = v.length();
			if (kLength <= vLength) {
				formattedMap.put(filter.arrangeString(k, vLength + 1), v + " ");
			} else {
				formattedMap.put(k + " ", filter.arrangeString(" ", kLength - vLength) + v);
			}
		});
		formattedMap.keySet().forEach(stringBuilder::append);
		stringBuilder.append("\n");
		formattedMap.values().forEach(stringBuilder::append);
		return stringBuilder.toString();
	}
}
