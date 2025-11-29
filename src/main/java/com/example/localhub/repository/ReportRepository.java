package com.example.localhub.repository;

import com.example.localhub.domain.report.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {

    /**
     * 특정 사용자가 특정 대상(게시글/댓글)을 이미 신고했는지 확인합니다.
     * * @param reporterId 신고자 ID
     * @param targetType 신고 대상 타입 ("POST" or "COMMENT")
     * @param targetId 신고 대상 ID
     * @return 이미 존재하면 true
     */
    boolean existsByReporterIdAndTargetTypeAndTargetId(Long reporterId, String targetType, Long targetId);
}

