package com.example.localhub.dto.report;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportRequest {

    private Long postId;
    private String reason; // "스팸 또는 광고" 이런 프론트 문자열
    private String detail; // 기타 설명
}

