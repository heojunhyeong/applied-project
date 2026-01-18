package com.team.wearly.domain.payment.controller;

import com.team.wearly.domain.membership.dto.MembershipResponse;
import com.team.wearly.domain.membership.entity.Membership;
import com.team.wearly.domain.membership.repository.MembershipRepository;
import com.team.wearly.domain.membership.service.MembershipService;
import com.team.wearly.domain.payment.dto.request.BillingConfirmRequest;
import com.team.wearly.domain.payment.dto.request.PaymentCancelRequest;
import com.team.wearly.domain.payment.dto.request.TossPaymentConfirmRequest;
import com.team.wearly.domain.payment.service.PaymentService;
import com.team.wearly.domain.user.entity.Seller;
import com.team.wearly.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 일반 결제 승인, 취소 및 멤버십 정기 결제(빌링) 관련 기능을 제공하는 컨트롤러
 *
 * @author 허준형
 * @DateOfCreated 2026-01-15
 * @DateOfEdit 2026-01-15
 */
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final MembershipService membershipService;
    private final MembershipRepository membershipRepository;

    /**
     * 토스 페이먼츠의 결제 승인 API를 호출하여 최종적으로 주문 결제를 완료하는 API
     *
     * @param authentication 인증된 사용자의 정보
     * @param request        결제 승인에 필요한 paymentKey, orderId, amount 정보
     * @return 결제 승인 성공 메시지
     * @author 허준형
     * @DateOfCreated 2026-01-15
     * @DateOfEdit 2026-01-15
     */
    @PostMapping("/toss/confirm")
    public ResponseEntity<String> confirmPayment(
            Authentication authentication, // 인증 정보 추가
            @RequestBody TossPaymentConfirmRequest request) {

        Long userId = getUserIdFromAuthentication(authentication);

        paymentService.confirmPayment(
                request.getPaymentKey(),
                request.getOrderId(),
                request.getAmount());
        return ResponseEntity.ok().build();

        // 테스트용, 위 주석으로 교체 필요
        // Long userId;
        // if (authentication == null) {
        // userId = 2L;
        // } else {
        // userId = Long.parseLong(authentication.getName());
        // }
        //
        // paymentService.confirmPayment(
        // request.getPaymentKey(),
        // request.getOrderId(),
        // request.getAmount()
        // );
        // return ResponseEntity.ok("결제 승인 성공!");
    }

    /**
     * 이미 완료된 주문 결제에 대해 취소 사유를 받아 환불 및 결제 취소를 처리하는 API
     *
     * @param orderId 취소할 주문 번호
     * @param request 결제 취소 사유를 담은 객체
     * @return 결제 취소 완료 메시지
     * @author 허준형
     * @DateOfCreated 2026-01-15
     * @DateOfEdit 2026-01-15
     */
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<String> cancelPayment(
            @PathVariable String orderId,
            @RequestBody PaymentCancelRequest request) {

        paymentService.cancelPayment(orderId, request.cancelReason());
        return ResponseEntity.ok("결제가 취소되었습니다.");
    }

    /**
     * 카드 인증 후 받은 authKey를 이용하여 정기 결제용 빌링키를 발급받고 멤버십을 활성화하는 API
     *
     * @param authentication 인증된 사용자의 정보
     * @param request        빌링키 발급 승인을 위한 authKey 및 customerKey 정보
     * @return 멤버십 정기 결제 등록 완료 메시지
     * @author 허준형
     * @DateOfCreated 2026-01-15
     * @DateOfEdit 2026-01-15
     */
    @PostMapping("/billing/confirm")
    public ResponseEntity<String> confirmBilling(
            Authentication authentication,
            @RequestBody BillingConfirmRequest request) {

        Long userId = getUserIdFromAuthentication(authentication);

        // 빌링키 발급 및 멤버십 활성화 로직 실행
        paymentService.confirmBilling(userId, request.getAuthKey(), request.getCustomerKey());

        return ResponseEntity.ok("멤버십 정기 결제가 성공적으로 등록되었습니다.");

        // 테스트용, 위 주석으로 교체 필요
        // Long userId;
        // if (authentication == null) {
        // userId = 2L;
        // } else {
        // userId = Long.parseLong(authentication.getName());
        // }
        //
        // // 빌링키 발급 및 멤버십 활성화 로직 실행
        // paymentService.confirmBilling(userId, request.getAuthKey(),
        // request.getCustomerKey());
        //
        // return ResponseEntity.ok("멤버십 정기 결제가 성공적으로 등록되었습니다.");
    }

    /**
     * 사용자의 멤버십을 즉시 종료하지 않고 다음 결제일에 갱신되지 않도록 해지 예약을 설정하는 API
     *
     * @param authentication 인증된 사용자의 정보
     * @return 멤버십 해지 예약 확인 메시지
     * @author 허준형
     * @DateOfCreated 2026-01-15
     * @DateOfEdit 2026-01-15
     */
    @PostMapping("/membership/terminate")
    public ResponseEntity<String> terminateMembership(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        membershipService.reserveTermination(userId);
        return ResponseEntity.ok("다음 결제일부터 멤버십이 갱신되지 않습니다. 이번 달 혜택은 유지됩니다.");

        // 테스트용, 위 주석으로 교체 필요
        // Long userId;
        // if (authentication == null) {
        // userId = 2L;
        // } else {
        // userId = Long.parseLong(authentication.getName());
        // }
        //
        // membershipService.reserveTermination(userId);
        // return ResponseEntity.ok("다음 결제일부터 멤버십이 갱신되지 않습니다. 이번 달 혜택은 유지됩니다.");
    }

    /**
     * 현재 로그인한 사용자의 멤버십 상태(활성, 해지 예약 등) 및 만료 예정일을 조회하는 API
     *
     * @param authentication 인증된 사용자의 정보
     * @return 사용자의 멤버십 상세 정보 응답 DTO
     * @author 허준형
     * @DateOfCreated 2026-01-15
     * @DateOfEdit 2026-01-15
     */
    @GetMapping("/membership/me")
    public ResponseEntity<MembershipResponse> getMyMembership(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);

        // 멤버십 정보를 조회 (없으면 null 혹은 404 처리)
        Membership membership = membershipRepository.findByUser_Id(userId)
                .orElseThrow(() -> new IllegalArgumentException("가입된 멤버십 정보가 없습니다."));

        return ResponseEntity.ok(MembershipResponse.from(membership));
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("인증 정보가 없습니다.");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof User) {
            return ((User) principal).getId();
        } else if (principal instanceof Seller) {
            return ((Seller) principal).getId();
        } else {
            throw new IllegalStateException("지원하지 않는 사용자 타입입니다.");
        }
    }
}
