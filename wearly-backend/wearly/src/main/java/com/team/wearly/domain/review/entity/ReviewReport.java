package com.team.wearly.domain.review.entity;

import com.team.wearly.domain.review.entity.enums.ReviewReportReason;
import com.team.wearly.domain.review.entity.enums.ReviewReportStatus;
import com.team.wearly.global.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(
        name = "review_report",
        indexes = {
                @Index(name = "idx_report_review", columnList = "review_id, created_date"),
                @Index(name = "idx_report_status", columnList = "status, created_date"),
                @Index(name = "idx_report_reporter", columnList = "reporter_id, created_date")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_report_review_reporter",
                        columnNames = {"review_id", "reporter_id"}
                )
        }
)
public class ReviewReport extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 신고 대상 리뷰 연관관계 // 리뷰를 join하기 위한 FK 관계
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "review_id", nullable = false)
    private ProductReview review;

    // 신고자 ID // 신고한 사람(판매자 or 유저)
    @Column(name = "reporter_id", nullable = false)
    private Long reporterId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReviewReportReason reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ReviewReportStatus status = ReviewReportStatus.PENDING;

    public void resolve() { this.status = ReviewReportStatus.RESOLVED; }
    public void reject() { this.status = ReviewReportStatus.REJECTED; }
}
