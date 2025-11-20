package com.example.localhub.controller;

import com.example.localhub.dto.report.ReportRequest;
import com.example.localhub.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public void report(@RequestBody ReportRequest request,
                       @RequestParam Long memberId) {
        reportService.reportPost(request, memberId);
    }
}

