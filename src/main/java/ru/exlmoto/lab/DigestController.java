package ru.exlmoto.lab;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@Controller
public class DigestController {
    @Autowired
    private IDigestRepository digestRepository;

    @Autowired
    private IDigestUserRepository digestUserRepository;

    @Value("${digest.post.max}")
    private int postPerPage;

    @Value("${digest.list.admins}")
    private String[] digestAdmins;

    @Value("${digest.list.coords}")
    private String[] digestCoords;

    @Value("${digest.list.moders}")
    private String[] digestModers;

    @Value("${digest.chat.room}")
    private long chatRoom;

    @RequestMapping(path = "/digest")
    public String digest(@RequestParam(name = "page", required = false) String page, Model model) {
        int pageCount = ((int) digestRepository.count() - 1) / postPerPage;

        int pageCurrent;
        try {
            pageCurrent = Integer.valueOf(page);
        } catch (NumberFormatException nfe) {
            pageCurrent = pageCount + 1;
        }

        if (pageCurrent < 1) {
            pageCurrent = 1;
        }

        Page<DigestEntity> digestEntities = digestRepository.findAll(PageRequest.of(pageCurrent - 1, postPerPage));

        DigestModelFactory digestModelFactory = new DigestModelFactory();

        for (DigestEntity digestEntity : digestEntities) {
            String username = String.valueOf(digestEntity.getAuthor());
            String group = String.valueOf(digestEntity.getAuthor());
            String avatar = String.valueOf(digestEntity.getAuthor());
            String date = String.valueOf(digestEntity.getAuthor());

            digestModelFactory.addDigest(username, group, avatar, digestEntity.getDigest(), date);
        }

        model.addAttribute("digests", digestModelFactory.getItems());

        return "digest";
    }

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
        sessionLocaleResolver.setDefaultLocale(Locale.forLanguageTag("ru"));
        return sessionLocaleResolver;
    }
}
