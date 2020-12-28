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

package ru.exlmoto.digest.site.controller;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.worker.AvatarWorker;
import ru.exlmoto.digest.bot.worker.CallbackQueriesWorker;
import ru.exlmoto.digest.bot.worker.DigestWorker;
import ru.exlmoto.digest.bot.worker.MotofanWorker;
import ru.exlmoto.digest.bot.worker.CovidWorker;
import ru.exlmoto.digest.bot.worker.FlatWorker;
import ru.exlmoto.digest.exchange.ExchangeService;
import ru.exlmoto.digest.service.DatabaseService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;

@SpringBootTest(properties = "site.obey-protection=false")
@AutoConfigureMockMvc
class ObeyControllerTest {
	@Autowired
	private MockMvc mvc;

	@SpyBean
	private DatabaseService databaseService;

	@MockBean
	private DigestWorker digestWorker;

	@MockBean
	private CallbackQueriesWorker callbackQueriesWorker;

	@MockBean
	private AvatarWorker avatarWorker;

	@MockBean
	private MotofanWorker motofanWorker;

	@MockBean
	private CovidWorker covidWorker;

	@MockBean
	private FlatWorker flatWorker;

	@MockBean
	private ExchangeService exchangeService;

	@MockBean
	private BotSender sender;

	private final ControllerHelper helper = new ControllerHelper();

	@Test
	public void testObey() throws Exception {
		helper.checkRedirect(mvc, "/obey", "**/ds-auth-login");
	}

	@Test
	@WithMockUser
	public void testObeyAuthorized() throws Exception {
		helper.validateHtmlUtf8(mvc, "/obey", "!DOCTYPE");
	}

	@Test
	public void testObeyDelete() throws Exception {
		helper.checkRedirect(mvc, "/obey/delete/100", "**/ds-auth-login");
	}

	@Test
	public void testObeyDeleteAuthorized() throws Exception {
		doNothing().when(databaseService).deleteDigest(anyLong());

		helper.checkUnauthorized(mvc, "/obey/delete/100");
		helper.checkAuthorizedWithoutCsrf(mvc, "/obey/delete/100");
		helper.checkAuthorizedWithCsrfRedirect(mvc, "/obey/delete/100", "/**/obey");
	}

	@Test
	public void testObeyEdit() throws Exception {
		helper.checkRedirect(mvc, "/obey/edit", "**/ds-auth-login");
	}

	@Test
	public void testObeyEditAuthorized() throws Exception {
		doNothing().when(databaseService).saveDigest(any());

		helper.checkUnauthorized(mvc, "/obey/edit");
		helper.checkAuthorizedWithoutCsrf(mvc, "/obey/edit");
		helper.checkAuthorizedWithCsrfRedirect(mvc, "/obey/edit", "/**/obey");
	}

	@Test
	public void testObeyShredder() throws Exception {
		helper.checkRedirect(mvc, "/obey/shredder", "**/ds-auth-login");
	}

	@Test
	@WithMockUser
	public void testObeyShredderAuthorized() throws Exception {
		helper.checkRedirect(mvc, "/obey/shredder", "/**/obey");
	}

	@Test
	public void testObeyBirthday() throws Exception {
		helper.checkRedirect(mvc, "/obey/birthday", "**/ds-auth-login");
	}

	@Test
	@WithMockUser
	public void testObeyBirthdayAuthorized() throws Exception {
		helper.checkRedirect(mvc, "/obey/birthday", "/**/obey");
	}

	@Test
	public void testObeyCallback() throws Exception {
		helper.checkRedirect(mvc, "/obey/callback", "**/ds-auth-login");
	}

	@Test
	@WithMockUser
	public void testObeyCallbackAuthorized() throws Exception {
		helper.checkRedirect(mvc, "/obey/callback", "/**/obey");
	}

	@Test
	public void testObeyUser() throws Exception {
		helper.checkRedirect(mvc, "/obey/user", "**/ds-auth-login");
	}

	@Test
	@WithMockUser
	public void testObeyUserAuthorized() throws Exception {
		helper.validateHtmlUtf8(mvc, "/obey/user", "!DOCTYPE");
	}

	@Test
	public void testObeyUserDelete() throws Exception {
		doNothing().when(databaseService).deleteDigestUser(anyLong());

		helper.checkUnauthorized(mvc, "/obey/user/delete/100");
		helper.checkAuthorizedWithoutCsrf(mvc, "/obey/user/delete/100");
		helper.checkAuthorizedWithCsrfRedirect(mvc, "/obey/user/delete/100", "/**/obey/user");
	}

	@Test
	public void testObeyUserEdit() throws Exception {
		doNothing().when(databaseService).saveDigestUser(any());

		helper.checkUnauthorized(mvc, "/obey/user/edit");
		helper.checkAuthorizedWithoutCsrf(mvc, "/obey/user/edit");
		helper.checkAuthorizedWithCsrfRedirect(mvc, "/obey/user/edit", "/**/obey/user");
	}

	@Test
	public void testObeyUserAvatar() throws Exception {
		helper.checkRedirect(mvc, "/obey/user/avatar", "**/ds-auth-login");
	}

	@Test
	@WithMockUser
	public void testObeyUserAvatarAuthorized() throws Exception {
		helper.checkRedirect(mvc, "/obey/user/avatar", "/**/obey/user");
	}

	@Test
	public void testObeySetup() throws Exception {
		helper.checkRedirect(mvc, "/obey/setup", "**/ds-auth-login");
	}

	@Test
	@WithMockUser
	public void testObeySetupAuthorized() throws Exception {
		helper.validateHtmlUtf8(mvc, "/obey/setup", "!DOCTYPE");
	}

	@Test
	public void testObeySetupEdit() throws Exception {
		doNothing().when(databaseService).saveSettings(any());

		helper.checkUnauthorized(mvc, "/obey/setup/edit");
		helper.checkAuthorizedWithoutCsrf(mvc, "/obey/setup/edit");
		helper.checkAuthorizedWithCsrfRedirect(mvc, "/obey/setup/edit", "/**/obey/setup");
	}

	@Test
	public void testObeySubDigest() throws Exception {
		helper.checkRedirect(mvc, "/obey/sub-digest", "**/ds-auth-login");
	}

	@Test
	@WithMockUser
	public void testObeySubDigestAuthorized() throws Exception {
		helper.validateHtmlUtf8(mvc, "/obey/sub-digest", "!DOCTYPE");
	}

	@Test
	public void testObeySubDigestEdit() throws Exception {
		doNothing().when(databaseService).saveDigestSub(any());

		helper.checkUnauthorized(mvc, "/obey/sub-digest/edit");
		helper.checkAuthorizedWithoutCsrf(mvc, "/obey/sub-digest/edit");
		helper.checkAuthorizedWithCsrfRedirectParam(mvc,
			"/obey/sub-digest/edit", "/**/obey/sub-digest",
			"chatId", "100", "chatName", "unknown");
	}

	@Test
	public void testObeySubDigestDelete() throws Exception {
		doNothing().when(databaseService).deleteDigestSub(anyLong());

		helper.checkUnauthorized(mvc, "/obey/sub-digest/delete/100");
		helper.checkAuthorizedWithoutCsrf(mvc, "/obey/sub-digest/delete/100");
		helper.checkAuthorizedWithCsrfRedirect(mvc,
			"/obey/sub-digest/delete/100", "/**/obey/sub-digest");
	}

	@Test
	public void testObeySubMotofan() throws Exception {
		helper.checkRedirect(mvc, "/obey/sub-motofan", "**/ds-auth-login");
	}

	@Test
	@WithMockUser
	public void testObeySubMotofanAuthorized() throws Exception {
		helper.validateHtmlUtf8(mvc, "/obey/sub-motofan", "!DOCTYPE");
	}

	@Test
	public void testObeySubMotofanEdit() throws Exception {
		doNothing().when(databaseService).saveMotofanSub(any());

		helper.checkUnauthorized(mvc, "/obey/sub-motofan/edit");
		helper.checkAuthorizedWithoutCsrf(mvc, "/obey/sub-motofan/edit");
		helper.checkAuthorizedWithCsrfRedirectParam(mvc,
			"/obey/sub-motofan/edit", "/**/obey/sub-motofan",
			"chatId", "100", "chatName", "unknown");
	}

	@Test
	public void testObeySubMotofanDelete() throws Exception {
		doNothing().when(databaseService).deleteMotofanSub(anyLong());

		helper.checkUnauthorized(mvc, "/obey/sub-motofan/delete/100");
		helper.checkAuthorizedWithoutCsrf(mvc, "/obey/sub-motofan/delete/100");
		helper.checkAuthorizedWithCsrfRedirect(mvc,
			"/obey/sub-motofan/delete/100", "/**/obey/sub-motofan");
	}

	@Test
	public void testObeyMotofanUpdate() throws Exception {
		helper.checkRedirect(mvc, "/obey/motofan/update", "**/ds-auth-login");
	}

	@Test
	@WithMockUser
	public void testObeyMotofanUpdateAuthorized() throws Exception {
		helper.checkRedirect(mvc, "/obey/motofan/update", "/**/obey/sub-motofan");
	}

	@Test
	public void testObeySubGreeting() throws Exception {
		helper.checkRedirect(mvc, "/obey/sub-greeting", "**/ds-auth-login");
	}

	@Test
	@WithMockUser
	public void testObeySubGreetingAuthorized() throws Exception {
		helper.validateHtmlUtf8(mvc, "/obey/sub-greeting", "!DOCTYPE");
	}

	@Test
	public void testObeySubGreetingEdit() throws Exception {
		doNothing().when(databaseService).addChatToGreetingIgnores(anyLong());

		helper.checkUnauthorized(mvc, "/obey/sub-greeting/edit");
		helper.checkAuthorizedWithoutCsrf(mvc, "/obey/sub-greeting/edit");
		helper.checkAuthorizedWithCsrfRedirectParam(mvc,
			"/obey/sub-greeting/edit", "/**/obey/sub-greeting",
			"chatId", "100", "chatName", "unknown");
	}

	@Test
	public void testObeySubGreetingDelete() throws Exception {
		doNothing().when(databaseService).deleteChatFromGreetingIgnores(anyLong());

		helper.checkUnauthorized(mvc, "/obey/sub-greeting/delete/100");
		helper.checkAuthorizedWithoutCsrf(mvc, "/obey/sub-greeting/delete/100");
		helper.checkAuthorizedWithCsrfRedirect(mvc,
			"/obey/sub-greeting/delete/100", "/**/obey/sub-greeting");
	}

	@Test
	public void testObeySubCovid() throws Exception {
		helper.checkRedirect(mvc, "/obey/sub-covid", "**/ds-auth-login");
	}

	@Test
	@WithMockUser
	public void testObeySubCovidAuthorized() throws Exception {
		helper.validateHtmlUtf8(mvc, "/obey/sub-covid", "!DOCTYPE");
	}

	@Test
	public void testObeySubCovidEdit() throws Exception {
		doNothing().when(databaseService).saveCovidSub(any());

		helper.checkUnauthorized(mvc, "/obey/sub-covid/edit");
		helper.checkAuthorizedWithoutCsrf(mvc, "/obey/sub-covid/edit");
		helper.checkAuthorizedWithCsrfRedirectParam(mvc,
			"/obey/sub-covid/edit", "/**/obey/sub-covid",
			"chatId", "100", "chatName", "unknown");
	}

	@Test
	public void testObeySubCovidDelete() throws Exception {
		doNothing().when(databaseService).deleteCovidSub(anyLong());

		helper.checkUnauthorized(mvc, "/obey/sub-covid/delete/100");
		helper.checkAuthorizedWithoutCsrf(mvc, "/obey/sub-covid/delete/100");
		helper.checkAuthorizedWithCsrfRedirect(mvc,
			"/obey/sub-covid/delete/100", "/**/obey/sub-covid");
	}

	@Test
	public void testObeyCovidSend() throws Exception {
		helper.checkRedirect(mvc, "/obey/covid/send", "**/ds-auth-login");
	}

	@Test
	@WithMockUser
	public void testObeyCovidSendAuthorized() throws Exception {
		helper.checkRedirect(mvc, "/obey/covid/send", "/**/obey/sub-covid");
	}

	@Test
	public void testObeySubCovidUa() throws Exception {
		helper.checkRedirect(mvc, "/obey/sub-covid-ua", "**/ds-auth-login");
	}

	@Test
	@WithMockUser
	public void testObeySubCovidUaAuthorized() throws Exception {
		helper.validateHtmlUtf8(mvc, "/obey/sub-covid-ua", "!DOCTYPE");
	}

	@Test
	public void testObeySubCovidUaEdit() throws Exception {
		doNothing().when(databaseService).saveCovidUaSub(any());

		helper.checkUnauthorized(mvc, "/obey/sub-covid-ua/edit");
		helper.checkAuthorizedWithoutCsrf(mvc, "/obey/sub-covid-ua/edit");
		helper.checkAuthorizedWithCsrfRedirectParam(mvc,
			"/obey/sub-covid-ua/edit", "/**/obey/sub-covid-ua",
			"chatId", "100", "chatName", "unknown");
	}

	@Test
	public void testObeySubCovidUaDelete() throws Exception {
		doNothing().when(databaseService).deleteCovidUaSub(anyLong());

		helper.checkUnauthorized(mvc, "/obey/sub-covid-ua/delete/100");
		helper.checkAuthorizedWithoutCsrf(mvc, "/obey/sub-covid-ua/delete/100");
		helper.checkAuthorizedWithCsrfRedirect(mvc,
			"/obey/sub-covid-ua/delete/100", "/**/obey/sub-covid-ua");
	}

	@Test
	public void testObeySubRate() throws Exception {
		helper.checkRedirect(mvc, "/obey/sub-rate", "**/ds-auth-login");
	}

	@Test
	public void testObeySubRateAuthorized() throws Exception {
		helper.validateHtmlUtf8(mvc, "/obey/sub-rate", "!DOCTYPE");
	}

	@Test
	public void testObeySubRateEdit() throws Exception {
		doNothing().when(databaseService).saveRateSub(any());

		helper.checkUnauthorized(mvc, "/obey/sub-rate/edit");
		helper.checkAuthorizedWithoutCsrf(mvc, "/obey/sub-rate/edit");
		helper.checkAuthorizedWithCsrfRedirectParam(mvc,
			"/obey/sub-rate/edit", "/**/obey/sub-rate",
			"chatId", "100", "chatName", "unknown");
	}

	@Test
	public void testObeySubRateDelete() throws Exception {
		doNothing().when(databaseService).deleteRateSub(anyLong());

		helper.checkUnauthorized(mvc, "/obey/sub-rate/delete/100");
		helper.checkAuthorizedWithoutCsrf(mvc, "/obey/sub-rate/delete/100");
		helper.checkAuthorizedWithCsrfRedirect(mvc,
			"/obey/sub-rate/delete/100", "/**/obey/sub-rate");
	}

	@Test
	public void testObeyExchange() throws Exception {
		helper.checkRedirect(mvc, "/obey/exchange", "**/ds-auth-login");
	}

	@Test
	@WithMockUser
	public void testObeyExchangeAuthorized() throws Exception {
		helper.validateHtmlUtf8(mvc, "/obey/exchange", "!DOCTYPE");
	}

	@Test
	public void testObeyExchangeEdit() throws Exception {
		doNothing().when(databaseService).saveExchange(any());

		helper.checkUnauthorized(mvc, "/obey/exchange/edit");
		helper.checkAuthorizedWithoutCsrf(mvc, "/obey/exchange/edit");
		helper.checkAuthorizedWithCsrfRedirect(mvc, "/obey/exchange/edit", "/**/obey/exchange");
	}

	@Test
	public void testObeyExchangeUpdate() throws Exception {
		helper.checkRedirect(mvc, "/obey/exchange/update", "**/ds-auth-login");
	}

	@Test
	@WithMockUser
	public void testObeyExchangeUpdateAuthorized() throws Exception {
		helper.checkRedirect(mvc, "/obey/exchange/update", "/**/obey/exchange");
	}

	@Test
	public void testObeyExchangeSend() throws Exception {
		helper.checkRedirect(mvc, "/obey/exchange/send", "**/ds-auth-login");
	}

	@Test
	@WithMockUser
	public void testObeyExchangeSendAuthorized() throws Exception {
		helper.checkRedirect(mvc, "/obey/exchange/send", "/**/obey/sub-rate");
	}

	@Test
	public void testObeySend() throws Exception {
		helper.checkRedirect(mvc, "/obey/send", "**/ds-auth-login");
	}

	@Test
	@WithMockUser
	public void testObeySendAuthorized() throws Exception {
		helper.validateHtmlUtf8(mvc, "/obey/send", "!DOCTYPE");
	}

	@Test
	public void testObeySendChat() throws Exception {
		helper.checkUnauthorized(mvc, "/obey/send/chat");
		helper.checkAuthorizedWithoutCsrf(mvc, "/obey/send/chat");
		helper.checkAuthorizedWithCsrfRedirect(mvc, "/obey/send/chat", "/**/obey/send");
	}

	@Test
	public void testObeyMember() throws Exception {
		helper.checkRedirect(mvc, "/obey/member", "**/ds-auth-login");
	}

	@Test
	@WithMockUser
	public void testObeyMemberAuthorized() throws Exception {
		helper.validateHtmlUtf8(mvc, "/obey/member", "!DOCTYPE");
	}

	@Test
	public void testObeyMemberEdit() throws Exception {
		doNothing().when(databaseService).saveMember(any());

		helper.checkUnauthorized(mvc, "/obey/member/edit");
		helper.checkAuthorizedWithoutCsrf(mvc, "/obey/member/edit");
		helper.checkAuthorizedWithCsrfRedirectWrongRole(mvc, "/obey/member/edit", "/**/obey");
	}

	@Test
	public void testObeyMemberEditAuthorized() throws Exception {
		doNothing().when(databaseService).saveMember(any());

		helper.checkUnauthorized(mvc, "/obey/member/edit");
		helper.checkAuthorizedWithoutCsrf(mvc, "/obey/member/edit");
		helper.checkAuthorizedWithCsrfRedirect(mvc, "/obey/member/edit", "/**/obey/member");
	}

	@Test
	public void testObeyMemberDelete() throws Exception {
		doNothing().when(databaseService).deleteMember(anyLong());

		helper.checkUnauthorized(mvc, "/obey/member/delete/100");
		helper.checkAuthorizedWithoutCsrf(mvc, "/obey/member/delete/100");
		helper.checkAuthorizedWithCsrfRedirectWrongRole(mvc, "/obey/member/delete/100", "/**/obey");
	}

	@Test
	public void testObeyMemberDeleteAuthorized() throws Exception {
		doNothing().when(databaseService).deleteMember(anyLong());

		helper.checkUnauthorized(mvc, "/obey/member/delete/100");
		helper.checkAuthorizedWithoutCsrf(mvc, "/obey/member/delete/100");
		helper.checkAuthorizedWithCsrfRedirect(mvc, "/obey/member/delete/100", "/**/obey/member");
	}

	@Test
	public void testObeyFlat() throws Exception {
		helper.checkRedirect(mvc, "/obey/flat", "**/ds-auth-login");
	}

	@Test
	@WithMockUser
	public void testObeyFlatAuthorized() throws Exception {
		helper.validateHtmlUtf8(mvc, "/obey/flat", "!DOCTYPE");
	}

	@Test
	public void testObeyFlatEdit() throws Exception {
		doNothing().when(databaseService).saveFlatSettings(any());

		helper.checkUnauthorized(mvc, "/obey/flat/edit");
		helper.checkAuthorizedWithoutCsrf(mvc, "/obey/flat/edit");
		helper.checkAuthorizedWithCsrfRedirectWrongRole(mvc, "/obey/member/delete/100", "/**/obey");
	}

	@Test
	public void testObeyFlatEditAuthorized() throws Exception {
		doNothing().when(databaseService).saveFlatSettings(any());

		helper.checkUnauthorized(mvc, "/obey/flat/edit");
		helper.checkAuthorizedWithoutCsrf(mvc, "/obey/flat/edit");
		helper.checkAuthorizedWithCsrfRedirect(mvc, "/obey/flat/edit", "/**/obey/flat");
	}

	@Test
	public void testObeyFlatSend() throws Exception {
		helper.checkRedirect(mvc, "/obey/flat/send", "**/ds-auth-login");
		helper.checkAuthorizedWithCsrfRedirectWrongRole(mvc, "/obey/flat/send", "/**/obey");
	}

	@Test
	@WithMockUser
	public void testObeyFlatSendAuthorized() throws Exception {
		helper.checkRedirect(mvc, "/obey/flat/send", "/**/obey");
	}
}
