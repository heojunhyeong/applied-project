package com.team.wearly.domain.payment.controller;

import com.team.wearly.domain.membership.dto.MembershipResponse;
import com.team.wearly.domain.membership.entity.Membership;
import com.team.wearly.domain.membership.repository.MembershipRepository;
import com.team.wearly.domain.membership.service.MembershipService;
import com.team.wearly.domain.payment.dto.request.BillingConfirmRequest;
import com.team.wearly.domain.payment.dto.request.PaymentCancelRequest;
import com.team.wearly.domain.payment.dto.request.TossPaymentConfirmRequest;
import com.team.wearly.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final MembershipService membershipService;
    private final MembershipRepository membershipRepository;

    @PostMapping("/toss/confirm")
    public ResponseEntity<String> confirmPayment(
            Authentication authentication, // 인증 정보 추가
            @RequestBody TossPaymentConfirmRequest request) {

//        Long userId = Long.parseLong(authentication.getName());
//
//        paymentService.confirmPayment(
//                request.getPaymentKey(),
//                request.getOrderId(),
//                request.getAmount()
//        );
//        return ResponseEntity.ok().build();

        // 테스트용, 위 주석으로 교체 필요
        Long userId;
        if (authentication == null) {
            userId = 2L;
        } else {
            userId = Long.parseLong(authentication.getName());
        }

        paymentService.confirmPayment(
                request.getPaymentKey(),
                request.getOrderId(),
                request.getAmount()
        );
        return ResponseEntity.ok("결제 승인 성공!");
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<String> cancelPayment(
            @PathVariable String orderId,
            @RequestBody PaymentCancelRequest request) {

        paymentService.cancelPayment(orderId, request.cancelReason());
        return ResponseEntity.ok("결제가 취소되었습니다.");
    }

    /**
     * 빌링키 발급 승인 엔드포인트
     * 프론트에서 카드 인증 후 받은 authKey를 우리 서버로 보내면
     * 우리 서버는 토스에 빌링키(정기 결제 권한)를 요청
     * TODO: 주석 추가 예정
     */
    @PostMapping("/billing/confirm")
    public ResponseEntity<String> confirmBilling(
            Authentication authentication,
            @RequestBody BillingConfirmRequest request) {

//        Long userId = Long.parseLong(authentication.getName());
//
//        // 빌링키 발급 및 멤버십 활성화 로직 실행
//        paymentService.confirmBilling(userId, request.getAuthKey(), request.getCustomerKey());
//
//        return ResponseEntity.ok("멤버십 정기 결제가 성공적으로 등록되었습니다.");

        // 테스트용, 위 주석으로 교체 필요
        Long userId;
        if (authentication == null) {
            userId = 2L;
        } else {
            userId = Long.parseLong(authentication.getName());
        }

        // 빌링키 발급 및 멤버십 활성화 로직 실행
        paymentService.confirmBilling(userId, request.getAuthKey(), request.getCustomerKey());

        return ResponseEntity.ok("멤버십 정기 결제가 성공적으로 등록되었습니다.");
    }

    @PostMapping("/membership/terminate")
    public ResponseEntity<String> terminateMembership(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        membershipService.reserveTermination(userId);
        return ResponseEntity.ok("다음 결제일부터 멤버십이 갱신되지 않습니다. 이번 달 혜택은 유지됩니다.");

        // 테스트용, 위 주석으로 교체 필요
//        Long userId;
//        if (authentication == null) {
//            userId = 2L;
//        } else {
//            userId = Long.parseLong(authentication.getName());
//        }
//
//        membershipService.reserveTermination(userId);
//        return ResponseEntity.ok("다음 결제일부터 멤버십이 갱신되지 않습니다. 이번 달 혜택은 유지됩니다.");
    }

    /**
     * 내 멤버십 상태 조회 API
     */
    @GetMapping("/membership/me")
    public ResponseEntity<MembershipResponse> getMyMembership(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());

        // 멤버십 정보를 조회 (없으면 null 혹은 404 처리)
        Membership membership = membershipRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("가입된 멤버십 정보가 없습니다."));

        return ResponseEntity.ok(MembershipResponse.from(membership));
    }
}
