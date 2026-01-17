import { useEffect, useState } from 'react';
import { useSearchParams, useNavigate, Link } from 'react-router';
import { CheckCircle2, Loader2, AlertCircle } from 'lucide-react';

const API_BASE_URL = '';

export default function PaymentSuccessPage() {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();

    const [isVerifying, setIsVerifying] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const paymentKey = searchParams.get('paymentKey');
    const orderId = searchParams.get('orderId');
    const amount = searchParams.get('amount');

    useEffect(() => {
        const confirmPayment = async () => {
            if (!paymentKey || !orderId || !amount) {
                setError('결제 정보가 누락되었습니다.');
                setIsVerifying(false);
                return;
            }

            try {
                const token = localStorage.getItem('accessToken');
                const response = await fetch(`${API_BASE_URL}/api/payment/toss/confirm`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        ...(token && { 'Authorization': `Bearer ${token}` }),
                    },
                    body: JSON.stringify({
                        paymentKey,
                        orderId,
                        amount: Number(amount),
                    }),
                });

                if (!response.ok) {
                    const errorData = await response.json().catch(() => null);
                    throw new Error(errorData?.message || '결제 승인 중 오류가 발생했습니다.');
                }

                // 승인 성공 시 3초 후 주문 내역으로 이동하거나 유지
                setIsVerifying(false);
            } catch (err) {
                console.error('승인 실패:', err);
                setError(err instanceof Error ? err.message : '결제 승인에 실패했습니다.');
                setIsVerifying(false);
            }
        };

        confirmPayment();
    }, [paymentKey, orderId, amount]);

    if (isVerifying) {
        return (
            <div className="flex flex-col items-center justify-center min-h-[60vh] gap-4">
                <Loader2 className="w-12 h-12 text-gray-900 animate-spin" />
                <h2 className="text-xl font-medium">결제 승인 중입니다...</h2>
                <p className="text-gray-500">잠시만 기다려주세요. 페이지를 새로고침하지 마세요.</p>
            </div>
        );
    }

    if (error) {
        return (
            <div className="flex flex-col items-center justify-center min-h-[60vh] gap-6 px-4 text-center">
                <AlertCircle className="w-16 h-16 text-red-500" />
                <div>
                    <h2 className="text-2xl font-bold text-gray-900 mb-2">결제 승인 실패</h2>
                    <p className="text-gray-600">{error}</p>
                </div>
                <button
                    onClick={() => navigate('/cart')}
                    className="px-8 py-3 bg-gray-900 text-white rounded-lg font-medium hover:bg-gray-800"
                >
                    장바구니로 돌아가기
                </button>
            </div>
        );
    }

    return (
        <div className="flex flex-col items-center justify-center min-h-[60vh] gap-8 px-4 text-center">
            <CheckCircle2 className="w-20 h-20 text-green-500" />
            <div>
                <h1 className="text-3xl font-bold text-gray-900 mb-2">결제가 완료되었습니다!</h1>
                <p className="text-gray-600">주문하신 상품의 배송이 곧 시작됩니다.</p>
            </div>

            <div className="bg-gray-50 p-6 rounded-xl border border-gray-200 w-full max-w-md text-left">
                <div className="flex justify-between mb-2">
                    <span className="text-gray-500">주문번호</span>
                    <span className="font-medium">{orderId}</span>
                </div>
                <div className="flex justify-between">
                    <span className="text-gray-500">결제금액</span>
                    <span className="font-medium text-gray-900">{Number(amount).toLocaleString()}원</span>
                </div>
            </div>

            <div className="flex gap-4">
                <Link
                    to="/orders"
                    className="px-8 py-3 bg-gray-900 text-white rounded-lg font-medium hover:bg-gray-800 transition-colors"
                >
                    주문 내역 보기
                </Link>
                <Link
                    to="/"
                    className="px-8 py-3 bg-white text-gray-900 border border-gray-300 rounded-lg font-medium hover:bg-gray-50 transition-colors"
                >
                    계속 쇼핑하기
                </Link>
            </div>
        </div>
    );
}