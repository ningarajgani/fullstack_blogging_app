package com.example.twitterapp.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            
            if (statusCode == 404) {
                model.addAttribute("errorCode", "404");
                model.addAttribute("errorMessage", "Page Not Found");
                model.addAttribute("errorDescription", "The page you are looking for doesn't exist.");
            } else if (statusCode == 500) {
                model.addAttribute("errorCode", "500");
                model.addAttribute("errorMessage", "Internal Server Error");
                model.addAttribute("errorDescription", "Something went wrong on our end.");
            } else {
                model.addAttribute("errorCode", statusCode.toString());
                model.addAttribute("errorMessage", "Error");
                model.addAttribute("errorDescription", "An unexpected error occurred.");
            }
        } else {
            model.addAttribute("errorCode", "Unknown");
            model.addAttribute("errorMessage", "Error");
            model.addAttribute("errorDescription", "An unexpected error occurred.");
        }
        
        return "error";
    }
}