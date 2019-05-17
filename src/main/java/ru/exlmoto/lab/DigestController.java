package ru.exlmoto.lab;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import org.thymeleaf.util.ArrayUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;

@Controller
public class DigestController {
    @Autowired
    private IDigestRepository digestRepository;

    @Autowired
    private IDigestUserRepository digestUserRepository;

    @Autowired
    private MessageSource messageSource;

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

    @Value("${digest.site.lang}")
    private String siteLanguage;

    @RequestMapping(path = "/digest")
    public String digest(@RequestParam(name = "page", required = false) String page, Model model) {
        int startPage = getValidHumanCurrentPage(page,((int) digestRepository.count() - 1) / postPerPage);
        Page<DigestEntity> digestEntities = digestRepository.findAll(PageRequest.of(startPage, postPerPage));

        DigestModelFactory digestModelFactory = new DigestModelFactory();
        for (DigestEntity digestEntity : digestEntities) {
            String username = getUserNameOrAvatarById(digestEntity.getAuthor(), true, digestUserRepository);
            String avatar = getUserNameOrAvatarById(digestEntity.getAuthor(), false, digestUserRepository);
            String group = getUserGroup(username);
            String date = getDataAndTime(digestEntity.getDate());
            digestModelFactory.addDigest(username, group, avatar, digestEntity.getDigest(), date);
        }
        model.addAttribute("digests", digestModelFactory.getItems());
        model.addAttribute("count", digestModelFactory.getSize());
        return "digest";
    }

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
        sessionLocaleResolver.setDefaultLocale(Locale.forLanguageTag(siteLanguage));
        return sessionLocaleResolver;
    }

    private String getUserGroup(String username) {
        if (ArrayUtils.contains(digestAdmins, username)) {
            return getI18nString("digest.group.admins");
        } else if (ArrayUtils.contains(digestCoords, username)) {
            return getI18nString("digest.group.coords");
        } else if (ArrayUtils.contains(digestModers, username)) {
            return getI18nString("digest.group.moders");
        } else {
            return getI18nString("digest.group.users");
        }
    }

    private String getDataAndTime(Long timestamp) {
        Date date = new Date((long) timestamp * 1000);
        DateFormat dateFormat = new SimpleDateFormat("'%date%:' dd-MMM-yyyy '| %time%:' HH:mm:ss", Locale.US);
        dateFormat.setTimeZone(TimeZone.getDefault());

        return dateFormat.format(date).replace("%date%",
                getI18nString("digest.date.date")).replace("%time%",
                getI18nString("digest.date.time"));
    }

    private String getI18nString(String path) {
        return messageSource.getMessage(path, null, LocaleContextHolder.getLocale());
    }

    private String getUserNameOrAvatarById(Integer id, boolean isUsername, IDigestUserRepository repository) {
        Optional<DigestUserEntity> digestUserEntityOptional = repository.findById(id);
        if (digestUserEntityOptional.isPresent()) {
            if (isUsername) {
                return digestUserEntityOptional.get().getUsername();
            } else {
                return digestUserEntityOptional.get().getAvatar();
            }
        } else {
            return getI18nString("digest.name.unknown");
        }
    }

    private int getValidHumanCurrentPage(String pageParam, int pageCount) {
        int pageCurrent;
        try {
            pageCurrent = Integer.valueOf(pageParam);
        } catch (NumberFormatException nfe) {
            pageCurrent = pageCount + 1;
        }

        if (pageCurrent < 1) {
            pageCurrent = 1;
        }

        if (pageCurrent > pageCount) {
            pageCurrent = pageCount + 1;
        }

        return pageCurrent - 1;
    }
}
