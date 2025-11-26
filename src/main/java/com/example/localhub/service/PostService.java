package com.example.localhub.service;

import com.example.localhub.domain.board.Post;
import com.example.localhub.domain.member.Member;
import com.example.localhub.dto.board.PostRequest;
import com.example.localhub.dto.board.PostResponse;
import com.example.localhub.dto.board.RecommendedPostResponse;
import com.example.localhub.repository.CommentRepository;
import com.example.localhub.repository.MemberRepository;
import com.example.localhub.repository.PostRepository;
import com.example.localhub.domain.board.PostLike;
import com.example.localhub.repository.PostLikeRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;



import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;


    // 전체 목록
    public Page<PostResponse> getPosts(Pageable pageable) {
        return postRepository.findAllByAdFalse(pageable)
                .map(this::toResponse);
    }

    // 지역 기반 목록 조회
    public Page<PostResponse> getPostsByRegion(String region, Pageable pageable) {
        return postRepository.findByRegionAndAdFalse(region, pageable)
                .map(this::toResponse);
    }

    // 상세 조회
    public PostResponse getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        post.setViews(post.getViews() + 1);
        postRepository.save(post);

        return toResponse(post);
    }

    // 생성
    public PostResponse createPost(PostRequest request, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원 없음"));

        Post post = new Post();
        post.setAuthor(member);
        post.setRegion(request.getRegion());
        post.setContent(request.getContent());
        post.setAd(request.isAd());
        post.setKeywords(request.getKeywords());

        Post saved = postRepository.save(post);
        return toResponse(saved);
    }

    // 수정
    public PostResponse updatePost(Long postId, PostRequest request, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        if (!post.getAuthor().getId().equals(memberId)) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }

        post.setRegion(request.getRegion());
        post.setContent(request.getContent());
        post.setAd(request.isAd());
        post.setKeywords(request.getKeywords());

        Post saved = postRepository.save(post);
        return toResponse(saved);
    }

    // 삭제
    public void deletePost(Long postId, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        if (!post.getAuthor().getId().equals(memberId)) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }

        postRepository.delete(post);
    }

    // 좋아요 기능
    public void likePost(Long postId, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        // 이미 좋아요 눌렀는지 체크
        if (postLikeRepository.existsByPostAndMember(post, member)) {
            throw new RuntimeException("이미 좋아요를 눌렀습니다.");
        }

        PostLike like = new PostLike();
        like.setPost(post);
        like.setMember(member);
        postLikeRepository.save(like);

        // 게시글 좋아요 수 증가
        post.setLikesCount(post.getLikesCount() + 1);
        postRepository.save(post);
    }

    // 좋아요 취소
    public void unlikePost(Long postId, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        if (!postLikeRepository.existsByPostAndMember(post, member)) {
            throw new RuntimeException("좋아요를 누르지 않은 상태입니다.");
        }

        postLikeRepository.deleteByPostAndMember(post, member);

        // 게시글 좋아요 수 감소
        post.setLikesCount(post.getLikesCount() - 1);
        postRepository.save(post);
    }

    // 추천
    public List<RecommendedPostResponse> getRecommended() {
        return postRepository.findTop5ByAdFalseOrderByViewsDescLikesCountDesc()
                .stream()
                .map(this::toRecommended)
                .toList();
    }

    // DTO 변환
    private PostResponse toResponse(Post post) {
        PostResponse dto = new PostResponse();

        dto.setId(post.getId());
        dto.setAuthor(post.getAuthor().getNickname());
        dto.setAuthorId(post.getAuthor().getId().toString());
        dto.setRegion(post.getRegion());
        dto.setContent(post.getContent());
        dto.setTimestamp(post.getCreatedAt());
        dto.setViews(post.getViews());
        dto.setLikesCount(post.getLikesCount());
        dto.setCommentsCount(post.getCommentsCount());
        dto.setRating(post.getRating());
        dto.setRatingCount(post.getRatingCount());
        dto.setTotalRatingScore(post.getTotalRatingScore());
        dto.setAd(post.isAd());
        dto.setKeywords(post.getKeywords());

        return dto;
    }

    private RecommendedPostResponse toRecommended(Post post) {
        RecommendedPostResponse dto = new RecommendedPostResponse();
        dto.setId(post.getId());
        dto.setAuthor(post.getAuthor().getNickname());

        String content = post.getContent();
        dto.setContent(content.length() > 100 ? content.substring(0, 100) + "..." : content);

        return dto;
    }
}




