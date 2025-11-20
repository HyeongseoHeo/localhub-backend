package com.example.localhub.service;

import com.example.localhub.domain.board.Post;
import com.example.localhub.domain.member.Member;
import com.example.localhub.domain.report.Report;
import com.example.localhub.domain.report.ReportReason;
import com.example.localhub.dto.report.ReportRequest;
import com.example.localhub.repository.MemberRepository;
import com.example.localhub.repository.PostRepository;
import com.example.localhub.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    public void reportPost(ReportRequest request, Long memberId) {
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new RuntimeException("게시글 없음"));

        Member reporter = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원 없음"));

        Report report = new Report();
        report.setPost(post);
        report.setReporter(reporter);
        report.setReason(mapReason(request.getReason()));
        report.setDetail(request.getDetail());

        reportRepository.save(report);
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

