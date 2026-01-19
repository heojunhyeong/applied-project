import { useState, useEffect } from 'react';
import { ChevronDown, ChevronRight } from 'lucide-react';
import AdminLayout from './AdminLayout';
import { apiFetch } from '../../api/http';

interface OrderListResponse {
  orderId: number;
  orderNumber: string;
  userId: number;
  userName: string | null;
  paymentStatus: string;
  totalAmount: number;
}

interface OrderDetailResponse {
  orderId: number;
  orderNumber: string;
  userId: number;
  userName: string | null;
  userNickname: string;
  userEmail: string;
  orderDate: string;
  totalPrice: number;
  couponDiscountPrice: number;
  finalPrice: number;
  orderStatus: string;
  deliveryStatus: string;
  isPaid: boolean;
  paymentInfo?: {
    exists: boolean;
    status: string;
    amount: number;
    paymentMethod: string;
    paymentDate: string;
  };
  orderItems: Array<{
    productId: number;
    productName: string;
    imageUrl: string;
    quantity: number;
    price: number;
    totalItemPrice: number;
  }>;
  deliveryInfo?: {
    address: string;
    detailAddress: string;
    zipCode: number;
    carrier: string;
    invoiceNumber: string;
  };
}

interface OrderProduct {
  productName: string;
  quantity: number;
  price: number;
  sellerId: string;
  status: 'Completed' | 'Pending' | 'Cancelled' | 'Refunded';
}

interface Order {
  orderId: string;
  orderNumber: string;
  userId: string;
  userName: string | null;
  totalAmount: number;
  orderStatus: 'Completed' | 'Pending' | 'Cancelled' | 'Refunded';
  products: OrderProduct[];
  detail?: OrderDetailResponse;
}

export default function OrderManagementPage() {
  const [expandedOrderId, setExpandedOrderId] = useState<string | null>(null);
  const [orders, setOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Fetch orders from API
  useEffect(() => {
    const fetchOrders = async () => {
      try {
        setLoading(true);
        setError(null);

        const response = await apiFetch<OrderListResponse[]>('/api/admin/orders');
        const mappedOrders: Order[] = (response || []).map((o) => ({
          orderId: o.orderId.toString(),
          orderNumber: o.orderNumber,
          userId: o.userId.toString(),
          userName: o.userName || o.userId.toString(),
          totalAmount: o.totalAmount || 0,
          orderStatus: o.paymentStatus === 'O' ? ('Completed' as const) : ('Pending' as const),
          products: [],
        }));

        setOrders(mappedOrders);
      } catch (err: any) {
        setError(err.message || '주문 목록을 불러오는데 실패했습니다.');
        console.error('Failed to fetch orders:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchOrders();
  }, []);

  // Fetch order detail when expanded
  const fetchOrderDetail = async (orderId: string) => {
    const order = orders.find((o) => o.orderId === orderId);
    if (order?.detail) {
      return; // Already loaded
    }

    try {
      const detail = await apiFetch<OrderDetailResponse>(`/api/admin/orders/${orderId}`);

      const statusMap: Record<string, 'Completed' | 'Pending' | 'Cancelled' | 'Refunded'> = {
        DELIVERY_COMPLETED: 'Completed',
        PAID: 'Completed',
        BEFORE_PAID: 'Pending',
        CANCELLED: 'Cancelled',
        REFUNDED: 'Refunded',
      };

      const mappedProducts: OrderProduct[] = detail.orderItems.map((item) => ({
        productName: item.productName,
        quantity: item.quantity,
        price: item.price,
        sellerId: '', // TODO: Get from product detail
        status: statusMap[detail.orderStatus] || 'Pending',
      }));

      setOrders((prevOrders) =>
        prevOrders.map((o) => {
          if (o.orderId === orderId) {
            const newStatus = statusMap[detail.orderStatus] || o.orderStatus;
            // 상세 정보를 불러올 때 상태가 실제로 변경된 경우에만 업데이트
            // 목록에서 이미 표시된 상태와 다를 수 있으므로, 실제 서버 상태로 동기화
            return {
              ...o,
              totalAmount: detail.finalPrice,
              orderStatus: newStatus,
              products: mappedProducts,
              detail,
            };
          }
          return o;
        })
      );
    } catch (err: any) {
      console.error('Failed to fetch order detail:', err);
      alert(`주문 상세 정보를 불러오는데 실패했습니다: ${err.message}`);
    }
  };

  const toggleOrderExpand = async (orderId: string) => {
    if (expandedOrderId === orderId) {
      setExpandedOrderId(null);
    } else {
      setExpandedOrderId(orderId);
      await fetchOrderDetail(orderId);
    }
  };

  // Handle order cancellation
  const handleCancelOrder = async (orderId: string) => {
    if (!confirm('정말 주문을 취소하시겠습니까?')) {
      return;
    }

    try {
      await apiFetch(`/api/admin/orders/${orderId}/cancel`, {
        method: 'PATCH',
      });

      // Update local state
      setOrders((prevOrders) =>
        prevOrders.map((order) =>
          order.orderId === orderId
            ? { ...order, orderStatus: 'Cancelled' as const }
            : order
        )
      );

      alert('주문이 취소되었습니다.');
    } catch (err: any) {
      alert(`주문 취소 실패: ${err.message || '알 수 없는 오류가 발생했습니다.'}`);
      console.error('Failed to cancel order:', err);
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'Completed':
        return 'bg-green-100 text-green-800';
      case 'Pending':
        return 'bg-yellow-100 text-yellow-800';
      case 'Cancelled':
        return 'bg-red-100 text-red-800';
      case 'Refunded':
        return 'bg-gray-100 text-gray-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  if (loading) {
    return (
      <AdminLayout>
        <div className="p-8">
          <div className="flex items-center justify-center py-12">
            <p className="text-gray-600">주문 목록을 불러오는 중...</p>
          </div>
        </div>
      </AdminLayout>
    );
  }

  if (error) {
    return (
      <AdminLayout>
        <div className="p-8">
          <div className="bg-red-50 border border-red-200 rounded-lg p-4">
            <p className="text-red-800">{error}</p>
          </div>
        </div>
      </AdminLayout>
    );
  }

  return (
    <AdminLayout>
      <div className="p-8">
        {/* Page Header */}
        <div className="mb-8">
          <h1 className="text-2xl font-semibold text-gray-900">주문 관리</h1>
          <p className="text-sm text-gray-600 mt-2">
            전체 주문을 조회하고 관리하세요
          </p>
        </div>

        {/* Order Table */}
        <div className="bg-white border border-gray-200 rounded-lg overflow-hidden">
          <table className="w-full">
            <thead className="bg-gray-50 border-b border-gray-200">
              <tr>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider w-12">

                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  주문 ID
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  주문 번호
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  사용자 ID
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  총 결제금액
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  주문 상태
                </th>
              </tr>
            </thead>
            <tbody>
              {orders.map((order) => (
                <>
                  {/* Order Row */}
                  <tr
                    key={order.orderId}
                    onClick={() => toggleOrderExpand(order.orderId)}
                    className="hover:bg-gray-50 transition-colors cursor-pointer border-b border-gray-200"
                  >
                    <td className="px-6 py-4 text-sm text-gray-500">
                      {expandedOrderId === order.orderId ? (
                        <ChevronDown className="w-4 h-4" />
                      ) : (
                        <ChevronRight className="w-4 h-4" />
                      )}
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-900 font-medium">
                      {order.orderId}
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-900">{order.orderNumber}</td>
                    <td className="px-6 py-4 text-sm text-gray-900">{order.userName || order.userId}</td>
                    <td className="px-6 py-4 text-sm font-medium text-gray-900">
                      {order.totalAmount.toLocaleString()}원
                    </td>
                    <td className="px-6 py-4 text-sm">
                      <div className="flex items-center gap-2">
                        <span
                          className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(
                            order.orderStatus
                          )}`}
                        >
                          {order.orderStatus}
                        </span>
                        {order.orderStatus === 'Pending' && (
                          <button
                            onClick={(e) => {
                              e.stopPropagation();
                              handleCancelOrder(order.orderId);
                            }}
                            className="px-3 py-1 text-xs font-medium text-red-600 border border-red-300 rounded-md hover:bg-red-50 transition-colors"
                          >
                            Cancel
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>

                  {/* Expanded Order Detail */}
                  {expandedOrderId === order.orderId && (
                    <tr className="bg-blue-50/30">
                      <td></td>
                      <td colSpan={5} className="px-6 py-4">
                        <div className="border border-gray-300 rounded bg-white p-4">
                          {(order.orderStatus === 'Pending' || order.detail?.orderStatus === 'BEFORE_PAID') && (
                            <div className="mb-4 flex justify-end">
                              <button
                                onClick={(e) => {
                                  e.stopPropagation();
                                  handleCancelOrder(order.orderId);
                                }}
                                className="px-4 py-2 text-sm font-medium text-white bg-red-600 border border-red-600 rounded-md hover:bg-red-700 transition-colors"
                              >
                                주문 취소
                              </button>
                            </div>
                          )}
                          <table className="w-full">
                            <thead className="bg-gray-100 border-b border-gray-300">
                              <tr>
                                <th className="px-4 py-2 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                                  상품명
                                </th>
                                <th className="px-4 py-2 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                                  수량
                                </th>
                                <th className="px-4 py-2 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                                  가격
                                </th>
                                <th className="px-4 py-2 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                                  판매자 ID
                                </th>
                                <th className="px-4 py-2 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                                  상태
                                </th>
                              </tr>
                            </thead>
                            <tbody className="divide-y divide-gray-200">
                              {order.products.map((product, index) => (
                                <tr key={index} className="hover:bg-gray-50 transition-colors">
                                  <td className="px-4 py-2 text-sm text-gray-900">
                                    {product.productName}
                                  </td>
                                  <td className="px-4 py-2 text-sm text-gray-900">
                                    {product.quantity}
                                  </td>
                                  <td className="px-4 py-2 text-sm font-medium text-gray-900">
                                    {product.price.toLocaleString()}원
                                  </td>
                                  <td className="px-4 py-2 text-sm text-gray-900">
                                    {product.sellerId}
                                  </td>
                                  <td className="px-4 py-2 text-sm">
                                    <span
                                      className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(
                                        product.status
                                      )}`}
                                    >
                                      {product.status}
                                    </span>
                                  </td>
                                </tr>
                              ))}
                            </tbody>
                          </table>
                        </div>
                      </td>
                    </tr>
                  )}
                </>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </AdminLayout>
  );
}