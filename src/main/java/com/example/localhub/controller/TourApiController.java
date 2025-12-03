package com.example.localhub.controller;

import com.example.localhub.service.TourApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tour")
@RequiredArgsConstructor
public class TourApiController {

    private final TourApiService tourApiService;

    // 상세 설명 조회 (이건 남겨두세요! 프론트에서 설명 볼 때 씀)
    @GetMapping("/detail")
    public ResponseEntity<String> getTourDetail(@RequestParam String contentId) {
        String overview = tourApiService.getTourOverview(contentId);
        return ResponseEntity.ok(overview);
    }
}
