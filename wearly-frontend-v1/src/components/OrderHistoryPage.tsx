import { Link } from 'react-router';
import { Truck, Edit3, Search } from 'lucide-react';
import { useState, useEffect } from 'react';
import { apiFetch } from '../api/http';

// API 응답 타입
interface OrderHistoryResponse {
    orderId: string;
    orderDate: string; // ISO 8601 형식
    totalPrice: number;
    orderStatus: string;
    representativeProductName: string;
    representativeImageUrl: string;
}

interface OrderItemDto {
    productId: number;
    productName: string;
    quantity: number;
    price: number;
    imageUrl: string;
    size: string;
    reviewId: number | null;
}

interface OrderDetailResponse {
    orderId: string;
    orderDate: string;
    totalPrice: number;
    orderStatus: string;
    address: string;
    detailAddress: string;
    zipCode: number;
    orderItems: OrderItemDto[];
}

// 주문 데이터 타입 (UI용)
interface Order {
    orderId: string;
    orderDate: string;
    orderNumber: string;
    items: OrderItemDto[];
    totalPrice: number;
    orderStatus: string;
}

export default function OrderHistoryPage() {
    const [inputValue, setInputValue] = useState('');
    const [searchTerm, setSearchTerm] = useState('');
    const [orders, setOrders] = useState<Order[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    // 전체 주문 내역 로드 함수
    const fetchOrders = async () => {
        try {
            setLoading(true);
            setError(null);

            // 주문 목록 가져오기
            const orderList = await apiFetch<OrderHistoryResponse[]>(`/api/users/orders`);

            // 각 주문의 상세 정보 가져오기
            const orderDetails = await Promise.all(
                orderList.map(async (orderSummary) => {
                    try {
                        const detail = await apiFetch<OrderDetailResponse>(
                            `/api/users/orders/${orderSummary.orderId}`
                        );
                        return {
                            orderId: detail.orderId,
                            orderDate: detail.orderDate,
                            orderNumber: detail.orderId, // orderId를 주문번호로 사용
                            items: detail.orderItems,
                            totalPrice: detail.totalPrice,
                            orderStatus: detail.orderStatus,
                        };
                    } catch (err) {
                        console.error(`Failed to fetch order detail for ${orderSummary.orderId}:`, err);
                        // 상세 정보를 가져오지 못해도 요약 정보로 표시
                        return {
                            orderId: orderSummary.orderId,
                            orderDate: orderSummary.orderDate,
                            orderNumber: orderSummary.orderId,
                            items: [{
                                productId: 0,
                                productName: orderSummary.representativeProductName,
                                quantity: 0,
                                price: orderSummary.totalPrice,
                                imageUrl: orderSummary.representativeImageUrl || '',
                                size: '',
                                reviewId: null,
                            }],
                            totalPrice: orderSummary.totalPrice,
                            orderStatus: orderSummary.orderStatus,
                        };
                    }
                })
            );

            setOrders(orderDetails);
        } catch (err: any) {
            setError(err.message || '주문 내역을 불러오는데 실패했습니다.');
            console.error('Failed to fetch orders:', err);
        } finally {
            setLoading(false);
        }
    };

    // 검색 함수
    const searchOrders = async (keyword: string) => {
        if (!keyword.trim()) {
            // 검색어가 없으면 전체 주문 다시 불러오기
            await fetchOrders();
            return;
        }

        try {
            setLoading(true);
            // 검색 API는 OrderItemDto 배열만 반환
            const searchResults = await apiFetch<OrderItemDto[]>(
                `/api/users/orders/search?keyword=${encodeURIComponent(keyword)}`
            );

            // 검색 결과를 Order 형식으로 변환
            // 각 OrderItemDto를 개별 주문 항목으로 표시
            const transformedOrders: Order[] = searchResults.map((item, index) => ({
                orderId: `search-${index}`, // 임시 주문 ID
                orderDate: new Date().toISOString(), // 임시 날짜 (실제로는 주문 정보가 없음)
                orderNumber: `search-${index}`,
                items: [item], // 각 항목을 개별 주문 항목으로 표시
                totalPrice: (item.price ?? 0) * (item.quantity ?? 0), // 개별 항목의 총 가격
                orderStatus: 'PAID', // 기본 상태
            }));

            setOrders(transformedOrders);
        } catch (err: any) {
            console.error('Failed to search orders:', err);
            setError('검색 중 오류가 발생했습니다.');
            // 검색 실패 시 빈 배열로 설정
            setOrders([]);
        } finally {
            setLoading(false);
        }
    };

    // 주문 내역 로드
    useEffect(() => {
        fetchOrders();
    }, []);

    // 검색어가 변경되면 검색 실행
    useEffect(() => {
        searchOrders(searchTerm);
    }, [searchTerm]);

    // 엔터키 핸들러
    const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
        if (e.key === 'Enter') {
            setSearchTerm(inputValue);
        }
    };

    // 돋보기 아이콘 클릭 핸들러
    const handleSearchClick = () => {
        setSearchTerm(inputValue);
    };

    // 주문 상태를 읽기 쉬운 텍스트로 변환
    const getStatusText = (status: string) => {
        const statusMap: Record<string, string> = {
            'BEFORE_PAID': '결제 대기',
            'PAID': '결제 완료',
            'IN_DELIVERY': '배송 중',
            'DELIVERY_COMPLETED': '배송 완료',
            'PURCHASE_CONFIRMED': '구매 확정',
        };
        return statusMap[status] || status;
    };

    // 날짜 포맷팅
    const formatDate = (dateString: string) => {
        const date = new Date(dateString);
        return date.toLocaleDateString('ko-KR', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
        });
    };

    if (loading) {
        return (
            <div className="min-h-screen bg-gray-50 flex items-center justify-center">
                <div className="text-xl text-gray-600">주문 내역을 불러오는 중...</div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="min-h-screen bg-gray-50 flex items-center justify-center">
                <div className="text-xl text-red-600">{error}</div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50">
            <div className="max-w-5xl mx-auto px-6 py-8">
                {/* Page Title */}
                <div className="mb-6">
                    <h1 className="text-2xl text-gray-900">Order History</h1>
                </div>

                {/* Search Bar */}
                <div className="mb-6 relative">
                    <input
                        type="text"
                        placeholder="Search ordered products"
                        value={inputValue}
                        onChange={(e) => setInputValue(e.target.value)}
                        onKeyDown={handleKeyDown}
                        className="w-full px-4 py-3 pr-12 bg-white border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent text-sm"
                    />
                    <button
                        onClick={handleSearchClick}
                        className="absolute right-4 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 transition-colors cursor-pointer"
                        aria-label="Search"
                    >
                        <Search className="w-5 h-5" />
                    </button>
                </div>

                {/* Order List */}
                <div className="space-y-8">
                    {orders.map((order) => (
                        <div key={order.orderId} className="space-y-4">
                            {/* Order Date Header */}
                            <div className="flex items-center justify-between px-4 py-3 bg-gray-100 rounded-lg">
                                <div className="flex items-center gap-4">
                                    <span className="text-sm text-gray-900">
                                        Order Date: {formatDate(order.orderDate)}
                                    </span>
                                    <span className="text-sm text-gray-500">
                                        Order No. {order.orderNumber}
                                    </span>
                                    <span className="text-sm text-gray-500">
                                        Total: ₩{(order.totalPrice ?? 0).toLocaleString()}
                                    </span>
                                </div>
                            </div>

                            {/* Order Items */}
                            <div className="bg-white border border-gray-200 rounded-lg overflow-hidden">
                                {order.items.map((item, index) => (
                                    <div key={`${item.productId}-${index}`}>
                                        {index > 0 && (
                                            <div className="border-t border-gray-100"></div>
                                        )}
                                        <div className="p-6">
                                            <div className="flex gap-6">
                                                {/* Product Image */}
                                                <Link to={`/product/${item.productId}`}>
                                                    <img
                                                        src={item.imageUrl || 'https://via.placeholder.com/400'}
                                                        alt={item.productName}
                                                        className="w-24 h-24 object-cover rounded-md border border-gray-200 hover:opacity-80 transition-opacity"
                                                    />
                                                </Link>

                                                {/* Product Info & Status */}
                                                <div className="flex-1 min-w-0">
                                                    <div className="flex items-start justify-between gap-4">
                                                        {/* Left: Product Details */}
                                                        <div className="flex-1">
                                                            <h3 className="text-base text-gray-900 mt-1">
                                                                {item.productName}
                                                            </h3>
                                                            <div className="flex items-center gap-4 mt-2">
                                                                <span className="text-sm text-gray-700">
                                                                    ₩{(item.price ?? 0).toLocaleString()}
                                                                </span>
                                                                <span className="text-sm text-gray-500">
                                                                    Quantity: {item.quantity ?? 0}
                                                                </span>
                                                                {item.size && (
                                                                    <span className="text-sm text-gray-500">
                                                                        Size: {typeof item.size === 'string' ? item.size : String(item.size)}
                                                                    </span>
                                                                )}
                                                            </div>

                                                            {/* Delivery Status */}
                                                            <div className="mt-4 flex items-center gap-3">
                                                                <span className={`inline-flex items-center px-3 py-1 rounded-full text-sm border ${
                                                                    order.orderStatus === 'DELIVERY_COMPLETED' || order.orderStatus === 'PURCHASE_CONFIRMED'
                                                                        ? 'bg-green-50 text-green-700 border-green-200'
                                                                        : order.orderStatus === 'IN_DELIVERY'
                                                                        ? 'bg-blue-50 text-blue-700 border-blue-200'
                                                                        : 'bg-gray-50 text-gray-700 border-gray-200'
                                                                }`}>
                                                                    {getStatusText(order.orderStatus)}
                                                                </span>
                                                            </div>
                                                        </div>

                                                        {/* Right: Action Buttons */}
                                                        <div className="flex flex-col gap-2 min-w-[140px]">
                                                            <Link
                                                                to={`/tracking/${order.orderNumber}`}
                                                                className="flex items-center justify-center gap-2 px-4 py-2.5 bg-gray-900 text-white text-sm rounded-md hover:bg-gray-800 transition-colors"
                                                            >
                                                                <Truck className="w-4 h-4" />
                                                                Delivery Tracking
                                                            </Link>
                                                            {item.reviewId ? (
                                                                <Link
                                                                    to={`/review/${item.reviewId}`}
                                                                    className="flex items-center justify-center gap-2 px-4 py-2.5 bg-white text-gray-700 text-sm rounded-md border border-gray-300 hover:bg-gray-50 transition-colors"
                                                                >
                                                                    <Edit3 className="w-4 h-4" />
                                                                    View Review
                                                                </Link>
                                                            ) : (
                                                                <button className="flex items-center justify-center gap-2 px-4 py-2.5 bg-white text-gray-700 text-sm rounded-md border border-gray-300 hover:bg-gray-50 transition-colors">
                                                                    <Edit3 className="w-4 h-4" />
                                                                    Write Review
                                                                </button>
                                                            )}
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>
                    ))}
                </div>

                {/* Empty State */}
                {orders.length === 0 && (
                    <div className="bg-white border border-gray-200 rounded-lg p-12 text-center">
                        <p className="text-gray-500">
                            {searchTerm ? 'No matching products found' : 'No orders yet'}
                        </p>
                        {!searchTerm && (
                            <Link
                                to="/"
                                className="inline-block mt-4 text-sm text-gray-900 hover:underline"
                            >
                                Start Shopping
                            </Link>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
}