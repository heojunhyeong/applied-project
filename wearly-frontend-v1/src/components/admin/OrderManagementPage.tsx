import { useState } from 'react';
import { ChevronDown, ChevronRight } from 'lucide-react';
import AdminLayout from './AdminLayout';

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
}

export default function OrderManagementPage() {
  const [expandedOrderId, setExpandedOrderId] = useState<string | null>(null);

  const [orders] = useState<Order[]>([
    {
      orderId: 'O001',
      orderNumber: '2024011501234',
      userId: 'U001',
      totalAmount: 248000,
      orderStatus: 'Completed',
      products: [
        {
          productName: 'NIKE Air Max 270',
          quantity: 1,
          price: 159000,
          sellerId: 'S001',
          status: 'Completed',
        },
        {
          productName: 'ADIDAS Originals Hoodie',
          quantity: 1,
          price: 89000,
          sellerId: 'S002',
          status: 'Completed',
        },
      ],
    },
    {
      orderId: 'O002',
      orderNumber: '2024011502345',
      userId: 'U003',
      totalAmount: 159000,
      orderStatus: 'Completed',
      products: [
        {
          productName: 'NIKE Air Force 1',
          quantity: 1,
          price: 159000,
          sellerId: 'S001',
          status: 'Completed',
        },
      ],
    },
    {
      orderId: 'O003',
      orderNumber: '2024011603456',
      userId: 'U002',
      totalAmount: 327000,
      orderStatus: 'Pending',
      products: [
        {
          productName: 'THE NORTH FACE Nuptse Jacket',
          quantity: 1,
          price: 298000,
          sellerId: 'S003',
          status: 'Pending',
        },
        {
          productName: "LEVI'S 501 Original Jeans",
          quantity: 1,
          price: 129000,
          sellerId: 'S004',
          status: 'Pending',
        },
      ],
    },
    {
      orderId: 'O004',
      orderNumber: '2024011604567',
      userId: 'U001',
      totalAmount: 89000,
      orderStatus: 'Cancelled',
      products: [
        {
          productName: 'ADIDAS Originals Hoodie',
          quantity: 1,
          price: 89000,
          sellerId: 'S002',
          status: 'Cancelled',
        },
      ],
    },
    {
      orderId: 'O005',
      orderNumber: '2024011605678',
      userId: 'U005',
      totalAmount: 456000,
      orderStatus: 'Completed',
      products: [
        {
          productName: 'NEW BALANCE 990v5',
          quantity: 2,
          price: 228000,
          sellerId: 'S005',
          status: 'Completed',
        },
      ],
    },
    {
      orderId: 'O006',
      orderNumber: '2024011606789',
      userId: 'U003',
      totalAmount: 128000,
      orderStatus: 'Refunded',
      products: [
        {
          productName: "LEVI'S Trucker Jacket",
          quantity: 1,
          price: 128000,
          sellerId: 'S004',
          status: 'Cancelled',
        },
      ],
    },
    {
      orderId: 'O007',
      orderNumber: '2024011607890',
      userId: 'U002',
      totalAmount: 215000,
      orderStatus: 'Pending',
      products: [
        {
          productName: 'NIKE Sportswear Tech Fleece',
          quantity: 1,
          price: 215000,
          sellerId: 'S001',
          status: 'Pending',
        },
      ],
    },
    {
      orderId: 'O008',
      orderNumber: '2024011608901',
      userId: 'U004',
      totalAmount: 98000,
      orderStatus: 'Completed',
      products: [
        {
          productName: 'ADIDAS Superstar Shoes',
          quantity: 1,
          price: 98000,
          sellerId: 'S002',
          status: 'Completed',
        },
      ],
    },
  ]);

  const toggleOrderExpand = (orderId: string) => {
    if (expandedOrderId === orderId) {
      setExpandedOrderId(null);
    } else {
      setExpandedOrderId(orderId);
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