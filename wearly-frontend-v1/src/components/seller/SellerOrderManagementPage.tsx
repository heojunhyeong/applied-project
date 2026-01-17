import { useState } from "react";
import SellerLayout from "./SellerLayout";

type Order = {
  id: string;
  buyerUserId: string;
  productName: string;
  quantity: number;
  price: number;
  orderStatus: "Pending" | "Completed" | "Cancelled";
  sellerProgressStatus: "WAIT_CHECK" | "CHECK" | "IN_DELIVERY" | "DELIVERY_COMPLETED";
};

const mockOrders: Order[] = [
  {
    id: "ORD001",
    buyerUserId: "user123",
    productName: "Air Max 90 - White/Red",
    quantity: 2,
    price: 258000,
    orderStatus: "Completed",
    sellerProgressStatus: "WAIT_CHECK",
  },
  {
    id: "ORD002",
    buyerUserId: "user456",
    productName: "Ultraboost 22 - Black",
    quantity: 1,
    price: 189000,
    orderStatus: "Pending",
    sellerProgressStatus: "CHECK",
  },
  {
    id: "ORD003",
    buyerUserId: "user789",
    productName: "Fresh Foam 1080v12",
    quantity: 1,
    price: 159000,
    orderStatus: "Completed",
    sellerProgressStatus: "IN_DELIVERY",
  },
  {
    id: "ORD004",
    buyerUserId: "user101",
    productName: "Classic Pullover Hoodie",
    quantity: 3,
    price: 294000,
    orderStatus: "Cancelled",
    sellerProgressStatus: "WAIT_CHECK",
  },
];

export default function SellerOrderManagementPage() {
  const [orders] = useState<Order[]>(mockOrders);

  // 상태 색상 매핑
  const getStatusColor = (status: Order["orderStatus"]) => {
    switch (status) {
      case "Completed":
        return "bg-green-100 text-green-800";
      case "Pending":
        return "bg-yellow-100 text-yellow-800";
      case "Cancelled":
        return "bg-red-100 text-red-800";
      default:
        return "bg-gray-100 text-gray-800";
    }
  };

  return (
    <SellerLayout>
      <div className="p-8">
        {/* Page Header */}
        <div className="mb-8">
          <h1 className="text-2xl font-semibold text-gray-900">
            Order Management
          </h1>
          <p className="text-sm text-gray-600 mt-2">
            Manage and monitor incoming orders
          </p>
        </div>

        {/* Order Table */}
        <div className="bg-white border border-gray-200 rounded-lg overflow-hidden">
          <table className="w-full">
            <thead className="bg-gray-50 border-b border-gray-200">
              <tr>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Order ID
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Buyer ID
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Product
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Quantity
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Total Price
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Order Status
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Seller Status
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-200">
              {orders.map((order) => (
                <tr key={order.id} className="hover:bg-gray-50 transition-colors">
                  <td className="px-6 py-4 text-sm text-gray-900">{order.id}</td>
                  <td className="px-6 py-4 text-sm text-gray-900">
                    {order.buyerUserId}
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-900">
                    {order.productName}
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-900">{order.quantity}</td>
                  <td className="px-6 py-4 text-sm text-gray-900">
                    {order.price.toLocaleString()}원
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
                  <td className="px-6 py-4 text-sm text-gray-900">
                    {order.sellerProgressStatus}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </SellerLayout>
  );
}
