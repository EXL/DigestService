package ru.exlmoto.digestbot.workers;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
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

	public void updateAllBanks() {
		mBotLogger.info("=> Start crawling currencies data...");

		mBotLogger.info("==> Start crawling BankRu...");
		if (!updateBank(mBankRu, mBankService.receiveBankRuData())) {
			updateBank(mBankRu, mBankService.receiveBankRuMirrorData());
		}
		mBotLogger.info("==> End crawling BankRu.");

		mBotLogger.info("==> Start crawling BankUa...");
		if (!updateBank(mBankUa, mBankService.receiveBankUaData())) {
			mBankUa.setIsMirror(true);
			updateBank(mBankUa, mBankService.receiveBankUaMirrorData());
		}
		mBotLogger.info("==> End crawling BankUa.");

		mBotLogger.info("==> Start crawling BankBy...");
		updateBank(mBankBy, mBankService.receiveBankByData());
		mBotLogger.info("==> End crawling BankBy.");

		mBotLogger.info("==> Start crawling BankKz...");
		updateBank(mBankKz, mBankService.receiveBankKzData());
		mBotLogger.info("==> End crawling BankKz.");

		mBotLogger.info("==> Start crawling MetalRu...");
		updateMetal(mMetalRu, mBankService.receiveBankMetalData());
		mBotLogger.info("==> End crawling MetalRu.");

		mBotLogger.info("=> End crawling currencies data.");
	}

	private boolean updateMetal(final Metal aMetal,
	                            final Pair<Boolean, String> aServerAnswer) {
		final String lServerAnswerString = aServerAnswer.getSecond();
		final boolean lIsHtml = lServerAnswerString.startsWith("<");
		if (aServerAnswer.getFirst() && lIsHtml) {
			aMetal.parseHtml(lServerAnswerString, mBotLogger);
			return true;
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
			aBank.parseXml(lServerAnswerString, mBotLogger);
			return true;
		} else {
			mBotLogger.error(String.format("Cannot get bank currencies list: '%s' '%s'.",
				aBank.getClass(), lServerAnswerString));
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
		return mYamlRatesIndexHelper.getTitleByKey("rate.change") +
			       addSignAndIcon(aValue) + '\n';
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
