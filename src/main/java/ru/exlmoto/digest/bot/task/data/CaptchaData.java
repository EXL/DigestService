/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2022 EXL <exlmotodev@gmail.com>
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

package ru.exlmoto.digest.bot.task.data;

import org.springframework.data.util.Pair;

import java.util.concurrent.ScheduledFuture;

public class CaptchaData {
	private final ScheduledFuture<?> timerHandle;

	// 1. Joined Message ID.
	// 2. Additional Message ID;
	private final Pair<Long, Long> messageIds;

	public CaptchaData(Pair<Long, Long> messageIds, ScheduledFuture<?> timerHandle) {
		this.messageIds = messageIds;
		this.timerHandle = timerHandle;
	}

	public ScheduledFuture<?> getTimerHandle() {
		return timerHandle;
	}

	public Pair<Long, Long> getMessageIds() {
		return messageIds;
	}
}
