package com.example.twitterapp.service;

import com.example.twitterapp.model.Post;
import com.example.twitterapp.model.User;
import com.example.twitterapp.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public void save(Post post) {
        if (post.getCreatedAt() == null) {
            post.setCreatedAt(LocalDateTime.now());
        }
        postRepository.save(post);
    }

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public List<Post> findByUser(User user) {
        return postRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }

    public void deleteById(Long id) {
        postRepository.deleteById(id);
    }

    public boolean isOwner(Long postId, User user) {
        Optional<Post> post = findById(postId);
        return post.isPresent() && post.get().getUser().getId().equals(user.getId());
    }
}
