package com.example.localhub.controller;

import com.example.localhub.dto.board.PostRequest;
import com.example.localhub.dto.board.PostResponse;
import com.example.localhub.dto.board.RecommendedPostResponse;
import com.example.localhub.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public Page<PostResponse> list(
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String tags,
            Pageable pageable
    ) {
        if (tags != null && !tags.isBlank()) {
            List<String> tagList = List.of(tags.split(","));
            return postService.getPostsByTags(tagList, pageable);
        }

        if (region != null && !region.isBlank()) {
            return postService.getPostsByRegion(region, pageable);
        }
        return postService.getPosts(pageable);
    }

    @GetMapping("/{id}")
    public PostResponse detail(@PathVariable Long id) {
        return postService.getPost(id);
    }

    @PostMapping
    public PostResponse create(@RequestBody PostRequest request) {
        return postService.createPost(request);
    }

    @PutMapping("/{id}")
    public PostResponse update(
            @PathVariable Long id,
            @RequestBody PostRequest request
    ) {
        return postService.updatePost(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id,
                       @RequestParam Long memberId) {
        postService.deletePost(id, memberId);
    }

    @PostMapping("/{id}/like")
    public void like(@PathVariable Long id, @RequestParam Long memberId) {
        postService.likePost(id, memberId);
    }

    @DeleteMapping("/{id}/like")
    public void unlike(@PathVariable Long id, @RequestParam Long memberId) {
        postService.unlikePost(id, memberId);
    }

    @GetMapping("/recommended")
    public List<RecommendedPostResponse> recommended() {
        return postService.getRecommended();
    }
}

