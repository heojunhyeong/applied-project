package com.team.wearly.domain.review.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "sale_reviews")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    private Long reviewerId;

//    private Long productId;

    // 리뷰 내용
    @Column(name = "content", nullable = false, length = 500)
    private String content;

    // 리뷰 작성일자
    @CreatedDate
    private LocalDateTime createdDate;

//    public static SaleReview create(Long reviewerId, Long productId, String content) {
//        return SaleReview.builder()
//                .reviewerId(reviewerId)
//                .productId(productId)
//                .content(content)
//                .createdDate(LocalDateTime.now())
//                .build();
//    }

    public void updateContent(String content) {
        this.content = content;
    }
}
