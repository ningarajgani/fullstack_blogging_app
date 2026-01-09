package com.example.twitterapp.service;

import com.example.twitterapp.model.User;

public interface UserService {
    User findByUsername(String username);

    User findByEmail(String email);

    User save(User userDto);

    boolean verifyUser(String email, String otp);
}