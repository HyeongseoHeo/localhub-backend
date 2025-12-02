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
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 전체 목록 조회 (인증 필수 아님)
    @GetMapping
    public Page<PostResponse> list(
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) String search,
            @AuthenticationPrincipal MemberDetailsService.MemberDetails memberDetails,
            Pageable pageable
    ) {
        Long memberId = (memberDetails != null) ? memberDetails.getMember().getId() : null;

        if (search != null && !search.isBlank()) {
            if (region != null && !region.isBlank()) {
                return postService.searchPostsByRegionAndKeyword(region, search, pageable, memberId);
            }
            return postService.searchPostsByKeyword(search, pageable, memberId);
        }


        if (tags != null && !tags.isBlank()) {
            List<String> tagList = List.of(tags.split(","));
            return postService.getPostsByTags(tagList, pageable, memberId);
        }

        if (region != null && !region.isBlank()) {
            return postService.getPostsByRegion(region, pageable, memberId);
        }
        return postService.getPosts(pageable, memberId);
    }

    // 상세 조회 (인증 필수 아님)
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
                    shouldIncreaseView = false;
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

    // 게시글 생성 (인증 필수)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // HTTP 201 응답
    public PostResponse create(@RequestBody PostRequest request,
                               @AuthenticationPrincipal MemberDetailsService.MemberDetails memberDetails) { // memberDetails 추가

        Long memberId = memberDetails.getMember().getId();
        return postService.createPost(request, memberId);
    }

    // 게시글 수정 (인증 필수)
    @PutMapping("/{id}")
    public PostResponse update(
            @PathVariable Long id,
            @RequestBody PostRequest request,
            @AuthenticationPrincipal MemberDetailsService.MemberDetails memberDetails // memberDetails 추가
    ) {
        Long memberId = memberDetails.getMember().getId();
        return postService.updatePost(id, request, memberId);
    }

    // 삭제 (인증 필수)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // HTTP 204 응답
    public void delete(@PathVariable Long id,
                       @AuthenticationPrincipal MemberDetailsService.MemberDetails memberDetails) {
        Long memberId = memberDetails.getMember().getId();
        postService.deletePost(id, memberId);
    }

    // 좋아요 (인증 필수)
    @PostMapping("/{id}/like")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void like(@PathVariable Long id, @AuthenticationPrincipal MemberDetailsService.MemberDetails memberDetails) {
        Long memberId = memberDetails.getMember().getId();
        postService.likePost(id, memberId);
    }

    // 좋아요 취소 (인증 필수)
    @DeleteMapping("/{id}/like")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unlike(@PathVariable Long id, @AuthenticationPrincipal MemberDetailsService.MemberDetails memberDetails) {
        Long memberId = memberDetails.getMember().getId();
        postService.unlikePost(id, memberId);
    }

    // 추천 목록 조회 (인증 필수 아님)
    @GetMapping("/recommended")
    public List<RecommendedPostResponse> recommended(
            @RequestParam String region, // [추가] 필수 파라미터로 받음
            @RequestParam(required = false) String tags
    ) {
        List<String> tagList = (tags != null && !tags.isBlank())
                ? List.of(tags.split(","))
                : Collections.emptyList();

        return postService.getRecommended(region, tagList);
    }

    // 별점 주기 API (인증 필수)
    @PostMapping("/{id}/rating")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void ratePost(@PathVariable Long id,
                         @AuthenticationPrincipal MemberDetailsService.MemberDetails memberDetails,
                         @RequestParam int score
    ) {
        Long memberId = memberDetails.getMember().getId();
        postService.ratePost(id, memberId, score);
    }

    // 즐겨찾기 (별) 토글
    @PostMapping("/{id}/bookmark")
    public void toggleBookmark(@PathVariable Long id, @AuthenticationPrincipal MemberDetailsService.MemberDetails memberDetails) {
        postService.toggleBookmark(id, memberDetails.getMember().getId());
    }

    // 특정 작성자 게시글 목록 조회
    @GetMapping("/author/{authorId}")
    public Page<PostResponse> getPostsByAuthor(
            @PathVariable Long authorId,
            @AuthenticationPrincipal MemberDetailsService.MemberDetails memberDetails,
            Pageable pageable
    ) {
        Long currentMemberId = (memberDetails != null) ? memberDetails.getMember().getId() : null;
        return postService.getPostsByAuthorId(authorId, pageable, currentMemberId);
    }
}

