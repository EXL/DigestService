package ru.exlmoto.digestbot.commands.impl;

import org.springframework.stereotype.Component;

import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.commands.BotCommand;
import ru.exlmoto.digestbot.utils.ReceivedMessage;
import ru.exlmoto.digestbot.yaml.impl.YamlLocalizationHelper;

@Component
public class SubscribeCommand extends BotCommand {
	@Override
	public void run(final DigestBot aDigestBot,
	                final YamlLocalizationHelper aLocalizationHelper,
	                final ReceivedMessage aReceivedMessage) {
		final Long lChatId = aReceivedMessage.getChatId();
		/*
		final boolean[] lIsChatSubscribed = { false };
		aDigestBot.getIMotoFanSubscribersRepository().findAll().forEach(((aMotoFanSubscriberEntity) -> {
			if (aMotoFanSubscriberEntity.getSubscription_id().equals(lChatId)) {
				lIsChatSubscribed[0] = true;
			}
		}));
		 */
		final boolean lChatInDataBaseMotofan = aDigestBot.getIMotoFanSubscribersRepository()
			                                .findOneMotoFanSubscriberEntityBySubscription(lChatId) != null;
		final String lStatusMotofan = getStatusStringAux(lChatInDataBaseMotofan, aLocalizationHelper);

		final boolean lChatInDataBaseDigest = aDigestBot.getIDigestSubscribersRepository()
				.findOneDigestSubscriberEntityBySubscription(lChatId) != null;
		final String lStatusDigest = getStatusStringAux(lChatInDataBaseDigest, aLocalizationHelper);

		aDigestBot.sendMarkdownMessageWithKeyboard(lChatId, aReceivedMessage.getMessageId(),
			String.format(aLocalizationHelper.getLocalizedString("command.subscribe"),
					lChatId, lStatusMotofan, lStatusDigest),
			aDigestBot.getSubscribeKeyboard().getSubscribeKeyboard());
	}

	private String getStatusStringAux(final boolean aStatus, final YamlLocalizationHelper aLocalizationHelper) {
		return (aStatus) ?
				aLocalizationHelper.getLocalizedString("command.subscribe.subscribed") :
				aLocalizationHelper.getLocalizedString("command.subscribe.unsubscribed");
	}
}
