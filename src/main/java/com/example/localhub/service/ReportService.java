package com.example.localhub.service;

import com.example.localhub.domain.board.Post;
import com.example.localhub.domain.board.Comment;
import com.example.localhub.domain.member.Member;
import com.example.localhub.domain.report.Report;
import com.example.localhub.domain.report.ReportReason;
import com.example.localhub.repository.CommentRepository;
import com.example.localhub.repository.MemberRepository;
import com.example.localhub.repository.PostRepository;
import com.example.localhub.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional // 데이터 변경 및 DB 조회(Exists)가 많으므로 트랜잭션 유지
public class ReportService {

    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository; // 👈 댓글 검증을 위해 주입

    /**
     * [최종] 신고 접수 로직 (게시글 및 댓글 모두 처리)
     */
    public void createReport(Long reporterId, String targetType, Long targetId, String reason, String content) {

        // 1. 신고자 존재 확인
        Member reporter = memberRepository.findById(reporterId)
                .orElseThrow(() -> new RuntimeException("신고자 정보(memberId)를 찾을 수 없습니다."));

        // 2. 중복 신고 확인
        if (reportRepository.existsByReporterIdAndTargetTypeAndTargetId(reporterId, targetType, targetId)) {
            throw new RuntimeException("이미 동일한 대상을 신고했습니다.");
        }

        // 3. 신고 대상(Post/Comment) 유효성 검사
        validateTargetExistence(targetType, targetId);

        // 4. Report 객체 생성 및 저장
        Report report = new Report();
        report.setReporter(reporter);

        // 5. 신고 대상 정보 기록
        report.setTargetType(targetType);
        report.setTargetId(targetId);
        report.setReason(mapReason(reason)); // String을 Enum으로 변환
        report.setDetail(content);

        reportRepository.save(report);

    }

    /**
     * 신고 대상이 실제로 존재하는지 확인 (Post 또는 Comment)
     */
    private void validateTargetExistence(String targetType, Long targetId) {
        if (targetType.equalsIgnoreCase("POST")) {
            postRepository.findById(targetId)
                    .orElseThrow(() -> new RuntimeException("신고 대상 게시글(Post)을 찾을 수 없습니다."));
        } else if (targetType.equalsIgnoreCase("COMMENT")) {
            commentRepository.findById(targetId)
                    .orElseThrow(() -> new RuntimeException("신고 대상 댓글(Comment)을 찾을 수 없습니다."));
        } else {
            throw new RuntimeException("유효하지 않은 신고 대상 타입입니다: " + targetType);
        }
    }


    /**
     * [재사용] 신고 사유 매핑 로직
     */
    private ReportReason mapReason(String reason) {
        return switch (reason) {
            case "스팸 또는 광고" -> ReportReason.SPAM_AD;
            case "욕설 또는 혐오 발언" -> ReportReason.ABUSE_HATE;
            case "부적절한 콘텐츠" -> ReportReason.INAPPROPRIATE;
            case "개인정보 노출" -> ReportReason.PERSONAL_INFO;
            case "저작권 침해" -> ReportReason.COPYRIGHT;
            default -> ReportReason.OTHER;
        };
    }
}

