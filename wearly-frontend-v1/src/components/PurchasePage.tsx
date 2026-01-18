import { useState, useEffect, useMemo } from 'react';
import { useNavigate, useSearchParams } from 'react-router';
import { apiFetch } from '../api/http';

interface OrderItem {
    productId: number;
    productName: string;
    quantity: number;
    price: number;
    size: string;
    imageUrl: string;
}

interface AvailableCoupon {
    userCouponId: number;
    couponName: string;
    discountValue: number;
    couponType: 'DISCOUNT_AMOUNT' | 'DISCOUNT_RATE';
}

interface OrderSheetResponse {
    items: OrderItem[];
    totalProductPrice: number;
    availableCoupons: AvailableCoupon[];
    deliveryFee: number;
}

interface CreateOrderResponse {
    orderId: string;
    orderNumber: string;
}

export default function PurchasePage() {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();

    const [address, setAddress] = useState('');
    const [detailedAddress, setDetailedAddress] = useState('');
    const [zipCode, setZipCode] = useState('');
    const [selectedCouponId, setSelectedCouponId] = useState<number | null>(null);
    const [orderSheet, setOrderSheet] = useState<OrderSheetResponse | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [processingPayment, setProcessingPayment] = useState(false);

    // URL 파라미터에서 상품 정보 읽기
    const productId = searchParams.get('productId');
    const quantity = searchParams.get('quantity');
    const size = searchParams.get('size');
    
    // cartItemIds를 useMemo로 메모이제이션하여 무한루프 방지
    const cartItemIds = useMemo(() => {
        const ids = searchParams.get('cartItemIds');
        return ids ? ids.split(',').map(Number).filter(Boolean) : undefined;
    }, [searchParams]);

    // 주문 시트 데이터 로드
    useEffect(() => {
        const fetchOrderSheet = async () => {
            try {
                setLoading(true);
                const params = new URLSearchParams();

                if (cartItemIds && cartItemIds.length > 0) {
                    cartItemIds.forEach(id => params.append('cartItemIds', id.toString()));
                } else if (productId) {
                    params.append('productId', productId);
                    if (quantity) params.append('quantity', quantity);
                    if (size) params.append('size', size);
                }

                const data = await apiFetch<OrderSheetResponse>(`/api/users/orders/sheet?${params.toString()}`);
                setOrderSheet(data);
            } catch (err: any) {
                setError(err.message || '알 수 없는 오류가 발생했습니다.');
                console.error('주문 시트 로드 실패:', err);
            } finally {
                setLoading(false);
            }
        };

        if (productId || (cartItemIds && cartItemIds.length > 0)) {
            fetchOrderSheet();
        } else {
            setError('주문 정보가 없습니다.');
            setLoading(false);
        }
    }, [productId, quantity, size, cartItemIds]);

    // 할인 금액 계산
    const calculateDiscount = () => {
        if (!orderSheet || !selectedCouponId) return 0;

        const coupon = orderSheet.availableCoupons.find(c => c.userCouponId === selectedCouponId);
        if (!coupon) return 0;

        if (coupon.couponType === 'DISCOUNT_AMOUNT') {
            return coupon.discountValue;
        } else if (coupon.couponType === 'DISCOUNT_RATE') {
            return Math.floor((orderSheet.totalProductPrice * coupon.discountValue) / 100);
        }
        return 0;
    };

    const appliedDiscount = calculateDiscount();
    const productTotal = orderSheet?.totalProductPrice || 0;
    const deliveryFee = orderSheet?.deliveryFee || 0;
    const finalAmount = productTotal - appliedDiscount + deliveryFee;

    // 결제 처리 (수정된 핵심 로직)
    const handlePayment = async () => {
        if (!address || !detailedAddress || !zipCode) {
            alert('배송지 정보를 모두 입력해주세요.');
            return;
        }

        if (!orderSheet) {
            alert('주문 정보를 불러오는 중입니다. 잠시 후 다시 시도해주세요.');
            return;
        }

        try {
            setProcessingPayment(true);

            // 1. 백엔드 주문 생성 (BEFORE_PAID 상태)
            const orderRequest: any = {
                totalPrice: finalAmount,
                address: address,
                detailAddress: detailedAddress,
                zipCode: zipCode ? Number(zipCode) : null,
            };

            // userCouponId는 선택된 경우에만 추가
            if (selectedCouponId !== null && selectedCouponId !== undefined) {
                orderRequest.userCouponId = selectedCouponId;
            }

            // cartItemIds가 있으면 장바구니 구매, 없으면 단일 상품 구매
            if (cartItemIds && cartItemIds.length > 0) {
                // 장바구니 구매: cartItemIds만 포함 (다른 필드는 포함하지 않음)
                orderRequest.cartItemIds = cartItemIds;
            } else if (productId) {
                // 단일 상품 구매: productId, quantity, size만 포함
                orderRequest.productId = Number(productId);
                orderRequest.quantity = quantity ? Number(quantity) : null;
                
                // size를 백엔드 enum 형식으로 변환
                if (size) {
                    const sizeMap: Record<string, string> = {
                        "S": "SMALL",
                        "M": "MEDIUM",
                        "L": "LARGE",
                        "XL": "EXTRA_LARGE"
                    };
                    orderRequest.size = sizeMap[size.toUpperCase()] || size.toUpperCase();
                }
            }

            // 디버깅: 실제 전송되는 요청 본문 확인
            console.log('주문 요청 데이터:', JSON.stringify(orderRequest, null, 2));
            console.log('cartItemIds:', cartItemIds);

            // 디버깅: 실제 전송되는 요청 본문 확인
            console.log('주문 요청 데이터:', JSON.stringify(orderRequest, null, 2));
            console.log('cartItemIds:', cartItemIds);

            // 직접 fetch로 호출하여 에러 응답 상세 확인
            const token = localStorage.getItem("accessToken");
            const response = await fetch(`/api/users/orders`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
                },
                body: JSON.stringify(orderRequest),
            });

            if (!response.ok) {
                const errorText = await response.text();
                let errorData;
                try {
                    errorData = JSON.parse(errorText);
                } catch {
                    errorData = { message: errorText };
                }
                
                console.error('백엔드 에러 응답:', {
                    status: response.status,
                    statusText: response.statusText,
                    body: errorData
                });
                
                const errorMessage = errorData?.message || errorData?.error || `서버 오류 (${response.status})`;
                alert(errorMessage);
                throw new Error(errorMessage);
            }

            const createdOrder = await response.json() as CreateOrderResponse;
            const orderId = createdOrder.orderId;

            // 2. 토스 페이먼츠 결제창 호출
            // @ts-ignore: index.html에 로드된 TossPayments SDK를 사용합니다.
            const tossPayments = window.TossPayments("test_ck_Poxy1XQL8RJ011jA1yj987nO5Wml");

            await tossPayments.requestPayment('CARD', {
                amount: finalAmount,
                orderId: orderId,
                orderName: orderSheet.items[0].productName + (orderSheet.items.length > 1 ? ` 외 ${orderSheet.items.length - 1}건` : ''),
                successUrl: `http://localhost:70/payment/success`,
                failUrl: `http://localhost:70/payment/fail`,
                method: 'CARD',
            });

        } catch (err: any) {
            alert(err.message || '결제 처리 중 오류가 발생했습니다.');
            console.error('결제 처리 실패:', err);
        } finally {
            setProcessingPayment(false);
        }
    };

    if (loading) {
        return (
            <div className="max-w-[800px] mx-auto px-8 py-12 text-center">주문 정보를 불러오는 중...</div>
        );
    }

    if (error || !orderSheet) {
        return (
            <div className="max-w-[800px] mx-auto px-8 py-12 text-center text-red-600">
                {error || '주문 정보를 불러올 수 없습니다.'}
            </div>
        );
    }

    return (
        <div className="max-w-[800px] mx-auto px-8 py-12">
            <h1 className="text-3xl font-semibold text-gray-900 mb-12">주문 / 결제</h1>

            {/* 주문 상품 목록 */}
            <div className="mb-12">
                <h2 className="text-xl font-semibold text-gray-900 mb-6">주문 상품</h2>
                <div className="space-y-4">
                    {orderSheet.items.map((item, index) => (
                        <div key={index} className="flex gap-4 p-4 border border-gray-200 rounded-lg">
                            <img src={item.imageUrl || '/placeholder-image.png'} alt={item.productName} className="w-24 h-24 object-cover rounded" />
                            <div className="flex-1">
                                <h3 className="font-medium text-gray-900">{item.productName}</h3>
                                <p className="text-sm text-gray-600">사이즈: {item.size} / 수량: {item.quantity}</p>
                                <p className="text-lg font-semibold text-gray-900 mt-2">
                                    {(item.price * item.quantity).toLocaleString()}원
                                </p>
                            </div>
                        </div>
                    ))}
                </div>
            </div>

            {/* 배송지 정보 */}
            <div className="mb-12">
                <h2 className="text-xl font-semibold text-gray-900 mb-6">배송지 정보</h2>
                <div className="space-y-4">
                    <input
                        type="text"
                        value={address}
                        onChange={(e) => setAddress(e.target.value)}
                        className="w-full px-4 py-3 border border-gray-300 rounded-lg"
                        placeholder="주소"
                    />
                    <input
                        type="text"
                        value={detailedAddress}
                        onChange={(e) => setDetailedAddress(e.target.value)}
                        className="w-full px-4 py-3 border border-gray-300 rounded-lg"
                        placeholder="상세 주소"
                    />
                    <input
                        type="text"
                        value={zipCode}
                        onChange={(e) => setZipCode(e.target.value)}
                        className="w-full px-4 py-3 border border-gray-300 rounded-lg"
                        placeholder="우편번호"
                    />
                </div>
            </div>

            {/* 쿠폰 선택 */}
            <div className="mb-12">
                <h2 className="text-xl font-semibold text-gray-900 mb-6">쿠폰 적용</h2>
                <select
                    value={selectedCouponId || ''}
                    onChange={(e) => setSelectedCouponId(e.target.value ? Number(e.target.value) : null)}
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg"
                >
                    <option value="">사용 가능한 쿠폰을 선택하세요</option>
                    {orderSheet.availableCoupons.map((coupon) => (
                        <option key={coupon.userCouponId} value={coupon.userCouponId}>
                            {coupon.couponName} ({coupon.discountValue.toLocaleString()}{coupon.couponType === 'DISCOUNT_RATE' ? '%' : '원'} 할인)
                        </option>
                    ))}
                </select>
            </div>

            {/* 최종 결제 금액 */}
            <div className="mb-12 bg-gray-50 p-6 rounded-lg border border-gray-200">
                <div className="flex justify-between mb-2">
                    <span>상품 금액</span>
                    <span>{productTotal.toLocaleString()}원</span>
                </div>
                <div className="flex justify-between mb-2 text-red-600">
                    <span>할인 금액</span>
                    <span>-{appliedDiscount.toLocaleString()}원</span>
                </div>
                <div className="flex justify-between mb-2">
                    <span>배송비</span>
                    <span>{deliveryFee.toLocaleString()}원</span>
                </div>
                <div className="flex justify-between mt-4 pt-4 border-t border-gray-300 font-bold text-xl">
                    <span>최종 결제 금액</span>
                    <span>{finalAmount.toLocaleString()}원</span>
                </div>
            </div>

            <button
                onClick={handlePayment}
                disabled={processingPayment}
                className="w-full py-4 bg-gray-900 text-white text-lg font-semibold rounded-lg hover:bg-gray-800 disabled:bg-gray-400"
            >
                {processingPayment ? '처리 중...' : `${finalAmount.toLocaleString()}원 결제하기`}
            </button>
        </div>
    );
}