package com.example.localhub.service;

import com.example.localhub.dto.tour.TourApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;
import java.nio.charset.StandardCharsets;

import java.net.URI;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TourApiService {

    @Value("${tour.api.key}")
    private String API_KEY;

    // ★ [추가] API 키를 URL 삽입 전에 수동으로 인코딩하는 헬퍼 메서드
    private String getEncodedKey() {
        // API_KEY에 있는 '='이나 '+' 문자를 %3D, %2B 등으로 치환하여 URL 구조가 깨지는 것을 방지
        return UriUtils.encode(API_KEY, StandardCharsets.UTF_8.name());
    }

    public List<TourApiResponse.Item> searchTourData(String keyword, Integer areaCode) {
        RestTemplate restTemplate = new RestTemplate();

        try {
            // ★ keyword도 수동으로 인코딩
            String encodedKeyword = keyword != null && !keyword.isEmpty()
                    ? UriUtils.encode(keyword, StandardCharsets.UTF_8.name())
                    : "";

            // ★ serviceKey도 인코딩
            String encodedServiceKey = getEncodedKey();

            // URL 생성 빌더
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://apis.data.go.kr/B551011/KorService1/searchKeyword1")
                    .queryParam("serviceKey", encodedServiceKey)
                    .queryParam("numOfRows", 10)
                    .queryParam("pageNo", 1)
                    .queryParam("MobileOS", "ETC")
                    .queryParam("MobileApp", "LocalHub")
                    .queryParam("_type", "json")
                    .queryParam("arrange", "A")
                    .queryParam("keyword", encodedKeyword)  // ★ 인코딩된 키워드 사용
                    .queryParam("contentTypeId", 12);

            if (areaCode != null && areaCode > 0) {
                builder.queryParam("areaCode", areaCode);
            }

            // 이미 인코딩했으므로 build(false)
            URI uri = builder.build(false).toUri();

            System.out.println("🌐 Tour API URI: " + uri);

            TourApiResponse response = restTemplate.getForObject(uri, TourApiResponse.class);

            if (response != null &&
                    response.getResponse() != null &&
                    response.getResponse().getBody() != null &&
                    response.getResponse().getBody().getItems() != null) {

                List<TourApiResponse.Item> items = response.getResponse().getBody().getItems().getItem();
                System.out.println("✅ Tour API 결과: " + (items != null ? items.size() : 0) + "개");
                return items;
            } else {
                System.out.println("⚠️ Tour API 응답 데이터 없음");
            }
        } catch (Exception e) {
            System.err.println("❌ Tour API 에러:");
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    public String getTourOverview(String contentId) {
        RestTemplate restTemplate = new RestTemplate();

        // 인코딩된 키 사용
        String encodedServiceKey = getEncodedKey();

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://apis.data.go.kr/B551011/KorService1/detailCommon1")
                .queryParam("serviceKey", encodedServiceKey) // ★ FIX: 인코딩된 키 사용
                .queryParam("MobileOS", "ETC")
                .queryParam("MobileApp", "LocalHub")
                .queryParam("_type", "json")
                .queryParam("contentId", contentId)
                .queryParam("overviewYN", "Y");

        // 인코딩된 키를 사용했으므로, build(false)로 빌드
        URI uri = builder.build(false).toUri();

        try {
            TourApiResponse response = restTemplate.getForObject(uri, TourApiResponse.class);

            if (response != null &&
                    response.getResponse().getBody().getItems() != null &&
                    !response.getResponse().getBody().getItems().getItem().isEmpty()) {

                return response.getResponse().getBody().getItems().getItem().get(0).getOverview();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "상세 정보가 없습니다.";
    }
}