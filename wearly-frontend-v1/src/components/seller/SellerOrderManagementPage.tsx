import { Fragment, useEffect, useMemo, useState } from "react";
import { apiFetch } from "../../api/http";

// // 주문(결제) 상태: 백엔드 OrderStatus에 맞춰 필요한 것만 사용
type OrderStatus = "BEFORE_PAID" | "PAID" | "CANCELLED";

// // 판매자 진행 상태(= detailStatus)
type DetailStatus =
  | "BEFORE_PAID"
  | "PAID"
  | "WAIT_CHECK"
  | "CHECK"
  | "IN_DELIVERY"
  | "DELIVERY_COMPLETED"
  | "CANCELLED";

type Carrier = "CJ" | "LOTTE" | "HANJIN" | "ROZEN";

type SellerOrderDetailListResponse = {
  orderDetailId: number;
  orderId: string;

  buyerName: string;
  buyerNickname: string;

  orderStatus: OrderStatus; // 결제 상태
  detailStatus: DetailStatus; // 판매자 진행 상태

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
  { value: "CANCELLED", label: "CANCELLED" },
];

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
      return "bg-red-100 text-red-800";
    case "PAID":
      return "bg-gray-100 text-gray-800";
    default:
      return "bg-gray-100 text-gray-800";
  }
};

const formatDate = (iso?: string) => {
  if (!iso) return "-";
  return iso.replace("T", " ").slice(0, 16);
};

// // 드롭다운 노출 가능한 판매자 진행 상태들
const EDITABLE_DETAIL_STATUSES: DetailStatus[] = [
  "PAID",
  "WAIT_CHECK",
  "CHECK",
  "IN_DELIVERY",
  "DELIVERY_COMPLETED",
];

// // 상태별로 “다음으로 갈 수 있는 상태” 옵션 (백엔드 전이 규칙에 맞춤)
const NEXT_STATUS_OPTIONS_BY_CURRENT: Record<DetailStatus, DetailStatus[]> = {
  BEFORE_PAID: [],
  PAID: ["WAIT_CHECK"],
  WAIT_CHECK: ["CHECK"],
  CHECK: ["IN_DELIVERY"],
  IN_DELIVERY: ["DELIVERY_COMPLETED"],
  DELIVERY_COMPLETED: [],
  CANCELLED: [],
};

export default function SellerOrderManagementPage() {
  const [orders, setOrders] = useState<SellerOrderDetailListResponse[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const pageSize = 10;

  // // 필터: 서버는 status 파라미터를 detailStatus 기준으로 필터함(백엔드 주석 기준)
  const [statusFilter, setStatusFilter] = useState<string>("ALL");
  const [searchQuery, setSearchQuery] = useState("");

  // // IN_DELIVERY 선택 시 확장되는 입력 폼 상태
  const [expandedOrderDetailId, setExpandedOrderDetailId] = useState<number | null>(null);
  const [tempStatusById, setTempStatusById] = useState<Record<number, DetailStatus>>({});
  const [tempDeliveryInfoById, setTempDeliveryInfoById] = useState<
    Record<number, { carrier: Carrier | ""; invoiceNumber: string }>
  >({});
  const [savingOrderDetailId, setSavingOrderDetailId] = useState<number | null>(null);
  const [saveErrorById, setSaveErrorById] = useState<Record<number, string>>({});

  // // 주문 목록 조회 메소드
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

  // // 최초 + page/statusFilter 변경 시 재조회
  useEffect(() => {
    fetchOrders(page);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page, statusFilter]);

  // // 서버에서 내려온 detailStatus를 임시 상태로 맞춰둠 (드롭다운 표시/롤백용)
  useEffect(() => {
    setTempStatusById((prev) => {
      const next: Record<number, DetailStatus> = { ...prev };
      orders.forEach((od) => {
        next[od.orderDetailId] = od.detailStatus;
      });
      return next;
    });
  }, [orders]);

  // // 검색은 서버에 안 넘기고 프론트에서만 필터
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

  // // 주문상세 상태 변경 API 호출 메소드
  const updateDetailStatus = async (
    orderDetailId: number,
    nextStatus: DetailStatus,
    extra?: { carrier?: Carrier | null; invoiceNumber?: string | null }
  ) => {
    await apiFetch<void>(`/api/seller/orders/${orderDetailId}/status`, {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json",
        Accept: "application/json",
      },
      body: JSON.stringify({
        nextStatus, // ✅ 백엔드 DTO 필드명에 맞춤
        carrier: extra?.carrier ?? null,
        invoiceNumber: extra?.invoiceNumber ?? null,
      }),
    });
  };

  // // 즉시 상태 변경 처리(배송중 제외)
  const handleImmediateStatusChange = async (
    orderDetailId: number,
    detailStatus: DetailStatus
  ) => {
    try {
      await updateDetailStatus(orderDetailId, detailStatus);

      setOrders((prev) =>
        prev.map((od) =>
          od.orderDetailId === orderDetailId ? { ...od, detailStatus } : od
        )
      );
      setTempStatusById((prev) => ({ ...prev, [orderDetailId]: detailStatus }));
    } catch (e: any) {
      // // 실패 시 임시 선택 상태 롤백
      setTempStatusById((prev) => {
        const current = orders.find((od) => od.orderDetailId === orderDetailId);
        return { ...prev, [orderDetailId]: current?.detailStatus ?? detailStatus };
      });

      setErrorMessage(e?.message ?? "상태 변경에 실패했습니다.");
    }
  };

  // // 드롭다운 선택 변경 핸들러
  const handleStatusSelectChange = async (
    od: SellerOrderDetailListResponse,
    nextStatus: DetailStatus
  ) => {
    // // 화면에서 선택 값은 먼저 바꿔둠
    setTempStatusById((prev) => ({ ...prev, [od.orderDetailId]: nextStatus }));

    if (nextStatus === "IN_DELIVERY") {
      // // 배송중은 저장 버튼을 눌러야만 실제 상태가 변경됨
      setExpandedOrderDetailId(od.orderDetailId);
      setTempDeliveryInfoById((prev) => {
        const existing = prev[od.orderDetailId];
        return {
          ...prev,
          [od.orderDetailId]: {
            carrier: existing?.carrier ?? od.carrier ?? "",
            invoiceNumber: existing?.invoiceNumber ?? od.invoiceNumber ?? "",
          },
        };
      });
      setSaveErrorById((prev) => ({ ...prev, [od.orderDetailId]: "" }));
      return;
    }

    // // 배송중 입력폼이 열려있던 상태에서 다른 상태 선택 시 닫기
    if (expandedOrderDetailId === od.orderDetailId) {
      setExpandedOrderDetailId(null);
    }

    await handleImmediateStatusChange(od.orderDetailId, nextStatus);
  };

  // // 배송 정보 저장 + 배송중으로 상태 변경
  const handleDeliverySave = async (orderDetailId: number) => {
    const tempInfo = tempDeliveryInfoById[orderDetailId];
    const carrierValue = tempInfo?.carrier ?? "";
    const invoiceValue = tempInfo?.invoiceNumber ?? "";

    if (!carrierValue || !invoiceValue.trim()) {
      setSaveErrorById((prev) => ({
        ...prev,
        [orderDetailId]: "택배사와 송장번호를 입력해주세요.",
      }));
      return;
    }

    try {
      setSavingOrderDetailId(orderDetailId);
      setSaveErrorById((prev) => ({ ...prev, [orderDetailId]: "" }));

      // // 저장 버튼 눌렀을 때만 IN_DELIVERY로 상태 변경 요청
      await updateDetailStatus(orderDetailId, "IN_DELIVERY", {
        carrier: carrierValue as Carrier,
        invoiceNumber: invoiceValue.trim(),
      });

      // // 성공 시 화면 반영
      setOrders((prev) =>
        prev.map((od) =>
          od.orderDetailId === orderDetailId
            ? {
                ...od,
                detailStatus: "IN_DELIVERY",
                carrier: carrierValue as Carrier,
                invoiceNumber: invoiceValue.trim(),
              }
            : od
        )
      );

      setTempStatusById((prev) => ({ ...prev, [orderDetailId]: "IN_DELIVERY" }));
      setExpandedOrderDetailId(null);
      // // 저장된 값은 화면에 계속 노출되도록 유지
      setTempDeliveryInfoById((prev) => ({
        ...prev,
        [orderDetailId]: {
          carrier: carrierValue as Carrier,
          invoiceNumber: invoiceValue.trim(),
        },
      }));
    } catch (e: any) {
      setSaveErrorById((prev) => ({
        ...prev,
        [orderDetailId]: e?.message ?? "배송 정보 저장에 실패했습니다.",
      }));
    } finally {
      setSavingOrderDetailId(null);
    }
  };

  return (
    <div className="p-8">
      <div className="bg-white border border-gray-200 rounded-2xl shadow-sm p-6">
        {/* Header */}
        <div className="mb-6">
          <h1 className="text-2xl font-semibold text-gray-900">Order Detail List</h1>
          <p className="text-sm text-gray-600 mt-2">OrderDetail 기준으로 쭉 보여줌</p>
          {errorMessage && <p className="text-sm text-red-600 mt-3">{errorMessage}</p>}
        </div>

        {/* Filters */}
        <div className="bg-gray-50 border border-gray-200 rounded-lg p-4 mb-6">
          <div className="flex items-center gap-4">
            <div className="flex items-center gap-3">
              <label className="text-sm font-medium text-gray-700">Detail Status</label>
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
                  <td colSpan={7} className="px-6 py-12 text-center text-sm text-gray-500">
                    Loading orders...
                  </td>
                </tr>
              ) : filteredOrders.length === 0 ? (
                <tr>
                  <td colSpan={7} className="px-6 py-12 text-center text-sm text-gray-500">
                    No orders found
                  </td>
                </tr>
              ) : (
                filteredOrders.map((od) => {
                  const tempStatus = tempStatusById[od.orderDetailId] ?? od.detailStatus;
                  const isExpanded = expandedOrderDetailId === od.orderDetailId;
                  const tempDeliveryInfo = tempDeliveryInfoById[od.orderDetailId];
                  const displayCarrier = tempDeliveryInfo?.carrier || od.carrier || "";
                  const displayInvoice =
                    tempDeliveryInfo?.invoiceNumber || od.invoiceNumber || "";
                  const hasDeliveryInfo = Boolean(displayCarrier || displayInvoice);

                  // // detailStatus가 특정 상태이면 드롭다운으로 상태변경 가능
                  const canEditStatus = EDITABLE_DETAIL_STATUSES.includes(od.detailStatus);

                  // // 현재 상태에 따른 “다음 상태 옵션”
                  const nextOptions = NEXT_STATUS_OPTIONS_BY_CURRENT[od.detailStatus] ?? [];

                  // // next 옵션이 아예 없으면 드롭다운 의미 없음(예: DELIVERY_COMPLETED)
                  const shouldShowDropdown = canEditStatus && nextOptions.length > 0;

                  return (
                    <Fragment key={od.orderDetailId}>
                      <tr
                        className="hover:bg-gray-50 transition-colors"
                        title={`orderId: ${od.orderId} / orderedAt: ${formatDate(od.orderedAt)}`}
                      >
                        <td className="px-6 py-4 text-sm text-gray-900">{od.orderDetailId}</td>

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

                        <td className="px-6 py-4 text-sm text-gray-900">{od.quantity ?? 0}</td>

                        <td className="px-6 py-4 text-sm text-gray-900">
                          {Number(od.price ?? 0).toLocaleString()}원
                        </td>

                        <td className="px-6 py-4 text-sm text-gray-900">
                          <div className="font-medium">{od.buyerName ?? "-"}</div>
                          <div className="text-xs text-gray-500">{od.buyerNickname ?? ""}</div>
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
                          {shouldShowDropdown ? (
                            <select
                              value={tempStatus}
                              onChange={(e) =>
                                handleStatusSelectChange(od, e.target.value as DetailStatus)
                              }
                              className="w-full min-w-[160px] px-2 py-1.5 border border-gray-300 rounded-md text-xs focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent"
                            >
                              {/* // 현재 상태(선택불가) */}
                              <option value={od.detailStatus} disabled>
                                {od.detailStatus}
                              </option>

                              {/* // 다음 상태 옵션 */}
                              {nextOptions.map((status) => (
                                <option key={status} value={status}>
                                  {status}
                                </option>
                              ))}
                            </select>
                          ) : (
                            <span
                              className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(
                                od.detailStatus
                              )}`}
                            >
                              {od.detailStatus ?? "-"}
                            </span>
                          )}
                        </td>
                      </tr>

                      {/* 입력/저장된 배송 정보는 항상 주문 상세 아래에 노출 */}
                      {hasDeliveryInfo && (
                        <tr className="bg-gray-50">
                          <td colSpan={7} className="px-6 py-3 text-sm text-gray-700">
                            택배사: {displayCarrier || "-"} / 송장번호:{" "}
                            {displayInvoice || "-"}
                          </td>
                        </tr>
                      )}

                      {/* 배송중 선택 시 아래로 펼쳐지는 입력 폼 */}
                      {isExpanded && (
                        <tr className="bg-gray-50">
                          <td colSpan={7} className="px-6 py-4">
                            <div className="border border-gray-200 rounded-lg bg-white p-4">
                              <div className="text-sm font-medium text-gray-800 mb-3">
                                배송 정보 입력
                              </div>

                              <div className="grid grid-cols-1 md:grid-cols-3 gap-3 items-center">
                                <div>
                                  <label className="block text-xs font-medium text-gray-600 mb-1">
                                    택배사
                                  </label>
                                  <select
                                    value={tempDeliveryInfo?.carrier ?? ""}
                                    onChange={(e) =>
                                      setTempDeliveryInfoById((prev) => ({
                                        ...prev,
                                        [od.orderDetailId]: {
                                          carrier: e.target.value as Carrier,
                                          invoiceNumber:
                                            prev[od.orderDetailId]?.invoiceNumber ?? "",
                                        },
                                      }))
                                    }
                                    className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent"
                                  >
                                    <option value="">선택</option>
                                    <option value="CJ">CJ</option>
                                    <option value="LOTTE">LOTTE</option>
                                    <option value="HANJIN">HANJIN</option>
                                    <option value="ROZEN">ROZEN</option>
                                  </select>
                                </div>

                                <div>
                                  <label className="block text-xs font-medium text-gray-600 mb-1">
                                    송장번호
                                  </label>
                                  <input
                                    type="text"
                                    value={tempDeliveryInfo?.invoiceNumber ?? ""}
                                    onChange={(e) =>
                                      setTempDeliveryInfoById((prev) => ({
                                        ...prev,
                                        [od.orderDetailId]: {
                                          carrier: prev[od.orderDetailId]?.carrier ?? "",
                                          invoiceNumber: e.target.value,
                                        },
                                      }))
                                    }
                                    placeholder="송장번호 입력"
                                    className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent"
                                  />
                                </div>

                                <div className="flex items-end justify-end gap-3">
                                  <button
                                    type="button"
                                    onClick={() => handleDeliverySave(od.orderDetailId)}
                                    disabled={savingOrderDetailId === od.orderDetailId}
                                    className="px-4 py-2 text-sm border border-gray-900 text-gray-900 rounded-md hover:bg-gray-900 hover:text-white disabled:opacity-50 disabled:cursor-not-allowed"
                                  >
                                    Save
                                  </button>
                                </div>
                              </div>

                              {saveErrorById[od.orderDetailId] && (
                                <p className="text-sm text-red-600 mt-3">
                                  {saveErrorById[od.orderDetailId]}
                                </p>
                              )}
                            </div>
                          </td>
                        </tr>
                      )}
                    </Fragment>
                  );
                })
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination */}
        <div className="flex items-center justify-between mt-6">
          <div className="text-sm text-gray-600">{totalElements.toLocaleString()} total</div>

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
