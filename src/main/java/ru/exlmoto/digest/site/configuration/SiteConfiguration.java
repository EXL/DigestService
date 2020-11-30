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

package ru.exlmoto.digest.site.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "site")
public class SiteConfiguration {
	private String address;

	private int pagePosts;
	private int pageDeep;
	private int pagePostsAdmin;
	private int pageDeepAdmin;

	private String motofanChatSlug;
	private String[] moderators;

	private String proxy;
	private boolean proxyEnabled;

	private boolean autolinkerEnabled;

	private boolean obeyProtection;
	private String obeyDebugPassword;
	private String obeyDebugRole;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getPagePosts() {
		return pagePosts;
	}

	public void setPagePosts(int pagePosts) {
		this.pagePosts = pagePosts;
	}

	public int getPageDeep() {
		return pageDeep;
	}

	public void setPageDeep(int pageDeep) {
		this.pageDeep = pageDeep;
	}

	public int getPagePostsAdmin() {
		return pagePostsAdmin;
	}

	public void setPagePostsAdmin(int pagePostsAdmin) {
		this.pagePostsAdmin = pagePostsAdmin;
	}

	public int getPageDeepAdmin() {
		return pageDeepAdmin;
	}

	public void setPageDeepAdmin(int pageDeepAdmin) {
		this.pageDeepAdmin = pageDeepAdmin;
	}

	public String getMotofanChatSlug() {
		return motofanChatSlug;
	}

	public void setMotofanChatSlug(String motofanChatSlug) {
		this.motofanChatSlug = motofanChatSlug;
	}

	public String[] getModerators() {
		return moderators;
	}

	public void setModerators(String[] moderators) {
		this.moderators = moderators;
	}

	public String getProxy() {
		return proxy;
	}

	public void setProxy(String proxy) {
		this.proxy = proxy;
	}

	public boolean isProxyEnabled() {
		return proxyEnabled;
	}

	public void setProxyEnabled(boolean proxyEnabled) {
		this.proxyEnabled = proxyEnabled;
	}

	public boolean isAutolinkerEnabled() {
		return autolinkerEnabled;
	}

	public void setAutolinkerEnabled(boolean autolinkerEnabled) {
		this.autolinkerEnabled = autolinkerEnabled;
	}

	public boolean isObeyProtection() {
		return obeyProtection;
	}

	public void setObeyProtection(boolean obeyProtection) {
		this.obeyProtection = obeyProtection;
	}

	public String getObeyDebugPassword() {
		return obeyDebugPassword;
	}

	public void setObeyDebugPassword(String obeyDebugPassword) {
		this.obeyDebugPassword = obeyDebugPassword;
	}

	public String getObeyDebugRole() {
		return obeyDebugRole;
	}

	public void setObeyDebugRole(String obeyDebugRole) {
		this.obeyDebugRole = obeyDebugRole;
	}
}
