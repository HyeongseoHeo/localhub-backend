package com.example.localhub.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CleanbotService {

    private final RestTemplate restTemplate;

    // 구글 Perspective API 주소
    private static final String API_URL = "https://commentanalyzer.googleapis.com/v1alpha1/comments:analyze?key=";

    @Value("${cleanbot.api.key}")
    private String apiKey;

    // ON/OFF 스위치 (기본값 true) 서버 전체 스위치임
    @Value("${cleanbot.enabled:true}")
    private boolean isEnabled;

    /**
     * 텍스트가 악성(욕설 등)인지 판별하는 메서드
     * @param content 검사할 내용
     * @return true(악성), false(정상)
     */
    public boolean isMalicious(String content) {
        // 1. 스위치가 꺼져있으면 무조건 정상(false) 처리
        if (!isEnabled) {
            return false;
        }

        if (content == null || content.isBlank()) {
            return false;
        }

        try {
            String url = API_URL + apiKey;

            // 2. 요청 Body 만들기
            Map<String, Object> body = new HashMap<>();
            Map<String, String> comment = new HashMap<>();
            comment.put("text", content);
            body.put("comment", comment);
            body.put("languages", Collections.singletonList("ko"));

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("TOXICITY", new HashMap<>());
            body.put("requestedAttributes", attributes);

            // 3. 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            // 4. API 호출
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            // 5. 점수 분석 (0.0 ~ 1.0)
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("attributeScores")) {
                Map<String, Object> attributeScores = (Map<String, Object>) responseBody.get("attributeScores");
                Map<String, Object> toxicity = (Map<String, Object>) attributeScores.get("TOXICITY");
                Map<String, Object> summaryScore = (Map<String, Object>) toxicity.get("summaryScore");

                // 안전하게 점수 추출
                Object valueObj = summaryScore.get("value");
                if (valueObj != null) {
                    Double score = Double.parseDouble(valueObj.toString());
                    log.info("클린봇 감지 점수: {}", score);

                    // 0.7점(70%) 이상이면 true 반환 (저장은 하되 꼬리표 붙임)
                    return score >= 0.7;
                }
            }

        } catch (Exception e) {
            log.error("클린봇 API 호출 중 오류 발생 (정상으로 간주하고 통과)", e);
            // API 오류가 나면 글 저장을 막지 말고 일단 통과시킴
            return false;
        }

        return false;
    }
}