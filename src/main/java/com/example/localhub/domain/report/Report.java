package com.example.localhub.domain.report;

import com.example.localhub.domain.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
// 👇 [수정/추가] 한 유저가 같은 대상을 중복 신고하지 못하도록 제약 조건 추가
@Table(name = "reports", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"target_type", "target_id", "reporter_id"})
})
@Getter
@Setter
@NoArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 👇 [추가] 신고 대상 타입 ("POST" 또는 "COMMENT")
    @Column(nullable = false)
    private String targetType;

    // 👇 [추가] 신고 대상 ID (게시글 ID 또는 댓글 ID)
    @Column(nullable = false)
    private Long targetId;

    // 신고한 사람 (FK는 유지)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id")
    private Member reporter;

    @Enumerated(EnumType.STRING)
    private ReportReason reason;

    @Column(columnDefinition = "TEXT")
    private String detail; // 기타 등 추가 설명 (기존 코드의 'detail' 필드를 재사용)

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

