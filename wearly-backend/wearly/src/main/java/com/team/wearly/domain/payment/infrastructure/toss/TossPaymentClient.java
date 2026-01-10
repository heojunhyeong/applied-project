package com.team.wearly.domain.payment.infrastructure.toss;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 서버가 토스 페이먼츠 서버에 승인 요청을 하는 클래스
 * RestTemplate을 사용하여 HTTP 요청을 보냄
 * 외부 API(토스)와 직접 통신하므로 infrastructure 패키지에 만들었음
 *
 *
 * @author 허준형
 * @DateOfCreated 2026-01-10
 * @DateOfEdit 2025-01-10
 */


@Component
public class TossPaymentClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${payment.toss.secret-key:test_secret_key_for_toss_payments}")
    private String secretKey;

    public TossConfirmResponse confirmPayment(String paymentKey, String orderId, Long amount) {

        // 토스 API URL
        String url = "https://api.tosspayments.com/v1/payments/confirm";

        // 인증 헤더 생성(Base64 인코딩)
        String authorizations = Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + authorizations);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 바디 생성(JSON)
        Map<String, Object> params = new HashMap<>();
        params.put("paymentKey", paymentKey);
        params.put("orderId", orderId);
        params.put("amount", amount);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(params, headers);

        // 토스 서버에 POST 요청 보내기
        try {
            ResponseEntity<TossConfirmResponse> response = restTemplate.postForEntity(
                    url,
                    request,
                    TossConfirmResponse.class
            );
            return response.getBody();
        } catch (Exception e) {
            // @TODO 해당 예외 추후 커스텀 예외로 교체 에정
            throw new RuntimeException("토스 결제 승인 요청 실패", e);
        }
// // Mock
//        return new TossConfirmResponse(
//                paymentKey,  // 프론트에서 보낸 키 그대로 사용
//                orderId,     // 프론트에서 보낸 주문번호 그대로 사용
//                "DONE",      // 상태는 무조건 성공(DONE)
//                amount,      // 금액 그대로 사용
//                "CARD",      // 결제수단은 카드로 가정
//                "2024-01-10T10:00:00+09:00" // 승인 시간
//        );
    }
}
