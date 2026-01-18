package com.team.wearly.domain.review.entity;

import com.team.wearly.domain.product.entity.Product;
import com.team.wearly.domain.review.entity.enums.ReviewStatus;
import com.team.wearly.global.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(
        name = "product_review",
        indexes = {
                @Index(name = "idx_review_product", columnList = "product_id, created_date"),
                @Index(name = "idx_review_reviewer", columnList = "reviewer_id, created_date")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_pr_reviewer_product_order",
                        columnNames = {"reviewer_id", "product_id", "order_id"}
                )
        }
)
public class ProductReview extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reviewer_id", nullable = false)
    private Long reviewerId;

    @Column(name = "order_id", nullable = false, length = 255)
    private String orderId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int rating;

    @Column(nullable = false, length = 2000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ReviewStatus status = ReviewStatus.ACTIVE;

    public void hide() { this.status = ReviewStatus.HIDDEN; }
    public void restore() { this.status = ReviewStatus.ACTIVE; }
}
