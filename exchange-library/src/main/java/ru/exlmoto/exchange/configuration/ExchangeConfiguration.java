package ru.exlmoto.exchange.configuration;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ConfigurationProperties("exchange")
@EnableJpaRepositories("ru.exlmoto.exchange.repository")
@EntityScan("ru.exlmoto.exchange.entity")
public class ExchangeConfiguration {
	private String bankRu;
	private String bankRuMirror;
	private String bankUa;
	private String bankUaMirror;
	private String bankBy;
	private String bankKz;
	private String metalRu;
	private String metalRuMirror;

	public String getBankRu() {
		return bankRu;
	}

	public void setBankRu(String bankRu) {
		this.bankRu = bankRu;
	}

	public String getBankRuMirror() {
		return bankRuMirror;
	}

	public void setBankRuMirror(String bankRuMirror) {
		this.bankRuMirror = bankRuMirror;
	}

	public String getBankUa() {
		return bankUa;
	}

	public void setBankUa(String bankUa) {
		this.bankUa = bankUa;
	}

	public String getBankUaMirror() {
		return bankUaMirror;
	}

	public void setBankUaMirror(String bankUaMirror) {
		this.bankUaMirror = bankUaMirror;
	}

	public String getBankBy() {
		return bankBy;
	}

	public void setBankBy(String bankBy) {
		this.bankBy = bankBy;
	}

	public String getBankKz() {
		return bankKz;
	}

	public void setBankKz(String bankKz) {
		this.bankKz = bankKz;
	}

	public String getMetalRu() {
		return metalRu;
	}

	public void setMetalRu(String metalRu) {
		this.metalRu = metalRu;
	}

	public String getMetalRuMirror() {
		return metalRuMirror;
	}

	public void setMetalRuMirror(String metalRuMirror) {
		this.metalRuMirror = metalRuMirror;
	}
}
