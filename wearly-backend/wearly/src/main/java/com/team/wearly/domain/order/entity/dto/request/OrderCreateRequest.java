package com.team.wearly.domain.order.entity.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequest {
    private Long userId;
    private Long totalPrice;


    private List<Long> cartItemIds;
    private String address;
    private String detailAddress;
    private Long zipCode;
}