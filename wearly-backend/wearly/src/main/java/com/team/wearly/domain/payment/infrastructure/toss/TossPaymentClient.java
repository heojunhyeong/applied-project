package com.team.wearly.domain.payment.infrastructure.toss;

import com.team.wearly.domain.payment.dto.response.TossBillingConfirmResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
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


    @Value("${payment.toss.secret-key:}")
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

    }

    public TossConfirmResponse executeBillingPayment(String billingKey, String customerKey, String orderId, Long amount, String orderName) {
        // 빌링키 결제 API URL
        String url = "https://api.tosspayments.com/v1/billing/" + billingKey;

        String authorizations = Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + authorizations);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 바디 (최초 결제 시 필요한 정보들)
        Map<String, Object> params = new HashMap<>();
        params.put("customerKey", customerKey);
        params.put("orderId", orderId);
        params.put("amount", amount);
        params.put("orderName", orderName);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<TossConfirmResponse> response = restTemplate.postForEntity(
                    url,
                    request,
                    TossConfirmResponse.class
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("빌링 결제 실행 실패: " + e.getMessage());
        }
    }

    // 결제 취소 메서드
    // TODO: 주석 추가 예정
    public void cancelPayment(String paymentKey, String cancelReason) {
        String url = "https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel";

        String authorizations = Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + authorizations);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> params = new HashMap<>();
        params.put("cancelReason", cancelReason);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(params, headers);

        try {
            restTemplate.postForEntity(url, request, Object.class);
        } catch (Exception e) {
            throw new RuntimeException("토스 결제 취소 요청 실패: " + e.getMessage());
        }
    }

    public TossBillingConfirmResponse issueBillingKey(String authKey, String customerKey) {
        String url = "https://api.tosspayments.com/v1/billing/authorizations/confirm";

        String authorizations = Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + authorizations);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> params = new HashMap<>();
        params.put("authKey", authKey);
        params.put("customerKey", customerKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<TossBillingConfirmResponse> response = restTemplate.postForEntity(
                    url,
                    request,
                    TossBillingConfirmResponse.class
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("빌링키 발급 요청 실패", e);
        }
    }

    // 결제 승인 전 최종 확인을 위한 메서드
    // TODO: 주석 추가 예정
    public TossConfirmResponse getPaymentByOrderId(String orderId) {
        String url = "https://api.tosspayments.com/v1/payments/orders/" + orderId;

        String authorizations = Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + authorizations);

        try {
            ResponseEntity<TossConfirmResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(headers), TossConfirmResponse.class);
            return response.getBody();
        } catch (Exception e) {
            // 결제 내역이 없으면 null 반환
            return null;
        }
    }
}
