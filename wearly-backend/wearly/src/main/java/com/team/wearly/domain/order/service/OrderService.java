package com.team.wearly.domain.order.service;


import com.team.wearly.domain.order.dto.response.OrderDetailResponse;
import com.team.wearly.domain.order.dto.response.OrderHistoryResponse;
import com.team.wearly.domain.order.dto.response.OrderSheetResponse;
import com.team.wearly.domain.order.entity.Order;
import com.team.wearly.domain.order.entity.dto.request.OrderCreateRequest;
import com.team.wearly.domain.product.entity.enums.Size;

import java.util.List;

public interface OrderService {
    public Order createOrder(Long userId, OrderCreateRequest request);

    public List<OrderHistoryResponse> getOrderHistory(Long userId);

    public OrderDetailResponse getOrderDetail(String orderId);

    public void confirmPurchase(Long userId, String orderId, Long orderDetailId);

    /**
     * 상품명 키워드로 주문 상세 검색 (같은 날 주문된 상품 포함)
     * @return 키워드가 포함된 상품과 같은 날 주문된 모든 상품의 주문 상세 목록
     */
    public List<OrderDetailResponse.OrderItemDto> searchOrderDetailsByKeyword(Long userId, String keyword);

    OrderSheetResponse getOrderSheet(Long userId, List<Long> cartItemIds, Long productId, Long quantity, Size size);}
