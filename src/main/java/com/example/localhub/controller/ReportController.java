package com.example.localhub.controller;

import com.example.localhub.dto.report.ReportRequest;
import com.example.localhub.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<Void> createReport(@RequestBody ReportRequest req) {
        // 서비스 호출 시 DTO의 모든 정보를 전달
        reportService.createReport(
                req.getReporterId(),
                req.getTargetType(),
                req.getTargetId(),
                req.getReason(),
                req.getContent()
        );
        // 성공 시 200 OK 응답을 반환
        return ResponseEntity.ok().build();
    }
}
