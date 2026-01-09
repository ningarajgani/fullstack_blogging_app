package com.example.twitterapp.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    private static final String ERROR_CODE = "errorCode";
    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String ERROR_DESCRIPTION = "errorDescription";

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());

            if (statusCode == 404) {
                model.addAttribute(ERROR_CODE, "404");
                model.addAttribute(ERROR_MESSAGE, "Page Not Found");
                model.addAttribute(ERROR_DESCRIPTION, "The page you are looking for doesn't exist.");
            } else if (statusCode == 500) {
                model.addAttribute(ERROR_CODE, "500");
                model.addAttribute(ERROR_MESSAGE, "Internal Server Error");
                model.addAttribute(ERROR_DESCRIPTION, "Something went wrong on our end.");
            } else {
                model.addAttribute(ERROR_CODE, statusCode.toString());
                model.addAttribute(ERROR_MESSAGE, "Error");
                model.addAttribute(ERROR_DESCRIPTION, "An unexpected error occurred.");
            }
        } else {
            model.addAttribute(ERROR_CODE, "Unknown");
            model.addAttribute(ERROR_MESSAGE, "Error");
            model.addAttribute(ERROR_DESCRIPTION, "An unexpected error occurred.");
        }

        return "error";
    }
}