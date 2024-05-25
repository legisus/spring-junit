package com.codesoft.edu.controller;

import com.codesoft.edu.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class HomeController {
    private final UserService userService;
    public HomeController(UserService userService) {
        this.userService = userService;
        log.info("HomeController was created");
    }

    @GetMapping({"/", "home"})
    public String home(Model model) {
        log.info("HomeController.home() was called");
        model.addAttribute("users", userService.getAll());
        log.info("HomeController.home() was called");
        log.info("return: home");
        return "home";
    }
}
