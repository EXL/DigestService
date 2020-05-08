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

package ru.exlmoto.digest.service;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.entity.FlatSetupEntity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class DatabaseServiceTest {
	@Autowired
	private DatabaseService service;

	@Test
	public void testDatabaseService() {
		assertNotNull(service);
		assertTrue(service.checkGreeting(0L));
	}

	@Test
	public void testOnWrongPageArgument() {
		assertThrows(IllegalArgumentException.class, () -> service.getChatDigestsCommand(0, 10, 0L));
		assertThrows(IllegalArgumentException.class, () -> service.getAllDigests(0, 10));
	}

	@Test
	public void testCheckFlatSettings() {
		FlatSetupEntity settings = new FlatSetupEntity();
		assertFalse(service.checkFlatSettings(settings));

		settings.setMaxVariants(25);
		assertFalse(service.checkFlatSettings(settings));

		settings.setApiCianUrl("https://exlmoto.ru");
		assertFalse(service.checkFlatSettings(settings));

		settings.setViewCianUrl("https://exlmoto.ru");
		assertTrue(service.checkFlatSettings(settings));

		settings = new FlatSetupEntity();
		settings.setMaxVariants(25);
		settings.setApiN1Url("https://exlmoto.ru");
		assertFalse(service.checkFlatSettings(settings));

		settings.setViewN1Url("https://exlmoto.ru");
		assertTrue(service.checkFlatSettings(settings));

		settings.setApiCianUrl("https://exlmoto.ru");
		assertTrue(service.checkFlatSettings(settings));

		settings.setViewCianUrl("https://exlmoto.ru");
		assertTrue(service.checkFlatSettings(settings));
	}
}
