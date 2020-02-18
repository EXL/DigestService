package ru.exlmoto.digest.site.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.exlmoto.digest.repository.BotDigestRepository;
import ru.exlmoto.digest.site.configuration.SiteConfiguration;
import ru.exlmoto.digest.site.form.GoToPageForm;

@Controller
public class SiteController {
	private final SiteConfiguration config;
	private final BotDigestRepository repository;

	public SiteController(SiteConfiguration config, BotDigestRepository repository) {
		this.config = config;
		this.repository = repository;
	}

	@RequestMapping(path = "/")
	public String index(@RequestParam(name = "page", required = false, defaultValue = "1") Integer page, Model model) {
		System.out.println("Page: " + page);



		model.addAttribute("paginator", new GoToPageForm("/"));
		model.addAttribute("all", pageCount + 1);
		model.addAttribute("startAux", startPage - ((pagePagin / 2) + 1));
		model.addAttribute("endAux", startPage + (pagePagin / 2));

		return "index";
	}
}
