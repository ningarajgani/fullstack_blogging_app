package com.example.twitterapp.controller;

import com.example.twitterapp.config.CustomUserDetails;
import com.example.twitterapp.model.Post;
import com.example.twitterapp.model.User;
import com.example.twitterapp.service.PostService;
import com.example.twitterapp.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class PostController {

    private final PostService postService;
    private final UserService userService;

    public PostController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<Post> posts = postService.findAll();
        model.addAttribute("posts", posts);
        return "home";
    }

    @GetMapping("/add")
    public String showAddPostForm(Model model) {
        model.addAttribute("post", new Post());
        return "add";
    }

    @PostMapping("/add")
    public String addPost(@ModelAttribute Post post) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        var existingUser = userService.findByUsername(user.getUsername());
        post.setUser(existingUser);
        postService.save(post);
        return "redirect:/";
    }

    @GetMapping("/myposts")
    public String myPosts(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        var existingUser = userService.findByUsername(user.getUsername());
        List<Post> userPosts = postService.findByUser(existingUser);
        model.addAttribute("posts", userPosts);
        model.addAttribute("username", existingUser.getUsername());
        return "myposts";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        var existingUser = userService.findByUsername(user.getUsername());
        
        Optional<Post> post = postService.findById(id);
        if (post.isPresent() && postService.isOwner(id, existingUser)) {
            model.addAttribute("post", post.get());
            return "edit";
        }
        return "redirect:/myposts";
    }

    @PostMapping("/edit/{id}")
    public String updatePost(@PathVariable Long id, @ModelAttribute Post post) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        var existingUser = userService.findByUsername(user.getUsername());
        
        if (postService.isOwner(id, existingUser)) {
            Optional<Post> existingPost = postService.findById(id);
            if (existingPost.isPresent()) {
                Post postToUpdate = existingPost.get();
                postToUpdate.setContent(post.getContent());
                postService.save(postToUpdate);
            }
        }
        return "redirect:/myposts";
    }

    @PostMapping("/delete/{id}")
    public String deletePost(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        var existingUser = userService.findByUsername(user.getUsername());
        
        if (postService.isOwner(id, existingUser)) {
            postService.deleteById(id);
        }
        return "redirect:/myposts";
    }
}
