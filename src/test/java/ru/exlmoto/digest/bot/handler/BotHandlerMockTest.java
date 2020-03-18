package ru.exlmoto.digest.bot.handler;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import ru.exlmoto.digest.bot.util.UpdateHelper;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = "bot.silent=true")
public class BotHandlerMockTest {
	@SpyBean
	private BotHandler handler;

	private final UpdateHelper update = new UpdateHelper();

	@Test
	public void testDisableGreetingsByGroup() {
		when(handler.checkGreetingStatus(anyLong())).thenReturn(false);

		/* No output. */
		handler.onNewUsers(update.getNewUsers(4));
		handler.onLeftUser(update.getLeftUser("Left"));
		handler.onNewPhotos(update.getNewPhotos(4));
	}
}
