const MyPage = () => {
    const [membershipStatus, setMembershipStatus] = useState('ACTIVE'); // 실제론 API로 가져옴

    const handleTerminate = async () => {
        if (!window.confirm("정말로 멤버십 해지를 예약하시겠습니까? 다음 결제일부터는 혜택이 사라집니다.")) {
            return;
        }

        const token = localStorage.getItem('accessToken');
        const response = await fetch('http://localhost:8080/api/payment/membership/terminate', {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.ok) {
            alert("해지 예약이 완료되었습니다.");
            setMembershipStatus('CANCEL_RESERVED');
        } else {
            alert("해지 처리에 실패했습니다.");
        }
    };

    return (
        <div>
            <h3>내 멤버십 정보</h3>
            {membershipStatus === 'ACTIVE' ? (
                <div>
                    <p>현재 프리미엄 멤버십 이용 중입니다.</p>
                    <button onClick={handleTerminate} style={{color: 'red'}}>멤버십 해지하기</button>
                </div>
            ) : membershipStatus === 'CANCEL_RESERVED' ? (
                <p>멤버십 해지 예약 상태입니다. 이번 이용 기간 종료 후 일반 회원으로 전환됩니다.</p>
            ) : (
                <p>멤버십 회원이 아닙니다. <a href="/membership">가입하기</a></p>
            )}
        </div>
    );
};