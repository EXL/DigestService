package ru.exlmoto.lab;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DigestController {
    @Autowired
    private IDigestRepository digestRepository;

    @Autowired
    private IDigestUsersRepository digestUsersRepository;

    @Autowired
    private IDigestBadRepository digestBadRepository;

    @Autowired
    private IDigestUsersBadRepository digestUsersBadRepository;

    @RequestMapping(path = "/digest")
    public String digest(@RequestParam(name = "page", required = false) String page, Model model) {
        return "digest";
    }

    @RequestMapping(path = "/migrate")
    public @ResponseBody Iterable<DigestEntity> getAllUsers() {
        System.out.println("Cnt 1: " + digestRepository.count() +
                " Cnt 2: " + digestUsersRepository.count() +
                " Cnt 3: " + digestBadRepository.count() +
                " Cnt 4: " + digestUsersBadRepository.count());

        Iterable<DigestBadEntity> dbe = digestBadRepository.findAll();
        Iterable<DigestUserBadEntity> dube = digestUsersBadRepository.findAll();
        Iterable<DigestUserEntity> gube = digestUsersRepository.findAll();

        DigestUserEntity ge = new DigestUserEntity();
        ge.setUsername("Guest");
        ge.setAvatar("NULL");
        digestUsersRepository.save(ge);
        for (DigestUserBadEntity e : dube) {
            DigestUserEntity ge1 = new DigestUserEntity();
            ge1.setUsername(e.getUsername());
            ge1.setAvatar(e.getAvatar());
            digestUsersRepository.save(ge1);
        }

        return null;
    }

    private int getUsernameId(String username, Iterable<DigestUserEntity> dube) {
        for (DigestUserEntity user : dube) {
            if (user.getUsername().equals(username)) {
                return user.getId();
            }
        }
        return 1;
    }

    @RequestMapping(path = "/mig")
    public @ResponseBody Iterable<DigestEntity> getAllUsersa() {
        System.out.println("Cnt 1: " + digestRepository.count() +
                " Cnt 2: " + digestUsersRepository.count() +
                " Cnt 3: " + digestBadRepository.count() +
                " Cnt 4: " + digestUsersBadRepository.count());

        Iterable<DigestBadEntity> dbe = digestBadRepository.findAll();
        Iterable<DigestUserBadEntity> dube = digestUsersBadRepository.findAll();
        Iterable<DigestUserEntity> gube = digestUsersRepository.findAll();

        for (DigestBadEntity e : dbe) {
            DigestEntity ge2 = new DigestEntity();
            ge2.setAuthor(getUsernameId(e.getUsername(), gube));
            ge2.setDate(Integer.valueOf(e.getDate()));
            ge2.setDigest(e.getMsg());
            digestRepository.save(ge2);
        }

        return null;
    }
}
