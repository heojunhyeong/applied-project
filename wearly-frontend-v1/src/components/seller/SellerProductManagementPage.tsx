import { ChangeEvent, useEffect, useRef, useState } from "react";
import { createPortal } from "react-dom";
import SellerLayout from "./SellerLayout";
import { apiFetch } from "../../api/http";

type ProductStatus = "ON_SALE" | "SOLD_OUT" | "DELETED";
type ProductSize = "SMALL" | "MEDIUM" | "LARGE" | "EXTRA_LARGE";
type ProductBrand =
  | "NIKE"
  | "ADIDAS"
  | "NEW_BALANCE"
  | "REEBOK"
  | "THE_NORTH_FACE"
  | "VANS";
type ProductCategory =
  | "PADDING"
  | "SHIRT"
  | "COAT"
  | "HOODIE"
  | "SWEATSHIRT"
  | "JEANS"
  | "SHORTS"
  | "MUFFLER";

type Product = {
  id: number;
  imageUrl: string;
  description: string;
  productName: string;
  brand: ProductBrand;
  productCategory: ProductCategory;
  price: number;
  stockQuantity: number;
  status: ProductStatus;
  availableSizes: ProductSize[];
};

type SellerProductResponse = {
  id: number;
  productName: string;
  price: number;
  stockQuantity: number;
  description: string;
  imageUrl: string;
  brand: ProductBrand;
  productCategory: ProductCategory;
  availableSizes?: ProductSize[];
  displayStatus?: ProductStatus;
  status?: ProductStatus;
};

type PageResponse<T> = {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
};

const BRAND_OPTIONS: { value: ProductBrand; label: string }[] = [
  { value: "NIKE", label: "NIKE" },
  { value: "ADIDAS", label: "ADIDAS" },
  { value: "NEW_BALANCE", label: "NEW BALANCE" },
  { value: "REEBOK", label: "REEBOK" },
  { value: "THE_NORTH_FACE", label: "THE NORTH FACE" },
  { value: "VANS", label: "VANS" },
];

const CATEGORY_OPTIONS: { value: ProductCategory; label: string }[] = [
  { value: "PADDING", label: "패딩" },
  { value: "SHIRT", label: "셔츠" },
  { value: "COAT", label: "코트" },
  { value: "HOODIE", label: "후드티" },
  { value: "SWEATSHIRT", label: "맨투맨" },
  { value: "JEANS", label: "청바지" },
  { value: "SHORTS", label: "반바지" },
  { value: "MUFFLER", label: "머플러" },
];

const STATUS_OPTIONS: { value: ProductStatus; label: string }[] = [
  { value: "ON_SALE", label: "판매중" },
  { value: "SOLD_OUT", label: "품절" },
  { value: "DELETED", label: "삭제" },
];

const SIZE_OPTIONS: { value: ProductSize; label: string }[] = [
  { value: "SMALL", label: "S" },
  { value: "MEDIUM", label: "M" },
  { value: "LARGE", label: "L" },
  { value: "EXTRA_LARGE", label: "XL" },
];

export default function SellerProductManagementPage() {
  const [products, setProducts] = useState<Product[]>([]);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [editingProductId, setEditingProductId] = useState<number | null>(null);
  const [draftProduct, setDraftProduct] = useState<Product | null>(null);
  const [priceInput, setPriceInput] = useState("");
  const [stockInput, setStockInput] = useState("");
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const pageSize = 10;

  const thumbnailInputRef = useRef<HTMLInputElement | null>(null);
  const descriptionInputRef = useRef<HTMLInputElement | null>(null);

  // 모달 열림/닫힘에 따른 스크롤 잠금 및 ESC 처리
  useEffect(() => {
    if (!isEditModalOpen) return;

    const previousOverflow = document.body.style.overflow;
    document.body.style.overflow = "hidden";

    const handleKeyDown = (event: KeyboardEvent) => {
      if (event.key === "Escape") {
        handleCancelEdit();
      }
    };

    window.addEventListener("keydown", handleKeyDown);

    return () => {
      document.body.style.overflow = previousOverflow;
      window.removeEventListener("keydown", handleKeyDown);
    };
  }, [isEditModalOpen]);

  // 페이지 변경 시 목록 재조회
  useEffect(() => {
    fetchProducts(page);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page]);

  // 상태 라벨 매핑
  const getStatusLabel = (status: ProductStatus) =>
    STATUS_OPTIONS.find((option) => option.value === status)?.label || "판매중";

  // 브랜드 라벨 매핑
  const getBrandLabel = (brand: ProductBrand) =>
    BRAND_OPTIONS.find((option) => option.value === brand)?.label || brand;

  // 카테고리 라벨 매핑
  const getCategoryLabel = (category: ProductCategory) =>
    CATEGORY_OPTIONS.find((option) => option.value === category)?.label || category;

  // 사이즈 선택 토글
  const toggleSize = (size: ProductSize) => {
    setDraftProduct((prev) => {
      if (!prev) return prev;
      const exists = prev.availableSizes.includes(size);
      const nextSizes = exists
        ? prev.availableSizes.filter((item) => item !== size)
        : [...prev.availableSizes, size];
      return { ...prev, availableSizes: nextSizes };
    });
  };

  // API 응답을 화면 모델로 변환
  const mapProductFromResponse = (data: SellerProductResponse): Product => {
    const status = data.displayStatus ?? data.status ?? "ON_SALE";
    return {
      id: data.id,
      imageUrl: data.imageUrl,
      description: data.description,
      productName: data.productName,
      brand: data.brand,
      productCategory: data.productCategory,
      price: Number(data.price),
      stockQuantity: Number(data.stockQuantity),
      status,
      availableSizes: data.availableSizes ?? [],
    };
  };

  // 상품 목록 조회
  const fetchProducts = async (nextPage = page) => {
    setIsLoading(true);
    setErrorMessage(null);
    try {
      const response = await apiFetch<PageResponse<SellerProductResponse>>(
        `/api/seller/products?page=${nextPage}&size=${pageSize}&sort=createdDate,desc`,
        { method: "GET" }
      );
      setProducts(response.content.map(mapProductFromResponse));
      setTotalPages(response.totalPages);
      setTotalElements(response.totalElements);
      setPage(response.number);
    } catch (error: any) {
      setErrorMessage(error.message ?? "상품 목록 조회에 실패했습니다.");
    } finally {
      setIsLoading(false);
    }
  };

  // 상품 상세 조회
  const fetchProductDetail = async (productId: number) => {
    try {
      const response = await apiFetch<SellerProductResponse>(
        `/api/seller/products/${productId}`,
        { method: "GET" }
      );
      const mapped = mapProductFromResponse(response);
      setDraftProduct(mapped);
      setPriceInput(String(mapped.price));
      setStockInput(String(mapped.stockQuantity));
    } catch (error: any) {
      setErrorMessage(error.message ?? "상품 상세 조회에 실패했습니다.");
    }
  };

  // 상태 색상 매핑
  const getStatusColor = (status: ProductStatus) => {
    switch (status) {
      case "ON_SALE":
        return "bg-green-100 text-green-800";
      case "SOLD_OUT":
        return "bg-yellow-100 text-yellow-800";
      case "DELETED":
        return "bg-red-100 text-red-800";
      default:
        return "bg-gray-100 text-gray-800";
    }
  };

  // 상품 수정 모달 열기
 // 상품 수정 모달 열기 (상세 재조회 포함)
const handleOpenEdit = async (product: Product) => {
  setErrorMessage(null);
  setEditingProductId(product.id);
  setIsEditModalOpen(true);

  // 일단 리스트 데이터로 바로 보여주고
  setDraftProduct(product);
  setPriceInput(String(product.price));
  setStockInput(String(product.stockQuantity));

  // 그 다음 상세 조회로 availableSizes까지 정확히 덮어쓰기
  await fetchProductDetail(product.id);
};


  // 상품 수정 모달 닫기
  const handleCloseEdit = () => {
    setIsEditModalOpen(false);
    setEditingProductId(null);
    setDraftProduct(null);
    setPriceInput("");
    setStockInput("");
  };

  // 상품 수정 취소
  const handleCancelEdit = () => {
    handleCloseEdit();
  };

  // 숫자 입력 정규화 (숫자만 허용, 선행 0 제거)
  const normalizeNumericInput = (value: string) => {
    const digitsOnly = value.replace(/\D/g, "");
    if (!digitsOnly) return "";
    if (digitsOnly.startsWith("0")) return "";
    return digitsOnly;
  };

  // presigned-url 업로드 처리
  const uploadImageWithPresignedUrl = async (
    file: File,
    type: "THUMBNAIL" | "DESCRIPTION"
  ) => {
    const extension = file.type.split("/")[1] || "jpg";
    const result = await apiFetch<{ url: string; key: string }>(
      `/api/seller/products/presigned-url?extension=${extension}&type=${type}`,
      { method: "POST" }
    );

    await fetch(result.url, {
      method: "PUT",
      headers: {
        "Content-Type": file.type,
      },
      body: file,
    });

    return result.url.split("?")[0];
  };

  // 썸네일 이미지 변경
  const handleChangeThumbnail = () => {
    thumbnailInputRef.current?.click();
  };

  // 설명 이미지 변경
  const handleChangeDescription = () => {
    descriptionInputRef.current?.click();
  };

  // 파일 선택 처리
  const handleFileSelected = async (
    event: ChangeEvent<HTMLInputElement>,
    type: "THUMBNAIL" | "DESCRIPTION"
  ) => {
    const file = event.target.files?.[0];
    if (!file || !draftProduct) return;
    try {
      const uploadedUrl = await uploadImageWithPresignedUrl(file, type);
      setDraftProduct((prev) =>
        prev
          ? {
              ...prev,
              imageUrl: type === "THUMBNAIL" ? uploadedUrl : prev.imageUrl,
              description: type === "DESCRIPTION" ? uploadedUrl : prev.description,
            }
          : prev
      );
      event.target.value = "";
    } catch {
      alert("이미지 업로드에 실패했습니다.");
    }
  };

  // 상품 수정 API 연결용 핸들러
  const updateMyProduct = async (productId: number, payload: Product) => {
    // 백엔드 SellerProductUpsertRequest에 맞춰서 보낼 바디만 추림
    const body = {
      productName: payload.productName,
      price: payload.price,
      stockQuantity: payload.stockQuantity,
      description: payload.description,
      imageUrl: payload.imageUrl,
      brand: payload.brand,
      productCategory: payload.productCategory,
      status: payload.status,
      availableSizes: payload.availableSizes,
    };
  
    const updated = await apiFetch<SellerProductResponse>(
      `/api/seller/products/${productId}`,
      {
        method: "PUT",
        body: JSON.stringify(body),
      }
    );
  
    return mapProductFromResponse(updated);
  };

  // 상품 수정 저장
  const handleSaveEdit = async () => {
    if (!editingProductId || !draftProduct) return;
  
    if (!priceInput || !stockInput) {
      alert("가격과 재고는 1 이상으로 입력해야 합니다.");
      return;
    }
  
    const priceValue = Number(priceInput);
    const stockValue = Number(stockInput);
  
    if (
      Number.isNaN(priceValue) ||
      Number.isNaN(stockValue) ||
      priceValue < 1 ||
      stockValue < 1
    ) {
      alert("가격과 재고는 1 이상이어야 합니다.");
      return;
    }
  
    const payload: Product = {
      ...draftProduct,
      price: priceValue,
      stockQuantity: stockValue,
      availableSizes: draftProduct.availableSizes ?? [],
    };
  
    try {
      const updated = await updateMyProduct(editingProductId, payload);
  
      // 리스트 state 갱신
      setProducts((prev) =>
        prev.map((p) => (p.id === editingProductId ? { ...p, ...updated } : p))
      );
  
      // 확실하게 DB 기준으로 다시 땡기고 싶으면 이거 켜
      await fetchProducts(page);
  
      handleCloseEdit();
    } catch (error: any) {
      alert(error?.message ?? "상품 수정에 실패했습니다.");
    }
  };

  return (
    <SellerLayout>
      <div className="p-8">
        {/* Page Header */}
        <div className="mb-8">
          <h1 className="text-2xl font-semibold text-gray-900">
            Product Management
          </h1>
          <p className="text-sm text-gray-600 mt-2">
            Manage your product listings and stock status
          </p>
        </div>

        {/* Product Table */}
        <div className="bg-white border border-gray-200 rounded-lg overflow-hidden">
          <table className="w-full">
            <thead className="bg-gray-50 border-b border-gray-200">
              <tr>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Product ID
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Product Image
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Product Name
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Brand
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Category
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Price
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Stock
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Status
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Action
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-200">
              {products.map((product) => (
                <tr key={product.id} className="hover:bg-gray-50 transition-colors">
                  <td className="px-6 py-4 text-sm text-gray-900">{product.id}</td>
                  <td className="px-6 py-4">
                    <img
                      src={product.imageUrl}
                      alt={product.productName}
                      className="w-16 h-16 object-cover rounded-md"
                    />
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-900">
                    {product.productName}
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-900">
                    {getBrandLabel(product.brand)}
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-900">
                    {getCategoryLabel(product.productCategory)}
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-900">
                    {product.price.toLocaleString()}원
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-900">
                    {product.stockQuantity}
                  </td>
                  <td className="px-6 py-4 text-sm">
                    <span
                      className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(
                        product.status
                      )}`}
                    >
                      {getStatusLabel(product.status)}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-sm">
                    <button
                      onClick={() => handleOpenEdit(product)}
                      className="px-4 py-2 text-xs font-medium text-gray-900 border border-gray-300 rounded-md hover:bg-gray-50 transition-colors"
                    >
                      Edit
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* Product Edit Modal */}
        {isEditModalOpen &&
          draftProduct &&
          createPortal(
            <div
              className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4"
              onClick={handleCancelEdit}
            >
              <div
                className="bg-white rounded-2xl shadow-2xl max-w-3xl w-full max-h-[90vh] overflow-hidden flex flex-col"
                onClick={(event) => event.stopPropagation()}
              >
                {/* Modal Header */}
                <div className="border-b border-gray-200 px-6 py-5 flex items-center justify-between">
                  <div>
                    <h2 className="text-xl font-semibold text-gray-900">
                      Edit Product
                    </h2>
                    <p className="text-sm text-gray-500 mt-1">
                      {draftProduct.productName}
                    </p>
                  </div>
                  <button
                    onClick={handleCancelEdit}
                    className="p-2 hover:bg-gray-100 rounded-full transition-colors"
                  >
                    ✕
                  </button>
                </div>

                {/* Modal Content */}
                <div className="px-6 py-6 space-y-6 overflow-y-auto">
                  {/* Image Section */}
                  <div className="grid grid-cols-2 gap-6">
                    <div className="border border-gray-200 rounded-xl p-4">
                      <h3 className="text-sm font-medium text-gray-900 mb-3">
                        Thumbnail
                      </h3>
                      <div className="w-full aspect-video bg-gray-100 rounded-lg flex items-center justify-center overflow-hidden">
                        {draftProduct.imageUrl ? (
                          <img
                            src={draftProduct.imageUrl}
                            alt="Thumbnail"
                            className="w-full h-full object-cover"
                          />
                        ) : (
                          <span className="text-xs text-gray-400">No Image</span>
                        )}
                      </div>
                      <div className="mt-3 flex items-center gap-2">
                        <button
                          onClick={handleChangeThumbnail}
                          className="px-3 py-2 text-xs font-medium border border-gray-300 rounded-md hover:bg-gray-50 transition-colors"
                        >
                          Change
                        </button>
                        <input
                          type="text"
                          value={draftProduct.imageUrl}
                          onChange={(e) =>
                            setDraftProduct((prev) =>
                              prev ? { ...prev, imageUrl: e.target.value } : prev
                            )
                          }
                          placeholder="Paste URL"
                          className="flex-1 px-3 py-2 text-xs border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-gray-900"
                        />
                        <input
                          ref={thumbnailInputRef}
                          type="file"
                          accept="image/*"
                          className="hidden"
                          onChange={(event) => handleFileSelected(event, "THUMBNAIL")}
                        />
                      </div>
                    </div>

                    <div className="border border-gray-200 rounded-xl p-4">
                      <h3 className="text-sm font-medium text-gray-900 mb-3">
                        설명 이미지
                      </h3>
                      <div className="w-full aspect-video bg-gray-100 rounded-lg flex items-center justify-center overflow-hidden">
                        {draftProduct.description ? (
                          <img
                            src={draftProduct.description}
                            alt="Description"
                            className="w-full h-full object-cover"
                          />
                        ) : (
                          <span className="text-xs text-gray-400">No Image</span>
                        )}
                      </div>
                      <div className="mt-3 flex items-center gap-2">
                        <button
                          onClick={handleChangeDescription}
                          className="px-3 py-2 text-xs font-medium border border-gray-300 rounded-md hover:bg-gray-50 transition-colors"
                        >
                          Change
                        </button>
                        <input
                          type="text"
                          value={draftProduct.description}
                          onChange={(e) =>
                            setDraftProduct((prev) =>
                              prev ? { ...prev, description: e.target.value } : prev
                            )
                          }
                          placeholder="Paste URL"
                          className="flex-1 px-3 py-2 text-xs border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-gray-900"
                        />
                        <input
                          ref={descriptionInputRef}
                          type="file"
                          accept="image/*"
                          className="hidden"
                          onChange={(event) =>
                            handleFileSelected(event, "DESCRIPTION")
                          }
                        />
                      </div>
                    </div>
                  </div>

                  {/* Form Section */}
                  <div className="grid grid-cols-2 gap-6">
                    <div>
                      <label className="block text-sm font-medium text-gray-900 mb-2">
                        Product Name
                      </label>
                      <input
                        type="text"
                        value={draftProduct.productName}
                        onChange={(e) =>
                          setDraftProduct((prev) =>
                            prev ? { ...prev, productName: e.target.value } : prev
                          )
                        }
                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-gray-900"
                      />
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-900 mb-2">
                        Brand
                      </label>
                      <select
                        value={draftProduct.brand}
                        onChange={(e) =>
                          setDraftProduct((prev) =>
                            prev
                              ? { ...prev, brand: e.target.value as ProductBrand }
                              : prev
                          )
                        }
                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-gray-900"
                      >
                        {BRAND_OPTIONS.map((option) => (
                          <option key={option.value} value={option.value}>
                            {option.label}
                          </option>
                        ))}
                      </select>
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-900 mb-2">
                        Category
                      </label>
                      <select
                        value={draftProduct.productCategory}
                        onChange={(e) =>
                          setDraftProduct((prev) =>
                            prev
                              ? {
                                  ...prev,
                                  productCategory: e.target.value as ProductCategory,
                                }
                              : prev
                          )
                        }
                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-gray-900"
                      >
                        {CATEGORY_OPTIONS.map((option) => (
                          <option key={option.value} value={option.value}>
                            {option.label}
                          </option>
                        ))}
                      </select>
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-900 mb-2">
                        Status
                      </label>
                      <select
                        value={draftProduct.status}
                        onChange={(e) =>
                          setDraftProduct((prev) =>
                            prev
                              ? { ...prev, status: e.target.value as ProductStatus }
                              : prev
                          )
                        }
                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-gray-900"
                      >
                        {STATUS_OPTIONS.map((option) => (
                          <option key={option.value} value={option.value}>
                            {option.label}
                          </option>
                        ))}
                      </select>
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-900 mb-2">
                        Price
                      </label>
                      <input
                      type="text"
                      inputMode="numeric"
                      pattern="[0-9]*"
                      value={priceInput}
                      onChange={(e) => setPriceInput(normalizeNumericInput(e.target.value))}
                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-gray-900"
                      />
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-900 mb-2">
                        Stock Quantity
                      </label>
                      <input
                      type="text"
                      inputMode="numeric"
                      pattern="[0-9]*"
                      value={stockInput}
                      onChange={(e) => setStockInput(normalizeNumericInput(e.target.value))}
                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-gray-900"
                      />
                    </div>
                  </div>
                </div>

                {/* Modal Actions */}
                <div className="border-t border-gray-200 px-6 py-4 flex justify-end gap-3">
                  <button
                    onClick={handleCancelEdit}
                    className="px-4 py-2 text-sm border border-gray-300 rounded-md hover:bg-gray-50 transition-colors"
                  >
                    Cancel
                  </button>
                  <button
                    onClick={handleSaveEdit}
                    className="px-4 py-2 text-sm bg-gray-900 text-white rounded-md hover:bg-gray-800 transition-colors"
                  >
                    Save
                  </button>
                </div>
              </div>
            </div>,
            document.body
          )}
      </div>
    </SellerLayout>
  );
}
