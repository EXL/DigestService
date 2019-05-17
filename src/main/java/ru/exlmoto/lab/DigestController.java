package ru.exlmoto.lab;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DigestController {
    @Autowired
    private IDigestRepository digestRepository;

    @Autowired
    private IDigestUsersRepository digestUsersRepository;

    @RequestMapping(path = "/digest")
    public String digest(@RequestParam(name = "page", required = false) String page, Model model) {

        System.out.println("Cnt 1: " + digestRepository.count() + " Cnt 2: " + digestUsersRepository.count());

        return "digest";
    }
}
