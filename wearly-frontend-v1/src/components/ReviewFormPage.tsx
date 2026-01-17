import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Star, ArrowLeft } from "lucide-react";
import { apiFetch } from "../api/http";

export default function ReviewFormPage() {
    const { productId } = useParams<{ productId: string }>();
    const navigate = useNavigate();
    const [rating, setRating] = useState(5);
    const [content, setContent] = useState("");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    // Optional: Fetch product name for context
    const [productName, setProductName] = useState("");

    useEffect(() => {
        if (productId) {
            apiFetch<{ productName: string }>(`/api/products/${productId}`)
                .then((data) => setProductName(data.productName))
                .catch(() => { }); // Ignore error if product fetch fails
        }
    }, [productId]);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!productId) return;

        setLoading(true);
        setError(null);

        try {
            // Generate a manual order ID as backend requires it but doesn't strictly validate existence yet
            const manualOrderId = `manual-review-${Date.now()}`;

            await apiFetch("/api/users/reviews", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    productId: Number(productId),
                    orderId: manualOrderId,
                    rating,
                    content,
                }),
            });

            alert("리뷰가 성공적으로 등록되었습니다.");
            navigate(`/product/${productId}`);
        } catch (err: any) {
            console.error(err);
            setError(err.message || "리뷰 등록에 실패했습니다.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
            <div className="max-w-md mx-auto bg-white rounded-lg shadow-sm overflow-hidden">
                <div className="px-6 py-4 border-b border-gray-200 flex items-center gap-4">
                    <button
                        onClick={() => navigate(-1)}
                        className="text-gray-500 hover:text-gray-700"
                    >
                        <ArrowLeft className="w-5 h-5" />
                    </button>
                    <h1 className="text-xl font-bold text-gray-900">리뷰 작성</h1>
                </div>

                <div className="p-6">
                    {productName && (
                        <div className="mb-6 text-sm text-gray-600">
                            상품: <span className="font-medium text-gray-900">{productName}</span>
                        </div>
                    )}

                    <form onSubmit={handleSubmit} className="space-y-6">
                        {/* Rating */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-2">
                                별점
                            </label>
                            <div className="flex gap-2">
                                {[1, 2, 3, 4, 5].map((star) => (
                                    <button
                                        key={star}
                                        type="button"
                                        onClick={() => setRating(star)}
                                        className="focus:outline-none transition-transform hover:scale-110"
                                    >
                                        <Star
                                            className={`w-8 h-8 ${star <= rating
                                                    ? "fill-yellow-400 text-yellow-400"
                                                    : "text-gray-300"
                                                }`}
                                        />
                                    </button>
                                ))}
                            </div>
                        </div>

                        {/* Content */}
                        <div>
                            <label
                                htmlFor="content"
                                className="block text-sm font-medium text-gray-700 mb-2"
                            >
                                리뷰 내용
                            </label>
                            <textarea
                                id="content"
                                rows={5}
                                required
                                value={content}
                                onChange={(e) => setContent(e.target.value)}
                                placeholder="상품에 대한 솔직한 리뷰를 남겨주세요."
                                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent resize-none"
                            />
                        </div>

                        {error && (
                            <div className="text-sm text-red-600 bg-red-50 p-3 rounded">
                                {error}
                            </div>
                        )}

                        <button
                            type="submit"
                            disabled={loading}
                            className={`w-full py-3 px-4 bg-gray-900 text-white rounded-lg font-medium hover:bg-gray-800 transition-colors ${loading ? "opacity-50 cursor-not-allowed" : ""
                                }`}
                        >
                            {loading ? "등록 중..." : "리뷰 등록하기"}
                        </button>
                    </form>
                </div>
            </div>
        </div>
    );
}
