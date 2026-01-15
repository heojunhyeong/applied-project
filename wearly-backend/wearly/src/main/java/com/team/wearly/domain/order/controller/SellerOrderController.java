package com.team.wearly.domain.order.controller;

import com.team.wearly.domain.order.dto.request.SellerOrderDeliveryUpdateRequest;
import com.team.wearly.domain.order.dto.request.SellerOrderStatusUpdateRequest;
import com.team.wearly.domain.order.dto.response.SellerOrderDetailResponse;
import com.team.wearly.domain.order.dto.response.SellerOrderListItemResponse;
import com.team.wearly.domain.order.entity.enums.OrderStatus;
import com.team.wearly.domain.order.service.SellerOrderService;
import com.team.wearly.domain.user.entity.Seller;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seller")
public class SellerOrderController {

}
