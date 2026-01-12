import React, { useEffect, useRef, useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';

const SuccessPage = () => {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const [status, setStatus] = useState("결제 승인 처리 중...");

    // 중복 요청 방지를 위한 플래그
    const isProcessing = useRef(false);

    useEffect(() => {
        // 이미 처리가 시작되었다면 중복 실행하지 않음
        if (isProcessing.current) return;

        const paymentKey = searchParams.get('paymentKey');
        const orderId = searchParams.get('orderId');
        const amount = searchParams.get('amount');

        if (!paymentKey || !orderId || !amount) {
            setStatus("잘못된 접근입니다. 결제 정보가 부족합니다.");
            return;
        }

        // 처리가 시작됨을 표시
        isProcessing.current = true;

        const confirmPayment = async () => {
            try {
                const response = await fetch('http://localhost:8080/api/payment/toss/confirm', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        paymentKey,
                        orderId,
                        amount: Number(amount)
                    })
                });

                if (response.ok) {
                    setStatus("결제가 성공적으로 완료되었습니다!");
                    // 2초 뒤에 메인이나 주문 완료 목록으로 이동
                    setTimeout(() => {
                        navigate('/order/complete');
                    }, 2000);
                } else {
                    const errorText = await response.text();
                    setStatus(`결제 승인 실패: ${errorText}`);
                    console.error("승인 실패 상세:", errorText);
                }
            } catch (error) {
                console.error("네트워크 에러:", error);
                setStatus("서버와 통신 중 에러가 발생했습니다.");
            }
        };

        confirmPayment();
    }, [searchParams, navigate]);

    return (
        <div style={styles.container}>
            <div style={styles.card}>
                <h2 style={styles.title}>{status}</h2>
                <p style={styles.description}>
                    창을 닫지 마세요. 결제 결과를 확인 중입니다.
                </p>
                {/* 로딩 애니메이션 등을 추가하면 좋습니다 */}
            </div>
        </div>
    );
};

// 간단한 스타일링
const styles = {
    container: {
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        height: '100vh',
        backgroundColor: '#f8f9fa'
    },
    card: {
        padding: '40px',
        borderRadius: '12px',
        backgroundColor: '#fff',
        boxShadow: '0 4px 12px rgba(0,0,0,0.1)',
        textAlign: 'center'
    },
    title: {
        color: '#333',
        marginBottom: '10px'
    },
    description: {
        color: '#666'
    }
};

export default SuccessPage;