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
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PostLikeRepository postLikeRepository;

    // 전체 목록 조회
    @Transactional(readOnly = true)
    public Page<PostResponse> getPosts(Pageable pageable, Long memberId) {
        return postRepository.findAll(pageable)
                .map(post -> {
                    // 1. 엔티티 -> DTO 변환
                    PostResponse dto = toResponse(post);

                    // 2. 로그인한 유저라면 좋아요 여부 확인해서 DTO에 세팅
                    if (memberId != null) {
                        boolean isLiked = postLikeRepository.existsByPostIdAndMemberId(post.getId(), memberId);
                        dto.setLiked(isLiked);
                    }
                    return dto;
                });
    }


     // 지역 기반 목록 조회
     @Transactional(readOnly = true)
     public Page<PostResponse> getPostsByRegion(String region, Pageable pageable, Long memberId) {
         return postRepository.findByRegion(region, pageable)
                 .map(post -> {
                     PostResponse dto = toResponse(post);

                     if (memberId != null) {
                         boolean isLiked = postLikeRepository.existsByPostIdAndMemberId(post.getId(), memberId);
                         dto.setLiked(isLiked);
                     }
                     return dto;
                 });
     }

    @Transactional(readOnly = true)
    public Page<PostResponse> getPostsByTags(List<String> tags, Pageable pageable, Long memberId) {
        return postRepository.findDistinctByTagsIn(tags, pageable)
                .map(post -> {
                    PostResponse dto = toResponse(post);
                    if (memberId != null) {
                        boolean isLiked = postLikeRepository.existsByPostIdAndMemberId(post.getId(), memberId);
                        dto.setLiked(isLiked);
                    }
                    return dto;
                });
    }


     // 상세 조회
    public PostResponse getPost(Long id, Long memberId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        PostResponse dto = toResponse(post);

        //로그인 안했으면 false 고정
        if (memberId == null) {
            dto.setLiked(false);
            return dto;
        }

        // 로그인 했으면 좋아요 여부 검사
        boolean liked = postLikeRepository.existsByPostIdAndMemberId(post.getId(), memberId);
        dto.setLiked(liked);

        return dto;
    }


     // 게시글 생성
    public PostResponse createPost(PostRequest request) {

        Long memberId = request.getMemberId();
        if (memberId == null) {
            throw new RuntimeException("memberId가 요청에 없습니다.");
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원 없음"));

        Post post = new Post();
        post.setAuthor(member);
        post.setRegion(request.getRegion());
        post.setContent(request.getContent());

        post.setAd(Boolean.TRUE.equals(request.getAd()));

        post.setTags(request.getTags() != null ? request.getTags() : Collections.emptyList());
        post.setImages(request.getImages() != null ? request.getImages() : Collections.emptyList());

        Post saved = postRepository.save(post);
        return toResponse(saved);
    }
    // 게시글 수정
    public PostResponse updatePost(Long postId, PostRequest request) {

        Long memberId = request.getMemberId();
        if (memberId == null) {
            throw new RuntimeException("memberId가 요청에 없습니다.");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        if (!post.getAuthor().getId().equals(memberId)) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }

        post.setRegion(request.getRegion());
        post.setContent(request.getContent());
        post.setAd(Boolean.TRUE.equals(request.getAd()));

        post.setTags(request.getTags() != null ? request.getTags() : Collections.emptyList());
        post.setImages(request.getImages() != null ? request.getImages() : Collections.emptyList());

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

    public void incrementView(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        post.setViews(post.getViews() + 1);
        postRepository.save(post);
    }


    // 좋아요
    public void likePost(Long postId, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        if (postLikeRepository.existsByPostAndMember(post, member)) {
            throw new RuntimeException("이미 좋아요를 눌렀습니다.");
        }

        PostLike like = new PostLike();
        like.setPost(post);
        like.setMember(member);
        postLikeRepository.save(like);

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

        post.setLikesCount(post.getLikesCount() - 1);
        postRepository.save(post);
    }

    // 추천 게시글 조회
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
        dto.setRole(post.getAuthor().getRole().name());
        dto.setRegion(post.getRegion());
        dto.setContent(post.getContent());
        dto.setTimestamp(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        dto.setViews(post.getViews());
        dto.setLikesCount(post.getLikesCount());
        dto.setCommentsCount(post.getCommentsCount());
        dto.setRating(post.getRating());
        dto.setRatingCount(post.getRatingCount());
        dto.setTotalRatingScore(post.getTotalRatingScore());
        dto.setAd(post.isAd());
        dto.setTags(post.getTags());
        dto.setImages(post.getImages());

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





