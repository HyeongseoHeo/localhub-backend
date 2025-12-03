package com.example.localhub.dto.report;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Getter @Setter
public class ReportRequest {

    @NotNull private Long reporterId;     // 신고자 ID
    @NotBlank private String targetType;  // 신고 대상 타입 ("POST" 또는 "COMMENT")
    @NotNull private Long targetId;       // 신고 대상 ID
    @NotBlank private String reason;      // 신고 사유 (Enum 또는 코드)

    private String content;
}

