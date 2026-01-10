//package com.team.wearly.global.config;
//
//// 테스트용 Mock
//
//import com.team.wearly.domain.order.entity.Order;
//import com.team.wearly.domain.order.entity.enums.OrderStatus;
//import com.team.wearly.domain.order.repository.OrderRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class DataInit implements ApplicationRunner {
//    private final OrderRepository orderRepository;
//
//    @Override
//    public void run(ApplicationArguments args) {
//        orderRepository.save(Order.builder()
//                .orderId("ORD-TEST-003") // 테스트용 아이디
//                .totalPrice(15000L)
//                .orderStatus(OrderStatus.BEFORE_PAID) // 결제 전 상태
//                .build());
//    }
//}