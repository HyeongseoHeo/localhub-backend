package com.example.localhub.controller;

import com.example.localhub.dto.board.PostRequest;
import com.example.localhub.dto.board.PostResponse;
import com.example.localhub.dto.board.RecommendedPostResponse;
import com.example.localhub.service.PostService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.example.localhub.security.details.MemberDetailsService;

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
            @AuthenticationPrincipal MemberDetailsService.MemberDetails memberDetails,
            Pageable pageable
    ) {
        Long memberId = (memberDetails != null) ? memberDetails.getMember().getId() : null;

        if (tags != null && !tags.isBlank()) {
            List<String> tagList = List.of(tags.split(","));
            return postService.getPostsByTags(tagList, pageable, memberId);
        }

        if (region != null && !region.isBlank()) {
            return postService.getPostsByRegion(region, pageable, memberId);
        }
        return postService.getPosts(pageable, memberId);
    }

    @GetMapping("/{id}")
    public PostResponse detail(
            @PathVariable Long id,
            @AuthenticationPrincipal MemberDetailsService.MemberDetails memberDetails,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        Long memberId = (memberDetails != null) ? memberDetails.getMember().getId() : null;

        String cookieName = "postView_" + id;
        boolean shouldIncreaseView = true;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    shouldIncreaseView = false; // 쿠키가 있으면 조회수 증가 X
                    break;
                }
            }
        }

        PostResponse postResponse = postService.getPost(id, memberId, shouldIncreaseView);

        if (shouldIncreaseView) {
            Cookie newCookie = new Cookie(cookieName, "true");
            newCookie.setPath("/");
            newCookie.setMaxAge(60 * 60 * 24); // 24시간
            response.addCookie(newCookie);
        }

        return postResponse;
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
                       @AuthenticationPrincipal MemberDetailsService.MemberDetails memberDetails) {
        Long memberId = memberDetails.getMember().getId();
        postService.deletePost(id, memberId);
    }

    @PostMapping("/{id}/like")
    public void like(@PathVariable Long id, @AuthenticationPrincipal MemberDetailsService.MemberDetails memberDetails) {
        Long memberId = memberDetails.getMember().getId();
        postService.likePost(id, memberId);
    }

    @DeleteMapping("/{id}/like")
    public void unlike(@PathVariable Long id, @AuthenticationPrincipal MemberDetailsService.MemberDetails memberDetails) {
        Long memberId = memberDetails.getMember().getId();
        postService.unlikePost(id, memberId);
    }

    @GetMapping("/recommended")
    public List<RecommendedPostResponse> recommended() {
        return postService.getRecommended();
    }

    // 별점 주기 API
    @PostMapping("/{id}/rating")
    public void ratePost(@PathVariable Long id,
                         @AuthenticationPrincipal MemberDetailsService.MemberDetails memberDetails,
                         @RequestParam int score
    ) {
        if (memberDetails == null) {
            throw new RuntimeException("로그인이 필요한 기능입니다.");
        }
        Long memberId = memberDetails.getMember().getId();
        postService.ratePost(id, memberId, score);
    }
}

