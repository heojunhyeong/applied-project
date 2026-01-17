import { useState } from "react";

type Order = {
  id: string;
  date: string;
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
    date: "2024-06-01",
    buyerUserId: "user123",
    productName: "Air Max 90 - White/Red",
    quantity: 2,
    price: 258000,
    orderStatus: "Completed",
    sellerProgressStatus: "WAIT_CHECK",
  },
  {
    id: "ORD002",
    date: "2024-06-03",
    buyerUserId: "user456",
    productName: "Ultraboost 22 - Black",
    quantity: 1,
    price: 189000,
    orderStatus: "Pending",
    sellerProgressStatus: "CHECK",
  },
  {
    id: "ORD003",
    date: "2024-06-04",
    buyerUserId: "user789",
    productName: "Fresh Foam 1080v12",
    quantity: 1,
    price: 159000,
    orderStatus: "Completed",
    sellerProgressStatus: "IN_DELIVERY",
  },
  {
    id: "ORD004",
    date: "2024-06-05",
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
  const [statusFilter, setStatusFilter] = useState<"All" | Order["orderStatus"]>(
    "All"
  );
  const [searchQuery, setSearchQuery] = useState("");

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

  // 주문 필터링
  const filteredOrders = orders.filter((order) => {
    const matchesStatus =
      statusFilter === "All" || order.orderStatus === statusFilter;
    const matchesSearch =
      searchQuery === "" ||
      order.id.toLowerCase().includes(searchQuery.toLowerCase()) ||
      order.buyerUserId.toLowerCase().includes(searchQuery.toLowerCase());
    return matchesStatus && matchesSearch;
  });

  return (
    <div className="p-8">
      <div className="bg-white border border-gray-200 rounded-2xl shadow-sm p-6">
          {/* Page Header */}
          <div className="mb-8">
            <h1 className="text-2xl font-semibold text-gray-900">
              Order Management
            </h1>
            <p className="text-sm text-gray-600 mt-2">
              Manage and monitor incoming orders
            </p>
          </div>

          {/* Filters */}
          <div className="bg-gray-50 border border-gray-200 rounded-lg p-4 mb-6">
            <div className="flex items-center gap-4">
              <div className="flex items-center gap-3">
                <label className="text-sm font-medium text-gray-700">
                  Status
                </label>
                <select
                  value={statusFilter}
                  onChange={(e) =>
                    setStatusFilter(e.target.value as "All" | Order["orderStatus"])
                  }
                  className="px-3 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent"
                >
                  <option value="All">All</option>
                  <option value="Pending">Pending</option>
                  <option value="Completed">Completed</option>
                  <option value="Cancelled">Cancelled</option>
                </select>
              </div>
              <div className="flex-1 max-w-md">
                <input
                  type="text"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  placeholder="Search by Order ID or Customer"
                  className="w-full px-4 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent"
                />
              </div>
              <div className="ml-auto text-sm text-gray-600">
                {filteredOrders.length} orders
              </div>
            </div>
          </div>

          {/* Order Table */}
          <div className="border border-gray-200 rounded-lg overflow-hidden">
            <table className="w-full">
              <thead className="bg-gray-50 border-b border-gray-200">
                <tr>
                  <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                    Order ID
                  </th>
                  <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                    Date
                  </th>
                  <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                    Customer
                  </th>
                  <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                    Total Price
                  </th>
                  <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                    Status
                  </th>
                  <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                    Delivery Status
                  </th>
                  <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                    Action
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200">
                {filteredOrders.map((order) => (
                  <tr key={order.id} className="hover:bg-gray-50 transition-colors">
                    <td className="px-6 py-4 text-sm text-gray-900">{order.id}</td>
                    <td className="px-6 py-4 text-sm text-gray-900">{order.date}</td>
                    <td className="px-6 py-4 text-sm text-gray-900">
                      {order.buyerUserId}
                    </td>
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
                    <td className="px-6 py-4 text-sm">
                      <div className="flex items-center gap-2">
                        <button className="px-3 py-2 text-xs font-medium text-gray-900 border border-gray-300 rounded-md hover:bg-gray-50 transition-colors">
                          View Detail
                        </button>
                        <button className="px-3 py-2 text-xs font-medium text-white bg-gray-900 rounded-md hover:bg-gray-800 transition-colors">
                          Update Status
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
      </div>
    </div>
  );
}
