import { useState, useEffect } from 'react';
import { ChevronDown, ChevronRight } from 'lucide-react';
import AdminLayout from './AdminLayout';
import { apiFetch } from '../../api/http';

interface OrderListResponse {
  orderId: number;
  orderNumber: string;
  userId: number;
  paymentStatus: string;
}

interface OrderDetailResponse {
  orderId: number;
  orderNumber: string;
  userId: number;
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
  status: 'Completed' | 'Pending' | 'Cancelled';
}

interface Order {
  orderId: string;
  orderNumber: string;
  userId: string;
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
          totalAmount: 0, // Will be filled from detail
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
        prevOrders.map((o) =>
          o.orderId === orderId
            ? {
                ...o,
                totalAmount: detail.finalPrice,
                orderStatus: statusMap[detail.orderStatus] || 'Pending',
                products: mappedProducts,
                detail,
              }
            : o
        )
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
            <p className="text-gray-600">로딩 중...</p>
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
          <h1 className="text-2xl font-semibold text-gray-900">Order Management</h1>
          <p className="text-sm text-gray-600 mt-2">
            View and manage all orders
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
                  Order ID
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Order Number
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  User ID
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Total Amount
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Order Status
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
                    <td className="px-6 py-4 text-sm text-gray-900">{order.userId}</td>
                    <td className="px-6 py-4 text-sm font-medium text-gray-900">
                      {order.totalAmount.toLocaleString()}원
                    </td>
                    <td className="px-6 py-4 text-sm">
                      <span
                        className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(
                          order.orderStatus
                        )}`}
                      >
                        {order.orderStatus}
                      </span>
                    </td>
                  </tr>

                  {/* Expanded Order Detail */}
                  {expandedOrderId === order.orderId && (
                    <tr className="bg-blue-50/30">
                      <td></td>
                      <td colSpan={5} className="px-6 py-4">
                        <div className="border border-gray-300 rounded bg-white">
                          <table className="w-full">
                            <thead className="bg-gray-100 border-b border-gray-300">
                              <tr>
                                <th className="px-4 py-2 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                                  Product Name
                                </th>
                                <th className="px-4 py-2 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                                  Quantity
                                </th>
                                <th className="px-4 py-2 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                                  Price
                                </th>
                                <th className="px-4 py-2 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                                  Seller ID
                                </th>
                                <th className="px-4 py-2 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                                  Status
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