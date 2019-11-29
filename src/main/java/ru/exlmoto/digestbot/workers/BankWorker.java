package ru.exlmoto.digestbot.workers;

import org.slf4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import ru.exlmoto.digestbot.services.impl.BankService;
import ru.exlmoto.digestbot.workers.banks.Bank;
import ru.exlmoto.digestbot.workers.banks.Metal;
import ru.exlmoto.digestbot.workers.banks.impl.*;
import ru.exlmoto.digestbot.yaml.impl.YamlRatesIndexHelper;

import java.math.BigDecimal;

@Service
public class BankWorker {
	private final BankService mBankService;
	private final YamlRatesIndexHelper mYamlRatesIndexHelper;
	private Logger mBotLogger = null;

	private final BankRu mBankRu;
	private final BankUa mBankUa;
	private final BankBy mBankBy;
	private final BankKz mBankKz;
	private final MetalRu mMetalRu;

	@Autowired
	public BankWorker(final BankService aBankService,
	                  final YamlRatesIndexHelper aYamlRatesIndexHelper) {
		mBankService = aBankService;
		mYamlRatesIndexHelper = aYamlRatesIndexHelper;

		mBankRu = new BankRu();
		mBankUa = new BankUa();
		mBankBy = new BankBy();
		mBankKz = new BankKz();
		mMetalRu = new MetalRu(mYamlRatesIndexHelper);
	}

	public void setBotLogger(final Logger aBotLogger) {
		mBotLogger = aBotLogger;
	}

	@Scheduled(cron = "${digestbot.crawler.rates.cron}")
	public void updateAllBanks() {
		mBotLogger.info("=> Crawling currencies data.");

		mBotLogger.info("==> Crawling BankRu.");
		if (!updateBank(mBankRu, mBankService.receiveBankRuData())) {
			mBotLogger.info("===> Crawling mirror BankRu.");
			updateBank(mBankRu, mBankService.receiveBankRuMirrorData());
		}

		mBotLogger.info("==> Crawling BankUa.");
		if (!updateBank(mBankUa, mBankService.receiveBankUaData())) {
			mBotLogger.info("===> Crawling mirror BankUa.");
			mBankUa.setIsMirror(true);
			updateBank(mBankUa, mBankService.receiveBankUaMirrorData());
		}

		mBotLogger.info("==> Crawling BankBy.");
		updateBank(mBankBy, mBankService.receiveBankByData());

		mBotLogger.info("==> Crawling BankKz.");
		updateBank(mBankKz, mBankService.receiveBankKzData());

		mBotLogger.info("==> Crawling MetalRu.");
		updateMetal(mMetalRu, mBankService.receiveBankMetalData());

		mBotLogger.info("=> End crawling currencies data.");
	}

	private boolean updateMetal(final Metal aMetal,
	                            final Pair<Boolean, String> aServerAnswer) {
		final String lServerAnswerString = aServerAnswer.getSecond();
		final boolean lIsHtml = lServerAnswerString.startsWith("<");
		if (aServerAnswer.getFirst() && lIsHtml) {
			return aMetal.parseHtml(lServerAnswerString, mBotLogger);
		} else {
			if (mBotLogger != null) {
				mBotLogger.error(String.format("Cannot get metal rates list: '%s' '%s'.",
					aMetal.getClass(), lServerAnswerString));
			}
			return false;
		}
	}

	private boolean updateBank(final Bank aBank,
	                           final Pair<Boolean, String> aServerAnswer) {
		final String lServerAnswerString = aServerAnswer.getSecond();
		final boolean lIsXml = lServerAnswerString.startsWith("<?xml") || lServerAnswerString.startsWith("<?XML");
		if (aServerAnswer.getFirst() && lIsXml) {
			return aBank.parseXml(lServerAnswerString, mBotLogger);
		} else {
			mBotLogger.error(String.format("Cannot get bank currencies list: '%s' '%s'.",
				aBank.getClass(), lServerAnswerString));
			aBank.clearValues();
			return false;
		}
	}

	public BankRu getBankRu() {
		return mBankRu;
	}

	public BankUa getBankUa() {
		return mBankUa;
	}

	public BankBy getBankBy() {
		return mBankBy;
	}

	public BankKz getBankKz() {
		return mBankKz;
	}

	public MetalRu getMetalRu() {
		return mMetalRu;
	}

	public String determineDifference(final BigDecimal aValue) {
		if (aValue == null) {
			return "\n";
		}
		return mYamlRatesIndexHelper.getTitleByKey("rate.change") + addSignAndIcon(aValue) + '\n';
	}

	private String addSignAndIcon(final BigDecimal aValue) {
		String lAnswer = " ";
		if (aValue.compareTo(BigDecimal.ZERO) < 0) {
			lAnswer += String.format("%.4f", aValue) + ' ' +
				           mYamlRatesIndexHelper.getTitleByKey("rate.change.down");
		} else {
			lAnswer += '+' + String.format("%.4f", aValue) + ' ' +
				           mYamlRatesIndexHelper.getTitleByKey("rate.change.up");
		}
		return lAnswer;
	}
}
