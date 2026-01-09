package com.example.twitterapp.service;

import com.example.twitterapp.model.User;
import com.example.twitterapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SupabaseAuthService supabaseAuthService;

    public UserServiceImpl(UserRepository userRepository) {
        super();
        this.userRepository = userRepository;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User save(User userDto) {
        // Try to signup with Supabase Auth first
        supabaseAuthService.signup(userDto.getEmail(), userDto.getPassword());

        // Even if supabase fails (e.g. user already exists in auth), we want to save to
        // our local DB
        // to manage the verification state if it's the first time they are registering
        // with Scriblog.
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setVerified(false);
        System.out.println("Processing local save for user: " + user.getUsername() + ", email: " + user.getEmail());
        User savedUser = userRepository.save(user);
        System.out.println("Local save successful for ID: " + savedUser.getId());
        return savedUser;
    }

    @Override
    public boolean verifyUser(String email, String otp) {
        if (supabaseAuthService.verifyOtp(email, otp)) {
            User user = userRepository.findByEmail(email);
            if (user != null) {
                user.setVerified(true);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

}