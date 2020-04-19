/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2020 EXL
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

package ru.exlmoto.digest.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bot_setup")
public class BotSetupEntity {
	public static final int SETUP_ROW = 1;

	@Id
	private int id;

	@Column(nullable = false)
	private boolean logUpdates;

	@Column(nullable = false)
	private boolean showGreetings;

	@Column(nullable = false)
	private boolean silentMode;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isLogUpdates() {
		return logUpdates;
	}

	public void setLogUpdates(boolean logUpdates) {
		this.logUpdates = logUpdates;
	}

	public boolean isShowGreetings() {
		return showGreetings;
	}

	public void setShowGreetings(boolean showGreetings) {
		this.showGreetings = showGreetings;
	}

	public boolean isSilentMode() {
		return silentMode;
	}

	public void setSilentMode(boolean silentMode) {
		this.silentMode = silentMode;
	}

	@Override
	public String toString() {
		return
			"SetupBotEntity{id=" + id +
			", logUpdates=" + logUpdates +
			", showGreetings=" + showGreetings +
			", silentMode=" + silentMode +
			"}";
	}
}
