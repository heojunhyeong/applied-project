package com.team.wearly.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

// 주문 내역 요약 DTO
@Getter
@Builder
@AllArgsConstructor
public class OrderHistoryResponse {
    private String orderId;
    private LocalDateTime orderDate;
    private Long totalPrice;
    private String orderStatus;
    // 대표 상품명
    private String representativeProductName;
    // 대표 이미지
    private String representativeImageUrl;
}