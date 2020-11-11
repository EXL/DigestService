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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

@Component
public class GameCommand extends MessageAbility {
	private final Logger log = LoggerFactory.getLogger(GameCommand.class);

	private final String QUAKE_2 = "Quake II";
	private final int Q2_PORT = 27910;
	private final String Q2_INFO = "info 34";

	private final String QUAKE_3 = "Quake III";
	private final int Q3_PORT = 27960;
	private final String Q3_INFO = "getinfo 68";

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
		if (config.isUrlGameImage()) {
			Answer<String> res = rest.getImageByLink(config.getUrlGame());
			if (res.ok()) {
				sender.replyPhoto(message.chat().id(), message.messageId(), res.answer(),
					locale.i18n("bot.command.game"));
			} else {
				sender.replyMarkdown(message.chat().id(), message.messageId(),
					String.format(locale.i18n("bot.error.game"), res.error()));
			}
		} else {
			final String q2 = config.getUrlGameQuake2();
			final String q3 = config.getUrlGameQuake3();
			String answer = getServerStat(udp.sendCommandAndGetAnswer(q2, Q2_PORT, Q2_INFO), q2, QUAKE_2, locale);
			answer += "\n\n";
			answer += getServerStat(udp.sendCommandAndGetAnswer(q3, Q3_PORT, Q3_INFO), q3, QUAKE_3, locale);
			sender.replyMarkdown(message.chat().id(), message.messageId(), answer);
		}
	}

	protected String parseInfoQuake2(String host, String answer, LocaleHelper locale) {
		String res = answer.substring(answer.indexOf('\n') + 1); // Drop first line.
		res = res.replace("/ ", "/"); // Drop space after slash character.
		res = filter.strip(res); // Strip string from spaces.
		String[] tokens = res.split("\\s+");
		tokens[0] = getHostName(host, tokens[0]);
		return generateTable(locale, tokens);
	}

	protected String parseInfoQuake3(String host, String answer, LocaleHelper locale) {
		final String HOSTNAME = "hostname";
		final String MAPNAME = "mapname";
		final String CLIENTS = "clients";
		final String MAXCLIENTS = "sv_maxclients";
		Map<String, String> values = new HashMap<>();
		String res = answer.substring(answer.indexOf('\n') + 1); // Drop first line.
		String[] tokens = res.split("\\\\"); // Split by backslash.
		try {
			for (int i = 1; i < tokens.length; i += 2) {
				values.put(tokens[i], tokens[i + 1]);
			}
		} catch (IndexOutOfBoundsException ioobe) {
			log.error("Cannot parse Quake 3 server statistic answer.", ioobe);
			return locale.i18n("bot.error.game.server");
		}
		res = getHostName(host, values.get(HOSTNAME)) + " " +
			values.get(MAPNAME) + " " + values.get(CLIENTS) + '/' + values.get(MAXCLIENTS);
		return generateTable(locale, res.split("\\s+"));
	}

	protected String generateTable(LocaleHelper locale, String[] tokens) {
		Map<String, String> map = new LinkedHashMap<>();
		Map<String, String> formattedMap = new LinkedHashMap<>();
		StringJoiner joiner = new StringJoiner(" ");
		for (int i = 0; i < tokens.length - 2; ++i) {
			joiner.add(tokens[i]);
		}
		map.put(locale.i18n("bot.command.game.host"), joiner.toString());
		map.put(locale.i18n("bot.command.game.map"), tokens[tokens.length - 2]);
		map.put(locale.i18n("bot.command.game.players"), tokens[tokens.length - 1]);
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

	private String getServerStat(Answer<String> res, String host, String game, LocaleHelper locale) {
		if (res.ok()) {
			return String.format(locale.i18n("bot.command.game.server"), game,
				locale.i18n("bot.command.game.online"), parseServerAnswer(host, game, res.answer(), locale)) + "\n" +
				String.format(locale.i18n("bot.command.game.server.info"), host);
		} else {
			return String.format(locale.i18n("bot.command.game.server"), game,
				locale.i18n("bot.command.game.offline"), res.error());
		}
	}

	private String parseServerAnswer(String host, String game, String answer, LocaleHelper locale) {
		switch (game) {
			case QUAKE_2:
				return parseInfoQuake2(host, answer, locale);
			case QUAKE_3:
				return parseInfoQuake3(host, answer, locale);
		}
		return locale.i18n("bot.error.game.server");
	}

	private String getHostName(String host, String hostname) {
		return config.isUrlGameUseOwnName() ? hostname : host;
	}
}
