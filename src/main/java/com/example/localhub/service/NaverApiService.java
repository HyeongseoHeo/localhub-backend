package com.example.localhub.service;

import com.example.localhub.dto.tour.NaverBlogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NaverApiService {

    // ★ 보안: application.properties에 설정한 값을 가져옵니다.
    @Value("${naver.client.id}")
    private String CLIENT_ID;

    @Value("${naver.client.secret}")
    private String CLIENT_SECRET;

    public List<NaverBlogResponse.Item> searchBlog(String keyword) {
        RestTemplate restTemplate = new RestTemplate();

        // 1. 헤더에 키 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Naver-Client-Id", CLIENT_ID);
        headers.add("X-Naver-Client-Secret", CLIENT_SECRET);

        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        // 2. 검색어 URL 생성 (정확도순 정렬, 10개만)
        // (URI 객체 대신 String을 쓰면 RestTemplate이 알아서 인코딩 해줍니다)
        String url = "https://openapi.naver.com/v1/search/blog.json?query=" + keyword + "&display=10&sort=sim";

        try {
            // 3. 네이버에 요청 전송
            ResponseEntity<NaverBlogResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, httpEntity, NaverBlogResponse.class
            );

            // 4. 결과 반환
            if (response.getBody() != null && response.getBody().getItems() != null) {
                return response.getBody().getItems();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
