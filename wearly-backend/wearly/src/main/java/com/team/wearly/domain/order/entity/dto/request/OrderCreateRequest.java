package com.team.wearly.domain.order.entity.dto.request;

import com.team.wearly.domain.product.entity.enums.Size;
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
    private Long productId;
    private Long quantity;
    private String address;
    private String detailAddress;
    private Long zipCode;
    private Size size;

    private Long userCouponId;
}