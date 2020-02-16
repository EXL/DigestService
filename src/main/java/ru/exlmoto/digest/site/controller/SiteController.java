package ru.exlmoto.digest.site.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SiteController {
	@RequestMapping(path = "/")
	public String index(@RequestParam(name = "page", required = false, defaultValue = "1") Integer page, Model model) {
		System.out.println("Page: " + page);
		return "index";
	}
}
