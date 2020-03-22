package ru.exlmoto.digest.bot.handler;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import ru.exlmoto.digest.bot.util.UpdateHelper;
import ru.exlmoto.digest.service.DatabaseService;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = "bot.silent=true")
public class BotHandlerMockTest {
	@Autowired
	private BotHandler handler;

	@MockBean
	private DatabaseService service;

	private final UpdateHelper update = new UpdateHelper();

	@Test
	public void testDisableGreetingsByGroup() {
		when(service.checkGreeting(anyLong())).thenReturn(false);

		/* No output. */
		handler.onNewUsers(update.getNewUsers(4));
		handler.onLeftUser(update.getLeftUser("Left"));
		handler.onNewPhotos(update.getNewPhotos(4));
	}
}
