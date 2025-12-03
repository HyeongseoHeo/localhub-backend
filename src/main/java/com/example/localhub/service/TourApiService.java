package com.example.localhub.service;

import com.example.localhub.dto.tour.TourApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TourApiService {

    @Value("${tour.api.key}")
    private String API_KEY;

    public List<TourApiResponse.Item> searchTourData(String keyword, Integer areaCode) {
        RestTemplate restTemplate = new RestTemplate();

        // URL 생성 빌더
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://apis.data.go.kr/B551011/KorService1/searchKeyword1")
                .queryParam("serviceKey", API_KEY)
                .queryParam("numOfRows", 10)      // 가져올 개수
                .queryParam("pageNo", 1)
                .queryParam("MobileOS", "ETC")
                .queryParam("MobileApp", "LocalHub")
                .queryParam("_type", "json")      // JSON 응답 요청
                .queryParam("arrange", "A")       // 정렬 (A=제목순)
                .queryParam("keyword", keyword)
                .queryParam("contentTypeId", 12); // 12: 관광지

        // ★ 핵심: 지역 코드가 있을 때만 URL에 파라미터 추가
        if (areaCode != null && areaCode > 0) {
            builder.queryParam("areaCode", areaCode);
        }

        // 인코딩 문제 방지를 위해 build(true) 사용
        URI uri = builder.encode().build(true).toUri();

        try {
            TourApiResponse response = restTemplate.getForObject(uri, TourApiResponse.class);

            // 데이터가 정상적으로 있는지 깊은 검사(Null Safety)
            if (response != null &&
                    response.getResponse() != null &&
                    response.getResponse().getBody() != null &&
                    response.getResponse().getBody().getItems() != null) {

                return response.getResponse().getBody().getItems().getItem();
            }
        } catch (Exception e) {
            e.printStackTrace(); // 에러 발생 시 로그 출력
        }

        // 데이터가 없거나 에러 나면 빈 리스트 반환 (서버 멈춤 방지)
        return Collections.emptyList();
    }

    public String getTourOverview(String contentId) {
        RestTemplate restTemplate = new RestTemplate();

        URI uri = UriComponentsBuilder.fromHttpUrl("http://apis.data.go.kr/B551011/KorService1/detailCommon1")
                .queryParam("serviceKey", API_KEY)
                .queryParam("MobileOS", "ETC")
                .queryParam("MobileApp", "LocalHub")
                .queryParam("_type", "json")
                .queryParam("contentId", contentId)
                .queryParam("overviewYN", "Y")      // ★ 설명 필수 요청
                .encode()
                .build(true)
                .toUri();

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