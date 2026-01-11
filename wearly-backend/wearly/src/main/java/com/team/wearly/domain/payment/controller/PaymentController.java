package com.team.wearly.domain.payment.controller;

import com.team.wearly.domain.payment.dto.request.TossPaymentConfirmRequest;
import com.team.wearly.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/toss/confirm")
    public ResponseEntity<String> confirmPayment(@RequestBody TossPaymentConfirmRequest request) {

        paymentService.confirmPayment(
                request.getPaymentKey(),
                request.getOrderId(),
                request.getAmount()
        );
        return ResponseEntity.ok().build();
    }
}
