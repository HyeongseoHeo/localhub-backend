package com.example.localhub.service;

import com.example.localhub.domain.board.Post;
import com.example.localhub.domain.member.Member;
import com.example.localhub.dto.board.PlaceResponse;
import com.example.localhub.dto.board.PostRequest;
import com.example.localhub.dto.board.PostResponse;
import com.example.localhub.dto.board.RecommendedPostResponse;
import com.example.localhub.repository.CommentRepository;
import com.example.localhub.repository.MemberRepository;
import com.example.localhub.repository.PostRepository;
import com.example.localhub.domain.board.PostLike;
import com.example.localhub.repository.PostLikeRepository;
import com.example.localhub.domain.board.PostRating;
import com.example.localhub.repository.PostRatingRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostRatingRepository postRatingRepository;

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
    public PostResponse getPost(Long id, Long memberId, boolean shouldIncreaseView) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        if (shouldIncreaseView) {
            post.setViews(post.getViews() + 1);
            postRepository.save(post);
        }

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
    public PostResponse createPost(PostRequest request, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원 없음"));

        Post post = new Post();
        post.setAuthor(member);
        post.setRegion(request.getRegion());
        post.setContent(request.getContent());
        post.setAd(Boolean.TRUE.equals(request.getAd()));
        post.setTags(request.getTags() != null ? request.getTags() : Collections.emptyList());
        post.setImages(request.getImages() != null ? request.getImages() : Collections.emptyList());

        if (request.getPlace() != null) {
            post.setAddress(request.getPlace().getAddress());
            post.setLatitude(request.getPlace().getLatitude());
            post.setLongitude(request.getPlace().getLongitude());
        }

        Post saved = postRepository.save(post);
        return toResponse(saved);
    }

    // 게시글 수정
    public PostResponse updatePost(Long postId, PostRequest request, Long memberId) {

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

        if (request.getPlace() != null) {
            post.setAddress(request.getPlace().getAddress());
            post.setLatitude(request.getPlace().getLatitude());
            post.setLongitude(request.getPlace().getLongitude());
        } else {
            post.setAddress(null);
            post.setLatitude(null);
            post.setLongitude(null);
        }


        //Post saved = postRepository.save(post);
        return toResponse(post);
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

    // 별점 등록
    public void ratePost(Long postId, Long memberId, int score) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원 없음"));

        PostRating rating = postRatingRepository.findByPostIdAndMemberId(postId, memberId)
                .orElse(null);

        if (rating != null) {
            rating.updateScore(score);
        } else {
            rating = PostRating.builder()
                    .post(post)
                    .member(member)
                    .score(score)
                    .build();
            postRatingRepository.save(rating);
        }

        Double average = postRatingRepository.getAverageScoreByPostId(postId);
        Long ratingCount = postRatingRepository.countByPostId(postId);
        Integer totalScore = postRatingRepository.sumScoreByPostId(postId);

        post.updateAverageRating(average != null ? average : 0.0);
        post.setRatingCount(ratingCount != null ? ratingCount.intValue() : 0);
        post.setTotalRatingScore(totalScore != null ? totalScore : 0);
        postRepository.save(post);
    }

    // 좋아요
    public void likePost(Long postId, Long memberId) {
        if (postLikeRepository.existsByPostIdAndMemberId(postId, memberId)) {
            throw new RuntimeException("이미 좋아요를 눌렀습니다.");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        PostLike like = new PostLike();
        like.setPost(post);
        like.setMember(member);
        postLikeRepository.save(like);

        post.setLikes(post.getLikes() + 1);
        postRepository.save(post);
    }

    // 좋아요 취소
    public void unlikePost(Long postId, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        PostLike postLike = postLikeRepository.findByPostIdAndMemberId(postId, memberId)
                .orElseThrow(() -> new RuntimeException("좋아요를 누르지 않았습니다."));

        postLikeRepository.delete(postLike);

        post.setLikes(post.getLikes() - 1);
        postRepository.save(post);
    }

    // 추천 게시글 조회
    public List<RecommendedPostResponse> getRecommended() {
        return postRepository.findTop5ByAdFalseOrderByViewsDescLikesDesc()
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
        dto.setLikesCount(post.getLikes());
        dto.setCommentsCount(post.getComments()!= null ? post.getComments().size() : 0);
        dto.setRating(post.getAverageRating() != null ? post.getAverageRating() : 0.0);
        dto.setRatingCount(post.getRatingCount());
        dto.setTotalRatingScore(post.getTotalRatingScore());
        dto.setAd(post.isAd());
        dto.setTags(post.getTags() != null ? post.getTags() : Collections.emptyList());
        dto.setImages(post.getImages() != null ? post.getImages() : Collections.emptyList());
        if (post.getAddress() != null || post.getLatitude() != null) {
            PlaceResponse placeDto = PlaceResponse.builder()
                    .address(post.getAddress())
                    .latitude(post.getLatitude())
                    .longitude(post.getLongitude())
                    .build();
            dto.setPlace(placeDto); // PostResponse에 setPlace 메서드가 있어야 함
        }

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





