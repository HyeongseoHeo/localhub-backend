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

    // 신고 대상 타입
    @Column(nullable = false)
    private String targetType;

    // 신고 대상 ID
    @Column(nullable = false)
    private Long targetId;

    // 신고한 사람 (FK는 유지)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id")
    private Member reporter;

    @Enumerated(EnumType.STRING)
    private ReportReason reason;

    @Column(columnDefinition = "TEXT")
    private String detail;

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

