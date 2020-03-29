package ru.exlmoto.digest.site.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ObeyController {
	@RequestMapping(path = "/obey")
	public String obey(@CookieValue(value = "lang", defaultValue = "ru") String tag) {

		return "obey";
	}
}
