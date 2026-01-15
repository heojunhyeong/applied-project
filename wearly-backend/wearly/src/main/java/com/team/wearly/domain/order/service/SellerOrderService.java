package com.team.wearly.domain.order.service;

import com.team.wearly.domain.order.dto.request.SellerOrderDeliveryUpdateRequest;
import com.team.wearly.domain.order.dto.request.SellerOrderStatusUpdateRequest;
import com.team.wearly.domain.order.dto.response.SellerOrderDetailResponse;
import com.team.wearly.domain.order.dto.response.SellerOrderListItemResponse;
import com.team.wearly.domain.order.entity.Order;
import com.team.wearly.domain.order.entity.OrderDelivery;
import com.team.wearly.domain.order.entity.enums.OrderStatus;
import com.team.wearly.domain.order.repository.SellerOrderDeliveryRepository;
import com.team.wearly.domain.order.repository.SellerOrderRepository;
import com.team.wearly.domain.user.entity.User;
import com.team.wearly.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SellerOrderService {

}
