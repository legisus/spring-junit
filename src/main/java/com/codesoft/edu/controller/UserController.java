package com.codesoft.edu.controller;

import com.codesoft.edu.model.User;
import com.codesoft.edu.service.RoleService;
import com.codesoft.edu.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
        log.info("UserController was created");
    }

    @GetMapping("/create")
    public String create(Model model) {
        log.info("@GetMapping: /create");
        model.addAttribute("user", new User());
        log.info("return: create-user");
        return "create-user";
    }

    @PostMapping("/create")
    public String create(@Validated @ModelAttribute("user") User user, BindingResult result) {
        log.info("@PostMapping: /create");
        if (result.hasErrors()) {
            log.error("Validation error: " + result.getAllErrors());
            log.error("Try to create user again");
            return "create-user";
        }
        user.setPassword(user.getPassword());
        user.setRole(roleService.readById(2));
        User newUser = userService.create(user);
        log.info("User has been created: " + newUser);
        log.error("return: redirect:/todos/all/users/newUser.getId()");
        return "redirect:/todos/all/users/" + newUser.getId();
    }

    @GetMapping("/{id}/read")
    public String read(@PathVariable long id, Model model) {
        log.info("@GetMapping: /" + id + "/read");
        User user = userService.readById(id);
        model.addAttribute("user", user);
        log.info("return: user-info");
        return "user-info";
    }

    @GetMapping("/{id}/update")
    public String update(@PathVariable long id, Model model) {
        log.info("@GetMapping: /" + id + "/update");
        User user = userService.readById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", roleService.getAll());
        log.info("return: update-user");
        return "update-user";
    }


    @PostMapping("/{id}/update")
    public String update(@PathVariable long id, Model model, @Validated @ModelAttribute("user") User user, @RequestParam("roleId") long roleId, BindingResult result) {
        log.info("@PostMapping: /" + id + "/update");
        User oldUser = userService.readById(id);
        if (result.hasErrors()) {
            log.error("Validation error: " + result.getAllErrors());
            user.setRole(oldUser.getRole());
            model.addAttribute("roles", roleService.getAll());
            log.info("Try to update user again");
            return "update-user";
        }
        if (oldUser.getRole().getName().equals("USER")) {
            log.info("User role is USER");
            user.setRole(oldUser.getRole());
        } else {
            log.info("User role is: " + oldUser.getRole().getName());
            user.setRole(roleService.readById(roleId));
            log.info("User role has been updated: " + user.getRole().getName());
        }
        log.info("Try to update user: " + user);
        userService.update(user);
        log.info("User has been updated successfully");
        log.info("return: redirect:/users/id/read");
        return "redirect:/users/" + id + "/read";
    }


    @GetMapping("/{id}/delete")
    public String delete(@PathVariable("id") long id) {
        log.info("@GetMapping: /" + id + "/delete");
        userService.delete(id);
        log.info("User has been deleted");
        log.info("return: redirect:/users/all");
        return "redirect:/users/all";
    }

    @GetMapping("/all")
    public String getAll(Model model) {
        log.info("@GetMapping: /all");
        model.addAttribute("users", userService.getAll());
        log.info("return: users-list");
        return "users-list";
    }
}
