package com.example.localhub.controller;

import com.example.localhub.dto.board.PostResponse;
import com.example.localhub.dto.tour.NaverBlogResponse;
import com.example.localhub.dto.tour.TourApiResponse;
import com.example.localhub.service.NaverApiService;
import com.example.localhub.service.TourApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*; // HashMap, List 추가
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sns")
@RequiredArgsConstructor
public class SnsApiController {

    private final TourApiService tourApiService;
    private final NaverApiService naverApiService;

    // ★★★ [오류 수정] Map.of() 대신 static HashMap으로 초기화 ★★★
    private static final Map<Integer, List<String>> EXCLUSION_MAP;

    static {
        EXCLUSION_MAP = new HashMap<>();
        EXCLUSION_MAP.put(33, List.of("충남", "대전", "세종"));
        EXCLUSION_MAP.put(34, List.of("충북", "대전", "세종"));
        EXCLUSION_MAP.put(8, List.of("충북", "충남", "대전"));
        EXCLUSION_MAP.put(3, List.of("충북", "충남", "세종"));

        EXCLUSION_MAP.put(5, List.of("전북", "전남"));
        EXCLUSION_MAP.put(37, List.of("전남", "광주"));
        EXCLUSION_MAP.put(38, List.of("전북", "광주"));

        EXCLUSION_MAP.put(6, List.of("울산", "경북", "경남", "대구"));
        EXCLUSION_MAP.put(7, List.of("부산", "경북", "경남", "대구"));
        EXCLUSION_MAP.put(35, List.of("부산", "울산", "경남", "대구"));
        EXCLUSION_MAP.put(36, List.of("부산", "울산", "경북", "대구"));
        EXCLUSION_MAP.put(4, List.of("부산", "울산", "경북", "경남"));
    }

    // 호출 주소: /api/sns/search?keyword=여행&areaCode=33
    @GetMapping("/search")
    public ResponseEntity<List<PostResponse>> searchCombinedSns(
            @RequestParam(required = false) String keyword, // 키워드 필수 해제
            @RequestParam(required = false) Integer areaCode
    ) {
        List<PostResponse> resultList = new ArrayList<>();

        // [CHANGE 1] ★ "여행" 키워드 자동 포함 로직 (기능 요구사항) ★
        String baseKeyword = keyword != null && !keyword.isEmpty() ? keyword : "";
        String tourKeyword = String.join(" ", baseKeyword, "여행").trim(); // 예: "맛집 여행"

        String regionName = convertCodeToName(areaCode);
        String blogQuery = String.join(" ", regionName, tourKeyword).trim(); // 예: "충북 맛집 여행"

        // ==========================================
        // A. 공공데이터 (관광지)
        // ==========================================
        // tourKeyword 사용
        List<TourApiResponse.Item> tourItems = tourApiService.searchTourData(tourKeyword, areaCode);
        if (tourItems != null) {
            List<PostResponse> tourPosts = tourItems.stream()
                    .map(item -> PostResponse.builder()
                            .id(Long.parseLong(item.getContentid()))
                            .title(item.getTitle())
                            .content(item.getAddr1() != null ? item.getAddr1() : "주소 정보 없음")
                            .images(item.getFirstimage() != null
                                    ? Collections.singletonList(item.getFirstimage())
                                    : Collections.emptyList())
                            .author("한국관광공사").authorId("admin").role("ADMIN")
                            .region(item.getAddr1())
                            .isSns(true)
                            .timestamp(LocalDateTime.now())
                            .views(0).likesCount(0).commentsCount(0).liked(false).bookmarked(false)
                            .build()
                    ).collect(Collectors.toList());
            resultList.addAll(tourPosts);
        }

        // ==========================================
        // B. 네이버 블로그 (제외 필터 적용)
        // ==========================================
        List<NaverBlogResponse.Item> blogItems = naverApiService.searchBlog(blogQuery);

        if (blogItems != null && areaCode != null) {
            List<String> exclusionKeywords = EXCLUSION_MAP.getOrDefault(areaCode, Collections.emptyList());

            // ★★★ [핵심: 제외 필터링] ★★★
            blogItems = blogItems.stream()
                    .filter(item -> exclusionKeywords.stream().noneMatch(
                            exKeyword -> item.getTitle().contains(exKeyword) || item.getDescription().contains(exKeyword)
                    ))
                    .collect(Collectors.toList());
        }

        if (blogItems != null) {
            List<PostResponse> blogPosts = blogItems.stream()
                    .map(item -> {
                        String cleanTitle = item.getTitle().replaceAll("<[^>]*>", "");
                        String cleanDesc = item.getDescription().replaceAll("<[^>]*>", "");

                        LocalDateTime postDate = LocalDateTime.now();
                        try {
                            LocalDate date = LocalDate.parse(item.getPostdate(), DateTimeFormatter.ofPattern("yyyyMMdd"));
                            postDate = date.atStartOfDay();
                        } catch (Exception ignored) {}

                        return PostResponse.builder()
                                .id(System.currentTimeMillis() + (long)(Math.random() * 100000))
                                .title(cleanTitle)
                                .content(cleanDesc)
                                .images(Collections.emptyList())
                                .author("네이버 블로그")
                                .authorId("naver")
                                .role("USER")
                                .region(item.getLink())
                                .isSns(true)
                                .timestamp(postDate)
                                .views(0).likesCount(0).commentsCount(0).liked(false).bookmarked(false)
                                .build();
                    }).collect(Collectors.toList());
            resultList.addAll(blogPosts);
        }

        // 3. 두 리스트를 랜덤하게 섞기
        Collections.shuffle(resultList);

        return ResponseEntity.ok(resultList);
    }

    // 숫자 코드를 지역 이름으로 바꾸는 헬퍼 메서드 (기존 로직 유지)
    private String convertCodeToName(Integer code) {
        if (code == null) return "";
        switch (code) {
            case 1: return "서울";
            case 2: return "인천";
            case 3: return "대전";
            case 4: return "대구";
            case 5: return "광주";
            case 6: return "부산";
            case 7: return "울산";
            case 8: return "세종";
            case 31: return "경기";
            case 32: return "강원";
            case 33: return "충북";
            case 34: return "충남";
            case 35: return "경북";
            case 36: return "경남";
            case 37: return "전북";
            case 38: return "전남";
            case 39: return "제주";
            default: return "";
        }
    }
}