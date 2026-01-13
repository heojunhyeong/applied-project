import React from 'react';

const MembershipPage = () => {

    const handleBillingAuth = async () => {
        // 1. 로컬 스토리지 등에서 토큰과 유저 정보 가져오기
        const token = localStorage.getItem('accessToken');
        const userData = JSON.parse(localStorage.getItem('user')); // 로그인 시 저장했다고 가정

        if (!token || !userData) {
            alert("로그인이 필요한 서비스입니다.");
            window.location.href = "/login";
            return;
        }

        const clientKey = 'test_ck_Poxy1XQL8RJ011jA1yj987nO5Wml';
        const tossPayments = window.TossPayments(clientKey);

        // 2. 고정된 1 대신 실제 유저 ID 사용
        const customerKey = `USER_${userData.id}`;

        try {
            await tossPayments.requestBillingAuth('카드', {
                customerKey: customerKey,
                successUrl: window.location.origin + '/membership/success',
                failUrl: window.location.origin + '/membership/fail',
            });
        } catch (error) {
            console.error("빌링 인증 요청 에러:", error);
        }
    };

    return (
        <div style={{ padding: '50px', textAlign: 'center' }}>
            <h2>웨어리 프리미엄 멤버십</h2>
            <button onClick={handleBillingAuth}>정기 결제 수단 등록하기</button>
        </div>
    );
};