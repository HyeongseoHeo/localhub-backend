package com.example.localhub.service;

import com.example.localhub.domain.board.Comment;
import com.example.localhub.domain.board.CommentLike;
import com.example.localhub.domain.board.Post;
import com.example.localhub.domain.member.Member;
import com.example.localhub.dto.comment.CommentRequest;
import com.example.localhub.dto.comment.CommentResponse;
import com.example.localhub.repository.CommentLikeRepository;
import com.example.localhub.repository.CommentRepository;
import com.example.localhub.repository.MemberRepository;
import com.example.localhub.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CleanbotService cleanbotService;
    private final CommentLikeRepository commentLikeRepository;

    // 댓글 목록 조회
    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(Long postId, Long viewerId) {

        // 1. 보는 사람의 '클린봇 설정' 확인
        boolean isViewerCleanbotOn = true;
        if (viewerId != null) {
            Member viewer = memberRepository.findById(viewerId).orElse(null);
            if (viewer != null) {
                isViewerCleanbotOn = viewer.isCleanbotOn();
            }
        }

        final boolean filterOn = isViewerCleanbotOn;

        // 2. 댓글 목록 가져와서 변환
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId)
                .stream()
                .map(comment -> {
                    // viewerId를 넘겨서 좋아요 여부도 같이 체크
                    CommentResponse dto = toResponse(comment, viewerId);

                    if (comment.isMalicious() && filterOn) {
                        dto.setContent("클린봇이 감지한 부적절한 표현입니다.");
                    }
                    return dto;
                })
                .toList();
    }

    // 댓글 생성
    public CommentResponse createComment(Long postId, CommentRequest request, Long memberId) {
        boolean isMalicious = cleanbotService.isMalicious(request.getContent());

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글 없음"));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원 없음"));

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setAuthor(member);
        comment.setContent(request.getContent());
        comment.setMalicious(isMalicious);
        comment.setAnonymous(Boolean.TRUE.equals(request.getAnonymous()));
        comment.setLikes(0);

        Comment saved = commentRepository.save(comment);

        return toResponse(saved, memberId);
    }

    // 댓글 삭제
    public void deleteComment(Long commentId, Long memberId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글 없음"));

        if (!comment.getAuthor().getId().equals(memberId)) {
            throw new RuntimeException("삭제 권한 없음");
        }

        commentRepository.delete(comment);
    }

    // 댓글 좋아요
    public void likeComment(Long commentId, Long memberId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글 없음"));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원 없음"));

        if (commentLikeRepository.existsByCommentIdAndMemberId(comment.getId(), member.getId())) {
            throw new RuntimeException("이미 좋아요를 눌렀습니다.");
        }

        CommentLike like = new CommentLike();
        like.setComment(comment);
        like.setMember(member);
        commentLikeRepository.save(like);

        comment.setLikes(comment.getLikes() + 1);
    }

    // 댓글 좋아요 취소
    public void unlikeComment(Long commentId, Long memberId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글 없음"));

        CommentLike commentLike = commentLikeRepository.findByCommentIdAndMemberId(commentId, memberId)
                .orElseThrow(() -> new RuntimeException("좋아요를 누르지 않은 상태입니다."));

        commentLikeRepository.delete(commentLike);

        comment.setLikes(Math.max(0, comment.getLikes() - 1));
    }

    public Page<CommentResponse> getMyComments(Long memberId, Pageable pageable) {
        return commentRepository.findAllByAuthorId(memberId, pageable)
                .map(comment -> toResponse(comment, memberId));
    }

    // DTO 변환 메서드 (viewerId를 받아서 좋아요 여부 체크)
    private CommentResponse toResponse(Comment comment, Long viewerId) {
        CommentResponse dto = new CommentResponse();
        dto.setId(comment.getId());

        if (comment.isAnonymous()) {
            dto.setAuthor("익명");
            dto.setRole(null);
        } else {
            dto.setAuthor(comment.getAuthor().getNickname());
            dto.setRole(comment.getAuthor().getRole().name());
        }

        dto.setAuthorId(comment.getAuthor().getId().toString());
        dto.setContent(comment.getContent());
        dto.setTimestamp(comment.getCreatedAt());
        dto.setLikesCount(comment.getLikes());

        if (viewerId != null) {
            boolean isLiked = commentLikeRepository.existsByCommentIdAndMemberId(comment.getId(), viewerId);
            dto.setLiked(isLiked);
        } else {
            dto.setLiked(false);
        }

        return dto;
    }
}