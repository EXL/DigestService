package ru.exlmoto.lab;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    @Value("${digestbot.site.post.per.page}")
    private int postPerPage;

    @Value("${digestbot.site.paginator.width}")
    private int pagePagin;

    @Value("${digestbot.admins}")
    private String[] digestAdmins;

    @Value("${digestbot.moderators}")
    private String[] digestModers;

    @Value("${digestbot.chat.motofan}")
    private long chatRoom;

    @Value("${general.lang}")
    private String siteLanguage;

    // TODO: Find digest in chat id where id is not motofan id?
    @RequestMapping(path = "/digest/search")
    public String search(@RequestParam(name = "page", required = false) String page,
                         @RequestParam(name = "text", required = false) String search, Model model, SearchForm form) {
        if (search != null && !search.isEmpty()) {
            int startPage = getValidHumanCurrentPage(page, Integer.MAX_VALUE);
            Iterable<DigestUserEntity> digestUserEntities = digestUserRepository.findByUsernameContainingIgnoreCase(search);
            Page<DigestEntity> digestEntities = digestRepository.findByDigestContainingIgnoreCase(
                    PageRequest.of(startPage, postPerPage, Sort.by(Sort.Order.desc("id"))), search);

            int pageCount = ((int) digestEntities.getTotalElements() - 1) / postPerPage;
            if (startPage > pageCount || digestEntities.isEmpty()) {
                pageCount = -1;
            }

            model.addAttribute("inNames", digestUserEntities);
            model.addAttribute("inDigests", digestEntities);

            // Pagination routine.
            startPage += 1;
            model.addAttribute("pageForm", new GoToPageForm("/digest/search?text=" + search, search));
            model.addAttribute("current", startPage);
            model.addAttribute("all", pageCount + 1);
            model.addAttribute("startAux", startPage - ((pagePagin / 2) + 1));
            model.addAttribute("endAux", startPage + (pagePagin / 2));
        }
        return "search";
    }

    @RequestMapping(path = "/digest")
    public String digest(@RequestParam(name = "page", required = false) String page, Model model) {
        int pageCount = ((int) digestRepository.count() - 1) / postPerPage;

        int startPage = getValidHumanCurrentPage(page, pageCount);
        Page<DigestEntity> digestEntities = digestRepository.findAll(PageRequest.of(startPage, postPerPage));

        DigestModelFactory digestModelFactory = new DigestModelFactory();
        for (DigestEntity digestEntity : digestEntities) {
            String username = getUserNameOrAvatarById(digestEntity.getAuthor(), true, digestUserRepository, false);
            String avatar = getUserNameOrAvatarById(digestEntity.getAuthor(), false, digestUserRepository, false);
            String group = getUserGroup(username);
            String date = getDataAndTime(digestEntity.getDate());
            String html = getUserNameOrAvatarById(digestEntity.getAuthor(), true, digestUserRepository, true);
            digestModelFactory.addDigest(username, group, avatar, digestEntity.getHtml(), date, html);
        }
        model.addAttribute("digests", digestModelFactory.getItems());
        model.addAttribute("count", digestModelFactory.getSize());
        model.addAttribute("pageForm", new GoToPageForm("/digest", null));
        model.addAttribute("searchForm", new SearchForm());

        // Pagination routine.
        startPage += 1;
        model.addAttribute("current", startPage);
        model.addAttribute("all", pageCount + 1);
        model.addAttribute("startAux", startPage - ((pagePagin / 2) + 1));
        model.addAttribute("endAux", startPage + (pagePagin / 2));

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

    private String getUserNameOrAvatarById(Integer id, boolean isUsername, IDigestUsersRepository repository, boolean html) {
        Optional<DigestUserEntity> digestUserEntityOptional = repository.findById(id);
        if (digestUserEntityOptional.isPresent()) {
            if (isUsername) {
                return (html) ? digestUserEntityOptional.get().getUsername_html() :
                        digestUserEntityOptional.get().getUsername();
            } else {
                return digestUserEntityOptional.get().getAvatarLink();
            }
        } else {
            return getI18nString("digest.name.unknown");
        }
    }

    private static int getValidHumanCurrentPage(String pageParam, int pageCount) {
        int pageCurrent = (pageCount != Integer.MAX_VALUE) ? pageCount : 0;
        try {
            pageCurrent = Integer.parseInt(pageParam) - 1;
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
