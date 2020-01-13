package ru.exlmoto.digest.site.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SiteRestController {
	@GetMapping("/")
	public String root() {
		return "Hello World!";
	}
}
