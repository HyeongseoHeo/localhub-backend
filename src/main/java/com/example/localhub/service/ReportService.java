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
@Transactional
public class ReportService {

    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public void createReport(Long reporterId, String targetType, Long targetId, String reason, String content) {

        Member reporter = memberRepository.findById(reporterId)
                .orElseThrow(() -> new RuntimeException("신고자 정보(memberId)를 찾을 수 없습니다."));

        if (reportRepository.existsByReporterIdAndTargetTypeAndTargetId(reporterId, targetType, targetId)) {
            throw new RuntimeException("이미 동일한 대상을 신고했습니다.");
        }

        validateTargetExistence(targetType, targetId);

        Report report = new Report();
        report.setReporter(reporter);

        report.setTargetType(targetType);
        report.setTargetId(targetId);
        report.setReason(mapReason(reason));
        report.setDetail(content);

        reportRepository.save(report);

    }

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

