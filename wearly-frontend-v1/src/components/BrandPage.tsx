import { useParams, Link } from "react-router";
import {
  ArrowLeft,
  Search,
  X,
  ArrowUpDown,
  AlertCircle
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

export default function BrandPage() {
  const { brandName } = useParams<{ brandName: string }>();
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Debug State
  const [debugTotalCount, setDebugTotalCount] = useState<number | null>(null);
  const [debugBrandEnum, setDebugBrandEnum] = useState<string>("");
  const [showDebug, setShowDebug] = useState(false);

  const [selectedCategory, setSelectedCategory] = useState("ALL");
  const [searchInput, setSearchInput] = useState("");
  const [sortBy, setSortBy] = useState<
    "latest" | "price-low" | "price-high"
  >("latest");

  useEffect(() => {
    if (!brandName) return;

    const fetchProducts = async () => {
      try {
        setLoading(true);
        setError(null);

        // Map URL friendly brand name to Backend Enum
        const brandEnum = brandName.toUpperCase().replace(/-/g, "_");
        setDebugBrandEnum(brandEnum);

        // Map sort to backend enum
        let sortEnum = "LATEST";
        if (sortBy === "price-low") sortEnum = "PRICE_LOW";
        if (sortBy === "price-high") sortEnum = "PRICE_HIGH";

        const params = new URLSearchParams();
        params.append("brand", brandEnum);
        if (selectedCategory !== "ALL") {
          params.append("category", selectedCategory);
        }
        if (searchInput) {
          params.append("keyword", searchInput);
        }
        params.append("sort", sortEnum);
        params.append("size", "100");

        const response = await apiFetch<PageResponse<Product>>(`/api/products?${params.toString()}`);
        setProducts(response.content);

        // DEBUG: Fetch all products count to see if DB is empty
        try {
          const allProducts = await apiFetch<PageResponse<Product>>(`/api/products?size=1`);
          setDebugTotalCount(allProducts.totalElements);
        } catch (e) { }

      } catch (err: any) {
        console.error("Failed to fetch products:", err);
        setProducts([]);
        if (err.status === 400 && brandName.toLowerCase() === 'levis') {
          setError("Brand 'Levis' is currently not available in our database.");
        } else {
          setError(`Failed to load products. ${err.message || ''}`);
        }
      } finally {
        setLoading(false);
      }
    };

    // Debounce search
    const timer = setTimeout(() => {
      fetchProducts();
    }, 300);

    return () => clearTimeout(timer);
  }, [brandName, selectedCategory, searchInput, sortBy]);


  const handleSearch = (e?: FormEvent) => {
    if (e) e.preventDefault();
  };

  const handleClearSearch = () => {
    setSearchInput("");
  };

  const handleKeyDown = (e: KeyboardEvent) => {
    if (e.key === "Enter") {
      handleSearch();
    }
  };

  const displayBrandName = brandName
    ? brandName.split('-').map(word => word.charAt(0).toUpperCase() + word.slice(1)).join(' ')
    : "";

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Brand Header */}
      <div className="bg-white border-b border-gray-200">
        <div className="max-w-[1400px] mx-auto px-8 py-12">
          <Link
            to="/"
            className="inline-flex items-center gap-2 text-sm text-gray-600 hover:text-gray-900 mb-6 transition-colors"
          >
            <ArrowLeft className="w-4 h-4" />
            Back to brands
          </Link>

          <h1 className="text-5xl font-bold text-gray-900 tracking-tight">
            {brandName === 'levis' ? "LEVI'S" : displayBrandName.toUpperCase()}
          </h1>
          <p className="text-lg text-gray-600 mt-3 mb-6">
            Explore our collection of {(brandName === 'levis' ? "LEVI'S" : displayBrandName).toLowerCase()} products
          </p>

          <button
            onClick={() => setShowDebug(!showDebug)}
            className="text-xs text-gray-400 hover:text-gray-600 mb-4 flex items-center gap-1"
          >
            <AlertCircle className="w-3 h-3" /> Debug Info
          </button>

          {showDebug && (
            <div className="bg-gray-100 p-4 rounded text-xs font-mono mb-6 space-y-1 text-gray-700">
              <div>URL Brand Parameter: <strong>{brandName}</strong></div>
              <div>Mapped Backend Enum: <strong>{debugBrandEnum}</strong></div>
              <div>Total Products in DB (Any Brand): <strong>{debugTotalCount !== null ? debugTotalCount : 'Loading...'}</strong></div>
              <div>Items Found for this Brand: <strong>{products.length}</strong></div>
              <div>Current Error: <strong>{error || 'None'}</strong></div>
            </div>
          )}

          {/* Search Bar */}
          <form onSubmit={handleSearch} className="mt-6">
            <div className="relative max-w-xl">
              <input
                type="text"
                value={searchInput}
                onChange={(e) => setSearchInput(e.target.value)}
                onKeyDown={handleKeyDown}
                placeholder="Search by product name"
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

      {/* Category Filter */}
      <div className="bg-white border-b border-gray-200">
        <div className="max-w-[1400px] mx-auto px-8 py-6">
          <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
            {/* Category Filters */}
            <div className="flex flex-wrap gap-3">
              {categories.map((category) => (
                <button
                  key={category.id}
                  onClick={() =>
                    setSelectedCategory(category.id)
                  }
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
                  setSortBy(
                    e.target.value as
                    | "latest"
                    | "price-low"
                    | "price-high",
                  )
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
                    {product.productCategory}
                  </p>
                  <h3 className="text-sm font-medium text-gray-900 mb-3 line-clamp-2">
                    {product.productName}
                  </h3>
                  <div className="flex items-center justify-between">
                    <p className="text-lg font-bold text-gray-900">
                      {product.price.toLocaleString()}원
                    </p>
                    <span className="px-4 py-2 bg-gray-900 text-white text-xs rounded group-hover:bg-gray-800 transition-colors">
                      View Details
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
              No products found for {brandName}.
            </p>
            <button
              onClick={() => {
                setSearchInput("");
                setSelectedCategory("ALL");
              }}
              className="mt-6 px-6 py-2 bg-gray-900 text-white text-sm rounded-lg hover:bg-gray-800 transition-colors"
            >
              Clear Filters
            </button>
          </div>
        )}
      </div>
    </div>
  );
}