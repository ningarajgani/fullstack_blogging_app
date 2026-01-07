package com.example.twitterapp.repository;

import com.example.twitterapp.model.Post;
import com.example.twitterapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserOrderByCreatedAtDesc(User user);
}
