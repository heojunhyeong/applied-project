const CheckoutPage = () => {
    const location = useLocation();
    const { totalPrice, orderName } = location.state || { totalPrice: 0, orderName: "주문" };

    const handlePayment = async () => {
        // 1. 서버에 주문 생성 요청 (여기서 서버가 가격을 다시 계산하게 하는 것이 베스트)
        const orderResponse = await fetch('http://localhost:8080/api/orders', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                userId: getLoggedInUserId(), // 실제 로그인 유저
                totalPrice: totalPrice       // 화면에 표시된 금액
            })
        });

        const orderData = await orderResponse.json();

        // 2. 토스 결제창 띄우기
        const tossPayments = window.TossPayments('test_ck_...');
        tossPayments.requestPayment('카드', {
            amount: orderData.totalPrice, // 서버가 최종 확인해서 내려준 금액 사용!
            orderId: orderData.orderId,
            orderName: orderName,
            successUrl: window.location.origin + '/payment/success',
            failUrl: window.location.origin + '/payment/fail',
        });
    };

    return (
        <div>
            <h2>{orderName}</h2>
            <p>최종 결제 금액: {totalPrice.toLocaleString()}원</p>
            <button onClick={handlePayment}>결제하기</button>
        </div>
    );
};