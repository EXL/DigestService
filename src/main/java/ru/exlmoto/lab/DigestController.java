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
import ru.exlmoto.digestbot.entities.DigestEntity;
import ru.exlmoto.digestbot.entities.DigestUserEntity;
import ru.exlmoto.digestbot.repos.IDigestEntriesRepository;
import ru.exlmoto.digestbot.repos.IDigestUsersRepository;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;

@Controller
public class DigestController {
    @Autowired
    private IDigestEntriesRepository digestRepository;

    @Autowired
    private IDigestUsersRepository digestUserRepository;

    @Autowired
    private MessageSource messageSource;

    @Value("${digest.post.max}")
    private int postPerPage;

    @Value("${digest.page.pagin}")
    private int pagePagin;

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

    @RequestMapping(path = "/digest/search")
    public String search(@RequestParam(name = "page", required = false) String page,
                         @RequestParam(name = "text", required = false) String search, Model model, SearchForm form) {
        int startPage = getValidHumanCurrentPageSearch(page);
        Iterable<DigestUserEntity> digestUserEntities = digestUserRepository.findByUsernameContainingIgnoreCase(search);
        Page<DigestEntity> digestEntities = digestRepository.findByDigestContainingIgnoreCase(
                PageRequest.of(startPage - 1, postPerPage), search);

        int pageCount = ((int) digestEntities.getTotalElements() - 1) / postPerPage;

        model.addAttribute("inNames", digestUserEntities);
        model.addAttribute("inDigests", digestEntities);

        // Pagination routine.
        startPage += 1;
        model.addAttribute("pageForm", new GoToPageForm("/digest/search"));
        model.addAttribute("current", startPage);
        model.addAttribute("all", pageCount + 1);
        model.addAttribute("startAux", startPage - ((pagePagin / 2) + 1));
        model.addAttribute("endAux", startPage + (pagePagin / 2));

        return "search";
    }

    @RequestMapping(path = "/digest")
    public String digest(@RequestParam(name = "page", required = false) String page, Model model) {
        int pageCount = ((int) digestRepository.count() - 1) / postPerPage;


        int startPage = getValidHumanCurrentPage(page, pageCount);
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
        model.addAttribute("pageForm", new GoToPageForm("/digest"));
        model.addAttribute("searchForm", new SearchForm());

        // Pagination routine.
        startPage += 1;
        model.addAttribute("current", startPage);
        model.addAttribute("all", pageCount + 1);
        model.addAttribute("startAux", startPage - ((pagePagin / 2) + 1));
        model.addAttribute("endAux", startPage + (pagePagin / 2));

        return "digest";
    }

    /*
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
        sessionLocaleResolver.setDefaultLocale(Locale.forLanguageTag(siteLanguage));
        return sessionLocaleResolver;
    }
    */

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

    private String getUserNameOrAvatarById(Integer id, boolean isUsername, IDigestUsersRepository repository) {
        Optional<DigestUserEntity> digestUserEntityOptional = repository.findById(id);
        if (digestUserEntityOptional.isPresent()) {
            if (isUsername) {
                return digestUserEntityOptional.get().getUsername();
            } else {
                return digestUserEntityOptional.get().getAvatarLink();
            }
        } else {
            return getI18nString("digest.name.unknown");
        }
    }

    private int getValidHumanCurrentPageSearch(String pageParam) {
        int pageCurrent = 1;
        try {
            pageCurrent = Integer.valueOf(pageParam) - 1;
        } catch (NumberFormatException ignored) { }
        if (pageCurrent < 0) {
            pageCurrent = 0;
        }
        return pageCurrent;
    }

    private int getValidHumanCurrentPage(String pageParam, int pageCount) {
        int pageCurrent = pageCount;
        try {
            pageCurrent = Integer.valueOf(pageParam) - 1;
        } catch (NumberFormatException ignored) { }
        if (pageCurrent < 0) {
            pageCurrent = 0;
        }
        if (pageCurrent > pageCount) {
            pageCurrent = pageCount;
        }
        return pageCurrent;
    }
}
