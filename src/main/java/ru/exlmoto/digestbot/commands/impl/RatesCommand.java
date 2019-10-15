package ru.exlmoto.digestbot.commands.impl;

import org.springframework.stereotype.Component;

import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.commands.BotCommand;
import ru.exlmoto.digestbot.utils.RatesKeyboard;
import ru.exlmoto.digestbot.utils.ReceivedMessage;
import ru.exlmoto.digestbot.workers.BankWorker;
import ru.exlmoto.digestbot.workers.banks.impl.BankRu;
import ru.exlmoto.digestbot.yaml.impl.YamlLocalizationHelper;
import ru.exlmoto.digestbot.yaml.impl.YamlRatesIndexHelper;

@Component
public class RatesCommand extends BotCommand {
	@Override
	public void run(final DigestBot aDigestBot,
	                final YamlLocalizationHelper aLocalizationHelper,
	                final ReceivedMessage aReceivedMessage) {
		final RatesKeyboard lRatesKeyboard = aDigestBot.getRatesKeyboard();
		final YamlRatesIndexHelper lYamlRatesIndexHelper = lRatesKeyboard.getYamlRatesIndexHelper();
		final String lTitle = lYamlRatesIndexHelper.getTitleByKey("i.rate.ru") + ' ';
		final String lTitleAux = lYamlRatesIndexHelper.getTitleByKey("rate") + '\n';
		final BankWorker lBankWorker = aDigestBot.getBankWorker();
		final BankRu lBankRu = lBankWorker.getBankRu();

		aDigestBot.sendMarkdownMessageWithKeyboard(aReceivedMessage.getChatId(), aReceivedMessage.getMessageId(),
			lTitle + lBankWorker.determineDifference(lBankRu.getDifference(), lYamlRatesIndexHelper) +
				lTitleAux + lBankRu.generateMarkdownAnswer(),
			lRatesKeyboard.getRatesKeyboard());
	}
}