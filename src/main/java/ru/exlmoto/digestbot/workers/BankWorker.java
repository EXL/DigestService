package ru.exlmoto.digestbot.workers;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import ru.exlmoto.digestbot.services.impl.BankService;
import ru.exlmoto.digestbot.workers.banks.Bank;
import ru.exlmoto.digestbot.workers.banks.Metal;
import ru.exlmoto.digestbot.workers.banks.impl.*;

@Service
public class BankWorker {
	private final BankService mBankService;
	private Logger mBotLogger = null;

	private final BankRu mBankRu;
	private final BankUa mBankUa;
	private final BankBy mBankBy;
	private final BankKz mBankKz;
	private final MetalRu mMetalRu;

	@Autowired
	public BankWorker(final BankService aBankService) {
		mBankService = aBankService;

		mBankRu = new BankRu();
		mBankUa = new BankUa();
		mBankBy = new BankBy();
		mBankKz = new BankKz();
		mMetalRu = new MetalRu();
	}

	public void setBotLogger(final Logger aBotLogger) {
		mBotLogger = aBotLogger;
	}

	public void updateAllBanks() {
		mBotLogger.info("Starting crawling currencies data!");
		if (!updateBank(mBankRu, mBankService.receiveBankRuData())) {
			updateBank(mBankRu, mBankService.receiveBankRuMirrorData());
		}
		if (!updateBank(mBankUa, mBankService.receiveBankUaData())) {
			updateBank(mBankUa, mBankService.receiveBankUaMirrorData());
		}
		updateBank(mBankBy, mBankService.receiveBankByData());
		updateBank(mBankKz, mBankService.receiveBankKzData());
		updateMetal(mMetalRu, mBankService.receiveBankMetalData());
	}

	private boolean updateMetal(final Metal aMetal,
	                            final Pair<Boolean, String> aServerAnswer) {
		final String lServerAnswerString = aServerAnswer.getSecond();
		final boolean lIsHtml = lServerAnswerString.startsWith("<!DOCTYPE HTML") ||
			                    lServerAnswerString.startsWith("<!doctype html");
		if (aServerAnswer.getFirst() && lIsHtml) {
			aMetal.parseHtml(lServerAnswerString);
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
			aBank.parseXml(lServerAnswerString);
			return true;
		} else {
			if (mBotLogger != null) {
				mBotLogger.error(String.format("Cannot get bank currencies list: '%s' '%s'.",
					aBank.getClass(), lServerAnswerString));
			}
			return false;
		}
	}
}
