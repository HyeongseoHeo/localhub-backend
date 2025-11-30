package com.example.localhub.controller;

import com.example.localhub.dto.comment.CommentRequest;
import com.example.localhub.dto.comment.CommentResponse;
import com.example.localhub.service.CommentService;
import com.example.localhub.security.details.MemberDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/posts/{postId}/comments")
    public List<CommentResponse> list(
            @PathVariable Long postId,
            @AuthenticationPrincipal MemberDetailsService.MemberDetails memberDetails
    ) {
        Long memberId = (memberDetails != null) ? memberDetails.getMember().getId() : null;
        return commentService.getComments(postId, memberId);
    }

    @PostMapping("/posts/{postId}/comments")
    public CommentResponse create(@PathVariable Long postId,
                                  @RequestBody CommentRequest request,
                                  @AuthenticationPrincipal MemberDetailsService.MemberDetails memberDetails
    ) {
        Long memberId = memberDetails.getMember().getId();
        return commentService.createComment(postId, request, memberId);
    }

    @DeleteMapping("/comments/{commentId}")
    public void delete(
                       @PathVariable Long commentId,
                       @AuthenticationPrincipal MemberDetailsService.MemberDetails memberDetails
    ) {
        Long memberId = memberDetails.getMember().getId();
        commentService.deleteComment(commentId, memberId);
    }

    @PostMapping("/comments/{commentId}/like")
    public void like(
            @PathVariable Long commentId,
            @AuthenticationPrincipal MemberDetailsService.MemberDetails memberDetails
    ) {
        Long memberId = memberDetails.getMember().getId();
        commentService.likeComment(commentId, memberId);
    }

    @DeleteMapping("/comments/{commentId}/like")
    public void unlike(
            @PathVariable Long commentId,
            @AuthenticationPrincipal MemberDetailsService.MemberDetails memberDetails
    ) {
        Long memberId = memberDetails.getMember().getId();
        commentService.unlikeComment(commentId, memberId);
    }
}

