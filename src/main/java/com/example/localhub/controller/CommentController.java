package com.example.localhub.controller;

import com.example.localhub.dto.comment.CommentRequest;
import com.example.localhub.dto.comment.CommentResponse;
import com.example.localhub.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    public List<CommentResponse> list(
            @PathVariable Long postId,
            @RequestParam(required = false) Long memberId
    ) {
        return commentService.getComments(postId, memberId);
    }

    @PostMapping
    public CommentResponse create(@PathVariable Long postId,
                                  @RequestBody CommentRequest request,
                                  @RequestParam Long memberId) {
        return commentService.createComment(postId, request, memberId);
    }

    @DeleteMapping("/{commentId}")
    public void delete(@PathVariable Long postId,
                       @PathVariable Long commentId,
                       @RequestParam Long memberId) {
        commentService.deleteComment(commentId, memberId);
    }
}

