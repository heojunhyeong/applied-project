import { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router';
import { CheckCircle, XCircle, CreditCard, Calendar, AlertCircle } from 'lucide-react';

const API_BASE_URL = ''; // TODO: 환경변수로 관리
const TOSS_CLIENT_KEY = 'test_ck_Poxy1XQL8RJ011jA1yj987nO5Wml'; // TODO: 환경변수로 관리

interface MembershipResponse {
    status: 'ACTIVE' | 'CANCELLATION_RESERVED' | 'EXPIRED';
    nextPaymentDate: string;
}

export default function MembershipPage() {
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();
    const [membership, setMembership] = useState<MembershipResponse | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [processing, setProcessing] = useState(false);
    const [tossPaymentsReady, setTossPaymentsReady] = useState(false);

    // URL 파라미터에서 authKey와 customerKey 읽기 (성공 페이지에서 리다이렉트된 경우)
    const authKey = searchParams.get('authKey');
    const customerKey = searchParams.get('customerKey');

    // 멤버십 정보 로드
    useEffect(() => {
        const loadMembership = async () => {
            try {
                setLoading(true);
                const token = localStorage.getItem('accessToken');

                if (!token) {
                    throw new Error('로그인이 필요합니다.');
                }

                const response = await fetch(`${API_BASE_URL}/api/payment/membership/me`, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`,
                    },
                    credentials: 'include',
                });

                if (response.status === 404 || !response.ok) {
                    // 멤버십이 없는 경우 (정상)
                    setMembership(null);
                    return;
                }

                const data: MembershipResponse = await response.json();
                setMembership(data);
            } catch (err) {
                if (err instanceof Error && err.message === '로그인이 필요합니다.') {
                    setError(err.message);
                } else {
                    // 멤버십이 없을 수 있으므로 에러로 처리하지 않음
                    setMembership(null);
                }
            } finally {
                setLoading(false);
            }
        };

        loadMembership();
    }, []);

    // 토스 페이먼츠 스크립트 로드 확인 - 이 useEffect 추가
    useEffect(() => {
        const checkTossPayments = () => {
            if (typeof window !== 'undefined' && (window as any).TossPayments) {
                setTossPaymentsReady(true);
            } else {
                // 스크립트가 아직 로드되지 않았다면 잠시 후 다시 확인
                setTimeout(checkTossPayments, 100);
            }
        };

        checkTossPayments();
    }, []);

    // 빌링 인증 성공 후 처리 (URL 파라미터로 받은 경우)
    useEffect(() => {
        console.log('URL 파라미터 확인:', { authKey, customerKey, loading, membership });
        if (authKey && customerKey && !loading && !membership) {
            handleBillingConfirm(authKey, customerKey);
        }
    }, [authKey, customerKey, loading, membership]);

    // 빌링 인증 요청 (토스 페이먼츠 위젯 호출)
    const handleBillingAuth = async () => {
        const token = localStorage.getItem('accessToken');

        if (!token) {
            alert('로그인이 필요한 서비스입니다.');
            navigate('/login');
            return;
        }

        if (!tossPaymentsReady) {
            alert('토스 페이먼츠를 준비하는 중입니다. 잠시 후 다시 시도해주세요.');
            return;
        }

        try {
            setProcessing(true);

            // 사용자 ID 가져오기
            const userId = localStorage.getItem('userId') || Date.now().toString();
            const customerKeyValue = `USER_${userId}`;

            const tossPayments = (window as any).TossPayments(TOSS_CLIENT_KEY);

            // successUrl과 failUrl 설정 - window.location.origin을 사용하여 동적으로 생성
            // 토스 페이먼츠는 성공 시 authKey를 쿼리 파라미터로 자동 추가함
            // 형식: successUrl?authKey=xxx&customerKey=xxx
            const baseUrl = window.location.origin;
            const successUrl = `${baseUrl}/membership?customerKey=${encodeURIComponent(customerKeyValue)}`;
            const failUrl = `${baseUrl}/membership?status=fail`;

            console.log('빌링 인증 요청:', {
                customerKey: customerKeyValue,
                successUrl,
                failUrl,
                baseUrl: baseUrl,
                windowOrigin: window.location.origin
            });

            await tossPayments.requestBillingAuth('TosePay', {
                customerKey: customerKeyValue,
                successUrl: successUrl,
                failUrl: failUrl,
            });
        } catch (error: any) {
            // 사용자 취소는 정상적인 동작이므로 별도 처리
            if (error.code === 'USER_CANCEL') {
                // 사용자가 취소한 경우 알림 없이 처리 종료
                console.log('사용자가 빌링 인증을 취소했습니다.');
            } else if (error.code === 'INVALID_SUCCESS_URL') {
                // successUrl 형식 오류인 경우
                alert('결제 인증 URL 설정에 문제가 있습니다. 관리자에게 문의해주세요.');
                console.error('successUrl 형식 오류:', error);
            } else if (error.code === 'INVALID_CARD_NUMBER' || error.code === 'INVALID_CARD_INFO') {
                // 카드번호 입력 오류는 토스 페이먼츠 위젯에서 처리되므로 여기서는 무시
                console.log('카드 정보 입력 오류 (위젯에서 처리됨):', error);
            } else {
                // 기타 오류만 알림 표시
                const errorMessage = error.message || '알 수 없는 오류';
                // 입력 검증 관련 오류는 사용자에게 표시하지 않음
                if (!errorMessage.includes('카드') && !errorMessage.includes('주민등록') && !errorMessage.includes('입력')) {
                    alert(`빌링 인증 요청에 실패했습니다: ${errorMessage}`);
                }
                console.error('빌링 인증 에러:', error);
                console.error('에러 상세:', {
                    code: error.code,
                    message: error.message,
                    successUrl: `${window.location.origin}/membership?customerKey=...`
                });
            }
            setProcessing(false);
        }
    };

    // 빌링키 발급 및 멤버십 활성화
    const handleBillingConfirm = async (authKey: string, customerKey: string) => {
        try {
            setProcessing(true);
            const token = localStorage.getItem('accessToken');

            // authKey와 customerKey 확인
            if (!authKey || !customerKey) {
                throw new Error('인증 정보가 누락되었습니다. 다시 시도해주세요.');
            }

            console.log('빌링 확인 요청:', {
                authKey,
                customerKey,
                apiUrl: `${API_BASE_URL}/api/payment/billing/confirm`
            });

            const response = await fetch(`${API_BASE_URL}/api/payment/billing/confirm`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`,
                },
                credentials: 'include',
                body: JSON.stringify({
                    authKey,
                    customerKey,
                }),
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => null);
                const errorMessage = errorData?.message || `서버 오류 (${response.status})`;
                throw new Error(errorMessage);
            }

            alert('멤버십 가입이 완료되었습니다!');
            // URL 파라미터 제거하고 새로고침
            window.location.href = '/membership';
        } catch (err) {
            const errorMessage = err instanceof Error
                ? err.message
                : '멤버십 가입 처리 중 오류가 발생했습니다.';
            alert(errorMessage);
            console.error('빌링 확인 실패:', err);
            console.error('에러 상세:', {
                message: err instanceof Error ? err.message : String(err),
                authKey: authKey,
                customerKey: customerKey,
                apiUrl: `${API_BASE_URL}/api/payment/billing/confirm`
            });
        } finally {
            setProcessing(false);
        }
    };

    // 멤버십 해지 예약
    const handleTerminate = async () => {
        if (!confirm('정말 멤버십을 해지하시겠습니까? 다음 결제일부터 멤버십이 갱신되지 않습니다.')) {
            return;
        }

        try {
            setProcessing(true);
            const token = localStorage.getItem('accessToken');

            const response = await fetch(`${API_BASE_URL}/api/payment/membership/terminate`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`,
                },
                credentials: 'include',
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => null);
                throw new Error(errorData?.message || '멤버십 해지 예약에 실패했습니다.');
            }

            alert('멤버십 해지가 예약되었습니다. 다음 결제일부터 멤버십이 갱신되지 않습니다.');
            // 멤버십 정보 다시 로드
            window.location.reload();
        } catch (err) {
            alert(err instanceof Error ? err.message : '멤버십 해지 예약 처리 중 오류가 발생했습니다.');
            console.error('멤버십 해지 실패:', err);
        } finally {
            setProcessing(false);
        }
    };

    // 상태 텍스트 변환
    const getStatusText = (status: string) => {
        switch (status) {
            case 'ACTIVE':
                return '활성';
            case 'CANCELLATION_RESERVED':
                return '해지 예약됨';
            case 'EXPIRED':
                return '만료됨';
            default:
                return status;
        }
    };

    // 날짜 포맷팅
    const formatDate = (dateString: string) => {
        const date = new Date(dateString);
        return date.toLocaleDateString('ko-KR', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
        });
    };

    if (loading) {
        return (
            <div className="min-h-[calc(100vh-80px)] bg-gray-50 py-12 px-4">
                <div className="max-w-2xl mx-auto">
                    <div className="text-center">로딩 중...</div>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="min-h-[calc(100vh-80px)] bg-gray-50 py-12 px-4">
                <div className="max-w-2xl mx-auto">
                    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-8">
                        <div className="text-center text-red-600">{error}</div>
                        <div className="text-center mt-4">
                            <button
                                onClick={() => navigate('/login')}
                                className="px-6 py-2 bg-gray-900 text-white rounded-lg hover:bg-gray-800"
                            >
                                로그인하기
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-[calc(100vh-80px)] bg-gray-50 py-12 px-4">
            <div className="max-w-2xl mx-auto">
                <h1 className="text-3xl font-semibold text-gray-900 mb-8 text-center">
                    웨어리 프리미엄 멤버십
                </h1>

                {membership ? (
                    // 멤버십 가입된 경우
                    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-8">
                        <div className="flex items-center justify-between mb-6">
                            <div className="flex items-center gap-3">
                                {membership.status === 'ACTIVE' ? (
                                    <CheckCircle className="w-8 h-8 text-green-600" />
                                ) : (
                                    <AlertCircle className="w-8 h-8 text-orange-600" />
                                )}
                                <div>
                                    <h2 className="text-xl font-semibold text-gray-900">
                                        멤버십 상태: {getStatusText(membership.status)}
                                    </h2>
                                </div>
                            </div>
                        </div>

                        <div className="space-y-4 mb-6">
                            <div className="flex items-center gap-3 p-4 bg-gray-50 rounded-lg">
                                <Calendar className="w-5 h-5 text-gray-600" />
                                <div>
                                    <p className="text-sm text-gray-600">다음 결제일</p>
                                    <p className="text-lg font-semibold text-gray-900">
                                        {formatDate(membership.nextPaymentDate)}
                                    </p>
                                </div>
                            </div>

                            <div className="p-4 bg-blue-50 border border-blue-200 rounded-lg">
                                <h3 className="font-semibold text-gray-900 mb-2">멤버십 혜택</h3>
                                <ul className="space-y-1 text-sm text-gray-700">
                                    <li>• 월 4,900원으로 프리미엄 혜택 이용</li>
                                    <li>• 전 상품 무료배송</li>
                                    <li>• 멤버십 전용 쿠폰 월 2장 제공</li>
                                </ul>
                            </div>
                        </div>

                        {membership.status === 'ACTIVE' && (
                            <button
                                onClick={handleTerminate}
                                disabled={processing}
                                className="w-full py-3 bg-red-50 text-red-700 border border-red-300 rounded-lg hover:bg-red-100 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                            >
                                {processing ? '처리 중...' : '멤버십 해지 예약'}
                            </button>
                        )}

                        {membership.status === 'CANCELLATION_RESERVED' && (
                            <div className="p-4 bg-orange-50 border border-orange-200 rounded-lg">
                                <p className="text-sm text-orange-700">
                                    멤버십 해지가 예약되었습니다. 다음 결제일({formatDate(membership.nextPaymentDate)})까지 혜택을 이용하실 수 있습니다.
                                </p>
                            </div>
                        )}
                    </div>
                ) : (
                    // 멤버십 미가입된 경우
                    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-8">
                        <div className="text-center mb-8">
                            <CreditCard className="w-16 h-16 text-gray-400 mx-auto mb-4" />
                            <h2 className="text-2xl font-semibold text-gray-900 mb-2">
                                프리미엄 멤버십에 가입하세요
                            </h2>
                            <p className="text-gray-600">
                                월 4,900원으로 다양한 혜택을 누려보세요.
                            </p>
                        </div>

                        <div className="space-y-4 mb-8">
                            <div className="p-4 bg-gray-50 rounded-lg">
                                <h3 className="font-semibold text-gray-900 mb-3">멤버십 혜택</h3>
                                <ul className="space-y-2 text-sm text-gray-700">
                                    <li className="flex items-start gap-2">
                                        <CheckCircle className="w-4 h-4 text-green-600 mt-0.5 flex-shrink-0" />
                                        <span>전 상품 무료배송</span>
                                    </li>
                                    <li className="flex items-start gap-2">
                                        <CheckCircle className="w-4 h-4 text-green-600 mt-0.5 flex-shrink-0" />
                                        <span>멤버십 전용 쿠폰 월 2장 제공</span>
                                    </li>
                                </ul>
                            </div>

                            <div className="p-4 bg-blue-50 border border-blue-200 rounded-lg">
                                <p className="text-sm text-gray-700">
                                    <strong>월 4,900원</strong> | 매월 자동 결제 (취소 가능)
                                </p>
                            </div>
                        </div>

                        <button
                            onClick={handleBillingAuth}
                            disabled={processing}
                            className="w-full py-4 bg-gray-900 text-white rounded-lg hover:bg-gray-800 transition-colors disabled:opacity-50 disabled:cursor-not-allowed text-lg font-semibold"
                        >
                            {processing ? '처리 중...' : '멤버십 가입하기'}
                        </button>

                        <p className="text-xs text-gray-500 text-center mt-4">
                            가입 시 정기 결제용 카드 등록이 필요합니다.
                        </p>
                    </div>
                )}
            </div>
        </div>
    );
}

