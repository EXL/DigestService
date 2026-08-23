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

package ru.exlmoto.digest.bot.util;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Chat.Type;
import com.pengrad.telegrambot.model.User;

import org.springframework.stereotype.Component;

import org.thymeleaf.util.ArrayUtils;

import ru.exlmoto.digest.bot.configuration.BotConfiguration;

@Component
public class BotHelper {
	private final BotConfiguration config;

	public BotHelper(BotConfiguration config) {
		this.config = config;
	}

	public String getValidUsername(User user) {
		String username = getValidUsernameAux(user.username(), user.lastName(), user.firstName());
		return username != null ? username : "user-" + user.id();
	}

	public boolean isUserAdmin(String username) {
		return username != null && ArrayUtils.contains(config.getAdmins(), username);
	}

	public String getValidChatName(Chat chat) {
		return (!chat.type().equals(Type.Private)) ? chat.title() :
			getValidUsernameAux(chat.username(), chat.lastName(), chat.firstName());
	}

	private String getValidUsernameAux(String username, String lastName, String firstName) {
		if (username != null) {
			return "@" + username;
		}
		String validFirstName = removeFirstCast(firstName);
		String validLastName = removeFirstCast(lastName);
		if (validFirstName != null && validLastName != null) {
			return validFirstName + " " + validLastName;
		}
		return validFirstName != null ? validFirstName : validLastName;
	}

	private String removeFirstCast(String name) {
		if (name == null) {
			return null;
		}
		if (name.charAt(0) != '@') {
			return name;
		}
		return "A" + name.substring(1);
	}
}
