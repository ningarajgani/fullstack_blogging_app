package com.example.twitterapp.controller;

import com.example.twitterapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class VerificationController {

    @Autowired
    private UserService userService;

    @GetMapping("/verify")
    public String showVerifyPage(@RequestParam("email") String email, Model model) {
        model.addAttribute("email", email);
        return "verify";
    }

    @PostMapping("/verify")
    public String verifyAccount(@RequestParam("email") String email,
            @RequestParam("otp") String otp,
            Model model) {
        boolean verified = userService.verifyUser(email, otp);
        if (verified) {
            return "redirect:/login?success";
        } else {
            model.addAttribute("email", email);
            model.addAttribute("error", "Invalid OTP or verification failed. Please try again.");
            return "verify";
        }
    }
}
