package ru.exlmoto.motofan.manager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RestManager {
	@Value("${general.connection.timeout}")
	private int timeOutSec;

}
