package com.example.localhub.service;

import com.example.localhub.domain.board.Comment;
import com.example.localhub.domain.board.Post;
import com.example.localhub.domain.member.Member;
import com.example.localhub.dto.comment.CommentRequest;
import com.example.localhub.dto.comment.CommentResponse;
import com.example.localhub.repository.CommentRepository;
import com.example.localhub.repository.MemberRepository;
import com.example.localhub.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.localhub.repository.CommentLikeRepository;
import com.example.localhub.domain.board.CommentLike;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional // 데이터 변경 안전성을 위해 추가
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CleanbotService cleanbotService;
    private final CommentLikeRepository commentLikeRepository;

    // 댓글 목록 조회
    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(Long postId, Long viewerId) {

        // 2. 보는 사람의 '클린봇 설정' 확인
        // (로그인 안 했거나 유저 정보가 없으면, 안전하게 기본적으로 '켜짐(true)' 처리)
        boolean isViewerCleanbotOn = true;
        if (viewerId != null) {
            Member viewer = memberRepository.findById(viewerId).orElse(null);
            if (viewer != null) {
                isViewerCleanbotOn = viewer.isCleanbotOn();
            }
        }

        final boolean filterOn = isViewerCleanbotOn; // 람다식에서 쓰기 위해 final 변수화

        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId)
                .stream()
                .map(comment -> {
                    CommentResponse dto = toResponse(comment);

                    if (comment.isMalicious() && filterOn) {
                        dto.setContent("클린봇이 감지한 부적절한 표현입니다."); // 내용 가리기
                    }
                    return dto;
                })
                .toList();
    }

    // 댓글 생성
    public CommentResponse createComment(Long postId, CommentRequest request, Long memberId) {
        // 저장하기 전에 클린봇 검사
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

        Comment saved = commentRepository.save(comment);
        // 게시글의 댓글 수 증가
        post.setCommentsCount(post.getCommentsCount() + 1);
        postRepository.save(post);

        return toResponse(saved);
    }

    // 댓글 삭제
    public void deleteComment(Long commentId, Long memberId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글 없음"));

        if (!comment.getAuthor().getId().equals(memberId)) {
            throw new RuntimeException("삭제 권한 없음");
        }

        Post post = comment.getPost();
        commentRepository.delete(comment);

        // 게시글의 댓글 수 감소
        post.setCommentsCount(post.getCommentsCount() - 1);
        postRepository.save(post);

    }

    public void likeComment(Long commentId, Long memberId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글 없음"));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원 없음"));

        // 이미 좋아요 눌렀는지 확인
        if (commentLikeRepository.existsByCommentAndMember(comment, member)) {
            throw new RuntimeException("이미 좋아요를 눌렀습니다.");
        }

        // 좋아요 저장
        CommentLike like = new CommentLike();
        like.setComment(comment);
        like.setMember(member);
        commentLikeRepository.save(like);
    }

    public void unlikeComment(Long commentId, Long memberId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글 없음"));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원 없음"));

        // 좋아요가 있는지 확인 (없으면 에러 또는 무시)
        if (!commentLikeRepository.existsByCommentAndMember(comment, member)) {
            throw new RuntimeException("좋아요를 누르지 않은 상태입니다.");
        }

        // 삭제 실행
        commentLikeRepository.deleteByCommentAndMember(comment, member);

        comment.setLikesCount(comment.getLikesCount() - 1);
    }

    private CommentResponse toResponse(Comment comment) {
        CommentResponse dto = new CommentResponse();
        dto.setId(comment.getId());
        if (comment.isAnonymous()) {
            dto.setAuthor("익명");
        } else {
            dto.setAuthor(comment.getAuthor().getNickname());
        }
        dto.setAuthorId(comment.getAuthor().getId().toString());
        dto.setContent(comment.getContent());
        dto.setTimestamp(comment.getCreatedAt());
        return dto;
    }
}
