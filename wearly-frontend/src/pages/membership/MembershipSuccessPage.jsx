import React, { useEffect } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';

const MembershipSuccessPage = () => {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();

    const authKey = searchParams.get('authKey');
    const customerKey = searchParams.get('customerKey');

    useEffect(() => {
        const confirmBilling = async () => {
            // 1. 저장된 토큰 가져오기
            const token = localStorage.getItem('accessToken');

            try {
                const response = await fetch('http://localhost:8080/api/payment/billing/confirm', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        // 2. 백엔드 Authentication 인증을 위해 토큰 전송
                        'Authorization': `Bearer ${token}`
                    },
                    body: JSON.stringify({
                        authKey: authKey,
                        customerKey: customerKey
                    })
                });

                if (response.ok) {
                    alert("멤버십 가입이 완료되었습니다!");
                    navigate("/profile");
                } else {
                    const errorData = await response.json();
                    alert(`승인 실패: ${errorData.message || '알 수 없는 오류'}`);
                    navigate("/membership");
                }
            } catch (error) {
                console.error("서버 통신 에러:", error);
                alert("서버와 통신 중 오류가 발생했습니다.");
            }
        };

        if (authKey && customerKey) {
            confirmBilling();
        }
    }, [authKey, customerKey, navigate]);

    return (
        <div style={{ textAlign: 'center', marginTop: '100px' }}>
            <h2>결제 승인 중...</h2>
            <p>잠시만 기다려 주세요.</p>
        </div>
    );
};