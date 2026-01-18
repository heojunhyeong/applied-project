package com.team.wearly.domain.payment.infrastructure.toss;

import com.team.wearly.domain.payment.dto.response.TossBillingConfirmResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 토스 페이먼츠 외부 API와 통신하여 결제 승인, 빌링키 발급, 취소 및 조회를 처리하는 클라이언트 클래스
 *
 * @author 허준형
 * @DateOfCreated 2026-01-10
 * @DateOfEdit 2026-01-15
 */
@Component
public class TossPaymentClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${payment.toss.secret-key:}")
    private String secretKey;

    /**
     * 프론트엔드에서 전달받은 결제 정보를 토스 서버에 보내 최종 결제 승인을 요청함
     *
     * @param paymentKey 토스에서 발행한 결제 고유 키
     * @param orderId    우리 시스템의 주문 번호
     * @param amount     결제 금액
     * @return 토스에서 응답한 결제 승인 상세 정보
     * @author 허준형
     * @DateOfCreated 2026-01-10
     * @DateOfEdit 2026-01-15
     */
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
                    TossConfirmResponse.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("토스 결제 승인 요청 실패: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("토스 결제 승인 요청 실패", e);
        }

    }

    /**
     * 발급된 빌링키를 사용하여 사용자의 개입 없이 정기 결제를 실행함
     *
     * @param billingKey  자동 결제용 빌링키
     * @param customerKey 고객 식별 키
     * @param orderId     주문 번호
     * @param amount      결제 금액
     * @param orderName   주문 상품명
     * @return 결제 승인 응답 정보
     * @author 허준형
     * @DateOfCreated 2026-01-15
     * @DateOfEdit 2026-01-15
     */
    public TossConfirmResponse executeBillingPayment(String billingKey, String customerKey, String orderId, Long amount,
            String orderName) {
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
                    TossConfirmResponse.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("빌링 결제 실행 실패: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("빌링 결제 실행 실패: " + e.getMessage());
        }
    }

    /**
     * 이미 완료된 결제 건에 대해 부분 혹은 전체 취소를 요청함
     *
     * @param paymentKey   취소할 결제의 고유 키
     * @param cancelReason 결제 취소 사유
     * @author 허준형
     * @DateOfCreated 2026-01-15
     * @DateOfEdit 2026-01-15
     */
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
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("토스 결제 취소 요청 실패: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("토스 결제 취소 요청 실패: " + e.getMessage());
        }
    }

    /**
     * 카드 인증 후 전달받은 authKey를 통해 정기 결제를 위한 빌링키를 최종 발급받음
     *
     * @param authKey     토스 창에서 인증 후 받은 인증 키
     * @param customerKey 고객 식별 키
     * @return 발급된 빌링키 정보를 포함한 응답 객체
     * @author 허준형
     * @DateOfCreated 2026-01-15
     * @DateOfEdit 2026-01-15
     */
    public TossBillingConfirmResponse issueBillingKey(String authKey, String customerKey) {
        String url = "https://api.tosspayments.com/v1/billing/authorizations/issue";

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
                    TossBillingConfirmResponse.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("빌링키 발급 요청 실패: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("빌링키 발급 요청 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 주문 번호를 기준으로 토스 서버에 현재 결제 상태 및 상세 내역을 조회함
     *
     * @param orderId 조회를 원하는 주문 번호
     * @return 결제 상세 정보 (내역이 없을 경우 null)
     * @author 허준형
     * @DateOfCreated 2026-01-15
     * @DateOfEdit 2026-01-15
     */
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
