package ru.exlmoto.digestbot.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.workers.BankWorker;
import ru.exlmoto.digestbot.workers.banks.RateEntity;
import ru.exlmoto.digestbot.yaml.impl.YamlLocalizationHelper;
import ru.exlmoto.digestbot.yaml.impl.YamlRatesIndexHelper;

import java.util.ArrayList;
import java.util.List;

@Component
public class RatesKeyboard {
	private final YamlRatesIndexHelper mYamlRatesIndexHelper;

	@Autowired
	public RatesKeyboard(final YamlRatesIndexHelper aYamlRatesIndexHelper) {
		mYamlRatesIndexHelper = aYamlRatesIndexHelper;
	}

	public InlineKeyboardMarkup getRatesKeyboard() {
		final ArrayList<String> lButtonLabels = mYamlRatesIndexHelper.getButtonLabels();
		final ArrayList<String> lKeys = mYamlRatesIndexHelper.getKeys();

		final InlineKeyboardMarkup lInlineKeyboardMarkup = new InlineKeyboardMarkup();
		final List<List<InlineKeyboardButton>> lKeyboardMarkup = new ArrayList<>();
		List<InlineKeyboardButton> lKeyboardRow = new ArrayList<>();

		for (int i = 0; i < lButtonLabels.size(); i++) {
			lKeyboardRow.add(new InlineKeyboardButton(lButtonLabels.get(i)).setCallbackData(lKeys.get(i)));
		}
		lKeyboardMarkup.add(lKeyboardRow);
		lInlineKeyboardMarkup.setKeyboard(lKeyboardMarkup);
		return lInlineKeyboardMarkup;
	}

	public YamlRatesIndexHelper getYamlRatesIndexHelper() {
		return mYamlRatesIndexHelper;
	}

	public void handleRatesKeyboard(final DigestBot aDigestBot, final CallbackQuery aCallbackQuery) {
		// TODO: ????
		final RatesKeyboard lRatesKeyboard = aDigestBot.getRatesKeyboard();
		final YamlRatesIndexHelper lYamlRatesIndexHelper = lRatesKeyboard.getYamlRatesIndexHelper();
		final YamlLocalizationHelper lLocalizationHelper = aDigestBot.getLocalizationHelper();
		String lTitleAux = lYamlRatesIndexHelper.getTitleByKey("rate") + '\n';
		final BankWorker lBankWorker = aDigestBot.getBankWorker();
		final String lCallbackQueryData = aCallbackQuery.getData();
		String lTitle = null;
		String lButtonLabel = null;
		RateEntity lRateEntity = null;

		if (lCallbackQueryData.startsWith("i.rate.ru")) {
			lTitle = lYamlRatesIndexHelper.getTitleByKey("i.rate.ru") + ' ';
			lButtonLabel = lYamlRatesIndexHelper.getButtonLabel("i.rate.ru");
			lRateEntity = lBankWorker.getBankRu();
		} else if (lCallbackQueryData.startsWith("i.rate.ua")) {
			lTitle = lYamlRatesIndexHelper.getTitleByKey("i.rate.ua") + ' ';
			lButtonLabel = lYamlRatesIndexHelper.getButtonLabel("i.rate.ua");
			lRateEntity = lBankWorker.getBankUa();
		} else if (lCallbackQueryData.startsWith("i.rate.by")) {
			lTitle = lYamlRatesIndexHelper.getTitleByKey("i.rate.by") + ' ';
			lButtonLabel = lYamlRatesIndexHelper.getButtonLabel("i.rate.by");
			lRateEntity = lBankWorker.getBankBy();
		} else if (lCallbackQueryData.startsWith("i.rate.kz")) {
			lTitle = lYamlRatesIndexHelper.getTitleByKey("i.rate.kz") + ' ';
			lButtonLabel = lYamlRatesIndexHelper.getButtonLabel("i.rate.kz");
			lRateEntity = lBankWorker.getBankKz();
		} else if (lCallbackQueryData.startsWith("i.rate.metal")) {
			lTitle = lYamlRatesIndexHelper.getTitleByKey("i.rate.metal") + ' ';
			lButtonLabel = lYamlRatesIndexHelper.getButtonLabel("i.rate.metal");
			lRateEntity = lBankWorker.getMetalRu();
			lTitleAux = "";
		}

		aDigestBot.createAndSendAnswerCallbackQuery(aCallbackQuery.getId(),
			lLocalizationHelper.getLocalizedString("inline.chart.selected") + ' ' + lButtonLabel);

		if (lRateEntity != null) {
			String lMarkdownAnswer = lRateEntity.generateAnswer();
			if (lMarkdownAnswer == null) {
				lMarkdownAnswer = lLocalizationHelper.getLocalizedString("error.rates");
			}
			aDigestBot.editMarkdownMessageWithKeyboard(aCallbackQuery.getMessage().getChatId(),
				aCallbackQuery.getMessage().getMessageId(),
				lTitle + lBankWorker.determineDifference(lRateEntity.getDifference()) +
					lTitleAux + lMarkdownAnswer, lRatesKeyboard.getRatesKeyboard());
		}
	}
}
