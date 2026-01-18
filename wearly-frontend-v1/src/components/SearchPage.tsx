import { useSearchParams, Link } from "react-router-dom";
import {
    Search,
    X,
    ArrowUpDown,
    ArrowLeft
} from "lucide-react";
import { ImageWithFallback } from "../components/figma/ImageWithFallback";
import { useState, useEffect, type FormEvent, type KeyboardEvent } from "react";
import { apiFetch } from "../api/http";

interface Product {
    id: number;
    productName: string;
    price: number;
    stockQuantity: number;
    description: string;
    imageUrl: string;
    brand: string;
    productCategory: string;
    availableSizes: string[];
}

interface PageResponse<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    number: number;
    size: number;
}

const categories = [
    { id: "ALL", label: "전체" },
    { id: "PADDING", label: "패딩" },
    { id: "SHIRT", label: "셔츠" },
    { id: "COAT", label: "코트" },
    { id: "HOODIE", label: "후드" },
    { id: "SWEATSHIRT", label: "맨투맨" },
    { id: "JEANS", label: "청바지" },
    { id: "SHORTS", label: "반바지" },
];

export default function SearchPage() {
    const [searchParams, setSearchParams] = useSearchParams();
    const initialKeyword = searchParams.get("keyword") || "";

    const [products, setProducts] = useState<Product[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const [selectedCategory, setSelectedCategory] = useState("ALL");
    // 검색어 입력 상태 (상단 검색바용 separate state)
    const [searchInput, setSearchInput] = useState(initialKeyword);

    // 실제 API 호출에 사용될 검색어 (엔터/버튼 클릭시 업데이트)
    const [appliedKeyword, setAppliedKeyword] = useState(initialKeyword);

    const [sortBy, setSortBy] = useState<
        "latest" | "price-low" | "price-high"
    >("latest");

    // URL 쿼리 파라미터가 변경되면 appliedKeyword 업데이트
    useEffect(() => {
        const keywordFromUrl = searchParams.get("keyword") || "";
        setAppliedKeyword(keywordFromUrl);
        setSearchInput(keywordFromUrl);
    }, [searchParams]);

    useEffect(() => {
        const fetchProducts = async () => {
            try {
                setLoading(true);
                setError(null);

                // Map sort to backend enum
                let sortEnum = "LATEST";
                if (sortBy === "price-low") sortEnum = "PRICE_LOW";
                if (sortBy === "price-high") sortEnum = "PRICE_HIGH";

                const params = new URLSearchParams();
                // Brand 필터 없음 -> 전체 브랜드 검색

                if (selectedCategory !== "ALL") {
                    params.append("category", selectedCategory);
                }
                if (appliedKeyword) {
                    params.append("keyword", appliedKeyword);
                }
                params.append("sort", sortEnum);
                params.append("size", "100");

                const response = await apiFetch<PageResponse<Product>>(`/api/products?${params.toString()}`);
                setProducts(response.content);

            } catch (err: any) {
                console.error("Failed to fetch products:", err);
                setProducts([]);
                setError(`Failed to load products. ${err.message || ''}`);
            } finally {
                setLoading(false);
            }
        };

        fetchProducts();
    }, [selectedCategory, appliedKeyword, sortBy]);


    const handleSearch = (e?: FormEvent) => {
        if (e) e.preventDefault();
        // URL 업데이트 -> useEffect 트리거
        setSearchParams({ keyword: searchInput });
    };

    const handleClearSearch = () => {
        setSearchInput("");
        setSearchParams({ keyword: "" });
    };

    const handleKeyDown = (e: KeyboardEvent) => {
        if (e.key === "Enter") {
            handleSearch();
        }
    };

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Search Header */}
            <div className="bg-white border-b border-gray-200">
                <div className="max-w-[1400px] mx-auto px-8 py-12">
                    <Link
                        to="/"
                        className="inline-flex items-center gap-2 text-sm text-gray-600 hover:text-gray-900 mb-6 transition-colors"
                    >
                        <ArrowLeft className="w-4 h-4" />
                        Back to Home
                    </Link>

                    <h1 className="text-4xl font-bold text-gray-900 tracking-tight mb-2">
                        Search Results
                    </h1>
                    <p className="text-gray-600 mb-8">
                        Showing results for <span className="font-semibold">"{appliedKeyword}"</span>
                    </p>

                    {/* Search Bar */}
                    <form onSubmit={handleSearch} className="max-w-xl">
                        <div className="relative">
                            <input
                                type="text"
                                value={searchInput}
                                onChange={(e) => setSearchInput(e.target.value)}
                                onKeyDown={handleKeyDown}
                                placeholder="Search products..."
                                className="w-full h-12 pl-4 pr-24 text-gray-900 bg-gray-50 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent"
                            />
                            {searchInput && (
                                <button
                                    type="button"
                                    onClick={handleClearSearch}
                                    className="absolute right-20 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
                                >
                                    <X className="w-5 h-5" />
                                </button>
                            )}
                            <button
                                type="submit"
                                className="absolute right-1 top-1/2 -translate-y-1/2 h-10 px-6 bg-gray-900 text-white text-sm rounded-md hover:bg-gray-800 transition-colors"
                            >
                                <Search className="w-4 h-4" />
                            </button>
                        </div>
                    </form>
                </div>
            </div>

            {/* Filter & Sort */}
            <div className="bg-white border-b border-gray-200">
                <div className="max-w-[1400px] mx-auto px-8 py-6">
                    <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
                        {/* Category Filters */}
                        <div className="flex flex-wrap gap-3">
                            {categories.map((category) => (
                                <button
                                    key={category.id}
                                    onClick={() => setSelectedCategory(category.id)}
                                    className={`px-6 py-2.5 text-sm font-medium rounded-full transition-all duration-200 ${selectedCategory === category.id
                                            ? "bg-gray-900 text-white shadow-sm"
                                            : "bg-gray-100 text-gray-700 hover:bg-gray-200"
                                        }`}
                                >
                                    {category.label}
                                </button>
                            ))}
                        </div>

                        {/* Sort Dropdown */}
                        <div className="flex items-center gap-2">
                            <ArrowUpDown className="w-4 h-4 text-gray-600" />
                            <select
                                value={sortBy}
                                onChange={(e) =>
                                    setSortBy(e.target.value as "latest" | "price-low" | "price-high")
                                }
                                className="h-10 px-4 pr-10 text-sm text-gray-900 bg-white border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent cursor-pointer"
                            >
                                <option value="latest">최신순</option>
                                <option value="price-low">낮은가격순</option>
                                <option value="price-high">높은가격순</option>
                            </select>
                        </div>
                    </div>
                </div>
            </div>

            {/* Products Grid */}
            <div className="max-w-[1400px] mx-auto px-8 py-12">
                {loading ? (
                    <div className="text-center py-20 text-gray-600">Loading products...</div>
                ) : error ? (
                    <div className="text-center py-20 text-red-600">{error}</div>
                ) : products.length > 0 ? (
                    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
                        {products.map((product) => (
                            <Link
                                key={product.id}
                                to={`/product/${product.id}`}
                                className="group bg-white rounded-lg overflow-hidden shadow-sm hover:shadow-lg transition-all duration-300 border border-gray-100"
                            >
                                <div className="aspect-[3/4] bg-gradient-to-br from-gray-100 to-gray-50 overflow-hidden">
                                    <ImageWithFallback
                                        src={product.imageUrl}
                                        alt={product.productName}
                                        className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
                                    />
                                </div>
                                <div className="p-5">
                                    <p className="text-xs text-gray-500 mb-2 uppercase tracking-wider">
                                        {product.brand} · {product.productCategory}
                                    </p>
                                    <h3 className="text-sm font-medium text-gray-900 mb-3 line-clamp-2">
                                        {product.productName}
                                    </h3>
                                    <div className="flex items-center justify-between">
                                        <p className="text-lg font-bold text-gray-900">
                                            {product.price.toLocaleString()}원
                                        </p>
                                        <span className="px-4 py-2 bg-gray-900 text-white text-xs rounded group-hover:bg-gray-800 transition-colors">
                                            View
                                        </span>
                                    </div>
                                </div>
                            </Link>
                        ))}
                    </div>
                ) : (
                    <div className="flex flex-col items-center justify-center py-20">
                        <div className="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mb-4">
                            <Search className="w-8 h-8 text-gray-400" />
                        </div>
                        <p className="text-lg text-gray-600">
                            No products found for "{appliedKeyword}"
                        </p>
                        <button
                            onClick={() => {
                                setSearchInput("");
                                setSearchParams({ keyword: "" });
                            }}
                            className="mt-6 px-6 py-2 bg-gray-900 text-white text-sm rounded-lg hover:bg-gray-800 transition-colors"
                        >
                            Clear Search
                        </button>
                    </div>
                )}
            </div>
        </div>
    );
}
