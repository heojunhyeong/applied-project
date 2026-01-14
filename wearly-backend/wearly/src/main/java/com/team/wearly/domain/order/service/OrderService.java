package com.team.wearly.domain.order.service;


import com.team.wearly.domain.order.dto.response.OrderDetailResponse;
import com.team.wearly.domain.order.dto.response.OrderHistoryResponse;
import com.team.wearly.domain.order.entity.Order;
import com.team.wearly.domain.order.entity.dto.request.OrderCreateRequest;

import java.util.List;

public interface OrderService {
    public Order createOrder(Long userId, OrderCreateRequest request);

    public List<OrderHistoryResponse> getOrderHistory(Long userId);

    public OrderDetailResponse getOrderDetail(String orderId);


}
