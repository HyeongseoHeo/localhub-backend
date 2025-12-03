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

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CleanbotService {

    private final RestTemplate restTemplate;

    private static final String API_URL = "https://commentanalyzer.googleapis.com/v1alpha1/comments:analyze?key=";

    @Value("${cleanbot.api.key}")
    private String apiKey;

    // ON/OFF 스위치 (기본값 true) 서버 전체 스위치임
    @Value("${cleanbot.enabled:true}")
    private boolean isEnabled;

    private static final List<String> BAD_WORDS = Arrays.asList(
            "지랄", "시발", "씨발", "병신", "개새끼", "니애미", "좆", "존나", "미친","지1랄",
            "지.랄", "시1발", "시.발", "병1신", "병.신", "fuck", "tlqkf", "Tlqkf", "wlfkf", "ㅗ",
            "븅신", "븅", "꺼져", "꺼1져", "꺼.져", "껒", "껒져"
    );

    public boolean isMalicious(String content) {
        if (!isEnabled) {
            return false;
        }

        if (content == null || content.isBlank()) {
            return false;
        }

        for (String badWord : BAD_WORDS) {
            if (content.contains(badWord)) {
                log.info("클린봇(내장): 금칙어 감지 -> {}", badWord);
                return true;
            }
        }

        try {
            String url = API_URL + apiKey;

            // 요청 Body 만들기
            Map<String, Object> body = new HashMap<>();
            Map<String, String> comment = new HashMap<>();
            comment.put("text", content);
            body.put("comment", comment);
            body.put("languages", Collections.singletonList("ko"));

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("TOXICITY", new HashMap<>());
            attributes.put("PROFANITY", new HashMap<>());
            body.put("requestedAttributes", attributes);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("attributeScores")) {
                Map<String, Object> attributeScores = (Map<String, Object>) responseBody.get("attributeScores");
                Map<String, Object> toxicity = (Map<String, Object>) attributeScores.get("TOXICITY");
                Map<String, Object> summaryScore = (Map<String, Object>) toxicity.get("summaryScore");

                Object valueObj = summaryScore.get("value");
                if (valueObj != null) {
                    Double score = Double.parseDouble(valueObj.toString());
                    log.info("클린봇 감지 점수: {}", score);

                    // 0.7점(70%) 이상이면 true 반환 (저장은 하되 꼬리표 붙임)
                    return score >= 0.7;
                }
            }

        } catch (Exception e) {
            log.error("클린봇 API 호출 중 오류 (통과 처리)", e);
            return false;
        }

        return false;
    }
}