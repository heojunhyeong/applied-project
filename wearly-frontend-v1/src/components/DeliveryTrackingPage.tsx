import { useParams, Link } from 'react-router';
import { Package, CheckCircle, ArrowLeft, Clock, Truck, XCircle } from 'lucide-react';
import { useState, useEffect } from 'react';
import { apiFetch } from '../api/http';

interface OrderDetailResponse {
    orderId: string;
    orderDate: string;
    totalPrice: number;
    orderStatus: string;
    address: string;
    detailAddress: string;
    zipCode: number;
    orderItems: Array<{
        productId: number;
        productName: string;
        brand: string;
        quantity: number;
        price: number;
        imageUrl: string;
        size: string;
        reviewId: number | null;
    }>;
}

export default function DeliveryTrackingPage() {
    const { orderNumber } = useParams<{ orderNumber: string }>();
    const [orderDetail, setOrderDetail] = useState<OrderDetailResponse | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchOrderDetail = async () => {
            if (!orderNumber) return;

            try {
                setLoading(true);
                setError(null);
                const data = await apiFetch<OrderDetailResponse>(`/api/users/orders/${orderNumber}`);
                setOrderDetail(data);
            } catch (err: any) {
                console.error('Failed to fetch order detail:', err);
                setError(err.message || '주문 정보를 불러오는데 실패했습니다.');
            } finally {
                setLoading(false);
            }
        };

        fetchOrderDetail();
    }, [orderNumber]);

    // 주문 상태를 한글로 변환
    const getStatusText = (status: string) => {
        const statusMap: Record<string, string> = {
            'BEFORE_PAID': '결제 대기',
            'PAID': '결제 완료',
            'WAIT_CHECK': '검수 대기',
            'CHECK': '검수 완료',
            'IN_DELIVERY': '배송 중',
            'DELIVERY_COMPLETED': '배송 완료',
            'PURCHASE_CONFIRMED': '구매 확정',
            'CANCELLED': '주문 취소',
            'RETURN_REQUESTED': '반품 요청',
            'RETURN_COMPLETED': '반품 완료',
        };
        return statusMap[status] || status;
    };

    // 주문 상태에 따른 배송 상태 아이콘 및 색상
    const getStatusConfig = (status: string) => {
        switch (status) {
            case 'DELIVERY_COMPLETED':
            case 'PURCHASE_CONFIRMED':
                return {
                    icon: CheckCircle,
                    bgColor: 'bg-green-50',
                    borderColor: 'border-green-200',
                    textColor: 'text-green-900',
                    iconColor: 'text-green-600',
                    statusText: '배송 완료',
                };
            case 'IN_DELIVERY':
                return {
                    icon: Truck,
                    bgColor: 'bg-blue-50',
                    borderColor: 'border-blue-200',
                    textColor: 'text-blue-900',
                    iconColor: 'text-blue-600',
                    statusText: '배송 중',
                };
            case 'CANCELLED':
            case 'RETURN_REQUESTED':
            case 'RETURN_COMPLETED':
                return {
                    icon: XCircle,
                    bgColor: 'bg-red-50',
                    borderColor: 'border-red-200',
                    textColor: 'text-red-900',
                    iconColor: 'text-red-600',
                    statusText: getStatusText(status),
                };
            default:
                return {
                    icon: Clock,
                    bgColor: 'bg-gray-50',
                    borderColor: 'border-gray-200',
                    textColor: 'text-gray-900',
                    iconColor: 'text-gray-600',
                    statusText: getStatusText(status),
                };
        }
    };

    // 날짜 포맷팅
    const formatDate = (dateString: string) => {
        const date = new Date(dateString);
        return date.toLocaleDateString('ko-KR', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit',
        });
    };

    if (loading) {
        return (
            <div className="min-h-screen bg-gray-50 flex items-center justify-center">
                <div className="text-xl text-gray-600">배송 정보를 불러오는 중...</div>
            </div>
        );
    }

    if (error || !orderDetail) {
        return (
            <div className="min-h-screen bg-gray-50 flex items-center justify-center">
                <div className="bg-white border border-gray-200 rounded-lg p-12 text-center max-w-md">
                    <p className="text-gray-500 mb-4">
                        {error || '배송 정보를 찾을 수 없습니다.'}
                    </p>
                    <Link
                        to="/orders"
                        className="inline-flex items-center gap-2 text-sm text-gray-900 hover:underline"
                    >
                        <ArrowLeft className="w-4 h-4" />
                        주문 내역으로 돌아가기
                    </Link>
                </div>
            </div>
        );
    }

    const statusConfig = getStatusConfig(orderDetail.orderStatus);
    const StatusIcon = statusConfig.icon;

    return (
        <div className="min-h-screen bg-gray-50">
            <div className="max-w-3xl mx-auto px-6 py-8">
                {/* Back Button */}
                <Link
                    to="/orders"
                    className="inline-flex items-center gap-2 text-sm text-gray-600 hover:text-gray-900 mb-6 transition-colors"
                >
                    <ArrowLeft className="w-4 h-4" />
                    주문 내역으로 돌아가기
                </Link>

                {/* Page Title */}
                <div className="mb-6">
                    <h1 className="text-2xl text-gray-900">배송 추적</h1>
                    <p className="text-sm text-gray-600 mt-2">주문번호: {orderDetail.orderId}</p>
                </div>

                {/* Delivery Status Banner */}
                <div className={`${statusConfig.bgColor} border ${statusConfig.borderColor} rounded-lg p-6 mb-6`}>
                    <div className="flex items-start gap-4">
                        <StatusIcon className={`w-6 h-6 ${statusConfig.iconColor} flex-shrink-0 mt-0.5`} />
                        <div>
                            <h2 className={`text-lg ${statusConfig.textColor} mb-1`}>
                                {statusConfig.statusText}
                            </h2>
                            <p className={`text-sm ${statusConfig.textColor} opacity-80`}>
                                주문일: {formatDate(orderDetail.orderDate)}
                            </p>
                        </div>
                    </div>
                </div>

                {/* Order Items */}
                <div className="bg-white border border-gray-200 rounded-lg p-6 mb-4">
                    <h2 className="text-lg text-gray-900 mb-4">주문 상품</h2>
                    <div className="space-y-4">
                        {orderDetail.orderItems.map((item, index) => (
                            <div key={`${item.productId}-${index}`} className="pb-4 border-b border-gray-100 last:border-0">
                                <div className="flex gap-4 mb-3">
                                    <img
                                        src={item.imageUrl || 'https://via.placeholder.com/400'}
                                        alt={item.productName}
                                        className="w-20 h-20 object-cover rounded-md border border-gray-200"
                                    />
                                    <div className="flex-1">
                                    </div>
                                </div>
                                <div className="ml-24 text-sm text-gray-600 space-y-1">
                                    <div className="flex items-center gap-4">
                                        <span className="font-medium text-gray-900">{item.brand}</span>
                                        <span className="text-gray-300">|</span>
                                        <span>수량: {item.quantity}개</span>
                                        {item.size && (
                                            <>
                                                <span className="text-gray-300">|</span>
                                                <span>사이즈: {item.size}</span>
                                            </>
                                        )}
                                    </div>
                                    <div>
                                        <span className="font-medium text-gray-900">₩{item.price.toLocaleString()}</span>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                    <div className="mt-4 pt-4 border-t border-gray-200">
                        <div className="flex justify-between items-center">
                            <span className="text-sm font-medium text-gray-900">총 주문 금액</span>
                            <span className="text-lg font-bold text-gray-900">
                                ₩{orderDetail.totalPrice.toLocaleString()}
                            </span>
                        </div>
                    </div>
                </div>

                {/* Delivery Information */}
                <div className="bg-white border border-gray-200 rounded-lg p-6 mb-4">
                    <div className="flex items-center gap-3 mb-6">
                        <Package className="w-5 h-5 text-gray-700" />
                        <h2 className="text-lg text-gray-900">배송 정보</h2>
                    </div>

                    <div className="space-y-4">
                        {/* Order Status */}
                        <div className="flex py-3 border-b border-gray-100">
                            <span className="text-sm text-gray-600 w-36">주문 상태</span>
                            <span className={`inline-flex items-center px-3 py-1 rounded-full text-sm ${
                                orderDetail.orderStatus === 'DELIVERY_COMPLETED' || orderDetail.orderStatus === 'PURCHASE_CONFIRMED'
                                    ? 'bg-green-50 text-green-700 border border-green-200'
                                    : orderDetail.orderStatus === 'IN_DELIVERY'
                                    ? 'bg-blue-50 text-blue-700 border border-blue-200'
                                    : orderDetail.orderStatus === 'CANCELLED' || orderDetail.orderStatus === 'RETURN_REQUESTED' || orderDetail.orderStatus === 'RETURN_COMPLETED'
                                    ? 'bg-red-50 text-red-700 border border-red-200'
                                    : 'bg-gray-50 text-gray-700 border border-gray-200'
                            }`}>
                                {getStatusText(orderDetail.orderStatus)}
                            </span>
                        </div>

                        {/* Delivery Address */}
                        <div className="flex py-3 border-b border-gray-100">
                            <span className="text-sm text-gray-600 w-36">배송지</span>
                            <div className="flex-1">
                                <p className="text-sm text-gray-900">
                                    ({orderDetail.zipCode}) {orderDetail.address} {orderDetail.detailAddress}
                                </p>
                            </div>
                        </div>

                        {/* Carrier and Tracking Number - 나중에 백엔드에서 추가될 수 있도록 */}
                        {/* 현재는 백엔드 API에 포함되지 않음 */}
                    </div>
                </div>
            </div>
        </div>
    );
}