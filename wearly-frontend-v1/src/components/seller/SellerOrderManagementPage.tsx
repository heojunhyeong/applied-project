import { useEffect, useMemo, useState } from "react";
import { apiFetch } from "../../api/http";

type OrderStatus =
  | "BEFORE_PAID"
  | "PAID"
  | "WAIT_CHECK"
  | "CHECK"
  | "IN_DELIVERY"
  | "DELIVERY_COMPLETED"
  | "CANCELLED"
  | "REFUNDED";

type Carrier =
  | "CJ"
  | "HANJIN"
  | "LOGEN"
  | "POST"
  | "ROCKET"
  | "ETC";

type SellerOrderDetailListResponse = {
  orderDetailId: number;
  orderId: string;

  buyerName: string;
  buyerNickname: string;

  orderStatus: OrderStatus;   // 결제 상태
  detailStatus: OrderStatus;  // 판매자 진행 상태

  productId: number;
  productName: string;
  productImageUrl: string;

  quantity: number;
  price: number;

  carrier: Carrier | null;
  invoiceNumber: string | null;

  orderedAt: string; // LocalDateTime -> ISO string
};

type PageResponse<T> = {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number; // current page (0-base)
  size: number;
};

const DETAIL_STATUS_FILTER_OPTIONS: { value: string; label: string }[] = [
  { value: "ALL", label: "All" },
  { value: "WAIT_CHECK", label: "WAIT_CHECK" },
  { value: "CHECK", label: "CHECK" },
  { value: "IN_DELIVERY", label: "IN_DELIVERY" },
  { value: "DELIVERY_COMPLETED", label: "DELIVERY_COMPLETED" },
];

// 상태 badge 색(너 디자인 유지)
const getStatusColor = (status?: string) => {
  switch (status) {
    case "DELIVERY_COMPLETED":
      return "bg-green-100 text-green-800";
    case "IN_DELIVERY":
      return "bg-blue-100 text-blue-800";
    case "CHECK":
      return "bg-yellow-100 text-yellow-800";
    case "WAIT_CHECK":
      return "bg-gray-100 text-gray-800";
    case "CANCELLED":
    case "REFUNDED":
      return "bg-red-100 text-red-800";
    default:
      return "bg-gray-100 text-gray-800";
  }
};

const formatDate = (iso?: string) => {
  if (!iso) return "-";
  // 2026-01-17T22:32:39 형태면 앞부분만
  return iso.replace("T", " ").slice(0, 16);
};

export default function SellerOrderManagementPage() {
  const [orders, setOrders] = useState<SellerOrderDetailListResponse[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const pageSize = 10;

  // 필터: detailStatus 기준으로 서버에 넘길거임 (컨트롤러가 status를 detailStatus로 필터함)
  const [statusFilter, setStatusFilter] = useState<string>("ALL");
  const [searchQuery, setSearchQuery] = useState("");

  const fetchOrders = async (nextPage = page) => {
    setIsLoading(true);
    setErrorMessage(null);

    try {
      const statusParam =
        statusFilter === "ALL" ? "" : `&status=${encodeURIComponent(statusFilter)}`;

      const response = await apiFetch<PageResponse<SellerOrderDetailListResponse>>(
        `/api/seller/orders?page=${nextPage}&size=${pageSize}${statusParam}&sort=orderedAt,desc`,
        { method: "GET" }
      );

      setOrders(response.content ?? []);
      setTotalPages(response.totalPages ?? 0);
      setTotalElements(response.totalElements ?? 0);
      setPage(response.number ?? nextPage);
    } catch (e: any) {
      setOrders([]);
      setTotalPages(0);
      setTotalElements(0);
      setErrorMessage(e?.message ?? "주문 목록 조회에 실패했습니다.");
    } finally {
      setIsLoading(false);
    }
  };

  // 최초 + page/statusFilter 변경 시 재조회
  useEffect(() => {
    fetchOrders(page);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page, statusFilter]);

  // 검색은 서버에 안 넘기고 프론트에서만 필터 (빠르게)
  const filteredOrders = useMemo(() => {
    const q = searchQuery.trim().toLowerCase();
    if (!q) return orders;

    return orders.filter((od) => {
      const orderId = (od.orderId ?? "").toLowerCase();
      const buyerName = (od.buyerName ?? "").toLowerCase();
      const buyerNick = (od.buyerNickname ?? "").toLowerCase();
      const productName = (od.productName ?? "").toLowerCase();

      return (
        orderId.includes(q) ||
        buyerName.includes(q) ||
        buyerNick.includes(q) ||
        productName.includes(q)
      );
    });
  }, [orders, searchQuery]);

  const handlePrevPage = () => {
    if (page <= 0) return;
    setPage((prev) => Math.max(prev - 1, 0));
  };

  const handleNextPage = () => {
    if (totalPages <= 0 || page >= totalPages - 1) return;
    setPage((prev) => Math.min(prev + 1, totalPages - 1));
  };

  return (
    <div className="p-8">
      <div className="bg-white border border-gray-200 rounded-2xl shadow-sm p-6">
        {/* Header */}
        <div className="mb-6">
          <h1 className="text-2xl font-semibold text-gray-900">
            Order Detail List
          </h1>
          <p className="text-sm text-gray-600 mt-2">
            OrderDetail 기준으로 쭉 보여줌
          </p>
          {errorMessage && (
            <p className="text-sm text-red-600 mt-3">{errorMessage}</p>
          )}
        </div>

        {/* Filters */}
        <div className="bg-gray-50 border border-gray-200 rounded-lg p-4 mb-6">
          <div className="flex items-center gap-4">
            <div className="flex items-center gap-3">
              <label className="text-sm font-medium text-gray-700">
                Detail Status
              </label>
              <select
                value={statusFilter}
                onChange={(e) => {
                  setPage(0);
                  setStatusFilter(e.target.value);
                }}
                className="px-3 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent"
              >
                {DETAIL_STATUS_FILTER_OPTIONS.map((opt) => (
                  <option key={opt.value} value={opt.value}>
                    {opt.label}
                  </option>
                ))}
              </select>
            </div>

            <div className="flex-1 max-w-md">
              <input
                type="text"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="Search by orderId / buyer / product"
                className="w-full px-4 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent"
              />
            </div>

            <div className="ml-auto text-sm text-gray-600">
              {filteredOrders.length} / {totalElements} rows
            </div>
          </div>
        </div>

        {/* Table */}
        <div className="border border-gray-200 rounded-lg overflow-hidden">
          <table className="w-full">
            <thead className="bg-gray-50 border-b border-gray-200">
              <tr>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  ODetail ID
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Product
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Qty
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Price
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Buyer
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Order Status
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Detail Status
                </th>
              </tr>
            </thead>

            <tbody className="divide-y divide-gray-200">
              {isLoading ? (
                <tr>
                  <td
                    colSpan={7}
                    className="px-6 py-12 text-center text-sm text-gray-500"
                  >
                    Loading orders...
                  </td>
                </tr>
              ) : filteredOrders.length === 0 ? (
                <tr>
                  <td
                    colSpan={7}
                    className="px-6 py-12 text-center text-sm text-gray-500"
                  >
                    No orders found
                  </td>
                </tr>
              ) : (
                filteredOrders.map((od) => (
                  <tr
                    key={od.orderDetailId}
                    className="hover:bg-gray-50 transition-colors"
                    title={`orderId: ${od.orderId} / orderedAt: ${formatDate(od.orderedAt)}`}
                  >
                    <td className="px-6 py-4 text-sm text-gray-900">
                      {od.orderDetailId}
                    </td>

                    <td className="px-6 py-4 text-sm text-gray-900">
                      <div className="flex items-center gap-3">
                        {od.productImageUrl ? (
                          <img
                            src={od.productImageUrl}
                            alt={od.productName}
                            className="w-10 h-10 rounded-md object-cover"
                          />
                        ) : (
                          <div className="w-10 h-10 rounded-md bg-gray-200" />
                        )}
                        <div className="min-w-0">
                          <div className="font-medium truncate max-w-[260px]">
                            {od.productName ?? "-"}
                          </div>
                          <div className="text-xs text-gray-500">
                            {od.orderId} · {formatDate(od.orderedAt)}
                          </div>
                        </div>
                      </div>
                    </td>

                    <td className="px-6 py-4 text-sm text-gray-900">
                      {od.quantity ?? 0}
                    </td>

                    <td className="px-6 py-4 text-sm text-gray-900">
                      {Number(od.price ?? 0).toLocaleString()}원
                    </td>

                    <td className="px-6 py-4 text-sm text-gray-900">
                      <div className="font-medium">{od.buyerName ?? "-"}</div>
                      <div className="text-xs text-gray-500">
                        {od.buyerNickname ?? ""}
                      </div>
                    </td>

                    <td className="px-6 py-4 text-sm">
                      <span
                        className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(
                          od.orderStatus
                        )}`}
                      >
                        {od.orderStatus ?? "-"}
                      </span>
                    </td>

                    <td className="px-6 py-4 text-sm">
                      <span
                        className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(
                          od.detailStatus
                        )}`}
                      >
                        {od.detailStatus ?? "-"}
                      </span>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination */}
        <div className="flex items-center justify-between mt-6">
          <div className="text-sm text-gray-600">
            {totalElements.toLocaleString()} total
          </div>

          <div className="flex items-center gap-2">
            <button
              onClick={handlePrevPage}
              disabled={page <= 0}
              className="px-4 py-2 text-sm border border-gray-300 rounded-md disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
            >
              Prev
            </button>

            <span className="text-sm text-gray-600">
              Page {page + 1} of {Math.max(totalPages, 1)}
            </span>

            <button
              onClick={handleNextPage}
              disabled={totalPages <= 0 || page >= totalPages - 1}
              className="px-4 py-2 text-sm border border-gray-300 rounded-md disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
            >
              Next
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
