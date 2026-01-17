import { useState } from "react";
import { ImageWithFallback } from "./figma/ImageWithFallback";
import { Star, Minus, Plus, Heart, Share2 } from "lucide-react";
import { useNavigate, useParams } from "react-router";
import { apiFetch } from "../api/http";

const productImages = [
  "https://images.unsplash.com/photo-1761891873744-eb181eb1334a?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxkZW5pbSUyMGplYW5zJTIwcHJvZHVjdHxlbnwxfHx8fDE3NjgzNjE3MzB8MA&ixlib=rb-4.1.0&q=80&w=1080&utm_source=figma&utm_medium=referral",
  "https://images.unsplash.com/photo-1761891873744-eb181eb1334a?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxkZW5pbSUyMGplYW5zJTIwcHJvZHVjdHxlbnwxfHx8fDE3NjgzNjE3MzB8MA&ixlib=rb-4.1.0&q=80&w=1080&utm_source=figma&utm_medium=referral",
  "https://images.unsplash.com/photo-1761891873744-eb181eb1334a?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxkZW5pbSUyMGplYW5zJTIwcHJvZHVjdHxlbnwxfHx8fDE3NjgzNjE3MzB8MA&ixlib=rb-4.1.0&q=80&w=1080&utm_source=figma&utm_medium=referral",
  "https://images.unsplash.com/photo-1761891873744-eb181eb1334a?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxkZW5pbSUyMGplYW5zJTIwcHJvZHVjdHxlbnwxfHx8fDE3NjgzNjE3MzB8MA&ixlib=rb-4.1.0&q=80&w=1080&utm_source=figma&utm_medium=referral",
];

const reviews = [
  {
    id: 1,
    userName: "김*영",
    rating: 5,
    date: "2026.01.10",
    content:
      "핏이 정말 좋아요! 기장도 딱 맞고 색상도 사진과 동일합니다. 배송도 빨라서 만족스러웠어요. 재구매 의사 있습니다.",
    helpful: 24,
  },
  {
    id: 2,
    userName: "이*수",
    rating: 4,
    date: "2026.01.08",
    content:
      "전반적으로 만족합니다. 소재가 좋고 마무리도 깔끔해요. 다만 사이즈가 약간 크게 나온 것 같아서 한 치수 작게 주문하시는 걸 추천드립니다.",
    helpful: 18,
  },
  {
    id: 3,
    userName: "박*민",
    rating: 5,
    date: "2026.01.05",
    content:
      "클래식한 디자인이 마음에 들어요. 어떤 옷이랑 매치해도 잘 어울리고 착용감이 편안합니다. 가격 대비 훌륭한 제품입니다!",
    helpful: 31,
  },
  {
    id: 4,
    userName: "최*현",
    rating: 5,
    date: "2026.01.03",
    content:
      "기대 이상입니다. 품질이 우수하고 스티치 마감도 깔끔해요. 세탁 후에도 형태가 잘 유지되네요. 강력 추천합니다!",
    helpful: 15,
  },
  {
    id: 5,
    userName: "정*아",
    rating: 4,
    date: "2025.12.28",
    content:
      "생각했던 것보다 더 마음에 들어요. 디자인도 세련되고 착용감도 좋습니다. 배송도 빠르고 포장 상태도 완벽했어요.",
    helpful: 12,
  },
];

const qnaList = [
  {
    id: 1,
    question: "사이즈 교환이 가능한가요?",
    answer:
      "네, 상품 수령 후 7일 이내에 교환 신청이 가능합니다. 단, 착용하지 않고 택이 부착된 상태여야 합니다.",
    userName: "구*자",
    date: "2026.01.12",
    answered: true,
  },
  {
    id: 2,
    question: "실제 색상이 사진과 동일한가요?",
    answer:
      "사진과 거의 동일합니다. 촬영 조명에 따라 약간의 차이가 있을 수 있으나 실물이 더 좋습니다.",
    userName: "송*진",
    date: "2026.01.10",
    answered: true,
  },
  {
    id: 3,
    question: "키 175cm에 몇 사이즈가 적당할까요?",
    answer:
      "체형에 따라 다르지만 보통 체형이시라면 32 사이즈를 추천드립니다.",
    userName: "윤*우",
    date: "2026.01.08",
    answered: true,
  },
  {
    id: 4,
    question: "세탁 방법이 어떻게 되나요?",
    answer: "",
    userName: "한*비",
    date: "2026.01.14",
    answered: false,
  },
];

export default function ProductDetailPage() {
  const [selectedImage, setSelectedImage] = useState(0);
  const [selectedSize, setSelectedSize] = useState("");
  const [quantity, setQuantity] = useState(1);
  const [activeTab, setActiveTab] = useState<
    "details" | "reviews" | "qna"
  >("details");

  const sizes = ["28", "30", "32", "34", "36"];
  const originalPrice = 98000;
  const discountRate = 15;
  const salePrice = Math.floor(
    originalPrice * (1 - discountRate / 100),
  );

  const navigate = useNavigate();
  const { productId } = useParams<{ productId: string }>();

    const handleAddToCart = async () => {
        // 사이즈 선택 확인
        if (!selectedSize) {
            alert("사이즈를 선택해주세요.");
            return;
        }

        try {
            // 장바구니에 상품 추가 API 호출
            await apiFetch("/api/users/cart/items", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    productId: Number(productId),
                    size: selectedSize,
                    quantity: quantity,
                }),
            });

            // 장바구니 페이지로 이동
            navigate("/cart");
        } catch (error: any) {
            alert(error?.message || "장바구니에 상품을 추가하는데 실패했습니다.");
        }
    };

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
                alt="Product"
                className="w-full h-full object-cover"
              />
            </div>

            {/* Thumbnail Images */}
            <div className="grid grid-cols-4 gap-3">
              {productImages.map((img, index) => (
                <button
                  key={index}
                  onClick={() => setSelectedImage(index)}
                  className={`aspect-square bg-gray-100 rounded-lg overflow-hidden border-2 transition-all ${
                    selectedImage === index
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
              LEVI'S
            </p>

            {/* Product Name */}
            <h1 className="text-3xl font-bold text-gray-900 mb-4">
              501 Original Fit Jeans - Classic Blue
            </h1>

            {/* Rating */}
            <div className="flex items-center gap-2 mb-6">
              <div className="flex items-center">
                {[...Array(5)].map((_, i) => (
                  <Star
                    key={i}
                    className="w-4 h-4 fill-yellow-400 text-yellow-400"
                  />
                ))}
              </div>
              <span className="text-sm text-gray-600">4.8</span>
              <span className="text-sm text-gray-400">|</span>
              <span className="text-sm text-gray-600">
                리뷰 {reviews.length}개
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
                {sizes.map((size) => (
                  <button
                    key={size}
                    onClick={() => setSelectedSize(size)}
                    className={`py-3 text-center border rounded-lg transition-all ${
                      selectedSize === size
                        ? "border-gray-900 bg-gray-900 text-white"
                        : "border-gray-300 bg-white text-gray-700 hover:border-gray-900"
                    }`}
                  >
                    {size}
                  </button>
                ))}
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
                onClick={() => navigate('/checkout')}
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
              className={`flex-1 py-4 text-center font-medium transition-colors ${
                activeTab === "details"
                  ? "text-gray-900 border-b-2 border-gray-900"
                  : "text-gray-500 hover:text-gray-700"
              }`}
            >
              상품상세
            </button>
            <button
              onClick={() => setActiveTab("reviews")}
              className={`flex-1 py-4 text-center font-medium transition-colors ${
                activeTab === "reviews"
                  ? "text-gray-900 border-b-2 border-gray-900"
                  : "text-gray-500 hover:text-gray-700"
              }`}
            >
              리뷰 ({reviews.length})
            </button>
          </div>

          {/* Tab Content */}
          <div className="p-8">
            {/* Product Details Tab */}
            {activeTab === "details" && (
              <div>
                <h2 className="text-2xl font-bold text-gray-900 mb-6">
                  제품 상세정보
                </h2>

                {/* Product Description */}
                <div className="prose max-w-none mb-8">
                  <p className="text-gray-700 leading-relaxed mb-4">
                    리바이스의 상징적인 501 오리지널 핏 진.
                    1873년부터 이어져 온 클래식한 디자인으로,
                    시대를 초월한 스타일을 선사합니다. 편안한
                    핏과 내구성 있는 소재로 일상에서 자유롭게
                    활용할 수 있습니다.
                  </p>
                  <p className="text-gray-700 leading-relaxed mb-4">
                    고급스러운 데님 원단을 사용하여 착용감이
                    우수하며, 세탁 후에도 형태가 잘 유지됩니다.
                    클래식 블루 워시로 어떤 스타일과도 매치하기
                    좋습니다.
                  </p>
                </div>

                {/* Product Specifications */}
                <div className="bg-gray-50 p-6 rounded-lg mb-8">
                  <h3 className="font-bold text-gray-900 mb-4">
                    제품 사양
                  </h3>
                  <div className="space-y-3 text-sm">
                    <div className="flex">
                      <span className="w-32 text-gray-600">
                        소재
                      </span>
                      <span className="text-gray-900">
                        면 100%
                      </span>
                    </div>
                    <div className="flex">
                      <span className="w-32 text-gray-600">
                        원산지
                      </span>
                      <span className="text-gray-900">USA</span>
                    </div>
                    <div className="flex">
                      <span className="w-32 text-gray-600">
                        색상
                      </span>
                      <span className="text-gray-900">
                        Classic Blue
                      </span>
                    </div>
                    <div className="flex">
                      <span className="w-32 text-gray-600">
                        핏
                      </span>
                      <span className="text-gray-900">
                        Regular Fit
                      </span>
                    </div>
                    <div className="flex">
                      <span className="w-32 text-gray-600">
                        제조사
                      </span>
                      <span className="text-gray-900">
                        Levi Strauss & Co.
                      </span>
                    </div>
                  </div>
                </div>

                {/* Detail Images */}
                <div className="space-y-4">
                  <h3 className="font-bold text-gray-900 mb-4">
                    상세 이미지
                  </h3>
                  {[1, 2, 3].map((i) => (
                    <div
                      key={i}
                      className="w-full aspect-[16/9] bg-gray-100 rounded-lg"
                    >
                      <ImageWithFallback
                        src={productImages[0]}
                        alt={`Detail ${i}`}
                        className="w-full h-full object-cover rounded-lg"
                      />
                    </div>
                  ))}
                </div>

                {/* Care Instructions */}
                <div className="mt-8 bg-blue-50 p-6 rounded-lg">
                  <h3 className="font-bold text-gray-900 mb-3">
                    세탁 및 관리 방법
                  </h3>
                  <ul className="space-y-2 text-sm text-gray-700">
                    <li>
                      • 찬물 또는 미지근한 물로 세탁하세요
                    </li>
                    <li>• 표백제 사용을 피하세요</li>
                    <li>
                      • 뒤집어서 세탁하면 색상 보호에 도움이
                      됩니다
                    </li>
                    <li>
                      • 건조기 사용 시 저온으로 설정하세요
                    </li>
                    <li>
                      • 직사광선을 피해 그늘에서 건조하세요
                    </li>
                  </ul>
                </div>
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
                        4.8
                      </div>
                      <div className="flex items-center justify-center mb-1">
                        {[...Array(5)].map((_, i) => (
                          <Star
                            key={i}
                            className="w-5 h-5 fill-yellow-400 text-yellow-400"
                          />
                        ))}
                      </div>
                      <div className="text-sm text-gray-600">
                        {reviews.length}개 리뷰
                      </div>
                    </div>

                    <div className="flex-1">
                      {[5, 4, 3, 2, 1].map((rating) => {
                        const count = reviews.filter(
                          (r) => r.rating === rating,
                        ).length;
                        const percentage =
                          (count / reviews.length) * 100;
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
                  {reviews.map((review) => (
                    <div
                      key={review.id}
                      className="border-b border-gray-200 pb-6"
                    >
                      <div className="flex items-center justify-between mb-3">
                        <div className="flex items-center gap-3">
                          <span className="font-medium text-gray-900">
                            {review.userName}
                          </span>
                          <div className="flex items-center">
                            {[...Array(5)].map((_, i) => (
                              <Star
                                key={i}
                                className={`w-4 h-4 ${
                                  i < review.rating
                                    ? "fill-yellow-400 text-yellow-400"
                                    : "text-gray-300"
                                }`}
                              />
                            ))}
                          </div>
                        </div>
                        <span className="text-sm text-gray-500">
                          {review.date}
                        </span>
                      </div>
                      <p className="text-gray-700 leading-relaxed mb-3">
                        {review.content}
                      </p>
                      <button className="text-sm text-gray-500 hover:text-gray-700">
                        도움이 돼요 ({review.helpful})
                      </button>
                    </div>
                  ))}
                </div>

                {/* Write Review Button */}
                <div className="mt-8 text-center">
                  <button className="px-8 py-3 bg-gray-900 text-white rounded-lg hover:bg-gray-800 transition-colors">
                    리뷰 작성하기
                  </button>
                </div>
              </div>
            )}

            {/* Q&A Tab */}
            {activeTab === "qna" && (
              <div>
                <div className="flex justify-between items-center mb-6">
                  <h2 className="text-2xl font-bold text-gray-900">
                    상품 문의
                  </h2>
                  <button className="px-6 py-2 bg-gray-900 text-white rounded-lg hover:bg-gray-800 transition-colors">
                    문의하기
                  </button>
                </div>

                {/* Q&A List */}
                <div className="space-y-4">
                  {qnaList.map((item) => (
                    <div
                      key={item.id}
                      className="border border-gray-200 rounded-lg overflow-hidden"
                    >
                      {/* Question */}
                      <div className="bg-gray-50 p-5">
                        <div className="flex items-start justify-between mb-2">
                          <div className="flex items-center gap-3">
                            <span className="px-2 py-1 bg-gray-200 text-gray-700 text-xs rounded">
                              Q
                            </span>
                            <span className="font-medium text-gray-900">
                              {item.question}
                            </span>
                          </div>
                          {!item.answered && (
                            <span className="text-xs text-blue-600 bg-blue-50 px-2 py-1 rounded">
                              답변대기
                            </span>
                          )}
                        </div>
                        <div className="flex items-center gap-3 text-sm text-gray-500 ml-9">
                          <span>{item.userName}</span>
                          <span>|</span>
                          <span>{item.date}</span>
                        </div>
                      </div>

                      {/* Answer */}
                      {item.answered && (
                        <div className="bg-white p-5 border-t border-gray-200">
                          <div className="flex items-start gap-3">
                            <span className="px-2 py-1 bg-blue-600 text-white text-xs rounded">
                              A
                            </span>
                            <p className="text-gray-700 flex-1">
                              {item.answer}
                            </p>
                          </div>
                        </div>
                      )}
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}