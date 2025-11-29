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
import org.springframework.transaction.annotation.Transactional; // 트랜잭션 추가

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional // 데이터 변경 안전성을 위해 추가
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CleanbotService cleanbotService; // 👈 1. 클린봇 서비스 주입

    // 댓글 목록 조회 (수정됨: 보는 사람 ID 추가)
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

                    // 3.핵심 로직: 댓글이 욕설이고(AND) 보는 사람이 필터를 켰다면?
                    if (comment.isMalicious() && filterOn) {
                        dto.setContent("클린봇이 감지한 부적절한 표현입니다."); // 내용 가리기
                    }
                    return dto;
                })
                .toList();
    }

    // 댓글 생성
    public CommentResponse createComment(Long postId, CommentRequest request, Long memberId) {
        // 4. 저장하기 전에 클린봇 검사!
        boolean isMalicious = cleanbotService.isMalicious(request.getContent());

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글 없음"));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원 없음"));

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setAuthor(member);
        comment.setContent(request.getContent());
        comment.setMalicious(isMalicious); // 👈 5. 검사 결과 저장 (true/false)

        Comment saved = commentRepository.save(comment);
        return toResponse(saved);
    }

    // 댓글 삭제 (기존 유지)
    public void deleteComment(Long commentId, Long memberId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글 없음"));

        if (!comment.getAuthor().getId().equals(memberId)) {
            throw new RuntimeException("삭제 권한 없음");
        }

        commentRepository.delete(comment);
    }

    private CommentResponse toResponse(Comment comment) {
        CommentResponse dto = new CommentResponse();
        dto.setId(comment.getId());
        dto.setAuthor(comment.getAuthor().getNickname());
        dto.setAuthorId(comment.getAuthor().getId().toString());
        dto.setContent(comment.getContent());
        dto.setTimestamp(comment.getCreatedAt());
        return dto;
    }
}
