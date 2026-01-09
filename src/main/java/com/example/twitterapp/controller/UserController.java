package com.example.twitterapp.controller;

import com.example.twitterapp.model.User;
import com.example.twitterapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    @Autowired
    private UserDetailsService userDetailsService;

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    private static final String REGISTER_VIEW = "register";

    @GetMapping("/login")
    public String login(Model model, User userDto) {
        model.addAttribute("user", userDto);
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model, User userDto) {
        model.addAttribute("user", userDto);
        return REGISTER_VIEW;
    }

    @PostMapping("/register")
    public String registerSava(@ModelAttribute("user") User userDto, Model model) {
        User existingUserByUsername = userService.findByUsername(userDto.getUsername());
        if (existingUserByUsername != null) {
            model.addAttribute("Userexist", "Username is already taken.");
            return REGISTER_VIEW;
        }
        User existingUserByEmail = userService.findByEmail(userDto.getEmail());
        if (existingUserByEmail != null) {
            model.addAttribute("Emailexist", "Email is already registered.");
            return REGISTER_VIEW;
        }
        userService.save(userDto);
        return "redirect:/verify?email=" + userDto.getEmail();
    }
}