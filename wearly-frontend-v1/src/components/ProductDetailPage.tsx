import { useState, useEffect } from "react";
import { ImageWithFallback } from "./figma/ImageWithFallback";
import { Star, Minus, Plus } from "lucide-react";
import { useNavigate, useParams } from "react-router";
import { apiFetch } from "../api/http";

type ProductSize = "SMALL" | "MEDIUM" | "LARGE" | "EXTRA_LARGE";

interface Review {
  reviewId: number;
  reviewerName: string;
  rating: number;
  content: string;
  createdDate: string;
}

interface Product {
  id: number;
  productName: string;
  price: number;
  stockQuantity: number;
  description: string;
  imageUrl: string;
  brand: string;
  productCategory: string;
  availableSizes: ProductSize[];
  reviews: Review[];
  averageRating: number;
  reviewCount: number;
}

// Map backend size enums to display strings
const SIZE_MAP: Record<ProductSize, string> = {
  SMALL: "S",
  MEDIUM: "M",
  LARGE: "L",
  EXTRA_LARGE: "XL"
};

export default function ProductDetailPage() {
  const { productId } = useParams<{ productId: string }>();
  const [product, setProduct] = useState<Product | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [selectedImage, setSelectedImage] = useState(0);
  const [selectedSize, setSelectedSize] = useState("");
  const [quantity, setQuantity] = useState(1);
  const [activeTab, setActiveTab] = useState<
    "details" | "reviews"
  >("details");

  const navigate = useNavigate();

  useEffect(() => {
    if (!productId) return;

    const fetchProduct = async () => {
      try {
        setLoading(true);
        const data = await apiFetch<Product>(`/api/products/${productId}`);
        setProduct(data);
      } catch (err: any) {
        setError(err.message || "Failed to load product");
      } finally {
        setLoading(false);
      }
    };

    fetchProduct();
  }, [productId]);

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-xl text-gray-600">Loading product...</div>
      </div>
    );
  }

  if (error || !product) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-xl text-red-600">
          {error || "Product not found"}
        </div>
      </div>
    );
  }

  // Use product images. Replicate main image for thumbnails if only one exists.
  const productImages = product.imageUrl
    ? [product.imageUrl, product.imageUrl, product.imageUrl, product.imageUrl]
    : [];

  // Map available sizes to display strings
  const sizes = product.availableSizes?.map(s => SIZE_MAP[s]) || [];

  // Calculate price logic
  const originalPrice = Math.round(product.price * 1.15); // Mock original price
  const discountRate = 15;
  const salePrice = product.price;

  // Use fetched reviews or empty array
  const reviews = product.reviews || [];

  return (
    <div className="bg-gray-50">
      {/* Product Main Section */}
      <div className="max-w-[1400px] mx-auto px-8 py-8">
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 bg-white p-8">
          {/* Left: Image Gallery */}
          <div>
            {/* Main Image */}
            <div className="aspect-[3/4] bg-gray-100 mb-4 overflow-hidden rounded-lg">
              <ImageWithFallback
                src={productImages[selectedImage]}
                alt={product.productName}
                className="w-full h-full object-cover"
              />
            </div>

            {/* Thumbnail Images */}
            <div className="grid grid-cols-4 gap-3">
              {productImages.map((img, index) => (
                <button
                  key={index}
                  onClick={() => setSelectedImage(index)}
                  className={`aspect-square bg-gray-100 rounded-lg overflow-hidden border-2 transition-all ${selectedImage === index
                    ? "border-gray-900"
                    : "border-transparent hover:border-gray-300"
                    }`}
                >
                  <ImageWithFallback
                    src={img}
                    alt={`Thumbnail ${index + 1}`}
                    className="w-full h-full object-cover"
                  />
                </button>
              ))}
            </div>
          </div>

          {/* Right: Product Information */}
          <div>
            {/* Brand */}
            <p className="text-sm text-gray-600 mb-2 uppercase tracking-wider">
              {product.brand}
            </p>

            {/* Product Name */}
            <h1 className="text-3xl font-bold text-gray-900 mb-4">
              {product.productName}
            </h1>

            {/* Rating */}
            <div className="flex items-center gap-2 mb-6">
              <div className="flex items-center">
                {[...Array(5)].map((_, i) => (
                  <Star
                    key={i}
                    className={`w-4 h-4 ${i < Math.round(product.averageRating || 0)
                      ? "fill-yellow-400 text-yellow-400"
                      : "text-gray-300"
                      }`}
                  />
                ))}
              </div>
              <span className="text-sm text-gray-600">{product.averageRating || 0}</span>
              <span className="text-sm text-gray-400">|</span>
              <span className="text-sm text-gray-600">
                리뷰 {product.reviewCount || 0}개
              </span>
            </div>

            {/* Price */}
            <div className="border-t border-b border-gray-200 py-6 mb-6">
              <div className="flex items-center gap-3 mb-2">
                <span className="text-2xl font-bold text-gray-900">
                  {salePrice.toLocaleString()}원
                </span>
                <span className="text-lg text-red-600 font-semibold">
                  {discountRate}%
                </span>
              </div>
              <div className="text-sm text-gray-500 line-through">
                {originalPrice.toLocaleString()}원
              </div>
            </div>

            {/* Size Selector */}
            <div className="mb-6">
              <label className="block text-sm font-medium text-gray-900 mb-3">
                사이즈 선택
              </label>
              <div className="grid grid-cols-5 gap-2">
                {sizes.length > 0 ? (
                  product.availableSizes?.map((sizeEnum) => {
                    const displaySize = SIZE_MAP[sizeEnum];
                    return (
                      <button
                        key={sizeEnum}
                        onClick={() => setSelectedSize(sizeEnum)}
                        className={`py-3 text-center border rounded-lg transition-all ${selectedSize === sizeEnum
                          ? "border-gray-900 bg-gray-900 text-white"
                          : "border-gray-300 bg-white text-gray-700 hover:border-gray-900"
                          }`}
                      >
                        {displaySize}
                      </button>
                    );
                  })
                ) : (
                  <div className="col-span-5 text-gray-500 text-sm">
                    선택 가능한 사이즈가 없습니다.
                  </div>
                )}
              </div>
              <p className="text-xs text-gray-500 mt-2">
                * 사이즈 가이드를 참고하세요
              </p>
            </div>

            {/* Quantity Selector */}
            <div className="mb-8">
              <label className="block text-sm font-medium text-gray-900 mb-3">
                수량
              </label>
              <div className="flex items-center gap-3">
                <button
                  onClick={() =>
                    setQuantity(Math.max(1, quantity - 1))
                  }
                  className="w-10 h-10 flex items-center justify-center border border-gray-300 rounded-lg hover:bg-gray-100"
                >
                  <Minus className="w-4 h-4" />
                </button>
                <span className="w-16 text-center text-lg font-medium">
                  {quantity}
                </span>
                <button
                  onClick={() => setQuantity(quantity + 1)}
                  className="w-10 h-10 flex items-center justify-center border border-gray-300 rounded-lg hover:bg-gray-100"
                >
                  <Plus className="w-4 h-4" />
                </button>
              </div>
              <div className="text-sm text-gray-500 mt-2">
                재고: {product.stockQuantity}개
              </div>
            </div>

            {/* Total Price */}
            <div className="bg-gray-50 p-4 rounded-lg mb-6">
              <div className="flex items-center justify-between">
                <span className="text-sm text-gray-600">
                  총 상품금액
                </span>
                <span className="text-2xl font-bold text-gray-900">
                  {(salePrice * quantity).toLocaleString()}원
                </span>
              </div>
            </div>

            {/* Action Buttons */}
            <div className="flex gap-3 mb-4">
              <button
                onClick={() => {
                  if (!selectedSize) {
                    alert('사이즈를 선택해주세요.');
                    return;
                  }
                  if (!productId) {
                    alert('상품 정보를 불러올 수 없습니다.');
                    return;
                  }
                  const params = new URLSearchParams({
                    productId: productId,
                    quantity: quantity.toString(),
                    size: selectedSize,
                  });
                  navigate(`/checkout?${params.toString()}`);
                }}
                className="flex-1 py-4 bg-gray-900 text-white rounded-lg hover:bg-gray-800 transition-colors font-medium"
              >
                구매하기
              </button>
              <button className="flex-1 py-4 bg-white text-gray-900 border-2 border-gray-900 rounded-lg hover:bg-gray-50 transition-colors font-medium">
                장바구니
              </button>
            </div>

            {/* Shipping Info */}
            <div className="mt-6 pt-6 border-t border-gray-200">
              <div className="space-y-3 text-sm">
                <div className="flex justify-between">
                  <span className="text-gray-600">배송비</span>
                  <span className="text-gray-900">
                    무료배송
                  </span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">
                    배송예정
                  </span>
                  <span className="text-gray-900">
                    1-2일 이내 출고
                  </span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">
                    반품/교환
                  </span>
                  <span className="text-gray-900">
                    수령 후 7일 이내
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Tabs Section */}
      <div className="max-w-[1400px] mx-auto px-8 py-8">
        <div className="bg-white">
          {/* Tab Headers */}
          <div className="flex border-b border-gray-200">
            <button
              onClick={() => setActiveTab("details")}
              className={`flex-1 py-4 text-center font-medium transition-colors ${activeTab === "details"
                ? "text-gray-900 border-b-2 border-gray-900"
                : "text-gray-500 hover:text-gray-700"
                }`}
            >
              상품상세
            </button>
            <button
              onClick={() => setActiveTab("reviews")}
              className={`flex-1 py-4 text-center font-medium transition-colors ${activeTab === "reviews"
                ? "text-gray-900 border-b-2 border-gray-900"
                : "text-gray-500 hover:text-gray-700"
                }`}
            >
              리뷰 ({product.reviewCount || 0})
            </button>
          </div>

          {/* Tab Content */}
          <div className="p-8">
            {/* Product Details Tab */}
            {activeTab === "details" && (
              <div className="w-full">
                {product.description && product.description.startsWith('http') ? (
                  <div className="w-full">
                    <ImageWithFallback
                      src={product.description}
                      alt="Product Detail"
                      className="w-full h-auto"
                    />
                  </div>
                ) : (
                  <div className="w-full flex justify-center py-20 bg-gray-50 text-gray-400">
                    <p>상세 이미지가 등록되지 않았습니다.</p>
                  </div>
                )}
              </div>
            )}

            {/* Reviews Tab */}
            {activeTab === "reviews" && (
              <div>
                {/* Rating Summary */}
                <div className="bg-gray-50 p-8 rounded-lg mb-8">
                  <div className="flex items-center gap-8">
                    <div className="text-center">
                      <div className="text-5xl font-bold text-gray-900 mb-2">
                        {product.averageRating || 0}
                      </div>
                      <div className="flex items-center justify-center mb-1">
                        {[...Array(5)].map((_, i) => (
                          <Star
                            key={i}
                            className={`w-5 h-5 ${i < Math.round(product.averageRating || 0)
                              ? "fill-yellow-400 text-yellow-400"
                              : "text-gray-300"
                              }`}
                          />
                        ))}
                      </div>
                      <div className="text-sm text-gray-600">
                        {product.reviewCount || 0}개 리뷰
                      </div>
                    </div>

                    <div className="flex-1">
                      {/* Note: Histogram data not available from backend 'SellerProductResponse' initially. 
                          Ideally we'd calculate this or fetch it. For now, hiding or showing dummy bars. 
                          Since we only have list of reviews, we can calculate from that if all reviews are returned,
                          but usually it's paginated. We will infer from the current list for now (up to 5). 
                      */}
                      {[5, 4, 3, 2, 1].map((rating) => {
                        // Simple count from the 'reviews' list (which is minimal or first page)
                        const count = reviews.filter(
                          (r) => r.rating === rating,
                        ).length;
                        const percentage = reviews.length > 0
                          ? (count / reviews.length) * 100
                          : 0;
                        return (
                          <div
                            key={rating}
                            className="flex items-center gap-3 mb-2"
                          >
                            <span className="text-sm text-gray-600 w-8">
                              {rating}점
                            </span>
                            <div className="flex-1 h-2 bg-gray-200 rounded-full overflow-hidden">
                              <div
                                className="h-full bg-yellow-400"
                                style={{
                                  width: `${percentage}%`,
                                }}
                              />
                            </div>
                            <span className="text-sm text-gray-600 w-8">
                              {count}
                            </span>
                          </div>
                        );
                      })}
                    </div>
                  </div>
                </div>

                {/* Review List */}
                <div className="space-y-6">
                  {reviews.length > 0 ? (
                    reviews.map((review) => (
                      <div
                        key={review.reviewId}
                        className="border-b border-gray-200 pb-6"
                      >
                        <div className="flex items-center justify-between mb-3">
                          <div className="flex items-center gap-3">
                            <span className="font-medium text-gray-900">
                              {review.reviewerName}
                            </span>
                            <div className="flex items-center">
                              {[...Array(5)].map((_, i) => (
                                <Star
                                  key={i}
                                  className={`w-4 h-4 ${i < review.rating
                                    ? "fill-yellow-400 text-yellow-400"
                                    : "text-gray-300"
                                    }`}
                                />
                              ))}
                            </div>
                          </div>
                          <span className="text-sm text-gray-500">
                            {new Date(review.createdDate).toLocaleDateString()}
                          </span>
                        </div>
                        <p className="text-gray-700 leading-relaxed mb-3">
                          {review.content}
                        </p>
                      </div>
                    ))
                  ) : (
                    <div className="text-center py-10 text-gray-500">
                      아직 작성된 리뷰가 없습니다.
                    </div>
                  )}
                </div>

                {/* Write Review Button */}
                <div className="mt-8 text-center">
                  <button
                    onClick={() => navigate(`/product/${productId}/review`)}
                    className="px-8 py-3 bg-gray-900 text-white rounded-lg hover:bg-gray-800 transition-colors"
                  >
                    리뷰 작성하기
                  </button>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}